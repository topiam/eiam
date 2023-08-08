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
 * 服务异常状态
 */
export enum ServerExceptionStatus {
  /**密码验证失败错误 */
  PASSWORD_VALIDATED_FAIL_ERROR = 'password_validated_fail_error',
  /**无效的 MFA 代码错误 */
  INVALID_MFA_CODE_ERROR = 'invalid_mfa_code_error',
  /**MFA 未发现秘密错误 */
  BIND_MFA_NOT_FOUND_SECRET_ERROR = 'bind_mfa_not_found_secret_error',
}
