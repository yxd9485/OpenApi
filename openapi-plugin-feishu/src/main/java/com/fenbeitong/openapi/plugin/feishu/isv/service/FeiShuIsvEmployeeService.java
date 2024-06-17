package com.fenbeitong.openapi.plugin.feishu.isv.service;

import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.etl.service.impl.EtlUtils;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuDepartmentListDataDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuDepartmentSimpleListRespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuUserInfoDTO;
import com.fenbeitong.openapi.plugin.feishu.common.service.AbstractFeiShuEmployeeService;
import com.fenbeitong.openapi.plugin.feishu.common.service.AbstractFeiShuOrganizationService;
import com.fenbeitong.openapi.plugin.feishu.common.util.AbstractFeiShuHttpUtils;
import com.fenbeitong.openapi.plugin.feishu.isv.entity.FeishuIsvCompany;
import com.fenbeitong.openapi.plugin.feishu.isv.util.FeiShuIsvHttpUtils;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 飞书人员service
 *
 * @author lizhen
 * @date 2020/6/1
 */
@ServiceAspect
@Service
@Slf4j
public class FeiShuIsvEmployeeService extends AbstractFeiShuEmployeeService {

    @Autowired
    private FeiShuIsvHttpUtils feiShuIsvHttpUtils;

    @Autowired
    private FeiShuIsvCompanyDefinitionService feiShuIsvCompanyDefinitionService;

    @Autowired
    private FeiShuIsvOrganizationService feiShuIsvOrganizationService;


    /**
     * 全量同步部门人员
     *
     * @param corpId
     */
    public void syncFeiShuIsvOrgEmployee(String corpId, String companyId) {
        FeishuIsvCompany feishuIsvCompany = feiShuIsvCompanyDefinitionService.getFeiShuIsvCompanyByCorpId(corpId);
        syncOrgEmployee(OpenType.FEISHU_ISV.getType(),corpId,companyId, feishuIsvCompany.getCompanyName());
    }

    /**
     * 全量同步部门主管
     *
     * @param corpId
     */
    public void syncFeiShuIsvOrgManagers(String corpId, String companyId) {
        FeishuIsvCompany feishuIsvCompany = feiShuIsvCompanyDefinitionService.getFeiShuIsvCompanyByCorpId(corpId);
        syncThirdOrgManagers(companyId,corpId, feishuIsvCompany.getCompanyName());
    }

    @Override
    protected AbstractFeiShuHttpUtils getFeiShuHttpUtils() {
        return feiShuIsvHttpUtils;
    }

    @Override
    protected AbstractFeiShuOrganizationService getOrganizationService() {
        return feiShuIsvOrganizationService;
    }


//    /**
//     * 获取飞书的全量人员
//     * @param corpId
//     * @param companyName
//     * @return
//     */
//    public  List<FeiShuUserInfoDTO> getAllFeiShuUserInfo(String corpId,String companyName){
//        //获取飞书全量部门
//        List<FeiShuDepartmentSimpleListRespDTO.DepartmentInfo> departmentInfos = getOrganizationService().getAllDepartments(corpId, companyName);
//        //获取飞书全量人员
//        List<FeiShuUserInfoDTO> userInfos = getAllUserInfos(departmentInfos, corpId);
//        return userInfos;
//    }


//    /**
//     * 获取飞书的全量人员ID
//     * @param corpId
//     * @return
//     */
//    public  Set<String> getAllFeiShuUserOpenId(String corpId){
//        //获取飞书全量部门
//        List<String> departmentInfos = getOrganizationService().getFeishuOneDepartmentList(corpId);
//        //获取飞书全量人员
//        Set<String> userOpenIds = getAllUserOpenIds(departmentInfos, corpId);
//        return userOpenIds;
//    }

    /**
     * 获取飞书的全量人员ID
     * @param corpId
     * @return
     */
    public  Set<String> getAllFeiShuUserOpenId(String corpId){
        //获取飞书全量部门
        List<String> departmentInfos = getOrganizationService().getFeishuOneDepartmentList(corpId);
        //获取飞书全量人员
        Set<String> userOpenIds = getAllUserOpenIds(departmentInfos, corpId);
        return userOpenIds;
    }

    /**
     * 人员同步前按需过滤
     *
     * @param employeeConfig
     * @param userInfo
     * @param openThirdEmployeeDTO
     * @return
     */
    public OpenThirdEmployeeDTO employeeBeforeSyncFilter(OpenThirdScriptConfig employeeConfig, FeiShuUserInfoDTO userInfo, OpenThirdEmployeeDTO openThirdEmployeeDTO) {

        Map<String, Object> params = new HashMap<String, Object>() {{
            put("userInfo", userInfo);
            put("openThirdEmployeeDTO",openThirdEmployeeDTO);
        }};
        if (StringUtils.isNotBlank(employeeConfig.getParamJson()) && JsonUtils.toObj(employeeConfig.getParamJson(), Map.class) != null) {
            params.putAll(JsonUtils.toObj(employeeConfig.getParamJson(), Map.class));
        }
        return (OpenThirdEmployeeDTO) EtlUtils.execute(employeeConfig.getScript(), params);
    }

}
