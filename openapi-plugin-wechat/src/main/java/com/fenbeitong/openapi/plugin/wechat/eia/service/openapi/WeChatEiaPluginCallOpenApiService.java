package com.fenbeitong.openapi.plugin.wechat.eia.service.openapi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.apply.service.IOpenTripApplyService;
import com.fenbeitong.openapi.plugin.support.citycode.dto.AirportCityDTO;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenApiResponseDTO;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.common.notice.sender.dao.MsgRecipientDefinitionDao;
import com.fenbeitong.openapi.plugin.support.common.notice.sender.entity.MsgRecipientDefinition;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.service.AbstractEmployeeService;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.dto.ApprovalInfo;
import com.fenbeitong.openapi.plugin.wechat.common.dto.FbUserCheck;
import com.fenbeitong.openapi.plugin.wechat.common.dto.GustUser;
import com.fenbeitong.openapi.plugin.wechat.common.dto.OpenApiResponse;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.common.notice.sender.WeChatNoticeSender;
import com.fenbeitong.openapi.sdk.dto.air.CityRespDTO;
import com.fenbeitong.openapi.sdk.dto.common.TypeEntity;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 企业微信调用openapi相关服务类
 * Created by dave.hansins on 19/12/16.
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatEiaPluginCallOpenApiService extends AbstractEmployeeService {

    private static com.google.common.cache.Cache<String, List> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(120, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();
    private static final int HOTEL_AREA_TYPE_CITY = 2;

    @Autowired
    PluginCorpDefinitionDao pluginCorpDefinitionDao;
    @Autowired
    WeChatEiaPluginCallOpenApiService weChatEiaPluginCallOpenApiService;
    @Autowired
    WeChatNoticeSender weChatNoticeSender;
    @Autowired
    MsgRecipientDefinitionDao msgRecipientDefinitionDao;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private IOpenTripApplyService openTripApplyService;

    @Value("${openapi.host}")
    private String openapiHost;

    /**
     * 获取企业OPENAPI access_token
     *
     * @param appId
     * @param appKey
     * @return
     */
    public String getOpenApiAccessToken(String appId, String appKey) {
        String url = openapiHost + "/open/api/auth/v1/dispense";
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("app_id", appId)
                .add("app_key", appKey)
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        final Call call = okHttpClient.newCall(request);
        ResponseBody body = null;
        String result = "";
        try {
            Response response = call.execute();
            log.info("返回结果 {}", JsonUtils.toJson(response));
            body = response.body();
            result = body.string();
            log.info("调用开放平台获取公司access_token {}", result);
        } catch (IOException e) {
            log.warn("调用开放平台获取公司access_token异常", e);
        }
        OpenApiResponse openApiResponse = JsonUtils.toObj(result, OpenApiResponse.class);
        log.info("调用开放平台获取公司access_token, 返回结果编码：code: {}", openApiResponse.getCode());
        if (openApiResponse != null && openApiResponse.getCode() == RespCode.SUCCESS) {
            String dataStr = (String) openApiResponse.getData();
            Map<String, String> accessTokenMap = JsonUtils.toObj(dataStr, Map.class);
            String access_token = accessTokenMap.get("access_token");
            return access_token;
        }
        throw new OpenApiPluginException(openApiResponse.getCode(), openApiResponse.getMsg());
    }


    /**
     * 根据城市名称获取分贝城市code
     *
     * @param cityName
     * @return
     */
    public String getOpenApiCityInfoByName(String cityName) {
        String url = openapiHost + "/open/api/common/getCityCodeByName/" + cityName;
        String result = getOkHttp(url);
        OpenApiResponse<CityRespDTO> openApiResponse = JsonUtils.toObj(result, OpenApiResponse.class);
        log.info("根据城市名称获取分贝城市信息, 返回结果：code: {}", openApiResponse.getCode());
        if (openApiResponse != null && openApiResponse.getCode() == RespCode.SUCCESS) {
            Map<String, String> data = (Map) openApiResponse.getData();
            if (!ObjectUtils.isEmpty(data)) {
                String cityCode = data.get("id");
                return cityCode;
            }
        }
        return null;
    }


    /**
     * 创建分贝差旅审批单
     *
     * @param companyId
     * @param approvalInfo
     * @param userId
     * @return
     */
    public OpenApiResponse createOpenApiApply(String companyId, ApprovalInfo approvalInfo, String userId) {
        Map<String, String> stringStringMap = genOpenApiSign(companyId, JsonUtils.toJsonSnake(approvalInfo), userId, false);
        String url = openapiHost + "/open/api/approve/create";
        String result = postFormOkHttp(url, approvalInfo, stringStringMap);
        OpenApiResponse openApiResponse = JsonUtils.toObj(result, OpenApiResponse.class);
        log.info("调用开放平台创建审批单： {}", openApiResponse.getCode());
        if (openApiResponse == null || openApiResponse.getCode() != RespCode.SUCCESS) {
            Map<String, String> dataMap = (Map) openApiResponse.getData();
            log.info("创建分贝通审批单失败 code {}, msg {},data {}", openApiResponse.getCode(), openApiResponse.getMsg(), JsonUtils.toJson(dataMap));
            return null;
        }
        return openApiResponse;
    }


    /**
     * 创建审批单,使用abstractTripApply,生成行程
     * @param companyId
     * @param approvalInfo
     * @param userId
     * @return
     */
    public CreateApplyRespDTO createTripApprove(String companyId, ApprovalInfo approvalInfo, String userId) {
        String ucEmployeeToken = userCenterService.getUcEmployeeToken(companyId, userId);
        TripApproveCreateReqDTO req = wechatTripApplyProcessInfo2TripApproveCreateReqDTO(approvalInfo, companyId);
        CreateApplyRespDTO createTripApproveRespDTO = null;
        try {
            createTripApproveRespDTO = openTripApplyService.createTripApprove(ucEmployeeToken, req);
        } catch (Exception e) {
            if ( e instanceof OpenApiPluginSupportException){
                log.warn("企业微信创建审批单失败 : {}",((OpenApiPluginSupportException) e).getArgs()[0]);
            }
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.CREATE_APPLY_ERROR), e.getMessage());
        }
        return createTripApproveRespDTO;
    }

    /**
     * 获取OpenAPI签名数据，后期弃用，
     * 使用openapi-sdk进行数据调用
     *
     * @param companyId
     * @param data
     * @param employeeId
     * @param fbtEmployee
     * @return
     */
    public Map<String, String> genOpenApiSign(String companyId, String data, String employeeId, boolean fbtEmployee) {
        log.info("获取是否为分贝用户原始类型: {}", fbtEmployee);
        log.info("获取是否为分贝用户请求数据: {}", data);
        log.info("获取是否为分贝用户Id: {}", employeeId);
        Map<String, String> params = new HashMap<>(5);
        Example example = new Example(PluginCorpDefinition.class);
        example.createCriteria().andEqualTo("appId", companyId);
        List<PluginCorpDefinition> pluginCorpDefinitions = pluginCorpDefinitionDao.listByExample(example);
        if (ObjectUtils.isEmpty(pluginCorpDefinitions)) {
            throw new OpenApiPluginException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_CORP_ID_NOT_EXIST));
        }
        PluginCorpDefinition pluginCorpDefinition = pluginCorpDefinitions.get(0);
