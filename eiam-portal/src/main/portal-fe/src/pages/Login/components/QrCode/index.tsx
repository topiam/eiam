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
import { IDP_TYPE } from '@/constants';
import { Tabs } from 'antd';
import DingTalkQrCode from './DingTalkQrCode';
import WeChatQrCode from './WeChatQrCode';
import WeWorkQrCode from './WeWorkQrCode';
import { createStyles } from 'antd-style';

/**
 * 扫码登录
 * @constructor
 */
const useStyle = createStyles({
  main: {
    ['.qr']: {
      position: 'relative',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      boxSizing: 'border-box',
      width: '328px',
      overflow: 'hidden',
      ['.code']: {
        height: '289px',
      },
    },
  },
});
const Index = (props: { code: string; name: string; type: IDP_TYPE | string }) => {
  const { type, name, code } = props;
  const { styles } = useStyle();

  return (
    <div className={styles.main}>
      <Tabs activeKey={type} items={[{ label: name, key: type }]} />
      <div className={'qr'}>
        <div className={'code'}>
          {/*企业微信扫码*/}
          {type === IDP_TYPE.WECHATWORK_QR && <WeWorkQrCode code={code} />}
          {/*钉钉扫码*/}
          {type === IDP_TYPE.DINGTALK_QR && <DingTalkQrCode code={code} />}
          {/*微信扫码*/}
          {type === IDP_TYPE.WECHAT_QR && <WeChatQrCode code={code} />}
        </div>
      </div>
    </div>
  );
};
export default Index;
