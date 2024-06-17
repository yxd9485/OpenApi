package com.fenbeitong.openapi.plugin.kingdee.common.listener;


import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeeK3CloudConfigDTO;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenKingdeeUrlConfig;
import com.fenbeitong.openapi.plugin.support.callback.entity.OpenBillDetailRecord;
import com.fenbeitong.openapi.plugin.support.callback.entity.OpenThirdBillRecord;

import java.util.List;
import java.util.Map;

/**
 * @Description 金蝶监听类
 * @Author duhui
 * @Date 2020-12-01
 **/
public interface KingDeekBillBeforeListener {

    /**
     * 获取三方数据前置监听 获取金蝶部门编码
     */
    void setThirdData(String companyId, String token, OpenBillDetailRecord openBillDetailRecord, OpenThirdBillRecord openThirdBillRecord, OpenKingdeeUrlConfig kingDeeUrlConfig, KingDeeK3CloudConfigDTO kingDee3KCloudConfigDTO, Object... objects);

    /**
     * 账单推送数据分组归类
     */
    Map<String, Map<String, List<OpenThirdBillRecord>>> dataGroup(List<OpenThirdBillRecord> openThirdBillRecordList);

}
