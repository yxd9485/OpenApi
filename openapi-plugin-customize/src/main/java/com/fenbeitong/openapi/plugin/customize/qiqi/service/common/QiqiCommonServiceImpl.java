package com.fenbeitong.openapi.plugin.customize.qiqi.service.common;

import com.fenbeitong.openapi.plugin.customize.qiqi.dto.QiqiCommonReqDetailDTO;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName QiqiCommonReqService
 * @Description 企企公共请求参数封装
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/5/14 下午5:14
 **/
@Component
@Slf4j
public class QiqiCommonServiceImpl<T> {

    /**
     * 树形参数封装
     * @param fieldName 对应企企外键字段
     * @param clazz 类的class对象
     * @param detailList 嵌套参数集合
     * @return commonReqDetail 嵌套参数
     */
    public QiqiCommonReqDetailDTO getTreeParam(String fieldName, Class<T> clazz, List<QiqiCommonReqDetailDTO> detailList) {

        QiqiCommonReqDetailDTO commonReqDetail = new QiqiCommonReqDetailDTO();
        Field[] declaredFields = clazz.getDeclaredFields();
        List fieldArray = Arrays.stream(declaredFields).map(f -> f.getName()).collect(Collectors.toList());
        if (CollectionUtils.isNotBlank(detailList)) {
            fieldArray.addAll(detailList);
        }
        commonReqDetail.setFieldName(fieldName);
        commonReqDetail.setFields(fieldArray.toArray());

        return commonReqDetail;
    }

}
