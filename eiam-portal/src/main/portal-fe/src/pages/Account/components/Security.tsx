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
import { useModel } from '@umijs/max';
import { useAsyncEffect } from 'ahooks';
import { List, Skeleton } from 'antd';
import { useState } from 'react';
import ModifyEmail from './ModifyEmail';
import ModifyPassword from './ModifyPassword';
import ModifyPhone from './ModifyPhone';
import classnames from 'classnames';
import { useStyle as useAntdStyle } from '@ant-design/pro-components';

type Unpacked<T> = T extends (infer U)[] ? U : T;

function useStyle(prefixCls: string) {
  return useAntdStyle('AccountSecurityComponent', (token) => {
    return [
      {
        [`.${prefixCls}`]: {
          '&-strong': {
            color: `${token.colorSuccess}`,
          },
          '&-medium': {
            color: `${token.colorWarning}`,
          },
          '&-weak': {
            color: `${token.colorError}`,
          },
        },
      },
    ];
  });
}
const SecurityView = () => {
  const prefixCls = 'account-security';
  const { wrapSSR, hashId } = useStyle(prefixCls);

  /**更新密码*/
  const [modifyPasswordVisible, setModifyPasswordVisible] = useState<boolean>(false);
  /**更新手机号*/
  const [modifyPhoneVisible, setModifyPhoneVisible] = useState<boolean>(false);
  /**更新邮箱*/
  const [modifyEmailVisible, setModifyEmailVisible] = useState<boolean>(false);
  /**刷新*/
  const [refresh, setRefresh] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>();
  const { initialState, setInitialState } = useModel('@@initialState');
  useAsyncEffect(async () => {
    setLoading(true);
    if (initialState && initialState?.currentUser) {
      setLoading(false);
    }
  }, [initialState]);

  useAsyncEffect(async () => {
    if (refresh) {
      setLoading(true);
      //获取当前用户信息
      const currentUser = await initialState?.fetchUserInfo?.();
      await setInitialState((s: any) => ({ ...s, currentUser: currentUser }));
      setRefresh(false);
      setLoading(false);
    }
  }, [refresh]);

  const passwordStrength = {
    strong: <span className={classnames(`${prefixCls}-strong`, hashId)}>强</span>,
    medium: <span className={classnames(`${prefixCls}-medium`, hashId)}>中</span>,
    weak: <span className={classnames(`${prefixCls}-weak`, hashId)}>弱</span>,
  };

  const getData = () => [
    {
      title: '账户密码',
      description: (
        <>
          当前密码强度：
          {passwordStrength.strong}
        </>
      ),
      actions: [
        <a
          key="Modify"
          onClick={() => {
            setModifyPasswordVisible(true);
          }}
        >
          修改
        </a>,
      ],
    },
    {
      title: '账户手机',
      description: initialState?.currentUser?.phone
        ? `已绑定手机：${initialState?.currentUser?.phone}`
        : `暂未绑定`,
      actions: [
        <a
          key="Modify"
          onClick={() => {
            setModifyPhoneVisible(true);
          }}
        >
          修改
        </a>,
      ],
    },
    {
      title: '账户邮箱',
      description: initialState?.currentUser?.email
        ? `已绑定邮箱：${initialState?.currentUser?.email}`
        : `暂未绑定`,
      actions: [
        <a
          key="Modify"
          onClick={() => {
            setModifyEmailVisible(true);
          }}
        >
          修改
        </a>,
      ],
    },
  ];

  const data = getData();

  return wrapSSR(
    <Skeleton loading={loading} paragraph={{ rows: 8 }}>
      <List<Unpacked<typeof data>>
        itemLayout="horizontal"
        className={classnames(`${prefixCls}`, hashId)}
        dataSource={data}
        renderItem={(item) => (
          <List.Item actions={item.actions}>
            <List.Item.Meta title={item.title} description={item.description} />
          </List.Item>
        )}
      />
      {/*更新密码*/}
      <ModifyPassword
        visible={modifyPasswordVisible}
        setRefresh={setRefresh}
        setVisible={(visible) => {
          setModifyPasswordVisible(visible);
        }}
        prefixCls={prefixCls}
      />
      {/*更新手机号*/}
      <ModifyPhone
        visible={modifyPhoneVisible}
        setRefresh={setRefresh}
        setVisible={(visible) => {
          setModifyPhoneVisible(visible);
        }}
        prefixCls={prefixCls}
      />
      {/*更新手机号*/}
      <ModifyEmail
        visible={modifyEmailVisible}
        setRefresh={setRefresh}
        setVisible={(visible) => {
          setModifyEmailVisible(visible);
        }}
        prefixCls={prefixCls}
      />
    </Skeleton>,
  );
};

export default SecurityView;
