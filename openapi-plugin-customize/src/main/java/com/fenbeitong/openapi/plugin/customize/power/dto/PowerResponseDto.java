package com.fenbeitong.openapi.plugin.customize.power.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

import java.math.BigDecimal;


/**
 * @author zhangjindong
 */
@Data
public class PowerResponseDto {

    private int code;

    private String msg;

    private T data;

}
