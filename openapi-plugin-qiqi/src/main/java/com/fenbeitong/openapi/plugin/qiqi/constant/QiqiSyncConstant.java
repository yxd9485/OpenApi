package com.fenbeitong.openapi.plugin.qiqi.constant;

/**
 * @ClassName QiqiSyncConstant
 * @Description 企企同步数据常量
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/18
 **/
public interface QiqiSyncConstant {
    /**
     * 三方系统档案ID前缀
     */
    String CUSTOM_ARCHIVE_ID_PRE = "qiqi";
    /**
     * 自定义档案名称后缀
     */
    String ARCHIVE_NAME_SUFFIX = "自定义档案";
    /**
     * 身份证号
     */
    String ID_NO = "id_no";
    /**
     * 项目的单据状态-已生效
     */
    String BILL_STATUS_EFFECTIVE = "BillStatus.effective";

    /**
     * 可用范围：1、全部员工可用
     */
    Integer USE_RANGE_ALL = 1;

    /**
     * 根部门默认id
     */
    String ROOT_ID = "root001";
}
