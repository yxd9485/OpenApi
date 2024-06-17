package com.fenbeitong.openapi.plugin.yunzhijia.service;

import com.fenbeitong.openapi.plugin.yunzhijia.dto.*;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.org.YunzhijiaOrgServiceImpl;

import java.util.List;
import java.util.Map;

public interface IYunzhijiaOrgService {

    YunzhijiaResponse<List<YunzhijiaOrgDTO>> getYunzhijiaOrgDetail(YunzhijiaOrgReqDTO yunzhijiaOrgReqDTO);

    YunzhijiaResponse<YunzhijiaOrgInChargeDTO>  getYunzhijiaRemoteOrgBaseOrLeaderDetail(YunzhijiaOrgReqDTO yunzhijiaOrgReqDTO);

    Map<String, Object> syncYunzhijiaOrgUnit(String yunzhijiaToken, String corpId, String deptId);

    void addOrgUnit(String companyId, List<YunzhijiaOrgServiceImpl.YunzhijiaOrgUnitAdd> addOrgList);

    void updateOrgUnit(String companyId, List<YunzhijiaOrgServiceImpl.YunzhijiaOrgUnitUpdate> updateOrgList);

    void deleteOrgUnit(List<YunzhijiaOrgServiceImpl.YunzhijiaOrgUnitDelete> deleteOrgList);


}


