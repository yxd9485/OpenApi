package com.fenbeitong.openapi.plugin.landray.ekp.common;

import com.google.common.collect.Lists;

import java.util.List;

/**
 *
 * @author lizhen
 * @date 2020/12/8
 */
public interface LandaryEkpConstant {

    /**
     * 泛微根部门
     */
    public static final String ECOLOGY_ROOT_DEPARTMENT_ID = "0";

    /**
     * 用户有效状态。0试用；1正式；2临时；3试用延期；4解聘；5离职；6退休；7无效
     */
    public static List<String> USER_VALID_STATUS = Lists.newArrayList("0", "1", "2", "3");

    /**
     * 泛微部门、分部封存标识
     */
    public static String ORGANIZATION_CANCELED = "1";

    /**
     * 调用接口成功的状态
     * 返回状态值为0时，该值返回空。
     * 返回状态值为1时，该值返回错误信息。
     * 返回状态值为2时，该值返回所有组织架构的基本信息json数组
     */
    public static final int EKP_STATUS_SUCCESS = 2;
}
