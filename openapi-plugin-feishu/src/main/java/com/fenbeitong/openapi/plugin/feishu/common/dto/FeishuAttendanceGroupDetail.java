package com.fenbeitong.openapi.plugin.feishu.common.dto;

import lombok.Data;

import java.util.List;

/**
 * 飞书考勤组详情
 * @Auther zhang.peng
 * @Date 2021/9/25
 */
@Data
public class FeishuAttendanceGroupDetail extends FeiShuRespDTO {

    private GroupDetail data;

    @Data
    public static class GroupDetail{
        private boolean allow_out_punch;
        private boolean allow_pc_punch;
        private boolean allow_remedy;
        private List<String> bind_dept_ids;
        private List<String> bind_user_ids;
        private int calendar_id;
        private List<String> except_dept_ids; 
        private List<String> except_user_ids; 
        private boolean face_downgrade;
        private boolean face_punch;
        private int face_punch_cfg;
        private int gps_range;
        private String group_id;
        private List<String> group_leader_ids;
        private String group_name;
        private int group_type;
        private boolean hide_staff_punch_time;
        private List<Location> locations; 
        private List<String> machines; 
        private List<NeedPunchSpecialDays> need_punch_special_days;
        private List<NeedPunchSpecialDays> no_need_punch_special_days;
        private List<String> punch_day_shift_ids;
        private int punch_type;
        private boolean remedy_date_limit;
        private int remedy_date_num;
        private boolean remedy_limit;
        private int remedy_limit_count;
        private int remedy_period_type;
        private int remedy_period_custom_date;
        private boolean replace_basic_pic;
        private boolean show_cumulative_time;
        private boolean show_over_time;
        private String time_zone;
    }
    
    @Data
    public static class Location{
        private String address; 
        private String bssid; 
        private String feature; 
        private String ip; 
//                latitude; 30.28994
        private String location_id;
        private String location_name;
        private int location_type;
//                longitude; 120.04509
        private int map_type;
        private String ssid; 
        private int gps_range;
    }

    @Data
    public static class NeedPunchSpecialDays{
        private int punch_day;
        private String shift_id;
    }
}
