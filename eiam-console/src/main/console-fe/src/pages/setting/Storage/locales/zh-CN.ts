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
  'pages.setting.storage': '文件存储',
  'pages.setting.storage.des': '配置云存储服务，如阿里云、腾讯云、MinIO等。',
  'pages.setting.storage_provider': '存储提供商',
  'pages.setting.storage_provider.form.switch.content':
    '关闭此功能将无法使用存储服务，请确认是否关闭。',
  'pages.setting.storage_provider.provider': '存储服务商',
  'pages.setting.storage_provider.provider.aliyun_oss': '阿里云OSS',
  'pages.setting.storage_provider.provider.aliyun_oss.domain': '绑定域名',
  'pages.setting.storage_provider.provider.aliyun_oss.domain.placeholder':
    '请输入阿里云OSS绑定域名',
  'pages.setting.storage_provider.provider.aliyun_oss.domain.rule.0.message':
    '阿里云OSS绑定域名为必填项',
  'pages.setting.storage_provider.provider.aliyun_oss.access_key_id.placeholder':
    '请输入阿里云OSS AccessKeyId',
  'pages.setting.storage_provider.provider.aliyun_oss.access_key_id.rule.0.message':
    '阿里云OSS AccessKeyId为必填项',
  'pages.setting.storage_provider.provider.aliyun_oss.access_key_secret.placeholder':
    '请输入阿里云OSS AccessKeySecret',
  'pages.setting.storage_provider.provider.aliyun_oss.access_key_secret.rule.0.message':
    '阿里云OSS AccessKeySecret为必填项',
  'pages.setting.storage_provider.provider.aliyun_oss.endpoint.placeholder':
    '请输入阿里云OSS Endpoint',
  'pages.setting.storage_provider.provider.aliyun_oss.endpoint.rule.0.message':
    '阿里云OSS Endpoint为必填项',
  'pages.setting.storage_provider.provider.aliyun_oss.bucket.placeholder': '请输入阿里云OSS Bucket',
  'pages.setting.storage_provider.provider.aliyun_oss.bucket.rule.0.message':
    '阿里云OSS Bucket为必填项',
  'pages.setting.storage_provider.provider.tencent_cos': '腾讯云COS',
  'pages.setting.storage_provider.provider.tencent_cos.domain': '绑定域名',
  'pages.setting.storage_provider.provider.tencent_cos.domain.placeholder':
    '请输入腾讯云COS绑定域名',
  'pages.setting.storage_provider.provider.tencent_cos.domain.rule.0.message':
    '腾讯云COS绑定域名为必填项',
  'pages.setting.storage_provider.provider.tencent_cos.app_id.placeholder': '请输入腾讯云COS AppId',
  'pages.setting.storage_provider.provider.tencent_cos.app_id.rule.0.message':
    '腾讯云COS AppId为必填项',
  'pages.setting.storage_provider.provider.tencent_cos.secret_id.placeholder':
    '请输入腾讯云COS SecretId',
  'pages.setting.storage_provider.provider.tencent_cos.secret_id.rule.0.message':
    '腾讯云COS SecretId为必填项',
  'pages.setting.storage_provider.provider.tencent_cos.secret_Key.placeholder':
    '请输入腾讯云COS SecretKey',
  'pages.setting.storage_provider.provider.tencent_cos.secret_Key.rule.0.message':
    '腾讯云COS SecretKey为必填项',
  'pages.setting.storage_provider.provider.tencent_cos.region.placeholder':
    '请输入腾讯云COS Region',
  'pages.setting.storage_provider.provider.tencent_cos.region.rule.0.message':
    '腾讯云COS Region为必填项',
  'pages.setting.storage_provider.provider.tencent_cos.bucket.placeholder':
    '请输入腾讯云COS Bucket',
  'pages.setting.storage_provider.provider.tencent_cos.bucket.rule.0.message':
    '腾讯云COS Bucket为必填项',
  'pages.setting.storage_provider.provider.qiniu_kodo': '七牛云Kodo',
  'pages.setting.storage_provider.provider.qiniu_kodo.domain': '绑定域名',
  'pages.setting.storage_provider.provider.qiniu_kodo.domain.placeholder':
    '请输入七牛云Kodo绑定域名',
  'pages.setting.storage_provider.provider.qiniu_kodo.domain.rule.0.message':
    '七牛云Kodo绑定域名为必填项',
  'pages.setting.storage_provider.provider.qiniu_kodo.access_key.placeholder':
    '请输入七牛云Kodo AccessKey',
  'pages.setting.storage_provider.provider.qiniu_kodo.access_key.rule.0.message':
    '七牛云Kodo AccessKey为必填项',
  'pages.setting.storage_provider.provider.qiniu_kodo.secret_key.placeholder':
    '请输入七牛云Kodo SecretKey',
  'pages.setting.storage_provider.provider.qiniu_kodo.secret_key.rule.0.message':
    '七牛云Kodo SecretKey为必填项',
  'pages.setting.storage_provider.provider.qiniu_kodo.bucket.placeholder':
    '请输入七牛云Kodo Bucket',
  'pages.setting.storage_provider.provider.qiniu_kodo.bucket.rule.0.message':
    '七牛云Kodo Bucket为必填项',
  'pages.setting.storage_provider.provider.minio': 'MinIO',
  'pages.setting.storage_provider.minio.access_key.placeholder': '请输入MinIO  AccessKey',
  'pages.setting.storage_provider.minio.access_key.rule.0.message': 'MinIO  AccessKey为必填项',
  'pages.setting.storage_provider.minio.secret_key.placeholder': '请输入MinIO SecretKey',
  'pages.setting.storage_provider.minio.secret_key.rule.0.message': 'MinIO  SecretKey为必填项',
  'pages.setting.storage_provider.minio.domain.placeholder': '请输入MinIO Domain',
  'pages.setting.storage_provider.minio.domain.rule.0.message': 'MinIO  Domain为必填项',
  'pages.setting.storage_provider.minio.endpoint.placeholder': '请输入MinIO Endpoint',
  'pages.setting.storage_provider.minio.endpoint.rule.0.message': 'MinIO  Endpoint为必填项',
  'pages.setting.storage_provider.minio.bucket.placeholder': '请输入MinIO  Bucket',
  'pages.setting.storage_provider.minio.bucket.rule.0.message': 'MinIO  Bucket为必填项',
  'pages.setting.storage_provider.provider.s3': 'S3',
  'pages.setting.storage_provider.provider.s3.endpoint': 'S3 域名',
  'pages.setting.storage_provider.provider.s3.endpoint.placeholder':
    '请输入 S3 域名',
  'pages.setting.storage_provider.provider.qiniu_kodo.endpoint.rule.0.message':
    '七牛云Kodo S3 域名为必填项',
  'pages.setting.storage_provider.provider.s3.domain': '外链域名',
  'pages.setting.storage_provider.provider.s3.domain.placeholder':
    '请输入S3 外链域名',
  'pages.setting.storage_provider.provider.s3.domain.rule.0.message':
    'S3 外链域名为必填项',
  'pages.setting.storage_provider.provider.s3.access_key_id.placeholder':
    '请输入S3 AccessKeyId',
  'pages.setting.storage_provider.provider.s3.access_key_id.rule.0.message':
    'S3 AccessKeyId为必填项',
  'pages.setting.storage_provider.provider.s3.secret_access_key.placeholder':
    '请输入S3 SecretAccessKey',
  'pages.setting.storage_provider.provider.s3.secret_access_key.rule.0.message':
    'S3 SecretAccessKey为必填项',
  'pages.setting.storage_provider.provider.s3.region.placeholder':
    '请输入S3 Region',
  'pages.setting.storage_provider.provider.s3.bucket.placeholder':
    '请输入S3 Bucket',
  'pages.setting.storage_provider.provider.s3.bucket.rule.0.message':
    'S3 Bucket为必填项',
};
