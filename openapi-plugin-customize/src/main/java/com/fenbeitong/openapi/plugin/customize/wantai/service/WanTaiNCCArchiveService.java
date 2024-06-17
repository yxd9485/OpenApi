package com.fenbeitong.openapi.plugin.customize.wantai.service;

import com.finhub.framework.common.service.aspect.ServiceAspect;

import com.fenbeitong.common.utils.md5.Md5Utils;
import com.fenbeitong.openapi.plugin.customize.common.exception.OpenApiCustomizeException;
import com.fenbeitong.openapi.plugin.customize.wantai.constant.ArchiveTaskStatusType;
import com.fenbeitong.openapi.plugin.customize.wantai.constant.WanTaiArchiveConstant;
import com.fenbeitong.openapi.plugin.customize.wantai.dto.ArchiveDataJobReqDTO;
import com.fenbeitong.openapi.plugin.customize.wantai.dto.FilingJobReqDTO;
import com.fenbeitong.openapi.plugin.customize.wantai.dto.NCCArchiveDataReqDTO;
import com.fenbeitong.openapi.plugin.customize.wantai.dto.NCCArchiveDataRespDTO;
import com.fenbeitong.openapi.plugin.customize.wantai.dto.NCCFilingReqDTO;
import com.fenbeitong.openapi.plugin.customize.wantai.dto.NCCFilingRespDTO;
import com.fenbeitong.openapi.plugin.customize.wantai.entity.OpenArchiveTask;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 万泰NCC档案
 *
 * @author lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class WanTaiNCCArchiveService extends AbstractArchiveService {


    @Override
    void callMakedoc(FilingJobReqDTO filingJobReqDTO, OpenArchiveTask openArchiveTask) {
        //请求NCC
        String url = filingJobReqDTO.getHost() + WanTaiArchiveConstant.URL_NCC_FILING_UP;
        NCCFilingReqDTO filingReqDTO = new NCCFilingReqDTO();
        filingReqDTO.setSyscode(filingJobReqDTO.getAccessKey());
        filingReqDTO.setSecretkey(filingJobReqDTO.getSecretKey());
        filingReqDTO.setTime(DateUtils.toSimpleStr(openArchiveTask.getEndTime()));
        filingReqDTO.setId(openArchiveTask.getTaskId());
        filingReqDTO.setArctype(openArchiveTask.getThirdArchiveType());
        filingReqDTO.setSign(
            Md5Utils.getMd5(
                filingJobReqDTO.getAccessKey() + "&" + filingReqDTO.getTime() + "&" + filingJobReqDTO.getSecretKey()));
        NCCFilingReqDTO.NCCFilingReqData filingReqData = new NCCFilingReqDTO.NCCFilingReqData();
        filingReqData.setOrgcode(openArchiveTask.getOrgCode());
        filingReqData.setBookcode(openArchiveTask.getBookCode());
        filingReqData.setYearperiod(openArchiveTask.getFiscalPeriod().replace("-", ""));
        filingReqData.setBegindate(DateUtils.toSimpleStr(openArchiveTask.getStartTime()));
        filingReqData.setEnddate(filingReqDTO.getTime());
        filingReqDTO.setData(filingReqData);
        String result = RestHttpUtils.postJson(url, JsonUtils.toJson(filingReqDTO));
        NCCFilingRespDTO nccFilingRespDTO = JsonUtils.toObj(result, NCCFilingRespDTO.class);
        //更新task
        if (nccFilingRespDTO == null || !"0".equals(nccFilingRespDTO.getStatus())) {
            throw new OpenApiCustomizeException(500, "[万泰电子档案]调用NCC归档指令失败：" + result);
        }
    }

    @Override
    List<Map<String, Object>> callGetData(ArchiveDataJobReqDTO archiveDataJobReqDTO,
        OpenArchiveTask openArchiveTask) {
        String time = DateUtils.toSimpleStr(DateUtils.now());
        String sign = Md5Utils.getMd5(
            archiveDataJobReqDTO.getAccessKey() + "&" + time + "&" + archiveDataJobReqDTO.getSecretKey());
        NCCArchiveDataReqDTO archiveDataReqDTO =
            NCCArchiveDataReqDTO.builder().arctype(openArchiveTask.getThirdArchiveType()).id(
                    openArchiveTask.getTaskId()).time(time).secretkey(archiveDataJobReqDTO.getSecretKey())
                .syscode(archiveDataJobReqDTO.getAccessKey()).sign(sign).build();
        String url = archiveDataJobReqDTO.getHost() + WanTaiArchiveConstant.URL_NCC_GET_DATA;
        String result = RestHttpUtils.postJson(url, JsonUtils.toJson(archiveDataReqDTO));
        //NCC取数
        NCCArchiveDataRespDTO archiveDataRespDTO = JsonUtils.toObj(result, NCCArchiveDataRespDTO.class);
        if (archiveDataRespDTO == null || !"0".equals(archiveDataRespDTO.getStatus())) {
            openArchiveTask.setStatus(ArchiveTaskStatusType.ERROR.getKey());
            openArchiveTask.setExecuteResultContent(result);
            return null;
        }
        return archiveDataRespDTO.getData();
    }

    @Override
    protected String getSysCode() {
        return WanTaiArchiveConstant.SYS_CODE_NCC;
    }
}
