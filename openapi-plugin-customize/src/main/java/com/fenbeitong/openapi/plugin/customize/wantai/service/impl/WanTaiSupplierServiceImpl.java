package com.fenbeitong.openapi.plugin.customize.wantai.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.customize.common.exception.OpenApiCustomizeException;
import com.fenbeitong.openapi.plugin.customize.wantai.constant.WanTaiArchiveConstant;
import com.fenbeitong.openapi.plugin.customize.wantai.dto.*;
import com.fenbeitong.openapi.plugin.customize.wantai.service.WanTaiSupplierService;
import com.fenbeitong.openapi.plugin.support.customform.dto.CustomFormDetailResDTO;
import com.fenbeitong.openapi.plugin.support.customform.dto.CustomFormListResDTO;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenSyncConstant;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.supplier.dto.ICustomSupAddOrUpdateReqDTO;
import com.fenbeitong.openapi.plugin.support.supplier.dto.ICustomSupDetailDTO;
import com.fenbeitong.openapi.plugin.support.supplier.service.OpenCustomSupplierService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.support.util.SuperAdminUtils;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.fenbeitong.openapi.plugin.customize.wantai.constant.WanTaiArchiveConstant.*;

/**
 * @author zhangjindong
 * @date 2022/9/21 8:45 PM
 */
@ServiceAspect
@Service
@Slf4j
public class WanTaiSupplierServiceImpl implements WanTaiSupplierService {

    String SUPPLIER_GET_DATA_REDIS_KEY = "supplier_get_data:{0}";
    @Value("${host.tiger}")
    private String tigerHost;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    OpenCustomSupplierService openCustomSupplierService;

    @Autowired
    private SuperAdminUtils superAdminUtils;

