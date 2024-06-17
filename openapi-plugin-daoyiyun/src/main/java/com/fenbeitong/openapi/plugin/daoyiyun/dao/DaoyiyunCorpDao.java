package com.fenbeitong.openapi.plugin.daoyiyun.dao;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.daoyiyun.entity.DaoyiyunCorp;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by lizhen on 2022/06/02.
 */
@Component
public class DaoyiyunCorpDao extends OpenApiBaseDao<DaoyiyunCorp> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<DaoyiyunCorp> listDaoyiyunCorp(Map<String, Object> condition) {
        Example example = new Example(DaoyiyunCorp.class);
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
    public DaoyiyunCorp getDaoyiyunCorp(Map<String, Object> condition) {
        Example example = new Example(DaoyiyunCorp.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

    /**
     * applicationId查询配置
     * @param applicationId
     * @return
     */
    public DaoyiyunCorp getByApplicationId(String applicationId) {
        if (StringUtils.isBlank(applicationId)) {
            throw new OpenApiArgumentException("【applicationId】为空");
        }
        Map<String, Object> condition = new HashMap<>();
        condition.put("applicationId", applicationId);
        DaoyiyunCorp corpConfigInfo = getDaoyiyunCorp(condition);
        if (corpConfigInfo == null) {
            throw new OpenApiArgumentException("企业配置不存在");
        }
        return corpConfigInfo;
    }

    public DaoyiyunCorp getByCompanyId(String companyId) {
        if (StringUtils.isBlank(companyId)) {
            throw new OpenApiArgumentException("【companyId】为空");
        }
        Map<String, Object> condition = new HashMap<>();
        condition.put("companyId", companyId);
        DaoyiyunCorp corpConfigInfo = getDaoyiyunCorp(condition);
        if (corpConfigInfo == null) {
            throw new OpenApiArgumentException("企业配置不存在");
        }
        return corpConfigInfo;
    }

}
