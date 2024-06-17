package com.fenbeitong.openapi.plugin.func.apply.controller;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.apply.service.FuncTripApplyServiceImpl;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.func.employee.service.FuncEmployeeService;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApplyAgreeReqDTO;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;

/**
 * <p>Title: FuncTripApplyController</p>
 * <p>Description: 行程审批</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/7 10:29 AM
 */
@RestController
@RequestMapping("/func/apply/trip")
@Api(value = "行程审批", tags = "行程审批", description = "第三方行程审批")
public class FuncTripApplyController {

    @Autowired
    private FuncTripApplyServiceImpl tripApplyService;

    @Autowired
    private FuncEmployeeService employeeService;

    @RequestMapping("/create")
    @ApiOperation(value = "创建审批单", notes = "创建行程审批单", httpMethod = "POST", response = FuncResultEntity.class)
    public Object createTripApply(@Valid ApiRequest apiRequest) throws Exception {
        Object result = tripApplyService.createTripApply(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/change")
    @ApiOperation(value = "变更审批单", notes = "变更行程审批单", httpMethod = "POST", response = FuncResultEntity.class)
    public Object changeTripApply(@Valid ApiRequest apiRequest) throws Exception {
        Object result = tripApplyService.changeTripApply(apiRequest);
        return FuncResponseUtils.success(result);
    }


    /**
     * 审批单查询详情
     *
     * @param apiRequest
     * @return
     * @throws Exception
     */
    @RequestMapping("/detail")
    @ApiOperation(value = "查询审批单详情", notes = "查询审批单详情", httpMethod = "POST", response = FuncResultEntity.class)
    public Object getDetailByApplyId(@Valid ApiRequest apiRequest) throws Exception {
        Object result = tripApplyService.getDetailByApplyId(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/notifyApplyAgree")
    @FuncAuthAnnotation
    public Object notifyApplyAgree(HttpServletRequest httpRequest, @Valid ApiRequest apiRequest) throws Exception {
        TripApplyAgreeReqDTO req = JsonUtils.toObj(apiRequest.getData(), TripApplyAgreeReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        req.setApplyId((String) httpRequest.getAttribute("apply_id"));
        req.setThirdId(httpRequest.getParameter("third_id"));
        String companyId = (String) httpRequest.getAttribute("companyId");
        String employee_id = httpRequest.getParameter("employee_id");
        String employee_type = httpRequest.getParameter("employee_type");
        String token = employeeService.getEmployeeFbToken(companyId, employee_id, employee_type);
        tripApplyService.notifyTripApplyAgree(apiRequest);
        return FuncResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 非行程管控差旅审批创建
     *
     * @return
     */
    @RequestMapping("/multi/create")
    @FuncAuthAnnotation
    public Object createMultiTripApply(@Valid ApiRequestBase apiRequest) throws Exception {
        String applyId = tripApplyService.createMultiTripApply(apiRequest);
        return FuncResponseUtils.success(new HashMap<String, String>() {{
            put("apply_id", applyId);
        }});
    }

    /**
     * 非行程管控差旅审批同意
     * <p>
     * "access_token":"5747fbc10f0e60e0709d8d722",
     * "timestamp":124124325,
     * "sign":"oihfnlyeofdh98",
     * "employee_id":"59b74c1323445f2d54dd07922",
     * "data":{
     * "apply_id":"5def123823445f39e2e90f59",
     * "third_id":"57613c455eac323d0c17"
     * }
     */
    @RequestMapping("/multi/notifyApplyAgree")
    public Object notifyMultiApplyAgree(@Valid ApiRequest apiRequest) throws Exception {
        tripApplyService.notifyMultiTripApplyAgree(apiRequest);
        return FuncResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 非行程模式差旅审批变更
     *
     * @return
     */
    @RequestMapping("/multi/update")
    @FuncAuthAnnotation
    public Object updateMultiTripApply(@Valid ApiRequestBase apiRequest) throws Exception {
        String applyId = tripApplyService.updateMultiTripApply(apiRequest);
        return FuncResponseUtils.success(new HashMap<String, String>() {{
            put("apply_id", applyId);
        }});
    }
}
