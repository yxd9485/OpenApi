package com.fenbeitong.openapi.plugin.voucher.service.impl;

import com.fenbeitong.fenbei.settlement.base.dto.BasePageDTO;
import com.fenbeitong.fenbei.settlement.base.enums.bill.BusinessCategoryEnum;
import com.fenbeitong.fenbei.settlement.external.api.dto.BillCostAttributionDTO;
import com.fenbeitong.fenbei.settlement.external.api.dto.BillDataListDTO;
import com.fenbeitong.openapi.plugin.etl.service.IEtlExcelService;
import com.fenbeitong.openapi.plugin.support.bill.dto.QueryOrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.support.bill.service.StereoBillService;
import com.fenbeitong.openapi.plugin.util.*;
import com.fenbeitong.openapi.plugin.voucher.constant.VoucherItemType;
import com.fenbeitong.openapi.plugin.voucher.dao.CustomizeVoucherDao;
import com.fenbeitong.openapi.plugin.voucher.dao.FinanceBusinessDataDao;
import com.fenbeitong.openapi.plugin.voucher.dao.OpenExpressConfigDao;
import com.fenbeitong.openapi.plugin.voucher.dao.OpenVoucherDraftDao;
import com.fenbeitong.openapi.plugin.voucher.dto.CustomizeVoucherDTO;
import com.fenbeitong.openapi.plugin.voucher.dto.FinanceCustomVoucherCreateReqDto;
import com.fenbeitong.openapi.plugin.voucher.entity.CustomizeVoucher;
import com.fenbeitong.openapi.plugin.voucher.entity.FinanceBusinessData;
import com.fenbeitong.openapi.plugin.voucher.entity.OpenExpressConfig;
import com.fenbeitong.openapi.plugin.voucher.entity.OpenVoucherDraft;
import com.fenbeitong.openapi.plugin.voucher.service.IExpressService;
import com.fenbeitong.openapi.plugin.voucher.service.IFinanceCustomMappingService;
import com.fenbeitong.openapi.plugin.voucher.service.IFinanceCustomVoucherListener;
import com.fenbeitong.openapi.plugin.voucher.service.IFinanceCustomVoucherService;
import com.finhub.framework.core.SpringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title: FinanceCustomVoucherServiceImpl</p>
 * <p>Description: 自定义凭证</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/9/24 2:28 PM
 */
@Service
@Slf4j
public class FinanceCustomVoucherServiceImpl implements IFinanceCustomVoucherService {

    @Autowired
    private StereoBillService stereoBillService;

    @Autowired
    private FinanceBusinessDataDao financeBusinessDataDao;

    @Autowired
    private OpenExpressConfigDao openExpressConfigDao;

    @Autowired
    private OpenVoucherDraftDao openVoucherDraftDao;

    @Autowired
    private CustomizeVoucherDao customizeVoucherDao;

    @Autowired
    private IExpressService expressService;

    @Autowired
    private IEtlExcelService etlExcelService;

    @Autowired
    private IFinanceCustomMappingService financeCustomMappingService;

    private static final String DEBIT_ACCOUNT_CODE = "debit_account_code";

    private static final String DEBIT_ACCOUNT_NAME = "debit_account_name";

    private static final String TAX_ACCOUNT_CODE = "tax_account_code";

    private static final String TAX_ACCOUNT_NAME = "tax_account_name";

    private static final String CREDIT_ACCOUNT_CODE = "credit_account_code";

    private static final String CREDIT_ACCOUNT_NAME = "credit_account_name";

    private static final String TAX_RATE = "tax_rate";

    @Async
    @Override
    public void createVoucherByPublicBill(String voucherId, FinanceCustomVoucherCreateReqDto reqDto) {
        createCustomizeVoucher(voucherId, reqDto);
        //导出excel
        String excelUrl = null;
        try {
            if (reqDto.getDeleteBill() == null || reqDto.getDeleteBill()) {
                //先删除老数据
                deleteBill(reqDto);
                //保存账单数据到业务数据表
                saveBill(reqDto);
            }
            //加载业务数据
            List<FinanceBusinessData> businessDataList = loadBill(reqDto);
            //删除历史凭证
            deleteVoucher(reqDto);
            //逐条生成凭证
            createBillVoucher(reqDto, businessDataList);
            //导出excel表格
            excelUrl = exportExcel(reqDto);
        } catch (Exception e) {
            log.warn("生成凭证失败", e);
        }
        updateCustomizeVoucher(voucherId, excelUrl);
    }

