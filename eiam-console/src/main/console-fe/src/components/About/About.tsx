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
import React from 'react';
import { Image, Modal, Space, Typography } from 'antd';
import { InfoCircleFilled } from '@ant-design/icons';
import { FormattedMessage } from '@umijs/max';
import { useSafeState } from 'ahooks';
import { createStyles } from 'antd-style';

const { Text, Link } = Typography;

const useStyle = createStyles({
  main: {
    display: 'flex',
    gap: '40px',
    padding: '20px 0',
  },
});
const About: React.FC = () => {
  const { styles } = useStyle();

  const [aboutOpen, setAboutOpen] = useSafeState(false);

  return (
    <>
      <InfoCircleFilled
        key="InfoCircleFilled"
        onClick={() => {
          setAboutOpen(true);
        }}
      />
      <Modal
        title={<FormattedMessage id={'component.right_content.about.title'} />}
        open={aboutOpen}
        centered
        footer={null}
        width={650}
        onCancel={() => setAboutOpen(false)}
      >
        <div className={styles.main}>
          <Image width={200} src={'/full-logo.svg'} preview={false} />
          <Space direction="vertical">
            <Text>产品：TopIAM 企业数字身份管控平台</Text>
            <Text>版本：社区版 1.0.0 </Text>
            <Link href="https://eiam.topiam.cn" target="_blank">
              https://eiam.topiam.cn
            </Link>
          </Space>
        </div>
      </Modal>
    </>
  );
};
export default About;
