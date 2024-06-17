package com.fenbeitong.openapi.plugin.beisen.standard.service;

import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenJobParamDTO;
import com.fenbeitong.openapi.plugin.beisen.common.entity.BeisenCorp;
import com.fenbeitong.openapi.plugin.support.employee.dto.AddAuthRankReqDTO;

import java.util.List;

/**
 * <p>Title: IBeisenSyncRank<p>
 * <p>Description: 北森全量同步职级<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author: liuhong
 * @date: 2022/9/12 15:33
 */
public interface IBeisenSyncRank extends IBeisenSyncOrg {
    /**
     * 全量同步职级并转换
     *
     * @param jobParamDTO 参数实体
     * @param beisenCorp  北森公司实体信息
     * @return
     */
    List<AddAuthRankReqDTO> getRankList(BeisenJobParamDTO jobParamDTO, BeisenCorp beisenCorp);
}
