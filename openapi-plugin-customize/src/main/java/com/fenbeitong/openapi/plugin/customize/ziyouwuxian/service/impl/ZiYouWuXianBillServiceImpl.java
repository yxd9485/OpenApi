package com.fenbeitong.openapi.plugin.customize.ziyouwuxian.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.openapi.plugin.core.util.OpenapiOssUtils;
import com.fenbeitong.openapi.plugin.customize.ziyouwuxian.dao.OpenBillSceneSumZywxDao;
import com.fenbeitong.openapi.plugin.customize.ziyouwuxian.dao.OpenBillSumZywxDao;
import com.fenbeitong.openapi.plugin.customize.ziyouwuxian.entity.OpenBillSceneSumZywx;
import com.fenbeitong.openapi.plugin.customize.ziyouwuxian.entity.OpenBillSumZywx;
import com.fenbeitong.openapi.plugin.customize.ziyouwuxian.service.IZiYouWuXianBillService;
import com.fenbeitong.openapi.plugin.etl.service.IEtlExcelService;
import com.fenbeitong.openapi.plugin.support.bill.service.IOpenBillService;
import com.fenbeitong.openapi.plugin.support.callback.dao.OpenBillDetailRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.OpenBillDetailRecord;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeBaseInfo;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeePageListResult;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Title: ZiYouWuXianBillServiceImpl</p>
 * <p>Description: 自由无限定制账单服务实现</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/10/19 5:01 PM
 */
@ServiceAspect
@Service
public class ZiYouWuXianBillServiceImpl implements IZiYouWuXianBillService {

    @Autowired
    private OpenBillSceneSumZywxDao billSceneSumZywjxDao;

    @Autowired
    private OpenBillSumZywxDao billSumZywjxDao;

    @Autowired
    private OpenBillDetailRecordDao openBillDetailRecordDao;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Autowired
    private OpenapiOssUtils ossUtils;

    @Value("${host.harmony}")
    private String harmonyHost;

    @Autowired
    private IOpenBillService openBillService;

    @Autowired
    private IEtlExcelService etlExcelService;

    @DubboReference(check = false)
    private IBaseEmployeeExtService employeeExtService;

    @Override
    public void pushBill(String companyId, String billNo) {
        openBillService.saveBill(companyId, billNo);
        createBill(companyId, billNo, "1");
        createBill(companyId, billNo, "2");
        sendBill(companyId, billNo);
    }

    @Override
    public void createBill(String companyId, String billNo, String type) {
        //需要过滤掉的人员列表
        List<String> filteredEmployeeIdList = filteredEmployeeIdList(companyId);
        //生成该账期的数据
        List<OpenBillDetailRecord> billDetailList = openBillDetailRecordDao.list(billNo, companyId);
        if (!ObjectUtils.isEmpty(filteredEmployeeIdList)) {
            billDetailList = billDetailList.stream().filter(bill -> !filteredEmployeeIdList.contains(bill.getEmployeeId())).collect(Collectors.toList());
        }
        if ("1".equals(type)) { //总的汇总数据
            List<OpenBillSumZywx> list = billSumZywjxDao.list(billNo, companyId);
            list.forEach(e -> billSumZywjxDao.delete(e));
            saveBillSumData(billDetailList, companyId, billNo);
        } else if ("2".equals(type)) { //场景汇总数据
            List<OpenBillSceneSumZywx> list = billSceneSumZywjxDao.list(billNo, companyId);
            list.forEach(e -> billSceneSumZywjxDao.delete(e));
            saveSceneBillSumData(billDetailList, billNo, companyId);
        }

    }

