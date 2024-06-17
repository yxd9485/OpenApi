package com.fenbeitong.openapi.plugin.customize.wawj.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.wawj.service.IWawjBillService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: WawjBillController</p>
 * <p>Description: 我爱我家账单保存</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/11/9 5:54 PM
 */
@RestController
@RequestMapping("/customize/5i5j/bill")
public class WawjBillController {

    @Autowired
    private IWawjBillService wawjBillService;

    @RequestMapping("/save")
    public Object save(String companyId, String billNo, @RequestParam(value = "delete", required = false) Integer delete) {
        wawjBillService.save(companyId, billNo, delete);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

}
