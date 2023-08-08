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
import { history } from '@@/core/history';

import { PageContainer } from '@ant-design/pro-components';
import { useMount } from 'ahooks';
import { App } from 'antd';
import { useState } from 'react';
import LoginAudit from './components/LoginAudit';
import UserInfo from './components/UserInfo';
import { UserDetailTabs } from './constant';
import queryString from 'query-string';
import { useLocation } from '@umijs/max';
import { useIntl } from '@@/exports';

export default () => {
  const location = useLocation();
  const intl = useIntl();
  const useApp = App.useApp();
  const query = queryString.parse(location.search);
  const { id } = query as { id: string };
  const { type } = query as {
    type: UserDetailTabs;
  };
  const [tabActiveKey, setTabActiveKey] = useState<string>();
  useMount(() => {
    if (!id) {
      useApp.message
        .warning(intl.formatMessage({ id: 'pages.account.user_detail.user_info.not_selected' }))
        .then();
      history.push(`/account/user`);
      return;
    }
    if (!type) {
      setTabActiveKey(UserDetailTabs.user_info);
      history.replace({
        pathname: location.pathname,
        search: queryString.stringify({ type: UserDetailTabs.user_info, id: id }),
      });
      return;
    }
    setTabActiveKey(type);
  });

  return (
    <PageContainer
      onBack={() => {
        history.push('/account/user');
      }}
      tabList={[
        {
          key: UserDetailTabs.user_info,
          tab: '用户信息',
        },
        {
          key: UserDetailTabs.login_audit,
          tab: '登录日志',
        },
      ]}
      tabActiveKey={tabActiveKey}
      onTabChange={(key: string) => {
        setTabActiveKey(key);
        history.replace({
          pathname: location.pathname,
          search: queryString.stringify({ type: key, id: id }),
        });
      }}
    >
      {/*用户信息*/}
      {UserDetailTabs.user_info === tabActiveKey && <UserInfo userId={id} />}
      {/*登录日志*/}
      {UserDetailTabs.login_audit === tabActiveKey && <LoginAudit id={id} />}
    </PageContainer>
  );
};
