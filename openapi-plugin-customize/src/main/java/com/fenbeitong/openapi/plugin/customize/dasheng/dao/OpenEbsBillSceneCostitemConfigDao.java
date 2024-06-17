package com.fenbeitong.openapi.plugin.customize.dasheng.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.customize.dasheng.entity.OpenEbsBillSceneCostitemConfig;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * <p>Title: OpenEbsBillSceneCostitemConfigDao</p>
 * <p>Description: 51talk场景对应费用类型</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/10/19 8:13 PM
 */
@Component
public class OpenEbsBillSceneCostitemConfigDao extends OpenApiBaseDao<OpenEbsBillSceneCostitemConfig> {

    public List<OpenEbsBillSceneCostitemConfig> list() {
        Example example = new Example(OpenEbsBillSceneCostitemConfig.class);
        example.createCriteria().andCondition("1=1");
        return listByExample(example);
    }
}
