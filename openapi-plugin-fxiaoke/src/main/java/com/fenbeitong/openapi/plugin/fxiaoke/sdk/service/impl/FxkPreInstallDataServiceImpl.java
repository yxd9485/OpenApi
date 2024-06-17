package com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.etl.service.IEtlStrategyService;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.FxkResponseCode;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.exception.OpenApiFxkException;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dao.FxiaokeCorpAppDao;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dao.FxiaokePreInstallObjDao;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.*;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity.FxiaokeCorpApp;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkAccessTokenService;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkPreInstallDataService;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.project.service.impl.OpenProjectServiceImpl;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.sdk.dto.project.CreateThirdProjectByBatchReqDTO;
import com.fenbeitong.openapi.sdk.dto.project.ListThirdProjectRespDTO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ServiceAspect
@Service
@Slf4j
public class FxkPreInstallDataServiceImpl implements IFxkPreInstallDataService {
    @Value("${fenxiaoke.host}")
    private String fxiaokeHost;

    @Autowired
    private RestHttpUtils httpUtils;
    @Autowired
    FxiaokeCorpAppDao fxiaokeCorpAppDao;
    @Autowired
    IFxkAccessTokenService iFxkAccessTokenService;
    @Autowired
    FxiaokePreInstallObjDao fxiaokePreInstallObjDao;
    @Autowired
    OpenProjectServiceImpl openProjectService;
    @Autowired
    PluginCorpDefinitionDao pluginCorpDefinitionDao;
    @Autowired
    private OpenEmployeeServiceImpl openEmployeeService;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Override
    public FxkGetCustomDataRespDTO getCustomData(FxkGetCustomDataReqDTO req) {
        return null;
    }

    @Autowired
    private IEtlStrategyService voucherStrategyService;

    private static final Long FEN_XIANG_XIAO_KE_COMPANY_CONFIG = 2600L;

    public FxkGetCustomDataListRespDTO getFxkPrInstallDataList(FxkGetCustomDataListReqDTO req) {
        String url = fxiaokeHost + "/cgi/crm/v2/data/query";
        String result = httpUtils.postJson(url, JsonUtils.toJson(req));
        FxkGetCustomDataListRespDTO fxkGetCustomDataListRespDTO = JsonUtils.toObj(result, FxkGetCustomDataListRespDTO.class);
        return fxkGetCustomDataListRespDTO;
    }


    public List<Map> pullPreInstallDataList(String corpId, String apiName, String apiUserId) {
        if (StringUtils.isBlank(corpId)) {
            throw new OpenApiFxkException(FxkResponseCode.FXK_CORP_ID_PARAM_ERROR);
        }
        if (StringUtils.isBlank(apiName)) {
            throw new OpenApiFxkException(FxkResponseCode.FXK_API_NAME_ERROR);
        }
        Example example = new Example(FxiaokeCorpApp.class);
        example.createCriteria().andEqualTo("corpId", corpId)
                .andEqualTo("appState", 0);
        FxiaokeCorpApp fxiaokeCorpApp = fxiaokeCorpAppDao.getByExample(example);
        if (!ObjectUtils.isEmpty(fxiaokeCorpApp)) {
            FxkGetCorpAccessTokenReqDTO tokenReqDTO = FxkGetCorpAccessTokenReqDTO.builder()
                    .appId(fxiaokeCorpApp.getAppId())
                    .appSecret(fxiaokeCorpApp.getAppSecret())
                    .permanentCode(fxiaokeCorpApp.getPermanent())
                    .build();
            FxkGetCorpAccessTokenRespDTO corpAccessToken = iFxkAccessTokenService.getCorpAccessToken(tokenReqDTO);
            if (!ObjectUtils.isEmpty(corpAccessToken)) {
                Integer errorCode = corpAccessToken.getErrorCode();
                if (0 == errorCode) {
                    int pageSize = 100;
                    int offset = 0;

                    //分页查询
                    List<Map> customDataList = new ArrayList<>();
//                    LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);//当天零点
//                    long todayBeginTimeL = todayStart.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    FxkGetCustomDataListRespDTO fxkPrInstallDataList = null;
                    do {
                        FxkGetCustomDataListReqDTO.FxkGetCustomDataListFilter fxkGetCustomDataListFilter = new FxkGetCustomDataListReqDTO.FxkGetCustomDataListFilter();
                        fxkGetCustomDataListFilter.setFieldName("is_deleted");
                        fxkGetCustomDataListFilter.setFieldValues(Lists.newArrayList("false"));
                        fxkGetCustomDataListFilter.setOperator("EQ");
                        FxkGetCustomDataListReqDTO.SearchQueryInfo searchQueryInfo = new FxkGetCustomDataListReqDTO.SearchQueryInfo();
                        searchQueryInfo.setOffset(offset * pageSize);
                        searchQueryInfo.setLimit(pageSize);
                        searchQueryInfo.setFilters(Lists.newArrayList(fxkGetCustomDataListFilter));

                        FxkGetCustomDataListReqDTO.FxkGetCustomDataListCondition build = FxkGetCustomDataListReqDTO.FxkGetCustomDataListCondition.builder()
                                .dataObjectApiName(apiName)
                                .searchqueryInfo(searchQueryInfo)
                                .build();

                        FxkGetCustomDataListReqDTO fxkGetCustomDataListReqDTO = FxkGetCustomDataListReqDTO.builder()
                                .corpId(corpId)
                                .corpAccessToken(corpAccessToken.getCorpAccessToken())
                                .currentOpenUserId(apiUserId)
                                .data(build)
                                .build();
                        //根据条件过滤分贝通需要的审批数据
                        fxkPrInstallDataList = getFxkPrInstallDataList(fxkGetCustomDataListReqDTO);
                        if (ObjectUtils.isEmpty(fxkPrInstallDataList)) {
                            log.info("拉取纷享销客预置对象空 {}",fxkPrInstallDataList);
                            throw new OpenApiFxkException(FxkResponseCode.FXK_PREINSTALL_DATA_ERROR);
                        }
                        Integer errorCode1 = fxkPrInstallDataList.getErrorCode();
                        if (0 != errorCode1) {
                            log.info("拉取纷享销客预置对象异常 {}",errorCode1);
                            throw new OpenApiFxkException(FxkResponseCode.FXK_PREINSTALL_DATA_ERROR);
                        }
                        List<Map> dataList = fxkPrInstallDataList.getData().getDataList();
                        customDataList.addAll(dataList);
                        offset++;
//                        fxkPrInstallDataList != null && fxkPrInstallDataList.getErrorCode() == 0 &&
                    } while (!ObjectUtils.isEmpty(fxkPrInstallDataList.getData().getDataList()));
                    log.info("获取纷享销客预设数据：{}", "拉取纷享销客预知对象完成");
                    return customDataList;
                }
            }
            log.info("获取纷享销客access_token 失败");
        }
        return null;
    }

