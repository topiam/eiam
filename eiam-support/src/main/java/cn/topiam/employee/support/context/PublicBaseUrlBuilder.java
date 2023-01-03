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
package cn.topiam.employee.support.context;

import org.springframework.util.Assert;

import lombok.Data;
import static cn.topiam.employee.support.constant.EiamConstants.COLON;

/**
 * 公共基础网址生成器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/10 22:57
 */
@Data
public class PublicBaseUrlBuilder {

    public static final String HTTPS = "https";
    public static final String HTTP  = "http";

    private String             scheme;

    private String             serverName;

    private int                port;

    public void setScheme(String scheme) {
        Assert.isTrue("http".equals(scheme) || "https".equals(scheme),
            () -> "Unsupported scheme '" + scheme + "'");
        this.scheme = scheme;
    }

    public String getUrl() {
        StringBuilder sb = new StringBuilder();
        Assert.notNull(this.scheme, "scheme cannot be null");
        Assert.notNull(this.serverName, "serverName cannot be null");
        sb.append(this.scheme).append("://").append(this.serverName);
        // 如果端口号不是该方案的标准，附加端口号
        if (this.port != (HTTP.equals(this.scheme) ? 80 : 443)) {
            sb.append(COLON).append(this.port);
        }
        return sb.toString();
    }

}
