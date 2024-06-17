package com.fenbeitong.openapi.plugin.yiduijie.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.support.company.dto.CompanySuperAdmin;
import com.fenbeitong.openapi.plugin.support.util.SuperAdminUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.RandomUtils;
import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieApiResponseCode;
import com.fenbeitong.openapi.plugin.yiduijie.dao.YiDuiJieConfDao;
import com.fenbeitong.openapi.plugin.yiduijie.dto.*;
import com.fenbeitong.openapi.plugin.yiduijie.entity.YiDuiJieConf;
import com.fenbeitong.openapi.plugin.yiduijie.exception.OpenApiYiDuiJieException;
import com.fenbeitong.openapi.plugin.yiduijie.model.app.CreateAppReqDTO;
import com.fenbeitong.openapi.plugin.yiduijie.model.app.GetAppListRespDTO;
import com.fenbeitong.openapi.plugin.yiduijie.sdk.YiDuiJieAppApi;
import com.fenbeitong.openapi.plugin.yiduijie.sdk.YiDuiJieClientApi;
import com.fenbeitong.openapi.plugin.yiduijie.sdk.YiDuiJieConfigApi;
import com.fenbeitong.openapi.plugin.yiduijie.sdk.YiDuiJieUserApi;
import com.fenbeitong.openapi.plugin.yiduijie.service.IYiDuiJieInitService;
import com.fenbeitong.openapi.plugin.yiduijie.service.IYiDuiJieTokenService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title: YiDuijieInitServiceImpl</p>
 * <p>Description: 易对接初始化服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 3:53 PM
 */
@Slf4j
@ServiceAspect
@Service
public class YiDuiJieInitServiceImpl implements IYiDuiJieInitService {

    @Autowired
    private IYiDuiJieTokenService yiDuiJieTokenService;

    @Autowired
    private SuperAdminUtils superAdminUtils;

    @Autowired
    private YiDuiJieAppApi yiDuiJieAppApi;

    @Autowired
    private YiDuiJieUserApi yiDuiJieUserApi;

    @Autowired
    private YiDuiJieClientApi yiDuiJieClientApi;

    @Autowired
    private YiDuiJieConfigApi yiDuiJieConfigApi;

    @Autowired
    private YiDuiJieConfDao yiDuiJieConfDao;

    @Override
    public List<GetAppListRespDTO> listApp() {
        String token = yiDuiJieTokenService.getYiDuiJieToken();
        YiDuiJieListMarketAppResp marketAppResp = yiDuiJieAppApi.listMarketApp(token);
        return marketAppResp == null ? Lists.newArrayList()
                : ObjectUtils.isEmpty(marketAppResp.getAppList()) ? Lists.newArrayList()
                : marketAppResp.getAppList().stream()
                .map(app -> GetAppListRespDTO.builder().appId(app.getAppId()).title(app.getTitle()).build())
                .collect(Collectors.toList());
    }

    @Override
    public void createApp(CreateAppReqDTO createAppReq) {
        YiDuiJieConf yiDuiJieConf = yiDuiJieConfDao.getByCompanyId(createAppReq.getCompanyId());
        if (yiDuiJieConf != null) {
            return;
        }
        //token
        String token = yiDuiJieTokenService.getYiDuiJieToken();
        //组织增加用户请求
        YiDuiJieAddUserReq addUserReq = buildYiDuiJieAddUserReq(createAppReq);
        //增加子帐号
        YiDuiJieAddUserResp jieAddUserResp = addUser(token, addUserReq);
        //子帐号id
        String userId = jieAddUserResp.getUserId();
        //增加客户端
        YiDuiJieAddClientResp jieAddClientResp = addClient(createAppReq, token, userId);
        YiDuiJieClient client = jieAddClientResp.getClient();
        //客户端id
        String clientId = client.getClientId();
        //增加应用实例
        YiDuiJieAddAppInstanceResp addAppInstanceResp = addAppInstance(createAppReq, token, userId, clientId);
        //应用实例id
        String appInstanceId = addAppInstanceResp.getApp().getId();
        //保存易对接信息
        saveYiDuiJieConf(createAppReq, addUserReq, userId, clientId, appInstanceId);
        //初始化导出配置
        setExportConfig(token, appInstanceId);
    }

