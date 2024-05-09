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
package cn.topiam.employee.common.entity.app;

import java.math.BigInteger;
import java.time.LocalDateTime;

import org.hibernate.annotations.SoftDelete;

import cn.topiam.employee.common.enums.app.AppCertUsingType;
import cn.topiam.employee.support.repository.SoftDeleteConverter;
import cn.topiam.employee.support.repository.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import static cn.topiam.employee.support.repository.base.BaseEntity.IS_DELETED_COLUMN;

/**
 * @author TopIAM
 * Created by support@topiam.cn on 2022/5/31 21:51
 */
@Getter
@Setter
@ToString
@Entity
@Accessors(chain = true)
@Table(name = "eiam_app_cert")
@SoftDelete(columnName = IS_DELETED_COLUMN, converter = SoftDeleteConverter.class)
public class AppCertEntity extends BaseEntity {

    public static final String APP_ID_FIELD_NAME     = "appId";

    public static final String USING_TYPE_FIELD_NAME = "usingType";

    /**
     * 应用ID
     */
    @Column(name = "app_id")
    private String             appId;

    /**
     * 证书序列号
     */
    @Column(name = "serial_")
    private BigInteger         serial;

    /**
     * 主题信息
     */
    @Column(name = "subject_")
    private String             subject;

    /**
     * 签发者信息
     */
    @Column(name = "issuer_")
    private String             issuer;

    /**
     * 开始时间
     */
    @Column(name = "begin_date")
    private LocalDateTime      beginDate;

    /**
     * 结束时间
     */
    @Column(name = "end_date")
    private LocalDateTime      endDate;

    /**
     * 有效天数
     */
    @Column(name = "validity_")
    private Integer            validity;

    /**
     * 算法
     */
    @Column(name = "sign_algo")
    private String             signAlgo;

    /**
     * 私钥长度
     */
    @Column(name = "key_long")
    private Integer            keyLong;

    /**
     * 私钥
     */
    @Column(name = "private_key")
    private String             privateKey;

    /**
     * 公钥
     */
    @Column(name = "public_key")
    private String             publicKey;

    /**
     * 证书
     */
    @Column(name = "cert_")
    private String             cert;

    /**
     * 使用类型
     */
    @Column(name = "using_type")
    private AppCertUsingType   usingType;
}
