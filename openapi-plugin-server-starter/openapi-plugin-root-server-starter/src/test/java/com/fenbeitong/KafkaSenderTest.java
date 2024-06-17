package com.fenbeitong;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * module: kafka测试<br/>
 * <p>
 * description: 描述<br/>
 *
 * @author FuQiang
 * @date 2022/4/27 16:18
 * @since 2.0
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Slf4j
public class KafkaSenderTest {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Test
    public void testThirdApplyCallback() {
        String msg = "{\"title\":\"对接审批通知-虚拟卡额度审批\",\"content\":\"你的虚拟卡额度审批已撤销\",\"desc\":\"你的虚拟卡额度审批已撤销\",\"alert\":true,\"msgType\":\"revoke\",\"msg\":\"{\\\"myself\\\":true,\\\"setting_type\\\":\\\"8\\\",\\\"view_type\\\":\\\"1\\\",\\\"id\\\":\\\"6268f8ce3cbc7f03632af2d2\\\",\\\"apply_type\\\":15}\",\"userId\":\"61768f971e7b1e65977fe65d\",\"companyId\":\"6171346fe39eaa41a24a2d31\",\"applyId\":\"6268f8ce3cbc7f03632af2d2\",\"settingType\":8}";
        kafkaTemplate.send("topic_apply_operate_msg", msg);
        while (true) {

        }
    }



}
