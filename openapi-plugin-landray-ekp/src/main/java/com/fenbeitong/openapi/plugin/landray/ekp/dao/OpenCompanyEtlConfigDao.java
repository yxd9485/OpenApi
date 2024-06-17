//package com.fenbeitong.openapi.plugin.landray.ekp.dao;
//
//import com.fenbeitong.openapi.plugin.landray.ekp.entity.OpenCompanyEtlConfig;
//
//import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
//import org.springframework.stereotype.Component;
import org.springframework.stereotype.Component;
//import tk.mybatis.mapper.entity.Example;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
///**
// * Created by zhangpeng on 2021/08/06.
// */
//@ServiceAspect
//@Component
//public class OpenCompanyEtlConfigDao extends OpenApiBaseDao<OpenCompanyEtlConfig> {
//
//    /**
//     * 使用map条件查询
//     * @param condition
//     * @return
//     */
//    public List<OpenCompanyEtlConfig> listOpenCompanyEtlConfig(Map<String, Object> condition) {
//        Example example = new Example(OpenCompanyEtlConfig.class);
//        Example.Criteria criteria = example.createCriteria();
//        for (String key : condition.keySet()) {
//            criteria.andEqualTo(key, condition.get(key));
//        }
//        return listByExample(example);
//    }
//
//    /**
//     * 使用map条件查询
//     * @param condition
//     * @return
//     */
//    public OpenCompanyEtlConfig getOpenCompanyEtlConfig(Map<String, Object> condition) {
//        Example example = new Example(OpenCompanyEtlConfig.class);
//        Example.Criteria criteria = example.createCriteria();
//        for (String key : condition.keySet()) {
//            criteria.andEqualTo(key, condition.get(key));
//        }
//        return getByExample(example);
//    }
//
//    public OpenCompanyEtlConfig getOpenLandrayFormConfigByCompany(String companyId,String bussinessType) {
//        Map<String, Object> condition = new HashMap<>();
//        condition.put("companyId", companyId);
//        condition.put("bussinessType", bussinessType);
//        return getOpenCompanyEtlConfig(condition);
//    }
//
//}
