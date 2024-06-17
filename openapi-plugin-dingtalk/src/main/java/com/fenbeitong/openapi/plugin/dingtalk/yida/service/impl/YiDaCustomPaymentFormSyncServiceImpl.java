package com.fenbeitong.openapi.plugin.dingtalk.yida.service.impl;

import com.fenbeitong.bank.api.model.ThirdPaymentSupplierReqRpcDTO;
import com.fenbeitong.bank.api.model.ThirdPaymentSupplierRespRpcDTO;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.dingtalk.yida.dto.PayerMsgDTO;
import com.fenbeitong.openapi.plugin.dingtalk.yida.dto.YiDaCallbackPaymentDTO;
import com.fenbeitong.openapi.plugin.dingtalk.yida.dto.YiDaDeptSelectDTO;
import com.fenbeitong.openapi.plugin.dingtalk.yida.dto.YiDaReceiptDTO;
import com.fenbeitong.openapi.plugin.dingtalk.yida.resolver.YiDaFormDataResolver;
import com.fenbeitong.openapi.plugin.dingtalk.yida.service.IYiDaCustomPaymentFormSyncService;
import com.fenbeitong.openapi.plugin.support.apply.dto.PaymentCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.IOpenPaymentApplyService;
import com.fenbeitong.openapi.plugin.support.common.constant.CostAttributionTypeEnum;
import com.fenbeitong.openapi.plugin.support.common.constant.CostTypeEnum;
import com.fenbeitong.openapi.plugin.support.common.dto.FbCostAttributionDTO;
import com.fenbeitong.openapi.plugin.support.company.dto.CompanySuperAdmin;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.payment.common.PaymentChannelEnum;
import com.fenbeitong.openapi.plugin.support.supplier.service.IOpenSupplierService;
import com.fenbeitong.openapi.plugin.support.util.SuperAdminUtils;
import com.fenbeitong.openapi.plugin.util.*;
import com.fenbeitong.usercenter.api.model.dto.common.CommonIdDTO;
import com.fenbeitong.usercenter.api.model.dto.orgunit.OrgUnitResult;
import com.fenbeitong.usercenter.api.model.enums.common.IdBusinessTypeEnums;
import com.fenbeitong.usercenter.api.model.enums.common.IdTypeEnums;
import com.fenbeitong.usercenter.api.service.common.ICommonService;
import com.fenbeitong.usercenter.api.service.orgunit.IOrgUnitService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 宜搭对公付款定制化业务
 *
 * @author ctl
 * @date 2022/3/7
 */
@Service
@ServiceAspect
@Slf4j
public class YiDaCustomPaymentFormSyncServiceImpl implements IYiDaCustomPaymentFormSyncService {

    @Autowired
    private IOpenSupplierService iOpenSupplierService;

    @Autowired
    private IOpenPaymentApplyService iOpenPaymentService;

    @Autowired
    private OpenEmployeeServiceImpl openEmployeeService;

    @Autowired
    private SuperAdminUtils superAdminUtils;

    @DubboReference(check = false)
    private ICommonService commonService;

    @DubboReference(check = false)
    private IOrgUnitService orgUnitService;

    @Override
    public void execute(Map<String, Object> params, String companyId, OpenMsgSetup openMsgSetup) {
        if (ObjectUtils.isEmpty(params)) {
            throw new FinhubException(9999, "宜搭对公付款申请单参数为空");
        }
        YiDaCallbackPaymentDTO yiDaCallbackPaymentDTO = JsonUtils.toObj(JsonUtils.toJson(params), YiDaCallbackPaymentDTO.class);
        if (yiDaCallbackPaymentDTO == null) {
            throw new FinhubException(9999, "宜搭对公付款申请单参数转换失败");
        }
        if (StringUtils.isBlank(yiDaCallbackPaymentDTO.getPaymentTime())) {
            throw new FinhubException(9999, "付款时间不能为空");
        }
        // 创建对公付款申请
        createPaymentApply(companyId, yiDaCallbackPaymentDTO, openMsgSetup);
    }

