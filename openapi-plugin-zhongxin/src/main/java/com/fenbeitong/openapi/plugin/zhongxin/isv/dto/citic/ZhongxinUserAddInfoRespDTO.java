package com.fenbeitong.openapi.plugin.zhongxin.isv.dto.citic;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * <p>Title:  </p>
 * <p>Description: </p>
 * <p>Company:  </p>
 *
 * @author: haoqiang.wang
 * @Date: 2021/4/20 上午9:39
 **/
@Data
public class ZhongxinUserAddInfoRespDTO {

    /**
     * 员工编号
     */
    @JSONField(name="EMPCODE")
    private String EMPCODE;

    /**
     * 员工唯一标识
     */
    @JSONField(name="HASH")
    private String HASH;

    /**
     * 返回码，表示成功失败
     */
    @JSONField(name="RETCODE")
    private String RETCODE;

    /**
     * 返回信息
     */
    @JSONField(name="RETINFO")
    private String RETINFO;
}
