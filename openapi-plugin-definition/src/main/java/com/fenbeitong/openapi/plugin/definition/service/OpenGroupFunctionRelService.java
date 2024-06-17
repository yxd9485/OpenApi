package com.fenbeitong.openapi.plugin.definition.service;

import com.fenbeitong.openapi.plugin.definition.constant.DefinitionRespCode;
import com.fenbeitong.openapi.plugin.definition.dto.function.OpenGroupFunctionDTO;
import com.fenbeitong.openapi.plugin.definition.exception.OpenApiDefinitionException;
import com.fenbeitong.openapi.plugin.support.function.dao.OpenGroupFunctionDao;
import com.fenbeitong.openapi.plugin.support.function.entity.OpenGroupFunction;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.*;
import java.util.stream.Collectors;

import com.fenbeitong.openapi.plugin.support.function.entity.OpenGroupFunctionRel;
import com.fenbeitong.openapi.plugin.support.function.dao.OpenGroupFunctionRelDao;
import com.fenbeitong.openapi.plugin.definition.dto.function.OpenGroupFunctionRelReqDTO;
import com.fenbeitong.openapi.plugin.definition.dto.function.OpenGroupFunctionRelDTO;

/**
 * 功能簇子功能前置条件
 * Created by lizhen on 2020/01/13.
 */
@ServiceAspect
@Service
public class OpenGroupFunctionRelService {

    @Autowired
    public OpenGroupFunctionRelDao openGroupFunctionRelDao;

    @Autowired
    private OpenGroupFunctionDao openGroupFunctionDao;

    /**
     * 保存功能簇子功能前置条件
     */
    public List<OpenGroupFunctionRelDTO> createOpenGroupFunctionRel(OpenGroupFunctionRelReqDTO req) {
        // 检查groupFunctionCode, preGroupFunctionCode是否存在
        OpenGroupFunction openGroupFunction = openGroupFunctionDao.getByGroupFunctionCode(req.getGroupFunctionCode());
        if (openGroupFunction == null) {
            throw new OpenApiDefinitionException(DefinitionRespCode.OPEN_GROUP_FUNCTION_CODE_UNKNOWN, req.getGroupFunctionCode());
        }
        List<OpenGroupFunctionRel> openGroupFunctionRelList = new ArrayList<>();
        Date now = DateUtils.now();
        for (String preGroupFunctionCode : req.getPreGroupFunctionCode()) {
            // 检查groupFunctionCode不可与preGroupFunctionCode相同
            if (preGroupFunctionCode.equals(req.getGroupFunctionCode())) {
                throw new OpenApiDefinitionException(DefinitionRespCode.OPEN_GROUP_FUNCTION_REL_SAME, req.getGroupFunctionCode(), preGroupFunctionCode);
            }
            OpenGroupFunction preOpenGroupFunction = openGroupFunctionDao.getByGroupFunctionCode(preGroupFunctionCode);
            if (preOpenGroupFunction == null) {
                throw new OpenApiDefinitionException(DefinitionRespCode.OPEN_GROUP_FUNCTION_CODE_UNKNOWN, preGroupFunctionCode);
            }
            // 检查groupFunctionCode + preGroupFunctionCode是否已添加过
            Map<String, Object> condition = new HashMap<>();
            condition.put("groupFunctionCode", req.getGroupFunctionCode());
            condition.put("preGroupFunctionCode", preGroupFunctionCode);
            List<OpenGroupFunctionRel> openGroupFunctionRels = openGroupFunctionRelDao.listOpenGroupFunctionRel(condition);
            if (!CollectionUtils.isBlank(openGroupFunctionRels)) {
                throw new OpenApiDefinitionException(DefinitionRespCode.OPEN_GROUP_FUNCTION_REL_ALREADY_EXIST, req.getGroupFunctionCode(), preGroupFunctionCode);
            }
            OpenGroupFunctionRel openGroupFunctionRel = OpenGroupFunctionRel.builder()
                    .groupFunctionCode(req.getGroupFunctionCode())
                    .groupFunctionName(openGroupFunction.getGroupFunctionName())
                    .preGroupFunctionCode(preGroupFunctionCode)
                    .preGroupFunctionName(preOpenGroupFunction.getGroupFunctionName())
                    .createTime(now)
                    .updateTime(now)
                    .build();
            openGroupFunctionRelList.add(openGroupFunctionRel);
        }
        openGroupFunctionRelDao.saveList(openGroupFunctionRelList);
        return openGroupFunctionRelList.stream().map(openGroupFunctionRel -> OpenGroupFunctionRelDTO.builder()
                .groupFunctionCode(openGroupFunctionRel.getGroupFunctionCode())
                .groupFunctionName(openGroupFunctionRel.getGroupFunctionName())
                .preGroupFunctionCode(openGroupFunctionRel.getPreGroupFunctionCode())
                .preGroupFunctionName(openGroupFunctionRel.getPreGroupFunctionName())
                .build()).collect(Collectors.toList());
    }

    /**
     * 获取groupFunction
     *
     * @param condition
     * @return
     */
    public List<OpenGroupFunctionRelDTO> listOpenGroupFunctionRel(Map<String, Object> condition) {
        List<OpenGroupFunctionRel> openGroupFunctionRels = openGroupFunctionRelDao.listOpenGroupFunctionRel(condition);
        return openGroupFunctionRels.stream().map(openGroupFunctionRel -> OpenGroupFunctionRelDTO.builder()
                .groupFunctionCode(openGroupFunctionRel.getGroupFunctionCode())
                .groupFunctionName(openGroupFunctionRel.getGroupFunctionName())
                .preGroupFunctionCode(openGroupFunctionRel.getPreGroupFunctionCode())
                .preGroupFunctionName(openGroupFunctionRel.getPreGroupFunctionName())
                .build()).collect(Collectors.toList());
    }

    /**
     * 删除功能簇子功能前置条件
     */
    public Map deleteOpenGroupFunctionRel(OpenGroupFunctionRelReqDTO req) {
        for (String preGroupFunctionCode : req.getPreGroupFunctionCode()) {
            openGroupFunctionRelDao.deleteOpenGroupFunctionRel(req.getGroupFunctionCode(), preGroupFunctionCode);
        }
        return new HashMap<>();
    }

}