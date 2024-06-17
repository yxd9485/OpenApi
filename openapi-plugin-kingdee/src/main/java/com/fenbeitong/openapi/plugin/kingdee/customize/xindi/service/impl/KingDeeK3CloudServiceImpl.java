package com.fenbeitong.openapi.plugin.kingdee.customize.xindi.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeeK3CloudConfigDTO;
import com.fenbeitong.openapi.plugin.kingdee.common.enums.KingDeeK3CloudEnum;
import com.fenbeitong.openapi.plugin.kingdee.customize.xindi.service.KingDeeK3CloudService;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.KingdeeConfig;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.ResultVo;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.ViewReqDTO;
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
 * <p>Title: JinDie3kCloudServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-10-14 11:14
 */
@ServiceAspect
@Service
@Slf4j
public class KingDeeK3CloudServiceImpl implements KingDeeK3CloudService {
    @Autowired
    private KingdeeConfig kingdeeConfig;

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

    @Override
    public List<List> findKingdeeListData(ViewReqDTO viewReqDTO) {
        // 获取三方数据
        List<List> dataDepList = getData(viewReqDTO);
        return dataDepList;
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
        List<List> dataDepList = getData(kingDee3KCloudConfigDTO.getDepartment());
        // 获取人员数据
        List<List> dataUseList = getData(kingDee3KCloudConfigDTO.getEmployee());
        //部门扩展字段用“,分割”
        String[] deps = expandFieldConfig.getDeptExpandFields().split(",");
        List depLst = Arrays.asList(deps);
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
            for (int i = 0; i < depLst.size(); i++) {
                depMap.put(depLst.get(i), t.get(4 + i).toString());
            }
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
        log.info("3kCloud人员信息:{}", JsonUtils.toJson(employeeList));
        Map<String, OpenThirdEmployeeDTO> openThirdEmployeeDTOMap = employeeList.stream().collect(Collectors.toMap(t -> t.getExtInfo().get(KingDeeK3CloudEnum.FSTAFFID.getKey()).toString(), Function.identity(), (o, n) -> n));
        // 编号映射ID
        departmentList.forEach(t -> {
            if (!ObjectUtils.isEmpty(expandFieldConfig)) {
                // 审单会计映射成人员编号存到部门扩展字段里
                Map<String, Object> map = Maps.newHashMap();
                for (int i = 0; i < depLst.size(); i++) {
                    if (i == 0) {
                        map.put(depLst.get(i).toString(), openThirdEmployeeDTOMap.get(t.getExtInfo().get(depLst.get(i).toString()))==null?"":openThirdEmployeeDTOMap.get(t.getExtInfo().get(depLst.get(i).toString())).getThirdEmployeeId());
                    } else {
                        map.put(depLst.get(i).toString(), t.getExtInfo().get(depLst.get(i).toString()));
                    }

                }
                t.setExtAttr(map);
            }
            t.setThirdOrgUnitId(t.getExtInfo().get(KingDeeK3CloudEnum.FNUMBER.getKey()).toString());
            t.setThirdOrgUnitParentId(openThirdOrgUnitDTOMap.get(t.getThirdOrgUnitParentId()) == null ? "0" : openThirdOrgUnitDTOMap.get(t.getThirdOrgUnitParentId()).getExtInfo().get(KingDeeK3CloudEnum.FNUMBER.getKey()).toString());
        });
        // 过滤主部门
        employeeList = getMasterDep(employeeList);
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
        String cookie = getCookie();
        List<List> data = new ArrayList<>();
        getListRecursion(viewReqDTO, cookie, data, 0);
        // 查询数据
        return data;
    }


    /**
     * 获取cookie
     */
    public String getCookie() {
        //  登录
        MultiValueMap loginParam = KingdeeBaseUtils.buildLogin(kingdeeConfig.getAcctId(), kingdeeConfig.getUserName(), kingdeeConfig.getPassword(), kingdeeConfig.getLcid());
        ResultVo login = kingdeeService.login(kingdeeConfig.getUrl() + kingdeeConfig.getLogin(), loginParam);
        if (login.getCode() != ResultEnum.SUCCESS.getCode()) {
            log.warn("【登录金蝶系统失败】：{}", login.getMsg());
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
    public void getListRecursion(ViewReqDTO viewReqDTO, String cookie, List<List> data, int count) {
        if (count >= 100) {
            throw new FinhubException(0, "程序异常");
        }
        String respData = kingdeeService.view(kingdeeConfig.getUrl().concat(kingdeeConfig.getBillQury()), cookie, JsonUtils.toJson(viewReqDTO));
        if (!respData.contains("ErrorCode") && !respData.contains("Errors")) {
            if (!"".equals(respData) && respData != null && !"[]".equals(respData)) {
                JSONArray jsonArray = JSONObject.parseArray(respData);
                List<List> dataList = jsonArray.toJavaList(List.class);
                data.addAll(dataList);
                viewReqDTO.getData().setStartRow(viewReqDTO.getData().getStartRow() + 2000);
                viewReqDTO.getData().setLimit(2000);
                getListRecursion(viewReqDTO, cookie, data, ++count);
            }
        } else {
            throw new FinhubException(0, "数据异常");
        }
    }

}
