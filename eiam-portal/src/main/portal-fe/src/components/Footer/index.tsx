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
import { GithubOutlined } from '@ant-design/icons';
import type { FooterProps } from '@ant-design/pro-components';
import { DefaultFooter } from '@ant-design/pro-components';

const currentYear = new Date().getFullYear();

export default (props: FooterProps) => (
  <DefaultFooter
    style={{ backgroundColor: 'transparent' }}
    copyright={`2020-${currentYear} TopIAM 版权所有`}
    links={[
      {
        key: 'website',
        title: '官方网站',
        href: 'https://eiam.topiam.cn',
        blankTarget: true,
      },
      {
        key: 'github',
        title: <GithubOutlined />,
        href: 'https://github.com/topiam/eiam',
        blankTarget: true,
      },
      {
        key: 'docs',
        title: '使用文档',
        href: 'https://eiam.topiam.cn/docs/introduction/overview',
        blankTarget: true,
      },
    ]}
    {...props}
  />
);
