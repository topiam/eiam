/*
 * eiam-identity-source-feishu - Employee Identity and Access Management
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
package cn.topiam.employee.identitysource.feishu.domain.request;

import java.io.Serializable;

import com.alibaba.fastjson2.annotation.JSONField;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * access token入参
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022-02-17 23:59
 */
@Data
@AllArgsConstructor
public class GetAccessTokenRequest implements Serializable {
    /**
     * appid
     */
    @JSONField(name = "app_id")
    private String appId;
    /**
     * secret
     */
    @JSONField(name = "app_secret")
    private String appSecret;
}
