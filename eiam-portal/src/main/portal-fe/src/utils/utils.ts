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
import { getEncryptSecret } from '@/services';
import type { SortOrder } from 'antd/es/table/interface';
import { parse } from 'querystring';
import type { ReactText } from 'react';
import { history, matchPath } from '@umijs/max';
import { PhoneNumber } from 'google-libphonenumber';

export const getPageQuery = () => {
  const { href } = window.location;
  const qsIndex = href.indexOf('?');
  const sharpIndex = href.indexOf('#');

  if (qsIndex !== -1) {
    if (qsIndex > sharpIndex) {
      return parse(href.split('?')[1]);
    }

    return parse(href.slice(qsIndex + 1, sharpIndex));
  }

  return {};
};
/**
 * 获取cookie
 * @param name
 */
export const getCookie = (name: string): string => {
  const match = document.cookie.match(new RegExp(`(^|;\\s*)(${name})=([^;]*)`));
  return <string>(match ? decodeURIComponent(match[3]) : null);
};

/**
 * 下载
 * @param blobData Blob
 * @param forDownLoadFileName 文件名称
 */
export const download = (blobData: Blob, forDownLoadFileName: string) => {
  const aLink = document.createElement('a');
  document.body.appendChild(aLink);
  aLink.style.display = 'none';
  aLink.href = window.URL.createObjectURL(blobData);
  const filename = forDownLoadFileName.split('filename=')[1].split('.');
  aLink.setAttribute('download', `${decodeURI(filename[0])}.${filename[1]}`);
  aLink.click();
  document.body.removeChild(aLink);
};

/**
 * jsonToUrlParams
 * @param params
 */
export const jsonToUrlParams = (params: Record<string, any>) => {
  return Object.keys(params)
    .map((key) => {
      return `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`;
    })
    .join('&');
};

/**
 * 排序参数
 *
 * @param value
 */
export const sortParamConverter = (value: Record<string, SortOrder> | undefined) => {
  const param: Record<string, any> = {};
  if (value)
    Object.entries(value).forEach(([key, sort], index) => {
      param[`sorts[${index}].sorter`] = key;
      param[`sorts[${index}].asc`] = sort === 'ascend';
    });
  return param;
};

/**
 * 过滤参数
 *
 * @param value
 */
export const filterParamConverter = (value: Record<string, ReactText[] | null> | undefined) => {
  const param: Record<string, any> = {};
  if (value)
    Object.entries(value).forEach(([key], index) => {
      param[`filters[${index}].sorter`] = key;
    });
  return param;
};

export const isEmptyOrSpaces = (str: any) => {
  return (
    str === undefined || str === null || false || str.match(/^ *$/) !== null || str === 'undefined'
  );
};

/**
 * 匹配登录地址
 */
export const LOGIN_PATH = '/login';

/**
 * 会话过期路径
 */
export const SESSION_EXPIRED_PATH = '/session-expired';

/**
 * 是否是会话过期路径
 */
export const isSessionExpiredPath = () => {
  return matchPath(
    {
      path: SESSION_EXPIRED_PATH,
    },
    history.location.pathname,
  );
};

/**
 * 是否是登录路径
 */
export const isLoginPath = () => {
  return matchPath(
    {
      path: LOGIN_PATH,
    },
    history.location.pathname,
  );
};

/**
 * 获取加密公钥
 */
export const onGetEncryptSecret = async (): Promise<undefined | string> => {
  const { success, result } = await getEncryptSecret();
  if (success && result) {
    return Promise.resolve(result.secret);
  }
  return Promise.resolve(undefined);
};

/**
 * 手机号是有效号码
 *
 * @param phone
 * @param phoneAreaCode
 */
export function phoneIsValidNumber(phone: string, phoneAreaCode: string): Promise<boolean> {
  const phoneNumberUtil = require('google-libphonenumber').PhoneNumberUtil.getInstance();
  const phoneNumber: PhoneNumber = phoneNumberUtil?.parseAndKeepRawInput(
    phone,
    phoneNumberUtil.getRegionCodeForCountryCode(Number(phoneAreaCode.replace('+', ''))),
  );
  if (!phoneNumberUtil.isValidNumber(phoneNumber)) {
    return Promise.resolve(false);
  }
  return Promise.resolve(true);
}

/**
 * phoneParseNumber
 *
 * @param phone
 */
export function phoneParseNumber(phone: string): PhoneNumber {
  const phoneNumberUtil = require('google-libphonenumber').PhoneNumberUtil.getInstance();
  return phoneNumberUtil?.parse(phone);
}

export function emailValidator(value: string) {
  //校验是否为邮箱
  const emailReg = new RegExp(/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/);
  return emailReg.test(value);
}
