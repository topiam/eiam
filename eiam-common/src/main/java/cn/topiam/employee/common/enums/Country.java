/*
 * eiam-common - Employee Identity and Access Management
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
package cn.topiam.employee.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import cn.topiam.employee.support.enums.BaseEnum;
import cn.topiam.employee.support.web.converter.EnumConvert;

/**
 * <p>
 * 国家编码
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/05/14
 */
public enum Country implements BaseEnum {
                                         /**
                                          * 安道尔
                                          */
                                         AD("AD", "Andorra", "安道尔"),
                                         /**
                                          * 奥地利
                                          */
                                         AT("AT", "Austria", "奥地利"),
                                         /**
                                          * 澳大利亚
                                          */
                                         AU("AU", "Australia", "澳大利亚"),
                                         /**
                                          * 阿尔巴尼亚
                                          */
                                         AL("AL", "Albania", "阿尔巴尼亚"),
                                         /**
                                          * 阿尔及利亚
                                          */
                                         DZ("DZ", "Algeria", "阿尔及利亚"),
                                         /**
                                          * 爱尔兰
                                          */
                                         IE("IE", "Ireland", "爱尔兰"),

                                         /**
                                          * 阿富汗
                                          */
                                         AF("AF", "Afghanistan", "阿富汗"),

                                         /**
                                          * 安圭拉
                                          */
                                         AI("AI", "Anguilla", "安圭拉"),
                                         /**
                                          * 安哥拉
                                          */
                                         AO("AO", "Angola", "安哥拉"),
                                         /**
                                          * 阿根廷
                                          */
                                         AR("AR", "Argentina", "阿根廷"),
                                         /**
                                          * 埃及
                                          */
                                         EG("EG", "Egypt", "埃及"),
                                         /**
                                          * 阿鲁巴
                                          */
                                         AW("AW", "Aruba", "阿鲁巴"),

                                         /**
                                          * 阿拉伯联合酋长国
                                          */
                                         AE("AE", "United Arab Emirates", "阿拉伯联合酋长国"),
                                         /**
                                          * 阿曼
                                          */
                                         OM("OM", "Oman", "阿曼"),
                                         /**
                                          * 阿塞拜疆
                                          */
                                         AZ("AZ", "Azerbaijan", "阿塞拜疆"),
                                         /**
                                          * 埃塞俄比亚
                                          */
                                         ET("ET", "Ethiopia", "埃塞俄比亚"),
                                         /**
                                          * 爱沙尼亚
                                          */
                                         EE("EE", "Estonia", "爱沙尼亚"),
                                         /**
                                          * 安提瓜岛和巴布达
                                          */
                                         AG("AG", "Antigua and Barbuda", "安提瓜岛和巴布达"),
                                         /**
                                          * 巴巴多斯岛
                                          */
                                         BB("BB", "Barbados", "巴巴多斯岛"),
                                         /**
                                          * 巴布亚新几内亚
                                          */
                                         PG("PG", "Papua New Guinea", "巴布亚新几内亚"),
                                         /**
                                          * 博茨瓦纳
                                          */
                                         BW("BW", "Botswana", "博茨瓦纳"),
                                         /**
                                          * 冰岛
                                          */
                                         IS("IS", "Iceland", "冰岛"),
                                         /**
                                          * 不丹
                                          */
                                         BT("BT", "Bhutan", "不丹"),
                                         /**
                                          * 波多黎各
                                          */
                                         PR("PR", "Puerto Rico", "波多黎各"),
                                         /**
                                          * 白俄罗斯
                                          */
                                         BY("BY", "Belarus", "白俄罗斯"),
                                         /**
                                          * 巴哈马
                                          */
                                         BS("BS", "Bahamas", "巴哈马"),
                                         /**
                                          * 保加利亚
                                          */
                                         BG("BG", "Bulgaria", "保加利亚"),
                                         /**
                                          * 布基纳法索
                                          */
                                         BF("BF", "Burkina Faso", "布基纳法索"),
                                         /**
                                          * 巴基斯坦
                                          */

