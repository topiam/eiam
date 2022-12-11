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
package cn.topiam.employee.support.excel;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.util.JdkIdGenerator;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;

import cn.topiam.employee.support.validation.ValidationHelp;

/**
 * Excel工具类
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020/9/16
 */
public class ExcelUtils {

    /**
     * 导出Excel模板
     *
     * @param response {@link HttpServletResponse}
     * @param data {@link List}
     * @param fileName {@link String}
     * @param sheetName {@link String}
     * @param clazz {@link Class}
     * @throws Exception Exception
     */
    public static void writeExcelTemplate(HttpServletResponse response, List<?> data,
                                          String fileName, String sheetName,
                                          Class<?> clazz) throws Exception {
        //表头样式
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        //设置表头居中对齐
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        //内容样式
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        //设置内容靠左对齐
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(
            headWriteCellStyle, contentWriteCellStyle);

        EasyExcel.write(getOutputStream(fileName, response), clazz).excelType(ExcelTypeEnum.XLSX)
            .sheet(sheetName).registerWriteHandler(horizontalCellStyleStrategy).doWrite(data);
    }

    /**
     * 构造EXCEL格式的输出流
     *
     * @param fileName {@link String}
     * @param response {@link HttpServletResponse}
     * @return OutputStream
     * @throws Exception Exception
     */
    private static OutputStream getOutputStream(String fileName,
                                                HttpServletResponse response) throws Exception {
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        return response.getOutputStream();
    }

    /**
     * 验证数据
     * @param data {@link Object}
     * @param context  {@link AnalysisContext}
     * @return {@link List}
     */
    public static List<DataImportFailReason> validation(Object data, AnalysisContext context) {
        //错误信息 key：行号，value ：消息
        List<DataImportFailReason> error = new ArrayList<>();
        //内容 key 字段名，value：excel 注解头内容属性
        HashMap<String, String> propertyMap = new HashMap<>(16);
        //将内容属性处理为MAP
        for (Map.Entry<Integer, Head> m : context.currentReadHolder().excelReadHeadProperty()
            .getHeadMap().entrySet()) {
            //封装内容，key为列顺序，value为excel列名
            List<String> nameList = m.getValue().getHeadNameList();
            propertyMap.put(m.getValue().getField().getName(), nameList.get(nameList.size() - 1));
        }
        //行号
        Integer index = context.readRowHolder().getRowIndex();
        //验证
        ValidationHelp.ValidationResult result = ValidationHelp.validateEntity(data);
        //存在异常
        if (result.isHasErrors()) {
            //错误消息
            Map<String, String> errorMsg = result.getErrorMsg();
            //循环错误
            for (Map.Entry<String, String> m : errorMsg.entrySet()) {
                //封装数据
                DataImportFailReason build = DataImportFailReason.builder().build();
                build.setId(new JdkIdGenerator().generateId().toString());
                //行号
                build.setRow(index);
                //列名
                build.setName(propertyMap.get(m.getKey()));
                //错误消息
                build.setReason(errorMsg.get(m.getKey()));
                error.add(build);
            }
        }
        return error;
    }

}
