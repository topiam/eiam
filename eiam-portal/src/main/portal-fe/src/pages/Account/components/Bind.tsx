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
import { App, Avatar, Button, List, Popconfirm, Skeleton } from 'antd';
import React, { Fragment, useState } from 'react';
import { useIntl } from '@@/exports';
import { useAsyncEffect } from 'ahooks';
import { getBoundIdpList, unbindIdp } from '@/pages/Account/service';
import { GetBoundIdpList } from '@/pages/Account/data.d';
import { ICON_LIST } from '@/components/IconFont/constant';
import { QuestionCircleOutlined } from '@ant-design/icons';
import { createStyles } from 'antd-style';

const useStyle = createStyles(({ prefixCls }) => {
  const antCls = `.${prefixCls}`;
  return {
    main: {
      [`${antCls}-form-item`]: {
        [`${antCls}-form-item-control-input`]: {
          width: '100%',
        },
      },
      [`${antCls}-list-item-meta-title`]: {
        marginTop: '0px',
      },
      [`${antCls}-icon`]: {
        fontSize: '30px',
        height: '100%',
      },
    },
  };
});

const BindingView: React.FC = () => {
  const { styles } = useStyle();
  const intl = useIntl();
  const { message } = App.useApp();
  const [loading, setLoading] = useState<boolean>(false);
  const [boundIdpList, setBoundIdpList] = useState<GetBoundIdpList[]>([]);

  useAsyncEffect(async () => {
    setLoading(true);
    await getBindList();
  }, []);

  async function getBindList() {
    const { result, success } = await getBoundIdpList().finally(() => {
      setLoading(false);
    });
    if (success && result) {
      setBoundIdpList(result);
    }
  }

  const getData = () => {
    return boundIdpList?.map((idp) => {
      return {
        title: idp.name,
        description: idp.bound ? `已绑定${idp.name}账号` : `未绑定${idp.name}账号`,
        actions: [
          idp.bound ? (
            <Popconfirm
              title={intl.formatMessage({ id: 'pages.account.unbind.confirm' })}
              placement="bottomRight"
              icon={
                <QuestionCircleOutlined
                  style={{
                    color: 'red',
                  }}
                />
              }
              onConfirm={async () => {
                const { success } = await unbindIdp(idp.idpId);
                if (success) {
                  message.success(intl.formatMessage({ id: 'app.operation_success' }));
                  await getBindList();
                }
              }}
              okText={intl.formatMessage({ id: 'app.yes' })}
              cancelText={intl.formatMessage({ id: 'app.no' })}
              key="offline"
            >
              <Button type="link" danger key="unbind">
                {intl.formatMessage({ id: 'page.account.unbind' })}
              </Button>
            </Popconfirm>
          ) : (
            <Button type="link" key="bind" disabled>
              {intl.formatMessage({ id: 'page.account.bind' })}
            </Button>
          ),
        ],
        avatar: <Avatar shape="square" size={50} src={ICON_LIST[idp.type]} key={idp.code} />,
      };
    });
  };

  return (
    <Fragment>
      <Skeleton loading={loading} paragraph={{ rows: 5 }}>
        <List
          className={styles.main}
          itemLayout="horizontal"
          dataSource={getData()}
          renderItem={(item) => (
            <List.Item actions={item.actions}>
              <List.Item.Meta
                avatar={item.avatar}
                title={item.title}
                description={item.description}
              />
            </List.Item>
          )}
        />
      </Skeleton>
    </Fragment>
  );
};

export default BindingView;
