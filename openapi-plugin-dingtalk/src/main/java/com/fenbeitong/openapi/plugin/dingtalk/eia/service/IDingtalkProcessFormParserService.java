package com.fenbeitong.openapi.plugin.dingtalk.eia.service;

import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkTripApplyProcessInfo;
import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyReqDTO;

/**
 * <p>Title: IDingtalkProcessFormParserService</p>
 * <p>Description: 钉钉表单解析服务接口</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/24 10:59 AM
 */
public interface IDingtalkProcessFormParserService {

    /**
     * 解析审批表单
     *
     * @param companyId            企业ID
     * @param applyType            审批单类型
     * @param instanceId           钉钉流程实例ID，
     * @param processInstanceTopVo 流程实例详情
     * @return ProcessInfo
     */
    DingtalkTripApplyProcessInfo parse(String corpId, String companyId, int applyType, String instanceId, OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo);

   }
