package com.fenbeitong.openapi.plugin.kingdee.support.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResultVo<T> implements Serializable {

    private static final long serialVersionUID = 4721351384345845191L;
    /**
     * 状态码
     */
    private int code;
    /**
     * 消息
     */
    private String msg;
    /**
     * 数据结果集
     */
    private T data;

    public ResultVo() {
    }

    public ResultVo(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

}