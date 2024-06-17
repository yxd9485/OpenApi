package com.fenbeitong.openapi.plugin.func.payment.service.impl;

import com.fenbeitong.bank.api.model.PaymentDetailDTO;
import com.fenbeitong.bank.api.model.PaymentOrderDetailDTO;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.func.common.FuncIdTypeEnums;
import com.fenbeitong.openapi.plugin.func.payment.dto.FuncPaymentApplyCreateDTO;
import com.fenbeitong.openapi.plugin.func.payment.dto.FuncSupplierListPageDTO;
import com.fenbeitong.openapi.plugin.func.payment.dto.FuncSupplierResDTO;
import com.fenbeitong.openapi.plugin.func.payment.service.FuncPaymentService;
import com.fenbeitong.openapi.plugin.support.apply.dto.PaymentCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.PaymentCreateResDataDTO;
import com.fenbeitong.openapi.plugin.support.common.constant.CostTypeEnum;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenBasePageDTO;
import com.fenbeitong.openapi.plugin.support.common.service.OpenCostAttrTranService;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.payment.common.PaymentChannelEnum;
import com.fenbeitong.openapi.plugin.support.payment.service.IPaymentService;
import com.fenbeitong.openapi.plugin.util.BigDecimalUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.payment.CompanyPaymentSupplierPageResultVO;
import com.fenbeitong.usercenter.api.model.dto.payment.CompanyPaymentSupplierVO;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

/**
 * 对公付款业务
 *
 * @author ctl
 * @date 2022/3/8
 */
@Service
@ServiceAspect
@Validated
public class FuncPaymentServiceImpl implements FuncPaymentService {

    @Autowired
    private IPaymentService iPaymentService;

    @Autowired
    private OpenEmployeeServiceImpl openEmployeeService;

    @Autowired
    private OpenCostAttrTranService openCostAttrTranService;

    @Override
    public PaymentCreateResDataDTO createPaymentApply(String companyId, String data) {
        checkParam(companyId, data);
        // data转换对公付款实体
        FuncPaymentApplyCreateDTO funcPaymentDTO = JsonUtils.toObj(data, FuncPaymentApplyCreateDTO.class);
        if (funcPaymentDTO == null) {
            throw new FinhubException(-9999, "[data]参数格式异常");
        }
        // token获取
        String thirdEmployeeId = funcPaymentDTO.getThirdEmployeeId();
        String token = openEmployeeService.getEmployeeFbToken(companyId, thirdEmployeeId, String.valueOf(FuncIdTypeEnums.THIRD_ID.getKey()));
        // 创建接收参数的实体
        PaymentCreateReqDTO paymentCreateDTO = buildPaymentCreateReqDTO(companyId, funcPaymentDTO);
        return new PaymentCreateResDataDTO(iPaymentService.createPaymentApply(token, companyId, thirdEmployeeId, paymentCreateDTO));
    }

    @Override
    public FuncSupplierListPageDTO listSuppliersByPage(String companyId, String data) {
        checkParam(companyId, data);
        OpenBasePageDTO openBasePageDTO = JsonUtils.toObj(data, OpenBasePageDTO.class);
        if (openBasePageDTO == null) {
            throw new FinhubException(-9999, "[data]参数格式异常");
        }
        return buildFuncSupplierListPageDTO(iPaymentService.listSuppliersByPage(companyId, openBasePageDTO), openBasePageDTO);
    }

    /**
     * 构建对外返回的供应商分页查询实体
     *
     * @param listSuppliersByPage
     * @return
     */
    private FuncSupplierListPageDTO buildFuncSupplierListPageDTO(CompanyPaymentSupplierPageResultVO listSuppliersByPage, OpenBasePageDTO openBasePageDTO) {
        FuncSupplierListPageDTO funcSupplierListPageDTO = new FuncSupplierListPageDTO();
        if (listSuppliersByPage != null) {
            funcSupplierListPageDTO.setSuppliers(buildSuppliers(listSuppliersByPage.getList()));
            funcSupplierListPageDTO.setTotalCount(listSuppliersByPage.getCount());
            // 总页数 = (count + pageSize -1) / pageSize
            int totalPages = (listSuppliersByPage.getCount() + openBasePageDTO.getPageSize() - 1) / openBasePageDTO.getPageSize();
            funcSupplierListPageDTO.setTotalPages(totalPages);
            funcSupplierListPageDTO.setPageIndex(openBasePageDTO.getPageIndex());
            funcSupplierListPageDTO.setPageSize(openBasePageDTO.getPageSize());
        }
        return funcSupplierListPageDTO;
    }

    /**
     * 构建对外结构的供应商列表
     *
     * @param sourceList
     * @return
     */
    private List<FuncSupplierResDTO> buildSuppliers(List<CompanyPaymentSupplierVO> sourceList) {
        List<FuncSupplierResDTO> targetList = Lists.newArrayList();
        if (!ObjectUtils.isEmpty(sourceList)) {
            for (CompanyPaymentSupplierVO companyPaymentSupplierVO : sourceList) {
                FuncSupplierResDTO funcSupplierResDTO = new FuncSupplierResDTO();
                funcSupplierResDTO.setId(companyPaymentSupplierVO.getId());
                funcSupplierResDTO.setThirdId(companyPaymentSupplierVO.getThirdId());
                funcSupplierResDTO.setCode(companyPaymentSupplierVO.getCompanySupplierCode());
                funcSupplierResDTO.setName(companyPaymentSupplierVO.getCompanySupplierName());
                funcSupplierResDTO.setBankAccount(companyPaymentSupplierVO.getBankAccount());
                funcSupplierResDTO.setBankAccountName(companyPaymentSupplierVO.getBankAccountName());
                funcSupplierResDTO.setBankName(companyPaymentSupplierVO.getBankName());
                funcSupplierResDTO.setBankId(companyPaymentSupplierVO.getBankId());
                funcSupplierResDTO.setSubbranchName(companyPaymentSupplierVO.getSubbranch());
                funcSupplierResDTO.setSubbranchId(companyPaymentSupplierVO.getUnionPayAccount());
                targetList.add(funcSupplierResDTO);
            }
        }
        return targetList;
    }

