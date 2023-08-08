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
 * 拷贝到剪切板
 * @param value
 */
export async function copyToClipboard(value: string) {
  return new Promise((resolve, reject) => {
    const textarea = document.createElement('textarea');
    document.body.appendChild(textarea);

    const textNode = document.createTextNode(value);
    textarea.appendChild(textNode);
    textarea.select();

    if (document.execCommand('copy')) {
      document.execCommand('copy');
      resolve(true);
    } else {
      reject(new Error('复制失败'));
    }
    document.body.removeChild(textarea);
  });
}
