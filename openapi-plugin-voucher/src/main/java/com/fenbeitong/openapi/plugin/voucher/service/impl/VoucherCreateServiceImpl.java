package com.fenbeitong.openapi.plugin.voucher.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.openapi.plugin.etl.service.IEtlExcelService;
import com.fenbeitong.openapi.plugin.etl.service.IEtlStrategyService;
import com.fenbeitong.openapi.plugin.support.common.dao.OpenCompanyVoucherConfigDao;
import com.fenbeitong.openapi.plugin.support.common.entity.OpenCompanyVoucherConfig;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.*;
import com.fenbeitong.openapi.plugin.voucher.dao.OpenVoucherDraftDao;
import com.fenbeitong.openapi.plugin.voucher.dto.FinanceBillVoucherDto;
import com.fenbeitong.openapi.plugin.voucher.dto.FinanceGlobalConfigDto;
import com.fenbeitong.openapi.plugin.voucher.dto.FinanceVoucherContractDto;
import com.fenbeitong.openapi.plugin.voucher.entity.OpenVoucherDraft;
import com.fenbeitong.openapi.plugin.voucher.service.IFinanceInitConfigService;
import com.fenbeitong.openapi.plugin.voucher.service.IVoucherCreateService;
import com.fenbeitong.openapi.plugin.yiduijie.model.voucher.CreateVoucherReqDTO;
import com.fenbeitong.saasplus.api.model.dto.finance.FinanceBillVoucherDTO;
import com.fenbeitong.saasplus.api.model.dto.finance.FinancePaymentVO;
import com.fenbeitong.saasplus.api.model.dto.finance.FinanceVoucherContract;
import com.fenbeitong.saasplus.api.service.finance.IFinancePaymentService;
import com.fenbeitong.saasplus.api.service.finance.IFinanceVoucherService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title: VoucherCreateServiceImpl</p>
 * <p>Description: 创建凭证服务实现类</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/23 8:38 PM
 */
@Slf4j
@ServiceAspect
@Service
public class VoucherCreateServiceImpl implements IVoucherCreateService {

    @Autowired
    private OpenCompanyVoucherConfigDao voucherConfigDao;

    @Autowired
    private IEtlStrategyService voucherStrategyService;

    @DubboReference(check = false)
    private IFinanceVoucherService financeVoucherService;

    @DubboReference(check = false)
    private IFinancePaymentService financePaymentService;

    @Autowired
    private IFinanceInitConfigService financeInitConfigService;

    @Autowired
    private OpenVoucherDraftDao voucherDraftDao;

    @Autowired
    private IEtlExcelService etlExcelService;

    @Async
    @Override
    public void createVoucher(String companyId, String operatorId, String operator, int voucherType, String batchId, String callBackUrl) {
        List<Map<String, Object>> srcList = listBusinessData(companyId, voucherType, batchId);
        if (ObjectUtils.isEmpty(srcList)) {
            return;
        }
        createVoucherBySrc(companyId, operatorId, operator, voucherType, batchId, srcList, callBackUrl);
    }

    @Async
    @Override
    public void createVoucherBySrc(String companyId, String operatorId, String operator, int voucherType, String batchId, List<Map<String, Object>> srcList, String callBackUrl) {
        OpenCompanyVoucherConfig voucherConfig = voucherConfigDao.getByCompanyIdAndType(companyId, voucherType);
        if (voucherConfig == null) {
            return;
        }
        FinanceGlobalConfigDto globalConfigDto = financeInitConfigService.initConfig(companyId, operatorId);
        List<Map<String, Object>> voucherList = voucherStrategyService.transfer(voucherConfig.getStrategyId(), srcList, globalConfigDto);
        for (Map<String, Object> voucherMap : voucherList) {
            OpenVoucherDraft voucherDraft = JsonUtils.toObj(JsonUtils.toJson(voucherMap), OpenVoucherDraft.class);
            voucherDraftDao.saveSelective(voucherDraft);
        }
        //1:自研;2:易对接
        Integer dockingType = voucherConfig.getDockingType();
        if (dockingType == 1) {
            String excelUrl = exportExcel(batchId, voucherConfig.getExcelConfigId());
            doCallBack(callBackUrl, excelUrl, voucherType, batchId);
        } else if (dockingType == 2) {
            createVoucherByYiDuiJie(callBackUrl, companyId, batchId, operatorId, operator);
        }
    }

