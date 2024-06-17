package com.fenbeitong.openapi.plugin.yiduijie.sdk;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * <p>Title: YiDuijieRouter</p>
 * <p>Description: 易对接访问路径</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 4:30 PM
 */
@Component
public class YiDuiJieRouter {

    @Value("${yiduijie.url}")
    private String yiDuiJieUrl;

    /**
     * @return 获取token url
     */
    public String getTokenUrl() {
        return yiDuiJieUrl + "/api/v1/token";
    }


    /**
     * @return 创建帐号接口 url
     */
    public String getAddUserUrl() {
        return yiDuiJieUrl + "/api/v1/users";
    }

    /**
     * @return 修改子账号 url
     */
    public String getUpdateUserUrl() {
        return yiDuiJieUrl + "/api/v1/users/%s";
    }

    /**
     * @return 查询子账号 url
     */
    public String getQueryUserUrl() {
        return yiDuiJieUrl + "/api/v1/users/%s";
    }

    /**
     * @return 查询所有子账号 url
     */
    public String getListUserUrl() {
        return yiDuiJieUrl + "/api/v1/users";
    }

    /**
     * @return 创建新的客户端 url
     */
    public String getAddClientUrl() {
        return yiDuiJieUrl + "/api/v1/clients";
    }

    /**
     * @return 修改客户端 url
     */
    public String getUpdateClientUrl() {
        return yiDuiJieUrl + "/api/v1/clients/%s";
    }

    /**
     * @return 查询客户端 url
     */
    public String getQueryClientUrl() {
        return yiDuiJieUrl + "/api/v1/clients/%s";
    }

    /**
     * @return 查询所有客户端 url
     */
    public String getListClientUrl() {
        return yiDuiJieUrl + "/api/v1/clients";
    }

    /**
     * @return 查询应用市场所有应用 url
     */
    public String getListMarketAppUrl() {
        return yiDuiJieUrl + "/api/v1/market/apps/";
    }

    /**
     * @return 创建应用实例 url
     */
    public String getAddAppUrl() {
        return yiDuiJieUrl + "/api/v1/apps";
    }

    /**
     * @return 查询应用 url
     */
    public String getQueryAppUrl() {
        return yiDuiJieUrl + "/api/v1/apps/%s";
    }

    /**
     * @return 查询所有应用实例 url
     */
    public String getListAppUrl() {
        return yiDuiJieUrl + "/api/v1/apps";
    }

    /**
     * @return 创建/修改映射 url
     */
    public String getMappingUrl() {
        return yiDuiJieUrl + "/api/v1/apps/%s/mappings/%s";
    }

    /**
     * @return 删除映射 url
     */
    public String getDeleteMappingUrl() {
        return yiDuiJieUrl + "/api/v1/apps/%s/mappings/%s/clear";
    }

    /**
     * @return 查询映射信息 url
     */
    public String getListMappingUrl() {
        return yiDuiJieUrl + "/api/v1/apps/%s/mappings/%s?pageIndex=%d&pageSize=%d";
    }

    /**
     * @return 创建或者修改数据集信息 url
     */
    public String getAddDatasetUrl() {
        return yiDuiJieUrl + "/api/v1/apps/%s/datasets/%s";
    }

    /**
     * @return 查询数据集信息 url
     */
    public String getListDatasetUrl() {
        return yiDuiJieUrl + "/api/v1/apps/%s/datasets/%s";
    }

    /**
     * @return 创建或者修改配置 url
     */
    public String getSetConfigUrl() {
        return yiDuiJieUrl + "/api/v1/apps/%s/settings";
    }

    /**
     * @return 查询配置信息 url
     */
    public String getListConfigUrl() {
        return yiDuiJieUrl + "/api/v1/apps/%s/settings";
    }

    /**
     * @return 创建/修改其他配置项 url
     */
    public String getSetExtConfigUrl() {
        return yiDuiJieUrl + "/api/v1/apps/%s/settings/ext";
    }

    /**
     * @return 查询其他配置信息 url
     */
    public String getListExtConfigUrl() {
        return yiDuiJieUrl + "/api/v1/apps/%s/settings/ext";
    }

    /**
     * @return 创建消息 url
     */
    public String getSendMessageUrl() {
        return yiDuiJieUrl + "/api/v1/messages";
    }

    /**
     * @return 数据转换 url
     */
    public String getTransformUrl() {
        return yiDuiJieUrl + "/api/v1/apps/%s/transform";
    }
}
