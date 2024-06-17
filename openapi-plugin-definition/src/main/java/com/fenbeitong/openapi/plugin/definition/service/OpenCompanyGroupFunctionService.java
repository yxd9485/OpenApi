package com.fenbeitong.openapi.plugin.definition.service;

import com.fenbeitong.openapi.plugin.definition.constant.DefinitionRespCode;
import com.fenbeitong.openapi.plugin.definition.dto.function.OpenCompanyGroupFunctionDTO;
import com.fenbeitong.openapi.plugin.definition.dto.function.OpenCompanyGroupFunctionReqDTO;
import com.fenbeitong.openapi.plugin.definition.exception.OpenApiDefinitionException;
import com.fenbeitong.openapi.plugin.definition.util.DefinitionCheckUtils;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.company.enums.AuthStatus;
import com.fenbeitong.openapi.plugin.support.function.dao.OpenCompanyGroupFunctionDao;
import com.fenbeitong.openapi.plugin.support.function.dao.OpenGroupFunctionDao;
import com.fenbeitong.openapi.plugin.support.function.dao.OpenGroupFunctionRelDao;
import com.fenbeitong.openapi.plugin.support.function.entity.OpenCompanyGroupFunction;
import com.fenbeitong.openapi.plugin.support.function.entity.OpenGroupFunction;
import com.fenbeitong.openapi.plugin.support.function.entity.OpenGroupFunctionRel;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 企业功能配置
 * Created by lizhen on 2020/01/10.
 */
@ServiceAspect
@Service
public class OpenCompanyGroupFunctionService {

    @Autowired
    public OpenCompanyGroupFunctionDao openCompanyGroupFunctionDao;

    @Autowired
    private AuthDefinitionDao authDefinitionDao;

    @Autowired
    private OpenGroupFunctionDao openGroupFunctionDao;

    @Autowired
    private OpenGroupFunctionRelDao openGroupFunctionRelDao;

