package com.fenbeitong.openapi.plugin.func.common;

/**
 * module: 应用模块名称<br/>
 * <p>
 * description: 描述<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/7/8 20:33
 * @since 2.0
 */
public interface FuncValidService {

    /**
     * 校验字符串长度
     * @param str
     * @param name
     */
    void lengthValid(String str,String name);
}
