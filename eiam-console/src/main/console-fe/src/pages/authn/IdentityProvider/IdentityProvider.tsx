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
import { ICON_LIST } from '@/components/IconFont/constant';
import {
  createIdentityProvider,
  disableIdentityProvider,
  enableIdentityProvider,
  getIdentityProvider,
  getIdpList,
  removeIdentityProvider,
  updateIdentityProvider,
} from './service';
import { history } from '@@/core/history';
import { PlusOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import type { ActionType } from '@ant-design/pro-components';
import { PageContainer, ProList } from '@ant-design/pro-components';
import { useMount } from 'ahooks';
import { App, Avatar, Button, Popconfirm, Tag, Typography } from 'antd';
import { Fragment, useRef, useState } from 'react';
import CreateDrawer from './components/CreateModal';
import UpdateDrawer from './components/UpdateModal';
import { EXIST_CALLBACK, IdentityProviderCategory } from './constant';
import useStyle from './style';
import classnames from 'classnames';
import queryString from 'query-string';
import { useIntl, useLocation } from '@umijs/max';
import { ListIdentityProvider } from './data.d';

const prefixCls = 'identity_provider-list';

const { Paragraph } = Typography;
/**
 * List
 *
 * @param props
 * @constructor
 */
const List = (props: { category: IdentityProviderCategory }) => {
  const { category } = props;
  const actionRef = useRef<ActionType>();
  const intl = useIntl();
  const { message, modal } = App.useApp();
  const [id, setId] = useState<string>();

  /**创建*/
  const [createDrawerVisible, setCreateDrawerVisible] = useState<boolean>(false);
  /**更新*/
  const [updateDrawerVisible, setUpdateDrawerVisible] = useState<boolean>(false);
  /**
   * 创建提交
   *
   * @param values
   */
  const onCreateFinish = async (values: Record<string, any>) => {
    try {
      const { success, result } = await createIdentityProvider(values);
      if (result && success) {
        //查询 callbackUrl，后端为什么不直接返回呢？解耦，因为有部分认证源是不需要回调地址的
        if (result.id && EXIST_CALLBACK.includes(result.type)) {
          const sourceResult = await getIdentityProvider(result.id);
          if (sourceResult.success && sourceResult.result) {
            const successModal = modal.success({
              title: intl.formatMessage({ id: 'app.add_success' }),
              content: (
                <>
                  {intl.formatMessage({
                    id: 'pages.authn.identity_provider.add-success-content',
                  })}
                  <Paragraph copyable={{ text: sourceResult.result.redirectUri }}>
                    <a href="javascript:void(0);">{sourceResult.result.redirectUri}</a>
                  </Paragraph>
                </>
              ),
              okText: intl.formatMessage({ id: 'app.confirm' }),
              onOk: () => {
                successModal.destroy();
              },
            });
          }
        } else {
          message.success(intl.formatMessage({ id: 'app.add_success' }));
        }
        setCreateDrawerVisible(false);
        actionRef.current?.reload();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  };

  /**
   * 更新提交
   *
   * @param values
   */
  const onUpdateFinish = async (values: Record<any, any>) => {
    try {
      const result = await updateIdentityProvider({ ...values });
      if (result?.result && result?.success) {
        message.success(intl.formatMessage({ id: 'app.operation_success' })).then();
        setUpdateDrawerVisible(false);
        actionRef.current?.reload();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  };

  /**
   * ListContent
   *
   * @param data
   * @constructor
   */
  const ListContent = (data: ListIdentityProvider) => (
    <div
      style={{
        flex: 1,
        display: 'flex',
        justifyContent: 'flex-end',
      }}
    >
      <div className={classnames(`${prefixCls}-content`)}>
        <div className={classnames(`${prefixCls}-item-content`)}>
          {data.enabled ? (
            <Tag color="#5BD8A6">
              {intl.formatMessage({ id: 'pages.authn.identity_provider.enabled' })}
            </Tag>
          ) : (
            <Tag color="#e54545">
              {intl.formatMessage({ id: 'pages.authn.identity_provider.not_enabled' })}
            </Tag>
          )}
        </div>
      </div>
    </div>
  );

  return (
    <div className={classnames(`${prefixCls}`)}>
      <ProList<ListIdentityProvider>
        actionRef={actionRef}
        rowKey="id"
        split
        pagination={{ showQuickJumper: false, defaultPageSize: 5, size: 'small' }}
        showActions="always"
        headerTitle={intl.formatMessage({
          id: 'pages.authn.identity_provider.header_title',
        })}
        toolBarRender={() => {
          return [
            <Button
              key={'create'}
              icon={<PlusOutlined />}
              type={'primary'}
              onClick={() => {
                setCreateDrawerVisible(true);
              }}
            >
              {intl.formatMessage({ id: 'pages.authn.identity_provider.add_button' })}
            </Button>,
          ];
        }}
        request={getIdpList}
        params={{ category: category }}
        search={{}}
        metas={{
          title: {
            dataIndex: 'name',
            title: intl.formatMessage({
              id: 'pages.authn.identity_provider.metas_title',
            }),
            render: (_, row) => {
              return (
                <span
                  onClick={() => {
                    setUpdateDrawerVisible(true);
                    setId(row.id);
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
              return <Avatar key={row.id} shape="square" size={50} src={ICON_LIST[row.type]} />;
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
            render: (text, row) => <ListContent key="context" {...row} />,
          },
          actions: {
            render: (text, row) => [
              <Fragment key={'status'}>
                {row.enabled ? (
                  <Popconfirm
                    title={intl.formatMessage({
                      id: 'pages.authn.identity_provider.disable_confirm',
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
                      const { success, result } = await disableIdentityProvider(row.id);
                      if (success && result) {
                        message.success(intl.formatMessage({ id: 'app.operation_success' }));
                        actionRef.current?.reload();
                        return;
                      }
                    }}
                    okText={intl.formatMessage({ id: 'app.yes' })}
                    cancelText={intl.formatMessage({ id: 'app.no' })}
                    key="disabled"
                  >
                    <a>
                      {intl.formatMessage({
                        id: 'app.disable',
                      })}
                    </a>
                  </Popconfirm>
                ) : (
                  <Popconfirm
                    title={intl.formatMessage({
                      id: 'pages.authn.identity_provider.enable_confirm',
                    })}
                    placement="bottomRight"
                    icon={<QuestionCircleOutlined />}
                    onConfirm={async () => {
                      const { success, result } = await enableIdentityProvider(row.id);
                      if (success && result) {
                        message.success(intl.formatMessage({ id: 'app.operation_success' }));
                        actionRef.current?.reload();
                        return;
                      }
                    }}
                    okText={intl.formatMessage({ id: 'app.yes' })}
                    cancelText={intl.formatMessage({ id: 'app.no' })}
                    key="enabled"
                  >
                    <a>
                      {intl.formatMessage({
                        id: 'app.enable',
                      })}
                    </a>
                  </Popconfirm>
                )}
              </Fragment>,
              <a
                key="update"
                onClick={() => {
                  setId(row.id);
                  setUpdateDrawerVisible(true);
                }}
              >
                {intl.formatMessage({
                  id: 'pages.authn.identity_provider.metas_config',
                })}
              </a>,
              <Popconfirm
                title={intl.formatMessage({
                  id: 'pages.authn.identity_provider.delete_confirm',
                })}
                key={'remove'}
                placement="bottomRight"
                icon={
                  <QuestionCircleOutlined
                    style={{
                      color: 'red',
                    }}
                  />
                }
                onConfirm={async () => {
                  const { success, result } = await removeIdentityProvider(row.id);
                  if (success && result) {
                    message.success(intl.formatMessage({ id: 'app.operation_success' }));
                    await actionRef.current?.reload();
                    return;
                  }
                }}
                okText={intl.formatMessage({ id: 'app.yes' })}
                cancelText={intl.formatMessage({ id: 'app.no' })}
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
      {/**创建*/}
      <CreateDrawer
        category={category}
        visible={createDrawerVisible}
        onCancel={() => {
          setCreateDrawerVisible(false);
        }}
        onFinish={onCreateFinish}
      />
      {/**更新*/}
      {id && (
        <UpdateDrawer
          visible={updateDrawerVisible}
          id={id}
          onFinish={onUpdateFinish}
          onCancel={() => {
            setUpdateDrawerVisible(false);
          }}
        />
      )}
    </div>
  );
};

const IdentityProvider = () => {
  const intl = useIntl();
  const location = useLocation();
  const [tabActiveKey, setTabActiveKey] = useState<string>();
  const query = queryString.parse(location.search);
  const { type } = query as { type: IdentityProviderCategory };
  const { styles } = useStyle(prefixCls);

  useMount(async () => {
    if (!type || !(type.toUpperCase() in IdentityProviderCategory)) {
      setTabActiveKey(IdentityProviderCategory.social);
      history.push({
        pathname: location.pathname,
        search: queryString.stringify({ type: IdentityProviderCategory.social }),
      });
      return;
    }
    setTabActiveKey(type);
  });

  return (
    <div className={styles.main}>
      <PageContainer
        content={intl.formatMessage({ id: 'pages.authn.identity_provider.desc' })}
        tabActiveKey={tabActiveKey}
        onTabChange={(key: string) => {
          setTabActiveKey(key);
          history.replace({
            pathname: location.pathname,
            search: queryString.stringify({ type: key }),
          });
        }}
        tabProps={{ size: 'small' }}
        className={classnames(`${prefixCls}`)}
        tabList={[
          {
            tab: intl.formatMessage({ id: 'pages.authn.identity_provider.social' }),
            key: IdentityProviderCategory.social,
          },
          {
            tab: intl.formatMessage({ id: 'pages.authn.identity_provider.enterprise' }),
            key: IdentityProviderCategory.enterprise,
          },
        ]}
      >
        {IdentityProviderCategory.social === tabActiveKey && (
          <List category={IdentityProviderCategory.social} />
        )}
        {IdentityProviderCategory.enterprise === tabActiveKey && (
          <List category={IdentityProviderCategory.enterprise} />
        )}
      </PageContainer>
    </div>
  );
};
export default IdentityProvider;
