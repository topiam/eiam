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
import {
  createIdentitySource,
  deleteIdentitySource,
  disableIdentitySource,
  enableIdentitySource,
  getIdentityProviderList,
} from './service';
import { history } from '@@/core/history';
import { PlusOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import type { ActionType } from '@ant-design/pro-components';
import { PageContainer, ProList } from '@ant-design/pro-components';
import { App, Avatar, Button, Popconfirm, Tag } from 'antd';
import { useRef, useState } from 'react';
import CreateModal from './components/CreateModal';
import useStyle from './style';
import classnames from 'classnames';
import { ICON_LIST } from '@/components/IconFont/constant';
import { useIntl } from '@umijs/max';

const prefixCls = 'identity-source-list';

/**
 * onDetailClick
 * @param id
 * @param provider
 */
const onDetailClick = (id: string, provider: string) => {
  history.push(`/account/identity-source/detail?id=${id}&type=config&provider=${provider}`);
};
/**
 * 身份源列表
 */
export default () => {
  const [createModalVisible, setCreateModalVisible] = useState<boolean>(false);
  const actionRef = useRef<ActionType>();
  const intl = useIntl();
  const { message, modal } = App.useApp();
  const { styles } = useStyle(prefixCls);
  const ListContent = (data: AccountAPI.ListIdentitySource) => (
    <div className={classnames(`${prefixCls}-content`)}>
      <div className={classnames(`${prefixCls}-item-content`)}>
        {data.enabled ? (
          <Tag color="#5BD8A6">{intl.formatMessage({ id: 'app.enabled' })}</Tag>
        ) : (
          <Tag color="#e54545">{intl.formatMessage({ id: 'app.not_enabled' })}</Tag>
        )}
      </div>
    </div>
  );
  return (
    <div className={styles.main}>
      <PageContainer
        content={intl.formatMessage({ id: 'pages.account.identity_source_list.desc' })}
        className={`${prefixCls}`}
      >
        <ProList<AccountAPI.ListIdentitySource>
          toolBarRender={() => [
            <Button
              key="add"
              type="primary"
              onClick={() => {
                setCreateModalVisible(true);
              }}
            >
              <PlusOutlined />
              {intl.formatMessage({
                id: 'pages.account.identity_source_list.tool_bar_render.button.add',
              })}
            </Button>,
          ]}
          search={{}}
          rowKey="id"
          headerTitle={intl.formatMessage({ id: 'pages.account.identity_source_list' })}
          actionRef={actionRef}
          split
          pagination={{ showQuickJumper: false, defaultPageSize: 5, size: 'small' }}
          showActions="always"
          request={getIdentityProviderList}
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
          metas={{
            title: {
              title: intl.formatMessage({ id: 'pages.account.identity_source_list.metas.title' }),
              dataIndex: 'name',
              render: (_, row) => {
                return (
                  <span
                    onClick={() => {
                      onDetailClick(row.id, row.provider);
                    }}
                  >
                    {row.name}
                  </span>
                );
              },
            },
            avatar: {
              search: false,
              render: (text, row) => {
                return (
                  <Avatar key={row.id} shape="square" size={50} src={ICON_LIST[row.provider]} />
                );
              },
            },
            description: {
              search: false,
              render: (_, row) => {
                return row.remark ? <span>{row.remark}</span> : <span>{row.desc}</span>;
              },
            },
            content: {
              search: false,
              render: (text, row) => [<ListContent key="context" {...row} />],
            },
            actions: {
              render: (text, row) => [
                <>
                  {row.enabled ? (
                    <Popconfirm
                      title={intl.formatMessage({
                        id: 'pages.account.identity_source_list.metas.actions.popconfirm.title.disable',
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
                        const { success, result } = await disableIdentitySource(row.id);
                        if (success && result) {
                          message.success(intl.formatMessage({ id: 'app.operation_success' }));
                          await actionRef.current?.reload();
                          return;
                        }
                      }}
                      okText={intl.formatMessage({ id: 'app.yes' })}
                      cancelText={intl.formatMessage({ id: 'app.no' })}
                      key="disabled"
                    >
                      <a key="disabled">{intl.formatMessage({ id: 'app.disable' })}</a>
                    </Popconfirm>
                  ) : (
                    <Popconfirm
                      title={intl.formatMessage({
                        id: 'pages.account.identity_source_list.metas.actions.popconfirm.title.enable',
                      })}
                      placement="bottomRight"
                      icon={<QuestionCircleOutlined />}
                      onConfirm={async () => {
                        const { success, result } = await enableIdentitySource(row.id);
                        if (success && result) {
                          message
                            .success(intl.formatMessage({ id: 'app.operation_success' }))
                            .then();
                          await actionRef.current?.reload();
                          return;
                        }
                      }}
                      okText={intl.formatMessage({ id: 'app.yes' })}
                      cancelText={intl.formatMessage({ id: 'app.no' })}
                      key="disabled"
                    >
                      <a key="enabled">{intl.formatMessage({ id: 'app.enable' })}</a>
                    </Popconfirm>
                  )}
                </>,
                <a
                  key={'detail'}
                  onClick={() => {
                    onDetailClick(row.id, row.provider);
                  }}
                >
                  {intl.formatMessage({ id: 'app.detail' })}
                </a>,
                <Popconfirm
                  title={intl.formatMessage({
                    id: 'pages.account.identity_source_list.metas.actions.popconfirm.delete',
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
                    const { success, result } = await deleteIdentitySource(row.id);
                    if (success && result) {
                      message.success(intl.formatMessage({ id: 'app.operation_success' }));
                      await actionRef.current?.reload();
                      return;
                    }
                  }}
                  okText={intl.formatMessage({ id: 'app.yes' })}
                  cancelText={intl.formatMessage({ id: 'app.no' })}
                  key="delete"
                >
                  <a
                    target="_blank"
                    style={{
                      color: 'red',
                    }}
                    key="remove"
                  >
                    {intl.formatMessage({ id: 'app.delete' })}
                  </a>
                </Popconfirm>,
              ],
            },
          }}
        />
        {/*新增身份源*/}
        <CreateModal
          visible={createModalVisible}
          onClose={() => {
            setCreateModalVisible(false);
          }}
          onFinish={async (values: Record<string, any>) => {
            try {
              const { result, success } = await createIdentitySource(values);
              if (success) {
                setCreateModalVisible(false);
                actionRef.current?.reload();
                const successModal = modal.success({
                  title: intl.formatMessage({
                    id: 'pages.account.identity_source_list.create_modal.success.title',
                  }),
                  content: intl.formatMessage({
                    id: 'pages.account.identity_source_list.create_modal.success.content',
                  }),
                  okText: intl.formatMessage({
                    id: 'pages.account.identity_source_list.create_modal.success.ok_text',
                  }),
                  onOk: () => {
                    successModal.destroy();
                    history.push(`/account/identity-source/detail?id=${result.id}`);
                  },
                });
                return true;
              }
              return false;
            } catch (e) {
              return false;
            }
          }}
        />
      </PageContainer>
    </div>
  );
};
