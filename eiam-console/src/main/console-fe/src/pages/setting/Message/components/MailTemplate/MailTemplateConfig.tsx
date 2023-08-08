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
import { disableCustomTemplate, getMailTemplate, saveMailTemplate } from '../../service';
import { useAsyncEffect } from 'ahooks';
import { Button, Drawer, Form, Input, App, Spin, Switch } from 'antd';
import 'codemirror/lib/codemirror.css';
import 'codemirror/mode/clike/clike';
import 'codemirror/mode/cmake/cmake';
import 'codemirror/mode/htmlembedded/htmlembedded';
import 'codemirror/mode/javascript/javascript';
import 'codemirror/theme/material.css';
import React, { useState } from 'react';
import { UnControlled as CodeMirror } from 'react-codemirror2';
import MailTemplateBrowse from './MailTemplateBrowse';
import { useIntl } from '@umijs/max';

export type MailTemplateConfigProps = {
  /** 标题 */
  title: string | React.ReactNode;
  /** 类型 */
  type: string;
  /** 是否显示 */
  visible: boolean;
  /** 取消方法 */
  onClose: () => void;
  /** 提交 */
  onSubmit?: () => void;
};
export default (props: MailTemplateConfigProps) => {
  const { visible, type, title, onClose, onSubmit } = props;
  const [form] = Form.useForm();
  const intl = useIntl();
  const { message } = App.useApp();
  const [browseVisible, setBrowseVisible] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(false);
  const [submitLoading, setSubmitLoading] = useState<boolean>(false);
  const [content, setContent] = useState<string>('');
  const [custom, setCustom] = useState<boolean>(false);

  useAsyncEffect(async () => {
    if (visible) {
      setLoading(true);
      const { success, result } = await getMailTemplate({ type });
      if (success) {
        form.setFieldsValue({ ...result });
        setCustom(result.custom);
        setContent(result.content);
      }
      setLoading(false);
    }
  }, [type, visible]);

  /**
   * 取消
   */
  function onCancel() {
    onClose();
    setSubmitLoading(false);
    form.resetFields();
  }

  return (
    <>
      <Drawer
        onClose={onCancel}
        destroyOnClose
        width="600px"
        title={title}
        placement="right"
        closable
        open={visible}
        footer={
          <div
            style={{
              textAlign: 'right',
            }}
          >
            <Button
              onClick={() => {
                setContent(content);
                setBrowseVisible(true);
              }}
              style={{ marginRight: 8 }}
            >
              {intl.formatMessage({ id: 'app.preview' })}
            </Button>
            {custom && (
              <Button
                loading={submitLoading}
                onClick={() => {
                  form.submit();
                }}
                type="primary"
              >
                {intl.formatMessage({ id: 'app.save' })}
              </Button>
            )}
          </div>
        }
      >
        <Form
          form={form}
          scrollToFirstError
          requiredMark
          onFinish={async (values) => {
            setSubmitLoading(true);
            try {
              await saveMailTemplate({ type, ...values, content }).then((value) => {
                if (value.success && value.result === true) {
                  message.success(intl.formatMessage({ id: 'app.save_success' }));
                  onCancel();
                  if (onSubmit) {
                    onSubmit();
                  }
                }
              });
            } catch (e) {
            } finally {
              setSubmitLoading(false);
            }
          }}
          layout="vertical"
        >
          <Spin spinning={loading} size="large">
            <Form.Item
              label={intl.formatMessage({
                id: 'pages.setting.message.mail_template_config.from.custom',
              })}
              name="custom"
              valuePropName={'checked'}
              extra={intl.formatMessage({
                id: 'pages.setting.message.mail_template_config.from.custom.extra',
              })}
            >
              <Switch
                onChange={async (checked) => {
                  setLoading(true);
                  //  禁用调用方法，启用不需要，因为启用不点保存，相当于禁用
                  if (!checked) {
                    const { success } = await disableCustomTemplate(type).finally(() => {
                      setLoading(false);
                    });
                    if (success) {
                      setCustom(checked);
                    }
                  } else {
                    setCustom(checked);
                    setLoading(false);
                  }
                }}
              />
            </Form.Item>
            {custom && (
              <>
                <Form.Item
                  label={intl.formatMessage({
                    id: 'pages.setting.message.mail_template_config.from.sender',
                  })}
                  name="sender"
                  rules={[
                    {
                      required: true,
                      message: intl.formatMessage({
                        id: 'pages.setting.message.mail_template_config.from.sender.placeholder',
                      }),
                    },
                  ]}
                  extra={intl.formatMessage(
                    {
                      id: 'pages.setting.message.mail_template_config.from.sender.extra',
                    },
                    {
                      client_name: '{client_name}',
                      time: '{time}',
                      user_email: '{user_email}',
                      client_description: '{client_description}',
                      password: '{password}',
                    },
                  )}
                >
                  <Input
                    autoComplete="off"
                    placeholder={intl.formatMessage({
                      id: 'pages.setting.message.mail_template_config.from.sender.placeholder',
                    })}
                  />
                </Form.Item>
                <Form.Item
                  label={intl.formatMessage({
                    id: 'pages.setting.message.mail_template_config.from.subject',
                  })}
                  name="subject"
                  rules={[
                    {
                      required: true,
                      message: intl.formatMessage({
                        id: 'pages.setting.message.mail_template_config.from.subject.placeholder',
                      }),
                    },
                  ]}
                  extra={intl.formatMessage(
                    {
                      id: 'pages.setting.message.mail_template_config.from.subject.extra',
                    },
                    {
                      client_name: '{client_name}',
                      time: '{time}',
                      client_description: '{client_description}',
                      user_email: '{user_email}',
                    },
                  )}
                >
                  <Input
                    autoComplete="off"
                    placeholder={intl.formatMessage({
                      id: 'pages.setting.message.mail_template_config.from.subject.placeholder',
                    })}
                  />
                </Form.Item>
                <Form.Item hidden name="content">
                  <Input hidden value={content} />
                </Form.Item>
                {!loading && (
                  <Form.Item
                    label={intl.formatMessage({
                      id: 'pages.setting.message.mail_template_config.from.content',
                    })}
                  >
                    {/*@ts-ignore*/}
                    <CodeMirror
                      options={{
                        mode: 'htmlembedded',
                        theme: 'material',
                        lineNumbers: false,
                        smartIndent: false, // 是否智能缩进
                        fullScreen: false, // 全屏模式
                      }}
                      onBlur={(editor) => {
                        setContent(editor.getValue());
                      }}
                      value={content}
                    />
                  </Form.Item>
                )}
              </>
            )}
          </Spin>
        </Form>
      </Drawer>
      {/* 邮件模板浏览 */}
      <MailTemplateBrowse
        content={content}
        title={title}
        visible={browseVisible}
        onCancel={() => {
          setBrowseVisible(false);
        }}
      />
    </>
  );
};
