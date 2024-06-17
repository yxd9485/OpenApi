package com.fenbeitong.openapi.plugin.yunzhijia.service.impl.sync;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.service.ThirdEmployeePostProcessService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdExpandFieldConfigDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdExpandFieldConfig;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnitManagers;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenDepartmentServiceImpl;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenSyncThirdOrgServiceImpl;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.RandomUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseCode;
import com.fenbeitong.openapi.plugin.yunzhijia.constant.YunzhijiaResourceLevelConstant;
import com.fenbeitong.openapi.plugin.yunzhijia.dao.YunzhijiaAddressListDao;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.*;
import com.fenbeitong.openapi.plugin.yunzhijia.entity.YunzhijiaAddressList;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaCorpAppService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaEmployeeService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.org.YunzhijiaOrgServiceImpl;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.org.YunzhijiaRemoteOrgService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.token.YunzhijiaTokenService;
import com.fenbeitong.openapi.sdk.dto.organization.UpdateOrgUnitLeaderReqDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@ServiceAspect
@Service
public class YunzhijiaSyncService {

    @Autowired
    YunzhijiaOrgServiceImpl yunzhijiaOrgService;
    @Autowired
    IYunzhijiaEmployeeService yunzhijiaEmployeeService;
    @Autowired
    private OpenSyncThirdOrgServiceImpl openSyncThirdOrgService;
    @Autowired
    YunzhijiaRemoteOrgService yunzhijiaRemoteOrgService;
    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;
    @Autowired
    IYunzhijiaCorpAppService yunzhijiaCorpAppService;
    @Autowired
    YunzhijiaTokenService yunzhijiaTokenService;
    @Autowired
    YunzhijiaAddressListDao yunzhijiaAddressListDao;
    @Autowired
    private OpenThirdExpandFieldConfigDao expandFieldConfigDao;
    @Autowired
    OpenEmployeeServiceImpl openEmployeeService;
    @Autowired
    OpenDepartmentServiceImpl openDepartmentService;

