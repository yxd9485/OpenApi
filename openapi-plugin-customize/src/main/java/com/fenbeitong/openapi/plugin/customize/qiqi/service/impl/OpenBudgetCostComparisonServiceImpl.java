package com.fenbeitong.openapi.plugin.customize.qiqi.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.customize.qiqi.common.QiqiResultEntity;
import com.fenbeitong.openapi.plugin.customize.qiqi.common.exception.OpenApiQiqiException;
import com.fenbeitong.openapi.plugin.customize.qiqi.constant.ErrorMessageEnum;
import com.fenbeitong.openapi.plugin.customize.qiqi.constant.QiqiSyncConstant;
import com.fenbeitong.openapi.plugin.customize.qiqi.dao.OpenBudgetCostComparisonDao;
import com.fenbeitong.openapi.plugin.customize.qiqi.dto.*;
import com.fenbeitong.openapi.plugin.customize.qiqi.entity.OpenBudgetCostComparison;
import com.fenbeitong.openapi.plugin.customize.qiqi.service.IOpenBudgetCostComparisonService;
import com.fenbeitong.openapi.plugin.customize.qiqi.service.common.QiqiCommonServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.constant.GlobalConstant;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.util.PageUtil;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.RandomUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName OpenBudgetCostComparisonServiceImpl
 * @Description 同步预算费用对照服务实现类
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/7/7
 **/
@Service
@Slf4j
public class OpenBudgetCostComparisonServiceImpl implements IOpenBudgetCostComparisonService {

    @Autowired
    OpenBudgetCostComparisonDao budgetCostComparisonDao;

    @Autowired
    QiqiCommonServiceImpl qiqiCommonService;

    @Value("${host.openplus}")
    private String openapiHost;

    /**
     * 批量同步，间隔时间：默认：500ms
     */
    @Value("${custom.batch.duration:500}")
    private int batchDuration;

    @Override
    public List<ComparisonErrorRespDTO> syncAll(String companyId, int openType, List<OpenBudgetCostComparison> budgetCostComparisonList) {
        if (ObjectUtils.isEmpty(budgetCostComparisonList)) {
            return null;
        }

        // 根据三方费用ID去重
        List<OpenBudgetCostComparison> distCostComparisonList = budgetCostComparisonList.stream().collect(
            Collectors.collectingAndThen(Collectors.toCollection(
                () -> new TreeSet<>(Comparator.comparing(OpenBudgetCostComparison::getCostId))
            ), ArrayList::new));
        if (distCostComparisonList.size() != budgetCostComparisonList.size()) {
            log.info("企企同步预算与费用关系存在费用id对应多个预算的情况,budgetCostComparisonList:{}", budgetCostComparisonList);
        }

        // 对比出 需要更新 创建 删除 的
        List<OpenBudgetCostComparison> needAddList = Lists.newLinkedList();
        List<OpenBudgetCostComparison> needUpdateList = Lists.newLinkedList();
        List<OpenBudgetCostComparison> needDeleteList = Lists.newLinkedList();

        //查询表中全部数据
        List<OpenBudgetCostComparison> srcComparisonList = budgetCostComparisonDao.listOpenBudgetCostComparison(companyId, openType);
        List<String> costIdList = distCostComparisonList.stream().map(OpenBudgetCostComparison::getCostId).collect(Collectors.toList());
        List<String> srcCostIdList = srcComparisonList.stream().map(OpenBudgetCostComparison::getCostId).collect(Collectors.toList());
        Map<String, OpenBudgetCostComparison> srcComparisonMap = srcComparisonList.stream().collect(Collectors.toMap(OpenBudgetCostComparison::getCostId, v -> v));

        //如果表里数据为空，则全部新增
        if (ObjectUtils.isEmpty(srcComparisonList)) {
            needAddList.addAll(distCostComparisonList);
            log.info("需要新增的数据,needAddList:{}", needAddList);
        } else {
            //需要新增的数据
            List<OpenBudgetCostComparison> addList = distCostComparisonList.stream().filter(e -> !srcCostIdList.contains(e.getCostId())).collect(Collectors.toList());
            if (!ObjectUtils.isEmpty(addList)) {
                needAddList.addAll(addList);
                log.info("需要新增的数据,needAddList:{}", needAddList);
            }
            //需要删除的数据
            List<OpenBudgetCostComparison> deleteList = srcComparisonList.stream().filter(e -> !costIdList.contains(e.getCostId())).collect(Collectors.toList());
            if (!ObjectUtils.isEmpty(deleteList)) {
                needDeleteList.addAll(deleteList);
                log.info("需要删除的数据,needDeleteList:{}", needDeleteList);
            }
            //需要修改的数据
            List<OpenBudgetCostComparison> mayBeUpdateList = distCostComparisonList.stream().filter(e -> srcCostIdList.contains(e.getCostId())).collect(Collectors.toList());
            if (!ObjectUtils.isEmpty(mayBeUpdateList)) {
                for (OpenBudgetCostComparison openBudgetCostComparison : mayBeUpdateList) {
                    if (isNeedUpdate(srcComparisonMap, openBudgetCostComparison)) {
                        needUpdateList.add(openBudgetCostComparison);
                    }
                }
                log.info("需要修改的数据,needUpdateList:{}", needUpdateList);
            }
        }

        //批量新增、修改、删除（分批）
        List<ComparisonErrorRespDTO> addErrorList = addList(needAddList);
        List<ComparisonErrorRespDTO> updateErrorList = updateList(needUpdateList);
        List<ComparisonErrorRespDTO> deleteErrorList = deleteList(needDeleteList);

        //返回同步失败的数据
        List<ComparisonErrorRespDTO> errorRespDTOList = Lists.newArrayList();
        if (!ObjectUtils.isEmpty(addErrorList)) {
            errorRespDTOList.addAll(addErrorList);
        }
        if (!ObjectUtils.isEmpty(updateErrorList)) {
            errorRespDTOList.addAll(updateErrorList);
        }
        if (!ObjectUtils.isEmpty(deleteErrorList)) {
            errorRespDTOList.addAll(deleteErrorList);
        }
        return errorRespDTOList;
    }

