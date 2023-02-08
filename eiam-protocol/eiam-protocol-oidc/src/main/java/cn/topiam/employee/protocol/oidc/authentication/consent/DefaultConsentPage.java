/*
 * eiam-protocol-oidc - Employee Identity and Access Management Program
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
package cn.topiam.employee.protocol.oidc.authentication.consent;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.OidcScopes;

/**
 * 默认重定向地址
 *
 * @author SanLi
 * Created by qinggang.zuo@gmail.com / 2689170096@qq.com on  2022/12/17 21:47
 */
@SuppressWarnings("AlibabaMethodTooLong")
public class DefaultConsentPage {
    private static final MediaType TEXT_HTML_UTF8 = new MediaType("text", "html",
        StandardCharsets.UTF_8);

    public static void displayConsent(HttpServletRequest request, HttpServletResponse response,
                                      String clientId, Authentication principal,
                                      Set<String> requestedScopes, Set<String> authorizedScopes,
                                      String state) throws IOException {
        String consentPage = generateConsentPage(request, clientId, principal, requestedScopes,
            authorizedScopes, state);
        response.setContentType(TEXT_HTML_UTF8.toString());
        response.setContentLength(consentPage.getBytes(StandardCharsets.UTF_8).length);
        response.getWriter().write(consentPage);
    }

    private static String generateConsentPage(HttpServletRequest request, String clientId,
                                              Authentication principal, Set<String> requestedScopes,
                                              Set<String> authorizedScopes, String state) {
        Set<String> scopesToAuthorize = new HashSet<>();
        Set<String> scopesPreviouslyAuthorized = new HashSet<>();
        for (String scope : requestedScopes) {
            if (authorizedScopes.contains(scope)) {
                scopesPreviouslyAuthorized.add(scope);
                // openid scope does not require consent
            } else if (!scope.equals(OidcScopes.OPENID)) {
                scopesToAuthorize.add(scope);
            }
        }

        StringBuilder builder = new StringBuilder();

        builder.append("<!DOCTYPE html>");
        builder.append("<html lang=\"en\">");
        builder.append("<head>");
        builder.append("    <meta charset=\"utf-8\">");
        builder.append(
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">");
        builder.append(
            "    <link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css\" integrity=\"sha384-JcKb8q3iqJ61gNV9KGb8thSsNjpSL0n8PARn9HuZOnIxN0hoP+VmmDGMN5t9UJ0Z\" crossorigin=\"anonymous\">");
        builder.append("    <title>Consent required</title>");
        builder.append("	<script>");
        builder.append("		function cancelConsent() {");
        builder.append("			document.consent_form.reset();");
        builder.append("			document.consent_form.submit();");
        builder.append("		}");
        builder.append("	</script>");
        builder.append("</head>");
        builder.append("<body>");
        builder.append("<div class=\"container\">");
        builder.append("    <div class=\"py-5\">");
        builder.append("        <h1 class=\"text-center\">Consent required</h1>");
        builder.append("    </div>");
        builder.append("    <div class=\"row\">");
        builder.append("        <div class=\"col text-center\">");
        builder.append("            <p><span class=\"font-weight-bold text-primary\">")
            .append(clientId)
            .append("</span> wants to access your account <span class=\"font-weight-bold\">")
            .append(principal.getName()).append("</span></p>");
        builder.append("        </div>");
        builder.append("    </div>");
        builder.append("    <div class=\"row pb-3\">");
        builder.append("        <div class=\"col text-center\">");
        builder.append(
            "            <p>The following permissions are requested by the above app.<br/>Please review these and consent if you approve.</p>");
        builder.append("        </div>");
        builder.append("    </div>");
        builder.append("    <div class=\"row\">");
        builder.append("        <div class=\"col text-center\">");
        builder.append("            <form name=\"consent_form\" method=\"post\" action=\"")
            .append(request.getHeader("Location")).append("\">");
        builder.append("                <input type=\"hidden\" name=\"client_id\" value=\"")
            .append(clientId).append("\">");
        builder.append("                <input type=\"hidden\" name=\"state\" value=\"")
            .append(state).append("\">");

        for (String scope : scopesToAuthorize) {
            builder.append("                <div class=\"form-group form-check py-1\">");
            builder.append(
                "                    <input class=\"form-check-input\" type=\"checkbox\" name=\"scope\" value=\"")
                .append(scope).append("\" id=\"").append(scope).append("\">");
            builder.append("                    <label class=\"form-check-label\" for=\"")
                .append(scope).append("\">").append(scope).append("</label>");
            builder.append("                </div>");
        }

        if (!scopesPreviouslyAuthorized.isEmpty()) {
            builder.append(
                "                <p>You have already granted the following permissions to the above app:</p>");
            for (String scope : scopesPreviouslyAuthorized) {
                builder.append("                <div class=\"form-group form-check py-1\">");
                builder.append(
                    "                    <input class=\"form-check-input\" type=\"checkbox\" name=\"scope\" id=\"")
                    .append(scope).append("\" checked disabled>");
                builder.append("                    <label class=\"form-check-label\" for=\"")
                    .append(scope).append("\">").append(scope).append("</label>");
                builder.append("                </div>");
            }
        }

        builder.append("                <div class=\"form-group pt-3\">");
        builder.append(
            "                    <button class=\"btn btn-primary btn-lg\" type=\"submit\" id=\"submit-consent\">Submit Consent</button>");
        builder.append("                </div>");
        builder.append("                <div class=\"form-group\">");
        builder.append(
            "                    <button class=\"btn btn-link regular\" type=\"button\" onclick=\"cancelConsent();\" id=\"cancel-consent\">Cancel</button>");
        builder.append("                </div>");
        builder.append("            </form>");
        builder.append("        </div>");
        builder.append("    </div>");
        builder.append("    <div class=\"row pt-4\">");
        builder.append("        <div class=\"col text-center\">");
        builder.append(
            "            <p><small>Your consent to provide access is required.<br/>If you do not approve, click Cancel, in which case no information will be shared with the app.</small></p>");
        builder.append("        </div>");
        builder.append("    </div>");
        builder.append("</div>");
        builder.append("</body>");
        builder.append("</html>");

        return builder.toString();
    }
}
