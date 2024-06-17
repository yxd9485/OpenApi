package com.fenbeitong.openapi.plugin.landray.ekp.service.impl;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.etl.entity.OpenEtlMappingConfig;
import com.fenbeitong.openapi.plugin.landray.ekp.service.LandrayFormDataBuildService;
import com.fenbeitong.openapi.plugin.support.apply.dao.ThirdApplyDefinitionDao;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.revert.apply.constant.ServiceTypeConstant;
import com.fenbeitong.openapi.plugin.support.revert.apply.service.AbstractFormService;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 蓝凌表单相关接口
 * @Auther zhang.peng
 * @Date 2021/8/4
 */

@ServiceAspect
@Service
@Slf4j
public class LandrayFormService extends AbstractFormService {

    @Autowired
    private LandrayFormDataServiceFactory landrayFormDataServiceFactory;

    @Autowired
    private ThirdApplyDefinitionDao thirdApplyDefinitionDao;

    @Autowired
    private LandrayEmployeeServiceImpl employeeService;

    @Override
    public Object fillFormData(FenbeitongApproveDto fenbeitongApproveDto , List<OpenEtlMappingConfig> openEtlMappingConfigList , String serviceType , String fbtApplyId) {
        String companyId = fenbeitongApproveDto.getCompanyId();
        int callBackType = convertCallBackType(serviceType);
        ThirdApplyDefinition thirdApply = thirdApplyDefinitionDao.getThirdApply(companyId, callBackType);
        if ( null == thirdApply ){
            log.info("蓝凌审批表单未配置 , serviceType : {} , companyId : {} ",serviceType,companyId);
            throw new OpenApiPluginException(SupportRespCode.FORM_INFO_NOT_EXIST_ERROR);
        }
        try {
            LandrayFormDataBuildService landrayFormDataBuildService = landrayFormDataServiceFactory.getServiceByType(serviceType);
            String templateId = thirdApply.getThirdProcessCode();
            ThirdEmployeeRes thirdEmployeeRes = employeeService.getEmployeeFbPhoneInfo(companyId,fenbeitongApproveDto.getEmployeeId(),fenbeitongApproveDto.getThirdEmployeeId());
            String phone = null == thirdEmployeeRes ? "" : thirdEmployeeRes.getEmployee().getPhone_num();
            fenbeitongApproveDto.setPhone(phone);
            if (StringUtils.isEmpty(fenbeitongApproveDto.getEmployeeId())){
                String employeeId = null == thirdEmployeeRes ? "" : thirdEmployeeRes.getEmployee().getId();
                fenbeitongApproveDto.setEmployeeId(employeeId);
            }
            MultiValueMap<String,Object> wholeForm = landrayFormDataBuildService.buildFormDataInfo(fenbeitongApproveDto,openEtlMappingConfigList,fbtApplyId);
            wholeForm.add("docCreator", "{\"LoginName\":\""+ phone + "\"}");
            wholeForm.add("fdTemplateId", templateId);
            return wholeForm;
        } catch (Exception e){
            log.warn("创建蓝凌表单失败 : {}",e.getMessage());
        }
        return null;
    }

    @Override
    public String createApply(Object param,String url) {
        try {
            if ( null == param ){
                return null;
            }
            MultiValueMap<String,Object> wholeForm = (MultiValueMap<String,Object>) param;
            RestTemplate yourRestTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            //如果EKP对该接口启用了Basic认证，那么客户端需要加入
            //addAuth(headers,"yourAccount"+":"+"yourPassword");是VO，则使用APPLICATION_JSON
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            //必须设置上传类型，如果入参是字符串，使用MediaType.TEXT_PLAIN；如果
            HttpEntity<MultiValueMap<String,Object>> entity = new HttpEntity<MultiValueMap<String,Object>>(wholeForm,headers);
            //有返回值的情况 VO可以替换成具体的JavaBean
            log.info("创建蓝凌流程开始 : 参数 , url : {} , param : {}",url,wholeForm.toString());
            ResponseEntity<String> obj = yourRestTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            log.info("创建蓝凌流程结束 , 返回结果 : {} ",obj.getBody());
            return obj.getBody();
        } catch (Exception e){
            log.warn("创建蓝凌工作流失败 : {}",e.getMessage());
            try {
                log.warn(new String (((HttpServerErrorException.InternalServerError)e).getResponseBodyAsByteArray(),"utf-8"));
            } catch (Exception e1){
                log.warn("error : {}",e1.getMessage());
            }
            return null;
        }
    }

    public int convertCallBackType(String serviceType){
        int callBackType = 0;
        if (ServiceTypeConstant.MALL.equals(serviceType)){
            callBackType = CallbackType.APPLY_MALL_REVERSE.getType();
        }
        if (ServiceTypeConstant.ORDER.equals(serviceType)){
            callBackType = CallbackType.APPLY_ORDER.getType();
        }
        if (ServiceTypeConstant.CAR.equals(serviceType)){
            callBackType = CallbackType.APPLY_TAXI_REVERSE.getType();
        }
        if (ServiceTypeConstant.DINNER.equals(serviceType)){
            callBackType = CallbackType.APPLY_DINNER_REVERSE.getType();
        }
        return callBackType;
    }

}
