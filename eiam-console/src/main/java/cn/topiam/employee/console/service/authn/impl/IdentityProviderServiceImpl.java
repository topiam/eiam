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
package cn.topiam.employee.console.service.authn.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.common.entity.authn.IdentityProviderEntity;
import cn.topiam.employee.common.repository.authentication.IdentityProviderRepository;
import cn.topiam.employee.console.converter.authn.IdentityProviderConverter;
import cn.topiam.employee.console.pojo.query.authn.IdentityProviderListQuery;
import cn.topiam.employee.console.pojo.result.authn.IdentityProviderCreateResult;
import cn.topiam.employee.console.pojo.result.authn.IdentityProviderListResult;
import cn.topiam.employee.console.pojo.result.authn.IdentityProviderResult;
import cn.topiam.employee.console.pojo.save.authn.IdentityProviderCreateParam;
import cn.topiam.employee.console.pojo.update.authn.IdpUpdateParam;
import cn.topiam.employee.console.service.authn.IdentityProviderService;
import cn.topiam.employee.support.context.ApplicationContextHelp;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.repository.page.domain.QueryDslRequest;
import cn.topiam.employee.support.util.BeanUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.common.constant.ConfigBeanNameConstants.DEFAULT_SECURITY_FILTER_CHAIN;

/**
 * <p>
 * 认证源配置 服务实现类
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-16
 */
@Slf4j
@Service
@AllArgsConstructor
public class IdentityProviderServiceImpl implements IdentityProviderService {

    /**
     * 平台是否启用
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Override
    public Boolean identityProviderIsEnable(String id) {
        Optional<IdentityProviderEntity> optional = identityProviderRepository
            .findById(Long.valueOf(id));
        return optional.isPresent() && optional.get().getEnabled();
    }

    /**
     * 通过平台类型获取
     *
     * @param provider {@link String}
     * @return {@link IdentityProviderEntity}
     */
    @Override
    public List<IdentityProviderEntity> getByIdentityProvider(String provider) {
        return identityProviderRepository.findByType(provider);
    }

    /**
     * 认证源列表
     *
     * @param pageModel {@link PageModel }
     * @param query     {@link  IdentityProviderListQuery}
     * @return {@link List}
     */
    @Override
    public Page<IdentityProviderListResult> getIdentityProviderList(PageModel pageModel,
                                                                    IdentityProviderListQuery query) {
        QueryDslRequest request = identityProviderConverter
            .queryIdentityProviderListParamConvertToPredicate(query, pageModel);
        org.springframework.data.domain.Page<IdentityProviderEntity> list = identityProviderRepository
            .findAll(request.getPredicate(), request.getPageRequest());
        return identityProviderConverter.entityConverterToIdentityProviderResult(list);
    }

    /**
     * 认证源详情
     *
     * @param id {@link String}
     * @return {@link IdentityProviderResult}
     */
    @Override
    public IdentityProviderResult getIdentityProvider(String id) {
        Optional<IdentityProviderEntity> optional = identityProviderRepository
            .findById(Long.valueOf(id));
        if (optional.isPresent()) {
            return identityProviderConverter
                .entityConverterToIdentityProviderDetailResult(optional.get());
        }
        return new IdentityProviderResult();
    }

    /**
     * 创建认证源
     *
     * @param param {@link IdentityProviderCreateParam}
     * @return {@link IdentityProviderCreateResult}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public IdentityProviderCreateResult createIdp(IdentityProviderCreateParam param) {
        //转换对象
        IdentityProviderEntity data = identityProviderConverter
            .identityProviderCreateParamConverterToEntity(param);
        identityProviderRepository.save(data);
        ApplicationContextHelp.refresh(DEFAULT_SECURITY_FILTER_CHAIN);
        AuditContext.setTarget(Target.builder().id(data.getId().toString())
            .type(TargetType.IDENTITY_PROVIDER).build());
        return IdentityProviderCreateResult.builder().id(String.valueOf(data.getId()))
            .type(data.getType()).build();
    }

    /**
     * 更新身份验证源
     *
     * @param param {@link IdpUpdateParam}
     * @return {@link Boolean}
     */
    @Override
    public Boolean updateIdentityProvider(IdpUpdateParam param) {
        Optional<IdentityProviderEntity> optional = identityProviderRepository
            .findById(Long.valueOf(param.getId()));
        if (optional.isPresent()) {
            IdentityProviderEntity entity = optional.get();
            //转换对象
            IdentityProviderEntity data = identityProviderConverter
                .identityProviderUpdateParamConverterToEntity(param);
            BeanUtils.merge(data, entity);
            identityProviderRepository.save(entity);
            ApplicationContextHelp.refresh(DEFAULT_SECURITY_FILTER_CHAIN);
            AuditContext.setTarget(Target.builder().id(entity.getId().toString())
                .name(entity.getName()).type(TargetType.IDENTITY_PROVIDER)
                .typeName(TargetType.IDENTITY_PROVIDER.getDesc()).build());
            return true;
        }
        throw new NullPointerException("系统不存在该身份源");
    }

    /**
     * 删除认证源
     *
     * @param id {@link  String}
     * @return {@link Boolean}
     */
    @Override
    public Boolean deleteIdentityProvider(String id) {
        Optional<IdentityProviderEntity> optional = identityProviderRepository
            .findById(Long.valueOf(id));
        //管理员不存在
        if (optional.isEmpty()) {
            AuditContext.setContent("删除失败，认证源不存在");
            log.warn(AuditContext.getContent());
            throw new TopIamException(AuditContext.getContent());
        }
        identityProviderRepository.deleteById(Long.valueOf(id));
        ApplicationContextHelp.refresh(DEFAULT_SECURITY_FILTER_CHAIN);
        AuditContext.setTarget(Target.builder().id(id).type(TargetType.IDENTITY_PROVIDER).build());
        return Boolean.TRUE;
    }

    /**
     * 更改认证源状态
     *
     * @param id      {@link String}
     * @param enabled {@link Boolean}
     * @return {@link Boolean}
     */
    @Override
    public Boolean updateIdentityProviderStatus(String id, Boolean enabled) {
        Optional<IdentityProviderEntity> optional = identityProviderRepository
            .findById(Long.valueOf(id));
        //管理员不存在
        if (optional.isEmpty()) {
            AuditContext.setContent("删除失败，认证源不存在");
            log.warn(AuditContext.getContent());
            throw new TopIamException(AuditContext.getContent());
        }
        boolean result = identityProviderRepository.updateIdentityProviderStatus(Long.valueOf(id),
            enabled) > 0;
        ApplicationContextHelp.refresh(DEFAULT_SECURITY_FILTER_CHAIN);
        AuditContext.setTarget(Target.builder().id(id).type(TargetType.IDENTITY_PROVIDER).build());
        return result;
    }

    /**
     * 身份源身份转换器
     */
    private final IdentityProviderConverter  identityProviderConverter;
    /**
     * 身份源repository
     */
    private final IdentityProviderRepository identityProviderRepository;
}
