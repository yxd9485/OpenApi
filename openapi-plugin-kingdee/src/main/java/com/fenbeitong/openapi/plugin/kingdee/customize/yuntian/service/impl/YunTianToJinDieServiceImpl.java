package com.fenbeitong.openapi.plugin.kingdee.customize.yuntian.service.impl;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeeConfigDTO;
import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeeProjectListDTO;
import com.fenbeitong.openapi.plugin.kingdee.customize.yuntian.service.YunTianToJinDieService;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.project.service.OpenProjectService;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.sdk.dto.project.CreateThirdProjectByBatchReqDTO;
import com.fenbeitong.openapi.sdk.dto.project.ListThirdProjectRespDTO;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title: YunTianToJinDieServiceImpl</p>
 * <p>Description:  云天励飞-对接金蝶</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-08-26 11:29
 */
@Slf4j
@ServiceAspect
@Service
public class YunTianToJinDieServiceImpl implements YunTianToJinDieService {


    @Autowired
    OpenProjectService openProjectService;

    @Autowired
    private OpenEmployeeServiceImpl openEmployeeService;

    @Autowired
    OpenSysConfigDao openSysConfigDao;

    @Autowired
    AuthDefinitionDao authDefinitionDao;

    @Autowired
    private ExceptionRemind exceptionRemind;

    /**
     * 执行数据同步
     */
    @Override
    public String syncProject(String companyId) {
        long start = System.currentTimeMillis();
        // 获取配置信息
        HashMap<String, Object> configMap = Maps.newHashMap();
        configMap.put("code", companyId);
        configMap.put("type", OpenSysConfigType.JINDIE_PROJECT_SYS_CONFIG.getType());
        OpenSysConfig openSysConfig = openSysConfigDao.getOpenSysConfig(configMap);
        if (ObjectUtils.isEmpty(openSysConfig)) {
            return "Failed";
        }
        KingDeeConfigDTO jinDieConfigDTo = JsonUtils.toObj(openSysConfig.getValue(), KingDeeConfigDTO.class);

        Map<String, Object> map = new HashMap<>();
        String token = getTokent(jinDieConfigDTo, companyId);
        map.put("token", token);
        log.info("云天励飞-对接金蝶，获取 token:{}", token);
        // 查询项目
        KingDeeProjectListDTO projectListDTo = new KingDeeProjectListDTO();
        try {
            String data = RestHttpUtils.get(jinDieConfigDTo.getIteamUrl(), new HttpHeaders(), map);
            projectListDTo = JsonUtils.toObj(data, KingDeeProjectListDTO.class);
            if (projectListDTo.getCode() != 200) {
                throw new FinhubException(0, data);
            }
        } catch (Exception e) {
            AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(companyId);
            String msg = String.format("请求三方接口异常\n企业id：[%s]\n企业名称：[%s]\n请求url：[%s]\n请求参数：[%s]\n异常信息：[%s]", companyId, authDefinition.getAppName(), jinDieConfigDTo.getIteamUrl(), JsonUtils.toJson(map), e);
            exceptionRemind.remindDingTalk(msg);
            log.warn("", e);
        }

        // 过滤
        projectListDTo.setData(projectListDTo.getData().stream().filter(t -> !ObjectUtils.isEmpty(t.getProjectNo())).collect(Collectors.toList()));
        // 查询本地数据
        ListThirdProjectRespDTO listThirdProjectRespDTO = null;
        long queryStart = System.currentTimeMillis();
        listThirdProjectRespDTO = openProjectService.getProjectByCompanyId(companyId);
        log.info("获取全量数据全量数据 ListThirdProjectRespDTO 查询耗时 {} ms", System.currentTimeMillis() - queryStart);

        // 数据对比
        CreateThirdProjectByBatchReqDTO createThirdProjectByBatchReqDTO = new CreateThirdProjectByBatchReqDTO();
        List<CreateThirdProjectByBatchReqDTO.Projectinfo> projectinfoList = new ArrayList<>();
        projectListDTo.getData().forEach(t -> {
            CreateThirdProjectByBatchReqDTO.Projectinfo projectinfo = new CreateThirdProjectByBatchReqDTO.Projectinfo();
            projectinfo.setName(t.getName());
            projectinfo.setThirdCostId(t.projectNo);
            projectinfo.setCode(t.getProjectNo());
            projectinfoList.add(projectinfo);
        });
        createThirdProjectByBatchReqDTO.setProjectInfo(projectinfoList);
        createThirdProjectByBatchReqDTO.setUserId(openEmployeeService.superAdmin(companyId));
        createThirdProjectByBatchReqDTO.setCompanyId(companyId);
        // 1 自动 2 手动
        createThirdProjectByBatchReqDTO.setAutoFlag(2);
        // 是否有第三方ID  1：没有 2 第三方项目ID
        createThirdProjectByBatchReqDTO.setType(1);

        long updateStart = System.currentTimeMillis();
        openProjectService.projectUpdateOrAdd(listThirdProjectRespDTO, createThirdProjectByBatchReqDTO);
        log.info("项目更据耗新 -> projectUpdateOrAdd 耗时 {} ms", System.currentTimeMillis() - updateStart);
        log.info("数据同步 -> syncProject 总耗时 {} ms", System.currentTimeMillis() - start);
        return "success";

    }


    /**
     * 获取token
     */
    private String getTokent(KingDeeConfigDTO jinDieConfigDTo, String companyId) {
        Map<String, Object> map = new HashMap<>();
        map.put("appId", jinDieConfigDTo.getAppId());
        map.put("appSecret", jinDieConfigDTo.getAppSecret());
        String reqData = JsonUtils.toJsonSnake(map);
        try {
            Map<String, Map<String, Object>> resultMap = JsonUtils.toObj(RestHttpUtils.postJson(jinDieConfigDTo.getTokenUrl(), reqData), Map.class);
            if (ObjectUtils.isEmpty(resultMap) || ObjectUtils.isEmpty(resultMap.get("data"))) {
                throw new FinhubException(0, JsonUtils.toJson(resultMap));
            }
            return resultMap.get("data") == null ? "" : resultMap.get("data").get("token") == null ? "" : resultMap.get("data").get("token").toString();
        } catch (Exception e) {
            AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(companyId);
            String msg = String.format("请求三方接口异常\n企业id：[%s]\n企业名称：[%s]\n请求url：[%s]\n请求参数：[%s]\n异常信息：[%s]", companyId, authDefinition.getAppName(), jinDieConfigDTo.getTokenUrl(), JsonUtils.toJson(reqData), e);
            exceptionRemind.remindDingTalk(msg);
            log.warn("", e);
        }
        return "";
    }
}
