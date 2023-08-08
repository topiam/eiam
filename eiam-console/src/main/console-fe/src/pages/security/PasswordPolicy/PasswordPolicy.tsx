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
import { App, Col, Form, InputNumber, Row, Spin, Switch } from 'antd';

import {
  FooterToolbar,
  PageContainer,
  ProCard,
  ProForm,
  ProFormDependency,
  ProFormItem,
  ProFormRadio,
  ProFormSwitch,
  ProFormTextArea,
} from '@ant-design/pro-components';
import { useState } from 'react';
import InternalWeakPasswordLib from './components/InternalWeakPasswordLib';
import { useIntl } from '@umijs/max';
import useStyles from './style';
import { useAsyncEffect } from 'ahooks';
import { getPasswordPolicyConfig, savePasswordPolicyConfig } from './service';
import { Container } from '@/components/Container';

const layout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 19 },
};

export default () => {
  const { styles } = useStyles();
  const [form] = Form.useForm();
  const { message } = App.useApp();
  const intl = useIntl();
  /** 内置弱密码库可见 */
  const [internalWeakPasswordLibVisible, setInternalWeakPasswordLibVisible] =
    useState<boolean>(false);
  /** 加载中 */
  const [spinning, setSpinning] = useState<boolean>(true);
  /** useEffect */
  useAsyncEffect(async () => {
    setSpinning(true);
    const value = await getPasswordPolicyConfig();
    if (value?.success) {
      form.setFieldsValue({ ...value?.result });
    }
    setSpinning(false);
  }, []);
  return (
    <PageContainer content={intl.formatMessage({ id: 'pages.setting.security.desc' })}>
      <ProCard>
        <Container>
          <ProForm
            form={form}
            scrollToFirstError
            {...layout}
            layout={'horizontal'}
            labelAlign="left"
            onFinish={async (values) => {
              setSpinning(true);
              const { success } = await savePasswordPolicyConfig(values).finally(() => {
                setSpinning(false);
              });
              if (success) {
                message.success(intl.formatMessage({ id: 'app.save_success' }));
              }
            }}
            submitter={{
              render: (p, dom) => {
                return <FooterToolbar>{dom}</FooterToolbar>;
              },
              resetButtonProps: {
                style: {
                  // 隐藏重置按钮
                  display: 'none',
                },
              },
            }}
          >
            <Spin spinning={spinning}>
              <ProFormItem
                label={intl.formatMessage({
                  id: 'pages.setting.security.password_policy.password_length_limit',
                })}
                className={styles.passwordLength}
                required={true}
              >
                <Row gutter={[8, 8]}>
                  <Col xs={10} sm={10} md={8} lg={8} xl={6}>
                    <Form.Item
                      label={intl.formatMessage({
                        id: 'pages.setting.security.password_policy.password_least_length',
                      })}
                      name={['passwordLeastLength']}
                      rules={[{ required: true }]}
                    >
                      <InputNumber min={8} max={10} />
                    </Form.Item>
                  </Col>
                  <Col xs={10} sm={10} md={8} lg={8} xl={6}>
                    <Form.Item
                      label={intl.formatMessage({
                        id: 'pages.setting.security.password_policy.password_biggest_length',
                      })}
                      name={['passwordBiggestLength']}
                      style={{ float: 'right' }}
                      rules={[{ required: true }]}
                    >
                      <InputNumber min={1} max={30} />
                    </Form.Item>
                  </Col>
                </Row>
              </ProFormItem>
              <ProFormRadio.Group
                layout={'vertical'}
                label={intl.formatMessage({
                  id: 'pages.setting.security.password_policy.password_complexity',
                })}
                name={['passwordComplexity']}
                options={[
                  {
                    value: '0',
                    label: intl.formatMessage({ id: 'pages.setting.security.password_policy.0' }),
                  },
                  {
                    value: '1',
                    label: intl.formatMessage({ id: 'pages.setting.security.password_policy.1' }),
                  },
                  {
                    value: '2',
                    label: intl.formatMessage({ id: 'pages.setting.security.password_policy.2' }),
                  },
                  {
                    value: '3',
                    label: intl.formatMessage({ id: 'pages.setting.security.password_policy.3' }),
                  },
                  {
                    value: '4',
                    label: intl.formatMessage({ id: 'pages.setting.security.password_policy.4' }),
                  },
                  {
                    value: '5',
                    label: intl.formatMessage({ id: 'pages.setting.security.password_policy.5' }),
                  },
                ]}
                formItemProps={{
                  className: styles.passwordComplexity,
                }}
                rules={[{ required: true }]}
              />
              <ProFormItem
                label={intl.formatMessage({
                  id: 'pages.setting.security.password_policy.password_expiration_check',
                })}
                extra={intl.formatMessage({
                  id: 'pages.setting.security.password_policy.password_expiration_check.tooltip',
                })}
                required={true}
              >
                <Row gutter={[8, 8]}>
                  <Col xs={10} sm={11} md={9} lg={8} xl={6}>
                    <Form.Item
                      label={intl.formatMessage({
                        id: 'pages.setting.security.password_policy.password_expiration_check.expiration-date',
                      })}
                      className={styles.expirationDate}
                      required={true}
                    >
                      <Form.Item name={['passwordValidDays']} noStyle rules={[{ required: true }]}>
                        <InputNumber min={7} max={365} />
                      </Form.Item>
                      <span>
                        &nbsp;
                        {intl.formatMessage({
                          id: 'pages.setting.security.password_policy.password_expiration_check.day',
                        })}
                      </span>
                    </Form.Item>
                  </Col>
                  <Col xs={12} sm={13} md={12} lg={9} xl={8}>
                    <Form.Item
                      label={intl.formatMessage({
                        id: 'pages.setting.security.password_policy.before_expiration',
                      })}
                      required={true}
                      className={styles.expirationDate}
                    >
                      <Form.Item
                        name={['passwordValidWarnBeforeDays']}
                        noStyle
                        rules={[{ required: true }]}
                      >
                        <InputNumber min={7} max={30} />
                      </Form.Item>
                      <span>
                        &nbsp;
                        {intl.formatMessage({
                          id: 'pages.setting.security.password_policy.before_expiration.day',
                        })}
                      </span>
                    </Form.Item>
                  </Col>
                </Row>
              </ProFormItem>

              {/*密码相同字符*/}
              <ProFormItem
                name={['notSameChars']}
                label={intl.formatMessage({
                  id: 'pages.setting.security.password_policy.password_same_characters',
                })}
                extra={intl.formatMessage({
                  id: 'pages.setting.security.password_policy.password_same_characters.tooltip',
                })}
                rules={[{ required: true }]}
              >
                <InputNumber min={1} max={10} />
              </ProFormItem>
              <ProFormSwitch
                name={['includeAccountCheck']}
                valuePropName="checked"
                label={intl.formatMessage({
                  id: 'pages.setting.security.password_policy.user_info_check',
                })}
                extra={intl.formatMessage({
                  id: 'pages.setting.security.password_policy.user_info_check.tooltip',
                })}
              />
              <ProFormDependency name={['rule']}>
                {({ rule }) => {
                  // 历史记录密码检查计数
                  return (
                    <>
                      <ProFormSwitch
                        name={['historyPasswordCheck']}
                        label={intl.formatMessage({
                          id: 'pages.setting.security.password_policy.hist_password_check',
                        })}
                        extra={intl.formatMessage({
                          id: 'pages.setting.security.password_policy.hist_password_check.tooltip',
                        })}
                        formItemProps={{
                          ...(rule?.historyPasswordCheck && {
                            className: styles.historyPasswordCheck,
                          }),
                        }}
                      />
                      {rule?.historyPasswordCheck && (
                        <Row>
                          <Col offset={5} span={19}>
                            <Form.Item
                              label={intl.formatMessage({
                                id: 'pages.setting.security.password_policy.range_password_history',
                              })}
                              tooltip={intl.formatMessage({
                                id: 'pages.setting.security.password_policy.range_password_history.tooltip',
                              })}
                              labelCol={{ span: 4 }}
                              labelAlign={'left'}
                              className={styles.historyPasswordCheckCount}
                            >
                              <Form.Item
                                name={['historyPasswordCheckCount']}
                                noStyle
                                rules={[{ required: true }]}
                              >
                                <InputNumber min={1} max={10} />
                              </Form.Item>
                            </Form.Item>
                          </Col>
                        </Row>
                      )}
                    </>
                  );
                }}
              </ProFormDependency>
              <Form.Item
                label={intl.formatMessage({
                  id: 'pages.setting.security.password_policy.illegal-character-sequences',
                })}
                name={['illegalSequenceCheck']}
                valuePropName="checked"
                extra={intl.formatMessage({
                  id: 'pages.setting.security.password_policy.illegal_character_sequences.tooltip',
                })}
              >
                <Switch />
              </Form.Item>
              <ProFormItem
                label={intl.formatMessage({
                  id: 'pages.setting.security.password_policy.weak_password_checking',
                })}
                extra={intl.formatMessage({
                  id: 'pages.setting.security.password_policy.weak_password_checking.tooltip',
                })}
              >
                <Form.Item noStyle name={['weakPasswordCheck']} valuePropName="checked">
                  <Switch />
                </Form.Item>
                <ProFormDependency name={['weakPasswordCheck']}>
                  {({ weakPasswordCheck }) => {
                    return (
                      weakPasswordCheck && (
                        <span>
                          <a
                            onClick={() => {
                              setInternalWeakPasswordLibVisible(true);
                            }}
                          >
                            &nbsp;
                            {intl.formatMessage({
                              id: 'pages.setting.security.password_policy.weak_password_checking.password_library',
                            })}
                          </a>
                        </span>
                      )
                    );
                  }}
                </ProFormDependency>
              </ProFormItem>
              <ProFormDependency name={['weakPasswordCheck']}>
                {({ weakPasswordCheck }) => {
                  return (
                    weakPasswordCheck && (
                      <ProFormTextArea
                        label={intl.formatMessage({
                          id: 'pages.setting.security.password_policy.other_weak_passwords',
                        })}
                        name={'customWeakPassword'}
                        extra={intl.formatMessage({
                          id: 'pages.setting.security.password_policy.other_weak_passwords.extra',
                        })}
                        fieldProps={{
                          rows: 3,
                        }}
                      />
                    )
                  );
                }}
              </ProFormDependency>
            </Spin>
          </ProForm>
        </Container>
      </ProCard>
      {/* 内置弱密码库 */}
      <InternalWeakPasswordLib
        visible={internalWeakPasswordLibVisible}
        onCancel={() => {
          setInternalWeakPasswordLibVisible(false);
        }}
      />
    </PageContainer>
  );
};