    /**
     * 创建对公付款申请
     *
     * @param companyId
     * @param yiDaCallbackPaymentDTO
     * @param openMsgSetup
     */
    private void createPaymentApply(String companyId, YiDaCallbackPaymentDTO yiDaCallbackPaymentDTO, OpenMsgSetup openMsgSetup) {
        String thirdEmployeeId = yiDaCallbackPaymentDTO.getThirdEmployeeId();
        // 构建申请单请求实体
        PaymentCreateReqDTO paymentCreateReqDTO = buildPaymentCreateReqDTO(yiDaCallbackPaymentDTO, companyId, openMsgSetup);
        // 获取token
        String token = openEmployeeService.getEmployeeFbToken(companyId, thirdEmployeeId, "1");
        // 调用底层接口
        String id = iOpenPaymentService.createPaymentApply(token, companyId, thirdEmployeeId, paymentCreateReqDTO);
        log.info("公司:{},通过宜搭同步对公付款申请单创建成功,申请单id:{}", companyId, id);
    }

    /**
     * 构建对公付款请求体
     *
     * @param yiDaCallbackPaymentDTO
     * @return
     */
    private PaymentCreateReqDTO buildPaymentCreateReqDTO(YiDaCallbackPaymentDTO yiDaCallbackPaymentDTO, String companyId, OpenMsgSetup openMsgSetup) {
        String strVal1 = openMsgSetup.getStrVal1();
        if (StringUtils.isBlank(strVal1)) {
            throw new FinhubException(9999, "[strVal1]为空,未配置付款账户信息");
        }
        PayerMsgDTO payerMsgDTO = JsonUtils.toObj(strVal1, PayerMsgDTO.class);
        if (ObjectUtils.isEmpty(payerMsgDTO)) {
            throw new FinhubException(9999, "[payerMsgDTO]为空,付款账户信息格式配置不正确");
        }
        String companyAccountId = payerMsgDTO.getCompanyAccountId();
        String showBankAccountName = payerMsgDTO.getShowBankAccountName();
        if (StringUtils.isBlank(companyAccountId)) {
            throw new FinhubException(9999, "[companyAccountId]为空,付款主体id未配置");
        }
        if (StringUtils.isBlank(showBankAccountName)) {
            throw new FinhubException(9999, "[showBankAccountName]为空,付款主体开户行未配置");
        }

        PaymentCreateReqDTO paymentCreateReqDTO = new PaymentCreateReqDTO();
        PaymentCreateReqDTO.ApplyDTO applyDTO = new PaymentCreateReqDTO.ApplyDTO();
        PaymentCreateReqDTO.ApplyDTO.CostInfoDTO costInfoDTO = new PaymentCreateReqDTO.ApplyDTO.CostInfoDTO();
        List<FbCostAttributionDTO> fbCostAttribution = new ArrayList<>();
        FbCostAttributionDTO fbCostAttributionDTO = new FbCostAttributionDTO();
        List<FbCostAttributionDTO.CostAttributionListDTO> costAttributionListDTOList = new ArrayList<>();
        FbCostAttributionDTO.CostAttributionListDTO costAttributionListDTO = new FbCostAttributionDTO.CostAttributionListDTO();

        // 构建事由特殊结构
        String applyReason = buildApplyReason(yiDaCallbackPaymentDTO);
        // 调用供应商接口
        ThirdPaymentSupplierRespRpcDTO supplierRespRpcDTO = addSupplier(yiDaCallbackPaymentDTO, companyId);
        if (supplierRespRpcDTO == null) {
            throw new FinhubException(9999, "供应商创建失败");
        }
        applyDTO.setThirdId(yiDaCallbackPaymentDTO.getThirdId());
        // 固定1 表示钉钉
        applyDTO.setCreateChannel(PaymentChannelEnum.DINGTALK.getType());
        applyDTO.setApplyReason(applyReason);
        // 这里的单位是分
        applyDTO.setBudget(BigDecimalUtils.yuan2fen(yiDaCallbackPaymentDTO.getPaymentAmount()));
        applyDTO.setPaymentName(StringUtils.isBlank(yiDaCallbackPaymentDTO.getPaymentName()) ? "付款申请" : yiDaCallbackPaymentDTO.getPaymentName());
        applyDTO.setCompanyAccountId(companyAccountId);
        applyDTO.setShowBankAccountName(showBankAccountName);
        applyDTO.setPayerTime(DateUtils.toSimpleStr(Long.parseLong(yiDaCallbackPaymentDTO.getPaymentTime()), true));
        applyDTO.setPaymentUse("付款申请");
        applyDTO.setReceiverId(supplierRespRpcDTO.getId());
        // 费用归属分摊类型 0 不分摊
        applyDTO.setCostInfoType(CostTypeEnum.NO.getType());
        // 解析部门三方id 转分贝通id
        String fbDeptId = getFbId(yiDaCallbackPaymentDTO, companyId);
        // 通过id查name
        String deptName = getDeptDetailById(companyId, fbDeptId);
        // 先随便给个值 无意义 但申请单的接口必须要这个字段...
        applyDTO.setCostAttributionCategory(CostAttributionTypeEnum.DEPT.getType());
        applyDTO.setCostAttributionId(fbDeptId);
        applyDTO.setCostAttributionName(deptName);
        // 设置费用归属
        costAttributionListDTO.setId(fbDeptId);
        costAttributionListDTO.setName(deptName);
        costAttributionListDTO.setWeight(BigDecimal.valueOf(100));
        // 费用归属里的单位是元
        costAttributionListDTO.setPrice(yiDaCallbackPaymentDTO.getPaymentAmount());
        costAttributionListDTOList.add(costAttributionListDTO);
        // 1部门 2项目 3自定义档案
        fbCostAttributionDTO.setCategory(CostAttributionTypeEnum.DEPT.getType());
        fbCostAttributionDTO.setCostAttributionList(costAttributionListDTOList);
        fbCostAttribution.add(fbCostAttributionDTO);
        costInfoDTO.setCostAttributionGroupList(fbCostAttribution);
        applyDTO.setCostInfo(costInfoDTO);
        paymentCreateReqDTO.setApply(applyDTO);
        return paymentCreateReqDTO;
    }

