package com.fenbeitong.openapi.plugin.func.frequent.service;

import com.fenbeitong.openapi.plugin.func.sign.dto.ApiRequestNoEmployee;
import com.fenbeitong.openapi.plugin.func.sign.service.FunctionAuthService;
import com.fenbeitong.openapi.plugin.support.frequent.dto.*;
import com.fenbeitong.openapi.plugin.support.frequent.service.AbstractFrequentService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.sdk.dto.frequent.CreateThirdFrequentRespDTO;
import com.fenbeitong.openapi.sdk.dto.frequent.GetThirdFrequentRespDTO;
import com.fenbeitong.openapi.sdk.dto.frequent.ListThirdFrequentRespDTO;
import com.fenbeitong.openapi.sdk.dto.frequent.UpdateThirdFrequentRespDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.List;

/**
 * 第三方常用联第人
 * Created by lizhen on 2020/3/16.
 */
@ServiceAspect
@Service
public class FuncFrequentService extends AbstractFrequentService {

    @Autowired
    private FunctionAuthService signService;


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
    protected void beforeCreateThirdFrequent(Object... createThirdFrequentParams) throws Exception {

    }

    @Override
    protected SupportCreateThirdFrequentReqDTO getCreateThirdFrequentReq(Object... createThirdFrequentParams) throws Exception {
        ApiRequestNoEmployee request = (ApiRequestNoEmployee) createThirdFrequentParams[0];
        SupportCreateThirdFrequentReqDTO createThirdFrequentReqDTO = JsonUtils.toObj(request.getData(), SupportCreateThirdFrequentReqDTO.class);
        return createThirdFrequentReqDTO;
    }

    @Override
    protected Object rebuildCreateThirdFrequent(CreateThirdFrequentRespDTO createThirdFrequentRes) {
        return createThirdFrequentRes;
    }

    @Override
    protected void beforeUpdateThirdFrequent(Object... updateThirdFrequentParams) throws Exception {

    }

    @Override
    protected SupportUpdateThirdFrequentReqDTO getUpdateThirdFrequentReq(Object... updateThirdFrequentParams) throws Exception {
        ApiRequestNoEmployee request = (ApiRequestNoEmployee) updateThirdFrequentParams[0];
        SupportUpdateThirdFrequentReqDTO updateThirdFrequentReqDTO = JsonUtils.toObj(request.getData(), SupportUpdateThirdFrequentReqDTO.class);
        return updateThirdFrequentReqDTO;
    }

    @Override
    protected Object rebuildUpdateThirdFrequent(UpdateThirdFrequentRespDTO updateThirdFrequentRes) {
        return updateThirdFrequentRes;
    }

    @Override
    protected void beforeDeleteThirdFrequent(Object... deleteThirdFrequentParams) throws Exception {

    }

    @Override
    protected SupportDeleteThirdFrequentReqDTO getDeleteThirdFrequentReq(Object... deleteThirdFrequentParams) throws Exception {
        ApiRequestNoEmployee request = (ApiRequestNoEmployee) deleteThirdFrequentParams[0];
        SupportDeleteThirdFrequentReqDTO deleteThirdFrequentReqDTO = JsonUtils.toObj(request.getData(), SupportDeleteThirdFrequentReqDTO.class);
        return deleteThirdFrequentReqDTO;
    }

    @Override
    protected void beforeListThirdFrequent(Object... listThirdFrequentParams) throws Exception {

    }

    @Override
    protected SupportListThirdFrequentReqDTO getListThirdFrequentReq(Object... listThirdFrequentParams) throws Exception {
        ApiRequestNoEmployee request = (ApiRequestNoEmployee) listThirdFrequentParams[0];
        SupportListThirdFrequentReqDTO listThirdFrequentReqDTO = JsonUtils.toObj(request.getData(), SupportListThirdFrequentReqDTO.class);
        return listThirdFrequentReqDTO;
    }

    @Override
    protected Object rebuildListThirdFrequent(List<ListThirdFrequentRespDTO> listThirdFrequentRes) {
        return listThirdFrequentRes;
    }

    @Override
    protected void beforeGetThirdFrequent(Object... getThirdFrequentParams) throws Exception {

    }

    @Override
    protected SupportGetThirdFrequentReqDTO getGetThirdFrequentReq(Object... getThirdFrequentParams) throws Exception {
        ApiRequestNoEmployee request = (ApiRequestNoEmployee) getThirdFrequentParams[0];
        SupportGetThirdFrequentReqDTO getThirdFrequentReqDTO = JsonUtils.toObj(request.getData(), SupportGetThirdFrequentReqDTO.class);
        return getThirdFrequentReqDTO;
    }

    @Override
    protected Object rebuildGetThirdFrequent(GetThirdFrequentRespDTO getThirdFrequentRes) {
        return getThirdFrequentRes;
    }

}
