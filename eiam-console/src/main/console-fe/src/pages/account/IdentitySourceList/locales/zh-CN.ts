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
  'pages.account.identity_source_list.desc':
    '支持企业以多种身份源途径同步用户和组织信息到系统，在高级配置中可以对导入的处理逻辑进行灵活配置\n' +
    '    ，实现从多个数据源的汇聚为一个完整的用户目录，部分身份源还可以通过回调的方式支持实时同步。',
  'pages.account.identity_source_list': '身份源列表',
  'pages.account.identity_source_list.tool_bar_render.button.add': '添加身份源',
  'pages.account.identity_source_list.metas.title': '身份源名称',
  'pages.account.identity_source_list.metas.actions.disable_title': '确定要禁用该身份源吗？',
  'pages.account.identity_source_list.metas.actions.disable_content': '禁用后该身份源将无法使用。',
  'pages.account.identity_source_list.metas.actions.enable_title': '确定要启用该身份源吗？',
  'pages.account.identity_source_list.metas.actions.enable_content': '启用后该身份源将恢复正常。',
  'pages.account.identity_source_list.metas.actions.delete_title': '确定要删除该身份源吗？',
  'pages.account.identity_source_list.metas.actions.delete_content':
    '删除操作无法恢复，请谨慎操作！',
  'pages.account.identity_source_list.create_modal': '新增身份源',
  'pages.account.identity_source_list.create_modal.success.title': '添加成功',
  'pages.account.identity_source_list.create_modal.success.content':
    '请进入身份源详情页面完善身份源配置信息',
  'pages.account.identity_source_list.create_modal.success.ok_text': '完善配置',
  'pages.account.identity_source_list.create_modal.provider': '提供商',
  'pages.account.identity_source_list.create_modal.provider.placeholder': '请选择身份源提供商',
  'pages.account.identity_source_list.create_modal.provider.placeholder.options.dingtalk': '钉钉',
  'pages.account.identity_source_list.create_modal.provider.placeholder.options.feishu': '飞书',
  'pages.account.identity_source_list.create_modal.provider.placeholder.rule.0.message':
    '请选择身份源提供商',
  'pages.account.identity_source_list.create_modal.name': '名称',
  'pages.account.identity_source_list.create_modal.name.placeholder': '请输入身份源名称',
  'pages.account.identity_source_list.create_modal.name.rule.0.message': '身份源名称为必填项',
  'pages.account.identity_source_list.create_modal.remark': '备注',
  'pages.account.identity_source_list.create_modal.remark.placeholder': '请输入备注',
};
