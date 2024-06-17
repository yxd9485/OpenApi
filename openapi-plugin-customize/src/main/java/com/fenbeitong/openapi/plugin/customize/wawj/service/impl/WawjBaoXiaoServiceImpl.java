package com.fenbeitong.openapi.plugin.customize.wawj.service.impl;

import com.fenbeitong.openapi.plugin.customize.wawj.dao.OpenWawjBillDetailDao;
import com.fenbeitong.openapi.plugin.customize.wawj.dao.OpenWawjBillSummaryDao;
import com.fenbeitong.openapi.plugin.customize.wawj.dto.WawjBaoXiaoReq;
import com.fenbeitong.openapi.plugin.customize.wawj.dto.WawjBillDetailDto;
import com.fenbeitong.openapi.plugin.customize.wawj.dto.WawjBillSummaryDto;
import com.fenbeitong.openapi.plugin.customize.wawj.entity.OpenWawjBillDetail;
import com.fenbeitong.openapi.plugin.customize.wawj.entity.OpenWawjBillSummary;
import com.fenbeitong.openapi.plugin.customize.wawj.service.IWawjBaoXiaoService;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title: WawjBaoXiaoServiceImpl</p>
 * <p>Description: 我爱我家报销单服务实现</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/11/19 10:37 AM
 */
@Slf4j
@ServiceAspect
@Service
public class WawjBaoXiaoServiceImpl implements IWawjBaoXiaoService {

    @Autowired
    private OpenWawjBillSummaryDao wawjBillSummaryDao;

    @Autowired
    private OpenWawjBillDetailDao wawjBillDetailDao;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Async
    @Override
    public void push(String companyId, List<String> batchIdList) throws Exception {
        List<OpenMsgSetup> openMsgSetups = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList("wiwj_customize_baoxiao_info"));
        Map baoxiaoInfo = JsonUtils.toObj(openMsgSetups.get(0).getStrVal1(), Map.class);
        String summaryUrl = (String) baoxiaoInfo.get("summary_url");
        String detailUrl = (String) baoxiaoInfo.get("detail_url");
        List<OpenWawjBillDetail> billDetailList = wawjBillDetailDao.listByBatchId(batchIdList);
        Map<String, List<OpenWawjBillDetail>> billDetailMap = billDetailList.stream().collect(Collectors.groupingBy(OpenWawjBillDetail::getBatchId));
        List<OpenWawjBillSummary> billSummaryList = wawjBillSummaryDao.listByBatchId(batchIdList);
        Map<String, List<OpenWawjBillSummary>> billSummaryMap = billSummaryList.stream().collect(Collectors.groupingBy(OpenWawjBillSummary::getBatchId));
        batchIdList.forEach(batchId -> batchPush(batchId, billSummaryMap.get(batchId), billDetailMap.get(batchId), summaryUrl, detailUrl));
    }

    private void batchPush(String batchId, List<OpenWawjBillSummary> billSummaryList, List<OpenWawjBillDetail> billDetailList, String summaryUrl, String detailUrl) {
        if (ObjectUtils.isEmpty(billSummaryList) || ObjectUtils.isEmpty(billDetailList)) {
            return;
        }
        //1、推送明细数据
        try {
            List<WawjBillDetailDto> billDetialDtos = billDetailList.stream().map(detail -> {
                WawjBillDetailDto detailDto = new WawjBillDetailDto();
                BeanUtils.copyProperties(detail, detailDto);
                return detailDto;
            }).collect(Collectors.toList());
            WawjBaoXiaoReq<WawjBillDetailDto> detailReq = new WawjBaoXiaoReq<>();
            detailReq.setResult(billDetialDtos);
            String summaryJson = JsonUtils.toJson(detailReq);
            log.info("推送我爱我家报销单明细数据，批次-{},数据-{}", batchId, summaryJson);
            String result = RestHttpUtils.postJson(detailUrl, summaryJson);
            log.info("推送我爱我家报销单明细数据，批次-{},数据-{},响应信息-{}", batchId, summaryJson, result);
        } catch (Exception e) {
            log.error("推送我爱我家报销单明细数据,异常,批次-" + batchId, e);
        }
        //2、推送汇总数据
        try {
            List<WawjBillSummaryDto> billSummaryDtos = billSummaryList.stream().map(summary -> {
                WawjBillSummaryDto summaryDto = new WawjBillSummaryDto();
                BeanUtils.copyProperties(summary, summaryDto);
                return summaryDto;
            }).collect(Collectors.toList());
            WawjBaoXiaoReq<WawjBillSummaryDto> summaryReq = new WawjBaoXiaoReq<>();
            summaryReq.setResult(billSummaryDtos);
            String summaryJson = JsonUtils.toJson(summaryReq);
            log.info("推送我爱我家报销单汇总数据，批次-{},数据-{}", batchId, summaryJson);
            String result = RestHttpUtils.postJson(summaryUrl, summaryJson);
            log.info("推送我爱我家报销单汇总数据，批次-{},数据-{},响应信息-{}", batchId, summaryJson, result);
        } catch (Exception e) {
            log.error("推送我爱我家报销单汇总数据,异常,批次-" + batchId, e);
        }
    }

}
