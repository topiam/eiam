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
import type { ActionType } from '@ant-design/pro-components';
import { PageContainer, ProList } from '@ant-design/pro-components';
import { Alert, Avatar, Card, Input, App, Typography } from 'antd';
import { useRef, useState } from 'react';
import type { AppList } from './data.d';
import { InitLoginType } from './data.d';
import { queryAppList } from './service';
import { useIntl } from '@@/exports';

const { Paragraph } = Typography;

const CardList = () => {
  const intl = useIntl();
  const { message } = App.useApp();
  const actionRef = useRef<ActionType>();
  const [searchParams, setSearchParams] = useState<{ name: string }>();

  const content = (
    <div style={{ textAlign: 'center' }}>
      <Input.Search
        placeholder={intl.formatMessage({ id: 'pages.application.search' })}
        enterButton={intl.formatMessage({ id: 'pages.application.search.enter_button' })}
        size="large"
        style={{ maxWidth: 522, width: '100%' }}
        onSearch={(value) => {
          setSearchParams({ name: value });
          actionRef.current?.reload();
        }}
      />
    </div>
  );
  const initSso = (idpInitUrl: string) => {
    const div = window.document.createElement('div');
    div.innerHTML =
      "<form action='" +
      idpInitUrl +
      "' method='POST' name='auto_submit_form' style='display:none'>";
    document.body.appendChild(div);
    // eslint-disable-next-line @typescript-eslint/dot-notation
    document.forms['auto_submit_form'].setAttribute('target', '_blank');
    // eslint-disable-next-line @typescript-eslint/dot-notation
    document.forms['auto_submit_form'].submit();
    document.body.removeChild(div);
  };

  return (
    <PageContainer content={content}>
      <Alert message={intl.formatMessage({ id: 'pages.application.alert' })} showIcon />
      <br />
      <ProList<AppList>
        rowKey="id"
        ghost
        grid={{
          xs: 1,
          sm: 2,
          md: 2,
          lg: 3,
          xl: 4,
          xxl: 5,
        }}
        request={queryAppList}
        pagination={{}}
        params={searchParams}
        actionRef={actionRef}
        renderItem={(item: AppList) => {
          return (
            item &&
            item.id && (
              <Card
                style={{ margin: 5 }}
                hoverable
                onClick={async () => {
                  if (item.initLoginType === InitLoginType.PORTAL_OR_APP_INIT_SSO) {
                    initSso(item.initLoginUrl);
                    return;
                  }
                  message.warning(
                    `${item.name}${intl.formatMessage({ id: 'pages.application.init.warning' })}`,
                  );
                }}
              >
                <Card.Meta
                  avatar={<Avatar key={item.id} shape="square" size={50} src={item.icon} />}
                  title={item.name}
                  description={
                    <Paragraph ellipsis={{ rows: 2, tooltip: true }}>{item.description}</Paragraph>
                  }
                />
              </Card>
            )
          );
        }}
      />
    </PageContainer>
  );
};

export default CardList;
