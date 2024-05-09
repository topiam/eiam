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

import { useAsyncEffect } from 'ahooks';
import { Skeleton } from 'antd';
import { useState } from 'react';
import FromConfig from './FromProtocolConfig';
import JwtConfig from './JwtProtocolConfig';
import OidcConfig from './OidcProtocolConfig';
import { GetApp } from '../../data.d';
import { useModel } from '@@/exports';

export default () => {
  const { app } = useModel('app.AppDetail.model');
  const [loading, setLoading] = useState<boolean>(true);
  useAsyncEffect(async () => {
    setLoading(true);

    setLoading(false);
  }, [app]);

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
    <Skeleton loading={loading} active={true} paragraph={{ rows: 5 }}>
      {app && ComponentByKey({ key: app?.protocol, app: app })}
    </Skeleton>
  );
};
