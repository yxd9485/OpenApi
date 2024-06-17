package com.fenbeitong.openapi.plugin.feishu.common.util;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 飞书出差控件日期精确到上午下午，当结束时间选择下午时，查询到的数据为第二天零点
 * @author xiaohai
 * @date 2022/01/26
 */
@Slf4j
public class FeiShuParseTimeUtils {

    /**
     * 当结束时间为零点时，日期取前一天
     * @param end ：结束时间
     * @return
     */
    public static String getEndTime(String end){
        String endTime = end.substring(0, end.indexOf("T"));
        try {
            String hour = end.substring(end.indexOf("T")+1, end.indexOf("T") + 3);
            if("00".equals(hour)){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(endTime);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                date = calendar.getTime();
                return sdf.format(date);
            }
        } catch (ParseException e) {
           log.warn("日期解析出错！！！！" );
        }
       return endTime;
    }
}

