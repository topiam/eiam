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
import { LOGIN_PATH } from '@/utils/utils';
import { history } from '@@/core/history';
import { useIntl, useLocation, useModel } from '@umijs/max';
import { useMount } from 'ahooks';
import { App } from 'antd';
import queryString from 'query-string';

export default () => {
  const { setInitialState } = useModel('@@initialState');
  const location = useLocation();
  const intl = useIntl();
  const { modal } = App.useApp();
  useMount(async () => {
    modal.warning({
      title: intl.formatMessage({ id: 'pages.session-expired.title' }),
      content: intl.formatMessage({ id: 'pages.session-expired.content' }),
      okText: intl.formatMessage({ id: 'pages.session-expired.okText' }),
      okType: 'danger',
      centered: false,
      maskClosable: false,
      okCancel: false,
      onOk: async () => {
        await setInitialState((s: any) => ({ ...s, currentUser: undefined }));
        const query = queryString.parse(location.search);
        const { redirect_uri } = query as { redirect_uri: string };
        let settings: Record<string, string> = { pathname: LOGIN_PATH };
        const domain: string[] | string = redirect_uri && redirect_uri.split('/');
        if (redirect_uri && redirect_uri !== domain[0] + '//' + domain[2] + '/') {
          settings = {
            ...settings,
            search: queryString.stringify({
              redirect_uri: redirect_uri,
            }),
          };
        }
        const href = history.createHref(settings);
        window.location.replace(href);
      },
    });
  });
  return <></>;
};
