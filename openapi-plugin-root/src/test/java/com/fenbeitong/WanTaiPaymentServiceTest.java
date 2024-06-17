package com.fenbeitong;

import com.fenbeitong.openapi.plugin.customize.wantai.service.WanTaiPaymentService;
import com.fenbeitong.openapi.plugin.func.callback.dto.PaymentRecordDTO;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author machao
 * @date 2022/9/28
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class WanTaiPaymentServiceTest {

    @Autowired
    private WanTaiPaymentService wanTaiPaymentService;

    @Test
    void successNotice() {
        PaymentRecordDTO req = new PaymentRecordDTO();
        req.setCompanyId("5d1b1d2f23445f4dca76304b");
        req.setPaymentState(80);
        req.setPaymentId("PMT202209281045574610944");
        wanTaiPaymentService.successNotice(req);
    }
}
