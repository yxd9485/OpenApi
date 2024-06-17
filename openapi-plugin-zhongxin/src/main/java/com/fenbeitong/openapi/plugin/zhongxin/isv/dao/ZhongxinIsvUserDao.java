package com.fenbeitong.openapi.plugin.zhongxin.isv.dao;

import com.fenbeitong.openapi.plugin.zhongxin.isv.entity.ZhongxinIsvUser;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by wanghaoqiang on 2021/04/22.
 */
@Component
public class ZhongxinIsvUserDao extends OpenApiBaseDao<ZhongxinIsvUser> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<ZhongxinIsvUser> listZhongxinIsvUser(Map<String, Object> condition) {
        Example example = new Example(ZhongxinIsvUser.class);
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
    public ZhongxinIsvUser getZhongxinIsvUser(Map<String, Object> condition) {
        Example example = new Example(ZhongxinIsvUser.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

    /**
     * 通过hash值获取用户信息
     * @param hash
     * @return
     */
    public ZhongxinIsvUser getZhongxinIsvUserByHash(String hash) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("hash", hash);
        return getZhongxinIsvUser(condition);
    }

    /**
     * 通过手机号查询用户信息
     * @param phoneNum
     * @param companyId
     * @return
     */
    public ZhongxinIsvUser getZhongxinIsvUserByPhoneNum(String phoneNum, String companyId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("phoneNum", phoneNum);
        condition.put("companyId", companyId);
        return getZhongxinIsvUser(condition);
    }
}
