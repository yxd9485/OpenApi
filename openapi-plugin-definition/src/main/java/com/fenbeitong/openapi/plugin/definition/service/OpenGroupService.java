package com.fenbeitong.openapi.plugin.definition.service;

import com.fenbeitong.openapi.plugin.definition.constant.DefinitionRespCode;
import com.fenbeitong.openapi.plugin.definition.dto.function.OpenFunctionDTO;
import com.fenbeitong.openapi.plugin.definition.dto.function.OpenGroupDTO;
import com.fenbeitong.openapi.plugin.definition.dto.function.OpenGroupReqDTO;
import com.fenbeitong.openapi.plugin.definition.exception.OpenApiDefinitionException;
import com.fenbeitong.openapi.plugin.support.function.dao.OpenGroupDao;
import com.fenbeitong.openapi.plugin.support.function.entity.OpenGroup;
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
 * 功能簇
 * Created by lizhen on 2020/01/13.
 */
@ServiceAspect
@Service
public class OpenGroupService {

	@Autowired
	public OpenGroupDao openGroupDao;

    /**
     * 保存功能簇
     */
    public OpenGroupDTO createOpenGroup(OpenGroupReqDTO req) {
        // 检查groupName是否已存在
        Map<String, Object> condition = new HashMap<>();
        condition.put("groupName", req.getGroupName());
        List<OpenGroup> openGroups = openGroupDao.listOpenGroup(condition);
        if (!CollectionUtils.isBlank(openGroups)) {
            throw new OpenApiDefinitionException(DefinitionRespCode.OPEN_GROUP_NAME_ALREADY_EXIST, req.getGroupName());
        }
        // 检查groupCode是否已存在
        condition.clear();
        condition.put("groupCode", req.getGroupCode());
        openGroups = openGroupDao.listOpenGroup(condition);
        if (!CollectionUtils.isBlank(openGroups)) {
            throw new OpenApiDefinitionException(DefinitionRespCode.OPEN_GROUP_CODE_ALREADY_EXIST, req.getGroupCode());
        }
        Date now = DateUtils.now();
        OpenGroup openGroup = OpenGroup.builder()
                .groupCode(req.getGroupCode())
                .groupName(req.getGroupName())
                .createTime(now)
                .updateTime(now)
                .build();
        openGroupDao.saveSelective(openGroup);
        return OpenGroupDTO.builder()
                .groupCode(openGroup.getGroupCode())
                .groupName(openGroup.getGroupName())
                .build();
    }

    /**
     * 查询功能簇
     */
    public List<OpenGroupDTO> getOpenGroup() {
        List<OpenGroup> openGroups = openGroupDao.listAll();
        return openGroups.stream().map(openGroup -> OpenGroupDTO.builder()
                .groupCode(openGroup.getGroupCode())
                .groupName(openGroup.getGroupName())
                .build()).collect(Collectors.toList());
    }

}