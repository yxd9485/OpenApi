package com.fenbeitong.openapi.plugin.zhongxin.isv.dto.citic;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * <p>Title:  ZhongxinUserReqListDTO</p>
 * <p>Description: </p>
 * <p>Company:  </p>
 *
 * @author: haoqiang.wang
 * @Date: 2021/4/19 下午7:04
 **/
@Data
public class ZhongxinUserAddInfoReqDTO {
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

    /**
     * 姓名
     */
    @JSONField(name="NAME")
    private String NAME;

    /**
     * 证件类型
     */
    @JSONField(name="IDTYPE")
    private String IDTYPE;

    /**
     * 证件号
     */
    @JSONField(name="IDNO")
    private String IDNO;
}
