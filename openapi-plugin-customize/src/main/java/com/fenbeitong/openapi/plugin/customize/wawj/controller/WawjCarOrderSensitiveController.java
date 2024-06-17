package com.fenbeitong.openapi.plugin.customize.wawj.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.wawj.service.IWawjCarOrderSensitiveService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: WawjCarOrderSensitiveController</p>
 * <p>Description: 我爱我家用车敏感订</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/5 11:49 AM
 */
@RestController
@RequestMapping("/customize/5i5j/carOrder")
public class WawjCarOrderSensitiveController {

    @Autowired
    private IWawjCarOrderSensitiveService carOrderSensitiveService;

    @RequestMapping("/setSensitive")
    public Object setSensitive(String companyId, int day) {
        carOrderSensitiveService.setSensitive(companyId, day);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }
}
