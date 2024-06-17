package com.fenbeitong.openapi.plugin.customize.wawj.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.wawj.dto.WawjFuLiShenQingSyncReqDTO;
import com.fenbeitong.openapi.plugin.customize.wawj.dto.WawjGrantVoucherByUserReq;
import com.fenbeitong.openapi.plugin.customize.wawj.dto.WawjKaoQinSyncReqDTO;
import com.fenbeitong.openapi.plugin.customize.wawj.dto.WawjKqDTO;
import com.fenbeitong.openapi.plugin.customize.wawj.service.IWawjKaoQinShenQingService;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>Title: WawjKaoQinShenQingController</p>
 * <p>Description: 我爱我家数据同步</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/10/26 5:32 PM
 */
@RestController
@RequestMapping("/customize/5i5j")
public class WawjKaoQinShenQingController {

    @Autowired
    private IWawjKaoQinShenQingService wawjKaoQinShenQingService;

    @FuncAuthAnnotation
    @RequestMapping("/attendance/sync")
    public Object synKaoQin(ApiRequest apiRequest, HttpServletRequest httpRequest) {
        WawjKaoQinSyncReqDTO req = JsonUtils.toObj(apiRequest.getData(), WawjKaoQinSyncReqDTO.class);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        wawjKaoQinShenQingService.synKaoQin(req);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    @FuncAuthAnnotation
    @RequestMapping("/fulishenqing/sync")
    public Object synFuLiShenQing(ApiRequest apiRequest, HttpServletRequest httpRequest) {
        WawjFuLiShenQingSyncReqDTO req = JsonUtils.toObj(apiRequest.getData(), WawjFuLiShenQingSyncReqDTO.class);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        wawjKaoQinShenQingService.synFuLiShenQing(req);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/grantVoucher")
    public Object grantVoucher(String companyId) {
        wawjKaoQinShenQingService.grantVoucher(companyId);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/grantVoucherByUser")
    public Object grantVoucherByUser(@RequestParam("jobConfig") String jobConfig) {
        WawjGrantVoucherByUserReq req = JsonUtils.toObj(jobConfig, WawjGrantVoucherByUserReq.class);
        wawjKaoQinShenQingService.grantVoucherByUser(req);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/updateAttendanceApply")
    public Object updateAttendanceByApply(String companyId) {
        wawjKaoQinShenQingService.updateAttendanceApply(companyId);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/closeApply")
    public Object closeApply(String companyId) {
        wawjKaoQinShenQingService.closeApply(companyId);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/closeAttendance")
    public Object closeAttendance(String companyId) {
        wawjKaoQinShenQingService.closeAttendance(companyId);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/kaoqin/sync/{companyId}")
    public Object kaoqinSync(@PathVariable("companyId") String companyId, @RequestBody List<WawjKqDTO> kqList) {
        wawjKaoQinShenQingService.kaoqinSync(companyId, kqList);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

}
