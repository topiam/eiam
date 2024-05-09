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
package cn.topiam.employee.common.entity.account;

import java.io.Serial;
import java.util.Objects;

import org.hibernate.annotations.SoftDelete;

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
 * 三方用户表
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2023/11/30 21:23
 */
@Accessors(chain = true)
@Getter
@Setter
@ToString
@Entity
@Table(name = "eiam_third_party_user")
@SoftDelete(columnName = IS_DELETED_COLUMN, converter = SoftDeleteConverter.class)
public class ThirdPartyUserEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 3716649301707341983L;

    /**
     * OpenId
     */
    @Column(name = "open_id")
    private String            openId;

    /**
     * unionId
     */
    @Column(name = "union_id")
    public String             unionId;

    /**
     * 身份提供商 ID
     */
    @Column(name = "idp_id")
    private String            idpId;

    /**
     * 身份提供商 类型
     */
    @Column(name = "idp_type")
    private String            idpType;

    /**
     * 个人邮箱
     */
    @Column(name = "email_")
    private String            email;

    /**
     * 手机号对应的国家号
     */
    @Column(name = "state_code")
    private String            stateCode;

    /**
     * 手机号
     */
    @Column(name = "mobile_")
    private String            mobile;

    /**
     * 昵称
     */
    @Column(name = "nick_name")
    private String            nickName;

    /**
     * 头像url
     */
    @Column(name = "avatar_url")
    private String            avatarUrl;

    /**
     * 附加信息
     */
    @Column(name = "addition_info")
    private String            additionInfo;

    public boolean equals(ThirdPartyUserEntity that) {
        if (this == that) {
            return true;
        }
        if (that == null || getClass() != that.getClass()) {
            return false;
        }
        return Objects.equals(openId, that.openId) && Objects.equals(unionId, that.unionId)
               && Objects.equals(idpId, that.idpId) && Objects.equals(idpType, that.idpType)
               && Objects.equals(email, that.email) && Objects.equals(stateCode, that.stateCode)
               && Objects.equals(mobile, that.mobile) && Objects.equals(nickName, that.nickName)
               && Objects.equals(avatarUrl, that.avatarUrl)
               && Objects.equals(additionInfo, that.additionInfo);
    }
}
