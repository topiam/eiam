/*
 * eiam-console - Employee Identity and Access Management Program
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
package cn.topiam.employee.console.converter.identitysource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;

import cn.topiam.employee.common.constants.CommonConstants;
import cn.topiam.employee.common.entity.identitysource.IdentitySourceEntity;
import cn.topiam.employee.common.entity.identitysource.QIdentitySourceEntity;
import cn.topiam.employee.common.enums.identitysource.IdentitySourceProvider;
import cn.topiam.employee.console.pojo.query.identity.IdentitySourceListQuery;
import cn.topiam.employee.console.pojo.result.identitysource.IdentitySourceConfigGetResult;
import cn.topiam.employee.console.pojo.result.identitysource.IdentitySourceGetResult;
import cn.topiam.employee.console.pojo.result.identitysource.IdentitySourceListResult;
import cn.topiam.employee.console.pojo.save.identitysource.IdentitySourceConfigSaveParam;
import cn.topiam.employee.console.pojo.save.identitysource.IdentitySourceCreateParam;
import cn.topiam.employee.console.pojo.update.identity.IdentitySourceUpdateParam;
import cn.topiam.employee.core.context.ServerContextHelp;
import cn.topiam.employee.identitysource.core.IdentitySourceConfig;
import cn.topiam.employee.identitysource.dingtalk.DingTalkConfig;
import cn.topiam.employee.identitysource.feishu.FeiShuConfig;
import cn.topiam.employee.identitysource.wechatwork.WeChatWorkConfig;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.repository.page.domain.QueryDslRequest;
import cn.topiam.employee.support.validation.ValidationHelp;

/**
 * 身份源转换器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/2/13 21:37
 */
@Mapper(componentModel = "spring")
public interface IdentitySourceConverter {

    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 身份源平台枚举转换器到身份源平台结果
     *
     * @param values {@link IdentitySourceEntity}
     * @return {@link List}
     */
    default cn.topiam.employee.support.repository.page.domain.Page<IdentitySourceListResult> entityConverterToIdentitySourceListResult(Page<IdentitySourceEntity> values) {

        cn.topiam.employee.support.repository.page.domain.Page<IdentitySourceListResult> result = new cn.topiam.employee.support.repository.page.domain.Page<>();
        if (!CollectionUtils.isEmpty(values.getContent())) {
            ArrayList<IdentitySourceListResult> list = new ArrayList<>();
            for (IdentitySourceEntity entity : values.getContent()) {
                list.add(entityConverterToIdentitySourceListResult(entity));
            }

            //@formatter:off
            result.setPagination(cn.topiam.employee.support.repository.page.domain.Page.Pagination.builder()
                    .total(values.getTotalElements())
                    .totalPages(values.getTotalPages())
                    .current(values.getPageable().getPageNumber() + 1)
                    .build());
            //@formatter:on
            result.setList(list);
        }
        return result;
    }

    /**
     * 身份源平台枚举转换器到身份源平台结果
     *
     * @param value {@link IdentitySourceEntity}
     * @return {@link List}
     */
    @Mapping(target = "desc", source = "provider.desc")
    @Mapping(target = "provider", source = "provider.code")
    @Mapping(target = "icon", ignore = true)
    IdentitySourceListResult entityConverterToIdentitySourceListResult(IdentitySourceEntity value);

