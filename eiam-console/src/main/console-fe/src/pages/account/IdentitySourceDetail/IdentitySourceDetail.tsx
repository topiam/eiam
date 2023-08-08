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
import {
  getIdentitySource,
  getIdentitySourceConfig,
  identitySourceConfigValidator,
  saveIdentitySourceConfig,
  updateIdentitySource,
} from './service';
import { history } from '@@/core/history';
import type { ProFormInstance } from '@ant-design/pro-components';
import {
  FooterToolbar,
  PageContainer,
  ProCard,
  ProDescriptions,
  RouteContext,
  StepsForm,
} from '@ant-design/pro-components';
import { useAsyncEffect, useMount } from 'ahooks';
import type { FormInstance } from 'antd';
import { App, Skeleton, Spin } from 'antd';
import { omit } from 'lodash';
import React, { useRef, useState } from 'react';
import type { BasicConfigInstance } from './components/BasicConfig';
import BasicConfig from './components/BasicConfig';
import JobConfig from './components/JobConfig';
import StrategyConfig from './components/StrategyConfig';
import SyncHistory from './components/SyncHistory';
import EventRecord from './components/EventRecord';
import { ConfigFormLayout, IdentitySourceDetailTabs, JobMode } from './constant';
import useStyles from './style';
import queryString from 'query-string';
import { useIntl, useLocation } from '@umijs/max';
import dayjs from 'dayjs';
import { IdentitySourceProvider } from '@/constant';
import { getOrganization } from '@/services/account';

/**
 * 身份源详情
 */
