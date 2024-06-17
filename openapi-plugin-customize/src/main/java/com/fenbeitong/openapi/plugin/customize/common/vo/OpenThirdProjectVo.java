package com.fenbeitong.openapi.plugin.customize.common.vo;

import com.fenbeitong.openapi.plugin.support.project.dto.SupportUcThirdProjectReqDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenThirdProjectVo {

    Integer pageNumber;
    Integer pageCount;
    Integer totalCount;
    List<SupportUcThirdProjectReqDTO> addThirdProjectReqDTO;


}
