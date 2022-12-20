/*
 * eiam-console - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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
"use strict";(self.webpackChunktopiam_console=self.webpackChunktopiam_console||[]).push([[8648],{34270:function(p,r,t){t.r(r),t.d(r,{default:function(){return x}});var s=t(97983),o=t.n(s),c=t(11281),e=t.n(c),a=t(40794),m=t.n(a),n=t(25191),P=t(84865),f=t(98971),h=t(78234),E=t(69400),i=t(67038),u=t(85893),C=function(){var v=(0,f.useModel)("@@initialState"),y=v.initialState,l=v.setInitialState,F=(0,f.useLocation)();return(0,h.Z)(m()(o()().mark(function T(){return o()().wrap(function(O){for(;;)switch(O.prev=O.next){case 0:E.Z.warning({title:"\u4F1A\u8BDD\u8FC7\u671F",content:"\u60A8\u7684\u767B\u5F55\u4FE1\u606F\u5DF2\u8FC7\u671F\uFF0C\u8BF7\u91CD\u65B0\u767B\u5F55\u3002",okText:"\u786E\u8BA4",okType:"danger",centered:!1,maskClosable:!1,okCancel:!1,onOk:function(){var U=m()(o()().mark(function B(){var g,R,d,I,D,S;return o()().wrap(function(M){for(;;)switch(M.prev=M.next){case 0:return M.next=2,l(e()(e()({},y),{},{currentUser:void 0}));case 2:g=(0,i.parse)(F.search),R=g,d=R.redirect_uri,I={pathname:n.wm},D=d&&d.split("/"),d&&d!==D[0]+"//"+D[2]+"/"&&(I=e()(e()({},I),{},{search:(0,i.stringify)({redirect_uri:d})})),S=P.m.createHref(I),window.location.replace(S);case 9:case"end":return M.stop()}},B)}));function A(){return U.apply(this,arguments)}return A}()});case 1:case"end":return O.stop()}},T)}))),(0,u.jsx)(u.Fragment,{})},x=function(){return(0,u.jsx)(C,{})}},78234:function(p,r,t){var s=t(67294),o=t(92770),c=t(31663);const e=a=>{c.Z&&((0,o.mf)(a)||console.error(`useMount: parameter \`fn\` expected to be a function, but got "${typeof a}".`)),(0,s.useEffect)(()=>{a==null||a()},[])};r.Z=e},92770:function(p,r,t){t.d(r,{mf:function(){return o}});const s=n=>n!==null&&typeof n=="object",o=n=>typeof n=="function",c=n=>typeof n=="string",e=n=>typeof n=="boolean",a=n=>typeof n=="number",m=n=>typeof n=="undefined"},31663:function(p,r){r.Z=!1},65223:function(p,r,t){t.d(r,{RV:function(){return n},Rk:function(){return P},Ux:function(){return h},aM:function(){return f},q3:function(){return a},qI:function(){return m}});var s=t(87462),o=t(71990),c=t(98423),e=t(67294),a=e.createContext({labelAlign:"right",vertical:!1,itemRef:function(){}}),m=e.createContext(null),n=function(i){var u=(0,c.Z)(i,["prefixCls"]);return e.createElement(o.RV,(0,s.Z)({},u))},P=e.createContext({prefixCls:""}),f=e.createContext({}),h=function(i){var u=i.children,C=i.status,x=i.override,v=(0,e.useContext)(f),y=(0,e.useMemo)(function(){var l=(0,s.Z)({},v);return x&&delete l.isFormItemInput,C&&(delete l.status,delete l.hasFeedback,delete l.feedbackIcon),l},[C,x,v]);return e.createElement(f.Provider,{value:y},u)}}}]);
