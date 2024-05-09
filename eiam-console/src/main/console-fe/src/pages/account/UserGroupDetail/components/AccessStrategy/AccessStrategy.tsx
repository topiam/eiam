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
import { AccessPolicyType } from '@/constant';
import { getAppAccessPolicyList, removeAppAccessPolicy } from '@/services/app';
import type { ActionType } from '@ant-design/pro-components';
import { ProList } from '@ant-design/pro-components';
import { App, Avatar, Card, Tooltip } from 'antd';
import React, { useRef } from 'react';
import classNames from 'classnames';
import useStyle from './style';
import { useIntl } from '@umijs/max';

const prefixCls = 'user-group-app-access-strategy';

export default (props: { userGroupId: string }) => {
  const { userGroupId } = props;
  const intl = useIntl();
  const actionRef = useRef<ActionType>();
  const { styles } = useStyle(prefixCls);
  const { message, modal } = App.useApp();

  return (
    <div className={styles.main}>
      <ProList<AppAPI.AppAccessPolicyList>
        pagination={{
          defaultPageSize: 10,
          showSizeChanger: false,
        }}
        actionRef={actionRef}
        grid={{
          xs: 1,
          sm: 2,
          md: 3,
          lg: 3,
          xl: 4,
          xxl: 5,
        }}
        headerTitle={intl.formatMessage({
          id: 'pages.account.user_group_detail.access_strategy',
        })}
        request={getAppAccessPolicyList}
        params={{ subjectId: userGroupId, subjectType: AccessPolicyType.USER_GROUP }}
        rowKey={'id'}
        renderItem={(row) => {
          return (
            <Card hoverable styles={{ body: { padding: 0 } }} style={{ margin: 10 }}>
              <div className={classNames(`${prefixCls}-content`)} key={row.id}>
                <div className={classNames(`${prefixCls}-content-status`)} />
                <div className={classNames(`${prefixCls}-content-title`)}>
                  <div className={classNames(`${prefixCls}-content-title-avatar`)}>
                    <Avatar src={row.appIcon} />
                  </div>
                  <Tooltip title={row.appName}>
                    <div className={classNames(`${prefixCls}-content-title-text`)}>
                      {row.appName}
                    </div>
                  </Tooltip>
                </div>
                <div className={classNames(`${prefixCls}-content-operate`)}>
                  <a
                    target="_blank"
                    key="remove"
                    style={{
                      color: 'red',
                    }}
                    onClick={() => {
                      const confirmed = modal.error({
                        title: intl.formatMessage({
                          id: 'pages.account.user_group_detail.access_strategy.remove_title',
                        }),
                        content: intl.formatMessage({
                          id: 'pages.account.user_group_detail.access_strategy.remove_content',
                        }),
                        okText: intl.formatMessage({ id: 'app.confirm' }),
                        centered: true,
                        okType: 'primary',
                        okCancel: true,
                        cancelText: intl.formatMessage({ id: 'app.cancel' }),
                        onOk: async () => {
                          const { success } = await removeAppAccessPolicy(row.id);
                          if (success) {
                            confirmed.destroy();
                            message.success(intl.formatMessage({ id: 'app.operation_success' }));
                            actionRef.current?.reload();
                            return;
                          }
                        },
                      });
                    }}
                  >
                    {intl.formatMessage({
                      id: 'pages.account.user_detail.access_strategy.remove',
                    })}
                  </a>
                </div>
              </div>
            </Card>
          );
        }}
      />
    </div>
  );
};
