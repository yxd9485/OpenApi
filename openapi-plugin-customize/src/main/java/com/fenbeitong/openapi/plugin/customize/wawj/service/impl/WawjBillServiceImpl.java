package com.fenbeitong.openapi.plugin.customize.wawj.service.impl;

import com.fenbeitong.fenbei.settlement.base.dto.BasePageDTO;
import com.fenbeitong.fenbei.settlement.external.api.api.bill.IBillOpenApi;
import com.fenbeitong.fenbei.settlement.external.api.api.bill.IBillPersonalApi;
import com.fenbeitong.fenbei.settlement.external.api.dto.BillDataListDTO;
import com.fenbeitong.fenbei.settlement.external.api.dto.BillPersonalConsumeDTO;
import com.fenbeitong.fenbei.settlement.external.api.query.BillDataListQuery;
import com.fenbeitong.fenbei.settlement.external.api.query.BillPersonalConsumeQuery;
import com.fenbeitong.openapi.plugin.customize.wawj.dao.*;
import com.fenbeitong.openapi.plugin.customize.wawj.dto.WawjBaoXiaoPushReqDTO;
import com.fenbeitong.openapi.plugin.customize.wawj.entity.*;
import com.fenbeitong.openapi.plugin.customize.wawj.service.IWawjBillService;
import com.fenbeitong.openapi.plugin.customize.wawj.utils.WawjBillUtil;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.usercenter.api.model.dto.orgunit.OrgUnitPageReqDTO;
import com.fenbeitong.usercenter.api.model.dto.orgunit.OrgUnitResDTO;
import com.fenbeitong.usercenter.api.service.orgunit.IOrgUnitService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>Title: WawjBillServiceImpl</p>
 * <p>Description: 我爱我家账单服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/11/9 8:26 PM
 */
@ServiceAspect
@Service
public class WawjBillServiceImpl implements IWawjBillService {


    @Value("${host.openplus}")
    private String hostOpenplus;

    @DubboReference(check = false)
    private IBillOpenApi stereoBillService;

    @DubboReference(check = false)
    private IBillPersonalApi billPersonalService;

    @DubboReference(check = false)
    private IOrgUnitService orgUnitService;

    @Autowired
    private OpenWawjBillDetailDao wawjBillDetailDao;

    @Autowired
    private OpenWawjAccontUnitConfDao wawjAccontUnitConfDao;

    @Autowired
    private OpenWawjBusinessTypeConfDao wawjBusinessTypeConfDao;

    @Autowired
    private OpenWawjReimburseTypeFeeProjectConfDao wawjReimburseTypeFeeProjectConfDao;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Autowired
    private OpenWawjCostcenterConfDao wawjCostcenterConfDao;

    @Autowired
    private OpenWawjBillSummaryDao wawjBillSummryDao;

