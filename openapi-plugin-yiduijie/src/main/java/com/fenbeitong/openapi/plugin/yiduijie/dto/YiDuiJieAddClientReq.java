package com.fenbeitong.openapi.plugin.yiduijie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: YiDuiJieAddClientReq</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 6:05 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YiDuiJieAddClientReq {

    private String userId;

    private String name;

}
