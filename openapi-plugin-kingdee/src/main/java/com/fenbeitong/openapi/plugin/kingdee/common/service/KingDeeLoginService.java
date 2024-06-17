package com.fenbeitong.openapi.plugin.kingdee.common.service;

import com.fenbeitong.openapi.plugin.kingdee.support.dao.OpenKingdeeUrlConfigDao;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenKingdeeUrlConfig;
import org.springframework.util.MultiValueMap;

/**
 * <p>Title: AbstactLogin</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/6/22 11:33 上午
 */
public interface KingDeeLoginService {
    /**
     * @param openKingdeeUrlConfig
     * @param baseUrl   免登或者密码登录url
     * @Description 免登获取token
     * @Author duhui
     * @Date 2021/7/27
     **/
    String gettoken(OpenKingdeeUrlConfig openKingdeeUrlConfig, String baseUrl);

    /**
     * @param url           请求路径
     * @param multiValueMap 请求数据
     * @Description 密码获取token
     * @Author duhui
     * @Date 2021/7/27
     **/
    String gettoken(String url, MultiValueMap multiValueMap);
}
