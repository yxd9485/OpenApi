package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import lombok.Data;

/**
 * <p>Title: FxkGetCustomDataReqDTO</p>
 * <p>Description: 纷享销客获取自定义对象数据详情请求参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/25 7:44 PM
 */
@Data
public class FxkGetCustomDataReqDTO {

    /**
     * 企业应用访问公司合法性凭证
     */
    private String corpAccessToken;

    /**
     * 开放平台公司账号
     */
    private String corpId;

    /**
     * 当前操作人的openUserId
     */
    private String currentOpenUserId;

    /**
     * 过滤条件
     */
    private FxkGetCustomDataCondition data;

    @Data
    public static class FxkGetCustomDataCondition{

        /**
         * 对象的api_name
         */
        private String dataObjectApiName;

        /**
         * 数据Id
         */
        private String objectDataId;
    }
}
