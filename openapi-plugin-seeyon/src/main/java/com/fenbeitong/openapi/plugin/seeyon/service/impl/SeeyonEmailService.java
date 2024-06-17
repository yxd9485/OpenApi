package com.fenbeitong.openapi.plugin.seeyon.service.impl;

import com.fenbeitong.openapi.plugin.seeyon.dao.SeeyonOpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountEmpResp;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountOrgResp;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonPersonInfoParam;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOpenMsgSetup;
import com.fenbeitong.openapi.plugin.seeyon.utils.MailMsg;
import com.fenbeitong.openapi.plugin.seeyon.utils.TextMailMsg;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@ServiceAspect
@Service
@Slf4j
public class SeeyonEmailService {
    @Autowired
    RestHttpUtils restHttpUtils;
    @Autowired
    SeeyonOpenMsgSetupDao seeyonOpenMsgSetupDao;
    @Value("${host.harmony}")
    private String harmonyHost;
    @Value("${email.template-id}")
    private String templateId;
    @Autowired
    SeeyonDepartmentServiceImpl seeyonDepartmentService;

    public void sendEmail(String orgName, Map<String, Object> map, StringBuilder contents, String specificEmail) {
        List<SeeyonOpenMsgSetup> list = seeyonOpenMsgSetupDao.listOpenMsgSetup(map);
        if (!ObjectUtils.isEmpty(list)) {//只有设置了属性值，才进行数据的推送，否则不进行数据推送
            SeeyonOpenMsgSetup seeyonOpenMsgSetup = list.get(0);
            if (seeyonOpenMsgSetup.getIntVal1() == 1) {
                String mailCustomerId = seeyonOpenMsgSetup.getStrVal1();
                String mailServerId = seeyonOpenMsgSetup.getStrVal2();
                List<String> mailReceiverList = Lists.newArrayList();
                if (StringUtils.isBlank(specificEmail)) {
                   specificEmail = seeyonOpenMsgSetup.getStrVal3();
                }
                String[] split = specificEmail.split(",");
                mailReceiverList = Arrays.asList(split);
                /* email 消息*/
                MailMsg build = TextMailMsg.builder()
                        .serverId(mailServerId)
                        .customerId(mailCustomerId)
                        .toList(mailReceiverList)
                        .ccList(null)
                        .bccList(null)
                        .subject(orgName + "推送错误信息邮件提醒")
                        .text(contents.toString())
                        .build();
                String harmonyUrl = harmonyHost + "/harmony/mail";
                restHttpUtils.postJson(harmonyUrl, JsonUtils.toJson(build));
            }
        }
    }


    /**
     * 发送html模板邮件
     *
     * @param orgName
     * @param map
     * @param errorEmployeeList
     */
    public void sendHtmlEmail(String orgName, String url, Map<String, String> tokenHeader, Map<String, Object> map, List<SeeyonAccountEmpResp> errorEmployeeList) {
        //查询公司是否配置邮件消息通知
        List<SeeyonOpenMsgSetup> list = seeyonOpenMsgSetupDao.listOpenMsgSetup(map);
        if (!ObjectUtils.isEmpty(list)) {//只有设置了属性值，才进行数据的推送，否则不进行数据推送
            SeeyonOpenMsgSetup seeyonOpenMsgSetup = list.get(0);
            if (seeyonOpenMsgSetup.getIntVal1() == 1) {
                List<SeeyonAccountEmpResp> collect = errorEmployeeList.stream().distinct().collect(Collectors.toList());
                Set<SeeyonAccountEmpResp> errorList = new HashSet<>();
                Set<SeeyonPersonInfoParam> personInfoParams = new HashSet<>();
                for (SeeyonAccountEmpResp error : collect) {
                    //根据部门ID查询部门详细信息
                    Long orgDepartmentId = error.getOrgDepartmentId();
                    List<String> orgIds = Lists.newArrayList();
                    orgIds.add(String.valueOf(orgDepartmentId));
                    List<SeeyonAccountOrgResp> orgDetail = seeyonDepartmentService.getOrgDetail(orgIds, url, tokenHeader);
                    if (!ObjectUtils.isEmpty(orgDetail)) {//根据部门ID可以查询到部门数据
                        SeeyonAccountOrgResp seeyonAccountOrgResp = orgDetail.get(0);
                        SeeyonPersonInfoParam seeyonPersonInfoParam = new SeeyonPersonInfoParam();
                        seeyonPersonInfoParam.setTrueName(error.getName());
                        seeyonPersonInfoParam.setMobilePhone(error.getTelNumber());
                        String wholeName = seeyonAccountOrgResp.getWholeName();
                        if (wholeName.contains(",")) {
                            String[] split = wholeName.split(",");
                            List<String> strings = Arrays.asList(split);
                            wholeName = String.join("/", strings);
                        }
                        seeyonPersonInfoParam.setDepartmentName(wholeName);
                        seeyonPersonInfoParam.setOcupationName(error.getOrgPostName());

                        personInfoParams.add(seeyonPersonInfoParam);
                    }
                }
                log.info("设置完成错误人员数据");

                Map<String, Object> html = Maps.newHashMap();
                html.put("templateId", templateId);
                //模板数据，属性与模板中设置的变量需一致
                Map<String, Object> param = Maps.newHashMap();
                param.put("errorPersonInfoList", personInfoParams);
                html.put("data", param);
                String mailCustomerId = seeyonOpenMsgSetup.getStrVal1();
                String mailServerId = seeyonOpenMsgSetup.getStrVal2();
                String mailListStr = seeyonOpenMsgSetup.getStrVal3();
                String[] split = mailListStr.split(",");
                List<String> mailReceiverList = Arrays.asList(split);
                /* email 消息*/
                MailMsg build = TextMailMsg.builder()
                        .serverId(mailServerId)
                        .customerId(mailCustomerId)
                        .toList(mailReceiverList)
                        .ccList(null)
                        .bccList(null)
                        .subject(orgName + "推送错误信息邮件提醒")
                        .html(html)
                        .build();
                String harmonyUrl = harmonyHost + "/harmony/mail";
                String s = restHttpUtils.postJson(harmonyUrl, JsonUtils.toJson(build));
                log.info("发送邮件通知结果 {}", s);
            }
        }
    }

}
