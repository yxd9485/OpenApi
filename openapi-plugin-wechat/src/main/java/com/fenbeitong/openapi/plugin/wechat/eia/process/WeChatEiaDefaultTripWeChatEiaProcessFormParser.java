package com.fenbeitong.openapi.plugin.wechat.eia.process;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.support.apply.constant.OrderSceneType;
import com.fenbeitong.openapi.plugin.support.apply.constant.TripRoundType;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.dto.ApprovalInfo;
import com.fenbeitong.openapi.plugin.wechat.common.dto.FbUserCheck;
import com.fenbeitong.openapi.plugin.wechat.common.dto.GustUser;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatApprovalDetail;
import com.fenbeitong.openapi.plugin.wechat.common.notice.sender.WeChatNoticeSender;
import com.fenbeitong.openapi.plugin.wechat.eia.enums.FbTripType;
import com.fenbeitong.openapi.plugin.wechat.eia.enums.WeChatApplyContentControl;
import com.fenbeitong.openapi.plugin.wechat.eia.enums.WeChatApplyType;
import com.fenbeitong.openapi.plugin.wechat.eia.service.openapi.WeChatEiaPluginCallOpenApiService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by dave.hansins on 19/12/16.
 */
@ServiceAspect
@Service("weChatEiaDefaultTripWeChatEiaProcessFormParser")
@Slf4j
public class WeChatEiaDefaultTripWeChatEiaProcessFormParser implements IWeChatEiaProcessFormParser {

    private static Cache<String, List> cache = CacheBuilder.newBuilder()
        .expireAfterWrite(120, TimeUnit.MINUTES)
        .maximumSize(1000)
        .build();

    private static String SCENE = "";
    private static final int HOTEL_AREA_TYPE_CITY = 2;
    private static final String TRIP_TYPE = "交通工具";
    private static final String SINGLE_TYPE = "单程往返";
    private static final String SINGLE = "单程";
    private static final String ROUND = "往返";
    private static final String BEGIN_CITY = "出发城市";
    private static final String END_CITY = "目的城市";
    private static final String BEGIN_DATE = "开始日期";
    private static final String END_DATE = "结束日期";
    private static final String APPLY_REASON = "申请事由";
    private static final String TRIP_APPLY = "差旅审批";
    private static final String TRIP_USER = "出行人";
    private static final String BEGIN_CITY_CODE = "出发城市三字码";
    private static final String END_CITY_CODE = "目的城市三字码";

    @Autowired
    PluginCorpDefinitionDao pluginCorpDefinitionDao;
    @Autowired
    WeChatEiaPluginCallOpenApiService weChatEiaPluginCallOpenApiService;
    @Autowired
    WeChatNoticeSender weChatNoticeSender;
    @Autowired
    OpenMsgSetupDao openMsgSetupDao;
    @Autowired
    private CommonApplyServiceImpl commonApplyService;
    @Autowired
    private OpenEmployeeServiceImpl openEmployeeService;
    @Override
    public ApprovalInfo parse(String companyId, int applyType, String instanceId, WeChatApprovalDetail.WeChatApprovalInfo approvalInfo) {
        ApprovalInfo processInfo = new ApprovalInfo();
        //不为空时进行具体数据解析
        if (!ObjectUtils.isEmpty(approvalInfo)) {
            // 行程表单信息
            List<ApprovalInfo.TripListBean> tripListBeans = this.getTripListBean(companyId, applyType, approvalInfo, processInfo);
            if (ObjectUtils.isEmpty(tripListBeans)) {
                return null;
            }
            processInfo.setTripList(tripListBeans);
        }
        return processInfo;
    }

