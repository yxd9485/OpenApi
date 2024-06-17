package com.fenbeitong.openapi.plugin.definition.process;

import com.fenbeitong.openapi.plugin.definition.dto.auto.CorpAutoCheckDTO;
import com.fenbeitong.openapi.plugin.definition.dto.auto.CorpAutoOrgEmpDTO;

import java.util.List;

public abstract class AbstrackCheckProcess  implements ICheckProcess{
    @Override
    public List<CorpAutoOrgEmpDTO> check(CorpAutoCheckDTO corpAutoCheckDTO) {
        return null;
    }
}
