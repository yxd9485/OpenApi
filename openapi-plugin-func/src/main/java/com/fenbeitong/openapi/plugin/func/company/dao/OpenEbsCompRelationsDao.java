package com.fenbeitong.openapi.plugin.func.company.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.func.company.entity.OpenEbsCompRelations;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * <p>Title: OpenEbsCompRelationsDao</p>
 * <p>Description: 公司与法人映射关系dao</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/9/21 6:09 PM
 */
@Component
public class OpenEbsCompRelationsDao extends OpenApiBaseDao<OpenEbsCompRelations> {

    public List<OpenEbsCompRelations> list() {
        Example example = new Example(OpenEbsCompRelations.class);
        example.createCriteria().andCondition("1=1");
        return listByExample(example);
    }
}