    /**
     * 获取具体行程信息集合
     *
     * @return
     */
    private List<ApprovalInfo.TripListBean> getTripListBean(String companyId, int applyType, WeChatApprovalDetail.WeChatApprovalInfo approvalInfo, ApprovalInfo processInfo) {
        WeChatApprovalDetail.Applyer applyer = approvalInfo.getApplyer();
        List<ApprovalInfo.TripListBean> tripBeans = Lists.newArrayList();
        List<GustUser> guestList = Lists.newArrayList();
        String spNo = approvalInfo.getSpNo();
        if (ObjectUtils.isEmpty(applyer)) {
            return null;
        }
        String userId = applyer.getUserId();
        log.info("审批单创建单号：{},人员ID: {},部门ID: {}", spNo, userId, applyer.getPartyId());
        WeChatApprovalDetail.ApplyData applyData = approvalInfo.getApplyData();
        if (ObjectUtils.isEmpty(applyData)) {
            return null;
        }
        List<WeChatApprovalDetail.Content> contens = applyData.getContens();
        if (!ObjectUtils.isEmpty(contens)) {
            // 设置审批信息
            ApprovalInfo.ApplyBean apply = new ApprovalInfo.ApplyBean();
            //进行具体行程数据解析，包括机票，酒店，火车的行程出发目的城市，出发目的时间，金额相关信息
            List<String> sceneList = Lists.newArrayList();
            String departureCityCode = "";
            String arrivedCityCode = "";
            String departureDate = "";
            String arrivedDate = "";
            String departurePlace = "";
            String arrivedPlace = "";
            String thirdRemark = "";
            int tripRountType = TripRoundType.SingleTrip.getValue();
            apply.setType(applyType);
            //必须为4
            apply.setFlowType(4);
            apply.setThirdId(spNo);
            apply.setBudget(0);
            //默认为单程
            boolean isSingle = true;
            String admin = openEmployeeService.superAdmin(companyId);
            String employeeFbToken = openEmployeeService.getEmployeeFbToken(companyId, admin, "0");
            //出发时间配置 0-精确时间（天） 1-范围时间
            int departureDateConfig = commonApplyService.queryApplyDepartureDate(employeeFbToken);
            //根据公司查询是否为企业微信老用户，配置的数据为老用户，需要向上兼容
            List<OpenMsgSetup> companyWechatCompatibityApplyList = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList("company_wechat_compatibity_apply"));
            if (!ObjectUtils.isEmpty(companyWechatCompatibityApplyList)) {//老逻辑
                for (WeChatApprovalDetail.Content content : contens) {
                    String control = content.getControl();
                    List<WeChatApprovalDetail.Title> titles1 = content.getTitles();
                    WeChatApprovalDetail.Title title = titles1.get(0);
                    String text1 = title.getText();
                    String selectorText = "";
                    if (WeChatApplyContentControl.SELECTOR.getValue().equals(control)) {//选择器，包括行程的列表，具体审批包含哪些审批
                        WeChatApprovalDetail.ContentValue contentValue = content.getContentValue();
                        WeChatApprovalDetail.Selector selector = contentValue.getSelector();
                        String type = selector.getType();
                        List<WeChatApprovalDetail.Option> options = selector.getOptions();
                        for (WeChatApprovalDetail.Option option : options) {
                            List<WeChatApprovalDetail.Value> values = option.getValues();
                            for (WeChatApprovalDetail.Value value : values) {//具体的行程信息包含数据，机票，酒店，火车
                                selectorText = value.getText().trim();
                                //具体的场景名称
                                if (TRIP_TYPE.equals(text1)) {//为场景多选框
                                    sceneList.add(selectorText);
                                } else if (SINGLE_TYPE.equals(text1)) {
                                    if ("往返".equals(selectorText)) {//往返
                                        //isSingle = false;
                                        tripRountType = TripRoundType.RoungTrip.getValue();
                                    }
                                }
                            }
                        }
                        log.info("解析审批单具体场景 {}", JsonUtils.toJson(sceneList));
                    } else if (WeChatApplyContentControl.TEXT.getValue().equals(control)) {//出发和目的城市
                        List<WeChatApprovalDetail.Title> titles = content.getTitles();
                        String text = titles.get(0).getText();
                        WeChatApprovalDetail.ContentValue contentValue = content.getContentValue();
                        int tripType = 0;
                        if (WeChatApplyType.AIR.getMsg().equals(selectorText)) {//国内机票
                            tripType = FbTripType.AIR.getCode();
                        } else if (WeChatApplyType.TRAIN.getMsg().equals(selectorText)) {//火车
                            tripType = FbTripType.TRAIN.getCode();
                        } else if (WeChatApplyType.HOTEL.getMsg().equals(selectorText)) {//酒店
                            tripType = FbTripType.HOTEL.getCode();
                        } else if (WeChatApplyType.INTL_AIR.getMsg().equals(selectorText)) {//国际机票
                            tripType = FbTripType.INTL_AIR.getCode();
                        }
                        if (BEGIN_CITY.equals(text)) {
                            //获取出发城市名称
                            departurePlace = contentValue.getText().trim();
                            log.info("解析审批单具体出发城市 {}", departurePlace);
                            //根据城市名称获取城市code,获取哪些城市code，需要根据场景来进行区分
                            departureCityCode = getCityCodeByCityName(companyId, tripType, departurePlace);
                        } else if (END_CITY.equals(text)) {
                            //到达城市名称
                            arrivedPlace = contentValue.getText().trim();
                            arrivedCityCode = getCityCodeByCityName(companyId, tripType, arrivedPlace);
                            log.info("解析审批单具体目的城市 {}", departurePlace);
                            //国际机票，为城市code字段，并且三字码字段不为空
                        } else if (BEGIN_CITY_CODE.equals(text)) {
                            String citycode = contentValue.getText().trim();
                            if (StringUtils.isNotBlank(citycode)) {
                                boolean matches = citycode.matches("^[a-zA-Z]*");
                                if (matches) {//是英文，进行大小写转换
                                    String upperCaseCityCode = citycode.toUpperCase();
                                    //国际机票并且返回为空，有可能是国际城市名称重复导致无法查询到结果，需要根据国际城市三字码查询分贝城市code
                                    String intlCityInfoByCityCode =
                                        weChatEiaPluginCallOpenApiService.getIntlCityInfoByCityCode(companyId, userId,
                                            upperCaseCityCode);
                                    if (StringUtils.isNotBlank(intlCityInfoByCityCode)) {
                                        departureCityCode = intlCityInfoByCityCode;
                                    }
                                }
                            }
                        } else if (END_CITY_CODE.equals(text)) {
                            String citycode = contentValue.getText().trim();
                            if (StringUtils.isNotBlank(citycode)) {
                                boolean matches = citycode.matches("^[a-zA-Z]*");
                                if (matches) {//是英文，进行大小写转换
                                    String upperCaseCityCode = citycode.toUpperCase();
                                    String intlCityInfoByCityCode =
                                        weChatEiaPluginCallOpenApiService.getIntlCityInfoByCityCode(companyId, userId,
                                            upperCaseCityCode);
                                    if (StringUtils.isNotBlank(intlCityInfoByCityCode)) {
                                        arrivedCityCode = intlCityInfoByCityCode;
                                    }
                                }
                            }
                        }
                    } else if (WeChatApplyContentControl.DATE.getValue().equals(control)) {//出发和到达时间
                        List<WeChatApprovalDetail.Title> titles = content.getTitles();
                        String text = titles.get(0).getText();
                        WeChatApprovalDetail.ContentValue contentValue = content.getContentValue();
                        if (BEGIN_DATE.equals(text)) {
                            long sDepatureTimestamp = contentValue.getDate().getSTimestamp();
                            departureDate = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date(Long.parseLong(sDepatureTimestamp + "") * 1000));
                            log.info("解析审批单具体出发日期 {}", departureDate);
                        } else if (END_DATE.equals(text)) {
                            long sArrivedTimestamp = contentValue.getDate().getSTimestamp();
                            arrivedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date(Long.parseLong(sArrivedTimestamp + "") * 1000));
                            log.info("解析审批单具体到达日期 {}", arrivedDate);
                        }
                    } else if (WeChatApplyContentControl.TEXTAREA.getValue().equals(control)) {
                        List<WeChatApprovalDetail.Title> titles = content.getTitles();
                        String text = titles.get(0).getText();
                        WeChatApprovalDetail.ContentValue contentValue = content.getContentValue();
                        if (APPLY_REASON.equals(text)) {
                            thirdRemark = contentValue.getText();
                        }
                    } else if (WeChatApplyContentControl.CONTACT.getValue().equals(control)) {//同行人信息
                        List<WeChatApprovalDetail.Title> titles = content.getTitles();
                        WeChatApprovalDetail.ContentValue contentValue = content.getContentValue();
                        String text = titles.get(0).getText();
                        if (TRIP_USER.equals(text)) {
                            List<WeChatApprovalDetail.Member> members = contentValue.getMember();
                            for (WeChatApprovalDetail.Member member : members) {
                                //同行人信息
                                String userid = member.getUserid();
                                String name = member.getName();
                                FbUserCheck fbUserCheck = weChatEiaPluginCallOpenApiService.CheckUserInfo(companyId, userid);
                                if (fbUserCheck.isFbUser()) {//为分贝用户
                                    GustUser gustUser = GustUser.builder().id(fbUserCheck.getFbUserId())
                                        .name(fbUserCheck.getFbUserName())
                                        .phoneNum(fbUserCheck.getFbUserPhone())
                                        .isEmployee(fbUserCheck.isFbUser())
                                        .build();
                                    guestList.add(gustUser);
                                } else {
                                    log.info("同行人userId: {}", userid);
                                    String msg = name + "未开通分贝通账号导致创建分贝通差旅审批失败，请企业微信管理员处理后再次提交审批";
                                    weChatNoticeSender.sender(companyId, userId, msg);
                                    throw new OpenApiPluginException(NumericUtils.obj2int(WeChatApiResponseCode.USER_THIRD_ID_NOT_EXISTS));
                                }
                            }
                        }
                    }
                }
                //异常消息通知
                if (StringUtils.isBlank(departureCityCode) || StringUtils.isBlank(arrivedCityCode)) {
                    String msg = "通知: 您创建的" + departureDate + "到" + arrivedDate + "的" + departurePlace + "到" + arrivedPlace + "的分贝通差旅审批单，因审批单中的城市不符合分贝通标准创建失败，请您准确填写城市并提交审批";
                    weChatNoticeSender.sender(companyId, userId, msg);
                    return null;
                }
                //根据解析的场景创建具体的场景实体,后期优化,简化代码
                for (String scene : sceneList) {
                    ApprovalInfo.TripListBean tripListBean = null;
                    if (isSingle) {//单程
                        if (WeChatApplyType.HOTEL.getMsg().equals(scene)) {
                            tripListBean = ApprovalInfo.TripListBean.builder()
                                .type(FbTripType.HOTEL.getCode())
                                .startCityId(arrivedCityCode)
                                .arrivalCityId(arrivedCityCode)
                                .startTime(departureDate)
                                .endTime(arrivedDate)
                                .estimatedAmount(0)
                                .tripType(tripRountType)
                                .build();
                        } else {
                            if (WeChatApplyType.AIR.getMsg().equals(scene)) {
                                tripListBean = ApprovalInfo.TripListBean.builder()
                                    .type(FbTripType.AIR.getCode())
                                    .startCityId(departureCityCode)
                                    .arrivalCityId(arrivedCityCode)
                                    .startTime(departureDate)
                                    .endTime(arrivedDate)
                                    .estimatedAmount(0)
                                    .build();
                            } else if (WeChatApplyType.TRAIN.getMsg().equals(scene)) {//火车
                                tripListBean = ApprovalInfo.TripListBean.builder()
                                    .type(FbTripType.TRAIN.getCode())
                                    .startCityId(departureCityCode)
                                    .arrivalCityId(arrivedCityCode)
                                    .startTime(departureDate)
                                    .endTime(arrivedDate)
                                    .estimatedAmount(0)
                                    .build();
                            } else if (WeChatApplyType.INTL_AIR.getMsg().equals(scene)) {//国际机票
                                tripListBean = ApprovalInfo.TripListBean.builder()
                                    .type(FbTripType.INTL_AIR.getCode())
                                    .startCityId(departureCityCode)
                                    .arrivalCityId(arrivedCityCode)
                                    .startTime(departureDate)
                                    .endTime(arrivedDate)
                                    .estimatedAmount(0)
                                    .tripType(1)
                                    .build();
                            }
                        }
                        tripBeans.add(tripListBean);
                    } else {//往返
                        if (WeChatApplyType.HOTEL.getMsg().equals(scene)) {
                            ApprovalInfo.TripListBean tripListBean0 = ApprovalInfo.TripListBean.builder()
                                .type(FbTripType.HOTEL.getCode())
                                .startCityId(arrivedCityCode)
                                .arrivalCityId(arrivedCityCode)
                                .startTime(departureDate)
                                .endTime(arrivedDate)
                                .estimatedAmount(0)
                                .build();
                            tripBeans.add(tripListBean0);
                        } else {
                            if (WeChatApplyType.AIR.getMsg().equals(scene)) {
                                ApprovalInfo.TripListBean tripListBean1 = ApprovalInfo.TripListBean.builder()
                                    .type(FbTripType.AIR.getCode())
                                    .startCityId(departureCityCode)
                                    .arrivalCityId(arrivedCityCode)
                                    .startTime(departureDate)
                                    .endTime(arrivedDate)
                                    .estimatedAmount(0)
                                    .build();
                                ApprovalInfo.TripListBean tripListBean2 = ApprovalInfo.TripListBean.builder()
                                    .type(FbTripType.AIR.getCode())
                                    .startCityId(arrivedCityCode)
                                    .arrivalCityId(departureCityCode)
                                    .startTime(departureDate)
                                    .endTime(arrivedDate)
                                    .estimatedAmount(0)
                                    .build();
                                tripBeans.add(tripListBean1);
                                tripBeans.add(tripListBean2);
                            } else if (WeChatApplyType.TRAIN.getMsg().equals(scene)) {//火车
                                ApprovalInfo.TripListBean tripListBean3 = ApprovalInfo.TripListBean.builder()
                                    .type(FbTripType.TRAIN.getCode())
                                    .startCityId(departureCityCode)
                                    .arrivalCityId(arrivedCityCode)
                                    .startTime(departureDate)
                                    .endTime(arrivedDate)
                                    .estimatedAmount(0)
                                    .build();

                                ApprovalInfo.TripListBean tripListBean4 = ApprovalInfo.TripListBean.builder()
                                    .type(FbTripType.TRAIN.getCode())
                                    .startCityId(arrivedCityCode)
                                    .arrivalCityId(departureCityCode)
                                    .startTime(departureDate)
                                    .endTime(arrivedDate)
                                    .estimatedAmount(0)
                                    .build();

                                tripBeans.add(tripListBean3);
                                tripBeans.add(tripListBean4);
                            } else if (WeChatApplyType.INTL_AIR.getMsg().equals(scene)) {//火车
                                ApprovalInfo.TripListBean tripListBean3 = ApprovalInfo.TripListBean.builder()
                                    .type(FbTripType.INTL_AIR.getCode())
                                    .startCityId(departureCityCode)
                                    .arrivalCityId(arrivedCityCode)
                                    .startTime(departureDate)
                                    .endTime(arrivedDate)
                                    .estimatedAmount(0)
                                    .tripType(2)
                                    .backStartTime(arrivedDate)
                                    .build();
                                tripBeans.add(tripListBean3);
                            }
                        }
                    }
                }

