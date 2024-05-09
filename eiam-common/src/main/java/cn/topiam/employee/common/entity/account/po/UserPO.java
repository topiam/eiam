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
package cn.topiam.employee.common.entity.account.po;

import java.io.Serial;

import cn.topiam.employee.common.entity.account.UserEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户 PO
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/2/10 22:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserPO extends UserEntity {

    @Serial
    private static final long serialVersionUID = 2330202241971348786L;

    /**
     * 组织机构显示目录
     */
    private String            orgDisplayPath;

    public UserPO(UserEntity user, String orgDisplayPath) {
        super.setId(user.getId());
        super.setUsername(user.getUsername());
        super.setPassword(user.getPassword());
        super.setEmail(user.getEmail());
        super.setPhone(user.getPhone());
        super.setPhoneAreaCode(user.getPhoneAreaCode());
        super.setFullName(user.getFullName());
        super.setNickName(user.getNickName());
        super.setAvatar(user.getAvatar());
        super.setStatus(user.getStatus());
        super.setDataOrigin(user.getDataOrigin());
        super.setEmailVerified(user.getEmailVerified());
        super.setPhoneVerified(user.getPhoneVerified());
        super.setAuthTotal(user.getAuthTotal());
        super.setLastAuthIp(user.getLastAuthIp());
        super.setLastAuthTime(user.getLastAuthTime());
        super.setExpand(user.getExpand());
        super.setExternalId(user.getExternalId());
        super.setExpireDate(user.getExpireDate());
        super.setCreateBy(user.getCreateBy());
        super.setCreateTime(user.getCreateTime());
        super.setUpdateBy(user.getUpdateBy());
        super.setUpdateTime(user.getUpdateTime());
        super.setRemark(user.getRemark());
        setOrgDisplayPath(orgDisplayPath);
    }
}
