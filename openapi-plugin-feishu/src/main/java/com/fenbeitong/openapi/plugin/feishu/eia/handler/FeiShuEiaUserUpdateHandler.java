package com.fenbeitong.openapi.plugin.feishu.eia.handler;

import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseCode;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.organizationv3.FeiShuSingleUserDetailRespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuUserInfoDTO;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuException;
import com.fenbeitong.openapi.plugin.feishu.eia.service.FeiShuEiaEmployeeService;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdEmployeeDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdExpandFieldConfigDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitManagersDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdEmployee;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdExpandFieldConfig;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.fenbeitong.openapi.plugin.support.util.PhoneCheckUtil;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lizhen
 * @date 2020/6/4
 */
@Component
@Slf4j
public class FeiShuEiaUserUpdateHandler implements ITaskHandler {
    @Autowired
    private FeiShuEiaEmployeeService feiShuEiaEmployeeService;
    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;
    @Autowired
    private FeiShuEiaUserAddHandler feiShuEiaUserAddHandler;
    @Autowired
    private OpenThirdEmployeeDao openThirdEmployeeDao;
    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;
    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;
    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;
    @Autowired
    private OpenThirdExpandFieldConfigDao expandFieldConfigDao;
    @Autowired
    private OpenThirdOrgUnitManagersDao openThirdOrgUnitManagersDao;
    // 身份证号
    private static final String ID_NO = "id_no";

    @Override
    public TaskType getTaskType() {
        return TaskType.FEISHU_EIA_UPDATE_USER;
    }

