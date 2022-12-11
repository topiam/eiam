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

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import static org.apache.commons.compress.utils.CharsetNames.UTF_8;

/**
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/3 22:32
 */
public class QrCodeUtils {
    public static String BASE64_URL = "data:image/png;base64,";

    /**
     * 创建二维码
     *
     * @return {@link String}
     */
    public static String createQrCode(String str, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            HashMap<EncodeHintType, Object> hints = new HashMap<>(16);
            hints.put(EncodeHintType.CHARACTER_SET, UTF_8);
            BitMatrix bitMatrix = qrCodeWriter.encode(str, BarcodeFormat.QR_CODE, width, height,
                hints);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            Base64.Encoder encoder = Base64.getEncoder();
            String text = encoder.encodeToString(outputStream.toByteArray());
            return BASE64_URL + text;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
