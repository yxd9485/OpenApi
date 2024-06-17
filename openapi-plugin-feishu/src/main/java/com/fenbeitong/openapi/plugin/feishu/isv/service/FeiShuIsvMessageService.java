package com.fenbeitong.openapi.plugin.feishu.isv.service;

import cn.hutool.core.collection.CollectionUtil;
import com.fenbeitong.openapi.plugin.core.constant.EventConstant;
import com.fenbeitong.openapi.plugin.event.saas.dto.WebAppPushEvents;
import com.fenbeitong.openapi.plugin.support.company.dto.CompanySuperAdmin;
import org.apache.dubbo.config.annotation.DubboReference;
import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.fenbeipay.api.constant.enums.cashier.OrderType;
import com.fenbeitong.finhub.common.utils.FinhubLogger;
import com.fenbeitong.finhub.kafka.msg.saas.KafkaPushMsg;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseCode;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuException;
import com.fenbeitong.openapi.plugin.feishu.isv.constant.PushMessageResultState;
import com.fenbeitong.openapi.plugin.feishu.isv.dao.PushMessageResultDao;
import com.fenbeitong.openapi.plugin.feishu.isv.dto.*;
import com.fenbeitong.openapi.plugin.feishu.isv.entity.FeishuIsvCompany;
import com.fenbeitong.openapi.plugin.feishu.isv.entity.PushMessageResult;
import com.fenbeitong.openapi.plugin.feishu.isv.util.FeiShuIsvHttpUtils;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigCode;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.company.entity.OpenCompanySourceType;
import com.fenbeitong.openapi.plugin.support.company.service.IOpenCompanySourceTypeService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenCompanyClientType;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.message.MessagePushUtils;
import com.fenbeitong.openapi.plugin.support.util.EmployeeUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.google.common.collect.Lists;
import com.luastar.swift.base.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import tk.mybatis.mapper.util.StringUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lizhen
 * @date 2020/6/8
 */
@ServiceAspect
@Service
@Slf4j
public class FeiShuIsvMessageService {

    @Value("${feishu.api-host}")
    private String feishuHost;

    @Value("${host.webapp}")
    private String webappHost;

    @Value("${feishu.isv.appId}")
    private String appId;

    @Autowired
    private FeiShuIsvHttpUtils feiShuIsvHttpUtils;

    @Autowired
    private FeiShuIsvCompanyDefinitionService feiShuIsvCompanyDefinitionService;

    @Autowired
    private FeiShuIsvEmployeeService feiShuIsvEmployeeService;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Autowired
    private FeiShuIsvCompanyAuthService feiShuIsvCompanyAuthService;

    @DubboReference(check = false)
    private IBaseEmployeeExtService employeeExtService;

    @Autowired
    private PushMessageResultDao pushMessageResultDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //创建固定大小为100 的线程池
    private static ExecutorService service = Executors.newFixedThreadPool(5);

    @Autowired
    private FeiShuIsvEmployeeService feishuIsvemployeeService;

    @Autowired
    private IOpenCompanySourceTypeService openCompanySourceTypeService;

    @Autowired
    private EmployeeUtils employeeUtils;

    /**
     * 向飞书推送消息
     *
     * @return
     */
    public void pushMessage(WebAppPushEvents kafkaPushMsg) {
        if (!EventConstant.MSG_TYPE_APPLY_CANCEL.equals(kafkaPushMsg.getMsgType())) {
            String companyId = kafkaPushMsg.getReceiveCompanyId();
            String userId = kafkaPushMsg.getUserId();
            String title = kafkaPushMsg.getTitle();
            String content = kafkaPushMsg.getContent();
            if (StringUtils.isBlank(companyId) || StringUtils.isBlank(userId) || StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
                log.info("【push信息】消息推送失败，缺少必要参数:{}", JsonUtils.toJson(kafkaPushMsg));
                return;
            }
            //查询企业授权信息
            FeishuIsvCompany feishuIsvCompany = feiShuIsvCompanyDefinitionService.getFeiShuIsvCompanyByCompanyId(companyId);
            if (feishuIsvCompany == null) {
                log.info("【push信息】非飞书 isv企业,companyId:{}", companyId);
                return;
            }
            OpenCompanySourceType openCompanySourceType = openCompanySourceTypeService.getOpenCompanySourceByCompanyId(companyId);
            Integer clientType = openCompanySourceType.getClientType();
            if (OpenCompanyClientType.MINI_PROGRAMS.getType().equals(clientType)) {
                log.info("小程序【push信息】飞书 消息推送 kafkaPushMsg");
                pushMiniProgram(feishuIsvCompany, kafkaPushMsg);
            } else {
                log.info("h5【push信息】飞书 消息推送kafkaPushMsg");
                pushH5Message(feishuIsvCompany, kafkaPushMsg);
            }
        }
    }

