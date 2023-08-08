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
import { GridContent } from '@ant-design/pro-components';
import { Card } from 'antd';
import React from 'react';

/**
 * 每个单独的卡片，为了复用样式抽成了组件
 * @param param0
 * @returns
 */
const InfoCard: React.FC<{
  title: string;
  index: number;
  desc: string;
  href: string;
}> = ({ title, href, index, desc }) => {
  return (
    <div
      style={{
        backgroundColor: '#FFFFFF',
        boxShadow: '0 2px 4px 0 rgba(35,49,128,0.02), 0 4px 8px 0 rgba(49,69,179,0.02)',
        borderRadius: '8px',
        fontSize: '14px',
        color: 'rgba(0,0,0,0.65)',
        textAlign: 'justify',
        lineHeight: ' 22px',
        padding: '16px 19px',
        flex: 1,
      }}
    >
      <div
        style={{
          display: 'flex',
          gap: '4px',
          alignItems: 'center',
        }}
      >
        <div
          style={{
            width: 48,
            height: 48,
            lineHeight: '22px',
            backgroundSize: '100%',
            textAlign: 'center',
            padding: '8px 16px 16px 12px',
            color: '#FFF',
            fontWeight: 'bold',
            backgroundImage:
              "url('data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHN2ZyB3aWR0aD0iNTRweCIgaGVpZ2h0PSI1MXB4IiB2aWV3Qm94PSIwIDAgNTQgNTEiIHZlcnNpb249IjEuMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayI+CiAgPHRpdGxlPuefqeW9ojwvdGl0bGU+CiAgPGRlZnM+CiAgICA8bGluZWFyR3JhZGllbnQgeDE9IjMzLjAwNjM5OTclIiB5MT0iMzMuMTAxNzc3MiUiIHgyPSI2OC4yMzAzNTYzJSIgeTI9IjczLjU1Mzk3ODclIiBpZD0ibGluZWFyR3JhZGllbnQtMSI+CiAgICAgIDxzdG9wIHN0b3AtY29sb3I9IiM0NzkxRkYiIG9mZnNldD0iMCUiPjwvc3RvcD4KICAgICAgPHN0b3Agc3RvcC1jb2xvcj0iIzJBNjVFQyIgb2Zmc2V0PSIxMDAlIj48L3N0b3A+CiAgICA8L2xpbmVhckdyYWRpZW50PgogICAgPHBhdGggZD0iTTcuNDkyMjE0NCwwIEwyOC40OTI2MDc0LDAgQzMwLjA5OTI1MzksLTQuMDM3MjkyMThlLTE1IDMxLjQwMTY5ODMsMS4zMDI0NDQzNiAzMS40MDE2OTgzLDIuOTA5MDkwOTEgQzMxLjQwMTY5ODMsMy4wODk1MzczMyAzMS4zODQ5MDkxLDMuMjY5NTkyMzggMzEuMzUxNTQ3OSwzLjQ0NjkyODA1IEwyNy4zNjY3MjYyLDI0LjYyODc0NjIgQzI3LjEwODAzMzEsMjYuMDAzODYxNyAyNS45MDcwMjI3LDI3IDI0LjUwNzc4NTYsMjcgTDMuNTA3MzkyNjMsMjcgQzEuOTAwNzQ2MDksMjcgMC41OTgzMDE3MjQsMjUuNjk3NTU1NiAwLjU5ODMwMTcyNCwyNC4wOTA5MDkxIEMwLjU5ODMwMTcyNCwyMy45MTA0NjI3IDAuNjE1MDkwODY1LDIzLjczMDQwNzYgMC42NDg0NTIwNzMsMjMuNTUzMDcxOSBMNC42MzMyNzM4NCwyLjM3MTI1Mzc3IEM0Ljg5MTk2NjkyLDAuOTk2MTM4MzI1IDYuMDkyOTc3MjUsMS4xNDUyMTQxMWUtMTUgNy40OTIyMTQ0LDAgWiIgaWQ9InBhdGgtMiI+PC9wYXRoPgogICAgPGZpbHRlciB4PSItNTcuOCUiIHk9Ii01Ny40JSIgd2lkdGg9IjIyOC4xJSIgaGVpZ2h0PSIyNTEuOSUiIGZpbHRlclVuaXRzPSJvYmplY3RCb3VuZGluZ0JveCIgaWQ9ImZpbHRlci0zIj4KICAgICAgPGZlT2Zmc2V0IGR4PSIyIiBkeT0iNSIgaW49IlNvdXJjZUFscGhhIiByZXN1bHQ9InNoYWRvd09mZnNldE91dGVyMSI+PC9mZU9mZnNldD4KICAgICAgPGZlR2F1c3NpYW5CbHVyIHN0ZERldmlhdGlvbj0iNiIgaW49InNoYWRvd09mZnNldE91dGVyMSIgcmVzdWx0PSJzaGFkb3dCbHVyT3V0ZXIxIj48L2ZlR2F1c3NpYW5CbHVyPgogICAgICA8ZmVDb2xvck1hdHJpeCB2YWx1ZXM9IjAgMCAwIDAgMC4yMDc4NDMxMzcgICAwIDAgMCAwIDAuNDQ3MDU4ODI0ICAgMCAwIDAgMCAwLjkyNTQ5MDE5NiAgMCAwIDAgMC4zIDAiIHR5cGU9Im1hdHJpeCIgaW49InNoYWRvd0JsdXJPdXRlcjEiPjwvZmVDb2xvck1hdHJpeD4KICAgIDwvZmlsdGVyPgogIDwvZGVmcz4KICA8ZyBpZD0i6aG16Z2iLTEiIHN0cm9rZT0ibm9uZSIgc3Ryb2tlLXdpZHRoPSIxIiBmaWxsPSJub25lIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiPgogICAgPGcgaWQ9Iue8lue7hC00NSIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoLTMxLjAwMDAwMCwgLTE1My4wMDAwMDApIj4KICAgICAgPGcgaWQ9Iue8lue7hC0zMiIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoMjQuMDAwMDAwLCAxNDQuMDAwMDAwKSI+CiAgICAgICAgPGcgaWQ9IuefqeW9oiIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoMTYuMDAwMDAwLCAxNi4wMDAwMDApIj4KICAgICAgICAgIDx1c2UgZmlsbD0iYmxhY2siIGZpbGwtb3BhY2l0eT0iMSIgZmlsdGVyPSJ1cmwoI2ZpbHRlci0zKSIgeGxpbms6aHJlZj0iI3BhdGgtMiI+PC91c2U+CiAgICAgICAgICA8dXNlIGZpbGw9InVybCgjbGluZWFyR3JhZGllbnQtMSkiIGZpbGwtcnVsZT0iZXZlbm9kZCIgeGxpbms6aHJlZj0iI3BhdGgtMiI+PC91c2U+CiAgICAgICAgPC9nPgogICAgICA8L2c+CiAgICA8L2c+CiAgPC9nPgo8L3N2Zz4K')",
          }}
        >
          {index}
        </div>
        <div
          style={{
            fontSize: '16px',
            color: 'rgba(0, 0, 0, 0.85)',
            paddingBottom: 8,
          }}
        >
          {title}
        </div>
      </div>
      <div
        style={{
          fontSize: '14px',
          color: 'rgba(0,0,0,0.65)',
          textAlign: 'justify',
          lineHeight: '22px',
          marginBottom: 8,
        }}
      >
        {desc}
      </div>
      <a href={href} target="_blank" rel="noreferrer">
        了解更多 {'>'}
      </a>
    </div>
  );
};

