package com.fenbeitong.openapi.plugin.voucher.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.voucher.entity.OpenVoucherDraft;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * <p>Title: OpenVoucherDraftDao</p>
 * <p>Description: 凭证草稿箱dao</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/29 4:10 PM
 */
@Component
public class OpenVoucherDraftDao extends OpenApiBaseDao<OpenVoucherDraft> {

    public List<OpenVoucherDraft> listByBatchId(String batchId) {
        Example example = new Example(OpenVoucherDraft.class);
        example.createCriteria().andEqualTo("batchId", batchId)
                .andEqualTo("status", 1);
        return listByExample(example);
    }
}