    private List<String> filteredEmployeeIdList(String companyId) {
        List<OpenMsgSetup> openMsgSetups = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList("zywx_un_exprot_dept_id"));
        OpenMsgSetup openMsgSetup = ObjectUtils.isEmpty(openMsgSetups) ? null : openMsgSetups.get(0);
        String deptId = openMsgSetup == null ? null : openMsgSetup.getStrVal1();
        List<String> filteredEmployeeIdList = Lists.newArrayList();
        if (!ObjectUtils.isEmpty(deptId)) {
            int pageIndex = 1;
            EmployeePageListResult employeeList = employeeExtService.queryEmployeeByDeptId(2, companyId, deptId, 100, pageIndex);
            while (employeeList != null && !ObjectUtils.isEmpty(employeeList.getData())) {
                filteredEmployeeIdList.addAll(employeeList.getData().stream().map(EmployeeBaseInfo::getId).collect(Collectors.toList()));
                employeeList = employeeExtService.queryEmployeeByDeptId(2, companyId, deptId, 100, ++pageIndex);
            }
        }
        return filteredEmployeeIdList;
    }

    @Override
    public void sendBill(String companyId, String billNo) {
        List<OpenMsgSetup> openMsgSetups = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList("wujixian_customize_bill_info"));
        Map billInfo = JsonUtils.toObj(openMsgSetups.get(0).getStrVal1(), Map.class);
        List<OpenBillDetailRecord> billDetailList = openBillDetailRecordDao.list(billNo, companyId);
        Date billEndDate = billDetailList.get(0).getBillEndDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(billEndDate);
        //int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        //查询汇总数据
        List<OpenBillSumZywx> sumList = billSumZywjxDao.list(billNo, companyId);
        //查询场景数据
        List<OpenBillSceneSumZywx> sceneList = billSceneSumZywjxDao.list(billNo, companyId);
        Map<String, Object> params = Maps.newHashMap();
        params.put("month", month);
        Map<String, List> srcData = Maps.newHashMap();
        srcData.put(month + "月分贝通汇总", sumList);
        srcData.put("财务-分贝通", sumList);
        //各场景汇总数据生成excel表格
        List<OpenBillSceneSumZywx> airSceneList = sceneList.stream().filter(e -> e.getOrderCategory() == 7).collect(Collectors.toList());
        if (airSceneList != null && airSceneList.size() > 0) {
            srcData.put("飞机票", airSceneList);
        }
        List<OpenBillSceneSumZywx> trainSceneList = sceneList.stream().filter(e -> e.getOrderCategory() == 15).collect(Collectors.toList());
        if (trainSceneList != null && trainSceneList.size() > 0) {
            srcData.put("火车票", trainSceneList);
        }
        List<OpenBillSceneSumZywx> hotelSceneList = sceneList.stream().filter(e -> e.getOrderCategory() == 11).collect(Collectors.toList());
        if (hotelSceneList != null && hotelSceneList.size() > 0) {
            srcData.put("酒店", hotelSceneList);
        }
        List<OpenBillSceneSumZywx> carSceneList = sceneList.stream().filter(e -> e.getOrderCategory() == 3).collect(Collectors.toList());
        if (carSceneList != null && carSceneList.size() > 0) {
            srcData.put("用车", carSceneList);
        }
        List<OpenBillSceneSumZywx> shangsongSceneList = sceneList.stream().filter(e -> e.getOrderCategory() == 130).collect(Collectors.toList());
        if (shangsongSceneList != null && shangsongSceneList.size() > 0) {
            srcData.put("闪送", shangsongSceneList);
        }
        List<OpenBillSceneSumZywx> dinnerSceneList = sceneList.stream().filter(e -> e.getOrderCategory() == 60 || e.getOrderCategory() == 50).collect(Collectors.toList());
        if (dinnerSceneList != null && dinnerSceneList.size() > 0) {
            srcData.put("餐费", dinnerSceneList);
        }
        List<OpenBillSceneSumZywx> mallSceneList = sceneList.stream().filter(e -> e.getOrderCategory() == 20).collect(Collectors.toList());
        if (mallSceneList != null && mallSceneList.size() > 0) {
            srcData.put("采购", mallSceneList);
        }
        List<OpenBillSceneSumZywx> altmanSceneList = sceneList.stream().filter(e -> e.getOrderCategory() == 911).collect(Collectors.toList());
        if (altmanSceneList != null && altmanSceneList.size() > 0) {
            srcData.put("其他订单", altmanSceneList);
        }
        String fileName = "PRphoto " + month + "月份分贝通支付明细";
        String url = etlExcelService.createExcel(1000L, fileName, srcData, params);
        //发送邮件
        sendEmail(url, fileName, companyId, month, billInfo);
    }

    private void saveSceneBillSumData(List<OpenBillDetailRecord> billDetailList, String billNo, String companyId) {
        List<Integer> sceneList = Lists.newArrayList(7, 11, 15, 3, 130, 60, 50, 911, 20);
        //先获取各个场景的数据
        Map<Integer, List<OpenBillDetailRecord>> sceneDataMap = billDetailList.stream().filter(e -> e.getOrderCategory() != null && sceneList.contains(e.getOrderCategory())).collect(Collectors.groupingBy(OpenBillDetailRecord::getOrderCategory));
        List<OpenBillDetailRecord> dinnerRecordList = new ArrayList<>();
        for (Integer key : sceneDataMap.keySet()) {
            if (key == 50 || key == 60) {
                dinnerRecordList.addAll(sceneDataMap.get(key));
            } else {
                List<OpenBillDetailRecord> openBillDetailRecords = sceneDataMap.get(key);
                //再获取各个场景各个项目的数据
                createSceneSumData(billNo, companyId, key, openBillDetailRecords);
            }
        }
        //保存用餐数据
        createSceneSumData(billNo, companyId, 50, dinnerRecordList);
    }

    private void sendEmail(String url, String fileName, String companyId, int month, Map billInfo) {
        if (ObjectUtils.isEmpty(url)) {
            return;
        }
        Map<String, Object> mailMap = Maps.newLinkedHashMap();
        mailMap.put("customerId", "open-api");
        mailMap.put("toList", Lists.newArrayList(String.valueOf(billInfo.get("admin_email")).split(",")));
        mailMap.put("ccList", Lists.newArrayList(String.valueOf(billInfo.get("copy_email")).split(",")));
        mailMap.put("bccList", Lists.newArrayList());
        mailMap.put("subject", "自由无限（北京）国际影像有限公司" + month + "月分贝通支付明细");
        // 模板数据
        Map<String, Object> dataMap = Maps.newLinkedHashMap();
        dataMap.put("admin", billInfo.get("admin"));
        dataMap.put("subject", billInfo.get("subject_name"));
        dataMap.put("billMonth", month + "月");
        // 模板
        Map<String, Object> htmlMap = Maps.newLinkedHashMap();
        htmlMap.put("templateId", "zywx_customize_bill_send_post.html");  // dev id   5fa29d3791b4910fc9c16dab
        htmlMap.put("data", dataMap);
        mailMap.put("html", htmlMap);
        Map<String, Object> attachMap = Maps.newHashMap();
        attachMap.put("url", url);
        attachMap.put("name", fileName + ".xlsx");
        attachMap.put("description", fileName + ".xlsx");
        mailMap.put("emailAttachmentList", Lists.newArrayList(attachMap));
        MultiValueMap multiValueMap = new LinkedMultiValueMap();
        multiValueMap.add("data", mailMap);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setConnection("Keep-Alive");
        headers.setCacheControl("no-cache");
        HttpEntity httpEntity = new HttpEntity(multiValueMap, headers);
        RestHttpUtils.post(harmonyHost + "/harmony/mail/attachment", httpEntity);
    }

    private void createSceneSumData(String billNo, String companyId, Integer key, List<OpenBillDetailRecord> openBillDetailRecords) {
        Map<String, List<OpenBillDetailRecord>> itemDataMap = openBillDetailRecords.stream().filter(e -> !StringUtils.isBlank(e.getItemCostAttributionCode())).collect(Collectors.groupingBy(OpenBillDetailRecord::getItemCostAttributionCode));
        List<String> projectCodeList = getSortedProjectCode(itemDataMap.keySet());
        for (String projectCode : projectCodeList) {
            List<OpenBillDetailRecord> itemsDetail = itemDataMap.get(projectCode);
            List<String> customerManagers = new ArrayList<>();
            String itemName = "";
            BigDecimal airRefundInsuanceChangeSum = BigDecimal.ZERO;
            BigDecimal trainRefundInsuranceChangeSum = BigDecimal.ZERO;
            BigDecimal airServiceFee = BigDecimal.ZERO;
            BigDecimal trainServiceFee = BigDecimal.ZERO;
            BigDecimal airTravelPrice = BigDecimal.ZERO;
            BigDecimal carTotalPrice = BigDecimal.ZERO;
            BigDecimal dinnerTotalPrice = BigDecimal.ZERO;
            BigDecimal hotelTotalPrice = BigDecimal.ZERO;
            BigDecimal shansongTotalPrice = BigDecimal.ZERO;
            BigDecimal trainTravelPrice = BigDecimal.ZERO;
            BigDecimal altmanTotalPrice = BigDecimal.ZERO;
            //采购总金额
            BigDecimal mallTotalPrice = BigDecimal.ZERO;
            for (OpenBillDetailRecord billDetail : itemsDetail) {
                if (projectCode.equals(billDetail.getItemCostAttributionCode())) {
                    itemName = billDetail.getItemCostAttributionName();
                }
                switch (key) {
                    case 7:
                        BigDecimal airRefundExtFee = billDetail.getRefundExtFee() == null ? BigDecimal.ZERO : billDetail.getRefundExtFee();
                        BigDecimal airInsurancePrice = billDetail.getInsurancePrice() == null ? BigDecimal.ZERO : billDetail.getInsurancePrice();
                        airRefundInsuanceChangeSum = airRefundInsuanceChangeSum.add(airRefundExtFee.add(airInsurancePrice));
                        airServiceFee = airServiceFee.add(billDetail.getServiceFee() != null ? billDetail.getServiceFee() : BigDecimal.ZERO);
                        BigDecimal airportFee = billDetail.getAirportFee() == null ? BigDecimal.ZERO : billDetail.getAirportFee();
                        BigDecimal airUpgrateFee = billDetail.getUpgrateFee() == null ? BigDecimal.ZERO : billDetail.getUpgrateFee();
                        BigDecimal fuelFee = billDetail.getFuelFee() == null ? BigDecimal.ZERO : billDetail.getFuelFee();
                        BigDecimal airSalePrice = billDetail.getSalePrice() == null ? BigDecimal.ZERO : "9".equals(billDetail.getTicketStstus()) ? BigDecimal.ZERO : billDetail.getSalePrice();
                        BigDecimal airChangeFee = billDetail.getChangeFee() == null ? BigDecimal.ZERO : billDetail.getChangeFee();
                        BigDecimal airChangeExtFee = billDetail.getChangeExtFee() == null ? BigDecimal.ZERO : billDetail.getChangeExtFee();
                        airTravelPrice = airTravelPrice.add(airSalePrice).add(airportFee).add(fuelFee).add(airUpgrateFee).add(airChangeFee).add(airChangeExtFee);
                        break;
                    case 15:
                        BigDecimal trainRefundExtFee = billDetail.getRefundExtFee() == null ? BigDecimal.ZERO : billDetail.getRefundExtFee();
                        BigDecimal trainInsurancePrice = billDetail.getInsurancePrice() == null ? BigDecimal.ZERO : billDetail.getInsurancePrice();
                        BigDecimal trainChangeExtFee = billDetail.getChangeExtFee() == null ? BigDecimal.ZERO : billDetail.getChangeExtFee();
                        trainRefundInsuranceChangeSum = trainRefundInsuranceChangeSum.add(trainRefundExtFee.add(trainInsurancePrice).add(trainChangeExtFee));
                        trainServiceFee = trainServiceFee.add(billDetail.getServiceFee() == null ? BigDecimal.ZERO : billDetail.getServiceFee());
                        BigDecimal trainSalePrice = billDetail.getSalePrice() == null ? BigDecimal.ZERO : "17".equals(billDetail.getTicketStstus()) ? BigDecimal.ZERO : billDetail.getSalePrice();
                        BigDecimal trainTaxes = billDetail.getTaxes() == null ? BigDecimal.ZERO : billDetail.getTaxes();
                        BigDecimal trainUpgrateFee = billDetail.getUpgrateFee() == null ? BigDecimal.ZERO : billDetail.getUpgrateFee();
                        BigDecimal trainChangeFee = billDetail.getChangeFee() == null ? BigDecimal.ZERO : billDetail.getChangeFee();
                        trainTravelPrice = trainTravelPrice.add(trainSalePrice).add(trainTaxes).add(trainUpgrateFee).add(trainChangeFee);
                        break;
                    case 11:
                        hotelTotalPrice = hotelTotalPrice.add(billDetail.getTotalAmount() != null ? billDetail.getTotalAmount() : BigDecimal.ZERO);
                        break;
                    case 3:
                        carTotalPrice = carTotalPrice.add(billDetail.getTotalAmount() != null ? billDetail.getTotalAmount() : BigDecimal.ZERO);
                        break;
                    case 130:
                        shansongTotalPrice = shansongTotalPrice.add(billDetail.getTotalAmount() != null ? billDetail.getTotalAmount() : BigDecimal.ZERO);
                        break;
                    case 60:
                    case 50:
                        dinnerTotalPrice = dinnerTotalPrice.add(billDetail.getTotalAmount() != null ? billDetail.getTotalAmount() : BigDecimal.ZERO);
                        break;
                    case 911:
                        altmanTotalPrice = altmanTotalPrice.add(billDetail.getTotalAmount() != null ? billDetail.getTotalAmount() : BigDecimal.ZERO);
                        break;
                    case 20:
                        mallTotalPrice = mallTotalPrice.add(billDetail.getTotalAmount() == null ? BigDecimal.ZERO : billDetail.getTotalAmount());
                }
                boolean dinner = billDetail.getOrderCategory() == 50 || billDetail.getOrderCategory() == 60;
                String customerManager = dinner ? billDetail.getCustomeField3() == null ? null : billDetail.getCustomeField3().trim().split("-")[1] : billDetail.getEmployeeName();
                if (!ObjectUtils.isEmpty(customerManager) && !customerManagers.contains(customerManager)) {
                    customerManagers.add(customerManager);
                }
            }
            String customerManagerStr = String.join(",", customerManagers);
            OpenBillSceneSumZywx billSceneSumZywjx = OpenBillSceneSumZywx.builder()
                    .airRefundInsuranceSum(airRefundInsuanceChangeSum)
                    .airServiceFee(airServiceFee)
                    .airTicketPrice(airTravelPrice)
                    .orderCategory(key)
                    .billNo(billNo)
                    .carTotalPrice(carTotalPrice)
                    .companyId(companyId)
                    .customerManager(customerManagerStr)
                    .dinnerTotalPrice(dinnerTotalPrice)
                    .hotelTotalPrice(hotelTotalPrice)
                    .itemName(itemName)
                    .itemNo(projectCode)
                    .shansongTotalPrice(shansongTotalPrice)
                    .trainRefundInsuranceSum(trainRefundInsuranceChangeSum)
                    .trainServiceFee(trainServiceFee)
                    .trainTicketPrice(trainTravelPrice)
                    .altmanTotalPrice(altmanTotalPrice)
                    .mallTotalPrice(mallTotalPrice)
                    .build();
            BigDecimal airTotalPrice = billSceneSumZywjx.getAirTicketPrice().add(billSceneSumZywjx.getAirRefundInsuranceSum()).add(billSceneSumZywjx.getAirServiceFee());
            BigDecimal trainTotalPrice = billSceneSumZywjx.getTrainTicketPrice().add(billSceneSumZywjx.getTrainServiceFee()).add(billSceneSumZywjx.getTrainRefundInsuranceSum());
            BigDecimal sumTotalPrice = airTotalPrice.add(trainTotalPrice).add(carTotalPrice).add(hotelTotalPrice).add(shansongTotalPrice).add(dinnerTotalPrice).add(altmanTotalPrice).add(mallTotalPrice);
            billSceneSumZywjx.setSumTotalPrice(sumTotalPrice);
            billSceneSumZywjxDao.saveSelective(billSceneSumZywjx);
        }
    }

    private void saveBillSumData(List<OpenBillDetailRecord> billDetailList, String companyId, String billNo) {
        List<Integer> sceneList = Lists.newArrayList(7, 11, 15, 3, 130, 60, 50, 911, 20);
        Map<String, List<OpenBillDetailRecord>> collect = billDetailList.stream().filter(e -> !StringUtils.isBlank(e.getItemCostAttributionCode()) && sceneList.contains(e.getOrderCategory())).collect(Collectors.groupingBy(OpenBillDetailRecord::getItemCostAttributionCode));
        List<String> projectCodeList = getSortedProjectCode(collect.keySet());
        for (String projectCode : projectCodeList) {
            List<OpenBillDetailRecord> openBillDetailRecords = collect.get(projectCode);
            List<String> customerManagers = new ArrayList<>();
            String itemName = openBillDetailRecords.get(0).getItemCostAttributionName();
            BigDecimal airRefundInsuanceChangeSum = BigDecimal.ZERO;
            BigDecimal trainRefundInsuranceChangeSum = BigDecimal.ZERO;
            BigDecimal airServiceFee = BigDecimal.ZERO;
            BigDecimal trainServiceFee = BigDecimal.ZERO;
            BigDecimal airTravelPrice = BigDecimal.ZERO;
            BigDecimal carTotalPrice = BigDecimal.ZERO;
            BigDecimal dinnerTotalPrice = BigDecimal.ZERO;
            BigDecimal hotelTotalPrice = BigDecimal.ZERO;
            BigDecimal shansongTotalPrice = BigDecimal.ZERO;
            BigDecimal trainTravelPrice = BigDecimal.ZERO;
            BigDecimal altmanTotalPrice = BigDecimal.ZERO;
            //采购总金额
            BigDecimal mallTotalPrice = BigDecimal.ZERO;
            for (OpenBillDetailRecord billDetail : openBillDetailRecords) {
                switch (billDetail.getOrderCategory()) {
                    case 7:
                        BigDecimal airRefundExtFee = billDetail.getRefundExtFee() == null ? BigDecimal.ZERO : billDetail.getRefundExtFee();
                        BigDecimal airInsurancePrice = billDetail.getInsurancePrice() == null ? BigDecimal.ZERO : billDetail.getInsurancePrice();
                        airRefundInsuanceChangeSum = airRefundInsuanceChangeSum.add(airRefundExtFee.add(airInsurancePrice));
                        airServiceFee = airServiceFee.add(billDetail.getServiceFee() != null ? billDetail.getServiceFee() : BigDecimal.ZERO);
                        BigDecimal airportFee = billDetail.getAirportFee() == null ? BigDecimal.ZERO : billDetail.getAirportFee();
                        BigDecimal airUpgrateFee = billDetail.getUpgrateFee() == null ? BigDecimal.ZERO : billDetail.getUpgrateFee();
                        BigDecimal fuelFee = billDetail.getFuelFee() == null ? BigDecimal.ZERO : billDetail.getFuelFee();
                        BigDecimal airSalePrice = billDetail.getSalePrice() == null ? BigDecimal.ZERO : "9".equals(billDetail.getTicketStstus()) ? BigDecimal.ZERO : billDetail.getSalePrice();
                        BigDecimal airChangeFee = billDetail.getChangeFee() == null ? BigDecimal.ZERO : billDetail.getChangeFee();
                        BigDecimal airChangeExtFee = billDetail.getChangeExtFee() == null ? BigDecimal.ZERO : billDetail.getChangeExtFee();
                        airTravelPrice = airTravelPrice.add(airSalePrice).add(airportFee).add(fuelFee).add(airUpgrateFee).add(airChangeFee).add(airChangeExtFee);
                        break;
                    case 15:
                        BigDecimal trainRefundExtFee = billDetail.getRefundExtFee() == null ? BigDecimal.ZERO : billDetail.getRefundExtFee();
                        BigDecimal trainInsurancePrice = billDetail.getInsurancePrice() == null ? BigDecimal.ZERO : billDetail.getInsurancePrice();
                        BigDecimal trainChangeExtFee = billDetail.getChangeExtFee() == null ? BigDecimal.ZERO : billDetail.getChangeExtFee();
                        trainRefundInsuranceChangeSum = trainRefundInsuranceChangeSum.add(trainRefundExtFee.add(trainInsurancePrice).add(trainChangeExtFee));
                        trainServiceFee = trainServiceFee.add(billDetail.getServiceFee() != null ? billDetail.getServiceFee() : BigDecimal.ZERO);
                        BigDecimal trainSalePrice = billDetail.getSalePrice() == null ? BigDecimal.ZERO : "17".equals(billDetail.getTicketStstus()) ? BigDecimal.ZERO : billDetail.getSalePrice();
                        BigDecimal trainTaxes = billDetail.getTaxes() == null ? BigDecimal.ZERO : billDetail.getTaxes();
                        BigDecimal trainUpgrateFee = billDetail.getUpgrateFee() == null ? BigDecimal.ZERO : billDetail.getUpgrateFee();
                        BigDecimal trainChangeFee = billDetail.getChangeFee() == null ? BigDecimal.ZERO : billDetail.getChangeFee();
                        trainTravelPrice = trainTravelPrice.add(trainSalePrice).add(trainTaxes).add(trainUpgrateFee).add(trainChangeFee);
                        break;
                    case 11:
                        hotelTotalPrice = hotelTotalPrice.add(billDetail.getTotalAmount() != null ? billDetail.getTotalAmount() : BigDecimal.ZERO);
                        break;
                    case 3:
                        carTotalPrice = carTotalPrice.add(billDetail.getTotalAmount() != null ? billDetail.getTotalAmount() : BigDecimal.ZERO);
                        break;
                    case 130:
                        shansongTotalPrice = shansongTotalPrice.add(billDetail.getTotalAmount() != null ? billDetail.getTotalAmount() : BigDecimal.ZERO);
                        break;
                    case 60:
                    case 50:
                        dinnerTotalPrice = dinnerTotalPrice.add(billDetail.getTotalAmount() != null ? billDetail.getTotalAmount() : BigDecimal.ZERO);
                        break;
                    case 911:
                        altmanTotalPrice = altmanTotalPrice.add(billDetail.getTotalAmount() != null ? billDetail.getTotalAmount() : BigDecimal.ZERO);
                        break;
                    case 20:
                        mallTotalPrice = mallTotalPrice.add(billDetail.getTotalAmount() == null ? BigDecimal.ZERO : billDetail.getTotalAmount());
                }
                boolean dinner = billDetail.getOrderCategory() == 60 || billDetail.getOrderCategory() == 50;
                String customerManager = dinner ? billDetail.getCustomeField3() == null ? null : billDetail.getCustomeField3().trim().split("-")[1] : billDetail.getEmployeeName();
                if (!ObjectUtils.isEmpty(customerManager) && !customerManagers.contains(customerManager)) {
                    customerManagers.add(customerManager);
                }
            }
            String customerManagerStr = String.join(",", customerManagers);

            OpenBillSumZywx billSumZywjx = OpenBillSumZywx.builder()
                    .airRefundInsuranceChangeSum(airRefundInsuanceChangeSum)
                    .airServiceFee(airServiceFee)
                    .airTicketPrice(airTravelPrice)
                    .billNo(billNo)
                    .carTotalPrice(carTotalPrice)
                    .companyId(companyId)
                    .customerManager(customerManagerStr)
                    .dinnerTotalPrice(dinnerTotalPrice)
                    .hotelTotalPrice(hotelTotalPrice)
                    .itemName(itemName)
                    .itemNo(projectCode)
                    .shansongTotalPrice(shansongTotalPrice)
                    .trainRefundInsuranceChangeSum(trainRefundInsuranceChangeSum)
                    .trainServiceFee(trainServiceFee)
                    .trainTicketPrice(trainTravelPrice)
                    .altmanTotalPrice(altmanTotalPrice)
                    .mallTotalPrice(mallTotalPrice)
                    .build();
            BigDecimal airTotalPrice = billSumZywjx.getAirTicketPrice().add(billSumZywjx.getAirRefundInsuranceChangeSum()).add(billSumZywjx.getAirServiceFee());
            BigDecimal trainTotalPrice = billSumZywjx.getTrainTicketPrice().add(billSumZywjx.getTrainServiceFee()).add(billSumZywjx.getTrainRefundInsuranceChangeSum());
            BigDecimal sumTotalPrice = airTotalPrice.add(trainTotalPrice).add(carTotalPrice).add(hotelTotalPrice).add(shansongTotalPrice).add(dinnerTotalPrice).add(altmanTotalPrice).add(mallTotalPrice);
            billSumZywjx.setSumTotalPrice(sumTotalPrice);
            billSumZywjxDao.saveSelective(billSumZywjx);
        }
    }

    private List<String> getSortedProjectCode(Set<String> projectCodeList) {
        List<String> sortedProjectCodeList = Lists.newArrayList();
        List<String> shProjectCodeList = projectCodeList.stream().filter(code -> code.endsWith("SH")).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(shProjectCodeList)) {
            sortedProjectCodeList.addAll(shProjectCodeList);
        }
        List<String> aProjectCodeList = projectCodeList.stream().filter(code -> code.endsWith("A")).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(aProjectCodeList)) {
            sortedProjectCodeList.addAll(aProjectCodeList);
        }
        List<String> otherProjectCodeList = projectCodeList.stream().filter(code -> !code.endsWith("SH") && !code.endsWith("A")).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(otherProjectCodeList)) {
            sortedProjectCodeList.addAll(otherProjectCodeList);
        }
        return sortedProjectCodeList;
    }

}
