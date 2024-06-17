package com.fenbeitong.openapi.plugin.zhongxin.isv.dto.citic;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * <p>Title:  ZhongxinCodeCheckRespDTO</p>
 * <p>Description: </p>
 * <p>Company:  </p>
 *
 * @author: haoqiang.wang
 * @Date: 2021/4/17 下午5:09
 **/
@Data
public class ZhongxinCodeCheckRespDTO extends BaseZhongxinIsvRespDTO{
    /**
     * 校验结果
     * 0-失败
     * 1-成功
     */
    @JSONField(name="CHECKRESULT")
    private String CHECKRESULT;
}
