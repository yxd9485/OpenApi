package com.fenbeitong.openapi.plugin.yiduijie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: YiDuiJieListAppInstanceResp</p>
 * <p>Description: 易对接查询应用实例响应</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 5:34 PM
 */
@Data
public class YiDuiJieListAppInstanceResp {

    @JsonProperty("body")
    private List<YiDuiJieAppInstance> appInstanceList;

    private String message;

    private Integer status;

    public boolean success() {
        return status != null && status == 0;
    }
}
