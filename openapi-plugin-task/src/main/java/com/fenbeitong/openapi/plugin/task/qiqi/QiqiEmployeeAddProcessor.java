package com.fenbeitong.openapi.plugin.task.qiqi;

import com.fenbeitong.common.utils.collect.CollectionUtils;
import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.constant.TaskConfig;
import com.fenbeitong.finhub.task.itf.ITaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResponseCode;
import com.fenbeitong.openapi.plugin.qiqi.common.exception.OpenApiQiqiException;
import com.fenbeitong.openapi.plugin.qiqi.constant.ObjectTypeEnum;
import com.fenbeitong.openapi.plugin.qiqi.dto.QiqiEmployeeReqDTO;
import com.fenbeitong.openapi.plugin.qiqi.entity.QiqiCorpInfo;
import com.fenbeitong.openapi.plugin.qiqi.orgemployee.dao.QiqiCorpInfoDao;
import com.fenbeitong.openapi.plugin.qiqi.service.common.QiqiCommonReqServiceImpl;
import com.fenbeitong.openapi.plugin.qiqi.service.department.IQiqiDepartmentService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.task.utils.FinhubTaskUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName QiqiEmployeeAddProcessor
 * @Description
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/25
 **/
@Component
@Slf4j
public class QiqiEmployeeAddProcessor implements ITaskProcessor {

    @Autowired
    private TaskConfig taskConfig;

    @Autowired
    private IQiqiDepartmentService qiqiDepartmentService;

    @Autowired
    private QiqiCommonReqServiceImpl qiqiCommonReqService;

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Autowired
    private QiqiCorpInfoDao corpInfoDao;

    @Override
    public String getTaskSrc() {
        return this.taskConfig.getTaskNamespace();
    }

    @Override
    public Integer getTaskType() {
        return TaskType.QIQI_SYNC_USER_ADD.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) throws Exception {
        //1.解析task
        String corpId = task.getCompanyId();
        String dataId = task.getDataId();
        //2.查询企业注册信息
        QiqiCorpInfo corpInfo = corpInfoDao.getBycorpId(corpId);
        //3.拉取人员增量数据
        String companyId = corpInfo.getCompanyId();
        List<QiqiEmployeeReqDTO> employeeInfos = qiqiCommonReqService.buildQiqiReq(companyId, ObjectTypeEnum.USER.getCode(), QiqiEmployeeReqDTO.class, "id='" + dataId + "'", qiqiDepartmentService.getTreeParam());
        if (CollectionUtils.isBlank(employeeInfos)) {
            log.info("【qiqi】 QiqiEmployeeAddProcessor, 查询三方数据为空");
            throw new OpenApiQiqiException(QiqiResponseCode.DATA_NOT_EXIST);
        }
        //4.转换人员
        List<OpenThirdEmployeeDTO> employeeList = qiqiDepartmentService.employeeConvert(employeeInfos, corpId, companyId);
        //5.同步
        openSyncThirdOrgService.addEmployee(OpenType.QIQI.getType(), companyId, employeeList);
        log.info("【qiqi】 QiqiEmployeeAddProcessor, 人员新增成功,dataId:{}", dataId);
        return TaskProcessResult.success("success");
    }

    @Override
    public Long getSleepSeconds(FinhubTask task) {
        return FinhubTaskUtils.getSleepSeconds(task);
    }
}
