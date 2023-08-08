/*
 * eiam-console - Employee Identity and Access Management
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
package cn.topiam.employee.console.pojo.query.analysis;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.Period;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.Getter;

import co.elastic.clients.elasticsearch._types.aggregations.CalendarInterval;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import static cn.topiam.employee.support.constant.EiamConstants.DEFAULT_DATE_TIME_FORMATTER_PATTERN;

/**
 * 统计查询入惨
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/11/22 23:16
 */
@Data
@Schema(description = "统计查询入惨")
public class AnalysisQuery implements Serializable {

    /**
     * 开始日期
     */
    @NotNull(message = "开始日期不能为空")
    @Schema(description = "开始日期")
    @DateTimeFormat(pattern = DEFAULT_DATE_TIME_FORMATTER_PATTERN)
    private LocalDateTime startTime;

    /**
     * 截止日期
     */
    @NotNull(message = "截止日期不能为空")
    @Schema(description = "截止日期")
    @DateTimeFormat(pattern = DEFAULT_DATE_TIME_FORMATTER_PATTERN)
    private LocalDateTime endTime;

    public Interval getTimeInterval() {
        Period interval = Period.between(startTime.toLocalDate(), endTime.toLocalDate());
        boolean today = interval.getDays() < 1;
        boolean isDay = interval.getMonths() < 1;
        return today ? Interval.HOUR : isDay ? Interval.DAY : Interval.MONTH;
    }

    @Getter
    public enum Interval {
                          /**
                           * HOUR
                           */
                          HOUR(CalendarInterval.Hour, "HH时"),
                          /**
                           * DAY
                           */
                          DAY(CalendarInterval.Day, "dd日"),
                          /**
                           * MONTH
                           */
                          MONTH(CalendarInterval.Month, "MM月");

        private final CalendarInterval type;
        private final String           format;

        Interval(CalendarInterval type, String format) {
            this.type = type;
            this.format = format;
        }
    }
}
