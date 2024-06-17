package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeiShuApprovalFormDTO {
    //表单ID
    @JsonProperty("id")
    private String id;
    //表单ID
    @JsonProperty("name")
    private String name;
    //表单type
    @JsonProperty("type")
    private String type;
    //具体数据
    @JsonProperty("value")
    private Value value;

    //具体数据
    @JsonProperty("stringValue")
    private String stringValue;


    @Data
    public static class Value{
        //差旅类型标识
        @JsonProperty("interval")
        private int interval;
        //原因
        @JsonProperty("reason")
        private String reason;
        //同行人信息
        @JsonProperty("peer")
        List<String> peer;
        //行程内容
        @JsonProperty("schedule")
        private List<Schedule> schedule;
        //同行人信息
        @JsonProperty("peer_open_ids")
        List<String> peerOpenIds;
        //结束时间
        @JsonProperty("end")
        private String end;
        //开始时间
        @JsonProperty("start")
        private String start;

    }


    @Data
    public static class Schedule{
        //出发地
        @JsonProperty("departure")
        private String departure;
        //目的地
        @JsonProperty("destination")
        private String destination;
        //结束时间
        @JsonProperty("end")
        private String end;
        //单程往返
        @JsonProperty("oneRound")
        private String oneRound;
        //原因
        @JsonProperty("remark")
        private String remark;
        //开始时间
        @JsonProperty("start")
        private String start;
        //交通工具类型名称
        @JsonProperty("transport")
        private String transport;
        //交通工具类型代码
        @JsonProperty("interval")
        private int interval;

    }



    /**
     * map转成object
     * @param data
     */
    public FeiShuApprovalFormDTO(LinkedHashMap<String, Object> data) {
        this.id = data.get("id").toString();
        this.name = data.get("name").toString();
        this.type = data.get("type").toString();
        this.value = JsonUtils.toObj(JsonUtils.toJson(data.get("value")), FeiShuApprovalFormDTO.Value.class);
        this.stringValue= data.get("value").toString();
    }



}
