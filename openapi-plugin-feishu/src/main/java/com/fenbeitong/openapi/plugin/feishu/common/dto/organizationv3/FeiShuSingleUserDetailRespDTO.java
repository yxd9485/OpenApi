package com.fenbeitong.openapi.plugin.feishu.common.dto.organizationv3;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuRespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuUserInfoDTO;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Maps;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 飞书单个用户详情DTO
 * @author zhangpeng
 * @date 2022/4/18 2:04 下午
 */
@Data
public class FeiShuSingleUserDetailRespDTO extends FeiShuRespDTO {

    @JsonProperty("msg")
    private String msg;

    @JsonProperty("data")
    private DataDTO data;

    @Data
    public static class DataDTO {
        @JsonProperty("user")
        private UserDTO user;

        @Data
        public static class UserDTO {
            @JsonProperty("avatar")
            private AvatarDTO avatar;
            @JsonProperty("city")
            private String city;
            @JsonProperty("country")
            private String country;
            @JsonProperty("custom_attrs")
            private List<CustomAttrsDTO> customAttrs;
            @JsonProperty("department_ids")
            private List<String> departmentIds;
            @JsonProperty("description")
            private String description;
            @JsonProperty("email")
            private String email;
            @JsonProperty("employee_no")
            private String employeeNo;
            @JsonProperty("employee_type")
            private Integer employeeType;
            @JsonProperty("en_name")
            private String enName;
            @JsonProperty("gender")
            private Integer gender;
            @JsonProperty("is_tenant_manager")
            private Boolean isTenantManager;
            @JsonProperty("job_title")
            private String jobTitle;
            @JsonProperty("join_time")
            private Integer joinTime;
            @JsonProperty("mobile")
            private String mobile;
            @JsonProperty("mobile_visible")
            private Boolean mobileVisible;
            @JsonProperty("name")
            private String name;
            @JsonProperty("open_id")
            private String openId;
            @JsonProperty("orders")
            private List<OrdersDTO> orders;
            @JsonProperty("status")
            private StatusDTO status;
            @JsonProperty("union_id")
            private String unionId;
            @JsonProperty("user_id")
            private String userId;
            @JsonProperty("work_station")
            private String workStation;
            @JsonProperty("leader_user_id")
            private String leaderUserId;

            @Data
            public static class OrdersDTO {
                @JsonProperty("department_id")
                private String departmentId;
                @JsonProperty("department_order")
                private Integer departmentOrder;
                @JsonProperty("user_order")
                private Integer userOrder;
            }
        }
    }


    public FeiShuUserInfoDTO buildSingleOldDTO(){
        if ( null == data){
            return null;
        }
        FeiShuSingleUserDetailRespDTO.DataDTO.UserDTO itemsDTO = data.getUser();
        if ( null == itemsDTO ){
            return null;
        }
        FeiShuUserInfoDTO oldDTO = new FeiShuUserInfoDTO();
        BeanUtils.copyProperties(itemsDTO,oldDTO);
        oldDTO.setEmployeeId(itemsDTO.getUserId());
        oldDTO.setDepartments(itemsDTO.getDepartmentIds());
        oldDTO.setLeaderEmployeeId(itemsDTO.getLeaderUserId());
        oldDTO.setLeaderOpenId(itemsDTO.getLeaderUserId());
        StatusDTO statusDTO = itemsDTO.getStatus();
        if ( null != statusDTO ){
            buildStatusInfo(statusDTO,oldDTO);
        }
        // 头像信息
        if ( null != itemsDTO.getAvatar() ){
            buildAvatarInfo(itemsDTO.getAvatar(),oldDTO);
        }
        // 自定义字段信息
        if (CollectionUtils.isBlank(itemsDTO.getCustomAttrs())){
            return oldDTO;
        }
        buildCustomInfo(itemsDTO.getCustomAttrs(),oldDTO);
        return oldDTO;
    }

    private void buildAvatarInfo(AvatarDTO avatarDTO , FeiShuUserInfoDTO oldDTO){
        if ( null != avatarDTO ){
            oldDTO.setAvatar72(avatarDTO.getAvatar72());
            oldDTO.setAvatar240(avatarDTO.getAvatar240());
            oldDTO.setAvatar640(avatarDTO.getAvatar640());
        }
    }

    private void buildCustomInfo(List<CustomAttrsDTO> customAttrsDTOList , FeiShuUserInfoDTO oldDTO){
        Map<String, Object> customAttrs = Maps.newHashMap();
        for (CustomAttrsDTO customAttr : customAttrsDTOList) {
            CustomAttrsDTO.ValueDTO valueDTO = customAttr.getValue();
            if ( null == valueDTO ){
                continue;
            }
            // text 和 optionValue 取不空的
            String text = valueDTO.getText() + (StringUtils.isBlank(valueDTO.getUrl()) ? "" : ":" + valueDTO.getUrl());
            String value = Optional.ofNullable(valueDTO.getOptionValue()).orElse(text);
            Map<String,Object> valueMap = Maps.newHashMap();
            valueMap.put("value",value);
            customAttrs.put(customAttr.getId(),valueMap);
            oldDTO.setCustomAttrs(customAttrs);
        }
    }

    private void buildStatusInfo(StatusDTO statusDTO , FeiShuUserInfoDTO feiShuUserInfoDTO){
        if ( null == statusDTO ){
            return;
        }
        // 用户状态，bit0(最低位): 1冻结，0未冻结；bit1:1离职，0在职；bit2:1未激活，0已激活
        String bit0 , bit1 , bit2 ,bit3;
        bit3 = "0";
        // 是否激活
        bit2 = statusDTO.getIsActivated() ? "0" : "1";
        // 是否离职
        bit1 = statusDTO.getIsResigned() ? "1" : "0";
        // 是否暂停
        bit0 = statusDTO.getIsFrozen() ? "1" : "0";
        String status = bit3 + bit2 + bit1 + bit0;
        feiShuUserInfoDTO.setStatus(Integer.parseInt(status,2));
    }

}
