package com.fenbeitong.openapi.plugin.func.apply.dto;


import com.luastar.swift.base.utils.ObjUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ApplyTravelTimeDTO {

    //出差时间
    private String travel_time;
    //类型
    private Integer travel_type;

    public String getTravel_time() {
        return travel_time;
    }

    public void setTravel_time(String travel_time) {
        this.travel_time = travel_time;
    }

    public Integer getTravel_type() {
        return travel_type;
    }

    public void setTravel_type(Integer travel_type) {
        this.travel_type = travel_type;
    }

    public static ApplyTravelTimeDTO FromModel(ApplyTravelTimeDTO applyTravelTime) {
        ApplyTravelTimeDTO applyTravelTimeContract = new ApplyTravelTimeDTO();
        applyTravelTimeContract.setTravel_time(ObjUtils.toString(applyTravelTime.getTravel_time()));
        applyTravelTimeContract.setTravel_type(applyTravelTime.getTravel_type());
        return applyTravelTimeContract;
    }
}

