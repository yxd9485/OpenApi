package com.fenbeitong.openapi.plugin.customize.common.service.impl;

import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.project.dto.SupportUcThirdProjectReqDTO;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.finhub.framework.core.SpringUtils;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenCustonmConstant;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenCustomizeConfigDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenCustomizePageConfigDao;
import com.fenbeitong.openapi.plugin.customize.common.service.OrgListener;
import com.fenbeitong.openapi.plugin.customize.common.service.ProjectListener;
import com.fenbeitong.openapi.plugin.customize.common.vo.OpenThirdEmployeeVo;
import com.fenbeitong.openapi.plugin.customize.common.vo.OpenThirdOrgUnitVo;
import com.fenbeitong.openapi.plugin.customize.common.vo.OpenThirdProjectVo;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenCustomizeConfig;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenCustomizePageConfig;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.service.DepartmentUtilService;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>Title: PrimaryCommonImpl</p>
 * <p>Description: 组织架构同步配置公共类</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-12-01 19:34
 */
@Slf4j
public class PrimaryCommonImpl {

    @Autowired
    OpenCustomizeConfigDao openCustomizeConfigDao;

    @Autowired
    OpenCustomizePageConfigDao openOrgPageConfigDao;

    @Autowired
    OpenSysConfigDao openSysConfigDao;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    IEtlService etlService;

    @Autowired
    OpenSyncThirdOrgService openSyncThirdOrgService;

    @Autowired
    DepartmentUtilService departmentUtilService;

    @Autowired
    AuthDefinitionDao authDefinitionDao;

    @Autowired
    OpenThirdOrgUnitDao openThirdOrgUnitDao;

    @Autowired
    private ExceptionRemind exceptionRemind;


    /**
     * @Description 获取全量部门信息
     * @Author duhui
     * @Date 2020-09-21
     **/
    public List<OpenThirdOrgUnitDTO> getAllDepartments(String companyId, OpenCustomizeConfig openOrgDepartmentConfig, OrgListener orgListener) {
        // 请求url
        String url = openOrgDepartmentConfig.getUrl();
        // 请求参数
        Map reqMap = JsonUtils.toObj(openOrgDepartmentConfig.getReqJson(), Map.class);
        reqMap = reqMap == null ? Maps.newHashMap() : reqMap;
        // 请求类型 post get delete
        String reqType = openOrgDepartmentConfig.getReqType();
        // 是否全量获取 0全量 1批量
        Integer isAll = openOrgDepartmentConfig.getIsAll();
        // etl配置
        Long etlConfigId = openOrgDepartmentConfig.getEtlConfigId();
        // 请求体类型
        String bodyType = openOrgDepartmentConfig.getBodyType();
        // 请求头配置
        Map<String, String> headMap = JsonUtils.toObj(openOrgDepartmentConfig.getHeadJson(), Map.class);
        headMap = headMap == null ? Maps.newHashMap() : headMap;
        // 设置签名
        orgListener.setHead(headMap, companyId);
        List<OpenThirdOrgUnitDTO> openThirdOrgUnitDTOArrayList = new ArrayList<>();
        if (isAll == 0) {
            // 设置请求参数
            orgListener.setBody(reqMap, companyId);
            String respData = getData(url, reqMap, reqType, headMap, bodyType, companyId);
            openThirdOrgUnitDTOArrayList = orgListener.getOrgMaping(etlConfigId, respData).getOpenThirdOrgUnitDTOS();
        } else if (isAll == 1) {
            Map<String, OpenCustomizePageConfig> openOrgPageConfigMap = getOpenCustomizePageConfig(companyId, OpenCustonmConstant.openCunstmizeConfigType.DEPTMENT);
            reqMap.put(openOrgPageConfigMap.get(OpenCustonmConstant.mapConfig.PAGESIZE).getSrcCol(), openOrgPageConfigMap.get(OpenCustonmConstant.mapConfig.PAGESIZE).getValue());
            reqMap.put(openOrgPageConfigMap.get(OpenCustonmConstant.mapConfig.PAGENUMBER).getSrcCol(), openOrgPageConfigMap.get(OpenCustonmConstant.mapConfig.PAGENUMBER).getValue());
            // 设置请求参数
            orgListener.setBody(reqMap, companyId);
            // 递归获取全量数据
            circulationOrgUnit(reqMap, url, reqType, etlConfigId, openThirdOrgUnitDTOArrayList, openOrgPageConfigMap, headMap, orgListener, bodyType, companyId);
        }
        return openThirdOrgUnitDTOArrayList;
    }