    /**
     * 获取部门详情
     *
     * @param companyId
     * @param fbDeptId
     * @return
     */
    private String getDeptDetailById(String companyId, String fbDeptId) {
        OrgUnitResult orgUnitResult = orgUnitService.queryOrgUnitAndParentUnit(companyId, fbDeptId);
        return orgUnitResult != null ? orgUnitResult.getName() : "";
    }

    /**
     * 换取分贝部门id
     *
     * @param yiDaCallbackPaymentDTO
     * @param companyId
     * @return
     */
    private String getFbId(YiDaCallbackPaymentDTO yiDaCallbackPaymentDTO, String companyId) {
        String fbId = null;
        List<String> thirdDeptIdList = YiDaFormDataResolver.resolveStringList(yiDaCallbackPaymentDTO.getThirdDeptId());
        if (ObjectUtils.isEmpty(thirdDeptIdList)) {
            // 如果表单不存在 取授权负责人的部门id
            CompanySuperAdmin superAdmin = superAdminUtils.companySuperAdmin(companyId);
            if (superAdmin != null) {
                fbId = superAdmin.getOrgUnitId();
            } else {
                throw new FinhubException(9999, "未获取到授权负责人");
            }
        } else {
            String thirdDeptId = thirdDeptIdList.get(0);
            List<CommonIdDTO> commonIdDTOList = commonService.queryIdDTO(companyId, Lists.newArrayList(thirdDeptId), IdTypeEnums.THIRD_ID.getKey(), IdBusinessTypeEnums.ORG.getKey());
            if (!ObjectUtils.isEmpty(commonIdDTOList)) {
                CommonIdDTO commonIdDTO = commonIdDTOList.get(0);
                if (thirdDeptId.equals(commonIdDTO.getId())) {
                    fbId = commonIdDTO.getThirdId();
                } else if (thirdDeptId.equals(commonIdDTO.getThirdId())) {
                    fbId = commonIdDTO.getId();
                }
            }
        }
        // 换取id可能失败 失败时使用授权负责人id
        if (StringUtils.isBlank(fbId)) {
            CompanySuperAdmin superAdmin = superAdminUtils.companySuperAdmin(companyId);
            if (superAdmin != null) {
                fbId = superAdmin.getOrgUnitId();
            } else {
                throw new FinhubException(9999, "未获取到授权负责人");
            }
        }
        return fbId;
    }

    /**
     * 添加供应商 根据公司id + 收款账户账号 + 账户名 唯一校验 存在直接返回 不存在则创建
     *
     * @param yiDaCallbackPaymentDTO
     * @param companyId
     * @return
     */
    private ThirdPaymentSupplierRespRpcDTO addSupplier(YiDaCallbackPaymentDTO yiDaCallbackPaymentDTO, String companyId) {
        ThirdPaymentSupplierReqRpcDTO req = buildThirdPaymentSupplierReqRpcDTO(yiDaCallbackPaymentDTO, companyId);
        return iOpenSupplierService.createSupplier(req);
    }

