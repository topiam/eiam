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
export default {
  'pages.account.user_detail.user_info': '账户信息',
  'pages.account.user_detail.user_info.not_selected': '未选择用户',
  'pages.account.user_detail.user_info.columns.open_id': '显示名',
  'pages.account.user_detail.user_info.columns.idp_name': '身份提供方',
  'pages.account.user_detail.user_info.columns.date_time': '绑定时间',
  'pages.account.user_detail.user_info.columns.option': '操作',
  'pages.account.user_detail.user_info.columns.option.popconfirm.title': '您确定要解绑该账户吗',
  'pages.account.user_detail.user_info.columns.option.popconfirm.remove': '解绑',
  'pages.account.user_detail.user_info.avatar': '头像',
  'pages.account.user_detail.user_info.username': '用户名称',
  'pages.account.user_detail.user_info.nick_name': '昵称',
  'pages.account.user_detail.user_info.nick_name.rule.0.message': '用户昵称为必填项',
  'pages.account.user_detail.user_info.full_name': '姓名',
  'pages.account.user_detail.user_info.full_name.rule.0.message': '用户姓名为必填项',
  'pages.account.user_detail.user_info.status': '用户状态',
  'pages.account.user_detail.user_info.status.value_enum.expired_locked': '过期锁定',
  'pages.account.user_detail.user_info.status.value_enum.password_expired_locked': '密码过期锁定',
  'pages.account.user_detail.user_info.status.rule.0.message': '用户状态为必选项',
  'pages.account.user_detail.user_info.data_origin': '数据来源',
  'pages.account.user_detail.user_info.data_origin.value_enum.input': '自建',
  'pages.account.user_detail.user_info.data_origin.value_enum.dingtalk': '钉钉导入',
  'pages.account.user_detail.user_info.data_origin.value_enum.wechat': '企业微信导入',
  'pages.account.user_detail.user_info.data_origin.value_enum.feishu': '飞书导入',
  'pages.account.user_detail.user_info.data_origin.value_enum.ldap': 'LDAP导入',
  'pages.account.user_detail.user_info.id': '账户 ID',
  'pages.account.user_detail.user_info.external_id': '外部 ID',
  'pages.account.user_detail.user_info.phone': '手机号',
  'pages.account.user_detail.user_info.phone.rule.0.message': '手机号格式不正确',
  'pages.account.user_detail.user_info.phone.rule.1.message': '手机号已存在',
  'pages.account.user_detail.user_info.email': '邮箱',
  'pages.account.user_detail.user_info.email.rule.0.message': '邮箱格式不正确',
  'pages.account.user_detail.user_info.email.rule.1.message': '邮箱已存在',
  'pages.account.user_detail.user_info.auth_total': '认证次数',
  'pages.account.user_detail.user_info.last_auth_ip': '最后登录 IP',
  'pages.account.user_detail.user_info.last_auth_time': '最后登录时间',
  'pages.account.user_detail.user_info.expire_date': '过期时间',
  'pages.account.user_detail.user_info.expire_date.rule.0.message': '过期时间为必填项',
  'pages.account.user_detail.user_info.create_time': '创建时间',
  'pages.account.user_detail.user_info.update_time': '修改时间',
  'pages.account.user_detail.user_info.remark': '备注',
  'pages.account.user_detail.user_info.account_bound': '已绑定账户',

  'pages.account.user_detail.login_audit.columns.app_name': '应用名称',
  'pages.account.user_detail.login_audit.columns.client_ip': '客户端IP',
  'pages.account.user_detail.login_audit.columns.browser': '浏览器',
  'pages.account.user_detail.login_audit.columns.location': '地理位置',
  'pages.account.user_detail.login_audit.columns.event_time': '登录时间',
  'pages.account.user_detail.login_audit.columns.event_status': '登录结果',

  'pages.account.user_detail.app_account.columns.app_name': '应用',
  'pages.account.user_detail.app_account.columns.app_protocol': '应用协议',
  'pages.account.user_detail.app_account.columns.account': '应用账户',
  'pages.account.user_detail.app_account.columns.create_time': '添加时间',
  'pages.account.user_detail.app_account.columns.option': '操作',
  'pages.account.user_detail.app_account.columns.option.popconfirm.title':
    '您确定要删除此应用账户？',

  'pages.account.user_detail.access_strategy': '已授权应用',
  'pages.account.user_detail.access_strategy.popconfirm.title': '您确定要取消应用授权？',
  'pages.account.user_detail.access_strategy.popconfirm.remove': '取消授权',
};
