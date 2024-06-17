package com.fenbeitong.openapi.plugin.zhongxin.isv.dao;

import cn.hutool.core.collection.CollectionUtil;
import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;
import com.fenbeitong.openapi.plugin.zhongxin.isv.entity.ZhongxinIsvCompany;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ZhongxinIsvCompanyDao extends OpenApiBaseDao<ZhongxinIsvCompany> {

    /**
     * 使用map条件查询
     *
     * @param condition
     * @return
     */
    public List<ZhongxinIsvCompany> listZhongxinIsvCompany(Map<String, Object> condition) {
        Example example = new Example(ZhongxinIsvCompany.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return listByExample(example);
    }

    /**
     * 使用map条件查询
     *
     * @param condition
     * @return
     */
    public ZhongxinIsvCompany getZhongxinIsvCompany(Map<String, Object> condition) {
        Example example = new Example(ZhongxinIsvCompany.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }


    /**
     * corpId查ZhongxinIsvCompany
     *
     * @param corpId
     * @return
     */
    public ZhongxinIsvCompany getZhongxinIsvCompanyByCorpId(String corpId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("corpId", corpId);
        return getZhongxinIsvCompany(condition);
    }

    /**
     * companyId查ZhongxinIsvCompany
     *
     * @param companyId
     * @return
     */
    public ZhongxinIsvCompany getZhongxinIsvCompanyByCompanyId(String companyId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("companyId", companyId);
        return getZhongxinIsvCompany(condition);
    }


    /**
     * 获取所有的公司
     *
     * @return
     */
    public List<ZhongxinIsvCompany> getFeiShuIsvAllCompany() {
        Map<String, Object> condition = new HashMap<>();
        condition.put("state", "0");
        return listZhongxinIsvCompany(condition);
    }

    /**
     * 根据创建时间和公司id查询
     *
     * @return
     */
    public List<ZhongxinIsvCompany> getFeiShuIsvByIdAndTime(List<String> companyIds, String createTimeBegin, String createTimeEnd) {
        Example example = new Example(ZhongxinIsvCompany.class);
        Example.Criteria criteria = example.createCriteria();
        if (CollectionUtil.isNotEmpty(companyIds)) {
            criteria.andIn("companyId", companyIds);
        }
        if (StringUtil.isNotEmpty(createTimeBegin) || StringUtil.isNotEmpty(createTimeEnd)) {
            criteria.andBetween("createTime", createTimeBegin, createTimeEnd);
        }
        return listByExample(example);
    }
}
