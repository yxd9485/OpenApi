package com.fenbeitong.openapi.plugin.dingtalk.eia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * <p>Title: GrantOverTimeVoucherReq</p>
 * <p>Description: 根据规则生成</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/4 9:21 PM
 */
@Data
public class GrantOverTimeVoucherReq {

    @ApiModelProperty("分贝公司id")
    @NotBlank(message = "[company_id]不可为空")
    @JsonProperty("company_id")
    private String companyId;

    @JsonProperty("rule_id_list")
    private List<Long> ruleIdList;
}
