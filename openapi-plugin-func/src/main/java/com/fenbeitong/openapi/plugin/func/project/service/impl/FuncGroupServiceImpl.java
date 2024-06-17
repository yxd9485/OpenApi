package com.fenbeitong.openapi.plugin.func.project.service.impl;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.func.project.dto.ProjectGroupDTO;
import com.fenbeitong.openapi.plugin.func.project.service.FuncGroupService;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.costcenter.CenterGroupDTO;
import com.fenbeitong.usercenter.api.service.costcenter.ICostCenterGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class FuncGroupServiceImpl implements FuncGroupService {

    @Autowired
    private CommonAuthService signService;

    @DubboReference(check = false)
    private ICostCenterGroupService iCostCenterGroupService;

    @Override
    public Object addGroup( ApiRequestBase request ) throws Exception {
        String companyId = signService.checkSign(request);
        ProjectGroupDTO projectGroupDTO = JsonUtils.toObj(request.getData(), ProjectGroupDTO.class);
        checkThirdGroupId( projectGroupDTO );
        CenterGroupDTO centerGroupDTO  = new CenterGroupDTO();
        BeanUtils.copyProperties( projectGroupDTO, centerGroupDTO );
        String groupId = iCostCenterGroupService.addCenterGroup(companyId, centerGroupDTO);
        Map<String,String> map = new HashMap<>();
        if(!StringUtils.isBlank(groupId)){
            map.put("id" , groupId);
        }
        return map;
    }

    @Override
    public Object updateGroup(ApiRequestBase request)  throws Exception {
        String companyId = signService.checkSign(request);
        ProjectGroupDTO projectGroupDTO = JsonUtils.toObj(request.getData(), ProjectGroupDTO.class);
        checkThirdGroupId( projectGroupDTO );
        CenterGroupDTO centerGroupDTO  = new CenterGroupDTO();
        BeanUtils.copyProperties( projectGroupDTO, centerGroupDTO );
        String groupId = iCostCenterGroupService.updateCenterGroup(companyId, centerGroupDTO);
        Map<String,String> map = new HashMap<>();
        if(!StringUtils.isBlank(groupId)){
            map.put("id" , groupId);
        }
        return map;
    }

    @Override
    public void deleteGroup(ApiRequestBase request)  throws Exception {
        String companyId = signService.checkSign(request);
        ProjectGroupDTO projectGroupDTO = JsonUtils.toObj(request.getData(), ProjectGroupDTO.class);
        checkThirdGroupId( projectGroupDTO );
        String thirdGroupId = projectGroupDTO.getThirdGroupId();
        iCostCenterGroupService.deleteCenterGroup( companyId , null , thirdGroupId );
    }

    @Override
    public List<CenterGroupDTO> list(ApiRequestBase request) throws Exception {
        String companyId = signService.checkSign(request);
        ProjectGroupDTO projectGroupDTO = JsonUtils.toObj(request.getData(), ProjectGroupDTO.class);
        String groupName = projectGroupDTO.getGroupName();
        return iCostCenterGroupService.list(companyId , groupName);
    }

    private void checkThirdGroupId(ProjectGroupDTO projectGroupDTO){
        String thirdGroupId = projectGroupDTO.getThirdGroupId();
        if(StringUtils.isBlank(thirdGroupId)){
            throw new OpenApiArgumentException("三方分组id不能为空！");
        }
        int length = 50;
        if (StringUtils.isNotBlank(thirdGroupId) && thirdGroupId.length()>length){
            throw new OpenApiArgumentException("三方分组id长度不能大于50 请检查third_group_id参数");
        }
    }

}
