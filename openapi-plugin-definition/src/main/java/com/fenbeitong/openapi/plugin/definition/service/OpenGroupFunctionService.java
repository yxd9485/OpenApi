package com.fenbeitong.openapi.plugin.definition.service;

import com.fenbeitong.openapi.plugin.definition.constant.DefinitionRespCode;
import com.fenbeitong.openapi.plugin.definition.dto.function.OpenGroupFunctionDTO;
import com.fenbeitong.openapi.plugin.definition.dto.function.OpenGroupFunctionReqDTO;
import com.fenbeitong.openapi.plugin.definition.exception.OpenApiDefinitionException;
import com.fenbeitong.openapi.plugin.support.function.dao.OpenFunctionDao;
import com.fenbeitong.openapi.plugin.support.function.dao.OpenGroupDao;
import com.fenbeitong.openapi.plugin.support.function.dao.OpenGroupFunctionDao;
import com.fenbeitong.openapi.plugin.support.function.entity.OpenFunction;
import com.fenbeitong.openapi.plugin.support.function.entity.OpenGroup;
import com.fenbeitong.openapi.plugin.support.function.entity.OpenGroupFunction;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.fenbeitong.openapi.plugin.support.company.enums.AuthStatus.ENABLE;

/**
 * 功能簇子功能
 * Created by lizhen on 2020/01/13.
 */
@ServiceAspect
@Service
public class OpenGroupFunctionService {

    @Autowired
    public OpenGroupFunctionDao openGroupFunctionDao;

    @Autowired
    public OpenGroupDao openGroupDao;

    @Autowired
    public OpenFunctionDao openFunctionDao;

    /**
     * 保存功能簇子功能
     */
    public OpenGroupFunctionDTO createOpenGroupFunction(OpenGroupFunctionReqDTO req) {
        //检查groupCode
        OpenGroup openGroup = openGroupDao.getByGroupCode(req.getGroupCode());
        if (openGroup == null) {
            throw new OpenApiDefinitionException(DefinitionRespCode.OPEN_GROUP_CODE_UNKNOW, req.getGroupCode());
        }
        //检查functionCode
        OpenFunction openFunction = openFunctionDao.getByFunctionCode(req.getFunctionCode());
        if (openFunction == null) {
            throw new OpenApiDefinitionException(DefinitionRespCode.OPEN_FUNCTION_CODE_UNKNOW, req.getFunctionCode());
        }
        //检查groupCode + functionCode是否已存在
        Map<String, Object> condition = new HashMap<>();
        condition.put("groupCode", req.getGroupCode());
        condition.put("functionCode", req.getFunctionCode());
        List<OpenGroupFunction> openGroupFunctions = openGroupFunctionDao.listOpenGroupFunction(condition);
        if (!CollectionUtils.isBlank(openGroupFunctions)) {
            throw new OpenApiDefinitionException(DefinitionRespCode.OPEN_GROUP_FUNCTION_ALREADY_EXIST, req.getGroupCode(), req.getFunctionCode());
        }
        Date now = DateUtils.now();
        OpenGroupFunction openGroupFunction = OpenGroupFunction.builder()
                .groupCode(req.getGroupCode())
                .functionCode(req.getFunctionCode())
                .groupFunctionCode(req.getGroupCode() + "_" + req.getFunctionCode())
                .groupFunctionName(openGroup.getGroupName() + "-" + openFunction.getFunctionName())
                .status(ENABLE.getState())
                .createTime(now)
                .updateTime(now)
                .build();
        openGroupFunctionDao.saveSelective(openGroupFunction);
        return OpenGroupFunctionDTO.builder()
                .groupCode(openGroupFunction.getGroupCode())
                .functionCode(openGroupFunction.getFunctionCode())
                .groupFunctionCode(openGroupFunction.getGroupFunctionCode())
                .groupFunctionName(openGroupFunction.getGroupFunctionName())
                .status(openGroupFunction.getStatus())
                .build();
    }

    /**
     * 获取groupFunction
     *
     * @param condition
     * @return
     */
    public List<OpenGroupFunctionDTO> listOpenGroupFunction(Map<String, Object> condition) {
        List<OpenGroupFunction> openGroupFunctions = openGroupFunctionDao.listOpenGroupFunction(condition);
        return openGroupFunctions.stream().map(openGroupFunction -> OpenGroupFunctionDTO.builder()
                .groupCode(openGroupFunction.getGroupCode())
                .functionCode(openGroupFunction.getFunctionCode())
                .groupFunctionCode(openGroupFunction.getGroupFunctionCode())
                .groupFunctionName(openGroupFunction.getGroupFunctionName())
                .status(openGroupFunction.getStatus())
                .build()).collect(Collectors.toList());
    }

}