    /**
     * 保存企业功能配置
     */
    public List<OpenCompanyGroupFunctionDTO> createOpenCompanyGroupFunction(OpenCompanyGroupFunctionReqDTO req) {
        // 检查企业appId是否存在
        AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(req.getAppId());
        DefinitionCheckUtils.checkAuthDefinition(req.getAppId(), authDefinition);
        List<String> groupFunctionCodes = req.getGroupFunctionCode();
        List<OpenCompanyGroupFunction> openCompanyGroupFunctionList = new ArrayList<>();
        Date now = DateUtils.now();
        // 逐条判断groupFunctionId是否合法
        for (String groupFunctionCode : groupFunctionCodes) {
            // 检查groupFunctionId是否存在
            OpenGroupFunction openGroupFunction = openGroupFunctionDao.getByGroupFunctionCode(groupFunctionCode);
            if (openGroupFunction == null) {
                throw new OpenApiDefinitionException(DefinitionRespCode.OPEN_GROUP_FUNCTION_CODE_UNKNOWN, groupFunctionCode);
            }
            // 检查企业是否已开通过该groupFunction
            OpenCompanyGroupFunction openCompanyGroupFunction = openCompanyGroupFunctionDao.getByAppIdGroupFunctionCode(req.getAppId(), groupFunctionCode);
            if (openCompanyGroupFunction != null) {
                throw new OpenApiDefinitionException(DefinitionRespCode.OPEN_CORP_GROUP_FUNCTION_ALREADY_EXIST, groupFunctionCode);
            }
            // 检查前置条件是否满足
            Map<String, Object> condition = new HashMap<>();
            condition.put("groupFunctionCode", groupFunctionCode);
            List<OpenGroupFunctionRel> openGroupFunctionRels = openGroupFunctionRelDao.listOpenGroupFunctionRel(condition);
            condition.clear();
            condition.put("appId", req.getAppId());
            List<OpenCompanyGroupFunction> openCompanyGroupFunctions = openCompanyGroupFunctionDao.listOpenCompanyGroupFunction(condition);
            if (!CollectionUtils.isEmpty(openGroupFunctionRels)) {
                List<String> preGroupFunctionIds = openGroupFunctionRels.stream().map(openGroupFunctionRel -> openGroupFunctionRel.getPreGroupFunctionCode()).collect(Collectors.toList());
                List<String> companyGroupFunctionIds = openCompanyGroupFunctions.stream().map(companyGroupFunction -> companyGroupFunction.getGroupFunctionCode()).collect(Collectors.toList());
                // 把用户传来的groupFunctionId也加入到企业功能中，用户可能批量提交的数据中包含了前置条件开通
                companyGroupFunctionIds.addAll(groupFunctionCodes);
                List<String> reduce = preGroupFunctionIds.stream().filter(item -> !companyGroupFunctionIds.contains(item)).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(reduce)) {
                    throw new OpenApiDefinitionException(DefinitionRespCode.OPEN_CORP_PRE_GROUP_FUNCTION_NOT_CONFIGURED, groupFunctionCode, reduce);
                }
            }
            // 满足条件，准备保存
            openCompanyGroupFunction = OpenCompanyGroupFunction.builder()
                    .appId(req.getAppId())
                    .groupFunctionCode(groupFunctionCode)
                    .groupFunctionName(openGroupFunction.getGroupFunctionName())
                    .status(AuthStatus.ENABLE.getState())
                    .createTime(now)
                    .updateTime(now)
                    .build();
            openCompanyGroupFunctionList.add(openCompanyGroupFunction);
        }
        openCompanyGroupFunctionDao.saveList(openCompanyGroupFunctionList);
        return openCompanyGroupFunctionList.stream().map(openCompanyGroupFunction -> OpenCompanyGroupFunctionDTO.builder()
                .appId(openCompanyGroupFunction.getAppId())
                .groupFunctionCode(openCompanyGroupFunction.getGroupFunctionCode())
                .groupFunctionName(openCompanyGroupFunction.getGroupFunctionName())
                .status(openCompanyGroupFunction.getStatus())
                .build()).collect(Collectors.toList());
    }

    /**
     * 禁用
     */
    public List<OpenCompanyGroupFunctionDTO> disable(OpenCompanyGroupFunctionReqDTO req) {
        List<String> groupFunctionCodes = req.getGroupFunctionCode();
        List<OpenCompanyGroupFunction> openCompanyGroupFunctionList = new ArrayList<>();
        Date now = DateUtils.now();
        for (String groupFunctionCode : groupFunctionCodes) {
            // 检查企业是否开通了该groupFunction
            OpenCompanyGroupFunction openCompanyGroupFunction = openCompanyGroupFunctionDao.getByAppIdGroupFunctionCode(req.getAppId(), groupFunctionCode);
            if (openCompanyGroupFunction == null) {
                throw new OpenApiDefinitionException(DefinitionRespCode.OPEN_CORP_GROUP_FUNCTION_UNKNOWN, groupFunctionCode);
            }
            if (AuthStatus.DISABLE.getState() != NumericUtils.obj2int(openCompanyGroupFunction.getStatus())) {
                openCompanyGroupFunction.setStatus(AuthStatus.DISABLE.getState());
                openCompanyGroupFunction.setUpdateTime(now);
                openCompanyGroupFunctionDao.updateById(openCompanyGroupFunction);
            }
            openCompanyGroupFunctionList.add(openCompanyGroupFunction);
        }
        return openCompanyGroupFunctionList.stream().map(openCompanyGroupFunction -> OpenCompanyGroupFunctionDTO.builder()
                .appId(openCompanyGroupFunction.getAppId())
                .groupFunctionCode(openCompanyGroupFunction.getGroupFunctionCode())
                .groupFunctionName(openCompanyGroupFunction.getGroupFunctionName())
                .status(openCompanyGroupFunction.getStatus())
                .build()).collect(Collectors.toList());
    }


    /**
     * 启用
     */
    public List<OpenCompanyGroupFunctionDTO> enable(OpenCompanyGroupFunctionReqDTO req) {
        List<String> groupFunctionCodes = req.getGroupFunctionCode();
        List<OpenCompanyGroupFunction> openCompanyGroupFunctionList = new ArrayList<>();
        Date now = DateUtils.now();
        for (String groupFunctionCode : groupFunctionCodes) {
            // 检查企业是否开通了该groupFunction
            OpenCompanyGroupFunction openCompanyGroupFunction = openCompanyGroupFunctionDao.getByAppIdGroupFunctionCode(req.getAppId(), groupFunctionCode);
            if (openCompanyGroupFunction == null) {
                throw new OpenApiDefinitionException(DefinitionRespCode.OPEN_CORP_GROUP_FUNCTION_UNKNOWN, groupFunctionCode);
            }
            if (AuthStatus.ENABLE.getState() != NumericUtils.obj2int(openCompanyGroupFunction.getStatus())) {
                openCompanyGroupFunction.setStatus(AuthStatus.ENABLE.getState());
                openCompanyGroupFunction.setUpdateTime(now);
                openCompanyGroupFunctionDao.updateById(openCompanyGroupFunction);
            }
            openCompanyGroupFunctionList.add(openCompanyGroupFunction);
        }
        return openCompanyGroupFunctionList.stream().map(openCompanyGroupFunction -> OpenCompanyGroupFunctionDTO.builder()
                .appId(openCompanyGroupFunction.getAppId())
                .groupFunctionCode(openCompanyGroupFunction.getGroupFunctionCode())
                .groupFunctionName(openCompanyGroupFunction.getGroupFunctionName())
                .status(openCompanyGroupFunction.getStatus())
                .build()).collect(Collectors.toList());
    }


    /**
     * 企业开能功能列表
     */
    public List<OpenCompanyGroupFunctionDTO> getOpenCompanyGroupFunction(Map<String, Object> condition) {
        List<OpenCompanyGroupFunction> openCompanyGroupFunctions = openCompanyGroupFunctionDao.listOpenCompanyGroupFunction(condition);
        return openCompanyGroupFunctions.stream().map(openCompanyGroupFunction -> OpenCompanyGroupFunctionDTO.builder()
                .appId(openCompanyGroupFunction.getAppId())
                .groupFunctionCode(openCompanyGroupFunction.getGroupFunctionCode())
                .groupFunctionName(openCompanyGroupFunction.getGroupFunctionName())
                .status(openCompanyGroupFunction.getStatus()).build()).collect(Collectors.toList());
    }

}