package com.fenbeitong.openapi.plugin.feishu.common.dao;

import com.fenbeitong.openapi.plugin.feishu.common.entity.OpenFeishuGroupInfo;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
/**   
 * Created by zhangpeng on 2021/09/25.
 */
@Component
public class OpenFeishuGroupInfoDao extends OpenApiBaseDao<OpenFeishuGroupInfo> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<OpenFeishuGroupInfo> listOpenFeishuGroupInfo(Map<String, Object> condition) {
        Example example = new Example(OpenFeishuGroupInfo.class);
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
    public OpenFeishuGroupInfo getOpenFeishuGroupInfo(Map<String, Object> condition) {
        Example example = new Example(OpenFeishuGroupInfo.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

    /**
     * 根据companyId查询企业配置
     */
    public List<OpenFeishuGroupInfo> getByCompanyId(String companyId) {
        Example example = new Example(OpenFeishuGroupInfo.class);
        example.createCriteria().andEqualTo("companyId", companyId);
        return listByExample(example);
    }

}
