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
package cn.topiam.employee.identitysource.core.enums;

/**
 * 事件通知类型
 *
 * @author TopIAM
 */
public enum IdentitySourceEventReceiveType {
                                            /**
                                             * 用户添加
                                             */
                                            USER_ADD,
                                            /**
                                             * 用户修改
                                             */
                                            USER_MODIFY,
                                            /**
                                             * 用户离职
                                             */
                                            USER_LEAVE,

                                            /**
                                             * 用户删除
                                             */
                                            USER_REMOVE,

                                            /**
                                             * 部门创建
                                             */
                                            DEPT_CREATE,

                                            /**
                                             * 部门修改
                                             */
                                            DEPT_MODIFY,
                                            /**
                                             * 部门删除
                                             */
                                            DEPT_REMOVE
}