    private void setExportConfig(String token, String appInstanceId) {
        Map<String, Object> req = Maps.newHashMap();
        req.put("export.header.titles", "摘要;部门;人员;科目名称;科目编码;借方;贷方");
        req.put("export.header.values", "item.summary;item.deptName;item.personName;item.accountName;item.accountCode;item.debit;item.credit");
        yiDuiJieConfigApi.setConfig(token, appInstanceId, req);
    }

    private void saveYiDuiJieConf(CreateAppReqDTO createAppReq, YiDuiJieAddUserReq addUserReq, String userId, String clientId, String appInstanceId) {
        YiDuiJieConf yiDuiJieConf = YiDuiJieConf.builder()
                .companyId(createAppReq.getCompanyId())
                .companyName(createAppReq.getCompanyName())
                .userId(userId)
                .clientId(clientId)
                .appId(appInstanceId)
                .remark(getRemark(createAppReq, addUserReq))
                .build();
        yiDuiJieConfDao.saveSelective(yiDuiJieConf);
    }

    @SuppressWarnings("all")
    private String getRemark(CreateAppReqDTO createAppReq, YiDuiJieAddUserReq addUserReq) {
        JSONObject jsonObject = JsonUtils.toObj(JsonUtils.toJsonSnake(addUserReq), JSONObject.class);
        jsonObject.put("app_id", createAppReq.getAppId());
        jsonObject.put("app_title", createAppReq.getAppTitle());
        return JsonUtils.toJson(jsonObject);
    }

    private YiDuiJieAddUserReq buildYiDuiJieAddUserReq(CreateAppReqDTO createAppReq) {
        CompanySuperAdmin superAdmin = getCompanySuperAdmin(createAppReq.getCompanyId());
        return YiDuiJieAddUserReq.builder()
                .username(createAppReq.getCompanyId() + "-" + createAppReq.getAppId() + RandomUtils.randomStr(2))
                .password(RandomUtils.randomStr(10))
                .email(superAdmin.getEmail())
                .companyName(createAppReq.getCompanyName()).build();
    }

    private YiDuiJieAddAppInstanceResp addAppInstance(CreateAppReqDTO createAppReq, String token, String userId, String clientId) {
        YiDuiJieAddAppInstanceResp addAppInstanceResp = yiDuiJieAppApi.addAppInstance(token, YiDuiJieAddAppInstanceReq.builder().userId(userId).clientId(clientId).appId(createAppReq.getAppId()).build());
        if (addAppInstanceResp == null || !addAppInstanceResp.success() || addAppInstanceResp.getApp() == null) {
            throw new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.CREATE_APP_ERROR));
        }
        return addAppInstanceResp;
    }

    private YiDuiJieAddClientResp addClient(CreateAppReqDTO createAppReq, String token, String userId) {
        YiDuiJieAddClientResp jieAddClientResp = yiDuiJieClientApi.addClient(token, YiDuiJieAddClientReq.builder().name(createAppReq.getCompanyName() + "-" + createAppReq.getAppTitle()).userId(userId).build());
        if (jieAddClientResp == null || !jieAddClientResp.success() || jieAddClientResp.getClient() == null) {
            throw new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.CREATE_APP_ERROR));
        }
        return jieAddClientResp;
    }

    private YiDuiJieAddUserResp addUser(String token, YiDuiJieAddUserReq addUserReq) {
        YiDuiJieAddUserResp jieAddUserResp = yiDuiJieUserApi.addUser(token, addUserReq);
        if (jieAddUserResp == null || !jieAddUserResp.success()) {
            throw new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.CREATE_APP_ERROR));
        }
        return jieAddUserResp;
    }

    private CompanySuperAdmin getCompanySuperAdmin(String companyId) {
       return superAdminUtils.companySuperAdmin(companyId);
    }

}
