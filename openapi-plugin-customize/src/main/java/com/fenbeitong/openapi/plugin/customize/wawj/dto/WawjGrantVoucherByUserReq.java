package com.fenbeitong.openapi.plugin.customize.wawj.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: WawjGrantVoucherByUserReq</p>
 * <p>Description: 发券指定人员参数信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/2/2 5:45 PM
 */
@Data
public class WawjGrantVoucherByUserReq {

    @JsonProperty("company_id")
    private String companyId;

    @JsonProperty("user_id_list")
    private List<String> userIdList;
}
