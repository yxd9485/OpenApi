package com.fenbeitong.openapi.plugin.wechat;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WechatUserListRespDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by log.chang on 2019/12/12.
 */
@RestController
@RequestMapping("/test/wechat")
public class MainTest {

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @RequestMapping("/sync")
    public Object main() {
        String companyId = "6108e6cb1fb413642854662b";
        String json = "{\"userid\":\"f-128330685401512630\",\"name\":\"徐佳庆\",\"department\":[29],\"position\":\"销售工程师（苏）\",\"mobile\":\"18662884005\",\"gender\":\"1\",\"email\":\"xujiaqing@rwdmall.com\",\"avatar\":\"https://wework.qpic.cn/wwhead/duc2TvpEgSTPk74IwG7Bs8Hv5RHlkd81T3WKSabaxpnzu9Qkia6q12JqUXNst7RR4crm5XyuCE9w/0\",\"status\":1,\"enable\":1,\"isleader\":0,\"extattr\":{\"attrs\":[{\"name\":\"分贝权限\",\"value\":\"6\",\"type\":0,\"text\":{\"value\":\"6\"}}]},\"hide_mobile\":0,\"telephone\":\"\",\"order\":[999979627],\"main_department\":29,\"qr_code\":\"https://open.work.weixin.qq.com/wwopen/userQRCode?vcode=vc45fc05125f1424d1\",\"alias\":\"\",\"is_leader_in_dept\":[0],\"thumb_avatar\":\"https://wework.qpic.cn/wwhead/duc2TvpEgSTPk74IwG7Bs8Hv5RHlkd81T3WKSabaxpnzu9Qkia6q12JqUXNst7RR4crm5XyuCE9w/100\"}";
        WechatUserListRespDTO.WechatUser wechatUser = JsonUtils.toObj(json, WechatUserListRespDTO.WechatUser.class);
        OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
        openThirdEmployeeDTO.setCompanyId(companyId);
        openThirdEmployeeDTO.setThirdDepartmentId(wechatUser.getDepartmentStr());
        openThirdEmployeeDTO.setThirdEmployeeId(wechatUser.getUserId());
        //openThirdEmployeeDTO.setThirdEmployeeName(wechatUser.getName());
        openThirdEmployeeDTO.setThirdEmployeePhone(wechatUser.getMobile());
        openThirdEmployeeDTO.setThirdEmployeeEmail(wechatUser.getEmail());
        if (!StringUtils.isBlank(wechatUser.getGender())) {
            openThirdEmployeeDTO.setThirdEmployeeGender(Integer.valueOf(wechatUser.getGender()));
        }
        // 1=已激活，2=已禁用，4=未激活，5=退出企业。
        if (1 == wechatUser.getStatus() || 2 == wechatUser.getStatus()) {
            openThirdEmployeeDTO.setStatus(wechatUser.getStatus());
        }
        // 未激活算正常状态
        if (4 == wechatUser.getStatus()) {
            openThirdEmployeeDTO.setStatus(1);
        }
        if ("1".equals(openThirdEmployeeDTO.getThirdDepartmentId())) {
            openThirdEmployeeDTO.setThirdDepartmentId("xxx");
        }

        // 权限
        String nFbPriv = wechatUser.getAttrValueByAttrName("分贝权限", "");
        openThirdEmployeeDTO.setThirdEmployeeRoleTye(nFbPriv);
        //同步
        openSyncThirdOrgService.syncThird(OpenType.WECHAT_EIA.getType(), companyId, Lists.newArrayList(), Lists.newArrayList(openThirdEmployeeDTO));
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

}
