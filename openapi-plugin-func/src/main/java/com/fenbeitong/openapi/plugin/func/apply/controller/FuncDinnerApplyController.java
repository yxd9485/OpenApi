package com.fenbeitong.openapi.plugin.func.apply.controller;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.apply.service.FuncDinnerApplyServiceImpl;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.common.FuncValidService;
import com.fenbeitong.openapi.plugin.func.order.dto.MallApplyApproveReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.DinnerApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenApiResponseDTO;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Map;

@RestController
@RequestMapping("/func/apply/dinner")
public class FuncDinnerApplyController {
    @Autowired
    FuncDinnerApplyServiceImpl funcDinnerApplyService;
    @Autowired
    CommonAuthService commonAuthService;
    @Autowired
    private FuncValidService validService;

    /**
     * 创建用餐审批单
     *
     * @param apiRequest
     * @return
     */
    @RequestMapping("/create")
    @ResponseBody
    public Object createDinnerApply(@Valid ApiRequest apiRequest) throws BindException {
        Map<String, String> stringStringMap = commonAuthService.signCheck(apiRequest);
        @NotBlank(message = "数据[data]不可为空") String data = apiRequest.getData();
        DinnerApproveCreateReqDTO dinnerApproveCreateReqDTO = JsonUtils.toObj(data, DinnerApproveCreateReqDTO.class);
        if (!ObjectUtils.isEmpty(dinnerApproveCreateReqDTO.getApply())){
            if (StringUtils.isNotBlank(dinnerApproveCreateReqDTO.getApply().getApplyReason())) {
                validService.lengthValid(dinnerApproveCreateReqDTO.getApply().getApplyReason().trim(),"apply_reason");
            }
        }
        Object fbDinnerApprove = funcDinnerApplyService.createFbDinnerApprove(stringStringMap.get("company_id"), stringStringMap.get("employee_id"), stringStringMap.get("employee_type"), dinnerApproveCreateReqDTO);
        //因底层抛异常无法跑出场景返回msg,无法在底层定义业务异常，故提至上层处理
        if (!ObjectUtils.isEmpty(fbDinnerApprove)) {
            OpenApiResponseDTO<CreateApplyRespDTO> fbDinnerApproveResp = (OpenApiResponseDTO<CreateApplyRespDTO>) fbDinnerApprove;
            if (!ObjectUtils.isEmpty(fbDinnerApprove)) {
                Integer code = fbDinnerApproveResp.getCode();
                if (code == 0) {
                    CreateApplyRespDTO data1 = fbDinnerApproveResp.getData();
                    return FuncResponseUtils.success(data1);
                } else {
                    return FuncResponseUtils.error(fbDinnerApproveResp.getCode(), fbDinnerApproveResp.getMsg());
                }
            }
        }
        return FuncResponseUtils.success(fbDinnerApprove);
    }

    @RequestMapping("/notifyApplyAgree")
    @FuncAuthAnnotation
    public Object notifyApplyAgree(HttpServletRequest httpRequest, @Valid ApiRequest apiRequest) throws Exception {
        MallApplyApproveReqDTO req = JsonUtils.toObj(apiRequest.getData(), MallApplyApproveReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        req.setEmployeeId(httpRequest.getParameter("employee_id"));
        req.setEmployeeType(httpRequest.getParameter("employee_type"));
        funcDinnerApplyService.notifyApplyAgree(req);
        return FuncResponseUtils.success(Maps.newHashMap());
    }

}
