package com.fenbeitong.openapi.plugin.definition.controller.enforcement;

import com.fenbeitong.openapi.plugin.definition.dto.DefinitionResultDTO;
import com.fenbeitong.openapi.plugin.definition.dto.plugin.corp.CreatePluginCorpDefinitionReqDTO;
import com.fenbeitong.openapi.plugin.definition.service.PluginCorpDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 插件集成三方企业配置
 * Created by log.chang on 2019/12/23.
 */
@Controller
@RequestMapping("/definitions/plugin/corp")
public class PluginCorpDefinitionController {

    @Autowired
    private PluginCorpDefinitionService pluginCorpDefinitionService;

    @PostMapping
    @ResponseBody
    public Object createPluginCorp(@RequestBody CreatePluginCorpDefinitionReqDTO req){
        return DefinitionResultDTO.success(pluginCorpDefinitionService.createPluginCorp(req));
    }

}
