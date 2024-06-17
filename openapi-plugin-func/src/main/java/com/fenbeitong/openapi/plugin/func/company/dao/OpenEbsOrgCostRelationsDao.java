package com.fenbeitong.openapi.plugin.func.company.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.func.company.entity.OpenEbsOrgCostRelations;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * <p>Title: OpenEbsOrgCostRelationsDao</p>
 * <p>Description: 部门与成本中心映射关系dao</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/9/22 10:24 AM
 */
@Component
public class OpenEbsOrgCostRelationsDao extends OpenApiBaseDao<OpenEbsOrgCostRelations> {

    public List<OpenEbsOrgCostRelations> list() {
        Example example = new Example(OpenEbsOrgCostRelations.class);
        example.createCriteria().andCondition("1=1");
        return listByExample(example);
    }

    public OpenEbsOrgCostRelations getByPsDeptId(String psDeptId) {
        Example example = new Example(OpenEbsOrgCostRelations.class);
        example.createCriteria().andEqualTo("psDeptId", psDeptId);
        return getByExample(example);
    }
}
