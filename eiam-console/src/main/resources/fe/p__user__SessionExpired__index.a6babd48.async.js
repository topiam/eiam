/*
 * eiam-console - Employee Identity and Access Management Program
 * Copyright © 2020-2023 TopIAM (support@topiam.cn)
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
"use strict";(self.webpackChunktopiam_console=self.webpackChunktopiam_console||[]).push([[648],{65626:function(C,o,t){t.r(o),t.d(o,{default:function(){return O}});var u=t(84019),r=t.n(u),n=t(76091),a=t.n(n),s=t(32061),f=t.n(s),e=t(74958),d=t(59251),E=t(96104),m=t(78035),v=t(27101),p=t(18541),c=t(63342),x=function(){var i=(0,E.useModel)("@@initialState"),R=i.initialState,S=i.setInitialState,F=(0,E.useLocation)();return(0,m.Z)(f()(r()().mark(function T(){return r()().wrap(function(y){for(;;)switch(y.prev=y.next){case 0:v.Z.warning({title:"\u4F1A\u8BDD\u8FC7\u671F",content:"\u60A8\u7684\u767B\u5F55\u4FE1\u606F\u5DF2\u8FC7\u671F\uFF0C\u8BF7\u91CD\u65B0\u767B\u5F55\u3002",okText:"\u786E\u8BA4",okType:"danger",centered:!1,maskClosable:!1,okCancel:!1,onOk:function(){var j=f()(r()().mark(function Z(){var h,P,l,I,g,D;return r()().wrap(function(M){for(;;)switch(M.prev=M.next){case 0:return M.next=2,S(a()(a()({},R),{},{currentUser:void 0}));case 2:h=p.Z.parse(F.search),P=h,l=P.redirect_uri,I={pathname:e.wm},g=l&&l.split("/"),l&&l!==g[0]+"//"+g[2]+"/"&&(I=a()(a()({},I),{},{search:p.Z.stringify({redirect_uri:l})})),D=d.m.createHref(I),window.location.replace(D);case 9:case"end":return M.stop()}},Z)}));function U(){return j.apply(this,arguments)}return U}()});case 1:case"end":return y.stop()}},T)}))),(0,c.jsx)(c.Fragment,{})},O=function(){return(0,c.jsx)(x,{})}},78035:function(C,o,t){var u=t(79685),r=t(43865),n=t(15035);const a=s=>{n.Z&&((0,r.mf)(s)||console.error(`useMount: parameter \`fn\` expected to be a function, but got "${typeof s}".`)),(0,u.useEffect)(()=>{s==null||s()},[])};o.Z=a},43865:function(C,o,t){t.d(o,{mf:function(){return r}});const u=e=>e!==null&&typeof e=="object",r=e=>typeof e=="function",n=e=>typeof e=="string",a=e=>typeof e=="boolean",s=e=>typeof e=="number",f=e=>typeof e=="undefined"},15035:function(C,o){o.Z=!1},44299:function(C,o,t){t.d(o,{RV:function(){return f},Rk:function(){return e},Ux:function(){return E},aM:function(){return d},q3:function(){return a},qI:function(){return s}});var u=t(72079),r=t(57231),n=t(79685);const a=n.createContext({labelAlign:"right",vertical:!1,itemRef:()=>{}}),s=n.createContext(null),f=m=>{const v=(0,r.Z)(m,["prefixCls"]);return n.createElement(u.RV,Object.assign({},v))},e=n.createContext({prefixCls:""}),d=n.createContext({}),E=m=>{let{children:v,status:p,override:c}=m;const x=(0,n.useContext)(d),O=(0,n.useMemo)(()=>{const i=Object.assign({},x);return c&&delete i.isFormItemInput,p&&(delete i.status,delete i.hasFeedback,delete i.feedbackIcon),i},[p,c,x]);return n.createElement(d.Provider,{value:O},v)}}}]);
