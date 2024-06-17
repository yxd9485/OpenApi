package com.fenbeitong.openapi.plugin.func.company.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.func.company.entity.OpenEbsCoeCostItems;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * <p>Title: OpenEbsCoeCostItemsDao</p>
 * <p>Description: 费用用途与会计科目dao</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/9/21 5:33 PM
 */
@Component
public class OpenEbsCoeCostItemsDao extends OpenApiBaseDao<OpenEbsCoeCostItems> {

    public List<OpenEbsCoeCostItems> list() {
        Example example = new Example(OpenEbsCoeCostItems.class);
        example.createCriteria().andCondition("1=1");
        return listByExample(example);
    }
}
