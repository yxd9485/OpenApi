package com.fenbeitong.openapi.plugin.func.apply.controller;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.apply.dto.FuncCustformApplyDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.func.sign.dto.ApiRequestNoEmployee;
import com.fenbeitong.openapi.plugin.support.apply.service.impl.OpenCustformApplyServiceImpl;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * <p>Title: FuncCustformApplyController<p>
 * <p>Description: 自定义审批单接口<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author: liuhong
 * @date: 2022/2/12 4:57 PM
 */
@RestController
@RequestMapping("/func/apply/custom")
public class FuncCustformApplyController {

    @Autowired
    private OpenCustformApplyServiceImpl openCustformApplyService;

    @FuncAuthAnnotation
    @RequestMapping("/detail")
    @ApiOperation(value = "查询自定义审批单详情", notes = "查询自定义审批单详情", httpMethod = "POST", response = FuncResultEntity.class)
    public Object getDetailByApplyId(HttpServletRequest httpRequest, @Valid ApiRequestNoEmployee apiRequest) throws Exception{
        FuncCustformApplyDetailReqDTO req = JsonUtils.toObj(apiRequest.getData(), FuncCustformApplyDetailReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        String companyId = StringUtils.obj2str(httpRequest.getAttribute("companyId"));
        Object result = openCustformApplyService.getCustformApplyDetail(req.getApplyId(), companyId);
        return FuncResponseUtils.success(result);
    }

}
