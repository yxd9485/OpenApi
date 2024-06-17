package com.fenbeitong.openapi.plugin.wechat.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * Created by lizhen on 2020/3/23.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {

    private Integer errcode;

    private String errmsg;

    @JsonProperty("CorpId")
    private String corpId;

    @JsonProperty("UserId")
    private String userId;

    @JsonProperty("DeviceId")
    private String deviceId;

    @JsonProperty("user_ticket")
    private String userTicket;

    private Integer expiresIn;

    @JsonProperty("extattr")
    private ExtattrBean extAttr;


    @Data
    public class ExtattrBean {
        @JsonProperty("attrs")
        private List<Attrs> attrs;

    }

    @Data
    public static class Attrs {

        @JsonProperty("name")
        private String name;

        @JsonProperty("value")
        private String value;

        @JsonProperty("type")
        private Integer type;
    }

    @JsonIgnore
    public String getAttrValueByAttrName(String attrName, String defaultValue) {
        String value = extAttr == null ? null : ObjectUtils.isEmpty(extAttr.getAttrs()) ? null :
                (String) extAttr.getAttrs().stream()
                        .filter(a -> attrName.equals(a.getName())).findFirst()
                        .orElse(new UserInfoResponse.Attrs())
                        .getValue();
        return ObjectUtils.isEmpty(value) ? defaultValue : value;
    }


}
