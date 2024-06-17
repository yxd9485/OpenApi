package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl.common;

import com.weaver.v8.workflow.WorkflowRequestTableField;

/**
 * @author zhangpeng
 * @date 2022/3/23 6:10 下午
 */
public class FileUrlValueBuilder {

    public static void buildFileUrls(String filePath , WorkflowRequestTableField workflowRequestTableField ){
        String fileName = filePath.substring(filePath.lastIndexOf("/")+1,filePath.length());
        workflowRequestTableField.setFieldValue(filePath);
        workflowRequestTableField.setFieldType("http:"+fileName);
    }

}
