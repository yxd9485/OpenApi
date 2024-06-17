package com.fenbeitong.openapi.plugin.func.reimburse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName KVEntity
 * @Description  扩展信息key、value键值对
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/9/21 上午9:34
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KVEntity {
    private String key;
    private Object value;
}
