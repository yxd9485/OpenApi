package com.fenbeitong.openapi.plugin.func.deprecated.valid;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * module: 应用模块名称<br/>
 * <p>
 * description: 描述<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/7/7 11:48
 * @since 1.0
 */
public interface ValidService {

    /**
     * 校验入参
     * @param obj
     */
    void checkRequest(Object obj);

    /**
     * 校验数据类型
     * @param obj
     * @return
     */
    Integer checkParameterType(Object obj);

    /**
     * 数据类型校验
     * @param param
     * @param tag
     * @return
     */
    Integer parameterTypeValid(Object param, Integer tag);



    /**
     * HttpServletRequest Map<String,String[]> map 转换
     * @param request
     * @return
     */
    Map<String,Object> transformMap(HttpServletRequest request);

    /**
     * <p>校验json串中，是否含有指定KEY，并KEY值不为空
     * @param jsonString
     * @param checkKeys
     * @return
     */
    Boolean validate(String jsonString, List<String> checkKeys);


}
