/*
 * eiam-audit - Employee Identity and Access Management
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
package cn.topiam.employee.audit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import lombok.Builder;
import lombok.Data;

/**
 * 审计elasticsearch实体
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/10/13 23:22
 */
@Data
@Builder
@Document(indexName = "#{@auditDynamicIndexName.getIndexName()}")
@Setting(replicas = 0)
public class AuditElasticSearchEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 6589338521638519634L;

    @Id
    @Field(type = FieldType.Keyword, name = "id")
    private String            id;

    /**
     * Request Id
     */
    @Field(type = FieldType.Keyword, name = "request_id")
    private String            requestId;

    /**
     * Session Id
     */
    @Field(type = FieldType.Keyword, name = "session_id")
    private String            sessionId;

    /**
     * 操作者
     */
    @Field(type = FieldType.Object, name = "actor")
    private Actor             actor;

    /**
     * 事件
     */
    @Field(type = FieldType.Object, name = "event")
    private Event             event;

    /**
     * 操作目标
     */
    @Field(type = FieldType.Object, name = "target")
    private List<Target>      targets;

    /**
     * UserAgent
     */
    @Field(type = FieldType.Object, name = "user_agent")
    private UserAgent         userAgent;

    /**
     * 地理位置
     */
    @Field(type = FieldType.Object, name = "geo_location")
    private GeoLocation       geoLocation;

}
