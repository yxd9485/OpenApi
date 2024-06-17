package com.fenbeitong.openapi.plugin.kingdee.common.listener.impl;

import com.fenbeitong.openapi.plugin.kingdee.common.listener.impl.KingDeeDefaultListener;
import com.fenbeitong.openapi.plugin.support.voucher.entity.FinanceVoucherData;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description 可配置化监听
 * @Author duhui
 * @Date 2020-11-26
 **/

@Service
public class KingDeeVoucherListener extends KingDeeDefaultListener {

    @Override
    public String saveParse(String data,String companyId, Object... params) {
        return data;
    }

    @Override
    public void setList(String dataKey, String dataValue, String companyId, StringBuffer strData, Object... objects) {
        List<FinanceVoucherData> list = (List<FinanceVoucherData>) objects[0];
        strData.insert(0, "\"" + dataKey + "\"" + ":[");
        list.forEach(t -> {
            String budget = String.format(dataValue,
                    "测试数据",
                    t.getTotalAmount(),
                    t.getDebitAmount() == null ? 0 : t.getDebitAmount(),
                    t.getCreditAmount() == null ? 0 : t.getCreditAmount());
            strData.append(budget).append(",");
        });
        strData.deleteCharAt(strData.length() - 1);
        strData.append("]");
    }


}
