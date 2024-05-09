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
import type {ActionType} from '@ant-design/pro-components';
import {PageContainer, ProCard, ProFormText, ProList, QueryFilter,} from '@ant-design/pro-components';
import {Avatar, Badge, Card, Typography} from 'antd';
import React, {useRef, useState} from 'react';
import {AppGroupList, AppList} from './data.d';
import {getAppGroupList, queryAppList} from './service';
import useStyle from './style';
import classnames from 'classnames';
import {useAsyncEffect} from 'ahooks';
import {SpinProps} from 'antd/es/spin';
import {useIntl} from '@umijs/max';

const { Paragraph } = Typography;
const prefixCls = 'topiam-app-list';
const all = 'all';
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

const CardList = () => {
  const intl = useIntl();
  const { styles } = useStyle(prefixCls);
  // 当前组
  const [currentGroup, setCurrentGroup] = useState<React.Key>();
  const actionRef = useRef<ActionType>();
  const [searchParams, setSearchParams] = useState<Record<string, any>>();
  const [appGroupList, setAppGroupList] = useState<AppGroupList[]>([]);
  const [loading, setLoading] = useState<boolean | SpinProps | undefined>(false);

  const getItems = () => {
    let data: { key: string; label: React.React.JSX.Element }[] = [
      {
        key: all,
        label: (
          <span>
            {intl.formatMessage({ id: 'pages.application.group_all' })}
            {renderBadge(0, currentGroup === all)}
          </span>
        ),
      },
    ];
    appGroupList.forEach((item) => {
      data.push({
        key: item.id,
        label: (
          <span>
            {item.name}
            {renderBadge(item.appCount, currentGroup === item.id)}
          </span>
        ),
      });
    });
    return data;
  };

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

  useAsyncEffect(async () => {
    setLoading(true);
    const { result, success } = await getAppGroupList().finally(() => {
      setLoading(false);
    });
    if (success && result) {
      setAppGroupList(result);
      setCurrentGroup(all);
      // 手动请求
      actionRef.current?.reload();
    }
  }, []);

  return (
    <div className={styles}>
      <PageContainer className={classnames(`${prefixCls}`)}>
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
          loading={loading}
          onLoadingChange={(loading) => {
            setLoading(loading);
          }}
          manualRequest
          request={queryAppList}
          pagination={{}}
          toolbar={{
            menu: {
              type: 'tab',
              activeKey: currentGroup,
              items: getItems(),
              onChange(key) {
                if (key) {
                  setCurrentGroup(key);
                  if (key === all) {
                    setSearchParams((values) => {
                      return { ...values, groupId: undefined };
                    });
                  } else {
                    setSearchParams((values) => {
                      return { ...values, groupId: key };
                    });
                  }
                  actionRef.current?.reload();
                }
              },
            },
          }}
          params={searchParams}
          actionRef={actionRef}
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
                    initSso(item.initLoginUrl);
                    return;
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
          tableExtraRender={() => {
            return (
              <ProCard bodyStyle={{ padding: 0 }}>
                <QueryFilter
                  layout="horizontal"
                  onFinish={(values) => {
                    setSearchParams({ ...searchParams, ...values });
                    actionRef.current?.reload();
                    return Promise.resolve();
                  }}
                  onReset={() => {
                    if (currentGroup && currentGroup !== all) {
                      setSearchParams({ groupId: currentGroup });
                    } else {
                      setSearchParams({});
                    }
                    actionRef.current?.reload();
                  }}
                >
                  <ProFormText
                    name="name"
                    label={intl.formatMessage({ id: 'pages.application.search.name' })}
                  />
                </QueryFilter>
              </ProCard>
            );
          }}
        />
      </PageContainer>
    </div>
  );
};

export default CardList;
