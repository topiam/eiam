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
package cn.topiam.employee.core.security.session;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import org.springframework.boot.autoconfigure.session.RedisSessionProperties;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.util.Assert;

import cn.topiam.employee.core.security.userdetails.UserDetails;
import static cn.topiam.employee.support.constant.EiamConstants.COLON;

/**
 * TopIamSessionBackedSessionRegistry
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/9/3 22:55
 */
public class TopIamSessionBackedSessionRegistry<T extends Session>
                                               extends SpringSessionBackedSessionRegistry<T> {

    /**
     * get all principals
     *
     * @return {@link List}
     */
    @Override
    public List<Object> getAllPrincipals() {
        //names
        Set<String> names = getAllPrincipalNames();
        //session infos
        List<SessionInformation> infos = new ArrayList<>();
        //根据用户名查询session，包括过期
        for (String s : names) {
            infos.addAll(super.getAllSessions(s, true));
        }
        return new ArrayList<>(getTopIamUserDetails(infos));
    }

    /**
     * get all principals
     *
     * @return {@link List}
     */
    public List<Object> getPrincipals(String username) {
        Assert.notNull(username, "用户名不能为空!");
        //names
        Boolean hasKey = stringRedisTemplate.hasKey(getPrincipalKey(username));
        if (Boolean.FALSE.equals(hasKey)) {
            return new ArrayList<>();
        }
        //根据用户名查询session，包括过期
        List<SessionInformation> infos = new ArrayList<>(super.getAllSessions(username, true));
        //根据session处理
        return new ArrayList<>(getTopIamUserDetails(infos));
    }

    private List<Object> getTopIamUserDetails(List<SessionInformation> infos) {
        List<SessionDetails> details = new ArrayList<>();
        for (SessionInformation information : infos) {
            //根据session id 获取缓存信息
            Session session = sessionRepository.findById(information.getSessionId());
            // session 为空，或者 session过期，跳过
            if (Objects.isNull(session) || information.isExpired()) {
                continue;
            }
            try {
                //转换为security context
                SecurityContext securityContext = session.getAttribute(SPRING_SECURITY_CONTEXT);
                //转为实体
                UserDetails principal = (UserDetails) securityContext.getAuthentication()
                    .getPrincipal();
                SessionDetails sessionDetails = new SessionDetails(principal.getId(),
                    principal.getUsername());
                //last request
                Instant instant = information.getLastRequest().toInstant();
                ZoneId zoneId = ZoneId.systemDefault();
                //最后请求时间
                LocalDateTime lastRequestTime = instant.atZone(zoneId).toLocalDateTime();
                sessionDetails.setLastRequestTime(lastRequestTime);
                //登录时间
                sessionDetails.setLoginTime(principal.getLoginTime());
                //登录时间
                sessionDetails.setAuthType(principal.getAuthType());
                //用户类型
                sessionDetails.setUserType(principal.getUserType());
                //地理位置
                sessionDetails.setGeoLocation(principal.getGeoLocation());
                //用户代理
                sessionDetails.setUserAgent(principal.getUserAgent());
                //会话ID
                sessionDetails.setSessionId(information.getSessionId());
                details.add(sessionDetails);
            } catch (NullPointerException ignored) {
            }
        }
        //处理
        return new ArrayList<>(details);
    }

    /**
     * 获取主体 key
     *
     * @param principalName {@link String}
     * @return {@link String}
     */
    String getPrincipalKey(String principalName) {
        return getPrincipalKeyPrefix() + principalName;
    }

    /**
     * 获取主体 key
     *
     * @return {@link String}
     */
    String getPrincipalKeyPrefix() {
        return redisSessionProperties.getNamespace() + COLON + "index" + COLON
               + FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME + COLON;
    }

    /**
     * 获取所有主体名称
     *
     * @return {@link  Set}
     */
    Set<String> getAllPrincipalNames() {
        Set<String> keys = new HashSet<>();
        stringRedisTemplate.execute((RedisCallback<Object>) connection -> {
            Cursor<byte[]> cursor = connection.scan(
                ScanOptions.scanOptions().match(getPrincipalKeyPrefix() + "*").count(1000).build());
            while (cursor.hasNext()) {
                String key = new String(cursor.next()).replaceAll(getPrincipalKeyPrefix(), "");
                keys.add(key);
            }
            return keys;
        });
        return keys;
    }

    /**
     * remove session
     *
     * @param sessionId {@link String}
     */
    @Override
    public void removeSessionInformation(String sessionId) {
        sessionRepository.deleteById(sessionId);
    }

    private static final String                       SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";
    /**
     * RedisSessionProperties
     */
    private final RedisSessionProperties              redisSessionProperties;

    private final StringRedisTemplate                 stringRedisTemplate;
    /**
     * FindByIndexNameSessionRepository
     */
    private final FindByIndexNameSessionRepository<?> sessionRepository;

    public TopIamSessionBackedSessionRegistry(FindByIndexNameSessionRepository<T> sessionRepository,
                                              RedisSessionProperties redisSessionProperties,
                                              StringRedisTemplate stringRedisTemplate) {
        super(sessionRepository);
        this.redisSessionProperties = redisSessionProperties;
        this.sessionRepository = sessionRepository;
        this.stringRedisTemplate = stringRedisTemplate;
    }
}