    @Override
    public TaskResult execute(Task task) {
        //1.解析task
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        //2.检查企业是否注册
        PluginCorpDefinition pluginCorpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        if (pluginCorpDefinition == null) {
            log.info("企业不存在，任务丢弃，taskId={}", task.getId());
            return TaskResult.EXPIRED;
        }
        String companyId = pluginCorpDefinition.getAppId();
        //3.调用飞书获取用用户信息
        FeiShuUserInfoDTO userInfo = null;
        try {
            userInfo = feiShuEiaEmployeeService.getSingleUserDetailInfo(FeiShuConstant.EIA_USER_ID_TYPE, dataId, corpId).buildSingleOldDTO();
        } catch (OpenApiFeiShuException e) {
            if (e.getCode() == NumericUtils.obj2int(FeiShuResponseCode.FEISHU_ISV_USER_BATCH_GET_FAILED)) {
                //4.如果在飞书查不到，标记为已处理
                return TaskResult.EXPIRED;
            }
            throw e;
        }
        // 如果更新的人员已离职则放弃任务。用户状态，bit0(最低位): 1冻结，0未冻结；bit1:1离职，0在职；bit2:1未激活，0已激活
        if ((userInfo.getStatus() >> 1 & 1) == 1) {
            return TaskResult.ABORT;
        }

        //人员追加飞书花名册信息
        ArrayList<FeiShuUserInfoDTO> userInfoList = Lists.newArrayList();
        userInfoList.add(userInfo);
        feiShuEiaEmployeeService.getAddFieldFeiShuUserInfoDTOS(corpId, userInfoList, companyId);

        //4.查询分贝通员工信息
        List<OpenThirdEmployee> srcEmployeeList = openThirdEmployeeDao.listEmployeeByThirdEmployeeId(OpenType.FEISHU_EIA.getType(), companyId, Lists.newArrayList(dataId));
        //5.如果员工不存在，则添加
        if (srcEmployeeList == null || srcEmployeeList.size() == 0) {
            return feiShuEiaUserAddHandler.execute(task);
        }
        //6.转换人员
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
        openThirdEmployeeDTO.setCompanyId(companyId);
        if (!ObjectUtils.isEmpty(userInfo.getDepartments())) {
            openThirdEmployeeDTO.setThirdDepartmentId(userInfo.getDepartments().get(0));
        } else {
            //无部门信息的放根部门
            openThirdEmployeeDTO.setThirdDepartmentId("0");
        }
        openThirdEmployeeDTO.setThirdEmployeeId(userInfo.getEmployeeId());
        openThirdEmployeeDTO.setThirdEmployeeName(userInfo.getName());
        openThirdEmployeeDTO.setThirdEmployeeEmail(userInfo.getEmail());
        openThirdEmployeeDTO.setThirdEmployeePhone(PhoneCheckUtil.getMobileWithoutCountryCode(userInfo.getMobile()));
        openThirdEmployeeDTO.setThirdEmployeeGender(userInfo.getGender());
        openThirdEmployeeDTO.setEmployeeNumber(userInfo.getEmployeeNo());
        if ("0".equals(openThirdEmployeeDTO.getThirdDepartmentId())) {
            openThirdEmployeeDTO.setThirdDepartmentId(corpId);
        }

        List<OpenMsgSetup> feishuFbtPriv = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList("company_feishu_fbt_priv"));
        String fbtPrivIdKey = "";
        if (!ObjectUtils.isEmpty(feishuFbtPriv)) {
            OpenMsgSetup openMsgSetup = feishuFbtPriv.get(0);
            Integer intVal1 = openMsgSetup.getIntVal1();
            if (intVal1 == 1) {//获取分贝通权限字段ID,用于获取飞书分贝通权限
                fbtPrivIdKey = openMsgSetup.getStrVal1();
            }
        }
        if (StringUtils.isNotBlank(fbtPrivIdKey)) {//不为空，进行分贝通权限字段设置
            Map<String, Object> customAttrs = userInfo.getCustomAttrs();
            if (!ObjectUtils.isEmpty(customAttrs)) {//如果人员没有配置分贝通权限，则拉取的详情数据中不会包含自定义字段内容
                Map fbtPrivMap = (Map) customAttrs.get(fbtPrivIdKey);
                if (!ObjectUtils.isEmpty(fbtPrivMap)) {
                    String fbtPriv = com.fenbeitong.openapi.plugin.util.StringUtils.obj2str(fbtPrivMap.get("value"));
                    if (StringUtils.isNotBlank(fbtPriv)) {//如果没有填写分贝通权限字段，则默认不进行权限同步，全部关闭
                        openThirdEmployeeDTO.setThirdEmployeeRoleTye(fbtPriv);
                    }
                }
            }
        }
        //人员扩展字段
        Map<String, Object> customAttrs = userInfo.getCustomAttrs();
        if (!ObjectUtils.isEmpty(customAttrs)) {
            //扩展字段配置
            OpenThirdExpandFieldConfig expandFieldConfig = expandFieldConfigDao.getByCompanyId(companyId);
            String userExpandFields = expandFieldConfig == null ? null : expandFieldConfig.getUserExpandFields();
            Map map = JsonUtils.toObj(userExpandFields, Map.class);
            if (!ObjectUtils.isEmpty(map)) {
                Map expandJson = Maps.newHashMap();
                map.forEach((k, v) -> {
                    // 身份证号不加到自定义字段里
                    // 自定义字段里有身份证号,则设置到分贝通
                    if (ID_NO.equals(v) && (customAttrs.get(k) != null)) {
                        Map valueInfo = customAttrs.get(k) == null ? null : (Map) customAttrs.get(k);
                        String value = valueInfo == null ? "" : (String) valueInfo.get("value");
                        openThirdEmployeeDTO.setThirdEmployeeIdCard(value);
                    }
                });
                openThirdEmployeeDTO.setExtAttr(expandJson);
            }
        }

        // 先查一下配置表 看是否需要过滤
        OpenThirdScriptConfig employeeConfig = openThirdScriptConfigDao.getEmployeeConfig(companyId);
        boolean employeeNeedFilter = employeeConfig != null;
        OpenThirdEmployeeDTO targetDTO = null;
        if (employeeNeedFilter) {
            targetDTO = feiShuEiaEmployeeService.employeeBeforeSyncFilter(employeeConfig, userInfo, openThirdEmployeeDTO);
        }
        employeeList.add(targetDTO != null ? targetDTO : openThirdEmployeeDTO);
        //7.同步
        openSyncThirdOrgService.updateEmployee(OpenType.FEISHU_EIA.getType(), companyId, employeeList);
        // 删除部门中间表
        openThirdOrgUnitManagersDao.deleteDepManagers(srcEmployeeList.get(0).getThirdDepartmentId(), openThirdEmployeeDTO, companyId);

        return TaskResult.SUCCESS;
    }

}
