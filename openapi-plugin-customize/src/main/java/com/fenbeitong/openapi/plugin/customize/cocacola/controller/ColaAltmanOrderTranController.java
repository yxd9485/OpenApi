package com.fenbeitong.openapi.plugin.customize.cocacola.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.cocacola.dto.ColaAltmanOrderDTO;
import com.fenbeitong.openapi.plugin.customize.cocacola.service.ColaAltmanOrderTranService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 可口可乐万能订单转换接口
 *
 * @author ctl
 * @date 2021/11/19
 */
@RestController
@RequestMapping("/customize/cola")
public class ColaAltmanOrderTranController {

    @Autowired
    private ColaAltmanOrderTranService colaAltmanOrderTranService;

    /**
     * 可口可乐万能订单转换
     *
     * @param sourceStr
     * @return
     */
    @PostMapping("/tran")
    public Object tran(@RequestBody String sourceStr) {
        return OpenapiResponseUtils.success(colaAltmanOrderTranService.tran(sourceStr));
    }

    /**
     * 可口可乐万能订单推送
     *
     * @param data
     * @param companyId
     * @return
     */
    @PostMapping("/push/{companyId}")
    public Object push(@RequestBody ColaAltmanOrderDTO data, @PathVariable String companyId) {
        colaAltmanOrderTranService.push(data, companyId);
        return OpenapiResponseUtils.success(null);
    }
}
