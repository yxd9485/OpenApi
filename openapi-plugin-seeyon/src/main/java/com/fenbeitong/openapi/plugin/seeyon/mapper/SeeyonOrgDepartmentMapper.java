package com.fenbeitong.openapi.plugin.seeyon.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOrgDepartment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper 接口
 *
 * @author Ivan
 * @since 2019-03-05
 */
@Component
@Mapper
public interface SeeyonOrgDepartmentMapper extends OpenApiBaseMapper<SeeyonOrgDepartment> {
    @Select(
            "select ID,PATH from seeyon_org_dept old where not exists ("
                    + "select ID from seeyon_org_dept new "
                    + "where (new.ID = old.ID) "
                    + "and (DATE_FORMAT(new.SEEYON_DATA_TIME, \"%Y-%m-%d\") = date_sub(curdate(), interval ${newDays} day))) "
                    + "and DATE_FORMAT(old.SEEYON_DATA_TIME, \"%Y-%m-%d\") = date_sub(curdate(), interval ${oldDays} day)")
    List<SeeyonOrgDepartment> getDiffAll(
            @Param("newDays") int newDays, @Param("oldDays") int oldDays);
}
