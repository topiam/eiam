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
import { Typography } from 'antd';
import type { RefObject } from 'react';
import { BASIC_CONFIG_FROM_PARAM } from '../../constant';
import DingTalkConfig from './DingTalkConfig';
import FeiShuConfig from './FeiShuConfig';
import WeChatWorkConfig from './WeChatWorkConfig';
import { IdentitySourceProvider } from '@/constant';

const { Paragraph } = Typography;
export interface BasicConfigInstance {
  /**验证连接*/
  configValidator: () => Promise<boolean>;
}

type BasicConfigProps = {
  provider: IdentitySourceProvider;
  configured: boolean;
  formRef?: RefObject<ProFormInstance | undefined>;
  basicConfigRef: RefObject<BasicConfigInstance | undefined>;
  onConfigValidator: (config: Record<string, string>) => Promise<boolean>;
};

export default (props: BasicConfigProps) => {
  const { provider, configured, basicConfigRef, onConfigValidator, formRef } = props;
  return (
    <>
      {/*钉钉*/}
      {provider === IdentitySourceProvider.dingtalk && (
        <DingTalkConfig
          configured={configured}
          basicConfigRef={basicConfigRef}
          onConfigValidator={onConfigValidator}
          formRef={formRef}
        />
      )}
      {/*微信*/}
      {provider === IdentitySourceProvider.wework && (
        <WeChatWorkConfig
          configured={configured}
          basicConfigRef={basicConfigRef}
          onConfigValidator={onConfigValidator}
          formRef={formRef}
        />
      )}
      {/*飞书*/}
      {provider === IdentitySourceProvider.feishu && (
        <FeiShuConfig
          configured={configured}
          basicConfigRef={basicConfigRef}
          onConfigValidator={onConfigValidator}
          formRef={formRef}
        />
      )}
      {provider !== IdentitySourceProvider.ldap && provider !== IdentitySourceProvider.ad && (
        <ProFormText
          label="回调地址"
          name={BASIC_CONFIG_FROM_PARAM.callbackUrl}
          proFieldProps={{
            render: (value: string) => {
              return (
                value && (
                  <Paragraph copyable={{ text: value }} style={{ marginBottom: '0' }}>
                    <span
                      dangerouslySetInnerHTML={{
                        __html: `<span>${value}</span>`,
                      }}
                    />
                  </Paragraph>
                )
              );
            },
          }}
          readonly
          fieldProps={{ autoComplete: 'off' }}
        />
      )}
    </>
  );
};
