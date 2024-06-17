package com.fenbeitong.openapi.plugin.func.apply.service;

import com.fenbeitong.openapi.plugin.func.employee.service.FuncEmployeeService;
import com.fenbeitong.openapi.plugin.func.order.dto.MallApplyApproveReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.MallApplyAgreeReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractMallApplyService;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.company.service.ICompanyService;
import com.fenbeitong.openapi.plugin.support.company.service.impl.UcCompanyServiceImpl;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.usercenter.api.model.dto.company.CompanyNewDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.Map;

/**
 * <p>Title: FuncOrderApplyServiceImpl</p>
 * <p>Description: 采购审批服务实现类</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/7/8 16:20 PM
 */
@SuppressWarnings("unchecked")
@ServiceAspect
@Service
@Slf4j
public class FuncMallApplyServiceImpl extends AbstractMallApplyService {

    @Autowired
    private FuncEmployeeService employeeService;

    @Autowired
    private ThirdCallbackRecordDao recordDao;

    @Autowired
    private UcCompanyServiceImpl ucCompanyService;

    @Autowired
    private ICompanyService companyService;

    public void notifyApplyCreated(String data) {
        Map<String, Object> dataMap = JsonUtils.toObj(data, Map.class);
        Map apply = (Map) dataMap.get("apply");
        String companyId = (String) apply.get("company_id");
        Map<String, Object> dockingMap = companyService.isDocking(companyId);
        if ((boolean) dockingMap.get("company_apply_mall")) {
            CompanyNewDto companyNewDto = ucCompanyService.getCompanyService().queryCompanyNewByCompanyId(companyId);
            notifyApplyCreated(companyId, companyNewDto.getCompanyName(), dataMap);
        } else {
            log.info("companyId " + companyId + "no need push");
        }
    }

    public void notifyApplyAgree(MallApplyApproveReqDTO req) {
        String token = employeeService.getEmployeeFbToken(req.getCompanyId(), req.getEmployeeId(), req.getEmployeeType());
        MallApplyAgreeReqDTO build = new MallApplyAgreeReqDTO().builder()
                .applyId(req.getApplyId())
                .thirdId(req.getThirdApplyId())
                .build();
        ThirdCallbackRecord record = recordDao.getApplyByApplyId(req.getApplyId(), 5);
        if (record != null) {
            agreeOrderApply(token, build);
        } else {
            throw new OpenApiPluginSupportException(SupportRespCode.FB_APPLY_ID_NOT_EXIST,"根据 applyId 查询订单为空");
        }
    }

    @Override
    public String getProcessorKey() {
        return FuncMallApplyServiceImpl.class.getName();
    }
}
