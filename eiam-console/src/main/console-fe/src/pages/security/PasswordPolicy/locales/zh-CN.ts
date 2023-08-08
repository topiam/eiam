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
  'pages.setting.basic_setting.session_maximum': '用户并发数',
  'pages.setting.basic_setting.session_maximum.extra': '同一用户同时在线数量，-1为不限制。',
  'pages.setting.basic_setting.session_validtime': '会话有效时间',
  'pages.setting.basic_setting.session_validtime.addon_after': '秒（暂未支持）',
  'pages.setting.basic_setting.remember_me_validtime': '记住我有效时间',
  'pages.setting.basic_setting.remember_me_validtime.addon_after': '秒',
  'pages.setting.basic_setting.verify_code_valid_time': '验证码有效时间',
  'pages.setting.basic_setting.verify_code_valid_time.extra': '验证场景下，验证码验证有效时间。',
  'pages.setting.basic_setting.verify_code_valid_time.addon_after': '分',
  'pages.setting.basic_setting.login_failure_duration': '连续登录失败持续时间',
  'pages.setting.basic_setting.login_failure_duration.addon_after': '分',
  'pages.setting.basic_setting.login_failure.count': '连续登录失败次数',
  'pages.setting.basic_setting.login_failure_count.extra':
    '管理员设置用户连续登录失败次数，在"允许持续时间" 达到设置值，当前账户被锁定。',
  'pages.setting.basic_setting.login_failure_count.addon_after': '自动锁定',
  'pages.setting.basic_setting.auto_unlock_time': '自动解锁时间',
  'pages.setting.basic_setting.auto_unlock_time.extra':
    '管理员设置自动解锁时间，用户连续登录失败被锁定后，达到对应时间，将自动解锁。',
  'pages.setting.basic_setting.auto_unlock_time.addon_after': '分钟',
  'pages.setting.security.mfa': '多因素认证',
  'pages.setting.security.captcha': '行为验证码',
  'pages.setting.security.password_policy': '密码策略',
  'pages.setting.security.password_policy.rule.0.message':
    '配置密码相关策略。保存后，所有密码新增或变更均会进行校验。',
  'pages.setting.security.password_policy.password_length_limit': '密码长度限制',
  'pages.setting.security.password_policy.password_least_length': '最小长度',
  'pages.setting.security.password_policy.password_biggest_length': '最大长度',
  'pages.setting.security.password_policy.password_complexity': '密码复杂度',
  'pages.setting.security.password_policy.0': '任意密码',
  'pages.setting.security.password_policy.1': '必须包含数字和字母',
  'pages.setting.security.password_policy.2': '必须包含数字和大写字母',
  'pages.setting.security.password_policy.3': '必须包含数字、大写字母、小写字母、和特殊字符',
  'pages.setting.security.password_policy.4': '至少包含数字、字母、和特殊字符中的两种',
  'pages.setting.security.password_policy.5':
    '至少包含数字、大写字母、小写字母、和特殊字符中的三种',
  'pages.setting.security.password_policy.password_expiration_check': '密码过期检查',
  'pages.setting.security.password_policy.password_expiration_check.tooltip':
    '开启后，系统会计算密码设置时间',
  'pages.setting.security.password_policy.password_expiration_check.expiration-date': '有效期',
  'pages.setting.security.password_policy.password_expiration_check.day': '天',
  'pages.setting.security.password_policy.before_expiration': '过期前',
  'pages.setting.security.password_policy.before_expiration.day': '天提醒',
  'pages.setting.security.password_policy.password_same_characters': '密码相同字符',
  'pages.setting.security.password_policy.password_same_characters.tooltip':
    '密码不能连续存在相同字符个数',
  'pages.setting.security.password_policy.user_info_check': '用户信息检查',
  'pages.setting.security.password_policy.user_info_check.tooltip':
    '开启后，密码中将不能包含用户名、手机号、邮箱前缀和姓名拼音',
  'pages.setting.security.password_policy.hist_password_check': '历史密码检查',
  'pages.setting.security.password_policy.hist_password_check.tooltip':
    '检查设置的密码是否为历史密码。',
  'pages.setting.security.password_policy.range_password_history': '历史密码次数范围',
  'pages.setting.security.password_policy.range_password_history.tooltip':
    '检查历史密码次数范围，该值必须介于1到10次密码之间。',
  'pages.setting.security.password_policy.illegal-character-sequences': '非法字符序列',
  'pages.setting.security.password_policy.illegal_character_sequences.tooltip':
    '防止非法字符序列，例如键盘、字母、数字。',
  'pages.setting.security.password_policy.weak_password_checking': '弱密码检查',
  'pages.setting.security.password_policy.weak_password_checking.tooltip':
    '弱密码通常是指容易被别人猜测到或被破解工具破解的密码。在设置或修改密码时，如果密码是弱密码字典中的密码，则系统会强制用户重新设置，以提升用户密码的安全性。',
  'pages.setting.security.password_policy.weak_password_checking.password_library': '内置弱密码库',
  'pages.setting.security.password_policy.other_weak_passwords': '其他弱密码',
  'pages.setting.security.password_policy.other_weak_passwords.extra': '每行代表一条弱密码',
  'pages.setting.security.mfa.massage': '支持在账密登录后，开启二次认证。',
  'pages.setting.security.mfa.mfa-mode': 'MFA模式',
  'pages.setting.security.mfa.mfa-mode.none': '关闭',
  'pages.setting.security.mfa.mfa-mode.always': '常开模式',
  'pages.setting.security.mfa.mfa-mode.smart': '智能模式（暂不支持）',
  'pages.setting.security.mfa.mfa-mode.span.0': '常开模式下每次登录都需要二次认证。',
  'pages.setting.security.mfa.mfa-mode.span.1':
    '智能模式下不必每次登录都二次认证，系统会灵活进行判断。',
  'pages.setting.security.mfa.secondary_auth': '二次认证方式',
  'pages.setting.security.mfa.secondary_auth.rule.0.message': '请选择二次认证方式',
  'pages.setting.security.mfa.secondary_auth.sms': '短信验证码',
  'pages.setting.security.mfa.secondary_auth.email': '邮件验证码',
  'pages.setting.security.mfa.secondary_auth.app': '软件动态码',
  'pages.setting.security.captcha.form.switch.content':
    '关闭此功能将无法使用行为验证码验证，请确认是否关闭。',
  'pages.setting.security.captcha.provider': '服务提供商',
  'pages.setting.security.captcha.provider.gee_test': '极验（暂未实现，欢迎PR）',
  'pages.setting.security.captcha.provider.aliyun': '阿里云（暂未实现，欢迎PR）',
  'pages.setting.security.captcha.provider.aliyun.region_id': '阿里云（暂未实现，欢迎PR）',
  'pages.setting.security.captcha.provider.aliyun.region_id.rule.0.message': ' RegionId 为必填项',
  'pages.setting.security.captcha.provider.aliyun.access_key_id.rule.0.message':
    'AccessKeyId 为必填项',
  'pages.setting.security.captcha.provider.aliyun.access_key_secret.rule.0.message':
    'AccessKeySecret 为必填项',
  'pages.setting.security.captcha.provider.aliyun.auth_method': '验证方式',
  'pages.setting.security.captcha.provider.aliyun.auth_method.rule.0.message': '验证方式',
  'pages.setting.security.captcha.provider.aliyun.auth_method.slide': '滑动验证',
  'pages.setting.security.captcha.provider.aliyun.auth_method.traceless': '无痕验证',
  'pages.setting.security.captcha.provider.aliyun.auth_method.smart': '智能验证',
  'pages.setting.security.captcha.provider.tencent': '腾讯云（暂未实现，欢迎PR）',
  'pages.setting.security.captcha.provider.recaptcha': 'reCaptcha（暂未实现，欢迎PR）',
  'pages.setting.security.captcha.provider.hcaptcha': 'hCaptcha（暂未实现，欢迎PR）',
  'pages.setting.security.captcha.captcha_id': '验证 ID',
  'pages.setting.security.captcha.captcha_id.rule.0.message': '验证 ID 为必填项',
  'pages.setting.security.captcha.captcha_key': '验证 KEY',
  'pages.setting.security.captcha.captcha_key.rule.0.message': '验证 KEY 为必填项',

  'pages.setting.security.security_defense': '防御策略',
  'pages.setting.security.security_defense.form.content_security_policy': '内容安全策略',
  'pages.setting.security.security_defense.form.content_security_policy.placeholder':
    '请输入内容安全策略',
  'pages.setting.security.security_defense.form.content_security_policy.extra':
    '容安全策略（CSP）是一个额外的安全层，用于检测并削弱某些特定类型的攻击，包括跨站脚本（XSS）和数据注入攻击等。',
  'pages.setting.security.security_defense.form.content_security_policy.required':
    '内容安全策略为必填项',
};
