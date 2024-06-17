package com.fenbeitong.openapi.plugin.yunzhijia.service.impl;

import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpAppDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.organization.AbstractOrganizationService;
import com.fenbeitong.openapi.plugin.yunzhijia.constant.YunzhijiaPullOrgConstant;
import com.fenbeitong.openapi.plugin.yunzhijia.constant.YunzhijiaResourceLevelConstant;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaAccessTokenRespDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaAccessTokenReqDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaResponse;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaCorpAppService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaOrgService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.org.YunzhijiaOrgServiceImpl;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.token.YunzhijiaTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

@ServiceAspect
@Service
@Slf4j
public class YunzhijiaPullOrgService extends AbstractOrganizationService {

    @Autowired
    YunzhijiaTokenService yunzhijiaTokenService;
    @Autowired
    IYunzhijiaOrgService yunzhijiaOrgService;
    @Autowired
    IYunzhijiaCorpAppService yunzhijiaCorpAppService;
    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    /**
     * 定时执行同步云之家部门数据到分贝通
     *
     * @param corpId
     * @param deptId
     * @return
     */
    public String pullThirdOrg(String corpId, String deptId) {
        long start = System.currentTimeMillis();
        PluginCorpDefinition corpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        //公司id
        String companyId = corpDefinition.getAppId();
        deptId = ObjectUtils.isEmpty(deptId) ? "1" : deptId;
        //构造云之家access_token请求参数
        PluginCorpAppDefinition byCorpId = yunzhijiaCorpAppService.getByCorpId(corpDefinition.getThirdCorpId());
        YunzhijiaAccessTokenReqDTO build = YunzhijiaAccessTokenReqDTO.builder()
                .secret(byCorpId.getThirdAppKey())
                .scope(YunzhijiaResourceLevelConstant.APP)
                .eid(corpDefinition.getThirdCorpId())
                .timestamp(System.currentTimeMillis())
                .appId(String.valueOf(byCorpId.getThirdAgentId()))
                .build();
        //返回云之家access_token
        YunzhijiaResponse<YunzhijiaAccessTokenRespDTO> yunzhijiaAccessTokenRespDTO = yunzhijiaTokenService.getYunzhijiaRemoteAccessToken(build);
        if(yunzhijiaAccessTokenRespDTO.getErrorCode() == RespCode.SUCCESS){
            String accessToken = yunzhijiaAccessTokenRespDTO.getData().getAccessToken();
            Map<String, Object> departmentMap = yunzhijiaOrgService.syncYunzhijiaOrgUnit(accessToken, corpId, deptId);
            //删除部门
            deleteDepartment(departmentMap);
            //更新或增加部门
            upsertDepartment(companyId, departmentMap);
            long end = System.currentTimeMillis();
            log.info("云之家部门同步完成，用时{}分钟...", (end - start) / 60000);
            return "Success";
        }
        return "Failed";
    }


    private void upsertDepartment(String companyId, Map<String, Object> departmentMap) {
        //需要新建的部门
        List<YunzhijiaOrgServiceImpl.YunzhijiaOrgUnitAdd> addOrgList = (List<YunzhijiaOrgServiceImpl.YunzhijiaOrgUnitAdd>) departmentMap.get(YunzhijiaPullOrgConstant.INSERT);
        yunzhijiaOrgService.addOrgUnit(companyId, addOrgList);
        //需要更新的部门
        List<YunzhijiaOrgServiceImpl.YunzhijiaOrgUnitUpdate> updateOrgList = (List<YunzhijiaOrgServiceImpl.YunzhijiaOrgUnitUpdate>) departmentMap.get(YunzhijiaPullOrgConstant.UPDATE);
        yunzhijiaOrgService.updateOrgUnit(companyId, updateOrgList);
    }

    private void deleteDepartment(Map<String, Object> departmentMap) {
        //需要删除的部门
        List<YunzhijiaOrgServiceImpl.YunzhijiaOrgUnitDelete> deleteOrgList = (List<YunzhijiaOrgServiceImpl.YunzhijiaOrgUnitDelete>) departmentMap.get(YunzhijiaPullOrgConstant.DELETE);
        //删除部门
        yunzhijiaOrgService.deleteOrgUnit(deleteOrgList);
    }


}
