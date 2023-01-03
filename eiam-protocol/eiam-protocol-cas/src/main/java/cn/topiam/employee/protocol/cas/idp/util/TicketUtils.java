/*
 * eiam-protocol-cas - Employee Identity and Access Management Program
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
package cn.topiam.employee.protocol.cas.idp.util;

import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/29 16:25
 */
public class TicketUtils {
    private static final char[] PRINTABLE_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ012345679"
        .toCharArray();

    private static String getNewString() {
        SecureRandom randomizer = new SecureRandom();
        byte[] random = new byte[20];
        randomizer.nextBytes(random);
        return convertBytesToString(random);
    }

    private static String convertBytesToString(byte[] random) {
        char[] output = new char[random.length];
        IntStream.range(0, random.length).forEach((i) -> {
            int index = Math.abs(random[i] % PRINTABLE_CHARACTERS.length);
            output[i] = PRINTABLE_CHARACTERS[index];
        });
        return new String(output);
    }

    private static AtomicLong count = new AtomicLong(0L);

    private static String getNextNumberAsString() {
        return Long.toString(getNextValue());
    }

    private static long getNextValue() {
        return count.compareAndSet(9223372036854775807L, 0L) ? 9223372036854775807L
            : count.getAndIncrement();
    }

    private static String getSuffix(String suffix) {
        if (StringUtils.isNotBlank(suffix)) {
            return suffix;
        }
        return getCasServerHostName();
    }

    private static String getCasServerHostName() {
        try {
            String hostName = InetAddress.getLocalHost().getCanonicalHostName();
            int index = hostName.indexOf(46);
            return index > 0 ? hostName.substring(0, index) : hostName;
        } catch (Exception var2) {
            throw new IllegalArgumentException("Host name could not be determined automatically.",
                var2);
        }
    }

    private static String generateTicket(String prefix, String suffix) {
        return prefix + "-" + getNextNumberAsString() + "-" + getNewString() + "-"
               + getSuffix(suffix);
    }

    public static String generateTicket(String prefix) {
        return generateTicket(prefix, null);
    }
}
