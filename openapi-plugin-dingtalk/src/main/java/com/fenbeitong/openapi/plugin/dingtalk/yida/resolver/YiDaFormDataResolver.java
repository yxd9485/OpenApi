package com.fenbeitong.openapi.plugin.dingtalk.yida.resolver;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.dingtalk.yida.dto.YiDaDeptSelectDTO;
import com.fenbeitong.openapi.plugin.dingtalk.yida.dto.YiDaReceiptDTO;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 宜搭表单数据解析
 *
 * @author ctl
 * @date 2022/3/5
 */
public class YiDaFormDataResolver {

    /**
     * 解析字符串list
     * 数据示例："[\"内容1\",\"内容2\"]"
     *
     * @param str
     * @return
     */
    public static List<String> resolveStringList(String str) {
        return StringUtils.isBlank(str) ? null : JsonUtils.toObj(str, new TypeReference<List<String>>() {
        });
    }

    /**
     * 解析[关联表单]组件list
     * 数据示例："[\"[{\\\"formType\\\":\\\"receipt\\\",\\\"formUuid\\\":\\\"FORM-08866RA15T0YD9GTZZHAJ3R0YBNK2D4CG080L5L1\\\",\\\"instanceId\\\":\\\"FINST-1I966XB1JV7YQLHK1BZHA79OFPLK2IJKXA90L57I\\\",\\\"subTitle\\\":\\\"test-1\\\",\\\"appType\\\":\\\"APP_KOTRZSJXRR12LMGDFM6Y\\\",\\\"title\\\":\\\"测试项目\\\"}]\",\"[{\\\"formType\\\":\\\"receipt\\\",\\\"formUuid\\\":\\\"FORM-08866RA15T0YD9GTZZHAJ3R0YBNK2D4CG080L5L1\\\",\\\"instanceId\\\":\\\"FINST-1I966XB1JV7YQLHK1BZHA79OFPLK2IJKXA90L57I\\\",\\\"subTitle\\\":\\\"test-1\\\",\\\"appType\\\":\\\"APP_KOTRZSJXRR12LMGDFM6Y\\\",\\\"title\\\":\\\"测试项目\\\"}]\"]"
     *
     * @param str
     * @return
     */
    public static List<YiDaReceiptDTO> resolveReceiptList(String str) {
        List<YiDaReceiptDTO> targetList = new ArrayList<>();
        if (StringUtils.isBlank(str)) {
            return null;
        }
        List<Object> list = JsonUtils.toObj(str, new TypeReference<List<Object>>() {
        });
        if (ObjectUtils.isEmpty(list)) {
            return null;
        }
        for (Object o : list) {
            if (ObjectUtils.isEmpty(o)) {
                continue;
            }
            List<YiDaReceiptDTO> receiptDTOList = JsonUtils.toObj(o.toString(), new TypeReference<List<YiDaReceiptDTO>>() {
            });
            if (ObjectUtils.isEmpty(receiptDTOList)) {
                continue;
            }
            targetList.addAll(receiptDTOList);
        }
        return targetList;
    }

    /**
     * 解析[部门]组件list
     * 数据示例："[\"[\\\"{\\\\\\\"text\\\\\\\":{\\\\\\\"pureEn_US\\\\\\\":\\\\\\\"门卫\\\\\\\",\\\\\\\"en_US\\\\\\\":\\\\\\\"门卫\\\\\\\",\\\\\\\"zh_CN\\\\\\\":\\\\\\\"门卫\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"i18n\\\\\\\"},\\\\\\\"value\\\\\\\":\\\\\\\"116530333\\\\\\\"}\\\"]\",\"[\\\"{\\\\\\\"text\\\\\\\":{\\\\\\\"pureEn_US\\\\\\\":\\\\\\\"IT信息化部\\\\\\\",\\\\\\\"en_US\\\\\\\":\\\\\\\"IT信息化部\\\\\\\",\\\\\\\"zh_CN\\\\\\\":\\\\\\\"IT信息化部\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"i18n\\\\\\\"},\\\\\\\"value\\\\\\\":\\\\\\\"149836119\\\\\\\"}\\\"]\"]"
     *
     * @param str
     * @return
     */
    public static List<YiDaDeptSelectDTO> resolveDeptSelectList(String str) {
        List<YiDaDeptSelectDTO> targetList = new ArrayList<>();
        if (StringUtils.isBlank(str)) {
            return null;
        }
        List<String> list = JsonUtils.toObj(str, new TypeReference<List<String>>() {
        });
        if (ObjectUtils.isEmpty(list)) {
            return null;
        }
        for (String s : list) {
            List<String> strList = JsonUtils.toObj(s, new TypeReference<List<String>>() {
            });
            if (ObjectUtils.isEmpty(strList)) {
                continue;
            }
            for (String s1 : strList) {
                YiDaDeptSelectDTO yiDaDeptSelectDTO = JsonUtils.toObj(s1, YiDaDeptSelectDTO.class);
                if (yiDaDeptSelectDTO == null) {
                    continue;
                }
                targetList.add(yiDaDeptSelectDTO);
            }
        }
        return targetList;
    }

}
