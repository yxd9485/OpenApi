package com.fenbeitong.openapi.plugin.func.city.service;

import com.fenbeitong.openapi.plugin.func.employee.service.FuncEmployeeService;
import com.fenbeitong.openapi.plugin.func.sign.dto.ApiRequestNoEmployee;
import com.fenbeitong.openapi.plugin.func.sign.service.FunctionAuthService;
import com.fenbeitong.openapi.plugin.support.city.dto.SupportCityCodeReqDTO;
import com.fenbeitong.openapi.plugin.support.city.dto.SupportCityListReqDTO;
import com.fenbeitong.openapi.plugin.support.city.dto.SupportCityNameReqDTO;
import com.fenbeitong.openapi.plugin.support.city.service.AbstractCityService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.sdk.dto.city.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.List;

/**
 * 城市服务
 */
@ServiceAspect
@Service
public class FuncCityService extends AbstractCityService {


    @Autowired
    private FunctionAuthService signService;

    @Autowired
    private FuncEmployeeService funcEmployeeService;

    @Override
    public String getProcessorKey() {
        return super.getProcessorKey();
    }


    @Override
    protected String checkSign(Object... params) throws Exception {
        ApiRequestBase request = (ApiRequestBase) params[0];
        return signService.checkSign(request);
    }

    @Override
    protected void beforeCityList(Object... cityList) throws Exception {

    }

    @Override
    protected SupportCityListReqDTO getCityListReq(Object... cityListParams) throws Exception {
        ApiRequestNoEmployee request = (ApiRequestNoEmployee) cityListParams[0];
        SupportCityListReqDTO cityListReqDTO = JsonUtils.toObj(request.getData(), SupportCityListReqDTO.class);
        return cityListReqDTO;
    }

    @Override
    protected Object rebuildCityList(CityListRespDTO cityListRes) {
        return cityListRes;
    }

    @Override
    protected void beforeCityCode(Object... cityCode) throws Exception {

    }

    @Override
    protected SupportCityCodeReqDTO getCityCodeReq(Object... cityCodeParams) throws Exception {
        ApiRequestNoEmployee request = (ApiRequestNoEmployee) cityCodeParams[0];
        SupportCityCodeReqDTO cityCodeReqDTO = JsonUtils.toObj(request.getData(), SupportCityCodeReqDTO.class);
        return cityCodeReqDTO;
    }

    @Override
    protected Object rebuildCityCode(CityCodeRespDTO cityCodeRes) {
        return cityCodeRes;
    }

    @Override
    protected void beforeCityCodeHotel(Object... cityCode) throws Exception {

    }

    @Override
    protected SupportCityCodeReqDTO getCityCodeHotelReq(Object... cityCodeParams) throws Exception {
        ApiRequestNoEmployee request = (ApiRequestNoEmployee) cityCodeParams[0];
        SupportCityCodeReqDTO cityCodeReqDTO = JsonUtils.toObj(request.getData(), SupportCityCodeReqDTO.class);
        return cityCodeReqDTO;
    }

    @Override
    protected Object rebuildCityCodeHotel(List<CityCodeHotelRespDTO> cityCodeRes) {
        return cityCodeRes;
    }

    @Override
    protected void beforeNationInfo(Object... nationalityParams) throws Exception {

    }

    @Override
    protected Object rebuildGetNationality(List<CodeNameEntity> nationalityRes) {
        return nationalityRes;
    }

    @Override
    protected void beforeGetCarCityByName(Object... getCarCityByNameParams) throws Exception {

    }

    @Override
    protected SupportCityNameReqDTO getCarCityByNameReq(Object... getCarCityByNameParams) throws Exception {
        ApiRequestNoEmployee request = (ApiRequestNoEmployee) getCarCityByNameParams[0];
        SupportCityNameReqDTO cityNameReqDTO = JsonUtils.toObj(request.getData(), SupportCityNameReqDTO.class);
        return cityNameReqDTO;
    }

    @Override
    protected Object rebuildGetCarCityByName(CityCarRespDTO getCarCityByNameRes) {
        return getCarCityByNameRes;
    }

    @Override
    protected void beforeGetIntelAirCityByName(Object... getIntelAirCityByNameParams) throws Exception {

    }

    @Override
    protected SupportCityNameReqDTO getIntelAirCityByNameReq(Object... getIntelAirCityByNameParams) throws Exception {
        ApiRequestNoEmployee request = (ApiRequestNoEmployee) getIntelAirCityByNameParams[0];
        SupportCityNameReqDTO cityNameReqDTO = JsonUtils.toObj(request.getData(), SupportCityNameReqDTO.class);
        return cityNameReqDTO;
    }

    @Override
    protected Object rebuildGetIntelAirCityByName(CityIntlAirRespDTO getIntelAirCityByNameRes) {
        return getIntelAirCityByNameRes;
    }

    @Override
    protected void beforeGetCityCodeByName(Object... getCityCodeByNameParams) throws Exception {

    }

    @Override
    protected SupportCityNameReqDTO getCityCodeByNameReq(Object... getCityCodeByNameParams) throws Exception {
        ApiRequestNoEmployee request = (ApiRequestNoEmployee) getCityCodeByNameParams[0];
        SupportCityNameReqDTO cityNameReqDTO = JsonUtils.toObj(request.getData(), SupportCityNameReqDTO.class);
        return cityNameReqDTO;
    }

    @Override
    protected Object rebuildGetCityCodeByName(CityByNameRespDTO getCityCodeByNameRes) {
        return getCityCodeByNameRes;
    }


    @Override
    protected String getAdminToken(String appId) {
        String admin = funcEmployeeService.superAdmin(appId);
        String employeeFbToken = funcEmployeeService.getEmployeeFbToken(appId, admin, "0");
        return employeeFbToken;
    }
}