//        CheckUtils.checkNull(company , "企业不存在");
        long timestamp = System.currentTimeMillis();
        String sign = getSign(String.valueOf(timestamp), data, pluginCorpDefinition.getSignKey());
        String accessToken = getOpenApiAccessToken(companyId, pluginCorpDefinition.getAppKey());
        String employeeType = fbtEmployee ? "0" : "1";
        log.info("获取是否为分贝用户类型 {}", employeeType);
        params.put("timestamp", String.valueOf(timestamp));
        params.put("access_token", accessToken);
        params.put("sign", sign);
        params.put("employee_id", employeeId);
        params.put("employee_type", employeeType);
        return params;
    }


    /**
     * 生成签名数据
     *
     * @param timestamp
     * @param jsonData
     * @param signKey
     * @return
     */
    public static String getSign(String timestamp, String jsonData, String signKey) {
        String sign = MessageFormat.format("timestamp={0}&data={1}&sign_key={2}", timestamp, jsonData, signKey);
        byte[] bytes;
        try {
            bytes = sign.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            log.warn("生成openapi签名出错", e);
            throw new OpenApiPluginException();
        }
        return DigestUtils.md5Hex(bytes);
    }



    /**
     * 根据国际城市三字码查询分贝国际城市code
     * @param companyId
     * @return
     */
    public String getIntlCityInfoByCityCode(String companyId, String userId,String citycodes) {
        log.info("根据国际城市三字码查询分贝国际机票城市code {}",citycodes);
        Map<String, String> dataMap = new HashMap<>(1);
        dataMap.put("citycodes", citycodes);
        Map<String, String> stringStringMap = genOpenApiSign(companyId, JsonUtils.toJsonSnake(dataMap), userId, false);
        String url = openapiHost + "/open/api/flight/order/FBAreasByIntlCitycodes";
        String result = postFormOkHttp(url, dataMap, stringStringMap);
        OpenApiResponse<List<AirportCityDTO>> openApiResponse = JsonUtils.toObj(result, new TypeReference<OpenApiResponse<List<AirportCityDTO>>>() {});
        if (openApiResponse != null && openApiResponse.getCode() == RespCode.SUCCESS) {
            List<AirportCityDTO> airportCityDTOs = openApiResponse.getData();
            log.info("调用开放平台获取国际城市名称接口完成, 返回结果编码：code: {}", JsonUtils.toJson(airportCityDTOs));
            if (!ObjectUtils.isEmpty(airportCityDTOs)) {
                AirportCityDTO airportCityDTO = airportCityDTOs.get(0);
                List<AirportCityDTO.FbAreaInfosBean> fbAreaInfos = airportCityDTO.getFbAreaInfos();
                AirportCityDTO.FbAreaInfosBean fbAreaInfosBean = fbAreaInfos.get(0);
                String fbAreaCode = fbAreaInfosBean.getFbAreaCode();
                return fbAreaCode;
            }
        }
        return null;
    }


    /**
     * 检查人员是否为分贝通用户
     *
     * @param companyId
     * @param thirdUserId
     * @return
     */
    public FbUserCheck CheckUserInfo(String companyId, String thirdUserId) {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("employee_id", thirdUserId);
        paramMap.put("type", 1);
        FbUserCheck fbUserCheck = this.hasBindFbtAccount(companyId, paramMap, thirdUserId);
        boolean isFbtUser = fbUserCheck.isFbUser();
        if (!isFbtUser) {//不是分贝通用户，进行消息通知
            //根据分贝公司ID查找企业微信公司ID
            Example example = new Example(PluginCorpDefinition.class);
            example.createCriteria().andEqualTo("appId", companyId);
            PluginCorpDefinition byExample = pluginCorpDefinitionDao.getByExample(example);
            String thirdCorpId = byExample.getThirdCorpId();
            Example example1 = new Example(MsgRecipientDefinition.class);
            example1.createCriteria().andEqualTo("thirdCorpId", thirdCorpId);
            List<MsgRecipientDefinition> msgRecipientDefinitions = msgRecipientDefinitionDao.listByExample(example1);
            if (!ObjectUtils.isEmpty(msgRecipientDefinitions)) {
                MsgRecipientDefinition msgRecipientDefinition = msgRecipientDefinitions.get(0);
                //TODO ,后期优化，发送固定消息
                String msg = thirdUserId + "未开通分贝通账号导致创建分贝通差旅审批失败，请企业微信管理员处理后再次提交审批";
                String senderId = msgRecipientDefinition.getThirdUserId() + "|" + thirdUserId;
                weChatNoticeSender.sender(companyId, senderId, msg);
                return fbUserCheck;
            }
            log.info("企业微信消息接收人员数据未配置");
        }
        return fbUserCheck;
    }


    /**
     * 根据企业微信人员ID查询是否绑定分贝通
     *
     * @param thirdUserId
     * @param companyId
     * @return
     */
    public FbUserCheck hasBindFbtAccount(String companyId, Object data, String thirdUserId) {
        log.info("校验人员是否绑定传入人员参数 :{},公司 :{}", thirdUserId, companyId);
        String url = openapiHost + "/open/api/third/employees/info";
        Map<String, String> stringStringMap = genOpenApiSign(companyId, JsonUtils.toJsonSnake(data), thirdUserId, false);
        String result = postFormOkHttp(url, data, stringStringMap);
        OpenApiResponse openApiResponse = JsonUtils.toObj(result, OpenApiResponse.class);
        int code = openApiResponse.getCode();
        if (code == RespCode.SUCCESS) {//成功后获取用户的分贝信息
            Map<String, Object> data1 = (Map) openApiResponse.getData();
            Map<String, Object> employeeInfo = (Map) data1.get("employee");
            FbUserCheck fbUserCheck = FbUserCheck.builder().fbUserId((String) employeeInfo.get("id"))
                    .fbUserName((String) employeeInfo.get("name"))
                    .thirdUserId((String) employeeInfo.get("third_employee_id"))
                    .fbUserPhone((String) employeeInfo.get("phone_num"))
                    .isFbUser(true).build();
            return fbUserCheck;
        } else if (code == NumericUtils.obj2int(WeChatApiResponseCode.USER_NOT_IN_COMPANY)) {
            FbUserCheck fbUserCheck = FbUserCheck.builder()
                    .isFbUser(false).build();
            return fbUserCheck;
        } else {
            throw new OpenApiPluginException(code, openApiResponse.getMsg());
        }
    }


    /**
     * 调用api，获取返回结果
     *
     * @param url
     * @param object
     * @param paramMap
     * @return
     */
    public String postFormOkHttp(String url, Object object, Map<String, String> paramMap) {
        OkHttpClient okHttpClient = new OkHttpClient();
        log.info("创建分贝通申请用车审批单data数据: {}", JsonUtils.toJson(object));
        FormBody formBody = new FormBody.Builder()
                .add("access_token", paramMap.get("access_token"))
                .add("timestamp", paramMap.get("timestamp"))
                .add("sign", paramMap.get("sign"))
                .add("employee_id", paramMap.get("employee_id"))
                .add("employee_type", paramMap.get("employee_type"))
                .add("data", JsonUtils.toJsonSnake(object))
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        final Call call = okHttpClient.newCall(request);
        ResponseBody body = null;
        String result = "";
        try {
            Response response = call.execute();
            log.info("返回结果 {}", JsonUtils.toJson(response));
            body = response.body();
            result = body.string();
            log.info("调用开放平台 返回结果： {}", result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getOkHttp(String url) {

        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        final Call call = okHttpClient.newCall(request);
        ResponseBody body = null;
        String result = "";
        try {
            Response response = call.execute();
            log.info("返回结果 {}", JsonUtils.toJson(response));
            body = response.body();
            result = body.string();
            log.info("根据城市名称获取分贝城市信息 {}", result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private TripApproveCreateReqDTO wechatTripApplyProcessInfo2TripApproveCreateReqDTO(ApprovalInfo approvalInfo, String companyId) {
        ApprovalInfo.ApplyBean dingApply = approvalInfo.getApply();
        List<ApprovalInfo.CustomField> wechatCustomFields = approvalInfo.getCustomFields();
        List<ApprovalInfo.TripListBean> wechatTripList = approvalInfo.getTripList();
        List<GustUser> wechatGuestList = approvalInfo.getGuestList();
        TripApproveCreateReqDTO tripApproveCreateReqDTO = new TripApproveCreateReqDTO();
        TripApproveApply apply = new TripApproveApply();
        List<TypeEntity> customFields = new ArrayList<>();
        List<TripApproveDetail> tripList = new ArrayList<>();
        List<TripApproveGuest> guestList = new ArrayList<>();
        tripApproveCreateReqDTO.setApply(apply);
        tripApproveCreateReqDTO.setCustomFields(customFields);
        tripApproveCreateReqDTO.setTripList(tripList);
        tripApproveCreateReqDTO.setGuestList(guestList);
        apply.setType(dingApply.getType());
        apply.setFlowType(dingApply.getFlowType());
        apply.setBudget(dingApply.getBudget());
        apply.setThirdId(dingApply.getThirdId());
        apply.setThirdRemark(dingApply.getThirdRemark());
        apply.setApplyReason(dingApply.getApplyReason());
        apply.setCompanyId(companyId);
        apply.setApplyReasonDesc(dingApply.getApplyReasonDesc());
        if (!ObjectUtils.isEmpty(wechatCustomFields)) {
            for (ApprovalInfo.CustomField wechatCustomField : wechatCustomFields) {
                TypeEntity typeEntity = new TypeEntity();
                typeEntity.setType(wechatCustomField.getType());
                typeEntity.setValue(wechatCustomField.getValue());
                customFields.add(typeEntity);
            }
        }
        if (!ObjectUtils.isEmpty(wechatTripList)) {
            for (ApprovalInfo.TripListBean wechatTrip : wechatTripList) {
                TripApproveDetail tripApproveDetail = new TripApproveDetail();
                tripApproveDetail.setType(wechatTrip.getType());
                tripApproveDetail.setTripType(wechatTrip.getTripType());
                tripApproveDetail.setStartCityId(wechatTrip.getStartCityId());
                tripApproveDetail.setArrivalCityId(wechatTrip.getArrivalCityId());
                tripApproveDetail.setStartTime(DateUtils.toDate(wechatTrip.getStartTime()));
                tripApproveDetail.setEndTime(DateUtils.toDate(wechatTrip.getEndTime()));
                tripApproveDetail.setBackStartTime(DateUtils.toDate(wechatTrip.getBackStartTime()));
                tripApproveDetail.setBackEndTime(DateUtils.toDate(wechatTrip.getBackEndTime()));
                tripApproveDetail.setEstimatedAmount(wechatTrip.getEstimatedAmount());
                tripList.add(tripApproveDetail);
            }
        }
        if (!ObjectUtils.isEmpty(guestList)) {
            for (GustUser wechatGustUser : wechatGuestList) {
                TripApproveGuest tripApproveGuest = new TripApproveGuest();
                tripApproveGuest.setId(wechatGustUser.getId());
                tripApproveGuest.setIsEmployee(wechatGustUser.getIsEmployee());
                tripApproveGuest.setName(wechatGustUser.getName());
                tripApproveGuest.setPhoneNum(wechatGustUser.getPhoneNum());
                guestList.add(tripApproveGuest);
            }
        }
        return tripApproveCreateReqDTO;
    }

}
