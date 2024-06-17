package com.fenbeitong.openapi.plugin.yunzhijia.dto;

import com.fenbeitong.openapi.plugin.yunzhijia.constant.YunzhijiaApplyConstant;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * https://yunzhijia.com/cloudflow-openplatform/before/1001
 */
public class Demo {
    public static void main(String[] args) {
        CloudflowConfiguration configuration = new CloudflowConfiguration();
        // 开发者设置页面可查询【请改为自己的appId】
        configuration.appId = "SP9977917";
        // 开发者设置页面可查询【请改为自己的开发者secret】
        configuration.secret = "OtTnxerSNoD5mQGhKVf4TxAjqhxLGw";
        // 开发者设置页面可查询【请改为自己的开发者key】
        configuration.key = "T22cdkEko3flglPe";
        // 在云之家首页右上角点击我的团队可查询【请改为自己的eid】
        configuration.eid = "9977917";
        // 文件集成secret(管理中心->系统设置->系统集成->文件服务上传下载)
        configuration.fileSecret = "oCHgV1ECUdTZlRaRUWwCSS65XxCPOYdD";

        Cloudflow cloudflow = new Cloudflow(configuration);

        // 获取表单实例
        Map<String, String> param1 = new HashMap<>();
        param1.put("formInstId", "5abdb6c44aed1a21ffed1c75");
        param1.put("formCodeId", "be0bac2cbb8f412a91109369b190c8e8");
        cloudflow.getFormInstance(param1);

        // 获取审批痕迹
        Map<String, String> param2 = new HashMap<>();
        param2.put("formInstId", "5abdb6c44aed1a21ffed1c75");
        param2.put("formCodeId", "be0bac2cbb8f412a91109369b190c8e8");
        cloudflow.getFlowRecord(param2);

        // 发起审批
        Map<String, Object> param3 = new HashMap<>();
        param3.put("formCodeId", "be0bac2cbb8f412a91109369b190c8e8");
        param3.put("creator", "586db606e4b0913965d37b1a");
        param3.put("oids", Collections.singletonList("586db606e4b0913965d37b1a"));
        Map<String, Object> widgetValue = new HashMap<>();
        widgetValue.put("_S_TITLE", "我是一个小标题");
        param3.put("widgetValue", widgetValue);
        cloudflow.createInst(param3);

        // 获取使用了互联控件的模版列表
        Map<String, Object> param4 = new HashMap<>();
        param4.put("groupId", YunzhijiaApplyConstant.GroupId.LEAVE);
        Map<String, Object> pageable = new HashMap<>();
        pageable.put("id", null);
        pageable.put("type", YunzhijiaApplyConstant.PageType.FIRST);
        pageable.put("pageSize", 10);
        param4.put("pageable", pageable);
        cloudflow.getTemplateListByGroupId(param4);

        // 获取模版
        Map<String, String> param5 = new HashMap<>();
        param5.put("formCodeId", "be0bac2cbb8f412a91109369b190c8e8");
        cloudflow.getTemplateByCodeId(param5);

        // 修改表单
        Map<String, Object> param6 = new HashMap<>();
        param6.put("formCodeId", "be0bac2cbb8f412a91109369b190c8e8");
        param6.put("formDefId", "5a4385250554d043d39f8f0a");
        param6.put("formInstId", "5ac337d709a2bd0d9a6f7b0f");
        param6.put("creator", "586db606e4b0913965d37b1a");
        Map<String, Object> widgetValue2 = new HashMap<>();
        param6.put("widgetValue", widgetValue2);
        cloudflow.modifyInst(param6);

        // 解密推送数据
        // 其中cipher为接收到的原始推送数据
        String cipher = "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";
        cloudflow.decryptNotification(cipher);

        // 获取审批状态
        Map<String, String> param7 = new HashMap<>();
        param7.put("flowInstId", "5b59a81d0554d05cc45eb525");
        cloudflow.getFlowStatus(param7);

        // 文件上传
        cloudflow.uploadFile();

        // 文件下载
        cloudflow.downloadFile("5b95ed9ddb5aa62268fc9c1c");

        //获取模板
        Map<String, Object> param8 = new HashMap<>();
        param8.put("identifyKey", "570f0350-5969-44d6-93c3-1ae6e9b4d4d9");
        cloudflow.getTemplates(param8);
        
        
        //获取模板
        Map<String, Object> param9 = new HashMap<>();
        param9.put("identifyKey", "570f0350-5969-44d6-93c3-1ae6e9b4d4d9");
        param9.put("formCodeIds", Arrays.asList("61cb1fcf19b94693acf6ddd924c61fcd"));
        cloudflow.findFlows(param9);
    }
}
