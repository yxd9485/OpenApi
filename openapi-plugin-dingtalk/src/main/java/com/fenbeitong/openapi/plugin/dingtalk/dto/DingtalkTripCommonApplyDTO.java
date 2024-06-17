package com.fenbeitong.openapi.plugin.dingtalk.dto;

import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: TripCommonApplyDTO<p>
 * <p>Description: 差旅通用申请详情<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author liuhong
 * @date 2022/5/12 11:03
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DingtalkTripCommonApplyDTO extends CommonApply {
    /**
     * 差旅申请单是否用车标识
     */
    private Boolean useCarFlag;
}
