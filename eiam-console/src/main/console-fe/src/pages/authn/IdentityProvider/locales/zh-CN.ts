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
  'pages.authn.identity_provider.header_title': '提供商列表',
  'pages.authn.identity_provider.desc':
    '支持设置多种身份提供商，企业用户即可通过不同方式登录门户。系统默认的认证源为用户密码和短信快捷认证，您还可添加钉钉扫码、微信扫码、企业微信、LDAP和CAS协议认证源作为认证源。',
  'pages.authn.identity_provider.social': '社交认证源',
  'pages.authn.identity_provider.enterprise': '企业认证源',
  'pages.authn.identity_provider.delete_confirm': '您确定要删除认证源吗？',
  'pages.authn.identity_provider.enable_confirm': '您是否要启用身份源吗？',
  'pages.authn.identity_provider.disable_confirm': '您是否要禁用认证源吗？',
  'pages.authn.identity_provider.add_button': '添加认证源',
  'pages.authn.identity_provider.update_button': '添加认证源',
  'pages.authn.identity_provider.enabled': '已启用',
  'pages.authn.identity_provider.not_enabled': '未启用',
  'pages.authn.identity_provider.add_success_content': '请前往提供商开发后台配置回调地址：',
  'pages.authn.identity_provider.metas_title': '认证源名称',
  'pages.authn.identity_provider.metas_config': '修改',
  'pages.authn.identity_provider.create_modal_title': '创建认证源',
  'pages.authn.identity_provider.update_modal_title': '修改认证源',
  'pages.authn.identity_provider.create_modal.form.type': '创建认证源',
  'pages.authn.identity_provider.create_modal.form.type.placeholder': '请选择认证提供商',
  'pages.authn.identity_provider.create_modal.form.type.rule.0.message': '请选择认证提供商',
  'pages.authn.identity_provider.create_modal.form.type.wechat_qr': '微信扫码认证',
  'pages.authn.identity_provider.create_modal.form.type.qq': 'QQ认证',
  'pages.authn.identity_provider.create_modal.form.type.gitee': 'Gitee认证',
  'pages.authn.identity_provider.create_modal.form.type.github': 'GitHub认证',
  'pages.authn.identity_provider.create_modal.form.type.alipay_oauth': '支付宝认证',
  'pages.authn.identity_provider.create_modal.form.type.dingtalk_oauth': '钉钉认证',
  'pages.authn.identity_provider.create_modal.form.type.dingtalk_qr': '钉钉扫码认证',
  'pages.authn.identity_provider.create_modal.form.type.feishu_oauth': '飞书认证',
  'pages.authn.identity_provider.create_modal.form.type.wechatwork_qr': '企业微信扫码认证',
  'pages.authn.identity_provider.create_modal.form.name': '认证源名称',
  'pages.authn.identity_provider.create_modal.form.name.placeholder': '请输入认证源名称',
  'pages.authn.identity_provider.create_modal.form.name.rule.0.message': '认证源名称为必填项',
  'pages.authn.identity_provider.create_modal.form.remark': '备注',
  'pages.authn.identity_provider.create_modal.form.remark.placeholder': '请输入备注信息',
  'pages.authn.identity_provider.config.callback_url': '回调地址',
  'pages.authn.identity_provider.config.form.switch.displayed.extra': '在登录页展示认证源图标',
  'pages.authn.identity_provider.config.ding_talk_oauth.app_id.extra':
    '在钉钉开放平台，应用开发 -> 企业内部开发，添加小程序或H5微应用后获取',
  'pages.authn.identity_provider.config.ding_talk_oauth.app_id.placeholder': '请填写AppId',
  'pages.authn.identity_provider.config.ding_talk_oauth.app_secret.placeholder': '请填写AppSecret',
  'pages.authn.identity_provider.config.feishu_scan_code.app_id.extra':
    '在飞书开放平台，开发者后台 -> 企业自建应用，创建企业自建应用',
  'pages.authn.identity_provider.config.qq_oauth.app_id.placeholder': '请填写AppId',
  'pages.authn.identity_provider.config.qq_oauth.app_secret.placeholder': '请填写AppKey',
  'pages.authn.identity_provider.config.gitee_oauth.client_id': '客户端ID',
  'pages.authn.identity_provider.config.gitee_oauth.client_id.placeholder': '请填写ClientId',
  'pages.authn.identity_provider.config.gitee_oauth.client_secret': '客户端秘钥',
  'pages.authn.identity_provider.config.gitee_oauth.client_secret.placeholder':
    '请填写ClientSecret',
  'pages.authn.identity_provider.config.gitee_oauth.client_id.extra': 'Gitee应用的Client ID',
  'pages.authn.identity_provider.config.gitee_oauth.client_secret.extra':
    'Gitee应用生成的Client secret',
  'pages.authn.identity_provider.config.github_oauth.client_id':
      '客户端ID',
  'pages.authn.identity_provider.config.github_oauth.client_id.placeholder': '请填写Client ID',
  'pages.authn.identity_provider.config.github_oauth.client_secret':
      '客户端秘钥',
  'pages.authn.identity_provider.config.github_oauth.client_secret.placeholder':
    '请填写Client Secret',
  'pages.authn.identity_provider.config.github_oauth.client_id.extra': 'GitHub应用的Client ID',
  'pages.authn.identity_provider.config.github_oauth.client_secret.extra':
    'GitHub应用生成的Client secret',

  'pages.authn.identity_provider.config.alipay_oauth.app_id': '应用ID',
  'pages.authn.identity_provider.config.alipay_oauth.app_id.placeholder': '请填写应用ID',
  'pages.authn.identity_provider.config.alipay_oauth.app_id.extra': 'xxxxxxxxx',
  'pages.authn.identity_provider.config.alipay_oauth.app_id.placeholder.extra': 'xxxxxxx',
  'pages.authn.identity_provider.config.alipay_oauth.app_private_key': '应用私钥',
  'pages.authn.identity_provider.config.alipay_oauth.app_private_key.placeholder':
    '请填写应用私钥',
  'pages.authn.identity_provider.config.alipay_oauth.app_private_key.extra': 'xxxxxxxxxxx',
  'pages.authn.identity_provider.config.alipay_oauth.alipay_public_key': '支付宝公钥',
  'pages.authn.identity_provider.config.alipay_oauth.alipay_public_key.placeholder':
    '请填写支付宝公钥',
  'pages.authn.identity_provider.config.alipay_oauth.alipay_public_key.extra': 'xxxxxxxxxxxxx',

  'pages.authn.identity_provider.config.wechat_scan_code.app_id.extra':
    '微信扫码登录开发申请获取的AppId',
  'pages.authn.identity_provider.config.wechat_scan_code.app_id.placeholder': '请填写获取的AppId',
  'pages.authn.identity_provider.config.wechat_scan_code.app_secret.extra':
    '微信扫码登录开发申请获取的AppSecret',
  'pages.authn.identity_provider.config.wechat_scan_code.app_secret.placeholder':
    '请输入获取的AppSecret',
  'pages.authn.identity_provider.config.wework_scan_code.corp_id': '企业ID',
  'pages.authn.identity_provider.config.wework_scan_code.corp_id.extra':
    '企业微信管理后台->我的企业->企业信息->企业ID',
  'pages.authn.identity_provider.config.wework_scan_code.corp_id.placeholder': '请输入获取的CorpID',
  'pages.authn.identity_provider.config.wework_scan_code.agent_id.extra':
    '企业微信管理后台->应用管理->自建应用->选择应用->AgentId',
  'pages.authn.identity_provider.config.wework_scan_code.agent_id.placeholder': '请输入AgentId',
  'pages.authn.identity_provider.config.wework_scan_code.app_secret.extra':
    '企业微信管理后台->应用管理->自建应用->选择应用->Secret',
  'pages.authn.identity_provider.config.wework_scan_code.app_secret.placeholder':
    '请输入获取的Secret',
  'pages.authn.identity_provider.add-success-content': '请复制以下链接访问门户端',
};
