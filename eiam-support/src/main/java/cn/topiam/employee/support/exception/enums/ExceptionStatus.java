/*
 * eiam-support - Employee Identity and Access Management Program
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
package cn.topiam.employee.support.exception.enums;

/**
 * 异常状态码
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2019/12/9 20:44
 */
public enum ExceptionStatus {
                             /**
                              * 参数校验失败(为空、错值提示)
                              */
                             EX900000("EX900000", "未知错误"),
                             /**
                              * 系统异常
                              */
                             EX900001("EX900001", "系统异常，请稍后重试"),
                             /**
                              * 获取配置信息错误
                              */
                             EX900002("EX900002", "获取配置信息错误"),
                             /**
                              * 参数校验错误
                              */
                             EX900003("EX900003", "参数校验错误"),
                             /**
                              * 未定义错误消息
                              */
                             EX900004("EX900004", "未定义错误消息"),
                             /**
                              * 数字签名校验错误
                              */
                             EX900005("EX900005", "数字签名校验错误"),
                             /**
                              * 参数类型不对
                              */
                             EX900006("EX900006", "参数类型不对"),
                             /**
                              * 演示模式，不允许操作
                              */
                             EX900007("EX900007", "演示模式，不允许操作"),
                             /**
                              * 授权失败
                              */
                             EX900008("EX900008", "授权失败"),
                             /**
                              * 数据库异常
                              */
                             EX900009("EX900009", "数据库异常"),
                             /**
                              * 文件上传失败
                              */
                             EX000100("EX000100", "文件上传失败"),
                             /**
                              * 用户名或密码错误
                              */
                             EX000101("EX000101", "用户名或密码错误"),
                             /**
                              * 验证码错误
                              */
                             EX000102("EX000102", "验证码错误"),
                             /**
                              * 用户被锁定
                              */
                             EX000103("EX000103", "用户被锁定，请联系管理员"),
                             /**
                              * 用户被禁用
                              */
                             EX000104("EX000104", "用户被禁用，请联系管理员"),
                             /**
                              * 没有用户权限
                              */
                             EX000105("EX000105", "没有可用权限，请联系管理员"),
                             /**
                              * 内部身份验证服务异常
                              */
                             EX000106("EX000106", "内部身份验证服务异常，请联系管理员"),
                             /**
                              * 用户不存在
                              */
                             EX000107("EX000107", "用户不存在"),
                             /**
                              * 用户已绑定
                              */
                             EX000108("EX000108", "用户已绑定"),
                             /**
                              * 用户未绑定
                              */
                             EX000109("EX000109", "用户未绑定"),
                             /**
                              * 参数不合法
                              */
                             EX000201("EX000201", "参数不合法"),
                             /**
                              * 系统暂未初始化
                              */
                             EX000202("EX000202", "系统暂未初始化"),
                             /**
                              * 会话已过期
                              */
                             EX000203("EX000203", "此会话已过期"),
                             /**
                              * 系统繁忙，请稍后重试
                              */
                             EX000205("EX000204", "系统繁忙，请稍后重试");

    private String code;
    private String message;

    ExceptionStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
