package com.fenbeitong.openapi.plugin.zhongxin.isv.dto.citic;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * <p>Title:  ZhongxinUserQueryRespDTO</p>
 * <p>Description: 根据手机号查询三方id返回数据</p>
 * <p>Company: 中信银行 </p>
 *
 * @author: haoqiang.wang
 * @Date: 2021/4/22 下午3:46
 **/
@Data
public class ZhongxinUserQueryRespDTO extends BaseZhongxinIsvRespDTO{
    /**
     * 员工唯一标识
     */
    @JSONField(name="HASH")
    private String HASH;
}
