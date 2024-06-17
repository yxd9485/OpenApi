package com.fenbeitong.openapi.plugin.kingdee.common.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeeK3CloudConfigDTO;
import com.fenbeitong.openapi.plugin.kingdee.common.enums.KingDeeK3CloudEnum;
import com.fenbeitong.openapi.plugin.kingdee.common.service.KingDeeOrgService;
import com.fenbeitong.openapi.plugin.kingdee.common.service.KingDeeService;
import com.fenbeitong.openapi.plugin.kingdee.support.dao.OpenKingdeeUrlConfigDao;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.KingdeeConfig;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.ResultVo;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.ViewReqDTO;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenKingdeeUrlConfig;
import com.fenbeitong.openapi.plugin.kingdee.support.service.KingdeeService;
import com.fenbeitong.openapi.plugin.support.callback.constant.ResultEnum;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdExpandFieldConfigDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdExpandFieldConfig;
import com.fenbeitong.openapi.plugin.support.init.service.DepartmentUtilService;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.util.KingdeeBaseUtils;
import com.fenbeitong.openapi.plugin.support.util.PhoneCheckUtil;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 金蝶服务实现
 *
 * @Auther zhang.peng
 * @Date 2021/6/7
 */
@ServiceAspect
@Service
@Slf4j
public class KingDeeOrgServiceImpl implements KingDeeOrgService {

    @Autowired
    OpenSysConfigDao openSysConfigDao;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private KingdeeService kingdeeService;

    @Autowired
    OpenSyncThirdOrgService openSyncThirdOrgService;

    @Autowired
    DepartmentUtilService departmentUtilService;

    @Autowired
    private OpenThirdExpandFieldConfigDao expandFieldConfigDao;

    @Autowired
    private OpenKingdeeUrlConfigDao kingDeeUrlConfigDao;

    @Autowired
    private KingDeeService jinDieService;

    @Autowired

    private KingdeeConfig kingdeeConfig;

    @Override
    public String syncOrganization(String companyId) {
        log.info("[金蝶k3_cloud] syncOrganization, 开始同步组织机构人员,companyId={}", companyId);
        String lockKey = MessageFormat.format(RedisKeyConstant.ORG_EMPLOYEE_SYNC_REDIS_KEY, companyId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                syncOrgEmployee(OpenType.JINDIE_K3_CLOUD.getType(), companyId);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("【金蝶k3_cloud】 syncOrganization, 未获取到锁，companyId={}", companyId);
        }

        return "success";
    }

