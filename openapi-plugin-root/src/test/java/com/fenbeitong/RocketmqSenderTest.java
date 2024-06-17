package com.fenbeitong;

import com.alibaba.fastjson.JSON;
import com.fenbeitong.openapi.plugin.event.callback.dto.BillDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * module: rocketmq测试<br/>
 * <p>
 * description: test<br/>
 *
 * @author FuQiang
 * @date 2022/4/29 17:23
 * @since 2.0
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Slf4j
public class RocketmqSenderTest {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Test
    public void testBillCallback() {

        BillDTO billDTO = BillDTO
            .builder()
            .companyId("test")
            .billNo("test")
            .state(2)
            .build();
        String topic = "FENBEI_SETTLEMENT_BILL_AFFIRM_TOPIC_DEV";

        rocketMQTemplate.asyncSend(topic, JSON.toJSONString(billDTO), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("[rocketmqProducer] [sendMsgRocketmqSuccess] [traceId:{}] [msgId:{}] Topic:{},payload:{}",
                    "", sendResult.getMsgId(), topic, "");
            }

            @Override
            public void onException(Throwable e) {
                log.error("[rocketmqProducer] [sendMsgRocketmqError] [traceId]:{} Topic:{},payload:{}", "", topic, "", e);
            }
        });

        while (true) {

        }
    }
}
