/*
 * eiam-portal - Employee Identity and Access Management Program
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
"use strict";(self.webpackChunktopiam_portal=self.webpackChunktopiam_portal||[]).push([[396],{81046:function(M,g,t){t.r(g),t.d(g,{default:function(){return H}});var A=t(84019),d=t.n(A),T=t(32061),y=t.n(T),C=t(7863),R=t.n(C),L=t(53448),I=t(62119),O=t(76815),Z=t(55470),B=t(24380),S=t(12790),E=t(90914),N=t(86977),x=t(79685),p;(function(o){o.ONLY_APP_INIT_SSO="only_app_init_sso",o.PORTAL_OR_APP_INIT_SSO="portal_or_app_init_sso"})(p||(p={}));var $=t(76091),f=t.n($),P=t(81996),z=t(84530);function D(o,i,c){return h.apply(this,arguments)}function h(){return h=y()(d()().mark(function o(i,c,v){var u,a,m;return d()().wrap(function(s){for(;;)switch(s.prev=s.next){case 0:return s.next=2,(0,z.request)("/api/app/list",{params:f()(f()(f()({},i),(0,P.YE)(c)),(0,P.oH)(v))});case 2:return u=s.sent,a=u.result,m=u.success,s.abrupt("return",{data:a!=null&&a.list?a==null?void 0:a.list:[],success:m,total:a!=null&&a.pagination?a==null?void 0:a.pagination.total:0});case 6:case"end":return s.stop()}},o)})),h.apply(this,arguments)}var r=t(63342),F=O.Z.Paragraph,G=function(){var i=(0,x.useRef)(),c=(0,x.useState)(),v=R()(c,2),u=v[0],a=v[1],m=(0,r.jsx)("div",{style:{textAlign:"center"},children:(0,r.jsx)(Z.Z.Search,{placeholder:"\u8BF7\u8F93\u5165",enterButton:"\u641C\u7D22",size:"large",style:{maxWidth:522,width:"100%"},onSearch:function(n){var e;a({name:n}),(e=i.current)===null||e===void 0||e.reload()}})}),j=function(n){var e=window.document.createElement("div");e.innerHTML="<form action='"+n+"' method='POST' name='auto_submit_form' style='display:none'>",document.body.appendChild(e),document.forms.auto_submit_form.setAttribute("target","_blank"),document.forms.auto_submit_form.submit(),document.body.removeChild(e)};return(0,r.jsxs)(L._z,{content:m,children:[(0,r.jsx)(B.Z,{message:"\u8BF7\u70B9\u51FB\u4E0B\u65B9\u7684\u5E94\u7528\u8FDB\u884C\u5355\u70B9\u767B\u5F55\u3002\u82E5\u5E0C\u671B\u4FEE\u6539\u5E94\u7528\u5185\u5BB9\uFF0C\u8BF7\u8054\u7CFB\u7BA1\u7406\u5458\u3002",showIcon:!0}),(0,r.jsx)("br",{}),(0,r.jsx)(I.Rs,{rowKey:"id",ghost:!0,grid:{xs:1,sm:2,md:2,lg:3,xl:4,xxl:5},request:D,pagination:{},params:u,actionRef:i,renderItem:function(n){return n&&n.id&&(0,r.jsx)(S.Z,{style:{margin:5},hoverable:!0,onClick:y()(d()().mark(function e(){return d()().wrap(function(l){for(;;)switch(l.prev=l.next){case 0:if(n.initLoginType!==p.PORTAL_OR_APP_INIT_SSO){l.next=3;break}return j(n.initLoginUrl),l.abrupt("return");case 3:E.ZP.warning("".concat(n.name,"\u4EC5\u5141\u8BB8\u5E94\u7528\u53D1\u8D77"));case 4:case"end":return l.stop()}},e)})),children:(0,r.jsx)(S.Z.Meta,{avatar:(0,r.jsx)(N.C,{shape:"square",size:50,src:n.icon},n.id),title:n.name,description:(0,r.jsx)(F,{ellipsis:{rows:2,tooltip:!0},children:n.description})})})}})]})},H=G}}]);
