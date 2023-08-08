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
import component from './zh-CN/component';
import menu from './zh-CN/menu';
import pages from './zh-CN/pages';

export default {
  'nav_bar.lang': '语言',
  'app.notification.rule.0.message': '提示',
  /* TopIAM 自定义 */
  'app.option': '操作',
  'app.manage': '管理',
  'app.detail': '详情',
  'app.create': '新增',
  'app.create_child': '新增下级',
  'app.update': '修改',
  'app.delete': '删除',
  'app.batch_delete': '批量删除',
  'app.import': '导入',
  'app.export': '导出',
  'app.delete_confirm': '您确定要删除吗？',
  'app.enable_confirm': '您确定要启用吗？',
  'app.disable_confirm': '您确定要禁用吗？',
  'app.delete_success': '删除成功',
  'app.create_success': '新增成功',
  'app.save_success': '保存成功',
  'app.add': '添加',
  'app.add_success': '添加成功',
  'app.add_fail': '添加失败',
  'app.update_success': '修改成功',
  'app.edit_success': '编辑成功',
  'app.update_fail': '修改失败',
  'app.operation_success': '操作成功',
  'app.operation_fail': '操作失败',
  'app.confirm': '确认',
  'app.success': '成功',
  'app.fail': '失败',
  'app.skip': '跳过',
  'app.enable': '启用',
  'app.enabled': '已启用',
  'app.not_enabled': '未启用',
  'app.disable': '禁用',
  'app.yes': '是',
  'app.no': '否',
  'app.selected': '已选',
  'app.item': '项',
  'app.deselect': '取消选择',
  'app.please_choose': '请选择',
  'app.please_enter': '请输入',
  'app.not_yet_realized': '暂未实现',
  'app.warn': '警告',
  'app.cancel': '取消',
  'app.expand': '展开',
  'app.away': '收起',
  'app.save': '保存',
  'app.rest': '重置',
  'app.save_continue': '保存并继续',
  'app.preview': '预览',
  'app.test': '测试',
  'app.unknown': '未知',
  'app.password.not_match': '两次密码不匹配',
  'app.start_time': '开始时间',
  'app.end_time': '结束时间',
  'app.today': '今日',
  'app.week': '本周',
  'app.month': '本月',
  'app.year': '本年',
  'app.normal': '正常',
  'app.minute': '分钟',
  'app.minute.not_update': '分钟（暂不支持修改）',
  'app.displayed': '是否显示',
  'app.issue': '不知道怎么配置？请参考 TopIAM ',
  'app.disposition': '配置',
  'app.required_field': '此项为必填项',
  'app.doc': '对接文档',
  'app.or': '或',
  'app.parse': '解析',
  'app.download': '下载',
  'app.lock': '锁定',
  'custom.display': '显示',
  'custom.hidden': '隐藏',
  'custom.selected': '已选中',
  'custom.selected.item': '项',
  'custom.unchecked': '暂未选中',
  'custom.search_button': '搜索',
  ...menu,
  ...component,
  ...pages,
};
