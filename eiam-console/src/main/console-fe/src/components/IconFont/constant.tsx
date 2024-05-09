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
import IconFont from '@/components/IconFont';
import type { ReactNode } from 'react';

/**
 * ICON
 */
export const ICON_LIST = {
  password: <IconFont name="icon-password" />,
  dingtalk: <IconFont name="icon-dingtalk" />,
  feishu: <IconFont name="icon-feishu" />,
  wechat_work: <IconFont name="icon-qiyeweixin" />,
  wechat_oauth: <IconFont name="icon-weixin" />,
  dingtalk_oauth: <IconFont name="icon-dingtalk" />,
  wechatwork_oauth: <IconFont name="icon-qiyeweixin" />,
  feishu_oauth: <IconFont name="icon-feishu" />,
  qq_oauth: <IconFont name="icon-qq" />,
  gitee_oauth: <IconFont name="icon-gitee" />,
  weibo_oauth: <IconFont name="icon-weibo" />,
  github_oauth: <IconFont name="icon-github" />,
  google_oauth: <IconFont name="icon-google" />,
  alipay_oauth: <IconFont name="icon-alipay" />,
  app_jwt: <IconFont name="icon-jwt" />,
  oidc: <IconFont name="icon-openid" />,
  app_form: <IconFont name="icon-form" />,
} as Record<string, ReactNode>;
