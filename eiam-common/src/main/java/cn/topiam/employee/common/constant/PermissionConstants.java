package cn.topiam.employee.common.constant;

import static cn.topiam.employee.support.constant.EiamConstants.COLON;
import static cn.topiam.employee.support.constant.EiamConstants.V1_API_PATH;

/**
 *
 * @author SanLi
 * Created by qinggang.zuo@gmail.com / 2689170096@qq.com on  2023/9/18 14:35
 */
public class PermissionConstants {

    /**
     * 权限管理API路径
     */
    public final static String PERMISSION_PATH              = V1_API_PATH + "/permission";

    /**
     * 组名称
     */
    public static final String PERMISSION_GROUP_NAME        = "应用管理";

    /**
     * 权限管理配置缓存前缀
     */
    public static final String PERMISSION_CACHE_NAME_PREFIX = "permission" + COLON;
}
