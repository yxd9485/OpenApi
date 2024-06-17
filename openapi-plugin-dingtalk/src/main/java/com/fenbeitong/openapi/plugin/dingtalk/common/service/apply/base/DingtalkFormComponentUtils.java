package com.fenbeitong.openapi.plugin.dingtalk.common.service.apply.base;

import com.dingtalk.api.request.OapiProcessinstanceCreateRequest;

import java.util.List;

/**
 * <p>Title: DingtalkFormComponetUtils<p>
 * <p>Description: 钉钉表单相关工具类<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author liuhong
 * @date 2022/7/5 16:06
 */
public class DingtalkFormComponentUtils {
    /**
     * 钉钉表单控件添加
     * @param componentName 控件名称
     * @param componentValue 控件值
     * @param list 控件列表
     */
    public static void addComponent(String componentName, String componentValue, List<OapiProcessinstanceCreateRequest.FormComponentValueVo> list) {
        if (componentValue != null) {
            OapiProcessinstanceCreateRequest.FormComponentValueVo formComponentValueVo = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            formComponentValueVo.setName(componentName);
            formComponentValueVo.setValue(componentValue);
            list.add(formComponentValueVo);
        }
    }

}
