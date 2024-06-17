package com.fenbeitong.openapi.plugin.seeyon.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * MailMsg
 *
 * @author Ivan
 * @version 1.0 Create by Ivan on 2019/4/10 - 11:17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailMsg {
  private String customerId;
  private String serverId;
  private List<String> toList;
  private List<String> ccList;
  private List<String> bccList;
  private String subject;
  private String text;
  private Map<String,Object> html;

}
