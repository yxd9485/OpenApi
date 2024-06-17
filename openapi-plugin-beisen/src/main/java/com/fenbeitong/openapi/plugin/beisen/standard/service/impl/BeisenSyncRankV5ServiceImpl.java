package com.fenbeitong.openapi.plugin.beisen.standard.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.beisen.common.constant.BeiSenConstant;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenJobParamDTO;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenParamConfig;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenRankV5DTO;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenReqBaseDTO;
import com.fenbeitong.openapi.plugin.beisen.common.entity.BeisenCorp;
import com.fenbeitong.openapi.plugin.beisen.standard.service.IBeisenSyncRank;
import com.fenbeitong.openapi.plugin.beisen.standard.service.third.BeisenApiService;
import com.fenbeitong.openapi.plugin.support.employee.dto.AddAuthRankReqDTO;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Title: BeisenSyncRankV5ServiceImpl<p>
 * <p>Description: 北森V5版本职级拉取service<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author: liuhong
 * @date: 2022/9/12 15:48
 */
@ServiceAspect
@Service
@Slf4j
public class BeisenSyncRankV5ServiceImpl implements IBeisenSyncRank {
    @Autowired
    private BeisenApiService beisenApiService;

    @Override
    public List<AddAuthRankReqDTO> getRankList(BeisenJobParamDTO jobParamDTO, BeisenCorp beisenCorp) {
        //1 构建请求参数
        BeisenReqBaseDTO rankReqDTO = new BeisenReqBaseDTO();
        rankReqDTO.setTimeWindowQueryType(1);
        rankReqDTO.setStartTime(StringUtils.isBlank(jobParamDTO.getStartDate()) ? BeiSenConstant.START_DATE : jobParamDTO.getStartDate());
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.FORMAT_DATE_PATTERN_T_1);
        rankReqDTO.setStopTime(StringUtils.isBlank(jobParamDTO.getStartDate()) ? dateFormat.format(new Date()) : jobParamDTO.getEndDate());
        rankReqDTO.setCapacity(300);
        rankReqDTO.setColumns(new String[]{"name", "oId", "status"});
        BeisenReqBaseDTO.ExtQuery extQuery = BeisenReqBaseDTO.ExtQuery.builder().fieldName("Status").queryType(5).values(new String[]{"1"}).build();
        List<BeisenReqBaseDTO.ExtQuery> extQueries = new ArrayList<>();
        extQueries.add(extQuery);
        rankReqDTO.setExtQueries(extQueries);
        //2 从北森拉取数据
        List<Object> beisenRankDTOList = beisenApiService.getResultByTimeWindow(rankReqDTO, beisenCorp, BeiSenConstant.BEISEN_JOB_LEVEL_URL_V5);
        if (CollectionUtils.isBlank(beisenRankDTOList)) {
            return null;
        }
        List<BeisenRankV5DTO> rankV5DTOList = JsonUtils.toObj(JsonUtils.toJson(beisenRankDTOList), new TypeReference<List<BeisenRankV5DTO>>() {
        });
        //3 转换数据
        return rankV5DTOList.stream()
            .map(rankDTO -> AddAuthRankReqDTO.builder()
                .rankName(rankDTO.getName())
                .thirdRankId(rankDTO.getOId())
                .type(BeiSenConstant.RANK_TYPE)
                .build())
            .collect(Collectors.toList());
    }
}
