package com.fenbeitong.openapi.plugin.task.qiqi;

import com.fenbeitong.common.utils.collect.CollectionUtils;
import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.constant.TaskConfig;
import com.fenbeitong.finhub.task.itf.ITaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.qiqi.constant.ObjectTypeEnum;
import com.fenbeitong.openapi.plugin.qiqi.constant.QiqiSyncConstant;
import com.fenbeitong.openapi.plugin.qiqi.constant.StateEnum;
import com.fenbeitong.openapi.plugin.qiqi.dto.QiqiCustomArchiveReqDTO;
import com.fenbeitong.openapi.plugin.qiqi.entity.QiqiCorpInfo;
import com.fenbeitong.openapi.plugin.qiqi.orgemployee.dao.QiqiCorpInfoDao;
import com.fenbeitong.openapi.plugin.qiqi.service.common.QiqiCommonReqServiceImpl;
import com.fenbeitong.openapi.plugin.support.archive.dto.ArchiveReqItemDTO;
import com.fenbeitong.openapi.plugin.support.archive.service.OpenThirdCustomArchiveProjectService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.task.utils.FinhubTaskUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName QiqiCustomArchiveUpdateProcessor
 * @Description
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/26
 **/
@Component
@Slf4j
public class QiqiCustomArchiveUpdateProcessor implements ITaskProcessor {
    @Autowired
    private TaskConfig taskConfig;

    @Autowired
    private QiqiCommonReqServiceImpl qiqiCommonReqService;

    @Autowired
    private QiqiCorpInfoDao corpInfoDao;

    @Autowired
    private OpenThirdCustomArchiveProjectService customArchiveProjectService;

    @Override
    public String getTaskSrc() {
        return this.taskConfig.getTaskNamespace();
    }

    @Override
    public Integer getTaskType() {
        return TaskType.QIQI_SYNC_BUDGET_ACCOUNT_UPDATE.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) throws Exception {
        //1.解析task
        String corpId = task.getCompanyId();
        String dataId = task.getDataId();

        //2.查询企业注册信息
        QiqiCorpInfo corpInfo = corpInfoDao.getBycorpId(corpId);
        String companyId = corpInfo.getCompanyId();

        //3.拉取自定义档案增量数据
        List<QiqiCustomArchiveReqDTO> customArchiveReqDTOS = qiqiCommonReqService.buildQiqiReq(companyId, ObjectTypeEnum.BUDGET_ACCOUNT.getCode(), QiqiCustomArchiveReqDTO.class, "id='" + dataId + "'and accountType.id = 'BudgetAccountType.expense' order by level", null);
        if (CollectionUtils.isBlank(customArchiveReqDTOS)) {
            log.info("【qiqi】 QiqiCustomArchiveUpdateProcessor, 查询三方数据为空");
            //自定义档案只查费用的，查不到直接返回
            return TaskProcessResult.success("customArchiveReqDTOS is null success");
        }

        //4.数据转换
        QiqiCustomArchiveReqDTO qiqiReqDTO = customArchiveReqDTOS.get(0);
        ArchiveReqItemDTO archiveReqItemDTO = new ArchiveReqItemDTO();
        archiveReqItemDTO.setThirdArchiveId(QiqiSyncConstant.CUSTOM_ARCHIVE_ID_PRE + companyId);
        archiveReqItemDTO.setArchiveName(corpInfo.getCompanyName() + QiqiSyncConstant.ARCHIVE_NAME_SUFFIX);
        archiveReqItemDTO.setCode(qiqiReqDTO.getCode());
        archiveReqItemDTO.setName(qiqiReqDTO.getName());
        archiveReqItemDTO.setThirdProjectId(qiqiReqDTO.getId());
        archiveReqItemDTO.setCompanyId(companyId);
        archiveReqItemDTO.setUseRange(QiqiSyncConstant.USE_RANGE_ALL);
        if (qiqiReqDTO.getDisabledTime() != null) {
            //计算当日零点时间戳
            Long currentTimestamps = System.currentTimeMillis();
            Long oneDayTimestamps = Long.valueOf(60 * 60 * 24 * 1000);
            long todayTimestamps = currentTimestamps - (currentTimestamps + 60 * 60 * 8 * 1000) % oneDayTimestamps;
            //如果停用日期大于当前日期，则为启用
            long timeDifference = qiqiReqDTO.getDisabledTime() - todayTimestamps;
            archiveReqItemDTO.setState(timeDifference > 0 ? StateEnum.STATE_ENABLE.getCode() : StateEnum.STATE_DISABBLE.getCode());
        } else {
            //停用日期为空时默认启用
            archiveReqItemDTO.setState(StateEnum.STATE_ENABLE.getCode());
        }
        if (StringUtils.isNotBlank(qiqiReqDTO.getParentId())) {
            archiveReqItemDTO.setThirdParentId(qiqiReqDTO.getParentId());
        }

        //5.同步
        customArchiveProjectService.updateArchiveProject(companyId, OpenType.QIQI.getType(), archiveReqItemDTO);
        log.info("【qiqi】 QiqiCustomArchiveUpdateProcessor, 自定义档案修改成功,dataId:{}", dataId);
        return TaskProcessResult.success("success");
    }

    @Override
    public Long getSleepSeconds(FinhubTask task) {
        return FinhubTaskUtils.getSleepSeconds(task);
    }
}
