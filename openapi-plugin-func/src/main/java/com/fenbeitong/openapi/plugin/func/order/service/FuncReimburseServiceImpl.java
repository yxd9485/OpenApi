package com.fenbeitong.openapi.plugin.func.order.service;

import com.fenbeitong.fenbei.settlement.base.dto.BasePageDTO;
import com.fenbeitong.fenbei.settlement.external.api.api.reimburse.IReimburseOpenApi;
import com.fenbeitong.fenbei.settlement.external.api.dto.ReimburseApplyDataDTO;
import com.fenbeitong.fenbei.settlement.external.api.dto.ReimburseSummaryDataDTO;
import com.fenbeitong.fenbei.settlement.external.api.query.ReimburseSummaryDataQuery;
import com.fenbeitong.openapi.plugin.func.apply.dto.ApplyOrderDetailDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.EimburseReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CompanyApplyDetailReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.impl.OpenApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description 报销汇总单
 * @Author duhui
 * @Date 2021/9/6
 **/
@SuppressWarnings("all")
@Slf4j
@Service
public class FuncReimburseServiceImpl {
    @DubboReference(check = false)
    IReimburseOpenApi iReimburseOpenApi;
    @Autowired
    private OpenApplyServiceImpl openApplyService;
    @Autowired
    private UserCenterService userCenterService;

    public ReimburseApplyDataDTO findReimburseByApplyCode(EimburseReqDTO eimburseReqDTO) {
        return iReimburseOpenApi.findReimburseByApplyCode(eimburseReqDTO.getApplyCode(), eimburseReqDTO.getPageIndex(), eimburseReqDTO.getPageSize());
    }

    public BasePageDTO<ReimburseSummaryDataDTO> findReimburseSummaryData(ReimburseSummaryDataQuery reimburseSummaryDataQuery) {
        BasePageDTO<ReimburseSummaryDataDTO> reimburseSummaryDataDTOBasePageDTO = iReimburseOpenApi.findReimburseSummaryData(reimburseSummaryDataQuery);
        return reimburseSummaryDataDTOBasePageDTO;
    }
}