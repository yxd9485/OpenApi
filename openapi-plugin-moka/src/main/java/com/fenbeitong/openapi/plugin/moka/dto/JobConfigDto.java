package com.fenbeitong.openapi.plugin.moka.dto;

import lombok.Data;

/**
 * <p>Title: jobConfigDto</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-11-25 20:42
 */
@Data
public class JobConfigDto {
    String companyId;
    String topId;
    boolean syncDepManager;
    String type;
}
