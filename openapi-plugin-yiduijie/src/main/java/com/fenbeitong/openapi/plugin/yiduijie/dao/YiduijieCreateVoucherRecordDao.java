package com.fenbeitong.openapi.plugin.yiduijie.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.yiduijie.entity.YiduijieCreateVoucherRecord;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * <p>Title: YiduijieCreateVoucherRecordDao</p>
 * <p>Description: 易对接生成</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/16 6:09 PM
 */
@Component
public class YiduijieCreateVoucherRecordDao extends OpenApiBaseDao<YiduijieCreateVoucherRecord> {

    public YiduijieCreateVoucherRecord getByBusinessTypeAndBatchId(String businessType, String batchId) {
        List<YiduijieCreateVoucherRecord> voucherRecordList = getByBatchId(batchId);
        return voucherRecordList == null ? null : voucherRecordList.stream().filter(r -> businessType.equals(r.getBusinessType())).findFirst().orElse(null);
    }

    public List<YiduijieCreateVoucherRecord> getByBatchId( String batchId) {
        Example example = new Example(YiduijieCreateVoucherRecord.class);
        example.createCriteria().andEqualTo("batchId", batchId);
        return listByExample(example);
    }
}
