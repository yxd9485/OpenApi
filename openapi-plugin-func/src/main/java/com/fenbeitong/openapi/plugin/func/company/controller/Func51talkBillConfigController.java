package com.fenbeitong.openapi.plugin.func.company.controller;

import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.company.dto.BillConfig51TalkSyncReqDTO;
import com.fenbeitong.openapi.plugin.func.company.service.BillConfig51TalkServiceImpl;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: Func51talkBillConfigController</p>
 * <p>Description: 51talk账单配置控制</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/9/24 4:47 PM
 */
@RestController
@RequestMapping("/func/51talkbillconfig")
public class Func51talkBillConfigController {

    @Autowired
    private BillConfig51TalkServiceImpl billConfig51TalkService;

    @FuncAuthAnnotation
    @RequestMapping("/sync")
    public Object sync(ApiRequest request) {
        BillConfig51TalkSyncReqDTO req = JsonUtils.toObj(request.getData(), BillConfig51TalkSyncReqDTO.class);
        billConfig51TalkService.doSync(req);
        return FuncResponseUtils.success(Maps.newHashMap());
    }
}
