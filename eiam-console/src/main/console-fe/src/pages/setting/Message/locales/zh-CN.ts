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
  'pages.setting.message.desc':
    '消息服务设置包括邮件服务配置和短信服务配置。邮件服务配置是指将消息发送到指定的电子邮件地址，需要配置SMTP服务器地址、端口号、用户名、密码等信息。短信服务配置是指将消息以短信的形式发送到指定的手机号码，需要配置短信服务提供商的API接口地址、账号、密码等信息。',
  'pages.setting.message.mail': '邮件服务',
  'pages.setting.message.sms': '短信服务',
  'pages.setting.message.sms_template': '邮件模版',
  'pages.setting.message.mail_provider': '邮件提供商',
  'pages.setting.message.mail_provider.form.content':
    '关闭此功能将无法使用邮件服务，请确认是否关闭。',
  'pages.setting.message.mail_provider.form.label': '开启邮件服务',
  'pages.setting.message.mail_provider.provider': '服务提供商',
  'pages.setting.message.mail_provider.provider.username': '用户名称',
  'pages.setting.message.mail_provider.provider.username.placeholder': '请输入用户名',
  'pages.setting.message.mail_provider.provider.sender_mailbox': '发件人邮箱',
  'pages.setting.message.mail_provider.provider.sender_mailbox.placeholder': '请输入发件人邮箱',
  'pages.setting.message.mail_provider.provider.sender_mailbox.rule.rule.0.message':
    '请输入正确的发件人邮箱',
  'pages.setting.message.mail_provider.provider.secret': '密码',
  'pages.setting.message.mail_provider.provider.secret.placeholder': '请输入密码',
  'pages.setting.message.mail_provider.provider.customize': '自定义',
  'pages.setting.message.mail_provider.provider.customize.smtp_url': 'SMTP地址',
  'pages.setting.message.mail_provider.provider.customize.smtp_url.placeholder': '请输入SMTP地址',
  'pages.setting.message.mail_provider.provider.customize.port': '端口号',
  'pages.setting.message.mail_provider.provider.customize.port_rule_0.placeholder': '请输入端口号',
  'pages.setting.message.mail_provider.provider.customize.safety_type': '安全验证',
  'pages.setting.message.mail_provider.provider.aliyun': '阿里云企业邮',
  'pages.setting.message.mail_provider.provider.tencent': '腾讯云企业邮',
  'pages.setting.message.mail_provider.provider.net_ease': '网易企业邮',
  'pages.setting.message.mail_template': '邮件模板',
  'pages.setting.message.mail_template.sub_title': '自定义',
  'pages.setting.message.mail_template_config': '配置',
  'pages.setting.message.mail_template_config.from.custom': '自定义邮件',
  'pages.setting.message.mail_template_config.from.custom.extra':
    '开关开启时，你可以自定义欢迎邮件，密码验证和账户注册验证的具体内容，保存后生效，开关关闭时，系统将启用默认模版。',
  'pages.setting.message.mail_template_config.from.sender': '发送人',
  'pages.setting.message.mail_template_config.from.sender.placeholder': '请输入邮件发送人',
  'pages.setting.message.mail_template_config.from.sender.extra':
    '你可以包括以下变量：${client_name}，${time}，${user_email}，${client_description}，${password}。 例如：${client_name} <support@yourcompany.com>。${password} 是用户注册成功后的密码，由于安全原因，此密码不会明文数据库中留存，仅仅会在初次注册时通过邮件传输一次。',
  'pages.setting.message.mail_template_config.from.subject': '主题',
  'pages.setting.message.mail_template_config.from.subject.placeholder': '请输入邮件主题',
  'pages.setting.message.mail_template_config.from.subject.extra':
    '你可以包括以下变量：${client_name}，${time}，${client_description}，${user_email}。 例如：欢迎来到 ${client_name}！',
  'pages.setting.message.mail_template_config.from.content': '内容',
  'pages.setting.message.mail_template_config.from.content.placeholder': '请输入邮件内容',
  'pages.setting.message.mail_template.actions.browse': '浏览',
  'pages.setting.message.sms_provider': '短信服务',
  'pages.setting.message.sms_provider.form.switch.content':
    '关闭此功能将无法使用短信服务，请确认是否关闭。',
  'pages.setting.message.sms_provider.provider': '服务提供商',
  'pages.setting.message.sms_provider.provider.aliyun': '阿里云',
  'pages.setting.message.sms_provider.provider.aliyun.access_key_id.placeholder':
    '请输入阿里云AccessKey ID',
  'pages.setting.message.sms_provider.provider.aliyun.access_key_id.rule.0.message':
    '阿里云AccessKey ID 为必填项',
  'pages.setting.message.sms_provider.provider.aliyun.access_key_secret.placeholder':
    '请输入阿里云AccessKey Secret',
  'pages.setting.message.sms_provider.provider.aliyun.access_key_secret.rule.0.message':
    '阿里云AccessKey Secret为必填项',
  'pages.setting.message.sms_provider.provider.aliyun.sign_name': '短信签名',
  'pages.setting.message.sms_provider.provider.aliyun.sign_name.placeholder':
    '请输入阿里云短信签名',
  'pages.setting.message.sms_provider.provider.aliyun.sign_name.rule.0.message':
    '阿里云短信签名为必填项',
  'pages.setting.message.sms_provider.provider.tencent': '腾讯云',
  'pages.setting.message.sms_provider.provider.tencent.region': '地域',
  'pages.setting.message.sms_provider.provider.tencent.region.placeholder': '请选择地域配置',
  'pages.setting.message.sms_provider.provider.tencent.region.rule.0.message': '腾讯云地域为必填项',
  'pages.setting.message.sms_provider.provider.tencent.region.beijing': '华北地区（北京）',
  'pages.setting.message.sms_provider.provider.tencent.region.guangzhou': '华南地区（广州） ',
  'pages.setting.message.sms_provider.provider.tencent.region.nanjing': '华东地区（南京）',
  'pages.setting.message.sms_provider.provider.tencent.secret_id.placeholder':
    '请输入腾讯云SecretId',
  'pages.setting.message.sms_provider.provider.tencent.secret_id.rule.0.message':
    '腾讯云SecretId为必填项',
  'pages.setting.message.sms_provider.provider.tencent.secret_key.placeholder':
    '请输入腾讯云SecretKey',
  'pages.setting.message.sms_provider.provider.tencent.secret_key.rule.0.message':
    '腾讯云SecretKey为必填项',
  'pages.setting.message.sms_provider.provider.tencent.sdk_app_id': '短信应用ID',
  'pages.setting.message.sms_provider.provider.tencent.sdk_app_id.placeholder': '请输入短信应用ID',
  'pages.setting.message.sms_provider.provider.tencent.sdk_app_id.rule.0.message':
    '腾讯云短信应用ID为必填项',
  'pages.setting.message.sms_provider.provider.tencent.sign_name': '短信签名',
  'pages.setting.message.sms_provider.provider.tencent.sign_name.placeholder':
    '请输入腾讯云短信签名',
  'pages.setting.message.sms_provider.provider.tencent.sign_name.rule.0.message':
    '腾讯云短信签名为必填项',
  'pages.setting.message.sms_provider.provider.qi_niu': '七牛云',
  'pages.setting.message.sms_provider.provider.qi_niu.access_key.placeholder':
    '请输入七牛云 AccessKey',
  'pages.setting.message.sms_provider.provider.qi_niu.access_key.rule.0.message':
    '七牛云AccessKey为必填项',
  'pages.setting.message.sms_provider.provider.qi_niu.secret_key.placeholder':
    '请输入七牛云SecretKey',
  'pages.setting.message.sms_provider.provider.qi_niu.secret_key.rule.0.message':
    '七牛云SecretKey为必填项',
  'pages.setting.message.sms_provider.send_scene': '发送场景',
  'pages.setting.message.sms_provider.send_scene.columns.name': '模板类型',
  'pages.setting.message.sms_provider.send_scene.columns.content': '模板内容',
  'pages.setting.message.sms_provider.send_scene.columns.code': '模板ID/CODE',
  'pages.setting.message.sms_provider.send_scene.columns.center': '操作',
  'pages.setting.message.sms_provider.send_scene.columns.center.warning': '请配置短信ID/CODE',
  'pages.setting.message.sms_provider.send_scene.modal.from': '测试短信发送',
  'pages.setting.message.sms_provider.send_scene.modal.from.phone': '手机号',
  'pages.setting.message.sms_provider.send_scene.modal.from.name': '场景',
  'pages.setting.message.sms_provider.send_scene.modal.from.content': '模板内容',
  'pages.setting.message.sms_provider.send_scene.modal.test.message':
    '发送短信成功，请查看手机是否接收到验证码',
};
