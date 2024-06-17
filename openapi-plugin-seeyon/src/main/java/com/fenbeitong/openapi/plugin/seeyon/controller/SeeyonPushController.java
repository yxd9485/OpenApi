package com.fenbeitong.openapi.plugin.seeyon.controller;

import com.fenbeitong.openapi.plugin.seeyon.common.SeeyonResponseUtils;
import com.fenbeitong.openapi.plugin.seeyon.common.SeeyonResultEntity;
import com.fenbeitong.openapi.plugin.seeyon.constant.FbOrgEmpConstants;
import com.fenbeitong.openapi.plugin.seeyon.constant.OpenApiConstant;
import com.fenbeitong.openapi.plugin.seeyon.dto.OpenApiRestRequest;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonOrgNameReq;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonFbErrorOrgEmp;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonClient;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonFbOrgEmp;
import com.fenbeitong.openapi.plugin.seeyon.enums.HttpStatusCodeEnum;
import com.fenbeitong.openapi.plugin.seeyon.exceptions.SeeyonApiException;
import com.fenbeitong.openapi.plugin.seeyon.helper.BeanHelper;
import com.fenbeitong.openapi.plugin.seeyon.helper.DingTalkRobotMsgHelper;
import com.fenbeitong.openapi.plugin.seeyon.helper.HarmonyMailHelper;
import com.fenbeitong.openapi.plugin.seeyon.helper.Jsr310DateHelper;
import com.fenbeitong.openapi.plugin.seeyon.process.FbOrgEmpProcessFactory;
import com.fenbeitong.openapi.plugin.seeyon.process.IFbOrgEmpProcess;
import com.fenbeitong.openapi.plugin.seeyon.service.*;
import com.fenbeitong.openapi.plugin.seeyon.service.impl.*;
import com.fenbeitong.openapi.plugin.seeyon.utils.MailMsg;
import com.fenbeitong.openapi.plugin.seeyon.utils.TextMailMsg;
import com.fenbeitong.openapi.plugin.seeyon.utils.TextMsg;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportDeleteEmployeeReqDTO;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportUpdateEmployeeReqDTO;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/seeyon")
public class SeeyonPushController {

    @Autowired
    SeeyonAccessTokenService seeyonAccessTokenService;
    @Autowired
    SeeyonAccountService seeyonAccountService;
    @Autowired
    SeeyonClientService seeyonClientService;
    @Autowired
    SeeyonFbOrgEmpService seeyonFbOrgEmpService;
    @Autowired
    SeeyonEmpService seeyonEmpService;
    @Autowired
    SeeyonDepartmentService seeyonDepartmentService;
    @Autowired
    SeeyonPullEmpService seeyonPullEmpService;
    @Autowired
    SeeyonPullOrgService seeyonPullOrgService;
    @Autowired
    SeeyonMiddlewareService seeyonMiddlewareService;
    @Autowired
    FbOrgEmpProcessFactory fbOrgEmpProcessFactory;
    @Autowired
    FbErrorOrgEmpService fbErrorOrgEmpService;
    @Autowired
    RestHttpUtils restHttpUtils;
    @Autowired
    SeeyonEmailService seeyonEmailService;
    @Autowired
    SeeyonDingtalkNoticeService seeyonDingtalkNoticeService;


