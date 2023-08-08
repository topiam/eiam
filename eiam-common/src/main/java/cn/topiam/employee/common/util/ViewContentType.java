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
package cn.topiam.employee.common.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import static org.springframework.http.MediaType.*;

/**
 * View Content Type
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/03/13 21:32
 */
public enum ViewContentType {
                             /**
                              * DEFAULT
                              */
                             DEFAULT("default", APPLICATION_OCTET_STREAM_VALUE),
                             /**
                              * JPG
                              */
                             JPG("jpg", IMAGE_JPEG_VALUE),
                             /**
                              * GIF
                              */
                             GIF("gif", IMAGE_GIF_VALUE),
                             /**
                              * JPG
                              */
                             JFIF("jfif", IMAGE_JPEG_VALUE),
                             /**
                              * PNG
                              */
                             PNG("png", IMAGE_PNG_VALUE),
                             /**
                              * ICO
                              */
                             ICO("ico", "image/vnd.microsoft.icon"),
                             /**
                              * JPEG
                              */
                             JPEG("jpeg", IMAGE_JPEG_VALUE),
                             /**
                              * SVG
                              */
                             SVG("svg", "image/svg+xml"),
                             /**
                              * JPE
                              */
                             JPE("jpe", IMAGE_JPEG_VALUE);

    private String suffix;
    private String type;

    public static String getContentType(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return DEFAULT.getType();
        }
        String suffix = FilenameUtils.getExtension(fileName);
        for (ViewContentType value : ViewContentType.values()) {
            if (suffix.equalsIgnoreCase(value.getSuffix())) {
                return value.getType();
            }
        }
        return DEFAULT.getType();
    }

    public static String getSuffix(String type) {
        for (ViewContentType value : ViewContentType.values()) {
            if (type.equalsIgnoreCase(value.getType())) {
                return value.getSuffix();
            }
        }
        return DEFAULT.getSuffix();
    }

    ViewContentType(String prefix, String type) {
        this.suffix = prefix;
        this.type = type;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getType() {
        return type;
    }
}
