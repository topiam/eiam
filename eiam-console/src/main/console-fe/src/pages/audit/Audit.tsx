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
import { PageContainer } from '@ant-design/pro-components';
import { UserType } from './data.d';
import Admin from './Admin';
import { useState } from 'react';
import User from './User';
import { useIntl } from '@umijs/max';

export default () => {
  const intl = useIntl();
  const [tabActiveKey, setTabActiveKey] = useState<string>(UserType.user);
  return (
    <PageContainer
      tabActiveKey={tabActiveKey}
      onTabChange={(key) => {
        setTabActiveKey(key);
      }}
      tabList={[
        {
          key: UserType.user,
          tab: intl.formatMessage({ id: 'pages.audit.user' }),
        },
        {
          key: UserType.admin,
          tab: intl.formatMessage({ id: 'pages.audit.admin' }),
        },
      ]}
      content={intl.formatMessage({ id: 'pages.audit.desc' })}
    >
      {tabActiveKey === UserType.user && <User />}
      {tabActiveKey === UserType.admin && <Admin />}
    </PageContainer>
  );
};
