package com.fenbeitong.openapi.plugin.task.qiqi;

import com.fenbeitong.common.utils.collect.CollectionUtils;
import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.constant.TaskConfig;
import com.fenbeitong.finhub.task.itf.ITaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResponseCode;
import com.fenbeitong.openapi.plugin.qiqi.common.exception.OpenApiQiqiException;
import com.fenbeitong.openapi.plugin.qiqi.constant.ObjectTypeEnum;
import com.fenbeitong.openapi.plugin.qiqi.dto.QiqiProjectGroupReqDTO;
import com.fenbeitong.openapi.plugin.qiqi.entity.QiqiCorpInfo;
import com.fenbeitong.openapi.plugin.qiqi.orgemployee.dao.QiqiCorpInfoDao;
import com.fenbeitong.openapi.plugin.qiqi.service.common.QiqiCommonReqServiceImpl;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.project.dto.AddProjectGroupReqDTO;
import com.fenbeitong.openapi.plugin.support.project.service.CacheProjectGroupService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.task.utils.FinhubTaskUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName QiqiProjectCategoryAddProcessor
 * @Description
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/25
 **/
@Component
@Slf4j
public class QiqiProjectCategoryAddProcessor implements ITaskProcessor {

    @Autowired
    private TaskConfig taskConfig;

    @Autowired
    private QiqiCommonReqServiceImpl qiqiCommonReqService;

    @Autowired
    private QiqiCorpInfoDao corpInfoDao;

    @Autowired
    private CacheProjectGroupService cacheProjectGroupService;


    @Override
    public String getTaskSrc() {
        return this.taskConfig.getTaskNamespace();
    }

    @Override
    public Integer getTaskType() {
        return TaskType.QIQI_SYNC_PROJECT_CATEGORY_ADD.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) throws Exception {
        //1.解析task
        String corpId = task.getCompanyId();
        String dataId = task.getDataId();

        //2.查询企业注册信息
        QiqiCorpInfo corpInfo = corpInfoDao.getBycorpId(corpId);

        //3.拉取项目分组增量数据
        List<QiqiProjectGroupReqDTO> projectGroupDTOS = qiqiCommonReqService.buildQiqiReq(corpInfo.getCompanyId(), ObjectTypeEnum.PROJECT_CATEGORY.getCode(), QiqiProjectGroupReqDTO.class, "id='" + dataId + "' and level = 0 order by level", null);
        if (CollectionUtils.isBlank(projectGroupDTOS)) {
            log.info("【qiqi】 QiqiProjectCategoryAddProcessor, 查询三方数据为空");
            throw new OpenApiQiqiException(QiqiResponseCode.DATA_NOT_EXIST);
        }

        //4.转换数据
        List<AddProjectGroupReqDTO> groupDTOList = Lists.newArrayList();
        for (QiqiProjectGroupReqDTO groupDTO : projectGroupDTOS) {
            AddProjectGroupReqDTO projectGroupDTO = AddProjectGroupReqDTO.builder().thirdGroupId(groupDTO.getId()).groupName(groupDTO.getName()).groupDesc(groupDTO.getDescrtption()).build();
            groupDTOList.add(projectGroupDTO);
        }

        //5.同步
        cacheProjectGroupService.addProjectGroupBatch(OpenType.QIQI.getType(), corpInfo.getCompanyId(), groupDTOList);
        log.info("【qiqi】 QiqiProjectCategoryAddProcessor, 项目分组新增成功,dataId:{}", dataId);
        return TaskProcessResult.success("success");
    }

    @Override
    public Long getSleepSeconds(FinhubTask task) {
        return FinhubTaskUtils.getSleepSeconds(task);
    }
}
