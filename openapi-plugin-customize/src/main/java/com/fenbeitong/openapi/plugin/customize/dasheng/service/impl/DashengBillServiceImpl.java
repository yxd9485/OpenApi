package com.fenbeitong.openapi.plugin.customize.dasheng.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.fenbei.settlement.base.dto.BasePageDTO;
import com.fenbeitong.fenbei.settlement.external.api.api.bill.IBillOpenApi;
import com.fenbeitong.fenbei.settlement.external.api.dto.BillDataListDTO;
import com.fenbeitong.fenbei.settlement.external.api.query.BillDataListQuery;
import com.fenbeitong.openapi.plugin.core.util.OpenapiOssUtils;
import com.fenbeitong.openapi.plugin.customize.dasheng.dao.OpenEbsBillDetailDao;
import com.fenbeitong.openapi.plugin.customize.dasheng.dao.OpenEbsBillSceneCostitemConfigDao;
import com.fenbeitong.openapi.plugin.customize.dasheng.dto.OpenEbsBillDetailDto;
import com.fenbeitong.openapi.plugin.customize.dasheng.entity.OpenEbsBillDetail;
import com.fenbeitong.openapi.plugin.customize.dasheng.entity.OpenEbsBillSceneCostitemConfig;
import com.fenbeitong.openapi.plugin.customize.dasheng.service.IDashengBillService;
import com.fenbeitong.openapi.plugin.func.company.dao.OpenEbsCoeCostItemsDao;
import com.fenbeitong.openapi.plugin.func.company.dao.OpenEbsCompRelationsDao;
import com.fenbeitong.openapi.plugin.func.company.dao.OpenEbsOrgCostRelationsDao;
import com.fenbeitong.openapi.plugin.func.company.entity.OpenEbsCoeCostItems;
import com.fenbeitong.openapi.plugin.func.company.entity.OpenEbsCompRelations;
import com.fenbeitong.openapi.plugin.support.common.constant.OrderCategoryEnum;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.usercenter.api.model.dto.orgunit.OrgUnitPageReqDTO;
import com.fenbeitong.usercenter.api.model.dto.orgunit.OrgUnitResDTO;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.fenbeitong.usercenter.api.service.orgunit.IOrgUnitService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>Title: DashengBillServiceImpl</p>
 * <p>Description: 51talk定制账单服务实现</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/10/19 5:01 PM
 */
@Slf4j
@ServiceAspect
@Service
public class DashengBillServiceImpl implements IDashengBillService {

    @Value("${host.harmony}")
    private String harmonyHost;

    @DubboReference(check = false)
    private IBillOpenApi stereoBillService;

    @DubboReference(check = false)
    private IBaseEmployeeExtService employeeExtService;

    @DubboReference(check = false)
    private IOrgUnitService orgUnitService;

    @Autowired
    private OpenEbsBillDetailDao openEbsBillDetailDao;

    @Autowired
    private OpenEbsCompRelationsDao compRelationsDao;

    @Autowired
    private OpenEbsOrgCostRelationsDao orgCostRelationsDao;

    @Autowired
    private OpenEbsCoeCostItemsDao coeCostItemsDao;

    @Autowired
    private OpenEbsBillSceneCostitemConfigDao sceneCostitemConfigDao;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Autowired
    private OpenapiOssUtils ossUtils;

    @Autowired
    private ExceptionRemind exceptionRemind;

    @Async
    @Override
    public void saveAndPushData(String companyId, String billNo) {
        try {
            Map<String, Object> saveRes = saveBillData(companyId, billNo);
            pushBill(companyId, billNo, (int) saveRes.get("year"), (int) saveRes.get("month"));
        } catch (Exception e) {
            String msg = "大生科技推送入账报表失败,companyId=" + companyId + ",billNo=" + billNo;
            log.error(msg, e);
            exceptionRemind.remindDingTalk(msg);
        }
    }

