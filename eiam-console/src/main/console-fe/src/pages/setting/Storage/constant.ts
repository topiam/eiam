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
export enum TabType {
  STORAGE = 'storage',
  LOCATION = 'location',
}

export enum OssProvider {
  ALIYUN_OSS = 'aliyun_oss',
  TENCENT_COS = 'tencent_cos',
  QINIU_KODO = 'qiniu_kodo',
  LOCAL = 'local',
  MINIO = 'minio',
  S3 = 's3',
}
export enum Language {
  ZH = 'zh',
  EN = 'en',
}

export enum Region {
  BEIJING = 'ap-beijing',
  GUANGZHOU = 'ap-guangzhou',
  NANJING = 'ap-nanjing',
}
