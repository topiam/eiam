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
package cn.topiam.employee.openapi.converter.app;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.util.CollectionUtils;

import cn.topiam.employee.common.entity.app.AppPermissionPolicyEntity;
import cn.topiam.employee.common.entity.app.po.AppPermissionPolicyPO;
import cn.topiam.employee.openapi.pojo.request.app.save.AppPermissionPolicyCreateParam;
import cn.topiam.employee.openapi.pojo.request.app.update.AppPermissionPolicyUpdateParam;
import cn.topiam.employee.support.repository.page.domain.Page;

/**
 * 策略映射
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/8/14 22:45
 */
@Mapper(componentModel = "spring", uses = AppPermissionActionConverter.class)
public interface AppPermissionPolicyConverter {

    /**
     * 资源创建参数转实体类
     *
     * @param param {@link AppPermissionPolicyCreateParam}
     * @return {@link AppPermissionPolicyEntity}
     */
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "remark", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    AppPermissionPolicyEntity policyCreateParamConvertToEntity(AppPermissionPolicyCreateParam param);

    /**
     * 资源修改参数转实体类
     *
     * @param param {@link AppPermissionPolicyCreateParam}
     * @return {@link AppPermissionPolicyEntity}
     */
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "remark", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    AppPermissionPolicyEntity policyUpdateParamConvertToEntity(AppPermissionPolicyUpdateParam param);

    /**
     * 资源转换为资源列表结果
     *
     * @param page {@link Page}
     * @return {@link Page}
     */
    default Page<AppPermissionPolicyPO> entityConvertToPolicyListResult(org.springframework.data.domain.Page<AppPermissionPolicyPO> page) {
        Page<AppPermissionPolicyPO> result = new Page<>();
        List<AppPermissionPolicyPO> pageList = page.getContent();
        if (!CollectionUtils.isEmpty(pageList)) {
            //@formatter:off
            result.setPagination(Page.Pagination.builder()
                    .total(page.getTotalElements())
                    .totalPages(page.getTotalPages())
                    .current(page.getPageable().getPageNumber() + 1)
                    .build());
            //@formatter:on
            result.setList(pageList);
        }
        return result;
    }
}
