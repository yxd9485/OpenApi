package com.fenbeitong.openapi.plugin.func.altman.service;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.fenbeimeta.sdk.enums.common.MetaCategoryTypeEnum;
import com.fenbeitong.fenbeimeta.sdk.enums.common.SystemEnum;
import com.fenbeitong.fenbeimeta.sdk.model.vo.data.DataFieldSimpleVO;
import com.fenbeitong.fenbeimeta.sdk.model.vo.data.DataListSimpleVO;
import com.fenbeitong.noc.api.service.altman.model.dto.req.AltmanOpenCreateReqRpcDTO;
import com.fenbeitong.noc.api.service.altman.model.dto.req.AltmanOrderStereoListReqRpcDTO;
import com.fenbeitong.noc.api.service.altman.model.dto.resp.AltmanOpenCreateResRpcDTO;
import com.fenbeitong.noc.api.service.altman.model.dto.resp.AltmanOrderStereoListResRpcDTO;
import com.fenbeitong.noc.api.service.altman.model.vo.AltmanConsumerInfoVO;
import com.fenbeitong.noc.api.service.altman.model.vo.AltmanOrderStereoInfoVO;
import com.fenbeitong.noc.api.service.altman.model.vo.AltmanStereoInfoVO;
import com.fenbeitong.noc.api.service.altman.model.vo.AltmanUserInfoVO;
import com.fenbeitong.noc.api.service.altman.service.IAltmanOrderSearchService;
import com.fenbeitong.noc.api.service.altman.service.IAltmanOrderService;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.func.altman.dto.OpenAltmanOrderCreateDTO;
import com.fenbeitong.openapi.plugin.func.altman.dto.OpenAltmanOrderDetailDTO;
import com.fenbeitong.openapi.plugin.func.altman.dto.OpenAltmanOrderListReqDTO;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseCode;
import com.fenbeitong.openapi.plugin.func.dao.OpenAltmanOrderConfigDao;
import com.fenbeitong.openapi.plugin.func.employee.service.FuncEmployeeService;
import com.fenbeitong.openapi.plugin.func.entity.OpenAltmanOrderConfig;
import com.fenbeitong.openapi.plugin.func.exception.OpenApiFuncException;
import com.fenbeitong.openapi.plugin.func.order.dto.AltmanOrderRefundDetailDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.AltmanOrderRefundListReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.OrderSaasInfoDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.OrderThirdInfoDTO;
import com.fenbeitong.openapi.plugin.support.apply.constant.ItemCodeEnum;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.company.service.impl.UcCompanyServiceImpl;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.meta.service.CommonMetaService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.support.util.ApiJwtTokenTool;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.*;
import com.fenbeitong.usercenter.api.model.dto.common.CommonIdDTO;
import com.fenbeitong.usercenter.api.model.dto.common.CommonInfoReqDTO;
import com.fenbeitong.usercenter.api.model.dto.common.CommonInfoResDTO;
import com.fenbeitong.usercenter.api.model.dto.company.CompanyNewDto;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.model.enums.common.IdBusinessTypeEnums;
import com.fenbeitong.usercenter.api.model.enums.common.IdTypeEnums;
import com.fenbeitong.usercenter.api.service.common.ICommonService;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * altmanOrder的实现类
 *
 * @author xiaowei
 * @date 2020/05/20
 */
@ServiceAspect
@Service
@Slf4j
public class FuncAltmanOrderImpl implements IFuncAltmanOrderService {

    private static final int ORDER_CHANNEL = 23;
    private static final int ACCOUNT_TYPE = 1;
    private static final String PARAM_ERROR = "改价时，原订单号不能为空";
    private static final String PARAM_DATE_ERROR = "时间格式不正确";
    private static final Long DEIBANG_LIST_CONFIGID = 1050l;
    private static final Long DEIBANG_DETAIL_CONFIGID = 1060l;

