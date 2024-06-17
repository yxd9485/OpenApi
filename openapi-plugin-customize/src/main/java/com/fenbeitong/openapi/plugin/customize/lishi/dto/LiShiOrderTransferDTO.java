package com.fenbeitong.openapi.plugin.customize.lishi.dto;

import lombok.Data;

import java.util.Map;

/**
 * <p>Title: LiShiOrderTransferDTO</p>
 * <p>Description: 理士订单转换结果</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/11 11:40 AM
 */
@Data
public class LiShiOrderTransferDTO {

    private Map air;

    private Map train;

    private Map hotel;

    public Map getData() {
        return air != null ? air : train != null ? train : hotel;
    }
}
