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
import { useEffect, useRef, useState } from 'react';
import { ProFormInstance, SubmitterProps } from '@ant-design/pro-components';
import { useIntl } from '@@/plugin-locale';
import useStyle from './style';
import Code from './Code';
import PassWord from './Password';
import Success from './Success';
import { forgetPassword, prepareForgetPassword } from '@/pages/Login/service';
import { Button } from 'antd';
import { LeftOutlined } from '@ant-design/icons';
import { onGetEncryptSecret } from '@/utils/utils';
import { aesEcbEncrypt } from '@/utils/aes';

const prefixCls = 'topiam-forget-password';
export default ({ close }: { close: () => void }) => {
  const [step, setStep] = useState(1);
  const [form, setForm] = useState({ phone: '', code: '', newPassword: '', passwordAgain: '' });
  const [time, setTime] = useState(3);

  const intl = useIntl();
  const formRef = useRef<ProFormInstance>();
  const { styles } = useStyle(prefixCls);

  // 成功自动返回 计时
  useEffect(() => {
    if (step === 3 && time !== 0) {
      const timer = setTimeout(() => {
        setTime((pre) => pre - 1);
        clearTimeout(timer);
      }, 1000);
    }
    if (!time) {
      close();
    }
  }, [step, time]);

  // 提交按钮 style
  const submitter: SubmitterProps = {
    searchConfig: {
      submitText: intl.formatMessage({
        id: 'pages.login.forget-password.next-step',
      }),
    },
    render: (_, dom) => dom.pop(),
    submitButtonProps: {
      size: 'large',
      htmlType: 'submit',
      style: {
        width: '100%',
      },
    },
  };

  return (
    <div className={styles.main}>
      {/* 返回 */}
      {step !== 3 && (
        <div className={`${prefixCls}-back`}>
          <Button
            style={{ paddingLeft: 0 }}
            type={'text'}
            icon={<LeftOutlined />}
            onClick={() => (step === 2 ? setStep((pre) => pre - 1) : close())}
          >
            {intl.formatMessage({ id: 'app.return' })}
          </Button>
        </div>
      )}
      {/*手机号验证*/}
      {step === 1 && (
        <Code
          formRef={formRef}
          autoComplete="off"
          submitter={submitter}
          onFinish={async (values) => {
            const publicSecret = await onGetEncryptSecret();
            if (publicSecret) {
              const { phone, code } = values;
              const { success } = await prepareForgetPassword(
                aesEcbEncrypt(JSON.stringify({ recipient: phone, code }), publicSecret),
              );
              if (success) {
                setStep((pre) => pre + 1);
                setForm((pre) => ({ ...pre, ...values }));
              }
            }
          }}
        />
      )}
      {/*新密码*/}
      {step === 2 && (
        <PassWord
          phone={form.phone}
          formRef={formRef}
          autoComplete="off"
          submitter={submitter}
          onFinish={async (values) => {
            const publicSecret = await onGetEncryptSecret();
            if (publicSecret) {
              const { success } = await forgetPassword(
                aesEcbEncrypt(JSON.stringify(values), publicSecret),
              );
              if (success) {
                setStep((pre) => pre + 1);
                setForm((pre) => ({ ...pre, ...values }));
              }
            }
          }}
        />
      )}
      {/*成功*/}
      {step === 3 && <Success close={close} />}
      {/*成功 自动返回*/}
      {step === 3 && (
        <div className={`${prefixCls}-back-time`}>
          {time} {intl.formatMessage({ id: 'pages.login.forget-password.back-time' })}
        </div>
      )}
    </div>
  );
};
