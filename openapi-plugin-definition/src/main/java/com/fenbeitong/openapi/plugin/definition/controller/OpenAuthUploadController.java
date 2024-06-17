package com.fenbeitong.openapi.plugin.definition.controller;

import com.fenbeitong.openapi.plugin.definition.dto.DefinitionResultDTO;
import com.fenbeitong.openapi.plugin.definition.service.excel.ExcelUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * 上传权限excel
 * Created by xiaowei on 2020/05/20.
 */
@Controller
@RequestMapping("/definitions/function/auth")
public class OpenAuthUploadController {

    @Autowired
    private ExcelUploadService excelUploadService;


    /**
     * 通过excel上传指定公司权限到数据库
     *
     * @param file excel文件   appId 公司的ID authNum 表示excel中权限的个数
     */
    @PostMapping("/upload/readexcel")
    @ResponseBody
    public Object readExcel(@RequestParam("file") MultipartFile file,
                            @RequestParam(value = "appId", required = true) String appId,
                            @RequestParam(value = "authNum", required = true) int authNum,
                            @RequestParam(value = "initDataFlag", required = false) Boolean initDataFlag) {
        return DefinitionResultDTO.success(excelUploadService.readExcel(file, appId, authNum, initDataFlag));
    }

    /**
     * 删除指定公司的权限数据
     *
     * @param appId 公司的ID authNum 表示excel中权限的个数
     */
    @DeleteMapping("/delete")
    @ResponseBody
    public Object readExcel(@RequestParam(value = "appId", required = true) String appId,
                            @RequestParam(value = "scenes", required = false) String scenes,
                            @RequestParam(value = "roleTypes", required = false) String roleTypes) {

        return DefinitionResultDTO.success(excelUploadService.deleteAuthByCondition(appId, scenes, roleTypes));
    }

}