package com.fenbeitong.openapi.plugin.task.qiqi;

import com.fenbeitong.common.utils.collect.CollectionUtils;
import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.constant.TaskConfig;
import com.fenbeitong.finhub.task.itf.ITaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResponseCode;
import com.fenbeitong.openapi.plugin.qiqi.common.exception.OpenApiQiqiException;
import com.fenbeitong.openapi.plugin.qiqi.constant.ObjectTypeEnum;
import com.fenbeitong.openapi.plugin.qiqi.dto.QiqiRankReqDTO;
import com.fenbeitong.openapi.plugin.qiqi.entity.QiqiCorpInfo;
import com.fenbeitong.openapi.plugin.qiqi.orgemployee.dao.QiqiCorpInfoDao;
import com.fenbeitong.openapi.plugin.qiqi.service.common.QiqiCommonReqServiceImpl;
import com.fenbeitong.openapi.plugin.support.employee.dto.AddAuthRankReqDTO;
import com.fenbeitong.openapi.plugin.support.employee.service.CacheEmployeeRankTemplateService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.task.utils.FinhubTaskUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName QiqiRankAddProcessor
 * @Description
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/27
 **/
@Component
@Slf4j
public class QiqiRankAddProcessor implements ITaskProcessor {

    @Autowired
    private TaskConfig taskConfig;

    @Autowired
    private QiqiCommonReqServiceImpl qiqiCommonReqService;

    @Autowired
    private QiqiCorpInfoDao corpInfoDao;

    @Autowired
    private CacheEmployeeRankTemplateService cacheEmployeeRankTemplateService;

    @Override
    public String getTaskSrc() {
        return this.taskConfig.getTaskNamespace();
    }

    @Override
    public Integer getTaskType() {
        return TaskType.QIQI_SYNC_RANK_ADD.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) throws Exception {
        //1.解析task
        String corpId = task.getCompanyId();
        String dataId = task.getDataId();

        //2.查询企业注册信息
        QiqiCorpInfo corpInfo = corpInfoDao.getBycorpId(corpId);

        //3.拉取职级增量数据
        List<QiqiRankReqDTO> qiqiRankReqDTOS = qiqiCommonReqService.buildQiqiReq(corpInfo.getCompanyId(), ObjectTypeEnum.RANK.getCode(), QiqiRankReqDTO.class, "id='" + dataId + "'", null);
        if (CollectionUtils.isBlank(qiqiRankReqDTOS)) {
            log.info("【qiqi】 QiqiRankAddProcessor, 查询三方数据为空");
            throw new OpenApiQiqiException(QiqiResponseCode.DATA_NOT_EXIST);
        }

        //4.数据转换
        List<AddAuthRankReqDTO> optRankDTOList = Lists.newArrayList();
        for (QiqiRankReqDTO reqDTO : qiqiRankReqDTOS) {
            AddAuthRankReqDTO optRankDTO = new AddAuthRankReqDTO();
            optRankDTO.setThirdRankId(dataId);
            optRankDTO.setRankName(reqDTO.getName());
            optRankDTOList.add(optRankDTO);
        }

        //5.同步
        cacheEmployeeRankTemplateService.addRankBatch(OpenType.QIQI.getType(), corpInfo.getCompanyId(), optRankDTOList);
        return TaskProcessResult.success("success");
    }

    @Override
    public Long getSleepSeconds(FinhubTask task) {
        return FinhubTaskUtils.getSleepSeconds(task);
    }
}
