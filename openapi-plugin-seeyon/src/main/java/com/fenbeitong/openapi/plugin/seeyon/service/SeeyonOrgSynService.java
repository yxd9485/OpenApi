package com.fenbeitong.openapi.plugin.seeyon.service;

import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonOrgNameReq;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonClient;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOpenMsgSetup;

/**
 * 致远组织机构同步
 * @Auther zhang.peng
 * @Date 2021/7/23
 */
public interface SeeyonOrgSynService {

    void doOrgSyn(String orgName , SeeyonClient seeyonClient, SeeyonOrgNameReq seeyonOrgNameReq, SeeyonOpenMsgSetup seeyonCompanySetup, String seeyonAccountOrgEmpKey);
}
