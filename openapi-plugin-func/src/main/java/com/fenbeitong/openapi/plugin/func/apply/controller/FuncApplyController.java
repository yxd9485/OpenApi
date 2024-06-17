package com.fenbeitong.openapi.plugin.func.apply.controller;
/**
 * <p>Title: FuncApplyController</p>
 * <p>Description:审批公用接口 </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author lizhen
 * @date 2021/7/27 7:24 下午
 */

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.apply.dto.ApplyRepulseDTO;
import com.fenbeitong.openapi.plugin.func.apply.dto.ApplyRevokeDTO;
import com.fenbeitong.openapi.plugin.func.apply.dto.FuncCompanyApplyDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.apply.dto.FuncCompanyApplyListReqDTO;
import com.fenbeitong.openapi.plugin.func.apply.service.FuncApplyServiceImpl;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.func.sign.dto.ApiRequestNoEmployee;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Created by lizhen on 2021/7/27.
 */
@RestController
@RequestMapping("/func/apply/common")
public class FuncApplyController {

    @Autowired
    private FuncApplyServiceImpl funcApplyService;

    @FuncAuthAnnotation
    @RequestMapping("/list")
    @ApiOperation(value = "查询审批单列表", notes = "查询审批单列表", httpMethod = "POST", response = FuncResultEntity.class)
    public Object getListByApplyId(HttpServletRequest httpRequest, @Valid ApiRequestNoEmployee apiRequest) throws Exception {
        FuncCompanyApplyListReqDTO req = JsonUtils.toObj(apiRequest.getData(), FuncCompanyApplyListReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        String companyId = StringUtils.obj2str(httpRequest.getAttribute("companyId"));
        Object result = funcApplyService.getCompanyApproveList(companyId, req);
        return FuncResponseUtils.success(result);
    }

    @FuncAuthAnnotation
    @RequestMapping("/detail")
    @ApiOperation(value = "查询审批单详情", notes = "查询审批单详情", httpMethod = "POST", response = FuncResultEntity.class)
    public Object getDetailByApplyId(HttpServletRequest httpRequest, @Valid ApiRequestNoEmployee apiRequest) throws Exception {
        FuncCompanyApplyDetailReqDTO req = JsonUtils.toObj(apiRequest.getData(), FuncCompanyApplyDetailReqDTO.class);
        String companyId = StringUtils.obj2str(httpRequest.getAttribute("companyId"));
        Object result = funcApplyService.getCompanyApproveDetail(companyId, req);
        return FuncResponseUtils.success(result);
    }
    @FuncAuthAnnotation
    @RequestMapping("/repulse")
    @ApiOperation(value = "新审批终审驳回", notes = "新审批终审驳回", httpMethod = "POST", response = FuncResultEntity.class)
    public Object  commonApplyRepulse(HttpServletRequest httpRequest, @Valid ApiRequestBase apiRequest){
        ApplyRepulseDTO applyRepulseDTO = JsonUtils.toObj(apiRequest.getData(), ApplyRepulseDTO.class);
         funcApplyService.applyRepulse(apiRequest);
        return FuncResponseUtils.success(Maps.newHashMap());
    }
    @FuncAuthAnnotation
    @RequestMapping("/revoke")
    @ApiOperation(value = "新审批终审撤回", notes = "新审批终审撤回", httpMethod = "POST", response = FuncResultEntity.class)
    public Object  commonApplyRevoke(HttpServletRequest httpRequest, @Valid ApiRequestBase apiRequest) {
        ApplyRevokeDTO applyRevokeDTO = JsonUtils.toObj(apiRequest.getData(), ApplyRevokeDTO.class);
        funcApplyService.applyRevoke(apiRequest);
        return FuncResponseUtils.success(Maps.newHashMap());
    }

    @FuncAuthAnnotation
    @RequestMapping("/cancel")
    @ApiOperation(value = "审批作废", notes = "第三方行程、非行程、自定义 审批单,(机票、火车、酒店)", httpMethod = "POST", response = FuncResultEntity.class)
    public Object commonApplyCancel(HttpServletRequest httpRequest, @Valid ApiRequest apiRequest) {
        if (funcApplyService.applyCancle(apiRequest)) {
            return FuncResponseUtils.success(Maps.newHashMap());
        } else {
            return FuncResponseUtils.error(-1, "操作失败");
        }
    }

    @FuncAuthAnnotation
    @RequestMapping("/agree")
    @ApiOperation(value = "通用审批通过", notes = "通用审批通过", httpMethod = "POST", response = FuncResultEntity.class)
    public Object commonApplyAgree(HttpServletRequest httpRequest,@Valid ApiRequestBase apiRequest) throws BindException {
        String companyId =StringUtils.obj2str(httpRequest.getAttribute("companyId"));
        funcApplyService.applyAgree(companyId,apiRequest);
        return FuncResponseUtils.success(Maps.newHashMap());
    }
}
