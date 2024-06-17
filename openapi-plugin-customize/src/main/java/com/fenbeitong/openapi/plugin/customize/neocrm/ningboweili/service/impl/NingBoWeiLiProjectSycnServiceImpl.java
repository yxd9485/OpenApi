package com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.service.impl;

import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.constant.NingBoWeiLiConstant;
import com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.dto.NingBoWeiLiCommonDetailsDto;
import com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.service.NingBoWeiLiProjectSycnService;
import com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.service.XiaoShouYiTransferService;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.project.service.OpenProjectService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.sdk.dto.project.CreateThirdProjectByBatchReqDTO;
import com.fenbeitong.openapi.sdk.dto.project.ListThirdProjectRespDTO;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Auther zhang.peng
 * @Date 2021/5/18
 */
@Slf4j
@ServiceAspect
@Service
public class NingBoWeiLiProjectSycnServiceImpl implements NingBoWeiLiProjectSycnService {

    @Autowired
    OpenProjectService openProjectService;

    @Autowired
    private OpenEmployeeServiceImpl openEmployeeService;

    @Autowired
    OpenSysConfigDao openSysConfigDao;

    @Autowired
    private XiaoShouYiTransferService xiaoShouYiTransferService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public String sycn(String companyId) {
        long start = System.currentTimeMillis();
        // 获取配置信息
        OpenSysConfig openSysConfig = getOpenSysConfig(companyId);
        if (ObjectUtils.isEmpty(openSysConfig)) {
            return "Failed";
        }
        // 获取token
        String token = getToken(openSysConfig);
        log.info("宁波伟立-对接销售易，获取 token:{}", token);
        // 获取销售易-销售机会数据
        List<NingBoWeiLiCommonDetailsDto.Records> opportunityDtos = getSourceData(token,companyId);
        // 查询本地数据
        ListThirdProjectRespDTO listThirdProjectRespDTO = null;
        long queryStart = System.currentTimeMillis();
        listThirdProjectRespDTO = openProjectService.getProjectByCompanyId(companyId);
        log.info("获取全量数据 ListThirdProjectRespDTO 查询耗时 {} ms", System.currentTimeMillis() - queryStart);
        // 用宁波伟立构建项目数据,并做数据对比
        CreateThirdProjectByBatchReqDTO createThirdProjectByBatchReqDTO = buildReqDTO(opportunityDtos,companyId);
        long updateStart = System.currentTimeMillis();
        openProjectService.projectUpdateOrAdd(listThirdProjectRespDTO, createThirdProjectByBatchReqDTO);
        log.info("项目更据耗新 -> projectUpdateOrAdd 耗时 {} ms", System.currentTimeMillis() - updateStart);
        log.info("数据同步 -> syncProject 总耗时 {} ms", System.currentTimeMillis() - start);
        return "success";
    }

    public OpenSysConfig getOpenSysConfig(String companyId){
        HashMap<String, Object> configMap = Maps.newHashMap();
        configMap.put("code", companyId);
        configMap.put("type", OpenSysConfigType.GET_CUSTOMIZE_COMPANY_TOKEN.getType());
        return openSysConfigDao.getOpenSysConfig(configMap);
    }

