package com.fenbeitong.openapi.plugin.yunzhijia.service.impl.auth;/**
 * <p>Title: YunzhijiaUserAuthServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author lizhen
 * @date 2021/4/30 3:38 下午
 */

import com.fenbeitong.finhub.common.constant.CompanyLoginChannelEnum;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseCode;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.ticket.YunzhijiaTicketRespDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.exception.YunzhijiaException;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaTicketService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaUserAuthService;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * Created by lizhen on 2021/4/30.
 */
@Slf4j
@ServiceAspect
@Service
public class YunzhijiaUserAuthServiceImpl implements IYunzhijiaUserAuthService {
    @Autowired
    private IYunzhijiaTicketService yunzhijiaTicketService;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;
    @Autowired
    OpenEmployeeServiceImpl openEmployeeService;

    @Override
    public LoginResVO userAuth(String corpId, String appId, String ticket) {
        PluginCorpDefinition corpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        if (corpDefinition == null) {
            throw new YunzhijiaException(NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_FB_EMPLOYEE_IS_NOT_EXIST));
        }
        YunzhijiaTicketRespDTO yunzhijiaTicketRespDTO = yunzhijiaTicketService.analysisTicket(corpId, appId, ticket);
        String thirdEmployeeId = yunzhijiaTicketRespDTO.getOpenid();
        LoginResVO loginResVO = openEmployeeService.loginAuthInitWithChannelInfo(corpDefinition.getAppId(), thirdEmployeeId, "1", CompanyLoginChannelEnum.YUNZHIJIA_H5);
        return loginResVO;
    }
}
