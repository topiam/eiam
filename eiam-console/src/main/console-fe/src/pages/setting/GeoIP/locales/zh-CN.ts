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
  'pages.setting.geoip': 'IP地理库',
  'pages.setting.geoip.desc':
    '配置IP地理库根据访问者的IP地址迅速识别出其所在的地理位置。通过识别用户IP地址的地理位置，可以根据地理位置信息制定相应的访问策略，从而提高系统的安全性。例如，您可以设置地区限制，阻止来自特定国家或地区的潜在恶意访问。',
  'pages.setting.geoip.form.content': '关闭此功能将无法使用地理位置服务，请确认是否关闭。',
  'pages.setting.geoip.form_select': '提供商',
  'pages.setting.geoip.form_select.option.maxmind': 'MaxMind',
  'pages.setting.geoip.maxmind.form_text.label': '注册码',
  'pages.setting.geoip.maxmind.form_text.placeholder': '请输入MaxMind注册码',
  'pages.setting.geoip.maxmind.form_text.rule.0.message': 'MaxMind注册码为必填项',
};
