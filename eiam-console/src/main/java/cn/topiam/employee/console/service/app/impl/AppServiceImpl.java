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
package cn.topiam.employee.console.service.app.impl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;

import cn.topiam.employee.application.ApplicationService;
import cn.topiam.employee.application.ApplicationServiceLoader;
import cn.topiam.employee.application.exception.AppNotExistException;
import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.entity.app.QAppEntity;
import cn.topiam.employee.common.repository.app.AppRepository;
import cn.topiam.employee.console.converter.app.AppConverter;
import cn.topiam.employee.console.pojo.query.app.AppQuery;
import cn.topiam.employee.console.pojo.result.app.AppCreateResult;
import cn.topiam.employee.console.pojo.result.app.AppGetResult;
import cn.topiam.employee.console.pojo.result.app.AppListResult;
import cn.topiam.employee.console.pojo.save.app.AppCreateParam;
import cn.topiam.employee.console.pojo.update.app.AppSaveConfigParam;
import cn.topiam.employee.console.pojo.update.app.AppUpdateParam;
import cn.topiam.employee.console.service.app.AppService;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.util.BeanUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_BY;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_TIME;

/**
 * ApplicationServiceImpl
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/11/29 22:23
 */
@Service
@Slf4j
@AllArgsConstructor
public class AppServiceImpl implements AppService {

    /**
     * 获取应用（分页）
     *
     * @param pageModel {@link PageModel}
     * @param query     {@link AppQuery}
     * @return {@link AppListResult}
     */
    @Override
    public Page<AppListResult> getAppList(PageModel pageModel, AppQuery query) {
        //查询条件
        Predicate predicate = appConverter.queryAppListParamConvertToPredicate(query);
        OrderSpecifier<LocalDateTime> desc = QAppEntity.appEntity.updateTime.desc();
        //分页条件
        QPageRequest request = QPageRequest.of(pageModel.getCurrent(), pageModel.getPageSize(),
            desc);
        //查询映射
        org.springframework.data.domain.Page<AppEntity> list = appRepository.findAll(predicate,
            request);
        return appConverter.entityConvertToAppListResult(list);
    }

    /**
     * 创建应用
     *
     * @param param {@link AppCreateParam}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppCreateResult createApp(AppCreateParam param) {
        // TODO 创建后没有数据权限
        ApplicationService applicationService = applicationServiceLoader
            .getApplicationService(param.getTemplate());
        String appId = applicationService.create(param.getName(), param.getIcon(),
            param.getRemark());
        AuditContext.setTarget(Target.builder().id(appId).type(TargetType.APPLICATION).build());
        return new AppCreateResult(appId);
    }

    /**
     * 修改应用
     *
     * @param param {@link AppUpdateParam}
     * @return {@link Boolean}
     */
    @Override
    public boolean updateApp(AppUpdateParam param) {
        AppEntity app = appRequireNonNull(param.getId());
        AppEntity entity = appConverter.appUpdateParamConverterToEntity(param);
        BeanUtils.merge(entity, app, LAST_MODIFIED_TIME, LAST_MODIFIED_BY);
        appRepository.save(app);
        AuditContext.setTarget(
            Target.builder().id(param.getId().toString()).type(TargetType.APPLICATION).build());
        return true;
    }

    /**
     * 删除应用
     *
     * @param id {@link  String}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteApp(Long id) {
        AppEntity app = appRequireNonNull(id);
        applicationServiceLoader.getApplicationService(app.getTemplate()).delete(id.toString());
        AuditContext
            .setTarget(Target.builder().id(id.toString()).type(TargetType.APPLICATION).build());
        return true;
    }

    /**
     * 获取单个应用详情
     *
     * @param id {@link Long}
     * @return {@link AppEntity}
     */
    @Override
    public AppGetResult getApp(Long id) {
        Optional<AppEntity> optional = appRepository.findById(id);
        if (optional.isPresent()) {
            AppEntity entity = optional.get();
            return appConverter.entityConvertToAppResult(entity);
        }
        return null;

    }

    /**
     * 启用应用
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Override
    public Boolean enableApp(String id) {
        appRequireNonNull(Long.valueOf(id));
        Integer count = appRepository.updateAppStatus(Long.valueOf(id), Boolean.TRUE);
        AuditContext.setTarget(Target.builder().id(id).type(TargetType.APPLICATION).build());
        return count > 0;
    }

    /**
     * 禁用应用
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Override
    public Boolean disableApp(String id) {
        appRequireNonNull(Long.valueOf(id));
        Integer count = appRepository.updateAppStatus(Long.valueOf(id), Boolean.FALSE);
        AuditContext.setTarget(Target.builder().id(id).type(TargetType.APPLICATION).build());
        return count > 0;
    }

    /**
     * 更新应用配置
     *
     * @param param {@link AppSaveConfigParam}
     * @return {@link Boolean}
     */
    @Override
    public Boolean saveAppConfig(AppSaveConfigParam param) {
        ApplicationService applicationService = applicationServiceLoader
            .getApplicationService(param.getTemplate());
        applicationService.saveConfig(param.getId(), param.getConfig());
        AuditContext
            .setTarget(Target.builder().id(param.getId()).type(TargetType.APPLICATION).build());
        return true;
    }

    /**
     * 获取应用配置
     *
     * @param appId {@link String}
     * @return {@link Map}
     */
    @Override
    public Object getAppConfig(String appId) {
        Optional<AppEntity> optional = appRepository.findById(Long.valueOf(appId));
        if (optional.isPresent()) {
            ApplicationService applicationService = applicationServiceLoader
                .getApplicationService(optional.get().getTemplate());
            return applicationService.getConfig(appId);
        }
        throw new AppNotExistException();
    }

    /**
     * 查询并检查app是否为空，非空返回
     *
     * @param id {@link Long}
     * @return {@link AppEntity}
     */
    private AppEntity appRequireNonNull(Long id) {
        Optional<AppEntity> optional = appRepository.findById(id);
        if (optional.isEmpty()) {
            AuditContext.setContent("操作失败，应用不存在");
            log.warn(AuditContext.getContent());
            throw new TopIamException(AuditContext.getContent());
        }
        return optional.get();
    }

    /**
     * ApplicationTemplateLoader
     */
    private final ApplicationServiceLoader applicationServiceLoader;

    /**
     * ApplicationRepository
     */
    private final AppRepository            appRepository;

    //    /**
    //     * 应用证书
    //     */
    //    private final AppCertRepository         appCertRepository;
    //
    //    /**
    //     * 应用账户
    //     */
    //    private final AppAccountRepository      appAccountRepository;
    //    /**
    //     * 应用策略
    //     */
    //    private final AppAccessPolicyRepository appAccessPolicyRepository;

    /**
     * ApplicationConverter
     */
    private final AppConverter             appConverter;
}
