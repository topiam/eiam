package cn.topiam.employee.application;

import cn.topiam.employee.core.protocol.CasSsoModel;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2023/1/2 11:50
 */
public interface CasApplicationService extends ApplicationService {

    /**
     * 获取SSO Modal
     *
     * @param appId {@link String}
     * @return {@link CasSsoModel}
     */
    CasSsoModel getSsoModel(Long appId);
}
