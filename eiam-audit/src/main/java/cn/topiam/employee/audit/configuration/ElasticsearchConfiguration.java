/*
 * eiam-audit - Employee Identity and Access Management Program
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
package cn.topiam.employee.audit.configuration;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;

import cn.topiam.employee.audit.enums.EventStatus;
import cn.topiam.employee.audit.enums.EventType;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.common.enums.UserType;
import cn.topiam.employee.common.geo.maxmind.enums.GeoLocationProvider;
import cn.topiam.employee.support.util.JsonUtils;

/**
 * ElasticsearchConfiguration
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/11/3 23:31
 */
@Configuration
public class ElasticsearchConfiguration {

    @Bean
    public ElasticsearchCustomConversions elasticsearchCustomConversions() {
        return new ElasticsearchCustomConversions(
            Lists.newArrayList(AuditTypeToStringConverter.INSTANCE,
                StringToAuditTypeConverter.INSTANCE, EventStatusToStringConverter.INSTANCE,
                StringToEventStatusConverter.INSTANCE, ActorTypeToStringConverter.INSTANCE,
                StringToActorTypeConverter.INSTANCE, GeoLocationProviderToStringConverter.INSTANCE,
                StringToGeoLocationProviderConverter.INSTANCE, TargetTypeToStringConverter.INSTANCE,
                StringToTargetTypeConverter.INSTANCE, StringToMapConverter.INSTANCE,
                MapToStringConverter.INSTANCE));
    }

    @WritingConverter
    enum AuditTypeToStringConverter implements Converter<EventType, String> {
                                                                             /**
                                                                              * INSTANCE
                                                                              */
                                                                             INSTANCE,;

        @Override
        public String convert(EventType source) {
            return source.getCode();
        }
    }

    @ReadingConverter
    enum StringToAuditTypeConverter implements Converter<String, EventType> {
                                                                             /**
                                                                              *INSTANCE
                                                                              */
                                                                             INSTANCE;

        @Override
        public EventType convert(@NotNull String source) {
            return EventType.getType(source);
        }
    }

    @WritingConverter
    enum ActorTypeToStringConverter implements Converter<UserType, String> {
                                                                            /**
                                                                             * INSTANCE
                                                                             */
                                                                            INSTANCE,;

        @Override
        public String convert(UserType source) {
            return source.getCode();
        }
    }

    @ReadingConverter
    enum StringToActorTypeConverter implements Converter<String, UserType> {
                                                                            /**
                                                                             *INSTANCE
                                                                             */
                                                                            INSTANCE;

        @Override
        public UserType convert(@NotNull String source) {
            return UserType.getType(source);
        }
    }

    @WritingConverter
    enum TargetTypeToStringConverter implements Converter<TargetType, String> {
                                                                               /**
                                                                                * INSTANCE
                                                                                */
                                                                               INSTANCE,;

        @Override
        public String convert(TargetType source) {
            return source.getCode();
        }
    }

    @ReadingConverter
    enum StringToTargetTypeConverter implements Converter<String, TargetType> {
                                                                               /**
                                                                                *INSTANCE
                                                                                */
                                                                               INSTANCE;

        @Override
        public TargetType convert(@NotNull String source) {
            return TargetType.getType(source);
        }
    }

    @WritingConverter
    enum GeoLocationProviderToStringConverter implements Converter<GeoLocationProvider, String> {
                                                                                                 /**
                                                                                                  * INSTANCE
                                                                                                  */
                                                                                                 INSTANCE,;

        @Override
        public String convert(GeoLocationProvider source) {
            return source.getCode();
        }
    }

    @ReadingConverter
    enum StringToGeoLocationProviderConverter implements Converter<String, GeoLocationProvider> {
                                                                                                 /**
                                                                                                  *INSTANCE
                                                                                                  */
                                                                                                 INSTANCE;

        @Override
        public GeoLocationProvider convert(@NotNull String source) {
            return GeoLocationProvider.getType(source);
        }
    }

    @WritingConverter
    enum MapToStringConverter implements Converter<Map<String, Object>, String> {
                                                                                 /**
                                                                                  * INSTANCE
                                                                                  */
                                                                                 INSTANCE,;

        @Override
        public String convert(@NotNull Map<String, Object> source) {
            return JsonUtils.writeValueAsString(source);
        }
    }

    @ReadingConverter
    enum StringToMapConverter implements Converter<String, Map<String, Object>> {
                                                                                 /**
                                                                                  *INSTANCE
                                                                                  */
                                                                                 INSTANCE;

        @Override
        public Map<String, Object> convert(@NotNull String source) {
            return JsonUtils.readValue(source, new TypeReference<>() {
            });
        }
    }

    @WritingConverter
    enum EventStatusToStringConverter implements Converter<EventStatus, String> {
                                                                                 /**
                                                                                  * INSTANCE
                                                                                  */
                                                                                 INSTANCE,;

        @Override
        public String convert(@NotNull EventStatus source) {
            return source.getCode();
        }
    }

    @ReadingConverter
    enum StringToEventStatusConverter implements Converter<String, EventStatus> {
                                                                                 /**
                                                                                  *INSTANCE
                                                                                  */
                                                                                 INSTANCE;

        @Override
        public EventStatus convert(@NotNull String source) {
            return EventStatus.getType(source);
        }
    }
}
