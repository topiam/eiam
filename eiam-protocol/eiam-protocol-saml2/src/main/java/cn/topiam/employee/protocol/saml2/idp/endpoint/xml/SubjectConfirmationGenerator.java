/*
 * eiam-protocol-saml2 - Employee Identity and Access Management Program
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
package cn.topiam.employee.protocol.saml2.idp.endpoint.xml;

import java.time.Instant;

import org.apache.commons.lang3.StringUtils;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml.saml2.core.impl.SubjectConfirmationBuilder;
import org.opensaml.saml.saml2.core.impl.SubjectConfirmationDataBuilder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Subject 生成器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/6/1 22:52
 */
@RequiredArgsConstructor
public class SubjectConfirmationGenerator {

    /**
     * 认证请求ID
     */
    @Setter
    @Getter
    private String        authnRequestId;
    /**
     * recipient
     */
    private final String  recipient;

    /**
     * 设置此主题无效的时间或之后的时间
     */
    private final Instant notOnOrAfter;

    public SubjectConfirmation generateSubjectConfirmations() {
        SubjectConfirmation subjectConfirmation = new SubjectConfirmationBuilder().buildObject();
        subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);

        SubjectConfirmationData subjectConfirmationData = new SubjectConfirmationDataBuilder()
            .buildObject();
        subjectConfirmationData.setNotOnOrAfter(notOnOrAfter);
        subjectConfirmationData.setRecipient(recipient);
        if (StringUtils.isNotBlank(authnRequestId)) {
            subjectConfirmationData.setInResponseTo(authnRequestId);
        }
        subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);

        return subjectConfirmation;
    }
}
