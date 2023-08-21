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
  'pages.app.config.basic': '基本配置',
  'pages.app.config.basic.name': '应用名称',
  'pages.app.config.basic.name.rule.0.message': '应用名称为必填项',
  'pages.app.config.basic.icon': '应用图标',
  'pages.app.config.basic.icon.desc.1': '必须为 PNG/JPG 格式',
  'pages.app.config.basic.icon.desc.2': '建议使用 256 * 256 像素方形图标',
  'pages.app.config.basic.enabled': '应用状态',
  'pages.app.config.basic.type': '应用类型',
  'pages.app.config.basic.type.value_enum.custom_made': '定制应用',
  'pages.app.config.basic.type.value_enum.standard': '标准应用',
  'pages.app.config.basic.type.value_enum.self_developed': '自研应用',
  'pages.app.config.basic.protocol_name': '应用协议',
  'pages.app.config.basic.client_id': '客户端 ID',
  'pages.app.config.basic.client_secret': '客户端秘钥',
  'pages.app.config.basic.create_time': '创建时间',
  'pages.app.config.basic.remark': '备注',
  'pages.app.config.items.login_access': '登录访问',
  'pages.app.config.items.login_access.protocol_config': '单点登录',

  'pages.app.config.items.login_access.protocol_config.common.authorization_type': '授权范围',
  'pages.app.config.items.login_access.protocol_config.common.authorization_type.extra':
    '若选择手动授权，需要在 访问授权 中进行权限分配。',
  'pages.app.config.items.login_access.protocol_config.common.authorization_type.rule.0.message':
    '请选择授权范围',
  'pages.app.config.items.login_access.protocol_config.common.authorization_type.option.0':
    '手动授权',
  'pages.app.config.items.login_access.protocol_config.common.authorization_type.option.1':
    '全员可访问',

  'pages.app.config.items.login_access.protocol_config.cas.client_service_url': 'CAS 客户端的地址',
  'pages.app.config.items.login_access.protocol_config.cas.client_service_url.extra':
    '单点登录时，将选中项作为 CAS 用户标识，传递给业务系统。',
  'pages.app.config.items.login_access.protocol_config.cas.client_service_url.rule.0.message':
    '请输入 CAS 客户端的地址',
  'pages.app.config.items.login_access.protocol_config.cas.client_service_url.rule.1.message':
    'CAS 客户端地址格式不正确',
  'pages.app.config.items.login_access.protocol_config.cas.user_identity_type': 'CAS 用户标识',
  'pages.app.config.items.login_access.protocol_config.cas.user_identity_type.option.0': '用户名称',
  'pages.app.config.items.login_access.protocol_config.cas.user_identity_type.option.1': '用户姓名',
  'pages.app.config.items.login_access.protocol_config.cas.user_identity_type.option.2': '用户昵称',
  'pages.app.config.items.login_access.protocol_config.cas.user_identity_type.option.3': '邮箱地址',
  'pages.app.config.items.login_access.protocol_config.cas.user_identity_type.option.4': '应用账户',
  'pages.app.config.items.login_access.protocol_config.cas.user_identity_type.extra':
    '单点登录时，将选中项作为 CAS 用户标识，传递给业务系统。',
  'pages.app.config.items.login_access.protocol_config.cas.user_identity_type.rule.0.message':
    '请选择 CAS 用户标识',
  'pages.app.config.items.login_access.protocol_config.cas.service_ticket_expire_time':
    'ST 过期时间',
  'pages.app.config.items.login_access.protocol_config.cas.service_ticket_expire_time.extra':
    'ServiceTicket 过期时间',
  'pages.app.config.items.login_access.protocol_config.cas.service_ticket_expire_time.rule.0.message':
    '请配置 ServiceTicket 过期时间',
  'pages.app.config.items.login_access.protocol_config.cas.init_login_type': 'SSO 发起方',
  'pages.app.config.items.login_access.protocol_config.cas.init_login_type.rule.0.message':
    '请配置 SSO 发起方',
  'pages.app.config.items.login_access.protocol_config.cas.init_login_type.option.0':
    '只允许应用发起',
  'pages.app.config.items.login_access.protocol_config.cas.init_login_type.option.1':
    '支持门户和应用发起',
  'pages.app.config.items.login_access.protocol_config.cas.init_login_type.extra.0':
    '门户发起：由 IAM 门户页点击进行 SSO 。',
  'pages.app.config.items.login_access.protocol_config.cas.init_login_type.extra.1':
    '应用发起：由应用登录主动发起。',
  'pages.app.config.items.login_access.protocol_config.cas.init_login_url': '登录发起地址',
  'pages.app.config.items.login_access.protocol_config.cas.init_login_url.placeholder':
    '请输入登录链接',
  'pages.app.config.items.login_access.protocol_config.cas.init_login_url.extra':
    '若您希望由 TopIAM 门户页访问应用，请填写 TopIAM 发起 SSO 请求访问的应用地址。该地址接收到请求，应即刻转向 TopIAM / login 授权端点。 ',
  'pages.app.config.items.login_access.protocol_config.cas.config_about': '应用配置信息',
  'pages.app.config.items.login_access.protocol_config.cas.config_about.cas_server_url_prefix':
    'CAS 服务地址前缀',
  'pages.app.config.items.login_access.protocol_config.cas.config_about.cas_sso_endpoint':
    'IdP SSO 地址',
  'pages.app.config.items.login_access.protocol_config.cas.config_about.cas_sso_endpoint.extra':
    '应用发起单点登录的地址。',
  'pages.app.config.items.login_access.protocol_config.cas.config_about.cas_slo_endpoint':
    '登出端点',
  'pages.app.config.items.login_access.protocol_config.cas.config_about.cas_slo_endpoint.extra':
    '应用发起单点登出的地址。',
  'pages.app.config.items.login_access.protocol_config.cas.config_about.cas_validate_endpoint':
    'service ticket 检验端点（CAS 1.0）',
  'pages.app.config.items.login_access.protocol_config.cas.config_about.cas_validate_v2_endpoint':
    'service ticket 检验端点（CAS 2.0）',
  'pages.app.config.items.login_access.protocol_config.cas.config_about.cas_validate_v3_endpoint':
    'service ticket 检验端点（CAS 3.0）',
  'pages.app.config.items.login_access.protocol_config.form': '表单代填单点登录',
  'pages.app.config.items.login_access.protocol_config.form.login_url': '登录提交URL',
  'pages.app.config.items.login_access.protocol_config.form.login_url.placeholder':
    '请输入登录提交URL',
  'pages.app.config.items.login_access.protocol_config.form.login_url.extra':
    '登录表单提交完整URL，以http://或https://开头，如：https://oa.xxxx.com/login',
  'pages.app.config.items.login_access.protocol_config.form.login_url.rule.0.message':
    '应用登录URL为必填项',
  'pages.app.config.items.login_access.protocol_config.form.login_url.rule.1.message':
    'URL格式不正确',
  'pages.app.config.items.login_access.protocol_config.form.username_field': '登录名属性名称',
  'pages.app.config.items.login_access.protocol_config.form.username_field.placeholder':
    '请输入登录名属性名称',
  'pages.app.config.items.login_access.protocol_config.form.username_field.extra':
    'username标签的name属性',
  'pages.app.config.items.login_access.protocol_config.form.username_field.rule.0.message':
    '登录名属性名称为必填项',
  'pages.app.config.items.login_access.protocol_config.form.password_field': '登录密码属性名称',
  'pages.app.config.items.login_access.protocol_config.form.password_field.placeholder':
    '请输入登录密码属性名称',
  'pages.app.config.items.login_access.protocol_config.form.password_field.extra':
    'password标签的name属性',
  'pages.app.config.items.login_access.protocol_config.form.password_field.rule.0.message':
    '登录密码属性名称为必填项',
  'pages.app.config.items.login_access.protocol_config.form.password_encrypt_type':
    '登录密码加密算法',
  'pages.app.config.items.login_access.protocol_config.form.password_encrypt_type.extra':
    '登录密码加密算法类型。',
  'pages.app.config.items.login_access.protocol_config.form.password_encrypt_key':
    '登录密码加密秘钥',
  'pages.app.config.items.login_access.protocol_config.form.password_encrypt_key.extra':
    '登录密码加密秘钥。',
  'pages.app.config.items.login_access.protocol_config.form.password_encrypt_key.rule.0.message':
    '登录密码加密秘钥为必填项',
  'pages.app.config.items.login_access.protocol_config.form.username_encrypt_type':
    '用户名加密算法',
  'pages.app.config.items.login_access.protocol_config.form.username_encrypt_type.extra':
    '用户名加密算法类型。',
  'pages.app.config.items.login_access.protocol_config.form.username_encrypt_key': '用户名加密秘钥',
  'pages.app.config.items.login_access.protocol_config.form.username_encrypt_key.extra':
    '用户名加密秘钥。',
  'pages.app.config.items.login_access.protocol_config.form.username_encrypt_key.rule.0.message':
    '用户名加密秘钥为必填项',
  'pages.app.config.items.login_access.protocol_config.form.submit_type': '登录提交方式',
  'pages.app.config.items.login_access.protocol_config.form.submit_type.rule.0.message':
    '登录提交方式为必选项',
  'pages.app.config.items.login_access.protocol_config.form.other_field': '登录其他字段',
  'pages.app.config.items.login_access.protocol_config.form.other_field.columns.field_name':
    '属性名称',
  'pages.app.config.items.login_access.protocol_config.form.other_field.columns.field_name.rule.0':
    '此项为必填项',
  'pages.app.config.items.login_access.protocol_config.form.other_field.columns.field_value':
    '属性值',
  'pages.app.config.items.login_access.protocol_config.form.other_field.columns.field_value.rule.0':
    '此项为必填项',
  'pages.app.config.items.login_access.protocol_config.form.other_field.columns.option': '操作',
  'pages.app.config.items.login_access.protocol_config.form.other_field.record_creator_props':
    '添加其他字段',
  'pages.app.config.items.login_access.protocol_config.form.other_field.editable':
    '您确定要删除此属性吗？',
  'pages.app.config.items.login_access.protocol_config.form.config_about': '应用配置信息',
  'pages.app.config.items.login_access.protocol_config.form.config_about.idp_sso_endpoint':
    'IdP SSO 地址',
  'pages.app.config.items.login_access.protocol_config.form.config_about.idp_sso_endpoint.extra':
    '应用发起单点登录的地址。',
  'pages.app.config.items.login_access.protocol_config.jwt': 'JWT单点登录',
  'pages.app.config.items.login_access.protocol_config.jwt.redirect_url': 'SSO 地址',
  'pages.app.config.items.login_access.protocol_config.jwt.redirect_url.extra':
    '业务系统中的JWT SSO地址，在单点登录时本系统将向该地址发送id_token信息，参数名为id_token，业务系统通过id_token与Public Key可获取业务系统中的用户信息，如果在业务系统（SP）发起登录，请求SP登录地址时如果携带redirect_uri参数，系统会检验合法性，成功后会将浏览器重定向到该地址，并携带id_token身份令牌。',
  'pages.app.config.items.login_access.protocol_config.jwt.redirect_url.rule.0.message':
    '请输入JWT应用SSO地址',
  'pages.app.config.items.login_access.protocol_config.jwt.redirect_url.rule.1.message':
    'JWT应用SSO地址格式不正确',
  'pages.app.config.items.login_access.protocol_config.jwt.target_link_url.extra':
    '业务系统中在JWT SSO成功后重定向的URL，一般用于跳转到二级菜单等，若设置了该URL，在JWT SSO时会以参数target_link_url优先传递该值，若未设置该值，此时若SSO中有请求参数target_link_url，则会按照请求参数传递该值。此项可选。',
  'pages.app.config.items.login_access.protocol_config.jwt.target_link_url.rule.0.message':
    '地址格式不正确',
  'pages.app.config.items.login_access.protocol_config.jwt.binding_type': 'SSO 绑定类型',
  'pages.app.config.items.login_access.protocol_config.jwt.binding_type.extra':
    '指定向JWT应用发送 id_token 的请求方式。',
  'pages.app.config.items.login_access.protocol_config.jwt.binding_type.rule.0.message':
    '登录提交方式为必选项',
  'pages.app.config.items.login_access.protocol_config.jwt.idtoken_subject_type':
    'id_token 主体类型',
  'pages.app.config.items.login_access.protocol_config.jwt.idtoken_subject_type.option.0': '用户ID',
  'pages.app.config.items.login_access.protocol_config.jwt.idtoken_subject_type.option.1':
    '应用账户',
  'pages.app.config.items.login_access.protocol_config.jwt.idtoken_subject_type.extra':
    'id_token 中 sub 主体(用户) 类型',
  'pages.app.config.items.login_access.protocol_config.jwt.idtoken_subject_type.rule.0.message':
    '请选择 CAS 用户标识',
  'pages.app.config.items.login_access.protocol_config.jwt.idtoken_time_to_live':
    'id_token 过期时间',
  'pages.app.config.items.login_access.protocol_config.jwt.idtoken_time_to_live.extra':
    'id_token 的有效期，单位为：秒。可设置范围为1-84600。',
  'pages.app.config.items.login_access.protocol_config.jwt.idtoken_time_to_live.rule.0.message':
    '请配置 id_token 的有效期',
  'pages.app.config.items.login_access.protocol_config.jwt.config_about': '应用配置信息',
  'pages.app.config.items.login_access.protocol_config.jwt.config_about.idp_sso_endpoint':
    'IdP SSO 地址',
  'pages.app.config.items.login_access.protocol_config.jwt.config_about.idp_sso_endpoint.extra':
    '应用发起单点登录的地址。',
  'pages.app.config.items.login_access.protocol_config.jwt.config_about.idp_slo_endpoint':
    '登出端点',
  'pages.app.config.items.login_access.protocol_config.jwt.config_about.idp_slo_endpoint.extra':
    '应用发起单点登出的地址。',
  'pages.app.config.items.login_access.protocol_config.jwt.config_about.idp_encrypt_cert':
    'JWT 验签公钥',
  'pages.app.config.items.login_access.protocol_config.jwt.config_about.idp_encrypt_cert.extra.0':
    '下载或复制证书，并导入或粘贴到应用中。',
  'pages.app.config.items.login_access.protocol_config.jwt.config_about.idp_encrypt_cert.extra.1':
    '复制证书内容',
  'pages.app.config.items.login_access.protocol_config.jwt.config_about.idp_encrypt_cert.extra.2':
    '下载证书 .cer 文件',
  'pages.app.config.items.login_access.protocol_config.oidc': 'OIDC 单点登录',
  'pages.app.config.items.login_access.protocol_config.oidc.auth_grant_types': '授权模式',
  'pages.app.config.items.login_access.protocol_config.oidc.auth_grant_types.rule.0.message':
    '请勾选授权模式',
  'pages.app.config.items.login_access.protocol_config.oidc.auth_grant_types.option.authorization_code.label.0':
    '授权码模式',
  'pages.app.config.items.login_access.protocol_config.oidc.auth_grant_types.option.authorization_code.label.1':
    ' authorization_code 模式，用于账户的登录认证、授权。',
  'pages.app.config.items.login_access.protocol_config.oidc.auth_grant_types.option.refresh_token.label.0':
    '令牌刷新模式',
  'pages.app.config.items.login_access.protocol_config.oidc.auth_grant_types.option.refresh_token.label.1':
    'refresh_token 模式，用于既有 token 的延期。',
  'pages.app.config.items.login_access.protocol_config.oidc.auth_grant_types.option.implicit.label.0':
    '隐式模式',
  'pages.app.config.items.login_access.protocol_config.oidc.auth_grant_types.option.implicit.label.1':
    ' implicit\n' +
    '                      模式，由于协议本身的安全性，通常不推荐使用。如果有特殊需求，可以使用变体的' +
    '                      PKCE 的授权码模式。',
  'pages.app.config.items.login_access.protocol_config.oidc.auth_grant_types.option.password.label.0':
    '密码模式',
  'pages.app.config.items.login_access.protocol_config.oidc.auth_grant_types.option.password.label.1':
    'password 模式，由于协议本身的安全性，通常不推荐使用。',
  'pages.app.config.items.login_access.protocol_config.oidc.auth_grant_types.option.device.label.0':
    '设备模式',
  'pages.app.config.items.login_access.protocol_config.oidc.auth_grant_types.option.device.label.1':
    'device 模式，兼容设备发起的登录流程。',
  'pages.app.config.items.login_access.protocol_config.oidc.require_proof_key':
    'PKCE（用于授权码模式）',
  'pages.app.config.items.login_access.protocol_config.oidc.require_proof_key.extra':
    'PKCE （Proof Key for Code Exchange）是 OAuth 2.0 的安全性扩展模式，用于防护 CSRF、中间人进攻等恶意攻击。',
  'pages.app.config.items.login_access.protocol_config.oidc.redirect_uris': '登录 Redirect URI',
  'pages.app.config.items.login_access.protocol_config.oidc.redirect_uris.extra':
    'Redirect URI 白名单，应用在请求登录时携带 redirect_uri 参数，该值需要在白名单中，IAM 才会在认证完成后发起跳转。若有多条，请点击添加进行扩展',
  'pages.app.config.items.login_access.protocol_config.oidc.redirect_uris.rule.0.message':
    '请配置登录 Redirect URI',
  'pages.app.config.items.login_access.protocol_config.oidc.redirect_uris.rule.1.message':
    'Redirect URI 格式不正确',
  'pages.app.config.items.login_access.protocol_config.oidc.redirect_uris.placeholder':
    '请输入登录 Redirect URI',
  'pages.app.config.items.login_access.protocol_config.oidc.post_logout_redirect_uris':
    '登出 Redirect URI',
  'pages.app.config.items.login_access.protocol_config.oidc.post_logout_redirect_uris.extra':
    '登出 Redirect URI 白名单，应用在请求登录时携带 post_logout_redirect_uri 参数，该值需要在白名单中，IAM 才会在认证完成后发起跳转。若有多条，请点击添加进行扩展',
  'pages.app.config.items.login_access.protocol_config.oidc.post_logout_redirect_uris.rule.0.message':
    '请配置登出 Redirect URI',
  'pages.app.config.items.login_access.protocol_config.oidc.post_logout_redirect_uris.rule.1.message':
    '登出 Redirect URI 格式不正确',
  'pages.app.config.items.login_access.protocol_config.oidc.post_logout_redirect_uris.placeholder':
    '请输入登出 Redirect URI',
  'pages.app.config.items.login_access.protocol_config.oidc.init_login_type': 'SSO 发起方',
  'pages.app.config.items.login_access.protocol_config.oidc.init_login_type.rule.0.message':
    '请配置 SSO 发起方',
  'pages.app.config.items.login_access.protocol_config.oidc.init_login_type.option.0':
    '只允许应用发起',
  'pages.app.config.items.login_access.protocol_config.oidc.init_login_type.option.1':
    '支持门户和应用发起',
  'pages.app.config.items.login_access.protocol_config.oidc.init_login_type.extra.0':
    '门户发起：由 IAM 门户页点击进行 SSO 。',
  'pages.app.config.items.login_access.protocol_config.oidc.init_login_type.extra.1':
    '应用发起：由应用登录主动发起。',
  'pages.app.config.items.login_access.protocol_config.oidc.init_login_url': '登录发起地址',
  'pages.app.config.items.login_access.protocol_config.oidc.init_login_url.rule.0.message':
    '登录发起地址不能为空',
  'pages.app.config.items.login_access.protocol_config.oidc.init_login_url.field_props':
    '请输入登录链接',
  'pages.app.config.items.login_access.protocol_config.oidc.init_login_url.extra':
    '若您希望由 TopIAM 门户页访问应用，请填写 TopIAM 发起 SSO 请求访问的应用地址。该地址接收到请求，应即刻转向 TopIAM / authorize 授权端点。',
  'pages.app.config.items.login_access.protocol_config.oidc.advanced': '显示高级配置',
  'pages.app.config.items.login_access.protocol_config.oidc.advanced.grant_scopes': '用户信息范围',
  'pages.app.config.items.login_access.protocol_config.oidc.advanced.grant_scopes.rule.0.message':
    '请勾选用户信息范围',
  'pages.app.config.items.login_access.protocol_config.oidc.advanced.grant_scopes.extra':
    '用户登录后，使用用户信息端点或解析 id_token 可以获取到的已登录用户信息 。',
  'pages.app.config.items.login_access.protocol_config.oidc.advanced.grant_scopes.option.1.label':
    '应用可获取登录用户邮箱信息。',
  'pages.app.config.items.login_access.protocol_config.oidc.advanced.grant_scopes.option.2.label':
    '应用可获取登录用户手机信息。',
  'pages.app.config.items.login_access.protocol_config.oidc.advanced.grant_scopes.option.3.label':
    '应用可获取登录用户详情信息。',
  'pages.app.config.items.login_access.protocol_config.oidc.advanced.access_token_time_to_live':
    'access_token 有效期',
  'pages.app.config.items.login_access.protocol_config.oidc.advanced.access_token_time_to_live.extra':
    'access_token 用于请求 IAM 接口，过期后需要使用 refresh_token 刷新，或重新登录。',
  'pages.app.config.items.login_access.protocol_config.oidc.advanced.refresh_token_time_to_live':
    'refresh_token 有效期',
  'pages.app.config.items.login_access.protocol_config.oidc.advanced.refresh_token_time_to_live.extra':
    '用于获取新的 access_token 和 id_token，refresh_token 过期后，用户需要重新登录。',
  'pages.app.config.items.login_access.protocol_config.oidc.advanced.idtoken_time_to_live':
    'id_token 有效期',
  'pages.app.config.items.login_access.protocol_config.oidc.advanced.idtoken_time_to_live.extra':
    'id_token 用于鉴别用户身份，JWT格式，允许应用使用公钥自行验证用户身份。最小5分钟，最大24小时，过期后需要使用refresh_token 刷新，或重新登录。',
  'pages.app.config.items.login_access.protocol_config.oidc.advanced.idtoken_signature_algorithm':
    'id_token 签名算法',
  'pages.app.config.items.login_access.protocol_config.oidc.advanced.idtoken_signature_algorithm.extra':
    'id_token 签名使用的非对称算法。',
  'pages.app.config.items.login_access.protocol_config.oidc.advanced.idtoken_signature_algorithm.rule.0.message':
    '请配置 id_token 签名算法',
  'pages.app.config.items.login_access.protocol_config.oidc.config_about': '应用配置信息',
  'pages.app.config.items.login_access.protocol_config.oidc.config_about.issuer.extra':
    '用于标识 token 发放来源的字段。同时是下述接口的 baseUrl。',
  'pages.app.config.items.login_access.protocol_config.oidc.discovery_endpoint': '发现端点',
  'pages.app.config.items.login_access.protocol_config.oidc.discovery_endpoint.extra':
    '用于获取当前 IAM 支持的各端点信息和支持的模式、参数信息，可公开访问。',
  'pages.app.config.items.login_access.protocol_config.oidc.authorization_endpoint': '授权端点',
  'pages.app.config.items.login_access.protocol_config.oidc.authorization_endpoint.extra':
    '应用发起单点登录的地址。',
  'pages.app.config.items.login_access.protocol_config.oidc.token_endpoint': '令牌端点',
  'pages.app.config.items.login_access.protocol_config.oidc.token_endpoint.extra':
    '应用在单点登录过程中，拿到 code 后，从后端发起换取 token 的接口地址。',
  'pages.app.config.items.login_access.protocol_config.oidc.revoke_endpoint': '令牌吊销端点',
  'pages.app.config.items.login_access.protocol_config.oidc.jwks_endpoint': '验签公钥端点',
  'pages.app.config.items.login_access.protocol_config.oidc.jwks_endpoint.extra':
    '用于验证 id_token、完成 SSO 流程的公钥端点。公钥可能会轮转。',
  'pages.app.config.items.login_access.protocol_config.oidc.userinfo_endpoint': '用户信息端点',
  'pages.app.config.items.login_access.protocol_config.oidc.userinfo_endpoint.extra':
    '在账户登录后，使用 access_token 调用用户信息端点，获取账户基本信息。',
  'pages.app.config.items.login_access.protocol_config.oidc.end_session_endpoint': '结束会话端点',
  'pages.app.config.items.login_access.protocol_config.oidc.end_session_endpoint.extra':
    '结束会话端点可用于触发单点注销。',
  'pages.app.config.items.login_access.protocol_config.saml2.standard': 'SAML 单点登录',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.meta_data_url.extra':
    'SP 元数据为应用侧生成的 SSO 配置文件。若已有元数据，直接导入，可自动填充下方 SSO 参数，简单快捷。',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.meta_data_url.placeholder':
    '请输入应用 metadata 地址',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.meta_data_url.label':
    '上传 SP 元数据 ',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.sp_acs_url.placeholder':
    '请输入单点登录地址',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.sp_acs_url.extra':
    '应用的 SAML SSO 核心地址，与 IAM 交互处理单点登录请求。',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.sp_acs_url.rule.0.message':
    '请输入单点登录地址',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.sp_entity_id.placeholder':
    '请输入应用唯一标识',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.sp_entity_id.extra':
    '应用在 IAM 中的标识，通常在应用侧获取，格式通常为应用 URI。',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.sp_entity_id.rule.0':
    '请输入应用唯一标识。',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.name_id_value_type':
    'Name ID 类型',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.name_id_value_type.extra':
    '单点登录时，将选中项作为账户标识，传递给业务系统。',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.name_id_value_type.rule.0.message':
    '请选择 Name ID',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.init_login_type':
    'SSO 发起方',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.init_login_type.rule.0.message':
    '请配置 SSO 发起方',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.init_login_type.option.0':
    '只允许应用发起',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.init_login_type.option.1':
    '支持门户和应用发起',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.init_login_type.extra.0':
    '门户发起：由 IAM 门户页点击进行 SSO 。',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.init_login_type.extra.1':
    '应用发起：由应用登录主动发起。',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.init_login_url':
    '登录发起地址',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.init_login_url.placeholder':
    '请输入登录链接',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.init_login_url.extra.0':
    '若应用支持 IDP 发起 SAMLResponse SSO 请求，可不填写。',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.init_login_url.extra.1':
    '若应用仅支持 SP 发起 SSO，您又希望实现由 IAM 门户页发起登录，请填写此项。门户页访问应用时，IAM 会跳转到本地址，应即刻自动向 IAM 发起 SAMLRequest 登录请求。地址可填写为任意受保护资源访问地址，在未登录时会自动触发 SAMLRequest 即可。',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced':
    '显示高级配置',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.relay_state':
    '默认RelayState',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.relay_state.placeholder':
    '请输入默认跳转地址',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.relay_state.rule.0.message':
    'RelayState 格式不正确',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.relay_state.extra':
    'DP 发起 SSO 登录成功后，应用应自动跳转的地址。在 SAML Response 中会在 RelayState 参数中传递，应用读取后实现跳转。',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion':
    '断言',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.audience.extra':
    'SAML断言的目标受众，默认和SP Entity ID相同。',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.audience.placeholder':
    '请输入SAML断言的目标受众',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.name_id_format':
    'NameID 格式',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.name_id_format.rule.0.message':
    '请选择 NameID 字段格式',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.name_id_format.extra':
    'SAML Response 中指定账户标识 NameID 字段格式。',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.acs_binding':
    'ACS 绑定类型',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.acs_binding.rule.0.message':
    '请选择 ACS 绑定类型',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.acs_binding.extra':
    '指定IDP向SP发送断言请求的方式。',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.assert_encrypted':
    '是否加密断言',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.assert_encrypted.rule.0.message':
    '请选择是否对断言加密。',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.assert_encrypt_algorithm':
    '断言加密算法。',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.assert_encrypt_algorithm.extra':
    '断言加密使用的算法',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.assert_encrypt_algorithm.rule.0.message':
    '请选择断言加密使用的算法',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.assert_signed':
    '是否签名断言',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.assert_signed.rule.0.message':
    '请选择是否对断言签名',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.assert_signed.extra':
    '是否对断言使用IdP的证书签名，对应SP元数据文件中“WantAssertionsSigned”值。',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.assert_sign_algorithm':
    '断言签名算法',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.assert_sign_algorithm.extra':
    '断言签名使用的算法',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.assert_sign_algorithm.rule.0.message':
    '请选择签名使用的算法',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.authn_context_classref':
    '身份认证上下文',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.authn_context_classref.extra':
    'SAML 身份认证上下文',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.authn_context_classref.rule.0.message':
    '请选择身份认证上下文',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.attribute_statements':
    '断言属性',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.attribute_statements.columns.name':
    '属性名称',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.attribute_statements.columns.name.rule.0':
    '此项为必填项',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.attribute_statements.columns.name_format':
    '名称格式',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.attribute_statements.columns.name_format.rule.0':
    '此项为必填项',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.attribute_statements.columns.value_expression':
    '属性值',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.attribute_statements.columns.value_expression.tooltip':
    '支持的变量：',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.attribute_statements.columns.value_expression.placeholder':
    '变量值',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.attribute_statements.columns.value_expression.rule.0':
    '此项为必填项',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.attribute_statements.columns.option':
    '操作',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.attribute_statements.recordCreatorProps':
    '添加断言属性',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.assertion.attribute_statements.editable':
    '您确定要删除此断言属性吗？',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.response_signed':
    '响应签名',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.response_signed.label':
    '是否签名响应',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.response_signed.rule.0.message':
    '请选择是否对响应签名',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.response_signed.response_sign_algorithm':
    '响应签名算法',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.response_signed.response_sign_algorithm.extra':
    '断言响应使用的算法',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.response_signed.response_sign_algorithm.rule.0.message':
    '请选择响应签名使用的算法',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.sp_requests':
    '验证请求',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.sp_requests.sp_requests_signed':
    '验证请求签名',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.sp_requests.sp_requests_signed.rule.0.message':
    '请选择是否验证请求签名',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.sp_requests.sp_requests_signed.extra':
    '用来对SAML Request签名进行验证，对应SP元数据文件中“AuthnRequestsSigned”值。',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.single_point_logout':
    '单点注销',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.single_point_logout.sp_slo_enabled':
    '启用 SLO',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.single_point_logout.sp_slo_enabled.rule.0.message':
    '请选择是否启用 SLO',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.single_point_logout.sp_slo_enabled.extra':
    'SP 是否支持单点注销 (SLO) 。',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.single_point_logout.sp_sls_url':
    '单点注销 URL',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.single_point_logout.sp_sls_url.rule.0.message':
    '单点注销地址格式不正确',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.single_point_logout.sp_sls_url.rule.1.message':
    '请输入单点注销地址',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.single_point_logout.sp_sls_url.extra':
    '会话注销功能，用户在 TopIAM 注销会话后通知该地址。',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.single_point_logout.sp_sls_binding':
    '单点注销绑定类型',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.single_point_logout.sp_sls_binding.rule.0.message':
    '请选择单点注销绑定类型',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.single_point_logout.sp_sls_binding.extra':
    '指定了 IDP 向 SP 发送 SLO 请求的方式。',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.sp_sign_cert':
    '签名证书',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.sp_sign_cert.rule.0.message':
    '请填写验证签名证书',
  'pages.app.config.items.login_access.protocol_config.saml2.standard.form.advanced.sp_sign_cert.extra':
    'SP公钥证书，用来验证SAML request的签名，对应SP元数据文件中use="signing"证书内容。',

  'pages.app.config.items.login_access.protocol_config.saml2.config_about': '应用配置信息',
  'pages.app.config.items.login_access.protocol_config.saml2.config_about.idp_meta_endpoint':
    'IdP 元数据',
  'pages.app.config.items.login_access.protocol_config.saml2.config_about.idp_meta_endpoint.extra':
    '若应用支持 metadata 配置信息上传/拉取，可以节省大量配置步骤。请在应用 SSO 配置中寻找是否有 metadata 上传能力。',
  'pages.app.config.items.login_access.protocol_config.saml2.config_about.idp_entity_id_endpoint':
    'IdP 唯一标识',
  'pages.app.config.items.login_access.protocol_config.saml2.config_about.idp_entity_id_endpoint.extra':
    'AM 在应用中的标识。需要将值填写在应用单点登录配置中。',
  'pages.app.config.items.login_access.protocol_config.saml2.config_about.idp_sso_endpoint':
    'IdP SSO 地址',
  'pages.app.config.items.login_access.protocol_config.saml2.config_about.idp_sso_endpoint.extra':
    'SAML 协议支持 SP 发起单点登录，可能需要填写此地址在应用配置中。由 IAM 提供。可以直接访问该地址，进行应用登录。',
  'pages.app.config.items.login_access.protocol_config.saml2.config_about.idp_slo_endpoint':
    '单点退出地址',
  'pages.app.config.items.login_access.protocol_config.saml2.config_about.idp_slo_endpoint.extra':
    'SAML 协议支持单点退出，可能需要填写此地址在应用配置中。由 IAM 提供。',
  'pages.app.config.items.login_access.protocol_config.saml2.config_about.idp_sign_cert':
    '签名公钥证书',
  'pages.app.config.items.login_access.protocol_config.saml2.config_about.idp_sign_cert.extra.0':
    '下载或复制证书，并导入或粘贴到应用中。',
  'pages.app.config.items.login_access.protocol_config.saml2.config_about.idp_sign_cert.extra.1':
    '复制证书内容',
  'pages.app.config.items.login_access.protocol_config.saml2.config_about.idp_sign_cert.extra.2':
    '下载证书 .cer 文件',
  'pages.app.config.items.login_access.app_account': '应用账户',
  'pages.app.config.items.login_access.app_account.alert.message':
    '如果您配置使用【系统账户名】，应用账户属性配置后不会生效；如需变更，请在 协议配置 更改配置。',
  'pages.app.config.items.login_access.app_account.columns.username': '系统用户',
  'pages.app.config.items.login_access.app_account.columns.account': '应用账户',
  'pages.app.config.items.login_access.app_account.columns.create_time': '添加时间',
  'pages.app.config.items.login_access.app_account.columns.option': '操作',
  'pages.app.config.items.login_access.app_account.columns.option.popconfirm.title':
    '您确定要删除此应用账户？',
  'pages.app.config.items.login_access.app_account.create_app_account': '添加应用账户',
  'pages.app.config.items.login_access.app_account.create_app_account.modal_form.user_id.rule.0.message':
    '请选择系统用户',
  'pages.app.config.items.login_access.app_account.create_app_account.modal_form.user_id.placeholder':
    '请输入用户名、手机或邮箱搜索用户',
  'pages.app.config.items.login_access.app_account.create_app_account.modal_form.account':
    '应用用户名',
  'pages.app.config.items.login_access.app_account.create_app_account.modal_form.account.rule.0.message':
    '请输入账户访问应用时所使用户名',
  'pages.app.config.items.login_access.app_account.create_app_account.modal_form.password':
    '应用用户密码',
  'pages.app.config.items.login_access.app_account.create_app_account.modal_form.password.rule.0.message':
    '请输入账户访问应用时所使密码',
  'pages.app.config.items.login_access.app_account.create_app_account.modal_form.app_identity':
    '应用身份',
  'pages.app.config.items.login_access.app_account.create_app_account.modal_form.app_identity.rule.0.message':
    '请输入账户访问应用时所使身份',
  'pages.app.config.items.login_access.access_policy': '访问授权',
  'pages.app.config.items.login_access.access_policy.columns.subject_name': '授权主体',
  'pages.app.config.items.login_access.access_policy.columns.subject_type': '主体类型',
  'pages.app.config.items.login_access.access_policy.columns.subject_type.value_enum.user': '用户',
  'pages.app.config.items.login_access.access_policy.columns.subject_type.value_enum.user_group':
    '用户组',
  'pages.app.config.items.login_access.access_policy.columns.subject_type.value_enum.organization':
    '组织机构',
  'pages.app.config.items.login_access.access_policy.columns.create_time': '授权时间',
  'pages.app.config.items.login_access.access_policy.columns.option': '操作',
  'pages.app.config.items.login_access.access_policy.columns.option.popconfirm.title':
    '您确定要取消主体授权？取消授权后不可自动恢复。',
  'pages.app.config.items.login_access.access_policy.create_policy': '添加授权',
  'pages.app.config.items.login_access.access_policy.cancel_policy': '取消授权',
  'pages.app.config.items.login_access.access_policy.create_policy.modal_form.subject_type':
    '授权类型',
  'pages.app.config.items.login_access.access_policy.create_policy.modal_form.subject_type.rule.0.message':
    '请选择授权类型',
  'pages.app.config.items.login_access.access_policy.create_policy.modal_form.subject_type.auth_user':
    '授权用户',
  'pages.app.config.items.login_access.access_policy.create_policy.modal_form.subject_type.auth_user.rule.0.message':
    '请选择授权用户',
  'pages.app.config.items.login_access.access_policy.create_policy.modal_form.subject_type.auth_user.placeholder':
    '请输入用户名搜索用户',
  'pages.app.config.items.login_access.access_policy.create_policy.modal_form.subject_type.auth_user_group':
    '授权分组',
  'pages.app.config.items.login_access.access_policy.create_policy.modal_form.subject_type.auth_user_group.rule.0.message':
    '请选择授权分组',
  'pages.app.config.items.login_access.access_policy.create_policy.modal_form.subject_type.auth_organization':
    '授权组织',
  'pages.app.config.items.login_access.access_policy.create_policy.modal_form.subject_type.auth_organization.rule.0.message':
    '请选择组织节点',
  'pages.app.config.items.account_sync': '账户同步',
  'pages.app.config.error': '未指定应用',
};