export default () => {
  const location = useLocation();
  const query = queryString.parse(location.search);
  const { id } = query as { id: string };
  const { type } = query as { type: IdentitySourceDetailTabs };
  const [tabActiveKey, setTabActiveKey] = useState<string>();
  const [detailLoading, setDetailLoading] = useState<boolean>(false);
  const [configLoading, setConfigLoading] = useState<boolean>(false);
  const [detail, setDetail] = useState<AccountAPI.GetIdentitySource | Record<string, string>>();
  const baseFormRef = useRef<FormInstance>();
  const basicConfigRef = useRef<BasicConfigInstance>();
  const jobFormRef = useRef<ProFormInstance>();
  const strategyFormRef = useRef<ProFormInstance>();
  const [current, setCurrent] = useState<number>(0);
  const [configured, setConfigured] = useState<boolean>(false);
  const { styles } = useStyles();
  const intl = useIntl();
  const { message } = App.useApp();

  useMount(async () => {
    if (!id) {
      message.warning(
        intl.formatMessage({ id: 'pages.account.identity_source_detail.warning.message' }),
      );
      history.push(`/account/identity-source`);
      return;
    }
    if (!type) {
      setTabActiveKey(IdentitySourceDetailTabs.config);
      history.push({
        pathname: location.pathname,
        search: queryString.stringify({ type: IdentitySourceDetailTabs.config, id: id }),
      });
      return;
    }
    setTabActiveKey(type);
  });

  useAsyncEffect(async () => {
    // 查询身份源信息
    if (id) {
      setDetailLoading(true);
      const { success, result } = await getIdentitySource(id);
      if (success && result) {
        //设置详情
        setDetail(result);
        setDetailLoading(false);
      }
    }
  }, [id]);

  useAsyncEffect(async () => {
    if (type === IdentitySourceDetailTabs.config) {
      setConfigLoading(true);
      getIdentitySourceConfig(id).then(async ({ result }) => {
        setConfigured(result?.configured);
        const targetId = result.strategyConfig?.organization?.targetId;
        let strategyConfig = result.strategyConfig;
        if (targetId && result?.configured) {
          const orgRes = await getOrganization(targetId);
          let target: { value: string | undefined; label: string | undefined } = {
            value: targetId,
            label: undefined,
          };
          if (orgRes && orgRes.success && orgRes.result) {
            target = { value: targetId, label: orgRes.result.name };
          }
          strategyConfig = {
            ...result.strategyConfig,
            organization: {
              targetId: target,
            },
          };
        }
        //提供商配置
        baseFormRef.current?.setFieldsValue({ basicConfig: { ...result.basicConfig } });
        //策略配置
        strategyFormRef.current?.setFieldsValue({ strategyConfig: { ...strategyConfig } });
        //任务配置
        const mode = result.jobConfig?.mode;
        const value = result.jobConfig?.value;
        jobFormRef.current?.setFieldsValue({
          jobConfig: {
            ...result.jobConfig,
            interval: mode === JobMode.period ? value : undefined,
            time: mode === JobMode.timed ? dayjs(value, 'HH:mm:ss') : undefined,
          },
        });
        setConfigLoading(false);
      });
    }
  }, [type]);
  /**
   * onSave
   *
   * @param key
   * @param record
   */
  const onSave = async (
    key: React.Key | React.Key[],
    record: AccountAPI.GetIdentitySource | Record<string, string>,
  ): Promise<any | void> => {
    //调用接口修改
    const { success } = await updateIdentitySource({
      id: record.id,
      name: record.name,
      remark: record.remark,
    });
    if (success) {
      message.success(intl.formatMessage({ id: 'app.operation_success' }));
      setDetail({ ...record });
      return Promise.resolve(true);
    }
    return Promise.resolve(false);
  };

  const content = (
    <RouteContext.Consumer>
      {({ isMobile }) =>
        detailLoading ? (
          <Skeleton active paragraph={{ rows: 1 }} />
        ) : (
          <ProDescriptions<AccountAPI.GetIdentitySource | Record<string, string>>
            size="small"
            column={isMobile ? 1 : 2}
            dataSource={{ ...detail }}
            //只有具有修改权限才可以修改该信息
            editable={{ onSave }}
          >
            <ProDescriptions.Item
              dataIndex="name"
              label={intl.formatMessage({ id: 'pages.account.identity_source_detail.name' })}
            />
            <ProDescriptions.Item
              dataIndex="remark"
              label={intl.formatMessage({ id: 'pages.account.identity_source_detail.remark' })}
              valueType={'textarea'}
              fieldProps={{ rows: 2, maxLength: 20 }}
            />
          </ProDescriptions>
        )
      }
    </RouteContext.Consumer>
  );

  return (
    <PageContainer
      onBack={() => {
        history.push('/account/identity-source');
      }}
      title={
        detailLoading ? (
          <Skeleton.Input style={{ width: 50 }} active size={'small'} />
        ) : (
          detail?.name
        )
      }
      content={content}
      tabList={[
        {
          key: IdentitySourceDetailTabs.config,
          tab: intl.formatMessage({
            id: 'pages.account.identity_source_detail.page_container.tab_list.config',
          }),
        },
        {
          key: IdentitySourceDetailTabs.sync_history,
          tab: intl.formatMessage({
            id: 'pages.account.identity_source_detail.page_container.tab_list.sync_history',
          }),
        },
        detail?.provider !== IdentitySourceProvider.ldap &&
        detail?.provider !== IdentitySourceProvider.ad
          ? {
              key: IdentitySourceDetailTabs.event_record,
              tab: intl.formatMessage({
                id: 'pages.account.identity_source_detail.page_container.tab_list.event_record',
              }),
            }
          : {},
      ]}
      tabActiveKey={tabActiveKey}
      onTabChange={(key: string) => {
        setTabActiveKey(key);
        history.replace({
          pathname: location.pathname,
          search: queryString.stringify({ type: key, id: id }),
        });
      }}
    >
      {/*同步配置*/}
      {IdentitySourceDetailTabs.config === tabActiveKey && (
        <ProCard>
          <Spin spinning={detailLoading}>
            <StepsForm
              containerStyle={{ width: '100%' }}
              current={current}
              onCurrentChange={setCurrent}
              formProps={{ layout: 'horizontal', ...ConfigFormLayout }}
              onFinish={async (config: Record<string, any>) => {
                try {
                  setDetailLoading(true);
                  const { result, success } = await saveIdentitySourceConfig({
                    id,
                    ...omit(config, 'strategyConfig'),
                    strategyConfig: {
                      ...config.strategyConfig,
                      organization: {
                        targetId: config?.strategyConfig?.organization?.targetId?.value,
                      },
                    },
                  });
                  if (success && result) {
                    setCurrent(0);
                    message.success(intl.formatMessage({ id: 'app.operation_success' }));
                    await getIdentitySource(id);
                    return;
                  }
                  setDetailLoading(false);
                } catch (e) {
                } finally {
                  setDetailLoading(false);
                }
              }}
              submitter={{
                render: (_, dom) => {
                  return <FooterToolbar>{dom}</FooterToolbar>;
                },
              }}
            >
              {/*提供商配置*/}
              <StepsForm.StepForm
                scrollToFirstError
                name="base-config"
                title={intl.formatMessage({
                  id: 'pages.account.identity_source_detail.step_form.base_config',
                })}
                formRef={baseFormRef}
                className={styles.step_form}
                onFinish={async () => {
                  const connect = basicConfigRef.current?.configValidator();
                  return Promise.resolve(connect);
                }}
              >
                <Spin spinning={configLoading}>
                  <BasicConfig
                    provider={detail?.provider}
                    configured={configured}
                    formRef={baseFormRef}
                    basicConfigRef={basicConfigRef}
                    onConfigValidator={async (config) => {
                      setDetailLoading(true);
                      const { result } = await identitySourceConfigValidator({
                        provider: detail?.provider,
                        config: config,
                      }).finally(() => {
                        setDetailLoading(false);
                      });
                      return result;
                    }}
                  />
                </Spin>
              </StepsForm.StepForm>
              {/*策略配置*/}
              <StepsForm.StepForm
                scrollToFirstError
                name="strategy-config"
                title={intl.formatMessage({
                  id: 'pages.account.identity_source_detail.step_form.strategy_config',
                })}
                formRef={strategyFormRef}
              >
                {configured !== undefined && <StrategyConfig configured={configured} />}
              </StepsForm.StepForm>
              {/*任务配置*/}
              <StepsForm.StepForm
                scrollToFirstError
                name="job-config"
                title={intl.formatMessage({
                  id: 'pages.account.identity_source_detail.step_form.job_config',
                })}
                formRef={jobFormRef}
              >
                {configured !== undefined && <JobConfig configured={configured} />}
              </StepsForm.StepForm>
            </StepsForm>
          </Spin>
        </ProCard>
      )}
      {/*同步记录*/}
      {IdentitySourceDetailTabs.sync_history === tabActiveKey && (
        <SyncHistory identitySourceId={id} />
      )}
      {/*事件记录*/}
      {IdentitySourceProvider.ldap !== detail?.provider &&
        IdentitySourceProvider.ad !== detail?.provider &&
        IdentitySourceDetailTabs.event_record === tabActiveKey && (
          <EventRecord identitySourceId={id} />
        )}
    </PageContainer>
  );
};
