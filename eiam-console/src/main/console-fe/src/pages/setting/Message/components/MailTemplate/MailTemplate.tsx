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
import { getMailTemplateList } from '../../service';

import { ProCard, ProList } from '@ant-design/pro-components';
import { useAsyncEffect } from 'ahooks';
import { Space, Spin, Tag } from 'antd';
import React, { useState } from 'react';
import MailTemplateBrowse from './MailTemplateBrowse';
import MailTemplateConfig from './MailTemplateConfig';
import useStyle from './style';
import classnames from 'classnames';
import { useIntl } from '@umijs/max';

const prefixCls = 'setting-mail-template';

export default (props: { visible: boolean }) => {
  const { visible } = props;
  const [configVisible, setConfigVisible] = useState<boolean>(false);
  const [title, setTitle] = useState<string | React.ReactNode>();
  const [content, setContent] = useState<string>('');
  const [configType, setConfigType] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(false);
  const [dataSource, setDataSource] = useState<SettingAPI.EmailTemplateList[]>([]);
  const [browseVisible, setBrowseVisible] = useState<boolean>(false);
  const { styles } = useStyle(prefixCls);
  const intl = useIntl();

  /**
   * 获取列表
   */
  async function getList() {
    setLoading(true);
    // 加载列表
    const { success, result } = await getMailTemplateList();
    if (success) {
      setDataSource(result);
    }
    setLoading(false);
  }

  useAsyncEffect(async () => {
    if (visible) {
      await getList();
    }
  }, [visible]);

  /**
   * 配置
   *
   * @param configTitle
   * @param type
   */
  const configOnClick = (configTitle: string, type: string) => {
    setTitle(<span>{`${configTitle}`}</span>);
    setConfigVisible(true);
    setConfigType(type);
  };
  /**
   * 浏览
   *
   * @param browseTitle
   */
  const browseOnClick = (browseTitle: string, browseContent: string) => {
    setTitle(<span>{`${browseTitle}`}</span>);
    setBrowseVisible(true);
    setContent(browseContent);
  };
  return (
    <div className={styles.main}>
      <ProCard className={`${prefixCls}`}>
        <Spin spinning={loading}>
          <ProList<SettingAPI.EmailTemplateList>
            rowKey="type"
            split
            cardProps={{ bodyStyle: { padding: 0 } }}
            showActions="always"
            dataSource={dataSource}
            metas={{
              title: {
                render: (text, row) => [
                  <p
                    key={row.name}
                    onClick={() => {
                      configOnClick(row.name, row.code);
                    }}
                    className={classnames(`${prefixCls}-title`)}
                  >
                    {row.name}
                  </p>,
                ],
              },
              description: {},
              subTitle: {
                render: (text, row) => {
                  return (
                    <Space size={0}>
                      {row.custom && (
                        <Tag key={'customize'} color="#108ee9">
                          {intl.formatMessage({
                            id: 'pages.setting.message.mail_template.sub_title',
                          })}
                        </Tag>
                      )}
                    </Space>
                  );
                },
              },
              actions: {
                render: (text, row) => {
                  return [
                    <a
                      key="config"
                      onClick={() => {
                        configOnClick(row.name, row.code);
                      }}
                    >
                      {intl.formatMessage({
                        id: 'pages.setting.message.mail_template_config',
                      })}
                    </a>,
                    <a
                      key="browse"
                      onClick={() => {
                        browseOnClick(row.name, row.content);
                      }}
                    >
                      {intl.formatMessage({
                        id: 'pages.setting.message.mail_template.actions.browse',
                      })}
                    </a>,
                  ];
                },
              },
            }}
          />
          {/* 邮件模板配置 */}
          <MailTemplateConfig
            visible={configVisible}
            type={configType}
            onClose={async () => {
              setConfigVisible(false);
              await getList();
            }}
            onSubmit={async () => {
              await getList();
            }}
            title={title}
          />
          {/* 邮件模板浏览 */}
          <MailTemplateBrowse
            content={content}
            title={title}
            visible={browseVisible}
            onCancel={() => {
              setBrowseVisible(false);
            }}
          />
        </Spin>
      </ProCard>
    </div>
  );
};
