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
export interface GeoLocation {
  ip: string;
  countryName: string;
  countryCode: string;
  provinceName: string;
  provinceCode: string;
  cityName: string;
  cityCode: string;
}

export interface UserAgent {
  browser: string;
  browserType: string;
  browserMajorVersion: string;
  deviceType: string;
  platform: string;
  platformVersion: string;
}

/**
 * SessionList
 */
export type SessionList = {
  id: string;
  username: string;
  geoLocation: GeoLocation;
  userAgent: UserAgent;
  loginTime: string;
  lastRequest: string;
  sessionId: string;
};
