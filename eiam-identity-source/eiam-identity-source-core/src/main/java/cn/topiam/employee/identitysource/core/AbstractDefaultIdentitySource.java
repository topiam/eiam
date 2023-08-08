/*
 * eiam-identity-source-core - Employee Identity and Access Management
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
package cn.topiam.employee.identitysource.core;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;

import cn.topiam.employee.common.enums.TriggerType;
import cn.topiam.employee.identitysource.core.client.IdentitySourceClient;
import cn.topiam.employee.identitysource.core.domain.Dept;
import cn.topiam.employee.identitysource.core.domain.User;
import cn.topiam.employee.identitysource.core.processor.IdentitySourceEventPostProcessor;
import cn.topiam.employee.identitysource.core.processor.IdentitySourceSyncDeptPostProcessor;
import cn.topiam.employee.identitysource.core.processor.IdentitySourceSyncUserPostProcessor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AbstractDefaultIdentityProvider
 * <p>
 * 这个只适合目前流行的钉钉、企业微信等，如果是AD,LDAP，不行
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/3/1 21:47
 */
@Slf4j
@Getter
@RequiredArgsConstructor
public abstract class AbstractDefaultIdentitySource<T extends IdentitySourceConfig>
                                                   implements IdentitySource<T> {

    public static final String URL_JOINER = "/";

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * sync
     *
     * @param triggerType {@link TriggerType} 执行方式
     */
    @Override
    public void sync(TriggerType triggerType) {
        String batch = RandomStringUtils.randomAlphanumeric(7);
        LocalDateTime deptListStartTime = LocalDateTime.now();
        //拉取部门
        List<Dept> deptList = identitySourceClient.getDeptList();
        //处理部门信息
        identitySourceSyncDeptPostProcessor.process(batch, getId(), deptList, deptListStartTime,
            triggerType);

        // 拉取用户，根据部门拉取用户，而不是直接调用getUserList()，第一考虑性能，也是为了数据准确性。
        LocalDateTime userListStartTime = LocalDateTime.now();
        List<User> userList = new ArrayList<>();
        for (Dept dept : deptList) {
            userList.addAll(identitySourceClient.getUserList(dept));
        }
        userList = userList.stream().distinct().collect(Collectors.toList());
        // 处理用户数据
        identitySourceSyncUserPostProcessor.process(batch, getId(), userList, userListStartTime,
            triggerType);
    }

    protected List<User> getUser(String id) {
        List<User> userList = new ArrayList<>();
        userList.add(getIdentitySourceClient().getUser(id));
        return userList;
    }

    protected List<Dept> getDept(String id) {
        List<Dept> deptList = new ArrayList<>();
        deptList.add(getIdentitySourceClient().getDept(id));
        return deptList;
    }

    protected List<String> getIdList(String id) {
        List<String> idList = new ArrayList<>();
        idList.add(id);
        return idList;
    }

    /**
     * 身份源ID
     */
    private final String                                id;

    /**
     * 身份源名称
     */
    private final String                                name;

    /**
     * 提供商配置
     */
    private final T                                     config;

    /**
     * 身份源提供商客户端
     */
    protected final IdentitySourceClient                identitySourceClient;

    /**
     * 身份源同步后处理器
     */
    protected final IdentitySourceSyncUserPostProcessor identitySourceSyncUserPostProcessor;

    /**
     * 身份源同步后处理器
     */
    protected final IdentitySourceSyncDeptPostProcessor identitySourceSyncDeptPostProcessor;

    /**
     * 身份源事件接收后处理器
     */
    protected final IdentitySourceEventPostProcessor    identitySourceEventPostProcessor;
}
