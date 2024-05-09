/*
 * eiam-common - Employee Identity and Access Management
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
package cn.topiam.employee.common.repository.app;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.entity.app.AppGroupAssociationEntity;

/**
 * 应用组成员
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023年09月06日22:03:18
 */
@Repository
public interface AppGroupAssociationRepository extends
                                               JpaRepository<AppGroupAssociationEntity, String>,
                                               AppGroupAssociationRepositoryCustomized {

    /**
     * 根据应用组ID和应用ID删除
     *
     * @param groupId {@link String}
     * @param appId  {@link String}
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query("DELETE FROM AppGroupAssociationEntity ass WHERE ass.groupId = :groupId and ass.app.id = :appId ")
    void deleteByGroupIdAndAppId(@Param("groupId") String groupId, @Param("appId") String appId);

    /**
     * 根据应用ID删除关联
     *
     * @param appEntity {@link  AppEntity}
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteByApp(AppEntity appEntity);

    /**
     * 根据应用组ID删除关联
     *
     * @param groupId {@link  String}
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteAllByGroupId(@Param(value = "groupId") String groupId);

    /**
     * 根据应用ID 查询关联信息
     *
     * @param appId {@link String}
     * @return {@link List}
     */
    @Query("SELECT ass.groupId FROM AppGroupAssociationEntity ass WHERE ass.app.id = :appId")
    List<String> findGroupIdByAppId(String appId);

    /**
     * 按应用 ID
     *
     * @param appEntity {@link AppEntity}
     * @return {@link List}
     */
    List<AppGroupAssociationEntity> findByApp(AppEntity appEntity);
}
