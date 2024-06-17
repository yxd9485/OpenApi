package com.fenbeitong.openapi.plugin.customize.lishi.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * <p>Title: LiShiOrderCallBackResDTO</p>
 * <p>Description: 理士订单推送响应信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/11 2:58 PM
 */
@Data
@XStreamAlias("root")
public class LiShiOrderCallBackResDTO {

    private LiShiOrderCallBackResData data;

    public boolean success() {
        return data != null && data.getResp() != null && "1".equals(data.getResp().getCode());
    }

    @Data
    public static class LiShiOrderCallBackResData {

        private LiShiOrderCallBackResp resp;
    }

    @Data
    public static class LiShiOrderCallBackResp {

        private String code;

        private String message;

        private String status;
    }
}
