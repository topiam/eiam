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
import { Button } from 'antd';
import useStyle from './style';
import { useIntl } from '@@/exports';
const prefixCls = 'topiam-forget-password';

const Success = ({ close }: { close: () => void }) => {
  const { styles } = useStyle(prefixCls);
  const intl = useIntl();
  return (
    <div className={styles.main}>
      <div className={`${prefixCls}-success`}>
        <div className={`${prefixCls}-success-title`}>
          {intl.formatMessage({ id: 'pages.login.forget-password.success' })}
        </div>
        <div className={`${prefixCls}-success-desc`}>
          {intl.formatMessage({ id: 'pages.login.forget-password.success.desc' })}
        </div>
        <Button
          type="primary"
          className={`${prefixCls}-success-button`}
          size={'large'}
          onClick={() => close()}
        >
          {intl.formatMessage({ id: 'app.return' })}
        </Button>
      </div>
    </div>
  );
};
export default Success;
