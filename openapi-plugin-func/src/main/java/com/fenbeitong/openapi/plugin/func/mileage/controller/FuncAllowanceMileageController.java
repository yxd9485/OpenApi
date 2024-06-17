package com.fenbeitong.openapi.plugin.func.mileage.controller;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.func.mileage.service.FuncAllowanceMileageService;
import com.fenbeitong.openapi.plugin.func.sign.dto.ApiRequestNoEmployee;
import com.fenbeitong.openapi.plugin.support.mileage.service.dto.AllowanceMileageReqDTO;
import com.fenbeitong.openapi.plugin.support.mileage.service.dto.AllowanceMileageRespDTO;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 里程补贴
 *
 * @author lizhen
 */
@RestController
@RequestMapping("/func/mileage")
public class FuncAllowanceMileageController {

    @Autowired
    private FuncAllowanceMileageService allowanceMileageService;

    @FuncAuthAnnotation
    @RequestMapping("/list")
    @ApiOperation(value = "查询里程补贴列表", notes = "查询里程补贴列表", httpMethod = "POST", response = FuncResultEntity.class)
    public Object getListByApplyId(HttpServletRequest httpRequest, @Valid ApiRequestNoEmployee apiRequest)
        throws Exception {
        AllowanceMileageReqDTO req = JsonUtils.toObj(apiRequest.getData(), AllowanceMileageReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        String companyId = StringUtils.obj2str(httpRequest.getAttribute("companyId"));
        AllowanceMileageRespDTO allowanceMileageByPage = allowanceMileageService.getAllowanceMileageByPage(companyId
            , req);
        return FuncResponseUtils.success(allowanceMileageByPage);
    }

}
