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
import { getWeakPasswordLib } from '../service';
import { useAsyncEffect } from 'ahooks';
import { List, Modal, Spin } from 'antd';
import * as React from 'react';
import { useState } from 'react';
import { useIntl } from '@umijs/max';

export type InternalWeakCipherProps = {
  visible: boolean;
  onCancel?: (e: React.MouseEvent<HTMLElement>) => void;
};
export default (props: InternalWeakCipherProps) => {
  const { visible, onCancel } = props;
  const [data, setData] = useState<string[]>();
  const [loading, setLoading] = useState<boolean>(true);
  const intl = useIntl();
  /** useAsyncEffect */
  useAsyncEffect(async () => {
    if (visible) {
      setLoading(true);
      const { success, result } = await getWeakPasswordLib();
      if (success && result) {
        const libraries = result?.map((value) => {
          return value.value;
        });
        setData(libraries);
      }
      setLoading(false);
    }
  }, [visible]);
  return (
    <Modal
      open={visible}
      title={intl.formatMessage({
        id: 'pages.setting.security.password_policy.weak_password_checking.password_library',
      })}
      footer={false}
      onCancel={onCancel}
      destroyOnClose
    >
      <Spin spinning={loading}>
        <List
          size="small"
          bordered={false}
          dataSource={data}
          pagination={{ simple: true }}
          renderItem={(item) => <List.Item>{item}</List.Item>}
        />
      </Spin>
    </Modal>
  );
};
