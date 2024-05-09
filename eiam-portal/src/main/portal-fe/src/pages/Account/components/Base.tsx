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
import { UploadOutlined } from '@ant-design/icons';
import { ProForm, ProFormText } from '@ant-design/pro-components';
import { App, Avatar, Button, Col, Form, Row, Skeleton, Spin, Upload } from 'antd';
import { useState } from 'react';

import { changeBaseInfo } from '../service';
import { aesEcbEncrypt } from '@/utils/aes';
import { onGetEncryptSecret } from '@/utils/utils';
import { useAsyncEffect } from 'ahooks';
import ImgCrop from 'antd-img-crop';
import { uploadFile } from '@/services/upload';
import { useModel } from '@umijs/max';
import { useIntl } from '@@/exports';
import { createStyles } from 'antd-style';
import { omit } from 'lodash';

const prefixCls = 'account-base';

const useStyles = createStyles(({ prefixCls, token }, props) => {
  const prefixClassName = `.${props}`;
  return {
    accountBase: {
      display: 'flex',
      paddingTop: '12px',
      [`@media screen and (max-width: ${token.screenMD}px)`]: {
        flexDirection: 'column-reverse',
      },
      [`${prefixClassName}-left`]: {
        minWidth: '224px',
        maxWidth: '448px',
      },
      [`${prefixClassName}-right`]: {
        flex: '1',
        paddingInlineStart: '104px',
        [`@media screen and (max-width: ${token.screenMD}px)`]: {
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          maxWidth: '448px',
          padding: '20px',
        },
      },
      [`${prefixClassName}-avatar`]: {
        marginBottom: '12px',
        overflow: 'hidden',

        img: {
          width: '100%',
        },

        [`&-name`]: {
          verticalAlign: 'middle',
          backgroundColor: `${token.colorPrimary} !important`,
        },

        [`${prefixCls}-avatar`]: {
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
          [`@media screen and (max-width: ${token.screenMD}px)`]: {
            display: 'none',
          },
        },

        [`&-button-view`]: {
          width: '144px',
          textAlign: 'center',
        },
      },
    },
  };
});

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
  const { message } = App.useApp();
  const { styles, cx } = useStyles(prefixCls);
  const [loading, setLoading] = useState<boolean>();
  const [submitLoading, setSubmitLoading] = useState<boolean>(false);
  const { initialState } = useModel('@@initialState');
  const [avatarURL, setAvatarURL] = useState<string | undefined>(initialState?.currentUser?.avatar);
  const [avatarUploaded, setAvatarUploaded] = useState<boolean>(false);
  const [name, setName] = useState<string>();

  const [form] = Form.useForm();

  useAsyncEffect(async () => {
    setLoading(true);
    if (initialState && initialState.currentUser) {
      setAvatarURL(initialState?.currentUser?.avatar);
      setName(initialState?.currentUser?.fullName || initialState?.currentUser?.username);
      setLoading(false);
    }
  }, [initialState]);

  const handleFinish = async (values: Record<string, string>) => {
    //加密传输
    const publicSecret = await onGetEncryptSecret();
    if (publicSecret) {
      const { success } = await changeBaseInfo(
        aesEcbEncrypt(
          JSON.stringify({
            fullName: values.fullName,
            nickName: values.nickName,
            personalProfile: values.personalProfile,
            custom: values.custom,
            avatar: avatarUploaded ? avatarURL : undefined,
          }),
          publicSecret,
        ),
      );
      if (success) {
        message.success(intl.formatMessage({ id: 'app.update_success' }));
      }
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
    name?: string;
    callBack: any;
  }) => (
    <>
      <div className={cx(`${prefixCls}-avatar-title`)}>
        {intl.formatMessage({ id: 'page.account.base.avatar_title' })}
      </div>
      <div className={cx(`${prefixCls}-avatar`)}>
        {avatar ? (
          <Avatar alt="avatar" shape={'circle'} size={144} src={avatar} />
        ) : (
          <Avatar shape={'circle'} className={cx(`${prefixCls}-avatar-name`)} size={144}>
            {name?.substring(0, 1)}
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
            const { success, result } = await uploadFile(files.file);
            if (success && result) {
              callBack(result);
            }
          }}
        >
          <div className={cx(`${prefixCls}-avatar-button-view`)}>
            <Button>
              <UploadOutlined />
              {intl.formatMessage({ id: 'page.account.base.avatar_change_title' })}
            </Button>
          </div>
        </Upload>
      </ImgCrop>
    </>
  );

  return (
    <>
      <div className={styles.accountBase}>
        {loading ? (
          <Skeleton paragraph={{ rows: 8 }} />
        ) : (
          <>
            <div className={cx(`${prefixCls}-left`)}>
              <Spin spinning={submitLoading}>
                <ProForm
                  layout="horizontal"
                  labelAlign={'left'}
                  {...FORM_ITEM_LAYOUT}
                  submitter={false}
                  initialValues={{
                    ...omit(initialState?.currentUser, 'custom'),
                    phone: initialState?.currentUser?.phone?.split('-'),
                  }}
                  requiredMark={false}
                  form={form}
                >
                  <ProFormText
                    width="md"
                    name="username"
                    readonly
                    label={intl.formatMessage({ id: 'page.account.base.form.username' })}
                  />
                  <ProFormText
                    width="md"
                    name="email"
                    readonly
                    label={intl.formatMessage({ id: 'page.account.base.form.email' })}
                  />
                  <ProFormText
                    width="md"
                    name="phone"
                    readonly
                    label={intl.formatMessage({ id: 'page.account.base.form.phone' })}
                  />
                  <ProFormText
                    width="md"
                    name="fullName"
                    label={intl.formatMessage({ id: 'page.account.base.form.full_name' })}
                    allowClear={false}
                  />
                  <ProFormText
                    width="md"
                    name="nickName"
                    label={intl.formatMessage({ id: 'page.account.base.form.nick_name' })}
                    allowClear={false}
                  />
                </ProForm>
              </Spin>
              <Row>
                <Col offset={5} />
                <Col span={19}>
                  <Button
                    type={'primary'}
                    loading={submitLoading}
                    onClick={async () => {
                      await form.validateFields();
                      let values = form.getFieldsValue();
                      setSubmitLoading(true);
                      await handleFinish(values).finally(() => {
                        setSubmitLoading(false);
                      });
                    }}
                  >
                    {intl.formatMessage({ id: 'app.save' })}
                  </Button>
                </Col>
              </Row>
            </div>
            <div className={cx(`${prefixCls}-right`)}>
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
      </div>
    </>
  );
};

export default BaseView;