    @Async
    @Override
    public void save(String companyId, String billNo, Integer delete) {
        if (delete != null && delete == 1) {
            deleteHistoryRecord(billNo);
        }
        //一个账单只推送一次
        boolean needSave = needSave(billNo);
        if (!needSave) {
            return;
        }
        List<OpenMsgSetup> openMsgSetups = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList("wiwj_customize_baoxiao_info"));
        Map baoxiaoInfo = JsonUtils.toObj(openMsgSetups.get(0).getStrVal1(), Map.class);
        //核算单位配置
        Map<String, OpenWawjAccontUnitConf> wawjAccontUnitConfMap = wawjAccontUnitConfDao.listAll().stream().collect(Collectors.toMap(OpenWawjAccontUnitConf::getAccountUnitName, Function.identity()));
        //业务类型配置
        Map<String, OpenWawjBusinessTypeConf> wawjBusinessTypeConfMap = wawjBusinessTypeConfDao.listAll().stream().collect(Collectors.toMap(OpenWawjBusinessTypeConf::getCityCompanyCode, Function.identity()));
        //报销类型及费用项目配置
        Map<Integer, OpenWawjReimburseTypeFeeProjectConf> wawjReimburseTypeFeeProjectConfMap = wawjReimburseTypeFeeProjectConfDao.listAll().stream().collect(Collectors.toMap(OpenWawjReimburseTypeFeeProjectConf::getOrderCategoryType, Function.identity()));
        //部门信息
        Map<String, WiwjOrgUnit> orgUnitMap = getOrgUnitMap(companyId);
        //获取第二级部门三方id
        List<String> secondLevelDeptIdList = secondLevelDeptIdList(companyId, orgUnitMap);
        //保存因公账单
        saveCompanyBill(companyId, billNo, baoxiaoInfo, wawjAccontUnitConfMap, wawjBusinessTypeConfMap, wawjReimburseTypeFeeProjectConfMap, orgUnitMap, secondLevelDeptIdList);
        //保存分贝券账单
        savePersonalBill(companyId, billNo, baoxiaoInfo, wawjAccontUnitConfMap, wawjBusinessTypeConfMap, wawjReimburseTypeFeeProjectConfMap, orgUnitMap, secondLevelDeptIdList);
        //汇总数据
        List<String> batchIdList = summaryBill(billNo, baoxiaoInfo);
        //推送数据
        pushData(companyId, batchIdList);
    }

    private void deleteHistoryRecord(String billNo) {
        Example example = new Example(OpenWawjBillDetail.class);
        example.createCriteria().andEqualTo("billNo", billNo);
        wawjBillDetailDao.deleteByExample(example);

        example = new Example(OpenWawjBillSummary.class);
        example.createCriteria().andEqualTo("billNo", billNo);
        wawjBillSummryDao.deleteByExample(example);
    }

    private List<String> secondLevelDeptIdList(String companyId, Map<String, WiwjOrgUnit> orgUnitMap) {
        List<WiwjOrgUnit> orgUnitList = Lists.newArrayList(orgUnitMap.values());
        //先找一级部门id
        List<String> firstLevelDeptIdList = orgUnitList.stream().filter(org -> companyId.equals(org.getParent_org_unit_id())).map(WiwjOrgUnit::getId).collect(Collectors.toList());
        //二级部门id
        return orgUnitList.stream().filter(org -> firstLevelDeptIdList.contains(org.getParent_org_unit_id())).map(WiwjOrgUnit::getThird_org_id).collect(Collectors.toList());
    }

    private Map<String, WiwjOrgUnit> getOrgUnitMap(String companyId) {
        List<OrgUnitResDTO> orgUnitList = Lists.newArrayList();
        OrgUnitPageReqDTO req = new OrgUnitPageReqDTO();
        req.setPageIndex(1);
        req.setPageSize(100);
        req.setCompanyId(companyId);
        List<OrgUnitResDTO> orgUnitResList = orgUnitService.queryOrgUnitResDTOByPage(req);
        while (!ObjectUtils.isEmpty(orgUnitResList)) {
            orgUnitList.addAll(orgUnitResList);
            req.setPageIndex(req.getPageIndex() + 1);
            orgUnitResList = orgUnitService.queryOrgUnitResDTOByPage(req);
        }
        Map<String, WiwjOrgUnit> wiwjOrgUnitMap = Maps.newHashMap();
        Map<String, OrgUnitResDTO> orgUnitResMap = orgUnitList.stream().filter(orgUnitRes -> !ObjectUtils.isEmpty(orgUnitRes.getThird_org_id())).collect(Collectors.toMap(OrgUnitResDTO::getId, Function.identity()));
        orgUnitResMap.forEach((k, v) -> {
            WiwjOrgUnit wiwjOrgUnit = new WiwjOrgUnit();
            wiwjOrgUnit.setId(v.getId());
            wiwjOrgUnit.setName(v.getName());
            wiwjOrgUnit.setParent_org_unit_id(v.getParent_org_unit_id());
            wiwjOrgUnit.setThird_org_id(v.getThird_org_id());
            String parentOrgUnitId = v.getParent_org_unit_id();
            OrgUnitResDTO parentOrgUnit = ObjectUtils.isEmpty(parentOrgUnitId) ? null : orgUnitResMap.get(parentOrgUnitId);
            wiwjOrgUnit.setThird_parent_org_unit_id(parentOrgUnit == null ? null : parentOrgUnit.getThird_org_id());
            wiwjOrgUnitMap.put(v.getThird_org_id(), wiwjOrgUnit);
        });
        return wiwjOrgUnitMap;
    }

    private void pushData(String companyId, List<String> batchIdList) {
        WawjBaoXiaoPushReqDTO req = new WawjBaoXiaoPushReqDTO();
        req.setCompanyId(companyId);
        req.setBatchIdList(batchIdList);
        RestHttpUtils.postJson(hostOpenplus + "/openapi/customize/5i5j/baoxiao/push", JsonUtils.toJson(req));
    }

    private boolean needSave(String billNo) {
        return wawjBillSummryDao.getCount(billNo) == 0;
    }

    private List<String> summaryBill(String billNo, Map baoxiaoInfo) {
        List<OpenWawjBillDetail> summaryList = wawjBillDetailDao.listSummary(billNo);
        String nowDate = DateUtils.toSimpleStr(new Date(), true);
        List<OpenWawjBillSummary> billSummaryList = summaryList.stream().map(summary -> {
            OpenWawjBillSummary billSummary = new OpenWawjBillSummary();
            BeanUtils.copyProperties(summary, billSummary);
            billSummary.setUnitCode(summary.getSummaryDeptCode());
            //收款对象
            billSummary.setPayeeCategory((String) baoxiaoInfo.get("payee_category"));
            //收款方代码
            billSummary.setPayeeCode((String) baoxiaoInfo.get("payee_code"));
            billSummary.setBillNo(billNo);
            billSummary.setReportDate(nowDate);
            return billSummary;
        }).collect(Collectors.toList());
        //同法人+同城市公司 分组
        Map<String, List<OpenWawjBillSummary>> billSummaryMap = billSummaryList.stream().collect(Collectors.groupingBy(billSummary -> billSummary.getCompanyCode() + "@" + billSummary.getIncorporatedCompany()));
        //批次数据
        Map<String, List<List<OpenWawjBillSummary>>> batchSummaryMap = Maps.newLinkedHashMap();
        //每一千行一个批次
        billSummaryMap.forEach((k, v) -> batchSummaryMap.put(k, CollectionUtils.batch(v, 1000)));
        //获取总的批次数
        int size = batchSummaryMap.values().stream().map(batch -> new BigDecimal(batch.size())).reduce(BigDecimal.ZERO, BigDecimal::add).intValue();
        List<String> batchIdList = getBatchIdList(size);
        AtomicInteger index = new AtomicInteger(0);
        Map<String, Object> batchMap = Maps.newHashMap();
        //设置批次号及行号
        batchSummaryMap.forEach((k, v) -> v.forEach(batchList -> {
            String batchId = batchIdList.get(index.getAndIncrement());
            for (int i = 0; i < batchList.size(); i++) {
                OpenWawjBillSummary openWawjBillSummary = batchList.get(i);
                openWawjBillSummary.setBatchId(batchId);
                openWawjBillSummary.setBatchLineId(i + 1);
                wawjBillSummryDao.saveSelective(openWawjBillSummary);
                batchMap.put(openWawjBillSummary.getMd5Value(), batchId);
            }
        }));
        batchMap.keySet().forEach(md5Value -> {
            List<OpenWawjBillDetail> billDetailList = wawjBillDetailDao.listByMd5Value(md5Value);
            billDetailList.forEach(billDetail -> {
                billDetail.setBatchId((String) batchMap.get(md5Value));
                wawjBillDetailDao.updateById(billDetail);
            });
        });
        return batchIdList;
    }

    private List<String> getBatchIdList(int size) {
        List<String> batchIdList = Lists.newArrayList();
        String dateStr = DateUtils.toStr(DateUtils.now(), "yyMMddHHmm");
        String prefix = "BXB" + dateStr;
        int i = 1;
        while (size > 0) {
            size--;
            batchIdList.add(prefix + String.format("%07d", i++));
        }
        return batchIdList;
    }

    private void savePersonalBill(String companyId, String billNo, Map baoxiaoInfo, Map<String, OpenWawjAccontUnitConf> wawjAccontUnitConfMap, Map<String, OpenWawjBusinessTypeConf> wawjBusinessTypeConfMap, Map<Integer, OpenWawjReimburseTypeFeeProjectConf> wawjReimburseTypeFeeProjectConfMap, Map<String, WiwjOrgUnit> orgUnitMap, List<String> secondLevelDeptIdList) {
        int pageIndex = 1;
        int pageSize = 100;
        BillPersonalConsumeQuery query = new BillPersonalConsumeQuery();
        query.setCompanyId(companyId);
        query.setBillNo(billNo);
        query.setPageIndex(1);
        query.setPageSize(pageSize);
        BasePageDTO<BillPersonalConsumeDTO> personalBillRes = billPersonalService.queryPersonalConsumeFlowDetail(query);
        saveBill(buildPersonalBill(personalBillRes, billNo), wawjAccontUnitConfMap, wawjBusinessTypeConfMap, wawjReimburseTypeFeeProjectConfMap, baoxiaoInfo, orgUnitMap, secondLevelDeptIdList);
        int totalPages = (personalBillRes.getCount() + personalBillRes.getPageSize() - 1) / personalBillRes.getPageSize();
        while (pageIndex < totalPages) {
            query = new BillPersonalConsumeQuery();
            query.setCompanyId(companyId);
            query.setBillNo(billNo);
            query.setPageIndex(++pageIndex);
            query.setPageSize(pageSize);
            personalBillRes = billPersonalService.queryPersonalConsumeFlowDetail(query);
            saveBill(buildPersonalBill(personalBillRes, billNo), wawjAccontUnitConfMap, wawjBusinessTypeConfMap, wawjReimburseTypeFeeProjectConfMap, baoxiaoInfo, orgUnitMap, secondLevelDeptIdList);
        }
    }

    private List<WawjBill> buildPersonalBill(BasePageDTO<BillPersonalConsumeDTO> personalBillRes, String billNo) {
        List<BillPersonalConsumeDTO> dataList = personalBillRes.getDtoList();
        List<WawjBill> billList = Lists.newArrayList();
        if (ObjectUtils.isEmpty(dataList)) {
            return billList;
        }
        dataList.forEach(billDetail -> {
            BillPersonalConsumeDTO.SummaryInfoBean summaryInfoBean = billDetail.getSummaryInfoBean();
            BillPersonalConsumeDTO.OrderInfoBean orderInfoBean = billDetail.getOrderInfoBean();
            BillPersonalConsumeDTO.VoucherInfoBean voucherInfoBean = billDetail.getVoucherInfoBean();
            Integer invoiceType = voucherInfoBean.getInvoiceType();
            if (invoiceType != null && invoiceType == 0) {
                WawjBill wawjBill = new WawjBill();
                wawjBill.setBillDetailId(voucherInfoBean.getBillDetailId());
                wawjBill.setBillId(voucherInfoBean.getBillId());
                wawjBill.setOrderType(2);
                wawjBill.setOrderId(orderInfoBean.getOrderId());
                wawjBill.setBillEndDate(summaryInfoBean.getBillEndDate());
                wawjBill.setBillNo(billNo);
                wawjBill.setThirdInfo(voucherInfoBean.getThirdExtJson());
                //分贝券金额
                wawjBill.setTotalAmount(voucherInfoBean.getVoucherAmount());
                wawjBill.setOrderCategory(orderInfoBean.getOrderCategory());
                wawjBill.setOrderDate(DateUtils.toSimpleStr(orderInfoBean.getOrderCreateTime(), true));
                String systemExt = voucherInfoBean.getSystemExt();
                if (!ObjectUtils.isEmpty(systemExt)) {
                    List<Map<String, Object>> systemExtList = JsonUtils.toObj(systemExt, List.class);
                    Map<String, Object> accountMap = ObjectUtils.isEmpty(systemExtList) ? null : systemExtList.stream().filter(ext -> "核算单位".equals(ext.get("title"))).findFirst().orElse(null);
                    if (!ObjectUtils.isEmpty(accountMap)) {
                        wawjBill.setCustomField1(accountMap.get("title") + "-" + accountMap.get("name"));
                    }
                }
                billList.add(wawjBill);
            }
        });
        return billList;
    }

    private void saveCompanyBill(String companyId, String billNo, Map baoxiaoInfo, Map<String, OpenWawjAccontUnitConf> wawjAccontUnitConfMap, Map<String, OpenWawjBusinessTypeConf> wawjBusinessTypeConfMap, Map<Integer, OpenWawjReimburseTypeFeeProjectConf> wawjReimburseTypeFeeProjectConfMap, Map<String, WiwjOrgUnit> orgUnitMap, List<String> secondLevelDeptIdList) {
        int pageIndex = 1;
        int pageSize = 100;
        BillDataListQuery queryOrderDetailReq = new BillDataListQuery();
        queryOrderDetailReq.setBillNo(billNo);
        queryOrderDetailReq.setCompanyId(companyId);
        queryOrderDetailReq.setPageIndex(pageIndex);
        queryOrderDetailReq.setPageSize(pageSize);
        BasePageDTO<BillDataListDTO> billDetailRes = stereoBillService.queryBillDataListDetail(queryOrderDetailReq);
        saveBill(buildCompanyBillList(billDetailRes), wawjAccontUnitConfMap, wawjBusinessTypeConfMap, wawjReimburseTypeFeeProjectConfMap, baoxiaoInfo, orgUnitMap, secondLevelDeptIdList);
        int totalPages = (billDetailRes.getCount() + billDetailRes.getPageSize() - 1) / billDetailRes.getPageSize();
        while (pageIndex < totalPages) {
            queryOrderDetailReq.setPageIndex(++pageIndex);
            billDetailRes = stereoBillService.queryBillDataListDetail(queryOrderDetailReq);
            saveBill(buildCompanyBillList(billDetailRes), wawjAccontUnitConfMap, wawjBusinessTypeConfMap, wawjReimburseTypeFeeProjectConfMap, baoxiaoInfo, orgUnitMap, secondLevelDeptIdList);
        }
    }

    private List<WawjBill> buildCompanyBillList(BasePageDTO<BillDataListDTO> billDetailRes) {
        List<BillDataListDTO> dataList = billDetailRes.getDtoList();
        List<WawjBill> billList = Lists.newArrayList();
        if (ObjectUtils.isEmpty(dataList)) {
            return billList;
        }
        dataList.forEach(billDetail -> {
            WawjBill wawjBill = new WawjBill();
            BeanUtils.copyProperties(billDetail, wawjBill);
            wawjBill.setBillDetailId(billDetail.getId());
            wawjBill.setOrderType(1);
            wawjBill.setOrderDate(DateUtils.toSimpleStr(billDetail.getOrderCreateTime(), true));
            billList.add(wawjBill);
        });
        return billList;
    }

    private void saveBill(List<WawjBill> billList, Map<String, OpenWawjAccontUnitConf> wawjAccontUnitConfMap, Map<String, OpenWawjBusinessTypeConf> wawjBusinessTypeConfMap, Map<Integer, OpenWawjReimburseTypeFeeProjectConf> wawjReimburseTypeFeeProjectConfMap, Map baoxiaoInfo, Map<String, WiwjOrgUnit> orgUnitMap, List<String> secondLevelDeptIdList) {
        if (ObjectUtils.isEmpty(billList)) {
            return;
        }
        Date billEndDate = billList.get(0).getBillEndDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(billEndDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        billList.forEach(bill -> {
            String customValue = bill.getCustomField1().contains("核算单位") ? bill.getCustomField1() : bill.getCustomField2().contains("核算单位") ? bill.getCustomField2() : bill.getCustomField3();
            //核算单位
            String accountName = ObjectUtils.isEmpty(customValue) || !customValue.contains("-") ? null : customValue.substring(customValue.indexOf("-") + 1);
            if (ObjectUtils.isEmpty(accountName)) {
                return;
            }
            //场景类型 3用车
            int orderCategory = bill.getOrderCategory();
            //核算单位配置
            OpenWawjAccontUnitConf wawjAccontUnitConf = wawjAccontUnitConfMap.get(accountName);
            OpenWawjBillDetail wawjBillDetail = new OpenWawjBillDetail();
            //账单编号
            wawjBillDetail.setBillNo(bill.getBillNo());
            //城市公司编码
            String cityCompanyCode = wawjAccontUnitConf.getCityCompanyCode();
            wawjBillDetail.setCompanyCode(cityCompanyCode);
            //报销日期
            wawjBillDetail.setReportDate(DateUtils.toSimpleStr(DateUtils.now(), true));
            //法人公司
            wawjBillDetail.setIncorporatedCompany(wawjAccontUnitConf.getIncorporatedCompanyCode());
            //核算单位
            wawjBillDetail.setAccountCompanyCode(wawjAccontUnitConf.getAccountUnitCode());
            //报销类型及费用项目配置
            OpenWawjReimburseTypeFeeProjectConf reimburseTypeFeeProjectConf = wawjReimburseTypeFeeProjectConfMap.get(orderCategory);
            //报销类型编码
            wawjBillDetail.setExpenseTypeCode(reimburseTypeFeeProjectConf.getReimburseTypeCode());
            //费用项目编码
            wawjBillDetail.setExpenseItemCode(reimburseTypeFeeProjectConf.getFeeProjectCode());
            //三方字段信息
            String thirdInfo = bill.getThirdInfo();
            Map thirdMap = JsonUtils.toObj(thirdInfo, Map.class);
            //部门编码
            String bookerDeptId = ((String) thirdMap.get("bookerDeptId"));
            //报销宝部门编码 需要拼接城市公司
            String deptCode = cityCompanyCode + bookerDeptId.split("_")[1];
            wawjBillDetail.setDeptCode(deptCode);
            //汇总部门
            String bookerSummaryDeptId = (String) thirdMap.get("bookerSummaryDeptId");
            String bookerSummaryDeptName = (String) thirdMap.get("bookerSummaryDeptName");
            //对应的二级部门
            WiwjOrgUnit secondLevelOrgUnit = ObjectUtils.isEmpty(bookerSummaryDeptId) ? getSummaryDept(bookerDeptId, orgUnitMap, secondLevelDeptIdList) : new WiwjOrgUnit(bookerSummaryDeptId, bookerSummaryDeptName);
            //二级部门三方id
            String secondLevelOrgUnitThirdOrgId = secondLevelOrgUnit.getThird_org_id();
            //报销宝汇总部门编码 需要拼接城市公司
            String summaryDeptCode = cityCompanyCode + secondLevelOrgUnitThirdOrgId.split("_")[1];
            wawjBillDetail.setSummaryDeptCode(summaryDeptCode);
            //报销人
            wawjBillDetail.setEmployeeCode((String) baoxiaoInfo.get("employee_code"));
            //项目
            wawjBillDetail.setDimension2Code((String) thirdMap.get("costAttributioncostCode"));
            OpenWawjBusinessTypeConf wawjBusinessTypeConf = wawjBusinessTypeConfMap.get(cityCompanyCode);
            //业务类型
            wawjBillDetail.setDimension4Code(wawjBusinessTypeConf.getBusinessTypeCode());
            //头描述
            wawjBillDetail.setDescription("");
            //期间-部门-报销类型
            String lineDescription = year + "-" + month + secondLevelOrgUnit.getName() + reimburseTypeFeeProjectConf.getReimburseTypeName();
            //行描述
            wawjBillDetail.setLineDescription(lineDescription);
            //门店
            wawjBillDetail.setDimension3Code("0");
            //报销金额
            wawjBillDetail.setReportAmount(bill.getTotalAmount());
            //记录冗余字段
            wawjBillDetail.setOrderCategoryType(reimburseTypeFeeProjectConf.getOrderCategoryType());
            wawjBillDetail.setOrderCategoryName(reimburseTypeFeeProjectConf.getOrderCategoryName());
            wawjBillDetail.setOrderId(bill.getOrderId());
            wawjBillDetail.setSourceOrderId(bill.getSourceOrderId());
            wawjBillDetail.setTicketNo(bill.getTicketNo());
            wawjBillDetail.setBillId(bill.getBillId());
            wawjBillDetail.setBillNo(bill.getBillNo());
            wawjBillDetail.setBillDetailId(bill.getBillDetailId());
            wawjBillDetail.setOrderType(bill.getOrderType());
            WawjBillUtil.setMd5Value(wawjBillDetail);
            wawjBillDetail.setOrderDate(bill.getOrderDate());
            wawjBillDetailDao.saveSelective(wawjBillDetail);
        });
    }

    /**
     * 获取汇总部门  指定第三级
     *
     * @param bookerDeptId          下单人部门编码
     * @param orgUnitMap            部门信息
     * @param secondLevelDeptIdList 二级部门三方部门ID
     * @return 汇总编码
     */
    private WiwjOrgUnit getSummaryDept(String bookerDeptId, Map<String, WiwjOrgUnit> orgUnitMap, List<String> secondLevelDeptIdList) {
        WiwjOrgUnit wiwjOrgUnit = null;
        if (secondLevelDeptIdList.contains(bookerDeptId)) {
            wiwjOrgUnit = orgUnitMap.get(bookerDeptId);
        } else {
            WiwjOrgUnit thisOrgUnit = orgUnitMap.get(bookerDeptId);
            String thirdParentOrgUnitId = thisOrgUnit.getThird_parent_org_unit_id();
            if (!ObjectUtils.isEmpty(thirdParentOrgUnitId)) {
                wiwjOrgUnit = getSummaryDept(thirdParentOrgUnitId, orgUnitMap, secondLevelDeptIdList);
            }
        }
        return wiwjOrgUnit;
    }

    @Data
    private static class WiwjOrgUnit {

        /**
         * 部门id
         */
        private String id;

        /**
         * 部门名称
         */
        private String name;

        /**
         * 父部门id
         */
        private String parent_org_unit_id;

        /**
         * 第三方部门id
         */
        private String third_org_id;

        /**
         * 第三方父部门id
         */
        private String third_parent_org_unit_id;

        public WiwjOrgUnit() {
        }

        public WiwjOrgUnit(String third_org_id, String name) {
            this.third_org_id = third_org_id;
            this.name = name;
        }
    }

    @Data
    private static class WawjBill {

        /**
         * 账单明细id
         */
        private String billDetailId;

        /**
         * 账单Id
         */
        private String billId;

        /**
         * 订单类型
         */
        private Integer orderType;

        /**
         * 订单号
         */
        private String orderId;
        /**
         * 主订单号
         */
        private String sourceOrderId;

        /**
         * 票号/取票号
         */
        private String ticketNo;

        /**
         * 账单结束时间
         */
        private Date billEndDate;
        /**
         * 账单编号
         */
        private String billNo;
        /**
         * 第三方字段
         */
        private String thirdInfo;

        /**
         * 应收总额
         */
        private BigDecimal totalAmount;

        /**
         * 业务线
         */
        private Integer orderCategory;

        /**
         * 订单日期
         */
        private String orderDate;

        /**
         * 自定义字段1
         */
        private String customField1;
        /**
         * 自定义字段2
         */
        private String customField2;
        /**
         * 自定义字段3
         */
        private String customField3;

    }
}