    /**
     * 构建对公付款实体
     *
     * @param companyId
     * @param funcPaymentDTO
     * @return
     */
    private PaymentCreateReqDTO buildPaymentCreateReqDTO(String companyId, @Valid FuncPaymentApplyCreateDTO funcPaymentDTO) {
        PaymentCreateReqDTO paymentCreateReqDTO = new PaymentCreateReqDTO();
        paymentCreateReqDTO.setApply(buildApplyDTO(companyId, funcPaymentDTO));
        return paymentCreateReqDTO;
    }

    /**
     * 构建申请详情dto
     *
     * @param companyId
     * @param funcPaymentDTO
     * @return
     */
    private PaymentCreateReqDTO.ApplyDTO buildApplyDTO(String companyId, FuncPaymentApplyCreateDTO funcPaymentDTO) {
        PaymentCreateReqDTO.ApplyDTO applyDTO = new PaymentCreateReqDTO.ApplyDTO();
        // 元转分
        applyDTO.setBudget(BigDecimalUtils.yuan2fen(funcPaymentDTO.getEstimatedTotalAmount()));
        applyDTO.setThirdId(funcPaymentDTO.getThirdApplyId());
        applyDTO.setThirdRemark(funcPaymentDTO.getThirdRemark());
        applyDTO.setPaymentName(funcPaymentDTO.getPaymentName());
        applyDTO.setReceiverId(funcPaymentDTO.getSupplierId());
        applyDTO.setCompanyAccountId(funcPaymentDTO.getPaymentAccountId());
        applyDTO.setPayerTime(funcPaymentDTO.getPaymentTime());
        applyDTO.setPaymentUse(funcPaymentDTO.getPaymentUse());
        applyDTO.setCreateChannel(PaymentChannelEnum.OPENAPI.getType());
        applyDTO.setApplyReason(funcPaymentDTO.getApplyReason());
        applyDTO.setApplyReasonDesc(funcPaymentDTO.getApplyRemark());
        applyDTO.setCostInfoType(CostTypeEnum.NO.getType());
        applyDTO.setCostInfo(buildCostInfoDTO(companyId, funcPaymentDTO));
        applyDTO.setContractId(funcPaymentDTO.getContractId());
        applyDTO.setPlanItemId(funcPaymentDTO.getPaymentPlanId());
        applyDTO.setInvoiceOption(funcPaymentDTO.getInvoiceType());
        applyDTO.setInvoiceList(funcPaymentDTO.getInvoiceIds());
        return applyDTO;
    }

    /**
     * 构建费用归属
     *
     * @param funcPaymentDTO
     * @return
     */
    private PaymentCreateReqDTO.ApplyDTO.CostInfoDTO buildCostInfoDTO(String companyId, FuncPaymentApplyCreateDTO funcPaymentDTO) {
        PaymentCreateReqDTO.ApplyDTO.CostInfoDTO costInfoDTO = new PaymentCreateReqDTO.ApplyDTO.CostInfoDTO();
        costInfoDTO.setCostCategory(buildCostCategoryDTO(funcPaymentDTO));
        costInfoDTO.setCostAttributionGroupList(openCostAttrTranService.openCostListToFbCostList(companyId, funcPaymentDTO.getCostAttributions()));
        return costInfoDTO;
    }

    /**
     * 构建费用类别dto
     *
     * @param funcPaymentDTO
     * @return
     */
    private PaymentCreateReqDTO.ApplyDTO.CostInfoDTO.CostCategoryDTO buildCostCategoryDTO(FuncPaymentApplyCreateDTO funcPaymentDTO) {
        PaymentCreateReqDTO.ApplyDTO.CostInfoDTO.CostCategoryDTO costCategoryDTO = new PaymentCreateReqDTO.ApplyDTO.CostInfoDTO.CostCategoryDTO();
        FuncPaymentApplyCreateDTO.CostCategory costCategory = funcPaymentDTO.getCostCategory();
        if (costCategory != null) {
            costCategoryDTO.setId(costCategory.getCode());
            costCategoryDTO.setName(costCategory.getName());
        }
        return costCategoryDTO;
    }

    @Override
    public PaymentDetailDTO getPaymentResultByPaymentId(String paymentId, String companyId) {
        return iPaymentService.getPaymentResultByPaymentId(paymentId, companyId);
    }

    @Override
    public PaymentDetailDTO getPaymentResultByApplyId(String applyId) {
        return iPaymentService.getPaymentResultByApplyId(applyId);
    }

    @Override
    public List<PaymentOrderDetailDTO> getElectronicListByPaymentId(String paymentId) {
        return iPaymentService.getElectronicListByPaymentId(paymentId);
    }

    /**
     * 参数检查
     *
     * @param companyId
     * @param data
     */
    private void checkParam(String companyId, String data) {
        if (StringUtils.isBlank(data)) {
            throw new FinhubException(-9999, "[data]不能为空");
        }
        if (StringUtils.isBlank(companyId)) {
            throw new FinhubException(-9999, "[companyId]不能为空");
        }
    }
}
