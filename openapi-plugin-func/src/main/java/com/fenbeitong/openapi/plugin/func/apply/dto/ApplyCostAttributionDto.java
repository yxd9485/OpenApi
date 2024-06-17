package com.fenbeitong.openapi.plugin.func.apply.dto;

import lombok.Data;

/**
 * <p>Title: ApplyCostAttributionDto</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/8/10 10:00 AM
 */
@Data
public class ApplyCostAttributionDto {

    private Integer cost_attribution_category;

    private String cost_attribution_id;

    private String cost_attribution_name;

    private String third_cost_attribution_id;
}
