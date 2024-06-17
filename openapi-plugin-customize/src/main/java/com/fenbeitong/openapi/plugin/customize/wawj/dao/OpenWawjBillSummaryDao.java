package com.fenbeitong.openapi.plugin.customize.wawj.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.customize.wawj.entity.OpenWawjBillSummary;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * <p>Title: OpenWawjBillSumarryDao</p>
 * <p>Description: 我爱我家账单汇总表</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/11/18 10:07 AM
 */
@Component
public class OpenWawjBillSummaryDao extends OpenApiBaseDao<OpenWawjBillSummary> {

    public List<OpenWawjBillSummary> listByBatchId(List<String> batchIdList) {
        Example example = new Example(OpenWawjBillSummary.class);
        example.createCriteria().andIn("batchId", batchIdList)
                .andIn("status", Lists.newArrayList(-1, 1));
        return listByExample(example);
    }

    public int getCount(String billNo) {
        Example example = new Example(OpenWawjBillSummary.class);
        example.createCriteria().andEqualTo("billNo", billNo);
        return count(example);
    }
}
