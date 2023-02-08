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
package cn.topiam.employee.core.configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;

public class CustomRedisSessionRepository extends RedisIndexedSessionRepository {

    /**
     * The default namespace for each key and channel in Redis used by Spring Session.
     */
    public static final String                    DEFAULT_NAMESPACE = "spring:session";

    /**
     * The namespace for every key used by Spring Session in Redis.
     */
    private String                                namespace         = DEFAULT_NAMESPACE + ":";

    private final RedisOperations<Object, Object> sessionRedisOperations;

    public CustomRedisSessionRepository(RedisOperations<Object, Object> sessionRedisOperations) {
        super(sessionRedisOperations);
        this.sessionRedisOperations = sessionRedisOperations;
    }

    @Override
    public Map findByIndexNameAndIndexValue(String indexName, String indexValue) {
        if (!PRINCIPAL_NAME_INDEX_NAME.equals(indexName)) {
            return Collections.emptyMap();
        }
        String principalKey = getPrincipalKey(indexValue);

        Set<Object> sessionIds = this.sessionRedisOperations.boundSetOps(principalKey).members();
        Map<String, Session> sessions = new HashMap<>(sessionIds.size());
        for (Object id : sessionIds) {
            // TODO
            Session session = findById((String) id);
            if (session != null) {
                sessions.put(session.getId(), session);
            }
        }
        return sessions;
    }

    String getPrincipalKey(String principalName) {
        return this.namespace + "index:"
               + FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME + ":" + principalName;
    }

    @Override
    public void setRedisKeyNamespace(String namespace) {
        super.setRedisKeyNamespace(namespace);
        this.namespace = namespace.trim() + ":";
    }
}
