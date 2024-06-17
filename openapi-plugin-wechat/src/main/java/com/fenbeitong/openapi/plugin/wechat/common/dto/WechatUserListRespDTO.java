package com.fenbeitong.openapi.plugin.wechat.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: WechatUserListRespDTO</p>
 * <p>Description: 企业微信人员拉取</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/2/25 3:57 PM
 */
@Data
public class WechatUserListRespDTO {

    @JsonProperty("errcode")
    private Integer errCode;

    @JsonProperty("errmsg")
    private String errMsg;

    @JsonProperty("userlist")
    private List<WechatUser> userList;

    @Data
    public static class WechatUser {

        @JsonProperty("userid")
        private String userId;

        private String name;

        @JsonProperty("department")
        private List<Long> departmentList;

        @JsonProperty("order")
        private List<Long> orderList;

        private String position;

        private String mobile;

        private String gender;

        private String email;

        @JsonProperty("is_leader_in_dept")
        private List<Integer> isLeaderInDept;

        private String avatar;

        @JsonProperty("thumb_avatar")
        private String thumbAvatar;

        private String telephone;

        private Integer enable;

        private String alias;

        private Integer status;

        private String address;

        @JsonProperty("hide_mobile")
        private Integer hideMobile;

        @JsonProperty("english_name")
        private String englishName;

        @JsonProperty("extattr")
        private WechatUserExtAttr extAttr;

        @JsonProperty("qr_code")
        private String qrCode;

        @JsonProperty("external_position")
        private String externalPosition;

        @JsonProperty("external_profile")
        private Map externalProfile;

        @JsonProperty("main_department")
        private Long mainDepartment;

        @JsonIgnore
        public String getDepartment() {
            return JsonUtils.toJson(departmentList);
        }

        @JsonIgnore
        public String getDepartmentStr() {
            return StringUtils.obj2str(mainDepartment);
        }

        @JsonIgnore
        public String getOrder() {
            return StringUtils.obj2str(orderList.get(0));
        }

        @JsonIgnore
        public String getAttrValueByAttrName(String attrName, String defaultValue) {
            String value = extAttr == null ? null : ObjectUtils.isEmpty(extAttr.getAttrs()) ? null :
                    (String) extAttr.getAttrs().stream()
                            .filter(a -> attrName.equals(a.getName())).findFirst()
                            .orElse(new Attr())
                            .getValue();
            return ObjectUtils.isEmpty(value) ? defaultValue : value;
        }

    }

    @Data
    public static class WechatUserExtAttr {

        private List<Attr> attrs;
    }

    @Data
    public static class Attr {

        private Integer type;

        private String name;

        private Text text;

        private Web web;

        @JsonIgnore
        public String getValue() {
            return text == null ? (web != null ? web.getUrl() : null) : text.getValue();
        }
    }

    @Data
    public static class Text {

        private String value;
    }

    @Data
    public static class Web {

        private String url;

        private String title;
    }
}