    private void createVoucherByYiDuiJie(String callBackUrl, String companyId, String batchId, String operatorId, String operator) {
        CreateVoucherReqDTO req = new CreateVoucherReqDTO();
        req.setBusinessType(100);
        req.setCompanyId(companyId);
        req.setBatchId(batchId);
        req.setCallBackUrl(callBackUrl);
        req.setOperator(operator);
        req.setOperatorId(operatorId);
        RestHttpUtils.postJson(callBackUrl, JsonUtils.toJsonSnake(req));
    }

    private void doCallBack(String callBackUrl, String excelUrl, int voucherType, String batchId) {
        if (callBackUrl == null) {
            return;
        }
        Map<String, Object> jsonMap = Maps.newHashMap();
        boolean success = !ObjectUtils.isEmpty(excelUrl) && excelUrl.startsWith("http");
        String message = success ? excelUrl : ObjectUtils.isEmpty(excelUrl) ? "生成失败" : excelUrl;
        jsonMap.put("status", success ? 0 : 1);
        jsonMap.put("documentType", "voucher");
        //1:报销单;2:账单;3:对公付款
        String voucherTypeDesc = voucherType == 1 ? "APPLY" : voucherType == 2 ? "BILL" : "";
        jsonMap.put("id", voucherTypeDesc + ":" + batchId);
        jsonMap.put("localId", null);
        jsonMap.put("message", message);
        RestHttpUtils.postJson(callBackUrl, JsonUtils.toJson(jsonMap));
    }

    @Override
    public List<Map<String, Object>> listBusinessData(String companyId, int voucherType, String batchId) {
        ThreadUtils.sleep(5000);
        List<Map<String, Object>> srcList = Lists.newArrayList();
        //核销单
        if (voucherType == 1) {
            srcList = loadCheckApplyData(companyId, batchId);
        }
        //账单
        else if (voucherType == 2) {
            srcList = loadBillData(companyId, batchId);
        } else if (voucherType == 3) {
            srcList = loadPublicPaymentData(companyId, batchId);
        }
        log.info("加载凭证业务数据列表,companyId:[{}],voucherType:[{}],batchId:[{}],srcList:{}", companyId, voucherType, batchId, JsonUtils.toJson(srcList));
        return srcList;
    }

    private List<Map<String, Object>> loadPublicPaymentData(String companyId, String batchId) {
        List<Map<String, Object>> srcList = Lists.newArrayList();
        List<FinancePaymentVO> paymentList = financePaymentService.queryPublicPaymentList(batchId);
        if (!ObjectUtils.isEmpty(paymentList)) {
            LocalDate localDate = LocalDate.now();
            for (FinancePaymentVO paymentVo : paymentList) {
                Map<String, Object> paymentMap = MapUtils.obj2map(paymentVo, false);
                paymentMap.put("batchId", batchId);
                paymentMap.put("year", localDate.getYear());
                paymentMap.put("month", localDate.getMonthValue());
                srcList.add(paymentMap);
            }
        }
        return srcList;
    }

    private List<Map<String, Object>> loadBillData(String companyId, String batchId) {
        List<Map<String, Object>> billListMap = Lists.newArrayList();
        FinanceBillVoucherDTO voucherData = new FinanceBillVoucherDTO();
        int pageIndex = 1;
        FinanceBillVoucherDTO pageData = null;
        String summary = null;
        while ((pageData = financeVoucherService.queryFinaneBillVoucherInfo(companyId, batchId, 1000, pageIndex++)) != null && !ObjectUtils.isEmpty(pageData.getBillList())) {
            summary = voucherData.getSummary();
            if (ObjectUtils.isEmpty(summary)) {
                voucherData.setSummary(pageData.getSummary());
            }
            List<FinanceBillVoucherDTO.BillDTO> billList = voucherData.getBillList();
            if (billList == null) {
                billList = Lists.newArrayList();
                voucherData.setBillList(billList);
            }
            List<FinanceBillVoucherDTO.BillDTO> pageDataBillList = pageData.getBillList();
            if (!ObjectUtils.isEmpty(pageDataBillList)) {
                billList.addAll(pageDataBillList);
            }
        }
        if (!ObjectUtils.isEmpty(voucherData.getBillList())) {
            String[] yeaAndMonth = summary == null ? null : summary.split("-");
            boolean validSummary = !ObjectUtils.isEmpty(yeaAndMonth) && yeaAndMonth.length == 2;
            LocalDate localDate = LocalDate.now();
            LocalDate lastMonthDate = localDate.minusMonths(1);
            int lastMonthYear = lastMonthDate.getYear();
            int lastMonthMonth = lastMonthDate.getMonthValue();
            List<FinanceBillVoucherDTO.BillDTO> billList = voucherData.getBillList();
            for (FinanceBillVoucherDTO.BillDTO billDto : billList) {
                FinanceBillVoucherDto voucherDto = new FinanceBillVoucherDto();
                voucherDto.setBatchId(batchId);
                BeanUtils.copyProperties(billDto, voucherDto);
                voucherDto.setSummary(summary);
                voucherDto.setYear(validSummary ? NumericUtils.obj2int(yeaAndMonth[0], lastMonthYear) : lastMonthYear);
                voucherDto.setMonth(validSummary ? NumericUtils.obj2int(yeaAndMonth[1], lastMonthMonth) : lastMonthMonth);
                Map<String, Object> billMap = MapUtils.request2map2(voucherDto);
                List<String> customFields = Lists.newArrayList(billDto.getCustomField1(), billDto.getCustomField2(), billDto.getCustomField3()).stream().filter(customField -> !ObjectUtils.isEmpty(customField)).collect(Collectors.toList());
                if (!ObjectUtils.isEmpty(customFields)) {
                    for (String customField : customFields) {
                        int index = customField.indexOf("-");
                        if (index < 0) {
                            continue;
                        }
                        billMap.put(customField.substring(0, index), customField.substring(index + 1));
                    }
                }
                billListMap.add(billMap);
            }
        }
        return billListMap;
    }

