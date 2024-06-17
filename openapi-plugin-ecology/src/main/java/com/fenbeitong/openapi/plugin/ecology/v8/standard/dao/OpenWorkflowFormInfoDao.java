package com.fenbeitong.openapi.plugin.ecology.v8.standard.dao;

import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflow;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenWorkflowFormInfo;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
/**
 * 泛微工作流表单配置信息
 * Created by zhangpeng on 2021/06/04.
 */
@Component
public class OpenWorkflowFormInfoDao extends OpenApiBaseDao<OpenWorkflowFormInfo> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<OpenWorkflowFormInfo> listOpenWorkflowFormInfo(Map<String, Object> condition) {
        Example example = new Example(OpenWorkflowFormInfo.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return listByExample(example);
    }

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public OpenWorkflowFormInfo getOpenWorkflowFormInfo(Map<String, Object> condition) {
        Example example = new Example(OpenWorkflowFormInfo.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

    /**
     * 根据公司id查询表单信息
     * @param companyId 公司id
     * @param type 场景类型
     * @return
     */
    public OpenWorkflowFormInfo getFormInfoByCompanyIdAndType(String companyId , String type) {
        Example example = new Example(OpenWorkflowFormInfo.class);
        example.createCriteria().andEqualTo("companyId", companyId)
        .andEqualTo("approveType",type);
        return getByExample(example);
    }

}
