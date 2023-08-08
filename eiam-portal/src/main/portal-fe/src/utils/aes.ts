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
import CryptoJS from 'crypto-js';

/**
 * AES-256-ECB对称加密
 * @param text {string} 要加密的明文
 * @param secretKey {string} 密钥，43位随机大小写与数字
 * @returns {string} 加密后的密文，Base64格式
 */
export const aesEcbEncrypt = (text: string, secretKey: string) => {
  const keyHex = CryptoJS.enc.Base64.parse(secretKey);
  const messageHex = CryptoJS.enc.Utf8.parse(text);
  const encrypted = CryptoJS.AES.encrypt(messageHex, keyHex, {
    mode: CryptoJS.mode.ECB,
    padding: CryptoJS.pad.Pkcs7,
  });
  return encrypted.toString();
};

/**
 * AES-256-ECB对称解密
 * @param textBase64 {string} 要解密的密文，Base64格式
 * @param secretKey {string} 密钥，43位随机大小写与数字
 * @returns {string} 解密后的明文
 */
export function aesEcbDecrypt(textBase64: string | CryptoJS.lib.CipherParams, secretKey: string) {
  const keyHex = CryptoJS.enc.Base64.parse(secretKey);
  const decrypt = CryptoJS.AES.decrypt(textBase64, keyHex, {
    mode: CryptoJS.mode.ECB,
    padding: CryptoJS.pad.Pkcs7,
  });
  return CryptoJS.enc.Utf8.stringify(decrypt);
}

/**
 * AES-256-CBC对称加密
 * @param text {string} 要加密的明文
 * @param secretKey {string} 密钥，43位随机大小写与数字
 * @returns {string} 加密后的密文，Base64格式
 */
export function aesBbcEncrypt({ text, secretKey }: { text: any; secretKey: any }) {
  const keyHex = CryptoJS.enc.Base64.parse(secretKey);
  const ivHex = keyHex.clone();
  // 前16字节作为向量
  ivHex.sigBytes = 16;
  ivHex.words.splice(4);
  const messageHex = CryptoJS.enc.Utf8.parse(text);
  const encrypted = CryptoJS.AES.encrypt(messageHex, keyHex, {
    iv: ivHex,
    mode: CryptoJS.mode.CBC,
    padding: CryptoJS.pad.Pkcs7,
  });
  return encrypted.toString();
}

/**
 * AES-256-CBC对称解密
 * @param textBase64 {string} 要解密的密文，Base64格式
 * @param secretKey {string} 密钥，43位随机大小写与数字
 * @returns {string} 解密后的明文
 */
export function aesCbcDecrypt({ textBase64, secretKey }: { textBase64: any; secretKey: any }) {
  const keyHex = CryptoJS.enc.Base64.parse(secretKey);
  const ivHex = keyHex.clone();
  // 前16字节作为向量
  ivHex.sigBytes = 16;
  ivHex.words.splice(4);
  const decrypt = CryptoJS.AES.decrypt(textBase64, keyHex, {
    iv: ivHex,
    mode: CryptoJS.mode.CBC,
    padding: CryptoJS.pad.Pkcs7,
  });
  return CryptoJS.enc.Utf8.stringify(decrypt);
}
