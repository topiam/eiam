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
import { AppProtocolType } from '@/constant';
import { getApp } from '@/services/app';
import { ProCard } from '@ant-design/pro-components';

import { useAsyncEffect } from 'ahooks';
import { Skeleton } from 'antd';
import { useState } from 'react';
import FromConfig from './FromProtocolConfig';
import JwtConfig from './JwtProtocolConfig';
import OidcConfig from './OidcProtocolConfig';
import { GetApp } from '../../data';
import { useIntl } from '@@/exports';

export default (props: { appId: string }) => {
  const { appId } = props;
  const intl = useIntl();
  const [loading, setLoading] = useState<boolean>(true);
  const [app, setApp] = useState<GetApp>();
  useAsyncEffect(async () => {
    setLoading(true);
    const { result, success } = await getApp(appId);
    if (success && result) {
      setApp(result);
    }
    setLoading(false);
  }, []);

  const ComponentByKey = ({ key, app }: { key: string; app: GetApp }) => {
    const components = {
      [AppProtocolType.jwt]: JwtConfig,
      [AppProtocolType.oidc]: OidcConfig,
      [AppProtocolType.form]: FromConfig,
    };
    const Component = components[key];
    return <Component app={app} />;
  };
  return (
    <ProCard
      title={intl.formatMessage({ id: 'pages.app.config.items.login_access.protocol_config' })}
      style={{ height: 'calc(100vh - 178px)', overflow: 'auto' }}
      bodyStyle={{ height: '100%' }}
      headerBordered
    >
      <Skeleton loading={loading} active={true} paragraph={{ rows: 5 }}>
        {app && ComponentByKey({ key: app?.protocol, app: app })}
      </Skeleton>
    </ProCard>
  );
};
