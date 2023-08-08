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
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ProTable } from '@ant-design/pro-components';
import { useRef } from 'react';
import { getIdentitySourceEventRecordList } from '../../service';
import { Tag } from 'antd';
import {
  CheckCircleOutlined,
  CloseCircleOutlined,
  ExclamationCircleOutlined,
} from '@ant-design/icons';
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
  const actionRef = useRef<ActionType>();
  const { identitySourceId } = props;
  const intl = useIntl();
  const { styles } = useStyle();

  const columns: ProColumns<Record<string, string>>[] = [
    {
      title: intl.formatMessage({ id: 'pages.account.identity_source_detail.common.action_type' }),
      dataIndex: 'actionType',
      align: 'center',
      ellipsis: true,
      width: 100,
      valueType: 'select',
      valueEnum: {
        insert: { text: intl.formatMessage({ id: 'app.create' }) },
        update: { text: intl.formatMessage({ id: 'app.update' }) },
        delete: { text: intl.formatMessage({ id: 'app.delete' }) },
      },
    },
    {
      title: intl.formatMessage({ id: 'pages.account.identity_source_detail.common.object_type' }),
      dataIndex: 'objectType',
      align: 'center',
      valueType: 'select',
      width: 100,
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
      title: intl.formatMessage({ id: 'pages.account.identity_source_detail.common.object_id' }),
      dataIndex: 'objectId',
      ellipsis: true,
      search: false,
    },
    {
      title: intl.formatMessage({ id: 'pages.account.identity_source_detail.common.object_name' }),
      dataIndex: 'objectName',
      search: false,
    },
    {
      title: intl.formatMessage({
        id: 'pages.account.identity_source_detail.event_record.columns.event_time',
      }),
      dataIndex: 'eventTime',
      align: 'center',
      search: false,
      ellipsis: true,
      valueType: 'dateTime',
    },
    {
      title: intl.formatMessage({ id: 'pages.account.identity_source_detail.common.desc' }),
      dataIndex: 'desc',
      ellipsis: true,
      search: false,
    },
    {
      title: intl.formatMessage({
        id: 'pages.account.identity_source_detail.event_record.columns.status',
      }),
      dataIndex: 'status',
      align: 'center',
      valueType: 'select',
      width: 80,
      valueEnum: {
        success: { text: intl.formatMessage({ id: 'app.success' }) },
        fail: { text: intl.formatMessage({ id: 'app.fail' }) },
        skip: { text: intl.formatMessage({ id: 'app.skip' }) },
      },
      renderText: (text: any) => (
        <>
          {text === 'success' && (
            <Tag icon={<CheckCircleOutlined />} color="#87d068">
              {intl.formatMessage({ id: 'app.success' })}
            </Tag>
          )}
          {text === 'fail' && (
            <Tag icon={<CloseCircleOutlined />} color="#e54545">
              {intl.formatMessage({ id: 'app.fail' })}
            </Tag>
          )}
          {text === 'skip' && (
            <Tag icon={<ExclamationCircleOutlined />} color="#faad14">
              {intl.formatMessage({ id: 'app.skip' })}
            </Tag>
          )}
        </>
      ),
    },
  ];

  return (
    <ProTable
      actionRef={actionRef}
      params={{ identitySourceId }}
      columns={columns}
      className={classnames(styles.main)}
      rowKey={'id'}
      search={{ filterType: 'light' }}
      scroll={{ x: 900 }}
      pagination={{ defaultPageSize: 10, showQuickJumper: false }}
      request={getIdentitySourceEventRecordList}
      toolBarRender={() => []}
    />
  );
};
