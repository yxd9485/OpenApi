package com.fenbeitong.openapi.plugin.customize.archive.yihai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * <p>Title: YiHaiConfigDTO</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021-05-17 10:47
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YiHaiConfigDTO {
    @NotBlank(message = "[thirdArchiveId]不可为空")
    String thirdArchiveId;
    @NotBlank(message = "[companyId]不可为空")
    String companyId;
    @NotBlank(message = "[url]不可为空")
    String url;
    @NotBlank(message = "[userName]不可为空")
    String userName;
    @NotBlank(message = "[passWord]不可为空")
    String passWord;
}
