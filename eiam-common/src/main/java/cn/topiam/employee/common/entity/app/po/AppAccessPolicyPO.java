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
package cn.topiam.employee.common.entity.app.po;

import cn.topiam.employee.common.entity.app.AppAccessPolicyEntity;
import cn.topiam.employee.common.enums.app.AppProtocol;
import cn.topiam.employee.common.enums.app.AppType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用授权策略po
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/2/10 22:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AppAccessPolicyPO extends AppAccessPolicyEntity {
    /**
     * 应用名称
     */
    private String      appName;

    /**
     * 模板
     */
    private String      appTemplate;

    /**
     * 协议
     */
    private AppProtocol appProtocol;

    /**
     * 应用类型
     */
    private AppType     appType;

    /**
     * 授权主体
     */
    private String      subjectName;
}