    @Override
    public void syncNccSupplier(NCCSupplierSyncReqDTO req) {
        String lockKey = req.getLockKey();
        lockKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY,
            MessageFormat.format(SUPPLIER_GET_DATA_REDIS_KEY, lockKey));
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, 7200 * 1000L);
        if (lockTime > 0) {
            try {
                syncSupplier(req);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("未获取到锁");
        }
    }

    private void syncSupplier(NCCSupplierSyncReqDTO req) {
        // 获取token
        String token = getERPToken(req.getHost(), req.getAccessKey(), req.getSecretKey());
        SupplierQueryRespDTO supplierQueryRespDTO;
        Object syncEndTime = redisTemplate.opsForValue().get(NCC_SUPPLIER_SYNC_END_TIME);
        log.info("从缓存中获取上次同步结束时间点{}", JsonUtils.toJson(syncEndTime));
        SimpleDateFormat df = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        String currentTime = df.format(new Date());
        List<ICustomSupAddOrUpdateReqDTO> supList;
        if (ObjectUtils.isEmpty(syncEndTime)) {
            // 缓存中没有同步结束时间,第一次全量同步
            supplierQueryRespDTO = getSupplier(token, req.getHost(), DEFAULT_START_TIME, currentTime);
            if (!ObjectUtils.isEmpty(supplierQueryRespDTO) && !ObjectUtils.isEmpty(supplierQueryRespDTO.getData())) {
                supList = buildRequestParam(supplierQueryRespDTO, req);
                // 按公司分组全量同步,只同步启用状态下供应商
                supList.forEach(s -> openCustomSupplierService.syncAllCustomSuppliers(s, OpenType.UNKNOW.getType(), true));
                redisTemplate.opsForValue().set(NCC_SUPPLIER_SYNC_END_TIME, currentTime);
            }
        } else {
            // 按公司分组增量同步
            supplierQueryRespDTO = getSupplier(token, req.getHost(), (String) syncEndTime, currentTime);
            if (!ObjectUtils.isEmpty(supplierQueryRespDTO) && !ObjectUtils.isEmpty(supplierQueryRespDTO.getData())) {
                supList = buildRequestParam(supplierQueryRespDTO, req);
                supList.forEach(s -> openCustomSupplierService.syncAllCustomSuppliers(s, OpenType.UNKNOW.getType(), true));
                redisTemplate.opsForValue().set(NCC_SUPPLIER_SYNC_END_TIME, currentTime);
                // 分组更新需停用供应商
                updateSupState(supplierQueryRespDTO, req);
            }
        }
    }

    private String getERPToken(String host, String accessKey, String secretKey) {
        MultiValueMap param = new LinkedMultiValueMap();
        param.add(ACCESS_KEY, accessKey);
        param.add(SECRET_KEY, secretKey);
        String result = RestHttpUtils.postForm(host.concat(WanTaiArchiveConstant.URL_ERP_TOKEN), param);
        ERPTokenRespDTO erpTokenRespDTO = JsonUtils.toObj(result, ERPTokenRespDTO.class);
        if (erpTokenRespDTO == null || erpTokenRespDTO.getCode() != 0 || StringUtils.isBlank(
            erpTokenRespDTO.getData())) {
            throw new OpenApiCustomizeException(500, "万泰获取ERP token失败：" + result);
        }
        return erpTokenRespDTO.getData();
    }


    private SupplierQueryRespDTO getSupplier(String token, String host, String startTime, String endTime) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(TOKEN, token);
        Map param = new HashMap();
        param.put("beginTime", startTime);
        param.put("endTime", endTime);
        String result = RestHttpUtils.postJson(host.concat(WanTaiArchiveConstant.URL_ERP_SUPPLIER), httpHeaders, JsonUtils.toJson(param));
        log.info("获取ncc supplier 返回{}", result);

        SupplierQueryRespDTO archiveDataRespDTO = JsonUtils.toObj(result,SupplierQueryRespDTO.class);
        if (archiveDataRespDTO == null || archiveDataRespDTO.getCode() != 0 || ObjectUtils.isEmpty(
            archiveDataRespDTO.getData())) {
            log.warn("获取万泰供应商获取失败或返回数据为空");
            return null;
        }

        return archiveDataRespDTO;
    }

    public List<ICustomSupAddOrUpdateReqDTO> buildRequestParam(SupplierQueryRespDTO supplierQueryRespDTO, NCCSupplierSyncReqDTO req) {
        Map<String, String> ztMap = req.getExtAttr().get("zt_company_mapping");
        List<ICustomSupAddOrUpdateReqDTO> paramList = Lists.newLinkedList();
        List<SupplierQueryRespDTO.SupplierDataDTO> srcList = supplierQueryRespDTO.getData();
        // 按企业分组组装参数
        Map<String, List<SupplierQueryRespDTO.SupplierDataDTO>> groupMap = srcList.stream().collect(Collectors.groupingBy(SupplierQueryRespDTO.SupplierDataDTO::getIdCom));
        // 遍历分组按企业组装参数
        for (String key : groupMap.keySet()) {
            String supplierId = null;
            String customerId = null;
            String companyId = ztMap.get(key);
            if (StringUtils.isBlank(companyId)) {
                continue;
            }
            String thirdEmpId = superAdminUtils.superAdminThirdEmployeeId(companyId);
            String formId = getFormIdByCompanyId(companyId);
            CustomFormDetailResDTO customFormDetailResDTO = getFormDetail(formId, companyId);
            List<CustomFormDetailResDTO.BusinessControls> businessControls = customFormDetailResDTO.getBusinessControls();
            Optional<List<CustomFormDetailResDTO.Options>> options = businessControls.stream().filter(h -> "供应商类型".equals(h.getName())).findFirst().map(CustomFormDetailResDTO.BusinessControls::getOptions);
            if (!ObjectUtils.isEmpty(options) && !ObjectUtils.isEmpty(options.get())) {
                List<CustomFormDetailResDTO.Options> optionList = options.get();
                supplierId = optionList.stream().filter(h -> "供应商".equals(h.getName())).findFirst().get().getId();
                customerId = optionList.stream().filter(h -> "客户".equals(h.getName())).findFirst().get().getId();
            }
            ICustomSupAddOrUpdateReqDTO supplierCreateReqDTO = new ICustomSupAddOrUpdateReqDTO();
            List<ICustomSupDetailDTO> supplierList = Lists.newArrayList();
            for (SupplierQueryRespDTO.SupplierDataDTO s : groupMap.get(key)) {
                if (StringUtils.isBlank(s.getIdCorr())) {
                    continue;
                }
                supplierCreateReqDTO.setCompanyId(ztMap.get(s.getIdCom()));
                supplierCreateReqDTO.setFormId(formId);
                ICustomSupDetailDTO supDetail = new ICustomSupDetailDTO();
                supDetail.setCode(s.getIdCorr());
                supDetail.setName(s.getNameCorr());
                supDetail.setState(("Y".equals(s.getFlagInvalid())) ? 0 : 1);
                supDetail.setTaxCode(s.getVarTaxno());
                supDetail.setThirdCreatorId(thirdEmpId);
                supDetail.setThirdId(s.getIdCorr());
                ICustomSupDetailDTO.Type supplierType = new ICustomSupDetailDTO.Type();
                supplierType.setId(0 == s.getFlagCorr() ? supplierId : customerId);
                supplierType.setName(0 == s.getFlagCorr() ? "供应商" : "客户");
                supDetail.setTypes(Lists.newArrayList(supplierType));
                supDetail.setRemark(s.getVarRemark());
                List<ICustomSupDetailDTO.BankAccount> bankAccounts = Lists.newArrayList();
                for (SupplierQueryRespDTO.SupplierAccoutDTO account : s.getAccouts()) {
                    if (ObjectUtils.isEmpty(account.getIdAcctpk()) || ObjectUtils.isEmpty(account.getVarBankname()) || ObjectUtils.isEmpty(account.getVarBankno())) {
                        continue;
                    }
                    ICustomSupDetailDTO.BankAccount bankAccount = new ICustomSupDetailDTO.BankAccount();
                    bankAccount.setSubbranchId(account.getVarBankno());
                    bankAccount.setCode(account.getVarBankacct());
                    bankAccount.setName(account.getVarBankname());
                    // 三方系统供应商账户ID
                    bankAccount.setThirdId(account.getIdAcctpk());
                    bankAccounts.add(bankAccount);
                }
                supDetail.setBankAccounts(bankAccounts);

                supplierList.add(supDetail);
                supplierCreateReqDTO.setSuppliers(supplierList);
            }
            paramList.add(supplierCreateReqDTO);
        }
        return paramList;
    }

    /**
     * 获取模版详情
     */
    private CustomFormDetailResDTO getFormDetail(String formId, String companyId) {
        Map param = new HashMap<>();
        param.put("form_type", 1);
        param.put("form_id", formId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("appId", companyId);
        String result = RestHttpUtils.postJson(tigerHost.concat("/openapi/common/custom_form/v1/form_detail"), httpHeaders, JsonUtils.toJson(param));
        TigerFormDetailRespDTO tigerRes = JsonUtils.toObj(result, TigerFormDetailRespDTO.class);
        if (0 != tigerRes.getCode()) {
            throw new FinhubException(-999, "查询模版详情异常");
        }
        return tigerRes.getData();
    }

    /**
     * 获取模版详情
     */

    private String getFormIdByCompanyId(String companyId) {
        Map param = new HashMap<>();
        param.put("form_type", 1);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("appId", companyId);
        String result = RestHttpUtils.postJson(tigerHost.concat("/openapi/common/custom_form/v1/form_list"), httpHeaders, JsonUtils.toJson(param));
        TigerFormListRespDTO tigerRes = JsonUtils.toObj(result, TigerFormListRespDTO.class);
        if (ObjectUtils.isEmpty(tigerRes.data) || ObjectUtils.isEmpty(tigerRes.getData().getForms())) {
            throw new FinhubException(-999, "查询模版列表异常");
        }
        CustomFormListResDTO.FormDetail form = tigerRes.getData().getForms().get(0);
        if (ObjectUtils.isEmpty(form)) {
            throw new FinhubException(-999, "查询供应商异常");
        }
        return form.getId();
    }

    private List<NCCSupplierUpdateDTO> buildUpdateStopStateSup(SupplierQueryRespDTO supplierQueryRespDTO, NCCSupplierSyncReqDTO req) {
        List<NCCSupplierUpdateDTO> stopSups = Lists.newArrayList();
        Map<String, String> ztMap = req.getExtAttr().get("zt_company_mapping");
        List<SupplierQueryRespDTO.SupplierDataDTO> srcList = supplierQueryRespDTO.getData();
        // 过滤启用状态供应商
        List<SupplierQueryRespDTO.SupplierDataDTO> updateStopSups = srcList.stream().filter(supplierDataDTO -> "Y".equals(supplierDataDTO.getFlagInvalid())).collect(Collectors.toList());

        // 按企业分组组装参数
        Map<String, List<SupplierQueryRespDTO.SupplierDataDTO>> groupMap = updateStopSups.stream().collect(Collectors.groupingBy(SupplierQueryRespDTO.SupplierDataDTO::getIdCom));
        for (String key : groupMap.keySet()) {
            if(StringUtils.isBlank(ztMap.get(key))){
                continue;
            }
            List<String> ids = groupMap.get(key).stream().map(SupplierQueryRespDTO.SupplierDataDTO::getIdCorr).collect(Collectors.toList());
            NCCSupplierUpdateDTO nCCSupplierUpdateDTO = NCCSupplierUpdateDTO.builder()
                .ids(ids).companyId(ztMap.get(key)).build();
            stopSups.add(nCCSupplierUpdateDTO);
        }
        return stopSups;
    }

    private void updateSupState(SupplierQueryRespDTO supplierQueryRespDTO, NCCSupplierSyncReqDTO req) {
        List<NCCSupplierUpdateDTO> updateStopDTOS = buildUpdateStopStateSup(supplierQueryRespDTO, req);
        if (CollectionUtils.isNotBlank(updateStopDTOS)) {
            updateStopDTOS.forEach(t -> {
                List<List<String>> groupIds = CollectionUtils.batch(t.getIds(), OpenSyncConstant.CUSTOM_SUPPLIER_BATCH_MAX);
                for (List<String> list : groupIds) {
                    openCustomSupplierService.updateStatusAndIntoDb(OpenType.UNKNOW.getType(), t.getCompanyId(), 0, list);
                }
            });
        }
    }

}
