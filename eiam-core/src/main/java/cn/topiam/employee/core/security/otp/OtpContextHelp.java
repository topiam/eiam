/*
 * eiam-core - Employee Identity and Access Management Program
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
package cn.topiam.employee.core.security.otp;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.stereotype.Component;

import cn.topiam.employee.common.enums.MailType;
import cn.topiam.employee.common.enums.MessageNoticeChannel;
import cn.topiam.employee.common.enums.SmsType;
import cn.topiam.employee.common.exception.OtpSendException;
import cn.topiam.employee.core.message.mail.MailMsgEventPublish;
import cn.topiam.employee.core.message.sms.SmsMsgEventPublish;
import cn.topiam.employee.support.exception.TopIamException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.core.context.SettingContextHelp.getCodeValidTime;
import static cn.topiam.employee.core.message.MsgVariable.TIME_TO_LIVE;
import static cn.topiam.employee.support.constant.EiamConstants.COLON;

/**
 * OtpUtils
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/16 22:21
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OtpContextHelp {
    /**
     * 发送
     *
     * @param recipient {@link String}
     * @param type      {@link String}
     * @param channel   {@link MessageNoticeChannel}
     */
    public void sendOtp(String recipient, String type, MessageNoticeChannel channel) {
        // 发送间隔
        RBucket<Boolean> intervalBucket = redissonClient
            .getBucket(getRedisKey(OTP_CODE_INTERVAL_PREFIX, recipient, type, channel));
        if (intervalBucket.isExists()) {
            throw new TopIamException(SEND_FREQUENTLY);
        } else {
            // 验证码
            String code = RandomStringUtils.randomNumeric(6);
            RScoredSortedSet<String> verifyCodeScored = redissonClient
                .getScoredSortedSet(getRedisKey(OTP_CODE_VALUE_PREFIX, recipient, type, channel));
            //过期时间
            Instant expireTime = Instant.now().plus(getCodeValidTime(), ChronoUnit.MINUTES);
            verifyCodeScored.expire(expireTime);
            try {
                removeExpireOtpCode(verifyCodeScored);
                // 验证码
                verifyCodeScored.add(expireTime.getEpochSecond(), code);
                // 发送间隔（默认1分钟）
                intervalBucket.set(true, TIME_TO_LIVE, TimeUnit.MINUTES);
                if (channel == MessageNoticeChannel.MAIL) {
                    // 发送邮件
                    mailMsgEventPublish.publishVerifyCode(recipient, MailType.getType(type), code);
                }
                if (channel == MessageNoticeChannel.SMS) {
                    // 发送短信
                    smsMsgEventPublish.publishVerifyCode(recipient, SmsType.getType(type), code);
                }
            } catch (Exception e) {
                //发送失败，删除
                verifyCodeScored.deleteAsync();
                intervalBucket.deleteAsync();
                throw new OtpSendException(e.getMessage());
            }
        }
    }

    /**
     * 删除过期验证码
     *
     * @param verifyCodeScored {@link RScoredSortedSet}
     */
    private void removeExpireOtpCode(RScoredSortedSet<String> verifyCodeScored) {
        int removeCount = verifyCodeScored.removeRangeByScore(Double.POSITIVE_INFINITY,
            Boolean.TRUE, getCurrentTime(), Boolean.TRUE);
        log.debug("删除 OTP 验证码计数: {}", removeCount);
    }

    /**
     * current time
     *
     * @return {@link long}
     */
    private long getCurrentTime() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 检查验证码
     *
     * @param type       {@link String}
     * @param channel    {@link String}
     * @param recipient  {@link String}
     * @param verifyCode {@link String}
     * @return {@link boolean}
     */
    public Boolean checkOtp(String type, MessageNoticeChannel channel, String recipient,
                            String verifyCode) {

        RScoredSortedSet<String> verifyCodeScored = redissonClient
            .getScoredSortedSet(getRedisKey(OTP_CODE_VALUE_PREFIX, recipient, type, channel));
        RAtomicLong verifyCodeFrequency = redissonClient
            .getAtomicLong(getRedisKey(OTP_CODE_CHECK_COUNT_PREFIX, recipient, type, channel));
        if (!verifyCodeScored.isExists()) {
            log.info("类型 [{}] 接受者 [{}]，未获取验证码", type, recipient);
            return false;
        }
        if (verifyCodeFrequency.isExists()) {
            long frequency = verifyCodeFrequency.incrementAndGet();
            if (frequency > FREQUENCY_THRESHOLD) {
                // 删除验证码
                verifyCodeScored.deleteAsync();
                verifyCodeFrequency.deleteAsync();
                log.error("类型 [{}] 接受者 [{}]，超出验证次数", type, recipient);
                return false;
            }
        } else {
            verifyCodeFrequency.set(1);
        }
        removeExpireOtpCode(verifyCodeScored);
        // 检查验证码
        if (!verifyCodeScored.contains(verifyCode)
            && verifyCodeScored.getScore(verifyCode) < getCurrentTime()) {
            log.error("类型 [{}] 接受者 [{}]，验证码不匹配或已过期", type, recipient);
            return false;
        }
        return true;
    }

    /**
     * redis key
     *
     * @param prefix    {@link String}
     * @param recipient {@link String}
     * @param type      {@link String}
     * @param channel   {@link MessageNoticeChannel}
     * @return {@link String}
     */
    @NotNull
    private String getRedisKey(String prefix, String recipient, String type,
                               MessageNoticeChannel channel) {
        String keyPrefix = cacheProperties.getRedis().getKeyPrefix();
        return keyPrefix + COLON + "otp" + COLON + prefix + COLON + channel.getCode() + COLON + type
               + COLON + recipient;
    }

    /**
     * 发送验证码频繁，请稍候重试
     */
    private static final String       SEND_FREQUENTLY             = "发送验证码频繁，请稍候重试";

    /**
     * 验证码 code 值前缀
     */
    public static final String        OTP_CODE_VALUE_PREFIX       = "code";

    /**
     * 验证码 间隔 值前缀
     */
    public static final String        OTP_CODE_INTERVAL_PREFIX    = "interval";
    /**
     * 验证码 校验次数 值前缀
     */
    public static final String        OTP_CODE_CHECK_COUNT_PREFIX = "check";
    /**
     * 验证码使用次数阀值
     */
    private static final long         FREQUENCY_THRESHOLD         = 3;

    /**
     * RedissonClient
     */
    private final RedissonClient      redissonClient;
    /**
     * 邮件消息发布
     */
    private final MailMsgEventPublish mailMsgEventPublish;
    /**
     * sms message publish
     */
    private final SmsMsgEventPublish  smsMsgEventPublish;

    /**
     * CacheProperties
     */
    private final CacheProperties     cacheProperties;
}
