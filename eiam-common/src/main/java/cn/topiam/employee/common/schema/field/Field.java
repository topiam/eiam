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
package cn.topiam.employee.common.schema.field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 基础控件配置项基类
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2023/4/12 21:27
 */
@Data
@Accessors(chain = true)
public abstract class Field {
    /**
     *标题
     */
    protected String              title;
    /**
     * 输入内容提示
     */
    protected List<String>        placeholder;
    /**
     * 副标题描述
     */
    protected String              description;
    /**
     * 气泡提示，支持 html 格式
     */
    protected Tooltip             tooltip;
    /**
     * 更多说明信息：extra 可以是 html string，也可以是纯文案，会展示在元素下面一行紧贴。
     */
    protected String              extra;
    /**
     * 校验规则，以 ant design Form rules 为准
     */
    protected List<Rule>          rules;
    /**
     *隐藏
     */
    protected Boolean             hidden;
    /**
     *禁用
     */
    protected Boolean             disabled;
    /**
     * 只读
     */
    protected Boolean             readOnly;
    /**
     * 额外属性
     */
    protected Map<String, Object> props;

    /**
     * 数据类型：（参考form-render）
     */
    protected final String        type;
    /**
     * 组件名称（参考form-render）
     */
    protected String              widget;
    /**
     * format
     */
    protected String              format;

    public Field(String type, String widget) {
        this.type = type;
        this.widget = widget;
        this.props = new HashedMap<>(1);
    }

    public Field(String type) {
        this.type = type;
        this.props = new HashedMap<>(1);
    }

    @Data
    public static class Tooltip {
        private String title;

        public Tooltip(String title) {
            this.title = title;
        }
    }

    /**
     * 规则，ant design Form 校验规则
     *
     * @author TopIAM
     * Created by support@topiam.cn on  2023/4/12 21:44
     */
    @Data
    public static class Rule {
        /**
         * 是否必填
         */
        private Boolean required;

        /**
         * 如果字段仅包含空格则校验不通过，只在 type: 'string' 时生效
         */
        private Boolean whitespace;

        /**
         * 错误提示
         */
        private String  message;
        /**
         * 正则表达式
         */
        private String  pattern;
        /**
         * 最小
         */
        private Integer min;
        /**
         * 最大
         */
        private Integer max;
        /**
         * 类型，常见有 string |number |boolean |url | email。
         * 更多请参考：<a href="https://github.com/yiminghe/async-validator#type">...</a>
         */
        private String  type;

        public Rule required(Boolean required) {
            this.required = required;
            return this;
        }

        public Rule message(String message) {
            this.message = message;
            return this;
        }

        public Rule pattern(String pattern) {
            this.pattern = pattern;
            return this;
        }

        public Rule min(Integer min) {
            this.min = min;
            return this;
        }

        public Rule max(Integer max) {
            this.max = max;
            return this;
        }

        public Rule whitespace(Boolean whitespace) {
            this.whitespace = whitespace;
            return this;
        }

        public Rule type(String type) {
            this.type = type;
            return this;
        }
    }

    public Field addRule(Rule rule) {
        if (this.rules == null) {
            this.rules = new ArrayList<>();
        }
        this.rules.add(rule);
        return this;
    }

    public Field addProps(Map<String, Object> props) {
        getProps().putAll(props);
        return this;
    }

    public Field addProps(String key, Object value) {
        getProps().put(key, value);
        return this;
    }
}
