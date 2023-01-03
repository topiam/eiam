/*
 * eiam-application-form - Employee Identity and Access Management Program
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
package cn.topiam.employee.application.form;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.topiam.employee.common.enums.app.AppProtocol;
import cn.topiam.employee.common.enums.app.AppType;
import cn.topiam.employee.common.repository.app.AppCertRepository;
import cn.topiam.employee.common.repository.app.AppRepository;

/**
 * Form 用户应用
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/20 23:20
 */
@Component
public class FormStandardApplicationServiceImpl extends AbstractFormApplicationService {

    /**
     * 更新应用配置
     *
     * @param appId {@link String}
     * @param config {@link Map}
     */
    @Override
    public void saveConfig(String appId, Map<String, Object> config) {
    }

    /**
     * 获取配置
     *
     * @param appId {@link String}
     * @return {@link Map}
     */
    @Override
    public Object getConfig(String appId) {
        return null;
    }

    /**
     * 获取应用标志
     *
     * @return {@link String}
     */
    @Override
    public String getCode() {
        return "form";
    }

    /**
     * 获取应用名称
     *
     * @return {@link String}
     */
    @Override
    public String getName() {
        return "表单代填";
    }

    /**
     * 获取应用描述
     *
     * @return {@link String}
     */
    @Override
    public String getDescription() {
        return "表单代填可以模拟用户在登录页输入用户名和密码，再通过表单提交的一种登录方式。应用的账号密码在 TopIAM 中使用 AES256 加密算法本地加密存储。很多旧系统、不支持标准认证协议的系统或不支持改造的系统可以使用表单代填实现统一身份管理。表单中有图片验证码、CSRF token、动态参数的场景不适用。";
    }

    /**
     * 获取应用类型
     *
     * @return {@link AppType}
     */
    @Override
    public AppType getType() {
        return AppType.STANDARD;
    }

    /**
     * 获取应用协议
     *
     * @return {@link AppProtocol}
     */
    @Override
    public AppProtocol getProtocol() {
        return AppProtocol.FORM;
    }

    /**
     * 获取表单Schema
     *
     * @return {@link Map}
     */
    @Override
    public List<Map> getFormSchema() {
        return null;
    }

    /**
     * 获取base64图标
     *
     * @return {@link String}
     */
    @Override
    public String getBase64Icon() {
        return "data:image/svg+xml;base64,PHN2ZyB0PSIxNjYwOTc4MDk3OTkyIiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHAtaWQ9IjMxNDMiIHdpZHRoPSIyMDAiIGhlaWdodD0iMjAwIj48cGF0aCBkPSJNMjk0LjkxMiA0MTQuMjA4aDg2LjUyOHYzNDIuMDE2SDI5NC45MTJ6TTQyNC45NiA0MTQuMjA4aDMwMy4xMDR2MzQyLjAxNkg0MjQuOTZ6TTI5NC45MTIgMjY3LjI2NGg0MzMuMTUydjk3Ljc5MkgyOTQuOTEyeiIgZmlsbD0iIzI3QTNGRiIgcC1pZD0iMzE0NCI+PC9wYXRoPjxwYXRoIGQ9Ik05MjEuNiAwSDEwMi40YTEwMi40IDEwMi40IDAgMCAwLTEwMi40IDEwMi40djgxOS4yYTEwMi40IDEwMi40IDAgMCAwIDEwMi40IDEwMi40aDgxOS4yYTEwMi40IDEwMi40IDAgMCAwIDEwMi40LTEwMi40VjEwMi40YTEwMi40IDEwMi40IDAgMCAwLTEwMi40LTEwMi40eiBtLTE1MC4wMTYgNzgwLjhhMjMuMDQgMjMuMDQgMCAwIDEtMjEuNTA0IDI0LjA2NEgyNzMuNDA4YTIzLjA0IDIzLjA0IDAgMCAxLTIxLjUwNC0yNC4wNjRWMjQyLjY4OGEyMy4wNCAyMy4wNCAwIDAgMSAyMS41MDQtMjQuMDY0aDQ3Ni42NzJhMjMuMDQgMjMuMDQgMCAwIDEgMjEuNTA0IDI0LjA2NHoiIGZpbGw9IiMyN0EzRkYiIHAtaWQ9IjMxNDUiPjwvcGF0aD48L3N2Zz4=";
    }

    /**
     * 创建应用
     *
     * @param name   {@link String} 名称
     * @param remark {@link String} 备注
     */
    @Override
    public String create(String name, String remark) {
        return "";
    }

    /**
     * 删除应用
     *
     * @param appId {@link String} 应用ID
     */
    @Override
    public void delete(String appId) {

    }

    protected FormStandardApplicationServiceImpl(AppCertRepository appCertRepository,
                                                 AppRepository appRepository) {
        super(appCertRepository, appRepository);
    }

}
