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
package cn.topiam.employee.core.security.util;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import cn.topiam.employee.common.entity.account.UserDetailEntity;
import cn.topiam.employee.common.entity.account.UserEntity;
import cn.topiam.employee.common.exception.UserNotFoundException;
import cn.topiam.employee.common.repository.account.UserDetailRepository;
import cn.topiam.employee.common.repository.account.UserRepository;
import cn.topiam.employee.support.context.ApplicationContextHelp;

/**
 * 用户工具
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/13 21:50
 */
public final class UserUtils {
    private static final Logger logger = LoggerFactory.getLogger(UserUtils.class);

    public static UserEntity getUser() {
        return getUser(cn.topiam.employee.support.security.util.SecurityUtils.getCurrentUserId());
    }

    public static UserEntity getUser(String userId) {
        Optional<UserEntity> optional = ApplicationContextHelp.getBean(UserRepository.class)
            .findById(Long.valueOf(userId));
        if (optional.isPresent()) {
            return optional.get();
        }
        SecurityContextHolder.clearContext();
        logger.error("根据用户ID: [{}] 未查询到用户信息", userId);
        throw new UserNotFoundException();
    }

    public static UserDetailEntity getUserDetails() {
        return getUserDetails(
            cn.topiam.employee.support.security.util.SecurityUtils.getCurrentUserId());
    }

    public static UserDetailEntity getUserDetails(String userId) {
        Optional<UserDetailEntity> optional = ApplicationContextHelp
            .getBean(UserDetailRepository.class).findByUserId(Long.valueOf(userId));
        if (optional.isPresent()) {
            return optional.get();
        }
        SecurityContextHolder.clearContext();
        logger.error("根据用户ID: [{}] 未查询到用户信息", userId);
        throw new UserNotFoundException();
    }
}
