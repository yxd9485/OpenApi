package com.fenbeitong.openapi.plugin.customize.zhiou.dao;

import com.fenbeitong.openapi.plugin.customize.zhiou.entity.OpenLandrayEkpConfig;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName LandrayEkpConfigMapper
 * @Description
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/8/31
 **/
@Component
public class LandrayEkpConfigDao extends OpenApiBaseDao<OpenLandrayEkpConfig> {

    /**
     * 使用map条件查询
     *
     * @param condition 查询条件
     * @return OpenLandrayEkpConfig 蓝凌配置表
     */
    public OpenLandrayEkpConfig getOpenLandrayEkpConfig(Map<String, Object> condition) {
        Example example = new Example(OpenLandrayEkpConfig.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

    /**
     * 根据公司id查询蓝凌配置
     * @param companyId 公司id
     * @return OpenLandrayEkpConfig 蓝凌配置表
     */
    public OpenLandrayEkpConfig getByCompanyId(String companyId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("companyId", companyId);
        return getOpenLandrayEkpConfig(condition);
    }
}
