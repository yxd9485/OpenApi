package com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.todo.result;

import lombok.Data;

/**
 * 泛微创建待办返回结果
 * @Auther zhang.peng
 * @Date 2021/12/7
 */
@Data
public class EcologyResultDTO {

    private String syscode;     // 异构系统标识

    private String dateType;    //数据类型 IsUse：统一待办中心 OtherSys：异构系统 WfType：流程类型 WfData：流程数据 SetParam：参数设置

    private String operType;    //操作类型 AutoNew	：自动创建 New：新建 AutoEdit：自动更新 Edit：编辑 Del：删除 Check：检测 Set：设置

    private String operResult;  //操作结果 1：成功 0：失败

    private String message;     //错误信息
}
