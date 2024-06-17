package com.fenbeitong.openapi.plugin.seeyon.process;

import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonClient;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonFbErrorOrgEmp;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonFbOrgEmp;
import com.fenbeitong.openapi.plugin.seeyon.helper.JacksonHelper;
import com.fenbeitong.openapi.plugin.seeyon.helper.Jsr310DateHelper;
import com.fenbeitong.openapi.plugin.seeyon.service.impl.FbOrgEmpService;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportCreateEmployeeReqDTO;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportEmployeeInsertDTO;
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
public class FbEmpCreateProcess extends FbOrgEmpService implements IFbOrgEmpProcess {

//    @Autowired
//    private  employeeService;

    /**
     * 根据传入的SeeyonFbOrgEmp，同步到分贝通，然后失败的数据返回
     *
     * @param seeyonFbOrgEmp
     * @return
     */
    public SeeyonFbErrorOrgEmp processFbOrgEmp(SeeyonClient seeyonClient, SeeyonFbOrgEmp seeyonFbOrgEmp) {
        String jsonData = seeyonFbOrgEmp.getJsonData();
        SupportCreateEmployeeReqDTO supportCreateEmployeeReqDTO = JsonUtils.toObj(jsonData, SupportCreateEmployeeReqDTO.class);
        supportCreateEmployeeReqDTO.setCompanyId(seeyonFbOrgEmp.getCompanyId());
        supportCreateEmployeeReqDTO.setOperatorId("OpenApi");
        Map<String, Object> fbUser = null;
//                employeeService.createFbUser(supportCreateEmployeeReqDTO);
        //更新fb_org_emp表里的记录，执行过以后就更新执行状态，后续再新增的时候，就可以不添加重复数据
        updateFbOrgEmp(seeyonClient, seeyonFbOrgEmp);
        if (!ObjectUtils.isEmpty(fbUser)) {
            String msg = (String) fbUser.get("msg");
            Object data = fbUser.get("data");
            if (StringUtils.isNotBlank(msg) || !ObjectUtils.isEmpty(data)) {//返回的错误信息
                SeeyonFbErrorOrgEmp build = SeeyonFbErrorOrgEmp.builder()
                        .id(seeyonFbOrgEmp.getId())
                        .companyId(seeyonFbOrgEmp.getCompanyId())
                        .jsonData(JsonUtils.toJson(supportCreateEmployeeReqDTO))
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
