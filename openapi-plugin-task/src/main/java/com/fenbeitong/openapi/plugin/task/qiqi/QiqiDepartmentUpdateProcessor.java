package com.fenbeitong.openapi.plugin.task.qiqi;

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
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitManagersDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnit;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.task.utils.FinhubTaskUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName QiqiDepartmentUpdateProcessor
 * @Description
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/25
 **/
@Component
@Slf4j
public class QiqiDepartmentUpdateProcessor implements ITaskProcessor {

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
    private OpenThirdOrgUnitDao openThirdOrgUnitDao;

    @Autowired
    private OpenSysConfigDao openSysConfigDao;

    @Autowired
    private OpenThirdOrgUnitManagersDao openThirdOrgUnitManagersDao;

    @Override
    public String getTaskSrc() {
        return this.taskConfig.getTaskNamespace();
    }

    @Override
    public Integer getTaskType() {
        return TaskType.QIQI_SYNC_DEPT_UPDATE.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) throws Exception {
        //1.解析task
        String corpId = task.getCompanyId();
        String dataId = task.getDataId();

        //2.查询企业注册信息
        QiqiCorpInfo corpInfo = corpInfoDao.getBycorpId(corpId);
        String companyId = corpInfo.getCompanyId();

        //3.拉取部门增量数据
        List<QiqiDepartmentReqDTO> departmentInfos = qiqiCommonReqService.buildQiqiReq(companyId, ObjectTypeEnum.DEPARTMENT.getCode(), QiqiDepartmentReqDTO.class, "id='" + dataId + "'", null);
        if (CollectionUtils.isBlank(departmentInfos)) {
            log.info("【qiqi】 QiqiDepartmentUpdateProcessor, 查询三方数据为空");
            throw new OpenApiQiqiException(QiqiResponseCode.DATA_NOT_EXIST);
        }

        //如果新增的是一级部门，就给它加一个根节点
        for (QiqiDepartmentReqDTO reqDTO : departmentInfos) {
            if (reqDTO.getLevel() == 0) {
                reqDTO.setParentId(QiqiSyncConstant.ROOT_ID);
            }
        }
        //计算当日零点时间戳
        Long currentTimestamps = System.currentTimeMillis();
        Long oneDayTimestamps = Long.valueOf(60 * 60 * 24 * 1000);
        long todayTimestamps = currentTimestamps - (currentTimestamps + 60 * 60 * 8 * 1000) % oneDayTimestamps;
        Long disabledTime = departmentInfos.get(0).getDisabledTime();
        // 查询配置判断该企业是否要同步部门主管
        OpenSysConfig openSysConfig = openSysConfigDao.selectDepManager(companyId);
        //如果停用日期小于等于当前日期，就删除部门
        if (disabledTime != null && disabledTime - todayTimestamps <= 0) {
            //转换数据
            List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
            departmentList.add(OpenThirdOrgUnitDTO.builder().companyId(companyId).thirdOrgUnitId(dataId).build());
            //删除部门
            openSyncThirdOrgService.deleteDepartment(OpenType.QIQI.getType(), companyId, departmentList);
            // 删除部门主管
            if (!ObjectUtils.isEmpty(openSysConfig)) {
                openThirdOrgUnitManagersDao.deleteByThirdOrgUnitId(companyId, dataId);
            }
        } else {
            //转换部门
            List<OpenThirdOrgUnitDTO> departmentList = qiqiDepartmentService.departmentConvert(departmentInfos, companyId, corpInfo.getCompanyName());

            List<OpenThirdOrgUnit> srcOrgUnitList = openThirdOrgUnitDao.listOrgUnitByThirdOrgUnitId(OpenType.QIQI.getType(), companyId, Lists.newArrayList(dataId));
            //如果中间表不存在要修改的数据就新增
            if (CollectionUtils.isBlank(srcOrgUnitList)) {
                openSyncThirdOrgService.addDepartment(OpenType.QIQI.getType(), companyId, departmentList);
            } else {
                //更新
                openSyncThirdOrgService.updateDepartment(OpenType.QIQI.getType(), companyId, departmentList);
            }
            // 判断设置部门主管配置时否为空
            if (!ObjectUtils.isEmpty(openSysConfig)) {
                openSyncThirdOrgService.setPartDepManagePackV2(departmentList, companyId);
            }
        }
        log.info("【qiqi】 QiqiDepartmentUpdateProcessor, 部门更新成功,dataId:{}", dataId);
        return TaskProcessResult.success("success");
    }

    @Override
    public Long getSleepSeconds(FinhubTask task) {
        return FinhubTaskUtils.getSleepSeconds(task);
    }
}
