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

const WeChatWorkConfig = (props: {
  configured: boolean;
  onConfigValidator: (config: Record<string, string>) => Promise<boolean>;
  basicConfigRef: RefObject<BasicConfigInstance | undefined>;
  formRef?: React.RefObject<ProFormInstance<Record<string, string>> | undefined>;
}) => {
  const { configured, formRef, onConfigValidator, basicConfigRef } = props;
  useEffect(() => {}, [configured]);
  const intl = useIntl();
  const { message } = App.useApp();

  /**
   * 验证连接
   */
  const configValidator = useCallback(async (): Promise<boolean> => {
    const current = formRef?.current;
    try {
      // 判断参数是否存在
      current?.validateFields([BASIC_CONFIG_FROM_PARAM.corpId, BASIC_CONFIG_FROM_PARAM.corpId]);
      //调用接口验证
      const result = await onConfigValidator({
        corpId: current?.getFieldValue(BASIC_CONFIG_FROM_PARAM.corpId),
        secret: current?.getFieldValue(BASIC_CONFIG_FROM_PARAM.secret),
        provider: IdentitySourceProvider.wework,
      });
      if (!result) {
        current?.setFields([
          {
            name: BASIC_CONFIG_FROM_PARAM.corpId,
            errors: [
              intl.formatMessage({ id: 'pages.account.identity_source_detail.common.test_fail' }),
            ],
          },
        ]);
        current?.setFields([
          {
            name: BASIC_CONFIG_FROM_PARAM.secret,
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
        name={BASIC_CONFIG_FROM_PARAM.corpId}
        label={intl.formatMessage({
          id: 'pages.account.identity_source_detail.basic_config.wechat_work.corp_id',
        })}
        placeholder={intl.formatMessage({
          id: 'pages.account.identity_source_detail.basic_config.wechat_work.corp_id.placeholder',
        })}
        extra={intl.formatMessage({
          id: 'pages.account.identity_source_detail.basic_config.wechat_work.corp_id.extra',
        })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.account.identity_source_detail.basic_config.wechat_work.corp_id.rule.0.message',
            }),
          },
        ]}
      />
      <ProFormText.Password
        name={BASIC_CONFIG_FROM_PARAM.secret}
        label={intl.formatMessage({
          id: 'pages.account.identity_source_detail.basic_config.wechat_work.secret',
        })}
        placeholder={intl.formatMessage({
          id: 'pages.account.identity_source_detail.basic_config.wechat_work.secret.placeholder',
        })}
        fieldProps={{ autoComplete: 'new-password' }}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'pages.account.identity_source_detail.basic_config.wechat_work.secret.rule.0.message',
            }),
          },
        ]}
        addonAfter={
          <Button type={'default'} onClick={configValidator}>
            {intl.formatMessage({ id: 'pages.account.identity_source_detail.common.test_connect' })}
          </Button>
        }
      />
      <ProFormText.Password
        name={['basicConfig', 'token']}
        label={intl.formatMessage({
          id: 'pages.account.identity_source_detail.basic_config.wechat_work.token',
        })}
        placeholder={intl.formatMessage({
          id: 'pages.account.identity_source_detail.basic_config.wechat_work.token.placeholder',
        })}
        fieldProps={{ autoComplete: 'new-password' }}
        extra={intl.formatMessage({
          id: 'pages.account.identity_source_detail.basic_config.wechat_work.token.extra',
        })}
      />
      <ProFormText.Password
        name={['basicConfig', 'encodingAESKey']}
        label={'EncodingAESKey'}
        placeholder={intl.formatMessage({
          id: 'pages.account.identity_source_detail.basic_config.wechat_work.encoding_aes_key.placeholder',
        })}
        fieldProps={{ autoComplete: 'new-password' }}
        extra={intl.formatMessage({
          id: 'pages.account.identity_source_detail.basic_config.wechat_work.encoding_aes_key.extra',
        })}
      />
    </>
  );
};
export default WeChatWorkConfig;
