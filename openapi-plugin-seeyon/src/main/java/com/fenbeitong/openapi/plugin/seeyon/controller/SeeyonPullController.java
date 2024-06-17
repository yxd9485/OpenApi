package com.fenbeitong.openapi.plugin.seeyon.controller;

import com.fenbeitong.openapi.plugin.seeyon.common.SeeyonResponseUtils;
import com.fenbeitong.openapi.plugin.seeyon.constant.FbOrgEmpConstants;
import com.fenbeitong.openapi.plugin.seeyon.constant.SeeyonConstant;
import com.fenbeitong.openapi.plugin.seeyon.dto.*;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonClient;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOpenMsgSetup;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOrgDepartment;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOrgEmployee;
import com.fenbeitong.openapi.plugin.seeyon.enums.ApiStatusCodeEnum;
import com.fenbeitong.openapi.plugin.seeyon.enums.HttpStatusCodeEnum;
import com.fenbeitong.openapi.plugin.seeyon.exceptions.SeeyonApiException;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonAccountService;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonDepartmentService;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonEmpService;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonFbOrgEmpService;
import com.fenbeitong.openapi.plugin.seeyon.service.impl.*;
import com.fenbeitong.openapi.plugin.seeyon.transformer.SeeyonOrgDepartmentTransformer;
import com.fenbeitong.openapi.plugin.seeyon.transformer.SeeyonOrgEmployeeTransformer;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@Slf4j
@RequestMapping("/seeyon/pull")
public class SeeyonPullController {
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
    SeeyonExtInfoService seeyonExtInfoService;


    @RequestMapping("/org")
    public Object getSeeyonOrg(@RequestParam("jobConfig") String jobConfig)
            throws IOException {
        log.info("执行拉取致远OA部门数据任务开始-----------");
        SeeyonOrgNameReq seeyonOrgNameReq = JsonUtils.toObj(jobConfig, SeeyonOrgNameReq.class);
        String orgName = "";
        Long compareDaysGap = 0L;

        if (!ObjectUtils.isEmpty(seeyonOrgNameReq)) {
            orgName = seeyonOrgNameReq.getOrgName();
            compareDaysGap = seeyonOrgNameReq.getCompareDaysGap();
        } else {
            throw new SeeyonApiException(HttpStatusCodeEnum.BAD_REQUEST);
        }

        Map<String, String> resultMap = new HashMap<>(2);
        SeeyonClient seeyonClient = seeyonClientService.getSeeyonClientByName(orgName);

        if (Objects.isNull(seeyonClient)) {
            throw new SeeyonApiException(ApiStatusCodeEnum.SEEYON_ORG_NAME_NOT_FOUND.transform());
        } else {
            Map<String, String> tokenHeader = new HashMap<>(1);

            String token =
                    seeyonAccessTokenService.getAccessToken(
                            SeeyonAccessTokenReq.builder()
                                    .userName(seeyonClient.getSeeyonUsername())
                                    .password(seeyonClient.getSeeyonPassword())
                                    .build(),
                            seeyonClient.getSeeyonSysUri());
            tokenHeader.put(SeeyonConstant.TOKEN_HEADER, token);
            String groupType = seeyonClient.getGroupType();
            String seeyonCode = seeyonClient.getSeeyonCode();
            String accountId = "";
            if (groupType.equals("0")) {//是否为集团用户
                accountId =
                        seeyonAccountService.getAccountCode(
                                SeeyonAccountParam.builder().accountCode(seeyonCode).build(),
                                seeyonClient.getSeeyonSysUri(),
                                tokenHeader);
            } else {
                accountId =
                        seeyonAccountService.getAccountId(
                                SeeyonAccountParam.builder().orgName(URLEncoder.encode(orgName, "UTF-8")).build(),
                                seeyonClient.getSeeyonSysUri(),
                                tokenHeader);
            }

            //
            List<SeeyonAccountOrgResp> accountOrg =
                    seeyonDepartmentService.getOrgInfo("",
                            SeeyonAccountParam.builder().orgAccountId(accountId).build(),
                            seeyonClient.getSeeyonSysUri(),
                            tokenHeader);
            SeeyonOpenMsgSetup syncFrequency = seeyonExtInfoService.getSyncFrequency(seeyonClient.getOpenapiAppId());
            Integer intVal1 = syncFrequency.getIntVal1();
            if (intVal1 == 1) {//说明配置了同步频率。不配置同步频率，不进行执行操作
                //具体同步频率
                Integer intVal2 = syncFrequency.getIntVal2();
                /* 循环保存 ,计入流水表*/
                for (SeeyonAccountOrgResp accountOrgResponse : accountOrg) {
                    SeeyonOrgDepartment seeyonOrgDepartment =
                            SeeyonOrgDepartmentTransformer.createFromIncome(
                                    accountOrgResponse, seeyonClient.getUuid(), LocalDateTime.now(), compareDaysGap);
                    seeyonMiddlewareService.saveSeeyonDepartment(seeyonOrgDepartment);

                    /* FB 数据初始化 */
                    if (FbOrgEmpConstants.INIT_DAY_TRIGGER.equals(compareDaysGap)) {
                        if (seeyonPullOrgService.initOrgData(seeyonClient, accountOrgResponse)) {
                            resultMap.put(SeeyonConstant.INIT_CALL, SeeyonConstant.CALL_SUCCESS);
                        } else {
                            resultMap.put(SeeyonConstant.INIT_CALL, SeeyonConstant.CALL_FAIL);
                        }
                    } else {
                        if (seeyonPullOrgService.filterOrgData(seeyonClient, accountOrgResponse, compareDaysGap, intVal2)) {
                            resultMap.put(SeeyonConstant.JOB_CALL, SeeyonConstant.CALL_SUCCESS);
                        } else {
                            resultMap.put(SeeyonConstant.JOB_CALL, SeeyonConstant.CALL_FAIL);
                        }
                    }
                }
            }

            /* 比对数据删除变化 */
            if (seeyonPullOrgService.filterDiffOrg(seeyonClient, accountOrg)) {
                resultMap.put(SeeyonConstant.COMPARE_DEL, SeeyonConstant.CALL_SUCCESS);
            } else {
                resultMap.put(SeeyonConstant.COMPARE_DEL, SeeyonConstant.CALL_FAIL);
            }

            /* 页面响应 */
            resultMap.put(SeeyonConstant.SEEYON_TOKEN, token);
            resultMap.put(SeeyonConstant.SEEYON_ACCOUT_ID, accountId);
//            resultMap.put(SeeyonConstant.SEEYON_ACCOUT_ORG, accountOrg.toString());
        }

        return SeeyonResponseUtils.success(resultMap);
    }


