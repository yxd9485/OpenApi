package com.fenbeitong.openapi.plugin.func.apply.service;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.employee.service.FuncEmployeeService;
import com.fenbeitong.openapi.plugin.func.order.dto.OrderApplyApproveChangeAndRefundReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.ApplyAgreeReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.OrderApplyAgreeReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractOrderApplyService;
import com.fenbeitong.openapi.plugin.support.apply.service.IOpenOrderApplyChangeService;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.company.entity.OpenCompanySourceType;
import com.fenbeitong.openapi.plugin.support.company.service.IOpenCompanySourceTypeService;
import com.fenbeitong.openapi.plugin.support.company.service.impl.UcCompanyServiceImpl;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.usercenter.api.model.dto.company.CompanyNewDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.Map;

/**
 * <p>Title: FuncOrderApplyServiceImpl</p>
 * <p>Description: 订单审批服务实现类</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/4/24 2:20 PM
 */
@SuppressWarnings("unchecked")
@ServiceAspect
@Service
public class FuncOrderApplyServiceImpl extends AbstractOrderApplyService {

    @Autowired
    private FuncEmployeeService employeeService;

    @Autowired
    private CommonAuthService commonAuthService;

    @Autowired
    private UcCompanyServiceImpl companyService;

    @Autowired
    private OpenEmployeeServiceImpl openEmployeeService;

    @Autowired
    private IOpenOrderApplyChangeService openOrderApplyChangeService;

    @Async
    public void notifyApplyCreated(String data) {
        Map<String, Object> dataMap = JsonUtils.toObj(data, Map.class);
        Map apply = (Map) dataMap.get("apply");
        String companyId = (String) apply.get("company_id");
        String employeeId = (String) apply.get("employee_id");
        //类型，0为分贝用户，1为第三方用户
        String token = employeeService.getEmployeeFbToken(companyId, employeeId, "0");
        CompanyNewDto companyNewDto = companyService.getCompanyService().queryCompanyNewByCompanyId(companyId);
        notifyApplyCreated(token, companyId, companyNewDto.getCompanyName(), dataMap);
    }

    public void notifyApplyAgree(ApiRequest apiRequest) throws Exception {
        Map<String, String> signMap = commonAuthService.signCheck(apiRequest);
        String companyId = signMap.get("company_id");
        String employeeId = signMap.get("employee_id");
        //类型，0为分贝用户，1为第三方用户
        String employeeType = signMap.get("employee_type");
        String token = employeeService.getEmployeeFbToken(companyId, employeeId, employeeType);
        OrderApplyAgreeReqDTO agreeReq = JsonUtils.toObj(apiRequest.getData(), OrderApplyAgreeReqDTO.class);
        ValidatorUtils.validateBySpring(agreeReq);
        notifyApplyAgree(token, agreeReq);
    }

    public void changeAndRefundNotifyApplyAgree(OrderApplyApproveChangeAndRefundReqDTO reqDTO) throws Exception {
        String companyId = reqDTO.getCompanyId();
        String employeeId = reqDTO.getEmployeeId();
        ValidatorUtils.validateBySpring(reqDTO);
        ApplyAgreeReqDTO applyAgreeReq = new ApplyAgreeReqDTO();
        applyAgreeReq.setThirdId(reqDTO.getThirdApplyId());
        applyAgreeReq.setApplyId(reqDTO.getApplyId());
        String employeeFbToken = openEmployeeService.getEmployeeFbToken(companyId, employeeId, reqDTO.getEmployeeType());
        openOrderApplyChangeService.notifyOrderApplyChangeAgree(employeeFbToken, applyAgreeReq);
    }

    @Override
    public String getProcessorKey() {
        return FuncOrderApplyServiceImpl.class.getName();
    }
}
