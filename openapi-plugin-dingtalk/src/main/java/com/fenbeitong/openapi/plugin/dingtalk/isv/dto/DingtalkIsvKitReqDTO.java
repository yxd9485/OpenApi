package com.fenbeitong.openapi.plugin.dingtalk.isv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: xiaohai
 * @Date: 2021/10/13 上午10:26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DingtalkIsvKitReqDTO {

    private String reqData;

    /**
     * 三方人员id
     */
    private String userId;

    /**
     * 三方企业id
     */
    private String corpId;

    private String companyId;

    private String bizAlias;

    private List<IFormFieldDTO> bizDataList;

    private Map<String,IFormFieldDTO> bizDataMap;

}
