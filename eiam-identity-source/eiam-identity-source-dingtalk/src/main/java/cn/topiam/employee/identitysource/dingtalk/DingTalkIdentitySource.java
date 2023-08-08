/*
 * eiam-identity-source-dingtalk - Employee Identity and Access Management
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
package cn.topiam.employee.identitysource.dingtalk;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.common.enums.identitysource.IdentitySourceProvider;
import cn.topiam.employee.common.util.RequestUtils;
import cn.topiam.employee.identitysource.core.AbstractDefaultIdentitySource;
import cn.topiam.employee.identitysource.core.client.IdentitySourceClient;
import cn.topiam.employee.identitysource.core.enums.IdentitySourceEventReceiveType;
import cn.topiam.employee.identitysource.core.processor.IdentitySourceEventPostProcessor;
import cn.topiam.employee.identitysource.core.processor.IdentitySourceSyncDeptPostProcessor;
import cn.topiam.employee.identitysource.core.processor.IdentitySourceSyncUserPostProcessor;
import cn.topiam.employee.identitysource.core.processor.modal.IdentitySourceEventProcessData;
import cn.topiam.employee.identitysource.dingtalk.enums.DingTalkEventType;
import cn.topiam.employee.identitysource.dingtalk.util.DingTalkEventCryptoUtils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 钉钉
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/9/21 21:59
 */
@Slf4j
public class DingTalkIdentitySource extends AbstractDefaultIdentitySource<DingTalkConfig> {

    public static final String ENCRYPT       = "encrypt";
    public static final String MSG_SIGNATURE = "msg_signature";
    public static final String TIMESTAMP     = "timestamp";
    public static final String NONCE         = "nonce";
    public static final String SUCCESS       = "success";

    /**
     * 回调
     *
     * @see [book events](https://open.dingtalk.com/document/orgapp-server/address-book-events)
     *
     * @param request  {@link HttpServletRequest}
     */
    @Override
    public Object event(HttpServletRequest request, String body) {
        LocalDateTime eventTime = LocalDateTime.now();
        Map<String, Object> params = RequestUtils.getParams(request);
        if (StringUtils.isNoneBlank(body)) {
            String encrypt = JSON.parseObject(body).getString(ENCRYPT);
            log.info("钉钉身份源 [{}] 回调入参: {}, encrypt: {}", getId(), JSON.toJSONString(params),
                encrypt);
            Object result = eventCallBack(eventTime, params, encrypt);
            log.debug("钉钉身份源 [{}] 回调返回: {}", getId(), JSON.toJSONString(result));
            return result;
        }
        return null;
    }

