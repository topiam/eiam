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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cn.topiam.employee.common.entity.account.ThirdPartyUserEntity;

/**
 * 三方用户表
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/11/30 22:18
 */
@Repository
public interface ThirdPartyUserRepository extends JpaRepository<ThirdPartyUserEntity, String> {

    Optional<ThirdPartyUserEntity> findByOpenIdAndIdpId(String openId, String idpId);
}
