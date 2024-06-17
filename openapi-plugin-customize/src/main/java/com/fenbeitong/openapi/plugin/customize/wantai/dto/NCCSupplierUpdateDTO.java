package com.fenbeitong.openapi.plugin.customize.wantai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


/**
 *
 * @author zhangjindong
 * @date 2022/9/21 8:21 PM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NCCSupplierUpdateDTO {

  private String companyId;

  private List<String> ids;
}
