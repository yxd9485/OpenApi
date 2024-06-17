package com.fenbeitong.openapi.plugin.kingdee.common.service.impl;

import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeeConfigDTO;
import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeeK3CloudConfigDTO;
import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeeProjectDto;
import com.fenbeitong.openapi.plugin.kingdee.common.service.KingDeeProjectService;
import com.fenbeitong.openapi.plugin.kingdee.common.service.KingDeeProjectSyncService;
import com.fenbeitong.openapi.plugin.kingdee.support.dao.OpenKingdeeUrlConfigDao;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.ViewReqDTO;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenKingdeeUrlConfig;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdEmployee;
import com.fenbeitong.openapi.plugin.support.organization.AbstractOrganizationService;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.sdk.dto.project.ListThirdProjectRespDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 金蝶同步项目信息
 * @Auther zhang.peng
 * @Date 2021/6/3
 */
@Slf4j
@ServiceAspect
@Service
public class KIngDeeProjectSyncServiceImpl extends AbstractOrganizationService implements KingDeeProjectSyncService {


    @Autowired
    private KingDeeServiceImpl jinDieService;

    @Autowired
    private KingDeeProjectService openJinDieProjectService;

    @Autowired
    private OpenKingdeeUrlConfigDao kingDeeUrlConfigDao;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

    @Override
    public String syncItem(String companyId) {
        long start = System.currentTimeMillis();
        // 获取配置信息
        OpenKingdeeUrlConfig kingDeeUrlConfig = kingDeeUrlConfigDao.getByCompanyId(companyId);
        // 获取 Token
        KingDeeConfigDTO jinDieConfigDTo = buildKingDeeDto(kingDeeUrlConfig);
//        String token = jinDieService.getToken(jinDieConfigDTo);
//        log.info("{} 对接金蝶，获取 token:{}", companyId , token);
        // 查询项目
        KingDeeK3CloudConfigDTO kingDeeK3CloudConfigDTO = jinDieService.getConfig(companyId);
        // 销售数据
        ViewReqDTO viewReqDTO = kingDeeK3CloudConfigDTO.getSaleInfo();
        // 获取Cookie
        String cookie = jinDieService.getCookie(kingDeeUrlConfig);
        // 查询金蝶三方接口 - 获取销售管理信息
        List<List> kingdeeDataList = jinDieService.getData(viewReqDTO,kingDeeUrlConfig,cookie);
        ViewReqDTO salerReq = buildSalerReq(kingdeeDataList,kingDeeK3CloudConfigDTO);
        // 查询金蝶三方接口 - 获取销售员数据
        List<List> kingdeeSalerDataList = jinDieService.getData(salerReq,kingDeeUrlConfig,cookie);
        Map<String,String> orgId2SalerIdMap = new HashMap<>();
        buildOrg2SalerIdMap(kingdeeSalerDataList,orgId2SalerIdMap);
        // 构建项目数据
        KingDeeProjectDto projectListDTo = buildProjectList(kingdeeDataList,orgId2SalerIdMap);
        long queryStart = System.currentTimeMillis();
        // 查询本地数据
        ListThirdProjectRespDTO listThirdProjectRespDTO = openJinDieProjectService.getProjectByCompanyId(companyId);
        log.info("获取全量数据全量数据 ListThirdProjectRespDTO 查询耗时 {} ms , ", System.currentTimeMillis() - queryStart);
        long updateStart = System.currentTimeMillis();
        // 数据对比并更新
        boolean successOrNot = openJinDieProjectService.addOrUpdateProjectInfo(listThirdProjectRespDTO,projectListDTo,companyId);
        log.info("项目更据耗新 -> projectUpdateOrAdd 耗时 {} ms", System.currentTimeMillis() - updateStart);
        log.info("数据同步 -> syncProject 总耗时 {} ms", System.currentTimeMillis() - start);
        if (successOrNot){
            return "success";
        }
        return "failed";
    }

    private KingDeeConfigDTO buildKingDeeDto(OpenKingdeeUrlConfig kingDeeUrlConfig) {
        KingDeeConfigDTO kingDeeConfigDTO = new KingDeeConfigDTO();
        BeanUtils.copyProperties(kingDeeUrlConfig,kingDeeConfigDTO);
        return kingDeeConfigDTO;
    }

