package com.fenbeitong.openapi.plugin.yunzhijia.service.impl.form;

import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.support.revert.apply.dto.CommonNoticeResultDto;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaAccessTokenReqDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaApplyRespDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaFormDataBuilderService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaFormService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.apply.YunzhijiaRemoteApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.Map;

/**
 * 云之家表单填充
 * @Auther zhang.peng
 * @Date 2021/7/12
 */
@ServiceAspect
@Service
public class YunzhijiaFormServiceImpl implements IYunzhijiaFormService {

    @Autowired
    private YunzhijiaFormDataServiceFactory yunzhijiaFormDataServiceFactory;

    @Autowired
    private YunzhijiaRemoteApplyService yunzhijiaRemoteApplyService;

    @Override
    public Map<String, Object> fillFormData(String type, FenbeitongApproveDto fenbeitongApproveDto) {
        IYunzhijiaFormDataBuilderService yunzhijiaFormDataBuildService = yunzhijiaFormDataServiceFactory.getServiceByType(type);
        return yunzhijiaFormDataBuildService.buildForm(fenbeitongApproveDto);
    }

    @Override
    public YunzhijiaApplyRespDTO createApply(YunzhijiaAccessTokenReqDTO build, Map<String, Object> yunzhijiaApplyReqMap, CommonNoticeResultDto noticeResultDto){
        return yunzhijiaRemoteApplyService.createYunzhijiaRemoteApply(build, yunzhijiaApplyReqMap, noticeResultDto.getThirdProcessCode(), noticeResultDto.getThirdEmployeeId());
    }

}
