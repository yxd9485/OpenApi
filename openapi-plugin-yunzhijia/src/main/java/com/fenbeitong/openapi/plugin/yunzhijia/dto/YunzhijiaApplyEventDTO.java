package com.fenbeitong.openapi.plugin.yunzhijia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 详细信息参考地址：
 * https://yunzhijia.com/cloudflow-openplatform/other/3002
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YunzhijiaApplyEventDTO {

    @JsonProperty("errorCode")
    private Integer errorCode;
    @JsonProperty("success")
    private boolean success;
    @JsonProperty("error")
    private boolean error;
    @JsonProperty("data")
    private YunzhijiaApplyData data;

    @Data
    public static  class YunzhijiaApplyData {
        @JsonProperty("basicInfo")
        private YunzhijiaApplyBasicInfoDTO basicInfo;
        @JsonProperty("formInfo")
        private YunzhijiaApplyDetailInfoDTO detailInfo;
    }

    @Data
    public static class YunzhijiaApplyDetailInfoDTO{
        @JsonProperty("detailMap")
        private DetailMapDTO detailMapDTO;
        @JsonProperty("widgetMap")
        private WidgetMapDTO widgetMap;

    }

    /**
     * detailMap数据
     */
    @Data
    public static class DetailMapDTO{
        @JsonProperty("_S_INT_ON_BUSINESS_DETAILED")
        private DetailDTO detailDTO;
    }


    @Data
    public static class DetailDTO{
        //名称
        @JsonProperty("buttonName")
        private String buttonName;
        //编号ID
        @JsonProperty("codeId")
        private String codeId;
        //标题
        @JsonProperty("title")
        private String title;
        //标题类型
        @JsonProperty("type")
        private String type;
        @JsonProperty("widgetValue")
        private List<WidgetValueDTO> widgetValue;
        @JsonProperty("widgetVos")
        private WidgetVoMapDTO widgetVos;

    }

    @Data
    public static class WidgetMapDTO{
        @JsonProperty("_S_SERIAL")
        private WidgetDTO serialNo;
        //用车城市
        @JsonProperty("Te_4")
        private WidgetDTO city;
        @JsonProperty("_S_DATE")
        private WidgetDTO date;
        @JsonProperty("Mo_0")
        private WidgetDTO money;
        //用车事由
        @JsonProperty("Te_3")
        private WidgetDTO reason;
        @JsonProperty("Nu_0")
        private WidgetDTO count;
        @JsonProperty("_S_TITLE")
        private WidgetDTO title;
        @JsonProperty("_S_APPLY")
        private WidgetDTO applyUser;
        //日期控件，原有审批模板时间控件删除后新增时间控件后为Dr_1
        @JsonProperty("Dr_1")
        private WidgetDTO dateList;
        @JsonProperty("Ps_0")
        private WidgetDTO contracts;

//        @JsonProperty("_S_INT_ON_BUSINESS_REASON")
//        private WidgetDTO _S_INT_ON_BUSINESS_REASON;
//        @JsonProperty("Im_0")
//        private WidgetDTO Im_0;
//        @JsonProperty("_S_INT_ON_BUSINESS_DESC")
//        private WidgetDTO _S_INT_ON_BUSINESS_DESC;
//

    }



    /**
     * 主表单
     */
    @Data
    public static class WidgetDTO{
        @JsonProperty("codeId")
        private String codeId;
        @JsonProperty("title")
        private String title;
        @JsonProperty("type")
        private String type;
        @JsonProperty("value")
        private Object value;
        //日期格式
        @JsonProperty("dateFormat")
        private String dateFormat;
        //差旅类型
        @JsonProperty("options")
        private List<Option> options;
        @JsonProperty("personInfo")
        private List<YunzhijiaEmployeeDTO> employeeDTOS;
    }
    @Data
    public static class WidgetVoMapDTO{
        //出差天数
        @JsonProperty("_S_INT_ON_BUSINESS_DAYS")
        private WidgetDTO businessDay;
        //开始城市
        @JsonProperty("_S_INT_ON_BUSINESS_DESTINATION")
        private WidgetDTO startCity;
        //目的城市
        @JsonProperty("Te_0")
        private WidgetDTO endCity;
        //出发城市三字码
        @JsonProperty("Te_1")
        private WidgetDTO startCityCodeC;
        //目的城市三字码
        @JsonProperty("Te_2")
        private WidgetDTO endCityCodeC;
        //差旅类型
        @JsonProperty("Ra_0")
        private WidgetDTO tripType;
        //单程往返
        @JsonProperty("Ra_1")
        private WidgetDTO single;
        @JsonProperty("_id_")
        private WidgetDTO id;
        //出差时间范围
        @JsonProperty("_S_INT_ON_BUSINESS_TIME")
        private WidgetDTO _S_INT_ON_BUSINESS_TIME;
    }

    @Data
    public static  class WidgetValueDTO{
        //出差天数
        @JsonProperty("_S_INT_ON_BUSINESS_DAYS")
        private String businessDay;
        //开始城市
        @JsonProperty("_S_INT_ON_BUSINESS_DESTINATION")
        private String startCity;
        //目的城市
        @JsonProperty("Te_0")
        private String endCity;
        //出发城市三字码
        @JsonProperty("Te_1")
        private String startCityCodeC;
        //目的城市三字码
        @JsonProperty("Te_2")
        private String endCityCodeC;
        //差旅类型
        @JsonProperty("Ra_0")
        private String tripType;
        //单程往返
        @JsonProperty("Ra_1")
        private String single;

        @JsonProperty("_id_")
        private String id;
        //出差时间范围
        @JsonProperty("_S_INT_ON_BUSINESS_TIME")
        private List<Long> businessTime;


    }




    @Data
    public static class Option{
        //差旅类型key
        private String key;
        //差旅类型名称
        private String value;
        //是否已经选中，暂时未使用
        private boolean checked;
    }

    @Data
    public static class YunzhijiaApplyBasicInfoDTO{
        // 审批节点名称
        @JsonProperty("nodeName")
        private String nodeName;

        @JsonProperty("eid")
        private String eid;
        // 表单模版id
        @JsonProperty("formDefId")
        private String formDefId;
        // 数据类型，0=测试数据，1=正式数据
        @JsonProperty("dataType")
        private int dataType;
        // 审批节点类型
        @JsonProperty("nodeType")
        private String nodeType;
        // 表单模版标题
        @JsonProperty("title")
        private String title;
        // 流程实例id
        @JsonProperty("flowInstId")
        private String flowInstId;
        // 表单模版codeId
        @JsonProperty("formCodeId")
        private String formCodeId;
        // 推送事件触发的操作类型：reach=节点到达，agree=节点同意，submit=节点提交，delete=单据删除，withdraw=节点撤回。备注：只有开始节点有撤回事件，开始节点的到达可以视为退回
        @JsonProperty("actionType")
        private String actionType;
        // // 表单实例id
        @JsonProperty("formInstId")
        private String formInstId;
        // 推送时间(开发者选项触发时间)，unix时间戳
        @JsonProperty("eventTime")
        private long eventTime;
        //开发者选项配置的接口id
        @JsonProperty("interfaceId")
        private String interfaceId;
        // 开发者选项配置的接口名
        @JsonProperty("interfaceName")
        private String interfaceName;
        // 是不是退回？
        @JsonProperty("returned")
        private boolean returned;
        // 节点id
        @JsonProperty("nodeId")
        private String nodeId;
        //提交人员信息
        @JsonProperty("myPersonInfo")
        YunzhijiaEmployeeDTO yunzhijiaEmployeeDTO;







    }
}
