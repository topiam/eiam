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
package cn.topiam.employee.core.dynamic;

import java.util.List;

import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import cn.topiam.employee.common.entity.account.UserElasticSearchEntity;
import cn.topiam.employee.common.entity.account.po.UserEsPO;
import cn.topiam.employee.common.repository.account.OrganizationRepository;
import cn.topiam.employee.common.repository.account.UserElasticSearchRepository;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.support.autoconfiguration.SupportProperties;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.trace.Trace;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.core.mq.UserMessageListener.getUserElasticSearchEntity;

/**
 * UserSyncTask
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/6/18 21:42
 */
@Slf4j
@RequiredArgsConstructor
public class UserSyncTask {

    @Lock(throwException = false)
    @Trace
    @Scheduled(fixedRate = 60000)
    public void syncUser() {
        try {
            long start = System.currentTimeMillis();
            log.info("同步用户数据到ES开始");
            IndexCoordinates userIndex = IndexCoordinates
                .of(supportProperties.getUser().getIndexPrefix());
            // 查询库中全部用户信息
            List<UserEsPO> userList = userRepository.getUserList(null);
            List<UserElasticSearchEntity> newUserElasticSearch = getUserElasticSearchEntity(
                userList, organizationRepository);
            List<UserElasticSearchEntity> saveUserList;
            int removeNumber = 0;
            // 存在索引，查询es用户数据
            List<UserElasticSearchEntity> oldUserElasticSearchList = userRepository
                .getAllUserElasticSearchEntity(userIndex);
            if (!CollectionUtils.isEmpty(oldUserElasticSearchList)) {
                // 要删除的数据
                List<String> removeUserIdList = oldUserElasticSearchList.stream()
                    .map(UserElasticSearchEntity::getId).filter(id -> newUserElasticSearch.stream()
                        .noneMatch(newUser -> newUser.getId().equals(id)))
                    .toList();
                removeNumber = removeUserIdList.size();
                // 删除
                if (!CollectionUtils.isEmpty(removeUserIdList)) {
                    userElasticSearchRepository.deleteAllById(removeUserIdList);
                }
                // 要更新的数据
                saveUserList = newUserElasticSearch.stream()
                    .filter(newUser -> oldUserElasticSearchList.stream()
                        .noneMatch(oldUser -> oldUser.equals(newUser)))
                    .toList();
            } else {
                saveUserList = newUserElasticSearch;
            }
            // 更新
            userElasticSearchRepository.saveAll(saveUserList);
            log.info("同步用户数据到ES成功, 更新:[{}], 删除:[{}], 耗时:[{}]s", saveUserList.size(), removeNumber,
                (System.currentTimeMillis() - start) / 1000);
        } catch (Exception e) {
            log.error("同步用户数据到ES异常:[{}]", e.getMessage(), e);
        }
    }

    /**
     * SupportProperties
     */
    private final SupportProperties           supportProperties;

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
}
