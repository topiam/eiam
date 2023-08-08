/*
 * eiam-identity-source-feishu - Employee Identity and Access Management
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
package cn.topiam.employee.identitysource.feishu.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import cn.topiam.employee.identitysource.core.client.IdentitySourceClient;
import cn.topiam.employee.identitysource.core.domain.Dept;
import cn.topiam.employee.identitysource.core.domain.User;
import cn.topiam.employee.identitysource.core.domain.UserDetail;
import cn.topiam.employee.identitysource.core.exception.ApiCallException;
import cn.topiam.employee.identitysource.feishu.FeiShuConfig;
import cn.topiam.employee.identitysource.feishu.domain.BaseRequest;
import cn.topiam.employee.identitysource.feishu.domain.request.GetDeptListRequest;
import cn.topiam.employee.identitysource.feishu.domain.request.GetUserListRequest;
import cn.topiam.employee.identitysource.feishu.domain.response.GetDepartmentResponse;
import cn.topiam.employee.identitysource.feishu.domain.response.GetDetailsResponse;
import cn.topiam.employee.identitysource.feishu.domain.response.GetListResponse;
import cn.topiam.employee.identitysource.feishu.domain.response.GetUserResponse;

import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.identitysource.feishu.FeiShuConstant.*;

/**
 * 飞书提供商client
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/29 21:56
 */
@Slf4j(topic = LOGGER_NAME)
public class FeiShuClient extends AbstractFeiShuClient implements IdentitySourceClient {