    @Override
    @Async
    public void syncAllArchiveAndCost(String companyId) throws Exception {
        log.info("【qiqi】 syncAllArchiveAndCost, 自定义档案与费用关系全量同步开始执行,companyId:{}", companyId);
        Long startTime = System.currentTimeMillis();
        //封装树形参数
        QiqiCommonReqDetailDTO refDoc = qiqiCommonService.getTreeParam(QiqiSyncConstant.REF_DOC, QiqiRefDocMappingDTO.RefDoc.class, null);
        QiqiCommonReqDetailDTO refDocMappingsObject = qiqiCommonService.getTreeParam(QiqiSyncConstant.REF_DOC_MAPPING, QiqiRefDocMappingDTO.class, Lists.newArrayList(refDoc));
        //参数封装
        QiqiReqDTO qiqiReqDto = QiqiReqDTO.builder().companyId(companyId).objectType(QiqiSyncConstant.BUDGET_ACOUNT)
            .queryConditions("id is not null and accountType.id = 'BudgetAccountType.expense' order by level").commonReqDetailList(Lists.newArrayList(refDocMappingsObject)).build();

        // 调用企企服务，全量拉取自定义档案数据
        QiqiResultEntity qiqiResultEntity = qiqiPostList(qiqiReqDto);
        if (qiqiResultEntity.getCode() != 0 || ObjectUtils.isEmpty(qiqiResultEntity.getData())) {
            log.info("调用企企查询失败");
            throw new OpenApiQiqiException(-9999);
        }
        List<QiqiCustomArchiveReqDTO> customArchiveInfos = JSONObject.parseArray(JSONObject.toJSONString(qiqiResultEntity.getData())).toJavaList(QiqiCustomArchiveReqDTO.class);
        //参数转换
        List<OpenBudgetCostComparison> comparisonList = Lists.newArrayList();
        for (QiqiCustomArchiveReqDTO info : customArchiveInfos) {
            List<QiqiRefDocMappingDTO> mappingList = info.getRefDocMappingsObject();
            if (CollectionUtils.isNotBlank(mappingList)) {
                for (QiqiRefDocMappingDTO mapping : mappingList) {
                    OpenBudgetCostComparison comparison = OpenBudgetCostComparison.builder().companyId(companyId).openType(OpenType.QIQI.getType())
                        .state(1).budgetId(info.getId()).budgetCode(info.getCode()).budgetName(info.getName())
                        .costId(mapping.getRefDoc().getId()).costCode(mapping.getRefDoc().getCode())
                        .costName(mapping.getRefDoc().getName()).costTypeName(QiqiSyncConstant.COST_TYPE_NAME).build();
                    comparisonList.add(comparison);
                }
            }
        }

        //同步数据
        List<ComparisonErrorRespDTO> errorRespDTOList = syncAll(companyId, OpenType.QIQI.getType(), comparisonList);
        if (!ObjectUtils.isEmpty(errorRespDTOList)) {
            log.info("自定义档案与费用关系同步失败的数据:{}", errorRespDTOList);
        }

        Long endTime = System.currentTimeMillis();
        log.info("【qiqi】 syncAllArchiveAndCost, 自定义档案与费用关系全量同步完成,用时{}分钟{}秒...", (endTime - startTime) / 60000, ((endTime - startTime) % 60000) / 1000);
    }

