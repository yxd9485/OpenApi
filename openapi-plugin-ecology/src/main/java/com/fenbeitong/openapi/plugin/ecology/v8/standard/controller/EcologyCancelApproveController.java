package com.fenbeitong.openapi.plugin.ecology.v8.standard.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.util.HttpServletRequestUtils;
import com.fenbeitong.openapi.plugin.ecology.v8.service.IDealApproveService;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.fanwei.ApplyRevokeDto;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 取消泛微审批流
 * @Auther zhang.peng
 * @Date 2021/11/18
 */
@RestController
@RequestMapping("/ecology/standard/fanwei/cancel")
@Slf4j
public class EcologyCancelApproveController {

    @Autowired
    private IDealApproveService dealApproveService;

    @RequestMapping("/approve")
    @ResponseBody
    public Object cancelApprove(HttpServletRequest request) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        boolean result = false;
        try {
            ApplyRevokeDto applyRevokeDto = JsonUtils.toObj(requestBody,ApplyRevokeDto.class);
            if ( null == applyRevokeDto ){
                return OpenapiResponseUtils.error(-1, "撤销审批失败:转换 applyRevokeDto 失败");
            }
            String msg = applyRevokeDto.getMsg();
            Map msgMap = JsonUtils.toObj(msg,Map.class);
            result = dealApproveService.revokeAndDeleteEcologyApprove(applyRevokeDto.getCompanyId(),(String)msgMap.get("id"),applyRevokeDto.getUserId());
            log.info("删除结果 {}",result);
        } catch (Exception e){
            log.info("删除泛微审批失败 : {}",e.getMessage());
            return OpenapiResponseUtils.error(-1, "撤销审批失败:");
        }
        return result ? OpenapiResponseUtils.success("删除成功") : OpenapiResponseUtils.error(-1,"删除失败 : 泛微接口返回失败");
    }

}
