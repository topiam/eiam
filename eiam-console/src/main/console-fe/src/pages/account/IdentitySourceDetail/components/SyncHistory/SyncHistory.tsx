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
import { executeIdentitySourceSync, getIdentitySourceSyncHistoryList } from '../../service';
import { CloudSyncOutlined, SyncOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ProTable } from '@ant-design/pro-components';
import { Alert, App, Button, Space, Tag } from 'antd';
import { useRef, useState } from 'react';
import SyncRecord from '../SyncRecord';
import { useIntl } from '@umijs/max';
import classnames from 'classnames';
import { createStyles } from 'antd-style';

export const useStyle = createStyles(({ prefixCls }) => {
  const antCls = `.${prefixCls}`;
  return {
    main: {
      [`${antCls}-pro-table-list-toolbar-right`]: {
        flex: 'none',
      },
    },
  };
});

export default (props: { identitySourceId: string }) => {
  const { identitySourceId } = props;
  const actionRef = useRef<ActionType>();
  const [recordOpen, setRecordOpen] = useState<boolean>(false);
  const [syncHistoryId, setSyncHistoryId] = useState<string>();
  const [objectType, setObjectType] = useState<string>();
  const intl = useIntl();
  const { styles } = useStyle();
  const { message, modal } = App.useApp();
  const columns: ProColumns<AccountAPI.ListIdentitySourceSyncHistory>[] = [
    {
      title: intl.formatMessage({
        id: 'pages.account.identity_source_detail.sync_history.columns.batch',
      }),
      fixed: 'left',
      width: 100,
      dataIndex: 'batch',
      align: 'center',
      search: false,
    },
    {
      title: intl.formatMessage({
        id: 'pages.account.identity_source_detail.sync_history.columns.trigger_type',
      }),
      dataIndex: 'triggerType',
      ellipsis: true,
      filterSearch: true,
      align: 'center',
      valueType: 'select',
      valueEnum: {
        manual: {
          text: intl.formatMessage({
            id: 'pages.account.identity_source_detail.sync_history.columns.value_enum.value_enum.manual',
          }),
        },
        job: {
          text: intl.formatMessage({
            id: 'pages.account.identity_source_detail.sync_history.columns.value_enum.value_enum.job',
          }),
        },
      },
    },
    {
      title: intl.formatMessage({ id: 'pages.account.identity_source_detail.common.object_type' }),
      dataIndex: 'objectType',
      align: 'center',
      valueType: 'select',
      ellipsis: true,
      filterSearch: true,
      valueEnum: {
        user: {
          text: intl.formatMessage({
            id: 'pages.account.identity_source_detail.sync_history.columns.object_type.value_enum.user',
          }),
        },
        organization: {
          text: intl.formatMessage({
            id: 'pages.account.identity_source_detail.sync_history.columns.object_type.value_enum.organization',
          }),
        },
      },
    },
    {
      title: intl.formatMessage({
        id: 'pages.account.identity_source_detail.sync_history.columns.created_count',
      }),
      dataIndex: 'createdCount',
      search: false,
    },
    {
      title: intl.formatMessage({
        id: 'pages.account.identity_source_detail.sync_history.columns.updated_count',
      }),
      dataIndex: 'updatedCount',
      search: false,
    },
    {
      title: intl.formatMessage({
        id: 'pages.account.identity_source_detail.sync_history.columns.deleted_count',
      }),
      dataIndex: 'deletedCount',
      search: false,
    },
    {
      title: intl.formatMessage({
        id: 'pages.account.identity_source_detail.sync_history.columns.skipped_count',
      }),
      dataIndex: 'skippedCount',
      search: false,
    },
    {
      title: intl.formatMessage({
        id: 'pages.account.identity_source_detail.sync_history.columns.start_time',
      }),
      dataIndex: 'startTime',
      ellipsis: true,
      search: false,
      align: 'center',
      valueType: 'dateTime',
    },
    {
      title: intl.formatMessage({
        id: 'pages.account.identity_source_detail.sync_history.columns.end_time',
      }),
      dataIndex: 'endTime',
      ellipsis: true,
      search: false,
      align: 'center',
      valueType: 'dateTime',
    },
    {
      title: intl.formatMessage({
        id: 'pages.account.identity_source_detail.sync_history.columns.spend_time',
      }),
      dataIndex: 'spendTime',
      valueType: 'second',
      search: false,
    },
    {
      title: intl.formatMessage({
        id: 'pages.account.identity_source_detail.sync_history.columns.status',
      }),
      dataIndex: 'status',
      filterSearch: true,
      valueType: 'select',
      width: 100,
      valueEnum: {
        success: { text: intl.formatMessage({ id: 'app.success' }) },
        fail: { text: intl.formatMessage({ id: 'app.fail' }) },
        pending: {
          text: intl.formatMessage({
            id: 'pages.account.identity_source_detail.sync_history.columns.status.valu_enum.pending',
          }),
        },
      },
      renderText: (text: any) => (
        <Space>
          {text === 'success' && (
            <Tag color="#87d068">{intl.formatMessage({ id: 'app.success' })}</Tag>
          )}
          {text === 'fail' && <Tag color="#e54545">{intl.formatMessage({ id: 'app.fail' })}</Tag>}
          {text === 'pending' && (
            <Tag icon={<SyncOutlined spin />} color="#1677ff">
              {intl.formatMessage({
                id: 'pages.account.identity_source_detail.sync_history.columns.status.valu_enum.pending',
              })}
            </Tag>
          )}
        </Space>
      ),
    },
    {
      title: intl.formatMessage({
        id: 'pages.account.identity_source_detail.sync_history.columns.option',
      }),
      align: 'center',
      valueType: 'option',
      width: 100,
      fixed: 'right',
      render: (_, row) => {
        return [
          <a
            target="_blank"
            key="details"
            onClick={() => {
              setSyncHistoryId(row.id);
              setRecordOpen(true);
              setObjectType(row.objectType);
            }}
          >
            {intl.formatMessage({ id: 'app.detail' })}
          </a>,
        ];
      },
    },
  ];
  return (
    <>
      <ProTable<AccountAPI.ListIdentitySourceSyncHistory>
        actionRef={actionRef}
        columns={columns}
        className={classnames(styles.main)}
        params={{ identitySourceId }}
        request={getIdentitySourceSyncHistoryList}
        rowKey={'id'}
        pagination={{ defaultPageSize: 10, showQuickJumper: true }}
        scroll={{ x: 1300 }}
        search={{ filterType: 'light' }}
        toolBarRender={() => [
          <Button
            icon={<CloudSyncOutlined />}
            type={'primary'}
            key={'pull'}
            onClick={() => {
              modal.info({
                title: intl.formatMessage({
                  id: 'pages.account.identity_source_detail.sync_history.tool_bar_render.title',
                }),
                width: 500,
                onOk: async () => {
                  const { success } = await executeIdentitySourceSync(identitySourceId);
                  if (success) {
                    message.success(
                      intl.formatMessage({
                        id: 'pages.account.identity_source_detail.sync_history.tool_bar_render.success.title',
                      }),
                    );
                    window.setInterval(function () {
                      actionRef.current?.reload();
                    }, 5000);
                  }
                },
                content: (
                  <>
                    <Space direction={'vertical'}>
                      <span>
                        {intl.formatMessage({
                          id: 'pages.account.identity_source_detail.sync_history.tool_bar_render.content.title',
                        })}
                      </span>
                      <Alert
                        type={'info'}
                        banner
                        showIcon={false}
                        description={intl.formatMessage({
                          id: 'pages.account.identity_source_detail.sync_history.tool_bar_render.content.description',
                        })}
                      />
                    </Space>
                  </>
                ),
                okText: intl.formatMessage({ id: 'app.confirm' }),
                okType: 'primary',
                centered: false,
                maskClosable: false,
                okCancel: true,
              });
            }}
          >
            {intl.formatMessage({
              id: 'pages.account.identity_source_detail.sync_history.tool_bar_render.title',
            })}
          </Button>,
        ]}
      />
      {/*同步记录*/}
      {syncHistoryId && objectType && (
        <SyncRecord
          open={recordOpen}
          syncHistoryId={syncHistoryId}
          objectType={objectType}
          onClose={() => {
            setRecordOpen(false);
          }}
        />
      )}
    </>
  );
};
