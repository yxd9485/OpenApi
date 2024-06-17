package com.fenbeitong.openapi.plugin.customize.chenguangrongxin.service.impl;

import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.dao.CustomizeJiandaoyunCorpDao;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.dto.ReimbursementDetailDTO;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.dto.ReimbursementPushDTO;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.entity.CustomizeJiandaoyunCorp;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.service.IReimbursementService;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.service.common.ChenguangCommonServiceImpl;
import com.fenbeitong.openapi.plugin.func.reimburse.dto.*;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 辰光融信报销单推送服务
 *
 * @author machao
 * @date 2022/9/16
 */
@ServiceAspect
@Slf4j
@Service("cgrxReimbursementServiceImpl")
public class ReimbursementServiceImpl implements IReimbursementService {

    @Value("${jiandaoyun.reimbursement-uri}")
    private String uri;

    @Autowired
    private ChenguangCommonServiceImpl chenguangCommonService;

    @Autowired
    private CustomizeJiandaoyunCorpDao customizeJiandaoyunCorpDao;

    @Override
    public void pushData(ReimbursementDetailDTO data) {
        if (data == null) {
            log.warn("推送数据为空");
            return;
        }
        // 推送信息
        push(buildPushData(data), data.getCompanyId());
    }

    private ReimbursementPushDTO buildPushData(ReimbursementDetailDTO data) {
        ReimbursementPushDTO pushData = new ReimbursementPushDTO();
        // 报销单号
        pushData.setReimbursementNumber(new ReimbursementPushDTO.Entry<>(data.getReimbId()));
        // 报销单自定义字段
        List<KVEntity> formCustomFields = data.getFormCustomFields();
        if (CollectionUtils.isNotEmpty(formCustomFields)) {
            for (KVEntity k : formCustomFields) {
                if (k != null) {
                    if (StringUtils.equals(k.getKey(), "提交人-法人公司")) {
                        pushData.setCorporateCompany(new ReimbursementPushDTO.Entry<>(k.getValue() == null ? "" : k.getValue().toString()));
                    }
                    if (StringUtils.equals(k.getKey(), "项目")) {
                        pushData.setProjectNo(new ReimbursementPushDTO.Entry<>(k.getValue() == null ? "" : k.getValue().toString()));
                    }
                    if (StringUtils.equals(k.getKey(), "项目类型")) {
                        pushData.setProjectType(new ReimbursementPushDTO.Entry<>(k.getValue() == null ? "" : k.getValue().toString()));
                    }
                    if (StringUtils.equals(k.getKey(), "报销申请日期")) {
                        pushData.setReimbursementApplicationDate(new ReimbursementPushDTO.Entry<>(k.getValue() == null ? "" : k.getValue().toString()));
                    }
                    if (StringUtils.equals(k.getKey(), "报销起始日期")) {
                        pushData.setReimbursementStartTime(new ReimbursementPushDTO.Entry<>(k.getValue() == null ? "" : k.getValue().toString()));
                    }
                    if (StringUtils.equals(k.getKey(), "报销结束日期")) {
                        pushData.setReimbursementEndTime(new ReimbursementPushDTO.Entry<>(k.getValue() == null ? "" : k.getValue().toString()));
                    }
                    if (StringUtils.equals(k.getKey(), "客商名称")) {
                        pushData.setMerchantName(new ReimbursementPushDTO.Entry<>(k.getValue() == null ? "" : k.getValue().toString()));
                    }
                    if (StringUtils.equals(k.getKey(), "备注")) {
                        pushData.setReasonForReimbursement(new ReimbursementPushDTO.Entry<>(k.getValue() == null ? "" : k.getValue().toString()));
                    }
                }
            }
        }
        // 员工信息
        List<EmployeeInfoDTO> employeeInfo = data.getEmployeeInfo();
        if (CollectionUtils.isNotEmpty(employeeInfo)) {
            EmployeeInfoDTO info = employeeInfo.get(0);
            if (info != null) {
                // 部门Id
                pushData.setDepId(new ReimbursementPushDTO.Entry<>(info.getThirdDepartmentId()));
                // 部门 数字类型
                pushData.setDepartment(new ReimbursementPushDTO.Entry<>(
                    NumberUtils.isDigits(info.getThirdDepartmentId()) ? NumberUtils.toLong(info.getThirdDepartmentId()) : null));
                // 提交人
                pushData.setSubmitter(new ReimbursementPushDTO.Entry<>(info.getThirdEmployeeId()));
            }
        }
        // 费用明细
        pushData.setChargedDetails(new ReimbursementPushDTO.Entry<>(chargesDetail(data)));
        return pushData;
    }

