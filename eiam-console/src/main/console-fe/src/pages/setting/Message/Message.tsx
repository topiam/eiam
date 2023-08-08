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
import React, { useEffect, useState } from 'react';
import MailProvider from '../Message/components/MailProvider';
import MailTemplate from '../Message/components/MailTemplate';
import SmsProvider from '../Message/components/SmsProvider';
import { TabType } from './constant';
import queryString from 'query-string';
import { useLocation } from '@umijs/max';
import { useIntl } from '@@/exports';

export default (): React.ReactNode => {
  const [activeKey, setActiveKey] = useState<string>();
  const location = useLocation();
  const intl = useIntl();
  useEffect(() => {
    const query = queryString.parse(location.search);
    const { type } = query as {
      type: TabType;
    };
    if (!type) {
      setActiveKey(TabType.mail_template);
      history.replace({
        pathname: location.pathname,
        search: queryString.stringify({ type: TabType.mail_template }),
      });
      return;
    }
    setActiveKey(type);
  }, [activeKey]);
  return (
    <PageContainer
      content={intl.formatMessage({ id: 'pages.setting.message.desc' })}
      tabActiveKey={activeKey}
      onTabChange={(key: string) => {
        setActiveKey(key);
        history.replace({
          pathname: location.pathname,
          search: queryString.stringify({ type: key }),
        });
      }}
      tabList={[
        {
          tab: intl.formatMessage({ id: 'pages.setting.message.sms_template' }),
          key: TabType.mail_template,
        },
        {
          tab: intl.formatMessage({ id: 'pages.setting.message.mail' }),
          key: TabType.mail,
        },
        {
          tab: intl.formatMessage({ id: 'pages.setting.message.sms' }),
          key: TabType.sms,
        },
      ]}
    >
      {/*邮件*/}
      {activeKey === TabType.mail && <MailProvider visible={activeKey === TabType.mail} />}
      {/*邮件模版*/}
      {activeKey === TabType.mail_template && (
        <MailTemplate visible={activeKey === TabType.mail_template} />
      )}
      {/*短信*/}
      {activeKey === TabType.sms && <SmsProvider visible={activeKey === TabType.sms} />}
    </PageContainer>
  );
};