    /**
     * 组织同步
     */
    public String syncOrgEmployee(int openType, String companyId) {
        // 获取初始化配置
        KingDeeK3CloudConfigDTO kingDee3KCloudConfigDTO = getConfig(companyId);
        //扩展字段配置
        OpenThirdExpandFieldConfig expandFieldConfig = expandFieldConfigDao.getByCompanyId(companyId);
        // 获取部门数据
        ViewReqDTO departReqDTO = kingDee3KCloudConfigDTO.getDepartment();
        departReqDTO.setCompanyId(companyId);
        // 获取配置信息
        OpenKingdeeUrlConfig kingDeeUrlConfig = kingDeeUrlConfigDao.getByCompanyId(companyId);
        // 获取Cookie
        String cookie = jinDieService.getCookie(kingDeeUrlConfig);
        List<List> dataDepList = jinDieService.getData(departReqDTO, kingDeeUrlConfig, cookie);
        // 获取人员数据
        ViewReqDTO userReqDTO = kingDee3KCloudConfigDTO.getEmployee();
        userReqDTO.setCompanyId(companyId);
        List<List> dataUseList = jinDieService.getData(userReqDTO, kingDeeUrlConfig, cookie);
        // 转换部门
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        for (List t : dataDepList) {
            OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
            openThirdOrgUnitDTO.setCompanyId(companyId);
            openThirdOrgUnitDTO.setThirdOrgUnitName(t.get(0).toString());
            openThirdOrgUnitDTO.setThirdOrgUnitParentId(t.get(1).toString());
            openThirdOrgUnitDTO.setThirdOrgUnitId(t.get(2).toString());
            Map depMap = Maps.newHashMap();
            // 编码
            depMap.put(KingDeeK3CloudEnum.FNUMBER.getKey(), t.get(3).toString());
            openThirdOrgUnitDTO.setExtInfo(depMap);
            departmentList.add(openThirdOrgUnitDTO);
        }
        log.info("3kCloud部门信息:{}", JsonUtils.toJson(departmentList));
        Map<String, OpenThirdOrgUnitDTO> openThirdOrgUnitDTOMap = departmentList.stream().collect(Collectors.toMap(t -> t.getThirdOrgUnitId(), Function.identity(), (o, n) -> n));
        // 人员转换
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        for (List l : dataUseList) {
            OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
            openThirdEmployeeDTO.setCompanyId(companyId);
            openThirdEmployeeDTO.setThirdDepartmentId(openThirdOrgUnitDTOMap.get(l.get(0).toString()) == null ? "0" : openThirdOrgUnitDTOMap.get(l.get(0).toString()).getExtInfo().get(KingDeeK3CloudEnum.FNUMBER.getKey()).toString());
            openThirdEmployeeDTO.setThirdEmployeeName(l.get(1).toString());
            if (StringUtils.isNotBlank(l.get(2).toString())) {
                openThirdEmployeeDTO.setThirdEmployeePhone(PhoneCheckUtil.getMobileWithoutCountryCode(l.get(2).toString()));
            }
            openThirdEmployeeDTO.setThirdEmployeeId(l.get(3).toString());
            Map employeeMap = Maps.newHashMap();
            // 用来存放内码
            employeeMap.put(KingDeeK3CloudEnum.FSTAFFID.getKey(), l.get(4).toString());
            // true 为主部门
            employeeMap.put(KingDeeK3CloudEnum.FISFIRSTPOST.getKey(), l.get(5).toString());
            openThirdEmployeeDTO.setExtInfo(employeeMap);
            employeeList.add(openThirdEmployeeDTO);
        }
        // 过滤主部门
        employeeList = getMasterDep(employeeList);
        log.info("3kCloud人员信息:{}", JsonUtils.toJson(employeeList));
        Map<String, OpenThirdEmployeeDTO> openThirdEmployeeDTOMap = employeeList.stream().collect(Collectors.toMap(t -> t.getExtInfo().get(KingDeeK3CloudEnum.FSTAFFID.getKey()).toString(), Function.identity(), (o, n) -> n));

        //部门扩展字段用“,分割”
        List finalDepLst=new ArrayList();
        if (!ObjectUtils.isEmpty(expandFieldConfig)) {
            String[] deps = expandFieldConfig.getDeptExpandFields().split(",");
            finalDepLst.addAll(Arrays.asList(deps));
        }
        departmentList.forEach(t -> {
            if (!ObjectUtils.isEmpty(expandFieldConfig)) {
                // 审单会计映射成人员编号存到部门扩展字段里
                Map<String, Object> map = Maps.newHashMap();
                for (int i = 0; i < finalDepLst.size(); i++) {
                    if (i == 0) {
                        map.put(finalDepLst.get(i).toString(), openThirdEmployeeDTOMap.get(t.getExtInfo().get(finalDepLst.get(i).toString())) == null ? "" : openThirdEmployeeDTOMap.get(t.getExtInfo().get(finalDepLst.get(i).toString())).getThirdEmployeeId());
                    } else {
                        map.put(finalDepLst.get(i).toString(), t.getExtInfo().get(finalDepLst.get(i).toString()));
                    }

                }
                t.setExtAttr(map);
            }
            t.setThirdOrgUnitId(t.getExtInfo().get(KingDeeK3CloudEnum.FNUMBER.getKey()).toString());
            t.setThirdOrgUnitParentId(openThirdOrgUnitDTOMap.get(t.getThirdOrgUnitParentId()) == null ? "0" : openThirdOrgUnitDTOMap.get(t.getThirdOrgUnitParentId()).getExtInfo().get(KingDeeK3CloudEnum.FNUMBER.getKey()).toString());
        });
        // 部门排序
        departmentList = departmentUtilService.deparmentSort(departmentList, "0");
        // 同步
        openSyncThirdOrgService.syncThird(openType, companyId, departmentList, employeeList);
        return "success";
    }


    /**
     * 人员主部门过滤
     */

