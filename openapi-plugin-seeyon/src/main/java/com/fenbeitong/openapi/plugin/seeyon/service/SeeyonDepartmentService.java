package com.fenbeitong.openapi.plugin.seeyon.service;

import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountOrgResp;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountParam;

import java.util.List;
import java.util.Map;

public interface SeeyonDepartmentService {

    public List<SeeyonAccountOrgResp> getOrgInfo(String companyId,
            SeeyonAccountParam accountParam, String accountOrgUrl, Map<String, String> tokenHeader);

    public List<SeeyonAccountOrgResp> getOrgDetail(
           List<String> orgIds, String accountOrgUrl, Map<String, String> tokenHeader);
}
