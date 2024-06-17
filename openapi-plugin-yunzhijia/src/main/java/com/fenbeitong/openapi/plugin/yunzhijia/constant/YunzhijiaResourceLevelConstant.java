package com.fenbeitong.openapi.plugin.yunzhijia.constant;

/**
 * 云之家不同资源授权级别具有差异
 * 详细信息参照
 * https://open.yunzhijia.com/openplatform/resourceCenter/doc#/gitbook-wiki/server-api/accessToken.html
 * 不同资源级别业务接口获取企业access_token方式不同
 * 授权级别scope
 */
public interface YunzhijiaResourceLevelConstant {

    String APP = "app";
    String TEAM = "team";
    String RES_GROUP_SECRET = "resGroupSecret";
}
