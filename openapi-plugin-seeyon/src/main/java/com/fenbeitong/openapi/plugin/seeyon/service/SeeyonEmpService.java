package com.fenbeitong.openapi.plugin.seeyon.service;

import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountEmpResp;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountParam;

import java.util.List;
import java.util.Map;

public interface SeeyonEmpService {
    public List<SeeyonAccountEmpResp> getEmpInfo(String companyId,
            SeeyonAccountParam accountParam, String accountEmpUrl, Map<String, String> tokenHeader);

    public List<SeeyonAccountEmpResp> getEmpDetail(
            List<String> empIds, String accountEmpUrl, Map<String, String> tokenHeader);


    public List<SeeyonAccountEmpResp> getOrgEmpInfo(String companyId,String orgId, String accountEmpUrl, Map<String, String> tokenHeader);
    public List<SeeyonAccountEmpResp> getAllEmployee(String accountId, String accountEmpUrl, Map<String, String> tokenHeader);
}
