package com.fenbeitong.openapi.plugin.dingtalk.isv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: xiaohai
 * @Date: 2021/10/13 下午3:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelDetaileDTO {

    /**
     * 出发城市
     */
    private IFormFieldDTO setOutCity;

    /**
     * 目的城市
     */
    private IFormFieldDTO objectiveCity;

    /**
     * 日期
     */
    private IFormFieldDTO travelTimeInterval;

    /**
     * 预估费用
     */
    private IFormFieldDTO travelMoneyField;

    /**
     * 非行程城市
     */
    private IFormFieldDTO travelNoSetOutCity;

    /**
     * 非行程预估费用
     */
    private IFormFieldDTO travelNoMoneyField;

}
