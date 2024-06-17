package com.fenbeitong.openapi.plugin.zhongxin.isv.dto.citic;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * <p>Title:  ZhongxinUserAddReqDTO</p>
 * <p>Description: </p>
 * <p>Company:  </p>
 *
 * @author: haoqiang.wang
 * @Date: 2021/4/19 下午7:03
 **/
@Data
public class ZhongxinUserAddReqDTO extends BaseZhongxinIsvReqDTO{
    /**
     * 企业协议号
     */
    @JSONField(name="INNPRTCNO")
    private String INNPRTCNO;

    /**
     * 员工数组
     */
    @JSONField(name="EMPLIST")
    private List<ZhongxinUserAddInfoReqDTO> EMPLIST;
}
