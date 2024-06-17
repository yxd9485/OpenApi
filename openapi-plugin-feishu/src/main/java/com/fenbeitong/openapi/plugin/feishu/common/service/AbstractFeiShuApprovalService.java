package com.fenbeitong.openapi.plugin.feishu.common.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseCode;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.*;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuException;
import com.fenbeitong.openapi.plugin.feishu.common.util.AbstractFeiShuHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ServiceAspect
@Service
@Slf4j
public abstract class AbstractFeiShuApprovalService {

    @Value("${feishu.approval-host}")
    private String feishuHost;

    /**
     * 获取飞书审批单详情
     *
     * @param corpId
     * @param approvalCode
     * @return
     */
    public FeiShuApprovalRespDTO getFeiShuApprovalDetail(String corpId, String approvalCode) {
        String url = feishuHost + FeiShuConstant.GET_APPROVAL_DETAIL;
        //默认返回中文结构
        Map<String, Object> param = new HashMap<>();
        param.put("instance_code", approvalCode);
        param.put("locale", "zh-CN");
        String res = getFeiShuHttpUtils().getWithTenantAccessToken(url, param, corpId);
        FeiShuApprovalRespDTO feiShuApprovalRespDTO = JsonUtils.toObj(res, FeiShuApprovalRespDTO.class);
        if (feiShuApprovalRespDTO == null || 0 != feiShuApprovalRespDTO.getCode()) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.GET_APPROVAL_DETAIL_FAILED);
        }
        return feiShuApprovalRespDTO;
    }

    /**
     * 获取FeiShuHttpUtils
     *
     * @return
     */
    protected abstract AbstractFeiShuHttpUtils getFeiShuHttpUtils();

    /**
     * 获取FeiShuApprovalService
     *
     * @return
     */
    protected abstract AbstractFeiShuEmployeeService getFeiShuEmployeeService();

    /**
     * 获取审批定义
     *
     * @param approvalCode
     * @param corpId
     * @return
     */
    public List<FeiShuApprovalSimpleFormDTO> getApprovalDefine(String approvalCode, String corpId) {
        String url = feishuHost + FeiShuConstant.GET_APPROVAL;
        //默认返回中文结构
        Map<String, Object> param = new HashMap<>();
        param.put("approval_code", approvalCode);
        param.put("locale", "zh-CN");
        String res = getFeiShuHttpUtils().postJsonWithTenantAccessToken(url, JsonUtils.toJson(param), corpId);
        FeiShuApprovalDefineRespDTO feiShuApprovalDefineRespDTO = JsonUtils.toObj(res, FeiShuApprovalDefineRespDTO.class);
        if (feiShuApprovalDefineRespDTO == null || 0 != feiShuApprovalDefineRespDTO.getCode()) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.GET_APPROVAL_DEFINE_FAILED);
        }
        String form = feiShuApprovalDefineRespDTO.getData().getForm();
        List<FeiShuApprovalSimpleFormDTO> feiShuApprovalSimpleFormDTO = JsonUtils.toObj(form, new TypeReference<List<FeiShuApprovalSimpleFormDTO>>() {
        });
        return feiShuApprovalSimpleFormDTO;
    }

    /**
     * 创建审批实例
     *
     * @param feiShuCreateInstanceReqDTO
     * @param corpId
     */
    public String createApprovalInstance(FeiShuCreateApprovalInstanceReqDTO feiShuCreateInstanceReqDTO, String corpId) {
        String url = feishuHost + FeiShuConstant.CREATE_INSTANCE;
        //默认返回中文结构
        String res = getFeiShuHttpUtils().postJsonWithTenantAccessToken(url, JsonUtils.toJson(feiShuCreateInstanceReqDTO), corpId);
        FeiShuCreateApprovalInstanceRespDTO feiShuCreateApprovalInstanceRespDTO = JsonUtils.toObj(res, FeiShuCreateApprovalInstanceRespDTO.class);
        if (feiShuCreateApprovalInstanceRespDTO == null || 0 != feiShuCreateApprovalInstanceRespDTO.getCode() || StringUtils.isBlank(feiShuCreateApprovalInstanceRespDTO.getData().getInstanceCode())) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.CREATE_APPROVAL_INSTANCE_FAILED);
        }
        return feiShuCreateApprovalInstanceRespDTO.getData().getInstanceCode();
    }


    /**
     * 审批注册
     * @param approvalCode
     * @param corpId
     * @return
     */
    public FeiShuRespDTO subscribeApproval(String approvalCode, String corpId) {
        String url = feishuHost + FeiShuConstant.SUBSCRIBE_APPROVAL;
        Map<String, Object> param = new HashMap<>();
        param.put("approval_code", approvalCode);
        String res = getFeiShuHttpUtils().postJsonWithTenantAccessToken(url, JsonUtils.toJson(param), corpId);
        FeiShuRespDTO feiShuRespDTO = JsonUtils.toObj(res, FeiShuRespDTO.class);
        return feiShuRespDTO;
    }

    /**
     * 上传文件
     * @param corpId
     * @return
     */
    public FeiShuFileuploadResp feiShuFileupload( String corpId , FeishuUploadFileReq fileReq) {
        String url = feishuHost + FeiShuConstant.FILE_UPLOAD;
        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("name", fileReq.getName());
        requestMap.add("type", fileReq.getType());
        requestMap.add("content", new FileSystemResource(fileReq.getContent()));
        String res = getFeiShuHttpUtils().postFileWithTenantAccessToken(url, requestMap , corpId);
        FeiShuFileuploadResp feiShuRespDTO = JsonUtils.toObj(res, FeiShuFileuploadResp.class);
        return feiShuRespDTO;
    }

}
