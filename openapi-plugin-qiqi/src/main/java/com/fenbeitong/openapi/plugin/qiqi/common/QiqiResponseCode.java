package com.fenbeitong.openapi.plugin.qiqi.common;

/**
 * @author helu
 * @date 2022/5/16 上午11:13
 * 返回码
 */
public interface QiqiResponseCode {

    /**
     * 企业不存在
     */
    Integer COMPANY_NOT_EXIST = 1600001;

    /**
     * 查询数据为空
     */
    Integer DATA_NOT_EXIST = 3000001;

    /**
     * 业务类型不存在
     */
    Integer TYPE_NOT_EXIST = 3000002;

    /**
     * 企业缺失配置信息
     */
    Integer COMPANY_SETTING_NOT_EXIST = 1600002;

    /**
     * 停用（更新）自定义档案项目状态失败
     */
    Integer UPDATE_ARCHIVE_PROJECT_ERROR = 3000003;

    /**
     * 增量同步入库任务执行失败
     */
    Integer DATA_ADD_JOB_ERROR = 3000004;

}
