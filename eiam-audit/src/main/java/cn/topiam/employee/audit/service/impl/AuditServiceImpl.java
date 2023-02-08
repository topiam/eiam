/*
 * eiam-audit - Employee Identity and Access Management Program
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
package cn.topiam.employee.audit.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import cn.topiam.employee.audit.controller.pojo.AuditDictResult;
import cn.topiam.employee.audit.controller.pojo.AuditListQuery;
import cn.topiam.employee.audit.controller.pojo.AuditListResult;
import cn.topiam.employee.audit.entity.AuditElasticSearchEntity;
import cn.topiam.employee.audit.enums.EventType;
import cn.topiam.employee.audit.service.AuditService;
import cn.topiam.employee.audit.service.converter.AuditDataConverter;
import cn.topiam.employee.common.enums.UserType;
import cn.topiam.employee.core.configuration.EiamSupportProperties;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;

import lombok.AllArgsConstructor;
import static cn.topiam.employee.common.constants.AuditConstants.getAuditIndexPrefix;

/**
 * 审计 service impl
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/10 23:06
 */
@Service
@AllArgsConstructor
public class AuditServiceImpl implements AuditService {

    /**
     * List
     *
     * @param query {@link AuditListQuery}
     * @param page {@link PageModel}
     * @return {@link Page}
     */
    @Override
    public Page<AuditListResult> getAuditList(AuditListQuery query, PageModel page) {
        //查询入参转查询条件
        NativeSearchQuery nsq = auditDataConverter.auditListRequestConvertToNativeSearchQuery(query,
            page);
        //查询列表
        SearchHits<AuditElasticSearchEntity> search = elasticsearchRestTemplate.search(nsq,
            AuditElasticSearchEntity.class, IndexCoordinates
                .of(getAuditIndexPrefix(eiamSupportProperties.getDemo().isOpen()) + "*"));
        //结果转返回结果
        return auditDataConverter.searchHitsConvertToAuditListResult(search, page);
    }

    /**
     * 获取字典类型
     *
     * @param userType {@link UserType}
     * @return {@link List}
     */
    @Override
    public List<AuditDictResult> getAuditDict(UserType userType) {
        List<EventType> types = Arrays.asList(EventType.values());
        //获取分组
        List<AuditDictResult> list = types.stream().map(EventType::getResource).toList().stream()
            .distinct().toList().stream().map(resource -> {
                AuditDictResult group = new AuditDictResult();
                group.setName(resource.getName());
                group.setCode(resource.getCode());
                return group;
            }).collect(Collectors.toList());
        //处理每个分组的审计类型
        list.forEach(dict -> {
            Set<AuditDictResult.AuditType> auditTypes = new HashSet<>();
            types.stream()
                .filter(auditType -> auditType.getResource().getCode().equals(dict.getCode()))
                .forEach(auditType -> {
                    if (auditType.getUserTypes().contains(userType)) {
                        AuditDictResult.AuditType type = new AuditDictResult.AuditType();
                        type.setName(auditType.getDesc());
                        type.setCode(auditType.getCode());
                        auditTypes.add(type);
                    }
                });
            dict.setTypes(auditTypes);
        });
        list = list.stream().filter(i -> !CollectionUtils.isEmpty(i.getTypes()))
            .collect(Collectors.toList());
        return list;
    }

    /**
     * EiamSupportProperties
     */
    private final EiamSupportProperties     eiamSupportProperties;

    /**
     * ElasticsearchRestTemplate
     */
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * AuditDataConverter
     */
    private final AuditDataConverter        auditDataConverter;

}
