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
import { App, Avatar, Image, Popconfirm, Skeleton } from 'antd';
import {
  ActionType,
  ProCard,
  ProColumns,
  ProDescriptions,
  ProTable,
} from '@ant-design/pro-components';
import React, { useRef, useState } from 'react';
import { useAsyncEffect } from 'ahooks';
import { getUser, getUserIdpBind, unbindIdp, updateUser, userParamCheck } from '@/services/account';
import { QuestionCircleOutlined } from '@ant-design/icons';

import { omit } from 'lodash';
import classNames from 'classnames';
import { ParamCheckType } from '@/constant';
import AccessStrategy from '../AccessStrategy';
import { useIntl } from '@umijs/max';

const prefixCls = 'user-detail-info';

export default (props: { userId: string }) => {
  const { userId } = props;
  const [loading, setLoading] = useState<boolean>();
  const [user, setUser] = useState<AccountAPI.GetUser>();
  const actionRef = useRef<ActionType>();
  const intl = useIntl();
  const { message } = App.useApp();

  useAsyncEffect(async () => {
    setLoading(true);
    const { result, success } = await getUser(userId);
    if (success) {
      setUser(result);
    }
    setLoading(false);
  }, []);

  const columns: ProColumns<AccountAPI.UserIdpBind>[] = [
    {
      title: intl.formatMessage({ id: 'pages.account.user_detail.user_info.columns.open_id' }),
      dataIndex: 'openId',
      ellipsis: true,
      fixed: 'left',
    },
    {
      title: intl.formatMessage({ id: 'pages.account.user_detail.user_info.columns.idp_name' }),
      dataIndex: 'idpName',
      copyable: false,
      search: false,
    },
    {
      title: intl.formatMessage({ id: 'pages.account.user_detail.user_info.columns.date_time' }),
      dataIndex: 'bindTime',
      valueType: 'dateTime',
      copyable: false,
      search: false,
    },
    {
      title: intl.formatMessage({ id: 'pages.account.user_detail.user_info.columns.option' }),
      align: 'center',
      valueType: 'option',
      fixed: 'right',
      width: 100,
      render: (text, row) => {
        return [
          <Popconfirm
            title={intl.formatMessage({
              id: 'pages.account.user_detail.user_info.columns.option.popconfirm.title',
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
              await unbindIdp(row.id);
              actionRef.current?.reload();
            }}
            okText={intl.formatMessage({ id: 'app.yes' })}
            cancelText={intl.formatMessage({ id: 'app.no' })}
            key="delete"
          >
            <a target="_blank" style={{ color: 'red' }} key="remove">
              {intl.formatMessage({
                id: 'pages.account.user_detail.user_info.columns.option.popconfirm.remove',
              })}
            </a>
          </Popconfirm>,
        ];
      },
    },
  ];
  /**
   * onSave
   *
   * @param key
   * @param record
   */
  const onSave = async (
    key: React.Key | React.Key[],
    record: AccountAPI.UpdateUser,
  ): Promise<any | void> => {
    const { success } = await updateUser(
      omit(
        record,
        'avatar',
        'username',
        'externalId',
        'authTotal',
        'lastAuthIp',
        'lastAuthTime',
        'createTime',
        'updateTime',
      ),
    );
    if (success) {
      await getUser(userId).then(({ result }) => {
        setUser(result);
      });
      message.success(intl.formatMessage({ id: 'app.update_success' }));
      return Promise.resolve(true);
    }
    return Promise.resolve(false);
  };

  return (
    <div>
      <ProCard
        style={{ height: '100%' }}
        title={intl.formatMessage({ id: 'pages.account.user_detail.user_info' })}
      >
        <Skeleton loading={loading} active={true} paragraph={{ rows: 8 }}>
          <div className={classNames(`${prefixCls}-descriptions`)}>
            <ProDescriptions<AccountAPI.GetUser>
              size="small"
              column={2}
              dataSource={user}
              editable={{
                onSave,
              }}
            >
              <ProDescriptions.Item
                dataIndex="avatar"
                label={intl.formatMessage({ id: 'pages.account.user_detail.user_info.avatar' })}
                editable={false}
                render={(_, row) => {
                  return row?.avatar ? (
                    <Avatar
                      size={'small'}
                      src={
                        <Image
                          src={row.avatar}
                          fallback="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAMIAAADDCAYAAADQvc6UAAABRWlDQ1BJQ0MgUHJvZmlsZQAAKJFjYGASSSwoyGFhYGDIzSspCnJ3UoiIjFJgf8LAwSDCIMogwMCcmFxc4BgQ4ANUwgCjUcG3awyMIPqyLsis7PPOq3QdDFcvjV3jOD1boQVTPQrgSkktTgbSf4A4LbmgqISBgTEFyFYuLykAsTuAbJEioKOA7DkgdjqEvQHEToKwj4DVhAQ5A9k3gGyB5IxEoBmML4BsnSQk8XQkNtReEOBxcfXxUQg1Mjc0dyHgXNJBSWpFCYh2zi+oLMpMzyhRcASGUqqCZ16yno6CkYGRAQMDKMwhqj/fAIcloxgHQqxAjIHBEugw5sUIsSQpBobtQPdLciLEVJYzMPBHMDBsayhILEqEO4DxG0txmrERhM29nYGBddr//5/DGRjYNRkY/l7////39v///y4Dmn+LgeHANwDrkl1AuO+pmgAAADhlWElmTU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAAqACAAQAAAABAAAAwqADAAQAAAABAAAAwwAAAAD9b/HnAAAHlklEQVR4Ae3dP3PTWBSGcbGzM6GCKqlIBRV0dHRJFarQ0eUT8LH4BnRU0NHR0UEFVdIlFRV7TzRksomPY8uykTk/zewQfKw/9znv4yvJynLv4uLiV2dBoDiBf4qP3/ARuCRABEFAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghgg0Aj8i0JO4OzsrPv69Wv+hi2qPHr0qNvf39+iI97soRIh4f3z58/u7du3SXX7Xt7Z2enevHmzfQe+oSN2apSAPj09TSrb+XKI/f379+08+A0cNRE2ANkupk+ACNPvkSPcAAEibACyXUyfABGm3yNHuAECRNgAZLuYPgEirKlHu7u7XdyytGwHAd8jjNyng4OD7vnz51dbPT8/7z58+NB9+/bt6jU/TI+AGWHEnrx48eJ/EsSmHzx40L18+fLyzxF3ZVMjEyDCiEDjMYZZS5wiPXnyZFbJaxMhQIQRGzHvWR7XCyOCXsOmiDAi1HmPMMQjDpbpEiDCiL358eNHurW/5SnWdIBbXiDCiA38/Pnzrce2YyZ4//59F3ePLNMl4PbpiL2J0L979+7yDtHDhw8vtzzvdGnEXdvUigSIsCLAWavHp/+qM0BcXMd/q25n1vF57TYBp0a3mUzilePj4+7k5KSLb6gt6ydAhPUzXnoPR0dHl79WGTNCfBnn1uvSCJdegQhLI1vvCk+fPu2ePXt2tZOYEV6/fn31dz+shwAR1sP1cqvLntbEN9MxA9xcYjsxS1jWR4AIa2Ibzx0tc44fYX/16lV6NDFLXH+YL32jwiACRBiEbf5KcXoTIsQSpzXx4N28Ja4BQoK7rgXiydbHjx/P25TaQAJEGAguWy0+2Q8PD6/Ki4R8EVl+bzBOnZY95fq9rj9zAkTI2SxdidBHqG9+skdw43borCXO/ZcJdraPWdv22uIEiLA4q7nvvCug8WTqzQveOH26fodo7g6uFe/a17W3+nFBAkRYENRdb1vkkz1CH9cPsVy/jrhr27PqMYvENYNlHAIesRiBYwRy0V+8iXP8+/fvX11Mr7L7ECueb/r48eMqm7FuI2BGWDEG8cm+7G3NEOfmdcTQw4h9/55lhm7DekRYKQPZF2ArbXTAyu4kDYB2YxUzwg0gi/41ztHnfQG26HbGel/crVrm7tNY+/1btkOEAZ2M05r4FB7r9GbAIdxaZYrHdOsgJ/wCEQY0J74TmOKnbxxT9n3FgGGWWsVdowHtjt9Nnvf7yQM2aZU/TIAIAxrw6dOnAWtZZcoEnBpNuTuObWMEiLAx1HY0ZQJEmHJ3HNvGCBBhY6jtaMoEiJB0Z29vL6ls58vxPcO8/zfrdo5qvKO+d3Fx8Wu8zf1dW4p/cPzLly/dtv9Ts/EbcvGAHhHyfBIhZ6NSiIBTo0LNNtScABFyNiqFCBChULMNNSdAhJyNSiECRCjUbEPNCRAhZ6NSiAARCjXbUHMCRMjZqBQiQIRCzTbUnAARcjYqhQgQoVCzDTUnQIScjUohAkQo1GxDzQkQIWejUogAEQo121BzAkTI2agUIkCEQs021JwAEXI2KoUIEKFQsw01J0CEnI1KIQJEKNRsQ80JECFno1KIABEKNdtQcwJEyNmoFCJAhELNNtScABFyNiqFCBChULMNNSdAhJyNSiECRCjUbEPNCRAhZ6NSiAARCjXbUHMCRMjZqBQiQIRCzTbUnAARcjYqhQgQoVCzDTUnQIScjUohAkQo1GxDzQkQIWejUogAEQo121BzAkTI2agUIkCEQs021JwAEXI2KoUIEKFQsw01J0CEnI1KIQJEKNRsQ80JECFno1KIABEKNdtQcwJEyNmoFCJAhELNNtScABFyNiqFCBChULMNNSdAhJyNSiECRCjUbEPNCRAhZ6NSiAARCjXbUHMCRMjZqBQiQIRCzTbUnAARcjYqhQgQoVCzDTUnQIScjUohAkQo1GxDzQkQIWejUogAEQo121BzAkTI2agUIkCEQs021JwAEXI2KoUIEKFQsw01J0CEnI1KIQJEKNRsQ80JECFno1KIABEKNdtQcwJEyNmoFCJAhELNNtScABFyNiqFCBChULMNNSdAhJyNSiEC/wGgKKC4YMA4TAAAAABJRU5ErkJggg=="
                        />
                      }
                    />
                  ) : (
                    <Avatar
                      style={{
                        color: '#f56a00',
                        backgroundColor: '#fde3cf',
                        verticalAlign: 'small',
                      }}
                      size="small"
                    >
                      {row?.fullName?.substring(0, 1).toLocaleUpperCase()}
                    </Avatar>
                  );
                }}
              />

              <ProDescriptions.Item
                dataIndex="username"
                label={intl.formatMessage({ id: 'pages.account.user_detail.user_info.username' })}
                editable={false}
                copyable
              />
              <ProDescriptions.Item
                dataIndex="nickName"
                label={intl.formatMessage({
                  id: 'pages.account.user_detail.user_info.nick_name',
                })}
                formItemProps={{
                  rules: [
                    {
                      required: true,
                      message: intl.formatMessage({
                        id: 'pages.account.user_detail.user_info.nick_name.rule.0.message',
                      }),
                    },
                  ],
                }}
              />
              <ProDescriptions.Item
                dataIndex="fullName"
                label={intl.formatMessage({
                  id: 'pages.account.user_detail.user_info.full_name',
                })}
                formItemProps={{
                  rules: [
                    {
                      required: true,
                      message: intl.formatMessage({
                        id: 'pages.account.user_detail.user_info.full_name.rule.0.message',
                      }),
                    },
                  ],
                }}
              />
              <ProDescriptions.Item
                dataIndex="status"
                label={intl.formatMessage({ id: 'pages.account.user_detail.user_info.status' })}
                valueEnum={{
                  enabled: { text: intl.formatMessage({ id: 'app.enable' }), status: 'Success' },
                  disabled: { text: intl.formatMessage({ id: 'app.disable' }), status: 'Error' },
                  locked: { text: intl.formatMessage({ id: 'app.lock' }), status: 'Warning' },
                  expired_locked: {
                    text: intl.formatMessage({
                      id: 'pages.account.user_detail.user_info.status.value_enum.expired_locked',
                    }),
                    status: 'Warning',
                    disabled: true,
                  },
                  password_expired_locked: {
                    text: intl.formatMessage({
                      id: 'pages.account.user_detail.user_info.status.value_enum.password_expired_locked',
                    }),
                    status: 'Warning',
                    disabled: true,
                  },
                }}
                formItemProps={{
                  rules: [
                    {
                      required: true,
                      message: intl.formatMessage({
                        id: 'pages.account.user_detail.user_info.status.rule.0.message',
                      }),
                    },
                  ],
                }}
              />
              <ProDescriptions.Item
                dataIndex="dataOrigin"
                label={intl.formatMessage({
                  id: 'pages.account.user_detail.user_info.data_origin',
                })}
                valueType={'select'}
                editable={false}
                valueEnum={{
                  input: {
                    text: intl.formatMessage({
                      id: 'pages.account.user_detail.user_info.data_origin.value_enum.input',
                    }),
                  },
                  dingtalk: {
                    text: intl.formatMessage({
                      id: 'pages.account.user_detail.user_info.data_origin.value_enum.dingtalk',
                    }),
                  },
                  wechat: {
                    text: intl.formatMessage({
                      id: 'pages.account.user_detail.user_info.data_origin.value_enum.wechat',
                    }),
                  },
                  feishu: {
                    text: intl.formatMessage({
                      id: 'pages.account.user_detail.user_info.data_origin.value_enum.feishu',
                    }),
                  },
                  ldap: {
                    text: intl.formatMessage({
                      id: 'pages.account.user_detail.user_info.data_origin.value_enum.ldap',
                    }),
                  },
                }}
              />
              <ProDescriptions.Item
                dataIndex="id"
                label={intl.formatMessage({ id: 'pages.account.user_detail.user_info.id' })}
                editable={false}
                copyable
              />
              <ProDescriptions.Item
                dataIndex="externalId"
                label={intl.formatMessage({
                  id: 'pages.account.user_detail.user_info.external_id',
                })}
                editable={false}
                copyable
              />
              <ProDescriptions.Item
                dataIndex="phone"
                label={intl.formatMessage({ id: 'pages.account.user_detail.user_info.phone' })}
                copyable
                formItemProps={{
                  rules: [
                    {
                      pattern: new RegExp(/^1[3-9]\d{9}$/),
                      message: intl.formatMessage({
                        id: 'pages.account.user_detail.user_info.phone.rule.0.message',
                      }),
                    },
                    {
                      validator: async (rule, value) => {
                        if (!value) {
                          return Promise.resolve();
                        }
                        const { success, result } = await userParamCheck(
                          ParamCheckType.PHONE,
                          value,
                          userId,
                        );
                        if (!success) {
                          return Promise.reject<any>();
                        }
                        if (!result) {
                          return Promise.reject<any>(
                            new Error(
                              intl.formatMessage({
                                id: 'pages.account.user_detail.user_info.phone.rule.1.message',
                              }),
                            ),
                          );
                        }
                      },
                      validateTrigger: ['onBlur'],
                    },
                  ],
                }}
              />
              <ProDescriptions.Item
                dataIndex="email"
                label={intl.formatMessage({ id: 'pages.account.user_detail.user_info.email' })}
                copyable
                formItemProps={{
                  rules: [
                    {
                      type: 'email',
                      message: intl.formatMessage({
                        id: 'pages.account.user_detail.user_info.email.rule.0.message',
                      }),
                    },
                    {
                      validator: async (rule, value) => {
                        if (!value) {
                          return Promise.resolve();
                        }
                        const { success, result } = await userParamCheck(
                          ParamCheckType.EMAIL,
                          value,
                          userId,
                        );
                        if (success && !result) {
                          return Promise.reject<any>(
                            new Error(
                              intl.formatMessage({
                                id: 'pages.account.user_detail.user_info.email.rule.1.message',
                              }),
                            ),
                          );
                        }
                      },
                      validateTrigger: ['onBlur'],
                    },
                  ],
                }}
              />
              <ProDescriptions.Item
                dataIndex="authTotal"
                label={intl.formatMessage({
                  id: 'pages.account.user_detail.user_info.auth_total',
                })}
                editable={false}
              />
              <ProDescriptions.Item
                dataIndex="lastAuthIp"
                label={intl.formatMessage({
                  id: 'pages.account.user_detail.user_info.last_auth_ip',
                })}
                editable={false}
              />
              <ProDescriptions.Item
                dataIndex="lastAuthTime"
                label={intl.formatMessage({
                  id: 'pages.account.user_detail.user_info.last_auth_time',
                })}
                valueType={'dateTime'}
                editable={false}
              />
              <ProDescriptions.Item
                dataIndex="expireDate"
                label={intl.formatMessage({
                  id: 'pages.account.user_detail.user_info.expire_date',
                })}
                valueType={'date'}
                formItemProps={{
                  rules: [
                    {
                      required: true,
                      message: intl.formatMessage({
                        id: 'pages.account.user_detail.user_info.expire_date.rule.0.message',
                      }),
                    },
                  ],
                }}
              />
              <ProDescriptions.Item
                label={intl.formatMessage({
                  id: 'pages.account.user_detail.user_info.create_time',
                })}
                dataIndex="createTime"
                editable={false}
                valueType={'dateTime'}
              />
              <ProDescriptions.Item
                label={intl.formatMessage({
                  id: 'pages.account.user_detail.user_info.update_time',
                })}
                editable={false}
                dataIndex="updateTime"
                valueType={'dateTime'}
              />
              <ProDescriptions.Item
                label={intl.formatMessage({ id: 'pages.account.user_detail.user_info.remark' })}
                dataIndex="remark"
                valueType={'textarea'}
                fieldProps={{
                  placeholder: intl.formatMessage({
                    id: 'pages.account.user_detail.user_info.remark',
                  }),
                  rows: 2,
                  autoComplete: 'off',
                  showCount: true,
                  maxLength: 20,
                }}
              />
            </ProDescriptions>
          </div>
        </Skeleton>
      </ProCard>
      <br />
      <ProCard
        style={{ height: '100%' }}
        title={intl.formatMessage({ id: 'pages.account.user_detail.user_info.account_bound' })}
        bodyStyle={{ padding: 20 }}
      >
        <ProTable
          rowKey={'id'}
          cardProps={{
            bodyStyle: { margin: 0, padding: 0 },
          }}
          search={false}
          actionRef={actionRef}
          params={{ userId }}
          pagination={false}
          columns={columns}
          request={getUserIdpBind}
          toolbar={{ settings: [] }}
        />
      </ProCard>
      <br />
      <AccessStrategy userId={userId} />
    </div>
  );
};
