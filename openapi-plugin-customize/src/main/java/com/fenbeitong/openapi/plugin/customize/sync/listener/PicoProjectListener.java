package com.fenbeitong.openapi.plugin.customize.sync.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.customize.common.service.ProjectListener;
import com.fenbeitong.openapi.plugin.customize.common.vo.OpenThirdProjectVo;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdEmployeeDao;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenCustomizeConfig;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdEmployee;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenDepartmentServiceImpl;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.project.dto.MemberNewEntity;
import com.fenbeitong.openapi.plugin.support.project.dto.SupportUcThirdProjectReqDTO;
import com.fenbeitong.openapi.plugin.support.project.service.OpenProjectServiceV2;
import com.fenbeitong.openapi.plugin.support.util.SuperAdminUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.costcenter.CenterGroupDTO;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author duhui
 * @Date 2021/8/5
 **/
@ServiceAspect
@Service
@Slf4j
public class PicoProjectListener implements ProjectListener {

    @Autowired
    OpenEmployeeServiceImpl openEmployeeService;

    @Autowired
    OpenThirdEmployeeDao openThirdEmployeeDao;

    @Autowired
    OpenProjectServiceV2 openProjectServiceV2;

    @Autowired
    private OpenDepartmentServiceImpl openDepartmentService;

    @Autowired
    private SuperAdminUtils superAdminUtils;

    @Override
    public List<SupportUcThirdProjectReqDTO> fileOpenThirdEmployeeDto(List<SupportUcThirdProjectReqDTO> addThirdProjectReqDTOList, String companyId) {
        return addThirdProjectReqDTOList;
    }

    @Override
    public OpenThirdProjectVo getProjectMaping(OpenCustomizeConfig openCustomizeConfig, String respData) {
        String superAdminId = superAdminUtils.companySuperAdmin(openCustomizeConfig.getCompanyId()).getThirdEmployeeId();
        List<OpenThirdEmployee> openThirdEmployeeList = openThirdEmployeeDao.listEmployeeByCompanyId(openCustomizeConfig.getCompanyId());
        List<String> thirdEmployeeIds = openThirdEmployeeList.stream().map(OpenThirdEmployee::getThirdEmployeeId).collect(Collectors.toList());
        Map cusConfigMap = JsonUtils.toObj(StringUtils.obj2str(openCustomizeConfig.getExtend()), Map.class);
        String emp = StringUtils.obj2str(cusConfigMap.get("emp"));
        List orgList = Arrays.asList(StringUtils.obj2str(cusConfigMap.get("org")).split(","));
        OpenThirdProjectVo openThirdProjectVo = new OpenThirdProjectVo();
        List<SupportUcThirdProjectReqDTO> addThirdProjectReqDTOList = new ArrayList<>();
        List<CenterGroupDTO> centerGroupDTOList = new ArrayList<>();
        Map<String, Object> map = JsonUtils.toObj(respData, Map.class);
        if (!ObjectUtils.isEmpty(map) && "200".equals(map.get("code").toString())) {
            List<Map<String, Object>> empList = JsonUtils.toObj(JsonUtils.toJson(map.get("result")), new TypeReference<List<Map<String, Object>>>() {
            });
            if (ObjectUtils.isEmpty(empList)) {
                throw new FinhubException(500, "笔克项目同步数据异常");
            }
            empList.forEach(employee -> {
                SupportUcThirdProjectReqDTO addThirdProjectReqDTO = new SupportUcThirdProjectReqDTO();
                addThirdProjectReqDTO.setCompanyId(openCustomizeConfig.getCompanyId());
                addThirdProjectReqDTO.setType(2);
                addThirdProjectReqDTO.setUsableRange(2);
                addThirdProjectReqDTO.setState(1);
                addThirdProjectReqDTO.setUserId(superAdminId);
                addThirdProjectReqDTO.setExpiredState(1);
                addThirdProjectReqDTO.setThirdCostId(StringUtils.obj2str(employee.get("ProjectID")));
                addThirdProjectReqDTO.setCode(StringUtils.obj2str(employee.get("ShowCode")));
                addThirdProjectReqDTO.setName(StringUtils.obj2str(employee.get("ProjectName")));
                List<MemberNewEntity> manager = new ArrayList<>();
                MemberNewEntity memberEntity = new MemberNewEntity();
                if (thirdEmployeeIds.contains(StringUtils.obj2str(employee.get("ProjectLeaderID")))) {
                    memberEntity.setMemberId(StringUtils.obj2str(employee.get("ProjectLeaderID")));
                    memberEntity.setIsManager(true);
                    memberEntity.setMemberName(StringUtils.obj2str(employee.get("LeaderName")));
                    manager.add(memberEntity);
                    addThirdProjectReqDTO.setManager(manager);
                }
                // 所属部门
                List<MemberNewEntity> memberDept = new ArrayList<>();
                // 项目成员
                List<MemberNewEntity> member = new ArrayList<>();
                String[] grop1 = StringUtils.obj2str(employee.get("ProjectDept")).split(",");
                // 项目分组
                List<String> thirdGroupList = new ArrayList<>();
                List<String> projectList = Arrays.asList(grop1);
                for (int i = 0; i < projectList.size(); i++) {
                    String[] grop2 = projectList.get(i).split("\\|");
                    if (i == 0) {
                        // 判断是否只有字符串和数字
                        if (grop2[1].matches("^[a-z0-9A-Z]+$")) {
                            CenterGroupDTO centerGroupDTO = new CenterGroupDTO();
                            centerGroupDTO.setThirdGroupId(grop2[0] + "_" + grop2[1]);
                            centerGroupDTO.setGroupName(grop2[1]);
                            centerGroupDTOList.add(centerGroupDTO);
                            thirdGroupList.add(grop2[0] + "_" + grop2[1]);
                        } else {
                            log.info("组织名称不能有汉字:{}", JsonUtils.toJson(projectList.get(i)));
                        }
                    }

                    MemberNewEntity memberEntity1 = new MemberNewEntity();
                    memberEntity1.setMemberId(grop2[0]);
                    memberEntity1.setMemberName(grop2[1]);
                    memberEntity1.setIsManager(false);
                    memberDept.add(memberEntity1);
                    // 客户定制需求，**可以查看特定部门的全部项目
                    if (orgList.contains(grop2[0]) && member.size() <= 1) {
                        MemberNewEntity memberEntity2 = new MemberNewEntity();
                        memberEntity2.setMemberId(emp);
                        member.add(memberEntity2);
                    }

                }
                addThirdProjectReqDTO.setMemberDept(memberDept);
                addThirdProjectReqDTO.setMember(member);
                addThirdProjectReqDTO.setThirdGroupList(thirdGroupList);
                addThirdProjectReqDTOList.add(addThirdProjectReqDTO);
            });
        }
        openThirdProjectVo.setAddThirdProjectReqDTO(addThirdProjectReqDTOList);
        projectGroupSync(openCustomizeConfig.getCompanyId(), centerGroupDTOList);
        return openThirdProjectVo;
    }

    /**
     * 项目分组同步
     */
    private void projectGroupSync(String companyId, List<CenterGroupDTO> groupDTOList) {
        openProjectServiceV2.projectGroupSync(groupDTOList, companyId);
    }


    @Override
    public void setHead(Map<String, String> map, String companyId) {

    }

    @Override
    public void setBody(Map<String, String> map, String companyId) {

    }

}
