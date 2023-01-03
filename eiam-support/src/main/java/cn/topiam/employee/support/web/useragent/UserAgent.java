/*
 * eiam-support - Employee Identity and Access Management Program
 * Copyright © 2020-2023 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.support.web.useragent;

import java.io.Serializable;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户代理
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/13 21:32
 */
@Data
@Accessors(chain = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class UserAgent implements Serializable {
    public UserAgent() {
    }

    String browser;
    String browserType;
    String browserMajorVersion;
    String deviceType;
    String platform;
    String platformVersion;
    /**
     * 自定义字段
     */
    String renderingEngineMaker;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
