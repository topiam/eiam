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
import { App, Avatar, Button, List, Skeleton } from 'antd';
import React, { Fragment, useState } from 'react';
import { history, useIntl } from '@@/exports';
import { useAsyncEffect } from 'ahooks';
import { getBoundIdpList, unbindIdp } from '@/pages/Account/service';
import { GetBoundIdpList } from '@/pages/Account/data.d';
import { ICON_LIST } from '@/components/IconFont/constant';
import { createStyles } from 'antd-style';
import { openPopup } from '@/utils/popup';
import queryString from 'query-string';

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
  const { message, modal } = App.useApp();
  const [loading, setLoading] = useState<boolean>(false);
  const [boundIdpList, setBoundIdpList] = useState<GetBoundIdpList[]>([]);

  async function getBindList() {
    const { result, success } = await getBoundIdpList().finally(() => {
      setLoading(false);
    });
    if (success && result) {
      setBoundIdpList(result);
    }
  }

  useAsyncEffect(async () => {
    setLoading(true);
    await getBindList();
  }, []);

  const getData = () => {
    return boundIdpList?.map((idp) => {
      return {
        title: idp.name,
        description: idp.bound ? `已绑定${idp.name}账号` : `未绑定${idp.name}账号`,
        actions: [
          idp.bound ? (
            <Button
              type="link"
              danger
              key="unbind"
              onClick={(event) => {
                event.stopPropagation();
                modal.warning({
                  closable: false,
                  maskClosable: false,
                  title: '系统通知',
                  content: intl.formatMessage({ id: 'page.account.unbind_confirm_content' }),
                  okText: intl.formatMessage({ id: 'app.confirm' }),
                  okType: 'primary',
                  centered: true,
                  okCancel: true,
                  cancelText: intl.formatMessage({ id: 'app.cancel' }),
                  onOk: async () => {
                    const { success } = await unbindIdp(idp.id);
                    if (success) {
                      message.success('解绑成功');
                      await getBindList();
                    }
                  },
                });
              }}
            >
              {intl.formatMessage({ id: 'page.account.unbind' })}
            </Button>
          ) : (
            <Button
              type="link"
              key="bind"
              onClick={() => {
                const query = queryString.parse(history.location.search);
                const { redirect_uri } = query as { redirect_uri: string };
                let path = idp.authorizationUri;
                if (redirect_uri) {
                  path = `${path}?redirect_uri=${redirect_uri}`;
                }
                openPopup(path, async (event, popup) => {
                  if (event.source !== popup) {
                    return;
                  }
                  const result = JSON.parse(event.data);
                  if (result.success) {
                    // 查询数据
                    message.success(intl.formatMessage({ id: 'app.operation_success' }));
                    setLoading(true);
                    await getBindList();
                  } else {
                    message.error(result.message);
                  }
                });
              }}
            >
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