    @Override
    public QiqiResultEntity qiqiPostList(QiqiReqDTO qiqiReqDto) {
        HttpHeaders httpHeaders = new HttpHeaders();
        String response = RestHttpUtils.postJson((openapiHost.concat("/openapi/qiqi/list/postList")), httpHeaders, JsonUtils.toJson(qiqiReqDto));
        return JsonUtils.toObj(response, QiqiResultEntity.class);
    }

    /**
     * 判断是否需要更新 null不操作
     *
     * @param srcComparisonMap         表中需要更新的数据
     * @param openBudgetCostComparison 待更新的预算费用关系数据
     * @return needUpdate 是否更新
     */
    private boolean isNeedUpdate(Map<String, OpenBudgetCostComparison> srcComparisonMap, OpenBudgetCostComparison openBudgetCostComparison) {
        boolean needUpdate = false;
        OpenBudgetCostComparison srcComparison = srcComparisonMap.get(openBudgetCostComparison.getCostId());
        if (!ObjectUtils.isEmpty(srcComparison)) {
            if (!ObjectUtils.isEmpty(openBudgetCostComparison.getBudgetId()) && !openBudgetCostComparison.getBudgetId().equals(srcComparison.getBudgetId())) {
                needUpdate = true;
            }
            if (!ObjectUtils.isEmpty(openBudgetCostComparison.getBudgetCode()) && !openBudgetCostComparison.getBudgetCode().equals(srcComparison.getBudgetCode())) {
                needUpdate = true;
            }
            if (!ObjectUtils.isEmpty(openBudgetCostComparison.getBudgetName()) && !openBudgetCostComparison.getBudgetName().equals(srcComparison.getBudgetName())) {
                needUpdate = true;
            }
            if (!ObjectUtils.isEmpty(openBudgetCostComparison.getCostCode()) && !openBudgetCostComparison.getCostCode().equals(srcComparison.getCostCode())) {
                needUpdate = true;
            }
            if (!ObjectUtils.isEmpty(openBudgetCostComparison.getCostName()) && !openBudgetCostComparison.getCostName().equals(srcComparison.getCostName())) {
                needUpdate = true;
            }
        }
        return needUpdate;
    }

    /**
     * 分批新增
     *
     * @param needAddList 需要新增的集合
     * @return List<ComparisonErrorRespDTO> 新增失败的费用id集合
     */
    private List<ComparisonErrorRespDTO> addList(List<OpenBudgetCostComparison> needAddList) {
        if (needAddList.size() <= GlobalConstant.BATCH_SIZE) {
            return addListIntoDb(needAddList);
        }
        return PageUtil.partialCall(needAddList, input -> addListIntoDb(input), batchDuration, GlobalConstant.BATCH_SIZE);
    }

