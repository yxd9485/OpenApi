package com.fenbeitong.openapi.plugin.task.qiqi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.constant.TaskConfig;
import com.fenbeitong.finhub.task.itf.ITaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResponseCode;
import com.fenbeitong.openapi.plugin.qiqi.common.exception.OpenApiQiqiException;
import com.fenbeitong.openapi.plugin.qiqi.constant.QiqiSyncConstant;
import com.fenbeitong.openapi.plugin.qiqi.constant.StateEnum;
import com.fenbeitong.openapi.plugin.qiqi.entity.QiqiCorpInfo;
import com.fenbeitong.openapi.plugin.qiqi.orgemployee.dao.QiqiCorpInfoDao;
import com.fenbeitong.openapi.plugin.support.archive.dao.OpenThirdCustomArchiveProjectDao;
import com.fenbeitong.openapi.plugin.support.archive.dto.ArchiveDeleteItemReqDTO;
import com.fenbeitong.openapi.plugin.support.archive.dto.ArchiveReqItemDTO;
import com.fenbeitong.openapi.plugin.support.archive.entity.OpenThirdCustomArchiveProject;
import com.fenbeitong.openapi.plugin.support.archive.service.OpenThirdCustomArchiveProjectService;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenApiResponseDTO;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.task.utils.FinhubTaskUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * @ClassName QiqiCustomArchiveDeleteProcessor
 * @Description
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/26
 **/
@Component
@Slf4j
public class QiqiCustomArchiveDeleteProcessor implements ITaskProcessor {
    @Autowired
    private TaskConfig taskConfig;

    @Autowired
    private QiqiCorpInfoDao corpInfoDao;

    @Autowired
    private OpenThirdCustomArchiveProjectService customArchiveProjectService;

    @Autowired
    private OpenThirdCustomArchiveProjectDao openThirdCustomArchiveProjectDao;

    @Autowired
    private UserCenterService userCenterService;

    @Override
    public String getTaskSrc() {
        return this.taskConfig.getTaskNamespace();
    }

    @Override
    public Integer getTaskType() {
        return TaskType.QIQI_SYNC_BUDGET_ACCOUNT_DELETE.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) throws Exception {
        //1.解析task
        String corpId = task.getCompanyId();
        String dataId = task.getDataId();

        //2.查询企业注册信息
        QiqiCorpInfo corpInfo = corpInfoDao.getBycorpId(corpId);
        String companyId = corpInfo.getCompanyId();

        //3.查询要删除的自定义档案信息
        OpenThirdCustomArchiveProject project = openThirdCustomArchiveProjectDao.getByCompanyIdAndThirdIdAndOpenType(companyId, dataId, OpenType.QIQI.getType());

        //4.1 修改数据转换
        ArchiveReqItemDTO archiveReqItemDTO = new ArchiveReqItemDTO();
        archiveReqItemDTO.setThirdArchiveId(QiqiSyncConstant.CUSTOM_ARCHIVE_ID_PRE + companyId);
        archiveReqItemDTO.setArchiveName(corpInfo.getCompanyName() + QiqiSyncConstant.ARCHIVE_NAME_SUFFIX);
        archiveReqItemDTO.setThirdProjectId(dataId);
        archiveReqItemDTO.setCode(project.getCode());
        archiveReqItemDTO.setName(project.getName());
        archiveReqItemDTO.setCompanyId(companyId);
        archiveReqItemDTO.setUseRange(QiqiSyncConstant.USE_RANGE_ALL);
        archiveReqItemDTO.setState(StateEnum.STATE_DISABBLE.getCode());

        //4.2 删除数据转换
        ArchiveDeleteItemReqDTO archiveDeleteDTO = new ArchiveDeleteItemReqDTO();
        archiveDeleteDTO.setThirdArchiveId(QiqiSyncConstant.CUSTOM_ARCHIVE_ID_PRE + companyId);
        archiveDeleteDTO.setIdList(Lists.newArrayList(dataId));

        //5 删除自定义档案项目
        deleteArchiveProject(companyId, archiveReqItemDTO, archiveDeleteDTO);
        log.info("【qiqi】 QiqiCustomArchiveDeleteProcessor, 自定义档案删除成功,dataId:{}", dataId);
        return TaskProcessResult.success("success");
    }

    @Override
    public Long getSleepSeconds(FinhubTask task) {
        return FinhubTaskUtils.getSleepSeconds(task);
    }


    /**
     * 停用并删除自定义档案项目
     * @param companyId
     * @param archiveReqItemDTO
     * @param archiveDeleteDTO
     */
    private void deleteArchiveProject(String companyId, ArchiveReqItemDTO archiveReqItemDTO, ArchiveDeleteItemReqDTO archiveDeleteDTO) {
        //1.修改自定义档案项目状态为停用
        String token = this.userCenterService.getUcSuperAdminToken(companyId);
        String result = customArchiveProjectService.thirdArchiveProjectUpdate(token, companyId, OpenType.QIQI.getType(), archiveReqItemDTO);
        OpenApiResponseDTO<?> res = (OpenApiResponseDTO)JsonUtils.toObj(result, new TypeReference<OpenApiResponseDTO<?>>() {
        });
        if (!ObjectUtils.isEmpty(res) && res.success()) {
            //2.删除自定义档案项目
            customArchiveProjectService.batchDeleteArchiveProject(companyId, OpenType.QIQI.getType(),archiveDeleteDTO);
        } else {
            log.info("【qiqi】 deleteArchiveProject, 停用（更新）自定义档案项目状态失败");
            throw new OpenApiQiqiException(QiqiResponseCode.UPDATE_ARCHIVE_PROJECT_ERROR);
        }
    }
}
