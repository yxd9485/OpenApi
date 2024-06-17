package com.fenbeitong.openapi.plugin.func.company.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.func.company.entity.OpenCompanyBillExtConfig;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * <p>Title: OpenCompanyBillExtConfigDao</p>
 * <p>Description: 公司账单扩展信息dao</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/3 4:53 PM
 */
@Component
public class OpenCompanyBillExtConfigDao extends OpenApiBaseDao<OpenCompanyBillExtConfig> {

    public List<OpenCompanyBillExtConfig> getByType(int type) {
        Example example = new Example(OpenCompanyBillExtConfig.class);
        example.createCriteria().andEqualTo("type", type);
        return listByExample(example);
    }
}
