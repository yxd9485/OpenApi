package com.fenbeitong.openapi.plugin.definition.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.support.city.entity.CityRelation;
import com.fenbeitong.openapi.plugin.support.city.service.CityRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 城市对照配置
 * Created by log.chang on 2021/1/5.
 */
@Controller
@Slf4j
@RequestMapping("city/definition")
public class CityRelationDefinitionController {

    @Autowired
    private CityRelationService cityRelationService;

    /**
     * 配置城市映射
     */
    @PostMapping("")
    @ResponseBody
    public Object getFbtCode(@RequestBody List<CityRelation> cityRelationList) {
        return OpenapiResponseUtils.success(cityRelationService.createCityRelation(cityRelationList));
    }

}