    @Override
    public Map<String, Object> saveBillData(String companyId, String billNo) {
        Map<String, Object> saveRes = Maps.newHashMap();
        List<OpenEbsBillDetail> billDetailList = openEbsBillDetailDao.list(billNo);
        billDetailList.forEach(billDetail -> openEbsBillDetailDao.deleteById(billDetail.getId()));
        Map<String, List<OpenEbsCompRelations>> companyMap = compRelationsDao.list().stream().collect(Collectors.groupingBy(OpenEbsCompRelations::getPsCompCode));
        Map<String, List<OpenEbsCoeCostItems>> costItemListMap = coeCostItemsDao.list().stream().collect(Collectors.groupingBy(OpenEbsCoeCostItems::getCostItemCode));
        Map<String, String> sceneItemMap = sceneCostitemConfigDao.list().stream().collect(Collectors.toMap(OpenEbsBillSceneCostitemConfig::getOrderCategory, OpenEbsBillSceneCostitemConfig::getCostItemCode));
        //部门信息
        Map<String, DaShengOrgUnit> orgUnitMap = getOrgUnitMap(companyId);
        //一级部门id列表
        List<String> firstLevelDeptIdList = orgUnitMap.values().stream().filter(o -> companyId.equals(o.getParent_org_unit_id())).map(DaShengOrgUnit::getThird_org_id).collect(Collectors.toList());
        int pageIndex = 1;
        int pageSize = 100;
        BillDataListQuery queryOrderDetailReq = new BillDataListQuery();
        queryOrderDetailReq.setBillNo(billNo);
        queryOrderDetailReq.setCompanyId(companyId);
        queryOrderDetailReq.setPageIndex(pageIndex);
        queryOrderDetailReq.setPageSize(pageSize);
        BasePageDTO<BillDataListDTO> billDetailRes = stereoBillService.queryBillDataListDetail(queryOrderDetailReq);
        Date billEndDate = billDetailRes.getDtoList().get(0).getBillEndDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(billEndDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        saveBill(billDetailRes, companyMap, costItemListMap, sceneItemMap, orgUnitMap, firstLevelDeptIdList);
        int totalPages = (billDetailRes.getCount() + billDetailRes.getPageSize() - 1) / billDetailRes.getPageSize();
        while (pageIndex < totalPages) {
            queryOrderDetailReq.setPageIndex(++pageIndex);
            billDetailRes = stereoBillService.queryBillDataListDetail(queryOrderDetailReq);
            saveBill(billDetailRes, companyMap, costItemListMap, sceneItemMap, orgUnitMap, firstLevelDeptIdList);
        }
        saveRes.put("year", year);
        saveRes.put("month", month);
        return saveRes;
    }

    private Map<String, DaShengOrgUnit> getOrgUnitMap(String companyId) {
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
        Map<String, DaShengOrgUnit> dashengOrgUnitMap = Maps.newHashMap();
        Map<String, OrgUnitResDTO> orgUnitResMap = orgUnitList.stream().filter(orgUnitRes -> !ObjectUtils.isEmpty(orgUnitRes.getThird_org_id())).collect(Collectors.toMap(OrgUnitResDTO::getId, Function.identity()));
        orgUnitResMap.forEach((k, v) -> {
            DaShengOrgUnit wiwjOrgUnit = new DaShengOrgUnit();
            wiwjOrgUnit.setId(v.getId());
            wiwjOrgUnit.setName(v.getName());
            wiwjOrgUnit.setParent_org_unit_id(v.getParent_org_unit_id());
            wiwjOrgUnit.setThird_org_id(v.getThird_org_id());
            String parentOrgUnitId = v.getParent_org_unit_id();
            OrgUnitResDTO parentOrgUnit = ObjectUtils.isEmpty(parentOrgUnitId) ? null : orgUnitResMap.get(parentOrgUnitId);
            wiwjOrgUnit.setThird_parent_org_unit_id(parentOrgUnit == null ? null : parentOrgUnit.getThird_org_id());
            dashengOrgUnitMap.put(v.getThird_org_id(), wiwjOrgUnit);
        });
        return dashengOrgUnitMap;
    }

    private void saveBill(BasePageDTO<BillDataListDTO> billDetailRes, Map<String, List<OpenEbsCompRelations>> companyMap, Map<String, List<OpenEbsCoeCostItems>> costItemListMap, Map<String, String> sceneItemMap, Map<String, DaShengOrgUnit> orgUnitMap, List<String> firstLevelDeptIdList) {
        List<BillDataListDTO> bilList = billDetailRes.getDtoList();
        if (ObjectUtils.isEmpty(bilList)) {
            return;
        }
        Date billEndDate = billDetailRes.getDtoList().get(0).getBillEndDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(billEndDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        for (BillDataListDTO billMap : bilList) {
            String thirdInfo = billMap.getThirdInfo();
            Map thirdInfoMap = JsonUtils.toObj(thirdInfo, Map.class);
            if (ObjectUtils.isEmpty(thirdInfoMap)) {
                log.info("大生科技账单三方字段为空:{}", JsonUtils.toJson(billMap));
                thirdInfoMap = Maps.newHashMap();
            }
            OpenEbsBillDetail openEbsBillDetail = new OpenEbsBillDetail();
            //人员所属公司
            String psCompanyCode = (String) thirdInfoMap.get("contracat_company_code1");
            //费用归属部门三方id
            String costAttributionDeptId = (String) thirdInfoMap.get("costAttributionDeptId");
            //人员一级部门三方id
            String firstLevelThirdDeptId = costAttributionDeptId == null ? null : getFirstLevelThirdDeptId(orgUnitMap, firstLevelDeptIdList, costAttributionDeptId);
            //ebs公司列表
            List<OpenEbsCompRelations> compRelationsList = companyMap.get(psCompanyCode) == null ? Lists.newArrayList() : companyMap.get(psCompanyCode);
            List<OpenEbsCompRelations> locationCompRelationsList = compRelationsList.stream().filter(c -> !ObjectUtils.isEmpty(c.getPsLocationCode())).collect(Collectors.toList());
            //人员位置
            String locationCode = (String) thirdInfoMap.get("location_code1");
            //先严格匹配
            List<OpenEbsCompRelations> matchCompRelList = locationCompRelationsList.stream().filter(cr -> cr.getPsLocationCode().equals(locationCode) && !ObjectUtils.isEmpty(cr.getAttribute2()) && cr.getAttribute2().equals(firstLevelThirdDeptId)).collect(Collectors.toList());
            //匹配不到再放松条件去掉 一级部门的条件
            if (ObjectUtils.isEmpty(matchCompRelList)) {
                matchCompRelList = locationCompRelationsList.stream().filter(cr -> cr.getPsLocationCode().equals(locationCode)).collect(Collectors.toList());
            }
            String coaCom = ObjectUtils.isEmpty(matchCompRelList) ? null : matchCompRelList.get(0).getEbsCompCode();
            if (coaCom == null) {
                coaCom = ObjectUtils.isEmpty(compRelationsList) ? null : compRelationsList.get(0).getEbsCompCode();
            }
            openEbsBillDetail.setCoaCom(coaCom);
            openEbsBillDetail.setBillId(billMap.getBillId());
            openEbsBillDetail.setBillDetailId(billMap.getId());
            //成本中心
            openEbsBillDetail.setCoaCc((String) thirdInfoMap.get("ebsCcCode"));
            String costAttrCode = (String) thirdInfoMap.get("ebsCcAttrCode");
            String costItemCode = sceneItemMap.get(String.valueOf(billMap.getOrderCategory()));
            List<OpenEbsCoeCostItems> openEbsCoeCostItemList = costItemListMap.get(costItemCode);
            OpenEbsCoeCostItems openEbsCoeCostItems = ObjectUtils.isEmpty(openEbsCoeCostItemList) ? null : openEbsCoeCostItemList.stream().filter(costItem -> costItem.getCostAttrCode().equals(costAttrCode)).findFirst().orElse(null);
            //科目code
            openEbsBillDetail.setCoaAcc(openEbsCoeCostItems == null ? null : openEbsCoeCostItems.getReferenceCode());
            openEbsBillDetail.setDebit(OrderCategoryEnum.Hotel.getKey() == billMap.getOrderCategory() ? billMap.getTotalAmount().divide(new BigDecimal("1.06"), 2, BigDecimal.ROUND_HALF_UP) : billMap.getTotalAmount());
            openEbsBillDetail.setBillNo(billMap.getBillNo());
            //场景名称
            String sceneTypeName = OrderCategoryEnum.getValueByKey(billMap.getOrderCategory());
            openEbsBillDetail.setDesp(year + "年" + month + "月-" + (String) thirdInfoMap.get("ebsCcDesc") + "-" + sceneTypeName + "-差旅费");
            openEbsBillDetailDao.saveSelective(openEbsBillDetail);
        }
    }

    private String getFirstLevelThirdDeptId(Map<String, DaShengOrgUnit> orgUnitMap, List<String> firstLevelDeptIdList, String thirdDeptId) {
        String firstLevelThirdDeptId = null;
        if (firstLevelDeptIdList.contains(thirdDeptId)) {
            firstLevelThirdDeptId = thirdDeptId;
        } else {
            DaShengOrgUnit thisOrgUnit = orgUnitMap.get(thirdDeptId);
            if (thisOrgUnit != null) {
                String thirdParentOrgUnitId = thisOrgUnit.getThird_parent_org_unit_id();
                if (!ObjectUtils.isEmpty(thirdParentOrgUnitId)) {
                    firstLevelThirdDeptId = getFirstLevelThirdDeptId(orgUnitMap, firstLevelDeptIdList, thirdParentOrgUnitId);
                }
            }
        }
        return firstLevelThirdDeptId;
    }


    @Override
    public void updateBillData(OpenEbsBillDetailDto openEbsBillDetailDto) {
        OpenEbsBillDetail openEbsBillDetail = new OpenEbsBillDetail();
        BeanUtils.copyProperties(openEbsBillDetailDto, openEbsBillDetail);
        openEbsBillDetailDao.updateById(openEbsBillDetail);
    }

    @Async
    @Override
    public void pushBill(String companyId, String billNo, int year, int month) {
        List<OpenMsgSetup> openMsgSetups = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList("51talk_customize_bill_info"));
        Map billInfo = JsonUtils.toObj(openMsgSetups.get(0).getStrVal1(), Map.class);
        //汇总数据生成excel表格
        List<OpenEbsBillDetail> billDetailSumList = openEbsBillDetailDao.listSumData(billNo);
        //按公司再次分组
        Map<String, List<OpenEbsBillDetail>> companyBillMap = billDetailSumList.stream().collect(Collectors.groupingBy(OpenEbsBillDetail::getCoaCom, LinkedHashMap::new, Collectors.toList()));
        //按公司生成表格
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        companyBillMap.forEach((companyCode, billList) -> buildWorkBook(workbook, companyCode, billList, billInfo, year, month));
        //发送邮件
        sendEmail(workbook, companyId, month, billInfo);
    }

    private void sendEmail(SXSSFWorkbook workbook, String companyId, int month, Map billInfo) {
        Map<String, Object> mailMap = Maps.newLinkedHashMap();
        String adminEmail = (String) billInfo.get("admin_email");
        mailMap.put("customerId", "open-api");
        mailMap.put("toList", Lists.newArrayList(adminEmail.split(",")));
        mailMap.put("ccList", Lists.newArrayList("jslj@fenbeitong.com"));
        mailMap.put("bccList", Lists.newArrayList());
        mailMap.put("subject", "北京大生在线科技有限公司入账报表");
        // 模板数据
        Map<String, Object> dataMap = Maps.newLinkedHashMap();
        dataMap.put("admin", billInfo.get("admin"));
        dataMap.put("billMonth", month + "月");
        // 模板
        Map<String, Object> htmlMap = Maps.newLinkedHashMap();
        htmlMap.put("templateId", "dasheng_customize_bill_send_post.html");
        htmlMap.put("data", dataMap);
        mailMap.put("html", htmlMap);
        File file = null;
        try (FileOutputStream fileOutputStream = new FileOutputStream(file = new File(FileUtils.getUserDirectory(), month + "月入账报表" + ".xlsx"))) {
            // 保存到临时文件
            workbook.write(fileOutputStream);
            //上传分贝通oss服务器
            OpenapiOssUtils.UploadFileResponse uploadFileResponse = ossUtils.uploadFiles("openapi", companyId, new File[]{file});
            String url = uploadFileResponse.getData().get(0).getUrl();
            Map<String, Object> attachMap = Maps.newHashMap();
            attachMap.put("url", url);
            attachMap.put("description", file.getName());
            attachMap.put("name", file.getName());
            mailMap.put("emailAttachmentList", Lists.newArrayList(attachMap));
            MultiValueMap multiValueMap = new LinkedMultiValueMap();
            multiValueMap.add("data", mailMap);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setCacheControl("no-cache");
            headers.setConnection("Keep-Alive");
            HttpEntity httpEntity = new HttpEntity(multiValueMap, headers);
            RestHttpUtils.post(harmonyHost + "/harmony/mail/attachment", httpEntity);
        } catch (IOException e) {
        } finally {
            // 确保要释放
            workbook.dispose();
        }
    }

    private void buildWorkBook(SXSSFWorkbook workbook, String companyCode, List<OpenEbsBillDetail> billList, Map billInfo, int year, int month) {
        String shortName = (String) billInfo.get(companyCode);
        if (shortName == null) {
            shortName = companyCode;
        }
        SXSSFSheet sheet = workbook.createSheet(shortName);
        final AtomicInteger row = new AtomicInteger(0);
        SXSSFRow titleRow = sheet.createRow(row.getAndIncrement());
        //设置样式-颜色
        CellStyle style = workbook.createCellStyle();
        //设置填充方案
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //设置自定义填充颜色
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
        List<String> titles = Lists.newArrayList("COA_COM,COA_BU,COA_CC,COA_ACC,COA_IC,COA_EC,COA_RE,COA_RESERVE1,COA_RESERVE2,COA_RESERVE3,借项,贷项,行说明".split(","));
        for (int index = 0; index < titles.size(); index++) {
            SXSSFCell cell = titleRow.createCell(index);
            cell.setCellValue(titles.get(index));
            cell.setCellStyle(style);
        }
        CellStyle numberStyle = workbook.createCellStyle();
        numberStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OpenEbsBillDetail bill : billList) {
            SXSSFRow debitRow = sheet.createRow(row.getAndIncrement());
            SXSSFCell cell = debitRow.createCell(0);
            cell.setCellValue(bill.getCoaCom());
            cell = debitRow.createCell(2);
            cell.setCellValue(bill.getCoaCc());
            cell = debitRow.createCell(3);
            cell.setCellValue(bill.getCoaAcc());
            cell = debitRow.createCell(10);
            cell.setCellValue(bill.getDebit().doubleValue());
            cell.setCellStyle(numberStyle);
            cell = debitRow.createCell(12);
            cell.setCellValue(bill.getDesp());
            totalPrice = totalPrice.add(bill.getDebit());
        }
        SXSSFRow creditRow = sheet.createRow(row.getAndIncrement());
        SXSSFCell cell = creditRow.createCell(0);
        cell.setCellValue(companyCode);
        cell = creditRow.createCell(2);
        cell.setCellValue((String) billInfo.get("credit_cost_center"));
        cell = creditRow.createCell(3);
        cell.setCellValue((String) billInfo.get("credit_account"));
        cell = creditRow.createCell(11);
        cell.setCellValue(totalPrice.doubleValue());
        cell.setCellStyle(numberStyle);
        cell = creditRow.createCell(12);
        cell.setCellValue(year + "年" + month + "月-" + (ObjectUtils.isEmpty(shortName) ? companyCode : shortName) + "-差旅费");
        sheet.trackAllColumnsForAutoSizing();
        for (int i = 0; i < titles.size(); i++) {
            sheet.autoSizeColumn(i);
            int width = Math.max(15 * 256, Math.min(255 * 256, sheet.getColumnWidth(i) * 12 / 10));
            sheet.setColumnWidth(i, width);
        }
    }

    @Data
    private static class DaShengOrgUnit {

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
    }
}
