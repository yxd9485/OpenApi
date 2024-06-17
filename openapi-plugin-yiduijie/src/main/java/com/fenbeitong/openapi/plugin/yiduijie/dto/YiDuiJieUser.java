package com.fenbeitong.openapi.plugin.yiduijie.dto;

import lombok.Data;

/**
 * <p>Title: YiDuiJieUser</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 5:57 PM
 */
@Data
public class YiDuiJieUser {

    private String companyName;

    private String userId;

    private String userName;

    private String password;

    private String creator;

    private String owner;

    private Long createdDate;

    private Boolean enabled;
}
