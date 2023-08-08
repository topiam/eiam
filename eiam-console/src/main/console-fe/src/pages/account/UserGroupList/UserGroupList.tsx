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
import { createUserGroup, getUserGroupList, removeUserGroup } from '@/services/account';
import { history } from '@@/core/history';
import { PlusOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import type { ActionType } from '@ant-design/pro-components';
import {
  PageContainer,
  ProCard,
  ProFormText,
  ProList,
  QueryFilter,
} from '@ant-design/pro-components';
import { App, Avatar, Button, Card, Popconfirm, Typography } from 'antd';
import React, { useRef, useState } from 'react';
import AddUserGroup from './components/AddUserGroup';
import useStyle from './style';
import { useIntl } from '@umijs/max';

const { Paragraph } = Typography;

const UserGroupList = () => {
  const intl = useIntl();
  const { styles } = useStyle();
  const { message } = App.useApp();
  const actionRef = useRef<ActionType>();
  const [addUserGroupVisible, setAddUserGroupVisible] = useState<boolean>(false);
  const [searchParams, setSearchParams] = useState<Record<string, any>>();

  /**
   * onDetailClick
   * @param id
   */
  const onDetailClick = (id: string) => {
    history.push(`/account/user-group/detail?id=${id}&type=member`);
  };
  return (
    <div className={styles.main}>
      <PageContainer content={intl.formatMessage({ id: 'pages.account.user_group_list.desc' })}>
        <ProCard bodyStyle={{ padding: 0 }}>
          <QueryFilter
            layout="horizontal"
            onFinish={(values) => {
              setSearchParams({ ...searchParams, ...values });
              actionRef.current?.reset?.();
              return Promise.resolve();
            }}
          >
            <ProFormText
              name="name"
              label={intl.formatMessage({ id: 'pages.account.user_group_list.metas.title' })}
            />
          </QueryFilter>
        </ProCard>
        <br />
        <ProList<AccountAPI.ListUserGroup>
          actionRef={actionRef}
          showActions="always"
          search={false}
          params={{ ...searchParams }}
          pagination={{
            size: 'small',
            defaultPageSize: 10,
            showSizeChanger: false,
          }}
          rowKey="id"
          toolBarRender={() => {
            return [
              <Button
                key={'create'}
                type={'primary'}
                onClick={() => {
                  setAddUserGroupVisible(true);
                }}
              >
                <PlusOutlined />
                {intl.formatMessage({
                  id: 'pages.account.user_group_list.tool_bar_render.button',
                })}
              </Button>,
            ];
          }}
          grid={{ gutter: 8, xs: 1, sm: 1, md: 3, lg: 3, xl: 4, xxl: 5 }}
          headerTitle={intl.formatMessage({ id: 'pages.account.user_group_list' })}
          form={{
            // 由于配置了 transform，提交的参与与定义的不同这里需要转化一下
            syncToUrl: (values, type) => {
              if (type === 'get') {
                return {
                  ...values,
                };
              }
              return values;
            },
          }}
          renderItem={(item) => {
            return (
              <Card
                hoverable
                style={{ margin: 10 }}
                actions={[
                  <a
                    key={'member'}
                    className={'user-group-detail'}
                    onClick={() => {
                      onDetailClick(item.id);
                    }}
                  >
                    {intl.formatMessage({ id: 'app.detail' })}
                  </a>,
                  // 删除用户组
                  <Popconfirm
                    key={'delete'}
                    title={intl.formatMessage({
                      id: 'pages.account.user_group_list.actions.popconfirm.title',
                    })}
                    placement="bottomRight"
                    icon={
                      <QuestionCircleOutlined
                        style={{
                          color: 'red',
                        }}
                      />
                    }
                    onConfirm={async () => {
                      let success: boolean;
                      const result = await removeUserGroup(item.id);
                      success = result.success;
                      if (success) {
                        message.success(intl.formatMessage({ id: 'app.operation_success' }));
                        actionRef.current?.reload();
                        return;
                      }
                    }}
                    okText={intl.formatMessage({ id: 'app.yes' })}
                    cancelText={intl.formatMessage({ id: 'app.no' })}
                  >
                    <a
                      target="_blank"
                      key="remove"
                      style={{
                        color: 'red',
                      }}
                    >
                      {intl.formatMessage({ id: 'app.delete' })}
                    </a>
                  </Popconfirm>,
                ]}
              >
                <Card.Meta
                  avatar={<Avatar>{item.name.substring(0, 1).toLocaleUpperCase()}</Avatar>}
                  title={item.name}
                  description={
                    <Paragraph
                      className={'user-group-remark'}
                      ellipsis={{ tooltip: item.remark }}
                      title={item.remark}
                    >
                      {item.remark ? item.remark : <>&nbsp;</>}
                    </Paragraph>
                  }
                />
              </Card>
            );
          }}
          request={(params, sort, filter) => {
            return getUserGroupList(params, sort, filter);
          }}
        />
        {/*新增用户组*/}
        <AddUserGroup
          visible={addUserGroupVisible}
          onFinish={async (values: AccountAPI.BaseUserGroup) => {
            try {
              let success = false;
              const result = await createUserGroup(values);
              success = result.success;
              actionRef.current?.reload();
              if (success) {
                message.success(intl.formatMessage({ id: 'app.operation_success' }));
                setAddUserGroupVisible(false);
                return true;
              }
              return false;
            } catch (e) {
              return false;
            }
          }}
          onCancel={() => {
            setAddUserGroupVisible(false);
          }}
        />
      </PageContainer>
    </div>
  );
};
export default () => {
  return <UserGroupList />;
};
