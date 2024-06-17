package com.fenbeitong.openapi.plugin.func.organization.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.func.organization.dto.OpenCreateLegalEntityReqDTO;
import com.fenbeitong.openapi.plugin.func.organization.dto.OpenQueryLegalEntityReqDTO;
import com.fenbeitong.openapi.plugin.func.organization.dto.OpenUpdateLegalEntityReqDTO;
import com.fenbeitong.openapi.plugin.func.organization.service.FuncLegalEntityService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * @ClassName FuncLegalEntityController
 * @Description 法人主体相关接口
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/4/13 下午7:15
 **/
@RestController
@RequestMapping("/func/legal/entity")
public class FuncLegalEntityController {

    @Autowired
    private FuncLegalEntityService legalEntityService;

    @FuncAuthAnnotation
    @RequestMapping("/create")
    @ApiOperation(value = "批量新增法人主体", notes = "批量新增法人主体", httpMethod = "POST", response = FuncResultEntity.class)
    public Object createLegalEntity(HttpServletRequest httpRequest, @Valid ApiRequestBase apiRequestBase) throws Exception {
        List<OpenCreateLegalEntityReqDTO> legalEntityList = JsonUtils.toObj(apiRequestBase.getData(), new TypeReference<List<OpenCreateLegalEntityReqDTO>>() {
        });
        String companyId = ((String) httpRequest.getAttribute("companyId"));
        return legalEntityService.batchCreateLegalEntity(companyId,legalEntityList);
    }

    @FuncAuthAnnotation
    @RequestMapping("/update")
    @ApiOperation(value = "更新法人主体", notes = "更新法人主体", httpMethod = "POST", response = FuncResultEntity.class)
    public Object updateLegalEntity(HttpServletRequest httpRequest, @Valid ApiRequestBase apiRequestBase) throws BindException {
        List<OpenUpdateLegalEntityReqDTO> legalEntityList = JsonUtils.toObj(apiRequestBase.getData(), new TypeReference<List<OpenUpdateLegalEntityReqDTO>>() {
        });
        String companyId = ((String) httpRequest.getAttribute("companyId"));
        return legalEntityService.batchUpdateLegalEntity(companyId,legalEntityList);
    }

    @FuncAuthAnnotation
    @RequestMapping("/delete")
    @ApiOperation(value = "删除法人主体", notes = "删除法人主体", httpMethod = "POST", response = FuncResultEntity.class)
    public Object deleteLegalEntity(HttpServletRequest httpRequest, @Valid ApiRequestBase apiRequestBase){
        List<String> legalEntityIds = JsonUtils.toObj(apiRequestBase.getData(), new TypeReference<List<String>>() {
        });
        String companyId = ((String) httpRequest.getAttribute("companyId"));
        return legalEntityService.deleteLegalEntity(companyId,legalEntityIds);
    }

    @FuncAuthAnnotation
    @RequestMapping("/detail")
    @ApiOperation(value = "批量查询法人详情信息", notes = "批量查询法人详情信息", httpMethod = "POST", response = FuncResultEntity.class)
    public Object batchQueryLegalEntity(HttpServletRequest httpRequest, @Valid ApiRequestBase apiRequestBase){
        OpenQueryLegalEntityReqDTO pageInfo = JsonUtils.toObj(apiRequestBase.getData(), new TypeReference<OpenQueryLegalEntityReqDTO>() {
        });
        String companyId = ((String) httpRequest.getAttribute("companyId"));
        return legalEntityService.listLegalEntities(companyId,pageInfo);
    }
}