    private KingDeeProjectDto buildProjectList(List<List> kingdeeDataList, Map<String, String> orgId2SalerIdMap) {
        KingDeeProjectDto kingDeeProjectListDTO = new KingDeeProjectDto();
        if ( CollectionUtils.isBlank(kingdeeDataList) ){
            return kingDeeProjectListDTO;
        }
        List<KingDeeProjectDto.Project> iteamList = new ArrayList<>();
        for (List fields : kingdeeDataList) {
            KingDeeProjectDto.Project iteam = new KingDeeProjectDto.Project();
            buildItemInfo(iteam,fields,orgId2SalerIdMap);
            iteamList.add(iteam);
        }
        kingDeeProjectListDTO.setProjectList(iteamList);
        return kingDeeProjectListDTO;
    }

    public String convertTime(String time){
        if (StringUtils.isBlank(time) || "null".equals(time)){
            return "";
        }
        if (time.contains(".")){
            time = time.substring(0,time.lastIndexOf("."));
        }
        LocalDateTime createDateRev = LocalDateTime.parse(time, formatter);
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(createDateRev);
    }

    public void buildItemInfo(KingDeeProjectDto.Project iteam, List fields, Map<String,String> orgId2SalerIdMap){
        // 销售单号
        String fBillNo = String.valueOf(fields.get(0));
        // 销售员ID
        String fSalerId = String.valueOf(fields.get(1));
        // 销售单号Id
        String fid = String.valueOf(fields.get(2));
        // 项目创建时间
        String createDate = String.valueOf(fields.get(3));
        // 项目计划交付时间
        String planEndDate = String.valueOf(fields.get(3));
        // 倒推开始时间
        String endDate = String.valueOf(fields.get(3));
        // 销售员对应的三方组织机构Id
        String orgId = orgId2SalerIdMap.get(fSalerId);
        // 查询分贝通组织机构Id
        if (StringUtils.isNotBlank(orgId) && orgId.contains("-")){
            String[] employInfos = orgId.split("-");
            iteam.setUserId(employInfos[0]);
            iteam.setUserName(employInfos[1]);
        }
        iteam.setProjectName(fBillNo);
        iteam.setProjectCode(fid);
    }

    public void buildOrg2SalerIdMap(List<List> kingdeeSalerDataList , Map<String,String> orgId2SalerIdMap){
        if ( CollectionUtils.isNotBlank(kingdeeSalerDataList) ){
            for (List fields : kingdeeSalerDataList) {
                // 销售员组织机构信息
                String fEmpNumber = String.valueOf(fields.get(0));
                // 销售员salerId
                String fId = String.valueOf(fields.get(1));
                // 销售员姓名
                String fName = String.valueOf(fields.get(2));
                if (!orgId2SalerIdMap.containsKey(fId)){
                    orgId2SalerIdMap.put(fId,fEmpNumber + "-" + fName);
                }
            }
        }
    }

    // 三方人员映射分贝通人员Id
    public Map buildThirdEmployeeId2FbtIdMap(List<OpenThirdEmployee> openThirdEmployeeList ){
        Map<String,String> thirdEmployeeId2FbtIdMap = new HashMap<>();
        if (CollectionUtils.isNotBlank(openThirdEmployeeList)){
            openThirdEmployeeList.stream().forEach(openThirdEmployee -> {
                if ( !thirdEmployeeId2FbtIdMap.containsKey(openThirdEmployee.getThirdEmployeeId()) ){
                    thirdEmployeeId2FbtIdMap.put(openThirdEmployee.getThirdEmployeeId(),openThirdEmployee.getEmployeeId());
                }
            });
        }
        return thirdEmployeeId2FbtIdMap;
    }

    public List<String> buildSalerList(List<List> kingdeeDataList){
        List<String> fSalerIds = new ArrayList<>();
        if ( CollectionUtils.isNotBlank(kingdeeDataList) ){
            for (List fields : kingdeeDataList) {
                // 订单所属销售员id
                String fSalerId = String.valueOf(fields.get(1));
                fSalerIds.add(fSalerId);
            }
        }
        return fSalerIds;
    }

    public ViewReqDTO buildSalerReq(List<List> kingdeeDataList, KingDeeK3CloudConfigDTO kingDeeK3CloudConfigDTO){
        List<String> fSalerIds = buildSalerList(kingdeeDataList);
        String fSalerIdParam = CollectionUtils.isBlank(fSalerIds) ? "" : StringUtils.strip(fSalerIds.toString(),"[]");
        // 查询销售员数据
        ViewReqDTO salerReq = kingDeeK3CloudConfigDTO.getSaler();
        if ( null != salerReq ){
            String filterString = salerReq.getData().getFilterString().replace("#",fSalerIdParam);
            salerReq.getData().setFilterString(filterString);
        }
        return salerReq;
    }

}
