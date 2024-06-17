package com.fenbeitong.openapi.plugin.wechat.common;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * OpenApiPros
 *
 * <p>OPEN API属性类
 *
 * @author ivan
 * @version 1.0 Created by ivan on 3/6/19 - 4:11 PM.
 */
@Component
//@PropertySource("classpath:/application-dev.yml")
@ConfigurationProperties(prefix = "wechat")
@Data
public class WeChatPros {


  private String wechatUrl = "";
//  @Value("${wechat.callback-tag}")
//  private String callBackTag = "";

}
