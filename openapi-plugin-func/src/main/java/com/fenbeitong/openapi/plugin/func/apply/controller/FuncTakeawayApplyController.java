package com.fenbeitong.openapi.plugin.func.apply.controller;

import com.fenbeitong.openapi.plugin.func.apply.service.FuncTakeawayApplyServiceImpl;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.func.common.FuncValidService;
import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.TakeawayApproveApply;
import com.fenbeitong.openapi.plugin.support.apply.dto.TakeawayApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenApiResponseDTO;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

@RestController
@RequestMapping("/func/apply/takeaway")
public class FuncTakeawayApplyController {

    @Autowired
    FuncTakeawayApplyServiceImpl funcTakeawayApplyService;
    @Autowired
    CommonAuthService commonAuthService;

    @Autowired
    private FuncValidService validService;

    @RequestMapping("/create")
    @ApiOperation(value = "创建审批单", notes = "创建外卖审批单", httpMethod = "POST", response = FuncResultEntity.class)
    @ResponseBody
    public Object createTakeawayApply(@Valid ApiRequest apiRequest) throws BindException {
        Map<String, String> stringStringMap = commonAuthService.signCheck(apiRequest);
        @NotBlank(message = "数据[data]不可为空") String data = apiRequest.getData();
        TakeawayApproveCreateReqDTO takeawayApproveCreateReqDTO = JsonUtils.toObj(data, TakeawayApproveCreateReqDTO.class);
        @Valid @NotNull(message = "审批单申请内容[apply]不可为空") TakeawayApproveApply apply = takeawayApproveCreateReqDTO.getApply();
        if (StringUtils.isNotBlank(apply.getApplyReason())) {
            validService.lengthValid(apply.getApplyReason().trim(),"apply_reason");
        }
        OpenApiResponseDTO<CreateApplyRespDTO> fbTakeawayApprove = funcTakeawayApplyService.createFbTakeawayApprove(stringStringMap.get("company_id"), stringStringMap.get("employee_id"), stringStringMap.get("employee_type"), takeawayApproveCreateReqDTO);
        //因底层抛异常无法跑出场景返回msg,无法在底层定义业务异常，故提至上层处理
        if (!ObjectUtils.isEmpty(fbTakeawayApprove)) {
                Integer code = fbTakeawayApprove.getCode();
                if (code == 0) {
                    CreateApplyRespDTO data1 = fbTakeawayApprove.getData();
                    return FuncResponseUtils.success(data1);
                } else {
                   return FuncResponseUtils.error(fbTakeawayApprove.getCode(), fbTakeawayApprove.getMsg());
                }
            }
        return FuncResponseUtils.success(fbTakeawayApprove);
    }
}
