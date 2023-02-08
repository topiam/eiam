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
package cn.topiam.employee.support.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

import com.google.common.net.InetAddresses;

import lombok.extern.slf4j.Slf4j;

/**
 * IpUtil
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2019-01-10 19:50
 */
@Slf4j
public class IpUtils {

    private static final String UNKNOWN = "Unknown";

    /**
     * 是否是Ip地址
     *
     * @param ip ip地址
     * @return 是否是IP
     */
    public static boolean isInternalIp(String ip) {
        InetAddress address = InetAddresses.forString(ip);
        return isInternalIp(address.getAddress()) || ip.startsWith("127");
    }

    /**
     * 是否是内部IP
     *
     * @param addr {@link Byte}
     * @return true false
     */
    private static boolean isInternalIp(byte[] addr) {
        final byte b0 = addr[0];
        final byte b1 = addr[1];
        // 10.x.x.x/8
        final byte section1 = 0x0A;
        // 172.16.x.x/12
        final byte section2 = (byte) 0xAC;
        final byte section3 = (byte) 0x10;
        final byte section4 = (byte) 0x1F;
        // 192.168.x.x/16
        final byte section5 = (byte) 0xC0;
        final byte section6 = (byte) 0xA8;
        switch (b0) {
            case section1:
                return true;
            case section2:
                if (b1 >= section3 && b1 <= section4) {
                    return true;
                }
            case section5:
                return b1 == section6;
            default:
                return false;
        }
    }

    /**
     * 获取用户真实IP
     *
     * @param request {@link HttpServletRequest}
     * @return {@link String} ip地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        try {
            if (request == null) {
                return UNKNOWN;
            }
            String ip = request.getHeader("x-original-forwarded-for");
            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("x-forwarded-for");
            }
            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Forwarded-For");
            }
            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Real-IP");
            }
            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
                String local = "127.0.0.1";
                String noAddressSpecified = "0:0:0:0:0:0:0:1";
                if (org.apache.commons.lang3.StringUtils.equalsAny(ip, local, noAddressSpecified)) {
                    //根据网卡取本机配置的IP
                    ip = InetAddress.getLocalHost().getHostAddress();
                    ip = org.apache.commons.lang3.StringUtils.defaultString(ip, local);
                }
            }
            //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            return org.apache.commons.lang3.StringUtils.substringBefore(ip, ",");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取主机名
     *
     * @return String
     */
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ignored) {
        }
        return UNKNOWN;
    }

    public static String getHostIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ignored) {
        }
        return "127.0.0.1";
    }
}
