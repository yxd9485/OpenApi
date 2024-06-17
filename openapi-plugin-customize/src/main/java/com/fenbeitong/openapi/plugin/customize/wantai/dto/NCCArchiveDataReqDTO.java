package com.fenbeitong.openapi.plugin.customize.wantai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NCCArchiveDataReqDTO {

    /**
     * 系统编码
     */
    private String syscode;

    /**
     * 密钥
     */
    private String secretkey;

    private String time;

    /**
     * 指令ID
     */
    private String id;

    /**
     * 档案类型编码
     */
    private String arctype;

    private String sign;

}