    /**
     * @Description 获取全量人员信息
     * @Author duhui
     * @Date 2020-09-22
     **/
    public List<OpenThirdEmployeeDTO> getAllPersonnel(String companyId, OpenCustomizeConfig openOrgDepartmentConfig, OrgListener orgListener) {
        // 请求url
        String url = openOrgDepartmentConfig.getUrl();
        // 请求参数
        Map reqMap = JsonUtils.toObj(openOrgDepartmentConfig.getReqJson(), Map.class);
        reqMap = reqMap == null ? Maps.newHashMap() : reqMap;
        // 请求类型 post get delete
        String reqType = openOrgDepartmentConfig.getReqType();
        // 是否全量获取 0全量 1批量
        Integer isAll = openOrgDepartmentConfig.getIsAll();
        // etl配置
        Long etlConfigId = openOrgDepartmentConfig.getEtlConfigId();
        // 请求体类型
        String bodyType = openOrgDepartmentConfig.getBodyType();
        // 请求头配置
        Map<String, String> headMap = JsonUtils.toObj(openOrgDepartmentConfig.getHeadJson(), Map.class);
        headMap = headMap == null ? Maps.newHashMap() : headMap;
        // 设置签名
        orgListener.setHead(headMap, companyId);
        List<OpenThirdEmployeeDTO> openThirdEmployeeDTOList = new ArrayList<>();
        if (isAll == 0) {
            // 设置请求参数
            orgListener.setBody(reqMap, companyId);
            // 请求数据
            String respData = getData(url, reqMap, reqType, headMap, openOrgDepartmentConfig.getBodyType(), companyId);
            // 映射数据
            openThirdEmployeeDTOList.addAll(orgListener.getEmployeeMaping(etlConfigId, respData).getOpenThirdEmployeeDTOS());
        } else if (isAll == 1) {
            Map<String, OpenCustomizePageConfig> openOrgPageConfigMap = getOpenCustomizePageConfig(companyId, OpenCustonmConstant.openCunstmizeConfigType.EMPLOYEE);
            reqMap.put(openOrgPageConfigMap.get(OpenCustonmConstant.mapConfig.PAGESIZE).getSrcCol(), openOrgPageConfigMap.get(OpenCustonmConstant.mapConfig.PAGESIZE).getValue());
            reqMap.put(openOrgPageConfigMap.get(OpenCustonmConstant.mapConfig.PAGENUMBER).getSrcCol(), openOrgPageConfigMap.get(OpenCustonmConstant.mapConfig.PAGENUMBER).getValue());
            // 设置请求参数
            orgListener.setBody(reqMap, companyId);
            // 递归获取全量数据
            circulationEmployee(reqMap, url, reqType, etlConfigId, openThirdEmployeeDTOList, openOrgPageConfigMap, headMap, orgListener, bodyType, companyId);

        }
        return openThirdEmployeeDTOList;
    }


    /**
     * @Description 获取全量项目信息
     * @Author duhui
     * @Date 2021/8/9
     **/

