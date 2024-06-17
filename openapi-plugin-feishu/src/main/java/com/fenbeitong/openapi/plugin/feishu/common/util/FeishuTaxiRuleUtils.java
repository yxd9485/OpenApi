package com.fenbeitong.openapi.plugin.feishu.common.util;

import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.sdk.dto.common.TypeEntity;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: xiaohai
 * @Date: 2022/8/5 上午10:26
 */

public class FeishuTaxiRuleUtils {

    /**
     *
     * @param carUseCount : 用车次数
     * @param carAmount ：用车金额
     * @return
     */
    public static List<TypeEntity>  buildUseCarRuleList(String carUseCount , String carAmount , Map<String, Object> queryDetail) {
        List<TypeEntity> ruleList = CollectionUtils.newArrayList();
        Object pricelimitFlag = queryDetail.get("price_limit_flag");
        if(StringUtils.isBlank(carUseCount) && pricelimitFlag.equals(2)){
            ruleList.add( TypeEntity.builder().type("times_limit_flag").value(0).build() );
        }
        if(StringUtils.isBlank(carAmount) && pricelimitFlag.equals(2)){
            ruleList.add( TypeEntity.builder().type("price_limit_flag").value(0).build() );
        }
        if(StringUtils.isNotBlank(carAmount)){
            ruleList.add( TypeEntity.builder().type("price_limit_flag").value(2).build() );
            ruleList.add( TypeEntity.builder().type("total_price").value(carAmount).build() );
        }
        if(StringUtils.isNotBlank(carUseCount)){
            ruleList.add( TypeEntity.builder().type("times_limit_flag").value(2).build() );
            ruleList.add( TypeEntity.builder().type("times_limit").value(carUseCount).build() );
        }
        return ruleList;
    }

}
