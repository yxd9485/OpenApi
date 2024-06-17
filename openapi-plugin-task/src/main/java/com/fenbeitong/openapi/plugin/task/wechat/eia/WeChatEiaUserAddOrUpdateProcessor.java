package com.fenbeitong.openapi.plugin.task.wechat.eia;

import cn.hutool.core.util.ObjectUtil;
import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.support.common.dao.OpenExtInfoDao;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenExtInfo;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.employee.service.ThirdEmployeePostProcessService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdEmployeeDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdExpandFieldConfigDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdEmployee;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdExpandFieldConfig;
import com.fenbeitong.openapi.plugin.support.init.enums.EmployeeDefineEnum;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.eia.service.employee.WeChatEiaEmployeeService;
import com.fenbeitong.openapi.plugin.wechat.eia.service.wechat.WechatTokenService;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatGetUserResponse;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dave.hansins on 19/7/2.
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatEiaUserAddOrUpdateProcessor extends AbstractTaskProcessor {

    @Autowired
    private WechatTokenService wechatTokenService;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Autowired
    private WeChatEiaEmployeeService qywxEmployeeService;

    @Autowired
    private OpenThirdExpandFieldConfigDao expandFieldConfigDao;

    @Autowired
    private OpenThirdEmployeeDao openThirdEmployeeDao;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Autowired
    private OpenExtInfoDao openExtInfoDao;

    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    @Autowired
    private ThirdEmployeePostProcessService thirdEmployeePostProcessService;

    @Override
    public Integer getTaskType() {
        return TaskType.WECHAT_EIA_CREATE_OR_UPDATE_USER.getCode();
    }


    @Override
    public TaskProcessResult process(FinhubTask task) {
        String dataId = task.getDataId();
        String corpId = task.getCompanyId();
        String companyId = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId).getAppId();
        String qywxAccessToken = wechatTokenService.getWeChatContactTokenByCorpId(corpId);
        WeChatGetUserResponse wechatUser = null;
        // 查询是否需要修改用户
        OpenThirdScriptConfig employeeConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.EMPLOYEE_SYNC);
        try {
            wechatUser = qywxEmployeeService.getQywxUserDetailByUserId(qywxAccessToken, dataId, corpId);
        } catch (OpenApiWechatException e) {
            if (e.getCode() == NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_CORP_EMPLOYEE_IS_NULL)) {
                log.info("人员已经被删除,corpId={},userId={}", corpId, dataId);
                return TaskProcessResult.success("人员已经被删除 success");
            }
        }
        //扩展字段配置
        OpenThirdExpandFieldConfig expandFieldConfig = expandFieldConfigDao.getByCompanyId(companyId);
        //人员扩展字段
        String userExpandFields = expandFieldConfig == null ? null : expandFieldConfig.getUserExpandFields();
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
        openThirdEmployeeDTO.setCompanyId(companyId);
        openThirdEmployeeDTO.setThirdDepartmentId(wechatUser.getDepartmentStr());
        openThirdEmployeeDTO.setThirdEmployeeId(wechatUser.getUserId());
        //openThirdEmployeeDTO.setThirdEmployeeName(wechatUser.getName());
        openThirdEmployeeDTO.setThirdEmployeePhone(wechatUser.getMobile());
        openThirdEmployeeDTO.setThirdEmployeeEmail(wechatUser.getEmail());
        if (!com.fenbeitong.openapi.plugin.util.StringUtils.isBlank(wechatUser.getGender())) {
            openThirdEmployeeDTO.setThirdEmployeeGender(Integer.valueOf(wechatUser.getGender()));
        }
        // 1=已激活，2=已禁用，4=未激活，5=退出企业。
        if (1 == wechatUser.getStatus() || 2 == wechatUser.getStatus()) {
            openThirdEmployeeDTO.setStatus(wechatUser.getStatus());
        }
        // 未激活算正常状态
        if (4 == wechatUser.getStatus()) {
            openThirdEmployeeDTO.setStatus(1);
        }
        // 退出企业丢弃删除
        if (5 == wechatUser.getStatus()) {
            return TaskProcessResult.success("退出企业丢弃删除 success");
        }
        if ("1".equals(openThirdEmployeeDTO.getThirdDepartmentId())) {
            openThirdEmployeeDTO.setThirdDepartmentId(corpId);
        }

        String thirdEmployeeName = "";
        // 获取配置
        Map map = openSysConfigService.getEmployeeDefinedConfig(companyId);
        if (ObjectUtils.isEmpty(map)) {
            thirdEmployeeName = wechatUser.getAttrValueByAttrName("分贝姓名", "");
        } else {
            //分贝通名字
            thirdEmployeeName = wechatUser.getAttrValueByAttrName(map.get(EmployeeDefineEnum.THIRDEMPLOYEENAME.getValue()) != null ?
                map.get(EmployeeDefineEnum.THIRDEMPLOYEENAME.getValue()).toString() : "", "");
        }
        if (StringUtils.isTrimBlank(thirdEmployeeName)) {
            openThirdEmployeeDTO.setThirdEmployeeName(wechatUser.getName());
        } else {
            openThirdEmployeeDTO.setThirdEmployeeName(thirdEmployeeName);
        }

        // 权限
        String nFbPriv = "";
        Map<String, OpenExtInfo> thirdPermissionMap = openExtInfoDao.getOpenExtInfosByCompanyId(companyId);
        if (ObjectUtils.isEmpty(thirdPermissionMap)) {
            nFbPriv = wechatUser.getAttrValueByAttrName("分贝权限", "");
            openThirdEmployeeDTO.setThirdEmployeeRoleTye(nFbPriv);
        } else {
            //分贝权限
            OpenExtInfo openExtInfoFirst = null;
            for (OpenExtInfo val : thirdPermissionMap.values()) {
                openExtInfoFirst = val;
                break;
            }
            nFbPriv = wechatUser.getAttrValueByAttrName(openExtInfoFirst.getMapKey() != null ?
                openExtInfoFirst.getMapKey() : "", "");

            if ((!StringUtils.isBlank(nFbPriv)) && ObjectUtil.isNotNull(thirdPermissionMap.get(nFbPriv))) {
                openThirdEmployeeDTO.setThirdEmployeeRoleTye(StringUtils.obj2str(thirdPermissionMap.get(nFbPriv).getRoleType()));
            }
        }

        //新身份证号
        String nIdCard = wechatUser.getAttrValueByAttrName("身份证号", "");
        if (!com.fenbeitong.openapi.plugin.util.StringUtils.isBlank(nIdCard)) {
            openThirdEmployeeDTO.setThirdEmployeeIdCard(nIdCard);
        }
        //分贝通手机号
        String fbtMobile = wechatUser.getAttrValueByAttrName("分贝手机", "");
        if (!StringUtils.isBlank(fbtMobile)) {
            openThirdEmployeeDTO.setThirdEmployeePhone(fbtMobile);
        }

        if (!ObjectUtils.isEmpty(userExpandFields)) {
            Map expandJson = Maps.newHashMap();
            final WeChatGetUserResponse finalWechatUser = wechatUser;
            Lists.newArrayList(userExpandFields.split(",")).forEach(field -> {
                expandJson.put(field, finalWechatUser.getAttrValueByAttrName(field, null));
            });
            openThirdEmployeeDTO.setExtAttr(expandJson);
        }
        openThirdEmployeeDTO = thirdEmployeePostProcessService.process(openThirdEmployeeDTO, wechatUser, companyId, employeeConfig);
        if (null != openThirdEmployeeDTO) {
            employeeList.add(openThirdEmployeeDTO);
        }
        //查询分贝通员工信息
        List<OpenThirdEmployee> srcEmployeeList = openThirdEmployeeDao.listEmployeeByThirdEmployeeId(OpenType.WECHAT_EIA.getType(), companyId, Lists.newArrayList(openThirdEmployeeDTO.getThirdEmployeeId()));
        //如果员工已存在，则更新
        if (srcEmployeeList != null && srcEmployeeList.size() > 0) {
            openSyncThirdOrgService.updateEmployee(OpenType.WECHAT_EIA.getType(), companyId, employeeList);
        } else {
            openSyncThirdOrgService.addEmployee(OpenType.WECHAT_EIA.getType(), companyId, employeeList);
        }
        return TaskProcessResult.success("success");
    }


}
