package com.fenbeitong.openapi.plugin.seeyon.process;

import com.fenbeitong.openapi.plugin.seeyon.dao.SeeyonFbOrgEmpDao;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonClient;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonFbErrorOrgEmp;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonFbOrgEmp;
import com.fenbeitong.openapi.plugin.seeyon.helper.Jsr310DateHelper;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonFbOrgEmpService;
import com.fenbeitong.openapi.plugin.seeyon.service.impl.FbOrgEmpService;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenApiRespDTO;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenDepartmentServiceImpl;
import com.fenbeitong.openapi.plugin.support.organization.dto.SupportCreateOrgUnitReqDTO;
import com.fenbeitong.openapi.plugin.util.JsonUtils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Map;


@Component
public class FbOrgCreateProcess extends FbOrgEmpService implements IFbOrgEmpProcess {
    @Autowired
    OpenDepartmentServiceImpl openDepartmentService;
    @Autowired
    SeeyonFbOrgEmpService seeyonFbOrgEmpService;
    @Autowired
    SeeyonFbOrgEmpDao seeyonFbOrgEmpDao;

    @Override
    public SeeyonFbErrorOrgEmp processFbOrgEmp(SeeyonClient seeyonClient, SeeyonFbOrgEmp seeyonFbOrgEmp) {
        String jsonData = seeyonFbOrgEmp.getJsonData();
        SupportCreateOrgUnitReqDTO supportCreateOrgUnitReqDTO = JsonUtils.toObj(jsonData, SupportCreateOrgUnitReqDTO.class);
        //同步部门数据到分贝通
        OpenApiRespDTO fbDepartment = openDepartmentService.createDepartmentForAPI(seeyonFbOrgEmp.getCompanyId(),
            supportCreateOrgUnitReqDTO);
        updateFbOrgEmp(seeyonClient, seeyonFbOrgEmp);
        if (!ObjectUtils.isEmpty(fbDepartment)) {
            String msg = fbDepartment.getMsg();
            Object data = fbDepartment.getData();
            if (StringUtils.isNotBlank(msg) || !ObjectUtils.isEmpty(data)) {//返回的错误信息
                SeeyonFbErrorOrgEmp build = SeeyonFbErrorOrgEmp.builder()
                        .id(seeyonFbOrgEmp.getId())
                        .companyId(seeyonFbOrgEmp.getCompanyId())
                        .jsonData(JsonUtils.toJson(supportCreateOrgUnitReqDTO))
                        .responseJsonData(msg + data)
                        .dataType(seeyonFbOrgEmp.getDataType())
                        .dataExecuteManner(seeyonFbOrgEmp.getDataExecuteManner())
                        .executeTimes(1)
                        .executeResult(1)
                        .createTime(Jsr310DateHelper.getStartTime())
                        .build();
                return build;
            }
        }
        return null;
    }
}
