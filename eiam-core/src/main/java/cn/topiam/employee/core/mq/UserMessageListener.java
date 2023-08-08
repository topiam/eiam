/*
 * eiam-core - Employee Identity and Access Management
 * Copyright © 2022-Present Jinan Yuanchuang Network Technology Co., Ltd. (support@topiam.cn)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cn.topiam.employee.core.mq;

import java.io.IOException;
import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.rabbitmq.client.Channel;

import cn.topiam.employee.common.entity.account.UserElasticSearchEntity;
import cn.topiam.employee.common.entity.account.po.UserEsPO;
import cn.topiam.employee.common.repository.account.OrganizationRepository;
import cn.topiam.employee.common.repository.account.UserElasticSearchRepository;
import cn.topiam.employee.common.repository.account.UserRepository;

import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.core.mq.AbstractMessagePublisher.USER_DELETE;
import static cn.topiam.employee.core.mq.AbstractMessagePublisher.USER_SAVE;

/**
 * 用户消息监听器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/5/30 23:12
 */
@Slf4j
@Component
public class UserMessageListener extends AbstractMessageListener {

    /**
     * 接收用户消息
     *
     * @param message {@link Message}
     * @param channel {@link Channel}
     * @param body    {@link String}
     * @param headers {@link Map}
     */
    @Override
    @RabbitListener(queues = { USER_SAVE, USER_DELETE }, ackMode = "MANUAL")
    @RabbitHandler()
    public void onMessage(Message message, Channel channel, @Payload String body,
                          @Headers Map<String, Object> headers) {
        super.onMessage(message, channel, body, headers);
        log.info("异步接收ES用户信息入参: [{}]", message);
        syncUser(message, channel, body);
    }

    /**
     * 同步用户数据
     *
     * @param message {@link Message}
     * @param channel {@link Channel}
     * @param body    {@link String}
     */
    private void syncUser(Message message, Channel channel, String body) {
        try {
            // 处理消息逻辑
            String queueName = message.getMessageProperties().getConsumerQueue();
            if (!StringUtils.hasText(body)) {
                log.warn("接收用户消息内容为空:[{}]", message.getMessageProperties().getDeliveryTag());
                return;
            }
            log.info("接收用户消息:[{}]", body);
            List<String> idList = Arrays.asList(body.split(","));
            if (queueName.equals(USER_SAVE)) {
                List<UserEsPO> userList = userRepository.getUserList(idList);
                List<UserElasticSearchEntity> userElasticSearchEntity = getUserElasticSearchEntity(
                    userList, organizationRepository);
                userElasticSearchRepository.saveAll(userElasticSearchEntity);
            } else if (queueName.equals(USER_DELETE)) {
                userElasticSearchRepository.deleteAllById(idList);
            }
            log.info("同步用户数据成功:[{}]", message.getMessageProperties().getDeliveryTag());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("处理用户数据失败出现异常: MessageProperties: [{}], 用户ID:[{}], Error:[{}]",
                message.getMessageProperties(), body, e.getMessage(), e);
            try {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            } catch (IOException exception) {
                log.error("接收用户消息退回出现异常: MessageProperties: [{}], 用户ID:[{}], Error:[{}]",
                    message.getMessageProperties(), body, e.getMessage(), e);
            }
        }
    }

    /**
     * 构建用户es对象
     *
     * @param userList                       {@link List}
     * @param organizationRepository         {@link OrganizationRepository}
     * @return {@link List}
     */
    @NotNull
    public static List<UserElasticSearchEntity> getUserElasticSearchEntity(List<UserEsPO> userList,
                                                                           OrganizationRepository organizationRepository) {
        List<UserElasticSearchEntity> userElasticSearchEntityList = new ArrayList<>();
        userList.forEach(user -> {
            UserElasticSearchEntity entity = UserElasticSearchEntity.builder().build();
            entity.setId(user.getId().toString());
            entity.setUsername(user.getUsername());
            entity.setEmail(user.getEmail());
            entity.setPhone(user.getPhone());
            entity.setPhoneAreaCode(user.getPhoneAreaCode());
            entity.setFullName(user.getFullName());
            entity.setNickName(user.getNickName());
            entity.setAvatar(user.getAvatar());
            if (Objects.nonNull(user.getStatus())) {
                entity.setStatus(user.getStatus().getCode());
            }
            if (Objects.nonNull(user.getDataOrigin())) {
                entity.setDataOrigin(user.getDataOrigin().getCode());
            }
            entity.setIdentitySourceId(user.getIdentitySourceId());
            entity.setEmailVerified(user.getEmailVerified());
            entity.setPhoneVerified(user.getPhoneVerified());
            entity.setAuthTotal(user.getAuthTotal());
            entity.setLastAuthIp(user.getLastAuthIp());
            if (Objects.nonNull(user.getLastUpdatePasswordTime())) {
                entity.setLastUpdatePasswordTime(user.getLastUpdatePasswordTime());
            }
            if (Objects.nonNull(user.getLastAuthTime())) {
                entity.setLastAuthTime(user.getLastAuthTime());
            }
            entity.setExpand(user.getExpand());
            entity.setExternalId(user.getExternalId());
            if (Objects.nonNull(user.getExpireDate())) {
                entity.setExpireDate(user.getExpireDate());
            }
            // 用户详情
            entity.setIdType(user.getIdType());
            entity.setIdCard(user.getIdCard());
            entity.setWebsite(user.getWebsite());
            entity.setAddress(user.getAddress());
            // 组织列表
            if (!CollectionUtils.isEmpty(user.getOrganizationIds())) {
                entity.setOrganizations(organizationRepository
                    .getOrganizationList(new ArrayList<>(user.getOrganizationIds())));
            }
            // 用户组
            List<UserElasticSearchEntity.UserGroup> userGroups = new ArrayList<>();
            if (!CollectionUtils.isEmpty(user.getUserGroups())) {
                userGroups.addAll(user.getUserGroups().entrySet().stream()
                    .map(group -> new UserElasticSearchEntity.UserGroup(group.getKey(),
                        group.getValue()))
                    .toList());
            }
            entity.setUserGroups(userGroups);
            userElasticSearchEntityList.add(entity);
        });

        return userElasticSearchEntityList;
    }

    /**
     * UserElasticSearchRepository
     */
    private final UserElasticSearchRepository userElasticSearchRepository;

    /**
     * UserRepository
     */
    private final UserRepository              userRepository;

    /**
     * OrganizationRepository
     */
    private final OrganizationRepository      organizationRepository;

    public UserMessageListener(UserElasticSearchRepository userElasticSearchRepository,
                               UserRepository userRepository,
                               OrganizationRepository organizationRepository) {
        this.userElasticSearchRepository = userElasticSearchRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
    }
}
