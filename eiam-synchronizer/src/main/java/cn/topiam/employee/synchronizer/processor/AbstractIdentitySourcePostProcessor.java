/*
 * eiam-synchronizer - Employee Identity and Access Management
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
package cn.topiam.employee.synchronizer.processor;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;

import cn.topiam.employee.common.entity.account.OrganizationEntity;
import cn.topiam.employee.common.entity.account.OrganizationMemberEntity;
import cn.topiam.employee.common.entity.account.UserDetailEntity;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.account.po.UserPO;
import cn.topiam.employee.common.entity.identitysource.IdentitySourceEntity;
import cn.topiam.employee.common.entity.identitysource.config.StrategyConfig;
import cn.topiam.employee.common.enums.MailType;
import cn.topiam.employee.common.enums.SyncStatus;
import cn.topiam.employee.common.enums.UserStatus;
import cn.topiam.employee.common.enums.ViewContentType;
import cn.topiam.employee.common.enums.identitysource.IdentitySourceActionType;
import cn.topiam.employee.common.enums.identitysource.IdentitySourceProvider;
import cn.topiam.employee.common.repository.identitysource.IdentitySourceRepository;
import cn.topiam.employee.common.repository.identitysource.IdentitySourceSyncHistoryRepository;
import cn.topiam.employee.common.repository.identitysource.IdentitySourceSyncRecordRepository;
import cn.topiam.employee.common.storage.Storage;
import cn.topiam.employee.core.message.MsgVariable;
import cn.topiam.employee.core.message.mail.MailMsgEventPublish;
import cn.topiam.employee.identitysource.core.domain.User;
import cn.topiam.employee.identitysource.core.domain.UserDetail;
import cn.topiam.employee.identitysource.core.exception.IdentitySourceNotExistException;
import cn.topiam.employee.support.security.password.PasswordGenerator;
import cn.topiam.employee.support.security.userdetails.DataOrigin;

import lombok.Builder;
import lombok.Cleanup;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.EntityManager;
import static cn.topiam.employee.common.enums.UserStatus.DISABLED;
import static cn.topiam.employee.common.enums.UserStatus.ENABLED;
import static cn.topiam.employee.common.enums.identitysource.IdentitySourceProvider.DINGTALK;
import static cn.topiam.employee.common.enums.identitysource.IdentitySourceProvider.FEISHU;
import static cn.topiam.employee.support.constant.EiamConstants.SYSTEM_DEFAULT_USER_NAME;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/9/25 00:32
 */
@Slf4j
public class AbstractIdentitySourcePostProcessor {

    /**
     * 对比用户
     *
     * @param user {@link User}
     * @param entity {@link UserPO}
     * @return {@link Boolean}
     */
    protected Boolean equalsUser(User user, UserEntity entity, String identitySourceId) {
        //@formatter:off
        if (user == null || user.getUserDetail() == null) {
            return false;
        }
        UserDetail userDetail = user.getUserDetail();
        return StringUtils.equals(entity.getExternalId(), user.getUserId())
                && StringUtils.equals(Objects.isNull(entity.getIdentitySourceId()) ? null:entity.getIdentitySourceId(), identitySourceId)
                && StringUtils.equals(entity.getPhoneAreaCode(), user.getPhoneAreaCode())
                //上游返回""，数据库有唯一索引，所以""在保存的时候被变成null，这里匹配需要注意
                && StringUtils.equals(StringUtils.isBlank(entity.getEmail()) ? null : entity.getEmail(), StringUtils.isBlank(user.getEmail()) ? null : user.getEmail())
                //上游返回""，数据库有唯一索引，所以""在保存的时候被变成null，这里匹配需要注意
                && StringUtils.equals(StringUtils.isBlank(entity.getPhone()) ? null : entity.getPhone(), StringUtils.isBlank(user.getPhone()) ? null : user.getPhone())
                && Objects.equals(UserStatus.ENABLED.equals(entity.getStatus()), user.getActive())
                && StringUtils.equals(entity.getFullName(),userDetail.getName())
                && StringUtils.equals(entity.getNickName(),userDetail.getNickName());
        //@formatter:on
    }

    /**
     * 设置用户最新值
     *
     * @param user {@link User}
     * @param entity {@link UserEntity}
     */
    protected void setUpdateUser(User user, UserEntity entity,
                                 IdentitySourceEntity identitySource) {
        if (StringUtils.isEmpty(entity.getAvatar())) {
            try {
                convertAvatar(user.getAvatar(), entity);
            } catch (Exception e) {
                log.error("修改用户, 同步头像发生异常: 用户: [{}], 异常: [{}]", user.getUserId(), e.getMessage());
            }
        }

        StrategyConfig.User strategyConfig = identitySource.getStrategyConfig().getUser();
        //@formatter:off
        boolean enabled = false;
        if (!Objects.isNull(strategyConfig)) {
            enabled = Boolean.TRUE.equals(strategyConfig.getEnabled());
        }
        entity.setStatus(enabled || user.getActive() ? ENABLED : DISABLED);
        entity.setPhone(StringUtils.isEmpty(user.getPhone()) ? null : user.getPhone());
        entity.setPhoneAreaCode(user.getPhoneAreaCode());
        entity.setEmail(StringUtils.isEmpty(user.getEmail()) ? null : user.getEmail());
        entity.setNickName(user.getUserDetail().getNickName());
        entity.setFullName(user.getUserDetail().getName());
        entity.setIdentitySourceId(identitySource.getId());
    }

