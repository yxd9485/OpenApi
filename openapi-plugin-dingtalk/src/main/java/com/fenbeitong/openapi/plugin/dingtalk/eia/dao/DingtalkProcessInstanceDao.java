package com.fenbeitong.openapi.plugin.dingtalk.eia.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingtalkProcessInstance;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * <p>Title: DingtalkProcessInstanceDao</p>
 * <p>Description: 钉钉审批实例dao</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/24 8:21 PM
 */
@Component
public class DingtalkProcessInstanceDao extends OpenApiBaseDao<DingtalkProcessInstance> {

    public List<DingtalkProcessInstance> listInstanceByBusinessId(String businessId) {
        Example example = new Example(DingtalkProcessInstance.class);
        example.createCriteria().andEqualTo("businessId", businessId);
        return listByExample(example);
    }
}
