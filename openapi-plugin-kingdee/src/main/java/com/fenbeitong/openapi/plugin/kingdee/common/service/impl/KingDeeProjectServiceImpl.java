package com.fenbeitong.openapi.plugin.kingdee.common.service.impl;

import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeeProjectDto;
import com.fenbeitong.openapi.plugin.kingdee.common.service.KingDeeProjectService;
import com.fenbeitong.openapi.plugin.support.project.service.OpenProjectService;
import com.fenbeitong.openapi.sdk.dto.project.AddThirdProjectReqDTO;
import com.fenbeitong.openapi.sdk.dto.project.ListThirdProjectRespDTO;
import com.fenbeitong.openapi.sdk.dto.project.MemberEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 获取分贝通项目信息
 * @Auther zhang.peng
 * @Date 2021/6/3
 */
@Slf4j
@ServiceAspect
@Service
public class KingDeeProjectServiceImpl implements KingDeeProjectService {

    @Autowired
    OpenProjectService openProjectService;

    @Override
    public ListThirdProjectRespDTO getProjectByCompanyId(String companyId) {
        return openProjectService.getProjectByCompanyId(companyId);
    }

    @Override
    public boolean addOrUpdateProjectInfo(ListThirdProjectRespDTO listThirdProjectRespDTO , KingDeeProjectDto projectListDTo , String companyId) {
        if ( null == projectListDTo ){
            log.info("金蝶项目信息为空 , companyId {} " , companyId);
            return false;
        }
        List<AddThirdProjectReqDTO> projectList = new ArrayList<>();
        Set<String> billNoSet = new HashSet<>();
        for (KingDeeProjectDto.Project project : projectListDTo.getProjectList()) {
            if (billNoSet.contains(project.getProjectCode())){
                continue;
            } else {
                AddThirdProjectReqDTO reqDTO = new AddThirdProjectReqDTO();
                buildReqDTO(companyId,project,reqDTO);
                projectList.add(reqDTO);
                billNoSet.add(project.getProjectCode());
            }
        }
        int result = openProjectService.projectUpdateOrAddByEach(listThirdProjectRespDTO, projectList, companyId);
        if ( result > 0 ){
            return true;
        }
        return false;
    }

    public void buildManagerInfo(String companyId, KingDeeProjectDto.Project projectDto, AddThirdProjectReqDTO reqDTO){
        List<MemberEntity> managers = new ArrayList<>();
        MemberEntity manager = new MemberEntity();
        manager.setCompanyId(companyId);
        manager.setIsManager(true);
        manager.setMemberId(projectDto.getUserId());
        manager.setMemberName(projectDto.getUserName());
        // 2是部门,3是员工
        manager.setMemberType(3);
        managers.add(manager);
        reqDTO.setManager(managers);
    }

    public void buildMemberInfo(KingDeeProjectDto.Project projectDto, AddThirdProjectReqDTO reqDTO){
        List<MemberEntity> members = new ArrayList<>();
        MemberEntity member = new MemberEntity();
        member.setMemberId(projectDto.getUserId());
        member.setIsManager(true);
        member.setMemberName(projectDto.getUserName());
        members.add(member);
        reqDTO.setMember(members);
    }

    public void buildReqDTO(String companyId, KingDeeProjectDto.Project projectDto, AddThirdProjectReqDTO reqDTO){
        // type 1  userId,  manager.memberId,   member.memberId  均为分贝ID memberDept.memberId 分贝部门ID;  2:第三方ID
        reqDTO.setType(2);
        // 设置项目创建人
        reqDTO.setUserId(projectDto.getUserId());
        reqDTO.setCode(projectDto.getProjectCode());
        reqDTO.setCompanyId(companyId);
        reqDTO.setName(projectDto.getProjectName());
        // 启用
        reqDTO.setState(1);
        reqDTO.setThirdCostId(projectDto.getProjectCode());
        reqDTO.setBeginDate(projectDto.getCreateTime());
        reqDTO.setEndDate(projectDto.getEndTime());
        // 项目负责人
        buildManagerInfo(companyId,projectDto,reqDTO);
        // 项目成员
        buildMemberInfo(projectDto,reqDTO);
    }
}
