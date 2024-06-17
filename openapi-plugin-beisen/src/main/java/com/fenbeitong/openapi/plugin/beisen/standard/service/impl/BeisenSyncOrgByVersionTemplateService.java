package com.fenbeitong.openapi.plugin.beisen.standard.service.impl;

import com.fenbeitong.openapi.plugin.beisen.common.constant.BeiSenResponseCode;
import com.fenbeitong.openapi.plugin.beisen.common.dao.BeisenCorpDao;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenJobParamDTO;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenParamConfig;
import com.fenbeitong.openapi.plugin.beisen.common.entity.BeisenCorp;
import com.fenbeitong.openapi.plugin.beisen.common.exception.OpenApiBeisenException;
import com.fenbeitong.openapi.plugin.beisen.standard.service.IBeisenSyncEmployeeAndDept;
import com.fenbeitong.openapi.plugin.beisen.standard.service.IBeisenSyncOrg;
import com.fenbeitong.openapi.plugin.beisen.standard.service.IBeisenSyncRank;
import com.fenbeitong.openapi.plugin.support.employee.dto.AddAuthRankReqDTO;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenTemplateConfigConstant;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenTemplateConfigDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenTemplateConfig;
import com.fenbeitong.openapi.plugin.support.init.service.FullDataSynchronizer;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.finhub.framework.core.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * <p>Title: AbstractBeisenSyncOrgByVersion<p>
 * <p>Description: 北森根据版本同步组织架构模版类<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author liuhong
 * @date 2022/9/9 14:29
 */
@ServiceAspect
@Service
@Slf4j
public class BeisenSyncOrgByVersionTemplateService {
    @Autowired
    private OpenTemplateConfigDao templateConfigDao;
    @Resource(name = "employeeRankTemplateFullDataSynchronizer")
    private FullDataSynchronizer rankSynchronizer;
    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;
    @Autowired
    private BeisenCorpDao beisenCorpDao;

    /**
     * 根据版本同步组织架构模版方法
     *
     * @param jobParamDTO 定时任务参数
     */
    @Async
    public void doSyncEmployeeAndDept(BeisenJobParamDTO jobParamDTO) {
        BeisenCorp beisenCorp = beisenCorpDao.getByCompanyId(jobParamDTO.getCompanyId());
        if (ObjectUtils.isEmpty(beisenCorp)) {
            log.info("未在beisen_corp表中配置公司信息companyId:{},请联系实施", jobParamDTO.getCompanyId());
            return;
        }
        // 1 找到实现类
        IBeisenSyncEmployeeAndDept syncEmployeeAndOrgService = (IBeisenSyncEmployeeAndDept) getServiceByTypeAndVersion(jobParamDTO.getCompanyId(), OpenTemplateConfigConstant.TYPE.org);
        // 2 查询部门并组装数据
        List<OpenThirdOrgUnitDTO> departmentList = syncEmployeeAndOrgService.getDeptList(jobParamDTO, beisenCorp);
        if (CollectionUtils.isBlank(departmentList)) {
            log.info("北森全量同步组织架构未获取到部门信息，companyId:{}",jobParamDTO.getCompanyId());
            return;
        }
        // 3 查询人员并组装数据
        List<OpenThirdEmployeeDTO> employeeList = syncEmployeeAndOrgService.getEmployeeList(jobParamDTO, beisenCorp);
        if (CollectionUtils.isBlank(employeeList)) {
            log.info("北森全量同步组织架构未获取到人员信息，companyId:{}",jobParamDTO.getCompanyId());
            return;
        }
        // 4 同步
        openSyncThirdOrgService.syncThird(OpenType.BEISEN.getType(), jobParamDTO.getCompanyId(), departmentList, employeeList);
    }

    /**
     * 全量同步职级
     *
     * @param jobParamDTO 职级同步参数
     */
    @Async
    public void doSyncRank(BeisenJobParamDTO jobParamDTO) {
        BeisenCorp beisenCorp = beisenCorpDao.getByCompanyId(jobParamDTO.getCompanyId());
        if (ObjectUtils.isEmpty(beisenCorp)) {
            log.info("未在beisen_corp表中配置公司信息companyId:{},请联系实施", jobParamDTO.getCompanyId());
            return;
        }
        //1 获取职级实现类
        IBeisenSyncRank beisenSyncRankService = (IBeisenSyncRank) getServiceByTypeAndVersion(jobParamDTO.getCompanyId(), OpenTemplateConfigConstant.TYPE.RANK);
        //2 从北森拉取职级列表并转换
        List<AddAuthRankReqDTO> rankList = beisenSyncRankService.getRankList(jobParamDTO, beisenCorp);
        if (CollectionUtils.isBlank(rankList)) {
            return;
        }
        //3 同步
        rankSynchronizer.sync(OpenType.BEISEN, jobParamDTO.getCompanyId(), rankList);

    }

    /**
     * @param companyId 公司id
     * @param type      3：部门及员工 10：职级
     * @return 组织架构实现类
     */
    private IBeisenSyncOrg getServiceByTypeAndVersion(String companyId, int type) {
        OpenTemplateConfig openTemplateConfig = templateConfigDao.selectByCompanyId(companyId, type, OpenType.BEISEN.getType());
        if (!Optional.ofNullable(openTemplateConfig).map(OpenTemplateConfig::getListenerClass).isPresent()) {
            log.info("北森同步组织架构参数配置未配置,请联系实施人员，companyId:{},type:{},openType:{}", companyId, type, OpenType.BEISEN.getType());
            throw new OpenApiBeisenException(BeiSenResponseCode.BEISEN_SYNC_ORG_TEMPLATE_PARAM_is_null, "北森同步组织架构参数配置未配置");
        }
        IBeisenSyncOrg beisenSyncOrgService;
        try {
            beisenSyncOrgService = (IBeisenSyncOrg) SpringUtils.getBean(Class.forName(openTemplateConfig.getListenerClass()));
        } catch (Exception e) {
            log.info("北森同步组织架构参数配置错误,请联系实施人员，OpenTemplateConfig:{},error:{}", JsonUtils.toJson(openTemplateConfig), e.getStackTrace());
            throw new OpenApiBeisenException(BeiSenResponseCode.BEISEN_SYNC_ORG_TEMPLATE_PARAM_ERROR, "北森同步组织架构参数配置错误");
        }
        return beisenSyncOrgService;
    }

}