    public List<SupportUcThirdProjectReqDTO> getAllProject(String companyId, OpenCustomizeConfig openCustomizeConfig, ProjectListener projectListener) {
        // 请求url
        String url = openCustomizeConfig.getUrl();
        // 请求参数
        Map reqMap = JsonUtils.toObj(openCustomizeConfig.getReqJson(), Map.class);
        reqMap = reqMap == null ? Maps.newHashMap() : reqMap;
        // 请求类型 post get delete
        String reqType = openCustomizeConfig.getReqType();
        // 是否全量获取 0全量 1批量
        Integer isAll = openCustomizeConfig.getIsAll();
        // etl配置
        Long etlConfigId = openCustomizeConfig.getEtlConfigId();
        // 请求体类型
        String bodyType = openCustomizeConfig.getBodyType();
        // 请求头配置
        Map<String, String> headMap = JsonUtils.toObj(openCustomizeConfig.getHeadJson(), Map.class);
        headMap = headMap == null ? Maps.newHashMap() : headMap;
        // 设置签名
        projectListener.setHead(headMap, companyId);
        List<SupportUcThirdProjectReqDTO> addThirdProjectReqDTOList = new ArrayList<>();
        if (isAll == 0) {
            // 设置请求参数
            projectListener.setBody(reqMap, companyId);
            // 请求数据
            String respData = getData(url, reqMap, reqType, headMap, openCustomizeConfig.getBodyType(), companyId);
            // 映射数据
            addThirdProjectReqDTOList.addAll(projectListener.getProjectMaping(openCustomizeConfig, respData).getAddThirdProjectReqDTO());
        } else if (isAll == 1) {
            Map<String, OpenCustomizePageConfig> openOrgPageConfigMap = getOpenCustomizePageConfig(companyId, OpenCustonmConstant.openCunstmizeConfigType.EMPLOYEE);
            reqMap.put(openOrgPageConfigMap.get(OpenCustonmConstant.mapConfig.PAGESIZE).getSrcCol(), openOrgPageConfigMap.get(OpenCustonmConstant.mapConfig.PAGESIZE).getValue());
            reqMap.put(openOrgPageConfigMap.get(OpenCustonmConstant.mapConfig.PAGENUMBER).getSrcCol(), openOrgPageConfigMap.get(OpenCustonmConstant.mapConfig.PAGENUMBER).getValue());
            // 设置请求参数
            projectListener.setBody(reqMap, companyId);
            // 递归获取全量数据
            circulationProject(reqMap, url, reqType, etlConfigId, addThirdProjectReqDTOList, openOrgPageConfigMap, headMap, projectListener, bodyType, openCustomizeConfig, companyId);

        }
        return addThirdProjectReqDTOList;
    }


    /**
     * 递归查询全量人员信息
     */
    public void circulationEmployee(Map reqMap, String url, String reqType, Long etlConfigId, List<OpenThirdEmployeeDTO> openThirdEmployeeDTOList, Map<String, OpenCustomizePageConfig> openOrgPageConfigMap, Map<String, String> headMap, OrgListener orgListener, String bodyType, String companyId) {
        // 请求数据
        String respData = getData(url, reqMap, reqType, headMap, bodyType, companyId);
        OpenThirdEmployeeVo employeeVo = orgListener.getEmployeeMaping(etlConfigId, respData);
        openThirdEmployeeDTOList.addAll(employeeVo.getOpenThirdEmployeeDTOS());
        while ((employeeVo.getTotalCount() > Integer.parseInt(reqMap.get(openOrgPageConfigMap.get(OpenCustonmConstant.mapConfig.PAGESIZE).getSrcCol()).toString()) * Integer.parseInt(reqMap.get(openOrgPageConfigMap.get(OpenCustonmConstant.mapConfig.PAGENUMBER).getSrcCol()).toString()))) {
            reqMap.put(openOrgPageConfigMap.get(OpenCustonmConstant.mapConfig.PAGENUMBER).getSrcCol(), Integer.parseInt(reqMap.get(openOrgPageConfigMap.get(OpenCustonmConstant.mapConfig.PAGENUMBER).getSrcCol()).toString()) + 1);
            circulationEmployee(reqMap, url, reqType, etlConfigId, openThirdEmployeeDTOList, openOrgPageConfigMap, headMap, orgListener, bodyType, companyId);
        }
    }


