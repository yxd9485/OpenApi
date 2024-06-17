package com.fenbeitong.openapi.plugin.yiduijie.common;


/**
 * <p>Title: YiDuiJieApiResponseCode</p>
 * <p>Description: 易对接模块响应码</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/03/10 15:08 PM
 */
public interface YiDuiJieApiResponseCode {

    /**
     * msg.140001=获取易对接token失败
     */
    String GET_TOKEN_ERROR = "140001";

    /**
     * msg.140002=创建易对接应用失败
     */
    String CREATE_APP_ERROR = "140002";

    /**
     * msg.140003=科目映射失败
     */
    String MAPPING_ACCOUNT_ERROR = "140003";

    /**
     * msg.140004=部门映射失败
     */
    String MAPPING_DEPT_ERROR = "140004";

    /**
     * msg.140005=同步科目失败
     */
    String UPSERT_ACCOUNT_ERROR = "140005";

    /**
     * msg.140006=同步记账部门失败
     */
    String UPSERT_DEPT_ERROR = "140006";

    /**
     * msg.140007=获取科目列表失败
     */
    String LIST_ACCOUNT_ERROR = "140007";

    /**
     * msg.140008=获取记账部门列表失败
     */
    String LIST_DEPT_ERROR = "140008";

    /**
     * msg.140009=获映射列表列表失败
     */
    String LIST_MAPPING_ERROR = "140009";

    /**
     * msg.140010=修改凭证管理通用配置失败
     */
    String MODIFY_VOUCHER_CONFIG_ERROR = "140010";

    /**
     * msg.140011=查询配置信息失败
     */
    String LIST_CONFIG_ERROR = "140011";

    /**
     * msg.140012=修改其他配置失败
     */
    String MODIFY_EXT_CONFIG_ERROR = "140012";

    /**
     * msg.140013=查询其他配置信息失败
     */
    String LIST_EXT_CONFIG_ERROR = "140013";

    /**
     * msg.140014=申请单创建生成凭证消息失败
     */
    String SEND_CREATE_VOUCHER_MSG_ERROR = "140014";

    /**
     * msg.140015=申请单预览生成凭证消息失败
     */
    String APPLY_PREVIEW_CREATE_VOUCHER_MSG_ERROR = "140015";

    /**
     * msg.140016=修改进项税(贷方)科目映射配置失败
     */
    String MODIFY_ACCOUNT_CONFIG_ERROR = "140016";

    /**
     * msg.140017=修改科目映射失败
     */
    String MAPPING_ACCOUNT_UPDATE_ERROR = "140017";

    /**
     * msg.140018=删除科目映射失败
     */
    String MAPPING_ACCOUNT_DELETE_ERROR = "140018";

    /**
     * msg.140019=修改部门映射失败
     */
    String MAPPING_DEPT_UPDATE_ERROR = "140019";

    /**
     * msg.140020=删除部门映射失败
     */
    String MAPPING_DEPT_DELETE_ERROR = "140020";

    /**
     * msg.140021=修改配置失败
     */
    String MODIFY_CONFIG_ERROR = "140021";

    /**
     * msg.140022=增加项目映射失败
     */
    String MAPPING_PROJECT_ADD_ERROR = "140022";

    /**
     * msg.140023=修改项目映射失败
     */
    String MAPPING_PROJECT_UPDATE_ERROR = "140023";

    /**
     * msg.140024=删除项目映射失败
     */
    String MAPPING_PROJECT_DELETE_ERROR = "140024";

}
