package com.fenbeitong.openapi.plugin.task.qiqi;

import com.fenbeitong.common.utils.basis.StringUtil;
import com.fenbeitong.common.utils.collect.CollectionUtils;
import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.constant.TaskConfig;
import com.fenbeitong.finhub.task.itf.ITaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResponseCode;
import com.fenbeitong.openapi.plugin.qiqi.common.exception.OpenApiQiqiException;
import com.fenbeitong.openapi.plugin.qiqi.constant.ObjectTypeEnum;
import com.fenbeitong.openapi.plugin.qiqi.constant.QiqiSyncConstant;
import com.fenbeitong.openapi.plugin.qiqi.dto.QiqiDepartmentReqDTO;
import com.fenbeitong.openapi.plugin.qiqi.entity.QiqiCorpInfo;
import com.fenbeitong.openapi.plugin.qiqi.orgemployee.dao.QiqiCorpInfoDao;
import com.fenbeitong.openapi.plugin.qiqi.service.common.QiqiCommonReqServiceImpl;
import com.fenbeitong.openapi.plugin.qiqi.service.department.IQiqiDepartmentService;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.task.utils.FinhubTaskUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @ClassName QiqiDepartmentAddSyncProcessor
 * @Description
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/23
 **/
@Component
@Slf4j
public class QiqiDepartmentAddProcessor implements ITaskProcessor {

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Autowired
    private TaskConfig taskConfig;

    @Autowired
    private IQiqiDepartmentService qiqiDepartmentService;

    @Autowired
    private QiqiCommonReqServiceImpl qiqiCommonReqService;

    @Autowired
    private QiqiCorpInfoDao corpInfoDao;

    @Autowired
    private OpenSysConfigDao openSysConfigDao;

    @Override
    public String getTaskSrc() {
        return this.taskConfig.getTaskNamespace();
    }

    @Override
    public Integer getTaskType() {
        return TaskType.QIQI_SYNC_DEPT_ADD.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) throws Exception {
        //1.解析task
        String corpId = task.getCompanyId();
        String dataId = task.getDataId();

        //2.查询企业注册信息
        QiqiCorpInfo corpInfo = corpInfoDao.getBycorpId(corpId);

        //获取当前时间戳
        String currentStr = StringUtil.obj2str(System.currentTimeMillis());
        Long currentTime = Long.valueOf(currentStr.substring(0, currentStr.length() - 3));

        //3.拉取部门增量数据
        List<QiqiDepartmentReqDTO> departmentInfos = qiqiCommonReqService.buildQiqiReq(corpInfo.getCompanyId(), ObjectTypeEnum.DEPARTMENT.getCode(), QiqiDepartmentReqDTO.class, "id='" + dataId + "' and (disabledTime is null or disabledTime>to_timestamp("+currentTime+"))", null);
        if (CollectionUtils.isBlank(departmentInfos)) {
            log.info("【qiqi】 QiqiDepartmentAddProcessor, 查询三方数据为空");
            throw new OpenApiQiqiException(QiqiResponseCode.DATA_NOT_EXIST);
        }

        //如果新增的是一级部门，就给它加一个根节点
        for (QiqiDepartmentReqDTO reqDTO : departmentInfos) {
            if (reqDTO.getLevel() == 0) {
                reqDTO.setParentId(QiqiSyncConstant.ROOT_ID);
            }
        }

        //4.转换部门
        List<OpenThirdOrgUnitDTO> departmentList = qiqiDepartmentService.departmentConvert(departmentInfos, corpInfo.getCompanyId(), corpInfo.getCompanyName());

        //5.同步
        openSyncThirdOrgService.addDepartment(OpenType.QIQI.getType(), corpInfo.getCompanyId(), departmentList);

        // 查询配置判断该企业是否要同步部门主管
        OpenSysConfig openSysConfig = openSysConfigDao.selectDepManager(corpInfo.getCompanyId());
        // 判断设置部门主管配置时否为空
        if (!ObjectUtils.isEmpty(openSysConfig)) {
            openSyncThirdOrgService.setPartDepManagePackV2(departmentList, corpInfo.getCompanyId());
        }
        log.info("【qiqi】 QiqiDepartmentAddProcessor, 部门新增成功,dataId:{}", dataId);
        return TaskProcessResult.success("success");
    }

    @Override
    public Long getSleepSeconds(FinhubTask task) {
        return FinhubTaskUtils.getSleepSeconds(task);
    }
}
