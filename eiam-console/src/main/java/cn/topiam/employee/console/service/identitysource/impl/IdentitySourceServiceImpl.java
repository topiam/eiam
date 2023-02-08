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
package cn.topiam.employee.console.service.identitysource.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.common.constants.AccountConstants;
import cn.topiam.employee.common.entity.identitysource.IdentitySourceEntity;
import cn.topiam.employee.common.repository.identitysource.IdentitySourceRepository;
import cn.topiam.employee.console.converter.identitysource.IdentitySourceConverter;
import cn.topiam.employee.console.pojo.other.IdentitySourceConfigValidatorParam;
import cn.topiam.employee.console.pojo.query.identity.IdentitySourceListQuery;
import cn.topiam.employee.console.pojo.result.identitysource.IdentitySourceListResult;
import cn.topiam.employee.console.pojo.save.authentication.IdentityProviderCreateParam;
import cn.topiam.employee.console.pojo.save.identitysource.IdentitySourceConfigSaveParam;
import cn.topiam.employee.console.pojo.save.identitysource.IdentitySourceCreateParam;
import cn.topiam.employee.console.pojo.save.identitysource.IdentitySourceCreateResult;
import cn.topiam.employee.console.pojo.update.identity.IdentitySourceUpdateParam;
import cn.topiam.employee.console.service.identitysource.IdentitySourceService;
import cn.topiam.employee.identitysource.dingtalk.DingTalkConfig;
import cn.topiam.employee.identitysource.dingtalk.DingTalkConfigValidator;
import cn.topiam.employee.identitysource.feishu.FeiShuConfig;
import cn.topiam.employee.identitysource.feishu.FeiShuConfigValidator;
import cn.topiam.employee.identitysource.wechatwork.WeChatWorkConfig;
import cn.topiam.employee.identitysource.wechatwork.WeChatWorkConfigValidator;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.repository.page.domain.QueryDslRequest;
import cn.topiam.employee.support.util.BeanUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_BY;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_TIME;

/**
 * <p>
 * 身份源配置 服务类
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-16
 */
@Slf4j
@Service
@AllArgsConstructor
@CacheConfig(cacheNames = { AccountConstants.IDS_CACHE_NAME })
public class IdentitySourceServiceImpl implements IdentitySourceService {

    /**
     * 身份源列表
     *
     * @param pageModel {@link  PageModel}
     * @return {@link List}
     */
    @Override
    public cn.topiam.employee.support.repository.page.domain.Page<IdentitySourceListResult> getIdentitySourceList(IdentitySourceListQuery query,
                                                                                                                  PageModel pageModel) {
        QueryDslRequest request = identitySourceConverter
            .queryIdentitySourceListParamConvertToPredicate(query, pageModel);
        org.springframework.data.domain.Page<IdentitySourceEntity> list = identitySourceRepository
            .findAll(request.getPredicate(), request.getPageRequest());
        return identitySourceConverter.entityConverterToIdentitySourceListResult(list);
    }

    /**
     * 身份源详情
     *
     * @param id {@link String}
     * @return {@link IdentitySourceEntity}
     */
    @Override
    public IdentitySourceEntity getIdentitySource(String id) {
        Optional<IdentitySourceEntity> provider = identitySourceRepository
            .findById(Long.valueOf(id));
        return provider.orElse(null);
    }

    /**
     * 保存身份源
     *
     * @param param {@link IdentityProviderCreateParam}
     * @return {@link IdentitySourceCreateResult}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public IdentitySourceCreateResult createIdentitySource(IdentitySourceCreateParam param) {
        IdentitySourceEntity entity = identitySourceConverter.createParamConverterToEntity(param);
        identitySourceRepository.save(entity);
        AuditContext.setTarget(Target.builder().id(entity.getId().toString())
            .type(TargetType.IDENTITY_SOURCE).build());
        return new IdentitySourceCreateResult(entity.getId().toString());
    }

    /**
     * 修改身份源
     *
     * @param param {@link IdentitySourceUpdateParam}
     * @return {@link Boolean}
     */
    @Override
    public Boolean updateIdentitySource(IdentitySourceUpdateParam param) {
        IdentitySourceEntity source = identitySourceConverter.updateParamConverterToEntity(param);
        IdentitySourceEntity entity = getIdentitySource(param.getId());
        BeanUtils.merge(source, entity, LAST_MODIFIED_TIME, LAST_MODIFIED_BY);
        identitySourceRepository.save(entity);
        AuditContext.setTarget(Target.builder().id(entity.getId().toString())
            .type(TargetType.IDENTITY_SOURCE).build());
        return true;
    }

