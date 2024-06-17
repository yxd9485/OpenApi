package com.fenbeitong.openapi.plugin.ecology.v8.standard.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflow;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * <p>Title: OpenEcologyWorkflowDao</p>
 * <p>Description: 泛微工作流dao</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/4/17 5:38 PM
 */
@Component
public class OpenEcologyWorkflowDao extends OpenApiBaseDao<OpenEcologyWorkflow> {

    public List<OpenEcologyWorkflow> findByCompanyIdAndRequestIdList(String companyId, List<String> requestIdList) {
        Example example = new Example(OpenEcologyWorkflow.class);
        example.selectProperties("id", "requestId");
        example.createCriteria().andEqualTo("companyId", companyId)
                .andIn("requestId", requestIdList);
        return listByExample(example);
    }

    public List<OpenEcologyWorkflow> findUnhandledWorkflowList(String companyId) {
        Example example = new Example(OpenEcologyWorkflow.class);
        example.createCriteria().andEqualTo("companyId", companyId)
                .andEqualTo("agreed", 0)
                .andEqualTo("state", 0);
        return listByExample(example);
    }
}
