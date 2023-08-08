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
import { useMount } from 'ahooks';
import { nanoid } from 'nanoid';
import { useEffect, useState } from 'react';
import { getDingtalkAuthorizeUrl } from '@/pages/Login/service';
import queryString from 'query-string';

/**
 * https://open.dingtalk.com/document/orgapp-server/scan-qr-code-to-log-on-to-third-party-websites
 *
 * @param props
 * @constructor
 */
const DingTalkQrCode = (props: { code: string }) => {
  const { code } = props;
  // 授权URL
  const [authorizeUrl, setAuthorizeUrl] = useState<string>();

  useMount(async () => {
    const query = queryString.parse(history.location.search);
    const { redirect_uri } = query as { redirect_uri: string };
    const { result, success } = await getDingtalkAuthorizeUrl(code, redirect_uri);
    if (success) {
      setAuthorizeUrl(result);
    }
  });

  useEffect(() => {
    /**
     * 监听消息处理方法
     * @param event
     */
    const handleMessage = (event: any) => {
      // 获取loginTempCode
      const loginTempCode = event.data;
      // 获取消息来源
      const { origin } = event;
      // 拼接 url
      const url = `${authorizeUrl}&loginTmpCode=${loginTempCode}`;
      // 如果来源为https://login.dingtalk.com，则在当前窗口打开回调链接
      if (origin === 'https://login.dingtalk.com') {
        window.open(encodeURI(url), '_parent');
      }
    };
    // 监听iframe的消息
    if (authorizeUrl) {
      // @ts-ignore
      if (typeof window.addEventListener !== 'undefined') {
        window.addEventListener('message', handleMessage, false);
        // @ts-ignore
      } else if (typeof window.attachEvent !== 'undefined') {
        // @ts-ignore
        window.attachEvent('onmessage', handleMessage);
      }
    }
  }, [authorizeUrl]);

  return (
    <>
      {authorizeUrl && (
        <div id="dingtalk_login_container" key={nanoid()}>
          <iframe
            width={365}
            height={400}
            frameBorder={0}
            scrolling={'none'}
            style={{ marginTop: -15 }}
            src={`https://login.dingtalk.com/login/qrcode.htm?goto=${encodeURIComponent(
              authorizeUrl,
            )}&style=border%3Anone%3Bbackground-color%3A%23fff%3B`}
          />
        </div>
      )}
    </>
  );
};
export default DingTalkQrCode;
