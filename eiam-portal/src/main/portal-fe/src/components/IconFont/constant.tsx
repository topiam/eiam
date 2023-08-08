/*
 * eiam-portal - Employee Identity and Access Management
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
import IconFont from '@/components/IconFont';
import type { ReactNode } from 'react';

/**
 * ICON
 */
export const ICON_LIST = {
  password: <IconFont name="icon-password" />,
  ldap: <IconFont name="icon-Ldap" />,
  windows_ad: <IconFont name="icon-windows_ad" />,
  dingtalk: <IconFont name="icon-dingtalk" />,
  feishu: <IconFont name="icon-feishu" />,
  wechat_work: <IconFont name="icon-qiyeweixin" />,
  wechat_qr: <IconFont name="icon-weixin" />,
  dingtalk_qr: <IconFont name="icon-dingtalk" />,
  dingtalk_oauth: <IconFont name="icon-dingtalk" />,
  wechatwork_qr: <IconFont name="icon-qiyeweixin" />,
  feishu_oauth: <IconFont name="icon-feishu" />,
  qq_oauth: <IconFont name="icon-qq" />,
  weibo_oauth: <IconFont name="icon-weibo" />,
  github_oauth: <IconFont name="icon-github" />,
  google_oauth: <IconFont name="icon-google" />,
  alipay_oauth: <IconFont name="icon-alipay" />,
  app_cas: <IconFont name="icon-cas" />,
  app_jwt: <IconFont name="icon-jwt" />,
  oidc: <IconFont name="icon-openid" />,
  app_form: <IconFont name="icon-form" />,
  saml2: <IconFont name="icon-saml" />,
  aliyun_saml2_user: <IconFont name="icon-aliyun" />,
  aliyun_saml2_role: <IconFont name="icon-aliyun" />,
  cloudtencent_saml2_role: <IconFont name="icon-tencent-cloud" />,
  cloudtencent_saml2_user: <IconFont name="icon-tencent-cloud" />,
  huaweicloud_saml2: <IconFont name="icon-huawei" />,
  baiducloud_saml2_user: <IconFont name="icon-baidu-cloud" />,
  baiducloud_saml2_role: <IconFont name="icon-baidu-cloud" />,
  app_jenkins_saml2: <IconFont name="icon-jenkins" />,
  app_rancher_saml2: <IconFont name="icon-rancher" />,
  app_kibana_saml2: <IconFont name="icon-kibana" />,
  app_jumpserver_saml2: <IconFont name="icon-jumpserver" />,
  app_kubesphere_oidc: <IconFont name="icon-kubesphere" />,
  app_aws_saml2: <IconFont name="icon-aws" />,
} as Record<string, ReactNode>;
