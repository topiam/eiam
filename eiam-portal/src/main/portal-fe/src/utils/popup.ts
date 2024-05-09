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
/**
 * 打开子窗口
 *
 * @param width
 * @param height
 * @param path
 * @param handlerMessage
 */
export const openPopup = (
  path: string,
  handlerMessage: (arg1: MessageEvent, arg2: Window | null) => void,
  width: number = 600,
  height: number = 600,
) => {
  // 计算窗口在屏幕上居中的位置
  const screenLeft = window.screenLeft !== undefined ? window.screenLeft : window.screenX;
  const screenTop = window.screenTop !== undefined ? window.screenTop : window.screenY;
  const innerWidth = window.innerWidth
    ? window.innerWidth
    : document.documentElement.clientWidth
      ? document.documentElement.clientWidth
      : window.screen.width;
  const innerHeight = window.innerHeight
    ? window.innerHeight
    : document.documentElement.clientHeight
      ? document.documentElement.clientHeight
      : window.screen.height;
  const left = (innerWidth - width) / 2 + screenLeft;
  const top = (innerHeight - height) / 2 + screenTop;
  const popup = window.open(
    path,
    'popup',
    `height=${height}, width=${width}, top=${top},left=${left}, toolbar=no, menubar=no, scrollbars=no, resizable=no,location=n o, status=no`,
  );

  // @ts-ignore
  if (typeof window.addEventListener !== 'undefined') {
    window.addEventListener('message', (e) => handlerMessage(e, popup), false);
    // @ts-ignore
  } else if (typeof window.attachEvent !== 'undefined') {
    // @ts-ignore
    window.attachEvent('onmessage', (e) => handlerMessage(e, popup));
  }
};
