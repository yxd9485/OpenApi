package com.fenbeitong.openapi.plugin.func.archive.controller;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.archive.dto.ArchiveItemResDTO;
import com.fenbeitong.openapi.plugin.func.archive.dto.DeleteArchiveItemReqDTO;
import com.fenbeitong.openapi.plugin.func.archive.dto.QueryArchiveItemListReqDTO;
import com.fenbeitong.openapi.plugin.func.archive.dto.UpdateArchiveItemReqDTO;
import com.fenbeitong.openapi.plugin.func.archive.service.FuncArchiveService;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.func.reimburse.dto.RemiDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.sign.dto.ApiRequestNoEmployee;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @ClassName FuncArchiveController
 * @Description 自定义档案
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/10/31 下午2:13
 **/
@RestController
@RequestMapping("/func/archive")
public class FuncArchiveController {
    @Autowired
    private CommonAuthService signService;
    @Autowired
    private FuncArchiveService archiveService;

    //新增或更新档案项目
    @FuncAuthAnnotation
    @RequestMapping("/createOrUpdate")
    @ApiOperation(value = "新增或更新档案项目", notes = "新增或更新档案项目", httpMethod = "POST", response = FuncResultEntity.class)
    public Object createOrUpdateArchiveItem(@Valid ApiRequestNoEmployee apiRequest) throws IOException, BindException {
        signService.checkSign(apiRequest);
        String appId = signService.getAppId(apiRequest);
        UpdateArchiveItemReqDTO req = JsonUtils.toObj(apiRequest.getData(), UpdateArchiveItemReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        List<ArchiveItemResDTO> resultList = archiveService.createOrUpdateArchiveItem(req, appId);
        return FuncResponseUtils.success(resultList);
    }
    //删除档案项目
    @FuncAuthAnnotation
    @RequestMapping("/delete")
    @ApiOperation(value = "删除档案项目", notes = "删除档案项目", httpMethod = "POST", response = FuncResultEntity.class)
    public Object deleteArchiveItem(@Valid ApiRequestNoEmployee apiRequest) throws IOException, BindException {
        signService.checkSign(apiRequest);
        String appId = signService.getAppId(apiRequest);
        DeleteArchiveItemReqDTO req = JsonUtils.toObj(apiRequest.getData(), DeleteArchiveItemReqDTO.class);
        ValidatorUtils.validateBySpring(req);
         archiveService.deleteArchiveItem(req, appId);
        return FuncResponseUtils.success(Maps.newHashMap());
    }
}