    /**
     * 新增
     *
     * @param needAddList 需要新增的集合
     * @return errorCostIdList 新增失败的费用id集合
     */
    private List<ComparisonErrorRespDTO> addListIntoDb(List<OpenBudgetCostComparison> needAddList) {
        List<ComparisonErrorRespDTO> errorCostIdList = Lists.newArrayList();
        if (!ObjectUtils.isEmpty(needAddList)) {
            for (OpenBudgetCostComparison comparison : needAddList) {
                comparison.setId(RandomUtils.mostSigBitsUuid());
                comparison.setCreateTime(new Date());
                comparison.setUpdateTime(new Date());
                try {
                    budgetCostComparisonDao.save(comparison);
                } catch (Exception e) {
                    errorCostIdList.add(ComparisonErrorRespDTO.builder().costId(comparison.getCostId()).errorMsg(ErrorMessageEnum.CREATE_ERR.getCode()).build());
                    log.warn("costId为[{}]的预算费用关系数据新增失败:{},e:{}", comparison.getCostId(), JsonUtils.toJson(comparison), e);
                }
            }
        }
        return errorCostIdList;
    }

    /**
     * 分批修改
     *
     * @param needUpdateList 需要修改的集合
     * @return List<ComparisonErrorRespDTO> 修改失败的费用id集合
     */
    private List<ComparisonErrorRespDTO> updateList(List<OpenBudgetCostComparison> needUpdateList) {
        if (needUpdateList.size() <= GlobalConstant.BATCH_SIZE) {
            return updateListIntoDb(needUpdateList);
        }
        return PageUtil.partialCall(needUpdateList, input -> updateListIntoDb(input), batchDuration, GlobalConstant.BATCH_SIZE);
    }

    /**
     * 根据公司id、费用id、来源类型修改预算与费用关系
     *
     * @param needUpdateList 需要修改的集合
     * @return errorCostIdList 修改失败的费用id集合
     */
    private List<ComparisonErrorRespDTO> updateListIntoDb(List<OpenBudgetCostComparison> needUpdateList) {
        List<ComparisonErrorRespDTO> errorCostIdList = Lists.newArrayList();
        if (!ObjectUtils.isEmpty(needUpdateList)) {
            for (OpenBudgetCostComparison comparison : needUpdateList) {
                try {
                    budgetCostComparisonDao.updateByCompanyIdAndCostIdAndOpenType(comparison, comparison.getCompanyId(), comparison.getCostId(), comparison.getOpenType());
                } catch (Exception e) {
                    errorCostIdList.add(ComparisonErrorRespDTO.builder().costId(comparison.getCostId()).errorMsg(ErrorMessageEnum.UPDATE_ERR.getCode()).build());
                    log.warn("costId为[{}]的预算费用关系数据修改失败:{},e:{}", comparison.getCostId(), JsonUtils.toJson(comparison), e);
                }
            }
        }
        return errorCostIdList;
    }

    /**
     * 分批删除
     *
     * @param needDeleteList 需要删除的集合
     * @return List<ComparisonErrorRespDTO> 删除失败的id集合
     */
    private List<ComparisonErrorRespDTO> deleteList(List<OpenBudgetCostComparison> needDeleteList) {
        if (needDeleteList.size() <= GlobalConstant.BATCH_SIZE) {
            return deleteListIntoDb(needDeleteList);
        }
        return PageUtil.partialCall(needDeleteList, input -> deleteListIntoDb(input), batchDuration, GlobalConstant.BATCH_SIZE);
    }

    /**
     * 根据公司id、费用id、来源类型删除预算与费用关系
     *
     * @param needDeleteList 需要删除的集合
     * @return errorCostIdList 删除失败的费用id集合
     */
    private List<ComparisonErrorRespDTO> deleteListIntoDb(List<OpenBudgetCostComparison> needDeleteList) {
        List<ComparisonErrorRespDTO> errorCostIdList = Lists.newArrayList();
        if (!ObjectUtils.isEmpty(needDeleteList)) {
            for (OpenBudgetCostComparison comparison : needDeleteList) {
                try {
                    budgetCostComparisonDao.deleteByCompanyIdAndCostIdAndOpenType(comparison.getCompanyId(), comparison.getCostId(), comparison.getOpenType());
                } catch (Exception e) {
                    errorCostIdList.add(ComparisonErrorRespDTO.builder().costId(comparison.getCostId()).errorMsg(ErrorMessageEnum.DELETE_ERR.getCode()).build());
                    log.warn("costId为[{}]的预算费用关系数据删除失败:{},e:{}", comparison.getCostId(), JsonUtils.toJson(comparison), e);
                }
            }
        }
        return errorCostIdList;
    }
}
