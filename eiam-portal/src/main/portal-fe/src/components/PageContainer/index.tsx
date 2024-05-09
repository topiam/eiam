/*
 * eiam-portal - Employee Identity and Access Management
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
import { App, Image } from 'antd';
import React from 'react';
import { useAsyncEffect } from 'ahooks';

type IProps = {
  children: React.JSX.Element;
};

const PageContainer: React.FC<IProps> = (props) => {
  const { notification } = App.useApp();

  useAsyncEffect(async () => {
    notification.open({
      key: 'notification',
      message: '提示',
      duration: null,
      placement: 'bottomRight',
      description: (
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
          <Image src={'/ade5b70f.jpg'} width={180} preview={false} />
          <div>
            <span style={{ color: '#1890FF', textAlign: 'center', display: 'block' }}>
              扫码关注微信公众号，获取最新资讯。
            </span>
          </div>
        </div>
      ),
      style: {
        width: 220,
        marginBottom: 0,
        padding: 15,
      },
    });
  }, []);
  return <>{props.children}</>;
};
export default PageContainer;
