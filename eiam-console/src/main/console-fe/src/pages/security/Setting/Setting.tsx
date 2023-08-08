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
import React, { useState } from 'react';
import { useIntl } from '@umijs/max';
import { TabType } from './constant';
import DefensePolicy from './components/DefensePolicy';
import Basic from './components/Basic';

export default () => {
  const [tabActiveKey, setTabActiveKey] = useState<string>(TabType.basic);
  const intl = useIntl();

  return (
    <PageContainer
      tabActiveKey={tabActiveKey}
      onTabChange={(key) => {
        setTabActiveKey(key);
      }}
      tabList={[
        {
          key: TabType.basic,
          tab: intl.formatMessage({ id: 'pages.setting.basic_setting' }),
        },
        {
          key: TabType.defense_policy,
          tab: intl.formatMessage({ id: 'pages.setting.security.defense_policy' }),
        },
      ]}
    >
      {tabActiveKey === TabType.basic && <Basic />}
      {tabActiveKey === TabType.defense_policy && <DefensePolicy />}
    </PageContainer>
  );
};
