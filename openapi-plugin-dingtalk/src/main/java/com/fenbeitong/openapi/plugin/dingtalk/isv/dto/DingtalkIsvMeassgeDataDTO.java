package com.fenbeitong.openapi.plugin.dingtalk.isv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: xiaohai
 * @Date: 2021/12/6 上午11:26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DingtalkIsvMeassgeDataDTO {

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息标题
     */
    private String title;

    /**
     * pc端跳转路径
     */
    private String link_pc;

    /**
     * 移动端跳转路径
     */
    private String link_mobile;

}
