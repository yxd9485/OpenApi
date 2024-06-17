package com.fenbeitong.openapi.plugin.feishu.eia.listener;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuPlugIn;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyTrip;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: DingTalkCommon</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-11-10 13:28
 */
@Slf4j
public abstract class AbstractFeiShuEiaCommon {


    public String textareaMap(Map<String, String> map) {
        return map.get(FeiShuPlugIn.field.VALUE) == null ? null : map.get(FeiShuPlugIn.field.VALUE).toString();
    }

    public String addressMap(Map<String, String> map) {
        String address = map.get(FeiShuPlugIn.field.VALUE) == null ? null : map.get(FeiShuPlugIn.field.VALUE).toString();
        if (!ObjectUtils.isEmpty(address)) {
            String[] grop = address.split("/");
            if (grop.length >= 3) {
                return grop[2];
            } else if (grop.length == 2) {
                return grop[1];
            }
        }
        return null;
    }

    public String addressString(String address) {
        if (!ObjectUtils.isEmpty(address)) {
            String[] grop = address.split("/");
            if (grop.length >= 3) {
                return grop[2];
            } else {
                return null;
            }
        }
        return null;
    }

    public Map<String, String> dateInterval(Map<String, String> map) {
        Map<String, String> map1 = JsonUtils.toObj(JsonUtils.toJson(map.get(FeiShuPlugIn.field.VALUE)), Map.class);
        map1.put(FeiShuPlugIn.field.START, map1.get(FeiShuPlugIn.field.START).substring(0, 10));
        map1.put(FeiShuPlugIn.field.END, map1.get(FeiShuPlugIn.field.END).substring(0, 10));
        return map1;
    }


    /**
     * 截取时间
     */
    public String getDate(String data) {
        if (!ObjectUtils.isEmpty(data)) {
            return data.substring(0, 10);
        } else {
            throw new FinhubException(1, "时间解析错误");
        }

    }

    /**
     * 生成行程
     */
    public void setTripList(Integer[] type, CommonApplyTrip oldCommonApplyTrip, List<CommonApplyTrip> tripList) {
        Arrays.asList(type).forEach(t -> {
            CommonApplyTrip commonApplyFromTripModel = new CommonApplyTrip();
            BeanUtils.copyProperties(oldCommonApplyTrip, commonApplyFromTripModel);
            commonApplyFromTripModel.setType(t);
            tripList.add(commonApplyFromTripModel);
        });
    }

}
