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
package cn.topiam.employee.common.util;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.springframework.core.io.ClassPathResource;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/3/30 22:41
 */
public class ImageAvatarUtils {

    /**
     * 绘制字体头像 默认大小100*100
     * 如果是英文名，只显示首字母大写
     * 如果是中文名，只显示最后两个字
     * @param name {@link String}
     */
    public static BufferedImage generateAvatarImg(String name) {
        try {
            int width = 100;
            int height = 100;
            int nameLen = name.length();
            String nameWritten;
            // 如果用户输入的姓名少于等于2个字符，不用截取
            if (nameLen <= 2) {
                nameWritten = name;
            } else {
                // 如果用户输入的姓名大于等于3个字符，截取后面两位
                String first = name.substring(0, 1);
                if (isChinese(first)) {
                    // 截取倒数两位汉字
                    nameWritten = name.substring(nameLen - 2);
                } else {
                    // 截取前面的两个英文字母
                    nameWritten = name.substring(0, 2).toUpperCase();
                }
            }

            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            Graphics2D g2 = (Graphics2D) bi.getGraphics();
            //消除文字锯齿
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            //消除画图锯
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setBackground(getRandomColor());

            g2.clearRect(0, 0, width, height);

            g2.setPaint(Color.WHITE);

            //加载外部字体文件
            ClassPathResource resource = new ClassPathResource(
                "/fonts/AlibabaPuHuiTi-2-55-Regular.ttf");
            InputStream inputStream = resource.getInputStream();
            Font font = Font.createFont(java.awt.Font.TRUETYPE_FONT, inputStream);
            // 两个字及以上
            if (nameWritten.length() >= 2) {
                font = font.deriveFont(Font.BOLD, 30);
                g2.setFont(font);
                String firstWritten = nameWritten.substring(0, 1);
                String secondWritten = nameWritten.substring(1, 2);
                // 两个中文 如 张三
                if (isChinese(firstWritten) && isChinese(secondWritten)) {
                    g2.drawString(nameWritten, 20, 60);
                }
                // 首中次英 如 张S
                else if (isChinese(firstWritten) && !isChinese(secondWritten)) {
                    g2.drawString(nameWritten, 27, 60);
                }
                // 首英,如 ZS
                else {
                    nameWritten = nameWritten.substring(0, 1);
                }
            }
            // 一个字
            if (nameWritten.length() == 1) {
                // 中文
                if (isChinese(nameWritten)) {
                    font = font.deriveFont(Font.PLAIN, 50);
                    g2.setFont(font);
                    g2.drawString(nameWritten, 25, 70);
                }
                // 英文
                else {
                    font = font.deriveFont(Font.PLAIN, 55);
                    g2.setFont(font);
                    g2.drawString(nameWritten.toUpperCase(), 33, 67);
                }

            }
            return makeRoundedCorner(bi, 99);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断字符串是否为中文
     * @param str {@link String}
     * @return {@link Boolean}
     */
    public static boolean isChinese(String str) {
        String regEx = "[\\u4e00-\\u9fa5]+";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 获得随机颜色
     * @return {@link Color}
     */
    private static Color getRandomColor() {
        String[] beautifulColors = new String[] { "22,119,255" };
        int len = beautifulColors.length;
        Random random = new Random();
        String[] color = beautifulColors[random.nextInt(len)].split(",");
        return new Color(Integer.parseInt(color[0]), Integer.parseInt(color[1]),
            Integer.parseInt(color[2]));
    }

    /**
     * 图片做圆角处理
     * @param image {@link BufferedImage}
     * @param cornerRadius {@link Integer}
     * @return {@link BufferedImage}
     */
    public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return output;
    }

    /**
     * BufferedImage 转换为 base64编码
     * @param bufferedImage {@link BufferedImage}
     * @return {@link String}
     */
    public static String bufferedImageToBase64(BufferedImage bufferedImage) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", stream);
            Base64 base = new Base64();
            String base64 = base.encodeToString(stream.toByteArray());
            return "data:image/png;base64," + base64;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
