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
import React from 'react';
import { Col, Divider, Image, Modal, Row, Space, Typography } from 'antd';
import { CopyrightOutlined, InfoCircleFilled } from '@ant-design/icons';
import { FormattedMessage } from '@umijs/max';
import { useSafeState } from 'ahooks';
import { createStyles } from 'antd-style';

const { Text, Link } = Typography;

const useStyle = createStyles({
  main: {
    padding: '10px 0',
  },
});
const About: React.FC = () => {
  const { styles } = useStyle();
  const currentYear = new Date().getFullYear();
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
          <Row gutter={16}>
            <Col span={6} style={{ borderRight: '3px solid #f0f2f5' }}>
              <Image src={'/full-logo.svg'} preview={false} />
              <Divider dashed style={{ marginBottom: 10, marginTop: 10 }} />
              <Image src={'/ade5b70f.jpg'} preview={false} />
              <Text style={{ textAlign: 'center', display: 'block' }}>微信公众号</Text>
            </Col>
            <Col span={18}>
              <Space direction="vertical" size={'middle'}>
                <Text>产品：TOPIAM 企业数字身份管控平台</Text>
                <Text>版本：社区版 1.1.0</Text>
                <Text>
                  版权所有 <CopyrightOutlined /> {'济南源创网络科技有限公司'} 2022-{currentYear}
                  。保留一切权利。
                </Text>
                <Text>
                  警告：本软件受著作权法和国际版权条约的保护，未经授权擅自复制、修改、分发本程序的全部或任何部分，将要承担一切由此导致的民事或刑事责任。
                </Text>
                <Link href="https://topiam.cn" target="_blank">
                  https://topiam.cn
                </Link>
              </Space>
            </Col>
          </Row>
        </div>
      </Modal>
    </>
  );
};
export default About;
