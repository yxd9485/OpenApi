package com.fenbeitong.openapi.plugin.func.project.service;

import com.fenbeitong.openapi.plugin.func.sign.dto.ApiRequestNoEmployee;
import com.fenbeitong.openapi.plugin.func.sign.service.FunctionAuthService;
import com.fenbeitong.openapi.plugin.support.project.dto.*;
import com.fenbeitong.openapi.plugin.support.project.service.AbstractProjectService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.sdk.dto.project.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.List;

/**w
 * 项目访问功能集成实现
 * Created by log.chang on 2019/12/3.
 */
@ServiceAspect
@Service
public class FuncProjectService extends AbstractProjectService {

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
    protected void beforeAddThirdProject(Object... addThirdProjectParams) throws Exception {

    }

    @Override
    protected SupportAddThirdProjectReqDTO getAddThirdProjectReq(Object... addThirdProjectParams) throws Exception {
        ApiRequestNoEmployee request = (ApiRequestNoEmployee) addThirdProjectParams[0];
        SupportAddThirdProjectReqDTO addThirdProjectReqDTO = JsonUtils.toObj(request.getData(), SupportAddThirdProjectReqDTO.class);
        return addThirdProjectReqDTO;
    }

    @Override
    protected Object rebuildAddThirdProject(AddThirdProjectRespDTO addThirdProjectRes) {
        return addThirdProjectRes;
    }

    @Override
    protected void beforeUpdateThirdProject(Object... updateThirdProjectParams) throws Exception {

    }

    @Override
    protected SupportUpdateThirdProjectReqDTO getUpdateThirdProjectReq(Object... updateThirdProjectParams) throws Exception {
        ApiRequestNoEmployee request = (ApiRequestNoEmployee) updateThirdProjectParams[0];
        SupportUpdateThirdProjectReqDTO updateThirdProjectReqDTO = JsonUtils.toObj(request.getData(), SupportUpdateThirdProjectReqDTO.class);
        return updateThirdProjectReqDTO;
    }

    @Override
    protected Object rebuildUpdateThirdProject(UpdateThirdProjectRespDTO updateThirdProjectRes) {
        return updateThirdProjectRes;
    }

    @Override
    protected void beforeUpdateThirdProjectState(Object... updateThirdProjectStateParams) throws Exception {

    }

    @Override
    protected SupportUpdateThirdProjectStateReqDTO getUpdateThirdProjectStateReq(Object... updateThirdProjectStateParams) throws Exception {
        ApiRequestNoEmployee request = (ApiRequestNoEmployee) updateThirdProjectStateParams[0];
        SupportUpdateThirdProjectStateReqDTO updateThirdProjectStateReqDTO = JsonUtils.toObj(request.getData(), SupportUpdateThirdProjectStateReqDTO.class);
        return updateThirdProjectStateReqDTO;
    }

    @Override
    protected void beforeUpdateThirdProjectStateByBatch(Object... updateThirdProjectStateByBatchParams) throws Exception {

    }

    @Override
    protected SupportUpdateThirdProjectStateByBatchReqDTO getUpdateThirdProjectStateByBatchReq(Object... updateThirdProjectStateByBatchParams) throws Exception {
        ApiRequestNoEmployee request = (ApiRequestNoEmployee) updateThirdProjectStateByBatchParams[0];
        SupportUpdateThirdProjectStateByBatchReqDTO updateThirdProjectStateByBatchReqDTO = JsonUtils.toObj(request.getData(), SupportUpdateThirdProjectStateByBatchReqDTO.class);
        return updateThirdProjectStateByBatchReqDTO;
    }

    @Override
    protected void beforeCreateThirdProjectByBatch(Object... createThirdProjectByBatchParams) throws Exception {

    }

    @Override
    protected SupportCreateThirdProjectByBatchReqDTO getCreateThirdProjectByBatchReq(Object... createThirdProjectByBatchParams) throws Exception {
        ApiRequestNoEmployee request = (ApiRequestNoEmployee) createThirdProjectByBatchParams[0];
        SupportCreateThirdProjectByBatchReqDTO createThirdProjectByBatchReqDTO = JsonUtils.toObj(request.getData(), SupportCreateThirdProjectByBatchReqDTO.class);
        return createThirdProjectByBatchReqDTO;
    }

    @Override
    protected Object rebuildCreateThirdProjectByBatch(List<CreateThirdProjectByBatchRespDTO> createThirdProjectByBatchRes) {
        return createThirdProjectByBatchRes;
    }

    @Override
    protected void beforeListThirdProject(Object... listThirdProjectParams) throws Exception {

    }

    @Override
    protected SupportListThirdProjectReqDTO getListThirdProjectReq(Object... listThirdProjectParams) throws Exception {
        ApiRequestNoEmployee request = (ApiRequestNoEmployee) listThirdProjectParams[0];
        SupportListThirdProjectReqDTO listThirdProjectReqDTO = JsonUtils.toObj(request.getData(), SupportListThirdProjectReqDTO.class);
        return listThirdProjectReqDTO;
    }

    @Override
    protected Object rebuildListThirdProject(ListThirdProjectRespDTO listThirdProjectRes) {
        return listThirdProjectRes;
    }

    @Override
    protected void beforeGetThirdProject(Object... getThirdProjectParams) throws Exception {

    }

    @Override
    protected SupportGetThirdProjectReqDTO getGetThirdProjectReq(Object... getThirdProjectParams) throws Exception {
        ApiRequestNoEmployee request = (ApiRequestNoEmployee) getThirdProjectParams[0];
        SupportGetThirdProjectReqDTO listThirdProjectReqDTO = JsonUtils.toObj(request.getData(), SupportGetThirdProjectReqDTO.class);
        return listThirdProjectReqDTO;
    }

    @Override
    protected Object rebuildGetThirdProject(GetThirdProjectRespDTO listThirdProjectRes) {
        return listThirdProjectRes;
    }

    @Override
    protected void beforeListThirdProjectApp(Object... listThirdProjectAppParams) throws Exception {

    }

    @Override
    protected SupportListThirdProjectAppReqDTO getListThirdProjectAppReq(Object... listThirdProjectAppParams) throws Exception {
        ApiRequestNoEmployee request = (ApiRequestNoEmployee) listThirdProjectAppParams[0];
        SupportListThirdProjectAppReqDTO listThirdProjectAppReqDTO = JsonUtils.toObj(request.getData(), SupportListThirdProjectAppReqDTO.class);
        return listThirdProjectAppReqDTO;
    }

    @Override
    protected Object rebuildListThirdProjectApp(List<ListThirdProjectAppRespDTO> listThirdProjectRes) {
        return listThirdProjectRes;
    }
}
