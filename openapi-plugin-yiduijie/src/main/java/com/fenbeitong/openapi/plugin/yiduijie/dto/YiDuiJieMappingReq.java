package com.fenbeitong.openapi.plugin.yiduijie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: YiDuiJieMappingReq</p>
 * <p>Description: 易对接映射参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/13 5:03 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YiDuiJieMappingReq {

    private String id;

    private String srcName;

    private String destName;

    private String extValue1;

    private String extValue2;

    private String extValue3;

    private String extValue4;

    private String extValue5;
}