    private void updateCustomizeVoucher(String voucherId, String excelUrl) {
        CustomizeVoucher customizeVoucher = new CustomizeVoucher();
        customizeVoucher.setId(voucherId);
        customizeVoucher.setStatus(ObjectUtils.isEmpty(excelUrl) ? 2 : 3);
        customizeVoucher.setUrl(excelUrl);
        customizeVoucherDao.updateById(customizeVoucher);
    }

    private String exportExcel(FinanceCustomVoucherCreateReqDto reqDto) {
        List<OpenVoucherDraft> voucherDraftList = openVoucherDraftDao.listByBatchId(reqDto.getBillNo());
        if (ObjectUtils.isEmpty(voucherDraftList)) {
            return null;
        }
        List<OpenVoucherDraft> filteredVoucherList = voucherDraftList.stream().filter(voucherDraft -> (voucherDraft.getCredit() != null && voucherDraft.getCredit().signum() != 0) || (voucherDraft.getDebit() != null && voucherDraft.getDebit().signum() != 0)).collect(Collectors.toList());
        if (ObjectUtils.isEmpty(filteredVoucherList)) {
            return null;
        }
        List<OpenVoucherDraft> errorDraftList = filteredVoucherList.stream().filter(voucherDraft -> ObjectUtils.isEmpty(voucherDraft.getAccountCode())).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(errorDraftList)) {
            return null;
        }
        Map<String, List> srcData = Maps.newHashMap();
        srcData.put("Sheet1", filteredVoucherList);
        return etlExcelService.createExcel(reqDto.getExcelConfigId(), "凭证导出", srcData);
    }

    private void createCustomizeVoucher(String voucherId, FinanceCustomVoucherCreateReqDto reqDto) {
        CustomizeVoucher customizeVoucher = new CustomizeVoucher();
        customizeVoucher.setId(voucherId);
        customizeVoucher.setCompanyId(reqDto.getCompanyId());
        customizeVoucher.setBillNo(reqDto.getBillNo());
        customizeVoucher.setStatus(1);
        customizeVoucher.setUrl("");
        customizeVoucher.setCreateAt(System.currentTimeMillis());
        customizeVoucher.setCreateBy("system");
        customizeVoucher.setCreateName("system");
        customizeVoucherDao.saveSelective(customizeVoucher);
    }

    private void deleteVoucher(FinanceCustomVoucherCreateReqDto reqDto) {
        Example example = new Example(FinanceBusinessData.class);
        example.createCriteria().andEqualTo("batchId", reqDto.getBillNo());
        openVoucherDraftDao.deleteByExample(example);
    }

    private void createBillVoucher(FinanceCustomVoucherCreateReqDto reqDto, List<FinanceBusinessData> businessDataList) {
        Map<String, Object> expressCondition = Maps.newHashMap();
        expressCondition.put("companyId", reqDto.getCompanyId());
        expressCondition.put("type", 1);
        OpenExpressConfig openExpressConfig = openExpressConfigDao.getOpenExpressConfig(expressCondition);
        IFinanceCustomVoucherListener customVoucherListener = getFinanceCustomVoucherListener(openExpressConfig);
        Map<String, Object> mappingMap = financeCustomMappingService.loadCustomMapping(reqDto.getCompanyId());
        ExpressRunner expressRunner = expressService.getExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.putAll(mappingMap);
        expressService.executeScript(expressRunner, context, openExpressConfig.getBeforeScript());
        for (FinanceBusinessData businessData : businessDataList) {
            Map<String, Object> srcData = JsonUtils.toObj(JsonUtils.toJson(businessData), Map.class);
            context.put("srcData", srcData);
            Map<String, Object> matchValue = expressService.getValue(openExpressConfig.getId(), expressRunner, context, srcData, openExpressConfig.getBeforeRowScript());
            context.put("matchValue", matchValue);
            List<OpenVoucherDraft> voucherDraftList = createBillRowVoucher(businessData, srcData, matchValue, customVoucherListener);
            context.put("tgtList", voucherDraftList);
            expressService.executeScript(expressRunner, context, openExpressConfig.getAfterRowScript());
            for (OpenVoucherDraft openVoucherDraft : voucherDraftList) {
                openVoucherDraftDao.saveSelective(openVoucherDraft);
            }
        }
        customVoucherListener.afterVoucherCreated(reqDto);
    }

    private List<OpenVoucherDraft> createBillRowVoucher(FinanceBusinessData businessData, Map<String, Object> srcData, Map<String, Object> matchValue, IFinanceCustomVoucherListener customVoucherListener) {
        List<OpenVoucherDraft> rowVoucherItemList = Lists.newArrayList();
        //进项税税率
        BigDecimal taxRate = BigDecimalUtils.obj2big(matchValue.get(TAX_RATE));
        //公司支付金额
        BigDecimal companyPayPrice = getCompanyPayPrice(businessData);
        //进项税金额
        BigDecimal taxDebit = companyPayPrice.divide(BigDecimal.ONE.add(taxRate), 2, BigDecimal.ROUND_HALF_UP).multiply(taxRate).setScale(2, BigDecimal.ROUND_HALF_UP);
        //贷方金额
        BigDecimal credit = businessData.getTotalPrice();
        //借方金额
        BigDecimal debit = credit.subtract(taxDebit).setScale(2, BigDecimal.ROUND_HALF_UP);
        //借方 进项税 贷方
        List<Integer> voucherItemTypeList = Lists.newArrayList(VoucherItemType.DEBIT.getType(), VoucherItemType.TAX_DEBIT.getType(), VoucherItemType.CREDIT.getType());
        for (Integer voucherItemType : voucherItemTypeList) {
            if (voucherItemType == VoucherItemType.TAX_DEBIT.getType() && taxDebit.signum() == 0) {
                continue;
            }
            OpenVoucherDraft openVoucherDraft = new OpenVoucherDraft();
            openVoucherDraft.setYear(businessData.getYear());
            openVoucherDraft.setMonth(businessData.getMonth());
            String accountCodeKey = voucherItemType == VoucherItemType.DEBIT.getType() ? DEBIT_ACCOUNT_CODE : voucherItemType == VoucherItemType.TAX_DEBIT.getType() ? TAX_ACCOUNT_CODE : CREDIT_ACCOUNT_CODE;
            openVoucherDraft.setAccountCode(StringUtils.obj2str(matchValue.get(accountCodeKey)));
            String accountNameKey = voucherItemType == VoucherItemType.DEBIT.getType() ? DEBIT_ACCOUNT_NAME : voucherItemType == VoucherItemType.TAX_DEBIT.getType() ? TAX_ACCOUNT_NAME : CREDIT_ACCOUNT_NAME;
            openVoucherDraft.setAccountName(StringUtils.obj2str(matchValue.get(accountNameKey)));
            openVoucherDraft.setVoucherType(voucherItemType);
            openVoucherDraft.setVoucherTypeName(VoucherItemType.getVoucherItemType(voucherItemType).getTypeName());
            openVoucherDraft.setDebit(voucherItemType == VoucherItemType.DEBIT.getType() ? debit : voucherItemType == VoucherItemType.TAX_DEBIT.getType() ? taxDebit : BigDecimal.ZERO);
            openVoucherDraft.setCredit(voucherItemType == VoucherItemType.CREDIT.getType() ? credit : BigDecimal.ZERO);
            openVoucherDraft.setEmployeeCode(businessData.getEmployeeId());
            openVoucherDraft.setEmployeeName(businessData.getEmployeeName());
            openVoucherDraft.setDeptCode(StringUtils.obj2str(srcData.get("dept_code")));
            openVoucherDraft.setDeptName(StringUtils.obj2str(srcData.get("dept_name")));
            openVoucherDraft.setProjectCode("");
            openVoucherDraft.setProjectName(businessData.getCostCenterName());
            openVoucherDraft.setSupplierCode(businessData.getSupplierCode());
            openVoucherDraft.setSupplierName(businessData.getSupplierName());
            openVoucherDraft.setOperatorId("");
            openVoucherDraft.setOperatorName("");
            openVoucherDraft.setVoucherDate(DateUtils.toSimpleStr(DateUtils.now(), true));
            openVoucherDraft.setCostCenterCode("");
            openVoucherDraft.setCostCenterName(businessData.getCostCenterName());
            openVoucherDraft.setOrgUnitFullName(businessData.getOrgUnitFullName());
            openVoucherDraft.setProjectAccounting(0);
            openVoucherDraft.setDepartmentAccounting(0);
            openVoucherDraft.setEmployeeAccounting(0);
            openVoucherDraft.setSupplierAccounting(0);
            openVoucherDraft.setBatchId(businessData.getBatchId());
            openVoucherDraft.setBatchLineId(businessData.getBusinessLineId());
            openVoucherDraft.setAttr1(StringUtils.obj2str(matchValue.get("attr1")));
            openVoucherDraft.setAttr2(StringUtils.obj2str(matchValue.get("attr2")));
            openVoucherDraft.setAttr3(StringUtils.obj2str(matchValue.get("attr3")));
            openVoucherDraft.setAttr4(StringUtils.obj2str(matchValue.get("attr4")));
            openVoucherDraft.setAttr5(StringUtils.obj2str(matchValue.get("attr5")));
            openVoucherDraft.setAttr19(businessData.getBusinessName());
            openVoucherDraft.setAttr20(StringUtils.obj2str(taxRate));
            openVoucherDraft.setAttr21(businessData.getBusinessExtJson());
            openVoucherDraft.setStatus(-1);
            customVoucherListener.createVoucherItem(businessData, srcData, openVoucherDraft);
            rowVoucherItemList.add(openVoucherDraft);
        }
        return rowVoucherItemList;
    }

    private BigDecimal getCompanyPayPrice(FinanceBusinessData businessData) {
        List<Integer> bizList = Lists.newArrayList(BusinessCategoryEnum.Air.getKey(), BusinessCategoryEnum.AirIntl.getKey(), BusinessCategoryEnum.Train.getKey());
        BigDecimal totalPrice = businessData.getTotalPrice();
        BigDecimal refundFee = businessData.getRefundFee() == null ? BigDecimal.ZERO : businessData.getRefundFee();
        BigDecimal companyPay;
        if (bizList.contains(businessData.getBusinessType())) {
            boolean isRefund = totalPrice.signum() < 0;
            boolean isDomestic = BusinessCategoryEnum.Air.getKey() == businessData.getBusinessType();
            if (isRefund) {
                companyPay = totalPrice.add(refundFee);
                if (isDomestic) {
                    companyPay = companyPay.add(new BigDecimal("50.00"));
                }
            } else {
                companyPay = totalPrice;
                if (isDomestic) {
                    companyPay = companyPay.subtract(new BigDecimal("50.00"));
                }
            }
        } else {
            companyPay = totalPrice;
        }
        return companyPay;
    }

    private List<FinanceBusinessData> loadBill(FinanceCustomVoucherCreateReqDto reqDto) {
        Example example = new Example(FinanceBusinessData.class);
        example.createCriteria().andEqualTo("companyId", reqDto.getCompanyId()).andEqualTo("batchId", reqDto.getBillNo());
        return financeBusinessDataDao.listByExample(example).stream().filter(d -> d.getBusinessType() != 126).collect(Collectors.toList());
    }

    private void deleteBill(FinanceCustomVoucherCreateReqDto reqDto) {
        Example example = new Example(FinanceBusinessData.class);
        example.createCriteria().andEqualTo("companyId", reqDto.getCompanyId()).andEqualTo("batchId", reqDto.getBillNo());
        financeBusinessDataDao.deleteByExample(example);
    }

    private void saveBill(FinanceCustomVoucherCreateReqDto reqDto) {
        int pageIndex = 1;
        QueryOrderDetailReqDTO orderDetailReqDTO = new QueryOrderDetailReqDTO();
        orderDetailReqDTO.setBillNo(reqDto.getBillNo());
        orderDetailReqDTO.setPageIndex(1);
        orderDetailReqDTO.setPageSize(100);
        orderDetailReqDTO.setCompanyId(reqDto.getCompanyId());
        BasePageDTO<BillDataListDTO> billDetailListRes = stereoBillService.queryBillDataListDetail(orderDetailReqDTO);
        while (billDetailListRes != null && !ObjectUtils.isEmpty(billDetailListRes.getDtoList())) {
            batchSaveBill(reqDto, billDetailListRes.getDtoList());
            orderDetailReqDTO.setPageIndex(++pageIndex);
            billDetailListRes = stereoBillService.queryBillDataListDetail(orderDetailReqDTO);
        }

    }

    private void batchSaveBill(FinanceCustomVoucherCreateReqDto reqDto, List<BillDataListDTO> billDataList) {
        for (BillDataListDTO billData : billDataList) {
            FinanceBusinessData financeBusinessData = new FinanceBusinessData();
            financeBusinessData.setId(RandomUtils.bsonId());
            //类型 1:虚拟卡核销单;2:商务消费账单;3:对公付款;4:报销单;5:个人消费账单
            financeBusinessData.setType(2);
            financeBusinessData.setCompanyId(reqDto.getCompanyId());
            financeBusinessData.setYear(reqDto.getYear());
            financeBusinessData.setMonth(reqDto.getMonth());
            financeBusinessData.setEmployeeId(getEmployeeId(billData, reqDto.getEmployeeType()));
            financeBusinessData.setEmployeeName(getEmployeeName(billData, reqDto.getEmployeeType()));
            financeBusinessData.setEmployeeType(1);
            financeBusinessData.setEmployeeCode(financeBusinessData.getEmployeeId());
            Map<String, String> orgUnitMap = getOrgUnitMap(billData, reqDto.getDepartmentType());
            financeBusinessData.setOrgUnitId(orgUnitMap.get("orgUnitId"));
            financeBusinessData.setOrgUnitName(orgUnitMap.get("orgUnitName"));
            financeBusinessData.setOrgUnitFullName(orgUnitMap.get("orgUnitFullName"));
            List<BillCostAttributionDTO> costAttributionList = billData.getCostAttributionList();
            if (!ObjectUtils.isEmpty(costAttributionList)) {
                BillCostAttributionDTO costAttribution = costAttributionList.stream().filter(c -> c.getCostAttributionCategory() == 2).findFirst().orElse(null);
                if (costAttribution != null) {
                    financeBusinessData.setCostCenterId(costAttribution.getCostAttributionID());
                    financeBusinessData.setCostCenterName(costAttribution.getCostAttributionName());
                }
            }
            financeBusinessData.setSupplierCode(reqDto.getSupplierCode());
            financeBusinessData.setSupplierName(reqDto.getSupplierName());
            financeBusinessData.setBusinessType(billData.getOrderCategory());
            financeBusinessData.setBusinessName(BusinessCategoryEnum.getValueByKey(billData.getOrderCategory()));
            financeBusinessData.setReasons(billData.getReason());
            financeBusinessData.setBatchId(reqDto.getBillNo());
            financeBusinessData.setBusinessId(billData.getBillId());
            financeBusinessData.setBusinessLineId(billData.getId());
            financeBusinessData.setBusinessDate(DateUtils.toSimpleStr(billData.getOrderCreateTime(), true));

            financeBusinessData.setTotalPrice(billData.getTotalAmount());
            financeBusinessData.setTaxPrice(BigDecimal.ZERO);
            financeBusinessData.setTicketPrice(ObjectUtils.isEmpty(billData.getSalePrice()) ? BigDecimal.ZERO : billData.getSalePrice());
            financeBusinessData.setCompanyPayPrice(ObjectUtils.isEmpty(billData.getCompanyPayPrice()) ? BigDecimal.ZERO : billData.getCompanyPayPrice());
            financeBusinessData.setServiceFee(ObjectUtils.isEmpty(billData.getServiceFee()) ? BigDecimal.ZERO : billData.getServiceFee());
            financeBusinessData.setRefundFee(ObjectUtils.isEmpty(billData.getRefundExtFee()) ? BigDecimal.ZERO : billData.getRefundExtFee());
            financeBusinessData.setAirportFee(BigDecimal.ZERO);
            financeBusinessData.setFuelFee(BigDecimal.ZERO);
            financeBusinessData.setBusinessExtJson(getBusinessExtJson(billData));
            financeBusinessData.setCreateTime(new Date());
            financeBusinessDataDao.saveSelective(financeBusinessData);
        }

    }

    private String getEmployeeName(BillDataListDTO billData, int personnel) {
        String employeeName = null;
        //0:预订人 1:实际使用人
        if (personnel == 1 && !ObjectUtils.isEmpty(billData.getConsumerBeanList())) {
            employeeName = billData.getConsumerBeanList().get(0).getName();
        }
        if (ObjectUtils.isEmpty(employeeName)) {
            employeeName = billData.getEmployeeName();
        }
        return employeeName;
    }

    private String getEmployeeId(BillDataListDTO billData, int personnel) {
        String employeeId = null;
        //0:预订人 1:实际使用人
        if (personnel == 1 && !ObjectUtils.isEmpty(billData.getConsumerBeanList())) {
            employeeId = billData.getConsumerBeanList().get(0).getId();
        }
        if (ObjectUtils.isEmpty(employeeId)) {
            employeeId = billData.getEmployeeId();
        }
        return employeeId;
    }

    private Map<String, String> getOrgUnitMap(BillDataListDTO billData, int department) {
        Map<String, String> orgUnitMap = new HashMap<>();
        String orgUnitId = null;
        String orgUnitName = null;
        String orgUnitFullName = null;
        //0:下单人部门;1:使用人部门;2:费用归属部门
        if (department == 2) {
            List<BillCostAttributionDTO> costAttributions = billData.getCostAttributionList().stream().filter(c -> c.getCostAttributionCategory() == 1).collect(Collectors.toList());
            if (!ObjectUtils.isEmpty(costAttributions)) {
                orgUnitId = costAttributions.get(0).getCostAttributionID();
                orgUnitName = costAttributions.get(0).getCostAttributionName();
                orgUnitFullName = costAttributions.get(0).getCostAttributionPath();
            }
        } else if (department == 1 && !ObjectUtils.isEmpty(billData.getConsumerBeanList())) {
            orgUnitName = billData.getCustomerDept();
            orgUnitFullName = billData.getCustomerHierarchyDept();
        }
        if (ObjectUtils.isEmpty(orgUnitFullName)) {
            orgUnitName = billData.getDepartmentName();
            orgUnitFullName = billData.getDepartmentHierarchy();
        }
        orgUnitMap.put("orgUnitId", orgUnitId);
        orgUnitMap.put("orgUnitName", orgUnitName);
        if (!ObjectUtils.isEmpty(orgUnitFullName)) {
            orgUnitMap.put("orgUnitFullName", orgUnitFullName.contains("/") ? orgUnitFullName.substring(orgUnitFullName.indexOf("/") + 1) : orgUnitFullName);
        }
        return orgUnitMap;
    }

    private String getBusinessExtJson(BillDataListDTO billData) {
        Map<String, Object> extJsonMap = Maps.newHashMap();
        extJsonMap.put("orderId", billData.getOrderId());
        extJsonMap.put("orderCreateTime", billData.getOrderCreateTime());
        extJsonMap.put("ticketNo", billData.getTicketNo());
        extJsonMap.put("tripType", billData.getTripType());
        extJsonMap.put("tripInfo", billData.getTripInfo());
        extJsonMap.put("flightNo", billData.getFlightNo());
        extJsonMap.put("departureName", billData.getDepartureName());
        extJsonMap.put("arrivalName", billData.getArrivalName());
        extJsonMap.put("fromStationName", billData.getFromStationName());
        extJsonMap.put("toStationName", billData.getToStationName());
        extJsonMap.put("departureTime", billData.getDepartureTime());
        extJsonMap.put("arrivalTime", billData.getBackDepartureTime());
        extJsonMap.put("discount", billData.getDiscount());
        extJsonMap.put("seatType", billData.getSeatType());
        extJsonMap.put("seatLocation", billData.getSeatLocation());
        extJsonMap.put("numberOfNights", billData.getNumberOfNights());
        extJsonMap.put("thirdInfo", billData.getThirdInfo());
        extJsonMap.put("tripApplyId", billData.getTripApplyId());
        extJsonMap.put("duringApplyId", billData.getDuringApplyId());
        return JsonUtils.toJson(extJsonMap);
    }

    private IFinanceCustomVoucherListener getFinanceCustomVoucherListener(OpenExpressConfig openExpressConfig) {
        String className = openExpressConfig.getListener();
        if (!ObjectUtils.isEmpty(className)) {
            Class clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (clazz != null) {
                Object bean = SpringUtils.getBean(clazz);
                if (bean != null && bean instanceof IFinanceCustomVoucherListener) {
                    return ((IFinanceCustomVoucherListener) bean);
                }
            }
        }
        return SpringUtils.getBean(DefaultFinanceCustomVoucherListener.class);
    }

    @Override
    public CustomizeVoucherDTO getVoucherDetail(String voucherId) {
        CustomizeVoucherDTO customizeVoucherDTO = new CustomizeVoucherDTO();
        CustomizeVoucher customizeVoucher = customizeVoucherDao.getById(voucherId);
        if (customizeVoucher != null) {
            BeanUtils.copyProperties(customizeVoucher, customizeVoucherDTO);
        }
        return customizeVoucherDTO;
    }
}
