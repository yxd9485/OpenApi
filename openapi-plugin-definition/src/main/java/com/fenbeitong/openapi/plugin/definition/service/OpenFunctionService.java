package com.fenbeitong.openapi.plugin.definition.service;

import com.fenbeitong.openapi.plugin.definition.constant.DefinitionRespCode;
import com.fenbeitong.openapi.plugin.definition.dto.function.OpenFunctionDTO;
import com.fenbeitong.openapi.plugin.definition.dto.function.OpenFunctionReqDTO;
import com.fenbeitong.openapi.plugin.definition.exception.OpenApiDefinitionException;
import com.fenbeitong.openapi.plugin.support.function.dao.OpenFunctionDao;
import com.fenbeitong.openapi.plugin.support.function.entity.OpenFunction;
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

/**
 * 功能
 * Created by lizhen on 2020/01/13.
 */
@ServiceAspect
@Service
public class OpenFunctionService {

    @Autowired
    public OpenFunctionDao openFunctionDao;

    /**
     * 保存功能
     */
    public OpenFunctionDTO createOpenFunction(OpenFunctionReqDTO req) {
        // 检查functionName是否已存在
        Map<String, Object> condition = new HashMap<>();
        condition.put("functionName", req.getFunctionName());
        List<OpenFunction> openFunctions = openFunctionDao.listOpenFunction(condition);
        if (!CollectionUtils.isBlank(openFunctions)) {
            throw new OpenApiDefinitionException(DefinitionRespCode.OPEN_FUNCTION_NAME_ALREADY_EXIST, req.getFunctionName());
        }
        // 检查functionCode是否已存在
        condition.clear();
        condition.put("functionCode", req.getFunctionCode());
        openFunctions = openFunctionDao.listOpenFunction(condition);
        if (!CollectionUtils.isBlank(openFunctions)) {
            throw new OpenApiDefinitionException(DefinitionRespCode.OPEN_FUNCTION_CODE_ALREADY_EXIST, req.getFunctionCode());
        }
        Date now = DateUtils.now();
        OpenFunction openFunction = OpenFunction.builder()
                .functionCode(req.getFunctionCode())
                .functionName(req.getFunctionName())
                .createTime(now)
                .updateTime(now)
                .build();
        openFunctionDao.saveSelective(openFunction);
        return OpenFunctionDTO.builder()
                .functionCode(openFunction.getFunctionCode())
                .functionName(openFunction.getFunctionName())
                .build();
    }

    /**
     * 查询所有功能
     */
    public List<OpenFunctionDTO> getOpenFunction() {
        List<OpenFunction> openFunctions = openFunctionDao.listAll();
        return openFunctions.stream().map(openFunction -> OpenFunctionDTO.builder()
                .functionName(openFunction.getFunctionName())
                .functionCode(openFunction.getFunctionCode())
                .build()).collect(Collectors.toList());
    }

}