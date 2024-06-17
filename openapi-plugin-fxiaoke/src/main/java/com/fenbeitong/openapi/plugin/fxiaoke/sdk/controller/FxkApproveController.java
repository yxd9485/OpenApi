package com.fenbeitong.openapi.plugin.fxiaoke.sdk.controller;

import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.FxkResponseUtils;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxkGetApprovalInstanceListReqDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxkGetApprovalInstanceListRespDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxkGetCorpAccessTokenRespDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxkGetCorpAccessTokenReqDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkAccessTokenService;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkApprovalInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/fxiaoke/approve")
public class FxkApproveController {
    @Autowired
    IFxkAccessTokenService fxkAccessTokenService;
    @Autowired
    IFxkApprovalInstanceService fxkApprovalInstanceService;

    @RequestMapping("/list")
    @ResponseBody
    public Object getFxiaokeApplyList(String corpId, String apiName, String state,long startTime,long endTime,String currentOpenUerId) {
        String appId = "FSAID_1318f50";
        corpId = "687889_sandbox";
        //appseret
        String appSecret = "ce8420ba39744a3485b4285eeb804ae3";
        //永久授权码，公司级别调用时使用 app级别调用无需使用
        String permantCode = "2C22DC604C7B230AAA36DD5973E87832";

        //对象的apiname,
//         apiName = "object_012DL__c";
        //审批的apiname
//        String applyApiName="apprZ69SQLPWPS__crmappr";
        FxkGetCorpAccessTokenReqDTO build = FxkGetCorpAccessTokenReqDTO.builder()
                .appId(appId)
                .appSecret(appSecret)
                .permanentCode(permantCode)
                .build();
        FxkGetCorpAccessTokenRespDTO corpAccessToken = fxkAccessTokenService.getCorpAccessToken(build);
        Integer errorCode = corpAccessToken.getErrorCode();
        if (errorCode == 0) {//成功
            String corpAccessToken1 = corpAccessToken.getCorpAccessToken();
//            long startTime=1585187408000L;
//            long endTime=1595728208000L;
//            String currentOpenUerId="FSUID_3FB8F57BF0D172F3451B4CADB87ACA54";
            FxkGetApprovalInstanceListReqDTO build1 = FxkGetApprovalInstanceListReqDTO.builder()
                    .corpId(corpAccessToken.getCorpId())
                    .corpAccessToken(corpAccessToken1)
                    .currentOpenUserId(currentOpenUerId)
                    .flowApiName(apiName)
                    .startTime(startTime)
                    .endTime(endTime)
                    .pageNumber(1)
                    .pageSize(200)
                    .state(state)
                    .build();
            //查询审批列表，企业级的审批数据
            FxkGetApprovalInstanceListRespDTO instanceList = fxkApprovalInstanceService.getInstanceList(build1);
            return FxkResponseUtils.success(instanceList);
        }

        return FxkResponseUtils.error(corpAccessToken.getErrorCode(), corpAccessToken.getErrorMessage());
    }

    @RequestMapping("/detail")
    @ResponseBody
    public Object getFxiaokeApplyDetail(String corpId, String formCodeId, String formInsId) {
        return null;
    }
}
