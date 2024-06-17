package com.fenbeitong.openapi.plugin.definition.process;

import com.fenbeitong.openapi.plugin.definition.dto.auto.CorpAutoCheckDTO;
import com.fenbeitong.openapi.plugin.definition.dto.auto.CorpAutoOrgEmpDTO;

import java.util.List;

public interface ICheckProcess {

    List<CorpAutoOrgEmpDTO>  check(CorpAutoCheckDTO corpAutoCheckDTO);
}
