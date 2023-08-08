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
import type { SortOrder } from 'antd/es/table/interface';
import yaml from 'js-yaml';
import { parse } from 'querystring';
import type { ReactText } from 'react';
import { history, matchPath } from '@umijs/max';
import YAML from 'yaml';
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
 * 生成随机字符串
 * @param len
 */
export const random = (len: number) => {
  const count = Math.ceil(Number(len) / 10) + 1;
  let ret = '';
  for (let i = 0; i < count; i += 1) {
    ret += Math.random().toString(36).substring(2);
  }
  return ret.substring(0, len);
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

/**
 *
 * @param pasLen 密码的长度
 */
export const getPasswords = (pasLen: number) => {
  const pasArr = [
    'a',
    'b',
    'c',
    'd',
    'e',
    'f',
    'g',
    'h',
    'i',
    'j',
    'k',
    'l',
    'm',
    'n',
    'o',
    'p',
    'q',
    'r',
    's',
    't',
    'u',
    'v',
    'w',
    'x',
    'y',
    'z',
    'A',
    'B',
    'C',
    'D',
    'E',
    'F',
    'G',
    'H',
    'I',
    'J',
    'K',
    'L',
    'M',
    'N',
    'O',
    'P',
    'Q',
    'R',
    'S',
    'T',
    'U',
    'V',
    'W',
    'X',
    'Y',
    'Z',
    '0',
    '1',
    '2',
    '3',
    '4',
    '5',
    '6',
    '7',
    '8',
    '9',
    '_',
    '-',
    '%',
    '@',
    '+',
    '!',
  ];
  let password = '';
  const pasArrLen = pasArr.length;
  for (let i = 0; i < pasLen; i++) {
    const x = Math.floor(Math.random() * pasArrLen);
    password += pasArr[x];
  }
  return password;
};

/**
 * Transform json string to yaml string
 * @param jsonStr
 */
export const json2yaml = (jsonStr: string): { data: string; error: boolean } => {
  try {
    return {
      data: yaml.dump(JSON.parse(jsonStr)),
      error: false,
    };
  } catch (err) {
    return {
      data: '',
      error: true,
    };
  }
};

/**
 * Transform yaml string to json
 * @param yamlStr
 * @param returnString true for json string , false for json object
 */
export const yaml2json = (
  yamlStr: string,
  returnString: boolean,
): { data: any; error: boolean } => {
  try {
    return {
      data: returnString ? JSON.stringify(YAML.parse(yamlStr)) : YAML.parse(yamlStr),
      error: false,
    };
  } catch (err) {
    return {
      data: '',
      error: true,
    };
  }
};

export const isEmptyOrSpaces = (str: string) => {
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

export async function copy(text: string): Promise<boolean> {
  // 动态创建 textarea 标签
  const textarea = document.createElement('textarea');
  // 将该 textarea 设为 readonly 防止 iOS 下自动唤起键盘，同时将 textarea 移出可视区域
  textarea.readOnly = Boolean('readonly');
  textarea.style.position = 'absolute';
  textarea.style.left = '-9999px';
  // 将要 copy 的值赋给 textarea 标签的 value 属性
  // 网上有些例子是赋值给innerText,这样也会赋值成功，但是识别不了\r\n的换行符，赋值给value属性就可以
  textarea.value = text;
  // 将 textarea 插入到 body 中
  document.body.appendChild(textarea);
  // 选中值并复制
  textarea.select();
  textarea.setSelectionRange(0, textarea.value.length);
  document.execCommand('Copy');
  document.body.removeChild(textarea);
  return true;
}

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

export function removeDuplicate(arr: string[]) {
  const newArr: string[] = [];
  arr.forEach((item) => {
    if (!newArr.includes(item)) {
      newArr.push(item);
    }
  });
  return newArr;
}

function formatTime(hours: number, minutes: number, seconds: number) {
  return (
    ('0' + hours).slice(-2) + ':' + ('0' + minutes).slice(-2) + ':' + ('0' + seconds).slice(-2)
  );
}

export function getTimeFromDateTime(dateTimeString: string) {
  const timeRegex = /(\d{2}):(\d{2}):(\d{2})/; // 匹配 "02:04:00" 格式的时间部分
  const dateTimeRegex = /(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2}):(\d{2})/; // 匹配 "2023-06-05 00:03:00" 格式的时间部分

  const timeMatch = dateTimeString.match(timeRegex);
  const dateTimeMatch = dateTimeString.match(dateTimeRegex);

  if (timeMatch) {
    // 格式为 "02:04:00"
    const hours = parseInt(timeMatch[1], 10);
    const minutes = parseInt(timeMatch[2], 10);
    const seconds = parseInt(timeMatch[3], 10);
    return formatTime(hours, minutes, seconds);
  } else if (dateTimeMatch) {
    // 格式为 "2023-06-05 00:03:00"
    const hours = parseInt(dateTimeMatch[4], 10);
    const minutes = parseInt(dateTimeMatch[5], 10);
    const seconds = parseInt(dateTimeMatch[6], 10);
    return formatTime(hours, minutes, seconds);
  } else {
    return null; // 无法解析时间部分，返回 null 或其他适当的值
  }
}

/**
 * 生成UUID
 */
export function generateUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    const r = (Math.random() * 16) | 0,
      v = c === 'x' ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
}
