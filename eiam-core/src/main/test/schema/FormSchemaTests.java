/*
 * eiam-core - Employee Identity and Access Management
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
package schema;

import cn.topiam.employee.common.schema.FormSchema;
import cn.topiam.employee.common.schema.field.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * FormSchemaTests
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/4/12 21:17
 */
public class FormSchemaTests {
    private final Logger logger= LoggerFactory.getLogger(FormSchemaTests.class);


    @Test
    public void test() throws JsonProcessingException {
        //@formatter:off
        // Input
        Input input = (Input) new Input("输入框")
                .addRule(new Field.Rule().required(true).min(2).max(20).type("string"))
                .setPlaceholder(Lists.newArrayList("请输入")).setDisabled(false);
        //数字输入
        NumberInput numberInput = new NumberInput("数字");
        //文本区域
        TextArea textArea = new TextArea("文本区域");
        // Select
        ConcurrentMap<String, Object> selectProps = Maps.newConcurrentMap();
        selectProps.put("allowClear", "true");
        Select select = (Select) new Select("下拉单选",
                Lists.newArrayList(
                        new Option("选项A", "A"),
                        new Option("选项B", "B"),
                        new Option("选项C", "C")))
                .addRule(new Field.Rule().required(true).message("请选择一个选项"))
                .setPlaceholder(Lists.newArrayList("请选择一个选项"))
                .addProps(selectProps);

        //多选Select
        MultiSelect multiSelect = (MultiSelect) new MultiSelect("下拉多选",
                Lists.newArrayList(
                        new Option("选项A", "A"),
                        new Option("选项B", "B"),
                        new Option("选项C", "C")))
                .addRule(new Field.Rule().required(true).message("请选择一个选项"))
                .setPlaceholder(Lists.newArrayList("请选择一个选项"))
                .addProps(selectProps);

        //Switch
        Switch aswitch = new Switch("开关");
        //Radio
        Radio radio = (Radio) new Radio("单选框",
                Lists.newArrayList(
                        new Option("选项A", "A"),
                        new Option("选项B", "B"),
                        new Option("选项C", "C")))
                .setRules(Lists.newArrayList(new Field.Rule().required(true).message("请选择一个选项")));

        //多选框
        Checkboxes checkBoxes = new Checkboxes("多选框", Lists.newArrayList(
                new Option("选项A", "A"),
                new Option("选项B", "B"),
                new Option("选项C", "C")));

        //单选框
        Checkbox checkBox = new Checkbox("单选框");

        //年
        Year year = new Year("年");

        //季度
        Quarter quarter = new Quarter("季度");

        //月份
        Month month = new Month("月份");

        //周选择
        Week week = new Week("周");

        //日期
        Date date = new Date("日期");

        //日期时间
        DateTime dateTime = new DateTime("日期时间");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        //FormSchema
        FormSchema formSchema = new FormSchema();
        formSchema.setType("object");
        HashMap<String, Object> map = new HashMap<>(16);
        map.put("input", input);
        map.put("number", numberInput);
        map.put("textarea", textArea);
        map.put("select", select);
        map.put("multiselect", multiSelect);
        map.put("radio", radio);
        map.put("switch", aswitch);
        map.put("checkbox", checkBox);
        map.put("checkboxes", checkBoxes);
        map.put("year", year);
        map.put("month", month);
        map.put("quarter", quarter);
        map.put("week", week);
        map.put("date", date);
        map.put("date_time", dateTime);
        formSchema.setProperties(map);

        String schema = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(formSchema);

        logger.info("JSON Schema: \n{}",schema);
    }
}
