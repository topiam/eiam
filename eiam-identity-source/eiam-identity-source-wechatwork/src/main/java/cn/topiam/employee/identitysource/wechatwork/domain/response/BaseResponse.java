/*
 * eiam-identity-source-wechatwork - Employee Identity and Access Management
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
package cn.topiam.employee.identitysource.wechatwork.domain.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import cn.topiam.employee.identitysource.wechatwork.WeChatWorkConstant;

import lombok.Data;

/**
 * 企业微信返回
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022-02-11 23:39
 */
@Data
public class BaseResponse implements Serializable {
    /**
     * 出错返回码，为0表示成功，非0表示调用失败
     */
    @JsonProperty("errcode")
    private Integer errCode;
    /**
     * 返回码提示语
     */
    @JsonProperty("errmsg")
    private String  errMsg;

    public boolean isSuccess() {
        return WeChatWorkConstant.REQUEST_SUCCESS.equals(errCode);
    }
}
