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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 拼音utils
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/4/17 21:20
 */
public class Pinyin4jUtils {

    /**
     * getFirstSpellPinYin:(多音字的时候获取第一个). <br/>
     * @param src  传入的拼音字符串，以逗号隔开
     * @param isFullSpell 是否全拼，true:全拼，false:第一个汉字全拼(其它汉字取首字母)
     * @return 第一个拼音
     */
    public static String getFirstSpellPinYin(String src, boolean isFullSpell) {
        String targetStr = Pinyin4jUtils
            .makeStringByStringSet(Pinyin4jUtils.getPinyin(src, isFullSpell));
        String[] split = targetStr.split(",");
        if (split.length > 1) {
            targetStr = split[0];
        }
        return targetStr;
    }

    /**
     * makeStringByStringSet:(拼音字符串集合转换字符串(逗号分隔)). <br/>
     * @param stringSet  拼音集合
     * @return  带逗号字符串
     */
    public static String makeStringByStringSet(Set<String> stringSet) {
        StringBuilder str = new StringBuilder();
        int i = 0;
        if (stringSet.size() > 0) {
            for (String s : stringSet) {
                if (i == stringSet.size() - 1) {
                    str.append(s);
                } else {
                    str.append(s).append(",");
                }
                i++;
            }
        }
        return str.toString().toLowerCase();
    }

    /**
     * getPinyin:(获取汉字拼音). <br/>
     * @param src   汉字
     * @param isFullSpell  是否全拼,如果为true：全拼，false:首字全拼
     * @return {@link  Set}
     */
    @SuppressWarnings("AlibabaAvoidComplexCondition")
    public static Set<String> getPinyin(String src, boolean isFullSpell) {
        if (src != null && !"".equalsIgnoreCase(src.trim())) {
            char[] srcChar;
            srcChar = src.toCharArray();
            // 汉语拼音格式输出类
            HanyuPinyinOutputFormat hanYuPinOutputFormat = new HanyuPinyinOutputFormat();

            // 输出设置，大小写，音标方式等
            hanYuPinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            hanYuPinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            hanYuPinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

            String[][] temp = new String[src.length()][];
            for (int i = 0; i < srcChar.length; i++) {
                char c = srcChar[i];
                //中文
                if (String.valueOf(c).matches("[\\u4E00-\\u9FA5]+")) {
                    try {
                        temp[i] = PinyinHelper.toHanyuPinyinStringArray(srcChar[i],
                            hanYuPinOutputFormat);
                        if (!isFullSpell) {
                            if (i == 0) {
                            } else {
                                String[] tTemps = new String[temp[i].length];
                                for (int j = 0; j < temp[i].length; j++) {
                                    char t = temp[i][j].charAt(0);
                                    tTemps[j] = Character.toString(t);
                                }
                                temp[i] = tTemps;
                            }
                        }
                    } catch (BadHanyuPinyinOutputFormatCombination e) {
                        e.printStackTrace();
                    }
                }
                //英文
                else if (((int) c >= 65 && (int) c <= 90) || ((int) c >= 97 && (int) c <= 122)) {
                    temp[i] = new String[] { String.valueOf(srcChar[i]) };
                } else {
                    temp[i] = new String[] { "" };
                }
            }
            String[] pingYinArray = exchange(temp);
            return new HashSet<>(Arrays.asList(pingYinArray));
        }
        return null;
    }

    /**
     * 递归
     * @param strJaggedArray {@link String}
     * @return {@link String}
     */
    public static String[] exchange(String[][] strJaggedArray) {
        String[][] temp = doExchange(strJaggedArray);
        return temp[0];
    }

    /**
     * 递归
     * @param strJaggedArray {@link String}
     * @return {@link String}
     */
    private static String[][] doExchange(String[][] strJaggedArray) {
        int len = strJaggedArray.length;
        if (len >= 2) {
            int len1 = strJaggedArray[0].length;
            int len2 = strJaggedArray[1].length;
            int newLen = len1 * len2;
            String[] temp = new String[newLen];
            int index = 0;
            for (int i = 0; i < len1; i++) {
                for (int j = 0; j < len2; j++) {
                    temp[index] = strJaggedArray[0][i] + strJaggedArray[1][j];
                    index++;
                }
            }
            String[][] newArray = new String[len - 1][];
            System.arraycopy(strJaggedArray, 2, newArray, 1, len - 2);
            newArray[0] = temp;
            return doExchange(newArray);
        } else {
            return strJaggedArray;
        }
    }

    /**
     * 返回中文的首字母
     *
     * @param str {@link  String}
     * @return {@link  String}
     */
    public static String getPinYinHeadChar(String str) {
        StringBuilder convert = new StringBuilder();
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert.append(pinyinArray[0].charAt(0));
            } else {
                convert.append(word);
            }
        }
        return convert.toString();
    }
}