    @Autowired
    private ThirdEmployeePostProcessService postProcessService;

    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    public String syncOrgEmployee(String companyId) {
        PluginCorpDefinition corpDefinition = pluginCorpDefinitionDao.getByCompanyId(companyId);
        //分贝公司id
        String thirdCorpId = corpDefinition.getThirdCorpId();
        YunzhijiaAddressList yunzhijiaToken = yunzhijiaTokenService.getYunzhijiaToken(thirdCorpId);
        YunzhijiaAccessTokenReqDTO build = YunzhijiaAccessTokenReqDTO.builder()
                .secret(yunzhijiaToken.getCorpSecret())
                .scope(YunzhijiaResourceLevelConstant.RES_GROUP_SECRET)
                .eid(corpDefinition.getThirdCorpId())
                .timestamp(System.currentTimeMillis())
                .build();
        //返回云之家access_token
        YunzhijiaResponse<YunzhijiaAccessTokenRespDTO> yunzhijiaAccessTokenRespDTO = yunzhijiaTokenService.getYunzhijiaRemoteAccessToken(build);
        if (ObjectUtils.isEmpty(yunzhijiaAccessTokenRespDTO)) {
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_TOKEN_ERROR)));
        }
        if (RespCode.SUCCESS != yunzhijiaAccessTokenRespDTO.getErrorCode()) {
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_TOKEN_ERROR)));
        }
        if (ObjectUtils.isEmpty(yunzhijiaAccessTokenRespDTO.getData())) {
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_TOKEN_ERROR)));
        }
        String accessToken = yunzhijiaAccessTokenRespDTO.getData().getAccessToken();
        if (StringUtils.isBlank(accessToken)) {
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_TOKEN_ERROR)));
        }
        //获取云之家全量部门
        List<YunzhijiaOrgRespDTO.YunzhijiaOrgDTO> yunzhijiaDepartmentList = yunzhijiaOrgService.getYunzhijiaDepartmentList(accessToken, thirdCorpId);
        //获取飞书全量人员
        YunzhijiaEmployeeReqDTO build1 = YunzhijiaEmployeeReqDTO.builder()
                .eid(thirdCorpId)
                .build();
        List<YunzhijiaEmployeeDTO> yunzhijiaEmployeeList = yunzhijiaEmployeeService.getYunzhijiaEmployeeList(accessToken, build1);
        //获取第三方根部门ID
        String rootDeptId = "";
        //转换部门
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        // 查询是否需要修改用户
        OpenThirdScriptConfig employeeConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.EMPLOYEE_SYNC);
        for (YunzhijiaOrgRespDTO.YunzhijiaOrgDTO yunzhijiaOrgDTO : yunzhijiaDepartmentList) {
            if (StringUtils.isNotBlank(yunzhijiaOrgDTO.getParentId())) {//为根部门
                OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
                openThirdOrgUnitDTO.setCompanyId(companyId);
                //全路径部门名称
                String fullOrgName = yunzhijiaOrgDTO.getDepartment();
                if (fullOrgName.contains("\\")) {//二级部门
                    fullOrgName = fullOrgName.replaceAll("\\\\", "/");
                }
                fullOrgName = corpDefinition.getAppName() + "/" + fullOrgName;
                openThirdOrgUnitDTO.setThirdOrgUnitFullName(fullOrgName);
                openThirdOrgUnitDTO.setThirdOrgUnitName(yunzhijiaOrgDTO.getName());
                openThirdOrgUnitDTO.setThirdOrgUnitParentId(yunzhijiaOrgDTO.getParentId());
                openThirdOrgUnitDTO.setThirdOrgUnitId(yunzhijiaOrgDTO.getId());
                departmentList.add(openThirdOrgUnitDTO);
            } else {
                rootDeptId = yunzhijiaOrgDTO.getId();
            }
        }
        departmentList.sort((OpenThirdOrgUnitDTO h1, OpenThirdOrgUnitDTO h2) -> h1.getThirdOrgUnitFullName().compareTo(h2.getThirdOrgUnitFullName()));
        OpenThirdExpandFieldConfig expandFieldConfig = expandFieldConfigDao.getByCompanyId(companyId);
        String userExpandFields = expandFieldConfig == null ? null : expandFieldConfig.getUserExpandFields();

        //转换人员
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        for (YunzhijiaEmployeeDTO userInfo : yunzhijiaEmployeeList) {
            if (userInfo.getStatus() == 1) {//正常状态
                OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
                openThirdEmployeeDTO.setCompanyId(companyId);
                if (StringUtils.isBlank(userInfo.getDepartment())) {//在根部门下的人员数据
                    openThirdEmployeeDTO.setThirdDepartmentId(rootDeptId);
                } else {
                    openThirdEmployeeDTO.setThirdDepartmentId(userInfo.getOrgId());
                }
                openThirdEmployeeDTO.setThirdEmployeeId(userInfo.getOpenId());
                openThirdEmployeeDTO.setThirdEmployeeName(userInfo.getName());
                openThirdEmployeeDTO.setThirdEmployeeEmail(userInfo.getEmail());
                openThirdEmployeeDTO.setThirdEmployeePhone(userInfo.getPhone());
                openThirdEmployeeDTO.setThirdEmployeeGender(userInfo.getGender());


                if (!ObjectUtils.isEmpty(userExpandFields)) {
                    Map map = JsonUtils.toObj(JsonUtils.toJson(userInfo), Map.class);
                    Map expandJson = Maps.newHashMap();
                    Lists.newArrayList(userExpandFields.split(",")).forEach(field -> {
                        expandJson.put(field, map.get(field));
                    });
                    openThirdEmployeeDTO.setExtAttr(expandJson);
                }
                String contactStr = userInfo.getContact();
                if (StringUtils.isNotBlank(contactStr)) {
                    List<YunzhijiaEmployeeContactDTO> list = JsonUtils.toObj(contactStr, new TypeReference<List<YunzhijiaEmployeeContactDTO>>() {
                    });
                    if (!ObjectUtils.isEmpty(list)) {
                        list.stream().forEach(contact -> {
                            if (contact.getName().trim().equals("分贝权限")) {
                                String fbRoleType = contact.getValue();
                                openThirdEmployeeDTO.setThirdEmployeeRoleTye(fbRoleType);
                            }
                        });
                    }
                }
                OpenThirdEmployeeDTO newOpenThirdEmployeeDTO = postProcessService.process(openThirdEmployeeDTO, userInfo, companyId,employeeConfig);
                if ( null != newOpenThirdEmployeeDTO ){
                    employeeList.add(newOpenThirdEmployeeDTO);
                }
            }
        }
        //同步
        openSyncThirdOrgService.syncThird(OpenType.YUNZHIJIA.getType(), companyId, departmentList, employeeList);
        //获取所有部门主管
        List<YunzhijiaOrgLeaderDTO> yunzhijiaRemoteAllOrgLeaders = yunzhijiaOrgService.getYunzhijiaRemoteAllOrgLeaders(thirdCorpId);
        // 同步部门主管
        setDepManagers(yunzhijiaRemoteAllOrgLeaders,companyId);
        return "successed";

    }

    private void setDepManagers(List<YunzhijiaOrgLeaderDTO> yunzhijiaRemoteAllOrgLeaders, String companyId) {
        if (!ObjectUtils.isEmpty(yunzhijiaRemoteAllOrgLeaders)) {
            List<OpenThirdOrgUnitManagers> newOpenThirdOrgUnitManagersList = new ArrayList<>();
            yunzhijiaRemoteAllOrgLeaders.forEach(yunzhijiaOrgLeaderDTO -> {
                newOpenThirdOrgUnitManagersList.add(OpenThirdOrgUnitManagers.builder()
                        .id(RandomUtils.bsonId())
                        .companyId(companyId)
                        .thirdEmployeeIds(yunzhijiaOrgLeaderDTO.getOpenId())
                        .thirdOrgUnitId(yunzhijiaOrgLeaderDTO.getDepartmentId())
                        .status(0)
                        .createTime(new Date())
                        .updateTime(new Date())
                        .build());
            });
            openSyncThirdOrgService.setAllDepManageV2(newOpenThirdOrgUnitManagersList, companyId);
        }
    }

}