    /**
     * 递归查询全量部门信息
     */
    public void circulationOrgUnit(Map reqMap, String url, String reqType, Long etlConfigId, List<OpenThirdOrgUnitDTO> openThirdOrgUnitDTOList, Map<String, OpenCustomizePageConfig> openOrgPageConfigMap, Map<String, String> headMap, OrgListener orgListener, String bodyType, String companyId) {
        // 请求数据
        String respData = getData(url, reqMap, reqType, headMap, bodyType, companyId);
        OpenThirdOrgUnitVo openThirdOrgUnitVo = orgListener.getOrgMaping(etlConfigId, respData);
        openThirdOrgUnitDTOList.addAll(openThirdOrgUnitVo.getOpenThirdOrgUnitDTOS());
        while ((openThirdOrgUnitVo.getTotalCount() > Integer.parseInt(reqMap.get(openOrgPageConfigMap.get(OpenCustonmConstant.mapConfig.PAGESIZE).getSrcCol()).toString()) * Integer.parseInt(reqMap.get(openOrgPageConfigMap.get(OpenCustonmConstant.mapConfig.PAGENUMBER).getSrcCol()).toString()))) {
            reqMap.put(openOrgPageConfigMap.get(OpenCustonmConstant.mapConfig.PAGENUMBER).getSrcCol(), Integer.parseInt(reqMap.get(openOrgPageConfigMap.get(OpenCustonmConstant.mapConfig.PAGENUMBER).getSrcCol()).toString()) + 1);
            circulationOrgUnit(reqMap, url, reqType, etlConfigId, openThirdOrgUnitDTOList, openOrgPageConfigMap, headMap, orgListener, bodyType, companyId);
        }
    }

    /**
     * 递归查询全量项目信心
     */
    public void circulationProject(Map reqMap, String url, String reqType, Long etlConfigId, List<SupportUcThirdProjectReqDTO> addThirdProjectReqDTOList, Map<String, OpenCustomizePageConfig> openOrgPageConfigMap, Map<String, String> headMap, ProjectListener projectListener, String bodyType, OpenCustomizeConfig openCustomizeConfig, String companyId) {
        // 请求数据
        String respData = getData(url, reqMap, reqType, headMap, bodyType, companyId);
        OpenThirdProjectVo openThirdProjectVo = projectListener.getProjectMaping(openCustomizeConfig, respData);
        addThirdProjectReqDTOList.addAll(openThirdProjectVo.getAddThirdProjectReqDTO());
        while ((openThirdProjectVo.getTotalCount() > Integer.parseInt(reqMap.get(openOrgPageConfigMap.get(OpenCustonmConstant.mapConfig.PAGESIZE).getSrcCol()).toString()) * Integer.parseInt(reqMap.get(openOrgPageConfigMap.get(OpenCustonmConstant.mapConfig.PAGENUMBER).getSrcCol()).toString()))) {
            reqMap.put(openOrgPageConfigMap.get(OpenCustonmConstant.mapConfig.PAGENUMBER).getSrcCol(), Integer.parseInt(reqMap.get(openOrgPageConfigMap.get(OpenCustonmConstant.mapConfig.PAGENUMBER).getSrcCol()).toString()) + 1);
            circulationProject(reqMap, url, reqType, etlConfigId, addThirdProjectReqDTOList, openOrgPageConfigMap, headMap, projectListener, bodyType, openCustomizeConfig, companyId);
        }
    }

    /**
     * 获取批量请求配置
     */

    public Map<String, OpenCustomizePageConfig> getOpenCustomizePageConfig(String companyId, Integer type) {
        Map map = Maps.newHashMap();
        map.put("companyId", companyId);
        map.put("type", type);
        map.put("state", 1);
        List<OpenCustomizePageConfig> openOrgPageConfigs = openOrgPageConfigDao.selectByCompanyId(map);
        Map<String, OpenCustomizePageConfig> openOrgPageConfigMap = openOrgPageConfigs.stream().collect(Collectors.toMap(t -> t.getTgtCol(), Function.identity(), (o, n) -> n));
        return openOrgPageConfigMap;

    }


    /**
     * 获取请求参数配置
     */
    public OpenCustomizeConfig getOpenCustomizeConfig(String companyId, Integer type, Integer isAll) {
        Map map = Maps.newHashMap();
        map.put("companyId", companyId);
        map.put("type", type);
        map.put("state", 1);
        map.put("isAll", isAll);
        OpenCustomizeConfig openOrgConfigs = openCustomizeConfigDao.selectByMap(map);
        return openOrgConfigs;
    }