    /**
     * 构建创建供应商请求参数体
     *
     * @param yiDaCallbackPaymentDTO
     * @return
     */
    private ThirdPaymentSupplierReqRpcDTO buildThirdPaymentSupplierReqRpcDTO(YiDaCallbackPaymentDTO yiDaCallbackPaymentDTO, String companyId) {
        ThirdPaymentSupplierReqRpcDTO reqRpcDTO = new ThirdPaymentSupplierReqRpcDTO();
        // 5表示openapi
        reqRpcDTO.setOsType(5);
        reqRpcDTO.setCompanyId(companyId);
        reqRpcDTO.setCompanySupplierName(yiDaCallbackPaymentDTO.getBankAccountName());
        reqRpcDTO.setBankAccount(yiDaCallbackPaymentDTO.getBankAccountCode());
        reqRpcDTO.setBankName(yiDaCallbackPaymentDTO.getBankName());
        reqRpcDTO.setBankAccountName(yiDaCallbackPaymentDTO.getBankAccountName());
        reqRpcDTO.setSubbranch(yiDaCallbackPaymentDTO.getSubbranchName());
        return reqRpcDTO;
    }

    /**
     * 明细中的applyReason
     *
     * @param yiDaCallbackPaymentDTO
     * @return
     */
    private String buildApplyReason(YiDaCallbackPaymentDTO yiDaCallbackPaymentDTO) {
        String applyReason = "";
        String listAmountStr = yiDaCallbackPaymentDTO.getListAmountStr();
        String listClientStr = yiDaCallbackPaymentDTO.getListClientStr();
        String listContentStr = yiDaCallbackPaymentDTO.getListContentStr();
        String listCurrencyStr = yiDaCallbackPaymentDTO.getListCurrencyStr();
        String listDeptStr = yiDaCallbackPaymentDTO.getListDeptStr();
        String listProjectStr = yiDaCallbackPaymentDTO.getListProjectStr();
        String listRequisitionStr = yiDaCallbackPaymentDTO.getListRequisitionStr();
        String listWayStr = yiDaCallbackPaymentDTO.getListWayStr();

        List<String> amountList = YiDaFormDataResolver.resolveStringList(listAmountStr);
        List<String> clientList = YiDaFormDataResolver.resolveStringList(listClientStr);
        List<String> contentList = YiDaFormDataResolver.resolveStringList(listContentStr);
        List<String> currencyList = YiDaFormDataResolver.resolveStringList(listCurrencyStr);
        List<YiDaDeptSelectDTO> deptList = YiDaFormDataResolver.resolveDeptSelectList(listDeptStr);
        List<YiDaReceiptDTO> projectList = YiDaFormDataResolver.resolveReceiptList(listProjectStr);
        List<YiDaReceiptDTO> requisitionList = YiDaFormDataResolver.resolveReceiptList(listRequisitionStr);
        List<String> wayList = YiDaFormDataResolver.resolveStringList(listWayStr);

        // 找到长度最大的list 需要先判空
        Integer maxSize = CollectionUtils.getMaxSize(amountList, clientList, contentList, currencyList, deptList, projectList, requisitionList, wayList);
        if (ObjectUtils.isEmpty(maxSize) || maxSize <= 0) {
            return applyReason;
        } else {
            List<String> reasonList = new ArrayList<>();
            for (int i = 0; i < maxSize; i++) {
                // 构建成applyReason 序号，费用归属部门，客户，付款内容，付款金额，项目名称；
                List<String> strList = new ArrayList<>();
                strList.add(String.valueOf(i + 1));
                strList.add(ObjectUtils.isEmpty(deptList) ? " " : deptList.get(i).getText().getZhCn());
                strList.add(ObjectUtils.isEmpty(clientList) ? " " : clientList.get(i));
                strList.add(ObjectUtils.isEmpty(contentList) ? " " : contentList.get(i));
                strList.add(ObjectUtils.isEmpty(amountList) ? " " : amountList.get(i));
                strList.add(ObjectUtils.isEmpty(projectList) ? " " : projectList.get(i).getTitle());
                String str = StringUtils.joinStr(",", strList);
                reasonList.add(str);
            }
            applyReason = StringUtils.joinStr(";", reasonList);
        }
        return applyReason;
    }


}
