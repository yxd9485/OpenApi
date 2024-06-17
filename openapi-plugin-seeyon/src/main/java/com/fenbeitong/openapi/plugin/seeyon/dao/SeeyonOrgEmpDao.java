package com.fenbeitong.openapi.plugin.seeyon.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOrgDepartment;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOrgEmployee;
import com.fenbeitong.openapi.plugin.seeyon.mapper.SeeyonOrgEmpMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class SeeyonOrgEmpDao extends OpenApiBaseDao<SeeyonOrgEmployee> {
    @Autowired
    SeeyonOrgEmpMapper seeyonOrgEmpMapper;

    public List<SeeyonOrgEmployee> getDiffEmpAll(int newDays, int oldDays) {
        return seeyonOrgEmpMapper.getDiffEmpAll(newDays, oldDays);
    }
}
