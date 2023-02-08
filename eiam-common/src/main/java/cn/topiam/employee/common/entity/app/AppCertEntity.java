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
package cn.topiam.employee.common.entity.app;

import java.math.BigInteger;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;
import org.hibernate.annotations.Where;

import cn.topiam.employee.common.enums.app.AppCertUsingType;
import cn.topiam.employee.support.repository.domain.LogicDeleteEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_SET;
import static cn.topiam.employee.support.repository.domain.LogicDeleteEntity.SOFT_DELETE_WHERE;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/5/31 20:51
 */
@Getter
@Setter
@ToString
@Entity
@Accessors(chain = true)
@Table(name = "app_cert")
@SQLDelete(sql = "update app_cert set " + SOFT_DELETE_SET + " where id_ = ?")
@SQLDeleteAll(sql = "update app_cert set " + SOFT_DELETE_SET + " where id_ = ?")
@Where(clause = SOFT_DELETE_WHERE)
public class AppCertEntity extends LogicDeleteEntity<Long> {
    /**
     * 应用ID
     */
    @Column(name = "app_id")
    private Long             appId;

    /**
     * 证书序列号
     */
    @Column(name = "serial_")
    private BigInteger       serial;

    /**
     * 主题信息
     */
    @Column(name = "subject_")
    private String           subject;

    /**
     * 签发者信息
     */
    @Column(name = "issuer_")
    private String           issuer;

    /**
     * 开始时间
     */
    @Column(name = "begin_date")
    private LocalDateTime    beginDate;

    /**
     * 结束时间
     */
    @Column(name = "end_date")
    private LocalDateTime    endDate;

    /**
     * 有效天数
     */
    @Column(name = "validity_")
    private Integer          validity;

    /**
     * 算法
     */
    @Column(name = "sign_algo")
    private String           signAlgo;

    /**
     * 私钥长度
     */
    @Column(name = "key_long")
    private Integer          keyLong;

    /**
     * 私钥
     */
    @Column(name = "private_key")
    private String           privateKey;

    /**
     * 公钥
     */
    @Column(name = "public_key")
    private String           publicKey;

    /**
     * 证书
     */
    @Column(name = "cert_")
    private String           cert;

    /**
     * 使用类型
     */
    @Column(name = "using_type")
    private AppCertUsingType usingType;
}