                if (StringUtils.isBlank(thirdRemark)) {
                    apply.setThirdRemark(TRIP_APPLY);
                    apply.setApplyReasonDesc(TRIP_APPLY);
                } else {
                    apply.setThirdRemark(thirdRemark);
                    apply.setApplyReasonDesc(thirdRemark);
                }
                processInfo.setApply(apply);
                processInfo.setGuestList(guestList);
                log.info("创建行程审批单具体行程信息：{}", JsonUtils.toJson(tripBeans));
            } else {//企业微信新模板
                for (WeChatApprovalDetail.Content content : contens) {
                    String control = content.getControl();
                    //新的审批表单包含申请事由和明细，其中明细里包含具体的行程信息
                    if (WeChatApplyContentControl.TEXT.getValue().equals(control)) {//文本，申请事由
                        List<WeChatApprovalDetail.Title> titles = content.getTitles();
                        WeChatApprovalDetail.Title title = titles.get(0);
                        String text1 = title.getText();
                        if (APPLY_REASON.equals(text1)) {
                            WeChatApprovalDetail.ContentValue contentValue = content.getContentValue();
                            String text = contentValue.getText();
                            apply.setApplyReason(text);
                            apply.setApplyReasonDesc(text);
                            apply.setThirdRemark(text);
                        }
                    } else if (WeChatApplyContentControl.TABLE.getValue().equals(control)) {//具体明细，包含具体的行程信息
                        WeChatApprovalDetail.ContentValue contentValue = content.getContentValue();
                        List<WeChatApprovalDetail.ChildList> children = contentValue.getChildren();
                        for (WeChatApprovalDetail.ChildList child : children) {
                            //每个行程信息
                            List<WeChatApprovalDetail.Content> list = child.getList();
                            //解析具体的行程信息
                            ApprovalInfo.TripListBean tripListBean = new ApprovalInfo.TripListBean();
                            boolean singleTrip = true;
                            String tripType = "";
                            for (WeChatApprovalDetail.Content content1 : list) {
                                int type=0;
                                String control1 = content1.getControl();
                                if (WeChatApplyContentControl.SELECTOR.getValue().equals(control1)) {//选择器，包括行程的列表，具体审批包含哪些审批
                                    log.info("解析审批单具体场景 {}", JsonUtils.toJson(sceneList));
                                    List<WeChatApprovalDetail.Title> titles = content1.getTitles();
                                    WeChatApprovalDetail.Title title = titles.get(0);
                                    String text = title.getText();

                                    if (TRIP_TYPE.equals(text)) {
                                        WeChatApprovalDetail.ContentValue contentValue1 = content1.getContentValue();
                                        //交通工具类型
                                        WeChatApprovalDetail.Selector selector = contentValue1.getSelector();
                                        List<WeChatApprovalDetail.Option> options = selector.getOptions();
                                        WeChatApprovalDetail.Option option = options.get(0);
                                        List<WeChatApprovalDetail.Value> values = option.getValues();
                                        WeChatApprovalDetail.Value value = values.get(0);
                                        tripType = value.getText();
                                        if (OrderSceneType.AIR.getValue().equals(tripType)) {
                                            type=FbTripType.AIR.getCode();
                                            tripListBean.setType(type);
                                        } else if (OrderSceneType.INTL_AIR.getValue().equals(tripType)) {
                                            type=FbTripType.INTL_AIR.getCode();
                                            tripListBean.setType(type);
                                        } else if (OrderSceneType.TRAIN.getValue().equals(tripType)) {
                                            type=FbTripType.TRAIN.getCode();
                                            tripListBean.setType(type);
                                        } else if (OrderSceneType.HOTEL.getValue().equals(tripType)) {
                                            type=FbTripType.HOTEL.getCode();
                                            tripListBean.setType(type);
                                        }
                                    }
                                    if (SINGLE_TYPE.equals(text)) {
                                        WeChatApprovalDetail.ContentValue contentValue1 = content1.getContentValue();
                                        //交通工具类型
                                        WeChatApprovalDetail.Selector selector = contentValue1.getSelector();
                                        List<WeChatApprovalDetail.Option> options = selector.getOptions();
                                        WeChatApprovalDetail.Option option = options.get(0);
                                        List<WeChatApprovalDetail.Value> values = option.getValues();
                                        WeChatApprovalDetail.Value value = values.get(0);
                                        //单程往返
                                        String roundType = value.getText();
                                        if (ROUND.equals(roundType)) {
                                            singleTrip = false;
                                            tripListBean.setTripType(TripRoundType.RoungTrip.getValue());
                                        }
                                    }
                                } else if (WeChatApplyContentControl.TEXT.getValue().equals(control1)) {//出发和目的城市
                                    List<WeChatApprovalDetail.Title> titles = content1.getTitles();
                                    String text = titles.get(0).getText();
                                    WeChatApprovalDetail.ContentValue contentValue1 = content1.getContentValue();
                                    if (BEGIN_CITY.equals(text)) {
                                        //获取出发城市名称
                                        departurePlace = contentValue1.getText().trim();
                                        log.info("解析审批单具体出发城市 {}", departurePlace);
                                        //根据城市名称获取城市code,获取哪些城市code，需要根据场景来进行区分
                                        departureCityCode = getCityCodeByCityName(companyId, type, departurePlace);
//                                        departureCityCode = weChatEiaPluginCallOpenApiService.getCityCodeByCityName(companyId, departurePlace);
                                        tripListBean.setStartCityId(departureCityCode);
                                    } else if (END_CITY.equals(text)) {
                                        //到达城市名称
                                        arrivedPlace = contentValue1.getText().trim();
                                        arrivedCityCode = getCityCodeByCityName(companyId, type, arrivedPlace);
//                                        arrivedCityCode = weChatEiaPluginCallOpenApiService.getCityCodeByCityName(companyId, arrivedPlace);
                                        log.info("解析审批单具体目的城市 {}", departurePlace);
                                        tripListBean.setArrivalCityId(arrivedCityCode);
                                    } else if (BEGIN_CITY_CODE.equals(text)) {//国际机票出发城市三字码
                                        if (tripListBean.getType() != FbTripType.INTL_AIR.getCode()) {
                                            log.info("非国际机票，不解析三字码。type:{}", tripListBean.getType());
                                            continue;
                                        }
                                        String citycode = contentValue1.getText().trim();
                                        if (StringUtils.isNotBlank(citycode)) {
                                            boolean matches = citycode.matches("^[a-zA-Z]*");
                                            if (matches) {//是英文，进行大小写转换
                                                String upperCaseCityCode = citycode.toUpperCase();
                                                //国际机票并且返回为空，有可能是国际城市名称重复导致无法查询到结果，需要根据国际城市三字码查询分贝城市code
                                                String intlCityInfoByCityCode =
                                                    weChatEiaPluginCallOpenApiService.getIntlCityInfoByCityCode(
                                                        companyId, userId, upperCaseCityCode);
                                                if (StringUtils.isNotBlank(intlCityInfoByCityCode)) {
                                                    departureCityCode = intlCityInfoByCityCode;
                                                    tripListBean.setStartCityId(departureCityCode);
                                                }
                                            }
                                        }
                                    } else if (END_CITY_CODE.equals(text)) {//国际机票目的城市三字码
                                        if (tripListBean.getType() != FbTripType.INTL_AIR.getCode()) {
                                            log.info("非国际机票，不解析三字码。type:{}", tripListBean.getType());
                                            continue;
                                        }
                                        String citycode = contentValue1.getText().trim();
                                        if (StringUtils.isNotBlank(citycode)) {
                                            boolean matches = citycode.matches("^[a-zA-Z]*");
                                            if (matches) {//是英文，进行大小写转换
                                                String upperCaseCityCode = citycode.toUpperCase();
                                                String intlCityInfoByCityCode =
                                                    weChatEiaPluginCallOpenApiService.getIntlCityInfoByCityCode(
                                                        companyId
                                                        , userId, upperCaseCityCode);
                                                if (StringUtils.isNotBlank(intlCityInfoByCityCode)) {
                                                    arrivedCityCode = intlCityInfoByCityCode;
                                                    tripListBean.setArrivalCityId(arrivedCityCode);
                                                }
                                            }
                                        }
                                    }
                                } else if (WeChatApplyContentControl.DATE.getValue().equals(control1)) {//出发和到达时间
                                    List<WeChatApprovalDetail.Title> titles = content1.getTitles();
                                    String text = titles.get(0).getText();
                                    WeChatApprovalDetail.ContentValue contentValue1 = content1.getContentValue();
                                    if (BEGIN_DATE.equals(text)) {
                                        long sDepatureTimestamp = contentValue1.getDate().getSTimestamp();
                                        departureDate = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date(Long.parseLong(sDepatureTimestamp + "") * 1000));
                                        log.info("解析审批单具体出发日期 {}", departureDate);
                                        tripListBean.setStartTime(departureDate);
                                    } else if (END_DATE.equals(text)) {
                                        long sArrivedTimestamp = contentValue1.getDate().getSTimestamp();
                                        arrivedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date(Long.parseLong(sArrivedTimestamp + "") * 1000));
                                        log.info("解析审批单具体到达日期 {}", arrivedDate);
                                        tripListBean.setEndTime(arrivedDate);
                                    }
                                }
                                if (OrderSceneType.HOTEL.getValue().equals(tripType)) {//酒店单独设置目的城市,开始城市取目的城市
                                    tripListBean.setStartCityId(arrivedCityCode);
                                }
                            }
                            if (!singleTrip && tripListBean.getType() == FbTripType.INTL_AIR.getCode()) {//往返行程设置
                                tripListBean.setBackStartTime(departureDateConfig == 0 ? tripListBean.getEndTime() : tripListBean.getStartTime());
                                tripListBean.setBackEndTime(tripListBean.getStartTime());
                            }
                            tripBeans.add(tripListBean);
                            //异常消息通知
                            if (StringUtils.isBlank(departureCityCode) || StringUtils.isBlank(arrivedCityCode)) {
                                String msg = "通知: 您创建的" + departureDate + "到" + arrivedDate + "的" + departurePlace + "到" + arrivedPlace + "的分贝通差旅审批单，因审批单中的城市不符合分贝通标准创建失败，请您准确填写城市并提交审批";
                                weChatNoticeSender.sender(companyId, userId, msg);
                                log.info("发送城市异常通知 {},{} ", companyId, userId);
                                return null;
                            }
                        }
                    }
                }
                processInfo.setApply(apply);
                //新版暂不支持同行人
            }
        }
        return tripBeans;
    }

    /**
     * 根据城市名称查询城市code
     *
     * @param companyId
     * @param cityName
     * @return
     */
    private String getCityCodeByCityName(String companyId, int tripType, String cityName) {
        log.info("根据城市查询分贝通城市编码，cityName: {}, companyId: {}", cityName, companyId);
        String openApiCityInfoByName = weChatEiaPluginCallOpenApiService.getOpenApiCityInfoByName(cityName);
        return openApiCityInfoByName;
    }

}
