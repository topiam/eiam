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
import { UploadOutlined } from '@ant-design/icons';
import { ProForm, ProFormText, useStyle as useAntdStyle } from '@ant-design/pro-components';
import { App, Avatar, Button, Form, Skeleton, Upload } from 'antd';
import { useState } from 'react';

import { changeBaseInfo } from '../service';
import { useAsyncEffect } from 'ahooks';
import ImgCrop from 'antd-img-crop';
import { uploadFile } from '@/services/upload';
import { useModel } from '@umijs/max';
import classnames from 'classnames';
import { useIntl } from '@@/exports';

const prefixCls = 'account-base';

function useStyle() {
  return useAntdStyle('AccountBaseComponent', (token) => {
    return [
      {
        [`.${prefixCls}`]: {
          display: 'flex',
          'padding-top': '12px',
          [`&-left`]: {
            minWidth: '224px',
            maxWidth: '448px',
          },
          [`&-right`]: {
            flex: 1,
            'padding-inline-start': '104px',
          },
          [`&-avatar`]: {
            marginBottom: '12px',
            overflow: 'hidden',
            img: {
              width: '100%',
            },
            [`&-name`]: {
              verticalAlign: 'middle',
              backgroundColor: `${token.colorPrimary} !important`,
            },
            [`${token.antCls}-avatar`]: {
              [`&-string`]: {
                fontSize: '75px',
              },
            },
            [`&-title`]: {
              height: '22px',
              marginBottom: '8px',
              color: '@heading-color',
              fontSize: '@font-size-base',
              lineHeight: '22px',
            },
            [`&-button-view`]: {
              width: '144px',
              textAlign: 'center',
            },
          },
        },
        [`@media screen and (max-width: ${token.screenXL}px)`]: {
          [`.${prefixCls}`]: {
            flexDirection: 'column-reverse',
            [`&-right`]: {
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              maxWidth: '448px',
              padding: '20px',
            },
            ['&-avatar']: {
              ['&-title']: {
                display: 'none',
              },
            },
          },
        },
      },
    ];
  });
}
export const FORM_ITEM_LAYOUT = {
  labelCol: {
    span: 5,
  },
  wrapperCol: {
    span: 19,
  },
};

