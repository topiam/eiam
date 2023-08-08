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
import { ArrowLeftOutlined, BulbTwoTone } from '@ant-design/icons';
import type { PageContainerProps } from '@ant-design/pro-components';
import { PageContainer as Container, RouteContext } from '@ant-design/pro-components';
import { Alert } from 'antd';
import React from 'react';
import styles from './styles.less';

type IProps = {
  description?: React.ReactNode | string;
} & PageContainerProps;

const PageContainer: React.FC<IProps> = (props) => {
  const { description, content, children, title = false } = props;
  return (
    <RouteContext.Consumer>
      {() => {
        return (
          <Container
            header={{
              backIcon: (
                <div className={styles.back}>
                  <ArrowLeftOutlined className={styles.backIcon} />
                </div>
              ),
            }}
            title={title}
            content={
              description ? (
                <Alert
                  banner
                  icon={<BulbTwoTone />}
                  showIcon
                  type="info"
                  message={false}
                  description={description}
                />
              ) : (
                content
              )
            }
            {...props}
          >
            {children}
          </Container>
        );
      }}
    </RouteContext.Consumer>
  );
};
export default PageContainer;
