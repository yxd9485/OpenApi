package com.fenbeitong.openapi.plugin.kingdee.support.dto;

import lombok.Data;

/**
 * <p>Title: ViewReqDTO</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-10-14 14:10
 */

@Data
public class ViewReqDTO {

    bean data;

    private String companyId;

    @Data
    public static class bean {
        String FormId;
        String FieldKeys;
        String FilterString;
        String OrderString;
        int TopRowCount;
        int StartRow;
        int Limit;
    }

}
