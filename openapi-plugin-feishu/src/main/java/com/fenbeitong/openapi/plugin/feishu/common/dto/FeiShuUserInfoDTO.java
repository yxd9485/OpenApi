package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.feishu.common.dto.organizationv3.*;
import com.fenbeitong.openapi.plugin.feishu.eia.dto.FeiShuEhrV1EmployeesDTO;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author lizhen
 * @date 2020/6/15
 */
@Data
public class FeiShuUserInfoDTO {

    private String name;

    @JsonProperty("name_py")
    private String namePy;

    @JsonProperty("en_name")
    private String enName;

    @JsonProperty("employee_id")
    private String employeeId;

    @JsonProperty("employee_no")
    private String employeeNo;

    @JsonProperty("open_id")
    private String openId;

    @JsonProperty("union_id")
    private String unionId;

    private Integer status;

    @JsonProperty("employee_type")
    private Integer employeeType;

    @JsonProperty("avatar_72")
    private String avatar72;

    @JsonProperty("avatar_240")
    private String avatar240;

    @JsonProperty("avatar_640")
    private String avatar640;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    private Integer gender;

    private String email;

    private String mobile;

    private String description;

    private String country;

    private String city;

    @JsonProperty("work_station")
    private String workStation;

    @JsonProperty("is_tenant_manager")
    private boolean isTenantManager;

    @JsonProperty("join_time")
    private Integer joinTime;

    @JsonProperty("update_time")
    private Integer updateTime;

    @JsonProperty("leader_employee_id")
    private String leaderEmployeeId;

    @JsonProperty("leader_open_id")
    private String leaderOpenId;

    @JsonProperty("leader_union_id")
    private String leaderUnionId;

    private List<String> departments;

    @JsonProperty("custom_attrs")
    private Map<String, Object> customAttrs;

    private FeiShuEhrV1EmployeesDTO.DataDTO.ItemsDTO feiShuEhrV1EmployeesItem;

    public static class Builder{

        public static List<FeiShuUserInfoDTO> convertNewUserDTO(List<FeiShuBatchUserListDetailV3RespDTO.DataDTO.ItemsDTO> newUserInfoList){
            List<FeiShuUserInfoDTO> feiShuUserInfoDTOList = Lists.newArrayList();
            if (CollectionUtils.isBlank(newUserInfoList)){
                return feiShuUserInfoDTOList;
            }
            for (FeiShuBatchUserListDetailV3RespDTO.DataDTO.ItemsDTO itemsDTO : newUserInfoList) {
                FeiShuUserInfoDTO oldDTO = new FeiShuUserInfoDTO();
                feiShuUserInfoDTOList.add(oldDTO);
                BeanUtils.copyProperties(itemsDTO,oldDTO);
                oldDTO.setEmployeeId(itemsDTO.getUserId());
                oldDTO.setDepartments(itemsDTO.getDepartmentIds());
                oldDTO.setLeaderEmployeeId(itemsDTO.getLeaderUserId());
                oldDTO.setLeaderOpenId(itemsDTO.getLeaderUserId());
                StatusDTO statusDTO = itemsDTO.getStatus();
                // 用户状态，bit0(最低位): 1冻结，0未冻结；bit1:1离职，0在职；bit2:1未激活，0已激活
                if ( null != statusDTO ){
                    buildStatusInfo(statusDTO,oldDTO);
                }
                // 头像信息
                if ( null != itemsDTO.getAvatar() ){
                    buildAvatarInfo(itemsDTO.getAvatar(),oldDTO);
                }
                // 自定义字段信息
                if (CollectionUtils.isBlank(itemsDTO.getCustomAttrs())){
                    continue;
                }
                buildCustomInfo(itemsDTO.getCustomAttrs(),oldDTO);
            }
            return feiShuUserInfoDTOList;
        }

        public static FeiShuUserInfoDTO buildSingleOldDTO( FeiShuSingleUserDetailRespDTO singleUserDetailRespDTO ){
            if ( null == singleUserDetailRespDTO || null == singleUserDetailRespDTO.getData()){
                return null;
            }
            FeiShuSingleUserDetailRespDTO.DataDTO.UserDTO itemsDTO = singleUserDetailRespDTO.getData().getUser();
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

        private static void buildAvatarInfo(AvatarDTO avatarDTO , FeiShuUserInfoDTO oldDTO){
            if ( null != avatarDTO ){
                oldDTO.setAvatar72(avatarDTO.getAvatar72());
                oldDTO.setAvatar240(avatarDTO.getAvatar240());
                oldDTO.setAvatar640(avatarDTO.getAvatar640());
            }
        }

        private static void buildCustomInfo(List<CustomAttrsDTO> customAttrsDTOList , FeiShuUserInfoDTO oldDTO){
            Map<String, Object> customAttrs = Maps.newHashMap();
            for (CustomAttrsDTO customAttr : customAttrsDTOList) {
                CustomAttrsDTO.ValueDTO valueDTO = customAttr.getValue();
                if ( null == valueDTO ){
                    continue;
                }
                // text 和 optionValue 取不空的
                String value = Optional.ofNullable(valueDTO.getText()).orElse(valueDTO.getOptionValue());
                Map<String,Object> valueMap = Maps.newHashMap();
                valueMap.put("value",value);
                customAttrs.put(customAttr.getId(),valueMap);
                oldDTO.setCustomAttrs(customAttrs);
            }
        }

        private static void buildStatusInfo(StatusDTO statusDTO , FeiShuUserInfoDTO feiShuUserInfoDTO){
            if ( null == statusDTO ){
                return;
            }
            // 用户状态，bit0(最低位): 1冻结，0未冻结；bit1:1离职，0在职；bit2:1未激活，0已激活
            String bit0 , bit1 , bit2 ,bit3;
            bit3 = "0";
            // 是否激活
            bit2 = statusDTO.getIsActivated() ? "1" : "0";
            // 是否离职
            bit1 = statusDTO.getIsResigned() ? "0" : "1";
            // 是否暂停
            bit0 = statusDTO.getIsFrozen() ? "1" : "0";
            String status = bit3 + bit2 + bit1 + bit0;
            feiShuUserInfoDTO.setStatus(Integer.parseInt(status,2));
        }

    }


}
