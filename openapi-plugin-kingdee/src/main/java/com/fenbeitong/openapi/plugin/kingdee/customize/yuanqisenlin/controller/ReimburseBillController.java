package com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.controller;

import com.fenbeitong.openapi.plugin.func.reimburse.dto.RemiDetailResDTO;
import com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.service.ReimburseBillService;
import com.fenbeitong.saasplus.api.service.bill.IApplyReimburseBillService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 账单生成应付单接口
 *
 * @author ctl
 * @date 2021/7/2
 */
@RestController
@Slf4j
@RequestMapping("/customize/yuanqisenlin")
public class ReimburseBillController {

    @Autowired
    private ReimburseBillService reimburseBillService;


    @DubboReference(check = false)
    private IApplyReimburseBillService applyReimburseBillService;

    /**
     * 费用报销单推送
     * 将分贝通自定义报销单推送到元气金蝶费用报销单（保存，提交，审核）
     *
     * @param data
     * @return
     */
    @PostMapping("/pushReimburseBill")
    public Object pushReimburseBill(@RequestBody RemiDetailResDTO data) {
        return reimburseBillService.pushReimburseBill(data);
    }


}
