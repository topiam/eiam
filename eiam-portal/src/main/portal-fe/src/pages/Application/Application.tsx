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
import { PageContainer, ProCard, ProList } from '@ant-design/pro-components';
import { Alert, App, Avatar, Badge, Card, Input, Typography } from 'antd';
import React, { useRef, useState } from 'react';
import type { AppList } from './data.d';
import { InitLoginType } from './data.d';
import { queryAppList } from './service';
import { useIntl } from '@@/exports';
import useStyle from './style';
import classnames from 'classnames';
const { Paragraph } = Typography;
const prefixCls = 'topiam-app-list';

const CardList = () => {
  const intl = useIntl();
  const { styles } = useStyle(prefixCls);
  const [activeKey, setActiveKey] = useState<React.Key | undefined>('tab1');

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

  const renderBadge = (count: number, active = false) => {
    return (
      <Badge
        count={count}
        style={{
          marginBlockStart: -2,
          marginInlineStart: 4,
          color: active ? '#1890FF' : '#999',
          backgroundColor: active ? '#E6F7FF' : '#eee',
        }}
      />
    );
  };

  return (
    <div className={styles}>
      <PageContainer
        className={classnames(`${prefixCls}`)}
        tabList={[
          {
            tab: '应用列表',
            key: 'list',
          },
          {
            tab: '应用账号',
            key: 'account',
          },
        ]}
      >
        <Alert
          banner
          type={'info'}
          message={intl.formatMessage({ id: 'pages.application.alert' })}
          showIcon
        />
        <br />
        <ProList<AppList>
          rowKey="id"
          split
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
          toolbar={{
            menu: {
              type: 'tab',
              activeKey,
              items: [
                {
                  key: 'tab1',
                  label: <span>开发分组{renderBadge(99, activeKey === 'tab1')}</span>,
                },
                {
                  key: 'tab2',
                  label: <span>运维分组{renderBadge(32, activeKey === 'tab2')}</span>,
                },
              ],
              onChange(key) {
                setActiveKey(key);
              },
            },
          }}
          params={searchParams}
          actionRef={actionRef}
          tableExtraRender={() => {
            return <ProCard>{content}</ProCard>;
          }}
          renderItem={(item: AppList) => {
            return (
              item &&
              item.id && (
                <Card
                  style={{ margin: 8 }}
                  className={`${prefixCls}-item-card`}
                  hoverable
                  bordered={false}
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
                  <div className={`${prefixCls}-item-content-wrapper`} key={item.id}>
                    <div className={`${prefixCls}-item-avatar`}>
                      <Avatar key={item.icon} shape="square" src={item.icon} size={45} />
                    </div>
                    <div className={`${prefixCls}-item-content`}>
                      <span className={`${prefixCls}-item-content-title`}>{item.name}</span>
                      <Paragraph
                        className={`${prefixCls}-item-content-desc`}
                        ellipsis={{ tooltip: item.description, rows: 2 }}
                        title={item.description}
                      >
                        {item.description ? item.description : <>&nbsp;</>}
                      </Paragraph>
                    </div>
                  </div>
                </Card>
              )
            );
          }}
        />
      </PageContainer>
    </div>
  );
};

export default CardList;
