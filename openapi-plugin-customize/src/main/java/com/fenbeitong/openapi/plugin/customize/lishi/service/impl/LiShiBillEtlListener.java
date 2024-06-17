package com.fenbeitong.openapi.plugin.customize.lishi.service.impl;

import com.fenbeitong.openapi.plugin.etl.entity.OpenEtlMappingConfig;
import com.fenbeitong.openapi.plugin.etl.entity.OpenEtlObjectConfig;
import com.fenbeitong.openapi.plugin.etl.service.impl.DefaultEtlListener;
import com.fenbeitong.openapi.plugin.support.common.constant.OrderCategoryEnum;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.Map;

/**
 * <p>Title: LiShiOrderEtlListener</p>
 * <p>Description: 理士订单etl监听</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/5/11 5:05 PM
 */
@ServiceAspect
@Service
public class LiShiBillEtlListener extends DefaultEtlListener {

    @Override
    public void afterTransform(Map<String, Object> srcMap, Map transformMap) {
//        if (srcMap.get("orderCategory") != null ) {
//            transformMap.put("type_name", OrderCategoryEnum.getValueByKey((Integer) srcMap.get("orderCategory")));
//        }
//        transformMap.put("moneytary_unit","元");
    }

    @Override
    public void afterSetItemValue(Map<String, Object> srcMap, Map<String, Object> objectMap, OpenEtlObjectConfig objectConfig, OpenEtlMappingConfig mappingConfig, String srcCol, String tgtCol, String group, Object srcValue) {
        if ("type_name".equals(tgtCol)) {
            if (group != null) {
                Map groupMap = (Map) objectMap.get(group);
                groupMap.put(tgtCol, OrderCategoryEnum.getValueByKey((Integer) srcValue));
            } else {
                objectMap.put(tgtCol, OrderCategoryEnum.getValueByKey((Integer) srcValue));
            }
        } else if ("monetary_unit".equals(tgtCol)) {
            if (group != null) {
                Map groupMap = (Map) objectMap.get(group);
                groupMap.put(tgtCol, "CNY");
            } else {
                objectMap.put(tgtCol, "CNY");
            }

        }
    }
}
