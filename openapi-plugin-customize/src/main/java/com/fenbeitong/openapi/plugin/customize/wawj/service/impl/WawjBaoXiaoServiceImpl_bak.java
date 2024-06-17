//package com.fenbeitong.openapi.plugin.customize.wawj.service.impl;
//
//import com.fenbeitong.openapi.plugin.customize.wawj.dao.OpenWawjBillSummaryDao;
//import com.fenbeitong.openapi.plugin.customize.wawj.entity.OpenWawjBillSummary;
//import com.fenbeitong.openapi.plugin.customize.wawj.service.IWawjBaoXiaoService;
//import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
//import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
//import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
//import com.fenbeitong.openapi.plugin.util.DateUtils;
//import com.fenbeitong.openapi.plugin.util.JsonUtils;
//import com.fenbeitong.openapi.plugin.util.StringUtils;
//import com.fenbeitong.openapi.plugin.util.xml.XmlUtil;
//import com.google.common.collect.Lists;
//import com.wiwj.*;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
//import org.springframework.util.ObjectUtils;
//
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
///**
// * <p>Title: WawjBaoXiaoServiceImpl</p>
// * <p>Description: 我爱我家报销单服务实现</p>
// * <p>Company: www.fenbeitong.com</p>
// *
// * @author hwangsy
// * @date 2020/11/19 10:37 AM
// */
//@Slf4j
//@ServiceAspect
//@Service
//public class WawjBaoXiaoServiceImpl_bak implements IWawjBaoXiaoService {
//
//    @Autowired
//    private ExceptionRemind exceptionRemind;
//
//    @Autowired
//    private OpenWawjBillSummaryDao wawjBillSummaryDao;
//
//    @Autowired
//    private OpenMsgSetupDao openMsgSetupDao;
//
//    @Async
//    @Override
//    public void push(String companyId, List<String> batchIdList) throws Exception {
//        List<OpenMsgSetup> openMsgSetups = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList("wiwj_customize_baoxiao_info"));
//        Map baoxiaoInfo = JsonUtils.toObj(openMsgSetups.get(0).getStrVal1(), Map.class);
//        String bxwsdl = (String) baoxiaoInfo.get("bxwsdl");
//        List<OpenWawjBillSummary> billSummaryList = wawjBillSummaryDao.listByBatchId(batchIdList);
//        Map<String, List<OpenWawjBillSummary>> billSummaryMap = billSummaryList.stream().collect(Collectors.groupingBy(OpenWawjBillSummary::getBatchId));
//        AutoName_serviceLocator locator = new AutoName_serviceLocator(WjwjEngineConfigurationFactory.newFactory().getClientEngineConfig(), bxwsdl);
//        AutoName_portType client = locator.getautoName_port();
//        String nowDate = DateUtils.toSimpleStr(DateUtils.now(), true);
//        batchIdList.forEach(batchId -> batchPush(client, batchId, nowDate, billSummaryMap.get(batchId)));
//    }
//
//    private void batchPush(AutoName_portType client, String batchId, String nowDate, List<OpenWawjBillSummary> billSummaryList) {
//        if (ObjectUtils.isEmpty(billSummaryList)) {
//            return;
//        }
//        DATA wiwjData = new DATA();
//        wiwjData.setBATCHID(batchId);
//        wiwjData.setBAOXIAODATAS(buildDetailDatas(nowDate, billSummaryList));
//        SOAPRESPONSE soapresponse = null;
//        try {
//            log.info("我爱我家推送报销单数据，批次-{},数据-{}", batchId, XmlUtil.object2Xml(wiwjData));
//            soapresponse = client.execute(wiwjData);
//            log.info("我爱我家推送报销单数据，批次-{},数据-{},响应信息-{}", batchId, XmlUtil.object2Xml(wiwjData), XmlUtil.object2Xml(soapresponse));
//        } catch (Exception e) {
//            log.warn("我爱我家推送报销单数据,异常,批次-" + batchId, e);
//        }
//        if (soapresponse == null) {
//            soapresponse = buildResp(batchId, wiwjData);
//        }
//        List<SOAPRESPONSEDETAILSDETAIL> respDetailList = Lists.newArrayList(soapresponse.getDETAILS());
//        boolean success = !respDetailList.stream().filter("E"::equals).collect(Collectors.toList()).isEmpty();
//        Map<String, SOAPRESPONSEDETAILSDETAIL> respDetailMap = respDetailList.stream().collect(Collectors.toMap(SOAPRESPONSEDETAILSDETAIL::getBATCH_LINE_ID, Function.identity()));
//        billSummaryList.forEach(bill -> {
//            OpenWawjBillSummary billSummary = new OpenWawjBillSummary();
//            billSummary.setId(bill.getId());
//            billSummary.setStatus(success ? 0 : 1);
//            SOAPRESPONSEDETAILSDETAIL respDetail = respDetailMap.get(StringUtils.obj2str(bill.getBatchLineId()));
//            if (success) {
//                billSummary.setRespMsg(respDetail.getERROR_MESSAGES());
//            } else {
//                billSummary.setRespMsg("本批次推送失败-" + respDetail.getERROR_MESSAGES());
//            }
//            wawjBillSummaryDao.updateById(billSummary);
//        });
//        if (!success) {
//            exceptionRemind.remindDingTalk("我爱我家报销单推送失败，批次号-[" + batchId + "]，请注意查看。");
//        }
//    }
//
//    private SOAPRESPONSE buildResp(String batchId, DATA wiwjData) {
//        SOAPRESPONSE resp = new SOAPRESPONSE();
//        resp.setBATCHID(batchId);
//        List<DATABAOXIAODATASBAOXIAODATA> reqDetailList = Lists.newArrayList(wiwjData.getBAOXIAODATAS());
//        List<SOAPRESPONSEDETAILSDETAIL> respDetailList = reqDetailList.stream().map(req -> {
//            SOAPRESPONSEDETAILSDETAIL respDetail = new SOAPRESPONSEDETAILSDETAIL();
//            respDetail.setBATCH_ID(batchId);
//            respDetail.setBATCH_LINE_ID(req.getBATCH_LINE_ID());
//            respDetail.setIMPORT_STATUS("E");
//            respDetail.setERROR_MESSAGES("推送失败");
//            return respDetail;
//        }).collect(Collectors.toList());
//        resp.setDETAILS(respDetailList.toArray(new SOAPRESPONSEDETAILSDETAIL[]{}));
//        return resp;
//    }
//
//    private DATABAOXIAODATASBAOXIAODATA[] buildDetailDatas(String nowDate, List<OpenWawjBillSummary> billSummaryList) {
//        List<DATABAOXIAODATASBAOXIAODATA> detailList = billSummaryList.stream().map(bill -> {
//            DATABAOXIAODATASBAOXIAODATA detailData = new DATABAOXIAODATASBAOXIAODATA();
//            detailData.setBATCH_LINE_ID(StringUtils.obj2str(bill.getBatchLineId()));
//            detailData.setCOMPANY_CODE(bill.getCompanyCode());
//            detailData.setREPORT_DATE(nowDate);
//            detailData.setEMPLOYEE_CODE(bill.getEmployeeCode());
//            detailData.setINCORPORATED_COMPANY(bill.getIncorporatedCompany());
//            detailData.setACCOUNT_COMPANY_CODE(bill.getAccountCompanyCode());
//            detailData.setDESCRIPTION("");
//            detailData.setLINE_DESCRIPTION(bill.getLineDescription());
//            detailData.setEXPENSE_TYPE_CODE(bill.getExpenseTypeCode());
//            detailData.setEXPENSE_ITEM_CODE(bill.getExpenseItemCode());
//            detailData.setREPORT_AMOUNT(StringUtils.obj2str(bill.getReportAmount()));
//            detailData.setUNIT_CODE(bill.getUnitCode());
//            detailData.setDIMENSION2_CODE(bill.getDimension2Code());
//            detailData.setDIMENSION3_CODE("0");
//            detailData.setDIMENSION4_CODE(bill.getDimension4Code());
//            detailData.setPAYEE_CATEGORY(bill.getPayeeCategory());
//            detailData.setPAYEE_CODE(bill.getPayeeCode());
//            detailData.setTAX_TYPE_CODE("");
//            detailData.setTAX_RATE("");
//            detailData.setATTRIBUTE1(bill.getAttribute1());
//            detailData.setATTRIBUTE2(bill.getAttribute2());
//            detailData.setATTRIBUTE3(bill.getAttribute3());
//            detailData.setATTRIBUTE4(bill.getAttribute4());
//            detailData.setATTRIBUTE5(bill.getAttribute5());
//            detailData.setATTRIBUTE6(bill.getAttribute6());
//            detailData.setATTRIBUTE7(bill.getAttribute7());
//            detailData.setATTRIBUTE8(bill.getAttribute8());
//            detailData.setATTRIBUTE9(bill.getAttribute9());
//            detailData.setATTRIBUTE10(bill.getAttribute10());
//            detailData.setATTRIBUTE11(bill.getAttribute11());
//            detailData.setATTRIBUTE12(bill.getAttribute12());
//            detailData.setATTRIBUTE13(bill.getAttribute13());
//            detailData.setATTRIBUTE14(bill.getAttribute14());
//            detailData.setATTRIBUTE15(bill.getAttribute15());
//            detailData.setATTRIBUTE16(bill.getAttribute16());
//            detailData.setATTRIBUTE17(bill.getAttribute17());
//            detailData.setATTRIBUTE18(bill.getAttribute18());
//            detailData.setATTRIBUTE19(bill.getAttribute19());
//            detailData.setATTRIBUTE20(bill.getAttribute20());
//            return detailData;
//        }).collect(Collectors.toList());
//        return detailList.toArray(new DATABAOXIAODATASBAOXIAODATA[]{});
//    }
//
//}
