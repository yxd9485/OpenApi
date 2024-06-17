package com.fenbeitong.openapi.plugin.feishu.common.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 飞书考勤打卡结果
 * @Auther zhang.peng
 * @Date 2021/9/25
 */
@Data
public class FeishuAttendanceRespDTO extends FeiShuRespDTO {

    private DataInfo data;

    @Data
    public static class DataInfo{
        List<TaskResult> user_task_results;
        private List<String> invalid_user_ids;
        private List<String> unauthorized_user_ids;
    }

    @Data
    public static class TaskResult{
        private String result_id;
        private String user_id;
        private String employee_name;
        private String day;
        private String group_id;
        private String shift_id;
        private List<CheckRecord> records;
    }

    @Data
    public static class CheckRecord{
        private String check_in_record_id;
        private CheckInResult check_in_record;
        private String check_out_record_id;
        private CheckOutResult check_out_record;
        private String check_in_result;
        private String check_out_result;
        private String check_in_result_supplement;
        private String check_out_result_supplement;
        private String check_in_shift_time;
        private String check_out_shift_time;
    }

    @Data
    public static class CheckInResult{
        private String user_id;
        private String creator_id;
        private String location_name;
        private String check_time;
        private String comment;
        private String record_id;
//                longitude; 30.28991
//                latitude; 120.04513
        private String ssid;
        private String bssid;
        @JsonProperty("is_field")
        private boolean field;
        @JsonProperty("is_wifi")
        private boolean wifi;
        private int type;
        private String device_id;
    }

    @Data
    public static class CheckOutResult{
        private String user_id;
        private String creator_id;
        private String location_name;
        private String check_time;
        private String comment;
        private String record_id;
        private double longitude;
        private double latitude;
        private String ssid;
        private String bssid;
        @JsonProperty("is_field")
        private boolean field;
        @JsonProperty("is_wifi")
        private boolean wifi;
        private int type;
        private String device_id;
    }
}
