package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * <p>Title: FxkGetCustomDataListReqDTO</p>
 * <p>Description: 纷享销客获取自定义对象数据列表相应信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/25 7:44 PM
 */
@Data
public class FxkGetCustomDataListRespDTO {

    /**
     * 返回码
     */
    private Integer errorCode;

    /**
     * 对返回码的文本描述内容
     */
    private String errorMessage;

    /**
     * 查询结果
     */
    private FxkGetCustomDataListData data;

    @Data
    public static class FxkGetCustomDataListData {

        /**
         * 总记录数
         */
        private Integer total;

        /**
         * 数据列表
         */
        private List<Map> dataList;

        private Integer offset;

        private Integer limit;
    }
}
