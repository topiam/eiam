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
import { getIdentitySourceSyncRecordList } from '../../service';
import {
  CheckCircleOutlined,
  CloseCircleOutlined,
  ExclamationCircleOutlined,
} from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ProTable } from '@ant-design/pro-components';
import { Drawer, Tag } from 'antd';
import React, { useRef } from 'react';
import { useIntl } from '@umijs/max';
import { createStyles } from 'antd-style';

interface SyncRecordProps {
  syncHistoryId: string;
  open: boolean;
  onClose: (e: React.MouseEvent | React.KeyboardEvent) => void;
  objectType: string;
}

const useStyle = createStyles(({ prefixCls }) => {
  const antCls = `.${prefixCls}`;

  return {
    main: {
      [`${antCls}-pro-card ${antCls}-pro-card-body`]: {
        padding: '24px 0px 0px',
      },
    },
  };
});

export default (props: SyncRecordProps) => {
  const { syncHistoryId, open, onClose, objectType } = props;
  const actionRef = useRef<ActionType>();
  const intl = useIntl();

  const columns: ProColumns<AccountAPI.ListIdentitySourceSyncRecord>[] = [
    {
      title: intl.formatMessage({ id: 'pages.account.identity_source_detail.common.action_type' }),
      dataIndex: 'actionType',
      align: 'center',
      width: 80,
      ellipsis: true,
      valueType: 'select',
      valueEnum: {
        insert: { text: intl.formatMessage({ id: 'app.create' }) },
        update: { text: intl.formatMessage({ id: 'app.update' }) },
        delete: { text: intl.formatMessage({ id: 'app.delete' }) },
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
      ellipsis: true,
      search: false,
    },
    {
      title: intl.formatMessage({ id: 'pages.account.identity_source_detail.common.desc' }),
      dataIndex: 'desc',
      ellipsis: true,
      search: false,
    },
    {
      title: intl.formatMessage({
        id: 'pages.account.identity_source_detail.sync_record.columns.status',
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

  const { styles } = useStyle();

  return (
    <Drawer
      open={open}
      title={intl.formatMessage({
        id: 'pages.account.identity_source_detail.sync_record.drawer.title',
      })}
      onClose={onClose}
      width={630}
      destroyOnClose
      bodyStyle={{ paddingTop: 0 }}
    >
      <ProTable<AccountAPI.ListIdentitySourceSyncRecord>
        actionRef={actionRef}
        className={styles.main}
        columns={columns}
        search={{ filterType: 'light' }}
        params={{ syncHistoryId, objectType }}
        request={getIdentitySourceSyncRecordList}
        rowKey={'id'}
        pagination={{ defaultPageSize: 10, showQuickJumper: false }}
      />
    </Drawer>
  );
};
