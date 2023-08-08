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
import type { ProFormInstance } from '@ant-design/pro-components';
import { ProFormText } from '@ant-design/pro-components';
import { App, Button } from 'antd';
import type { RefObject } from 'react';
import React, { useCallback, useEffect, useImperativeHandle } from 'react';
import { BASIC_CONFIG_FROM_PARAM } from '../../constant';
import type { BasicConfigInstance } from '../BasicConfig';
import { useIntl } from '@umijs/max';
import { IdentitySourceProvider } from '@/constant';

const FeiShuConfig = (props: {
  configured: boolean;
  onConfigValidator: (config: Record<string, string>) => Promise<boolean>;
  basicConfigRef: RefObject<BasicConfigInstance | undefined>;
  formRef?: React.RefObject<ProFormInstance<Record<string, string>> | undefined>;
}) => {
  const { configured, formRef, basicConfigRef, onConfigValidator } = props;
  const intl = useIntl();
  const { message } = App.useApp();

  useEffect(() => {}, [configured]);
  /**
   * 验证连接
   */
  const configValidator = useCallback(async (): Promise<boolean> => {
    const current = formRef?.current;
    try {
      // 判断参数是否存在
      await current?.validateFields([
        BASIC_CONFIG_FROM_PARAM.appId,
        BASIC_CONFIG_FROM_PARAM.appSecret,
      ]);
      // 调用接口验证
      const result = await onConfigValidator({
        provider: IdentitySourceProvider.feishu,
        appId: current?.getFieldValue(BASIC_CONFIG_FROM_PARAM.appId),
        appSecret: current?.getFieldValue(BASIC_CONFIG_FROM_PARAM.appSecret),
      });
      if (!result) {
        current?.setFields([
          {
            name: BASIC_CONFIG_FROM_PARAM.appId,
            errors: [
              intl.formatMessage({ id: 'pages.account.identity_source_detail.common.test_fail' }),
            ],
          },
        ]);
        current?.setFields([
          {
            name: BASIC_CONFIG_FROM_PARAM.appSecret,
            errors: [
              intl.formatMessage({ id: 'pages.account.identity_source_detail.common.test_fail' }),
            ],
          },
        ]);
        return false;
      }
      message.success(
        intl.formatMessage({ id: 'pages.account.identity_source_detail.common.test_succeed' }),
      );
      return true;
    } catch (e) {
      return false;
    }
  }, [formRef, onConfigValidator]);

  useImperativeHandle(
    basicConfigRef,
    () => {
      return { configValidator: configValidator };
    },
    [configValidator],
  );

  return (
    <>
      <ProFormText
        name={BASIC_CONFIG_FROM_PARAM.appId}
        label={intl.formatMessage({
          id: 'pages.account.identity_source_detail.basic_config.fei_shu.app_id',
        })}
        placeholder={intl.formatMessage({
          id: 'pages.account.identity_source_detail.basic_config.fei_shu.app_id.placeholder',
        })}
        extra={intl.formatMessage({
          id: 'pages.account.identity_source_detail.basic_config.fei_shu.app_id.extra',
        })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.account.identity_source_detail.basic_config.fei_shu.app_id.rule.0.message',
            }),
          },
        ]}
      />
      <ProFormText.Password
        name={BASIC_CONFIG_FROM_PARAM.appSecret}
        label={intl.formatMessage({ id: 'pages.account.identity_source_detail.common.app_secret' })}
        placeholder={intl.formatMessage({
          id: 'pages.account.identity_source_detail.common.app_secret.placeholder',
        })}
        extra={intl.formatMessage({
          id: 'pages.account.identity_source_detail.basic_config.fei_shu.app_secret.extra',
        })}
        fieldProps={{ autoComplete: 'new-password' }}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.account.identity_source_detail.common.app_secret.rule.0.message',
            }),
          },
        ]}
        addonAfter={
          <Button type={'default'} onClick={configValidator}>
            {intl.formatMessage({ id: 'pages.account.identity_source_detail.common.test_connect' })}
          </Button>
        }
      />
      {/*回调事件*/}
      <ProFormText.Password
        name={['basicConfig', 'encryptKey']}
        label={'EncryptKey'}
        extra={intl.formatMessage({
          id: 'pages.account.identity_source_detail.basic_config.fei_shu.encrypt_key.extra',
        })}
        fieldProps={{ autoComplete: 'new-password' }}
      />
      <ProFormText.Password
        name={['basicConfig', 'verificationToken']}
        label={'VerificationToken'}
        extra={intl.formatMessage({
          id: 'pages.account.identity_source_detail.basic_config.fei_shu.verification_token.extra',
        })}
        fieldProps={{ autoComplete: 'new-password' }}
      />
    </>
  );
};
export default FeiShuConfig;
