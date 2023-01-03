package cn.topiam.employee.protocol.cas.idp.tickets;

import java.io.Serializable;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/29 16:25
 */
public interface Ticket extends Serializable {
    String getId();

    boolean isExpired();

    long getCreateTime();
}
