package com.fenbeitong.openapi.plugin.seeyon.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOrgDepartment;
import com.fenbeitong.openapi.plugin.seeyon.mapper.SeeyonOrgDepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeeyonOrgDepartmentDao extends OpenApiBaseDao<SeeyonOrgDepartment> {
    @Autowired
    SeeyonOrgDepartmentMapper seeyonOrgDepartmentMapper;

    public List<SeeyonOrgDepartment> getDiffOrgAll(int newDays, int oldDays) {
        List<SeeyonOrgDepartment> diffAll = seeyonOrgDepartmentMapper.getDiffAll(newDays, oldDays);
        return diffAll;
    }
}
