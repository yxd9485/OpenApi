package com.fenbeitong.openapi.plugin.yiduijie.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * <p>Title: YiDuiJieUpsertMappingResp</p>
 * <p>Description: 易对接映射响应信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/27 4:21 PM
 */
@Data
public class YiDuiJieUpsertMappingResp {

    private YiDuiJieAddMappingRespBody body;

    private String message;

    private Integer status;

    public boolean success() {
        return status != null && status == 0;
    }

    @JsonIgnore
    public String getId() {
        return body == null ? null : body.getId();
    }

    @Data
    public static class YiDuiJieAddMappingRespBody {

        private String id;

        private String srcName;

        private String destName;

        private String extValue1;

        private String extValue2;

        private String extValue3;

        private String extValue4;

        private String extValue5;

        private String mappingType;
    }
}
