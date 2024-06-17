package com.fenbeitong.openapi.plugin.kingdee.common.listener;


/**
 * @Description 金蝶监听类
 * @Author duhui
 * @Date 2020-12-01
 **/
public interface KingDeekListener {

    /**
     * 保存
     */
    String saveParse(String data,String companyId, Object... objects);

    /**
     * 提交
     */
    String commitParse(String data, String number);

    /**
     * 审核
     */
    String auditParse(String data, String number);

    /**
     * 请求参数封装
     */
    void setList(String dataKey, String dataValue, String companyId, StringBuffer strData, Object... objects);

    /**
     * 请求参数封装
     */
    void setMap(String dataKey, String dataValue, String companyId, StringBuffer strData, Object... objects);

}
