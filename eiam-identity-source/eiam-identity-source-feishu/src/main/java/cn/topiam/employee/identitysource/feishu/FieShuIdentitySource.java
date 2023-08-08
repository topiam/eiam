/*
 * eiam-identity-source-feishu - Employee Identity and Access Management
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
package cn.topiam.employee.identitysource.feishu;

import java.time.LocalDateTime;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.common.enums.identitysource.IdentitySourceProvider;
import cn.topiam.employee.identitysource.core.AbstractDefaultIdentitySource;
import cn.topiam.employee.identitysource.core.client.IdentitySourceClient;
import cn.topiam.employee.identitysource.core.enums.IdentitySourceEventReceiveType;
import cn.topiam.employee.identitysource.core.processor.IdentitySourceEventPostProcessor;
import cn.topiam.employee.identitysource.core.processor.IdentitySourceSyncDeptPostProcessor;
import cn.topiam.employee.identitysource.core.processor.IdentitySourceSyncUserPostProcessor;
import cn.topiam.employee.identitysource.core.processor.modal.IdentitySourceEventProcessData;
import cn.topiam.employee.identitysource.feishu.enums.FeiShuEventType;
import cn.topiam.employee.identitysource.feishu.util.FeiShuEventDecryptUtils;

import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 飞书身份源
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/9/21 21:04
 */
@Slf4j
public class FieShuIdentitySource extends AbstractDefaultIdentitySource<FeiShuConfig> {
    public static final String CHALLENGE     = "challenge";
    public static final String TOKEN         = "token";
    public static final String ENCRYPT       = "encrypt";
    public static final String TYPE          = "type";
    public static final String USER_ID       = "user_id";
    public static final String DEPARTMENT_ID = "open_department_id";
    public static final String HEADER        = "header";
    public static final String EVENT_TYPE    = "event_type";
    public static final String EVENT         = "event";
    public static final String OBJECT        = "object";

    /**
     * 回调
     *
     * @param request  {@link HttpServletRequest}
     * @param body {@link String}
     */
    @Override
    public Object event(HttpServletRequest request, String body) {
        LocalDateTime eventTime = LocalDateTime.now();
        log.debug("飞书身份源 [{}] 事件回调入参: {}", getId(), body);
        JSONObject result = eventCallBack(eventTime, JSON.parseObject(body));
        log.debug("飞书身份源 [{}] 事件回调返回: {}", getId(), JSON.toJSONString(result));
        return result;
    }

    /**
     * 飞书事件事件回调
     *
     * @param json {@link JSONObject}
     * @return 返回第三方平台结果
     */
    private JSONObject eventCallBack(LocalDateTime eventTime, JSONObject json) {
        try {
            FeiShuConfig config = getConfig();
            String decrypt = FeiShuEventDecryptUtils.decrypt(config.getEncryptKey(),
                json.getString(ENCRYPT));
            JSONObject params = JSON.parseObject(decrypt);
            String token = params.containsKey(TOKEN) ? params.getString(TOKEN)
                : params.getJSONObject(HEADER).getString(TOKEN);
            String type = params.containsKey(TYPE) ? params.getString(TYPE)
                : params.getJSONObject(HEADER).getString(EVENT_TYPE);
            if (config.getVerificationToken().equals(token)) {
                FeiShuEventType feiShuEventType = FeiShuEventType.getType(type);
                log.debug("处理飞书身份源 [{}] 执行: [{}]事件", this.getId(), feiShuEventType);
                IdentitySourceEventReceiveType eventType;
                List<?> param;
                switch (feiShuEventType) {
                    case URL_VERIFICATION:
                        return new JSONObject().fluentPut(CHALLENGE, params.getString(CHALLENGE));
                    case USER_ADD_ORG:
                        eventType = IdentitySourceEventReceiveType.USER_ADD;
                        param = getUser(getId(params, USER_ID));
                        break;
                    case USER_MODIFY_ORG:
                        eventType = IdentitySourceEventReceiveType.USER_MODIFY;
                        param = getUser(getId(params, USER_ID));
                        break;
                    case USER_LEAVE_ORG:
                        eventType = IdentitySourceEventReceiveType.USER_LEAVE;
                        param = getIdList(getId(params, USER_ID));
                        break;
                    case ORG_DEPT_CREATE:
                        eventType = IdentitySourceEventReceiveType.DEPT_CREATE;
                        param = getDept(getId(params, DEPARTMENT_ID));
                        break;
                    case ORG_DEPT_MODIFY:
                        eventType = IdentitySourceEventReceiveType.DEPT_MODIFY;
                        param = getDept(getId(params, DEPARTMENT_ID));
                        break;
                    case ORG_DEPT_REMOVE:
                        eventType = IdentitySourceEventReceiveType.DEPT_REMOVE;
                        param = getIdList(getId(params, DEPARTMENT_ID));
                        break;
                    default:
                        throw new IllegalArgumentException("飞书身份提供商事件回调非法事件");
                }
                IdentitySourceEventProcessData<?> data = new IdentitySourceEventProcessData<>(
                    getId(), param, IdentitySourceProvider.FEISHU, eventTime, eventType);
                identitySourceEventPostProcessor.process(data);
                return new JSONObject().fluentPut(CHALLENGE, params.getString(CHALLENGE));
            }
        } catch (Exception e) {
            log.error("飞书身份源 [{}] 事件回调发生异常: {}", getId(), e.getMessage());
        }
        return new JSONObject();
    }

    private String getId(JSONObject params, String name) {
        return params.getJSONObject(EVENT).getJSONObject(OBJECT).getString(name);
    }

    public FieShuIdentitySource(String id, String name, FeiShuConfig config,
                                IdentitySourceClient identitySourceClient,
                                IdentitySourceSyncUserPostProcessor identitySourceSyncUserPostProcessor,
                                IdentitySourceSyncDeptPostProcessor identitySourceSyncDeptPostProcessor,
                                IdentitySourceEventPostProcessor identitySourceEventPostProcessor) {
        super(id, name, config, identitySourceClient, identitySourceSyncUserPostProcessor,
            identitySourceSyncDeptPostProcessor, identitySourceEventPostProcessor);
    }

}
