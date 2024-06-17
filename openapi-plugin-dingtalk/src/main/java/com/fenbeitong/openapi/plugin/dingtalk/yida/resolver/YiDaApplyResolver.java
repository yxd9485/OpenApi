package com.fenbeitong.openapi.plugin.dingtalk.yida.resolver;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Lists;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: YiDaApplyResolver</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author lizhen
 * @date 2021/8/12 10:10 下午
 */
public class YiDaApplyResolver {

    /**
     * 预置解析
     *
     * @param param
     * @param mapping
     * @return
     */
    public static Map<String, Object> process(Map<String, Object> param, Map<String, Object> mapping) {
        Map<String, Object> result = new HashMap<>();
        //映射块
        Map<String, String> applyMapping = (Map<String, String>) mapping.get("apply");
        Map<String, String> tripListMapping = (Map<String, String>) mapping.get("trip_list");
        List<Map<String, String>> customFieldsMappingList = (List<Map<String, String>>) mapping.get("custom_fields");
        Map<String, String> costAttributionListMapping = (Map<String, String>) mapping.get("cost_attribution_list");
        Map<String, String> costAttributionMapping = (Map<String, String>) mapping.get("cost_attribution");
        //解析apply数据
        Map<String, Object> applyResultMap = router(param, applyMapping);
        Map<String, Object> applyResult = new HashMap<>();
        List<Map<String, Object>> costAttributionList = Lists.newArrayList();
        applyResultMap.put("cost_attribution_list", costAttributionList);
        applyResult.put("apply", applyResultMap);
        //解析trip_list数据
        Map<String, Object> tripListResult = router(param, tripListMapping);
        Map<String, Object> customFieldsResult = new HashMap<>();
        //解析custom_fields数据
        if (!ObjectUtils.isEmpty(customFieldsMappingList)) {
            List<Map<String, Object>> customFieldsResultList = Lists.newArrayList();
            customFieldsResult.put("custom_fields", customFieldsResultList);
            for (Map<String, String> customFieldsMapping : customFieldsMappingList) {
                Map<String, Object> customFieldsProcessResult = router(param, customFieldsMapping);
                for (String key : customFieldsProcessResult.keySet()) {
                    Map<String, Object> customFields = new HashMap<>();
                    customFields.put("type", key);
                    customFields.put("value", customFieldsProcessResult.get(key));
                    customFieldsResultList.add(customFields);
                }
            }
        }

        //处理成本中心-关联表单
        if (!ObjectUtils.isEmpty(costAttributionListMapping)) {
            Map<String, Object> costAttributionProcessResult = router(param, costAttributionListMapping);
            for (String key : costAttributionProcessResult.keySet()) {
                List<Map<String, Object>> costAttributionFieldList = (List<Map<String, Object>>) costAttributionProcessResult.get(key);
                for (Map<String, Object> map : costAttributionFieldList) {
                    Map<String, Object> costAttribution = new HashMap<>();
                    for (String costAttributionMappingKey : costAttributionMapping.keySet()) {
                        costAttribution.put(costAttributionMapping.get(costAttributionMappingKey), map.get(costAttributionMappingKey));
                    }
                    costAttribution.put("cost_attribution_category", 2);
                    costAttributionList.add(costAttribution);
                }
            }
        }
        //解析结束，返回结果
        result.putAll(applyResult);
        result.putAll(tripListResult);
        result.putAll(customFieldsResult);
        return result;
    }


    /**
     * 解析器路由
     *
     * @param param
     * @param mapping
     * @return
     */
    public static Map<String, Object> router(Map<String, Object> param, Map<String, String> mapping) {
        Map<String, Object> result = new HashMap<>();
        if (ObjectUtils.isEmpty(mapping)) {
            return result;
        }
        for (String key : param.keySet()) {
            //如果映射字段中不存在则丢弃
            if (ObjectUtils.isEmpty(mapping.get(key))) {
                continue;
            }
            String[] fieldNameArr = key.split("_");
            if (fieldNameArr.length >= 2) {
                String fieldType = fieldNameArr[0];
                switch (fieldType) {
                    case "selectField":
                        selectFieldResolver(param, result, mapping, key);
                        break;
                    case "textField":
                        textFieldResolver(param, result, mapping, key);
                        break;
                    case "tableField":
                        tableFieldResolver(param, result, mapping, key);
                        break;
                    case "cascadeDateField":
                        cascadeDateFieldResolver(param, result, mapping, key);
                        break;
                    case "addressField":
                        addressFieldResolver(param, result, mapping, key);
                        break;
                    case "associationFormField":
                        associationFormFieldResolver(param, result, mapping, key);
                        break;
                    default:
                        break;
                }
            }
        }
        return result;
    }

