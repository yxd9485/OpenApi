package com.fenbeitong.openapi.plugin.yunzhijia.notice.sender;

import com.fenbeitong.openapi.plugin.support.common.notice.sender.AbstractNoticeSender;
import com.fenbeitong.openapi.plugin.support.common.notice.sender.SendMessageDto;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.RandomUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.dao.YunzhijiaMsgSendDao;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaMsgReqDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaPubTokenDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.entity.YunzhijiaMsgSend;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class YunzhijiaNoticeSender extends AbstractNoticeSender {
    @Autowired
    RestHttpUtils restHttpUtils;
    @Autowired
    YunzhijiaMsgSendDao yunzhijiaMsgSendDao;

    @Override
    public void sender(String corpId, List<String> userIds, String msg) {
        //构造to
        //构造from
        String random = RandomUtils.bsonId();
        long timestamp = System.currentTimeMillis();
        //1.根据公司ID查询公众号的ID和key
        Example example = new Example(YunzhijiaMsgSend.class);
        example.createCriteria().andEqualTo("corpId", corpId).andEqualTo("type", 1);
        YunzhijiaMsgSend byExample = yunzhijiaMsgSendDao.getByExample(example);
        if (ObjectUtils.isEmpty(byExample)) {
            return;
        }
        String pubId = byExample.getAgentId();
        String pubSecret = byExample.getAgentSecret();
        YunzhijiaPubTokenDTO pubTokenBuild = YunzhijiaPubTokenDTO.builder()
                .no(corpId)
                .nonce(random)
                .pubId(pubId)
                .pubSecret(pubSecret)
                .time(String.valueOf(timestamp))
                .build();

        String pubToken = sha(pubTokenBuild.getNo(),pubTokenBuild.getPubId(), pubTokenBuild.getPubSecret(), pubTokenBuild.getNonce(), pubTokenBuild.getTime());
        //2.生成token
        YunzhijiaMsgReqDTO.From from = YunzhijiaMsgReqDTO.From
                .builder()
                .no(corpId)
                .nonce(random)
                .pub(pubId)
                .pubtoken(pubToken)
                .time(timestamp)
                .build();
        YunzhijiaMsgReqDTO.To to = YunzhijiaMsgReqDTO.To.builder()
                .no(corpId)
                .user(userIds)
                .build();
        List<YunzhijiaMsgReqDTO.To> toList = Lists.newArrayList();
        toList.add(to);
        //具体消息内容
        YunzhijiaMsgReqDTO.Text text = YunzhijiaMsgReqDTO.Text.builder()
                .text(msg)
                .build();
        //消息发送体
        YunzhijiaMsgReqDTO msgBuilder = YunzhijiaMsgReqDTO.builder()
                .type(2)
                .msg(text)
                .from(from)
                .to(toList)
                .build();
        //调用消息推送
        String url = "https://www.yunzhijia.com/pubacc/pubsend";
        log.info("云之家推送消息请求参数: {}", JsonUtils.toJson(msgBuilder));
        String result = restHttpUtils.postJson(url, JsonUtils.toJson(msgBuilder));
        log.info("云之家推送消息返回结果: {}", result);
    }

    @Override
    public void sender(String corpId, List<String> userIds, SendMessageDto messageDto) {
        //构造to
        //构造from
        String random = RandomUtils.bsonId();
        long timestamp = System.currentTimeMillis();
        //1.根据公司ID查询公众号的ID和key
        Example example = new Example(YunzhijiaMsgSend.class);
        example.createCriteria().andEqualTo("corpId", corpId).andEqualTo("type", 1);
        YunzhijiaMsgSend byExample = yunzhijiaMsgSendDao.getByExample(example);
        if (ObjectUtils.isEmpty(byExample)) {
            return;
        }
        String pubId = byExample.getAgentId();
        String pubSecret = byExample.getAgentSecret();
        YunzhijiaPubTokenDTO pubTokenBuild = YunzhijiaPubTokenDTO.builder()
                .no(corpId)
                .nonce(random)
                .pubId(pubId)
                .pubSecret(pubSecret)
                .time(String.valueOf(timestamp))
                .build();

        String pubToken = sha(pubTokenBuild.getNo(),pubTokenBuild.getPubId(), pubTokenBuild.getPubSecret(), pubTokenBuild.getNonce(), pubTokenBuild.getTime());
        //2.生成token
        YunzhijiaMsgReqDTO.From from = YunzhijiaMsgReqDTO.From
                .builder()
                .no(corpId)
                .nonce(random)
                .pub(pubId)
                .pubtoken(pubToken)
                .time(timestamp)
                .build();
        YunzhijiaMsgReqDTO.To to = YunzhijiaMsgReqDTO.To.builder()
                .no(corpId)
                .user(userIds)
                .build();
        List<YunzhijiaMsgReqDTO.To> toList = Lists.newArrayList();
        toList.add(to);
        //具体消息内容
        YunzhijiaMsgReqDTO.Text text = YunzhijiaMsgReqDTO.Text.builder()
                .build();
        BeanUtils.copyProperties(messageDto,text);
        //消息发送体
        YunzhijiaMsgReqDTO msgBuilder = YunzhijiaMsgReqDTO.builder()
                .type(5)
                .msg(text)
                .from(from)
                .to(toList)
                .build();
        //调用消息推送
        String url = "https://www.yunzhijia.com/pubacc/pubsend";
        log.info("云之家推送消息请求参数: {}", JsonUtils.toJson(msgBuilder));
        String result = restHttpUtils.postJson(url, JsonUtils.toJson(msgBuilder));
        log.info("云之家推送消息返回结果: {}", result);
    }

    private static String sha(String... data) {
        Arrays.sort(data);
        String join = StringUtils.join(data);
        String pubtoken = DigestUtils.sha1Hex(join);
        return pubtoken;
    }

    @Override
    public void sender(String companyId, String userId, String msg) {
        List<String> employeeIds = Lists.newArrayList();
        employeeIds.add(userId);
        this.sender(companyId,employeeIds,msg);
    }
}
