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
  'pages.account.identity_source_detail.common.action_type': '动作类型',
  'pages.account.identity_source_detail.common.object_id': '对象 ID',
  'pages.account.identity_source_detail.common.object_name': '对象名称',
  'pages.account.identity_source_detail.common.desc': '描述',
  'pages.account.identity_source_detail.common.object_type': '对象类型',
  'pages.account.identity_source_detail.common.app_secret': '应用凭证密钥',
  'pages.account.identity_source_detail.common.app_secret.placeholder': '请输入应用凭证密钥',
  'pages.account.identity_source_detail.common.app_secret.rule.0.message': '应用凭证密钥为必填项',
  'pages.account.identity_source_detail.common.test_connect': '测试连接',
  'pages.account.identity_source_detail.common.test_succeed': '测试成功',
  'pages.account.identity_source_detail.common.test_fail': '测试连接失败，请核实参数信息',

  'pages.account.identity_source_detail.warning.message': '身份源不存在',
  'pages.account.identity_source_detail.name': '名称',
  'pages.account.identity_source_detail.remark': '备注',
  'pages.account.identity_source_detail.page_container.tab_list.config': '同步配置',
  'pages.account.identity_source_detail.page_container.tab_list.sync_history': '同步历史',
  'pages.account.identity_source_detail.page_container.tab_list.event_record': '事件记录',
  'pages.account.identity_source_detail.step_form.base_config': '基本配置',
  'pages.account.identity_source_detail.step_form.strategy_config': '高级配置',
  'pages.account.identity_source_detail.step_form.job_config': '任务配置',
  'pages.account.identity_source_detail.sync_record.columns.status': '状态',
  'pages.account.identity_source_detail.sync_record.drawer.title': '详情记录',
  'pages.account.identity_source_detail.sync_history.title': '详情记录',
  'pages.account.identity_source_detail.sync_history.columns.batch': '批号',
  'pages.account.identity_source_detail.sync_history.columns.trigger_type': '触发类型',
  'pages.account.identity_source_detail.sync_history.columns.value_enum.value_enum.manual':
    '手动触发',
  'pages.account.identity_source_detail.sync_history.columns.value_enum.value_enum.job': '任务触发',
  'pages.account.identity_source_detail.sync_history.columns.object_type.value_enum.user': '用户',
  'pages.account.identity_source_detail.sync_history.columns.object_type.value_enum.organization':
    '组织',
  'pages.account.identity_source_detail.sync_history.columns.created_count': '创建数量',
  'pages.account.identity_source_detail.sync_history.columns.updated_count': '更新数量',
  'pages.account.identity_source_detail.sync_history.columns.deleted_count': '删除数量',
  'pages.account.identity_source_detail.sync_history.columns.skipped_count': '跳过数量',
  'pages.account.identity_source_detail.sync_history.columns.start_time': '开始时间',
  'pages.account.identity_source_detail.sync_history.columns.end_time': '结束时间',
  'pages.account.identity_source_detail.sync_history.columns.spend_time': '耗时',
  'pages.account.identity_source_detail.sync_history.columns.status': '状态',
  'pages.account.identity_source_detail.sync_history.columns.status.valu_enum.pending': '同步中',
  'pages.account.identity_source_detail.sync_history.columns.option': '操作',
  'pages.account.identity_source_detail.sync_history.tool_bar_render.title': '一键拉取',
  'pages.account.identity_source_detail.sync_history.tool_bar_render.success.title': '任务触发成功',
  'pages.account.identity_source_detail.sync_history.tool_bar_render.content.title':
    'TopIAM 将全量拉取身份源授权范围内部门和账户，置于同步目标组织下。',
  'pages.account.identity_source_detail.sync_history.tool_bar_render.content.description':
    'TopIAM 将全量拉取身份源授权范围内部门和账户，置于同步目标组织下。',
  'pages.account.identity_source_detail.strategy_config.target_extra.0':
    '身份源组织同步至 TopIAM 后的父级组织。',
  'pages.account.identity_source_detail.strategy_config.target_extra.1':
    '如果不填，将自动创建顶层组织。配置后不可更改。',
  'pages.account.identity_source_detail.strategy_config.organization_policies': '组织相关策略',
  'pages.account.identity_source_detail.strategy_config.organization_policies.target_id':
    '目标根组织',
  'pages.account.identity_source_detail.strategy_config.organization_policies.target_id.placeholder':
    '请选择目标根组织',
  'pages.account.identity_source_detail.strategy_config.user_policies': '用户相关策略',
  'pages.account.identity_source_detail.strategy_config.user_policies.default_password': '默认密码',
  'pages.account.identity_source_detail.strategy_config.user_policies.default_password.extra':
    '同步账户时给账户设置的默认密码，若不设置，将使用设置中密码策略随机生成。',
  'pages.account.identity_source_detail.strategy_config.user_policies.default_password.placeholder':
    '请输入默认密码',
  'pages.account.identity_source_detail.strategy_config.user_policies.enabled': '是否启用',
  'pages.account.identity_source_detail.strategy_config.user_policies.enabled.extra.0':
    '默认启用，首次同步用户时，用户是否在 TopIAM 中启用。',
  'pages.account.identity_source_detail.strategy_config.user_policies.enabled.extra.1':
    '注意：身份源用户未启用时，将已身份源为准。',
  'pages.account.identity_source_detail.strategy_config.user_policies.email_notify': '邮件通知',
  'pages.account.identity_source_detail.strategy_config.user_policies.email_notify.extra':
    '默认启用，首次同步用户成功后，将发送电子邮件，若没有获取到邮件地址，将无法进行发送。',
  'pages.account.identity_source_detail.job_config.alert.description':
    '定时配置必须同时配置星期数及执行方式。例：勾选周二并选择每隔2小时执行一次时，表示在每个周二，每隔2小时执行一次同步。',
  'pages.account.identity_source_detail.job_config.day_of_week.options.0': '每天',
  'pages.account.identity_source_detail.job_config.day_of_week.rule.0.message': '该项为必选项',
  'pages.account.identity_source_detail.job_config.day_of_week.options.1': '周一',
  'pages.account.identity_source_detail.job_config.day_of_week.options.2': '周二',
  'pages.account.identity_source_detail.job_config.day_of_week.options.3': '周三',
  'pages.account.identity_source_detail.job_config.day_of_week.options.4': '周四',
  'pages.account.identity_source_detail.job_config.day_of_week.options.5': '周五',
  'pages.account.identity_source_detail.job_config.day_of_week.options.6': '周六',
  'pages.account.identity_source_detail.job_config.day_of_week.options.7': '周天',
  'pages.account.identity_source_detail.job_config.mode.options.0': '周期执行',
  'pages.account.identity_source_detail.job_config.mode.options.1': '定时执行',
  'pages.account.identity_source_detail.job_config.mode.rule.0.message': '请配置执行方式',
  'pages.account.identity_source_detail.job_config.interval.addon_before': '每隔',
  'pages.account.identity_source_detail.job_config.interval.addon_after': '小时执行一次',
  'pages.account.identity_source_detail.job_config.interval.rule.0.message': '请输入间隔小时',
  'pages.account.identity_source_detail.job_config.time.addon_before': '执行时间',
  'pages.account.identity_source_detail.job_config.time.addon_before.rule.0.message':
    '请选择执行时间',
  'pages.account.identity_source_detail.event_record.columns.event_time': '事件时间',
  'pages.account.identity_source_detail.event_record.columns.status': '事件状态',
  'pages.account.identity_source_detail.basic_config.ding_talk.corp_id': '企业ID',
  'pages.account.identity_source_detail.basic_config.ding_talk.corp_id.placeholder': '请输入企业ID',
  'pages.account.identity_source_detail.basic_config.ding_talk.corp_id.extra':
    '你可以在钉钉开放平台(https://open-dev.dingtalk.com/)首页右上角获取企业 ID（CorpId）。',
  'pages.account.identity_source_detail.basic_config.ding_talk.app_key': '应用开发Key',
  'pages.account.identity_source_detail.basic_config.ding_talk.app_key.placeholder':
    '请输入应用开发Key',
  'pages.account.identity_source_detail.basic_config.ding_talk.app_key.extra':
    '你可以在钉钉开放平台(https://open-dev.dingtalk.com/)应用详情的「基础信息」页面获取 AppKey。',
  'pages.account.identity_source_detail.basic_config.ding_talk.app_key.rule.0.message':
    '应用开发Key为必填项',
  'pages.account.identity_source_detail.basic_config.ding_talk.app_secret.extra':
    '可以在钉钉开放平台(https://open-dev.dingtalk.com/)应用详情的「基础信息」页面获取 AppSecret。',
  'pages.account.identity_source_detail.basic_config.ding_talk.aes_key': '加密AesKey',
  'pages.account.identity_source_detail.basic_config.ding_talk.aes_key.extra':
    '你可以在钉钉开放平台(https://open-dev.dingtalk.com/)应用详情的「事件与回调」页面获取加密 aes_key。如果你需要开启实时同步，此参数必填。',
  'pages.account.identity_source_detail.basic_config.ding_talk.token': '签名Token',
  'pages.account.identity_source_detail.basic_config.ding_talk.token.extra':
    '你可以在钉钉开放平台(https://open-dev.dingtalk.com/)应用详情的「事件与回调」页面获取签名 token。如果你需要开启实时同步，此参数必填。',
  'pages.account.identity_source_detail.basic_config.fei_shu.app_id': '应用 ID',
  'pages.account.identity_source_detail.basic_config.fei_shu.app_id.placeholder': '请输入应用ID',
  'pages.account.identity_source_detail.basic_config.fei_shu.app_id.extra':
    '登录开放中心，点击应用 ，在凭证与基础信息页面查看APP ID。',
  'pages.account.identity_source_detail.basic_config.fei_shu.app_id.rule.0.message':
    '应用ID为必填项',
  'pages.account.identity_source_detail.basic_config.fei_shu.app_secret.extra':
    '登录开放中心，点击应用 ，在凭证与基础信息页面查看 APP Secret。',
  'pages.account.identity_source_detail.basic_config.fei_shu.encrypt_key.extra':
    '飞书事件订阅的 EncryptKey，可以在飞书开放平台应用详情的「事件订阅」页面获取。如果你需要开启实时同步，此参数必填。',
  'pages.account.identity_source_detail.basic_config.fei_shu.verification_token.extra':
    '飞书事件订阅的 VerificationToken，可以在飞书开放平台应用详情的「事件订阅」页面获取。如果你需要开启实时同步，此参数必填。',
  'pages.account.identity_source_detail.basic_config.wechat_work.corp_id': '企业 ID',
  'pages.account.identity_source_detail.basic_config.wechat_work.corp_id.placeholder':
    '请输入企业 ID（CorpId）',
  'pages.account.identity_source_detail.basic_config.wechat_work.corp_id.extra':
    '你可以在企业微信后台（https://work.weixin.qq.com/）「我的企业」-「企业信息」页面获取企业 ID（CorpId）。',
  'pages.account.identity_source_detail.basic_config.wechat_work.corp_id.rule.0.message':
    '企业 ID（CorpId）为必填项',
  'pages.account.identity_source_detail.basic_config.wechat_work.secret': '密钥 Secret',
  'pages.account.identity_source_detail.basic_config.wechat_work.secret.placeholder':
    '请输入企业微信通讯录密钥 Secret',
  'pages.account.identity_source_detail.basic_config.wechat_work.secret.rule.0.message':
    '企业微信通讯录密钥 Secret为必填项',
  'pages.account.identity_source_detail.basic_config.wechat_work.token': '事件回调 Token',
  'pages.account.identity_source_detail.basic_config.wechat_work.token.placeholder':
    '请输入企业微信回调 Token',
  'pages.account.identity_source_detail.basic_config.wechat_work.token.extra':
    '你可以在企业微信后台（https://work.weixin.qq.com/）「管理工具」-「通讯录同步」页面点击「设置接收事件服务器」按钮，之后可以获取到此 Token。如果你需要开启实时同步，此参数必填。',
  'pages.account.identity_source_detail.basic_config.wechat_work.encoding_aes_key.placeholder':
    '请输入通讯录事件同步 EncodingAESKey',
  'pages.account.identity_source_detail.basic_config.wechat_work.encoding_aes_key.extra':
    '你可以在企业微信后台（https://work.weixin.qq.com/）「管理工具」-「通讯录同步」页面点击「设置接收事件服务器」按钮，之后可以获取到此 EncodingAESKey。如果你需要开启实时同步，此参数必填。',
};
