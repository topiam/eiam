/*
 * eiam-synchronizer - Employee Identity and Access Management
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
package cn.topiam.employee.synchronizer.processor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cn.topiam.employee.common.entity.account.OrganizationEntity;
import cn.topiam.employee.common.entity.identitysource.IdentitySourceEntity;
import cn.topiam.employee.common.entity.identitysource.IdentitySourceSyncHistoryEntity;
import cn.topiam.employee.common.entity.identitysource.IdentitySourceSyncRecordEntity;
import cn.topiam.employee.common.entity.identitysource.config.StrategyConfig;
import cn.topiam.employee.common.enums.SyncStatus;
import cn.topiam.employee.common.enums.TriggerType;
import cn.topiam.employee.common.enums.account.OrganizationType;
import cn.topiam.employee.common.enums.identitysource.IdentitySourceActionType;
import cn.topiam.employee.common.enums.identitysource.IdentitySourceObjectType;
import cn.topiam.employee.common.repository.account.OrganizationRepository;
import cn.topiam.employee.common.repository.identitysource.IdentitySourceRepository;
import cn.topiam.employee.common.repository.identitysource.IdentitySourceSyncHistoryRepository;
import cn.topiam.employee.common.repository.identitysource.IdentitySourceSyncRecordRepository;
import cn.topiam.employee.common.storage.Storage;
import cn.topiam.employee.core.message.mail.MailMsgEventPublish;
import cn.topiam.employee.core.message.sms.SmsMsgEventPublish;
import cn.topiam.employee.identitysource.core.domain.Dept;
import cn.topiam.employee.identitysource.core.processor.IdentitySourceSyncDeptPostProcessor;
import cn.topiam.employee.support.repository.domain.IdEntity;
import cn.topiam.employee.support.security.password.PasswordGenerator;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.EntityManager;
import static java.util.stream.Collectors.toList;

import static cn.topiam.employee.common.constant.AccountConstants.ROOT_DEPT_ID;
import static cn.topiam.employee.common.constant.AccountConstants.ROOT_DEPT_NAME;
import static cn.topiam.employee.common.constant.CommonConstants.PATH_SEPARATOR;
import static cn.topiam.employee.common.constant.CommonConstants.SYSTEM_DEFAULT_USER_NAME;

/**
 * 身份源数据 pull post 处理器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/3/1 22:04
 */
