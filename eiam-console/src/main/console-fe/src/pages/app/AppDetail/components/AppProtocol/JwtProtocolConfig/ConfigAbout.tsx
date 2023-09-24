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
import { CertUsingType } from '@/pages/app/AppDetail/constant';
import { useIntl } from '@umijs/max';
import Alert from '@/components/Alert';
import { createStyles } from 'antd-style';
import { ColProps } from 'antd/es/grid/col';

const IDP_ENCRYPT_CERT = 'idpEncryptCert';

const useStyles = createStyles(({ prefixCls }) => ({
  alert: {
    [`.${prefixCls}-alert-content .${prefixCls}-alert-description .${prefixCls}-form-item:last-child`]:
      {
        marginBottom: '0 !important',
      },
  },
}));
/**
 * 配置信息
 *
 * @param props
 */
export default (props: {
  appId: string;
  protocolEndpoint: Record<string, string>;
  collapsed?: boolean;
  labelCol?: ColProps;
  wrapperCol?: ColProps;
}) => {
  const [configForm] = Form.useForm();
  const {
    protocolEndpoint,
    appId,
    collapsed = true,
    labelCol = {
      span: 6,
    },
    wrapperCol = {
      span: 12,
    },
  } = props;
  const intl = useIntl();
  const { styles } = useStyles();

  useAsyncEffect(async () => {
    configForm.setFieldsValue(protocolEndpoint);
  }, [appId, protocolEndpoint]);

  useAsyncEffect(async () => {
    //获取JWT签名证书
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
      labelCol={labelCol}
      wrapperCol={wrapperCol}
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
                  id: 'pages.app.config.detail.items.login_access.protocol_config.jwt.config_about',
                })}
              </a>
            ),
            children: (
              <Alert
                type={'grey'}
                className={styles.alert}
                description={
                  <>
                    <ProFormText
                      label={intl.formatMessage({
                        id: 'pages.app.config.detail.items.login_access.protocol_config.jwt.config_about.idp_sso_endpoint',
                      })}
                      name={'idpSsoEndpoint'}
                      extra={intl.formatMessage({
                        id: 'pages.app.config.detail.items.login_access.protocol_config.jwt.config_about.idp_sso_endpoint.extra',
                      })}
                      readonly
                      proFieldProps={{
                        render: (value: string) => {
                          return value && <Typography.Text copyable>{value}</Typography.Text>;
                        },
                      }}
                      fieldProps={{ autoComplete: 'off' }}
                    />
                    <ProFormText
                      label={intl.formatMessage({
                        id: 'pages.app.config.detail.items.login_access.protocol_config.jwt.config_about.idp_slo_endpoint',
                      })}
                      name={'idpSloEndpoint'}
                      extra={intl.formatMessage({
                        id: 'pages.app.config.detail.items.login_access.protocol_config.jwt.config_about.idp_slo_endpoint.extra',
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
                        id: 'pages.app.config.detail.items.login_access.protocol_config.jwt.config_about.idp_encrypt_cert',
                      })}
                      name={IDP_ENCRYPT_CERT}
                      disabled
                      fieldProps={{ autoComplete: 'off', rows: 3 }}
                      extra={
                        <div style={{ display: 'inline-block' }}>
                          <div style={{ display: 'inline-block' }}>
                            {intl.formatMessage({
                              id: 'pages.app.config.detail.items.login_access.protocol_config.jwt.config_about.idp_encrypt_cert.extra.0',
                            })}
                          </div>
                          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                            <Typography.Paragraph
                              style={{ display: 'inline-block' }}
                              copyable={{ text: configForm.getFieldValue(IDP_ENCRYPT_CERT) }}
                            >
                              <a>
                                {intl.formatMessage({
                                  id: 'pages.app.config.detail.items.login_access.protocol_config.jwt.config_about.idp_encrypt_cert.extra.1',
                                })}
                              </a>
                            </Typography.Paragraph>
                            <a onClick={downloadEncryptCert}>
                              {intl.formatMessage({
                                id: 'pages.app.config.detail.items.login_access.protocol_config.jwt.config_about.idp_encrypt_cert.extra.2',
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
