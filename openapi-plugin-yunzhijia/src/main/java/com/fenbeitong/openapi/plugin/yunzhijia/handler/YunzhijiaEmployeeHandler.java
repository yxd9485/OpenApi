package com.fenbeitong.openapi.plugin.yunzhijia.handler;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdExpandFieldConfigDao;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdExpandFieldConfig;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseCode;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaEmployeeDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaEmployeeReqDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaResponse;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaCorpService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaEmployeeService;
import com.fenbeitong.openapi.sdk.dto.employee.EmployeeInsertDTO;
import com.fenbeitong.openapi.sdk.dto.employee.EmployeeUpdateDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class YunzhijiaEmployeeHandler {
    @Autowired
    IYunzhijiaCorpService yunzhijiaCorpService;
    @Autowired
    IYunzhijiaEmployeeService yunzhijiaEmployeeService;
    @Autowired
    private OpenThirdExpandFieldConfigDao expandFieldConfigDao;

    /**
     * 根据企业ID查询插件注册信息
     *
     * @param corpId
     * @return
     */
    public PluginCorpDefinition getPluginCorpDefinitionByCorpId(String corpId) {
        //2.检查企业是否注册
        PluginCorpDefinition byCorpId = yunzhijiaCorpService.getByCorpId(corpId);
        if (ObjectUtils.isEmpty(byCorpId)) {
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_CORP_UN_REGIST)));
        }
        return byCorpId;
    }

    /**
     * 查询云之家部门详情
     *
     * @param corpId
     * @param dataId
     * @return
     */
    public YunzhijiaResponse<List<YunzhijiaEmployeeDTO>> getYunzhijiaEmp(String corpId, String dataId) {
        List<String> openIdList = Lists.newArrayList();
        openIdList.add(dataId);
        YunzhijiaEmployeeReqDTO build = YunzhijiaEmployeeReqDTO.builder()
                .eid(corpId)
                .type(1)
                .array(openIdList)
                .build();
        YunzhijiaResponse<List<YunzhijiaEmployeeDTO>> yunzhijiaEmployeeDetail = yunzhijiaEmployeeService.getYunzhijiaEmployeeDetail(build);
        if (ObjectUtils.isEmpty(yunzhijiaEmployeeDetail) || yunzhijiaEmployeeDetail.getData().size() < 0) {
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_EMPLOYEE_NULL)));
        }
        return yunzhijiaEmployeeDetail;
    }


    public String checkExpandFieldConfig(String companyId) {
        OpenThirdExpandFieldConfig expandFieldConfig = expandFieldConfigDao.getByCompanyId(companyId);
        String userExpandFields = expandFieldConfig == null ? null : expandFieldConfig.getUserExpandFields();
        return userExpandFields;
    }

    public void setExpandJson(String userExpandFields, YunzhijiaEmployeeDTO yunzhijiaEmployeeDTO, EmployeeInsertDTO employeeInsertDTO, EmployeeUpdateDTO employeeUpdateDTO) {
        if (!ObjectUtils.isEmpty(userExpandFields)) {
            Map map = JsonUtils.toObj(JsonUtils.toJson(yunzhijiaEmployeeDTO), Map.class);
            Map sourceExpandJson = Maps.newHashMap();
            com.google.common.collect.Lists.newArrayList(userExpandFields.split(",")).forEach(field -> {
                sourceExpandJson.put(field, map.get(field));
            });

            if (!ObjectUtils.isEmpty(sourceExpandJson)) {
                HashMap<String, Object> targetMap = Maps.newHashMap();
                //取出自定义map值，进行字段扩展，一个扩展成三个，人员自定义会包含三种角色，下单人，使用人，同行/住人
                Set<String> keySet = sourceExpandJson.keySet();
                for (String key : keySet) {
                    targetMap.put(key + "1", sourceExpandJson.get(key));
                    targetMap.put(key + "2", sourceExpandJson.get(key));
                    targetMap.put(key + "3", sourceExpandJson.get(key));
                }
                List<Map> maps = com.google.common.collect.Lists.newArrayList(targetMap);
                if (!ObjectUtils.isEmpty(maps)) {
                    if (!ObjectUtils.isEmpty(employeeInsertDTO)) {
                        employeeInsertDTO.setExpandJson(JsonUtils.toJson(maps));
                    } else {
                        employeeUpdateDTO.setExpandJson(JsonUtils.toJson(maps));
                    }
                }
            }
//            employeeInsertDTO.setExpandJson(JsonUtils.toJson(sourceExpandJson));
        }
    }


}
