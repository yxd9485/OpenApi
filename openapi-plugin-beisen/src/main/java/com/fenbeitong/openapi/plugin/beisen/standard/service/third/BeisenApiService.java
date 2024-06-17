package com.fenbeitong.openapi.plugin.beisen.standard.service.third;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.beisen.common.constant.BeiSenConstant;
import com.fenbeitong.openapi.plugin.beisen.common.dto.*;
import com.fenbeitong.openapi.plugin.beisen.common.entity.BeisenCorp;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.etl.service.impl.EtlUtils;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 北森的接口api
 *
 * @author xiaowei
 * @date 2020/06/16
 */
@ServiceAspect
@Service
@Slf4j
public class BeisenApiService extends BeiSenApiGetToken{

    @Autowired
    OpenThirdScriptConfigDao openThirdScriptConfigDao;

    /**
     * 获取组织信息
     */
    public List<BeisenOrgListDTO.OrgDto> getOrgListData(BeisenParamConfig beisenParamConfig) {
        BeisenOrgListParam beisenOrgListParam = new BeisenOrgListParam();
        beisenOrgListParam.setStartTime(com.fenbeitong.openapi.plugin.util.StringUtils.isBlank(beisenParamConfig.getStartDate()) ? START_DATE : beisenParamConfig.getStartDate());
        beisenOrgListParam.setStopTime(com.fenbeitong.openapi.plugin.util.StringUtils.isBlank(beisenParamConfig.getEndDate()) ? DateUtils.toSimpleStr(new Date(), false) : beisenParamConfig.getEndDate());
        List<BeisenOrgListDTO.OrgDto> data = new ArrayList<>();
        Integer pageIndex = 1;
        Integer pageSize = 300;
        beisenOrgListParam.setWithDisabled(true);
        beisenOrgListParam.setPageSize(pageSize);
        Integer remainDateCount = 1;
        beisenOrgListParam.setColumns(new String[]{"name", "POIdOrgAdmin", "ShortName", "Status", "CreatedTime", "ModifiedTime"});
        String getTokenUrl;
        if (beisenParamConfig.getTokenUrlIsNew()) {
            getTokenUrl = BeiSenConstant.org_list_url_new;
        } else {
            getTokenUrl = MessageFormat.format(beisenBaseUrl.concat(beisenOrgListUrl), beisenParamConfig.getTenantId());
        }
        int count = 1;
        String result = null;
        final String beisenTokenKey = MessageFormat.format(OPEN_PLUGIN_BEISEN_REDIS_KEY, beisenParamConfig.getCompanyId());
        while (count <= BEISEN_TRY_COUNT) {
            try {
                while (remainDateCount > 0) {
                    beisenOrgListParam.setPageIndex(pageIndex);
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("Authorization", "Bearer " + getAccessToken(beisenParamConfig));
                    result = RestHttpUtils.postJson(getTokenUrl, httpHeaders, JsonUtils.toJson(beisenOrgListParam));
                    if (!StringUtils.isBlank(result)) {
                        BeisenOrgListDTO beisenOrgListDTO = JsonUtils.toObj(result, BeisenOrgListDTO.class);
                        data.addAll(beisenOrgListDTO.getData());
                        remainDateCount = beisenOrgListDTO.getTotal() - pageSize * pageIndex;
                        pageIndex++;
                    } else {
                        count++;
                    }
                }
                return data;
            } catch (Exception e) {
                if (e.getMessage().contains("401")) {
                    redisTemplate.delete(beisenTokenKey);
                }
                log.error("get beisen getOrgListData error", e);
                count++;
            }
        }
        return data;
    }

