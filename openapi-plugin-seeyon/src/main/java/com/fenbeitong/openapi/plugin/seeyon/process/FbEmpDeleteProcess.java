package com.fenbeitong.openapi.plugin.seeyon.process;

import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.openapi.plugin.seeyon.constant.FbOrgEmpConstants;
import com.fenbeitong.openapi.plugin.seeyon.dao.SeeyonFbOrgEmpDao;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonClient;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonFbErrorOrgEmp;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonFbOrgEmp;
import com.fenbeitong.openapi.plugin.seeyon.helper.Jsr310DateHelper;
import com.fenbeitong.openapi.plugin.seeyon.service.impl.FbOrgEmpService;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportDeleteEmployeeReqDTO;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;
import com.fenbeitong.usercenter.api.service.employee.IThirdEmployeeService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Map;

@Component
public class FbEmpDeleteProcess extends FbOrgEmpService implements IFbOrgEmpProcess {
    //    @Autowired
//    private OpenEmployeeServiceImpl employeeService;
    @Autowired
    SeeyonFbOrgEmpDao seeyonFbOrgEmpDao;
    @DubboReference(check = false)
    private IThirdEmployeeService iThirdEmployeeService;

    @Override
    public SeeyonFbErrorOrgEmp processFbOrgEmp(SeeyonClient seeyonClient, SeeyonFbOrgEmp seeyonFbOrgEmp) {
        String jsonData = seeyonFbOrgEmp.getJsonData();
        String thirdEmployeeId = "";
        if (FbOrgEmpConstants.CALL_ORDER_ONE.equals(seeyonFbOrgEmp.getSort())) {//删除人
            thirdEmployeeId = jsonData.substring(jsonData.indexOf("[") + 2, jsonData.indexOf("]") - 1);
        }
        SupportDeleteEmployeeReqDTO supportDeleteEmployeeReqDTO = new SupportDeleteEmployeeReqDTO();
        supportDeleteEmployeeReqDTO.setCompanyId(seeyonFbOrgEmp.getCompanyId());
        supportDeleteEmployeeReqDTO.setOperatorId("OpenApi");
        supportDeleteEmployeeReqDTO.setThirdEmployeeIds(Lists.newArrayList(thirdEmployeeId));
        ThirdEmployeeRes employeeByThirdId = getEmployeeByThirdId(seeyonClient.getOpenapiAppId(), thirdEmployeeId);

        if (!ObjectUtils.isEmpty(employeeByThirdId)) {//查询分贝，可以查询到人员时进行删除操作
            Map<String, Object> fbUser = null;
//            employeeService.deleteFbUser(supportDeleteEmployeeReqDTO);
            updateFbOrgEmp(seeyonClient, seeyonFbOrgEmp);
            if (!ObjectUtils.isEmpty(fbUser)) {
                String msg = (String) fbUser.get("msg");
                Object data = fbUser.get("data");
                if (StringUtils.isNotBlank(msg) || !ObjectUtils.isEmpty(data)) {//返回的错误信息
                    SeeyonFbErrorOrgEmp build = SeeyonFbErrorOrgEmp.builder()
                            .id(seeyonFbOrgEmp.getId())
                            .companyId(seeyonFbOrgEmp.getCompanyId())
                            .jsonData(JsonUtils.toJson(supportDeleteEmployeeReqDTO))
                            .dataType(seeyonFbOrgEmp.getDataType())
                            .dataExecuteManner(seeyonFbOrgEmp.getDataExecuteManner())
                            .executeTimes(1)
                            .executeResult(1)
                            .responseJsonData(msg + data)
                            .createTime(Jsr310DateHelper.getStartTime())
                            .build();
                    return build;
                }
            }
        } else {
            seeyonFbOrgEmpDao.delete(seeyonFbOrgEmp);
        }
        return null;
    }

    @Override
    protected IThirdEmployeeService getThirdEmployeeService() {
        return iThirdEmployeeService;
    }

}
