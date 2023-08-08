/*
 * eiam-audit - Employee Identity and Access Management
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
package cn.topiam.employee.audit.service.impl;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import cn.topiam.employee.audit.controller.pojo.AuditListQuery;
import cn.topiam.employee.audit.controller.pojo.AuditListResult;
import cn.topiam.employee.audit.controller.pojo.DictResult;
import cn.topiam.employee.audit.entity.AuditElasticSearchEntity;
import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.audit.service.AuditService;
import cn.topiam.employee.audit.service.converter.AuditDataConverter;
import cn.topiam.employee.support.autoconfiguration.SupportProperties;
import cn.topiam.employee.support.exception.BadParamsException;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import cn.topiam.employee.support.security.userdetails.UserType;
import cn.topiam.employee.support.security.util.SecurityUtils;
import static cn.topiam.employee.common.constant.AuditConstants.getAuditIndexPrefix;
import static cn.topiam.employee.support.security.userdetails.UserType.USER;

/**
 * 审计 service impl
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/10 23:06
 */
@Service
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
        if (USER.equals(SecurityUtils.getCurrentUser().getUserType())
            && !USER.getType().equals(query.getUserType())) {
            throw new BadParamsException("用户类型错误");
        }
        //查询入参转查询条件
        NativeQuery nsq = auditDataConverter.auditListRequestConvertToNativeQuery(query, page);
        //查询列表
        SearchHits<AuditElasticSearchEntity> search = elasticsearchTemplate.search(nsq,
            AuditElasticSearchEntity.class, IndexCoordinates
                .of(getAuditIndexPrefix(supportProperties.getAudit().getIndexPrefix()) + "*"));
        //结果转返回结果
        return auditDataConverter.searchHitsConvertToAuditListResult(search, page);
    }

    /**
     * 获取字典类型
     *
     * @param userType {@link String}
     * @return {@link List}
     */
    @Override
    public List<DictResult> getAuditDict(String userType) {
        List<EventType> types = Arrays.asList(EventType.values());
        //获取分组
        List<DictResult> list = types.stream().map(EventType::getResource).toList().stream()
            .distinct().toList().stream().map(resource -> {
                DictResult group = new DictResult();
                group.setName(resource.getName());
                group.setCode(resource.getCode());
                return group;
            }).collect(Collectors.toList());
        //处理每个分组的审计类型
        list.forEach(dict -> {
            Set<DictResult.AuditType> auditTypes = new LinkedHashSet<>();
            types.stream()
                .filter(auditType -> auditType.getResource().getCode().equals(dict.getCode()))
                .forEach(auditType -> {
                    if (auditType.getUserTypes().stream().map(UserType::getType).toList()
                        .contains(userType)) {
                        DictResult.AuditType type = new DictResult.AuditType();
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
     * AuditProperties
     */
    private final SupportProperties     supportProperties;

    /**
     * ElasticsearchTemplate
     */
    private final ElasticsearchTemplate elasticsearchTemplate;

    /**
     * AuditDataConverter
     */
    private final AuditDataConverter    auditDataConverter;

    public AuditServiceImpl(SupportProperties supportProperties,
                            ElasticsearchTemplate elasticsearchTemplate,
                            AuditDataConverter auditDataConverter) {
        this.supportProperties = supportProperties;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.auditDataConverter = auditDataConverter;
    }

}
