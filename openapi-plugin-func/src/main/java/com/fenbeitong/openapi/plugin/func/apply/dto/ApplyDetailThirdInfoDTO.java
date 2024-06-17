package com.fenbeitong.openapi.plugin.func.apply.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class ApplyDetailThirdInfoDTO {
    private String user_id;
    private String unit_id;
    private String apply_id;
    private String passenger_user_id;
    private String passenger_unit_id;
    private String cost_dept_id;
    private String cost_project_id;
}
