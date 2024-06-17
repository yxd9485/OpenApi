package com.fenbeitong.openapi.plugin.customize.hairou.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.customize.hairou.constant.HaiRouRestApi;
import com.fenbeitong.openapi.plugin.customize.hairou.dto.*;
import com.fenbeitong.openapi.plugin.customize.hairou.service.HaiRouProjectService;
import com.fenbeitong.openapi.plugin.customize.hairou.util.HaiRouRestApiUtils;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.project.dto.OpenThirdProject;
import com.fenbeitong.openapi.plugin.support.project.dto.SupportUcThirdProjectReqDTO;
import com.fenbeitong.openapi.plugin.support.project.service.OpenBaseProjectService;
import com.fenbeitong.openapi.plugin.support.project.service.OpenProjectServiceV2;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.support.util.SuperAdminUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author :zhiqiang.zhang
 * @title: HaiRouProjectApiServiceImpl
 * @projectName openapi-plugin
 * @description: 海柔创新接口项目数据获取实现
 * @date 2022/5/18
 */
@Service
@Slf4j
@ServiceAspect
public class HaiRouProjectServiceImpl implements HaiRouProjectService {

    @Autowired
    private SuperAdminUtils superAdminUtils;

    @Autowired
    private OpenBaseProjectService openBaseProjectService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public int getProjectCount(HaiRouProjectJobConfigDTO jobConfig) {

        JSONObject paramDataJson = getCommonParamDataJson(jobConfig);
        String url = jobConfig.getUrlHost() + HaiRouRestApi.GET_PROJECT_COUNT;
        String result = RestHttpUtils.postForm(url, getParam(paramDataJson));

        HaiRouProjectDTO haiRouProjectDTO = interfaceCheckCommon(result, "海柔创新获取项目数量失败，");
        HaiRouProjectCountDTO haiRouProjectCountDTO = JsonUtils.toObj(haiRouProjectDTO.getResult(), HaiRouProjectCountDTO.class);
        if (haiRouProjectCountDTO.getPageCount() == null) {
            throw new OpenApiPluginSupportException(SupportRespCode.PROJECT_LIST_ERROR, "海柔创新获取项目数量失败<response缺少pageCount字段>");
        }
        return haiRouProjectCountDTO.getPageCount();
    }