    public String fxkPreInstallDataHandle(String corpId, String apiName, String apiUserId) {
        long start = System.currentTimeMillis();
        //加载源数据
        List<Map> maps = pullPreInstallDataList(corpId, apiName, apiUserId);

        if (ObjectUtils.isEmpty(maps)) {//空数据
            log.info("纷享销客拉取预置对象失败");
            throw new OpenApiFxkException(FxkResponseCode.FXK_PREINSTALL_DATA_ERROR);
        }
        PluginCorpDefinition corpByThirdCorpId = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        if (ObjectUtils.isEmpty(corpByThirdCorpId)) {
            log.info("纷享销客拉取预置对象企业为注册 {}",corpId);
            throw new OpenApiFxkException(FxkResponseCode.FXK_CORP_UN_REGIST);
        }
        String companyId = corpByThirdCorpId.getAppId();
        //加载分贝数据Map
        ListThirdProjectRespDTO listThirdProjectRespDTO = null;
        long queryStart = System.currentTimeMillis();
        listThirdProjectRespDTO = openProjectService.getProjectByCompanyId(companyId);
        log.info("获取全量数据全量数据 ListThirdProjectRespDTO 查询耗时 {} ms", System.currentTimeMillis() - queryStart);
        CreateThirdProjectByBatchReqDTO createThirdProjectByBatchReqDTO = new CreateThirdProjectByBatchReqDTO();
        List<CreateThirdProjectByBatchReqDTO.Projectinfo> projectinfoList = new ArrayList<>();
//        maps.stream().forEach(map -> {
//            CreateThirdProjectByBatchReqDTO.Projectinfo projectinfo = new CreateThirdProjectByBatchReqDTO.Projectinfo();
//            projectinfo.setName((String) map.get("name"));
//            projectinfo.setThirdCostId((String) map.get("_id"));
//            projectinfo.setCode((String) map.get("_id"));
//            projectinfoList.add(projectinfo);
//        });
        OpenMsgSetup openMsgSetup=openMsgSetupDao.getByCompanyFlag(companyId);
        Long strategyId=null;
        if(ObjectUtil.isNotNull(openMsgSetup)){
            strategyId=Long.parseLong(openMsgSetup.getItemCode());
        }else {
            strategyId=FEN_XIANG_XIAO_KE_COMPANY_CONFIG;
        }
        List<Map<String, Object>>  transform=voucherStrategyService.transfer(strategyId, JsonUtils.toObj(JsonUtils.toJson(maps), new TypeReference<List<Map<String, Object>>>() {}));
        transform.stream().forEach(map -> {
            CreateThirdProjectByBatchReqDTO.Projectinfo projectinfo = new CreateThirdProjectByBatchReqDTO.Projectinfo();
            projectinfo.setName((String) map.get("name"));
            projectinfo.setThirdCostId((String) map.get("thirdCostId"));
            projectinfo.setCode((String) map.get("code"));
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
        log.info("添加或更新纷享销客预置对象 {}",JsonUtils.toJson(createThirdProjectByBatchReqDTO));
        openProjectService.projectUpdateOrAdd(listThirdProjectRespDTO, createThirdProjectByBatchReqDTO);
        log.info("纷享销客项目更新 -> projectUpdateOrAdd 耗时 {} ms", System.currentTimeMillis() - updateStart);
        log.info("纷享销客数据同步 -> syncProject 总耗时 {} ms", System.currentTimeMillis() - start);
        return "successed";
    }


}