    //小程序推送信息（原来逻辑）
    public void pushMiniProgram(FeishuIsvCompany feishuIsvCompany, WebAppPushEvents kafkaPushMsg) {
        String content = kafkaPushMsg.getContent();
        String corpId = feishuIsvCompany.getCorpId();
        String messageUrl = notifyUrlTransfer(kafkaPushMsg);
        if (!StringUtils.isBlank(messageUrl)) {
            String[] split = messageUrl.split("\\?");
            String path = split[0];
            String query = "";
            if (split.length == 2) {
                query = split[1];
            }
            FeiShuIsvNotifyReqDTO feiShuIsvNotifyReqDTO = new FeiShuIsvNotifyReqDTO();
            feiShuIsvNotifyReqDTO.setOpenId(kafkaPushMsg.getThirdEmployeeId());
            feiShuIsvNotifyReqDTO.setNotifyContent(content);
            FeiShuIsvNotifyReqDTO.Schema schema = new FeiShuIsvNotifyReqDTO.Schema();
            schema.setPath(path);
            schema.setQuery(query);
            feiShuIsvNotifyReqDTO.setPcSchema(schema);
            feiShuIsvNotifyReqDTO.setIosSchema(schema);
            feiShuIsvNotifyReqDTO.setAndroidSchema(schema);
            sendNotify(JsonUtils.toJson(feiShuIsvNotifyReqDTO), corpId);
        }
    }

