package com.fenbeitong.openapi.plugin.feishu.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: xiaohai
 * @Date: 2022/7/5 下午2:37
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeishuApplyReqDTO {

    private String reqObj;
    private String applyType;
    private Integer processType;
    private String thirdEmployeeId;
    private String applyId;
    private String companyId;

}
