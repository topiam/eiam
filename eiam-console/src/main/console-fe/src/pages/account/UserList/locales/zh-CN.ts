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
  'pages.account.user_list.desc':
    '组织与用户用于解决企业组织、用户等实体的管理问题。这些实体您可以直接在TopIAM中进行维护，也可以通过配置身份源后，同步身份源的数据。',
  'pages.account.user_list.common.base': '基础字段',
  'pages.account.user_list.common.custom': '扩展字段',
  'pages.account.user_list.common.step_next': '下一步',
  'pages.account.user_list.common.step_previous': '上一步',
  'pages.account.user_list.common.save': '确认',
  'pages.account.user_list.common.cancel': '取消',
  'pages.account.user_list.user.columns.username': '用户名称',
  'pages.account.user_list.user.columns.full_name': '用户姓名',
  'pages.account.user_list.user.columns.phone': '手机号码',
  'pages.account.user_list.user.columns.email': '邮箱地址',
  'pages.account.user_list.user.columns.data_origin': '数据来源',
  'pages.account.user_list.user.columns.data_origin.value_enum.input': '自建',
  'pages.account.user_list.user.columns.data_origin.value_enum.dingtalk': '钉钉导入',
  'pages.account.user_list.user.columns.data_origin.value_enum.wechat': '企业微信导入',
  'pages.account.user_list.user.columns.data_origin.value_enum.feishu': '飞书导入',
  'pages.account.user_list.user.columns.data_origin.value_enum.ldap': 'LDAP导入',
  'pages.account.user_list.user.columns.org_display_path': '所属组织',
  'pages.account.user_list.user.columns.status': '状态',
  'pages.account.user_list.user.columns.status.value_enum.expired_locked': '过期锁定',
  'pages.account.user_list.user.columns.status.value_enum.password_expired_locked': '密码过期锁定',
  'pages.account.user_list.user.columns.last_auth_time': '最后登录时间',
  'pages.account.user_list.user.columns.option': '操作',
  'pages.account.user_list.user.columns.option.disable.popconfirm':
    '禁用后该用户无法使用，确定禁用此用户？',
  'pages.account.user_list.user.columns.option.enable.popconfirm': '确定启用该用户吗？',
  'pages.account.user_list.user.columns.option.delete': '删除用户',
  'pages.account.user_list.user.columns.option.delete.popconfirm':
    '删除后该用户无法使用，确定删除此用户？',
  'pages.account.user_list.user.columns.option.reset_password': '重置密码',
  'pages.account.user_list.user.table_alert_option_render': '您确定要批量删除选中用户吗？',
  'pages.account.user_list.user.toolbar.tooltip.title':
    '若不勾选，则只会搜索当前直属节点的账户，不包含下级节点账户。',
  'pages.account.user_list.user.toolbar.tooltip.text': '包含节点下全部账户',
  'pages.account.user_list.user.identity_import': '身份源导入',
  'pages.account.user_list.user.create': '新增用户',
  'pages.account.user_list.user.update': '修改用户',
  'pages.account.user_list.user.form.organization_name': '所属组织',
  'pages.account.user_list.user.form.username': '用户名称',
  'pages.account.user_list.user.form.username.placeholder': '请输入用户名',
  'pages.account.user_list.user.form.username.rule.0.message': '用户名为必填项',
  'pages.account.user_list.user.form.username.rule.1.message': '用户名格式不合法',
  'pages.account.user_list.user.form.username.rule.2.message': '用户名已存在',
  'pages.account.user_list.user.form.username.extra':
    '账户名称不能以特殊字符开始，可包含大写字母、小写字母、数字、中划线(-)、下划线(_)、长度至少 4 位',
  'pages.account.user_list.user.form.full_name': '姓名',
  'pages.account.user_list.user.form.full_name.placeholder': '请输入姓名',
  'pages.account.user_list.user.form.full_name.rule.0.message': '姓名为必填项',
  'pages.account.user_list.user.form.nick_name': '账户昵称',
  'pages.account.user_list.user.form.nick_name.placeholder': '请输入账户昵称',
  'pages.account.user_list.user.form.password': '登录密码',
  'pages.account.user_list.user.form.password.placeholder': '请输入登录密码',
  'pages.account.user_list.user.form.password.rule.0.message': '登录密码为必填项',
  'pages.account.user_list.user.form.password.addon_after.button': '自动生成',
  'pages.account.user_list.user.form.phone': '手机号',
  'pages.account.user_list.user.form.phone.placeholder': '请输入手机号',
  'pages.account.user_list.user.form.phone.rule.0.message': '手机号格式不正确',
  'pages.account.user_list.user.form.phone.rule.1.message': '手机号已存在',
  'pages.account.user_list.user.form.phone.extra': '手机号或邮箱至少填写一个。',
  'pages.account.user_list.user.from.phone_email.required.message': '手机号或邮箱至少填写一个',
  'pages.account.user_list.user.form.email': '邮箱',
  'pages.account.user_list.user.form.email.extra': '手机号或邮箱至少填写一个。',
  'pages.account.user_list.user.form.email.placeholder': '请输入邮箱',
  'pages.account.user_list.user.form.email.rule.0.message': '请输入邮箱',
  'pages.account.user_list.user.form.email.rule.1.message': '邮箱格式不正确',
  'pages.account.user_list.user.form.email.rule.2.message': '邮箱已存在',
  'pages.account.user_list.user.form.expire_time': '过期时间',
  'pages.account.user_list.user.form.expire_time.extra': '不填将使用系统默认过期时间 2116-12-31',
  'pages.account.user_list.user.form.remark': '备注',
  'pages.account.user_list.user.form.remark.extra': '用户备注信息',
  'pages.account.user_list.user.form.reset_password_model': '重置用户密码',
  'pages.account.user_list.user.form.reset_password_model.password': '新密码',
  'pages.account.user_list.user.form.reset_password_model.password.placeholder': '请输入新密码',
  'pages.account.user_list.user.form.reset_password_model.password.rules.0': '密码不能为空',
  'pages.account.user_list.user.form.reset_password_model.password.addon_after.button': '自动生成',
  'pages.account.user_list.user.form.reset_password_model.copy_outlined': '复制账户密码',
  'pages.account.user_list.user.common.form.enable_notice': '密码通知',
  'pages.account.user_list.user.common.form.enable_notice.extra': '勾选后，新密码将发送给用户',
  'pages.account.user_list.user.common.form.notice_channels': '密码通知方式',
  'pages.account.user_list.user.common.form.notice_channels.options.0': '邮件',
  'pages.account.user_list.user.common.form.notice_channels.options.1': '短信',

  'pages.account.user_list.organization.search_tree.search.placeholder': '输入组织名称或组织编码',
  'pages.account.user_list.organization.tree.menu_items.item.0': '新增子组织',
  'pages.account.user_list.organization.tree.menu_items.item.1': '编辑组织',
  'pages.account.user_list.organization.tree.menu_items.item.2': '移动组织',
  'pages.account.user_list.organization.tree.menu_items.item.3': '删除组织',
  'pages.account.user_list.organization.tree.menu_items.item.3.confirm.content':
    '删除操作无法恢复，请谨慎删除',
  'pages.account.user_list.organization.move_drawer': '移动组织',
  'pages.account.user_list.organization.move_drawer.alert.message':
    '请选择需要移动到的部门或组织, 并点击确认按钮。',
  'pages.account.user_list.organization.move_drawer.on_finish.message': '请选择要移动到的组织机构',

  'pages.account.user_list.organization.add_organization': '新增组织架构',
  'pages.account.user_list.organization.edit_organization': '编辑组织架构',
  'pages.account.user_list.organization.form.parent_name': '上级组织',
  'pages.account.user_list.organization.form.type': '组织类型',
  'pages.account.user_list.organization.form.type.placeholder': '请选择组织类型',
  'pages.account.user_list.organization.form.type.options.0': '集团',
  'pages.account.user_list.organization.form.type.options.1': '公司',
  'pages.account.user_list.organization.form.type.options.2': '部门',
  'pages.account.user_list.organization.form.type.options.3': '单位',
  'pages.account.user_list.organization.form.type.rule.0.message': '组织类型为必填项',
  'pages.account.user_list.organization.form.name': '组织名称',
  'pages.account.user_list.organization.form.name.rule.0.message': '组织名称为必填项',
  'pages.account.user_list.organization.form.name.placeholder': '请输入组织名称',
  'pages.account.user_list.organization.form.code': '组织代码',
  'pages.account.user_list.organization.form.code.extra': '组织代码全局唯一，创建后不可修改。',
  'pages.account.user_list.organization.form.code.rule.0.message': '组织代码为必填项',
  'pages.account.user_list.organization.form.code.placeholder': '请输入组织代码',
  'pages.account.user_list.organization.form.order': '排序',
  'pages.account.user_list.organization.form.desc': '描述',
};