    /**
     * 获取请求参数配置
     */
    public OpenCustomizeConfig getOpenCustomizeConfig(String companyId, Integer type) {
        Map map = Maps.newHashMap();
        map.put("companyId", companyId);
        map.put("type", type);
        map.put("state", 1);
        OpenCustomizeConfig openOrgConfigs = openCustomizeConfigDao.selectByMap(map);
        return openOrgConfigs;
    }

    /**
     * 获取请求参数配置
     */
    public List<OpenCustomizeConfig> getOpenCustomizeConfigList(String companyId, Integer type) {
        Map map = Maps.newHashMap();
        map.put("companyId", companyId);
        map.put("type", type);
        map.put("state", 1);
        return openCustomizeConfigDao.selectByMapList(map);
    }

    /**
     * @param url     请求的数据URL
     * @param reqJson 请求参数
     * @param reqType 请求类型
     * @Description 获取数据
     * @Author duhui
     * @Date 2020-09-22
     **/
    public String getData(String url, Map reqJson, String reqType, Map<String, String> headMap, String bodyType, String companyId) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            if (!ObjectUtils.isEmpty(headMap)) {
                for (String key : headMap.keySet()) {
                    httpHeaders.add(key, headMap.get(key));
                }
            }
            String data = "";
            if (OpenCustonmConstant.reqType.POST.equals(reqType)) {
                if (!StringUtils.isBlank(bodyType)) {
                    switch (bodyType) {
                        case OpenCustonmConstant.bodyType.FORM:
                            Map map = JsonUtils.toObj(JsonUtils.toJson(reqJson), Map.class);
                            MultiValueMap multiValueMap = new LinkedMultiValueMap<>();
                            map.forEach((k, v) -> {
                                multiValueMap.add(k, v);
                            });
                            data = RestHttpUtils.postForm(url, httpHeaders, multiValueMap);
                            break;
                        case OpenCustonmConstant.bodyType.JSON:
                            data = RestHttpUtils.postJson(url, httpHeaders, JsonUtils.toJson(reqJson));
                            break;
                    }
                } else {
                    data = RestHttpUtils.postJson(url, httpHeaders, JsonUtils.toJson(reqJson));
                }
            } else if (OpenCustonmConstant.reqType.GET.equals(reqType)) {
                data = RestHttpUtils.get(url, httpHeaders, reqJson);
            }
            return data;
        } catch (Exception e) {
            AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(companyId);
            String msg = String.format("请求三方接口异常\n企业id：[%s]\n企业名称：[%s]\n请求url：[%s]\n请求参数：[%s]\n异常信息：[%s]", companyId, authDefinition.getAppName(), url, JsonUtils.toJson(reqJson),e);
            exceptionRemind.remindDingTalk(msg);
            log.warn("", e);
        }
        return null;
    }


    /**
     * 反射获取监听类
     */
    public OrgListener getOrgLister(OpenCustomizeConfig openOrgConfig) {
        String className = openOrgConfig.getListenerClass();
        if (!ObjectUtils.isEmpty(className)) {
            Class clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (clazz != null) {
                Object bean = SpringUtils.getBean(clazz);
                if (bean != null && bean instanceof OrgListener) {
                    return ((OrgListener) bean);
                }
            }
        }
        return SpringUtils.getBean(DefaultOrgListener.class);
    }

    /**
     * 反射获取监听类
     */
    public ProjectListener getProjectListener(OpenCustomizeConfig openOrgConfig) {
        String className = openOrgConfig.getListenerClass();
        if (!ObjectUtils.isEmpty(className)) {
            Class clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (clazz != null) {
                Object bean = SpringUtils.getBean(clazz);
                if (bean != null && bean instanceof ProjectListener) {
                    return ((ProjectListener) bean);
                }
            }
        }
        return SpringUtils.getBean(DefaultCustomProjectListener.class);
    }
}
