package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 试用天数
 * Created by lizhen on 2020/3/28.
 */
@Data
public class WeChatIsvTrialDayResponse {

    private Integer code;

    private String msg;

    private WeChatIsvTrialDay data;

    @Data
    public static class WeChatIsvTrialDay {
        @JsonProperty("wechat_isv_trial_day")
        private String weChatIsvTrialDay;
    }

}
