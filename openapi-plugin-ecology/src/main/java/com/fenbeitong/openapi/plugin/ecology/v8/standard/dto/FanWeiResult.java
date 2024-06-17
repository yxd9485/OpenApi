package com.fenbeitong.openapi.plugin.ecology.v8.standard.dto;

import lombok.Data;

/**
 * 泛微返回结果
 * @Auther zhang.peng
 * @Date 2021/6/22
 */
@Data
public class FanWeiResult {

    private int code;

    private String message;

    private boolean success;

    public static FanWeiResult success(){
        FanWeiResult result = new FanWeiResult();
        result.setCode(0);
        result.setSuccess(true);
        result.setMessage("成功");
        return result;
    }

    public static FanWeiResult error(String message){
        FanWeiResult result = new FanWeiResult();
        result.setCode(-1);
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }
}
