package com.fenbeitong.openapi.plugin.zhongxin.isv.dto.citic;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * <p>Title:  ZhongxinUserAddRespDTO</p>
 * <p>Description: </p>
 * <p>Company:  </p>
 *
 * @author: haoqiang.wang
 * @Date: 2021/4/20 上午9:38
 **/
@Data
public class ZhongxinUserAddRespDTO extends BaseZhongxinIsvRespDTO{
    @JSONField(name="RESULTLIST")
    private List<ZhongxinUserAddInfoRespDTO> RESULTLIST;
}
