package com.fenbeitong.openapi.plugin.ecology.v8.standard.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflowConfig;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

/**
 * <p>Title: OpenEcologyWorkflowConfigDao</p>
 * <p>Description: 泛微工作流配置dao</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/1 12:41 PM
 */
@Component
public class OpenEcologyWorkflowConfigDao extends OpenApiBaseDao<OpenEcologyWorkflowConfig> {

    public OpenEcologyWorkflowConfig findByCompanyId(String companyId) {
        Example example = new Example(OpenEcologyWorkflowConfig.class);
        example.createCriteria().andEqualTo("companyId", companyId);
        return getByExample(example);
    }
}