const BaseView = () => {
  const intl = useIntl();
  const useApp = App.useApp();
  const { wrapSSR, hashId } = useStyle();
  const [loading, setLoading] = useState<boolean>();
  const { initialState, setInitialState } = useModel('@@initialState');
  const [avatarURL, setAvatarURL] = useState<string | undefined>(initialState?.currentUser?.avatar);
  const [avatarUploaded, setAvatarUploaded] = useState<boolean>(false);
  const [name, setName] = useState<string>('');

  useAsyncEffect(async () => {
    setLoading(true);
    if (initialState && initialState.currentUser) {
      setAvatarURL(initialState?.currentUser?.avatar);
      setName(initialState?.currentUser?.fullName || initialState?.currentUser?.username);
      setTimeout(async () => {
        setLoading(false);
      }, 500);
    }
  }, [initialState]);

  const handleFinish = async (values: Record<string, string>) => {
    const { success } = await changeBaseInfo({
      fullName: values.fullName,
      nickName: values.nickName,
      avatar: avatarUploaded ? avatarURL : undefined,
    });
    if (success) {
      useApp.message.success(intl.formatMessage({ id: 'app.update_success' }));
      //获取当前用户信息
      const currentUser = await initialState?.fetchUserInfo?.();
      await setInitialState((s: any) => ({ ...s, currentUser: currentUser }));
    }
  };

  /**
   * 头像组件 方便以后独立，增加裁剪之类的功能
   *
   * @param avatar
   * @param name
   * @param callBack
   * @constructor
   */
  const AvatarView = ({
    avatar,
    name,
    callBack,
  }: {
    avatar: string | undefined;
    name: string;
    callBack: any;
  }) => (
    <>
      <div className={classnames(`${prefixCls}-avatar-title`, hashId)}>
        {intl.formatMessage({ id: 'page.user.profile.base.avatar_title' })}
      </div>
      <div className={classnames(`${prefixCls}-avatar`, hashId)}>
        {avatar ? (
          <Avatar alt="avatar" shape={'circle'} size={144} src={avatar} />
        ) : (
          <Avatar
            shape={'circle'}
            className={classnames(`${prefixCls}-avatar-name`, hashId)}
            size={144}
          >
            {name.substring(0, 1)}
          </Avatar>
        )}
      </div>
      <ImgCrop
        rotationSlider
        aspectSlider
        modalOk={intl.formatMessage({ id: 'app.confirm' })}
        modalCancel={intl.formatMessage({ id: 'app.cancel' })}
      >
        <Upload
          name="file"
          showUploadList={false}
          accept="image/png, image/jpeg"
          customRequest={async (files) => {
            if (!files.file) {
              return;
            }
            const { success, result, message } = await uploadFile(files.file);
            if (success && result) {
              callBack(result);
              return;
            }
            useApp.message.error(message);
          }}
        >
          <div className={classnames(`${prefixCls}-avatar-button-view`, hashId)}>
            <Button>
              <UploadOutlined />
              {intl.formatMessage({ id: 'page.user.profile.base.avatar_change_title' })}
            </Button>
          </div>
        </Upload>
      </ImgCrop>
    </>
  );

  return wrapSSR(
    <div className={classnames(`${prefixCls}`, hashId)}>
      {loading ? (
        <Skeleton paragraph={{ rows: 8 }} active />
      ) : (
        <>
          <div className={classnames(`${prefixCls}-left`, hashId)}>
            <ProForm
              layout="horizontal"
              labelAlign={'left'}
              {...FORM_ITEM_LAYOUT}
              onFinish={handleFinish}
              submitter={{
                render: (p, dom) => {
                  return <Form.Item wrapperCol={{ span: 19, offset: 5 }}>{dom}</Form.Item>;
                },
                searchConfig: {
                  submitText: intl.formatMessage({
                    id: 'page.user.profile.base.form.update_button',
                  }),
                },
                resetButtonProps: {
                  style: {
                    display: 'none',
                  },
                },
              }}
              initialValues={{
                ...initialState?.currentUser,
                phone: initialState?.currentUser?.phone?.split('-'),
              }}
              requiredMark={false}
            >
              <ProFormText
                width="md"
                name="accountId"
                readonly
                label={intl.formatMessage({ id: 'page.user.profile.base.form.account_id' })}
              />
              <ProFormText
                width="md"
                name="username"
                readonly
                label={intl.formatMessage({ id: 'page.user.profile.base.form.username' })}
              />
              <ProFormText
                width="md"
                name="email"
                readonly
                label={intl.formatMessage({ id: 'page.user.profile.base.form.email' })}
              />
              <ProFormText
                width="md"
                name="phone"
                readonly
                label={intl.formatMessage({ id: 'page.user.profile.base.form.phone' })}
              />
              <ProFormText
                width="md"
                name="fullName"
                label={intl.formatMessage({ id: 'page.user.profile.base.form.full_name' })}
                allowClear={false}
              />
              <ProFormText
                width="md"
                name="nickName"
                label={intl.formatMessage({ id: 'page.user.profile.base.form.nick_name' })}
                allowClear={false}
                rules={[
                  {
                    required: true,
                    message: intl.formatMessage({
                      id: 'page.user.profile.base.form.nick_name.rule.0',
                    }),
                  },
                ]}
              />
            </ProForm>
          </div>
          <div className={classnames(`${prefixCls}-right`, hashId)}>
            <AvatarView
              avatar={avatarURL}
              callBack={(avatarUrl: string) => {
                setAvatarURL(avatarUrl);
                setAvatarUploaded(true);
              }}
              name={name}
            />
          </div>
        </>
      )}
    </div>,
  );
};

export default BaseView;
