//package com.fenbeitong.openapi.plugin.yunzhijia.controller;
//
//import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
//import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseUtils;
//import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaResponse;
//import com.fenbeitong.openapi.plugin.yunzhijia.dto.ticket.YunzhijiaTicketDto;
//import com.fenbeitong.openapi.plugin.yunzhijia.dto.ticket.YunzhijiaTicketSelfReqDto;
//import com.fenbeitong.openapi.plugin.yunzhijia.dto.ticket.YunzhijiaUserReqDto;
//import com.fenbeitong.openapi.plugin.yunzhijia.exception.YunzhijiaErrorCode;
//import com.fenbeitong.openapi.plugin.yunzhijia.exception.YunzhijiaException;
//import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaEmployeeService;
//import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.corp.YunzhijiaCorpServiceImpl;
//import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.ticket.YunzhijiaTicketServiceImpl;
//import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
///**
// * @Auther zhang.peng
// * @Date 2021/4/26
// */
//@Controller
//@RequestMapping("/yunzhijia/ticket")
//@Slf4j
//public class YunzhijiaTicketController {
//
//    @Autowired
//    private YunzhijiaTicketServiceImpl yunzhijiaTicketService;
//
//    @Autowired
//    IYunzhijiaEmployeeService yunzhijiaEmployeeServiceImpl;
//
//    @Autowired
//    YunzhijiaCorpServiceImpl yunzhijiaCorpService;
//
//    @RequestMapping("/analysis")
//    @ResponseBody
//    public Object analysisTicket(@RequestBody YunzhijiaTicketSelfReqDto reqDto){
//        if ( null == reqDto ){
//            throw new YunzhijiaException(YunzhijiaErrorCode.PARAM_ERROR,"参数为空");
//        }
//        YunzhijiaResponse<YunzhijiaTicketDto> ticketDtoYunzhijiaResponse = yunzhijiaTicketService.analysisTicket(reqDto);
//        if ( null == ticketDtoYunzhijiaResponse ){
//            throw new YunzhijiaException(YunzhijiaErrorCode.EMPTY_RESULT,"云之家返回结果为空");
//        }
//        if ( !ticketDtoYunzhijiaResponse.isSuccess() ){
//            throw new YunzhijiaException(ticketDtoYunzhijiaResponse.getErrorCode(),ticketDtoYunzhijiaResponse.getError());
//        }
//        YunzhijiaTicketDto ticketDto = ticketDtoYunzhijiaResponse.getData();
//        if ( null == ticketDto ){
//            throw new YunzhijiaException(YunzhijiaErrorCode.EMPTY_RESULT,"云之家返回 data 结果为空");
//        }
//        log.info(" 解析云之家 ticket 信息后结果 ：{} ",ticketDto.toString());
//        PluginCorpDefinition pluginCorpDefinition = yunzhijiaCorpService.getByCorpId(ticketDto.getEid());
//        YunzhijiaUserReqDto userReqDto = YunzhijiaUserReqDto.builder()
//                .userId(ticketDto.getOpenid())
//                .companyId( null == pluginCorpDefinition ? "" : pluginCorpDefinition.getAppId())
//                .build();
//        LoginResVO loginResVO = yunzhijiaEmployeeServiceImpl.getUserInfoByCorIdAndThirdUserId(userReqDto);
//        if ( null == loginResVO ){
//            return YunzhijiaResponseUtils.error(YunzhijiaErrorCode.UC_EMPTY_RESULT,"UC返回结果为空");
//        }
//        return YunzhijiaResponseUtils.success(loginResVO);
//    }
//
//}