    public List<NingBoWeiLiCommonDetailsDto.Records> getSourceData(String token,String companyId){
        String accountInfoKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(NingBoWeiLiConstant.NINGBOWEILI_ACCOUNT_INFO, companyId));
        String accountCountKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(NingBoWeiLiConstant.NINGBOWEILI_ACCOUNT_COUNT, companyId));
        // 首先查询redis数据是否是最新的,通过数量比较
        Object accountCount = redisTemplate.opsForValue().get(accountCountKey);
        int account = null == accountCount ? 0 : (Integer) accountCount;
        NingBoWeiLiCommonDetailsDto detailsDto = xiaoShouYiTransferService.fetchDtoInfo(String.format(NingBoWeiLiConstant.FETCH_ACCOUNT_SQL,"limit 0,300"),token);
        // 如果数量对不上,重新加载缓存,全量更新
        if (account != detailsDto.getResult().getCount()){
            List<NingBoWeiLiCommonDetailsDto.Records> accountDtos = queryByPage(NingBoWeiLiConstant.FETCH_ACCOUNT_SQL,token);
            Map<String, String> accountInfoMap = new HashMap<>();
            for (NingBoWeiLiCommonDetailsDto.Records accountDto : accountDtos) {
                if (accountInfoMap.containsKey(accountDto.getId())){
                    continue;
                }
                accountInfoMap.put(accountDto.getId(),accountDto.getAccountName());
            }
            // 设置一周过期时间
            redisTemplate.opsForValue().set(accountCountKey,accountDtos.size(),7, TimeUnit.DAYS);
            redisTemplate.opsForHash().putAll(accountInfoKey,accountInfoMap);
            redisTemplate.expire(accountInfoKey,7,TimeUnit.DAYS);
        }
        List<NingBoWeiLiCommonDetailsDto.Records> opportunityDtos = queryByPage(NingBoWeiLiConstant.FETCH_OPPORTUNITY_SQL,token);
        // 赋值销售机会中的客户名称
        if ( CollectionUtils.isNotBlank(opportunityDtos) ){
            for (NingBoWeiLiCommonDetailsDto.Records opportunityDto : opportunityDtos) {
                opportunityDto.setAccountNameInOpportunity((String) redisTemplate.opsForHash().get(accountInfoKey,opportunityDto.getAccountId()));
            }
        }
        // 取销售机会的数据,客户名称只作为关联信息,不单独同步
        return opportunityDtos;
    }

    public CreateThirdProjectByBatchReqDTO buildReqDTO(List<NingBoWeiLiCommonDetailsDto.Records> opportunityDtos , String companyId){
        CreateThirdProjectByBatchReqDTO createThirdProjectByBatchReqDTO = new CreateThirdProjectByBatchReqDTO();
        createThirdProjectByBatchReqDTO.setUserId(openEmployeeService.superAdmin(companyId));
        createThirdProjectByBatchReqDTO.setCompanyId(companyId);
        // 1 自动 2 手动
        createThirdProjectByBatchReqDTO.setAutoFlag(2);
        // 是否有第三方ID  1：没有 2 第三方项目ID
        createThirdProjectByBatchReqDTO.setType(1);
        if (CollectionUtils.isBlank(opportunityDtos)){
            return createThirdProjectByBatchReqDTO;
        }
        List<CreateThirdProjectByBatchReqDTO.Projectinfo> projectinfoList = new ArrayList<>();
        opportunityDtos.forEach(record -> {
            CreateThirdProjectByBatchReqDTO.Projectinfo projectinfo = new CreateThirdProjectByBatchReqDTO.Projectinfo();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(record.getOpportunityName()).append(" (").append(record.getAccountNameInOpportunity()).append(")");
            projectinfo.setName(stringBuilder.toString());
            projectinfo.setThirdCostId(record.getId());
            projectinfo.setCode(record.getId());
            projectinfoList.add(projectinfo);
        });
        createThirdProjectByBatchReqDTO.setProjectInfo(projectinfoList);
        return createThirdProjectByBatchReqDTO;
    }

    /**
     * 获取token
     */
    private String getToken(OpenSysConfig openSysConfig) {
        Map<String, String> map = new HashMap<>();
        Map<String, String> resultMap = JsonUtils.toObj(RestHttpUtils.get(openSysConfig.getValue(), map), Map.class);
        return resultMap.get("access_token") == null ? "" : resultMap.get("access_token").toString();
    }

    public List<NingBoWeiLiCommonDetailsDto.Records> queryByPage(String sql,String token){
        int limit = 0 ;
        int offset = 300;
        int count = 0;
        List<NingBoWeiLiCommonDetailsDto.Records> result = new ArrayList<>();
        do {
            String sqlBak = sql;
            NingBoWeiLiCommonDetailsDto ningBoWeiLiCommonDetailsDto = null;
            if (limit == 0){
                sqlBak = String.format(sqlBak,"limit 0,300");
            } else {
                sqlBak = String.format(sqlBak,"limit " + ( limit * offset +1 ) + "," + offset);
            }
            limit++;
            ningBoWeiLiCommonDetailsDto = xiaoShouYiTransferService.fetchDtoInfo(sqlBak,token);
            if ( null == ningBoWeiLiCommonDetailsDto ){
                break;
            }
            count = ningBoWeiLiCommonDetailsDto.getResult().getCount();
            if ( CollectionUtils.isNotBlank(ningBoWeiLiCommonDetailsDto.getResult().getRecords()) ){
                result.addAll(ningBoWeiLiCommonDetailsDto.getResult().getRecords());
            }
        } while (count != 0);
        return result;
    }

}
