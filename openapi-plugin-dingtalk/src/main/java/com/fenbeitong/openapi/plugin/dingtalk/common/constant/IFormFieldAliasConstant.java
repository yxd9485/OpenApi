package com.fenbeitong.openapi.plugin.dingtalk.common.constant;

/**
 *
 * 自定义套件 控件别名
 * @author xiaohai
 * @date 2020/08/25 10:10
 */
public interface IFormFieldAliasConstant {

    /**
     * 用车规则
     */
    String CAR_RULE = "carRule";

    /**
     * 申请事由
     */
    String CAR_LEAVE_TYEP= "leaveType";

    /**
     * 事由补充
     */
    String CAR_SUBTEXTAREA_FIELD = "subTextareaField";

    /**
     * 用车城市
     */
    String CAR_CITY = "carCity";

    /**
     * 开始时间和结束时间
     */
    String CAR_TIME_SECTION = "carTimeSection";


    /**
     * 开始时间
     */
    String CAR_LEAVE_STARTTIME = "leaveStartTime";

    /**
     * 结束时间
     */
    String CAR_LEAVE_END_TIME = "leaveEndTime";

    /**
     * 用车次数
     */
    String CAR_CAR_FREQUENCY = "carFrequency";

    /**
     * 用车费用(金额)
     */
    String CAR_MONEY_FIELD = "carMoneyField";

    /**
     * 所在部门
     */
    String CAR_DEPARTMENT_FIELD = "carDepartmentField";

    /**
     * 所在项目
     */
    String CAR_PROJECT_FIELD = "carProjectField";

    /**
     * 选择部门和项目
     */
    String CAR_TAB_FIELD = "carTabField";

    /**
     * 费用归属部门
     */
    String CAR_COST_DEPARTMENT_FIELD = "carCostDepartmentField";

    /**
     * 费用归属项目
     */
    String CAR_COST_PROJECT_FIELD = "carCostProjectField";


    /**
     * 费用归属部门和项目
     */
    String CAR_COST_TAB_FIELD = "carCostTabField";


    //==================差旅===============
    /**
     * 申请事由
     */
    String APPLY_SUBJECT = "applySubject";

    /**
     * 事由补充
     */
    String SUBJECT_SUPPLEMENT = "subjectSupplement";

    /**
     * 出发城市
     */
    String SET_OUT_CITY = "setOutCity";

    /**
     * 到达城市
     */
    String OBJECTIVE_CITY = "objectiveCity";

    /**
     * 开始时间和结束时间(范围时间)
     */
    String TRAVEL_TIME_INTERVAL  = "travelTimeInterval";

    /**
     * 出发日期
     */
    String TRAVEL_DATE_FIELD  = "travelDateField";

    /**
     * 预估费用
     */
    String TRAVEL_MONEY_FIELD = "travelMoneyField";

    /**
     * 非行程明细套件
     */
    String TRAVEL_NO_DETAILRD = "travelNoDetailed";

    /**
     * 非行程出发城市
     */
    String TRAVEL_NO_SET_OUT_CITY = "travelNoSetOutCity";

    /**
     * 非行程预估费用
     */
    String TRAVEL_NO_MONEY_FIELD = "travelNoMoneyField";

    /**
     * 出差时间
     */
    String TRAVEL_BUSINESS_TIME_OLD = "travelBusinessTime";

    /**
     * 出差时间
     */
    String TRAVEL_BUSINESS_TIME = "travelBusinessTimeNew";

    /**
     * 出差时长
     */
    String TRAVEL_ALL_NUMBER = "travelAllNumber";

    /**
     * 差旅费用归属和部门
     */
    String TRAVEL_COST_TAB_FIELD = "travelCostTabField";

    /**
     * 差旅费用归属部门
     */
    String TRAVEL_COST_DEPARTMENT = "travelCostDepartment";

    /**
     * 差旅费用归属项目
     */
    String TRAVEL_COST_PROJECT_TEST = "travelCostProject";

    /**
     * 差旅规则
     */
    String TRAVEL_RULE = "travelRule";

    /**
     * 是否用车
     */
    String TRAVEL_OR_CAR = "travelOrCar";
    /**
     * 自定义组件
     */
    String CUSTOMER_FIELD = "travelNewDetailed";

    /**
     * 总预估金额
     */
    String TRAVEL_ALL_MONEY = "travelAllMoney";

    /**
     * 出行人
     */
    String TRAVEL_TRAVELER = "travelTraveler";

    /**
     * corpId
     */
    String CORP_ID_TEXT = "CorpIdText";

    //===========用餐==================

    /**
     * 用餐申请事由
     */
    String DINNER_REASON = "reasonApplication";

    /**
     * 用餐事由补充
     */
    String DINNER_REASON_SUPPLEMENT = "subjectSupplement";

    /**
     * 用餐总费用
     */
    String DINNER_COST = "haveMealsCost";

    /**
     * 用餐规则
     */
    String DINNER_RULE = "seeHaveMeals";

    /**
     * 用餐人数
     */
    String DINNER_PERSON = "numberOfPeople";

    /**
     * 用餐城市
     */
    String DINNER_CITY = "cityHaveMeals";

    /**
     * 用餐开始时间
     */
    String DINNER_START_TIME = "haveMealsStartTime";

    /**
     * 用餐结束时间
     */
    String DINNER_END_TIME = "haveMealsEndTime";

    /**
     * 用餐日期
     */
    String DINNER_TIME = "haveMealsTime";

    /**
     * 用餐时段
     */
    String DINNER_INTREVAL = "timeInterval";

    /**
     * 部门项目切换
     */
    String DINNER_DEP_PROJ_TAB = "haveMealsTab";

    /**
     * 费用归属部门
     */
    String DINNER_COST_DEPARTMENT = "costDepartment";

    /**
     * 费用归属项目
     */
    String DINNER_COST_PROJECT = "costProject";

    //===========外卖==================

    /**
     * 外卖申请事由
     */
    String TAKEAWAY_REASON = "takeawayReason";

    /**
     * 外卖事由补充
     */
    String TAKEAWAY_REASON_SUPPLEMENT = "takeawaySubject";

    /**
     * 外卖规则
     */
    String TAKEAWAY_RULE = "takeawayRule";

    /**
     * 外卖费用
     */
    String TAKEAWAY_COST_MONEY = "takeawayCostMoney";

    /**
     * 送餐日期
     */
    String TAKEAWAY_DATE = "takeawayDate";

    /**
     * 送餐时段
     */
    String TAKEAWAY_INTERVAL = "takeawayInterval";

    /**
     * 外卖地址
     */
    String TAKEAWAY_ADDRESS = "takeawayAddress";

    /**
     * 外卖费用归属和部门
     */
    String TAKEAWAY_COST_TAB_FIELD = "takeawayTab";

    /**
     * 外卖费用归属部门
     */
    String TAKEAWAY_COST_DEPARTMENT = "takeawayCostDepartment";

    /**
     * 外卖费用归属项目
     */
    String TAKEAWAY_COST_PROJECT = "takeawayCostProject";

}
