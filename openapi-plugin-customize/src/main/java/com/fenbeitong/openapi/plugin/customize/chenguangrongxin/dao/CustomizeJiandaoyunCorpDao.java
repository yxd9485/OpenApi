package com.fenbeitong.openapi.plugin.customize.chenguangrongxin.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.entity.CustomizeJiandaoyunCorp;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName CustomizeJiandaoyunCorpDao
 * @Description
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/9/4
 **/
@Component
public class CustomizeJiandaoyunCorpDao extends OpenApiBaseDao<CustomizeJiandaoyunCorp> {
    /**
     * 使用map条件查询
     *
     * @param condition 查询条件
     * @return CustomizeJiandaoyunCorp 简道云配置表
     */
    public CustomizeJiandaoyunCorp getCorp(Map<String, Object> condition) {
        Example example = new Example(CustomizeJiandaoyunCorp.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

    /**
     * 根据公司id查询简道云配置
     * @param companyId 公司id
     * @return CustomizeJiandaoyunCorp 简道云配置表
     */
    public CustomizeJiandaoyunCorp getByCompanyId(String companyId) {
        if (StringUtils.isBlank(companyId)) {
            throw new OpenApiArgumentException("companyId为空");
        }
        Map<String, Object> condition = new HashMap<>();
        condition.put("companyId", companyId);
        return getCorp(condition);
    }
}
