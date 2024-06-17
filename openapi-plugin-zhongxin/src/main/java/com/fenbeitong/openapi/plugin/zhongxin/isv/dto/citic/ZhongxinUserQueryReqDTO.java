package com.fenbeitong.openapi.plugin.zhongxin.isv.dto.citic;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * <p>Title:  ZhongxinUserQueryReqDTO</p>
 * <p>Description: 根据手机号查询三方id请求数据</p>
 * <p>Company:  中信银行</p>
 *
 * @author: haoqiang.wang
 * @Date: 2021/4/22 下午3:45
 **/
@Data
public class ZhongxinUserQueryReqDTO extends BaseZhongxinIsvReqDTO{
    /**
     * 企业协议号
     */
    @JSONField(name="INNPRTCNO")
    private String INNPRTCNO;

    /**
     * 查询方式
     */
    @JSONField(name="QRYTYP")
    private String QRYTYP;

    /**
     * 员工编号
     */
    @JSONField(name="EMPCODE")
    private String EMPCODE;

    /**
     * 手机号
     */
    @JSONField(name="MOBNO")
    private String MOBNO;
}
