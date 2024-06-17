package com.fenbeitong.openapi.plugin.dingtalk.common.constant;

/**
 * @author xiaohai
 * @Description: 钉钉套件
 * @date 2021-12-13 16:01
 */
public interface DingTalkKitConstant {

    /**
     * 0-字段非必填
     */
    Integer FIELD_NOT_REQUIRED = 0 ;
    /**
     * 1-字段必填
     */
    Integer FIELD_REQUIRED = 1 ;

    /**
     * 0-精确时间
     */
    Integer PRECISE_TIME = 0 ;

    /**
     * 1-范围时间
     */
    Integer RANGE_TIME = 1 ;

    /**
     * 错误提示
     */
    interface ErrorTip {
        String START_AND_ENDCITY_NOT_EMPTY = "出发城市和目的城市不能为空!";
        String START_CITY_NOT_EMPTY = "出发城市不能为空!";
        String HOTEL_CITY_NOT_EMPTY = "酒店目的城市不能为空!";
        String CITY_NOT_EMPTY = "出差城市不能为空!";
        String STARTCITY_AND_ENDCITY_NOT_EMPTY = "请补全出发城市和目的城市!";
        String START_TIME_NOT_EMPTY = "出发时间不能为空!";
        String START_AND_ENDTIME_NOT_EMPTY = "出发日期和返程日期不能为空!";
        String HOTEL_TIME_NOT_EMPTY = "入住日期和离店日期不能为空!";
        String ESTIMATED_AMOUNT_NOT_EMPTY = "预估费用不能为空!";
        String TRAVEL_TIME_NOT_EMPTY = "出差开始时间和结束时间都不能为空!";
        String TRAVEL_END_TIME = "出差结束时间不能为空!";
        String CONFIGURATION_ERROR = "后台配置已被修改，请退出当前页并重新发起申请!";
        String TRIP_INFO_ERROR = "行程信息有误,请填写行程信息！";
        String TRIP_SCENE_ERROR = "请选择至少添加一个差旅行程！";
        String MULTI_TRIP_SCENE_ERROR = "请选择至少选择一种差旅场景！";
        String USE_AND_NOCITY = "请选择城市后提交，否则您出差过程中无法使用用车服务!";
        String NOT_USER_CAR = "用车无法获取出差城市，请把是否用车设置为“否”，然后重新提交。若需用车，请单独提交用车审批!";
    }

    /**
     * 套件类型
     */
    interface KitType {
        /**
         * 用车
         */
        String CAR_TYPE = "2" ;
        /**
         * 差旅
         */
        String TRAVEL_TYPE = "1" ;
        /**
         * 用餐
         */
        String DINNER_TYPE = "6" ;
        /**
         * 外卖
         */
        String TAKEAWAY_TYPE = "13" ;
    }

    /**
     * 差旅类型 1-按行程填写申请单,2-仅填写城市、日期、出行方式等信息
     */
    interface ApplyTripType {
        /**
         * 显示且必填
         */
        String APPLY_TRIP = "1" ;
        /**
         * 显示非必填
         */
        String APPLY_MULITY_TRIP = "2" ;
    }

    /**
     * 差旅城市必填不必填
     */
    interface ApplyTripCity {
        /**
         * 显示且必填
         */
        String DISPLAY_AND_REQUIRED = "1" ;
        /**
         * 显示非必填
         */
        String DISPLAY_NOT_REQUIRED = "2" ;
        /**
         * 不显示
         */
        String NOT_DISPLAY = "3" ;
    }

    /**
     * 出差时间0:关闭 1:开启
     */
    interface TravelStatics {
        /**
         * 关闭
         */
        String TRAVEL_STATICS_CLOSE = "0" ;
        /**
         * 开启
         */
        String TRAVEL_STATICS_OPEN = "1" ;
    }

    /**
     * 预估费用必填不必填
     */
    interface TripApplyBuget {
        /**
         * 不显示
         */
        String NOT_DISPLAY = "0" ;
        /**
         * 显示必填
         */
        String DISPLAY_AND_REQUIRED = "1" ;
    }

    /**
     * 费用归属
     */
    interface CostAttribution {
        /**
         * 项目归属0-不展示 1-展示选填 2-展示必填
         */
        String NOT_DISPLAY = "0" ;
        String DISPLAY_NOT_REQUIRED = "1" ;
        String DISPLAY_AND_REQUIRED = "2" ;
        /**
         * 项目和部门归属两者选其一
         */
        String COST_ATTRBUTION_SCOPE = "2" ;
        /**
         * 项目归属部门
         */
        String COST_ATTRBUTION_DEPATMENT = "1" ;
        /**
         * 项目归属项目
         */
        String COST_ATTRBUTION_PROJECT = "2" ;

        /**
         * 不勾选
         */
        String NOT_CHECKED = "0" ;

        /**
         * 勾选
         */
        String CHECKED = "1" ;
    }

    /**
     * 费用归属
     */
    interface ProjectInfo {

        /**
         * 项目状态 1：可用
         */
        Integer PROJECT_STATUS = 1 ;
        /**
         * 可见范围 0：全部可见
         */
        Integer PROJECT_SEERANGE = 0 ;
        /**
         * 用户类型 2：三方用户id
         */
        Integer USER_TYPE = 2 ;

    }

}
