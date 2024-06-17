//package com.fenbeitong.openapi.plugin.wechat.eia.service.wechat;
//
//import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
//import com.fenbeitong.openapi.plugin.util.JsonUtils;
//import com.fenbeitong.openapi.plugin.wechat.eia.dao.WechatTokenConfDao;
//import com.fenbeitong.openapi.plugin.wechat.eia.entity.WechatTokenConf;
//import com.google.common.collect.Maps;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
//import org.springframework.util.ObjectUtils;
//
//import java.util.Map;
//
///**
// * <p>Title: ThirdWechatTokenService</p>
// * <p>Description: </p>
// * <p>Company: www.fenbeitong.com</p>
// *
// * @author hwangsy
// * @date 2020/2/26 4:15 PM
// */
//@ServiceAspect
//@Service
//public class WechatEiaTokenService extends WechatTokenService {
//
//    @Autowired
//    private RestHttpUtils httpUtils;
//
//    @Autowired
//    private WechatTokenConfDao wechatTokenConfDao;
//
//    @Override
//    public String getWeChatContactToken(String companyId) {
//        WechatTokenConf wechatTokenConf = wechatTokenConfDao.getByCompany(companyId);
//        String requestMethod = wechatTokenConf.getRequestMethod();
//        String requestUrl = wechatTokenConf.getRequestUrl();
//        String requestBody = wechatTokenConf.getRequestBody();
//        String tokenExpress = wechatTokenConf.getTokenExpress();
//        String result = null;
//        if ("GET".equals(requestMethod)) {
//            result = httpUtils.get(requestUrl, Maps.newHashMap());
//        } else {
//            result = httpUtils.postJson(requestUrl, ObjectUtils.isEmpty(requestBody) ? "{}" : requestBody);
//        }
//        result = ObjectUtils.isEmpty(result) ? "" : result;
//        if ("*".equals(tokenExpress)) {
//            return result;
//        }
//        result = ObjectUtils.isEmpty(result) ? "{}" : result;
//        Map resultMap = JsonUtils.toObj(result, Map.class);
//        String[] expressList = tokenExpress.split(":");
//        String token = null;
//        Map middleMap = resultMap;
//        for (int i = 0; i < expressList.length; i++) {
//            if (i == expressList.length - 1) {
//                token = (String) middleMap.get(expressList[i]);
//            } else {
//                middleMap = (Map) middleMap.get(expressList[i]);
//            }
//        }
//        return token;
//    }
//}
