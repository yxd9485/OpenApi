package com.fenbeitong.openapi.plugin.zhongxin.isv.dto.citic;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * <p>Title:  BaseZhongxinIsvReqDTO</p>
 * <p>Description: </p>
 * <p>Company:  </p>
 *
 * @author: haoqiang.wang
 * @Date: 2021/4/17 下午4:29
 **/

@Data
public class BaseZhongxinIsvReqDTO {
    /**
     * 开放银行接口版本号
     */
    @JSONField(name="OPENVER")
    private String OPENVER;

    /**
     * 开放银行接口码
     */
    @JSONField(name="OPENTRANSCODE")
    private String OPENTRANSCODE;

    /**
     * 开放银行合作方APPID
     */
    @JSONField(name="OPENMERCODE")
    private String OPENMERCODE;

    /**
     * 开放银行合作方名称
     */
    @JSONField(name="OPENMERNAME")
    private String OPENMERNAME;

    /**
     * 商户业务类型
     */
    @JSONField(name="OPENBUSITYPE")
    private String OPENBUSITYPE;

    /**
     * 商户交易发起日期
     */
    @JSONField(name="OPENLAUNCHDATE")
    private String OPENLAUNCHDATE;

    /**
     * 商户交易发起时间
     */
    @JSONField(name="OPENLAUNCHTIME")
    private String OPENLAUNCHTIME;

    /**
     * 商户请求流水号
     */
    @JSONField(name="OPENMERFLOWID")
    private String OPENMERFLOWID;
}
