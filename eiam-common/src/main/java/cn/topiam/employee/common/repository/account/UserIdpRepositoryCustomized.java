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
package cn.topiam.employee.common.repository.account;

import java.util.Optional;

import cn.topiam.employee.common.entity.account.po.UserIdpBindPo;
import cn.topiam.employee.support.repository.page.domain.Page;

/**
 * UserIdp Repository Customized
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/12/29 21:27
 */
public interface UserIdpRepositoryCustomized {

    /**
     * 根据身份源ID和openId查询
     *
     * @param idpId  {@link  String}
     * @param openId {@link  String}
     * @return {@link Optional}
     */
    Optional<UserIdpBindPo> findByIdpIdAndOpenId(String idpId, String openId);

    /**
     * 根据身份源ID和userId查询
     *
     * @param idpId  {@link  String}
     * @param userId {@link  String}
     * @return {@link Optional}
     */
    Optional<UserIdpBindPo> findByIdpIdAndUserId(String idpId, Long userId);

    /**
     * 查询用户身份提供商绑定
     *
     * @param userId     {@link  Long}
     * @return {@link Page}
     */
    Iterable<UserIdpBindPo> getUserIdpBindList(Long userId);
}
