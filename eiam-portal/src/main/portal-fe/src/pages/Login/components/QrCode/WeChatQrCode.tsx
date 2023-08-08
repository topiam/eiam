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
import { history } from '@@/core/history';
import { nanoid } from 'nanoid';
import queryString from 'query-string';

const WeChatQrCode = (props: { code: string }) => {
  const { code } = props;
  const query = queryString.parse(history.location.search);
  const { redirect_uri } = query as { redirect_uri: string };
  let path = `/api/v1/authorization/wechat_qr/${code}`;
  if (redirect_uri) {
    path = `${path}?redirect_uri=${redirect_uri}`;
  }
  return (
    <div id="wechat_login_container" key={nanoid()}>
      <iframe src={`${path}`} frameBorder="0" scrolling="no" width="365px" height="400px" />
    </div>
  );
};
export default WeChatQrCode;
