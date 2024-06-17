package com.fenbeitong.openapi.plugin.seeyon.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOrgDepartment;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOrgEmployee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Mapper
public interface SeeyonOrgEmpMapper extends OpenApiBaseMapper<SeeyonOrgEmployee> {

    @Select(
            "select ID from seeyon_org_employee old where not exists ("
                    + "select ID from seeyon_org_employee new "
                    + "where (new.ID = old.ID) "
                    + "and (DATE_FORMAT(new.SEEYON_DATA_TIME, \"%Y-%m-%d\") = date_sub(curdate(), interval ${newDays} day))) "
                    + "and DATE_FORMAT(old.SEEYON_DATA_TIME, \"%Y-%m-%d\") = date_sub(curdate(), interval ${oldDays} day)")
    List<SeeyonOrgEmployee> getDiffEmpAll(@Param("newDays") int newDays, @Param("oldDays") int oldDays);
}
