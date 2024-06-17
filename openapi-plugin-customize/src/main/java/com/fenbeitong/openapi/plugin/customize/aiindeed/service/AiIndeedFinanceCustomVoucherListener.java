package com.fenbeitong.openapi.plugin.customize.aiindeed.service;

import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.voucher.constant.VoucherItemType;
import com.fenbeitong.openapi.plugin.voucher.dao.OpenVoucherDraftDao;
import com.fenbeitong.openapi.plugin.voucher.dto.FinanceCustomVoucherCreateReqDto;
import com.fenbeitong.openapi.plugin.voucher.entity.FinanceBusinessData;
import com.fenbeitong.openapi.plugin.voucher.entity.OpenVoucherDraft;
import com.fenbeitong.openapi.plugin.voucher.service.impl.DefaultFinanceCustomVoucherListener;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>Title: AiIndeedFinanceCustomVoucherListener</p>
 * <p>Description: 实在智能账单合并</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/9/27 3:02 PM
 */
@Component
public class AiIndeedFinanceCustomVoucherListener extends DefaultFinanceCustomVoucherListener {

    @Autowired
    private OpenVoucherDraftDao openVoucherDraftDao;

    @Override
    public void createVoucherItem(FinanceBusinessData businessData, Map<String, Object> srcData, OpenVoucherDraft openVoucherDraft) {
        String businessExtJson = businessData.getBusinessExtJson();
        if (!ObjectUtils.isEmpty(businessExtJson)) {
            Map<String, Object> businessExt = JsonUtils.toObj(businessExtJson, Map.class);
            openVoucherDraft.setAttr11((String) businessExt.get("tripApplyId"));
            openVoucherDraft.setAttr12((String) businessExt.get("duringApplyId"));
        }
        openVoucherDraft.setAttr13(businessData.getBusinessDate());
    }

    @Override
    public void afterVoucherCreated(FinanceCustomVoucherCreateReqDto reqDto) {
        Example example = new Example(OpenVoucherDraft.class);
        example.createCriteria().andEqualTo("batchId", reqDto.getBillNo());
        example.selectProperties("attr11", "employeeCode");
        example.setDistinct(true);
        List<OpenVoucherDraft> voucherDraftList = openVoucherDraftDao.listByExample(example);
        Set<String> applyIdList = voucherDraftList.stream().filter(v -> !ObjectUtils.isEmpty(v.getAttr11())).map(OpenVoucherDraft::getAttr11).collect(Collectors.toSet());
        AtomicInteger voucherNum = new AtomicInteger(1000);
        createVoucherByApplyId(reqDto.getBillNo(), applyIdList, voucherNum);
        Set<String> employeeCodeList = voucherDraftList.stream().filter(v -> ObjectUtils.isEmpty(v.getAttr11())).map(OpenVoucherDraft::getEmployeeCode).collect(Collectors.toSet());
        createVoucherByEmployeeCode(reqDto.getBillNo(), employeeCodeList, voucherNum);
    }

    private void createVoucherByEmployeeCode(String billNo, Set<String> employeeCodeList, AtomicInteger voucherNum) {
        List<List<String>> batchEmployeeCodeList = CollectionUtils.batch(Lists.newArrayList(employeeCodeList), 200);
        for (List<String> thisBatchEmployeeCodes : batchEmployeeCodeList) {
            Example example = new Example(OpenVoucherDraft.class);
            example.createCriteria().andEqualTo("batchId", billNo).andIn("employeeCode", thisBatchEmployeeCodes).andEqualTo("status", -1);
            List<OpenVoucherDraft> voucherDraftList = openVoucherDraftDao.listByExample(example);
            voucherDraftList = voucherDraftList.stream().filter(v -> ObjectUtils.isEmpty(v.getAttr11())).collect(Collectors.toList());
            //借方
            Map<String, List<OpenVoucherDraft>> debitMapList = voucherDraftList.stream().filter(v -> v.getVoucherType() != VoucherItemType.CREDIT.getType()).collect(Collectors.groupingBy(OpenVoucherDraft::getEmployeeCode));
            //贷方
            Map<String, List<OpenVoucherDraft>> creditMapList = voucherDraftList.stream().filter(v -> v.getVoucherType() == VoucherItemType.CREDIT.getType()).collect(Collectors.groupingBy(OpenVoucherDraft::getEmployeeCode));
            createVoucher(thisBatchEmployeeCodes, debitMapList, creditMapList, voucherNum);
        }
    }

    private void createVoucherByApplyId(String billNo, Set<String> applyIdList, AtomicInteger voucherNum) {
        List<List<String>> batchApplyIdList = CollectionUtils.batch(Lists.newArrayList(applyIdList), 200);
        for (List<String> thisBatchApplyIds : batchApplyIdList) {
            Example example = new Example(OpenVoucherDraft.class);
            example.createCriteria().andEqualTo("batchId", billNo).andIn("attr11", thisBatchApplyIds).andEqualTo("status", -1);
            List<OpenVoucherDraft> voucherDraftList = openVoucherDraftDao.listByExample(example);
            //借方
            Map<String, List<OpenVoucherDraft>> debitMapList = voucherDraftList.stream().filter(v -> v.getVoucherType() != VoucherItemType.CREDIT.getType()).collect(Collectors.groupingBy(OpenVoucherDraft::getAttr11));
            //贷方
            Map<String, List<OpenVoucherDraft>> creditMapList = voucherDraftList.stream().filter(v -> v.getVoucherType() == VoucherItemType.CREDIT.getType()).collect(Collectors.groupingBy(OpenVoucherDraft::getAttr11));
            createVoucher(thisBatchApplyIds, debitMapList, creditMapList, voucherNum);
        }
    }

    private void createVoucher(List<String> voucherKeyList, Map<String, List<OpenVoucherDraft>> debitMapList, Map<String, List<OpenVoucherDraft>> creditMapList, AtomicInteger voucherNum) {
        for (String voucherKey : voucherKeyList) {
            int voucherIndex = voucherNum.getAndIncrement();
            List<OpenVoucherDraft> debitList = debitMapList.get(voucherKey);
            debitList.forEach(d -> {
                d.setId(null);
                d.setAttr6(StringUtils.obj2str(voucherIndex));
                d.setStatus(1);
                openVoucherDraftDao.saveSelective(d);
            });
            List<OpenVoucherDraft> creditList = creditMapList.get(voucherKey);
            BigDecimal sumCredit = creditList.stream().map(OpenVoucherDraft::getCredit).reduce(BigDecimal.ZERO, BigDecimal::add);
            OpenVoucherDraft openVoucherDraft = creditList.get(0);
            openVoucherDraft.setId(null);
            openVoucherDraft.setAttr6(StringUtils.obj2str(voucherIndex));
            openVoucherDraft.setStatus(1);
            openVoucherDraft.setCredit(sumCredit);
            openVoucherDraftDao.saveSelective(openVoucherDraft);
        }
    }
}
