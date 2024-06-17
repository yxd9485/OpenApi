package com.fenbeitong.openapi.plugin.qiqi.service.rank.impl;

import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResponseCode;
import com.fenbeitong.openapi.plugin.qiqi.common.exception.OpenApiQiqiException;
import com.fenbeitong.openapi.plugin.qiqi.constant.ObjectTypeEnum;
import com.fenbeitong.openapi.plugin.qiqi.dto.QiqiRankReqDTO;
import com.fenbeitong.openapi.plugin.qiqi.service.common.QiqiCommonReqServiceImpl;
import com.fenbeitong.openapi.plugin.qiqi.service.rank.IQiqiRankService;
import com.fenbeitong.openapi.plugin.support.employee.dto.AddAuthRankReqDTO;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.service.FullDataSynchronizer;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.finhub.framework.exception.ArgumentException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;

/**
 * @ClassName QiqiRankServiceImpl
 * @Description 企企职级数据同步
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/28
 **/
@Service
@Slf4j
public class QiqiRankServiceImpl implements IQiqiRankService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    QiqiCommonReqServiceImpl qiqiCommonReqService;
    @Resource(name = "employeeRankTemplateFullDataSynchronizer")
    private FullDataSynchronizer rankSynchronizer;


    @Override
    public void syncQiqiRank(String companyId) throws Exception {
        log.info("【qiqi】 syncQiqiRank, 开始同步职级,companyId={}", companyId);
        String lockKey = MessageFormat.format(RedisKeyConstant.RANK_SYNC_REDIS_KEY, companyId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                syncRank(companyId);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("【qiqi】 syncQiqiRank, 未获取到锁，companyId={}", companyId);
            throw new ArgumentException("未获取到锁");
        }
    }

    public void syncRank(String companyId) throws Exception {
        //1.全量拉取职级数据
        List<QiqiRankReqDTO> rankInfos = qiqiCommonReqService.buildQiqiReq(companyId, ObjectTypeEnum.RANK.getCode(), QiqiRankReqDTO.class, "id is not null", null);
        if (CollectionUtils.isBlank(rankInfos)) {
            log.info("【qiqi】 syncRank, 查询三方职级数据为空");
            return;
        }
        //2.字段转换
        List<AddAuthRankReqDTO> optRankDTOList = Lists.newArrayList();
        for (QiqiRankReqDTO reqDTO : rankInfos) {
            AddAuthRankReqDTO optRankDTO = new AddAuthRankReqDTO();
            optRankDTO.setThirdRankId(reqDTO.getId());
            optRankDTO.setRankName(reqDTO.getName());
            optRankDTOList.add(optRankDTO);
        }
        log.info("【qiqi】 syncRank, 参数optRankDTOList:{}", JsonUtils.toJson(optRankDTOList));
        //3.同步数据
        rankSynchronizer.sync(OpenType.QIQI, companyId, optRankDTOList);

    }
}
