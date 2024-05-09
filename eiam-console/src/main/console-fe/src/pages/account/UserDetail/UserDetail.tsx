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
import { App, Button } from 'antd';
import React, { useState } from 'react';
import LoginAudit from './components/LoginAudit';
import UserInfo from './components/UserInfo';
import { UserDetailTabs } from './constant';
import queryString from 'query-string';
import { useLocation } from '@umijs/max';
import { useIntl } from '@@/exports';
import { ExclamationCircleFilled } from '@ant-design/icons';
import { removeUser } from '@/services/account';

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

  const goUserList = () => {
    history.push(`/account/user`);
  };

  useMount(() => {
    if (!id) {
      useApp.message
        .warning(intl.formatMessage({ id: 'pages.account.user_detail.user_info.not_selected' }))
        .then();
      goUserList();
      return;
    }
    if (!type || !UserDetailTabs[type]) {
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
        goUserList();
      }}
      tabList={[
        {
          key: UserDetailTabs.user_info,
          tab: intl.formatMessage({ id: 'pages.account.user_detail.tabs.user_info' }),
        },
        {
          key: UserDetailTabs.login_audit,
          tab: intl.formatMessage({ id: 'pages.account.user_detail.tabs.login_audit' }),
        },
      ]}
      extra={[
        <Button
          key="delete"
          type="primary"
          danger
          onClick={() => {
            const confirmed = useApp.modal.error({
              centered: true,
              title: intl.formatMessage({
                id: 'pages.account.user_detail.extra.delete.confirm_title',
              }),
              icon: <ExclamationCircleFilled />,
              content: intl.formatMessage({
                id: 'pages.account.user_detail.extra.delete.confirm_content',
              }),
              okText: intl.formatMessage({ id: 'app.confirm' }),
              okType: 'danger',
              okCancel: true,
              cancelText: intl.formatMessage({ id: 'app.cancel' }),
              onOk: async () => {
                const { success } = await removeUser(id);
                if (success) {
                  useApp.message.success(intl.formatMessage({ id: 'app.operation_success' }));
                  confirmed.destroy();
                  goUserList();
                }
              },
            });
          }}
        >
          {intl.formatMessage({ id: 'pages.account.user_detail.extra.delete' })}
        </Button>,
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
      {UserDetailTabs.login_audit === tabActiveKey && <LoginAudit userId={id} />}
    </PageContainer>
  );
};
