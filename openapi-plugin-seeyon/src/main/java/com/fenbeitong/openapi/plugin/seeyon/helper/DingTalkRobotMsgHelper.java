package com.fenbeitong.openapi.plugin.seeyon.helper;

import com.fenbeitong.openapi.plugin.seeyon.enums.JsonNamingStrategyEnum;
import com.fenbeitong.openapi.plugin.seeyon.utils.RobotMsg;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

/**
 * DingTalkRobotMsgHelper
 *
 * @author ivan
 * @version 1.0 Created by ivan on 4/8/19 - 3:54 PM.
 */
public class DingTalkRobotMsgHelper {

    public static void sendRobotMsg(String url, RobotMsg robotMsg) {
        RestHttpUtils restHttpUtils = new RestHttpUtils();
        String result = restHttpUtils.postJson(url, JsonUtils.toJsonSnake(robotMsg));
//    restCaller.doPostWithJson(
//        url, robotMsg, new HashMap<>(0), JsonNamingStrategyEnum.LOWER_CAMEL_CASE);
//  }
        return;
    }
}
