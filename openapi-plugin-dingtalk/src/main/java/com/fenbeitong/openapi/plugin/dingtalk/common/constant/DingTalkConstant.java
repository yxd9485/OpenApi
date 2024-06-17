package com.fenbeitong.openapi.plugin.dingtalk.common.constant;

/**
 * <p>Title: Constant</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-11-10 16:01
 */
public interface DingTalkConstant {

    interface dinner {
        String KEY_APPLY_REASON = "申请事由";
        String KEY_START_CITY = "用餐城市";

        String KEY_START_END_TIME = "[\"开始时间\",\"结束时间\"]";
        String KEY_USER_COUNT = "用餐人数";
        String KEY_TRIP_FEE = "用餐费用";
    }

    interface car {
        String KEY_APPLY_REASON = "申请事由";
        String KEY_START_CITY = "用车城市";
        String KEY_START_CITY1 = "用车城市1";
        String KEY_START_CITY2 = "用车城市2";
        String KEY_START_CITY3 = "用车城市3";
        String KEY_START_CITY4 = "用车城市4";
        String KEY_START_CITY5 = "用车城市5";
        String KEY_CITY = "城市";

        String KEY_START_END_TIME = "[\"开始时间\",\"结束时间\"]";
        String KEY_TRIP_COUNT = "用车次数";
        String KEY_TRIP_FEE = "用车费用";
    }

    interface business {
        String FORM_LABEL = "出差";
        String KEY_APPLY_TYPE = "交通工具";
        String KEY_START_CITY = "出发城市";
        String KEY_ARRIVAL_CITY = "目的城市";
        String KEY_START_TIME = "开始时间";
        String KEY_END_TIME = "结束时间";
        String KEY_TRIP_TYPE = "单程往返";
        String KEY_TRIP_DURATION = "时长";
        String KEY_PARTNER = "出行人（同行人）";

        String VAL_APPLY_TYPE_AIR = "飞机";
        String VAL_APPLY_TYPE_TRAIN = "火车";
        String VAL_APPLY_TYPE_HOTEL = "其他(酒店)";
        String VAL_APPLY_TYPE_CAR = "汽车";
        String VAL_TRIP_TYPE_ONE_WAY = "单程";
        String VAL_TRIP_TYPE_ROUND = "往返";
        int VAL_VEHICLE_TYPE_TRAIN = 1;
        int VAL_VEHICLE_TYPE_AIR = 2;
        int VAL_VEHICLE_TYPE_INTL_AIR = 0;

        int VAL_REGION_INTERNAL = 0;
        int VAL_REGION_INTERNATIONAL = 1;
    }

    interface applyBusiness {
        // 出差
        String FORM_LABEL = "form_label";
        // 交通工具
        String KEY_APPLY_TYPE = "key_apply_type";
        // 出发城市
        String KEY_START_CITY = "key_start_city";
        // 目的城市
        String KEY_ARRIVAL_CITY = "key_arrival_city";
        // 开始时间
        String KEY_START_TIME = "key_start_time";
        // 结束时间
        String KEY_END_TIME = "key_end_time";
        // 单程往返
        String KEY_TRIP_TYPE = "key_trip_type";
        // 时长
        String KEY_TRIP_DURATION = "key_trip_duration";
        // 飞机
        String VAL_APPLY_TYPE_AIR = "val_apply_type_air";
        // 火车
        String VAL_APPLY_TYPE_TRAIN = "val_apply_type_train";
        // 其他(酒店)
        String VAL_APPLY_TYPE_HOTEL = "val_apply_type_hotel";
        // 汽车
        String VAL_APPLY_TYPE_CAR = "val_apply_type_car";
        // 单程
        String VAL_TRIP_TYPE_ONE_WAY = "val_trip_type_one_way";
        // 往返
        String VAL_TRIP_TYPE_ROUND = "val_trip_type_round";
    }

    interface applyCar {
        // 申请事由
        String KEY_APPLY_REASON = "key_apply_reason";
        // 用车城市
        String KEY_START_CITY = "key_start_city";
        // 用车城市1
        String KEY_START_CITY1 = "key_start_city1";
        // 用车城市2
        String KEY_START_CITY2 = "key_start_city2";
        // 用车城市3
        String KEY_START_CITY3 = "key_start_city3";
        // 用车城市4
        String KEY_START_CITY4 = "key_start_city4";
        // 用车城市5
        String KEY_START_CITY5 = "key_start_city5";
        // 开始时间,结束时间
        String KEY_START_END_TIME = "key_start_end_time";
        // 用车次数
        String KEY_TRIP_COUNT = "key_trip_count";
        // 用车费用
        String KEY_TRIP_FEE = "key_trip_fee";
    }

    interface partner {

        // 同行人组件类型
        String FIELD_TYPE = "InnerContactField";
        // 组件名
        String COMPONENT_NAME = "componentName";
        String PROPS = "props";
        String LABEL = "label";
        String EXT_VALUE = "extValue";
        String EMPL_ID = "emplId";
        String NAME = "name";

    }

    interface custformField {
        String START_DATE = "开始日期";
        String END_DATE = "结束日期";
        String START_END_DATEE = "时间段控件";

    }

    interface custformApplyState {
        String AUDIT = "待审核";
        String FINISH = "已通过";
        String REVOCATION = "废弃";
    }

}
