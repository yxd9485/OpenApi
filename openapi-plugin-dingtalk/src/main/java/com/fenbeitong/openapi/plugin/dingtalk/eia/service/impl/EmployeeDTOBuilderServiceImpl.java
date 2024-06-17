package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.dingtalk.api.response.OapiSmartworkHrmEmployeeV2ListResponse;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkCorpAppService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkRosterInfoService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IEmployeeDTOBuilderService;
import com.fenbeitong.openapi.plugin.support.apply.constant.ItemCodeEnum;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpAppDefinition;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangpeng
 * @date 2022/3/24 2:29 下午
 */
@ServiceAspect
@Service
@Slf4j
public class EmployeeDTOBuilderServiceImpl implements IEmployeeDTOBuilderService {

    @Autowired
    private IDingtalkRosterInfoService iDingtalkRosterInfoService;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Autowired
    private IDingtalkCorpAppService dingtalkCorpAppService;

    @Override
    public Map<String,Map<String,String>> getRouterInfo(String companyId , String thirdCorpId , List<String> userIds ){
        try {
            // 查询配置 , 是否有花名册配置
            OpenMsgSetup openMsgSetup = openMsgSetupDao.selectByCompanyIdAndItemCode(companyId, ItemCodeEnum.DINGTALK_ROSTER_CONFIG.getCode());
            if ( null == openMsgSetup ){
                return new HashMap<>();
            }
            // 花名册查询字段
            String rosterInfo = openMsgSetup.getStrVal1();
            if (StringUtils.isBlank(rosterInfo)){
                return new HashMap<>();
            }
            PluginCorpAppDefinition corpAppDefinition = dingtalkCorpAppService.getByCorpId(thirdCorpId);
            if ( null == corpAppDefinition ){
                log.info("查询花名册 dingtalk_corp_app 未配置 ");
                return new HashMap<>();
            }
            // 员工id和花名册信息映射
            Map<String,Map<String,String>> userId2RosterInfoMap = new HashMap<>();
            // 钉钉花名册信息 , 最多传 100 个人员
            CollectionUtils.batch(userIds, 100).forEach(batch->{
                OapiSmartworkHrmEmployeeV2ListResponse oapiSmartworkHrmEmployeeV2ListResponse = iDingtalkRosterInfoService.getHrmEmployeeList(thirdCorpId, org.apache.commons.lang3.StringUtils.strip(batch.toString(),"[]"),rosterInfo,corpAppDefinition.getThirdAgentId());
                List<OapiSmartworkHrmEmployeeV2ListResponse.EmpRosterFieldVo> empRosterFieldVos = oapiSmartworkHrmEmployeeV2ListResponse.getResult();
                if (CollectionUtils.isNotBlank(empRosterFieldVos)){
                    // 处理花名信息,做映射
                    for (OapiSmartworkHrmEmployeeV2ListResponse.EmpRosterFieldVo empRosterFieldVo : empRosterFieldVos) {
                        if (CollectionUtils.isBlank(empRosterFieldVo.getFieldDataList())){
                            continue;
                        }
                        // 花名册的字段信息映射
                        Map<String,String> field2ValueMap = new HashMap<>();
                        empRosterFieldVo.getFieldDataList().forEach(empFieldDataVo -> {
                            field2ValueMap.put(empFieldDataVo.getFieldCode(),CollectionUtils.isBlank(empFieldDataVo.getFieldValueList()) ? "" : empFieldDataVo.getFieldValueList().get(0).getValue());
                        });
                        userId2RosterInfoMap.put(empRosterFieldVo.getUserid(),field2ValueMap);
                    }
                }
            });
            return userId2RosterInfoMap;
        } catch (Exception e){
            log.info("钉钉花名册数据转化异常 {} ",e.getMessage());
            return new HashMap<>();
        }
    }
}