    @Override
    @Async
    public void getProjectListSync(HaiRouProjectJobConfigDTO jobConfig) {
        String jsonJobConfig = JsonUtils.toJson(jobConfig);
        log.info("海柔创新项目数据开始同步，config={}", jsonJobConfig);
        // 加锁，防止并发场景下分页参数错乱
        String lockKey = MessageFormat.format(RedisKeyConstant.TASK_REDIS_KEY, "sync_pull_hairou_project_" + jobConfig.getCompanyId());
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                projectsDataPagePull(jobConfig);
            } catch (Exception e) {
                log.info("海柔创新项目数据同步失败,config={}: ExceptionMessage:{}", jsonJobConfig, e);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("海柔创新项目数据同步时未获取到锁<redisKey:{}>,config={}", lockKey, jsonJobConfig);
        }
        log.info("海柔创新项目数据同步完毕，config={}", jsonJobConfig);

    }

    /**
     * 分页拉取海柔项目列表数据
     *
     * @param jobConfig 定时任务传过来的参数
     */
    private void projectsDataPagePull(HaiRouProjectJobConfigDTO jobConfig) {
        String thirdEmployeeId = superAdminUtils.companySuperAdmin(jobConfig.getCompanyId()).getThirdEmployeeId();
        //获取项目数据总条数
        int totalCount = getProjectCount(jobConfig);
        //可设置每页拉取的数据量
        int pageSize = 500;
        int totalPage = (totalCount + pageSize - 1) / pageSize;

        //存储获取的每页待同步的项目数据
        List<OpenThirdProject> projects = Lists.newArrayList();

        //分页拉取数据
        for (int currentPage = 1; currentPage <= totalPage; currentPage++) {
            JSONObject paramDataJson = getCommonParamDataJson(jobConfig);
            JSONObject pageInfo = getPageInfo(currentPage, pageSize);
            paramDataJson.put("pageInfo", pageInfo);
            String url = jobConfig.getUrlHost() + HaiRouRestApi.GET_PROJECT_LIST;
            String result = RestHttpUtils.postForm(url, getParam(paramDataJson));
            HaiRouProjectDTO haiRouProjectDTO = interfaceCheckCommon(result, "海柔创新获取项目列表失败,");
            List<HaiRouProjectListDTO<HaiRouProjectMaiTableDTO>> projectList = JsonUtils.toObj(haiRouProjectDTO.getResult(), new TypeReference<List<HaiRouProjectListDTO<HaiRouProjectMaiTableDTO>>>() {
            });
            projectList.forEach(haiRouProjects -> {
                HaiRouProjectMaiTableDTO mainTable = haiRouProjects.getMainTable();

                //映射接口数据,并将转换完的数据放到待同步列表里
                projects.add(OpenThirdProject.builder()
                    .companyId(jobConfig.getCompanyId())
                    .userId(thirdEmployeeId)
                    .thirdId(mainTable.getId())
                    .code(mainTable.getXsxmbm())
                    .name(mainTable.getXsxmbm())
                    .openType(OpenType.FANWEI.getType())
                    .usableRange(1)
                    .expiredType(1)
                    .state(1)
                    .build());

            });
        }

        //同步获取到的当前页的项目数据到UC
        openBaseProjectService.syncAllProject(jobConfig.getCompanyId(), OpenType.FANWEI.getType(), projects, jobConfig.isForceUpdate);
    }

    /**
     * 海柔接口公共的接口校验部分
     *
     * @param result       接收的实际接口数据
     * @param errorMessage 具体的错误信息说明
     * @return 实际的接口业务数据
     */
    private HaiRouProjectDTO interfaceCheckCommon(String result, String errorMessage) {
        HaiRouResponseStatusDTO haiRouResponseStatusDTO = JsonUtils.toObj(result, HaiRouResponseStatusDTO.class);
        if (haiRouResponseStatusDTO.getStatus() != null) {
            throw new OpenApiPluginSupportException(SupportRespCode.PROJECT_LIST_ERROR, errorMessage.concat(haiRouResponseStatusDTO.getInfo()));
        }
        HaiRouProjectDTO haiRouProjectDTO = JsonUtils.toObj(result, HaiRouProjectDTO.class);
        if (haiRouProjectDTO.getResult() == null) {
            throw new OpenApiPluginSupportException(SupportRespCode.PROJECT_LIST_ERROR, errorMessage.concat("<response缺少result字段>"));
        }
        return haiRouProjectDTO;
    }

    /**
     * 获取接口请求参数
     *
     * @param paramDataJson 接口请求封装的数据
     * @return 接口请求参数
     */
    private LinkedMultiValueMap<String, Object> getParam(JSONObject paramDataJson) {

        JSONObject paramJson = new JSONObject();
        paramJson.put("datajson", paramDataJson);
        LinkedMultiValueMap<String, Object> linkedMultiValueMap = new LinkedMultiValueMap<>();
        linkedMultiValueMap.setAll(paramJson);
        return linkedMultiValueMap;
    }

    /**
     * 获取海柔项目数据接口公共入参
     *
     * @return 项目数据接口公共入参
     */
    private JSONObject getCommonParamDataJson(HaiRouProjectJobConfigDTO jobConfig) {

        JSONObject paramDataJson = new JSONObject();
        JSONObject operationInfoJson = getOperationInfoJson(jobConfig);
        JSONObject mainTableJson = getMainTableJson();
        JSONObject headerMap = getHeaderMap(jobConfig);
        //组装接口请求参数
        paramDataJson.put("operationinfo", operationInfoJson);
        paramDataJson.put("mainTable", mainTableJson);
        paramDataJson.put("header", headerMap);
        return paramDataJson;
    }

    private JSONObject getHeaderMap(HaiRouProjectJobConfigDTO jobConfig) {

        //获取时间戳
        String currentTimeTamp = HaiRouRestApiUtils.getTimestamp();
        //header
        JSONObject header = new JSONObject();
        //封装header里的参数
        header.put("systemid", jobConfig.getSystemId());
        header.put("currentDateTime", currentTimeTamp);
        String md5Source = jobConfig.getSystemId() + jobConfig.getSystemPassword() + currentTimeTamp;
        String md5OfStr = HaiRouRestApiUtils.getMD5Str(md5Source).toLowerCase();
        //Md5是：系统标识+密码+时间戳 并且md5加密的结果
        header.put("Md5", md5OfStr);
        return header;
    }

    private JSONObject getMainTableJson() {

        //封装mainTable参数
        JSONObject mainTable = new JSONObject();
        mainTable.put("xsxmbm", "");
        return mainTable;
    }

    private JSONObject getOperationInfoJson(HaiRouProjectJobConfigDTO jobConfig) {

        //封装operationInfo参数
        JSONObject operationInfo = new JSONObject();
        //系统管理员为1
        operationInfo.put("operator", jobConfig.getOperator());
        return operationInfo;
    }

    /**
     * @param pageNo   查询页码（默认值：1）
     * @param pageSize 每页数据数量（默认值：10）
     * @return 分页查询信息
     */
    private JSONObject getPageInfo(int pageNo, int pageSize) {
        //封装pageInfo
        JSONObject pageInfo = new JSONObject();
        pageInfo.put("pageNo", pageNo);
        pageInfo.put("pageSize", pageSize);
        return pageInfo;
    }

}
