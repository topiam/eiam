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
  'pages.setting.security.desc':
    '系统全局安全配置、如密码策略、会话策略。 在密码策略中可以设置相应的密码复杂度、相应的锁定解锁策略，还可以设置是否允许与历史密码重复等高级策略。同时，可以通过开启弱密码字典库来检查密码的安全强度。',
  'pages.setting.basic_setting': '基础设置',
  'pages.setting.security.basic.session_maximum': '用户并发数',
  'pages.setting.security.basic.session_maximum.extra': '同一用户同时在线数量，-1为不限制。',
  'pages.setting.security.basic.session_validtime': '会话有效时间',
  'pages.setting.security.basic.session_validtime.addon_after': '秒（暂未支持）',
  'pages.setting.security.basic.remember_me_validtime': '记住我有效时间',
  'pages.setting.security.basic.remember_me_validtime.addon_after': '秒',
  'pages.setting.security.basic.verify_code_valid_time': '验证码有效时间',
  'pages.setting.security.basic.verify_code_valid_time.extra': '验证场景下，验证码验证有效时间。',
  'pages.setting.security.basic.verify_code_valid_time.addon_after': '分',
  'pages.setting.security.basic.login_failure_duration': '连续登录失败持续时间',
  'pages.setting.security.basic.login_failure_duration.addon_after': '分',
  'pages.setting.security.basic.login_failure.count': '连续登录失败次数',
  'pages.setting.security.basic.login_failure_count.extra':
    '管理员设置用户连续登录失败次数，在连续登录失败持续时间达到设置值，当前账户被锁定。',
  'pages.setting.security.basic.login_failure_count.addon_after': '自动锁定',
  'pages.setting.security.basic.auto_unlock_time': '自动解锁时间',
  'pages.setting.security.basic.auto_unlock_time.extra':
    '管理员设置自动解锁时间，用户连续登录失败被锁定后，达到对应时间，将自动解锁。',
  'pages.setting.security.basic.auto_unlock_time.addon_after': '分钟',

  'pages.setting.security.defense_policy': '防御策略',
  'pages.setting.security.defense_policy.content_security_policy': '内容安全策略',
  'pages.setting.security.defense_policy.brute_force_detection': '暴力检测',
  'pages.setting.security.defense_policy.form.content_security_policy': '内容安全策略',
  'pages.setting.security.defense_policy.form.content_security_policy.placeholder':
    '请输入内容安全策略',
  'pages.setting.security.defense_policy.form.content_security_policy.extra':
    '内容安全策略（CSP）是一个额外的安全层，用于检测并削弱某些特定类型的攻击，包括跨站脚本（XSS）和数据注入攻击等。',
  'pages.setting.security.defense_policy.form.content_security_policy.required':
    '内容安全策略为必填项',
};
