/*
 * eiam-console - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.console.converter.authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.ConstraintViolationException;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.querydsl.QPageRequest;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;

import cn.topiam.employee.authentication.common.config.IdentityProviderConfig;
import cn.topiam.employee.authentication.dingtalk.DingTalkIdpOauthConfig;
import cn.topiam.employee.authentication.dingtalk.DingTalkIdpScanCodeConfig;
import cn.topiam.employee.authentication.qq.QqIdpOauthConfig;
import cn.topiam.employee.authentication.wechat.WeChatIdpScanCodeConfig;
import cn.topiam.employee.authentication.wechatwork.WeChatWorkIdpScanCodeConfig;
import cn.topiam.employee.common.entity.authentication.IdentityProviderEntity;
import cn.topiam.employee.common.entity.authentication.QIdentityProviderEntity;
import cn.topiam.employee.common.enums.IdentityProviderType;
import cn.topiam.employee.console.pojo.query.authentication.IdentityProviderListQuery;
import cn.topiam.employee.console.pojo.result.authentication.IdentityProviderListResult;
import cn.topiam.employee.console.pojo.result.authentication.IdentityProviderResult;
import cn.topiam.employee.console.pojo.save.authentication.IdentityProviderCreateParam;
import cn.topiam.employee.console.pojo.update.authentication.IdpUpdateParam;
import cn.topiam.employee.core.context.ServerContextHelp;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.repository.page.domain.QueryDslRequest;
import cn.topiam.employee.support.validation.ValidationHelp;

/**
 * 身份提供商转换器
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/22 23:59
 */
@Mapper(componentModel = "spring")
public interface IdentityProviderConverter {

    /**
     * 认证源平台枚举转换器到认证源平台结果
     *
     * @param values {@link  List}
     * @return {@link List}
     */
    default Page<IdentityProviderListResult> entityConverterToIdentityProviderResult(org.springframework.data.domain.Page<IdentityProviderEntity> values) {
        Page<IdentityProviderListResult> result = new Page<>();
        ArrayList<IdentityProviderListResult> list = new ArrayList<>();
        for (IdentityProviderEntity entity : values.getContent()) {
            list.add(entityConverterToIdentityProviderResult(entity));
        }
        //@formatter:off
        result.setPagination(Page.Pagination.builder()
                .total(values.getTotalElements())
                .totalPages(values.getTotalPages())
                .current(values.getPageable().getPageNumber() + 1)
                .build());
        //@formatter:on
        result.setList(list);
        return result;
    }

    /**
     * 实体转认证源详情
     *
     * @param entity {@link  IdentityProviderEntity}
     * @return {@link  IdentityProviderListResult}
     */
    @Mapping(target = "desc", source = "type.desc")
    IdentityProviderListResult entityConverterToIdentityProviderResult(IdentityProviderEntity entity);