    /**
     * 单选解析器
     *
     * @param param
     */
    public static void selectFieldResolver(Map<String, Object> param, Map<String, Object> result, Map<String, String> mapping, String key) {
        Object selectField = param.get(key);
        if (!ObjectUtils.isEmpty(param)) {
            result.put(mapping.get(key), StringUtils.obj2str(selectField));
        }
    }

    /**
     * 文本解析器
     *
     * @param param
     * @return
     */
    public static void textFieldResolver(Map<String, Object> param, Map<String, Object> result, Map<String, String> mapping, String key) {
        Object textField = param.get(key);
        if (!ObjectUtils.isEmpty(param)) {
            result.put(mapping.get(key), StringUtils.obj2str(textField));
        }
    }

    /**
     * 明细解析器，递归重新路由
     *
     * @param param
     * @param mapping
     * @return
     */
    public static void tableFieldResolver(Map<String, Object> param, Map<String, Object> result, Map<String, String> mapping, String key) {
        Object tableField = param.get(key);
        if (!ObjectUtils.isEmpty(tableField)) {
            List<Map<String, Object>> tableObject = (List<Map<String, Object>>) tableField;
            List<Map<String, Object>> tableList = Lists.newArrayList();
            if (!ObjectUtils.isEmpty(tableObject)) {
                for (Map<String, Object> table : tableObject) {
                    Map<String, Object> process = router(table, mapping);
                    tableList.add(process);
                }
            }
            result.put(mapping.get(key), tableList);
        }
    }

    /**
     * 时间解析器
     *
     * @param param
     * @return
     */
    public static void cascadeDateFieldResolver(Map<String, Object> param, Map<String, Object> result, Map<String, String> mapping, String key) {
        Object addressField = param.get(key);
        if (!ObjectUtils.isEmpty(addressField)) {
            List<String> regionIds = (List) addressField;
            if (!ObjectUtils.isEmpty(regionIds) && regionIds.size() >= 2) {
                result.put("start_time", DateUtils.toSimpleStr(NumericUtils.obj2long(regionIds.get(0)), true));
                result.put("end_time", DateUtils.toSimpleStr(NumericUtils.obj2long(regionIds.get(1)), true));
            }
        }
    }

    /**
     * 地址解析器
     *
     * @param param
     * @return
     */
    public static void addressFieldResolver(Map<String, Object> param, Map<String, Object> result, Map<String, String> mapping, String key) {
        Object addressField = param.get(key);
        if (!ObjectUtils.isEmpty(addressField)) {
            Map<String, Object> addressObject = JsonUtils.toObj(StringUtils.obj2str(addressField), Map.class);
            List<Long> regionIds = (List) addressObject.get("regionIds");
            if (!ObjectUtils.isEmpty(regionIds) && regionIds.size() >= 2) {
                result.put(mapping.get(key), StringUtils.obj2str(regionIds.get(1)));
            }
        }
    }

    public static void associationFormFieldResolver(Map<String, Object> param, Map<String, Object> result, Map<String, String> mapping, String key) {
        String associationFormField = StringUtils.obj2str(param.get(key));
        if (!ObjectUtils.isEmpty(associationFormField)) {
            if (associationFormField.startsWith("\"")) {
                associationFormField = associationFormField.substring(1);
            }
            if (associationFormField.endsWith("\"")) {
                associationFormField = associationFormField.substring(0, associationFormField.length() - 1);
            }
            associationFormField = associationFormField.replace("\\\"", "\"");
            List<Map<String, Object>> list = JsonUtils.toObj(associationFormField, new TypeReference<List<Map<String, Object>>>() {
            });
            result.put(mapping.get(key), list);
        }
    }


