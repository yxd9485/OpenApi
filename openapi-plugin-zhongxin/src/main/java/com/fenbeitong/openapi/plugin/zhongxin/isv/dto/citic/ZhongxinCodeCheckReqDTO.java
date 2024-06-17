package com.fenbeitong.openapi.plugin.zhongxin.isv.dto.citic;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * <p>Title: ZhongxinCodeCheckReqDTO </p>
 * <p>Description: 授权码授权</p>
 * <p>Company:  中信银行</p>
 *
 * @author: haoqiang.wang
 * @Date: 2021/4/17 下午5:06
 **/
@Data
public class ZhongxinCodeCheckReqDTO extends BaseZhongxinIsvReqDTO{
    /**
     * 企业id
     */
    @JSONField(name="CORP_ID")
    private String CORP_ID;

    /**
     * 经办人用户名（手机号）
     */
    @JSONField(name="USER_ID")
    private String USER_ID;

    /**
     * 校验码
     */
    @JSONField(name="CHECKCODE")
    private String CHECKCODE;
}