const Welcome: React.FC = () => {
  return (
    <GridContent>
      <Card
        style={{
          borderRadius: 8,
        }}
        bodyStyle={{
          backgroundImage:
            'radial-gradient(circle at 97% 10%, #EBF2FF 0%, #F5F8FF 28%, #EBF1FF 124%)',
        }}
      >
        <div
          style={{
            backgroundPosition: '100% -30%',
            backgroundRepeat: 'no-repeat',
            backgroundSize: '274px auto',
            backgroundImage: "url('/x4v0w8nb.png')",
          }}
        >
          <div
            style={{
              fontSize: '20px',
              color: '#1A1A1A',
            }}
          >
            欢迎使用 TopIAM 企业数字身份管控平台
          </div>
          <p
            style={{
              fontSize: '14px',
              color: 'rgba(0,0,0,0.65)',
              lineHeight: '22px',
              marginTop: 16,
              marginBottom: 32,
              width: '65%',
            }}
          >
            TopIAM 数字身份管控平台（Employee Identity and Access
            Management）用于管理企业内员工的账号、权限、身份认证、应用访问，帮助整合部署在本地或云端的内部办公系统、业务系统及三方
            SaaS 系统的所有身份，实现一个账号打通所有应用的服务。
          </p>
          <div
            style={{
              display: 'flex',
              gap: 16,
            }}
          >
            <InfoCard
              index={1}
              href="https://eiam.topiam.cn/docs/introduction/"
              title="入门文档"
              desc="入门文档"
            />
            <InfoCard
              index={2}
              title="绑定现有账户系统"
              href="https://eiam.topiam.cn/docs/identity-source/"
              desc="快速导入账户，并复用现有的登录方式。"
            />
            <InfoCard
              index={3}
              title="集成身份提供商"
              href="https://eiam.topiam.cn/docs/authentication/"
              desc="集成身份提供商，快速纳入三方平台登录。"
            />
          </div>
          <br />
          <div
            style={{
              display: 'flex',
              gap: 16,
            }}
          >
            <InfoCard
              index={4}
              title="实现应用单点登录"
              href="https://eiam.topiam.cn/docs/application/"
              desc="创建配置应用，实现应用单点登录。"
            />
            <InfoCard
              index={5}
              title="系统配置"
              href="https://eiam.topiam.cn/docs/guide/admin/setting/"
              desc="进行系统基础配置，如文件存储、地理位置、邮件短信服务。"
            />
            <InfoCard
              index={6}
              title="安全配置"
              href="https://eiam.topiam.cn/docs/guide/admin/security/"
              desc="进行系统安全配置，如密码策略、会话策略，完善系统安全。"
            />
          </div>
        </div>
      </Card>
    </GridContent>
  );
};

export default Welcome;