    @Autowired
    private ApiJwtTokenTool jwtTokenTool;
    @Autowired
    private AuthDefinitionDao authDefinitionDao;
    @Autowired
    FuncEmployeeService funcEmployeeService;
    //url = "dubbo://192.168.8.203:20880"
    @DubboReference(check = false)
    private IAltmanOrderService altmanOrderService;
    //url = "dubbo://192.168.8.203:20880"
    @DubboReference(check = false)
    private IAltmanOrderSearchService altmanOrderSearchService;
    @Autowired
    private UcCompanyServiceImpl companyService;
    @Autowired
    private IEtlService etlService;

    @Autowired
    private OpenAltmanOrderConfigDao openAltmanOrderConfigDao;

    @DubboReference(check = false)
    private IBaseEmployeeExtService employeeExtService;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Autowired
    private CommonMetaService commonMetaService;

    @DubboReference(check = false)
    private ICommonService commonService;

    @Override
    public String saveAltmanOrder(ApiRequest apiRequest) throws BindException {
        OpenAltmanOrderCreateDTO openAltmanOrderCreateDTO = JsonUtils.toObj(apiRequest.getData(), OpenAltmanOrderCreateDTO.class);

        //校验下参数
        ValidatorUtils.validateBySpring(openAltmanOrderCreateDTO);
        checkData(openAltmanOrderCreateDTO);
        AuthDefinition authDefinition = getAuthByToken(apiRequest.getAccessToken());
        log.info("altmanOrder create compnayId : " + authDefinition.getAppId() + " data: " + JsonUtils.toJson(openAltmanOrderCreateDTO));
        return convert(authDefinition, openAltmanOrderCreateDTO);
    }

    @Override
    public Object listAltmanOrder(OpenAltmanOrderListReqDTO req, String apiVersion) throws BindException {
        //校验下参数
        ValidatorUtils.validateBySpring(req);
        Map<String, Object> results = new HashMap<>();
        AltmanOrderStereoListResRpcDTO stereoList = altmanOrderSearchService.stereoList(assemblyParam(req, req.getCompany_id()));
        if (stereoList != null && stereoList.getResults() != null && stereoList.getResults().size() > 0) {
            List<Map> transferList = etlService.transform(DEIBANG_LIST_CONFIGID, JsonUtils.toObj(JsonUtils.toJson(stereoList.getResults()), new TypeReference<List<Map<String, Object>>>() {
            }));
            if (!ObjectUtils.isEmpty(transferList) && "v_1.0".equals(apiVersion)) {
                transferList.forEach(rowMap -> {
                    rowMap.put("order_info", rowMap.get("order_Info"));
                    rowMap.remove("order_Info");
                });
            }
            results.put("results", transferList);
            results.put("page_index", stereoList.getPageIndex());
            results.put("page_size", stereoList.getPageSize());
            results.put("total_count", stereoList.getTotalCount());
            results.put("total_pages", (stereoList.getTotalCount() + stereoList.getPageSize() - 1) / stereoList.getPageSize());
        }
        return results;
    }

    private AltmanOrderStereoListReqRpcDTO assemblyParam(OpenAltmanOrderListReqDTO listReq, String companyId) {
        AltmanOrderStereoListReqRpcDTO stereoReq = new AltmanOrderStereoListReqRpcDTO();
        stereoReq.setCompanyId(companyId);
        stereoReq.setOrderType(listReq.getOrderType());
        stereoReq.setConsumerPhone(!StringUtils.isBlank(listReq.getConsumer_phone()) ? listReq.getConsumer_phone() : null);
        stereoReq.setConsumerName(!StringUtils.isBlank(listReq.getConsumer_name()) ? listReq.getConsumer_name() : null);
        stereoReq.setOrderId(!StringUtils.isBlank(listReq.getOrder_id()) ? listReq.getOrder_id() : null);
        stereoReq.setPageIndex(listReq.getPage_index() > 0 ? listReq.getPage_index() : 1);
        stereoReq.setPageSize((listReq.getPage_size() > 0 && listReq.getPage_size() <= 500) ? listReq.getPage_size() : 10);
        stereoReq.setCreateBegin(!StringUtils.isBlank(listReq.getCreate_time_begin()) ? listReq.getCreate_time_begin() : null);
        stereoReq.setCreateEnd(!StringUtils.isBlank(listReq.getCreate_time_end()) ? listReq.getCreate_time_end() : null);
        stereoReq.setRequestType("stereo");
        stereoReq.setCurrentOperatorId("openapi");
        stereoReq.setCurrentOperatorName("openapi");
        return stereoReq;
    }

