package com.fenbeitong.openapi.plugin.seeyon.process;

import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonClient;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonFbErrorOrgEmp;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonFbOrgEmp;
import com.fenbeitong.openapi.plugin.seeyon.helper.JacksonHelper;
import com.fenbeitong.openapi.plugin.seeyon.helper.Jsr310DateHelper;
import com.fenbeitong.openapi.plugin.seeyon.service.impl.FbOrgEmpService;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportCreateEmployeeReqDTO;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportEmployeeUpdateDTO;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportUpdateEmployeeReqDTO;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

@Component
public class FbEmpUpdateProcess extends FbOrgEmpService implements IFbOrgEmpProcess {
//    @Autowired
//    private OpenEmployeeServiceImpl employeeService;


    @Override
    public SeeyonFbErrorOrgEmp processFbOrgEmp(SeeyonClient seeyonClient, SeeyonFbOrgEmp seeyonFbOrgEmp) {
        String jsonData = seeyonFbOrgEmp.getJsonData();
        SupportUpdateEmployeeReqDTO supportUPdateEmployeeReqDTO = JsonUtils.toObj(jsonData, SupportUpdateEmployeeReqDTO.class);
        supportUPdateEmployeeReqDTO.setCompanyId(seeyonFbOrgEmp.getCompanyId());
        supportUPdateEmployeeReqDTO.setOperatorId("OpenApi");
        Map<String, Object> fbUser = null;
//        employeeService.updateFbUser(supportUPdateEmployeeReqDTO);
        updateFbOrgEmp(seeyonClient, seeyonFbOrgEmp);
        if (!ObjectUtils.isEmpty(fbUser)) {
            String msg = (String) fbUser.get("msg");
            Object data = fbUser.get("data");
            if (StringUtils.isNotBlank(msg) || !ObjectUtils.isEmpty(data)) {//返回的错误信息
                SeeyonFbErrorOrgEmp build = SeeyonFbErrorOrgEmp.builder()
                        .id(seeyonFbOrgEmp.getId())
                        .companyId(seeyonFbOrgEmp.getCompanyId())
                        .jsonData(JsonUtils.toJson(supportUPdateEmployeeReqDTO))
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
        return null;
    }

}