    /**
     * 获取员工信息
     */
    public List<BeisenEmployeeListDTO.EmployeeDto> getEmployeeListData(BeisenParamConfig beisenParamConfig) {
        final String beisenTokenKey = MessageFormat.format(OPEN_PLUGIN_BEISEN_REDIS_KEY, beisenParamConfig.getCompanyId());
        Integer pageIndex = 1;
        Integer pageSize = 300;
        Integer remainDateCount = 1;
        List<BeisenEmployeeListDTO.EmployeeDto> data = new ArrayList<>();
        BeisenEmployeeListParam beisenEmployeeListParam = new BeisenEmployeeListParam();
        OpenThirdScriptConfig employeeConfig = openThirdScriptConfigDao.getCommonScriptConfig(beisenParamConfig.getCompanyId(), EtlScriptType.EMPLOYEE_SYNC);
        if (!ObjectUtils.isEmpty(employeeConfig)) {
            beisenEmployeeListParam = JsonUtils.toObj(employeeConfig.getParamJson(), BeisenEmployeeListParam.class);
        } else {
            if (beisenParamConfig.getHour() != null && beisenParamConfig.getHour() > 0) {
                beisenEmployeeListParam.setStartTime(DateUtils.beforeHourToNowDate(beisenParamConfig.getHour()));
            } else {
                beisenEmployeeListParam.setStartTime(beisenParamConfig.getStartDate());
            }
            beisenEmployeeListParam.setStopTime(DateUtils.toSimpleStr(new Date(), false));
            beisenEmployeeListParam.setWithDisabled(true);
            beisenEmployeeListParam.setEmpStatus(new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
            beisenEmployeeListParam.setEmployType(new int[]{0, 2});
            beisenEmployeeListParam.setServiceType(new int[]{0});
            beisenEmployeeListParam.setPageSize(pageSize);
            beisenEmployeeListParam.setColumns(new String[]{"Name", "Email", "IDNumber", "MobilePhone", "Gender", "ModifiedTime", "OIdDepartment", "EmployeeStatus"});
        }

        String getTokenUrl;
        if (beisenParamConfig.getTokenUrlIsNew()) {
            getTokenUrl = BeiSenConstant.employee_list_url_new;
        } else {
            getTokenUrl = MessageFormat.format(beisenBaseUrl.concat(beisenEmployeeListUrl), beisenParamConfig.getTenantId());
        }
        int count = 1;
        String result = null;
        while (count <= BEISEN_TRY_COUNT) {
            try {
                while (remainDateCount > 0) {
                    beisenEmployeeListParam.setPageIndex(pageIndex);
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("Authorization", "Bearer " + getAccessToken(beisenParamConfig));
                    result = RestHttpUtils.postJson(getTokenUrl, httpHeaders, JsonUtils.toJson(beisenEmployeeListParam));
                    if (!StringUtils.isBlank(result)) {
                        BeisenEmployeeListDTO beisenEmployeeListDTO = new BeisenEmployeeListDTO();
                        if (!ObjectUtils.isEmpty(employeeConfig)) {
                            result = employeeBeforeSyncFilter(employeeConfig, result);
                        }
                        beisenEmployeeListDTO = JsonUtils.toObj(result, BeisenEmployeeListDTO.class);
                        data.addAll(beisenEmployeeListDTO.getData());
                        remainDateCount = beisenEmployeeListDTO.getTotal() - pageSize * pageIndex;
                        pageIndex++;
                    } else {
                        count++;
                    }
                }
                return data;
            } catch (Exception e) {
                if (e.getMessage().contains("401")) {
                    redisTemplate.delete(beisenTokenKey);
                }
                log.error("get beisen getEmployeeData error", e);
                count++;
            }
        }
        return data;
    }

    /**
     * 获取员工职务任职信息
     */
    public List<BeisenEmployeeJobListDTO> getEmployeeJobData(BeisenParamConfig beisenParamConfig, int[] ids) {
        BeisenEmployeeListParam beisenEmployeeListParam = new BeisenEmployeeListParam();
        beisenEmployeeListParam.setIds(ids);
        Integer pageIndex = 1;
        Integer pageSize = 300;
        Integer batchIds = 200;
        beisenEmployeeListParam.setPageSize(pageSize);
        int[] remainIds = beisenEmployeeListParam.getIds();
        List<BeisenEmployeeJobListDTO> beisenEmployeeJobListDTOS = new ArrayList<>();
        beisenEmployeeListParam.setColumns(new String[]{"OIdDepartment", "EmployeeStatus", "OIdJobLevel", "UserID", "ModifiedTime"});
        String getTokenUrl;
        if (beisenParamConfig.getTokenUrlIsNew()) {
            getTokenUrl = BeiSenConstant.employee_job_list_url;
        } else {
            getTokenUrl = MessageFormat.format(beisenBaseUrl.concat(beisenEmployeeJobListUrl), beisenParamConfig.getTenantId());
        }
        int count = 1;
        String result = null;
        final String beisenTokenKey = MessageFormat.format(OPEN_PLUGIN_BEISEN_REDIS_KEY, beisenParamConfig.getCompanyId());
        while (count <= BEISEN_TRY_COUNT) {
            try {
                while (remainIds.length > 0) {
                    beisenEmployeeListParam.setPageIndex(pageIndex);
                    if (remainIds.length > batchIds) {
                        beisenEmployeeListParam.setIds(Arrays.copyOfRange(remainIds, 0, batchIds));
                        remainIds = Arrays.copyOfRange(remainIds, batchIds, remainIds.length);
                    } else {
                        beisenEmployeeListParam.setIds(Arrays.copyOfRange(remainIds, 0, remainIds.length));
                        remainIds = new int[0];
                    }
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("Authorization", "Bearer " + getAccessToken(beisenParamConfig));
                    result = RestHttpUtils.postJson(getTokenUrl, httpHeaders, JsonUtils.toJson(beisenEmployeeListParam));
                    if (!StringUtils.isBlank(result)) {
                        beisenEmployeeJobListDTOS.addAll(JsonUtils.toObj(result, new TypeReference<List<BeisenEmployeeJobListDTO>>() {
                        }));
                        pageIndex++;
                    } else {
                        count++;
                    }
                }
                return beisenEmployeeJobListDTOS;
            } catch (Exception e) {
                if (e.getMessage().contains("401")) {
                    redisTemplate.delete(beisenTokenKey);
                }
                log.error("get beisen getEmployeeJobListData error", e);
                count++;
            }
        }
        return beisenEmployeeJobListDTOS;
    }

    /**
     * 获取员工职级信息
     */
    public List<BeisenEmployeeJobLevelListDTO> getEmployeeJobLevelData(BeisenParamConfig beisenParamConfig, String objectName) {
        BeisenEmployeeListParam beisenEmployeeListParam = new BeisenEmployeeListParam();
        Integer pageIndex = 1;
        Integer pageSize = 300;
        beisenEmployeeListParam.setPageSize(pageSize);
        Integer remainDateCount = 1;
        List<BeisenEmployeeJobLevelListDTO> beisenEmployeeJobListDTOS = new ArrayList<>();
        beisenEmployeeListParam.setColumns(new String[]{"Name", "_id", "CreatedTime", "ModifiedTime"});
        String getTokenUrl;
        if (beisenParamConfig.getTokenUrlIsNew()) {
            getTokenUrl = BeiSenConstant.business_object_data_url_new + "?objectName=JobLevel";
        } else {
            getTokenUrl = MessageFormat.format(beisenBaseUrl.concat(beisenObjectDataUrl), beisenParamConfig.getTenantId(), objectName);
        }
        int count = 1;
        String result = null;
        while (count <= BEISEN_TRY_COUNT) {
            try {
                while (remainDateCount > 0) {
                    beisenEmployeeListParam.setPageIndex(pageIndex);
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("Authorization", "Bearer " + getAccessToken(beisenParamConfig));
                    result = RestHttpUtils.postJson(getTokenUrl, httpHeaders, JsonUtils.toJson(beisenEmployeeListParam));
                    if (!StringUtils.isBlank(result)) {
                        BeisenEmployeeJobLevelDTO beisenEmployeeJobLevelDTO = JsonUtils.toObj(result, BeisenEmployeeJobLevelDTO.class);
                        beisenEmployeeJobListDTOS.addAll(beisenEmployeeJobLevelDTO.getData());
                        remainDateCount = beisenEmployeeJobLevelDTO.getTotal() - pageSize * pageIndex;
                        pageIndex++;
                    } else {
                        count++;
                    }
                }
                return beisenEmployeeJobListDTOS;
            } catch (Exception e) {
                log.error("get beisen getEmployeeJobLevelListData error", e);
                count++;
            }
        }
        log.info("companyId: {}  jobLevelData: {}", beisenParamConfig.getCompanyId(), JsonUtils.toJson(beisenEmployeeJobListDTOS));
        return beisenEmployeeJobListDTOS;
    }


    /**
     * 获取出差行程信息
     */
    public List<BeisenApplyListDTO.BusinessList> getApplyData(BeisenParamConfig beisenParamConfig) {
        List<BeisenApplyListDTO.BusinessList> data = new ArrayList<>();
        Integer pageIndex = 1;
        Integer pageSize = 300;
        BeisenApplyListParam beisenApplyListParam = new BeisenApplyListParam();

        beisenApplyListParam.setPageSize(beisenParamConfig.getPageSize() != null ? beisenApplyListParam.getPageSize() : pageSize);
        if (StringUtils.isBlank(beisenParamConfig.getStartDate()) || StringUtils.isBlank(beisenParamConfig.getEndDate())) {
            beisenApplyListParam.setStartDate(beisenParamConfig.getMine() != null && beisenParamConfig.getMine() > 0 ? DateUtils.beforeMineToNowDate(beisenParamConfig.getMine()) : (beisenParamConfig.getHour() != null && beisenParamConfig.getHour() > 0 ? DateUtils.beforeHourToNowDate(beisenParamConfig.getHour()) : DateUtils.beforeHourToNowDate(48)));
            beisenApplyListParam.setEndDate(DateUtils.toSimpleStr(new Date(), false));
        } else {
            beisenApplyListParam.setStartDate(beisenParamConfig.getStartDate());
            beisenApplyListParam.setEndDate(beisenParamConfig.getEndDate());
        }
        final String beisenTokenKey = MessageFormat.format(OPEN_PLUGIN_BEISEN_REDIS_KEY, beisenParamConfig.getCompanyId());
        beisenApplyListParam.setTenantId(beisenParamConfig.getTenantId());
        Integer remainDateCount = 1;
        String url = MessageFormat.format(beisenBaseUrl.concat(beisenBusinessApplyListUrl), beisenParamConfig.getTenantId());
        int count = 1;
        String result = null;
        pageIndex = beisenParamConfig.getPageIndex() != null ? beisenParamConfig.getPageIndex() : pageIndex;
        while (count <= BEISEN_TRY_COUNT) {
            try {
                while (remainDateCount > 0) {
                    beisenApplyListParam.setPageIndex(pageIndex);
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("Authorization", "Bearer " + getAccessToken(beisenParamConfig));
                    result = RestHttpUtils.postJson(url, httpHeaders, JsonUtils.toJson(beisenApplyListParam));
                    if (!StringUtils.isBlank(result)) {
                        BeisenApplyListDTO beisenApplyListDTO = JsonUtils.toObj(result, BeisenApplyListDTO.class);
                        if (beisenApplyListDTO != null && beisenApplyListDTO.getData() != null && beisenApplyListDTO.getData().getBusinessList().size() > 0) {
                            data.addAll(beisenApplyListDTO.getData().getBusinessList());
                        }
                        remainDateCount = beisenApplyListDTO.getData().getTotal() - pageSize * pageIndex;
                        pageIndex++;
                    } else {
                        count++;
                    }
                }
                return data;
            } catch (Exception e) {
                if (e.getMessage().contains("401")) {
                    redisTemplate.delete(beisenTokenKey);
                }
                log.error("get beisen getApplyData error", e);
                count++;
            }
        }
        return data;
    }

    /**
     * 获取出差行程信息
     */
    public List<BeisenOutwardApplyListDTO.OutwardInfo> getOutWardApplyData(BeisenParamConfig beisenParamConfig) {
        List<BeisenOutwardApplyListDTO.OutwardInfo> data = new ArrayList<>();
        Integer pageIndex = 1;
        Integer pageSize = 300;
        BeisenOutwardApplyListParam beisenOutwardApplyListParam = new BeisenOutwardApplyListParam();

        beisenOutwardApplyListParam.setPageSize(beisenParamConfig.getPageSize() != null ? beisenOutwardApplyListParam.getPageSize() : pageSize);
        if (StringUtils.isBlank(beisenParamConfig.getStartDate()) || StringUtils.isBlank(beisenParamConfig.getEndDate())) {
            beisenOutwardApplyListParam.setStartDate(beisenParamConfig.getMine() != null && beisenParamConfig.getMine() > 0 ? DateUtils.beforeMineToNowDate(beisenParamConfig.getMine()) : (beisenParamConfig.getHour() != null && beisenParamConfig.getHour() > 0 ? DateUtils.beforeHourToNowDate(beisenParamConfig.getHour()) : DateUtils.beforeHourToNowDate(48)));
            beisenOutwardApplyListParam.setEndDate(DateUtils.toSimpleStr(new Date(), false));
        } else {
            beisenOutwardApplyListParam.setStartDate(beisenParamConfig.getStartDate());
            beisenOutwardApplyListParam.setEndDate(beisenParamConfig.getEndDate());
        }
        final String beisenTokenKey = MessageFormat.format(OPEN_PLUGIN_BEISEN_REDIS_KEY, beisenParamConfig.getCompanyId());
        beisenOutwardApplyListParam.setTenantId(beisenParamConfig.getTenantId());
        Integer remainDateCount = 1;
        String url = MessageFormat.format(beisenBaseUrl.concat(beisenBusinessOutWardApplyListUrl), beisenParamConfig.getTenantId());
        int count = 1;
        String result = null;
        pageIndex = beisenParamConfig.getPageIndex() != null ? beisenParamConfig.getPageIndex() : pageIndex;
        while (count <= BEISEN_TRY_COUNT) {
            try {
                while (remainDateCount > 0) {
                    beisenOutwardApplyListParam.setPageIndex(pageIndex);
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("Authorization", "Bearer " + getAccessToken(beisenParamConfig));
                    result = RestHttpUtils.postJson(url, httpHeaders, JsonUtils.toJson(beisenOutwardApplyListParam));
                    if (!StringUtils.isBlank(result)) {
                        BeisenOutwardApplyListDTO beisenOutwardApplyListDTO = JsonUtils.toObj(result, BeisenOutwardApplyListDTO.class);
                        if (beisenOutwardApplyListDTO != null && beisenOutwardApplyListDTO.getData() != null && beisenOutwardApplyListDTO.getData().getOutwardInfos().size() > 0) {
                            data.addAll(beisenOutwardApplyListDTO.getData().getOutwardInfos());
                        }
                        remainDateCount = beisenOutwardApplyListDTO.getData().getTotal() - pageSize * pageIndex;
                        pageIndex++;
                    } else {
                        count++;
                    }
                }
                return data;
            } catch (Exception e) {
                if (e.getMessage().contains("401")) {
                    redisTemplate.delete(beisenTokenKey);
                }
                log.error("get beisen getOutwardApplyData error", e);
                count++;
            }
        }
        return data;
    }

    /**
     * 公共封获取数据接口
     */
    public String getData(BeisenParamConfig beisenParamConfig, String url, String reqData) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + getAccessToken(beisenParamConfig));
        return RestHttpUtils.postJson(url, httpHeaders, reqData);
    }



    public String employeeBeforeSyncFilter(OpenThirdScriptConfig employeeConfig, String result) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("result", result);
        }};
        if (org.apache.commons.lang3.StringUtils.isNotBlank(employeeConfig.getParamJson()) && JsonUtils.toObj(employeeConfig.getParamJson(), Map.class) != null) {
            params.putAll(JsonUtils.toObj(employeeConfig.getParamJson(), Map.class));
        }
        return (String) EtlUtils.execute(employeeConfig.getScript(), params);
    }


    /**
     * 按照审批通过时间获取出差数据
     */
    public List<BeiSenBusinessListDTO.Businesslist> getApprovalCompletedBusinessList(BeisenParamConfig beisenParamConfig) {
        List<BeiSenBusinessListDTO.Businesslist> data = new ArrayList<>();
        Integer pageIndex = 1;
        Integer pageSize = 300;
        Map reqMap = new HashMap();
        if (StringUtils.isBlank(beisenParamConfig.getStartDate()) || StringUtils.isBlank(beisenParamConfig.getEndDate())) {
            if (!ObjectUtils.isEmpty(beisenParamConfig.getDay())) {
                reqMap.put("StartDate", DateUtils.afterDay(beisenParamConfig.getDay()));
            } else {
                reqMap.put("StartDate", DateUtils.afterDay(-50));
            }
            reqMap.put("EndDate", DateUtils.afterDay(0));
        } else {
            reqMap.put("StartDate", beisenParamConfig.getStartDate());
            reqMap.put("EndDate", beisenParamConfig.getEndDate());
        }
        final String beisenTokenKey = MessageFormat.format(OPEN_PLUGIN_BEISEN_REDIS_KEY, beisenParamConfig.getCompanyId());
        Integer remainDateCount = 1;
        int count = 1;
        String result = null;
        pageIndex = beisenParamConfig.getPageIndex() != null ? beisenParamConfig.getPageIndex() : pageIndex;
        while (count <= BEISEN_TRY_COUNT) {
            try {
                while (remainDateCount > 0) {
                    reqMap.put("PageIndex", pageIndex);
                    reqMap.put("PageSize", pageSize);
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("Authorization", "Bearer " + getAccessToken(beisenParamConfig));
                    result = RestHttpUtils.postJson(BeiSenConstant.business_apply_list_url_new, httpHeaders, JsonUtils.toJson(reqMap));
                    if (!StringUtils.isBlank(result)) {
                        BeiSenBusinessListDTO beiSenBusinessListDTO = JsonUtils.toObj(result, BeiSenBusinessListDTO.class);
                        if (beiSenBusinessListDTO != null && beiSenBusinessListDTO.getData() != null && beiSenBusinessListDTO.getData().getBusinessList().size() > 0) {
                            data.addAll(beiSenBusinessListDTO.getData().getBusinessList());
                        }
                        remainDateCount = beiSenBusinessListDTO.getData().getTotal() - pageSize * pageIndex;
                        pageIndex++;
                    } else {
                        count++;
                    }
                }
                return data;
            } catch (Exception e) {
                if (e.getMessage().contains("401")) {
                    redisTemplate.delete(beisenTokenKey);
                }
                log.error("get beisen GetApprovalCompletedBusinessList error", e);
                count++;
            }
        }
        return data;
    }


    /**
     * 获取员工职级信息
     */
    public List<BeisenRankDTO.RankDto> getAllRank(BeisenParamConfig beisenParamConfig) {
        BeisenRankListParam rankListParam = new BeisenRankListParam();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.FORMAT_DATE_PATTERN_T_1);
        rankListParam.setStartTime(com.fenbeitong.openapi.plugin.util.StringUtils.isBlank(beisenParamConfig.getStartDate()) ? BeiSenConstant.START_DATE : beisenParamConfig.getStartDate());
        rankListParam.setStopTime(com.fenbeitong.openapi.plugin.util.StringUtils.isBlank(beisenParamConfig.getEndDate()) ? dateFormat.format(new Date()) : beisenParamConfig.getEndDate());
        rankListParam.setTimeWindowQueryType(1);
        rankListParam.setCapacity(BeiSenConstant.CAPACITY);
        rankListParam.setColumns(new String[]{"name", "oId","status","pOIdOrgAdmin", "createdTime", "modifiedTime","broadType"});
        rankListParam.setExtQueries(com.google.common.collect.Lists.newArrayList(BeisenRankListParam.BeisenExtQuery.builder().fieldName("BroadType").queryType(5).values(new String[]{"1"}).build()));
        rankListParam.setEnableTranslate(true);
        String scrollId = "";
        Integer pageIndex = 1;
        Integer remainDateCount = 1;
        List<BeisenRankDTO.RankDto> rankDTOList = new ArrayList<>();
        int count = 1;
        String result = null;
        while (count <= BEISEN_TRY_COUNT) {
            try {
                while (remainDateCount > 0) {
                    rankListParam.setScrollId(scrollId);
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("Authorization", "Bearer " + getAccessToken(beisenParamConfig));
                    log.info("参数:{}",JsonUtils.toJson(rankListParam));
                    result = RestHttpUtils.postJson(BeiSenConstant.BEISEN_RANK_URL_V5, httpHeaders, JsonUtils.toJson(rankListParam));
                    log.info("companyId: {},调用北森接口查询全部职级返回result:{}",beisenParamConfig.getCompanyId(),result);
                    if (!StringUtils.isBlank(result)) {
                        BeisenRankDTO rankDTO = JsonUtils.toObj(result, BeisenRankDTO.class);
                        rankDTOList.addAll(rankDTO.getData());
                        remainDateCount = rankDTO.getTotal() - BeiSenConstant.CAPACITY * pageIndex;
                        scrollId = rankDTO.getScrollId();
                        pageIndex++;
                    } else {
                        count++;
                    }
                }
                return rankDTOList;
            } catch (Exception e) {
                log.error("get beisen getAllRank error", e);
                count++;
            }
        }
        log.info("companyId: {}  rankDTOList: {}", beisenParamConfig.getCompanyId(), JsonUtils.toJson(rankDTOList));
        return rankDTOList;
    }

    /**
     * 查询所有员工的任职机构
     */
    public List<BeisenEmpOrgDTO> getEmployeeOrganization(BeisenParamConfig beisenParamConfig) {
        BeisenEmployeeOrganizationParam employeeOrganizationParam = new BeisenEmployeeOrganizationParam();
        employeeOrganizationParam.setEmpStatus(new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
        employeeOrganizationParam.setEmployType(new int[]{0, 2});
        employeeOrganizationParam.setServiceType(new int[]{0});
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.FORMAT_DATE_PATTERN_T_1);
        employeeOrganizationParam.setStartTime(com.fenbeitong.openapi.plugin.util.StringUtils.isBlank(beisenParamConfig.getStartDate()) ? BeiSenConstant.START_DATE : beisenParamConfig.getStartDate());
        employeeOrganizationParam.setStopTime(com.fenbeitong.openapi.plugin.util.StringUtils.isBlank(beisenParamConfig.getEndDate()) ? dateFormat.format(new Date()) : beisenParamConfig.getEndDate());
        employeeOrganizationParam.setTimeWindowQueryType(1);
        employeeOrganizationParam.setCapacity(BeiSenConstant.CAPACITY);
        employeeOrganizationParam.setColumns(new String[]{"userID","name","oIdOrganization"});
        employeeOrganizationParam.setEnableTranslate(true);
        String scrollId = "";
        Integer pageIndex = 1;
        Integer remainDateCount = 1;
        List<BeisenEmpOrgDTO> empOrgDTOList = Lists.newArrayList();
        List<BeisenEmployeeOrganizationDTO.BeisenEmployeeOrganizationInfo> employeeOrganizationList = Lists.newArrayList();
        int count = 1;
        String result = null;
        while (count <= BEISEN_TRY_COUNT) {
            try {
                while (remainDateCount > 0) {
                    employeeOrganizationParam.setScrollId(scrollId);
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("Authorization", "Bearer " + getAccessToken(beisenParamConfig));
                    log.info("参数:{}",JsonUtils.toJson(employeeOrganizationParam));
                    result = RestHttpUtils.postJson(BeiSenConstant.BEISEN_EMPLOYEE_ORGANIZATION_V5, httpHeaders, JsonUtils.toJson(employeeOrganizationParam));
                    log.info("companyId: {},调用北森接口查询所有员工任职机构返回result:{}",beisenParamConfig.getCompanyId(),result);
                    if (!StringUtils.isBlank(result)) {
                        BeisenEmployeeOrganizationDTO employeeOrganizationDTO = JsonUtils.toObj(result, BeisenEmployeeOrganizationDTO.class);
                        employeeOrganizationList.addAll(employeeOrganizationDTO.getData());
                        remainDateCount = employeeOrganizationDTO.getTotal() - BeiSenConstant.CAPACITY * pageIndex;
                        scrollId = employeeOrganizationDTO.getScrollId();
                        pageIndex++;
                    } else {
                        count++;
                    }
                }
                for (BeisenEmployeeOrganizationDTO.BeisenEmployeeOrganizationInfo info : employeeOrganizationList) {
                    BeisenEmpOrgDTO empOrgDTO = BeisenEmpOrgDTO.builder().userID(info.getEmployeeInfo().getUserID()).name(info.getEmployeeInfo().getName()).oIdOrganization(info.getRecordInfo().getOIdOrganization()).build();
                    empOrgDTOList.add(empOrgDTO);
                }
                return empOrgDTOList;
            } catch (Exception e) {
                log.error("get beisen getEmployeeOrganization error", e);
                count++;
            }
        }
        log.info("companyId: {}  rankDTOList: {}", beisenParamConfig.getCompanyId(), JsonUtils.toJson(empOrgDTOList));
        return empOrgDTOList;
    }

    /**
     * 轮询请求接口，直到结果data中没有数据则停止
     */
    public List<Object> getResultByTimeWindow(BeisenReqBaseDTO beisenReqBaseDTO, BeisenCorp beisenCorp, String url) {
        boolean isOver = false;
        List<Object> dataList = new ArrayList<>();
        do {
            BeisenResultBaseDTO resultBaseDTO = postUrlWithToken(beisenReqBaseDTO, url, beisenCorp);
            if (CollectionUtils.isBlank(resultBaseDTO.getData())) {
                isOver = true;
            } else {
                //本批次的ScrollId 依赖上次查询结果
                beisenReqBaseDTO.setScrollId(resultBaseDTO.getScrollId());
                dataList.addAll(resultBaseDTO.getData());
            }
        } while (!isOver);
        return dataList;
    }

}
