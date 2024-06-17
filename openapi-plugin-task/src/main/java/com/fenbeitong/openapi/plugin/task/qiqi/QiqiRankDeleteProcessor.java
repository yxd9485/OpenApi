package com.fenbeitong.openapi.plugin.task.qiqi;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.constant.TaskConfig;
import com.fenbeitong.finhub.task.itf.ITaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.qiqi.entity.QiqiCorpInfo;
import com.fenbeitong.openapi.plugin.qiqi.orgemployee.dao.QiqiCorpInfoDao;
import com.fenbeitong.openapi.plugin.support.employee.dto.DeleteAuthRankReqDTO;
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
 * @ClassName QiqiRankDeleteProcessor
 * @Description
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/27
 **/
@Component
@Slf4j
public class QiqiRankDeleteProcessor implements ITaskProcessor {
    @Autowired
    private TaskConfig taskConfig;

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
        return TaskType.QIQI_SYNC_RANK_DELETE.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) throws Exception {
        //1.解析task
        String corpId = task.getCompanyId();
        String dataId = task.getDataId();

        //2.查询企业注册信息
        QiqiCorpInfo corpInfo = corpInfoDao.getBycorpId(corpId);

        //3.数据转换
        List<DeleteAuthRankReqDTO> optRankDTOList = Lists.newArrayList();
        DeleteAuthRankReqDTO optRankDTO = new DeleteAuthRankReqDTO();
        optRankDTO.setThirdRankId(dataId);
        optRankDTOList.add(optRankDTO);

        //4.同步
        cacheEmployeeRankTemplateService.deleteRankBatch(OpenType.QIQI.getType(), corpInfo.getCompanyId(), optRankDTOList);
        return TaskProcessResult.success("success");
    }

    @Override
    public Long getSleepSeconds(FinhubTask task) {
        return FinhubTaskUtils.getSleepSeconds(task);
    }
}