    /**
     * 上游用户转系统用户Entity
     *
     * @param user {@link User} 上游用户
     * @return {@link UserDetailEntity}
     */
    protected UserEntity thirdPartyUserToUserEntity(User user,
                                                    IdentitySourceEntity identitySource) {
        DataOrigin dataOrigin = getDataOrigin(identitySource.getProvider());
        StrategyConfig.User strategyConfig = identitySource.getStrategyConfig().getUser();
        //@formatter:off
        boolean enabled = false;
        String defaultPassword = null;
        if (!Objects.isNull(strategyConfig)) {
            enabled = Boolean.TRUE.equals(strategyConfig.getEnabled());
            defaultPassword = Objects.toString(strategyConfig.getDefaultPassword());
        }
        if (StringUtils.isBlank(defaultPassword)) {
            defaultPassword = passwordGenerator.generatePassword();
        }
        //封装
        UserEntity entity = new UserEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setExternalId(user.getUserId());
        String avatar = user.getAvatar();
        if (StringUtils.isNotEmpty(avatar)) {
            try {
                convertAvatar(user.getAvatar(), entity);
            }
            catch (Exception e) {
                log.error("创建用户, 同步头像发生异常: 用户: [{}], 异常: [{}]", user.getUserId(), e.getMessage());
            }
        }
        entity.setEmail(StringUtils.isEmpty(user.getEmail()) ? null : user.getEmail());
        entity.setPhone(StringUtils.isEmpty(user.getPhone()) ? null : user.getPhone());
        entity.setFullName(user.getUserDetail().getName());
        entity.setNickName(user.getUserDetail().getNickName());
        entity.setExpireDate(LocalDate.of(2116,12,31));
        entity.setPhoneAreaCode(user.getPhoneAreaCode());
        //配置为启用，上游未启用，用户为禁用
        entity.setStatus(enabled || user.getActive() ? ENABLED : DISABLED);
        entity.setUsername(user.getUserId());
        entity.setDataOrigin(dataOrigin.getType());
        entity.setIdentitySourceId(identitySource.getId());
        entity.setPassword(passwordEncoder.encode(defaultPassword));
        entity.setPasswordPlainText(defaultPassword);

        //必须字段
        entity.setCreateBy(SYSTEM_DEFAULT_USER_NAME);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateBy(SYSTEM_DEFAULT_USER_NAME);
        entity.setUpdateTime(LocalDateTime.now());
        return entity;
        //@formatter:on
    }

    /**
     * 上游头像转换为iam系统地址
     *
     * @param avatar {@link String}
     * @param entity {@link UserEntity}
     */
    private void convertAvatar(String avatar, UserEntity entity) throws Exception {
        // 上传至对象存储空间
        URL url = new URL(avatar);
        URLConnection urlConnection = url.openConnection();
        String contentType = urlConnection.getContentType();
        String name = FilenameUtils.getBaseName(avatar);
        String suffix = ViewContentType.getSuffix(contentType);
        @Cleanup
        InputStream inputStream = urlConnection.getInputStream();
        if (Objects.nonNull(inputStream)) {
            entity.setAvatar(storage.upload(name + "." + suffix, inputStream));
        }
    }

    /**
     * 创建组织用户关系
     *
     * @param createOrganizationMembers {@link Set}
     * @param currentUser {@link UserEntity}
     * @param organization {@link OrganizationEntity}
     */
    protected static void createOrganizationMember(Set<OrganizationMemberEntity> createOrganizationMembers,
                                                   UserEntity currentUser,
                                                   OrganizationEntity organization) {
        OrganizationMemberEntity entity = new OrganizationMemberEntity(UUID.randomUUID().toString(),
            organization.getId(), currentUser.getId());
        entity.setCreateBy(SYSTEM_DEFAULT_USER_NAME);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateBy(SYSTEM_DEFAULT_USER_NAME);
        entity.setUpdateTime(LocalDateTime.now());
        createOrganizationMembers.add(entity);
    }

