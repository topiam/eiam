/*
 * eiam-identity-source-wechatwork - Employee Identity and Access Management
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
package cn.topiam.employee.identitysource.wechatwork.client;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.util.Assert;

import cn.topiam.employee.identitysource.core.client.IdentitySourceClient;
import cn.topiam.employee.identitysource.core.domain.Dept;
import cn.topiam.employee.identitysource.core.domain.User;
import cn.topiam.employee.identitysource.core.domain.UserDetail;
import cn.topiam.employee.identitysource.core.exception.ApiCallException;
import cn.topiam.employee.identitysource.wechatwork.WeChatWorkConfig;
import cn.topiam.employee.identitysource.wechatwork.WeChatWorkConstant;
import cn.topiam.employee.identitysource.wechatwork.domain.response.*;

import lombok.extern.slf4j.Slf4j;

/**
 * 企业微信提供商client
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/29 21:56
 */
@Slf4j(topic = WeChatWorkConstant.LOGGER_NAME)
public class WeChatWorkClient extends AbstractWeChatWorkClient implements IdentitySourceClient {

    public static final String ACCESS_TOKEN = "?access_token=";

    public WeChatWorkClient(WeChatWorkConfig config) {
        super(config);
    }

    /**
     *  build user
     *
     * @param userid {@link  String}
     * @param active {@link  Boolean}
     * @param name {@link  String}
     * @param deptIdList {@link  List}
     * @param avatar {@link  String}
     * @param email {@link  String}
     * @param orgEmail {@link  String}
     * @param mobile {@link  String}
     * @return {@link  User}
     */
    private User buildUser(String userid, Boolean active, String name, List<String> deptIdList,
                           String avatar, String email, String orgEmail, String mobile) {
        User user = new User();
        user.setUserId(userid);
        user.setActive(active);
        user.setDeptIdList(deptIdList);
        user.setAvatar(avatar);
        user.setEmail(email);
        user.setOrgEmail(orgEmail);
        user.setPhone(mobile);
        user.setPhoneAreaCode("86");
        UserDetail userDetail = new UserDetail();
        userDetail.setName(name);
        userDetail.setNickName(name);
        user.setUserDetail(userDetail);
        return user;
    }

    @Override
    public Dept getDept(String id) {
        String queryParams = ACCESS_TOKEN + getAccessToken() + "&id=" + id;
        GetDeptResponse deptDTO = restOperations
            .getForObject(WeChatWorkConstant.DEPT_DETAILS + queryParams, GetDeptResponse.class);
        Assert.notNull(deptDTO, "获取部门详情返回结果为空");
        if (deptDTO.isSuccess()) {
            Department department = deptDTO.getDepartment();
            Dept dept = new Dept();
            dept.setDeptId(String.valueOf(department.getId()));
            dept.setParentId(String.valueOf(department.getParentId()));
            dept.setName(department.getName());
            dept.setOrder(department.getOrder());
            return dept;
        }
        log.error("企业微信获取部门详情信息失败: {}", deptDTO.getErrMsg());
        throw new ApiCallException("企业微信获取部门详情信息失败: " + deptDTO.getErrMsg());
    }

    @Override
    public List<Dept> getDeptList(String id) {
        String queryParams = ACCESS_TOKEN + getAccessToken() + "&id=" + id;
        GetDeptListResponse deptDTO = restOperations
            .getForObject(WeChatWorkConstant.DEPT_URL + queryParams, GetDeptListResponse.class);
        Assert.notNull(deptDTO, "获取部门列表返回结果为空");
        if (deptDTO.isSuccess()) {
            List<Department> department = deptDTO.getDepartment();
            List<Dept> deptList = department.stream().map(item -> {
                Dept dept = new Dept();
                dept.setDeptId(String.valueOf(item.getId()));
                dept.setParentId(String.valueOf(item.getParentId()));
                dept.setName(item.getName());
                return dept;
            }).toList();

            Map<String, List<Dept>> groupDept = deptList.stream()
                .collect(Collectors.groupingBy(Dept::getParentId));
            deptList.forEach(item -> item.setChildren(groupDept.get(item.getDeptId())));
            return deptList.stream().filter(dept -> dept.getParentId().equals(id)).toList();
        }
        log.error("企业微信获取子部门列表失败: {}", deptDTO.getErrMsg());
        throw new ApiCallException("企业微信获取子部门列表失败: " + deptDTO.getErrMsg());
    }

    @Override
    public List<Dept> getDeptList() {
        return getDeptList(getRootId());
    }

    @Override
    public User getUser(String userId) {
        String queryParams = ACCESS_TOKEN + getAccessToken() + "&userid=" + userId;
        GetUserResponse user = restOperations
            .getForObject(WeChatWorkConstant.USER_DETAILS + queryParams, GetUserResponse.class);
        Assert.notNull(user, "获取用户详情返回结果为空");
        if (user.isSuccess()) {
            return buildUser(user.getUserid(),
                WeChatWorkConstant.USER_ACTIVE.equals(user.getStatus()), user.getName(),
                user.getDepartment(), user.getAvatar(), user.getEmail(), user.getBizMail(),
                user.getMobile());
        }
        log.error("获取用户详细信息失败: {}", user.getErrMsg());
        throw new ApiCallException("获取用户详细信息失败: " + user.getErrMsg());
    }

    @Override
    public List<User> getUserList(String deptId) {
        // 部门递归获取子部门下面的成员
        String queryParams = ACCESS_TOKEN + getAccessToken() + "&department_id=" + deptId
                             + "&fetch_child=1";
        GetUserListResponse userDTO = restOperations
            .getForObject(WeChatWorkConstant.WX_USER_URL + queryParams, GetUserListResponse.class);
        Assert.notNull(userDTO, "获取用户列表返回结果为空");
        if (userDTO.isSuccess()) {
            List<GetUserListResponse.UserList> userList = userDTO.getUserList();
            return userList.stream()
                .map(item -> buildUser(item.getUserid(),
                    WeChatWorkConstant.USER_ACTIVE.equals(item.getStatus()), item.getName(),
                    item.getDepartment(), item.getAvatar(), item.getEmail(), item.getBizMail(),
                    item.getMobile()))
                .toList();
        }
        log.error("获取部门下用户列表信息失败: {}", userDTO.getErrMsg());
        throw new ApiCallException("获取部门下用户列表信息失败: " + userDTO.getErrMsg());
    }

    @Override
    public List<User> getUserList() {
        return getUserList(getRootId());
    }

    private String getRootId() {
        return "1";
    }
}
