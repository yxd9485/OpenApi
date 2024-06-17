package com.fenbeitong.openapi.plugin.func.bank.controller;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.bank.dto.BankPaymentBatchQueryDTO;
import com.fenbeitong.openapi.plugin.func.bank.dto.BankPaymentQueryDTO;
import com.fenbeitong.openapi.plugin.func.bank.service.OpenBankPaymentService;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 对公付款查询
 *
 * @author ctl
 * @date 2021/11/10
 */
@RestController
@RequestMapping("/func/bank/public/pay")
public class OpenBankPaymentController {

    @Autowired
    private OpenBankPaymentService openBankPaymentService;

    /**
     * 根据付款单id查询详情
     *
     * @param apiRequestBase
     * @return
     */
    @PostMapping("/getDetailById")
    @FuncAuthAnnotation
    public Object bankPaymentDetailById(HttpServletRequest httpRequest, @Valid ApiRequestBase apiRequestBase) {
        BankPaymentQueryDTO req = JsonUtils.toObj(apiRequestBase.getData(), BankPaymentQueryDTO.class);
        if (req == null) {
            throw new OpenApiArgumentException("[data] 参数格式有误");
        }
        if (StringUtils.isBlank(req.getPaymentId())) {
            throw new OpenApiArgumentException("[payment_id] 不能为空");
        }
        return FuncResponseUtils.success(openBankPaymentService.bankPaymentDetailById((String) httpRequest.getAttribute("companyId"), req.getPaymentId()));
    }

    /**
     * 根据付款单id集合查询详情集合
     *
     * @param apiRequestBase
     * @return
     */
    @PostMapping("/getDetailListByIds")
    @FuncAuthAnnotation
    public Object bankPaymentDetailListByIds(HttpServletRequest httpRequest, @Valid ApiRequestBase apiRequestBase) {
        BankPaymentBatchQueryDTO req = JsonUtils.toObj(apiRequestBase.getData(), BankPaymentBatchQueryDTO.class);
        if (req == null) {
            throw new OpenApiArgumentException("[data] 参数格式有误");
        }
        if (ObjectUtils.isEmpty(req.getPaymentIds())) {
            throw new OpenApiArgumentException("[payment_ids] 不能为空");
        }
        return FuncResponseUtils.success(openBankPaymentService.bankPaymentDetailListByIds((String) httpRequest.getAttribute("companyId"), req.getPaymentIds()));
    }

}
