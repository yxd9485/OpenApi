package com.fenbeitong.openapi.plugin.func.company.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: BillConfig51TalkSyncReqDTO</p>
 * <p>Description: 51talk账单同步配置参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/9/24 4:58 PM
 */
@Data
public class BillConfig51TalkSyncReqDTO {

    @JsonProperty("coe_ps_ebs_comp_relations")
    private List<CoePsEbsCompRelationsDTO> coePsEbsCompRelations;

    @JsonProperty("coe_gl_cost_center_rel")
    private List<CoeGlCostCenterRelDTO> coeGlCostCenterRel;

    @JsonProperty("coe_cost_items")
    private List<CoeCostItemsDTO> coeCostItems;
}
