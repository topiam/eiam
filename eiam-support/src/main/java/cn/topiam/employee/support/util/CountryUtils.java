/*
 * eiam-support - Employee Identity and Access Management Program
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
package cn.topiam.employee.support.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 国家区号
 * @author smallbun
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CountryUtils {

    public static final Map<String, CountryInfo> COUNTRY = new HashMap<>(16);

    static {
        COUNTRY.put("AD", new CountryInfo("Andorra", "安道尔", "AD", "+376"));
        COUNTRY.put("AE", new CountryInfo("United Arab Emirates", "阿拉伯联合酋长国", "AE", "+971"));
        COUNTRY.put("AF", new CountryInfo("Afghanistan", "阿富汗", "AF", "+93"));
        COUNTRY.put("AG", new CountryInfo("Antigua and Barbuda", "安提瓜和巴布达", "AG", "+1268"));
        COUNTRY.put("AI", new CountryInfo("Anguilla", "安圭拉", "AI", "+1264"));
        COUNTRY.put("AL", new CountryInfo("Albania", "阿尔巴尼亚", "AL", "+355"));
        COUNTRY.put("AM", new CountryInfo("Armenia", "亚美尼亚", "AM", "+374"));
        COUNTRY.put("AO", new CountryInfo("Angola", "安哥拉", "AO", "+244"));
        COUNTRY.put("AR", new CountryInfo("Argentina", "阿根廷", "AR", "+54"));
        COUNTRY.put("AS", new CountryInfo("American Samoa", "美属萨摩亚", "AS", "+1684"));
        COUNTRY.put("AT", new CountryInfo("Austria", "奥地利", "AT", "+43"));
        COUNTRY.put("AU", new CountryInfo("Australia", "澳大利亚", "AU", "+61"));
        COUNTRY.put("AW", new CountryInfo("Aruba", "阿鲁巴", "AW", "+297"));
        COUNTRY.put("AZ", new CountryInfo("Azerbaijan", "阿塞拜疆", "AZ", "+994"));
        COUNTRY.put("BA", new CountryInfo("Bosniaand Herzegovina", "波斯尼亚和黑塞哥维那", "BA", "+387"));
        COUNTRY.put("BB", new CountryInfo("Barbados", "巴巴多斯", "BB", "+1246"));
        COUNTRY.put("BD", new CountryInfo("Bangladesh", "孟加拉国", "BD", "+880"));
        COUNTRY.put("BE", new CountryInfo("Belgium", "比利时", "BE", "+32"));
        COUNTRY.put("BF", new CountryInfo("Burkina Faso", "布基纳法索", "BF", "+226"));
        COUNTRY.put("BG", new CountryInfo("Bulgaria", "保加利亚", "BG", "+359"));
        COUNTRY.put("BH", new CountryInfo("Bahrain", "巴林", "BH", "+973"));
        COUNTRY.put("BI", new CountryInfo("Burundi", "布隆迪", "BI", "+257"));
        COUNTRY.put("BJ", new CountryInfo("Benin", "贝宁", "BJ", "+229"));
        COUNTRY.put("BM", new CountryInfo("Bermuda", "百慕大群岛", "BM", "+1441"));
        COUNTRY.put("BN", new CountryInfo("Brunei", "文莱", "BN", "+673"));
        COUNTRY.put("BO", new CountryInfo("Bolivia", "玻利维亚", "BO", "+591"));
        COUNTRY.put("BQ", new CountryInfo("Caribisch Nederland", "荷兰加勒比", "BQ", "+599"));
        COUNTRY.put("BR", new CountryInfo("Brazil", "巴西", "BR", "+55"));
        COUNTRY.put("BS", new CountryInfo("Bahamas", "巴哈马", "BS", "+1242"));
        COUNTRY.put("BT", new CountryInfo("Bhutan", "不丹", "BT", "+975"));
        COUNTRY.put("BW", new CountryInfo("Botswana", "博茨瓦纳", "BW", "+267"));
        COUNTRY.put("BY", new CountryInfo("Belarus", "白俄罗斯", "BY", "+375"));
        COUNTRY.put("BZ", new CountryInfo("Belize", "伯利兹", "BZ", "+501"));
        COUNTRY.put("CA", new CountryInfo("Canada", "加拿大", "CA", "+1"));
        COUNTRY.put("CD",
            new CountryInfo("Democratic Republic of theCongo", "刚果民主共和国", "CD", "+243"));
        COUNTRY.put("CF", new CountryInfo("Central African Republic", "中非共和国", "CF", "+236"));
        COUNTRY.put("CG", new CountryInfo("Republic Of The Congo", "刚果共和国", "CG", "+242"));
        COUNTRY.put("CH", new CountryInfo("Switzerland", "瑞士", "CH", "+41"));
        COUNTRY.put("CI", new CountryInfo("Ivory Coast", "象牙海岸", "CI", "+225"));
        COUNTRY.put("CK", new CountryInfo("Cook Islands", "库克群岛", "CK", "+682"));
        COUNTRY.put("CL", new CountryInfo("Chile", "智利", "CL", "+56"));
        COUNTRY.put("CM", new CountryInfo("Cameroon", "喀麦隆", "CM", "+237"));
        COUNTRY.put("CN", new CountryInfo("China", "中国", "CN", "+86"));
        COUNTRY.put("CO", new CountryInfo("Colombia", "哥伦比亚", "CO", "+57"));
        COUNTRY.put("CR", new CountryInfo("CostaRica", "哥斯达黎加", "CR", "+506"));
        COUNTRY.put("CU", new CountryInfo("Cuba", "古巴", "CU", "+53"));
        COUNTRY.put("CV", new CountryInfo("Cape Verde", "开普", "CV", "+238"));
        COUNTRY.put("CW", new CountryInfo("Curacao", "库拉索", "CW", "+599"));
        COUNTRY.put("CY", new CountryInfo("Cyprus", "塞浦路斯", "CY", "+357"));
        COUNTRY.put("CZ", new CountryInfo("Czech", "捷克", "CZ", "+420"));
        COUNTRY.put("DE", new CountryInfo("Germany", "德国", "DE", "+49"));
        COUNTRY.put("DJ", new CountryInfo("Djibouti", "吉布提", "DJ", "+253"));
        COUNTRY.put("DK", new CountryInfo("Denmark", "丹麦", "DK", "+45"));
        COUNTRY.put("DM", new CountryInfo("Dominica", "多米尼加", "DM", "+1767"));
        COUNTRY.put("DO", new CountryInfo("dominican republic", "多米尼加共和国", "DO", "+1809"));
        COUNTRY.put("DZ", new CountryInfo("Algeria", "阿尔及利亚", "DZ", "+213"));
        COUNTRY.put("EC", new CountryInfo("Ecuador", "厄瓜多尔", "EC", "+593"));
        COUNTRY.put("EE", new CountryInfo("Estonia", "爱沙尼亚", "EE", "+372"));
        COUNTRY.put("EG", new CountryInfo("Egypt", "埃及", "EG", "+20"));
        COUNTRY.put("ER", new CountryInfo("Eritrea", "厄立特里亚", "ER", "+291"));
        COUNTRY.put("ES", new CountryInfo("Spain", "西班牙", "ES", "+34"));
        COUNTRY.put("ET", new CountryInfo("Ethiopia", "埃塞俄比亚", "ET", "+251"));
        COUNTRY.put("FI", new CountryInfo("Finland", "芬兰", "FI", "+358"));
        COUNTRY.put("FJ", new CountryInfo("Fiji", "斐济", "FJ", "+679"));
        COUNTRY.put("FM", new CountryInfo("Micronesia", "密克罗尼西亚", "FM", "+691"));
        COUNTRY.put("FO", new CountryInfo("Faroe Islands", "法罗群岛", "FO", "+298"));
        COUNTRY.put("FR", new CountryInfo("France", "法国", "FR", "+33"));
        COUNTRY.put("GA", new CountryInfo("Gabon", "加蓬", "GA", "+241"));
        COUNTRY.put("GB", new CountryInfo("United Kingdom", "英国", "GB", "+44"));
        COUNTRY.put("GD", new CountryInfo("Grenada", "格林纳达", "GD", "+1473"));
        COUNTRY.put("GE", new CountryInfo("Georgia", "格鲁吉亚", "GE", "+995"));
        COUNTRY.put("GF", new CountryInfo("French Guiana", "法属圭亚那", "GF", "+594"));
        COUNTRY.put("GH", new CountryInfo("Ghana", "加纳", "GH", "+233"));
        COUNTRY.put("GI", new CountryInfo("Gibraltar", "直布罗陀", "GI", "+350"));
        COUNTRY.put("GL", new CountryInfo("Greenland", "格陵兰岛", "GL", "+299"));
        COUNTRY.put("GM", new CountryInfo("Gambia", "冈比亚", "GM", "+220"));
        COUNTRY.put("GN", new CountryInfo("Guinea", "几内亚", "GN", "+224"));
        COUNTRY.put("GP", new CountryInfo("Guadeloupe", "瓜德罗普岛", "GP", "+590"));
        COUNTRY.put("GQ", new CountryInfo("Equatorial Guinea", "赤道几内亚", "GQ", "+240"));
        COUNTRY.put("GR", new CountryInfo("Greece", "希腊", "GR", "+30"));
        COUNTRY.put("GT", new CountryInfo("Guatemala", "瓜地马拉", "GT", "+502"));
        COUNTRY.put("GU", new CountryInfo("Guam", "关岛", "GU", "+1671"));
        COUNTRY.put("GW", new CountryInfo("Guinea-Bissau", "几内亚比绍共和国", "GW", "+245"));
        COUNTRY.put("GY", new CountryInfo("Guyana", "圭亚那", "GY", "+592"));
        COUNTRY.put("HK", new CountryInfo("Hong Kong", "中国香港", "HK", "+852"));
        COUNTRY.put("HN", new CountryInfo("Honduras", "洪都拉斯", "HN", "+504"));
        COUNTRY.put("HR", new CountryInfo("Croatia", "克罗地亚", "HR", "+385"));
        COUNTRY.put("HT", new CountryInfo("Haiti", "海地", "HT", "+509"));
        COUNTRY.put("HU", new CountryInfo("Hungary", "匈牙利", "HU", "+36"));
        COUNTRY.put("ID", new CountryInfo("Indonesia", "印度尼西亚", "ID", "+62"));
        COUNTRY.put("IE", new CountryInfo("Ireland", "爱尔兰", "IE", "+353"));
        COUNTRY.put("IL", new CountryInfo("Israel", "以色列", "IL", "+972"));
        COUNTRY.put("IN", new CountryInfo("India", "印度", "IN", "+91"));
        COUNTRY.put("IQ", new CountryInfo("Iraq", "伊拉克", "IQ", "+964"));
        COUNTRY.put("IR", new CountryInfo("Iran", "伊朗", "IR", "+98"));
        COUNTRY.put("IS", new CountryInfo("Iceland", "冰岛", "IS", "+354"));
        COUNTRY.put("IT", new CountryInfo("Italy", "意大利", "IT", "+39"));
        COUNTRY.put("JM", new CountryInfo("Jamaica", "牙买加", "JM", "+1876"));
        COUNTRY.put("JO", new CountryInfo("Jordan", "约旦", "JO", "+962"));
        COUNTRY.put("JP", new CountryInfo("Japan", "日本", "JP", "+81"));
        COUNTRY.put("KE", new CountryInfo("Kenya", "肯尼亚", "KE", "+254"));
        COUNTRY.put("KG", new CountryInfo("Kyrgyzstan", "吉尔吉斯斯坦", "KG", "+996"));
        COUNTRY.put("KH", new CountryInfo("Cambodia", "柬埔寨", "KH", "+855"));
        COUNTRY.put("KI", new CountryInfo("Kiribati", "基里巴斯", "KI", "+686"));
        COUNTRY.put("KM", new CountryInfo("Comoros", "科摩罗", "KM", "+269"));
        COUNTRY.put("KN", new CountryInfo("Saint Kitts and Nevis", "圣基茨和尼维斯", "KN", "+1869"));
        COUNTRY.put("KP", new CountryInfo("Korea Democratic Rep.", "朝鲜", "KP", "+850"));
        COUNTRY.put("KR", new CountryInfo("South Korea", "韩国", "KR", "+82"));
        COUNTRY.put("KW", new CountryInfo("Kuwait", "科威特", "KW", "+965"));
        COUNTRY.put("KY", new CountryInfo("Cayman Islands", "开曼群岛", "KY", "+1345"));
        COUNTRY.put("KZ", new CountryInfo("Kazakhstan", "哈萨克斯坦", "KZ", "+7"));
        COUNTRY.put("LA", new CountryInfo("Laos", "老挝", "LA", "+856"));
        COUNTRY.put("LB", new CountryInfo("Lebanon", "黎巴嫩", "LB", "+961"));
        COUNTRY.put("LC", new CountryInfo("Saint Lucia", "圣露西亚", "LC", "+1758"));
        COUNTRY.put("LI", new CountryInfo("Liechtenstein", "列支敦士登", "LI", "+423"));
        COUNTRY.put("LK", new CountryInfo("Sri Lanka", "斯里兰卡", "LK", "+94"));
        COUNTRY.put("LR", new CountryInfo("Liberia", "利比里亚", "LR", "+231"));
        COUNTRY.put("LS", new CountryInfo("Lesotho", "莱索托", "LS", "+266"));
        COUNTRY.put("LT", new CountryInfo("Lithuania", "立陶宛", "LT", "+370"));
        COUNTRY.put("LU", new CountryInfo("Luxembourg", "卢森堡", "LU", "+352"));
        COUNTRY.put("LV", new CountryInfo("Latvia", "拉脱维亚", "LV", "+371"));
        COUNTRY.put("LY", new CountryInfo("Libya", "利比亚", "LY", "+218"));
        COUNTRY.put("MA", new CountryInfo("Morocco", "摩洛哥", "MA", "+212"));
        COUNTRY.put("MC", new CountryInfo("Monaco", "摩纳哥", "MC", "+377"));
        COUNTRY.put("MD", new CountryInfo("Moldova", "摩尔多瓦", "MD", "+373"));
        COUNTRY.put("ME", new CountryInfo("Montenegro", "黑山", "ME", "+382"));
        COUNTRY.put("MG", new CountryInfo("Madagascar", "马达加斯加", "MG", "+261"));
        COUNTRY.put("MH", new CountryInfo("Marshall Islands", "马绍尔群岛", "MH", "+692"));
        COUNTRY.put("MK", new CountryInfo("Macedonia", "马其顿", "MK", "+389"));
        COUNTRY.put("ML", new CountryInfo("Mali", "马里", "ML", "+223"));
        COUNTRY.put("MM", new CountryInfo("Myanmar", "缅甸", "MM", "+95"));
        COUNTRY.put("MN", new CountryInfo("Mongolia", "蒙古", "MN", "+976"));
        COUNTRY.put("MO", new CountryInfo("Macau", "中国澳门", "MO", "+853"));
        COUNTRY.put("MR", new CountryInfo("Mauritania", "毛里塔尼亚", "MR", "+222"));
        COUNTRY.put("MS", new CountryInfo("Montserrat", "蒙特塞拉特岛", "MS", "+1664"));
        COUNTRY.put("MT", new CountryInfo("Malta", "马耳他", "MT", "+356"));
        COUNTRY.put("MU", new CountryInfo("Mauritius", "毛里求斯", "MU", "+230"));
        COUNTRY.put("MV", new CountryInfo("Maldives", "马尔代夫", "MV", "+960"));
        COUNTRY.put("MW", new CountryInfo("Malawi", "马拉维", "MW", "+265"));
        COUNTRY.put("MX", new CountryInfo("Mexico", "墨西哥", "MX", "+52"));
        COUNTRY.put("MY", new CountryInfo("Malaysia", "马来西亚", "MY", "+60"));
        COUNTRY.put("MZ", new CountryInfo("Mozambique", "莫桑比克", "MZ", "+258"));
        COUNTRY.put("NA", new CountryInfo("Namibia", "纳米比亚", "NA", "+264"));
        COUNTRY.put("NC", new CountryInfo("New Caledonia", "新喀里多尼亚", "NC", "+687"));
        COUNTRY.put("NE", new CountryInfo("Niger", "尼日尔", "NE", "+227"));
        COUNTRY.put("NG", new CountryInfo("Nigeria", "尼日利亚", "NG", "+234"));
        COUNTRY.put("NI", new CountryInfo("Nicaragua", "尼加拉瓜", "NI", "+505"));
        COUNTRY.put("NL", new CountryInfo("Netherlands", "荷兰", "NL", "+31"));
        COUNTRY.put("NO", new CountryInfo("Norway", "挪威", "NO", "+47"));
        COUNTRY.put("NP", new CountryInfo("Nepal", "尼泊尔", "NP", "+977"));
        COUNTRY.put("NR", new CountryInfo("Nauru", "拿鲁岛", "NR", "+674"));
        COUNTRY.put("NZ", new CountryInfo("New Zealand", "新西兰", "NZ", "+64"));
        COUNTRY.put("OM", new CountryInfo("Oman", "阿曼", "OM", "+968"));
        COUNTRY.put("PA", new CountryInfo("Panama", "巴拿马", "PA", "+507"));
        COUNTRY.put("PE", new CountryInfo("Peru", "秘鲁", "PE", "+51"));
        COUNTRY.put("PF", new CountryInfo("French Polynesia", "法属波利尼西亚", "PF", "+689"));
        COUNTRY.put("PG", new CountryInfo("Papua New Guinea", "巴布亚新几内亚", "PG", "+675"));
        COUNTRY.put("PH", new CountryInfo("Philippines", "菲律宾", "PH", "+63"));
        COUNTRY.put("PK", new CountryInfo("Pakistan", "巴基斯坦", "PK", "+92"));
        COUNTRY.put("PL", new CountryInfo("Poland", "波兰", "PL", "+48"));
        COUNTRY.put("PM", new CountryInfo("Saint Pierreand Miquelon", "圣彼埃尔和密克隆岛", "PM", "+508"));
        COUNTRY.put("PR", new CountryInfo("Puerto Rico", "波多黎各", "PR", "+1787"));
        COUNTRY.put("PT", new CountryInfo("Portugal", "葡萄牙", "PT", "+351"));
        COUNTRY.put("PW", new CountryInfo("Palau", "帕劳", "PW", "+680"));
        COUNTRY.put("PY", new CountryInfo("Paraguay", "巴拉圭", "PY", "+595"));
        COUNTRY.put("QA", new CountryInfo("Qatar", "卡塔尔", "QA", "+974"));
        COUNTRY.put("RE", new CountryInfo("Réunion Island", "留尼汪", "RE", "+262"));
        COUNTRY.put("RO", new CountryInfo("Romania", "罗马尼亚", "RO", "+40"));
        COUNTRY.put("RS", new CountryInfo("Serbia", "塞尔维亚", "RS", "+381"));
        COUNTRY.put("RU", new CountryInfo("Russia", "俄罗斯", "RU", "+7"));
        COUNTRY.put("RW", new CountryInfo("Rwanda", "卢旺达", "RW", "+250"));
        COUNTRY.put("SA", new CountryInfo("Saudi Arabia", "沙特阿拉伯", "SA", "+966"));
        COUNTRY.put("SB", new CountryInfo("Solomon Islands", "所罗门群岛", "SB", "+677"));
        COUNTRY.put("SC", new CountryInfo("Seychelles", "塞舌尔", "SC", "+248"));
        COUNTRY.put("SD", new CountryInfo("Sudan", "苏丹", "SD", "+249"));
        COUNTRY.put("SE", new CountryInfo("Sweden", "瑞典", "SE", "+46"));
        COUNTRY.put("SG", new CountryInfo("Singapore", "新加坡", "SG", "+65"));
        COUNTRY.put("SI", new CountryInfo("Slovenia", "斯洛文尼亚", "SI", "+386"));
        COUNTRY.put("SK", new CountryInfo("Slovakia", "斯洛伐克", "SK", "+421"));
        COUNTRY.put("SL", new CountryInfo("Sierra Leone", "塞拉利昂", "SL", "+232"));
        COUNTRY.put("SM", new CountryInfo("San Marino", "圣马力诺", "SM", "+378"));
        COUNTRY.put("SN", new CountryInfo("Senegal", "塞内加尔", "SN", "+221"));
        COUNTRY.put("SO", new CountryInfo("Somalia", "索马里", "SO", "+252"));
        COUNTRY.put("SR", new CountryInfo("Suriname", "苏里南", "SR", "+597"));
        COUNTRY.put("ST", new CountryInfo("Sao Tome and Principe", "圣多美和普林西比", "ST", "+239"));
        COUNTRY.put("SV", new CountryInfo("ElSalvador", "萨尔瓦多", "SV", "+503"));
        COUNTRY.put("SY", new CountryInfo("Syria", "叙利亚", "SY", "+963"));
        COUNTRY.put("SZ", new CountryInfo("Swaziland", "斯威士兰", "SZ", "+268"));
        COUNTRY.put("TC", new CountryInfo("Turksand Caicos Islands", "特克斯和凯科斯群岛", "TC", "+1649"));
        COUNTRY.put("TD", new CountryInfo("Chad", "乍得", "TD", "+235"));
        COUNTRY.put("TG", new CountryInfo("Togo", "多哥", "TG", "+228"));
        COUNTRY.put("TH", new CountryInfo("Thailand", "泰国", "TH", "+66"));
        COUNTRY.put("TJ", new CountryInfo("Tajikistan", "塔吉克斯坦", "TJ", "+992"));
        COUNTRY.put("TL", new CountryInfo("East Timor", "东帝汶", "TL", "+670"));
        COUNTRY.put("TM", new CountryInfo("Turkmenistan", "土库曼斯坦", "TM", "+993"));
        COUNTRY.put("TN", new CountryInfo("Tunisia", "突尼斯", "TN", "+216"));
        COUNTRY.put("TO", new CountryInfo("Tonga", "汤加", "TO", "+676"));
        COUNTRY.put("TR", new CountryInfo("Turkey", "土耳其", "TR", "+90"));
        COUNTRY.put("TT", new CountryInfo("Trinidadand Tobago", "特立尼达和多巴哥", "TT", "+1868"));
        COUNTRY.put("TW", new CountryInfo("Taiwan", "中国台湾", "TW", "+886"));
        COUNTRY.put("TZ", new CountryInfo("Tanzania", "坦桑尼亚", "TZ", "+255"));
        COUNTRY.put("UA", new CountryInfo("Ukraine", "乌克兰", "UA", "+380"));
        COUNTRY.put("UG", new CountryInfo("Uganda", "乌干达", "UG", "+256"));
        COUNTRY.put("US", new CountryInfo("United States", "美国", "US", "+1"));
        COUNTRY.put("UY", new CountryInfo("Uruguay", "乌拉圭", "UY", "+598"));
        COUNTRY.put("UZ", new CountryInfo("Uzbekistan", "乌兹别克斯坦", "UZ", "+998"));
        COUNTRY.put("VC",
            new CountryInfo("Saint Vincent and The Grenadines", "圣文森特和格林纳丁斯", "VC", "+1784"));
        COUNTRY.put("VE", new CountryInfo("Venezuela", "委内瑞拉", "VE", "+58"));
        COUNTRY.put("VG", new CountryInfo("VirginIslands,British", "英属处女群岛", "VG", "+1284"));
        COUNTRY.put("VN", new CountryInfo("Vietnam", "越南", "VN", "+84"));
        COUNTRY.put("VU", new CountryInfo("Vanuatu", "瓦努阿图", "VU", "+678"));
        COUNTRY.put("WS", new CountryInfo("Samoa", "萨摩亚", "WS", "+685"));
        COUNTRY.put("YE", new CountryInfo("Yemen", "也门", "YE", "+967"));
        COUNTRY.put("YT", new CountryInfo("Mayotte", "马约特", "YT", "+269"));
        COUNTRY.put("ZA", new CountryInfo("South Africa", "南非", "ZA", "+27"));
        COUNTRY.put("ZM", new CountryInfo("Zambia", "赞比亚", "ZM", "+260"));
        COUNTRY.put("ZW", new CountryInfo("Zimbabwe", "津巴布韦", "ZW", "+263"));
    }

    /**
     * 获取国家电话区号
     * @param countryCode {@link String} 国家code
     * @return {@link String} 电话区号
     */
    public static String getPhoneCode(String countryCode) {
        if (StringUtils.isBlank(countryCode)) {
            return countryCode;
        }
        CountryInfo countryInfo = COUNTRY.get(countryCode);
        return Objects.isNull(countryInfo) ? countryCode : countryInfo.getPhoneCode();
    }

    @Setter
    @Getter
    public static class CountryInfo implements Serializable {
        private String englishName;
        private String chineseName;
        private String countryCode;
        private String phoneCode;

        public CountryInfo(String englishName, String chineseName, String countryCode,
                           String phoneCode) {
            this.englishName = englishName;
            this.chineseName = chineseName;
            this.countryCode = countryCode;
            this.phoneCode = phoneCode;
        }

    }
}