    /**
     * 保存入参转换为实体类
     *
     * @param param {@link IdentityProviderCreateParam}
     * @return {@link IdentityProviderEntity}
     */
    default IdentityProviderEntity identityProviderCreateParamConverterToEntity(IdentityProviderCreateParam param) {
        if (param == null) {
            return null;
        }
        if (!param.getCategory().getProviders().contains(param.getType())) {
            throw new TopIamException("认证源类型与认证源提供商不匹配");
        }
        try {
            IdentityProviderConfig identityProviderConfig = getIdentityProviderConfig(
                param.getType(), param.getConfig());
            ObjectMapper objectMapper = new ObjectMapper();
            // 指定序列化输入的类型
            objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
            //封装数据
            IdentityProviderEntity identityProviderEntity = new IdentityProviderEntity();
            identityProviderEntity.setName(param.getName());
            identityProviderEntity.setCode(
                org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric(32).toLowerCase());
            identityProviderEntity.setType(param.getType());
            identityProviderEntity.setCategory(param.getCategory());
            identityProviderEntity.setDisplayed(param.getDisplayed());
            identityProviderEntity.setEnabled(Boolean.TRUE);
            identityProviderEntity.setRemark(param.getRemark());
            //配置
            identityProviderEntity
                .setConfig(objectMapper.writeValueAsString(identityProviderConfig));
            return identityProviderEntity;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * entity转换为详细信息结果
     *
     * @param entity {@link IdentityProviderEntity}
     * @return {@link IdentityProviderListResult}
     */
    default IdentityProviderResult entityConverterToIdentityProviderDetailResult(IdentityProviderEntity entity) {
        if (entity == null) {
            return null;
        }
        IdentityProviderResult result = new IdentityProviderResult();
        if (entity.getId() != null) {
            result.setId(String.valueOf(entity.getId()));
        }
        result.setType(entity.getType());
        result.setDisplayed(entity.getDisplayed());
        result.setName(entity.getName());
        result.setRemark(entity.getRemark());
        //回调地址
        result.setRedirectUri(ServerContextHelp.getPortalPublicBaseUrl()
                              + entity.getType().getLoginPathPrefix() + "/" + entity.getCode());
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // 指定序列化输入的类型
            objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
            IdentityProviderConfig config = objectMapper.readValue(entity.getConfig(),
                IdentityProviderConfig.class);
            result.setConfig(config);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 查询身份源列表参数转Predicate
     *
     * @param query     {@link IdentityProviderListQuery}
     * @param pageModel {@link  PageModel}
     * @return {@link  QueryDslRequest}
     */
    default QueryDslRequest queryIdentityProviderListParamConvertToPredicate(IdentityProviderListQuery query,
                                                                             PageModel pageModel) {
        QueryDslRequest request = new QueryDslRequest();
        QIdentityProviderEntity queryEntity = QIdentityProviderEntity.identityProviderEntity;
        Predicate predicate = queryEntity.isNotNull();
        //查询条件
        //@formatter:off
        predicate = Objects.isNull(query.getCategory()) ? predicate : ExpressionUtils.and(predicate, queryEntity.category.eq(query.getCategory()));
        predicate = Objects.isNull(query.getName()) ? predicate : ExpressionUtils.and(predicate, queryEntity.name.eq(query.getName()));
        //@formatter:on
        request.setPredicate(predicate);
        //分页条件
        request.setPageRequest(QPageRequest.of(pageModel.getCurrent(), pageModel.getPageSize()));
        return request;
    }

    /**
     * 修改入参转换为实体类
     *
     * @param param {@link IdpUpdateParam}
     * @return {@link IdentityProviderEntity}
     */
    default IdentityProviderEntity identityProviderUpdateParamConverterToEntity(IdpUpdateParam param) {
        if (param == null) {
            return null;
        }
        IdentityProviderConfig identityProviderConfig = getIdentityProviderConfig(param.getType(),
            param.getConfig());
        ObjectMapper objectMapper = new ObjectMapper();
        // 指定序列化输入的类型
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        try {
            //封装数据
            IdentityProviderEntity identityProviderEntity = new IdentityProviderEntity();
            identityProviderEntity.setName(param.getName());
            identityProviderEntity.setType(param.getType());
            identityProviderEntity.setDisplayed(param.getDisplayed());
            identityProviderEntity.setRemark(param.getRemark());
            //配置
            identityProviderEntity
                .setConfig(objectMapper.writeValueAsString(identityProviderConfig));
            return identityProviderEntity;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取认证源配置
     *
     * @param type   {@link  IdentityProviderType}
     * @param config {@link  JSONObject}
     * @return {@link  IdentityProviderConfig}
     */
    default IdentityProviderConfig getIdentityProviderConfig(IdentityProviderType type,
                                                             JSONObject config) {
        //开始处理不同提供商的配置
        IdentityProviderConfig identityProviderConfig;
        switch (type) {
            //微信扫码
            case WECHAT_SCAN_CODE ->
                identityProviderConfig = config.to(WeChatIdpScanCodeConfig.class);
            //钉钉扫码
            case DINGTALK_SCAN_CODE ->
                identityProviderConfig = config.to(DingTalkIdpScanCodeConfig.class);
            //钉钉Oauth
            case DINGTALK_OAUTH -> identityProviderConfig = config.to(DingTalkIdpOauthConfig.class);
            //企业微信扫码
            case WECHATWORK_SCAN_CODE ->
                identityProviderConfig = config.to(WeChatWorkIdpScanCodeConfig.class);
            //QQ认证
            case QQ -> identityProviderConfig = config.to(QqIdpOauthConfig.class);
            default -> throw new TopIamException("不支持此身份提供商");
        }
        if (!Objects.nonNull(identityProviderConfig)) {
            throw new NullPointerException("提供商配置不能为空");
        }
        ValidationHelp.ValidationResult<?> validationResult = ValidationHelp
            .validateEntity(identityProviderConfig);
        if (validationResult.isHasErrors()) {
            throw new ConstraintViolationException(validationResult.getConstraintViolations());
        }
        return identityProviderConfig;
    }
}