    @Override
    public Object getAltmanOrder(OpenAltmanOrderDetailDTO req, String apiVersion) throws BindException {
        //校验下参数
        ValidatorUtils.validateBySpring(req);
        AltmanStereoInfoVO fenbeinoc = altmanOrderSearchService.stereoDetail(req.getOrder_id(), null, "stereo", "openapi", "openapi");
        log.info("万能订单详情查询返回：{}", JsonUtils.toJson(fenbeinoc));
        // 定制化接口不走下面的逻辑 直接转发到自己的接口中 获取结果并返回
        List<OpenMsgSetup> openMsgSetups = openMsgSetupDao.listByCompanyIdAndItemCodeList(req.getCompany_id(), Lists.newArrayList(ItemCodeEnum.ALTMAN_ORDER_CUSTOM.getCode()));
        if (!ObjectUtils.isEmpty(openMsgSetups)) {
            return getCustomResult(fenbeinoc, openMsgSetups);
        } else {
            // 增加自定义字段返回
            if (fenbeinoc == null) {
                return new HashMap<>();
            }

            Map sourceMap = JsonUtils.toObj(JsonUtils.toJson(fenbeinoc), Map.class);

            List<Map<String, Map<String, Object>>> extList = null;
            try {
                extList = getExtList(req, fenbeinoc);
            } catch (Exception e) {
                log.warn("获取自定义字段失败:{}", e.toString());
            }

            if (!ObjectUtils.isEmpty(extList)) {
                sourceMap.put("ext_list", extList);
            }

            Map transformMap = etlService.transform(DEIBANG_DETAIL_CONFIGID, sourceMap);
            if (!ObjectUtils.isEmpty(transformMap) && "v_1.0".equals(apiVersion)) {
                transformMap.put("order_info", transformMap.get("order_Info"));
                transformMap.remove("order_Info");
            }
            if (!ObjectUtils.isEmpty(transformMap)) {
                EmployeeContract employeeContract = employeeExtService.queryEmployeeInfo(fenbeinoc.getOrderStereoInfoVO().getUserId(), fenbeinoc.getOrderStereoInfoVO().getCompanyId());
                if (employeeContract != null) {
                    Map<String, Object> orderInfo = (Map) transformMap.get("order_info");
                    if (orderInfo != null) {
                        orderInfo.put("employee_number", employeeContract.getEmployee_number());
                    }
                }
                setThirdInfo(transformMap, req.getCompany_id());
                AltmanOrderStereoInfoVO orderInfo =  fenbeinoc.getOrderStereoInfoVO();
                Map tarPriceInfo = (Map) MapUtils.getValueByExpress(transformMap, "price_info");

                if (!ObjectUtils.isEmpty(orderInfo) && !ObjectUtils.isEmpty(tarPriceInfo)) {
                    BigDecimal amountCompany = BigDecimalUtils.obj2big(orderInfo.getCompanyAccountPay(), BigDecimal.ZERO);
                    BigDecimal amountRedcoupon = BigDecimalUtils.obj2big(orderInfo.getCompanyRedcoupon(), BigDecimal.ZERO);
                    BigDecimal companyTotalPay = amountCompany.add(amountRedcoupon);
                    tarPriceInfo.put("company_total_pay", companyTotalPay);
                }
            }
            return transformMap;
        }
    }