    private List<ReimbursementPushDTO.Charge> chargesDetail(ReimbursementDetailDTO data) {
        List<ReimbursementPushDTO.Charge> charges = new ArrayList<>();
        List<RemiCostResDTO> reimbExpense = data.getReimbExpense();
        if (CollectionUtils.isNotEmpty(reimbExpense)) {
            for (RemiCostResDTO r : reimbExpense) {
                ReimbursementPushDTO.Charge charge = new ReimbursementPushDTO.Charge();
                // 费用名称
                charge.setChargeDetailsCost(new ReimbursementPushDTO.Entry<>(r.getCostCategoryName()));
                // 费用类型自定义字段
                List<KVEntity> costCustomFields = r.getCostCustomFields();
                if (CollectionUtils.isNotEmpty(costCustomFields)) {
                    for (KVEntity k : costCustomFields) {
                        if (StringUtils.equals(k.getKey(), "支付方式")) {
                            charge.setChargeDetailsPaymentMethod(new ReimbursementPushDTO.Entry<>(k.getValue() == null ? "" : k.getValue().toString()));
                        }
                        if (StringUtils.equals(k.getKey(), "费用描述")) {
                            charge.setChargeDetailsFeeDescription(new ReimbursementPushDTO.Entry<>(k.getValue() == null ? "" : k.getValue().toString()));
                        }
                        // 工作类型 基础控件：单选框 目前获取不到
                        // 费用类型 基础控件：单选框 目前获取不到
                    }
                }
                // 费用归属
                List<CostAttributionGroupDTO> costAttributionGroup = r.getCostAttributionGroup();
                if (CollectionUtils.isNotEmpty(costAttributionGroup)) {
                    CostAttributionGroupDTO group = costAttributionGroup.get(0);
                    List<CostAttributionDTO> cost = group.getCostAttributionList();
                    if (CollectionUtils.isNotEmpty(cost)) {
                        CostAttributionDTO c = cost.get(0);
                        if (c != null) {
                            // 费用归属名称
                            charge.setChargeDetailsAttributionOfExpenses(new ReimbursementPushDTO.Entry<>(c.getName()));
                            // 费用归属三方id
                            charge.setCostAttributionId(new ReimbursementPushDTO.Entry<>(c.getThirdId()));
                            // 交易金额
                            charge.setTotalTransaction(new ReimbursementPushDTO.Entry<>(c.getAmount()));
                        }
                    }
                    // 费用归属类型
                    charge.setChargeDetailsExpenseAttributionType(new ReimbursementPushDTO.Entry<>(group.getCategoryName()));
                }
                charges.add(charge);
            }
        }
        return charges;
    }

    private void push(ReimbursementPushDTO pushData, String companyId) {
        // 简道云token配置
        CustomizeJiandaoyunCorp jiandaoyunCorp = customizeJiandaoyunCorpDao.getByCompanyId(companyId);
        if (jiandaoyunCorp != null) {
            // 推送简道云
            chenguangCommonService.push(uri, pushData.getReimbursementNumber().getValue(), pushData, jiandaoyunCorp);
        }
    }
}