    public List<OpenThirdEmployeeDTO> getMasterDep(List<OpenThirdEmployeeDTO> employeeList) {

        List<OpenThirdEmployeeDTO> list = new ArrayList<>();
        // 分组
        Map<String, List<OpenThirdEmployeeDTO>> groupBy = employeeList.stream().collect(Collectors.groupingBy(OpenThirdEmployeeDTO::getThirdEmployeeId));
        for (List<OpenThirdEmployeeDTO> openThirdEmployeeDTOList : groupBy.values()) {
            if (openThirdEmployeeDTOList.size() > 1) {
                Map<String, List<OpenThirdEmployeeDTO>> groupByFirst = openThirdEmployeeDTOList.stream().collect(Collectors.groupingBy(t -> t.getExtInfo().get(KingDeeK3CloudEnum.FISFIRSTPOST.getKey()).toString()));
                if (groupByFirst.get("true") != null) {
                    list.addAll(groupByFirst.get("true"));
                }
            } else {
                list.addAll(openThirdEmployeeDTOList);
            }
        }

        return list;
    }


    /**
     * 获取配置
     */
    public KingDeeK3CloudConfigDTO getConfig(String companyId) {
        // 获取配置信息
        HashMap<String, Object> configMap = Maps.newHashMap();
        configMap.put("code", companyId);
        configMap.put("type", OpenSysConfigType.JINDIE_3kCLOUD_SYS_CONFIG.getType());
        OpenSysConfig openSysConfig = openSysConfigDao.getOpenSysConfig(configMap);
        KingDeeK3CloudConfigDTO kingDee3KCloudConfigDTO = JsonUtils.toObj(openSysConfig.getValue(), KingDeeK3CloudConfigDTO.class);
        return kingDee3KCloudConfigDTO;
    }

    /**
     * 获取部门或人员数据
     */
    public List<List> getData(ViewReqDTO viewReqDTO) {
        // 获取Cookie
        OpenKingdeeUrlConfig kingDeeUrlConfig = kingDeeUrlConfigDao.getByCompanyId(viewReqDTO.getCompanyId());
        String cookie = getCookie(kingDeeUrlConfig);
        List<List> data = new ArrayList<>();
        getListRecursion(viewReqDTO, cookie, data, 0, kingDeeUrlConfig);
        // 查询数据
        return data;
    }


    /**
     * 获取cookie
     */
    public String getCookie(OpenKingdeeUrlConfig kingDeeUrlConfig) {
        //  登录
        MultiValueMap loginParam = KingdeeBaseUtils.buildLogin(kingDeeUrlConfig.getAcctId(), kingDeeUrlConfig.getUserName(), kingDeeUrlConfig.getPassword(), Long.parseLong(kingDeeUrlConfig.getLcid()));
        ResultVo login = kingdeeService.login(kingDeeUrlConfig.getUrl() + kingdeeConfig.getLogin(), loginParam);
        if (login.getCode() != ResultEnum.SUCCESS.getCode()) {
            log.info("【登录金蝶系统失败】：{}", login.getMsg());
            return null;
        }
        // 获取cookie
        Map<String, Object> map2 = (Map<String, Object>) login.getData();
        String cookie = map2.get("cookie").toString();
        return cookie;
    }

    /**
     * 递归查询数据
     */
    public void getListRecursion(ViewReqDTO viewReqDTO, String cookie, List<List> data, int count, OpenKingdeeUrlConfig kingDeeUrlConfig) {
        if (count >= 100) {
            throw new FinhubException(0, "程序异常");
        }
        String respData = kingdeeService.view(kingdeeConfig.getBillQury(), cookie, JsonUtils.toJson(viewReqDTO));
        if (!respData.contains("ErrorCode") && !respData.contains("Errors")) {
            if (!"".equals(respData) && respData != null && !"[]".equals(respData)) {
                JSONArray jsonArray = JSONObject.parseArray(respData);
                List<List> dataList = jsonArray.toJavaList(List.class);
                data.addAll(dataList);
                viewReqDTO.getData().setStartRow(viewReqDTO.getData().getStartRow() + 2000);
                viewReqDTO.getData().setLimit(2000);
                getListRecursion(viewReqDTO, cookie, data, ++count, kingDeeUrlConfig);
            }
        } else {
            throw new FinhubException(0, "数据异常");
        }
    }
}
