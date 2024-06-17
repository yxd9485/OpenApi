package com.fenbeitong.openapi.plugin.func.apply.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserContactDTO {

    private String id;

    private String name;
    private String id_number;
    private CodeValueItem id_type;
    private CodeValueItem gender;
    private String birth_date;
    private String phone;
    /**
     * 该联系人是否还存在
     */
    private Boolean exist;

    private Boolean is_employee;
    /**
     * is_employee=true,此值为部门列表(多部门用;分开)     is_employee=false,此值为"来自手动添加"
     */

    private String desc;

    private String phone_num;
    //居住人类型1:入住人，2:同住人
    private Integer occupier_type;
    //房间分组
    private Integer room_group;

    private BigDecimal price_limit;

    private String selected_employee_id;

    private String avatar_url;

    private String third_employee_id;

    public UserContactDTO() {
    }
}