package com.fenbeitong.openapi.plugin.kingdee.common.listener.impl;

import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeeK3CloudConfigDTO;
import com.fenbeitong.openapi.plugin.kingdee.common.listener.KingDeekBillBeforeListener;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenKingdeeUrlConfig;
import com.fenbeitong.openapi.plugin.support.callback.entity.OpenBillDetailRecord;
import com.fenbeitong.openapi.plugin.support.callback.entity.OpenThirdBillRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description 可配置化监听
 * @Author duhui
 * @Date 2020-11-26
 **/

@Service
@Slf4j
public class KingDeeBillBeforeDefaultListener implements KingDeekBillBeforeListener {

    @Override
    public void setThirdData(String companyId, String token, OpenBillDetailRecord openBillDetailRecord, OpenThirdBillRecord openThirdBillRecord, OpenKingdeeUrlConfig kingDeeUrlConfig, KingDeeK3CloudConfigDTO kingDee3KCloudConfigDTO, Object... objects) {
        log.info("KingDeeBillBeforeDefaultListener");
    }

    @Override
    public Map<String, Map<String, List<OpenThirdBillRecord>>> dataGroup(List<OpenThirdBillRecord> openThirdBillRecordList) {
        Map<String, Map<String, List<OpenThirdBillRecord>>> resData = new HashMap<>();
        Map<String, List<OpenThirdBillRecord>> listMap = openThirdBillRecordList.stream().filter(t -> !StringUtils.isEmpty(t.getField1()) && !StringUtils.isEmpty(t.getField3()) && !StringUtils.isEmpty(t.getField4()) && !StringUtils.isEmpty(t.getField5())).collect(Collectors.groupingBy(t -> t.getField1()));
        listMap.forEach((k, v) -> {
            resData.put(k, v.stream().collect(Collectors.groupingBy(t -> t.getField3().concat("_").concat(t.getField4()).concat("_").concat(t.getField5()))));

        });
        return resData;
    }

}
