package com.fenbeitong.openapi.plugin.task.qiqi;

import com.fenbeitong.common.utils.collect.CollectionUtils;
import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.constant.TaskConfig;
import com.fenbeitong.finhub.task.itf.ITaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResponseCode;
import com.fenbeitong.openapi.plugin.qiqi.common.exception.OpenApiQiqiException;
import com.fenbeitong.openapi.plugin.qiqi.constant.ObjectTypeEnum;
import com.fenbeitong.openapi.plugin.qiqi.dto.QiqiProjectReqDTO;
import com.fenbeitong.openapi.plugin.qiqi.entity.QiqiCorpInfo;
import com.fenbeitong.openapi.plugin.qiqi.orgemployee.dao.QiqiCorpInfoDao;
import com.fenbeitong.openapi.plugin.qiqi.service.common.QiqiCommonReqServiceImpl;
import com.fenbeitong.openapi.plugin.qiqi.service.project.IQiqiProjectService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.project.dto.SupportUcThirdProjectReqDTO;
import com.fenbeitong.openapi.plugin.support.project.service.OpenBaseProjectService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.task.utils.FinhubTaskUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName QiqiProjectUpdateProcessor
 * @Description
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/26
 **/
@Component
@Slf4j
public class QiqiProjectUpdateProcessor implements ITaskProcessor {

    @Autowired
    private TaskConfig taskConfig;

    @Autowired
    private QiqiCommonReqServiceImpl qiqiCommonReqService;

    @Autowired
    private QiqiCorpInfoDao corpInfoDao;

    @Autowired
    private OpenBaseProjectService openBaseProjectService;

    @Autowired
    private IQiqiProjectService qiqiProjectService;

    @Override
    public String getTaskSrc() {
        return this.taskConfig.getTaskNamespace();
    }

    @Override
    public Integer getTaskType() {
        return TaskType.QIQI_SYNC_PROJECT_UPDATE.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) throws Exception {
        //1.解析task
        String corpId = task.getCompanyId();
        String dataId = task.getDataId();

        //2.查询企业注册信息
        QiqiCorpInfo corpInfo = corpInfoDao.getBycorpId(corpId);
        String companyId = corpInfo.getCompanyId();

        //3.拉取项目增量数据
        List<QiqiProjectReqDTO> projectDTOS = qiqiCommonReqService.buildQiqiReq(companyId, ObjectTypeEnum.PROJECT.getCode(), QiqiProjectReqDTO.class, "id='" + dataId + "' and isChangeBill = false", qiqiProjectService.getTreeParam());
        if (CollectionUtils.isBlank(projectDTOS)) {
            log.info("【qiqi】 QiqiProjectUpdateProcessor, 查询三方数据为空");
            throw new OpenApiQiqiException(QiqiResponseCode.DATA_NOT_EXIST);
        }
        QiqiProjectReqDTO qiqiProjectDTO = projectDTOS.get(0);

        //4.转换数据
        SupportUcThirdProjectReqDTO projectDTO = qiqiProjectService.projectConvertForAdd(companyId, qiqiProjectDTO);

        //5.更新
        openBaseProjectService.updateProject(companyId, OpenType.QIQI.getType(), projectDTO);
        log.info("【qiqi】 QiqiProjectUpdateProcessor, 项目修改成功,dataId:{}", dataId);
        return TaskProcessResult.success("success");
    }

    @Override
    public Long getSleepSeconds(FinhubTask task) {
        return FinhubTaskUtils.getSleepSeconds(task);
    }
}
