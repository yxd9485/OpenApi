package com.fenbeitong.openapi.plugin.seeyon.process;

import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonClient;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonFbErrorOrgEmp;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonFbOrgEmp;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportCreateEmployeeReqDTO;

import java.util.List;

public interface IFbOrgEmpProcess {

    public SeeyonFbErrorOrgEmp processFbOrgEmp(SeeyonClient seeyonClient, SeeyonFbOrgEmp seeyonFbOrgEmp);
}
