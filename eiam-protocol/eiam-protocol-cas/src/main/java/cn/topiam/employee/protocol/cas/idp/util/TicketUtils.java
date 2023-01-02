package cn.topiam.employee.protocol.cas.idp.util;

import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

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
