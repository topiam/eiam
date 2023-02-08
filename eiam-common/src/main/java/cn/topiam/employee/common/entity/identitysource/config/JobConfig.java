/*
 * eiam-common - Employee Identity and Access Management Program
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
package cn.topiam.employee.common.entity.identitysource.config;

import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.scheduling.support.CronExpression;

import com.alibaba.fastjson2.annotation.JSONField;
import com.cronutils.builder.CronBuilder;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.field.expression.Every;
import com.cronutils.model.field.expression.FieldExpression;
import com.cronutils.model.field.value.IntegerFieldValue;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.topiam.employee.support.exception.TopIamException;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import static com.cronutils.model.field.expression.FieldExpressionFactory.*;

/**
 * 任务配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/9/24 23:09
 */
@Slf4j
@Data
@Schema(description = "任务配置")
public class JobConfig {
    /**
     * 模式
     */
    @Parameter(description = "任务执行模式")
    @NotNull(message = "请选择任务执行模式")
    private Mode            mode;

    /**
     * 任务执行星期值
     */
    @NotNull(message = "请选择任务执行星期值")
    @Parameter(description = "任务执行星期值")
    private List<DayOfWeek> dayOfWeek;

    /**
     * 值
     */
    @Parameter(description = "任务执行值")
    @NotEmpty(message = "任务执行值不能为空")
    @JsonAlias({ "time", "interval" })
    private String          value;

    public enum Mode {
                      /**
                       * 周期
                       */
                      period,
                      /**
                       * 定时
                       */
                      timed
    }

    /**
     * 星期几
     */
    public enum DayOfWeek {
                           /**
                            * 总是
                            */
                           always(between(1, 7)),
                           /**
                            * 周一
                            */
                           monday(on(1)),
                           /**
                            * 周二
                            */
                           tuesday(on(2)),
                           /**
                            * 周三
                            */
                           wednesday(on(3)),
                           /**
                            * 周四
                            */
                           thursday(on(4)),
                           /**
                            * 周五
                            */
                           friday(on(5)),
                           /**
                            * 周六
                            */
                           saturday(on(6)),
                           /**
                            * 周天
                            */
                           sunday(on(7));

        private final FieldExpression code;

        DayOfWeek(FieldExpression code) {
            this.code = code;
        }

        public FieldExpression getCode() {
            return code;
        }
    }

    /**
     * 获取表达式
     *
     * @return {@link  String}
     */
    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    public String getCronExpression(CronType cronType) {
        if (!(cronType.equals(CronType.SPRING) || cronType.equals(CronType.QUARTZ))) {
            throw new TopIamException("不支持该类型 [" + cronType + "]");
        }
        //小时
        FieldExpression hour = always();
        //分钟
        FieldExpression minute = always();
        //秒
        FieldExpression second = always();
        //处理星期
        FieldExpression dayOfWeek = null;
        if (this.dayOfWeek.contains(DayOfWeek.always)) {
            dayOfWeek = DayOfWeek.always.getCode();
        } else {
            for (DayOfWeek week : this.dayOfWeek) {
                if (Objects.isNull(dayOfWeek)) {
                    dayOfWeek = week.getCode();
                    continue;
                }
                dayOfWeek = dayOfWeek.and(week.getCode());
            }
        }
        //模式为定时 解析时分秒
        if (mode.equals(Mode.timed)) {
            LocalTime time = LocalTime.parse(value, DateTimeFormatter.ofPattern("H[H]:mm:ss"));
            hour = on(time.getHour());
            minute = on(time.getMinute());
            second = on(time.getSecond());
        }
        //模式为周期（0- 某个小时）执行
        if (mode.equals(Mode.period)) {
            hour = new Every(on(0), new IntegerFieldValue(Integer.parseInt(value)));
            minute = on(0);
            second = on(0);
        }
        /*
         *     Java(Quartz)
         *     *    *    *    *    *    *    *
         *     -    -    -    -    -    -    -
         *     |    |    |    |    |    |    |
         *     |    |    |    |    |    |    + year [optional]
         *     |    |    |    |    |    +----- day of week (1 - 7) sun,mon,tue,wed,thu,fri,sat
         *     |    |    |    |    +---------- month (1 - 12) OR jan,feb,mar,apr ...
         *     |    |    |    +--------------- day of month (1 - 31)
         *     |    |    +-------------------- hour (0 - 23)
         *     |    +------------------------- min (0 - 59)
         *     +------------------------------ second (0 - 59)
         */

        /*
         *     Java(Spring)
         *      *    *    *    *    *    *    *
         *      -    -    -    -    -    -    -
         *      |    |    |    |    |    |    |
         *      |    |    |    |    |    |    + year [optional]
         *      |    |    |    |    |    +----- day of week (1 - 7) sun,mon,tue,wed,thu,fri,sat
         *      |    |    |    |    +---------- month (1 - 12) OR jan,feb,mar,apr ...
         *      |    |    |    +--------------- day of month (1 - 31)
         *      |    |    +-------------------- hour (0 - 23)
         *      |    +------------------------- min (0 - 59)
         *      +------------------------------ second (0 - 59)
         */
        CronBuilder cronBuilder = CronBuilder
            .cron(CronDefinitionBuilder.instanceDefinitionFor(cronType))
            //秒
            .withSecond(second)
            //分钟
            .withMinute(minute)
            //小时
            .withHour(hour)
            //天
            .withDoM(questionMark())
            //月份
            .withMonth(always())
            //星期几
            .withDoW(dayOfWeek);
        //年
        if (cronType.equals(CronType.QUARTZ)) {
            cronBuilder.withYear(always());
        }
        Cron cron = cronBuilder.instance();
        // Obtain the string expression
        String cronAsString = cron.asString();
        //QUARTZ
        if (cronType.equals(CronType.QUARTZ)) {
            try {
                org.quartz.CronExpression cronExpression = new org.quartz.CronExpression(
                    cronAsString);
                log.debug("Quartz Cron Expression: \n{} ", cronExpression.getExpressionSummary());
                return cronExpression.toString();
            } catch (ParseException exception) {
                throw new RuntimeException(exception);
            }
        }
        //SPRING
        CronExpression parse = CronExpression.parse(cronAsString);
        log.debug("Spring Cron Expression: {} ", parse);
        return parse.toString();
    }
}
