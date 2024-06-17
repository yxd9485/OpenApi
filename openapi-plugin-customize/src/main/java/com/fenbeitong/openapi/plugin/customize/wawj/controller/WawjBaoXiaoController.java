package com.fenbeitong.openapi.plugin.customize.wawj.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.wawj.dto.WawjBaoXiaoPushReqDTO;
import com.fenbeitong.openapi.plugin.customize.wawj.service.IWawjBaoXiaoService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: WawjBaoXiaoPushController</p>
 * <p>Description: 我爱我家报销单推送</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/11/19 10:17 AM
 */
@RestController
@RequestMapping("/customize/5i5j/baoxiao")
public class WawjBaoXiaoController {

    @Autowired
    private IWawjBaoXiaoService wawjBaoXiaoService;

    @RequestMapping("/push")
    public Object push(@RequestBody WawjBaoXiaoPushReqDTO req) throws Exception {
        wawjBaoXiaoService.push(req.getCompanyId(), req.getBatchIdList());
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }
}
