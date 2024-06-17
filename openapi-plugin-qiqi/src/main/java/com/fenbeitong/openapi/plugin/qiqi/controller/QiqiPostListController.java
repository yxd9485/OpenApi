package com.fenbeitong.openapi.plugin.qiqi.controller;

import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResponseUtils;
import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResultEntity;
import com.fenbeitong.openapi.plugin.qiqi.constant.QiqiObjectEnum;
import com.fenbeitong.openapi.plugin.qiqi.dto.QiqiReqDTO;
import com.fenbeitong.openapi.plugin.qiqi.service.common.QiqiCommonReqServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @ClassName QiqiPostListController
 * @Description
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/7/8
 **/
@RestController
@RequestMapping("/qiqi/list")
@Api(value = "企企查询服务", tags = "企企查询服务")
public class QiqiPostListController {
    @Autowired
    QiqiCommonReqServiceImpl commonReqService;

    @RequestMapping("/postList")
    QiqiResultEntity postList(@RequestBody QiqiReqDTO qiqiReqDto) {
        QiqiObjectEnum objectEnum = QiqiObjectEnum.parse(qiqiReqDto.getObjectType());
        return QiqiResponseUtils.success(commonReqService.buildQiqiReq(qiqiReqDto.getCompanyId(), qiqiReqDto.getObjectType(), objectEnum.getDesc(), qiqiReqDto.getQueryConditions(), qiqiReqDto.getCommonReqDetailList()));
    }
}
