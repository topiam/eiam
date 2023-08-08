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
import { createApp, getAppTemplateList } from '@/services/app';
import { history } from '@@/core/history';
import {
  ActionType,
  ModalForm,
  PageContainer,
  ProCard,
  ProFormText,
  ProFormTextArea,
  ProList,
  QueryFilter,
} from '@ant-design/pro-components';

import { useAsyncEffect } from 'ahooks';
import { App, Avatar, Card, Spin, Typography } from 'antd';
import { useForm } from 'antd/es/form/Form';
import * as React from 'react';
import { useRef, useState } from 'react';
import useStyle from './style';
import { useIntl } from '@umijs/max';
import classnames from 'classnames';
import { ListTemplate } from '@/pages/app/AppCreate/data.d';
import { AppType } from '@/constant';

const { Paragraph } = Typography;
const prefixCls = 'topiam-create-app';

/**
 * 创建应用
 *
 * @constructor
 */
const CreateApp = (props: {
  name: string;
  code: string;
  protocol: string;
  open: boolean;
  onCancel: (e?: React.MouseEvent<HTMLButtonElement>) => void;
}) => {
  const intl = useIntl();
  const { name, code, protocol, open, onCancel } = props;
  const [form] = useForm();
  const [loading, setLoading] = useState<boolean>(false);
  const { modal } = App.useApp();

  useAsyncEffect(async () => {
    if (open) {
      form?.setFieldsValue({ name: `${name} 应用` });
    }
  }, [open]);

  return (
    <ModalForm
      title={intl.formatMessage({ id: 'pages.app.create.modal_form.title' })}
      open={open}
      width={500}
      form={form}
      scrollToFirstError
      initialValues={{ template: code }}
      autoFocusFirstInput
      layout={'horizontal'}
      labelAlign={'left'}
      labelCol={{ span: 4 }}
      wrapperCol={{ span: 20 }}
      requiredMark={false}
      preserve={false}
      modalProps={{
        onCancel: (e) => {
          form.resetFields();
          onCancel(e);
        },
      }}
      onFinish={async (values: Record<string, string>) => {
        setLoading(true);
        const { success, result } = await createApp(values).finally(() => {
          setLoading(false);
        });
        if (success && result) {
          onCancel();
          setTimeout(() => {
            const successModal = modal.success({
              title: intl.formatMessage({ id: 'app.add_success' }),
              content: intl.formatMessage({
                id: 'pages.app.create.modal_form.modal_success.content',
              }),
              okText: intl.formatMessage({
                id: 'pages.app.create.modal_form.modal_success.ok_text',
              }),
              onOk: () => {
                successModal.destroy();
                history.push(
                  `/app/config?id=${result.id}&name=${values.name}&protocol=${protocol}`,
                );
              },
            });
          }, 300);
        }
      }}
    >
      <Spin spinning={loading}>
        <ProFormText name={'template'} hidden />
        <ProFormText
          name="name"
          preserve={false}
          label={intl.formatMessage({ id: 'pages.app.create.modal_form.name' })}
          placeholder={intl.formatMessage({ id: 'pages.app.create.modal_form.name.placeholder' })}
          rules={[
            {
              required: true,
              message: intl.formatMessage({ id: 'pages.app.create.modal_form.name.rule.0' }),
            },
          ]}
        />
        <ProFormTextArea
          name="remark"
          preserve={false}
          fieldProps={{ rows: 2, maxLength: 20, showCount: false }}
          placeholder={intl.formatMessage({ id: 'pages.app.create.modal_form.remark.placeholder' })}
          label={intl.formatMessage({ id: 'pages.app.create.modal_form.remark' })}
        />
      </Spin>
    </ModalForm>
  );
};
const AppCreate = () => {
  const intl = useIntl();
  const [searchParams, setSearchParams] = useState<Record<string, any>>();
  const [createAppOpen, setCreateAppOpen] = useState<boolean>(false);
  const [createAppTemplate, setCreateAppTemplate] = useState<ListTemplate>();
  const actionRef = useRef<ActionType>();
  const { styles } = useStyle(prefixCls);

  return (
    <div className={styles}>
      <PageContainer
        onBack={() => {
          history.push('/app');
        }}
        className={classnames(`${prefixCls}`)}
        content={
          <>
            <span>{intl.formatMessage({ id: 'pages.app.create.desc.0' })}</span>
            <span>{intl.formatMessage({ id: 'pages.app.create.desc.1' })}</span>
          </>
        }
      >
        <ProCard bodyStyle={{ padding: 0 }}>
          <QueryFilter
            layout="horizontal"
            onFinish={(values) => {
              setSearchParams({ ...searchParams, ...values });
              actionRef.current?.reset?.();
              return Promise.resolve();
            }}
          >
            <ProFormText
              name="name"
              label={intl.formatMessage({ id: 'pages.app.create.form_text.name' })}
            />
          </QueryFilter>
        </ProCard>
        <br />
        <ProList<ListTemplate>
          split
          rowKey="code"
          showActions="always"
          headerTitle={intl.formatMessage({ id: 'pages.app.create.header_title' })}
          search={false}
          actionRef={actionRef}
          request={getAppTemplateList}
          pagination={{ defaultPageSize: 20, size: 'small' }}
          grid={{
            xs: 1,
            sm: 2,
            md: 2,
            lg: 3,
            xl: 4,
            xxl: 5,
          }}
          params={{ ...searchParams }}
          renderItem={(row) => {
            return (
              <Card
                className={`${prefixCls}-item-card`}
                style={{ margin: 8 }}
                hoverable
                bordered={false}
              >
                <div
                  className={`${prefixCls}-item-content-wrapper`}
                  key={row.code}
                  onClick={() => {
                    setCreateAppOpen(true);
                    setCreateAppTemplate(row);
                  }}
                >
                  <div className={`${prefixCls}-item-avatar`}>
                    <Avatar key={row.icon} shape="square" src={row.icon} size={45} />
                  </div>
                  <div className={`${prefixCls}-item-content`}>
                    <span className={`${prefixCls}-item-content-title`}>{row.name}</span>
                    {row.type === AppType.CUSTOM_MADE && (
                      <span className={`${prefixCls}-item-content-type`}>{row.protocol}</span>
                    )}
                    <Paragraph
                      className={`${prefixCls}-item-content-desc`}
                      ellipsis={{ tooltip: row.desc, rows: 2 }}
                      title={row.desc}
                    >
                      {row.desc ? row.desc : <>&nbsp;</>}
                    </Paragraph>
                  </div>
                </div>
              </Card>
            );
          }}
        />
        {createAppTemplate && (
          <CreateApp
            code={createAppTemplate?.code}
            name={createAppTemplate?.name}
            protocol={createAppTemplate.protocol}
            open={createAppOpen}
            onCancel={() => {
              setCreateAppOpen(false);
              setCreateAppTemplate(undefined);
            }}
          />
        )}
      </PageContainer>
    </div>
  );
};
export default () => {
  return <AppCreate />;
};
