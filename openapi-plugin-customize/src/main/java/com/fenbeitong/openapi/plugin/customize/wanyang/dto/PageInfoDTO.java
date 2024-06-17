package com.fenbeitong.openapi.plugin.customize.wanyang.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: PageInfoDTO</p>
 * <p>Description: 分页信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author helu
 * @date 2022/8/2 8:17 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageInfoDTO {
    private String total;
    private String pages;
    private String pageSize;
    private String pageNum;

}
