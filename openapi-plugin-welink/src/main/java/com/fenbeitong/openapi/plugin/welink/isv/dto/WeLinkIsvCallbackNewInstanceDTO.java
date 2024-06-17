package com.fenbeitong.openapi.plugin.welink.isv.dto;

import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author lizhen
 * @date 2020/4/28
 */
@Data
public class WeLinkIsvCallbackNewInstanceDTO extends WeLinkIsvCallbackMarketCorpBaseDTO {

    /**
     * 客户在华为云注册账号的唯一标识
     */
    private String customerId;

    /**
     * 客户在华为云注册的账户名
     */
    private String customerName;

    /**
     * 客户以IAM用户认证方式登录时对应子用户的唯一标识
     */
    private String userId;

    /**
     * 客户以IAM用户认证方式登录的用户名
     */
    private String userName;

    /**
     * 客户手机号
     */
    private String mobilePhone;

    /**
     * 客户邮箱
     */
    private String email;

    /**
     * 云市场业务ID
     */
    private String businessId;

    /**
     * 云市场订单ID
     */
    private String orderId;

    /**
     * 产品规格标识
     */
    private String skuCode;

    /**
     * 产品标识
     */
    private String productId;

    /**
     * 是否为调试请求,1：调试请求,0： 非调试请求
     */
    private String testFlag;

    /**
     * 是否是开通试用实例,1：试用实例,0：非试用实例
     */
    private String trialFlag;

    /**
     *
     */
    private String expireTime;

    /**
     * 计费模式。3：表示按次购买。
     */
    private Integer chargingMode;

    /**
     * 扩展参数
     */
    private String saasExtendParams;

    /**
     * 数量类型的商品定价属性
     */
    private Integer amount;

    @Data
    public static class PlatformParams {

        private String tenantName;

        private String tennantId;

        private String userId;
    }

    public PlatformParams getPlatformParams() {
        if (!StringUtils.isBlank(saasExtendParams)) {
            List<Map<String, String>> arrayList = JsonUtils.toObj(saasExtendParams, ArrayList.class);
            if (!ObjectUtils.isEmpty(arrayList)) {
                for (Map<String, String> saasExtendParam : arrayList) {
                    if("platformParams".equals(saasExtendParam.get("name"))) {
                        return JsonUtils.toObj(saasExtendParam.get("value"), PlatformParams.class);
                    }
                }
            }
        }
        return null;
    }


}
