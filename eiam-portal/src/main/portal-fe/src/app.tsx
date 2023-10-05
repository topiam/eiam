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
import { isLoginPath, isSessionExpiredPath, LOGIN_PATH } from '@/utils/utils';
import type {
  MenuDataItem,
  ProLayoutProps,
  Settings as LayoutSettings,
} from '@ant-design/pro-components';
import queryString from 'query-string';
import { history, RunTimeLayoutConfig, SelectLang } from '@umijs/max';
import defaultSettings from '../config/defaultSettings';
import { getCurrentUser } from './services';
import { requestConfig } from '@/request';
import PageLoading from '@/components/PageLoading';
import React from 'react';
import Banner from '@/components/Banner';
import { AvatarProps } from 'antd';
import { GithubFilled, QuestionCircleFilled } from '@ant-design/icons';
import About from './components/About';
import { AvatarDropdown, AvatarName } from '@/components/RightContent/AvatarDropdown';

const showBanner = process.env.PREVIEW_ENV || process.env.NODE_ENV === 'development';

/**
 * 跳转登录页面
 */
const goLogin = () => {
  const query = queryString.parse(history.location.search);
  const { redirect } = query as { redirect: string };
  history.replace({
    pathname: LOGIN_PATH,
    search:
      redirect &&
      queryString.stringify({
        redirect: redirect,
      }),
  });
};

/**
 * 获取当前用户信息
 */
const fetchUserInfo = async (): Promise<API.CurrentUser | undefined> => {
  const result = await getCurrentUser().catch(() => undefined);
  if (result?.success && result.result) {
    return result.result;
  }
  return undefined;
};

export async function getInitialState(): Promise<{
  fetchUserInfo: () => Promise<API.CurrentUser | undefined>;
  settings: Partial<LayoutSettings>;
  currentUser: API.CurrentUser | undefined;
}> {
  /**
   * 控制台打印
   */
  console.log('%c欢迎使用 TopIAM 企业数字身份管控平台', 'font-size: 24px;');
  return {
    fetchUserInfo,
    currentUser: isLoginPath() ? undefined : await fetchUserInfo(),
    settings: {
      ...defaultSettings,
    } as Partial<LayoutSettings>,
  };
}

// ProLayout 支持的api https://procomponents.ant.design/components/layout
export const layout: RunTimeLayoutConfig = ({ initialState, loading }) => {
  return {
    bgLayoutImgList: [
      {
        src: 'https://img.alicdn.com/imgextra/i2/O1CN01O4etvp1DvpFLKfuWq_!!6000000000279-2-tps-609-606.png',
        left: 85,
        bottom: 100,
        height: '303px',
      },
      {
        src: 'https://img.alicdn.com/imgextra/i2/O1CN01O4etvp1DvpFLKfuWq_!!6000000000279-2-tps-609-606.png',
        bottom: -68,
        right: -45,
        height: '303px',
      },
      {
        src: 'https://img.alicdn.com/imgextra/i3/O1CN018NxReL1shX85Yz6Cx_!!6000000005798-2-tps-884-496.png',
        bottom: 0,
        left: 0,
        width: '331px',
      },
    ],
    bgLayout: '#fff',
    token: {
      sider: {
        colorMenuBackground: '#fff',
        colorTextMenu: '#595959',
        colorTextMenuSelected: 'rgba(42,122,251,1)',
        colorTextMenuItemHover: 'rgba(42,122,251,1)',
      },
      header: {
        colorBgHeader: '#fff',
        heightLayoutHeader: showBanner ? 78 : 56,
      },
      pageContainer: {
        paddingBlockPageContainerContent: 12,
        paddingInlinePageContainerContent: 24,
      },
    },
    headerRender: (props: ProLayoutProps, defaultDom: React.ReactNode) => {
      return showBanner ? (
        <>
          <Banner play={props.isMobile} />
          {React.cloneElement(defaultDom as any, {
            style: {
              height: '56px',
              lineHeight: '56px',
            },
          })}
        </>
      ) : (
        defaultDom
      );
    },
    avatarProps: {
      src: initialState?.currentUser?.avatar,
      size: 'small',
      title: <AvatarName />,
      render: (props: AvatarProps, defaultDom: React.ReactNode) => {
        return <AvatarDropdown>{defaultDom}</AvatarDropdown>;
      },
    },
    actionsRender: (props: ProLayoutProps) => {
      if (props.isMobile) return [];
      return [
        <About key={'about'} />,
        <QuestionCircleFilled
          key="QuestionCircleFilled"
          onClick={() => {
            window.open('https://eiam.topiam.cn');
          }}
        />,
        <GithubFilled
          key="GithubFilled"
          onClick={() => {
            window.open('https://github.com/topiam/eiam');
          }}
        />,
        <SelectLang key="SelectLang" />,
      ];
    },
    disableContentMargin: false,
    waterMarkProps: {
      content: initialState?.currentUser?.fullName,
    },
    childrenRender: (dom) => {
      if (loading) {
        return <PageLoading />;
      }
      return dom;
    },
    menuDataRender: (menuData: MenuDataItem[]) => {
      return menuData;
    },
    onPageChange: () => {
      let gotoLogin: boolean = false;
      //任何一项没有数据没有数据
      if (!initialState || !initialState?.currentUser) {
        gotoLogin = true;
      }
      // 上述判断需要登录、路径为登录路径，会话过期路径不拦截
      // prettier-ignore
      if (gotoLogin && (isLoginPath() || isSessionExpiredPath())) {
        return;
      }
      //登录页面
      if (gotoLogin) {
        goLogin();
        return;
      }
    },
    ...initialState?.settings,
  };
};

/**
 * @name request 配置，可以配置错误处理
 * 它基于 axios 和 ahooks 的 useRequest 提供了一套统一的网络请求和错误处理方案。
 * @doc https://umijs.org/docs/max/request#配置
 */
export const request = {
  ...requestConfig,
};
