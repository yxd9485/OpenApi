package com.fenbeitong.openapi.plugin.qiqi.service.common;

import com.alibaba.fastjson.JSON;
import com.fenbeitong.openapi.plugin.qiqi.common.exception.OpenApiQiqiException;
import com.fenbeitong.openapi.plugin.qiqi.dto.QiqiCommonReqData;
import com.fenbeitong.openapi.plugin.qiqi.dto.QiqiCommonReqDetailDTO;
import com.fenbeitong.openapi.plugin.qiqi.entity.QiqiCorpInfo;
import com.fenbeitong.openapi.plugin.qiqi.service.AbstractQiqiCommonService;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.q7link.openapi.model.ApiParams;
import com.q7link.openapi.model.ApiResponse;
import com.q7link.openapi.model.PostListRequest;
import com.q7link.openapi.model.PostListResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
public class QiqiCommonReqServiceImpl<T> extends AbstractQiqiCommonService {

    /**
     * 调用企企查询接口
     * @param companyId 公司id
     * @param objectType 目标对象
     * @param clazz 类的class对象
     * @param queryConditions 查询条件
     * @param commonReqDetailList 嵌套参数集合
     * @return 企企数据集合
     */
    public List<T> buildQiqiReq(String companyId, String objectType, Class<T> clazz, String queryConditions, List<QiqiCommonReqDetailDTO> commonReqDetailList) {
        QiqiCorpInfo corpInfo = getCorpInfo(companyId);
        //封装请求数据
        QiqiCommonReqData commonReq = new QiqiCommonReqData();
        Field[] declaredFields = clazz.getDeclaredFields();
        List fieldArray  = Arrays.stream(declaredFields).map(f -> f.getName()).collect(Collectors.toList());
        if (CollectionUtils.isNotBlank(commonReqDetailList)) {
            fieldArray.addAll(commonReqDetailList);
        }
        commonReq.setCriteriaStr(queryConditions);
        commonReq.setObjectType(objectType);
        commonReq.setFields(fieldArray.toArray());

        //获取企企数据
        PostListRequest request = new PostListRequest();
        ApiParams apiParams = new ApiParams();
        //企企接口要求前后换行
        String param = JSON.toJSONString(commonReq);
        apiParams.setJson("{\n"+param.substring(1,param.length()-1)+"\n}");
        log.info("apiParams:{}",apiParams);
        request.sdkRequestConfig(getSdkRequestConfig(request,corpInfo.getAccessKeyId(),corpInfo.getOpenId()));
        request.setApiParams(apiParams);
        PostListResult postListResult = openapi(corpInfo.getAccessKeyId(),corpInfo.getSecretAccessKey()).postList(request);
        ApiResponse apiResponse = postListResult.getApiResponse();
        log.info("调用企企接口返回数据,apiResponse:{}",JsonUtils.toJson(apiResponse));
        if (ObjectUtils.isEmpty(apiResponse)) {
            log.info("调用企企查询接口异常，openId：{},ObjectType:{}",companyId,objectType);
            throw new OpenApiQiqiException(-9999, "调用企企查询接口异常,openId:{},ObjectType:{}",companyId, objectType);
        }
        if(!StringUtils.isBlank(apiResponse.getError())){
            log.info("调用企企查询失败");
            throw new OpenApiQiqiException(-9999, "调用企企查询接口失败，错误信息:{}",apiResponse.getError());
        }
        Object errors = MapUtils.getValueByExpress(JsonUtils.toObj(apiResponse.getJson(), Map.class), "errors");
        if(!ObjectUtils.isEmpty(errors)){
            log.info("调用企企查询接口存在错误信息，企业id:{},查询类型:{},错误信息：{}",companyId,objectType,JsonUtils.toJson(errors));
        }
        String apiResponseJson = apiResponse.getJson();
        Map apiMap = JsonUtils.toObj(apiResponseJson, Map.class);
        Object valueByExpress = MapUtils.getValueByExpress(apiMap, "data:list");
        String apiJson = JsonUtils.toJson(valueByExpress);
        List<T> resList = JSON.parseArray(apiJson, clazz);
        return resList;
    }

}