    @RequestMapping("/pushData")
    @ResponseBody
    public Object syncSeeyonMiddle2Fbt(@RequestParam("jobConfig") String jobConfig) {
        SeeyonOrgNameReq seeyonOrgNameReq = JsonUtils.toObj(jobConfig, SeeyonOrgNameReq.class);
        String orgName;
        Long compareDaysGap;
        if (!ObjectUtils.isEmpty(seeyonOrgNameReq)) {
            orgName = seeyonOrgNameReq.getOrgName();
            compareDaysGap = seeyonOrgNameReq.getCompareDaysGap();
        } else {
            throw new SeeyonApiException(HttpStatusCodeEnum.BAD_REQUEST);
        }

        Map<String, String> resultMap = new HashMap<>();
        SeeyonClient seeyonClient = seeyonClientService.getSeeyonClientByName(orgName);
        //查询出待推送到分贝通的数据 ，循环同步数据到分贝通
        List<SeeyonFbOrgEmp> fbOrgEmpList;
        if (compareDaysGap == 0) {
            fbOrgEmpList = seeyonFbOrgEmpService.getSeeyonFbOrgEmps(seeyonClient);
        } else {
            fbOrgEmpList = seeyonFbOrgEmpService.getSeeyonFbOrgEmpsDesc(seeyonClient);
        }
        //过滤同步数据，需要根据与客户约定的删除部门和人员数量，保证数量同步的安全性，避免删除数量过大导致的误操作问题
        fbOrgEmpList =  seeyonFbOrgEmpService.filterList(seeyonClient,fbOrgEmpList);
        List<SeeyonFbErrorOrgEmp> fbOrgEmpList1 = Lists.newArrayList();
        //TODO  循环同步数据到分贝通,看是否可以优化批量添加和更新和删除，现有逻辑是根据不同类型的数据调用不同的处理器,而且需要根据顺序执行
        fbOrgEmpList.stream().forEach(fbOrgEmp -> {
            IFbOrgEmpProcess fbOrgEmpProcessor = fbOrgEmpProcessFactory.getFbOrgEmpProcessor(fbOrgEmp.getSort());
            SeeyonFbErrorOrgEmp seeyonFbOrgEmp = fbOrgEmpProcessor.processFbOrgEmp(seeyonClient, fbOrgEmp);
            if (!ObjectUtils.isEmpty(seeyonFbOrgEmp)) {
                fbOrgEmpList1.add(seeyonFbOrgEmp);
            }
        });

        /* 错误消息*/
        StringBuilder contents = new StringBuilder();
        contents.append("OpenApi-Push-Total-Size: ").append(fbOrgEmpList.size()).append("; ");
        if (!ObjectUtils.isEmpty(fbOrgEmpList1)) {
            /* 错误记录 */
            fbOrgEmpList1.stream().forEach(fbOrgEmp -> {
                        //先存储数据到error表,然后推送错误数据到dingtalk
                        SeeyonFbErrorOrgEmp seeyonFbErrorOrgEmp = BeanHelper.beanToBean(fbOrgEmp, SeeyonFbErrorOrgEmp.class);
                        seeyonFbErrorOrgEmp.setCreateTime(LocalDateTime.now());
                        seeyonFbErrorOrgEmp.setExecuteResult(FbOrgEmpConstants.ERROR_DATA_EXE_RESULT_WAIT);
                        seeyonFbErrorOrgEmp.setResponseJsonData(JsonUtils.toJson(seeyonFbErrorOrgEmp.getResponseJsonData()));
                        fbErrorOrgEmpService.saveSeeyonFbErrorOrgEmp(seeyonFbErrorOrgEmp);
                        resultMap.put(seeyonFbErrorOrgEmp.getId(), "数据推送失败");
                        /*msg消息*/
                        contents.append("[Record Id: ").append(seeyonFbErrorOrgEmp.getId()).append("; ");
                        contents.append("Content: ").append(seeyonFbErrorOrgEmp.getJsonData()).append("; ");
                        contents.append(fbOrgEmp.getResponseJsonData()).append("]");
                    }
            );
        }
        seeyonDingtalkNoticeService.sendDingtalkNotice(contents);
        //发送邮件错误信息进行相应通知
        Map sendEmailNoticeMap = Maps.newHashMap();
        sendEmailNoticeMap.put("companyId", seeyonClient.getOpenapiAppId());
        sendEmailNoticeMap.put("itemCode", "company_send_self_email_notice");
        seeyonEmailService.sendEmail(orgName,sendEmailNoticeMap,contents,"");
        return SeeyonResponseUtils.success(new HashMap<>());
    }

}
