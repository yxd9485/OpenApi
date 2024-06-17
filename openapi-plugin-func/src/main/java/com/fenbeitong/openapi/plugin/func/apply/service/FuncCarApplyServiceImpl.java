package com.fenbeitong.openapi.plugin.func.apply.service;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.common.FuncValidService;
import com.fenbeitong.openapi.plugin.func.employee.service.FuncEmployeeService;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractCarApplyService;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: FuncCarApplyServiceImpl</p>
 * <p>Description: 用车申请单</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/7 11:53 AM
 */
@ServiceAspect
@Service
public class FuncCarApplyServiceImpl extends AbstractCarApplyService {

    @Autowired
    private CommonAuthService signService;
    @Autowired
    CommonApplyServiceImpl commonApplyService;
    @Autowired
    private FuncEmployeeService employeeService;
    @Autowired
    private CommonAuthService commonAuthService;
    @Autowired
    private ThirdCallbackRecordDao recordDao;
    @Autowired
    private FuncValidService validService;


    public Object createCarApply(ApiRequest request) throws Exception {
        String token = signService.checkSign(request);
        CarApproveCreateReqDTO createReq = JsonUtils.toObj(request.getData(), CarApproveCreateReqDTO.class);
        ValidatorUtils.validateBySpring(createReq);
        @Valid @NotNull(message = "审批单申请内容[apply]不可为空") CarApproveApply apply = createReq.getApply();
        //获取appId
        if (StringUtils.isNotBlank(apply.getApplyReason())) {
            validService.lengthValid(apply.getApplyReason().trim(),"apply_reason");

        }
        apply.setCompanyId(signService.getAppId(request));
        createReq.setApply(apply);
        return createCarApprove(token, createReq);
    }

    public void notifyCarApplyAgree(ApiRequest apiRequest) {
        Map<String, String> signMap = commonAuthService.signCheck(apiRequest);
        String companyId = signMap.get("company_id");
        String employeeId = signMap.get("employee_id");
        //类型，0为分贝用户，1为第三方用户
        String employeeType = signMap.get("employee_type");
        String token = employeeService.getEmployeeFbToken(companyId, employeeId, employeeType);
        TaxiApplyAgreeReqDTO agreeReq = JsonUtils.toObj(apiRequest.getData(), TaxiApplyAgreeReqDTO.class);
        ThirdCallbackRecord record = recordDao.getApplyByApplyId(agreeReq.getApplyId(), CallbackType.APPLY_TAXI_REVERSE.getType());
        if (record != null) {
            agreeOrderApply(token, agreeReq);
        } else {
            throw new OpenApiPluginSupportException(SupportRespCode.FB_APPLY_ID_NOT_EXIST,"根据 applyId 查询订单为空");
        }
    }

}
