package com.fenbeitong.openapi.plugin.customize.saikekanglun.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: FinanceVoucherSkklUtils </p>
 * <p>Description: 赛科康伦凭证生成定制化工具类 </p>
 * <p>Company:  www.fenbeitong.com</p>
 *
 * @author: haoqiang.wang
 * @Date: 2021/7/21 下午7:27
 **/
public class FinanceVoucherSkklUtils {

    /**
     * 赛科康伦增加额外三条记录
     * @param tgtList
     * @return
     */
    public static void addExtendRecord(List<Map<String, Object>> tgtList){
        //金额固定显示
        BigDecimal amount = new BigDecimal("100.00");
        //最后一行记录为贷方信息
        Map<String, Object> creditRecord = tgtList.get(tgtList.size()-1);
        //增加三条记录
        Map<String, Object> firstRecord = new HashMap<>();
        firstRecord.putAll(creditRecord);
        firstRecord.put("deptCode", "");
        firstRecord.put("projectCode", "");
        firstRecord.put("employeeCode", "");
        firstRecord.put("credit", amount);
        firstRecord.put("supplierCode", "0500");
        firstRecord.put("summary", "其他应收款/单位往来-北京中科康仑环境你科技研究院有限公司");
        tgtList.add(firstRecord);

        Map<String, Object> secondRecord = new HashMap<>();
        secondRecord.putAll(firstRecord);
        secondRecord.put("supplierCode", "0182");
        secondRecord.put("summary", "其他应收款/单位往来-鞍山康盛环保科技有限公司");
        tgtList.add(secondRecord);

        Map<String, Object> thirdRecord = new HashMap<>();
        thirdRecord.putAll(firstRecord);
        thirdRecord.put("supplierCode", "0463");
        thirdRecord.put("summary", "其他应收款/单位往来-河南中科康仑环保科技有限公司");
        tgtList.add(thirdRecord);
    }
}
