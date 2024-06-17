package com.fenbeitong.openapi.plugin.func.city.controller;

import com.fenbeitong.openapi.plugin.func.city.service.FuncCityService;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.func.sign.dto.ApiRequestNoEmployee;
import com.fenbeitong.openapi.plugin.support.citycode.dto.CityBaseInfo;
import com.fenbeitong.openapi.plugin.support.citycode.service.CityCodeService;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

/**
 * 城市服务
 */
@RestController
@RequestMapping("/func/city")
@Api(value = "城市服务", tags = "城市服务", description = "城市服务")
public class FuncCityController {

    @Autowired
    private FuncCityService funcCityService;

    @Autowired
    private CityCodeService cityCodeService;

    @RequestMapping("/cityList")
    @ApiOperation(value = "根据城市获取机票机场列表和火车站列表", notes = "根据城市获取机票机场列表和火车站列表", httpMethod = "POST", response = FuncResultEntity.class)
    public Object cityList(@Valid ApiRequestNoEmployee apiRequest) throws Exception {
        Object result = funcCityService.cityList(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/cityCode")
    @ApiOperation(value = "获取城市编码", notes = "获取城市编码", httpMethod = "POST", response = FuncResultEntity.class)
    public Object cityCode(@Valid ApiRequestNoEmployee apiRequest) throws Exception {
        Object result = funcCityService.cityCode(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/cityCodeHotel")
    @ApiOperation(value = "获取酒店城市编码", notes = "获取酒店城市编码", httpMethod = "POST", response = FuncResultEntity.class)
    public Object cityCodeHotel(@Valid ApiRequestNoEmployee apiRequest) throws Exception {
        Object result = funcCityService.cityCodeHotel(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/national")
    @ApiOperation(value = "查询国际信息", notes = "查询国际信息", httpMethod = "POST", response = FuncResultEntity.class)
    public Object getNationality(@Valid ApiRequestNoEmployee apiRequest) throws Exception {
        Object result = funcCityService.getNationality(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/car/getCityByName")
    @ApiOperation(value = "获取用车城市信息", notes = "获取用车城市信息", httpMethod = "POST", response = FuncResultEntity.class)
    public Object getCarCityByName(@Valid ApiRequestNoEmployee apiRequest) throws Exception {
        Object result = funcCityService.getCarCityByName(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/air/getCityByName")
    @ApiOperation(value = "获取国际机票城市信息", notes = "获取国际机票城市信息", httpMethod = "POST", response = FuncResultEntity.class)
    public Object getIntelAirCityByName(@Valid ApiRequestNoEmployee apiRequest) throws Exception {
        Object result = funcCityService.getIntelAirCityByName(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/getCityCodeByName")
    @ApiOperation(value = "获取城市编码", notes = "获取城市编码", httpMethod = "POST", response = FuncResultEntity.class)
    public Object getCityCodeByName(@Valid ApiRequestNoEmployee apiRequest) throws Exception {
        Object result = funcCityService.getCityCodeByName(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/getAirCity")
    public Object getAirCity(String input) {
        Map<String, CityBaseInfo> airCityMap = cityCodeService.getAirCode(Lists.newArrayList(input));
        return FuncResponseUtils.success(airCityMap);
    }

    @RequestMapping("/getIntlAirCity")
    public Object getIntlAirCity(String input) {
        Map<String, CityBaseInfo> intlAirCityMap = cityCodeService.getIntlAirCode(Lists.newArrayList(input));
        return FuncResponseUtils.success(intlAirCityMap);
    }

    @RequestMapping("/getTrainCity")
    public Object getTrainCity(String input) {
        Map<String, CityBaseInfo> trainCityMap = cityCodeService.getTrainCode(Lists.newArrayList(input));
        return FuncResponseUtils.success(trainCityMap);
    }

    @RequestMapping("/getHotelCity")
    public Object getHotelCity(String input) {
        Map<String, CityBaseInfo> hotelCityMap = cityCodeService.getHotelCode(Lists.newArrayList(input));
        return FuncResponseUtils.success(hotelCityMap);
    }
}
