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
import { ProForm, ProFormText, ProFormTextArea } from '@ant-design/pro-components';
import { useAsyncEffect } from 'ahooks';
import { Collapse, Form, Typography } from 'antd';
import { VerticalAlignBottomOutlined } from '@ant-design/icons';
import { getCertList } from '@/services/app';
import { CertUsingType } from '@/pages/app/AppConfig/constant';
import { useIntl } from '@umijs/max';
import Alert from '@/components/Alert';

const IDP_ENCRYPT_CERT = 'idpEncryptCert';

/**
 * 配置信息
 *
 * @param props
 */
export default (props: {
  appId: string;
  protocolEndpoint: Record<string, string>;
  collapsed?: boolean;
}) => {
  const [configForm] = Form.useForm();
  const { protocolEndpoint, appId, collapsed = true } = props;
  const intl = useIntl();

  useAsyncEffect(async () => {
    configForm.setFieldsValue(protocolEndpoint);
  }, [appId, protocolEndpoint]);

  useAsyncEffect(async () => {
    //获取SAML2签名证书
    const certResult = await getCertList(appId, CertUsingType.JWT_ENCRYPT);
    if (certResult.success && certResult.result) {
      certResult.result.forEach((value) => {
        if (value.usingType === CertUsingType.JWT_ENCRYPT) {
          configForm.setFieldsValue({ idpEncryptCert: value.cert });
        }
      });
    }
  }, [appId]);

  const downloadEncryptCert = () => {
    const value = configForm.getFieldValue(IDP_ENCRYPT_CERT);
    if (!value) {
      return;
    }
    const blob = new Blob([value], { type: 'application/x-x509-ca-cert' });
    const url = URL.createObjectURL(blob);

    const a = document.createElement('a');
    a.href = url;
    a.download = appId + 'sign.cer';
    document.documentElement.appendChild(a);
    a.click();
    document.documentElement.removeChild(a);
  };

  return (
    <ProForm
      layout={'horizontal'}
      labelCol={{ xs: { span: 24 }, sm: { span: 6 } }}
      wrapperCol={{ xs: { span: 24 }, sm: { span: 12 } }}
      labelAlign={'right'}
      labelWrap
      submitter={false}
      form={configForm}
    >
      <Collapse
        ghost
        expandIconPosition={'start'}
        defaultActiveKey={collapsed ? undefined : 'config'}
        items={[
          {
            key: 'config',
            label: (
              <a>
                {intl.formatMessage({
                  id: 'pages.app.config.items.login_access.protocol_config.jwt.config_about',
                })}
              </a>
            ),
            children: (
              <Alert
                type={'grey'}
                description={
                  <>
                    <ProFormText
                      label={intl.formatMessage({
                        id: 'pages.app.config.items.login_access.protocol_config.jwt.config_about.idp_sso_endpoint',
                      })}
                      name={'idpSsoEndpoint'}
                      extra={intl.formatMessage({
                        id: 'pages.app.config.items.login_access.protocol_config.jwt.config_about.idp_sso_endpoint.extra',
                      })}
                      readonly
                      proFieldProps={{
                        render: (value: string) => {
                          return value && <Typography.Text copyable>{value}</Typography.Text>;
                        },
                      }}
                      fieldProps={{ autoComplete: 'off' }}
                    />
                    <ProFormTextArea
                      label={intl.formatMessage({
                        id: 'pages.app.config.items.login_access.protocol_config.jwt.config_about.idp_encrypt_cert',
                      })}
                      name={IDP_ENCRYPT_CERT}
                      disabled
                      fieldProps={{ autoComplete: 'off', rows: 3 }}
                      extra={
                        <div style={{ display: 'inline-block' }}>
                          <div style={{ display: 'inline-block' }}>
                            {intl.formatMessage({
                              id: 'pages.app.config.items.login_access.protocol_config.jwt.config_about.idp_encrypt_cert.extra.0',
                            })}
                          </div>
                          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                            <Typography.Paragraph
                              style={{ display: 'inline-block' }}
                              copyable={{ text: configForm.getFieldValue(IDP_ENCRYPT_CERT) }}
                            >
                              <a>
                                {intl.formatMessage({
                                  id: 'pages.app.config.items.login_access.protocol_config.jwt.config_about.idp_encrypt_cert.extra.1',
                                })}
                              </a>
                            </Typography.Paragraph>
                            <a onClick={downloadEncryptCert}>
                              {intl.formatMessage({
                                id: 'pages.app.config.items.login_access.protocol_config.jwt.config_about.idp_encrypt_cert.extra.2',
                              })}
                              <VerticalAlignBottomOutlined />
                            </a>
                          </div>
                        </div>
                      }
                    />
                  </>
                }
              />
            ),
          },
        ]}
      />
    </ProForm>
  );
};
