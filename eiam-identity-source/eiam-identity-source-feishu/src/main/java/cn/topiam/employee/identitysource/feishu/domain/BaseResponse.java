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
package cn.topiam.employee.identitysource.feishu.domain;

import java.io.Serializable;

import lombok.Data;

/**
 * @author TopIAM
 * Created by support@topiam.cn on 2022-02-17 22:00
 */
@Data
public class BaseResponse implements Serializable {
    /**
     * 错误码，非 0 表示失败
     */
    private int    code;
    /**
     * 错误描述
     */
    private String msg;

    public boolean isSuccess() {
        return code == 0;
    }
}
