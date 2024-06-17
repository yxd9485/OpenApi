package com.fenbeitong.openapi.plugin.ecology.v8.standard.dao;

import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyResturlConfig;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
/**
 * Created by zhangpeng on 2021/12/14.
 */
@Component
public class OpenEcologyResturlConfigDao extends OpenApiBaseDao<OpenEcologyResturlConfig> {

    public OpenEcologyResturlConfig findListOpenEcologyResturlConfig( String companyId ) {
        Example example = new Example(OpenEcologyResturlConfig.class);
        example.createCriteria().andEqualTo("companyId", companyId);
        return getByExample(example);
    }

    public OpenEcologyResturlConfig findOpenEcologyResturlConfigByCompanyIdAndType( String companyId , String type ) {
        Example example = new Example(OpenEcologyResturlConfig.class);
        example.createCriteria().andEqualTo("companyId", companyId);
        example.createCriteria().andEqualTo("type", type);
        return getByExample(example);
    }

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<OpenEcologyResturlConfig> listOpenEcologyResturlConfig(Map<String, Object> condition) {
        Example example = new Example(OpenEcologyResturlConfig.class);
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
    public OpenEcologyResturlConfig getOpenEcologyResturlConfig(Map<String, Object> condition) {
        Example example = new Example(OpenEcologyResturlConfig.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

    /**
     * 根据公司id查询rest api信息
     * @param companyId 公司id
     * @param type 接口类型类型
     * @return rest 配置信息
     */
    public OpenEcologyResturlConfig getRestConfigCompanyId(String companyId ) {
        Example example = new Example(OpenEcologyResturlConfig.class);
        example.createCriteria().andEqualTo("companyId", companyId);
        return getByExample(example);
    }

}
