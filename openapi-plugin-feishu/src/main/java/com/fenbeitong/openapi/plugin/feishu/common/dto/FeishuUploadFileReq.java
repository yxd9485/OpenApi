package com.fenbeitong.openapi.plugin.feishu.common.dto;

import lombok.Data;

import java.io.File;

/**
 * 飞书上传文件
 * @Auther xiaohai
 * @Date 2022/03/22
 */
@Data
public class FeishuUploadFileReq {

    private String name;

    private String type;

    private File content;


}
