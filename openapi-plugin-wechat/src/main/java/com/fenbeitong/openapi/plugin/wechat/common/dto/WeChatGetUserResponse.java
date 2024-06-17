package com.fenbeitong.openapi.plugin.wechat.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by lizhen on 2020/3/24.
 */
@Data
public class WeChatGetUserResponse {

    @JsonProperty("errcode")
    private Integer errCode;

    @JsonProperty("errmsg")
    private String errMsg;

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

    @JsonProperty("extattr")
    private WechatUserListRespDTO.WechatUserExtAttr extAttr;

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
                        .orElse(new WechatUserListRespDTO.Attr())
                        .getValue();
        return ObjectUtils.isEmpty(value) ? defaultValue : value;
    }


}
