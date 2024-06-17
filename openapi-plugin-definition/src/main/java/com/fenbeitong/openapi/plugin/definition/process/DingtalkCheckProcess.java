package com.fenbeitong.openapi.plugin.definition.process;

import com.fenbeitong.openapi.plugin.definition.dto.auto.CorpAutoCheckDTO;
import com.fenbeitong.openapi.plugin.definition.dto.auto.CorpAutoOrgEmpDTO;
import com.fenbeitong.openapi.plugin.definition.service.auto.AutoDingtalkCorpCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DingtalkCheckProcess extends AbstrackCheckProcess{
    @Autowired
    AutoDingtalkCorpCheckService autoDingtalkCorpCheckService;
    public List<CorpAutoOrgEmpDTO> check(CorpAutoCheckDTO corpAutoCheckDTO){
       return autoDingtalkCorpCheckService.check(corpAutoCheckDTO);
    }


}
