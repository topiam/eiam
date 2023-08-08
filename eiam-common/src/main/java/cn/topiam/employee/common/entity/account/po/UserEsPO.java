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
import java.util.Map;
import java.util.Set;

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
public class UserEsPO extends UserEntity {

    @Serial
    private static final long   serialVersionUID = 2330202241972458786L;

    /**
     * 证件类型
     */
    private String              idType;

    /**
     * 身份证号
     */
    private String              idCard;

    /**
     * 个人主页
     */
    private String              website;

    /**
     * 地址
     */
    private String              address;

    /**
     * 组织id列表
     */
    private Set<String>         organizationIds;

    /**
     * 静态用户组
     */
    private Map<String, String> userGroups;
}
