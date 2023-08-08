/*
 * eiam-openapi - Employee Identity and Access Management
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
package cn.topiam.employee.openapi.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.common.entity.account.*;
import cn.topiam.employee.common.repository.account.OrganizationMemberRepository;
import cn.topiam.employee.common.repository.account.OrganizationRepository;
import cn.topiam.employee.core.mq.UserMessagePublisher;
import cn.topiam.employee.core.mq.UserMessageTag;
import cn.topiam.employee.openapi.constants.OpenApiStatus;
import cn.topiam.employee.openapi.converter.account.OrganizationConverter;
import cn.topiam.employee.openapi.exception.OpenApiException;
import cn.topiam.employee.openapi.pojo.request.account.save.account.OrganizationCreateParam;
import cn.topiam.employee.openapi.pojo.request.account.update.account.OrganizationUpdateParam;
import cn.topiam.employee.openapi.pojo.response.account.OrganizationChildResult;
import cn.topiam.employee.openapi.pojo.response.account.OrganizationResult;
import cn.topiam.employee.openapi.service.OrganizationService;
import cn.topiam.employee.support.repository.domain.IdEntity;
import cn.topiam.employee.support.util.BeanUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.support.constant.EiamConstants.ROOT_NODE;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_BY;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_TIME;

/**
 * <p>
 * 组织架构 服务实现类
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {
    public static final String SEPARATE = "/";

    /**
     * 创建组织架构
     *
     * @param param {@link OrganizationCreateParam}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrg(OrganizationCreateParam param) {
        //保存
        OrganizationEntity entity = organizationConverter.orgCreateParamConvertToEntity(param);
        //查询父节点
        Optional<OrganizationEntity> parent = organizationRepository.findById(param.getParentId());
        //新建
        organizationRepository.save(entity);
        //展示路径枚举
        parent.ifPresent(org -> entity
            .setDisplayPath(StringUtils.isEmpty(org.getPath()) ? SEPARATE + entity.getName()
                : org.getDisplayPath() + SEPARATE + entity.getName()));
        //设置路径枚举
        parent.ifPresent(
            org -> entity.setPath(StringUtils.isEmpty(org.getPath()) ? SEPARATE + entity.getId()
                : org.getPath() + SEPARATE + entity.getId()));
        //存在父节点，更改为非叶子节点
        if (parent.isPresent() && parent.get().getLeaf()) {
            organizationRepository.updateIsLeaf(parent.get().getId(), false);
        }
        AuditContext
            .setTarget(Target.builder().id(entity.getId()).type(TargetType.ORGANIZATION).build());
    }

    /**
     * 修改组织架构
     *
     * @param param {@link OrganizationCreateParam}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrg(OrganizationUpdateParam param) {
        OrganizationEntity organization = organizationConverter
            .orgUpdateParamConvertToEntity(param);
        Optional<OrganizationEntity> optional = this.organizationRepository.findById(param.getId());
        if (optional.isPresent()) {
            OrganizationEntity entity = optional.get();
            String userIds;
            //如果修改了名字，递归修改和该组织有关所有节点信息的展示路径
            if (!optional.get().getName().equals(param.getName())) {
                //修改名称
                organization.setDisplayPath(getNewDisplayPath(param.getId(), param.getName(),
                    entity.getPath(), entity.getDisplayPath()));
                //递归更改下级名称
                if (!entity.getLeaf()) {
                    recursiveUpdateDisplayPath(entity.getId(), entity.getId(), param.getName());
                }
                userIds = organizationRepository
                    .getOrgMemberList(organization.getId(), QUserEntity.userEntity.id).stream()
                    .map(String::valueOf).collect(Collectors.joining(","));
            } else {
                List<OrganizationMemberEntity> orgMemberList = organizationMemberRepository
                    .findAllByOrgId(entity.getId());
                userIds = orgMemberList.stream().map(item -> String.valueOf(item.getUserId()))
                    .collect(Collectors.joining(","));
            }
            //修改
            BeanUtils.merge(organization, entity, LAST_MODIFIED_BY, LAST_MODIFIED_TIME);
            organizationRepository.save(entity);
            // 更新用户es信息
            if (StringUtils.isNotBlank(userIds)) {
                userMessagePublisher.sendUserChangeMessage(UserMessageTag.SAVE, userIds);
            }
            AuditContext.setTarget(
                Target.builder().id(entity.getId()).type(TargetType.ORGANIZATION).build());
        }
        throw new OpenApiException(OpenApiStatus.DEPARTMENT_NOT_EXIST);
    }

    /**
     * 递归修改显示名称
     *
     * @param parentId {@link  String} 上级ID
     * @param id       {@link  String} ID 要更改名称的节点ID
     * @param name     {@link  String} 名称
     */
    protected void recursiveUpdateDisplayPath(String parentId, String id, String name) {
        List<OrganizationEntity> childNodes = organizationRepository.findByParentId(parentId);
        for (OrganizationEntity org : childNodes) {
            org.setDisplayPath(getNewDisplayPath(id, name, org.getPath(), org.getDisplayPath()));
            organizationRepository.save(org);
            //存在下级节点
            if (!org.getLeaf()) {
                //递归处理
                recursiveUpdateDisplayPath(org.getId(), id, name);
            }
        }
    }

    /**
     * 获取新的显示路径
     *
     * @param id          {@link  String} 要更改的ID
     * @param name        {@link  String} 要更改的新名称
     * @param path        {@link  String} 路径
     * @param displayPath {@link  String} 显示路径
     * @return {@link  String} 新显示名称
     */
    private String getNewDisplayPath(String id, String name, String path, String displayPath) {
        // 修改名称有个问题，如果名称一致，使用替换就会出问题，所以使用KEY=VALUE更改
        String[] pathIds = path.split(SEPARATE);
        String[] pathNames = displayPath.split(SEPARATE);
        //路径数据
        Map<String, String> pathData = new LinkedHashMap<>();
        if (pathIds.length == pathNames.length) {
            //i=1起步，以为切割数组，第0位为""
            for (int i = 1; i < pathNames.length; i++) {
                pathData.put(pathIds[i], pathNames[i]);
            }
        }
        pathData.put(id, name);
        //封装 displayPath
        StringBuilder newDisplayPath = new StringBuilder();

        for (Map.Entry<String, String> next : pathData.entrySet()) {
            newDisplayPath.append(SEPARATE).append(next.getValue());
        }
        return newDisplayPath.toString();
    }

    /**
     * 启用/禁用
     *
     * @param id      {@link String}
     * @param enabled {@link Boolean}
     */
    @Override
    public void updateStatus(String id, boolean enabled) {
        organizationRepository.findById(id)
            .orElseThrow(() -> new OpenApiException(OpenApiStatus.DEPARTMENT_NOT_EXIST));
        organizationRepository.updateStatus(id, enabled);
    }

    /**
     * 删除组织架构
     *
     * @param id {@link List}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrg(String id) {
        Optional<OrganizationEntity> optional = organizationRepository.findById(id);
        if (optional.isPresent()) {
            //是否存在子节点
            List<OrganizationEntity> list = organizationRepository.findByParentId(id);
            if (CollectionUtils.isEmpty(list)) {
                //查询当前机构和当前机构下子机构下是否存在用户，不存在删除，存在抛出异常
                Long count = getOrgMemberCount(id);
                if (count > 0) {
                    throw new OpenApiException(OpenApiStatus.DEPARTMENT_HAS_USER);
                }
                //删除
                organizationRepository.deleteById(id);
                AuditContext
                    .setTarget(Target.builder().id(id).type(TargetType.ORGANIZATION).build());
                //查询该子节点上级组织机构是否存在子节点，如果不存在，更改 leaf=true
                list = organizationRepository.findByParentId(optional.get().getParentId());
                //不存在子部门，且父节点非根节点，执行更改 leaf=true 操作
                if (CollectionUtils.isEmpty(list)
                    && !StringUtils.equals(ROOT_NODE, optional.get().getParentId())) {
                    organizationRepository.updateIsLeaf(optional.get().getParentId(), true);
                }
            }
            throw new OpenApiException(OpenApiStatus.DEPARTMENT_HAS_SUB_DEPARTMENT);
        }
        throw new OpenApiException(OpenApiStatus.DEPARTMENT_NOT_EXIST);
    }

    /**
     * 组织机构详情
     *
     * @param id {@link String}
     * @return {@link OrganizationResult}
     */
    @Override
    public OrganizationResult getOrganizationById(String id) {
        OrganizationEntity entity = organizationRepository.findById(id)
            .orElseThrow(() -> new OpenApiException(OpenApiStatus.DEPARTMENT_NOT_EXIST));
        OrganizationResult organizationResult = organizationConverter
            .entityConvertToOrgDetailResult(entity);
        return organizationResult;
    }

    /**
     * 查询子组织
     *
     * @param parentId {@link String}
     * @return {@link OrganizationResult}
     */
    @Override
    public List<OrganizationChildResult> getChildOrganization(String parentId) {
        organizationRepository.findById(parentId)
            .orElseThrow(() -> new OpenApiException(OpenApiStatus.DEPARTMENT_NOT_EXIST));
        List<OrganizationEntity> entityList = organizationRepository
            .findByParentIdOrderByOrderAsc(parentId);
        return organizationConverter.entityConvertToChildOrgListResult(entityList);
    }

    /**
     * 查询组织成员数量
     *
     * @param orgId {@link  String}
     * @return {@link  Long}
     */
    public Long getOrgMemberCount(String orgId) {
        //条件
        QUserEntity user = QUserEntity.userEntity;
        QOrganizationEntity qOrganization = QOrganizationEntity.organizationEntity;
        Predicate predicate = ExpressionUtils.and(user.isNotNull(), user.deleted.eq(Boolean.FALSE));
        //FIND_IN_SET函数
        BooleanExpression template = Expressions.booleanTemplate(
            "FIND_IN_SET({0}, replace({1}, '/', ','))> 0", orgId, qOrganization.path);
        predicate = ExpressionUtils.and(predicate, qOrganization.id.eq(orgId).or(template));
        //构造查询
        JPAQuery<Long> jpaQuery = jpaQueryFactory.selectFrom(user).select(user.count())
            .innerJoin(QOrganizationMemberEntity.organizationMemberEntity)
            .on(user.id.eq(QOrganizationMemberEntity.organizationMemberEntity.userId))
            .innerJoin(qOrganization)
            .on(qOrganization.id.eq(QOrganizationMemberEntity.organizationMemberEntity.orgId))
            .where(predicate);
        return jpaQuery.fetch().get(0);
    }

    @Override
    public String getOrganizationIdByExternalId(String externalId) {
        Optional<OrganizationEntity> organization = organizationRepository
            .findByExternalId(externalId);
        return organization.map(IdEntity::getId)
            .orElseThrow(() -> new OpenApiException(OpenApiStatus.DEPARTMENT_NOT_EXIST));
    }

    private final JPAQueryFactory              jpaQueryFactory;

    /**
     * 组织架构数据映射器
     */
    private final OrganizationConverter        organizationConverter;

    /**
     * OrganizationRepository
     */
    private final OrganizationRepository       organizationRepository;

    /**
     * UserMessagePublisher
     */
    private final UserMessagePublisher         userMessagePublisher;

    /**
     * OrganizationMemberRepository
     */
    private final OrganizationMemberRepository organizationMemberRepository;
}
