package com.fenbeitong.openapi.plugin.func.sso.service;

/**
 * <p>Title: IFbtWebSsoService</p>
 * <p>Description: 玉符单点登录</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/26 7:42 PM
 */
public interface IYuFuSsoService {

    /**
     * 企业web单点登录
     *
     * @param companyId 公司id
     * @param token
     * @return 响应信息
     * @throws Exception
     */
    Object loginWeb(String companyId, String token);

    /**
     * 登录webapp
     *
     * @param companyId
     * @param token
     * @return
     */
    Object loginWebapp(String companyId, String token);

    /**
     * 获取登录信息
     *
     * @param id 唯一id
     * @return 用户登录id
     */
    Object getWebLoginInfo(String id);

}
