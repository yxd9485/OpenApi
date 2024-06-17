package com.fenbeitong.openapi.plugin.func.company.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: CostAttributionDTO</p>
 * <p>Description: 费用归属对象</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/13 4:19 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CostAttributionDTO {

    private String id;

    private String category;

    private String name;
}