    @RequestMapping(value = "/emp")
    @ResponseBody
    public Object getSeeyonEmp(@RequestParam("jobConfig") String jobConfig)
            throws UnsupportedEncodingException {
        log.info("执行拉取致远OA人员数据任务开始-----------");
        SeeyonOrgNameReq seeyonOrgNameReq = JsonUtils.toObj(jobConfig, SeeyonOrgNameReq.class);
        String orgName = "";
        Long compareDaysGap = 0L;

        if (!ObjectUtils.isEmpty(seeyonOrgNameReq)) {
            orgName = seeyonOrgNameReq.getOrgName();
            compareDaysGap = seeyonOrgNameReq.getCompareDaysGap();
        } else {
            throw new SeeyonApiException(HttpStatusCodeEnum.BAD_REQUEST);
        }

        Map<String, String> resultMap = new HashMap<>(2);
        SeeyonClient seeyonClient = seeyonClientService.getSeeyonClientByName(orgName);
        if (Objects.isNull(seeyonClient)) {
            throw new SeeyonApiException(ApiStatusCodeEnum.SEEYON_ORG_NAME_NOT_FOUND.transform());
        } else {
            Map<String, String> tokenHeader = new HashMap<>(1);
            String token =
                    seeyonAccessTokenService.getAccessToken(
                            SeeyonAccessTokenReq.builder()
                                    .userName(seeyonClient.getSeeyonUsername())
                                    .password(seeyonClient.getSeeyonPassword())
                                    .build(),
                            seeyonClient.getSeeyonSysUri());
            tokenHeader.put(SeeyonConstant.TOKEN_HEADER, token);
            String groupType = seeyonClient.getGroupType();

            String accountId = "";
            if (groupType.equals("0")) {//是否为集团用户
                String seeyonCode = seeyonClient.getSeeyonCode();
                accountId =
                        seeyonAccountService.getAccountCode(
                                SeeyonAccountParam.builder().accountCode(seeyonCode).build(),
                                seeyonClient.getSeeyonSysUri(),
                                tokenHeader);
            } else {
                accountId =
                        seeyonAccountService.getAccountId(
                                SeeyonAccountParam.builder().orgName(URLEncoder.encode(orgName, "UTF-8")).build(),
                                seeyonClient.getSeeyonSysUri(),
                                tokenHeader);
            }

            List<SeeyonAccountEmpResp> empInfo =
                    seeyonEmpService.getEmpInfo("",
                            SeeyonAccountParam.builder().orgAccountId(accountId).build(),
                            seeyonClient.getSeeyonSysUri(),
                            tokenHeader);

            //循环保存获取的数据
            for (SeeyonAccountEmpResp accountEmpResponse : empInfo) {
                SeeyonOrgEmployee seeyonOrgEmployee =
                        SeeyonOrgEmployeeTransformer.createFromIncome(
                                accountEmpResponse, seeyonClient.getUuid(), LocalDateTime.now(), compareDaysGap);
                //
                boolean b = seeyonFbOrgEmpService.saveEmp(seeyonOrgEmployee);
                if (b) {//存储成功进行初始化或者数据处理
                    if (FbOrgEmpConstants.INIT_DAY_TRIGGER.equals(compareDaysGap)) {
                        if (seeyonPullEmpService.initEmpData(seeyonClient, accountEmpResponse)) {
                            resultMap.put(SeeyonConstant.INIT_CALL, SeeyonConstant.CALL_SUCCESS);
                        } else {
                            resultMap.put(SeeyonConstant.INIT_CALL, SeeyonConstant.CALL_FAIL);
                        }
                    } else {
                        if (seeyonPullEmpService.filterEmpData(seeyonClient, accountEmpResponse, compareDaysGap)) {
                            resultMap.put(SeeyonConstant.JOB_CALL, SeeyonConstant.CALL_SUCCESS);
                        } else {
                            resultMap.put(SeeyonConstant.JOB_CALL, SeeyonConstant.CALL_FAIL);
                        }
                    }
                } else {
                    // TODO 数据没有成功存储到流水表里，进行异常处理,
                    //  新增手动处理没有同步到人员流水表的数据，通过接口拉取数据到流水表
                    log.info("存储人员数据到流水表异常 {}", accountEmpResponse.getId());
                }
            }
            //根据人员的状态来判断是否为删除操作
            if (seeyonPullEmpService.filterDiffEmp(seeyonClient, empInfo)) {
                resultMap.put(SeeyonConstant.COMPARE_DEL, SeeyonConstant.CALL_SUCCESS);
            } else {
                resultMap.put(SeeyonConstant.COMPARE_DEL, SeeyonConstant.CALL_FAIL);
            }

            resultMap.put(SeeyonConstant.SEEYON_TOKEN, token);
            resultMap.put(SeeyonConstant.SEEYON_ACCOUT_ID, accountId);
//            resultMap.put(SeeyonConstant.SEEYON_ACCOUT_EMP, empInfo.toString());
        }
        return SeeyonResponseUtils.success(resultMap);
    }


//    public Object syncThirdOrgEmp(){
//
//        seeyonPullOrgService.syncThirdOrg();
//        return SeeyonResponseUtils.success(new HashMap<>());
//    }

    @RequestMapping(value = "/bindAllDept")
    public Object bindAllDeptId(@RequestParam String token) {
        String accountId = "4897941458681613629";
        String url = "http://121.10.238.34:9090/seeyon/rest";
        Map<String, String> tokenHeader = new HashMap<>(1);
        tokenHeader.put(SeeyonConstant.TOKEN_HEADER, token);
        SeeyonAccountParam build = SeeyonAccountParam.builder().orgAccountId(accountId).build();
        String companyId = "5eb285cc23445f1200a7cd8f";
        List<SeeyonAccountOrgResp> seeyonAccountOrgs = seeyonDepartmentService.getOrgInfo("",build, url, tokenHeader);
        List<String> strings = new ArrayList<>();
        Map map = seeyonPullOrgService.bindAllDepIdByNameList(accountId, companyId, seeyonAccountOrgs, strings);
        return SeeyonResponseUtils.success(map);
    }

}