    /**
     * 创建入参转换为实体类
     *
     * @param param {@link IdentitySourceCreateParam}
     * @return {@link IdentitySourceEntity}
     */
    @Mapping(target = "code", expression = "java(org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric(32).toLowerCase())")
    @Mapping(target = "configured", expression = "java(Boolean.FALSE)")
    @Mapping(target = "enabled", expression = "java(Boolean.TRUE)")
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "basicConfig", expression = "java(\"{}\")")
    @Mapping(target = "jobConfig", expression = "java(new cn.topiam.employee.common.entity.identitysource.config.JobConfig())")
    @Mapping(target = "strategyConfig", expression = "java(new cn.topiam.employee.common.entity.identitysource.config.StrategyConfig())")
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "remark", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    IdentitySourceEntity createParamConverterToEntity(IdentitySourceCreateParam param);

    /**
     * entity转换为详细信息结果
     *
     * @param entity {@link IdentitySourceEntity}
     * @return {@link IdentitySourceGetResult}
     */
    default IdentitySourceGetResult entityConverterToIdentitySourceGetResult(IdentitySourceEntity entity) {
        if (entity == null) {
            return null;
        }
        IdentitySourceGetResult identitySourceGetResult = new IdentitySourceGetResult();
        if (entity.getId() != null) {
            identitySourceGetResult.setId(String.valueOf(entity.getId()));
        }
        identitySourceGetResult.setName(entity.getName());
        identitySourceGetResult.setProvider(entity.getProvider());
        identitySourceGetResult.setEnabled(entity.getEnabled());
        identitySourceGetResult.setConfigured(entity.getConfigured());
        identitySourceGetResult.setRemark(entity.getRemark());
        return identitySourceGetResult;
    }

    /**
     * 保存入参转换为实体类
     *
     * @param param {@link IdentitySourceCreateParam}
     * @return {@link IdentitySourceEntity}
     */
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "configured", ignore = true)
    @Mapping(target = "strategyConfig", ignore = true)
    @Mapping(target = "basicConfig", ignore = true)
    @Mapping(target = "jobConfig", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    IdentitySourceEntity updateParamConverterToEntity(IdentitySourceUpdateParam param);

    /**
     * 保存配置参数转entity
     *
     * @param param {@link  IdentitySourceConfigSaveParam}
     * @return {@link  IdentitySourceEntity}
     */
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "configured", expression = "java(Boolean.TRUE)")
    @Mapping(target = "basicConfig", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "remark", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    IdentitySourceEntity saveConfigParamConverterToEntity(IdentitySourceConfigSaveParam param);

    /**
     * 保存配置参数转entity
     *
     * @param param    {@link  IdentitySourceConfigSaveParam}
     * @param provider {@link  IdentitySourceProvider}
     * @return {@link  IdentitySourceEntity}
     */
    default IdentitySourceEntity saveConfigParamConverterToEntity(IdentitySourceConfigSaveParam param,
                                                                  IdentitySourceProvider provider) {
        ValidationHelp.ValidationResult<?> validationResult;
        IdentitySourceConfig clientConfig = null;
        //钉钉
        if (Objects.equals(provider, IdentitySourceProvider.DINGTALK)) {
            clientConfig = param.getBasicConfig().to(DingTalkConfig.class);
        }
        //企业微信
        if (Objects.equals(provider, IdentitySourceProvider.WECHAT_WORK)) {
            clientConfig = param.getBasicConfig().to(WeChatWorkConfig.class);
        }
        //飞书
        if (Objects.equals(provider, IdentitySourceProvider.FEISHU)) {
            clientConfig = param.getBasicConfig().to(FeiShuConfig.class);
        }
        //放置参数，并验证参数
        if (!Objects.nonNull(clientConfig)) {
            throw new NullPointerException("提供商配置不能为空!");
        }
        validationResult = ValidationHelp.validateEntity(clientConfig);
        //处理异常
        if (validationResult.isHasErrors()) {
            throw new ConstraintViolationException(validationResult.getConstraintViolations());
        }
        //封装数据
        IdentitySourceEntity source = saveConfigParamConverterToEntity(param);
        source.setBasicConfig(
            JSONObject.toJSONString(clientConfig, JSONWriter.Feature.WriteClassName));
        return source;
    }

    /**
     * 查询身份源列表参数转 Request
     *
     * @param query     {@link  IdentitySourceListQuery}
     * @param pageModel {@link  PageModel}
     * @return {@link QueryDslRequest }
     */
    default QueryDslRequest queryIdentitySourceListParamConvertToPredicate(IdentitySourceListQuery query,
                                                                           PageModel pageModel) {
        QueryDslRequest request = new QueryDslRequest();
        QIdentitySourceEntity queryEntity = QIdentitySourceEntity.identitySourceEntity;
        Predicate predicate = ExpressionUtils.and(queryEntity.isNotNull(),
            queryEntity.isDeleted.eq(Boolean.FALSE));
        //查询条件
        //@formatter:off
        predicate = StringUtils.isBlank(query.getName()) ? predicate : ExpressionUtils.and(predicate, queryEntity.name.like("%" + query.getName() + "%"));
        //@formatter:on
        request.setPredicate(predicate);
        //分页条件
        //@formatter:off
        request.setPageRequest(QPageRequest.of(pageModel.getCurrent(), pageModel.getPageSize()));
        return request;
    }

    /**
     * entity转换为配置结果
     *
     * @param entity {@link IdentitySourceEntity}
     * @return {@link IdentitySourceConfigGetResult}
     */
  default   IdentitySourceConfigGetResult entityConverterToIdentitySourceConfigGetResult(IdentitySourceEntity entity){
      if (entity == null) {
          return null;
      }
      IdentitySourceConfigGetResult identitySourceResult = new IdentitySourceConfigGetResult();
      if (entity.getId() != null) {
          identitySourceResult.setId(String.valueOf(entity.getId()));
      }
      identitySourceResult.setConfigured(entity.getConfigured());
      identitySourceResult.setJobConfig(entity.getJobConfig());
      identitySourceResult.setStrategyConfig(entity.getStrategyConfig());
      try {
          JSONObject value = OBJECT_MAPPER.readValue(entity.getBasicConfig(), JSONObject.class);
          value.remove(CommonConstants.TYPE);
          //@formatter:off
          value.put(CommonConstants.CALLBACK_URL, ServerContextHelp.getSynchronizerPublicBaseUrl() + "/api/synchronizer/event_receive/" +  entity.getCode());
          //@formatter:on
            identitySourceResult.setBasicConfig(value);
        } catch (Exception e) {
            throw new TopIamException(e.getMessage());
        }
        return identitySourceResult;
    }
}
