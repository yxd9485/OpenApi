package com.fenbeitong.openapi.plugin.yiduijie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.yiduijie.model.config.ConfigDTO;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: YiDuiJieListConfigResp</p>
 * <p>Description: 查询易对接配置信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/16 11:02 AM
 */
@Data
public class YiDuiJieListConfigResp {

    @JsonProperty("body")
    private List<ConfigDTO> configList;

    private String message;

    private Integer status;

    public boolean success() {
        return status != null && status == 0;
    }
}
