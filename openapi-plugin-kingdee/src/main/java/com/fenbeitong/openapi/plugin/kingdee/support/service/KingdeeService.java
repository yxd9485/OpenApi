package com.fenbeitong.openapi.plugin.kingdee.support.service;

import com.fenbeitong.openapi.plugin.kingdee.support.dto.ResultVo;
import org.springframework.util.MultiValueMap;

/**
 * @Description: 金蝶接口服务
 * @date 2020/09/17 14:09
 */
public interface KingdeeService {


    /**
     * @param url      接口地址
     * @param postData 查询参数
     * @Description: 金蝶登录接
     * @date 2020/9/10 14:12
     */
    ResultVo login(String url, MultiValueMap postData);


    /**
     * @param url     接口地址
     * @param cookie  登录cookie
     * @param content 查询参数
     * @Description: 金蝶查询接口
     * @date 2020/9/10 17:21
     */
    String view(String url, String cookie, String content);

    /**
     * @param url     接口地址
     * @param cookie  登录cookie
     * @param content json格式参数
     * @Description: 保存接口
     * @date 2020/9/10 14:18
     */
    ResultVo save(String url, String cookie, String content);

    /**
     * @param url     接口地址
     * @param cookie  登录cookie
     * @param content json格式参数
     * @Description: 提交接口
     * @date 2020/10/14 14:18
     */
    ResultVo submit(String url, String cookie, String content);


    /**
     * @Description 保存
     * @Author duhui
     * @Date 2021/6/27
     **/
    String getNumberBySave(String url, String cookie, String content);


    /**
     * @Description 提交和审核
     * @Author duhui
     * @Date 2021/6/27
     **/
    boolean submitAndAudit(String url, String cookie, String content);

}
