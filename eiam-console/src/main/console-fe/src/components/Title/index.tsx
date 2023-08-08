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
import { Button } from 'antd';
import { history } from '@umijs/max';
import classnames from 'classnames';
import useStyle from './style';
import React from 'react';

const prefixCls = 'topiam-title-wrapper';
export type IProps = {
  title: string | React.ReactNode;
  aside?: any;
  size?: 'h1' | 'h2' | 'h3';
  hasBack?: boolean;
};

const fontsize = {
  h1: '30px',
  h2: '20px',
  h3: '16px',
};

export const Title = (props: IProps) => {
  const { aside = null, hasBack = false, title, size = 'h1' } = props;
  const { wrapSSR, hashId } = useStyle(prefixCls);

  const style = {
    fontSize: fontsize[size],
  };

  const onBack = () => {
    history.back();
  };
  return wrapSSR(
    <div className={classnames(`${prefixCls}`, hashId)}>
      <div className={classnames(`${prefixCls}-title`, hashId)} style={style}>
        <span>{title}</span>
        {hasBack && (
          <Button
            size="small"
            onClick={onBack}
            className={classnames(`${prefixCls}-title-back-btn`, hashId)}
          >
            返回
          </Button>
        )}
      </div>

      <div>{aside}</div>
    </div>,
  );
};
export default Title;
