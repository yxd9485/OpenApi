package com.fenbeitong.openapi.plugin.qiqi.controller;

import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResponseUtils;
import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResultEntity;
import com.fenbeitong.openapi.plugin.qiqi.service.department.IQiqiDepartmentService;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName QiqiDepartmentController
 * @Description 企企主部门数据对接
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/5/13 下午2:07
 **/
@RestController
@RequestMapping("/qiqi/job")
@Api(value = "企企部门数据同步", tags = "企企部门数据同步", description = "企企部门数据同步")
public class QiqiDepartmentController {


    @Autowired
    private IQiqiDepartmentService qiqiDepartmentService;

    @RequestMapping("/syncOrgEmployee")
    @ResponseBody
    QiqiResultEntity syncDepartment(@RequestParam(value = "companyId", required = true) String companyId) throws Exception {
        qiqiDepartmentService.syncQiqiOrgEmployee(companyId);
        return QiqiResponseUtils.success(Maps.newHashMap());
    }

}
