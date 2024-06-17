package com.fenbeitong.openapi.plugin.func.organization.dto;

import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import lombok.Data;

import java.util.List;

/**
 * Created by lizhen on 2020/10/28.
 */
@Data
public class OpenThirdOrgUnitReqDTO {
    private List<OpenThirdOrgUnitDTO> departmentList;
}
