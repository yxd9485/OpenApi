package com.fenbeitong.openapi.plugin.customize.chenguangrongxin.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.core.util.HttpServletRequestUtils;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.constant.ChenguangrongxinConstant;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.dao.CustomizeJiandaoyunCorpDao;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.dto.ApplyDetailDTO;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.dto.BankPaymentPushDTO;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.dto.BankPaymentRequestDTO;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.dto.CostAttributionsDTO;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.entity.CustomizeJiandaoyunCorp;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.service.IBankPaymentService;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.service.common.ChenguangCommonServiceImpl;
import com.fenbeitong.openapi.plugin.customize.common.exception.OpenApiCustomizeException;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @ClassName BankPaymentServiceImpl
 * @Description 辰光融信对公付款实现类
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/9/20
 **/
@Service
@Slf4j
public class BankPaymentServiceImpl implements IBankPaymentService {

    @Autowired
    private ChenguangCommonServiceImpl chenguangCommonService;

    @Autowired
    CustomizeJiandaoyunCorpDao jiandaoyunCorpDao;

    @Value("${host.tiger}")
    private String hostTiger;

    @Value("${jiandaoyun.bank-payment-uri}")
    private String bankPaymentUrl;


    @Override
    public void pushData(HttpServletRequest request, String companyId) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        BankPaymentRequestDTO bankPaymentRequestDTO = JsonUtils.toObj(requestBody, BankPaymentRequestDTO.class);
        log.info("对公付款申请单推送开始,公司:{},bankPaymentRequestDTO:{}", companyId, bankPaymentRequestDTO);
        long start = System.currentTimeMillis();
        //查询简道云配置
        CustomizeJiandaoyunCorp jiandaoyunCorp = jiandaoyunCorpDao.getByCompanyId(companyId);
        //已付款状态单据推送
        if (ChenguangrongxinConstant.COMPLETED_PAY.equals(bankPaymentRequestDTO.getPaymentState())) {
            //查询对公付款申请单明细
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("appId", companyId);
            String detail = RestHttpUtils.postJson(hostTiger + ChenguangrongxinConstant.APPLY_DETAIL_URL_SUFFIX, httpHeaders, JsonUtils.toJson(bankPaymentRequestDTO));
            Map<String, Object> detailMap = JsonUtils.toObj(detail, Map.class);
            Map<String, Object> data = detailMap == null ? Maps.newHashMap() : (Map<String, Object>) detailMap.get("data");
            if (ObjectUtils.isEmpty(data)) {
                log.info("查询审批详情失败,公司:{},审批单号:{}", companyId, bankPaymentRequestDTO.getApplyId());
                throw new OpenApiCustomizeException(-9999, "查询审批详情失败,公司:{},审批单号:{}", companyId, bankPaymentRequestDTO.getApplyId());
            }
            //参数封装
            BankPaymentPushDTO paymentPushDTO = buildPushData(data);
            //推送对公付款单到简道云
            chenguangCommonService.push(bankPaymentUrl, paymentPushDTO.getJdyThirdApplyId().getValue(), paymentPushDTO, jiandaoyunCorp);
        } else {
            log.info("对公付款申请单无需推送,公司:{},申请单id:{}", companyId, bankPaymentRequestDTO.getApplyId());
        }
        long end = System.currentTimeMillis();
        log.info("公司:{},对公付款申请单推送结束，用时{}分钟{}秒...", companyId, (end - start) / 60000L, (end - start) % 60000L / 1000L);
    }

    /**
     * 参数转换
     *
     * @param data 申请单明细
     * @return paymentPushDTO 待推送的单据
     */
    private BankPaymentPushDTO buildPushData(Map<String, Object> data) {
        BankPaymentPushDTO paymentPushDTO = new BankPaymentPushDTO();
        //申请单内容
        ApplyDetailDTO apply = JsonUtils.toObj(JsonUtils.toJson(data.get("apply")), ApplyDetailDTO.class);
        //三方供应商id
        String thirdSupplierId = (String) MapUtils.getValueByExpress(data, "supplier:third_id");
        if (!ObjectUtils.isEmpty(apply)) {
            paymentPushDTO.setJdyThirdApplyId(new BankPaymentPushDTO.Entry<>(apply.getApplyId()));
            paymentPushDTO.setJdyThirdEmployeeId(new BankPaymentPushDTO.Entry<>(apply.getThirdEmployeeId()));
            paymentPushDTO.setJdyEstimatedTotalAmount(new BankPaymentPushDTO.Entry<>(apply.getEstimatedTotalAmount()));
            paymentPushDTO.setJdyPaymentName(new BankPaymentPushDTO.Entry<>(apply.getName()));
            paymentPushDTO.setJdySupplierId(new BankPaymentPushDTO.Entry<>(StringUtils.isEmpty(thirdSupplierId) ? "" : thirdSupplierId));
            paymentPushDTO.setJdyPaymentTime(new BankPaymentPushDTO.Entry<>(StringUtils.isEmpty(apply.getPaymentTime()) ? "" : apply.getPaymentTime()));
            paymentPushDTO.setJdyPaymentUse(new BankPaymentPushDTO.Entry<>(apply.getPaymentUse()));
            paymentPushDTO.setJdyApplyReason(new BankPaymentPushDTO.Entry<>(apply.getApplyReason()));
            //费用信息
            Map<String, Object> costMap = (Map<String, Object>) data.get("cost");
            if (!ObjectUtils.isEmpty(costMap)) {
                String costCategoryCode = (String) MapUtils.getValueByExpress(costMap, "cost_category:code");
                String costCategoryName = (String) MapUtils.getValueByExpress(costMap, "cost_category:name");
                paymentPushDTO.setJdyCostcategoryCode(new BankPaymentPushDTO.Entry<>(StringUtils.isEmpty(costCategoryCode) ? "" : costCategoryCode));
                paymentPushDTO.setJdyCostcategoryName(new BankPaymentPushDTO.Entry<>(StringUtils.isEmpty(costCategoryName) ? "" : costCategoryName));
                //费用归属信息（只有一个）
                List<CostAttributionsDTO> costAttributionList = JsonUtils.toObj(JsonUtils.toJson(costMap.get("cost_attributions")), new TypeReference<List<CostAttributionsDTO>>() {
                });
                if (!CollectionUtils.isEmpty(costAttributionList)) {
                    CostAttributionsDTO costAttributionsDTO = costAttributionList.get(0);
                    paymentPushDTO.setJdyCostType(new BankPaymentPushDTO.Entry<>(costAttributionsDTO.getType()));
                    paymentPushDTO.setJdyThirdArchiveId(new BankPaymentPushDTO.Entry<>(costAttributionsDTO.getThirdArchiveId()));
                    paymentPushDTO.setJdyArchiveName(new BankPaymentPushDTO.Entry<>(costAttributionsDTO.getArchiveName()));
                    //费用归属明细（只有一个）
                    List<CostAttributionsDTO.CostAttributionDetail> details = costAttributionsDTO.getDetails();
                    if (!CollectionUtils.isEmpty(details)) {
                        CostAttributionsDTO.CostAttributionDetail detail = details.get(0);
                        paymentPushDTO.setJdyThirdId(new BankPaymentPushDTO.Entry<>(detail.getThirdId()));
                        paymentPushDTO.setJdyName(new BankPaymentPushDTO.Entry<>(detail.getName()));
                    }
                }
            }
        }
        return paymentPushDTO;
    }
}
