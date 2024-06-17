package com.fenbeitong.openapi.plugin.customize.wawj.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: WawjFuLiShenQingSyncReqDTO</p>
 * <p>Description: 我爱我家福利申请同步</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/10/26 6:08 PM
 */
@Data
public class WawjFuLiShenQingSyncReqDTO {

    private String companyId;

    @JsonProperty("apply_list")
    private List<WawjFuLiShenQingDTO> applyList;
}
