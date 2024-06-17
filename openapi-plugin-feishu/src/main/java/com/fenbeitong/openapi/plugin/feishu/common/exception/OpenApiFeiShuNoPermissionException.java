package com.fenbeitong.openapi.plugin.feishu.common.exception;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;

/**
 * <p>Title: OpenApiFeiShuNoPermissionException</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaohai
 * @date 2021/12/29 5:03 PM
 */
public class OpenApiFeiShuNoPermissionException extends OpenApiPluginException {

    public OpenApiFeiShuNoPermissionException(int code) {
        super(code);
    }

}