    public FeiShuClient(FeiShuConfig config) {
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
     * @param country {@link  String}
     * @param nickName {@link  String}
     * @return {@link  User}
     */
    private User buildUser(String userid, Boolean active, String name, List<String> deptIdList,
                           String avatar, String email, String orgEmail, String mobile,
                           String country, String nickName) {
        User user = new User();
        user.setUserId(userid);
        user.setActive(active);
        user.setDeptIdList(deptIdList);
        user.setAvatar(avatar);
        user.setEmail(email);
        user.setOrgEmail(orgEmail);
        user.setPhone(mobile);
        try {
            Phonenumber.PhoneNumber cn = PhoneNumberUtil.getInstance().parse(mobile, "CN");
            user.setPhone(String.valueOf(cn.getNationalNumber()));
            user.setPhoneAreaCode(String.valueOf(cn.getCountryCode()));
        } catch (NumberParseException e) {
            throw new RuntimeException(e);
        }
        UserDetail userDetail = new UserDetail();
        userDetail.setName(name);
        userDetail.setNickName(nickName);
        user.setUserDetail(userDetail);
        return user;
    }

    private List<Dept> getDepartmentList(String deptId, List<Dept> departmentList,
                                         String pageToken) {
        GetListResponse<GetDepartmentResponse> deptResponse = request(
            String.format(DEPARTMENT_LIST, deptId),
            new GetDeptListRequest().setPageToken(pageToken), new ParameterizedTypeReference<>() {
            }, getAccessToken());
        if (deptResponse.isSuccess()) {
            GetListResponse.ListData<GetDepartmentResponse> data = deptResponse.getData();
            List<GetDepartmentResponse> items = data.getItems();
            if (!CollectionUtils.isEmpty(items)) {
                List<Dept> deptList = items.stream().map(item -> {
                    String departmentId = item.getOpenDepartmentId();
                    Dept dept = new Dept();
                    dept.setDeptId(departmentId);
                    dept.setParentId(item.getParentDepartmentId());
                    dept.setName(item.getName());
                    dept.setOrder(Long.valueOf(item.getOrder()));
                    dept.setChildren(getDepartmentList(departmentId, new ArrayList<>(), null));
                    return dept;
                }).toList();
                departmentList.addAll(deptList);
            }
            if (StringUtils.isNoneBlank(data.getPageToken())) {
                return getDepartmentList(deptId, departmentList, data.getPageToken());
            }
            return departmentList;
        }
        log.error("获取飞书子部门列表失败: {}", deptResponse.getMsg());
        throw new ApiCallException("获取飞书子部门列表失败: " + deptResponse.getMsg());
    }

    @Override
    public Dept getDept(String id) {
        GetDetailsResponse<GetDepartmentResponse> deptResponse = request(DEPARTMENT_GET + id,
            new BaseRequest(), new ParameterizedTypeReference<>() {
            }, getAccessToken());
        if (deptResponse.isSuccess()) {
            GetDepartmentResponse department = deptResponse.getData().getDepartment();
            Dept dept = new Dept();
            dept.setDeptId(department.getOpenDepartmentId());
            dept.setParentId(department.getParentDepartmentId());
            dept.setName(department.getName());
            return dept;
        }
        log.error("飞书获取部门详情信息失败: {}", deptResponse.getMsg());
        throw new ApiCallException("飞书获取部门详情信息失败: " + deptResponse.getMsg());
    }

    @Override
    public List<Dept> getDeptList(String id) {
        return getDepartmentList(id, new ArrayList<>(), null);

    }

    @Override
    public List<Dept> getDeptList() {
        Dept dept = getDept(getRootId());
        List<Dept> deptList = getDeptList(getRootId());
        deptList.add(dept);
        return deptList;
    }

    /**
     * 调用获取用户列表
     */
    private List<GetUserResponse> getUserList(String deptId, String pageToken,
                                              List<GetUserResponse> userList) {
        GetListResponse<GetUserResponse> userDTO = request(USER_LIST,
            new GetUserListRequest().setPageToken(pageToken).setDepartmentId(deptId),
            new ParameterizedTypeReference<>() {
            }, getAccessToken());
        if (userDTO.isSuccess()) {
            GetListResponse.ListData<GetUserResponse> data = userDTO.getData();
            List<GetUserResponse> items = data.getItems();
            if (!CollectionUtils.isEmpty(items)) {
                userList.addAll(items);
            }
            pageToken = data.getPageToken();
            if (StringUtils.isNoneBlank(pageToken)) {
                return getUserList(deptId, pageToken, userList);
            }
            return userList;
        }
        log.error("获取用户列表失败: {}", userDTO.getMsg());
        throw new ApiCallException("获取部门用户列表失败: " + userDTO.getMsg());
    }

    @Override
    public User getUser(String userId) {
        GetDetailsResponse<GetUserResponse> userResponse = request(GET_USER + userId,
            new BaseRequest(), new ParameterizedTypeReference<>() {
            }, getAccessToken());
        if (userResponse.isSuccess()) {
            GetUserResponse user = userResponse.getData().getUser();
            return buildUser(user.getUserId(), user.getStatus().isActive(), user.getName(),
                user.getDepartmentIds(), user.getAvatar().getAvatar(), user.getEmail(),
                user.getEnterpriseEmail(), user.getMobile(), user.getCountry(), user.getNickname());
        }
        log.error("获取用户详细信息失败: {}", userResponse.getMsg());
        throw new ApiCallException("获取用户详细信息失败: " + userResponse.getMsg());
    }

    @Override
    public List<User> getUserList(String deptId) {
        List<GetUserResponse> userList = getUserList(deptId, null, new ArrayList<>());
        return userList.stream()
            .map(user -> buildUser(user.getUserId(), user.getStatus().isActive(), user.getName(),
                user.getDepartmentIds(), user.getAvatar().getAvatar(), user.getEmail(),
                user.getEnterpriseEmail(), user.getMobile(), user.getCountry(), user.getNickname()))
            .toList();
    }

    @Override
    public List<User> getUserList() {
        List<Dept> deptList = getDeptList();
        List<User> users = new ArrayList<>();
        deptList.forEach(deptId -> users.addAll(getUserList(deptId.getDeptId())));
        return users;
    }

    /**
     * 请求接口
     *
     * @param url {@link String}
     * @param json {@link Object}
     * @param reference {@link ParameterizedTypeReference<T>}
     * @param token {@link String }
     * @return {@link T}
     */
    private <T> T request(String url, Object json, ParameterizedTypeReference<T> reference,
                          String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        //  构建请求地址
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if (Objects.nonNull(json)) {
            JSONObject params = JSON.parseObject(JSON.toJSONString(json), JSONObject.class);
            params.forEach(builder::queryParam);
        }
        ResponseEntity<T> response = restOperations.exchange(builder.build().encode().toString(),
            HttpMethod.GET, requestEntity, reference);
        T body = response.getBody();
        Assert.notNull(body, "请求[{" + url + "}], 返回结果为空");
        return body;
    }

    private String getRootId() {
        return "0";
    }
}
