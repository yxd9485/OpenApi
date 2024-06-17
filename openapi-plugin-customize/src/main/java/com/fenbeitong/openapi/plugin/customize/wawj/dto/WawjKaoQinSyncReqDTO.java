package com.fenbeitong.openapi.plugin.customize.wawj.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: WawjKaoQinSyncReqDTO</p>
 * <p>Description: 我爱我家考勤</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/10/26 5:53 PM
 */
@Data
public class WawjKaoQinSyncReqDTO {

    private String companyId;

    @JsonProperty("attendance_list")
    private List<WawjKaoQinDTO> attendanceList;
}
