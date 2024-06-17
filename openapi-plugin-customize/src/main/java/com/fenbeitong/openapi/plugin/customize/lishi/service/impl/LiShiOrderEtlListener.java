package com.fenbeitong.openapi.plugin.customize.lishi.service.impl;

import com.fenbeitong.openapi.plugin.etl.entity.OpenEtlMappingConfig;
import com.fenbeitong.openapi.plugin.etl.service.impl.DefaultEtlListener;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title: LiShiOrderEtlListener</p>
 * <p>Description: 理士订单etl监听</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/11 5:05 PM
 */
@ServiceAspect
@Service
public class LiShiOrderEtlListener extends DefaultEtlListener {

    @Override
    public List<OpenEtlMappingConfig> filterMappingConfigList(List<OpenEtlMappingConfig> mappingConfigList, Map<String, Object> srcMap) {
        //1:机票;3:火车;4:酒店
        int type = NumericUtils.obj2int(srcMap.get("type"));
        String groupName = type == 1 ? "air" : type == 3 ? "train" : "hotel";
        return mappingConfigList.stream().filter(mc -> groupName.equals(mc.getGroupName())).collect(Collectors.toList());
    }
}