    private List<Map<String, Object>> loadCheckApplyData(String companyId, String batchId) {
        List<Map<String, Object>> checkApplyList = Lists.newArrayList();
        FinanceVoucherContract voucherContract = financeVoucherService.queryFinaneVoucherInfo(companyId, batchId);
        if (voucherContract == null || ObjectUtils.isEmpty(voucherContract.getApply_list())) {
            return checkApplyList;
        }
        List<FinanceVoucherContract.FinanceVoucherInfo> applyList = voucherContract.getApply_list();
        int applyCode = 1000;
        for (FinanceVoucherContract.FinanceVoucherInfo apply : applyList) {
            List<FinanceVoucherContract.TradeInformation> tradeInfoList = apply.getTrade_info_list();
            for (FinanceVoucherContract.TradeInformation tradeInfo : tradeInfoList) {
                List<FinanceVoucherContract.Invoice> invoiceList = tradeInfo.getInvoice_list();
                for (FinanceVoucherContract.Invoice invoice : invoiceList) {
                    FinanceVoucherContractDto voucherContractDto = new FinanceVoucherContractDto();
                    voucherContractDto.setBatch_id(batchId);
                    voucherContractDto.setSummary(voucherContract.getSummary());
                    voucherContractDto.setUsername(apply.getUsername());
                    voucherContractDto.setDepartment(apply.getDepartment());
                    voucherContractDto.setDept_sub_name(apply.getDept_sub_name());
                    voucherContractDto.setApply_code(StringUtils.obj2str(applyCode++));
                    voucherContractDto.setCost_category_id(tradeInfo.getCost_category_id());
                    voucherContractDto.setCost_category(tradeInfo.getCost_category());
                    voucherContractDto.setTotal_amount(invoice.getTotal_amount());
                    voucherContractDto.setTax_amount(invoice.getTotal_amount());
                    voucherContractDto.setTotal_price_plus_tax(invoice.getTotal_price_plus_tax());
                    checkApplyList.add(MapUtils.obj2map(voucherContractDto, false));
                }
            }
        }
        return checkApplyList;
    }

    @Override
    public String exportExcel(String batchId, Long excelConfigId) {
        if (excelConfigId == null) {
            return null;
        }
        List<OpenVoucherDraft> voucherDraftList = voucherDraftDao.listByBatchId(batchId);
        if (ObjectUtils.isEmpty(voucherDraftList)) {
            return null;
        }
        List<OpenVoucherDraft> filteredVoucherList = voucherDraftList.stream().filter(voucherDraft -> (voucherDraft.getCredit() != null && voucherDraft.getCredit().signum() != 0) || (voucherDraft.getDebit() != null && voucherDraft.getDebit().signum() != 0)).collect(Collectors.toList());
        if (ObjectUtils.isEmpty(filteredVoucherList)) {
            return null;
        }
        List<OpenVoucherDraft> errorDraftList = filteredVoucherList.stream().filter(voucherDraft -> ObjectUtils.isEmpty(voucherDraft.getAccountCode())).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(errorDraftList)) {
            return "生成失败,未找到科目信息，BatchId=" + batchId + ",BatchLineId=" + String.join(",", errorDraftList.stream().map(OpenVoucherDraft::getBatchLineId).collect(Collectors.toList()));
        }
        Map<String, List> srcData = Maps.newHashMap();
        srcData.put("Sheet1", filteredVoucherList);
        return etlExcelService.createExcel(excelConfigId, "凭证导出", srcData);
    }
}
