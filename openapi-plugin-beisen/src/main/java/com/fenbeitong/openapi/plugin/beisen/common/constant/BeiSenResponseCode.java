package com.fenbeitong.openapi.plugin.beisen.common.constant;

/**
 * <p>Title: BeiSenResponseCode<p>
 * <p>Description: 北森响应码<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author: liuhong
 * @date: 2022/9/8 10:58
 */
public interface BeiSenResponseCode {
    /**
     * 北森定时任务参数错误
     */
    Integer BEISEN_JOB_PARAM_ERROR = 180001;
    /**
     * 北森同步组织架构参数配置未配置
     */
    Integer BEISEN_SYNC_ORG_TEMPLATE_PARAM_is_null = 180002;
    /**
     * 北森同步组织架构参数配置错误
     */
    Integer BEISEN_SYNC_ORG_TEMPLATE_PARAM_ERROR = 180003;
    /**
     * 北森接口超过最大重试次数
     */
    Integer BEISEN_TRY_COUNT_MAX_ERROR = 180004;
}
