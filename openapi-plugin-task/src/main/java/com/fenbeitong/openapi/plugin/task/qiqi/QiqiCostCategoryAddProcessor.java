package com.fenbeitong.openapi.plugin.task.qiqi;

import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.common.utils.collect.CollectionUtils;
import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.constant.TaskConfig;
import com.fenbeitong.finhub.task.itf.ITaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResponseCode;
import com.fenbeitong.openapi.plugin.qiqi.common.exception.OpenApiQiqiException;
import com.fenbeitong.openapi.plugin.qiqi.constant.ObjectTypeEnum;
import com.fenbeitong.openapi.plugin.qiqi.constant.StateEnum;
import com.fenbeitong.openapi.plugin.qiqi.dto.QiqiCostReqDTO;
import com.fenbeitong.openapi.plugin.qiqi.entity.QiqiCorpInfo;
import com.fenbeitong.openapi.plugin.qiqi.orgemployee.dao.QiqiCorpInfoDao;
import com.fenbeitong.openapi.plugin.qiqi.service.common.QiqiCommonReqServiceImpl;
import com.fenbeitong.openapi.plugin.support.budget.dto.AddCostCategoryReqDTO;
import com.fenbeitong.openapi.plugin.support.budget.dto.AddCostCategoryTypeReqDTO;
import com.fenbeitong.openapi.plugin.support.budget.dto.CostCategoryErrorRespDTO;
import com.fenbeitong.openapi.plugin.support.budget.service.CacheCostCategoryService;
import com.fenbeitong.openapi.plugin.support.budget.service.CostCategoryTypeService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.task.utils.FinhubTaskUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName QiqiCostCategoryAddProcessor
 * @Description
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/27
 **/
@Component
@Slf4j
public class QiqiCostCategoryAddProcessor implements ITaskProcessor {
    @Autowired
    private TaskConfig taskConfig;

    @Autowired
    private QiqiCommonReqServiceImpl qiqiCommonReqService;

    @Autowired
    private QiqiCorpInfoDao corpInfoDao;

    @Autowired
    private CacheCostCategoryService cacheCostCategoryService;

    @Autowired
    CostCategoryTypeService categoryTypeService;

    @Override
    public String getTaskSrc() {
        return this.taskConfig.getTaskNamespace();
    }

    @Override
    public Integer getTaskType() {
        return TaskType.QIQI_SYNC_COST_ADD.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) throws Exception {
        //1.解析task
        String corpId = task.getCompanyId();
        String dataId = task.getDataId();

        //2.查询企业注册信息
        QiqiCorpInfo corpInfo = corpInfoDao.getBycorpId(corpId);
        String companyId = corpInfo.getCompanyId();

        //3.拉取费用类别全量数据
        List<QiqiCostReqDTO> costInfos = qiqiCommonReqService.buildQiqiReq(companyId, ObjectTypeEnum.COST.getCode(), QiqiCostReqDTO.class, "id is not null order by level", null);
        if (com.fenbeitong.openapi.plugin.util.CollectionUtils.isBlank(costInfos)) {
            log.info("【qiqi】 QiqiCostCategoryUpdateProcessor, 查询三方数据为空");
            throw new OpenApiQiqiException(QiqiResponseCode.DATA_NOT_EXIST);
        }
        Map<String, QiqiCostReqDTO> costMap = costInfos.stream().collect(Collectors.toMap(QiqiCostReqDTO::getId, Function.identity(), (o, n) -> n));
        //获取新增的费用分类或者类别
        QiqiCostReqDTO costReqDTO = costMap.get(dataId);
        //如果是根节点，调用费用分类的新增
        if (costReqDTO.getLevel() == 0) {
            AddCostCategoryTypeReqDTO costCategoryTypeReqDTO = AddCostCategoryTypeReqDTO.builder().thirdId(costReqDTO.getId()).name(costReqDTO.getName()).build();
            categoryTypeService.addCostCategoryTypeBatch(companyId, Lists.newArrayList(costCategoryTypeReqDTO));
        } else if (costReqDTO.getLevel() != 0 && costReqDTO.getIsLeaf() == false) {
            return TaskProcessResult.success("费用类别为非叶子节点，不需要同步");
        } else {
            //4.数据转换
            List<AddCostCategoryReqDTO> costCategoryDTOList = Lists.newArrayList();
            List<AddCostCategoryReqDTO> costUpdateDtos = Lists.newArrayList();
            List<AddCostCategoryReqDTO> parentDtoList = Lists.newArrayList();
            //因企企与分贝通费用层级无法对应，特殊处理，新增子级时判断上级是否为根节点，不是则置为停用
            Integer level = costReqDTO.getLevel();
            String parentId = costReqDTO.getParentId();
            while (level > 1) {
                //查询费用类别的父节点
                QiqiCostReqDTO costParentDto = costMap.get(parentId);
                if (ObjectUtils.isEmpty(costParentDto)) {
                    throw new OpenApiQiqiException(QiqiResponseCode.DATA_NOT_EXIST);
                }
                level = costParentDto.getLevel();
                parentId = costParentDto.getParentId();
                //状态改为停用
                AddCostCategoryReqDTO costUpdateDto = AddCostCategoryReqDTO.builder().thirdId(costParentDto.getId()).thirdCostGroupId(parentId).customCode(costParentDto.getCode()).name(costParentDto.getName()).remark(costParentDto.getDescription()).state(StateEnum.STATE_DISABBLE.getCode()).build();
                costUpdateDtos.add(costUpdateDto);
            }
            for (AddCostCategoryReqDTO dto : costUpdateDtos) {
                AddCostCategoryReqDTO parentDto = new AddCostCategoryReqDTO();
                BeanUtils.copyProperties(dto, parentDto);
                parentDto.setThirdCostGroupId(parentId);
                parentDtoList.add(parentDto);
            }
            AddCostCategoryReqDTO costAddDto = AddCostCategoryReqDTO.builder().thirdId(dataId).thirdCostGroupId(parentId).customCode(costReqDTO.getCode()).name(costReqDTO.getName()).remark(costReqDTO.getDescription()).state(costReqDTO.getIsDisabled() == false ? StateEnum.STATE_ENABLE.getCode() : StateEnum.STATE_DISABBLE.getCode()).build();
            costCategoryDTOList.add(costAddDto);
            //5 同步增量数据
            List<CostCategoryErrorRespDTO> costCategoryErrorRespDTOS = cacheCostCategoryService.addCostCategoryBatch(OpenType.QIQI.getType(), corpInfo.getCompanyId(), costCategoryDTOList);
            if (!CollectionUtils.isBlank(costCategoryErrorRespDTOS)) {
                return TaskProcessResult.fail(JSONObject.toJSONString(costCategoryErrorRespDTOS));
            }
            // 将中间各级数据改为停用
            if (!CollectionUtils.isBlank(costUpdateDtos)) {
                cacheCostCategoryService.updateCostCategoryBatch(OpenType.QIQI.getType(), corpInfo.getCompanyId(), parentDtoList);
            }
        }
        log.info("【qiqi】 QiqiCostCategoryAddProcessor, 费用新增成功,dataId:{}", dataId);
        return TaskProcessResult.success("success");
    }

    @Override
    public Long getSleepSeconds(FinhubTask task) {
        return FinhubTaskUtils.getSleepSeconds(task);
    }
}
