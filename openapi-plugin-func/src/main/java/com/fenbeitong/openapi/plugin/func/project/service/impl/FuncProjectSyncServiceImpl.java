package com.fenbeitong.openapi.plugin.func.project.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.func.project.service.FuncProjectSyncService;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenDepartmentServiceImpl;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.project.service.OpenProjectService;
import com.fenbeitong.openapi.sdk.dto.project.AddThirdProjectReqDTO;
import com.fenbeitong.openapi.sdk.dto.project.ListThirdProjectRespDTO;
import com.fenbeitong.openapi.sdk.dto.project.UpdateThirdProjectStateReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * <p>Title: FuncProjectSyncServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-12-11 14:06
 */
@ServiceAspect
@Service
@Slf4j
public class FuncProjectSyncServiceImpl implements FuncProjectSyncService {

    @Autowired
    OpenProjectService openProjectService;

    @Autowired
    private OpenDepartmentServiceImpl departmentService;

    @Autowired
    private OpenEmployeeServiceImpl openEmployeeService;

    @Override
    public String allProjectSync(String data) {
        long start = System.currentTimeMillis();
        log.info("接收的项目数据:{}", data);
        JSONArray jsonArray = JSONObject.parseArray(data);
        List<AddThirdProjectReqDTO> projectList = jsonArray.toJavaList(AddThirdProjectReqDTO.class);
        if (ObjectUtils.isEmpty(projectList)) {
            return "fail";
        }
        String companyId = projectList.get(0).getCompanyId();
        // 封装UC数据
        packageUC(projectList, companyId);
        // 查询本地数据
        long queryStart = System.currentTimeMillis();
        ListThirdProjectRespDTO listThirdProjectRespDTO = openProjectService.getProjectByCompanyId(companyId);
        log.info("companyId:{},获取全量项目数据全量数据 ListThirdProjectRespDTO 查询耗时 {} ms", companyId, System.currentTimeMillis() - queryStart);

        // 数据同步
        long updateStart = System.currentTimeMillis();
        openProjectService.projectUpdateOrAdd(listThirdProjectRespDTO, projectList, companyId);
        log.info("companyId:{},项目更据耗新 -> projectUpdateOrAdd 耗时 {} ms", companyId, System.currentTimeMillis() - updateStart);
        log.info("数据同步 -> projectSync 总耗时 {} ms", System.currentTimeMillis() - start);
        return "success";
    }


    @Override
    public String partProjectSync(String data) {
        long start = System.currentTimeMillis();
        log.info("接收的项目数据:{}", data);
        JSONArray jsonArray = JSONObject.parseArray(data);
        List<AddThirdProjectReqDTO> projectList = jsonArray.toJavaList(AddThirdProjectReqDTO.class);
        if (ObjectUtils.isEmpty(projectList)) {
            return "fail";
        }
        String companyId = projectList.get(0).getCompanyId();
        // 封装UC数据
        packageUC(projectList, companyId);

        // 数据同步
        long updateStart = System.currentTimeMillis();
        openProjectService.partProjectUpdateOrAdd(projectList);
        log.info("companyId:{},项目更据耗新 -> projectUpdateOrAdd 耗时 {} ms", companyId, System.currentTimeMillis() - updateStart);
        log.info("数据同步 -> projectSync 总耗时 {} ms", System.currentTimeMillis() - start);
        return "success";
    }

    @Override
    public String updateStatus(String data) {
        JSONArray jsonArray = JSONObject.parseArray(data);
        List<AddThirdProjectReqDTO> projectList = jsonArray.toJavaList(AddThirdProjectReqDTO.class);
        projectList.forEach(t -> {
            UpdateThirdProjectStateReqDTO updateThirdProjectStateReqDTO = new UpdateThirdProjectStateReqDTO();
            updateThirdProjectStateReqDTO.setState(0);
            updateThirdProjectStateReqDTO.setThirdCostId(t.getThirdCostId());
            updateThirdProjectStateReqDTO.setCompanyId(t.getCompanyId());
            openProjectService.projectUpdateStatus(updateThirdProjectStateReqDTO);
        });
        return "success";
    }


    /**
     * 封装参数
     */
    public void packageUC(List<AddThirdProjectReqDTO> projectList, String companyId) {
        String admin = departmentService.superAdmin(companyId);
        projectList.forEach(project -> {
            project.setUserId(admin);
            project.setType(1);
            // 项目状态 1启用 0停用
            project.setState(1);
            // 过期后是否自动停用 1：不停用 2：停
            project.setExpiredState(1);
            // 使用范围 1不限2限制
            project.setUsableRange(2);
        });
    }

}
