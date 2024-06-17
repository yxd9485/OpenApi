package com.fenbeitong.openapi.plugin.definition.controller.enforcement;

import com.fenbeitong.openapi.plugin.definition.dto.DefinitionResultDTO;
import com.fenbeitong.openapi.plugin.definition.dto.plugin.apply.CreateThirdApplyDefinitionReqDTO;
import com.fenbeitong.openapi.plugin.definition.service.ThirdApplyDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 企业插件集成审批配置
 * Created by log.chang on 2019/12/25.
 */
@Controller
@RequestMapping("/definitions/plugin/apply")
public class ThirdApplyDefinitionController {

    @Autowired
    private ThirdApplyDefinitionService thirdApplyDefinitionService;

    @PostMapping
    @ResponseBody
    public Object createThirdApplyDefinition(@RequestBody CreateThirdApplyDefinitionReqDTO req) {
        return DefinitionResultDTO.success(thirdApplyDefinitionService.createThirdApplyDefinition(req.getThirdProcessCode(),
                req.getThirdProcessName(), req.getProcessType(), req.getAppId()));
    }


}
