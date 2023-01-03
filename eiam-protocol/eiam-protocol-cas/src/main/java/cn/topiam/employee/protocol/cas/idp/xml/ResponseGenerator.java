package cn.topiam.employee.protocol.cas.idp.xml;

import java.io.IOException;
import java.util.Map;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2023/1/2 21:22
 */
public interface ResponseGenerator {

    void genFailedMessage(String serviceTicketId);

    void genSucceedMessage(String casUser, Map<String, Object> attributes);

    void sendMessage() throws IOException;
}