    //H5信息推送
    public void pushH5Message(FeishuIsvCompany feishuIsvCompany, WebAppPushEvents kafkaPushMsg) {
        String title = kafkaPushMsg.getTitle();
        String content = kafkaPushMsg.getContent();
        String corpId = feishuIsvCompany.getCorpId();
        FeiShuIsvSendMessageReqDTO feiShuIsvSendMessageReqDTO = new FeiShuIsvSendMessageReqDTO();
        FeiShuIsvSendMessageReqDTO.MsgContent msgContent = new FeiShuIsvSendMessageReqDTO.MsgContent();
        FeiShuIsvSendMessageReqDTO.Post post = new FeiShuIsvSendMessageReqDTO.Post();
        FeiShuIsvSendMessageReqDTO.ZhCn zhch = new FeiShuIsvSendMessageReqDTO.ZhCn();
        zhch.setTitle(title);
        FeiShuIsvSendMessageReqDTO.PostContent postContent1 = new FeiShuIsvSendMessageReqDTO.PostContent();
        // 第一行，content
        postContent1.setTag("text");
        postContent1.setUnEscape(true);
        postContent1.setText(content);
        FeiShuIsvSendMessageReqDTO.PostContent postContent2 = new FeiShuIsvSendMessageReqDTO.PostContent();
        // 第二行，超链接
        postContent2.setText("查看详情");
        postContent2.setTag("a");
        postContent1.setUnEscape(true);
        List contentList1 = Lists.newArrayList(postContent1);
        List contentList2 = Lists.newArrayList(postContent2);
        zhch.setContent(Lists.newArrayList(contentList1, contentList2));
        post.setZhCn(zhch);
        msgContent.setPost(post);
        feiShuIsvSendMessageReqDTO.setOpenId(kafkaPushMsg.getThirdEmployeeId());
        feiShuIsvSendMessageReqDTO.setMsgType("post");
        feiShuIsvSendMessageReqDTO.setContent(msgContent);
        //String uri = webappHost + FeiShuConstant.FEISHU_ISV_APP_HOME;
        //String uri = webappHost + String.format(FeiShuConstant.FEISHU_ISV_APP_HOME, appId);
        String uri =  String.format(FeiShuConstant.FEISHU_ISV_APPLINK_HOME_URL, appId );
        String messageUrl = MessagePushUtils.messageUrlTransfer(kafkaPushMsg, "");
        log.info("messageUrl:{}", messageUrl);
        try {
            if (!StringUtils.isBlank(messageUrl)) {
                messageUrl =  messageUrl.replace("?", "&");
                String loginUrl = "pages/login/index?";
                messageUrl = loginUrl + messageUrl.replace("url=", "redirectFbtUrl=");
                String encodeUrl = URLEncoder.encode(messageUrl, "utf-8");
                String url = uri + encodeUrl;
                postContent2.setHref(url);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sendMessage(feiShuIsvSendMessageReqDTO, corpId);
    }

    public static String notifyUrlTransfer(KafkaPushMsg kafkaPushMsg) {
        String msgType = kafkaPushMsg.getMsgType();
        String msg = kafkaPushMsg.getMsg();
        Map eventMsgMap = JsonUtils.toObj(msg, Map.class);
        String url = null;
        if (FeiShuConstant.MSG_TYPE_APPLY.equals(msgType)) {
            url = initApplicationUrl(eventMsgMap);
        } else if (FeiShuConstant.MSG_TYPE_ORDER.equals(msgType)) {
            url = initOrderUrl(eventMsgMap);
        }
        return url;

    }


    /**
     * 初始化订单跳转的url
     *
     * @param eventMsgMap
     */
    private static String initOrderUrl(Map eventMsgMap) {
        String url = null;
        if (eventMsgMap != null) {
            String orderType = StringUtils.obj2str(eventMsgMap.get("order_type"));
            String orderId = StringUtils.obj2str(eventMsgMap.get("order_id"));
            String ft = StringUtils.obj2str(eventMsgMap.get("ft"));
            String applyTransNo = StringUtils.obj2str(eventMsgMap.get("applyTransNo"));
            if (!StringUtils.isBlank(orderType) && !StringUtils.isBlank(orderId)) {
                int orderTypeInt = Integer.valueOf(orderType);
                OrderType ot = OrderType.getEnum(orderTypeInt);
                switch (ot) {
                    case Air:
                        url = FeiShuConstant.MESSAGE_URL_ORDER_AIR + orderId;
                        break;
                    case Hotel:
                        url = FeiShuConstant.MESSAGE_URL_ORDER_HOTEL + orderId;
                        break;
                    case Taxi:
                        url = FeiShuConstant.MESSAGE_URL_ORDER_TAXI + orderId;
                        break;
                    case Train:
                        url = FeiShuConstant.MESSAGE_URL_ORDER_TRAIN + orderId;
                        break;
                    default:
                        break;
                }
            } else if ("261".equals(ft)) {
                url = FeiShuConstant.MESSAGE_URL_APPLY_VIRTUAL_DETAIL;
            }
        }
        if (StringUtils.isBlank(url)) {
            url = FeiShuConstant.MESSAGE_URL_ORDER;
        }
        return url;
    }

    /**
     * 初始化审批跳转的url
     *
     * @param eventMsgMap
     */
    private static String initApplicationUrl(Map eventMsgMap) {
        String url = null;
        if (eventMsgMap != null) {
            Integer applyType = (Integer) eventMsgMap.get("apply_type");
            String id = StringUtils.obj2str(eventMsgMap.get("id"));
            String settingType = StringUtils.obj2str(eventMsgMap.get("setting_type"));
            String viewType = StringUtils.obj2str(eventMsgMap.get("view_type"));

            //saas_push的view_type，1申请人，2审批人，3抄送人。 给前端跳转的type，1.审批人，2申请人，3抄送人
            if (FeiShuConstant.SAAS_VIEW_TYPE_APPLYER.equals(viewType)) {
                viewType = "false";
            } else if (FeiShuConstant.SAAS_VIEW_TYPE_APPROVER.equals(viewType)) {
                viewType = "true";
            }
            // 行程审批
            if ("1".equals(settingType)) {
                if (applyType != null && !StringUtils.isBlank(id)) {
                    if (applyType == 1) {
                        url = FeiShuConstant.MESSAGE_URL_APPLICATION_TRIP_APPLY + id + "&histShow=" + viewType;
                    } else if (applyType == 12) {
                        url = FeiShuConstant.MESSAGE_URL_APPLICATION_TAXI_APPLY + id + "&histShow=" + viewType;
                    }
                }
            } else if ("2".equals(settingType)) {
                String orderType = StringUtils.obj2str(eventMsgMap.get("order_type"));
                if (orderType != null && !StringUtils.isBlank(id)) {
                    url = FeiShuConstant.MESSAGE_URL_APPLICATION_DETAIL_APPLY + id + "&histShow=" + viewType;
                }
            } else if ("6".equals(settingType)) {
                String orderType = StringUtils.obj2str(eventMsgMap.get("order_type"));
                if (orderType != null && !StringUtils.isBlank(id)) {
                    url = FeiShuConstant.MESSAGE_URL_APPLICATION_REFUND_CHANGE_DETAIL_APPLY + id + "&histShow=" + viewType;
                }
            } else if ("8".equals(settingType)) {
                //个人虚拟卡审批
                url = FeiShuConstant.MESSAGE_URL_BANK_INDIVIDUAL_APPLY + id + "&histShow=" + viewType;
            } else if ("9".equals(settingType)) {
                //虚拟卡核销审批
                url = FeiShuConstant.MESSAGE_URL_VIRTUAL_CARDWRITE_OFF_APPLY + id + "&histShow=" + viewType;
            }
        }
        if (StringUtils.isBlank(url)) {
            url = FeiShuConstant.MESSAGE_URL_APPLY;
        }
        return url;
    }


    public void sendMessage(FeiShuIsvSendMessageReqDTO feiShuIsvSendMessageReqDTO, String corpId) {
        sendMessage(JsonUtils.toJson(feiShuIsvSendMessageReqDTO), corpId);
    }

    public void sendMessage(String message, String corpId) {
        String url = feishuHost + FeiShuConstant.SEND_MESSAGE_URL;
        String res = feiShuIsvHttpUtils.postJsonWithTenantAccessToken(url, message, corpId);
        JSONObject jsonObject = JSONObject.parseObject(res);
        if (jsonObject == null || 0 != jsonObject.getInteger("code")) {
            log.info("feishu isv sendMessage:{}", res);
            String msg = jsonObject == null ? "" : jsonObject.getString("msg");
            throw new OpenApiFeiShuException(FeiShuResponseCode.SEND_MESSAGE_FAILED, msg);
        }
    }


    /**
     * 员工首次打开机器人的消息
     */
    public void processP2PChatCreate(String decryptMsg) {
        FeiShuIsvCallbackP2PChatCreateDTO feiShuIsvCallbackP2PChatCreateDTO = JsonUtils.toObj(decryptMsg, FeiShuIsvCallbackP2PChatCreateDTO.class);
        String tenantKey = feiShuIsvCallbackP2PChatCreateDTO.getEvent().getTenantKey();
        FeishuIsvCompany feiShuIsvCompany = feiShuIsvCompanyDefinitionService.getFeiShuIsvCompanyByCorpId(tenantKey);
        if (feiShuIsvCompany == null) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_COMPANY_UNDEFINED);
        }
        String companyId = feiShuIsvCompany.getCompanyId();
        String admin = feiShuIsvEmployeeService.superAdminThirdEmployeeId(companyId);
        //会话人
        String openId = feiShuIsvCallbackP2PChatCreateDTO.getEvent().getUser().getOpenId();
        if (!StringUtils.isBlank(openId) && openId.equals(admin)) {
            log.info("授权负责人会话，不再推送欢迎消息");
            return;
        }
        String chatId = feiShuIsvCallbackP2PChatCreateDTO.getEvent().getChatId();
        String message = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.FEISHU_ISV_MESSAGE_P2P_CHAT_CREATE.getCode());
        message = String.format(message, chatId);
        sendMessage(message, tenantKey);
    }

    /**
     * 接收消息
     */
    public void processMessage(String decryptMsg) {
        FeiShuIsvCallbackBotMessageDTO feiShuIsvCallbackBotMessageDTO = JsonUtils.toObj(decryptMsg, FeiShuIsvCallbackBotMessageDTO.class);
        String tenantKey = feiShuIsvCallbackBotMessageDTO.getEvent().getTenantKey();
        String chatId = feiShuIsvCallbackBotMessageDTO.getEvent().getOpenChatId();
        String message = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.FEISHU_ISV_MESSAGE_MESSAGE.getCode());
        message = String.format(message, chatId);
        sendMessage(message, tenantKey);
    }

    /**
     * 用户进群
     *
     * @param decryptMsg
     */
    public void processAddUserToChat(String decryptMsg) {
        FeiShuIsvCallbackAddUserToChatDTO feiShuIsvCallbackAddUserToChatDTO = JsonUtils.toObj(decryptMsg, FeiShuIsvCallbackAddUserToChatDTO.class);
        String tenantKey = feiShuIsvCallbackAddUserToChatDTO.getEvent().getTenantKey();
        String chatId = feiShuIsvCallbackAddUserToChatDTO.getEvent().getChatId();
        List<FeiShuIsvCallbackAddUserToChatDTO.User> users = feiShuIsvCallbackAddUserToChatDTO.getEvent().getUsers();
        String userNames = "";
        for (FeiShuIsvCallbackAddUserToChatDTO.User user : users) {
            userNames = userNames + "【" + user.getName() + "】";
        }
        String message = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.FEISHU_ISV_MESSAGE_ADD_USER_TO_CHAT.getCode());
        message = String.format(message, chatId, userNames);
        sendMessage(message, tenantKey);
    }


    public void sendNotify(String message, String corpId) {
        String url = feishuHost + FeiShuConstant.SEND_NOTIFY_URL;
        String res = feiShuIsvHttpUtils.postJsonWithTenantAccessToken(url, message, corpId);
        JSONObject jsonObject = JSONObject.parseObject(res);
        if (jsonObject == null || 0 != jsonObject.getInteger("code")) {
            log.info("feishu isv sendNotify:{}", res);
            String msg = jsonObject == null ? "" : jsonObject.getString("msg");
            throw new OpenApiFeiShuException(FeiShuResponseCode.SEND_MESSAGE_FAILED, msg);
        }
    }

    /**
     * 机器人进群
     */
    public void processAddBot(String decryptMsg) {
        FeiShuIsvCallbackAddBotDTO feiShuIsvCallbackAddBotDTO = JsonUtils.toObj(decryptMsg, FeiShuIsvCallbackAddBotDTO.class);
        String tenantKey = feiShuIsvCallbackAddBotDTO.getEvent().getTenantKey();
        String chatId = feiShuIsvCallbackAddBotDTO.getEvent().getOpenChatId();
        String message = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.FEISHU_ISV_MESSAGE_ADD_BOT.getCode());
        message = String.format(message, chatId);
        sendMessage(message, tenantKey);
    }

    /**
     * 安装应用
     *
     * @param thirdEmployeeId
     * @param corpId
     */
    public void processInstall(String thirdEmployeeId, String corpId) {
        String message = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.FEISHU_ISV_MESSAGE_INSTALL.getCode());
        message = String.format(message, thirdEmployeeId);
        sendMessage(message, corpId);
    }


    /**
     * 向飞书推送消息
     *
     * @return
     */
    public String sendMessageForAppointUser(FeiShuPushMessageRespDTO feiShuPushMessageDataRespDTO, List<FeiShuPushMessageDataDTO> pushMessageDataDTOs) {
        String redisKey = StrUtils.formatString(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, StrUtils.formatString(RedisKeyConstant.FEISHU_PUSH_MSG_KEY, "_" + JsonUtils.toJson(feiShuPushMessageDataRespDTO)));
        //重复性交验的
        try {
            if (checkRepeatMsg(redisKey)) {
                return "repeat";
            }
//            BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(10);
//            ThreadPoolExecutor pool = new ThreadPoolExecutor(5, 5, 3, TimeUnit.MINUTES, queue);
            if (CollectionUtil.isEmpty(pushMessageDataDTOs)) {
                if (CollectionUtil.isEmpty(feiShuPushMessageDataRespDTO.getSendCompanyList())) {
                    //发给所有公司的所有用户进行发送
                    List<FeishuIsvCompany> feishuIsvCompaniesList = feiShuIsvCompanyDefinitionService.getFeiShuIsvAllCompany();
                    if (CollectionUtil.isNotEmpty(feishuIsvCompaniesList)) {
                        feishuIsvCompaniesList.forEach(feishuIsvCompany -> {
                            pushMessageDataDTOs.add(FeiShuPushMessageDataDTO.builder().companyId(feishuIsvCompany.getCompanyId())
                                    .corpId(feishuIsvCompany.getCorpId()).msgType(feiShuPushMessageDataRespDTO.getMsgType()).content(feiShuPushMessageDataRespDTO.getContent()).card(feiShuPushMessageDataRespDTO.getCard())
                                    .build());
                        });
                    }
                } else {
                    List<String> companyIds = feiShuPushMessageDataRespDTO.getSendCompanyList().stream().map(FeiShuPushMessageRespDTO.SendCompany::getCompanyId).collect(Collectors.toList());
                    List<FeishuIsvCompany> feishuIsvCompanyList = feiShuIsvCompanyDefinitionService.getFeiShuIsvByIdAndTime(companyIds, null, null);
                    if (CollectionUtil.isNotEmpty(feishuIsvCompanyList)) {
                        Map<String, FeishuIsvCompany> feishuIsvCompanyMap = feishuIsvCompanyList.stream().collect(Collectors.toMap(FeishuIsvCompany::getCompanyId, Function.identity()));
                        feiShuPushMessageDataRespDTO.getSendCompanyList().forEach(company -> {
                            if (company.getCompanyId() != null && feishuIsvCompanyMap.get(company.getCompanyId()) != null) {
                                //给选定公司里的所有用户进行发送
                                FeiShuPushMessageDataDTO feiShuPushMessageDataDTO = FeiShuPushMessageDataDTO.builder().companyId(company.getCompanyId())
                                        .msgType(feiShuPushMessageDataRespDTO.getMsgType()).content(feiShuPushMessageDataRespDTO.getContent()).card(feiShuPushMessageDataRespDTO.getCard())
                                        .corpId(feishuIsvCompanyMap.get(company.getCompanyId()).getCorpId()).build();
                                if (StringUtil.isNotEmpty(company.getEmployeeIds())) {
                                    //给选定公司的选定用户进行发送FeiShuIsvMessageService.java
                                    String[] employeeIds = company.getEmployeeIds().split(",");
                                    if (employeeIds != null) {
                                        List<FeiShuPushMessageDataDTO.PushEmployee> pushEmployeeList = new ArrayList<>();
                                        Arrays.asList(employeeIds).forEach(e -> {
                                            pushEmployeeList.add(FeiShuPushMessageDataDTO.PushEmployee.builder().employeeId(e).build());
                                        });
                                        feiShuPushMessageDataDTO.setEmployees(pushEmployeeList);
                                    }
                                }
                                pushMessageDataDTOs.add(feiShuPushMessageDataDTO);
                            }
                        });
                    }
                }
                log.info("向飞书定向推送消息的企业家数为：{}", pushMessageDataDTOs != null ? pushMessageDataDTOs.size() : 0);
                //拿到发送员工的openid
                if (CollectionUtil.isNotEmpty(pushMessageDataDTOs)) {
                    pushMessageDataDTOs.forEach(pushMessageDataDTO -> {
                        if (CollectionUtil.isNotEmpty(pushMessageDataDTO.getEmployees())) {
                            List<String> employeeIds = pushMessageDataDTO.getEmployees().stream().map(FeiShuPushMessageDataDTO.PushEmployee::getEmployeeId).collect(Collectors.toList());
                            List<EmployeeContract> employeeContractList = employeeUtils.queryEmployees(employeeIds, pushMessageDataDTO.getCompanyId());
                            List<FeiShuPushMessageDataDTO.PushEmployee> pushEmployeeList = new ArrayList<>();
                            if (employeeContractList != null) {
                                employeeContractList.forEach(employeeContract -> {
                                    if (StringUtil.isNotEmpty(employeeContract.getThird_employee_id())) {
                                        pushEmployeeList.add(FeiShuPushMessageDataDTO.PushEmployee.builder().employeeId(employeeContract.getEmployee_id()).
                                                openId(employeeContract.getThird_employee_id()).build());
                                    }
                                });
                            }
                            pushMessageDataDTO.setEmployees(pushEmployeeList);
                        } else {
                            List<String> userOpenIds = feiShuIsvEmployeeService.getAllUserOpenIdsFromOpenThirdEmployee(OpenType.FEISHU_ISV.getType(), pushMessageDataDTO.getCompanyId());
                            List<FeiShuPushMessageDataDTO.PushEmployee> pushEmployeeList = new ArrayList<>();
                            if (userOpenIds != null) {
                                userOpenIds.forEach(userOpenId -> {
                                    pushEmployeeList.add(FeiShuPushMessageDataDTO.PushEmployee.builder().
                                            openId(userOpenId).build());
                                });
                            }
                            pushMessageDataDTO.setEmployees(pushEmployeeList);
                        }
                        log.info("向飞书定向推送消息的企页id为：{},员工数为：{}", pushMessageDataDTO.getCompanyId(), pushMessageDataDTO.getEmployees() != null ?
                                pushMessageDataDTO.getEmployees().size() : 0);
                    });
                }

            }
            if (CollectionUtil.isNotEmpty(pushMessageDataDTOs)) {
                for (int i = 0; i < pushMessageDataDTOs.size(); i++) {
                    if (CollectionUtil.isNotEmpty(pushMessageDataDTOs.get(i).getEmployees())) {
                        //final int k = i;
//                        pool.execute(() -> {
                        Map<String, Object> map = new HashMap<>();
                        if ("text".equals(pushMessageDataDTOs.get(i).getMsgType())) {
                            map.put("msg_type", "text");
                            map.put("content", pushMessageDataDTOs.get(i).getContent());
                        }
                        if ("interactive".equals(pushMessageDataDTOs.get(i).getMsgType())) {
                            map.put("msg_type", "interactive");
                            map.put("card", pushMessageDataDTOs.get(i).getCard());
                        }

                        List<String> openIds = pushMessageDataDTOs.get(i).getEmployees().stream().map(FeiShuPushMessageDataDTO.PushEmployee::getOpenId).collect(Collectors.toList());
                        if (openIds.size() <= 0) {
                            continue;
                        }
                        List<String> openBatch = new ArrayList<>();
                        if (openIds.size() > 200) {
                            int num = openIds.size() / 200;
                            for (int j = 0; j < num + 1; j++) {
                                if (j == num) {
                                    openBatch = openIds.subList(200 * j, pushMessageDataDTOs.get(i).getEmployees().size());
                                } else {
                                    openBatch = openIds.subList(200 * j, 200 * (j + 1));
                                }
                                map.put("open_ids", openBatch);
                                if (openBatch.size() > 0) {
                                    Integer code = sendFeishuMessage(JsonUtils.toJson(map), pushMessageDataDTOs.get(i).getCorpId());
                                    if (code != null && (code == 0)) {
                                        addList(pushMessageDataDTOs.get(i), openBatch, PushMessageResultState.SEND_SUCCESS.getCode(), code);
                                    } else {
                                        addList(pushMessageDataDTOs.get(i), openBatch, PushMessageResultState.SEND_FAIL.getCode(), code);
                                        //如果第一条执行失败 这个任务就终止
//                                        if (k == 0) {
//                                            redisTemplate.delete(redisKey);
//                                            return;
//                                        }
                                    }
                                }
                            }
                        } else {
                            map.put("open_ids", openIds);
                            Integer code = sendFeishuMessage(JsonUtils.toJson(map), pushMessageDataDTOs.get(i).getCorpId());
                            if (code != null && (code == 0)) {
                                addList(pushMessageDataDTOs.get(i), openIds, PushMessageResultState.SEND_SUCCESS.getCode(), code);
                            } else {
                                addList(pushMessageDataDTOs.get(i), openIds, PushMessageResultState.SEND_FAIL.getCode(), code);
                                //如果第一条执行失败 这个任务就终止
//                                    if (k == 0) {
//                                        redisTemplate.delete(redisKey);
//                                        return;
//                                    }
                            }

                        }
                        //});

                    }
                }
            }


            return "success";
        } catch (Exception e) {
            FinhubLogger.info("飞书推送消息的时候推送的异常，异常信息为={}", e);
            log.info("飞书推送消息的时候推送的异常，异常信息为={}", e);
        } finally {
            redisTemplate.delete(redisKey);
        }
        return null;
    }

    /**
     * 飞书的消息推送
     *
     * @param message
     * @param corpId
     * @return
     */
    public Integer sendFeishuMessage(String message, String corpId) {
        String url = feishuHost + FeiShuConstant.BATCH_SEND_MESSAGE_URL;
        try {
            String res = feiShuIsvHttpUtils.postJsonWithTenantAccessToken(url, message, corpId);
            JSONObject jsonObject = JSONObject.parseObject(res);
            Integer code = jsonObject.getInteger("code");
            return code;
        } catch (Exception e) {
            log.error("飞书的消息推送异常消息为:{},消息体为:{},公司ID为:{}", e, message, corpId);
            return null;
        }
    }

    /**
     * 飞书推送消息的时候短期内防止重复推送
     */
    public Boolean checkRepeatMsg(String redisKey) {
        String redisMessage = (String) redisTemplate.opsForValue().get(redisKey);
        if (StringUtil.isNotEmpty(redisMessage)) {
            log.info("飞书推送消息的时候已推送过，不再推送，请求参数={}", JsonUtils.toJson(redisKey));
            FinhubLogger.info("飞书推送消息的时候已推送过，不再推送，请求参数={}", JsonUtils.toJson(redisKey));
            return true;
        } else {
            redisTemplate.opsForValue().set(redisKey, "1", 1, TimeUnit.HOURS);
            return false;
        }
    }

    /**
     * 批量去增加到消息结果通知表
     */
    public void addList(FeiShuPushMessageDataDTO pushMessageDataDTO, List<String> openIds, Integer sendSuccess, Integer code) {
        Integer failNum = sendSuccess == PushMessageResultState.SEND_SUCCESS.getCode() ? 0 : 1;
        List<PushMessageResult> pushMessageResultList = new ArrayList<>();
        HashMap map = new HashMap();
        if ("text".equals(pushMessageDataDTO.getMsgType())) {
            map.put("content", pushMessageDataDTO.getContent());
        }
        if ("interactive".equals(pushMessageDataDTO.getMsgType())) {
            map.put("card", pushMessageDataDTO.getCard());
        }
        openIds.forEach(openId -> {
            PushMessageResult pushMessageResult = PushMessageResult.builder().companyId(pushMessageDataDTO.getCompanyId())
                    .thirdEmployeeId(openId).openType(OpenSysConfigCode.FEISHU_ISV_PUSH_MESSAGE.getCode()).sendContent(JsonUtils.toJson(map)).messageId(code)
                    .msgType(pushMessageDataDTO.getMsgType()).sendSuccess(sendSuccess).failNum(failNum).createTime(new Date()).corpId(pushMessageDataDTO.getCorpId()).build();
            pushMessageResultList.add(pushMessageResult);
        });
        if (pushMessageResultList.size() > 0) {
            pushMessageResultDao.saveList(pushMessageResultList);
        }
    }


    /**
     * 定时任务给飞书推送消息
     */
    public void sendTaskMessageForAppointUser() {
        List<OpenSysConfig> openSysConfigList = openSysConfigService.getOpenSysConfigByTypeCode(OpenSysConfigCode.FEISHU_ISV_PUSH_MESSAGE.getCode());
        if (CollectionUtil.isNotEmpty(openSysConfigList)) {
            openSysConfigList.forEach(openSysConfig -> {
                FeiShuPushMessageRespDTO feiShuPushMessageRespDTO = JSONObject.parseObject(openSysConfig.getValue(), FeiShuPushMessageRespDTO.class);
                Integer joinDay = feiShuPushMessageRespDTO.getJoin_day();
                //发给所有公司的所有用户进行发送
                List<FeishuIsvCompany> feishuIsvCompaniesList = feiShuIsvCompanyDefinitionService.getFeiShuIsvByIdAndTime(
                        null, DateUtils.getPreDateByNum(joinDay).get(0), DateUtils.getPreDateByNum(joinDay).get(1));

                FinhubLogger.info("定时任务给飞书推送消息，加入第几天：{}，查询出的所有公司条数：{}", joinDay, feishuIsvCompaniesList != null ? feishuIsvCompaniesList.size() : 0);
                log.info("定时任务给飞书推送消息，加入第几天：{}，查询出的所有公司条数：{}", joinDay, feishuIsvCompaniesList != null ? feishuIsvCompaniesList.size() : 0);

                List<FeiShuPushMessageRespDTO.SendCompany> newList = new ArrayList<>();
                List<FeiShuPushMessageDataDTO> pushMessageDataDTOs = new ArrayList<>();
                if (CollectionUtil.isNotEmpty(feishuIsvCompaniesList)) {

                    if (feiShuPushMessageRespDTO.getAdminOnlyWhether() != null && feiShuPushMessageRespDTO.getAdminOnlyWhether() == 1) {
                        if (CollectionUtil.isEmpty(feiShuPushMessageRespDTO.getSendCompanyList())) {
                            feishuIsvCompaniesList.forEach(feishuIsvCompany -> {
                                CompanySuperAdmin companySuperAdmin = feishuIsvemployeeService.companySuperAdmin(feishuIsvCompany.getCompanyId());
                                if (companySuperAdmin != null) {
                                    FeiShuPushMessageDataDTO feiShuPushMessageDataDTO = new FeiShuPushMessageDataDTO();
                                    BeanUtils.copyProperties(feiShuPushMessageRespDTO, feiShuPushMessageDataDTO);
                                    feiShuPushMessageDataDTO.setCompanyId(feishuIsvCompany.getCompanyId());
                                    feiShuPushMessageDataDTO.setCorpId(feishuIsvCompany.getCorpId());
                                    feiShuPushMessageDataDTO.setEmployees(Arrays.asList(FeiShuPushMessageDataDTO.PushEmployee.builder().employeeId(companySuperAdmin.getId()).
                                            openId(companySuperAdmin.getThirdEmployeeId()).build()));
                                    pushMessageDataDTOs.add(feiShuPushMessageDataDTO);
                                }
                            });
                        } else {
                            feishuIsvCompaniesList.forEach(feishuIsvCompany -> {
                                for (FeiShuPushMessageRespDTO.SendCompany excompany : feiShuPushMessageRespDTO.getSendCompanyList()) {
                                    if (feishuIsvCompany.getCompanyId().equals(excompany.getCompanyId())) {
                                        CompanySuperAdmin companySuperAdmin = feishuIsvemployeeService.companySuperAdmin(feishuIsvCompany.getCompanyId());
                                        if (companySuperAdmin != null) {
                                            FeiShuPushMessageDataDTO feiShuPushMessageDataDTO = new FeiShuPushMessageDataDTO();
                                            BeanUtils.copyProperties(feiShuPushMessageRespDTO, feiShuPushMessageDataDTO);
                                            feiShuPushMessageDataDTO.setCompanyId(feishuIsvCompany.getCompanyId());
                                            feiShuPushMessageDataDTO.setCorpId(feishuIsvCompany.getCorpId());
                                            feiShuPushMessageDataDTO.setEmployees(Arrays.asList(FeiShuPushMessageDataDTO.PushEmployee.builder().employeeId(companySuperAdmin.getId()).
                                                    openId(companySuperAdmin.getThirdEmployeeId()).build()));
                                            pushMessageDataDTOs.add(feiShuPushMessageDataDTO);
                                        }
                                        break;
                                    }
                                }
                            });
                        }
                    } else {
                        if (CollectionUtil.isEmpty(feiShuPushMessageRespDTO.getSendCompanyList())) {
                            feishuIsvCompaniesList.forEach(feishuIsvCompany -> {
                                newList.add(FeiShuPushMessageRespDTO.SendCompany.builder().companyId(feishuIsvCompany.getCompanyId()).build());
                            });
                        } else {
                            feishuIsvCompaniesList.forEach(feishuIsvCompany -> {
                                for (FeiShuPushMessageRespDTO.SendCompany excompany : feiShuPushMessageRespDTO.getSendCompanyList()) {
                                    if (feishuIsvCompany.getCompanyId().equals(excompany.getCompanyId())) {
                                        newList.add(excompany);
                                        break;
                                    }
                                }
                            });
                        }
                    }

                    FinhubLogger.info("定时任务给飞书推送消息，加入第几天：{}，最终去发送的公司条数：{}", joinDay, pushMessageDataDTOs != null ? pushMessageDataDTOs.size() : 0);
                    log.info("定时任务给飞书推送消息，加入第几天：{}，最终去发送的公司条数：{}", joinDay, pushMessageDataDTOs != null ? pushMessageDataDTOs.size() : 0);
                    feiShuPushMessageRespDTO.setSendCompanyList(newList);
                    sendMessageForAppointUser(feiShuPushMessageRespDTO, pushMessageDataDTOs);
                }
            });
        }
    }

    /**
     * 给发送失败的人重新发送
     *
     * @param request
     */
    public String sendForFailUser(String request) {
        PushMessageFailDataDTO pushMessageFailDataDTO = JSONObject.parseObject(request, PushMessageFailDataDTO.class);
        String redisKey = StrUtils.formatString(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, StrUtils.formatString(RedisKeyConstant.FEISHU_PUSH_MESSAGE_FAIL_RESULT_KEY, "_" +
                JsonUtils.toJson(pushMessageFailDataDTO)));
        try {
            //防止重复点击
            if (checkRepeatMsg(redisKey)) {
                return "repeat";
            }

            List<PushMessageResult> pushMessageResultList = pushMessageResultDao.listFindFailUser(pushMessageFailDataDTO.getThirdEmployeeId(), pushMessageFailDataDTO.getCompanyId(),
                    pushMessageFailDataDTO.getCreateTimeBegin(), pushMessageFailDataDTO.getCreateTimeEnd(), PushMessageResultState.SEND_FAIL.getCode());

            if (CollectionUtil.isNotEmpty(pushMessageResultList)) {
                //根据公司进行分组
                Map<String, List<PushMessageResult>> pushMessageResultMap = pushMessageResultList.stream().collect(Collectors.groupingBy(PushMessageResult::getCorpId));
                pushMessageResultMap.forEach((corpId, pushMessageList) -> {
                    Map<String, List<PushMessageResult>> sendContentMap = pushMessageList.stream().collect(Collectors.groupingBy(PushMessageResult::getSendContent));
                    sendContentMap.forEach((sendContent, sendMessageList) -> {
                        List<String> openIds = sendMessageList.stream().map(PushMessageResult::getThirdEmployeeId).collect(Collectors.toList());
                        Map<String, Object> map = new HashMap<>();
                        Map<String, Object> mapSendContent = JSONObject.parseObject(sendContent, Map.class);
                        if ("text".equals(sendMessageList.get(0).getMsgType())) {
                            map.put("msg_type", "text");
                            map.put("content", mapSendContent.get("content"));
                        }
                        if ("interactive".equals(sendMessageList.get(0).getMsgType())) {
                            map.put("msg_type", "interactive");
                            map.put("card", mapSendContent.get("card"));
                        }
                        List<String> openBatch = new ArrayList<>();
                        if (openIds.size() > 200) {
                            int num = openIds.size() / 200;
                            for (int i = 0; i < num + 1; i++) {
                                if (i == num) {
                                    openBatch = openIds.subList(200 * i, openIds.size());
                                } else {
                                    openBatch = openIds.subList(200 * i, 200 * (i + 1));
                                }
                                map.put("open_ids", openBatch);
                                Integer code = sendFeishuMessage(JsonUtils.toJson(map), corpId);
                                if (code != null && (code == 0)) {
                                    updatePushMessageList(sendMessageList, PushMessageResultState.SEND_SUCCESS.getCode(), code);
                                } else {
                                    updatePushMessageList(sendMessageList, PushMessageResultState.SEND_FAIL.getCode(), code);
                                }
                            }
                        } else {
                            map.put("open_ids", openIds);
                            Integer code = sendFeishuMessage(JsonUtils.toJson(map), corpId);
                            if (code != null && (code == 0)) {
                                updatePushMessageList(sendMessageList, PushMessageResultState.SEND_SUCCESS.getCode(), code);
                            } else {
                                updatePushMessageList(sendMessageList, PushMessageResultState.SEND_FAIL.getCode(), code);
                            }
                        }
                    });
                });
            }
            return "success";
        } catch (Exception e) {
            FinhubLogger.info("给发送失败的人重新发送异常，异常信息为={}", e);
            log.info("给发送失败的人重新发送异常，异常信息为={}", e);
        } finally {
            redisTemplate.delete(redisKey);
        }
        return null;
    }

    /**
     * 批量去修改消息结果通知表
     */
    public void updatePushMessageList(List<PushMessageResult> pushMessageResultList, Integer sendSuccess, Integer code) {
        Integer failNum = sendSuccess == PushMessageResultState.SEND_SUCCESS.getCode() ? 0 : 1;
        pushMessageResultList.forEach(pushMessageResult -> {
            pushMessageResult.setFailNum(pushMessageResult.getFailNum() + failNum);
            pushMessageResult.setSendSuccess(sendSuccess);
            pushMessageResult.setMessageId(code);
            pushMessageResultDao.updateById(pushMessageResult);
        });
        //pushMessageResultDao.batchUpdateList(pushMessageResultList);
    }
}