                                         PK("PK", "Pakistan", "巴基斯坦"),
                                         /**
                                          * 波兰
                                          */
                                         PL("PL", "Poland", "波兰"),
                                         /**
                                          * 巴林
                                          */
                                         BH("BH", "Bahrain", "巴林"),
                                         /**
                                          * 布隆迪
                                          */
                                         BI("BI", "Burundi", "布隆迪"),
                                         /**
                                          *巴拉圭
                                          */
                                         PY("PY", "Paraguay", "巴拉圭"),
                                         /**
                                         *比利时
                                         */
                                         BE("BE", "Belgium", "比利时"),
                                         /**
                                         *巴勒斯坦
                                         */
                                         PS("PS", "Palestine, State of", "巴勒斯坦"),
                                         /**
                                         *玻利维亚
                                         */
                                         BO("BO", "Bolivia, Plurinational State of", "玻利维亚"),
                                         /**
                                         *伯利兹
                                         */
                                         BZ("BZ", "Belize", "伯利兹"),
                                         /**
                                         *百慕大
                                         */
                                         BM("BM", "Bermuda", "百慕大"),
                                         /**
                                         *北马里亚纳群岛
                                         */
                                         MP("MP", "Northern Mariana Islands", "北马里亚纳群岛"),
                                         /**
                                         *贝宁
                                         */
                                         BJ("BJ", "Benin", "贝宁"),
                                         /**
                                         *巴拿马
                                         */
                                         PA("PA", "Panama", "巴拿马"),
                                         /**
                                         *波斯尼亚和黑塞哥维那
                                         */
                                         BA("BA", "Bosnia and Herzegovina", "波斯尼亚和黑塞哥维那"),
                                         /**
                                         *布韦岛
                                         */
                                         BV("BV", "Bouvet Island", "布韦岛"),
                                         /**
                                         *巴西
                                         */
                                         BR("BR", "Brazil", "巴西"),
                                         /**
                                         *赤道几内亚
                                         */
                                         GQ("GQ", "Equatorial Guinea", "赤道几内亚"),
                                         /**
                                         *朝鲜
                                         */
                                         KP("KP", "Korea, Democratic People's Republic of", "朝鲜"),
                                         /**
                                         *东帝汶
                                         */
                                         TL("TL", "Timor-Leste", "东帝汶"),
                                         /**
                                         *德国
                                         */
                                         DE("DE", "Germany", "德国"),
                                         /**
                                         *多哥
                                         */
                                         TG("TG", "Togo", "多哥"),
                                         /**
                                         *梵蒂冈
                                         */
                                         VA("VA", "Holy See (Vatican City State)", "梵蒂冈"),
                                         /**
                                         *丹麦
                                         */
                                         DK("DK", "Denmark", "丹麦"),
                                         /**
                                         *多米尼加
                                         */
                                         DM("DM", "Dominica", "多米尼加"),
                                         /**
                                         *多米尼加共和国
                                         */
                                         DO("DO", "Dominican Republic", "多米尼加共和国"),
                                         /**
                                         *厄瓜多尔
                                         */
                                         EC("EC", "Ecuador", "厄瓜多尔"),
                                         /**
                                         *俄罗斯
                                         */
                                         RU("RU", "Russian Federation", "俄罗斯"),
                                         /**
                                         *厄立特里亚
                                         */
                                         ER("ER", "Eritrea", "厄立特里亚"),
                                         /**
                                         *佛得角
                                         */
                                         CV("CV", "Cape Verde", "佛得角"),
                                         /**
                                         *法国
                                         */
                                         FR("FR", "France", "法国"),
                                         /**
                                         *芬兰
                                         */
                                         FI("FI", "Finland", "芬兰"),
                                         /**
                                         *菲律宾
                                         */
                                         PH("PH", "Philippines", "菲律宾"),
                                         /**
                                         *弗兰克群岛
                                         */
                                         FK("FK", "Falkland Islands (Malvinas)", "弗兰克群岛"),
                                         /**
                                         *法罗群岛
                                         */
                                         FO("FO", "Faroe Islands", "法罗群岛"),
                                         /**
                                         *法属波利尼西亚
                                         */
                                         PF("PF", "French Polynesia", "法属波利尼西亚"),
                                         /**
                                         *法属圭亚那
                                         */
                                         GF("GF", "French Guiana", "法属圭亚那"),
                                         /**
                                         *法属南部领地
                                         */
                                         TF("TF", "French Southern Territories", "法属南部领地"),
                                         /**
                                         *古巴
                                         */
                                         CU("CU", "Cuba", "古巴"),
                                         /**
                                         *冈比亚
                                         */
                                         GM("GM", "Gambia", "冈比亚"),
                                         /**
                                         *关岛
                                         */
                                         GU("GU", "Guam", "关岛"),
                                         /**
                                         *瓜德罗普岛
                                         */
                                         GP("GP", "Guadeloupe", "瓜德罗普岛"),
                                         /**
                                         *刚果
                                         */
                                         CG("CG", "Congo", "刚果"),
                                         /**
                                         *刚果（金）
                                         */
                                         CD("CD", "Congo, the Democratic Republic of the", "刚果（金）"),
                                         /**
                                         *哥伦比亚
                                         */
                                         CO("CO", "Colombia", "哥伦比亚"),
                                         /**
                                         *格鲁吉亚
                                         */
                                         GE("GE", "Georgia", "格鲁吉亚"),
                                         /**
                                         *格陵兰
                                         */
                                         GL("GL", "Greenland", "格陵兰"),
                                         /**
                                         *格林纳达
                                         */
                                         GD("GD", "Grenada", "格林纳达"),
                                         /**
                                         *哥斯达黎加
                                         */
                                         CR("CR", "Costa Rica", "哥斯达黎加"),
                                         /**
                                         *根西岛
                                         */
                                         GG("GG", "Guernsey", "根西岛"),
                                         /**
                                         *圭亚那
                                         */
                                         GY("GY", "Guyana", "圭亚那"),
                                         /**
                                         *海地
                                         */
                                         HT("HT", "Haiti", "海地"),
                                         /**
                                         *赫德和麦克唐纳群岛
                                         */
                                         HM("HM", "Heard Island and McDonald Islands", "赫德和麦克唐纳群岛"),
                                         /**
                                         *洪都拉斯
                                         */
                                         HN("HN", "Honduras", "洪都拉斯"),
                                         /**
                                         *韩国
                                         */
                                         KR("KR", "Korea, Republic of", "韩国"),
                                         /**
                                         *荷兰
                                         */
                                         NL("NL", "Netherlands", "荷兰"),
                                         /**
                                         *黑山
                                         */
                                         ME("ME", "Montenegro", "黑山"),
                                         /**
                                         *哈萨克斯坦
                                         */
                                         KZ("KZ", "Kazakhstan", "哈萨克斯坦"),
                                         /**
                                         *斐济
                                         */
                                         FJ("FJ", "Fiji", "斐济"),
                                         /**
                                         *津巴布韦
                                         */
                                         ZW("ZW", "Zimbabwe", "津巴布韦"),
                                         /**
                                         *吉布提
                                         */
                                         DJ("DJ", "Djibouti", "吉布提"),
                                         /**
                                         *吉尔吉斯斯坦
                                         */
                                         KG("KG", "Kyrgyzstan", "吉尔吉斯斯坦"),
                                         /**
                                         *捷克
                                         */
                                         CZ("CZ", "Czech Republic", "捷克"),
                                         /**
                                         *基里巴斯
                                         */
                                         KI("KI", "Kiribati", "基里巴斯"),
                                         /**
                                         *加纳
                                         */
                                         GH("GH", "Ghana", "加纳"),
                                         /**
                                         *加拿大
                                         */
                                         CA("CA", "Canada", "加拿大"),
                                         /**
                                         *几内亚
                                         */
                                         GN("GN", "Guinea", "几内亚"),
                                         /**
                                         *几内亚比绍"
                                         */
                                         GW("GW", "Guinea-Bissau", "几内亚比绍"),
                                         /**
                                         *加蓬
                                         */
                                         GA("GA", "Gabon", "加蓬"),
                                         /**
                                         *柬埔寨
                                         */
                                         KH("KH", "Cambodia", "柬埔寨"),
                                         /**
                                         *库克群岛
                                         */
                                         CK("CK", "Cook Islands", "库克群岛"),
                                         /**
                                         *科科斯群岛
                                         */
                                         CC("CC", "Cocos (Keeling) Islands", "科科斯群岛"),
                                         /**
                                         *克罗地亚
                                         */
                                         HR("HR", "Croatia", "克罗地亚"),
                                         /**
                                         *科摩罗
                                         */
                                         KM("KM", "Comoros", "科摩罗"),
                                         /**
                                         *喀麦隆
                                         */
                                         CM("CM", "Cameroon", "喀麦隆"),
                                         /**
                                         *开曼群岛
                                         */
                                         KY("KY", "Cayman Islands", "开曼群岛"),
                                         /**
                                         *肯尼亚
                                         */
                                         KE("KE", "Kenya", "肯尼亚"),
                                         /**
                                         *卡塔尔
                                         */
                                         QA("QA", "Qatar", "卡塔尔"),
                                         /**
                                         *科威特
                                         */
                                         KW("KW", "Kuwait", "科威特"),
                                         /**
                                         *瑙鲁
                                         */
                                         NR("NR", "Nauru", "瑙鲁"),
                                         /**
                                         *利比里亚
                                         */
                                         LR("LR", "Liberia", "利比里亚"),
                                         /**
                                         *黎巴嫩
                                         */
                                         LB("LB", "Lebanon", "黎巴嫩"),
                                         /**
                                         *利比亚
                                         */
                                         LY("LY", "Libya", "利比亚"),
                                         /**
                                         *罗马尼亚
                                         */
                                         RO("RO", "Romania", "罗马尼亚"),
                                         /**
                                         *卢森堡
                                         */
                                         LU("LU", "Luxembourg", "卢森堡"),
                                         /**
                                         *莱索托
                                         */
                                         LS("LS", "Lesotho", "莱索托"),
                                         /**
                                         *立陶宛
                                         */
                                         LT("LT", "Lithuania", "立陶宛"),
                                         /**
                                         *拉脱维亚
                                         */
                                         LV("LV", "Latvia", "拉脱维亚"),
                                         /**
                                         *老挝
                                         */
                                         LA("LA", "Lao People's Democratic Republic", "老挝"),
                                         /**
                                         *卢旺达
                                         */
                                         RW("RW", "Rwanda", "卢旺达"),
                                         /**
                                         *列支敦士登
                                         */
                                         LI("LI", "Liechtenstein", "列支敦士登"),
                                         /**
                                         *缅甸
                                         */
                                         MM("MM", "Myanmar", "缅甸"),
                                         /**
                                         *马达加斯加
                                         */
                                         MG("MG", "Madagascar", "马达加斯加"),
                                         /**
                                         *马恩岛
                                         */
                                         IM("IM", "Isle of Man", "马恩岛"),
                                         /**
                                         *马尔代夫
                                         */
                                         MV("MV", "Maldives", "马尔代夫"),
                                         /**
                                         *摩尔多瓦
                                         */
                                         MD("MD", "Moldova, Republic of", "摩尔多瓦"),
                                         /**
                                         *马耳他
                                         */
                                         MT("MT", "Malta", "马耳他"),
                                         /**
                                         *美国
                                         */
                                         US("US", "United States", "美国"),
                                         /**
                                         *蒙古
                                         */
                                         MN("MN", "Mongolia", "蒙古"),
                                         /**
                                         *孟加拉
                                         */
                                         BD("BD", "Bangladesh", "孟加拉"),
                                         /**
                                         *密克罗尼西亚
                                         */
                                         FM("FM", "Micronesia, Federated States of", "密克罗尼西亚"),
                                         /**
                                         *秘鲁
                                         */
                                         PE("PE", "Peru", "秘鲁"),
                                         /**
                                         *马里
                                         */
                                         ML("ML", "Mali", "马里"),
                                         /**
                                         *摩洛哥
                                         */
                                         MA("MA", "Morocco", "摩洛哥"),
                                         /**
                                         *毛里求斯
                                         */
                                         MU("MU", "Mauritius", "毛里求斯"),
                                         /**
                                         *毛里塔尼亚
                                         */
                                         MR("MR", "Mauritania", "毛里塔尼亚"),
                                         /**
                                         *马拉维
                                         */
                                         MW("MW", "Malawi", "马拉维"),
                                         /**
                                         *马来西亚
                                         */
                                         MY("MY", "Malaysia", "马来西亚"),
                                         /**
                                         *摩纳哥
                                         */
                                         MC("MC", "Monaco", "摩纳哥"),
                                         /**
                                         *马其顿
                                         */
                                         MK("MK", "Macedonia, the Former Yugoslav Republic of",
                                            "马其顿"),
                                         /**
                                         *莫桑比克
                                         */
                                         MZ("MZ", "Mozambique", "莫桑比克"),
                                         /**
                                         *马绍尔群岛
                                         */
                                         MH("MH", "Marshall Islands", "马绍尔群岛"),
                                         /**
                                         *美属萨摩亚
                                         */
                                         AS("AS", "American Samoa", "美属萨摩亚"),
                                         /**
                                         *马提尼克岛
                                         */
                                         MQ("MQ", "Martinique", "马提尼克岛"),
                                         /**
                                         *蒙特塞拉特
                                         */
                                         MS("MS", "Montserrat", "蒙特塞拉特"),
                                         /**
                                         *墨西哥
                                         */
                                         MX("MX", "Mexico", "墨西哥"),
                                         /**
                                         *马约特
                                         */
                                         YT("YT", "Mayotte", "马约特"),
                                         /**
                                         *纽埃
                                         */
                                         NU("NU", "Niue", "纽埃"),
                                         /**
                                         *尼泊尔
                                         */
                                         NP("NP", "Nepal", "尼泊尔"),
                                         /**
                                         *南非
                                         */
                                         ZA("ZA", "South Africa", "南非"),
                                         /**
                                         *诺福克岛
                                         */
                                         NF("NF", "Norfolk Island", "诺福克岛"),
                                         /**
                                         *尼加拉瓜
                                         */
                                         NI("NI", "Nicaragua", "尼加拉瓜"),
                                         /**
                                         *
                                         */
                                         NA("NA", "Namibia", "纳米比亚"),
                                         /**
                                         *南乔治亚和南桑德威奇群岛
                                         */
                                         GS("GS", "South Georgia and the South Sandwich Islands",
                                            "南乔治亚和南桑德威奇群岛"),
                                         /**
                                         *尼日尔
                                         */
                                         NE("NE", "Niger", "尼日尔"),
                                         /**
                                         *尼日利亚
                                         */
                                         NG("NG", "Nigeria", "尼日利亚"),
                                         /**
                                         *南苏丹
                                         */
                                         SS("SS", "South Sudan", "南苏丹"),
                                         /**
                                         *挪威
                                         */
                                         NO("NO", "Norway", "挪威"),
                                         /**
                                         *帕劳
                                         */
                                         PW("PW", "Palau", "帕劳"),
                                         /**
                                         *皮特凯恩
                                         */
                                         PN("PN", "Pitcairn", "皮特凯恩"),
                                         /**
                                         *葡萄牙
                                         */
                                         PT("PT", "Portugal", "葡萄牙"),
                                         /**
                                         *日本
                                         */
                                         JP("JP", "Japan", "日本"),
                                         /**
                                         *瑞典
                                         */
                                         SE("SE", "Sweden", "瑞典"),
                                         /**
                                         *瑞士
                                         */
                                         CH("CH", "Switzerland", "瑞士"),
                                         /**
                                         *苏丹
                                         */
                                         SD("SD", "Sudan", "苏丹"),
                                         /**
                                         *圣诞岛
                                         */
                                         CX("CX", "Christmas Island", "圣诞岛"),
                                         /**
                                         *圣多美和普林西比
                                         */
                                         ST("ST", "Sao Tome and Principe", "圣多美和普林西比"),
                                         /**
                                         *萨尔瓦多
                                         */
                                         SV("SV", "El Salvador", "萨尔瓦多"),
                                         /**
                                         *塞尔维亚
                                         */
                                         RS("RS", "Serbia", "塞尔维亚"),
                                         /**
                                         *圣赫勒拿
                                         */
                                         SH("SH", "Saint Helena, Ascension and Tristan da Cunha",
                                            "圣赫勒拿"),
                                         /**
                                         *圣基茨和尼维斯
                                         */
                                         KN("KN", "Saint Kitts and Nevis", "圣基茨和尼维斯"),
                                         /**
                                         *斯洛伐克
                                         */
                                         SK("SK", "Slovakia", "斯洛伐克"),
                                         /**
                                         *塞拉利昂
                                         */
                                         SL("SL", "Sierra Leone", "塞拉利昂"),
                                         /**
                                         *斯里兰卡
                                         */
                                         LK("LK", "Sri Lanka", "斯里兰卡"),
                                         /**
                                         *所罗门群岛
                                         */
                                         SB("SB", "Solomon Islands", "所罗门群岛"),
                                         /**
                                         *苏里南
                                         */
                                         SR("SR", "Suriname", "苏里南"),
                                         /**
                                         *斯洛文尼亚
                                         */
                                         SI("SI", "Slovenia", "斯洛文尼亚"),
                                         /**
                                         *圣卢西亚
                                         */
                                         LC("LC", "Saint Lucia", "圣卢西亚"),
                                         /**
                                         *索马里
                                         */
                                         SO("SO", "Somalia", "索马里"),
                                         /**
                                         *圣马力诺
                                         */
                                         SM("SM", "San Marino", "圣马力诺"),
                                         /**
                                         *萨摩亚
                                         */
                                         WS("WS", "Samoa", "萨摩亚"),
                                         /**
                                         *塞内加尔
                                         */
                                         SN("SN", "Senegal", "塞内加尔"),
                                         /**
                                         *圣皮埃尔和米克隆群岛
                                         */
                                         PM("PM", "Saint Pierre and Miquelon", "圣皮埃尔和米克隆群岛"),
                                         /**
                                         *塞浦路斯
                                         */
                                         CY("CY", "Cyprus", "塞浦路斯"),
                                         /**
                                         *塞舌尔
                                         */
                                         SC("SC", "Seychelles", "塞舌尔"),
                                         /**
                                         *沙特阿拉伯
                                         */
                                         SA("SA", "Saudi Arabia", "沙特阿拉伯"),
                                         /**
                                         *斯瓦尔巴和扬马廷
                                         */
                                         SJ("SJ", "Svalbard and Jan Mayen", "斯瓦尔巴和扬马廷"),
                                         /**
                                         *斯威士兰
                                         */
                                         SZ("SZ", "Swaziland", "斯威士兰"),
                                         /**
                                         *圣文森特和格林纳丁斯
                                         */
                                         VC("VC", "Saint Vincent and the Grenadines", "圣文森特和格林纳丁斯"),
                                         /**
                                         *土耳其
                                         */
                                         TR("TR", "Turkey", "土耳其"),
                                         /**
                                         *泰国
                                         */
                                         TH("TH", "Thailand", "泰国"),
                                         /**
                                         *汤加
                                         */
                                         TO("TO", "Tonga", "汤加"),
                                         /**
                                         *塔吉克斯坦
                                         */
                                         TJ("TJ", "Tajikistan", "塔吉克斯坦"),
                                         /**
                                         *托克劳
                                         */
                                         TK("TK", "Tokelau", "托克劳"),
                                         /**
                                         *土库曼斯坦
                                         */
                                         TM("TM", "Turkmenistan", "土库曼斯坦"),
                                         /**
                                         *特克斯和凯克特斯群岛
                                         */
                                         TC("TC", "Turks and Caicos Islands", "特克斯和凯克特斯群岛"),
                                         /**
                                         *特立尼达和多巴哥
                                         */
                                         TT("TT", "Trinidad and Tobago", "特立尼达和多巴哥"),
                                         /**
                                         *突尼斯
                                         */
                                         TN("TN", "Tunisia", "突尼斯"),
                                         /**
                                         *坦桑尼亚
                                         */
                                         TZ("TZ", "Tanzania, United Republic of", "坦桑尼亚"),
                                         /**
                                         *图瓦卢
                                         */
                                         TV("TV", "Tuvalu", "图瓦卢"),
                                         /**
                                         *危地马拉
                                         */
                                         GT("GT", "Guatemala", "危地马拉"),
                                         /**
                                         *维尔京群岛，英属
                                         */
                                         VG("VG", "Virgin Islands, British", "维尔京群岛，英属"),
                                         /**
                                         *维尔京群岛，美属
                                         */
                                         VI("VI", "Virgin Islands, U.S.", "维尔京群岛，美属"),
                                         /**
                                         *乌干达
                                         */
                                         UG("UG", "Uganda", "乌干达"),
                                         /**
                                         *乌克兰
                                         */
                                         UA("UA", "Ukraine", "乌克兰"),
                                         /**
                                         *文莱
                                         */
                                         BN("BN", "Brunei Darussalam", "文莱"),
                                         /**
                                         *乌拉圭
                                         */
                                         UY("UY", "Uruguay", "乌拉圭"),
                                         /**
                                         *瓦利斯和福图纳
                                         */
                                         WF("WF", "Wallis and Futuna", "瓦利斯和福图纳"),
                                         /**
                                         *瓦努阿图
                                         */
                                         VU("VU", "Vanuatu", "瓦努阿图"),
                                         /**
                                         *委内瑞拉
                                         */
                                         VE("VE", "Venezuela, Bolivarian Republic of", "委内瑞拉"),
                                         /**
                                         *乌兹别克斯坦
                                         */
                                         UZ("UZ", "Uzbekistan", "乌兹别克斯坦"),
                                         /**
                                         *西班牙
                                         */
                                         ES("ES", "Spain", "西班牙"),
                                         /**
                                         *新加坡
                                         */
                                         SG("SG", "Singapore", "新加坡"),
                                         /**
                                         *新喀里多尼亚
                                         */
                                         NC("NC", "New Caledonia", "新喀里多尼亚"),
                                         /**
                                         *希腊
                                         */
                                         GR("GR", "Greece", "希腊"),
                                         /**
                                         *叙利亚
                                         */
                                         SY("SY", "Syrian Arab Republic", "叙利亚"),
                                         /**
                                         *西撒哈拉
                                         */
                                         EH("EH", "Western Sahara", "西撒哈拉"),
                                         /**
                                         *新西兰
                                         */
                                         NZ("NZ", "New Zealand", "新西兰"),
                                         /**
                                         *匈牙利
                                         */
                                         HU("HU", "Hungary", "匈牙利"),
                                         /**
                                         *约旦
                                         */
                                         JO("JO", "Jordan", "约旦"),
                                         /**
                                         *印度
                                         */
                                         IN("IN", "India", "印度"),
                                         /**
                                         *意大利
                                         */
                                         IT("IT", "Italy", "意大利"),
                                         /**
                                         *印度尼西亚
                                         */
                                         ID("ID", "Indonesia", "印度尼西亚"),
                                         /**
                                         *英国
                                         */
                                         GB("GB", "United Kingdom", "英国"),
                                         /**
                                         *伊朗
                                         */
                                         IR("IR", "Iran, Islamic Republic of", "伊朗"),
                                         /**
                                         *伊拉克
                                         */
                                         IQ("IQ", "Iraq", "伊拉克"),
                                         /**
                                         *也门
                                         */
                                         YE("YE", "Yemen", "也门"),
                                         /**
                                         *牙买加
                                         */
                                         JM("JM", "Jamaica", "牙买加"),
                                         /**
                                         *亚美尼亚
                                         */
                                         AM("AM", "Armenia", "亚美尼亚"),
                                         /**
                                         *越南
                                         */
                                         VN("VN", "Viet Nam", "越南"),
                                         /**
                                         *以色列
                                         */
                                         IL("IL", "Israel", "以色列"),
                                         /**
                                         *英属印度洋领地
                                         */
                                         IO("IO", "British Indian Ocean Territory", "英属印度洋领地"),
                                         /**
                                         *直布罗陀
                                         */
                                         GI("GI", "Gibraltar", "直布罗陀"),
                                         /**
                                         *赞比亚
                                         */
                                         ZM("ZM", "Zambia", "赞比亚"),
                                         /**
                                         *乍得
                                         */
                                         TD("TD", "Chad", "乍得"),
                                         /**
                                         *中非
                                         */
                                         CF("CF", "Central African Republic", "中非"),
                                         /**
                                         *中国
                                         */
                                         CN("CN", "China", "中国"),
                                         /**
                                         *中国澳门
                                         */
                                         MO("MO", "Macao", "中国澳门"),
                                         /**
                                         *中国台湾
                                         */
                                         TW("TW", "Taiwan, Province of China", "中国台湾"),
                                         /**
                                         *中国香港
                                         */
                                         HK("HK", "Hong Kong", "中国香港"),
                                         /**
                                         *智利
                                         */
                                         CL("CL", "Chile", "智利"),
                                         /**
                                         *泽西岛
                                         */
                                         JE("JE", "Jersey", "泽西岛"),
                                         /**
                                         *博内尔岛、圣尤斯特歇斯岛和萨巴岛
                                         */
                                         BQ("BQ", "Bonaire, Sint Eustatius and Saba",
                                            "博内尔岛、圣尤斯特歇斯岛和萨巴岛"),
                                         /**
                                         *美国本土外小岛屿
                                         */
                                         UM("UM", "United States Minor Outlying Islands (the)",
                                            "美国本土外小岛屿 (the)"),
                                         /**
                                         * 象牙海岸
                                         */
                                         CI("CI", "Côte d'Ivoire", "象牙海岸");

    @JsonValue
    private final String code;
    private final String name;
    private final String chineseAbbreviation;

    Country(String code, String name, String chineseAbbreviation) {
        this.code = code;
        this.name = name;
        this.chineseAbbreviation = chineseAbbreviation;
    }

    @EnumConvert
    public static Country getCountry(String code) {
        Country[] values = values();
        for (Country country : values) {
            if (String.valueOf(country.getCode()).equals(code)) {
                return country;
            }
        }
        return null;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return name;
    }

    public String getName() {
        return name;
    }

    public String getChineseAbbreviation() {
        return chineseAbbreviation;
    }
}
