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
export type AppList = {
  id: string;
  type: string;
  protocol: string;
  template: string;
  icon?: string;
  name: string;
  initLoginType: InitLoginType;
  initLoginUrl: string;
  description: string;
};

export enum InitLoginType {
  /**
   * 仅允许应用发起 SSO
   */
  ONLY_APP_INIT_SSO = 'only_app_init_sso',
  /**
   * 门户或应用发起 SSO
   */
  PORTAL_OR_APP_INIT_SSO = 'portal_or_app_init_sso',
}
