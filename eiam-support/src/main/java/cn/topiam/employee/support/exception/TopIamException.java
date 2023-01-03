/*
 * eiam-support - Employee Identity and Access Management Program
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
package cn.topiam.employee.support.exception;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.http.HttpStatus;

/**
 * TopIamException
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/9/25 03:40
 */
public class TopIamException extends RuntimeException {

    public static final String     DEFAULT_EXCEPTION              = "unknown_exception";
    /**
     * 违反约束异常
     */
    public static final String     CONSTRAINT_VIOLATION_EXCEPTION = "constraint_violation_exception";
    /**
     * 绑定异常
     */
    public static final String     BIND_EXCEPTION                 = "bind_exception";

    public static final HttpStatus DEFAULT_STATUS                 = HttpStatus.INTERNAL_SERVER_ERROR;

    public static final String     ERROR                          = "error";

    public static final String     DESCRIPTION                    = "error_description";

    public static final String     STATUS                         = "status";

    private Map<String, String>    additionalInformation          = null;

    private final HttpStatus       status;

    private final String           error;

    public TopIamException(String msg, Throwable t) {
        super(msg, t);
        this.error = DEFAULT_EXCEPTION;
        this.status = DEFAULT_STATUS;
    }

    public TopIamException(String msg) {
        this(DEFAULT_EXCEPTION, msg, DEFAULT_STATUS);
    }

    public TopIamException(String msg, HttpStatus status) {
        this(DEFAULT_EXCEPTION, msg, status);
    }

    public TopIamException(String error, String description) {
        super(description);
        this.error = error;
        this.status = DEFAULT_STATUS;
    }

    public TopIamException(String error, String description, HttpStatus status) {
        super(description);
        this.error = error;
        this.status = status;
    }

    public TopIamException(Throwable cause, String error, String description, HttpStatus status) {
        super(description, cause);
        this.error = error;
        this.status = status;
    }

    /**
     * The error code.
     *
     * @return The error code.
     */
    public String getErrorCode() {
        return error;
    }

    /**
     * The HTTP status associated with this error.
     *
     * @return The HTTP status associated with this error.
     */
    public HttpStatus getHttpStatus() {
        return status;
    }

    /**
     * Get any additional information associated with this error.
     *
     * @return Additional information, or null if none.
     */
    public Map<String, String> getAdditionalInformation() {
        return this.additionalInformation;
    }

    /**
     * Add some additional information with this OAuth error.
     *
     * @param key The key.
     * @param value The value.
     */
    public void addAdditionalInformation(String key, String value) {
        if (this.additionalInformation == null) {
            this.additionalInformation = new TreeMap<>();
        }
        this.additionalInformation.put(key, value);

    }

    /**
     * Creates an {@link TopIamException} from a {@link Map}.
     *
     * @param errorParams a map with additional error information
     * @return the exception with error information
     */
    public static TopIamException valueOf(Map<String, String> errorParams) {
        String errorCode = errorParams.get(ERROR);
        String errorMessage = errorParams.getOrDefault(DESCRIPTION, null);
        HttpStatus status = DEFAULT_STATUS;
        if (errorParams.containsKey(STATUS)) {
            try {
                status = HttpStatus.valueOf(Integer.parseInt(errorParams.get(STATUS)));
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        TopIamException ex = new TopIamException(errorCode, errorMessage, status);
        Set<Map.Entry<String, String>> entries = errorParams.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            if (!ERROR.equals(key) && !DESCRIPTION.equals(key)) {
                ex.addAdditionalInformation(key, entry.getValue());
            }
        }

        return ex;
    }

    @Override
    public String toString() {
        return getSummary();
    }

    /**
     * @return a comma-delimited list of details (key=value pairs)
     */
    public String getSummary() {

        StringBuilder builder = new StringBuilder();

        String delim = "";

        String error = this.getErrorCode();
        if (error != null) {
            builder.append(delim).append("error=\"").append(error).append("\"");
            delim = ", ";
        }

        String errorMessage = this.getMessage();
        if (errorMessage != null) {
            builder.append(delim).append("error_description=\"").append(errorMessage).append("\"");
            delim = ", ";
        }

        Map<String, String> additionalParams = this.getAdditionalInformation();
        if (additionalParams != null) {
            for (Map.Entry<String, String> param : additionalParams.entrySet()) {
                builder.append(delim).append(param.getKey()).append("=\"").append(param.getValue())
                    .append("\"");
                delim = ", ";
            }
        }

        return builder.toString();

    }
}