@SuppressWarnings("DuplicatedCode")
@Slf4j
@Component
public class DefaultIdentitySourceDeptPostProcessor extends AbstractIdentitySourcePostProcessor
                                                    implements IdentitySourceSyncDeptPostProcessor {

    @Override
    public void process(String batch, String identitySourceId, List<Dept> deptList,
                        LocalDateTime startTime, TriggerType triggerType) {
        //保存同步历史
        IdentitySourceSyncHistoryEntity history = new IdentitySourceSyncHistoryEntity();
        history.setBatch(batch);
        history.setIdentitySourceId(Long.valueOf(identitySourceId));
        history.setStartTime(startTime);
        history.setObjectType(IdentitySourceObjectType.ORGANIZATION);
        history.setTriggerType(triggerType);
        history.setStatus(SyncStatus.PENDING);
        identitySourceSyncHistoryRepository.save(history);
        //手动开启事务
        TransactionStatus transactionStatus = platformTransactionManager
            .getTransaction(transactionDefinition);
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            log.info("处理组织机构数据开始");
            IdentitySourceEntity identitySource = getIdentitySource(identitySourceId);
            StrategyConfig strategyConfig = getStrategyConfig(identitySourceId);
            //过滤掉身份源根目录
            deptList = deptList.stream().filter(
                item -> !getRootExternalId(identitySource.getProvider()).equals(item.getDeptId()))
                .collect(toList());
            Statistics statistics = new Statistics();
            //处理组织父节点
            rootOrganizationHandler(identitySource, deptList, strategyConfig, statistics);
            //处理组织子节点
            childrenOrganizationHandler(identitySource, deptList, strategyConfig, statistics);
            stopWatch.stop();
            log.info("处理组织机构数据结束, 执行时长: {} ms", stopWatch.getTotalTimeMillis());
            //更新同步历史
            history.setUpdatedCount(statistics.getUpdateOrganizations().size());
            history.setCreatedCount(statistics.getCreateOrganizations().size());
            history.setSkippedCount(statistics.getSkipOrganizations().size());
            history.setDeletedCount(statistics.getDeleteOrganizations().size());
            history.setEndTime(LocalDateTime.now());
            history.setStatus(SyncStatus.SUCCESS);
            identitySourceSyncHistoryRepository.save(history);
            //保存同步记录
            saveSyncHistoryRecord(history.getId(), statistics);
            //提交事务
            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            log.error("处理组织机构数据发生异常", e);
            platformTransactionManager.rollback(transactionStatus);
            //更新同步历史
            history.setEndTime(LocalDateTime.now());
            history.setStatus(SyncStatus.FAIL);
            identitySourceSyncHistoryRepository.save(history);
        }
    }

    /**
     * 处理根组织
     *
     * @param identitySource {@link IdentitySourceEntity}
     * @param deptList {@link Dept}
     * @param strategyConfig {@link StrategyConfig}
     * @param statistics {@link Statistics}
     */
    private void rootOrganizationHandler(IdentitySourceEntity identitySource, List<Dept> deptList,
                                         StrategyConfig strategyConfig, Statistics statistics) {

        // 身份源没有选择现有组织
        if (Objects.isNull(strategyConfig.getOrganization())
            || StringUtils.isBlank(strategyConfig.getOrganization().getTargetId())) {
            //创建根组织
            String targetId = String.valueOf(SNOWFLAKE.nextId());
            OrganizationEntity entity = createRootOrganization(targetId, identitySource,
                CollectionUtils.isEmpty(deptList));
            statistics.getCreateOrganizations().add(entity);
            //更新身份源根组织配置
            StrategyConfig.Organization organization = new StrategyConfig.Organization();
            organization.setTargetId(targetId);
            strategyConfig.setOrganization(organization);
            identitySourceRepository.updateStrategyConfig(identitySource.getId(),
                JSON.toJSONString(strategyConfig));
            return;
        }
        // 查询身份源配置的根组织，如果不存在，创建根组织
        String targetId = strategyConfig.getOrganization().getTargetId();
        Optional<OrganizationEntity> optional = organizationRepository.findById(targetId);
        if (optional.isEmpty()) {
            OrganizationEntity entity = createRootOrganization(targetId, identitySource,
                CollectionUtils.isEmpty(deptList));
            statistics.getCreateOrganizations().add(entity);
            return;
        }
        //存在根组织，部分字段不一致进行更新操作
        OrganizationEntity entity = optional.get();
        //TODO 如果存在多个身份源选择一个父组织的时候，扩展ID是否更新呢？不更新就会造成用户关联问题
        if (BooleanUtils.compare(entity.getLeaf(), CollectionUtils.isEmpty(deptList)) != 0
            || !StringUtils.equals(entity.getExternalId(),
                getRootExternalId(identitySource.getProvider()))) {
            entity.setLeaf(CollectionUtils.isEmpty(deptList));
            entity.setExternalId(getRootExternalId(identitySource.getProvider()));
            statistics.getUpdateOrganizations().add(entity);
            organizationRepository.save(entity);
        }
    }

    /**
     * 处理子节点
     *
     * 1、上游不存在，本地不存在-新增
     * 2、上游存在，本地存在-修改
     * 3、上游不存在，本地存在-删除
     *
     * @param identitySource {@link IdentitySourceEntity}
     * @param deptList {@link Dept}
     * @param strategyConfig {@link StrategyConfig}
     * @param statistics {@link Statistics}
     */
    private void childrenOrganizationHandler(IdentitySourceEntity identitySource,
                                             List<Dept> deptList, StrategyConfig strategyConfig,
                                             Statistics statistics) {
        //@formatter:off
        Set<OrganizationEntity> createSet = Sets.newLinkedHashSet(), updateSet = Sets.newLinkedHashSet();
        Set<SkipOrganization> skipSet=Sets.newLinkedHashSet();
        //查询身份源下所有数据
        List<OrganizationEntity> list = organizationRepository.findByIdentitySourceId(identitySource.getId());
        Optional<OrganizationEntity> optional = organizationRepository.findById(strategyConfig.getOrganization().getTargetId());
        if (optional.isEmpty()) {
            log.error("处理子组织数据失败，父组织不存在");
            throw new RuntimeException("处理子组织数据失败，父组织不存在");
        }
        //构建新关联关系
        Set<Modal> modals = buildModal(deptList, list);
        //构建更新&修改对象
        buildEntity(optional.get(), identitySource.getId(), modals, createSet, updateSet,skipSet);
        entityManager.clear();

        //保存
        if (!CollectionUtils.isEmpty(createSet)){
            organizationRepository.batchSave(new ArrayList<>(createSet));
            statistics.getCreateOrganizations().addAll(createSet);
        }
        //修改
        if (!CollectionUtils.isEmpty(updateSet)){
            organizationRepository.batchUpdate(new ArrayList<>(updateSet));
            statistics.getUpdateOrganizations().addAll(updateSet);
        }
        //所有当前部门三方ID列表
        //所有上游部门ID列表
        List<String> thirdPartyDeptIds = getDeptIds(deptList);
        //过滤要删除的部门
        Set<OrganizationEntity> deleteOrganizations = list.stream().filter(item -> !thirdPartyDeptIds.contains(item.getExternalId())).collect(Collectors.toSet());
        deleteOrganizations=deleteOrganizations.stream().filter(i-> !i.getExternalId().equals(getRootExternalId(identitySource.getProvider()))).collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(deleteOrganizations)){
            organizationRepository.deleteAllById(deleteOrganizations.stream().map(IdEntity::getId).collect(toList()));
            statistics.getDeleteOrganizations().addAll(deleteOrganizations);
        }
        //跳过
        statistics.getSkipOrganizations().addAll(skipSet);
        //@formatter:on
    }

    private List<String> getDeptIds(List<Dept> deptList) {
        List<String> ids = deptList.stream().map(Dept::getDeptId).distinct().collect(toList());
        deptList.forEach(item -> {
            if (!CollectionUtils.isEmpty(item.getChildren())) {
                List<String> deptIds = getDeptIds(item.getChildren());
                ids.addAll(deptIds);
            }
        });
        return ids;
    }

    /**
     * 根据新模型，构建新增，修改数据集
     *
     * @param modals {@link Modal} 新模型数据
     *
     */
    private void buildEntity(OrganizationEntity parent, Long identitySourceId, Set<Modal> modals,
                             Set<OrganizationEntity> createSet, Set<OrganizationEntity> updateSet,
                             Set<SkipOrganization> skipSet) {
        for (Modal modal : modals) {
            Dept thirdParty = modal.getThirdParty();
            Set<Modal> children = modal.getChildren();
            IdentitySourceEntity identitySource = getIdentitySource(identitySourceId.toString());
            //构建新增数据
            if (modal.isNew()) {
                OrganizationEntity entity = new OrganizationEntity();
                entity.setId(String.valueOf(SNOWFLAKE.nextId()));
                entity.setName(thirdParty.getName());
                entity.setCode(RandomStringUtils.randomAlphanumeric(7));
                entity.setType(OrganizationType.DEPARTMENT);
                entity.setParentId(parent.getId());
                entity.setPath(parent.getPath() + PATH_SEPARATOR + entity.getId());
                entity.setDisplayPath(parent.getDisplayPath() + PATH_SEPARATOR + entity.getName());
                entity.setExternalId(thirdParty.getDeptId());
                entity.setDataOrigin(getDataOrigin(identitySource.getProvider()));
                entity.setIdentitySourceId(identitySourceId);
                entity.setLeaf(CollectionUtils.isEmpty(children));
                entity.setEnabled(Boolean.TRUE);
                entity.setOrder(thirdParty.getOrder());
                entity.setCreateBy(SYSTEM_DEFAULT_USER_NAME);
                entity.setCreateTime(LocalDateTime.now());
                entity.setUpdateBy(SYSTEM_DEFAULT_USER_NAME);
                entity.setUpdateTime(LocalDateTime.now());
                createSet.add(entity);
                //处理子节点
                if (!CollectionUtils.isEmpty(children)) {
                    buildEntity(entity, identitySourceId, children, createSet, updateSet, skipSet);
                }
            }
            //构建修改数据
            else {
                OrganizationEntity current = modal.getCurrent();
                //是否需要修改
                if (!equalsOrganization(modal, parent)) {
                    current.setName(thirdParty.getName());
                    current.setOrder(thirdParty.getOrder());
                    current.setParentId(parent.getId());
                    current.setPath(parent.getPath() + PATH_SEPARATOR + current.getId());
                    current.setDisplayPath(
                        parent.getDisplayPath() + PATH_SEPARATOR + thirdParty.getName());
                    current.setExternalId(thirdParty.getDeptId());
                    current.setLeaf(CollectionUtils.isEmpty(children));
                    current.setUpdateBy(SYSTEM_DEFAULT_USER_NAME);
                    current.setUpdateTime(LocalDateTime.now());
                    updateSet.add(current);
                } else {
                    skipSet.add(SkipOrganization.builder().organization(current)
                        .actionType(IdentitySourceActionType.UPDATE).reason("组织信息一致").build());
                }
                //处理子节点
                if (!CollectionUtils.isEmpty(children)) {
                    buildEntity(current, identitySourceId, children, createSet, updateSet, skipSet);
                }
            }
        }
    }

    /**
     * 是否需要更新组织
     *
     * @param modal {@link OrganizationEntity} 模型
     * @param parent {@link OrganizationEntity} 父组织
     * @return {@link Boolean} 是否需要修改
     */
    private Boolean equalsOrganization(Modal modal, OrganizationEntity parent) {
        Dept thirdParty = modal.getThirdParty();
        OrganizationEntity current = modal.getCurrent();
        //@formatter:off
        return
                //名称
                StringUtils.equals(thirdParty.getName(),current.getName()) &&
                //上级部门ID
                StringUtils.equals(parent.getId(),current.getParentId()) &&
                //部门PATH
                StringUtils.equals(parent.getPath() + PATH_SEPARATOR + current.getId(),current.getPath()) &&
                //部门显示PATH
                StringUtils.equals(parent.getDisplayPath() + PATH_SEPARATOR + thirdParty.getName(),current.getDisplayPath()) &&
                //是否叶子节点
                Objects.equals(CollectionUtils.isEmpty(modal.getChildren()),current.getLeaf()) &&
                //次序
                Objects.equals(thirdParty.getOrder(),current.getOrder());
        //@formatter:on
    }

    /**
     * 根据同步数据构建模型
     *
     * @param deptList {@link Dept} 同步来源数据
     * @param list {@link OrganizationEntity} 数据库现有数据
     */
    private Set<Modal> buildModal(List<Dept> deptList, List<OrganizationEntity> list) {
        if (Objects.isNull(deptList)) {
            return Collections.emptySet();
        }
        Set<Modal> modals = Sets.newLinkedHashSet();
        //KEY：外部ID，VALUE: 部门实体
        Map<String, OrganizationEntity> map = new HashMap<>(16);
        list.forEach(dept -> map.put(dept.getExternalId(), dept));
        for (Dept dept : deptList) {
            if (map.containsKey(dept.getDeptId())) {
                Modal modal = new Modal();
                modal.setCurrent(map.get(dept.getDeptId()));
                modal.setThirdParty(dept);
                modal.setChildren(buildModal(dept.getChildren(), list));
                modals.add(modal);
                continue;
            }
            Modal modal = new Modal();
            modal.setThirdParty(dept);
            modal.setChildren(buildModal(dept.getChildren(), list));
            modals.add(modal);
        }
        return modals;
    }

    /**
     * 创建根组织
     *
     * @param identitySource {@link String} 身份提供商
     * @param isLeaf {@link Boolean} 是否叶子节点
     */
    private OrganizationEntity createRootOrganization(String id,
                                                      IdentitySourceEntity identitySource,
                                                      Boolean isLeaf) {
        //@formatter:off
        //添加节点
        entityManager.createNativeQuery("INSERT INTO organization (id_, code_, name_, parent_id, is_leaf, external_id, data_origin, type_, is_enabled, path_, display_path, identity_source_id,create_by,update_by,is_deleted) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)")
                .setParameter(1,id)
                .setParameter(2,RandomStringUtils.randomAlphanumeric(7))
                .setParameter(3,identitySource.getName())
                .setParameter(4,ROOT_DEPT_ID)
                .setParameter(5,isLeaf)
                .setParameter(6,getRootExternalId(identitySource.getProvider()))
                .setParameter(7,getDataOrigin(identitySource.getProvider()).getCode())
                .setParameter(8, OrganizationType.DEPARTMENT.getCode())
                .setParameter(9,Boolean.TRUE)
                .setParameter(10,PATH_SEPARATOR + ROOT_DEPT_ID + PATH_SEPARATOR + id)
                .setParameter(11,PATH_SEPARATOR + ROOT_DEPT_NAME + PATH_SEPARATOR + identitySource.getName())
                .setParameter(12,identitySource.getId())
                .setParameter(13,SYSTEM_DEFAULT_USER_NAME)
                .setParameter(14,SYSTEM_DEFAULT_USER_NAME)
                .setParameter(15,false)
                .executeUpdate();
        //@formatter:on
        return organizationRepository.findById(id).orElse(null);
    }

    /**
     * 新增同步记录
     *
     * @param historyId  {@link Long}
     * @param statistics {@link Statistics}
     */
    private void saveSyncHistoryRecord(Long historyId, Statistics statistics) {
        //创建组织
        saveCreateOrganizationsRecord(historyId, statistics.getCreateOrganizations());
        //更新组织记录
        saveUpdateOrganizationsRecord(historyId, statistics.getUpdateOrganizations());
        //删除组织记录
        saveDeleteOrganizationsRecord(historyId, statistics.getDeleteOrganizations());
        //跳过组织记录
        saveSkipOrganizationsRecord(historyId, statistics.getSkipOrganizations());
    }

    /**
     * 保存创建组织记录
     *
     * @param historyId {@link Long}
     * @param createOrganizations {@link Set}
     */
    private void saveCreateOrganizationsRecord(Long historyId,
                                               Set<OrganizationEntity> createOrganizations) {
        List<IdentitySourceSyncRecordEntity> list = Lists.newArrayList();
        createOrganizations.forEach(user -> {
            IdentitySourceSyncRecordEntity entity = new IdentitySourceSyncRecordEntity();
            entity.setId(SNOWFLAKE.nextId());
            entity.setSyncHistoryId(historyId);
            entity.setStatus(SyncStatus.SUCCESS);
            entity.setObjectType(IdentitySourceObjectType.ORGANIZATION);
            entity.setObjectId(user.getId());
            entity.setObjectName(user.getName());
            entity.setActionType(IdentitySourceActionType.INSERT);
            //其他信息
            entity.setCreateBy(SYSTEM_DEFAULT_USER_NAME);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateBy(SYSTEM_DEFAULT_USER_NAME);
            entity.setUpdateTime(LocalDateTime.now());
            list.add(entity);
        });
        //保存
        identitySourceSyncRecordRepository.batchSave(list);
    }

    /**
     * 保存更新组织记录
     *
     * @param historyId {@link Long}
     * @param updateOrganizations {@link Set}
     */
    private void saveUpdateOrganizationsRecord(Long historyId,
                                               Set<OrganizationEntity> updateOrganizations) {
        List<IdentitySourceSyncRecordEntity> list = Lists.newArrayList();
        updateOrganizations.forEach(user -> {
            IdentitySourceSyncRecordEntity entity = new IdentitySourceSyncRecordEntity();
            entity.setId(SNOWFLAKE.nextId());
            entity.setSyncHistoryId(historyId);
            entity.setStatus(SyncStatus.SUCCESS);
            entity.setObjectType(IdentitySourceObjectType.ORGANIZATION);
            entity.setObjectId(user.getId());
            entity.setObjectName(user.getName());
            entity.setActionType(IdentitySourceActionType.UPDATE);
            //其他信息
            entity.setCreateBy(SYSTEM_DEFAULT_USER_NAME);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateBy(SYSTEM_DEFAULT_USER_NAME);
            entity.setUpdateTime(LocalDateTime.now());
            list.add(entity);
        });
        //保存
        identitySourceSyncRecordRepository.batchSave(list);
    }

    /**
     * 保存删除组织记录
     *
     * @param historyId {@link Long}
     * @param deleteOrganizations {@link Set}
     */
    private void saveDeleteOrganizationsRecord(Long historyId,
                                               Set<OrganizationEntity> deleteOrganizations) {
        List<IdentitySourceSyncRecordEntity> list = Lists.newArrayList();
        deleteOrganizations.forEach(user -> {
            IdentitySourceSyncRecordEntity entity = new IdentitySourceSyncRecordEntity();
            entity.setId(SNOWFLAKE.nextId());
            entity.setSyncHistoryId(historyId);
            entity.setStatus(SyncStatus.SUCCESS);
            entity.setObjectType(IdentitySourceObjectType.ORGANIZATION);
            entity.setObjectId(user.getId());
            entity.setObjectName(user.getName());
            entity.setActionType(IdentitySourceActionType.DELETE);
            //其他信息
            entity.setCreateBy(SYSTEM_DEFAULT_USER_NAME);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateBy(SYSTEM_DEFAULT_USER_NAME);
            entity.setUpdateTime(LocalDateTime.now());
            list.add(entity);
        });
        //保存
        identitySourceSyncRecordRepository.batchSave(list);
    }

    /**
     * 保存跳过组织记录
     *
     * @param historyId {@link Long}
     * @param skipOrganizations {@link Set}
     */
    private void saveSkipOrganizationsRecord(Long historyId,
                                             Set<SkipOrganization> skipOrganizations) {
        List<IdentitySourceSyncRecordEntity> list = Lists.newArrayList();
        skipOrganizations.forEach(skipUser -> {
            OrganizationEntity organization = skipUser.getOrganization();
            IdentitySourceSyncRecordEntity entity = new IdentitySourceSyncRecordEntity();
            entity.setId(SNOWFLAKE.nextId());
            entity.setSyncHistoryId(historyId);
            entity.setStatus(SyncStatus.SKIP);
            entity.setObjectType(IdentitySourceObjectType.ORGANIZATION);
            entity.setObjectId(organization.getId());
            entity.setObjectName(organization.getName());
            entity.setActionType(skipUser.getActionType());
            //其他信息
            entity.setCreateBy(SYSTEM_DEFAULT_USER_NAME);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateBy(SYSTEM_DEFAULT_USER_NAME);
            entity.setUpdateTime(LocalDateTime.now());
            entity.setDesc(skipUser.getReason());
            list.add(entity);
        });
        //保存
        identitySourceSyncRecordRepository.batchSave(list);
    }

    @Data
    public static class Modal implements Serializable {
        /**
         * 主数据 (如果为空，代表新数据)
         */
        private OrganizationEntity current;
        /**
         * 第三方数据
         */
        private Dept               thirdParty;
        /**
         * 子集
         */
        private Set<Modal>         children;

        public Boolean isNew() {
            return Objects.isNull(current);
        }
    }

    @Data
    public static class Statistics implements Serializable {

        @Serial
        private static final long       serialVersionUID = 7025078516116020630L;

        /**
         * 创建
         */
        private Set<OrganizationEntity> createOrganizations;

        /**
         * 修改
         */
        private Set<OrganizationEntity> updateOrganizations;

        /**
         * 删除
         */
        private Set<OrganizationEntity> deleteOrganizations;

        /**
         * 跳过
         */
        private Set<SkipOrganization>   skipOrganizations;

        public Statistics() {
            this.setUpdateOrganizations(Sets.newLinkedHashSet());
            this.setSkipOrganizations(Sets.newLinkedHashSet());
            this.setCreateOrganizations(Sets.newLinkedHashSet());
            this.setDeleteOrganizations(Sets.newLinkedHashSet());
        }
    }

    @Data
    @Builder
    private static class SkipOrganization {

        private OrganizationEntity       organization;
        private IdentitySourceActionType actionType;
        private String                   reason;
    }

    /**
     * EntityManager
     */
    private final EntityManager            entityManager;
    /**
     * 身份源 Repository
     */
    private final IdentitySourceRepository identitySourceRepository;
    /**
     * 组织 Repository
     */
    private final OrganizationRepository   organizationRepository;

    public DefaultIdentitySourceDeptPostProcessor(SmsMsgEventPublish smsMsgEventPublish,
                                                  MailMsgEventPublish mailMsgEventPublish,
                                                  PasswordEncoder passwordEncoder,
                                                  PasswordGenerator passwordGenerator,
                                                  TransactionDefinition transactionDefinition,
                                                  PlatformTransactionManager platformTransactionManager,
                                                  EntityManager entityManager,
                                                  IdentitySourceRepository identitySourceRepository,
                                                  OrganizationRepository organizationRepository,
                                                  IdentitySourceSyncHistoryRepository identitySourceSyncHistoryRepository,
                                                  IdentitySourceSyncRecordRepository identitySourceSyncRecordRepository,
                                                  Storage storage) {
        super(smsMsgEventPublish, mailMsgEventPublish, passwordEncoder, passwordGenerator,
            transactionDefinition, platformTransactionManager, entityManager,
            identitySourceRepository, identitySourceSyncHistoryRepository,
            identitySourceSyncRecordRepository, storage);
        this.entityManager = entityManager;
        this.identitySourceRepository = identitySourceRepository;
        this.organizationRepository = organizationRepository;
    }
}
