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
package cn.topiam.employee.console.initializer;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import cn.topiam.employee.common.entity.account.OrganizationEntity;
import cn.topiam.employee.common.enums.account.OrganizationType;
import cn.topiam.employee.common.repository.account.OrganizationRepository;
import cn.topiam.employee.support.config.AbstractSystemInitializer;
import cn.topiam.employee.support.config.InitializationException;
import cn.topiam.employee.support.security.util.SecurityUtils;
import static cn.topiam.employee.support.constant.EiamConstants.*;
import static cn.topiam.employee.support.security.userdetails.DataOrigin.INPUT;

/**
 * SystemInitializer
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2024/04/04 21:24
 */
@Component
public class RootOrganizationInitializer extends AbstractSystemInitializer {

    private final Logger logger = LoggerFactory.getLogger(RootOrganizationInitializer.class);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void init() throws InitializationException {
        //@formatter:off
        Optional<OrganizationEntity> optional = organizationRepository.findById(ROOT_NODE);
        if (optional.isEmpty()) {
            logger.info("初始化父级组织");
            OrganizationEntity organization = new OrganizationEntity();
            organization.setId(ROOT_NODE);
            organization.setName(ROOT_DEPT_NAME);
            organization.setCode(ROOT_NODE);
            organization.setPath(PATH_SEPARATOR+ROOT_NODE);
            organization.setDisplayPath(PATH_SEPARATOR+ROOT_DEPT_NAME);
            organization.setDataOrigin(INPUT.getType());
            organization.setType(OrganizationType.GROUP);
            organization.setLeaf(false);
            organization.setEnabled(true);
            organization.setOrder(0L);
            organization.setCreateBy(SecurityUtils.getCurrentUserName());
            organization.setCreateTime(LocalDateTime.now());
            organization.setUpdateBy(SecurityUtils.getCurrentUserName());
            organization.setUpdateTime(LocalDateTime.now());
            organization.setRemark("Root organization");
            organizationRepository.batchSave(Lists.newArrayList(organization));
        }
        //@formatter:on
    }

    @Override
    public int getOrder() {
        return 1;
    }

    /**
     * OrganizationRepository
     */
    private final OrganizationRepository organizationRepository;

    /**
     *
     * @param organizationRepository {@link OrganizationRepository}
     */
    public RootOrganizationInitializer(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }
}
