package com.fenbeitong.openapi.plugin.feishu.common.dto;

import lombok.Data;

/**
 *
 * @Auther xiaohai
 * @Date 2022/03/22
 */
@Data
public class FeiShuFileuploadResp {

    private Integer code;
    private String msg;
    private FileInfo data;

    @Data
    public static class FileInfo  {

        private String code;
        private String url;

    }

}
