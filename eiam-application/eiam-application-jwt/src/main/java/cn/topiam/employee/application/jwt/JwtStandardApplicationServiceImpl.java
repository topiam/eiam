/*
 * eiam-application-jwt - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.application.jwt;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.topiam.employee.common.enums.app.AppProtocol;
import cn.topiam.employee.common.enums.app.AppType;
import cn.topiam.employee.common.repository.app.AppCertRepository;
import cn.topiam.employee.common.repository.app.AppRepository;

/**
 * JWT 用户应用
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/20 23:20
 */
@Component
public class JwtStandardApplicationServiceImpl extends AbstractJwtApplicationService {

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
        return "jwt";
    }

    /**
     * 获取应用名称
     *
     * @return {@link String}
     */
    @Override
    public String getName() {
        return "JWT";
    }

    /**
     * 获取应用描述
     *
     * @return {@link String}
     */
    @Override
    public String getDescription() {
        return "JWT（JSON Web Token）是在网络应用环境声明的一种基于 JSON 的开放标准。TopIAM 使用 JWT 进行分布式站点的单点登录 （SSO）。JWT 单点登录基于非对称加密，由 TopIAM 将用户状态和信息使用私钥加密，传递给应用后，应用使用公钥解密并进行验证。使用场景非常广泛，集成简单。";
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
        return AppProtocol.JWT;
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
        return "data:image/svg+xml;base64,PHN2ZyB0PSIxNjYwOTc4MjExNDg3IiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHAtaWQ9IjM4NzEiIHdpZHRoPSIyMDAiIGhlaWdodD0iMjAwIj48cGF0aCBkPSJNNTg5LjMxMiAyNzUuNDU2TDU4OC4yODggMGgtMTUzLjZsMS4wMjQgMjc1LjQ1NiA3Ni44IDEwNS40NzJ6IG0tMTUzLjYgNDcyLjA2NHYyNzYuNDhoMTUzLjZ2LTI3Ni40OEw1MTIuNTEyIDY0Mi4wNDh6IiBmaWxsPSIjRkZGRkZGIiBwLWlkPSIzODcyIj48L3BhdGg+PHBhdGggZD0iTTU4OS4zMTIgNzQ3LjUybDE2MS43OTIgMjIzLjIzMiAxMjMuOTA0LTkwLjExMi0xNjEuNzkyLTIyMy4yMzItMTIzLjkwNC0zOS45MzZ6IG0tMTUzLjYtNDcyLjA2NEwyNzIuODk2IDUyLjIyNGwtMTIzLjkwNCA5MC4xMTJMMzEwLjc4NCAzNjUuNTY4bDEyNC45MjggMzkuOTM2eiIgZmlsbD0iIzAwRjJFNiIgcC1pZD0iMzg3MyI+PC9wYXRoPjxwYXRoIGQ9Ik0zMTAuNzg0IDM2NS41NjhMNDguNjQgMjgwLjU3NiAxLjUzNiA0MjUuOTg0IDI2My42OCA1MTJsMTIzLjkwNC00MC45NnogbTMyNS42MzIgMTg2LjM2OGw3Ni44IDEwNS40NzIgMjYyLjE0NCA4NC45OTIgNDcuMTA0LTE0NS40MDgtMjYyLjE0NC04NC45OTJ6IiBmaWxsPSIjMDBCOUYxIiBwLWlkPSIzODc0Ij48L3BhdGg+PHBhdGggZD0iTTc2MC4zMiA1MTJsMjYyLjE0NC04Ni4wMTYtNDcuMTA0LTE0NS40MDhMNzEzLjIxNiAzNjUuNTY4bC03Ni44IDEwNS40NzJ6IG0tNDk2LjY0IDBMMS41MzYgNTk2Ljk5MiA0OC42NCA3NDIuNGwyNjIuMTQ0LTg0Ljk5MiA3Ni44LTEwNS40NzJ6IiBmaWxsPSIjRDYzQUZGIiBwLWlkPSIzODc1Ij48L3BhdGg+PHBhdGggZD0iTTMxMC43ODQgNjU3LjQwOEwxNDguOTkyIDg4MC42NGwxMjMuOTA0IDkwLjExMiAxNjIuODE2LTIyMy4yMzJWNjE3LjQ3MnpNNzEzLjIxNiAzNjUuNTY4bDE2MS43OTItMjIzLjIzMi0xMjMuOTA0LTkwLjExMi0xNjEuNzkyIDIyMy4yMzJ2MTMwLjA0OHoiIGZpbGw9IiNGQjAxNUIiIHAtaWQ9IjM4NzYiPjwvcGF0aD48L3N2Zz4=";
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

    public JwtStandardApplicationServiceImpl(AppCertRepository appCertRepository,
                                             AppRepository appRepository) {
        super(appCertRepository, appRepository);
    }
}
