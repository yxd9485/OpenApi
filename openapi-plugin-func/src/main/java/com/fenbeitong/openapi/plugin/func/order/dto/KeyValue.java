package com.fenbeitong.openapi.plugin.func.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Create by liuwei on 2020/11/13 10:55
 * 封装key-value模式
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KeyValue<K extends Serializable,V extends Serializable> extends BaseDTO {

    private K key;

    private V value;


}