    public static void main(String[] args) {
        String data = "{\"textField_kt590993\":\"北京\",\"tableField_kt590997\":[{\"cascadeDateField_kt59099a\":[\"1630684800000\",\"1630771200000\"],\"selectField_kt590999\":\"火车\",\"selectField_kt590998_id\":\"1\",\"selectField_kt590998\":\"单程\",\"selectField_kt590999_id\":\"15\",\"addressField_kt59099b\":\"{\\\"address\\\":\\\"\\\",\\\"regionIds\\\":[310000,310100],\\\"regionText\\\":[{\\\"en_US\\\":\\\"shang hai\\\",\\\"zh_CN\\\":\\\"上海\\\"},{\\\"en_US\\\":\\\"shang hai shi\\\",\\\"zh_CN\\\":\\\"上海市\\\"}]}\",\"addressField_kt59099c\":\"{\\\"address\\\":\\\"\\\",\\\"regionIds\\\":[110000,110100],\\\"regionText\\\":[{\\\"en_US\\\":\\\"bei jing\\\",\\\"zh_CN\\\":\\\"北京\\\"},{\\\"en_US\\\":\\\"bei jing shi\\\",\\\"zh_CN\\\":\\\"北京市\\\"}]}\",\"addressField_kt59099c_id\":\"\\\"北京/北京市\\\"\",\"addressField_kt59099b_id\":\"\\\"上海/上海市\\\"\"}],\"associationFormField_kt590996_id\":\"\\\"[{\\\\\\\"formType\\\\\\\":\\\\\\\"receipt\\\\\\\",\\\\\\\"formUuid\\\\\\\":\\\\\\\"FORM-XJ866N71QI1SWQ9B030V5736G5UH2570ULDRKI5\\\\\\\",\\\\\\\"instanceId\\\\\\\":\\\\\\\"FINST-6J8668A1UD8TCWRVY40S8V51PWLW1PJIVG0TK5\\\\\\\",\\\\\\\"subTitle\\\\\\\":\\\\\\\"GY-2021-8\\\\\\\",\\\\\\\"appType\\\\\\\":\\\\\\\"APP_FKRK7Y94DPI1S9DV1605\\\\\\\",\\\\\\\"title\\\\\\\":\\\\\\\"《千古玦尘》豆瓣口碑营销\\\\\\\"}]\\\"\"}";
        Map<String, Object> param = JsonUtils.toObj(data, Map.class);
        Map<String, String> applyMapping = new HashMap<>();
        Map<String, String> tripListMapping = new HashMap<>();
        Map<String, String> customFieldsMapping = new HashMap<>();
//        List<Map<String, String>> customFieldsMappingList = Lists.newArrayList();
//        customFieldsMappingList.add(customFieldsMapping);
        Map<String, String> costAttribution = new HashMap<>();
        Map<String, String> costAttributionList = new HashMap<>();
        applyMapping.put("textField_kt590993", "apply_reason_desc");
        tripListMapping.put("tableField_kt590997", "trip_list");
        tripListMapping.put("selectField_kt590999_id", "type");
        tripListMapping.put("selectField_kt590998_id", "trip_type");
        tripListMapping.put("cascadeDateField_kt59099a", "start_time");
        tripListMapping.put("addressField_kt59099b", "start_city_id");
        tripListMapping.put("addressField_kt59099b", "arrival_city_id");
        //customFieldsMapping.put("selectField_kse9mgag", "成本中心");
        Map<String, Object> mapping = new HashMap<>();
        mapping.put("apply", applyMapping);
        mapping.put("trip_list", tripListMapping);
        // mapping.put("custom_fields", customFieldsMappingList);
        costAttributionList.put("associationFormField_kt590996_id", "cost_attribution_list");
        costAttribution.put("title", "cost_attribution_name");
        costAttribution.put("instanceId", "cost_attribution_id");
        mapping.put("cost_attribution", costAttribution);
        mapping.put("cost_attribution_list", costAttributionList);
        System.out.println(data);
        Map<String, Object> process = process(param, mapping);
        System.out.println(JsonUtils.toJson(process));
    }


}