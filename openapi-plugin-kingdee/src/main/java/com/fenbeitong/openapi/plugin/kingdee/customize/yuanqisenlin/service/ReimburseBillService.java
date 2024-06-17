package com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.service;

import com.fenbeitong.openapi.plugin.func.callback.dto.ReimburseBillDTO;
import com.fenbeitong.openapi.plugin.func.reimburse.dto.RemiDetailResDTO;

public interface ReimburseBillService {


    /**
     * 推送费用报销单
     *
     * @param data
     * @return
     */
    Object pushReimburseBill(RemiDetailResDTO data);
}
