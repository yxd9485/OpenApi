package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Description
 * @Author duhui
 * @Date  2021/12/17
**/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FxiaokeOrgConfigDTO {
    String topId;
    String companyId;
}
