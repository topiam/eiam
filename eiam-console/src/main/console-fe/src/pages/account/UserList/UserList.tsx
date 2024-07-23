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
import { PageContainer } from '@ant-design/pro-components';
import { Col, Row } from 'antd';
import { useState } from 'react';
import OrgTree from './components/Organization';
import UserList from './components/User';
import { useIntl } from '@umijs/max';

/**左侧布局*/
const leftLayout = { xxl: 5, lg: 6, md: 24, sm: 24, xs: 24 };
/**右侧布局*/
const rightLayout = { xxl: 19, lg: 18, md: 24, sm: 24, xs: 24 };
/**
 * 组织&用户
 */
export const User = () => {
  const [organization, setOrganization] = useState<{
    id: string | number;
    name: string;
  }>();

  /**
   * tree select
   *
   * @param id
   * @param name
   */
  const treeOnSelect = (id: string | number, name: string) => {
    setOrganization({ id: id, name });
  };
  const intl = useIntl();

  return (
    <PageContainer content={intl.formatMessage({ id: 'pages.account.user_list.desc' })}>
      <Row gutter={[16, 16]}>
        {/* 左侧 */}
        <Col {...leftLayout} style={{ minHeight: '100%', overflow: 'auto' }}>
          <OrgTree onSelect={treeOnSelect} />
        </Col>
        {/* 表格 */}
        <Col {...rightLayout} style={{ minHeight: '100%', overflow: 'auto' }}>
          <UserList organization={organization} />
        </Col>
      </Row>
    </PageContainer>
  );
};
export default () => {
  return <User />;
};