    /**
     * 获取身份源策略配置
     *
     * @param id {@link String}
     * @return {@link IdentitySourceEntity}
     */
    protected IdentitySourceEntity getIdentitySource(String id) {
        Optional<IdentitySourceEntity> optional = identitySourceRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IdentitySourceNotExistException();
        }
        return optional.get();
    }

    /**
     * 获取身份源策略配置
     *
     * @param id {@link String}
     * @return {@link StrategyConfig}
     */
    protected StrategyConfig getStrategyConfig(String id) {
        IdentitySourceEntity entity = getIdentitySource(id);
        return entity.getStrategyConfig();
    }

    /**
     * 获取根组织扩展ID
     *
     * @return {@link String}
     */
    protected String getRootExternalId(IdentitySourceProvider provider) {
        if (DINGTALK.equals(provider)) {
            return "1";
        }
        if (FEISHU.equals(provider)) {
            return "0";
        }
        throw new IdentitySourceNotExistException();
    }

    /**
     * 获取数据来源
     *
     * @param provider {@link IdentitySourceProvider}
     * @return {@link String}
     */
    protected DataOrigin getDataOrigin(IdentitySourceProvider provider) {
        if (DINGTALK.equals(provider)) {
            return DataOrigin.DING_TALK;
        }
        if (FEISHU.equals(provider)) {
            return DataOrigin.FEI_SHU;
        }
        throw new IdentitySourceNotExistException();
    }

    /**
     * 批量发送短信邮件欢迎信息(密码通知)
     *
     * @param users {@link List<UserEntity>} 新增用户信息
     */
    protected void publishMessage(List<UserEntity> users) {
        try {
            users.forEach(thirdPartyUser -> {
                log.info("批量发送邮件欢迎信息:[{}]开始, email: {}, phone: {}", thirdPartyUser.getUsername(),
                    thirdPartyUser.getEmail(), thirdPartyUser.getPhone());
                if (StringUtils.isNotEmpty(thirdPartyUser.getEmail())) {
                    Map<String, Object> parameter = new HashMap<>(16);
                    parameter.put(MsgVariable.PASSWORD, thirdPartyUser.getPasswordPlainText());
                    mailMsgEventPublish.publish(MailType.RESET_PASSWORD_CONFIRM,
                        thirdPartyUser.getEmail(), parameter);
                }
            });
        } catch (Exception e) {
            log.error("身份源同步用户发送邮件异常:[{}]", e.getMessage(), e);
        }
    }

    @Data
    @Builder
    protected static class SkipUser {

        /**
         * 用户
         */
        private UserEntity               user;

        /**
         * 操作类型
         */
        private IdentitySourceActionType actionType;

        /**
         * 状态
         */
        private SyncStatus               status;

        /**
         * 原因
         */
        private String                   reason;
    }

    /**
     * PasswordEncoder
     */
    private final PasswordEncoder                       passwordEncoder;

    /**
     * 密码生成器
     */
    private final PasswordGenerator                     passwordGenerator;

    /**
     * 邮件消息事件发布
     */
    private final MailMsgEventPublish                   mailMsgEventPublish;

    /**
     * Storage
     */
    private final Storage                               storage;

    /**
     * TransactionDefinition
     */
    protected final TransactionDefinition               transactionDefinition;

    /**
     * PlatformTransactionManager
     */
    protected final PlatformTransactionManager          platformTransactionManager;

    /**
     * EntityManager
     */
    protected final EntityManager                       entityManager;

    /**
     * 身份源 Repository
     */
    protected final IdentitySourceRepository            identitySourceRepository;

    /**
     * IdentitySourceSyncHistoryRepository
     */
    protected final IdentitySourceSyncHistoryRepository identitySourceSyncHistoryRepository;

    /**
     * IdentitySourceSyncRecordRepository
     */
    protected final IdentitySourceSyncRecordRepository  identitySourceSyncRecordRepository;

    public AbstractIdentitySourcePostProcessor(MailMsgEventPublish mailMsgEventPublish,
                                               PasswordEncoder passwordEncoder,
                                               PasswordGenerator passwordGenerator,
                                               TransactionDefinition transactionDefinition,
                                               PlatformTransactionManager platformTransactionManager,
                                               EntityManager entityManager,
                                               IdentitySourceRepository identitySourceRepository,
                                               IdentitySourceSyncHistoryRepository identitySourceSyncHistoryRepository,
                                               IdentitySourceSyncRecordRepository identitySourceSyncRecordRepository,
                                               Storage storage) {
        this.mailMsgEventPublish = mailMsgEventPublish;
        this.passwordEncoder = passwordEncoder;
        this.passwordGenerator = passwordGenerator;
        this.transactionDefinition = transactionDefinition;
        this.platformTransactionManager = platformTransactionManager;
        this.entityManager = entityManager;
        this.identitySourceRepository = identitySourceRepository;
        this.identitySourceSyncHistoryRepository = identitySourceSyncHistoryRepository;
        this.identitySourceSyncRecordRepository = identitySourceSyncRecordRepository;
        this.storage = storage;
    }

}
