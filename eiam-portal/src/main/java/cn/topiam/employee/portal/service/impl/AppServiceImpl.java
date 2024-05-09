/*
 * eiam-portal - Employee Identity and Access Management
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
package cn.topiam.employee.portal.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import cn.topiam.employee.common.entity.account.UserGroupMemberEntity;
import cn.topiam.employee.common.entity.account.po.OrganizationMemberPO;
import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.entity.app.po.AppGroupPO;
import cn.topiam.employee.common.entity.app.query.AppGroupQuery;
import cn.topiam.employee.common.entity.app.query.GetAppListQuery;
import cn.topiam.employee.common.repository.account.OrganizationMemberRepository;
import cn.topiam.employee.common.repository.account.UserGroupMemberRepository;
import cn.topiam.employee.common.repository.app.AppGroupRepository;
import cn.topiam.employee.common.repository.app.AppRepository;
import cn.topiam.employee.portal.converter.AppConverter;
import cn.topiam.employee.portal.converter.AppGroupConverter;
import cn.topiam.employee.portal.pojo.result.AppGroupListResult;
import cn.topiam.employee.portal.pojo.result.GetAppListResult;
import cn.topiam.employee.portal.service.AppService;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.security.userdetails.Application;
import cn.topiam.employee.support.security.userdetails.UserDetails;
import cn.topiam.employee.support.security.util.SecurityUtils;

/**
 * AppService
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/7/6 22:36
 */
@Service
public class AppServiceImpl implements AppService {

    /**
     * 获取应用列表
     *
     * @param appQuery {@link GetAppListQuery}
     * @return {@link Page}
     */
    @Override
    public Page<GetAppListResult> getAppList(GetAppListQuery appQuery, PageModel pageModel) {
        UserDetails userDetails = SecurityUtils.getCurrentUser();
        Stream<Application> stream = userDetails.getApplications().stream();
        //应用名
        if (org.apache.commons.lang3.StringUtils.isNoneBlank(appQuery.getName())) {
            stream = stream
                .filter(t -> t.getName().toUpperCase().contains(appQuery.getName().toUpperCase()));
        }
        //分组ID
        if (Objects.nonNull(appQuery.getGroupId())) {
            stream = stream.filter(t -> Objects.nonNull(t.getGroups()))
                .filter(t -> t.getGroups().stream().anyMatch(
                    applicationGroup -> applicationGroup.getId().equals(appQuery.getGroupId())));
        }
        List<Application> applications = stream.toList();
        List<AppEntity> appList = appRepository
            .findByIdIn(applications.stream().map(Application::getId).toList());
        //查询映射
        return appConverter.entityConvertToAppListResult(appList, pageModel);
    }

    /**
     * 查询应用分组
     *
     * @param appGroupQuery {@link AppGroupQuery}
     * @return {@link AppGroupListResult}
     */
    @Override
    public List<AppGroupListResult> getAppGroupList(AppGroupQuery appGroupQuery) {
        //查询映射
        String userId = SecurityUtils.getCurrentUserId();
        List<AppGroupPO> list = appGroupRepository.getAppGroupList(getSubjectIds(userId),
            appGroupQuery);
        return appGroupConverter.entityConvertToAppGroupListResult(list);
    }

    /**
     * 获取应用数量
     *
     * @param groupId {@link String}
     * @return {@link Integer}
     */
    @Override
    public Long getAppCount(String groupId) {
        UserDetails userDetails = SecurityUtils.getCurrentUser();
        Stream<Application> stream = userDetails.getApplications().stream();
        return stream.filter(t -> Objects.nonNull(t.getGroups())).filter(t -> t.getGroups().stream()
            .anyMatch(applicationGroup -> applicationGroup.getId().equals(groupId))).count();
    }

    /**
     * 获取应用数量
     *
     * @return {@link Long}
     */
    @Override
    public Long getAppCount() {
        UserDetails userDetails = SecurityUtils.getCurrentUser();
        Stream<Application> stream = userDetails.getApplications().stream();
        return stream.count();
    }

    @NotNull
    private List<String> getSubjectIds(String userId) {
        List<String> paramList = Lists.newArrayList();
        //当前用户加入的用户组Id
        List<String> groupIdList = userGroupMemberRepository.findByUserId(userId).stream()
            .map(UserGroupMemberEntity::getGroupId).toList();
        //当前用户加入的组织id
        List<String> orgId = organizationMemberRepository.findAllPoByUserId(userId).stream()
            .map(OrganizationMemberPO::getOrgId).toList();
        paramList.addAll(groupIdList);
        paramList.addAll(orgId);
        paramList.add(userId);
        return paramList;
    }

    /**
     * AppRepository
     */
    private final AppRepository                appRepository;

    /**
     * AppConverter
     */
    private final AppConverter                 appConverter;

    /**
     * AppGroupRepository
     */
    private final AppGroupRepository           appGroupRepository;

    /**
     * AppGroupConverter
     */
    private final AppGroupConverter            appGroupConverter;

    /**
     * UserGroupMemberRepository
     */
    private final UserGroupMemberRepository    userGroupMemberRepository;

    /**
     * OrganizationMemberRepository
     */
    private final OrganizationMemberRepository organizationMemberRepository;

    public AppServiceImpl(AppRepository appRepository, AppConverter appConverter,
                          AppGroupRepository appGroupRepository,
                          AppGroupConverter appGroupConverter,
                          UserGroupMemberRepository userGroupMemberRepository,
                          OrganizationMemberRepository organizationMemberRepository) {
        this.appRepository = appRepository;
        this.appConverter = appConverter;
        this.appGroupRepository = appGroupRepository;
        this.appGroupConverter = appGroupConverter;
        this.userGroupMemberRepository = userGroupMemberRepository;
        this.organizationMemberRepository = organizationMemberRepository;
    }

}