    /**
     * 1. 从http请求中获取加解密参数
     * 2. 使用加解密类型
     * Constant.OWNER_KEY 说明：
     * 1、开发者后台配置的订阅事件为应用级事件推送，此时OWNER_KEY为应用的APP_KEY。
     * 2、调用订阅事件接口订阅的事件为企业级事件推送，此时OWNER_KEY为：企 业的appkey（企业内部应用）或SUITE_KEY（三方应用）
     * @param syncMap {@link  Map}
     * @return {@link  Map}
     */
    private Object eventCallBack(LocalDateTime eventTime, Map<String, Object> syncMap,
                                 String encrypt) {
        try {
            DingTalkConfig config = getConfig();
            String msgSignature = (String) syncMap.get(MSG_SIGNATURE);
            String timeStamp = (String) syncMap.get(TIMESTAMP);
            String nonce = (String) syncMap.get(NONCE);
            DingTalkEventCryptoUtils eventCryptoUtils = new DingTalkEventCryptoUtils(
                config.getToken(), config.getAesKey(), config.getAppKey());
            String decryptMsg = eventCryptoUtils.getDecryptMsg(msgSignature, timeStamp, nonce,
                encrypt);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            DingTalkEventRequest callbackRequest = objectMapper.readValue(decryptMsg,
                DingTalkEventRequest.class);
            // 4. 根据EventType分类处理
            IdentitySourceEventReceiveType eventType = null;
            List<?> param = new ArrayList<>();
            List<String> idList = callbackRequest.getIdList();
            log.info("处理钉钉身份源 [{}] [{}] 事件,参数: [{}]", this.getId(),
                callbackRequest.getEventType().getName(), callbackRequest);
            switch (callbackRequest.getEventType()) {
                case CHECK_URL:
                    break;
                case USER_ADD_ORG:
                    eventType = IdentitySourceEventReceiveType.USER_ADD;
                    param = getData(idList, this.getIdentitySourceClient()::getUser);
                    break;
                case USER_MODIFY_ORG:
                case USER_ACTIVE_ORG:
                    eventType = IdentitySourceEventReceiveType.USER_MODIFY;
                    param = getData(idList, this.getIdentitySourceClient()::getUser);
                    break;
                case USER_LEAVE_ORG:
                    eventType = IdentitySourceEventReceiveType.USER_LEAVE;
                    param = idList;
                    break;
                case ORG_DEPT_CREATE:
                    eventType = IdentitySourceEventReceiveType.DEPT_CREATE;
                    param = getData(idList, this.getIdentitySourceClient()::getDept);
                    break;
                case ORG_DEPT_MODIFY:
                    eventType = IdentitySourceEventReceiveType.DEPT_MODIFY;
                    param = getData(idList, this.getIdentitySourceClient()::getDept);
                    break;
                case ORG_DEPT_REMOVE:
                    eventType = IdentitySourceEventReceiveType.DEPT_REMOVE;
                    param = idList;
                    break;
                default:
                    throw new IllegalArgumentException("钉钉身份提供商回调非法事件");
            }
            if (eventType != null) {
                IdentitySourceEventProcessData<?> data = new IdentitySourceEventProcessData<>(
                    getId(), param, IdentitySourceProvider.DINGTALK, eventTime, eventType);
                identitySourceEventPostProcessor.process(data);
            }
            // 5. 返回success的加密数据
            return eventCryptoUtils.getEncryptedMap(SUCCESS);
        } catch (Exception e) {
            log.error("钉钉身份源 [{}] 回调发生异常: {}", getId(), e.getMessage());
        }
        return Collections.emptyMap();
    }

    /**
     * 获取钉钉事件数据
     *
     * @param idList {@link List<String>}
     * @param function {@link Function}
     */
    private <T> List<T> getData(List<String> idList, Function<String, T> function) {
        List<T> list = new ArrayList<>();
        idList.forEach(item -> list.add(function.apply(item)));
        return list;
    }

    public DingTalkIdentitySource(String id, String name, DingTalkConfig config,
                                  IdentitySourceClient identitySourceClient,
                                  IdentitySourceSyncUserPostProcessor identitySourceSyncUserPostProcessor,
                                  IdentitySourceSyncDeptPostProcessor identitySourceSyncDeptPostProcessor,
                                  IdentitySourceEventPostProcessor identitySourceEventPostProcessor) {
        super(id, name, config, identitySourceClient, identitySourceSyncUserPostProcessor,
            identitySourceSyncDeptPostProcessor, identitySourceEventPostProcessor);
    }

    /**
     * 钉钉回调请求
     *
     * @author TopIAM
     * Created by support@topiam.cn on  2022/2/28 22:56
     */
    @Data
    public static class DingTalkEventRequest implements Serializable {

        @Serial
        private static final long serialVersionUID = 856826238722398023L;

        @JsonAlias(value = "CorpId")
        private String            corpId;
        @JsonAlias(value = "EventType")
        private DingTalkEventType eventType;
        @JsonAlias(value = "TimeStamp")
        private String            timeStamp;
        @JsonAlias(value = { "UserId", "DeptId" })
        private List<String>      idList;
        @JsonAlias(value = "OptStaffId")
        private String            optStaffId;

        public DingTalkEventRequest() {
        }
    }
}