    /**
     * 禁用身份源
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Override
    public Boolean disableIdentitySource(String id) {
        Optional<IdentitySourceEntity> optional = identitySourceRepository
            .findById(Long.valueOf(id));
        //身份源不存在
        if (optional.isEmpty()) {
            AuditContext.setContent("操作失败，身份源不存在");
            log.warn(AuditContext.getContent());
            throw new TopIamException(AuditContext.getContent());
        }
        Integer count = identitySourceRepository.updateIdentitySourceStatus(Long.valueOf(id),
            Boolean.FALSE);
        AuditContext.setTarget(Target.builder().id(id).type(TargetType.IDENTITY_SOURCE).build());
        return count > 0;
    }

    /**
     * 启用身份源
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Override
    public Boolean enableIdentitySource(String id) {
        Optional<IdentitySourceEntity> optional = identitySourceRepository
            .findById(Long.valueOf(id));
        //用户不存在
        if (optional.isEmpty()) {
            AuditContext.setContent("操作失败，身份源不存在");
            log.warn(AuditContext.getContent());
            throw new TopIamException(AuditContext.getContent());
        }
        Integer count = identitySourceRepository.updateIdentitySourceStatus(Long.valueOf(id),
            Boolean.TRUE);
        AuditContext.setTarget(Target.builder().id(id).type(TargetType.IDENTITY_SOURCE).build());
        return count > 0;
    }

    /**
     * 删除身份源
     *
     * @param id {@link  String}
     * @return {@link  Boolean}
     */
    @Override
    public Boolean deleteIdentitySource(String id) {
        Optional<IdentitySourceEntity> optional = identitySourceRepository
            .findById(Long.valueOf(id));
        //用户不存在
        if (optional.isEmpty()) {
            AuditContext.setContent("操作失败，身份源不存在");
            log.warn(AuditContext.getContent());
            throw new TopIamException(AuditContext.getContent());
        }
        identitySourceRepository.deleteById(Long.valueOf(id));
        AuditContext.setTarget(Target.builder().id(id).type(TargetType.IDENTITY_SOURCE).build());
        return true;
    }

    /**
     * 保存身份源配置
     *
     * @param param {@link  IdentitySourceConfigSaveParam}
     * @return {@link  Boolean}
     */
    @Override
    public Boolean saveIdentitySourceConfig(IdentitySourceConfigSaveParam param) {
        IdentitySourceEntity entity = getIdentitySource(param.getId());
        param.getBasicConfig().putAll(JSONObject.parseObject(entity.getBasicConfig()));
        //转换
        IdentitySourceEntity source = identitySourceConverter
            .saveConfigParamConverterToEntity(param, entity.getProvider());
        //合并对象
        BeanUtils.merge(source, entity, LAST_MODIFIED_BY, LAST_MODIFIED_TIME);
        identitySourceRepository.save(entity);
        AuditContext.setTarget(Target.builder().id(entity.getId().toString())
            .type(TargetType.IDENTITY_SOURCE).build());
        return true;
    }

    /**
     * 更新身份源策略
     *
     * @param id             {@link Long} 主键
     * @param strategyConfig {@link String} 策略
     */
    @Override
    public void updateStrategyConfig(Long id, String strategyConfig) {
        Optional<IdentitySourceEntity> optional = identitySourceRepository.findById(id);
        //用户不存在
        if (optional.isEmpty()) {
            AuditContext.setContent("操作失败，身份源不存在");
            log.warn(AuditContext.getContent());
            throw new TopIamException(AuditContext.getContent());
        }
        identitySourceRepository.updateStrategyConfig(id, strategyConfig);
        AuditContext
            .setTarget(Target.builder().id(id.toString()).type(TargetType.IDENTITY_SOURCE).build());

    }

    /**
     * 身份源配置验证
     *
     * @param param {@link  IdentitySourceConfigValidatorParam}
     * @return {@link  Boolean}
     */
    @Override
    public Boolean identitySourceConfigValidator(IdentitySourceConfigValidatorParam param) {
        switch (param.getProvider()) {
            //钉钉
            case DINGTALK: {
                DingTalkConfig config = JSONObject.parseObject(param.getConfig().toJSONString(),
                    DingTalkConfig.class);
                return new DingTalkConfigValidator().validate(config);
            }
            case FEISHU: {
                FeiShuConfig config = JSONObject.parseObject(param.getConfig().toJSONString(),
                    FeiShuConfig.class);
                return new FeiShuConfigValidator().validate(config);
            }
            case WECHAT_WORK: {
                WeChatWorkConfig config = JSONObject.parseObject(param.getConfig().toJSONString(),
                    WeChatWorkConfig.class);
                return new WeChatWorkConfigValidator().validate(config);
            }
            default: {
                throw new TopIamException("暂未支持此提供商连接验证");
            }
        }
    }

    /**
     * 身份验证源转换器
     */
    private final IdentitySourceConverter  identitySourceConverter;

    /**
     * 身份源
     */
    private final IdentitySourceRepository identitySourceRepository;

}
