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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.google.common.collect.Sets;

import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.entity.account.UserGroupMemberEntity;
import cn.topiam.employee.common.entity.account.po.OrganizationPO;
import cn.topiam.employee.common.entity.app.po.AppPO;
import cn.topiam.employee.common.repository.account.OrganizationRepository;
import cn.topiam.employee.common.repository.account.UserGroupMemberRepository;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.common.repository.app.AppRepository;
import cn.topiam.employee.portal.service.UserService;
import cn.topiam.employee.support.security.userdetails.Application;
import cn.topiam.employee.support.security.userdetails.Group;
import cn.topiam.employee.support.security.userdetails.Organization;
import cn.topiam.employee.support.security.userdetails.UserDetails;

/**
 * UserService
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2024/3/2 22:22
 */
@Service
public class UserServiceImpl implements UserService {
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    /**
     * 获取用户详情
     *
     * @param userId {@link String}
     * @return {@link UserDetails}
     */
    @Override
    public UserDetails getUserDetails(String userId) {
        Optional<UserEntity> optional = userRepository.findById(userId);
        return optional.map(this::getUserDetails).orElse(null);
    }

    /**
     * 获取用户详情
     *
     * @param user {@link UserEntity}
     * @return {@link UserEntity}
     */
    @Override
    public UserDetails getUserDetails(UserEntity user) {
        //@formatter:off
        UserDetails details = user.toUserDetails(Sets.newHashSet());
        // 获取用户组信息
        List<UserGroupMemberEntity> userGroupMemberList = userGroupMemberRepository.findByUserId(user.getId());
        details.setGroups(userGroupMemberList.stream().map((group) -> new Group(group.getGroupId())).toList());
        // 获取组织信息
        List<OrganizationPO> organizationList = organizationRepository.getOrganizationList(user.getId());
        details.setOrganizations(organizationList.stream().filter(OrganizationPO::getEnabled).map(org -> new Organization(org.getId(), org.getPath())).toList());
        // 获取用户拥有的应用
        List<String> subjectIds = new ArrayList<>();
        subjectIds.add(user.getId());
        subjectIds.addAll(details.getGroups().stream().map(Group::getId).toList());
        subjectIds.addAll(details.getOrganizations().stream().map(Organization::getId).toList());
        List<AppPO> appList = appRepository.getAppList(subjectIds);
        details.setApplications(appList.stream().map(app -> new Application(app.getId(), app.getCode(), app.getName(),app.getGroup())).toList());
        //@formatter:on
        return details;
    }

    /**
     * 根据用户名、手机号、邮箱查询用户
     *
     * @return {@link UserEntity}
     */
    @Override
    public Optional<UserEntity> findByUsernameOrPhoneOrEmail(String keyword) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 异步执行查询
        CompletableFuture<Optional<UserEntity>> findByUsernameFuture = CompletableFuture
            .supplyAsync(() -> userRepository.findByUsername(keyword));

        CompletableFuture<Optional<UserEntity>> findByPhoneFuture = CompletableFuture
            .supplyAsync(() -> userRepository.findByPhone(keyword));

        CompletableFuture<Optional<UserEntity>> findByEmailFuture = CompletableFuture
            .supplyAsync(() -> userRepository.findByEmail(keyword));

        // 等待所有查询完成，并处理结果
        CompletableFuture<Optional<UserEntity>> combinedFuture = CompletableFuture
            .allOf(findByUsernameFuture, findByPhoneFuture, findByEmailFuture).thenApply(voided -> {
                try {
                    if (findByUsernameFuture.get().isPresent()) {
                        return findByUsernameFuture.get();
                    } else if (findByPhoneFuture.get().isPresent()) {
                        return findByPhoneFuture.get();
                    } else {
                        return findByEmailFuture.get();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    return Optional.empty();
                }
            });

        try {
            // 等待最终结果
            return combinedFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            // 处理异常
            return Optional.empty();
        } finally {
            // 结束计时
            stopWatch.stop();
            logger.info("根据用户名、手机号、邮箱查询用户耗时:{}ms", stopWatch.getTotalTimeMillis());
        }
    }

    /**
     * UserRepository
     */
    private final UserRepository            userRepository;

    /**
     * UserGroupMemberRepository
     */
    private final UserGroupMemberRepository userGroupMemberRepository;

    /**
     * OrganizationRepository
     */
    private final OrganizationRepository    organizationRepository;

    /**
     * AppRepository
     */
    private final AppRepository             appRepository;

    public UserServiceImpl(UserRepository userRepository,
                           UserGroupMemberRepository userGroupMemberRepository,
                           OrganizationRepository organizationRepository,
                           AppRepository appRepository) {
        this.userRepository = userRepository;
        this.userGroupMemberRepository = userGroupMemberRepository;
        this.organizationRepository = organizationRepository;
        this.appRepository = appRepository;
    }
}
