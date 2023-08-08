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
import type { RequestOptions } from '@@/plugin-request/request';
import type { RequestConfig } from '@umijs/max';
import { notification, message as Msg } from 'antd';
import { parse, stringify } from 'querystring';
import { history } from '@@/core/history';

import { isLoginPath, isSessionExpiredPath, SESSION_EXPIRED_PATH } from './utils/utils';

/**
 * @name 错误处理
 * pro 自带的错误处理， 可以在这里做自己的改动
 * @doc https://umijs.org/docs/max/request#配置
 */
export const requestConfig: RequestConfig = {
  withCredentials: true,
  xsrfHeaderName: 'topiam-csrf',
  xsrfCookieName: 'topiam-csrf-cookie',
  // 错误处理： umi@3 的错误处理方案。
  errorConfig: {
    // 错误接收及处理
    errorHandler: (error: any, opts: any) => {
      if (opts?.skipErrorHandler) throw error;
      if (error.response) {
        // Axios 的错误
        // 请求成功发出且服务器也响应了状态码，但状态代码超出了 2xx 的范围
        if (error.response.status === 401) {
          // 排除 登录、登录回调
          if (isLoginPath() || isSessionExpiredPath()) {
            return;
          }
          const query = parse(history.location.search);
          const { redirect_uri } = query as { redirect_uri: string };
          if (!isLoginPath() && !redirect_uri) {
            let settings: Record<string, string> = { pathname: SESSION_EXPIRED_PATH };
            const domain: string[] | string = window.location.href.split('/');
            if (window.location.href !== domain[0] + '//' + domain[2] + '/') {
              settings = {
                ...settings,
                search: stringify({
                  redirect_uri: window.location.href,
                }),
              };
            }
            const href = history.createHref(settings);
            window.location.replace(href);
          }
          return;
        }
        const status = error.response.status;
        if (status === 502 || status === 503 || status === 504) {
          notification.error({
            message: `请求错误`,
            description: `服务暂时不可用，请稍后重试！`,
          });
          return;
        }
        notification.error({
          description: error.response.data.message,
          message: '请求错误',
        });
      } else if (error.request) {
        // 请求已经成功发起，但没有收到响应
        // \`error.request\` 在浏览器中是 XMLHttpRequest 的实例，
        // 而在node.js中是 http.ClientRequest 的实例
        Msg.error('None response! Please retry.').then();
      } else {
        // 发送请求时出了点问题
        Msg.error('Request error, please retry.').then();
      }
    },
  },
  // 请求拦截器
  requestInterceptors: [
    (config: RequestOptions) => {
      // requestType==='form' ，或者requestType 为 undefined 更改 Accept 为 'application/json'
      if (config.requestType === 'form' || !config.requestType) {
        config.headers = { ...config.headers, Accept: 'application/json' };
      }
      return { ...config };
    },
  ],

  // 响应拦截器
  responseInterceptors: [
    (response) => {
      return response;
    },
  ],
};