    /**
     * 获取自定义字段映射
     * 格式：{"ext_list":[{"ext1":{"val":"213","desc":"自定义字段1"}},{"ext1":{"val":"213","desc":"自定义字段1"}}]}
     *
     * @param req
     * @param fenbeinoc
     * @return
     */
    private List<Map<String, Map<String, Object>>> getExtList(OpenAltmanOrderDetailDTO req, AltmanStereoInfoVO fenbeinoc) {
        List<Map<String, Map<String, Object>>> list = new ArrayList<>();
        AltmanOrderStereoInfoVO orderStereoInfoVO = fenbeinoc.getOrderStereoInfoVO();
        if (orderStereoInfoVO != null) {
            String extStr = orderStereoInfoVO.getExt();
            if (!StringUtils.isBlank(extStr)) {
                // key:自定义字段key  val:值
                Map<String, Object> extMap = JsonUtils.toObj(extStr, new TypeReference<Map<String, Object>>() {
                });
                if (!ObjectUtils.isEmpty(extMap)) {
                    List<Map<String, Object>> extList = new ArrayList<>();
                    extList.add(extMap);
                    DataListSimpleVO dataList = commonMetaService.getDataList(req.getCompany_id(), SystemEnum.FENBEI_NOC, MetaCategoryTypeEnum.ULTRA_MAN, JsonUtils.toJson(extList));
                    if (dataList != null) {
                        List<DataFieldSimpleVO> fieldDescribeList = dataList.getFieldDescribeList();
                        if (!ObjectUtils.isEmpty(fieldDescribeList)) {
                            // key:自定义字段key  val:定义
                            Map<String, String> descMapping = fieldDescribeList.stream().collect(Collectors.toMap(DataFieldSimpleVO::getFieldApiName, DataFieldSimpleVO::getFieldName));
                            Set<String> extMapKeys = extMap.keySet();
                            int i = 0;
                            for (String extMapKey : extMapKeys) {
                                Object val = extMap.get(extMapKey);
                                String desc = descMapping.get(extMapKey);
                                Map<String, Map<String, Object>> extInfo = new HashMap<>();
                                Map<String, Object> childMap = new HashMap<>();
                                childMap.put("val", val);
                                childMap.put("desc", desc);
                                extInfo.put("ext" + i, childMap);
                                list.add(extInfo);
                                i++;
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * 转发到定制化接口获取结果
     *
     * @param fenbeinoc
     * @param openMsgSetups
     * @return
     */
    private Object getCustomResult(AltmanStereoInfoVO fenbeinoc, List<OpenMsgSetup> openMsgSetups) {
        String url = openMsgSetups.get(0) != null ? openMsgSetups.get(0).getStrVal1() : "";
        if (StringUtils.isBlank(url)) {
            throw new OpenApiArgumentException("定制万能订单没有配置转换url");
        }
        String result = RestHttpUtils.postJson(url, JsonUtils.toJson(fenbeinoc));
        if (!StringUtils.isBlank(result)) {
            Map map = JsonUtils.toObj(result, Map.class);
            return !ObjectUtils.isEmpty(map) ? map.get("data") : new HashMap<>();
        } else {
            return new HashMap<>();
        }
    }


    private String convert(AuthDefinition authDefinition, OpenAltmanOrderCreateDTO openAltmanOrderCreateDTO) {
        Map<String, Object> condition = new HashMap<>();
        String companyId = authDefinition.getAppId();
        condition.put("companyId", companyId);
        CompanyNewDto companyNewDto = companyService.getCompanyService().queryCompanyNewByCompanyId(companyId);
        OpenAltmanOrderConfig openAltmanOrderConfig = openAltmanOrderConfigDao.getOpenAltmanOrderConfig(condition);
        if (openAltmanOrderConfig == null) {
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.QUERY_THIRD_ALTMAN_CONFIG_ERROR));
        }
        AltmanOpenCreateReqRpcDTO req = new AltmanOpenCreateReqRpcDTO();
        //必填字段
        req.setOrderName(openAltmanOrderConfig.getOrderName());
        req.setOrderSnapshot(openAltmanOrderConfig.getOrderSnapshot());
        req.setTotalPrice(openAltmanOrderCreateDTO.getTotal_price());//订单总价
        req.setPayPrice(openAltmanOrderCreateDTO.getPay_price() == null ? openAltmanOrderCreateDTO.getTotal_price() : openAltmanOrderCreateDTO.getPay_price());//支付价格（支付价格=订单总价-优惠券价格）
        req.setCostPrice(openAltmanOrderCreateDTO.getCost_price() == null ? openAltmanOrderCreateDTO.getTotal_price() : openAltmanOrderCreateDTO.getCost_price());//订单采购价

        req.setTotalDiscount(openAltmanOrderCreateDTO.getTotal_discount() == null ? new BigDecimal(0) : openAltmanOrderCreateDTO.getTotal_discount());
        req.setCompanyTotalPay(openAltmanOrderCreateDTO.getCompany_total_pay() == null ? openAltmanOrderCreateDTO.getCompany_total_pay() : openAltmanOrderCreateDTO.getCompany_total_pay());
        req.setPersonalTotalPay(openAltmanOrderCreateDTO.getPersonal_total_pay() == null ? new BigDecimal(0) : openAltmanOrderCreateDTO.getPersonal_total_pay());
        req.setSupplierTime(DateUtils.toDate(openAltmanOrderCreateDTO.getOrder_pay_time(), DateUtils.FORMAT_DATE_TIME_PATTERN));

        req.setSupplierId(openAltmanOrderConfig.getSupplierId());//供应商ID 3
        req.setSupplierName(openAltmanOrderConfig.getSupplierName());//供应商名称 曹操
        req.setSupplierOrderId(openAltmanOrderCreateDTO.getSupplier_order_id());//供应商订单号
        req.setOrderTypeClassify(openAltmanOrderConfig.getOrderTypeClassify());//业务类别ID
        req.setOrderTypeClassifyName(openAltmanOrderConfig.getOrderTypeClassifyName());//业务类别名称
        req.setOrderTypeName(openAltmanOrderConfig.getOrderTypeName());//业务名称
        req.setOrderTypeDesc(openAltmanOrderConfig.getOrderTypeDesc());//业务描述
        req.setAccountType(ACCOUNT_TYPE);//帐号类型 1 因公 2 因私
        req.setOrderChannel(ORDER_CHANNEL);//订单来源
        List<OpenAltmanOrderCreateDTO.Consumer> consumers = openAltmanOrderCreateDTO.getConsumers();
        AltmanUserInfoVO altmanUserInfoVO = new AltmanUserInfoVO();
        altmanUserInfoVO.setCompanyId(companyId);
        altmanUserInfoVO.setCompanyName(companyNewDto.getCompanyName());
        altmanUserInfoVO.setUserName(StringUtils.isBlank(openAltmanOrderCreateDTO.getBook_user_name()) ? consumers.get(0).getConsumer_phone() : openAltmanOrderCreateDTO.getBook_user_name());
        altmanUserInfoVO.setUserPhone(StringUtils.isBlank(openAltmanOrderCreateDTO.getBook_user_phone()) ? consumers.get(0).getConsumer_phone() : openAltmanOrderCreateDTO.getBook_user_phone());
        altmanUserInfoVO.setUserFullUnitName(StringUtils.isBlank(openAltmanOrderCreateDTO.getBook_department()) ? consumers.get(0).getConsumer_department() : openAltmanOrderCreateDTO.getBook_department());
        //AltmanOperaterInfoVO altmanOperaterInfoVO = new AltmanOperaterInfoVO();
        //AltmanCostInfoVO altmanCostInfoVO = new AltmanCostInfoVO();
        List<AltmanConsumerInfoVO> consumerInfoVOList = new ArrayList<>();
        consumers.forEach(consumer -> {
            AltmanConsumerInfoVO altmanConsumerInfoVO = new AltmanConsumerInfoVO();
            altmanConsumerInfoVO.setConsumerPhone(consumer.getConsumer_phone());
            altmanConsumerInfoVO.setConsumerName(consumer.getConsumer_name());
            altmanConsumerInfoVO.setConsumerUnitName(consumer.getConsumer_department());
            consumerInfoVOList.add(altmanConsumerInfoVO);
        });

        req.setAltmanUserInfoVO(altmanUserInfoVO);
        req.setConsumerInfoVOList(consumerInfoVOList);

        JSONObject extField = new JSONObject();
        extField.put("supplierOrderId", openAltmanOrderCreateDTO.getSupplier_order_id());
        extField.put("totalPrice", openAltmanOrderCreateDTO.getTotal_price());
        extField.put("consumerPhone", openAltmanOrderCreateDTO.getConsumers().get(0).getConsumer_phone());
        extField.put("orderTime", openAltmanOrderCreateDTO.getOperator_order_time());
        extField.put("onCarTime", openAltmanOrderCreateDTO.getStart_time());
        extField.put("offCarTime", openAltmanOrderCreateDTO.getEnd_time());
        extField.put("payDeBangTime", openAltmanOrderCreateDTO.getOrder_pay_time());
        extField.put("payFenbeiTime", DateUtils.toSimpleStr(new Date()));
        extField.put("originPlace", openAltmanOrderCreateDTO.getStart_destination());
        extField.put("destinationPlace", openAltmanOrderCreateDTO.getEnd_destination());
        req.setExt(extField.toString());//订单扩展字段
        req.setBusinessMode(openAltmanOrderConfig.getBusinessType());//业务模式:1.pop 2托管 3采销
        req.setInvoiceProvideStatus(openAltmanOrderConfig.getInvoicProvideStatus());//发票提供的状态1、提供 0、不提供
        req.setInvoiceProvideType(openAltmanOrderConfig.getInvoiceProvideType());//开票方 1、遵循开票规则 2、回填所选供应商名称'
        req.setInvoiceProvideName(openAltmanOrderConfig.getInvoiceProvideName());//开票方名称 / 遵循开票规则 / 回填所选供应商名称
        req.setSceneInvoiceType(openAltmanOrderConfig.getSceneInvoiceType());//开票的类型 1、专票 2、普票/电子票 27、企业配置
        AltmanOpenCreateResRpcDTO altmanOpenCreateResRpcDTO = null;
        if ("0".equals(openAltmanOrderCreateDTO.getOrder_type())) {
            altmanOpenCreateResRpcDTO = altmanOrderService.createOpenOrder(req);
        } else if ("1".equals(openAltmanOrderCreateDTO.getOrder_type())) {
            if (openAltmanOrderCreateDTO.getOrigin_order_id() == null) {
                throw new OpenApiArgumentException(PARAM_ERROR);
            }
            req.setOriginOrderId(openAltmanOrderCreateDTO.getOrigin_order_id());
            altmanOpenCreateResRpcDTO = altmanOrderService.updateOrderPrice(req);
        }
        return altmanOpenCreateResRpcDTO.getOrderId();
    }


    private AuthDefinition getAuthByToken(String accessToken) {
        DecodedJWT jwt = jwtTokenTool.verifyToken(accessToken);
        String appId = jwt.getClaim("appId").asString();
        return authDefinitionDao.getAuthInfoByAppId(appId);
    }

    private void checkData(OpenAltmanOrderCreateDTO openAltmanOrderCreateDTO) {
        try {
            if (DateUtils.toDate(openAltmanOrderCreateDTO.getStart_time(), DateUtils.FORMAT_DATE_TIME_PATTERN) == null ||
                DateUtils.toDate(openAltmanOrderCreateDTO.getEnd_time(), DateUtils.FORMAT_DATE_TIME_PATTERN) == null ||
                DateUtils.toDate(openAltmanOrderCreateDTO.getOperator_order_time(), DateUtils.FORMAT_DATE_TIME_PATTERN) == null ||
                DateUtils.toDate(openAltmanOrderCreateDTO.getOrder_pay_time(), DateUtils.FORMAT_DATE_TIME_PATTERN) == null
            ) {
                throw new OpenApiArgumentException(PARAM_DATE_ERROR);
            }
        } catch (Exception e) {
            log.warn("data parse error", e);
            throw new OpenApiArgumentException(e.getMessage());
        }

    }

    @Override
    public Object refundList(AltmanOrderRefundListReqDTO req, String apiVersion) throws BindException {
        //校验下参数
        ValidatorUtils.validateBySpring(req);
        AltmanOrderStereoListReqRpcDTO stereoReq = new AltmanOrderStereoListReqRpcDTO();
        stereoReq.setPageIndex(req.getPageIndex() == null ? 1 : req.getPageIndex());
        stereoReq.setPageSize(req.getPageSize());
        stereoReq.setOrderId(req.getOrderId());
        stereoReq.setRefundOrderId(req.getRefundOrderId());
        stereoReq.setPageSize((req.getPageSize() != null && req.getPageSize() > 0 && req.getPageSize() <= 500) ? req.getPageSize() : 10);
        stereoReq.setCreateBegin(!StringUtils.isBlank(req.getCreateTimeBegin()) ? req.getCreateTimeBegin() : null);
        stereoReq.setCreateEnd(!StringUtils.isBlank(req.getCreateTimeEnd()) ? req.getCreateTimeEnd() : null);
        stereoReq.setCompanyId(req.getCompanyId());
        stereoReq.setConsumerName(req.getConsumerName());
        stereoReq.setConsumerPhone(req.getConsumerPhone());
        stereoReq.setOrderStatus(req.getStatusList());
        stereoReq.setOrderType(req.getOrderType());
        stereoReq.setRequestType("stereo");
        stereoReq.setCurrentOperatorId("openapi");
        stereoReq.setCurrentOperatorName("openapi");
        Map<String, Object> results = new HashMap<>();
        AltmanOrderStereoListResRpcDTO stereoList = altmanOrderSearchService.stereoAfterList(stereoReq);
        if (stereoList != null && stereoList.getResults() != null && stereoList.getResults().size() > 0) {
            List<Map> transferList = etlService.transform(DEIBANG_LIST_CONFIGID, JsonUtils.toObj(JsonUtils.toJson(stereoList.getResults()), new TypeReference<List<Map<String, Object>>>() {
            }));
            transferList.forEach(rowMap -> {
                rowMap.put("order_info", rowMap.get("order_Info"));
                rowMap.remove("order_Info");
            });
            results.put("results", transferList);
            results.put("page_index", stereoList.getPageIndex());
            results.put("page_size", stereoList.getPageSize());
            results.put("total_count", stereoList.getTotalCount());
            results.put("total_pages", (stereoList.getTotalCount() + stereoList.getPageSize() - 1) / stereoList.getPageSize());
        }
        return results;
    }

    @Override
    public Object refundDetail(AltmanOrderRefundDetailDTO req, String apiVersion) throws BindException {
        //校验下参数
        ValidatorUtils.validateBySpring(req);
        AltmanStereoInfoVO result = altmanOrderSearchService.stereoAfterDetail(req.getRefundOrderId(), null, "stereo", "openapi", "openapi");
        log.info("万能订单逆向单查询返回:{}", JsonUtils.toJson(result));
        Map transformMap = etlService.transform(DEIBANG_DETAIL_CONFIGID, JsonUtils.toObj(JsonUtils.toJson(result), Map.class));
        if (!ObjectUtils.isEmpty(transformMap) && "v_1.0".equals(apiVersion)) {
            transformMap.put("order_info", transformMap.get("order_Info"));
            transformMap.remove("order_Info");
        }
        if (!ObjectUtils.isEmpty(transformMap)) {
            EmployeeContract employeeContract = employeeExtService.queryEmployeeInfo(result.getOrderStereoInfoVO().getUserId(), result.getOrderStereoInfoVO().getCompanyId());
            if (employeeContract != null) {
                Map<String, Object> orderInfo = (Map) transformMap.get("order_info");
                if (orderInfo != null) {
                    orderInfo.put("employee_number", employeeContract.getEmployee_number());
                }
            }
            //设置三方信息
            setThirdInfo(transformMap, req.getCompanyId());
            AltmanOrderStereoInfoVO orderInfo =  result.getOrderStereoInfoVO();
            Map tarPriceInfo = (Map) MapUtils.getValueByExpress(transformMap, "price_info");

            if (!ObjectUtils.isEmpty(orderInfo) && !ObjectUtils.isEmpty(tarPriceInfo)) {
                BigDecimal amountCompany = BigDecimalUtils.obj2big(orderInfo.getCompanyAccountPay(), BigDecimal.ZERO);
                BigDecimal amountRedcoupon = BigDecimalUtils.obj2big(orderInfo.getCompanyRedcoupon(), BigDecimal.ZERO);
                BigDecimal companyTotalPay = amountCompany.add(amountRedcoupon);
                tarPriceInfo.put("company_total_pay", companyTotalPay);
            }
        }
        return transformMap;
    }

    private void setThirdInfo(Map<String, Object> altmanOrderMap, String companyId) {
        try {
            if (!ObjectUtils.isEmpty(altmanOrderMap)) {
                String userId = StringUtils.obj2str(MapUtils.getValueByExpress(altmanOrderMap, "order_info:user_id"));
                String unitId = StringUtils.obj2str(MapUtils.getValueByExpress(altmanOrderMap, "order_info:unit_id"));
                List<OrderSaasInfoDTO.CostAttribution> costAttributionList = JsonUtils.toObj(JsonUtils.toJson(MapUtils.getValueByExpress(altmanOrderMap, "cost_info")), new TypeReference<List<OrderSaasInfoDTO.CostAttribution>>() {
                });

                OrderThirdInfoDTO orderThirdInfoDTO = new OrderThirdInfoDTO();
                CommonInfoReqDTO req = new CommonInfoReqDTO();
                req.setCompanyId(companyId);
                req.setType(IdTypeEnums.FB_ID.getKey());
                req.setBusinessType(IdBusinessTypeEnums.EMPLOYEE.getKey());
                req.setIdList(Lists.newArrayList(userId));
                List<CommonInfoResDTO> employeeList = commonService.queryCommonInfoByType(req);
                if (!ObjectUtils.isEmpty(employeeList)) {
                    orderThirdInfoDTO.setUserId(employeeList.get(0).getThirdId());
                }

                req.setType(IdTypeEnums.FB_ID.getKey());
                req.setBusinessType(IdBusinessTypeEnums.ORG.getKey());
                req.setIdList(Lists.newArrayList(unitId));
                List<CommonInfoResDTO> orgUnitList = commonService.queryCommonInfoByType(req);
                if (!ObjectUtils.isEmpty(orgUnitList)) {
                    orderThirdInfoDTO.setUnitId(orgUnitList.get(0).getThirdId());
                }
                //设置三方费用归属信息
                if (!ObjectUtils.isEmpty(costAttributionList)) {
                    Map<Integer, List<OrderSaasInfoDTO.CostAttribution>> categoryMap = costAttributionList.stream().collect(Collectors.groupingBy(c -> c.getCostAttributionCategory()));
                    for (Map.Entry<Integer, List<OrderSaasInfoDTO.CostAttribution>> entry : categoryMap.entrySet()) {
                        Integer category = entry.getKey();
                        List<OrderSaasInfoDTO.CostAttribution> costListByGroup = entry.getValue();
                        List<String> idList = costListByGroup.stream().map(c -> StringUtils.obj2str(c.getCostAttributionId())).collect(Collectors.toList());
                        List<CommonIdDTO> idDtoList = ObjectUtils.isEmpty(idList) ? null : commonService.queryIdDTO(companyId, idList, 1, category);
                        Map<String, CommonIdDTO> commonIdDtoMap = ObjectUtils.isEmpty(idDtoList) ? null : idDtoList.stream().collect(Collectors.toMap(CommonIdDTO::getId, Function.identity()));
                        if (!ObjectUtils.isEmpty(commonIdDtoMap)) {
                            idList.forEach(id -> {
                                CommonIdDTO commonIdDto = commonIdDtoMap.get(id);
                                String thirdId = commonIdDto == null ? null : commonIdDto.getThirdId();
                                if (!StringUtils.isBlank(thirdId)) {
                                    if (category == 1) {
                                        orderThirdInfoDTO.setCostDeptId(thirdId);
                                    } else {
                                        orderThirdInfoDTO.setCostProjectId(thirdId);
                                    }
                                }
                            });
                        }
                    }
                }
                altmanOrderMap.put("third_info", JsonUtils.toObj(JsonUtils.toJson(orderThirdInfoDTO), Map.class));
            }
        } catch (Exception e) {
            log.info(">>>万能订单获取第三方信息接口调用时异常:{}>>>", e);
            altmanOrderMap.put("third_info", null);
        }
    }
}
