package com.fenbeitong.openapi.plugin.task.feishu.isv;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseCode;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuUserInfoDTO;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuException;
import com.fenbeitong.openapi.plugin.feishu.isv.entity.FeishuIsvCompany;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvEmployeeService;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvOrganizationService;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigCode;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdEmployeeDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdExpandFieldConfigDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdEmployee;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdExpandFieldConfig;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.privilege.service.OpenEmployeePrivService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户新增事件
 *
 * @author lizhen
 */
@Component
@Slf4j
public class FeiShuIsvUserAddProcessor extends AbstractTaskProcessor {
    @Autowired
    private FeiShuIsvCompanyDefinitionService feiShuIsvCompanyDefinitionService;
    @Autowired
    private FeiShuIsvEmployeeService feiShuIsvEmployeeService;
    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;
    @Autowired
    private FeiShuIsvUserUpdateProcessor feiShuIsvUserUpdateHandler;
    @Autowired
    private OpenThirdEmployeeDao openThirdEmployeeDao;

    @Autowired
    private OpenEmployeePrivService openEmployeePrivService;

    @Autowired
    private FeiShuIsvOrganizationService feiShuIsvOrganizationService;

    @Autowired
    private OpenThirdExpandFieldConfigDao expandFieldConfigDao;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    @Override
    public Integer getTaskType() {
        return TaskType.FEISHU_ISV_CREATE_USER.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) {
        //1.解析task
        String corpId = task.getCompanyId();
        String dataId = task.getDataId();
        //2.检查企业是否注册
        FeishuIsvCompany feishuIsvCompany = feiShuIsvCompanyDefinitionService.getFeiShuIsvCompanyByCorpId(corpId);
        if (feishuIsvCompany == null) {
            return TaskProcessResult.success("检查企业未注册 success");
        }
        String companyId = feishuIsvCompany.getCompanyId();
        //3.调用飞书获取用用户信息
        FeiShuUserInfoDTO userInfo = null;
        try {
            userInfo = feiShuIsvEmployeeService.getUserInfo(FeiShuConstant.ID_TYPE_OPEN_ID, dataId, corpId);
        } catch (OpenApiFeiShuException e) {
            if (e.getCode() == NumericUtils.obj2int(FeiShuResponseCode.FEISHU_ISV_USER_BATCH_GET_FAILED)) {
                //4.如果在飞书查不到，标记为已处理
                return TaskProcessResult.success("如果在飞书查不到，标记为已处理 success");
            }
            throw e;
        }
        if (userInfo == null) {
            return TaskProcessResult.success("userInfo is null success");
        }
        //4.查询分贝通员工信息
        List<OpenThirdEmployee> srcEmployeeList = openThirdEmployeeDao.listEmployeeByThirdEmployeeId(OpenType.FEISHU_ISV.getType(), companyId, Lists.newArrayList(dataId));
        //5.如果员工已存在，则更新
        if (srcEmployeeList != null && srcEmployeeList.size() > 0) {
            return feiShuIsvUserUpdateHandler.process(task);
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
        openThirdEmployeeDTO.setThirdEmployeeId(userInfo.getOpenId());
        openThirdEmployeeDTO.setThirdEmployeeName(userInfo.getName());
        openThirdEmployeeDTO.setThirdEmployeeEmail(userInfo.getEmail());
        openThirdEmployeeDTO.setThirdEmployeePhone(userInfo.getMobile());
        openThirdEmployeeDTO.setThirdEmployeeGender(userInfo.getGender());
        if ("0".equals(openThirdEmployeeDTO.getThirdDepartmentId())) {
            openThirdEmployeeDTO.setThirdDepartmentId(corpId);
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
                    String key = k + ":value";
                    String value = StringUtils.obj2str(MapUtils.getValueByExpress(customAttrs, key));
                    if (!StringUtils.isBlank(value)) {
                        expandJson.put(v, value);
                    }
                });
                openThirdEmployeeDTO.setExtAttr(expandJson);
            }
        }
        String initRoleTypeFlag = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.CODE_ISV_INIT_ROLE_TYPE_FLAG.getCode());
        if ("true".equals(initRoleTypeFlag)) {
            //初始化权限
            openEmployeePrivService.initRoleType("1", OpenSysConfigCode.CODE_ISV_INIT_ROLE_TYPE.getCode(), companyId);
        }
        // 先查一下配置表 看是否需要过滤
        OpenThirdScriptConfig employeeConfig = openThirdScriptConfigDao.getEmployeeConfig(companyId);
        boolean employeeNeedFilter = employeeConfig != null;
        OpenThirdEmployeeDTO targetDTO = null;
        if (employeeNeedFilter) {
            targetDTO = feiShuIsvEmployeeService.employeeBeforeSyncFilter(employeeConfig, userInfo, openThirdEmployeeDTO);
        }
        employeeList.add(targetDTO != null ? targetDTO : openThirdEmployeeDTO);

        //7.同步
        openSyncThirdOrgService.addEmployee(OpenType.FEISHU_ISV.getType(), companyId, employeeList);
        return TaskProcessResult.success("success");
    }
}
