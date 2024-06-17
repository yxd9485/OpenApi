package com.fenbeitong.openapi.plugin.func.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Title: FuncOrderCallBack</p>
 * <p>Description: 订单回调统一拦截</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/1 7:34 PM
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FuncOrderCallBack {

    /**
     * 公司id所在的位置
     */
    String companyId() default "";

    /**
     * 场景类型
     */
    int type() default -1;

    /**
     * 版本号
     *
     * @return
     */
    String version() default "2.0";


    /**
     * orderId 所在的位置
     */
    String orderId() default "orderId";

    /**
     * status 所在的位置
     */
    String status() default "";

    /**
     * 接收的状态列表
     *
     * @return
     */
    int[] statusList() default {};

    /**
     * 逻辑主键
     *
     * @return
     */
    String[] logicKeys() default {};

    /**
     * 表达式 用于判断是否要存盘校验
     *
     * @return
     */
    String ignoreExpress() default "";

    /**
     * 回调类型 默认是1 表示订单 兼容老数据
     *
     * @see com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType
     */
    int callbackType() default 1;

}
