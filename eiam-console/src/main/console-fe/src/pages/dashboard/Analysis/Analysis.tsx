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
import { GridContent } from '@ant-design/pro-components';
import { Col, Row } from 'antd';
import Overview from './components/Overview';
import HotAuthnProvider from './components/HotAuthnProvider';
import AuthnZone from './components/AuthnZone';
import AppVisitRank from './components/AppVisitRank';
import AuthnQuantity from './components/AuthnQuantity';

const Analysis = () => {
  return (
    <GridContent style={{ height: '100%' }}>
      {/*概述*/}
      <Overview />
      <Row gutter={[24, 24]}>
        <Col
          {...{
            xs: 24,
            sm: 24,
            md: 24,
            lg: 12,
            xl: 12,
            style: { marginBottom: 24 },
          }}
        >
          {/*授权数量*/}
          <AuthnQuantity />
        </Col>
        <Col
          {...{
            xs: 24,
            sm: 24,
            md: 24,
            lg: 12,
            xl: 12,
            style: { marginBottom: 24 },
          }}
        >
          {/*访问量统计*/}
          <AppVisitRank />
        </Col>
      </Row>
      <Row gutter={[24, 24]}>
        <Col
          {...{
            xs: 24,
            sm: 24,
            md: 24,
            lg: 12,
            xl: 12,
            style: { marginBottom: 24 },
          }}
        >
          {/*热点提供商*/}
          <HotAuthnProvider />
        </Col>
        <Col
          {...{
            xs: 24,
            sm: 24,
            md: 24,
            lg: 12,
            xl: 12,
            style: { marginBottom: 24 },
          }}
        >
          {/*认证区域*/}
          <AuthnZone />
        </Col>
      </Row>
    </GridContent>
  );
};
export default () => {
  return <Analysis />;
};
