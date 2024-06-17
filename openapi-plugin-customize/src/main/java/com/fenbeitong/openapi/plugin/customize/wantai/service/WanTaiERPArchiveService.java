package com.fenbeitong.openapi.plugin.customize.wantai.service;

import com.finhub.framework.common.service.aspect.ServiceAspect;

import com.fenbeitong.openapi.plugin.customize.common.exception.OpenApiCustomizeException;
import com.fenbeitong.openapi.plugin.customize.wantai.constant.ArchiveTaskStatusType;
import com.fenbeitong.openapi.plugin.customize.wantai.constant.WanTaiArchiveConstant;
import com.fenbeitong.openapi.plugin.customize.wantai.dto.ArchiveDataJobReqDTO;
import com.fenbeitong.openapi.plugin.customize.wantai.dto.ERPArchiveDataReqDTO;
import com.fenbeitong.openapi.plugin.customize.wantai.dto.ERPArchiveDataRespDTO;
import com.fenbeitong.openapi.plugin.customize.wantai.dto.ERPFilingReqDTO;
import com.fenbeitong.openapi.plugin.customize.wantai.dto.ERPTokenRespDTO;
import com.fenbeitong.openapi.plugin.customize.wantai.dto.FilingJobReqDTO;
import com.fenbeitong.openapi.plugin.customize.wantai.entity.OpenArchiveTask;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 万泰ERP档案
 *
 * @author lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class WanTaiERPArchiveService extends AbstractArchiveService {


    @Override
    void callMakedoc(FilingJobReqDTO filingJobReqDTO, OpenArchiveTask openArchiveTask) {
        String erpToken =
            getERPToken(filingJobReqDTO.getHost(), filingJobReqDTO.getAccessKey(), filingJobReqDTO.getSecretKey());
        ERPFilingReqDTO archiveDataReqDTO =
            ERPFilingReqDTO.builder().taskId(openArchiveTask.getTaskId()).orgCode(
                openArchiveTask.getOrgCode()).period(
                openArchiveTask.getFiscalPeriod().replace("-", "")).beginTime(
                DateUtils.toSimpleStr(openArchiveTask.getStartTime())).endTime(
                DateUtils.toSimpleStr(openArchiveTask.getEndTime())).build();
        String url = filingJobReqDTO.getHost() + WanTaiArchiveConstant.URL_ERP_FILING_UP;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("token", erpToken);
        String result = RestHttpUtils.postJson(url, httpHeaders, JsonUtils.toJson(archiveDataReqDTO));
        ERPTokenRespDTO erpRespDTO = JsonUtils.toObj(result, ERPTokenRespDTO.class);
        //更新task
        if (erpRespDTO == null || 0 != erpRespDTO.getCode()) {
            throw new OpenApiCustomizeException(500, "[万泰电子档案]调用ERP归档指令失败：" + result);
        }
    }

    @Override
    List<Map<String, Object>> callGetData(ArchiveDataJobReqDTO archiveDataJobReqDTO,
        OpenArchiveTask openArchiveTask) {
        List<Map<String, Object>> result = new ArrayList<>();

        String url = archiveDataJobReqDTO.getHost() + WanTaiArchiveConstant.URL_ERP_GET_DATA;
        int totalPage = 1;
        for (int i = 1; i <= totalPage; i++) {
            String erpToken = getERPToken(archiveDataJobReqDTO.getHost(), archiveDataJobReqDTO.getAccessKey(),
                archiveDataJobReqDTO.getSecretKey());
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("token", erpToken);
            ERPArchiveDataReqDTO archiveDataReqDTO = new ERPArchiveDataReqDTO();
            archiveDataReqDTO.setIdTask(openArchiveTask.getTaskId());
            archiveDataReqDTO.setPageIndex(i);
            archiveDataReqDTO.setPageSize(100);
            String erpResult = RestHttpUtils.postJson(url, httpHeaders, JsonUtils.toJson(archiveDataReqDTO));
            ERPArchiveDataRespDTO archiveDataRespDTO = JsonUtils.toObj(erpResult, ERPArchiveDataRespDTO.class);
            //第1页拉不到数据时记录失败
            if (i == 1 && (archiveDataRespDTO == null || 0 != archiveDataRespDTO.getCode())) {
                openArchiveTask.setStatus(ArchiveTaskStatusType.ERROR.getKey());
                openArchiveTask.setExecuteResultContent(erpResult);
                return null;
            }
            ERPArchiveDataRespDTO.ERPArchiveDataDTO data = archiveDataRespDTO.getData();
            if (i == 1) {
                String pageCountStr = data.getPageCount();
                totalPage = NumericUtils.obj2int(pageCountStr);
            }
            result.addAll(data.getRecords());
        }
        return result;
    }

    @Override
    protected String getSysCode() {
        return WanTaiArchiveConstant.SYS_CODE_ERP;
    }


    private String getERPToken(String host, String accessKey, String secretKey) {
        String url = host + WanTaiArchiveConstant.URL_ERP_TOKEN;
        MultiValueMap param = new LinkedMultiValueMap();
        param.add("accessKey", accessKey);
        param.add("secretKey", secretKey);
        String result = RestHttpUtils.postForm(url, param);
        ERPTokenRespDTO erpTokenRespDTO = JsonUtils.toObj(result, ERPTokenRespDTO.class);
        if (erpTokenRespDTO == null || erpTokenRespDTO.getCode() != 0 || StringUtils.isBlank(
            erpTokenRespDTO.getData())) {
            throw new OpenApiCustomizeException(500, "[万泰电子档案]获取ERP token失败：" + result);
        }
        return erpTokenRespDTO.getData();
    }

}
