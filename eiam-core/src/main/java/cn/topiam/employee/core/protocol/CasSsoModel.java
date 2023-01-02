package cn.topiam.employee.core.protocol;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2023/1/2 11:50
 */
@Data
@Builder
public class CasSsoModel implements Serializable {

    private String ssoCallbackUrl;

}
