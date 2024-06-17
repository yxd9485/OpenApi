package com.fenbeitong.openapi.plugin.kingdee.customize.xindi.controller;

import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.kingdee.customize.xindi.service.impl.ReimburseCallbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: LiShiBillCallbackController</p>
 * <p>Description: 报销单数据推送接口</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/10/09
 */
@RestController
@RequestMapping("/customize/reimburse")
@Slf4j
public class ReimburseJobCallbackController {

    @Autowired
    private ReimburseCallbackService reimburseCallbackService;

    @RequestMapping("/create")
    public Object pushReimburseData() {
        if (reimburseCallbackService.createReimburseJob()){
            return FuncResponseUtils.success("success");
        }else {
            return FuncResponseUtils.error(-1,"failure");
        }
    }
}

