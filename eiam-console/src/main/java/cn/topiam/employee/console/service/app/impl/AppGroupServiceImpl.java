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
import java.util.Optional;

import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;

import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.common.entity.app.AppGroupEntity;
import cn.topiam.employee.common.entity.app.QAppGroupEntity;
import cn.topiam.employee.common.repository.app.AppGroupRepository;
import cn.topiam.employee.console.converter.app.AppGroupConverter;
import cn.topiam.employee.console.pojo.query.app.AppGroupQuery;
import cn.topiam.employee.console.pojo.result.app.AppGroupGetResult;
import cn.topiam.employee.console.pojo.result.app.AppGroupListResult;
import cn.topiam.employee.console.pojo.save.app.AppGroupCreateParam;
import cn.topiam.employee.console.pojo.update.app.AppGroupUpdateParam;
import cn.topiam.employee.console.service.app.AppGroupService;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.util.BeanUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_BY;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_TIME;

/**
 * AppGroupServiceImpl
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/8/31 14:23
 */
@Service
@Slf4j
@AllArgsConstructor
public class AppGroupServiceImpl implements AppGroupService {

    /**
     * 获取应用分组（分页）
     *
     * @param pageModel {@link PageModel}
     * @param query     {@link AppGroupQuery}
     * @return {@link AppGroupListResult}
     */
    @Override
    public Page<AppGroupListResult> getAppGroupList(PageModel pageModel, AppGroupQuery query) {
        //查询条件
        Predicate predicate = appGroupConverter.queryAppGroupListParamConvertToPredicate(query);
        OrderSpecifier<LocalDateTime> desc = QAppGroupEntity.appGroupEntity.updateTime.desc();
        //分页条件
        QPageRequest request = QPageRequest.of(pageModel.getCurrent(), pageModel.getPageSize(),
            desc);
        //查询映射
        org.springframework.data.domain.Page<AppGroupEntity> list = appGroupRepository
            .findAll(predicate, request);
        return appGroupConverter.entityConvertToAppGroupListResult(list);
    }

    /**
     * 创建应用分组
     *
     * @param param {@link AppGroupCreateParam}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createAppGroup(AppGroupCreateParam param) {
        // TODO 创建后没有数据权限
        AppGroupEntity entity = appGroupConverter.appGroupCreateParamConvertToEntity(param);
        entity.setEnabled(true);
        appGroupRepository.save(entity);
        AuditContext.setTarget(
            Target.builder().id(String.valueOf(entity.getId())).type(TargetType.APP_GROUP).build());
        return true;
    }

    /**
     * 修改应用分组
     *
     * @param param {@link AppGroupUpdateParam}
     * @return {@link Boolean}
     */
    @Override
    public boolean updateAppGroup(AppGroupUpdateParam param) {
        AppGroupEntity appGroup = appGroupRequireNonNull(param.getId());
        AppGroupEntity entity = appGroupConverter.appGroupUpdateParamConverterToEntity(param);
        BeanUtils.merge(entity, appGroup, LAST_MODIFIED_TIME, LAST_MODIFIED_BY);
        appGroupRepository.save(appGroup);
        AuditContext.setTarget(
            Target.builder().id(param.getId().toString()).type(TargetType.APP_GROUP).build());
        return true;
    }

    /**
     * 删除应用分组
     *
     * @param id {@link  String}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAppGroup(Long id) {
        appGroupRequireNonNull(id);
        appGroupRepository.deleteById(id);
        AuditContext
            .setTarget(Target.builder().id(id.toString()).type(TargetType.APP_GROUP).build());
        return true;
    }

    /**
     * 获取单个应用分组详情
     *
     * @param id {@link Long}
     * @return {@link AppGroupEntity}
     */
    @Override
    public AppGroupGetResult getAppGroup(Long id) {
        Optional<AppGroupEntity> optional = appGroupRepository.findById(id);
        if (optional.isPresent()) {
            AppGroupEntity entity = optional.get();
            return appGroupConverter.entityConvertToAppGroupResult(entity);
        }
        return null;

    }

    /**
     * 启用应用分组
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Override
    public Boolean enableAppGroup(String id) {
        appGroupRequireNonNull(Long.valueOf(id));
        Integer count = appGroupRepository.updateAppGroupStatus(Long.valueOf(id), Boolean.TRUE);
        AuditContext.setTarget(Target.builder().id(id).type(TargetType.APP_GROUP).build());
        return count > 0;
    }

    /**
     * 禁用应用分组
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Override
    public Boolean disableAppGroup(String id) {
        appGroupRequireNonNull(Long.valueOf(id));
        Integer count = appGroupRepository.updateAppGroupStatus(Long.valueOf(id), Boolean.FALSE);
        AuditContext.setTarget(Target.builder().id(id).type(TargetType.APP_GROUP).build());
        return count > 0;
    }

    /**
     * 查询并检查分组是否为空，非空返回
     *
     * @param id {@link Long}
     * @return {@link AppGroupEntity}
     */
    private AppGroupEntity appGroupRequireNonNull(Long id) {
        Optional<AppGroupEntity> optional = appGroupRepository.findById(id);
        if (optional.isEmpty()) {
            AuditContext.setContent("操作失败，应用分组不存在");
            log.warn(AuditContext.getContent());
            throw new TopIamException(AuditContext.getContent());
        }
        return optional.get();
    }

    /**
     * AppGroupRepository
     */
    private final AppGroupRepository appGroupRepository;

    /**
     * AppGroupConverter
     */
    private final AppGroupConverter  appGroupConverter;
}
