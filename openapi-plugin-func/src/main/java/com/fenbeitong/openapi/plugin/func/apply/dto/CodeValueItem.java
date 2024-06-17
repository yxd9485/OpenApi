package com.fenbeitong.openapi.plugin.func.apply.dto;

import com.fenbeitong.openapi.plugin.func.order.dto.KeyValue;

/**
 * @author wuchao
 * @date 2020/3/11
 */
public class CodeValueItem extends KeyValue<Integer, String> {

    public CodeValueItem() {
    }

    public CodeValueItem(Integer key, String value) {
        super(key, value);
    }
}
