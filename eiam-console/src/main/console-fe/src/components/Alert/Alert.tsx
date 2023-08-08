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
import { Alert, AlertProps } from 'antd';
import React from 'react';
import { omit } from 'lodash';
export interface Props extends Omit<AlertProps, 'type'> {
  type?: 'success' | 'info' | 'warning' | 'error' | 'grey';
}
export default (props: Props) => {
  if (props.type === 'grey') {
    return (
      <Alert
        {...omit(props, 'type')}
        style={{ backgroundColor: '#f1f1f2', border: '1px solid #f1f1f2', ...props.style }}
      />
    );
  }
  return <Alert type={props.type} {...omit(props, 'type')} />;
};
