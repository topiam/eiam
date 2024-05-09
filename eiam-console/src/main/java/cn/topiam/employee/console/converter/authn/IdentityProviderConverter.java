/*
 * eiam-console - Employee Identity and Access Management
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
package cn.topiam.employee.console.converter.authn;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.jpa.domain.Specification;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.authentication.alipay.AlipayIdentityProviderOAuth2Config;
import cn.topiam.employee.authentication.common.IdentityProviderCategory;
import cn.topiam.employee.authentication.common.IdentityProviderType;
import cn.topiam.employee.authentication.common.client.IdentityProviderConfig;
import cn.topiam.employee.authentication.dingtalk.DingTalkIdentityProviderOAuth2Config;
import cn.topiam.employee.authentication.feishu.FeiShuIdentityProviderOAuth2Config;
import cn.topiam.employee.authentication.gitee.GiteeIdentityProviderOAuth2Config;
import cn.topiam.employee.authentication.github.GithubIdentityProviderOAuth2Config;
import cn.topiam.employee.authentication.qq.QqIdentityProviderOAuth2Config;
import cn.topiam.employee.authentication.wechat.WeChatIdentityProviderOAuth2Config;
import cn.topiam.employee.authentication.wechatwork.WeChatWorkIdentityProviderOAuth2Config;
import cn.topiam.employee.common.entity.authn.IdentityProviderEntity;
import cn.topiam.employee.console.pojo.query.authn.IdentityProviderListQuery;
import cn.topiam.employee.console.pojo.result.authn.IdentityProviderListResult;
import cn.topiam.employee.console.pojo.result.authn.IdentityProviderResult;
import cn.topiam.employee.console.pojo.save.authn.IdentityProviderCreateParam;
import cn.topiam.employee.console.pojo.update.authn.IdpUpdateParam;
import cn.topiam.employee.core.context.ContextService;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.validation.ValidationUtils;

import jakarta.persistence.criteria.Predicate;
import jakarta.validation.ConstraintViolationException;
import static cn.topiam.employee.authentication.common.IdentityProviderType.*;
import static cn.topiam.employee.common.entity.authn.IdentityProviderEntity.CATEGORY_FIELD_NAME;
import static cn.topiam.employee.common.entity.authn.IdentityProviderEntity.NAME_FIELD_NAME;
import static cn.topiam.employee.support.repository.base.BaseEntity.LAST_MODIFIED_TIME;

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
        cn.topiam.employee.support.repository.page.domain.Page<IdentityProviderListResult> result = new cn.topiam.employee.support.repository.page.domain.Page<>();
        ArrayList<IdentityProviderListResult> list = new ArrayList<>();
        for (IdentityProviderEntity entity : values.getContent()) {
            list.add(entityConverterToIdentityProviderResult(entity));
        }
        //@formatter:off
        result.setPagination(cn.topiam.employee.support.repository.page.domain.Page.Pagination.builder()
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
    @Mapping(target = "desc", expression = "java(cn.topiam.employee.authentication.common.IdentityProviderType.getIdentityProviderType(entity.getType()).desc())")
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
        IdentityProviderCategory category = IdentityProviderCategory.getType(param.getCategory());
        if (!category.getProviders().stream().map(IdentityProviderType::value).toList()
            .contains(param.getType())) {
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
            IdentityProviderEntity entity = new IdentityProviderEntity();
            entity.setName(param.getName());
            entity.setCode(RandomStringUtils.randomAlphanumeric(32).toLowerCase());
            entity.setType(param.getType());
            entity.setCategory(param.getCategory());
            entity.setDisplayed(param.getDisplayed());
            entity.setEnabled(Boolean.TRUE);
            entity.setRemark(param.getRemark());
            //配置
            entity.setConfig(objectMapper.writeValueAsString(identityProviderConfig));
            return entity;
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
        result.setRedirectUri(ContextService.getPortalPublicBaseUrl()
                              + getIdentityProviderType(entity.getType()).getLoginPathPrefix() + "/"
                              + entity.getCode());
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
     * @param listQuery     {@link IdentityProviderListQuery}
     * @return {@link  Specification}
     */
    default Specification<IdentityProviderEntity> queryIdentityProviderListParamConvertToSpecification(IdentityProviderListQuery listQuery) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(listQuery.getName())) {
                predicates.add(cb.like(root.get(NAME_FIELD_NAME), "%" + listQuery.getName() + "%"));
            }
            if (Objects.nonNull(listQuery.getCategory())) {
                predicates.add(cb.equal(root.get(CATEGORY_FIELD_NAME), listQuery.getCategory()));
            }
            query.where(predicates.toArray(new Predicate[0]));
            query.orderBy(cb.desc(root.get(LAST_MODIFIED_TIME)));
            return query.getRestriction();
        };
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
    default IdentityProviderConfig getIdentityProviderConfig(String type, JSONObject config) {
        //开始处理不同提供商的配置
        IdentityProviderConfig identityProviderConfig;
        //微信扫码
        if (type.equals(WECHAT.value())) {
            identityProviderConfig = config.to(WeChatIdentityProviderOAuth2Config.class);
        }
        //钉钉OAuth
        else if (type.equals(DINGTALK_OAUTH.value())) {
            identityProviderConfig = config.to(DingTalkIdentityProviderOAuth2Config.class);
            //企业微信扫码
        } else if (type.equals(WECHAT_WORK_OAUTH.value())) {
            identityProviderConfig = config.to(WeChatWorkIdentityProviderOAuth2Config.class);
            //QQ认证
        } else if (type.equals(QQ_OAUTH.value())) {
            identityProviderConfig = config.to(QqIdentityProviderOAuth2Config.class);
            //飞书认证
        } else if (type.equals(FEISHU_OAUTH.value())) {
            identityProviderConfig = config.to(FeiShuIdentityProviderOAuth2Config.class);
            //GITHUB认证
        } else if (type.equals(GITHUB_OAUTH.value())) {
            identityProviderConfig = config.to(GithubIdentityProviderOAuth2Config.class);
        }
        //Gitee认证
        else if (type.equals(GITEE_OAUTH.value())) {
            identityProviderConfig = config.to(GiteeIdentityProviderOAuth2Config.class);
        }
        //支付宝认证
        else if (type.equals(ALIPAY_OAUTH.value())) {
            identityProviderConfig = config.to(AlipayIdentityProviderOAuth2Config.class);
        } else {
            throw new TopIamException("不支持此身份提供商");
        }
        if (!Objects.nonNull(identityProviderConfig)) {
            throw new NullPointerException("提供商配置不能为空");
        }
        ValidationUtils.ValidationResult<?> validationResult = ValidationUtils
            .validateEntity(identityProviderConfig);
        if (validationResult.isHasErrors()) {
            throw new ConstraintViolationException(validationResult.getConstraintViolations());
        }
        return identityProviderConfig;
    }
}
