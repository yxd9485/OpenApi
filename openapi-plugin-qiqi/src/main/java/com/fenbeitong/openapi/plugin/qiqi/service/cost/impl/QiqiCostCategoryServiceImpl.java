package com.fenbeitong.openapi.plugin.qiqi.service.cost.impl;

import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResponseCode;
import com.fenbeitong.openapi.plugin.qiqi.common.exception.OpenApiQiqiException;
import com.fenbeitong.openapi.plugin.qiqi.constant.ObjectTypeEnum;
import com.fenbeitong.openapi.plugin.qiqi.constant.StateEnum;
import com.fenbeitong.openapi.plugin.qiqi.dto.QiqiCostReqDTO;
import com.fenbeitong.openapi.plugin.qiqi.service.common.QiqiCommonReqServiceImpl;
import com.fenbeitong.openapi.plugin.qiqi.service.cost.IQiqiCostCategoryService;
import com.fenbeitong.openapi.plugin.support.budget.dto.AddCostCategoryReqDTO;
import com.fenbeitong.openapi.plugin.support.budget.dto.AddCostCategoryTypeReqDTO;
import com.fenbeitong.openapi.plugin.support.budget.service.CacheCostCategoryService;
import com.fenbeitong.openapi.plugin.support.budget.service.CostCategoryTypeService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.service.FullDataSynchronizer;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.exception.ArgumentException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName QiqiCostServiceImpl
 * @Description 企企同步费用类别数据
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/28
 **/
@Service
@Slf4j
public class QiqiCostCategoryServiceImpl implements IQiqiCostCategoryService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private QiqiCommonReqServiceImpl qiqiCommonReqService;
    @Resource(name = "costCategoryFullDataSynchronizer")
    private FullDataSynchronizer costCategorySynchronizer;
    @Autowired
    private CostCategoryTypeService costCategoryTypeService;

    @Override
    @Async
    public void syncQiqiCostCategory(String companyId) throws Exception {
        log.info("【qiqi】 syncQiqiCostCategory, 开始同步费用类别,companyId={}", companyId);
        String lockKey = MessageFormat.format(RedisKeyConstant.COST_CATEGORY_SYNC_REDIS_KEY, companyId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                syncCostCategory(companyId);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("【qiqi】 syncQiqiCostCategory, 未获取到锁，companyId={}", companyId);
            throw new ArgumentException("未获取到锁");
        }
    }

    /**
     * 全量拉取费用类别数据并存到中间表
     *
     * @param companyId
     * @throws Exception
     */
    public void syncCostCategory(String companyId) throws Exception {
        //1.全量拉取费用类别数据
        List<QiqiCostReqDTO> costInfos = qiqiCommonReqService.buildQiqiReq(companyId, ObjectTypeEnum.COST.getCode(), QiqiCostReqDTO.class, "id is not null order by level", null);
        if (CollectionUtils.isBlank(costInfos)) {
            log.info("【qiqi】 syncCostCategory, 查询三方费用类别数据为空");
            return;
        }
        //根节点集合
        List<QiqiCostReqDTO> rootList = costInfos.stream().filter(e -> e.getLevel() == 0).collect(Collectors.toList());
        //叶节点集合
        List<QiqiCostReqDTO> leafList = costInfos.stream().filter(e -> e.getIsLeaf() == true && e.getLevel() != 0).collect(Collectors.toList());

        //2.1 根节点字段转换
        List<AddCostCategoryTypeReqDTO> costRootList = Lists.newArrayList();
        for (QiqiCostReqDTO reqDTO : rootList) {
            AddCostCategoryTypeReqDTO categoryTypeDTO = AddCostCategoryTypeReqDTO.builder().thirdId(reqDTO.getId()).name(reqDTO.getName()).build();
            costRootList.add(categoryTypeDTO);
        }
        log.info("【qiqi】 syncCostCategory, 参数costRootList:{}", JsonUtils.toJson(costRootList));

        //2.2 叶子节点字段转换
        List<AddCostCategoryReqDTO> costLeafList = costAllLeafConvert(leafList, costInfos);
        //3.1 同步费用分类数据
        costCategoryTypeService.addCostCategoryTypeBatch(companyId, costRootList);
        //3.2 同步费用类别数据
        costCategorySynchronizer.sync(OpenType.QIQI,companyId,costLeafList);
    }

    /**
     * 费用类别叶子节点字段转换(全量同步)
     * @return
     */
    @Override
    public List<AddCostCategoryReqDTO> costAllLeafConvert(List<QiqiCostReqDTO> leafList, List<QiqiCostReqDTO> costInfos) {
        List<AddCostCategoryReqDTO> costLeafList = Lists.newArrayList();
        Map<String,QiqiCostReqDTO> costMap = costInfos.stream().collect(Collectors.toMap(QiqiCostReqDTO::getId, Function.identity(), (o, n) -> n));

        for (QiqiCostReqDTO reqDTO : leafList) {
            AddCostCategoryReqDTO categoryDTO = new AddCostCategoryReqDTO();
            categoryDTO.setThirdId(reqDTO.getId());
            categoryDTO.setCustomCode(reqDTO.getCode());
            categoryDTO.setName(reqDTO.getName());
            categoryDTO.setRemark(reqDTO.getDescription());
            //除了根与叶子节点的修改启用状态均为停用
            if (reqDTO.getLevel() == 0 || reqDTO.getIsLeaf() == true) {
                categoryDTO.setState(reqDTO.getIsDisabled() == false ? StateEnum.STATE_ENABLE.getCode() : StateEnum.STATE_DISABBLE.getCode());
            } else {
                categoryDTO.setState(StateEnum.STATE_DISABBLE.getCode());
            }
            //将所有叶子结点的根节点设为其父节点
            Integer level = reqDTO.getLevel();
            String parentId = reqDTO.getParentId();
            while (level > 1) {
                QiqiCostReqDTO costReqDTO = costMap.get(parentId);
                if (ObjectUtils.isEmpty(costReqDTO)) {
                    throw new OpenApiQiqiException(QiqiResponseCode.DATA_NOT_EXIST);
                }

                level = costReqDTO.getLevel();
                parentId = costReqDTO.getParentId();
            }
            categoryDTO.setThirdCostGroupId(parentId);
            costLeafList.add(categoryDTO);
        }
        log.info("【qiqi】costLeafConvert, 参数 costLeafList:{}", JsonUtils.toJson(costLeafList));
        return costLeafList;
    }
}
