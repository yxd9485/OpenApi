package com.fenbeitong.openapi.plugin.definition.controller.enforcement;

import com.fenbeitong.openapi.plugin.definition.dto.DefinitionResultDTO;
import com.fenbeitong.openapi.plugin.definition.dto.plugin.message.CreateMsgRecipientDefinitionReqDTO;
import com.fenbeitong.openapi.plugin.definition.service.MsgRecipientDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 企业插件集成消息推送配置
 * Created by log.chang on 2019/12/25.
 */
@Controller
@RequestMapping("/definitions/plugin/message")
public class MsgRecipientDefinitionController {

    @Autowired
    private MsgRecipientDefinitionService msgRecipientDefinitionService;

    @PostMapping
    @ResponseBody
    public Object createMsgRecipientDefinition(@RequestBody CreateMsgRecipientDefinitionReqDTO req) {
        return DefinitionResultDTO.success(msgRecipientDefinitionService.createMsgRecipientDefinition(req.getThirdCorpId(),
                req.getThirdAgentId(), req.getThirdUserId(), req.getThirdUserName()));
    }


}
