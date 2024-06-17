package com.fenbeitong.openapi.plugin.seeyon.helper;

import com.fenbeitong.openapi.plugin.seeyon.utils.MailMsg;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;


/**
 * HarmonyMailHelper
 *
 * @author Ivan
 * @version 1.0 Create by Ivan on 2019/4/10 - 11:13
 */
public class HarmonyMailHelper {

  public static String sendHarmonyEmail(String mailUrl, MailMsg mailMsg) {
    RestHttpUtils restHttpUtils = new RestHttpUtils();
    String sendReturn = restHttpUtils.postJson(mailUrl, JsonUtils.toJsonSnake(mailMsg));
    return sendReturn;
  }
}
