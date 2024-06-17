package com.fenbeitong.openapi.plugin.dingtalk.eia.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingtalkRoute;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

/**
 * <p>Title: DingtalkRouteDao</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/16 11:41 AM
 */
@Component
public class DingtalkRouteDao extends OpenApiBaseDao<DingtalkRoute> {

    public DingtalkRoute getRouteByCorpId(String corpId) {
        Example example = new Example(DingtalkRoute.class);
        example.createCriteria().andEqualTo("corpId", corpId);
        return getByExample(example);
    }
}
