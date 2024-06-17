package com.fenbeitong.openapi.plugin.kingdee.common.listener.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.kingdee.common.constant.KingdeeNvlCostEnum;
import com.fenbeitong.openapi.plugin.kingdee.common.listener.impl.KingDeeDefaultListener;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description 可配置化监听
 * @Author duhui
 * @Date 2020-11-26
 **/

@Service
@Slf4j
public class KingDeeProcurementListener extends KingDeeDefaultListener {
    @Autowired
    OpenSysConfigDao openSysConfigDao;

    /**
     * 税率
     */
    private String fEntryTaxRate;

    /**
     * 费用项目
     */
    private String fChargeProjectID;


    @Override
    public String saveParse(String data, String companyId, Object... objects) {
        Map<String, String> map = (Map<String, String>) objects[0];
        String reason = (String) MapUtils.getValueByExpress(map, "saas_info:remark:reason");
        String[] config = getconfigData(companyId, reason);
        if (!ObjectUtils.isEmpty(map)) {
            // 采购日期
            String FDate = (String) MapUtils.getValueByExpress(map, "order_info:create_time");
            String FPurchaseOrgId = ((List) MapUtils.getValueByExpress(map, "saas_info:custom_ext_list:custom_field_content")).get(0).toString();
            if (!StringUtils.isBlank(FPurchaseOrgId)) {
                FPurchaseOrgId = FPurchaseOrgId.split("_")[1];
            }
            data = String.format(data, config[0], config[1], FDate, FPurchaseOrgId);
        }
        return data;
    }


    @Override
    public void setList(String dataKey, String dataValue, String companyId, StringBuffer strData, Object... objects) {
        Map<String, String> map = (Map<String, String>) objects[0];
        String reason = (String) MapUtils.getValueByExpress(map, "saas_info:remark:reason");
        String[] config = getconfigData(companyId, reason);
        if (!ObjectUtils.isEmpty(map)) {
            // 采购日期
            String FDate = (String) MapUtils.getValueByExpress(map, "order_info:create_time");
            String totalPrice = StringUtils.obj2str(MapUtils.getValueByExpress(map, "price_info:company_total_pay"));
            String redEnvelope = StringUtils.obj2str(MapUtils.getValueByExpress(map, "price_info:red_envelope"));
            // 采购中的快递费
            String freight = StringUtils.obj2str(MapUtils.getValueByExpress(map,"price_info:freight"));
            String companyActualPay = StringUtils.obj2str(new BigDecimal(totalPrice).subtract(new BigDecimal(redEnvelope)));
            if (!ObjectUtils.isEmpty(map.get("product_info"))) {
                List<Map<String, String>> mapList = JsonUtils.toObj(JsonUtils.toJson(map.get("product_info")), new TypeReference<List<Map<String, String>>>() {
                });
                strData.insert(0, "\"" + dataKey + "\"" + ":[");
                // 税率
                mapList.forEach(t -> {
                    // 描述
                    String FMaterialDesc = t.get("name");
                    // 价格
                    String budget = String.format(dataValue,
                        config[2],
                        FMaterialDesc,
                        FDate,
                        companyActualPay,
                        KingdeeNvlCostEnum.OFFICE.getRate(),
                        KingdeeNvlCostEnum.OFFICE.getCode()
                    );
                    strData.append(budget).append(",");
                });

                // 描述
                if (StringUtils.isNotBlank(freight)) {
                    // 价格
                    String budget = String.format(dataValue,
                        config[2],
                        KingdeeNvlCostEnum.EXPRESS.getName(),
                        FDate,
                        freight,
                        KingdeeNvlCostEnum.EXPRESS.getRate(),
                        KingdeeNvlCostEnum.EXPRESS.getCode()
                    );
                    strData.append(budget).append(",");
                }
                strData.deleteCharAt(strData.length() - 1);
                strData.append("]");
            }
            ;
        }
    }

    public String[] getconfigData(String companyId, String flag) {
        String data = getOpenSysConfig(OpenSysConfigType.KINGDEE_PROCUREMENT_CONFIG.getType(), companyId);
        Map map = JsonUtils.toObj(data, Map.class);
        if (!ObjectUtils.isEmpty(map)) {
            return map.get(flag).toString().split(",");
        }
        return null;
    }

}
