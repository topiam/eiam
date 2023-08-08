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
import classnames from 'classnames';
import useStyle from './style';

type IconFontProps = {
  name: string;
};
const prefixCls = 'topiam-icon';

/**
 * Icon Font
 * https://www.iconfont.cn/help/detail?helptype=code
 */
export default (props: IconFontProps) => {
  const { name } = props;
  const { wrapSSR, hashId } = useStyle(prefixCls);

  return wrapSSR(
    <svg className={classnames(`${prefixCls}`, hashId)} aria-hidden="true">
      <use xlinkHref={`#${name}`} />
    </svg>,
  );
};
