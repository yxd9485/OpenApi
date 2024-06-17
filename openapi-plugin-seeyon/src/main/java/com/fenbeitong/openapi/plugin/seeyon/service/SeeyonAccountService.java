package com.fenbeitong.openapi.plugin.seeyon.service;

import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountOrgListResp;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountParam;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountResp;

import java.util.List;
import java.util.Map;

public interface SeeyonAccountService {

    String getAccountId(SeeyonAccountParam accountParam, String accountIdUrl, Map<String, String> tokenHeader);
    String getAccountCode(SeeyonAccountParam accountParam, String accountIdUrl, Map<String, String> tokenHeader);
    List<SeeyonAccountOrgListResp> getOrgAccounts(String accountIdUrl, Map<String, String> tokenHeader);
    SeeyonAccountResp getAccountInfoByName(SeeyonAccountParam accountParam, String accountIdUrl, Map<String, String> tokenHeader);
}
