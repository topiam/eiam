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
  'pages.account.user_group_list.desc':
    '用户组是用户的集合，将具有相同职能的用户添加到一起，即形成用户组。将权限授权到用户组后，用户组内的用户都将继承该权限，因此，用户组十分适合用于批量授权。',
  'pages.account.user_group_list': '用户组列表',
  'pages.account.user_group_list.type.static': '静态用户组',
  'pages.account.user_group_list.tool_bar_render.button': '新增用户组',
  'pages.account.user_group_list.metas.title': '用户组名称',
  'pages.account.user_group_list.actions.popconfirm.title': '您确定要删除该用户组吗？',

  'pages.account.user_group_list.form.title': '新建用户组',
  'pages.account.user_group_list.form.name': '名称',
  'pages.account.user_group_list.form.name.placeholder': '请输入用户组名称',
  'pages.account.user_group_list.form.name.rule.0.message': '用户组名称为必填项',
  'pages.account.user_group_list.form.code': '编码',
  'pages.account.user_group_list.form.code.placeholder': '请输入用户组编码',
  'pages.account.user_group_list.form.code.rule.0.message': '用户组编码为必填项',
  'pages.account.user_group_list.form.code.rule.1.message':
    '只允许包含英文字母、数字、下划线 _、横线 -',
  'pages.account.user_group_list.form.remark': '备注',
  'pages.account.user_group_list.form.remark.placeholder': '请输入备注信息',
};
