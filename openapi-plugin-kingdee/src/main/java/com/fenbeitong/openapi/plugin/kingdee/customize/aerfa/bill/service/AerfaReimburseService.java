package com.fenbeitong.openapi.plugin.kingdee.customize.aerfa.bill.service;

import com.fenbeitong.openapi.plugin.kingdee.customize.aerfa.bill.dto.KingdeeSaveReimbursementDTO;

/**
 * @author helu
 * @date 2022/9/9
 * 阿尔法账单推送金蝶生成报销单
 */
public interface AerfaReimburseService {

    /**
     * 账单生成费用报销单
     *
     * @param companyId
     * @param billNo
     */
    void convertReimb(String companyId, String billNo);

    /**
     * 推送账单
     * @author helu
     * @date 2022/9/15 下午5:06
     * @param data 分组推送的账单数据
     * @param companyId 公司id
     * @return Object
     */
    Object pushReimb(KingdeeSaveReimbursementDTO data, String companyId);

}
