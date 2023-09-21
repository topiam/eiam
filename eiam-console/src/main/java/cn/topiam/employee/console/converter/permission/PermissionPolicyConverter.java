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
package cn.topiam.employee.console.converter.permission;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.util.CollectionUtils;

import cn.topiam.employee.common.entity.permission.PermissionPolicyEntity;
import cn.topiam.employee.common.entity.permission.po.PermissionPolicyPO;
import cn.topiam.employee.console.pojo.result.permission.PermissionPolicyListResult;
import cn.topiam.employee.console.pojo.save.permission.PermissionPolicyCreateParam;
import cn.topiam.employee.console.pojo.update.permission.PermissionPolicyUpdateParam;
import cn.topiam.employee.support.repository.page.domain.Page;

/**
 * 策略映射
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/14 22:45
 */
@Mapper(componentModel = "spring", uses = PermissionActionConverter.class)
public interface PermissionPolicyConverter {

    /**
     * 资源创建参数转实体类
     *
     * @param param {@link PermissionPolicyCreateParam}
     * @return {@link PermissionPolicyEntity}
     */
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "remark", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    PermissionPolicyEntity policyCreateParamConvertToEntity(PermissionPolicyCreateParam param);

    /**
     * 资源修改参数转实体类
     *
     * @param param {@link PermissionPolicyCreateParam}
     * @return {@link PermissionPolicyEntity}
     */
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "remark", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    PermissionPolicyEntity policyUpdateParamConvertToEntity(PermissionPolicyUpdateParam param);

    /**
     * 资源转换为资源列表结果
     *
     * @param page {@link Page}
     * @return {@link Page}
     */
    default Page<PermissionPolicyListResult> entityConvertToPolicyListResult(org.springframework.data.domain.Page<PermissionPolicyPO> page) {
        Page<PermissionPolicyListResult> result = new Page<>();
        List<PermissionPolicyPO> pageList = page.getContent();
        if (!CollectionUtils.isEmpty(pageList)) {
            //@formatter:off
            result.setPagination(Page.Pagination.builder()
                    .total(page.getTotalElements())
                    .totalPages(page.getTotalPages())
                    .current(page.getPageable().getPageNumber() + 1)
                    .build());
            //@formatter:on
            List<PermissionPolicyListResult> list = new ArrayList<>();
            for (PermissionPolicyPO po : pageList) {
                list.add(entityConvertToPolicyListResult(po));
            }
            result.setList(list);
        }
        return result;
    }

    /**
     * entityConvertToPolicyListResult
     *
     * @param entity {@link PermissionPolicyListResult}
     * @return {@link PermissionPolicyPO}
     */
    PermissionPolicyListResult entityConvertToPolicyListResult(PermissionPolicyPO entity);
}
