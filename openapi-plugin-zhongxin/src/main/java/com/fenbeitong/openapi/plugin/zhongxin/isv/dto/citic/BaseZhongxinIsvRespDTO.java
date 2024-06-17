package com.fenbeitong.openapi.plugin.zhongxin.isv.dto.citic;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: BaseZhongxinIsvRespDTO </p>
 * <p>Description: 返回中信银行报文基类</p>
 * <p>Company: 中信银行 </p>
 *
 * @author: haoqiang.wang
 * @Date: 2021/4/17 上午10:17
 **/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseZhongxinIsvRespDTO {
    @JSONField(name="RETCODE")
    private String RETCODE;

    @JSONField(name="RETMSG")
    private String RETMSG;
}
