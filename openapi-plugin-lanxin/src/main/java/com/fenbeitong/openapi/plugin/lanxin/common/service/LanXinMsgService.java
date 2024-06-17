package com.fenbeitong.openapi.plugin.lanxin.common.service;

import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>Title: LanXinMsgService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/12/13 3:56 下午
 */
@Slf4j
public class LanXinMsgService {
    @Autowired
    private ExceptionRemind exceptionRemind;
    @Autowired
    AuthDefinitionDao authDefinitionDao;

    /**
     * 钉钉消息通知
     */
    public void sendMsg(String companyId, String url, Object req, Object e) {
        AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(companyId);
        String msg = String.format("请求蓝信接口异常\n企业id：[%s]\n企业名称：[%s]\n请求url：[%s]\n请求参数：[%s]\n异常信息：[%s]", companyId, authDefinition.getAppName(), url, JsonUtils.toJson(req), e);
        exceptionRemind.remindDingTalk(msg);
    }
}
