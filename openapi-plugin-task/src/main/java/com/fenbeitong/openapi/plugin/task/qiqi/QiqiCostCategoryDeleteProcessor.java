package com.fenbeitong.openapi.plugin.task.qiqi;

import com.fenbeitong.common.utils.collect.CollectionUtils;
import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.constant.TaskConfig;
import com.fenbeitong.finhub.task.itf.ITaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResponseCode;
import com.fenbeitong.openapi.plugin.qiqi.common.exception.OpenApiQiqiException;
import com.fenbeitong.openapi.plugin.qiqi.constant.ObjectTypeEnum;
import com.fenbeitong.openapi.plugin.qiqi.dto.QiqiCostReqDTO;
import com.fenbeitong.openapi.plugin.qiqi.entity.QiqiCorpInfo;
import com.fenbeitong.openapi.plugin.qiqi.orgemployee.dao.QiqiCorpInfoDao;
import com.fenbeitong.openapi.plugin.qiqi.service.common.QiqiCommonReqServiceImpl;
import com.fenbeitong.openapi.plugin.qiqi.service.cost.IQiqiCostCategoryService;
import com.fenbeitong.openapi.plugin.support.budget.dao.OpenThirdCostCategoryDao;
import com.fenbeitong.openapi.plugin.support.budget.dto.AddCostCategoryReqDTO;
import com.fenbeitong.openapi.plugin.support.budget.dto.DeleteCostCategoryReqDTO;
import com.fenbeitong.openapi.plugin.support.budget.dto.DeleteCostCategoryTypeReqDTO;
import com.fenbeitong.openapi.plugin.support.budget.entity.OpenThirdCostCategory;
import com.fenbeitong.openapi.plugin.support.budget.service.CacheCostCategoryService;
import com.fenbeitong.openapi.plugin.support.budget.service.CostCategoryTypeService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.service.FullDataSynchronizer;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.task.utils.FinhubTaskUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName QiqiCostCategoryDeleteProcessor
 * @Description
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/27
 **/
@Component
@Slf4j
public class QiqiCostCategoryDeleteProcessor implements ITaskProcessor {
    @Autowired
    private TaskConfig taskConfig;

    @Autowired
    private QiqiCorpInfoDao corpInfoDao;

    @Autowired
    private CacheCostCategoryService cacheCostCategoryService;

    @Autowired
    private QiqiCommonReqServiceImpl qiqiCommonReqService;

    @Autowired
    private CostCategoryTypeService categoryTypeService;

    @Autowired
    private IQiqiCostCategoryService qiqiCostCategoryService;

    @Autowired
    private OpenThirdCostCategoryDao openThirdCostCategoryDao;

    @Resource(name = "costCategoryFullDataSynchronizer")
    private FullDataSynchronizer costCategorySynchronizer;

    @Override
    public String getTaskSrc() {
        return this.taskConfig.getTaskNamespace();
    }

    @Override
    public Integer getTaskType() {
        return TaskType.QIQI_SYNC_COST_DELETE.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) throws Exception {
        //1.解析task
        String corpId = task.getCompanyId();
        String dataId = task.getDataId();

        //2.查询企业注册信息
        QiqiCorpInfo corpInfo = corpInfoDao.getBycorpId(corpId);
        String companyId = corpInfo.getCompanyId();

        //3.查询被删数据在中间表中是否有父级节点
        List<OpenThirdCostCategory> openThirdCostCategories = openThirdCostCategoryDao.listOpenThirdCostCategory(OpenType.QIQI.getType(), companyId, Lists.newArrayList(dataId));
        if (CollectionUtils.isBlank(openThirdCostCategories)) {
            List<DeleteCostCategoryTypeReqDTO> typeReqDTOList = Lists.newArrayList();
            DeleteCostCategoryTypeReqDTO typeReqDTO = DeleteCostCategoryTypeReqDTO.builder().thirdId(dataId).build();
            typeReqDTOList.add(typeReqDTO);
            categoryTypeService.deleteCostCategoryTypeBatch(companyId, typeReqDTOList);
        } else {
            // 拉取费用类别所有结点
            List<QiqiCostReqDTO> costInfos = qiqiCommonReqService.buildQiqiReq(companyId, ObjectTypeEnum.COST.getCode(), QiqiCostReqDTO.class, "id is not null order by level", null);
            if (com.fenbeitong.openapi.plugin.util.CollectionUtils.isBlank(costInfos)) {
                log.info("【qiqi】 QiqiCostCategoryUpdateProcessor, 查询三方数据为空");
                throw new OpenApiQiqiException(QiqiResponseCode.DATA_NOT_EXIST);
            }
            //叶节点集合
            List<QiqiCostReqDTO> leafList = costInfos.stream().filter(e -> e.getIsLeaf() && e.getLevel() != 0).collect(Collectors.toList());

            List<AddCostCategoryReqDTO> costLeafDTOList = qiqiCostCategoryService.costAllLeafConvert(leafList, costInfos);
            costCategorySynchronizer.sync(OpenType.QIQI, companyId, costLeafDTOList);
            // 删除此节点
            List<DeleteCostCategoryReqDTO> costCategoryDTOList = Lists.newArrayList();
            DeleteCostCategoryReqDTO categoryDTO = DeleteCostCategoryReqDTO.builder().thirdId(dataId).build();
            costCategoryDTOList.add(categoryDTO);
            cacheCostCategoryService.deleteCostCategoryBatch(OpenType.QIQI.getType(), companyId, costCategoryDTOList);
        }
        log.info("【qiqi】 QiqiCostCategoryDeleteProcessor, 费用删除成功,dataId:{}",dataId);
        return TaskProcessResult.success("success");
    }

    @Override
    public Long getSleepSeconds(FinhubTask task) {
        return FinhubTaskUtils.getSleepSeconds(task);
    }
}
