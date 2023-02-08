/*
 * eiam-portal - Employee Identity and Access Management Program
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
package cn.topiam.employee.portal.pojo.result;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import cn.topiam.employee.common.enums.CaptchaProviderType;

import lombok.Builder;
import lombok.Data;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * LoginConfigResult
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/13 21:29
 */
@Builder
@Data
public class LoginConfigResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 7255002979319970337L;

    /**
     * idps
     */
    @Parameter(description = "IDPS")
    private List<Idps>        idps;

    /**
     * 行为验证码提供商
     */
    @Parameter(description = "行为验证码提供商")
    private Captcha           captcha;

    @Data
    @Builder
    public static class Captcha {
        /**
         * 应用ID
         */
        private String              appId;
        /**
         * 类型
         */
        private CaptchaProviderType type;
    }

    @Data
    public static class Idps implements Serializable {
        @Serial
        private static final long serialVersionUID = -6482651783349719888L;

        /**
         * CODE
         */
        @Schema(description = "CODE")
        private String            code;

        /**
         * name
         */
        @Parameter(description = "名称")
        private String            name;

        /**
         * 提供商
         */
        @Parameter(description = "提供商")
        private String            type;

        /**
         * 提供商类型
         */
        @Parameter(description = "提供商类型")
        private String            category;
    }
}
