package com.fenbeitong.openapi.plugin.yiduijie.service.impl;

import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.RandomUtils;
import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieApiResponseCode;
import com.fenbeitong.openapi.plugin.yiduijie.constant.MappingType;
import com.fenbeitong.openapi.plugin.yiduijie.dao.YiDuiJieConfDao;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieBaseResp;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieListMappingResp;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieMappingReq;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieUpsertMappingResp;
import com.fenbeitong.openapi.plugin.yiduijie.entity.YiDuiJieConf;
import com.fenbeitong.openapi.plugin.yiduijie.exception.OpenApiYiDuiJieException;
import com.fenbeitong.openapi.plugin.yiduijie.model.mapping.AccountMappingReqDTO;
import com.fenbeitong.openapi.plugin.yiduijie.model.mapping.DepartmentMappingReqDTO;
import com.fenbeitong.openapi.plugin.yiduijie.model.mapping.MappingDTO;
import com.fenbeitong.openapi.plugin.yiduijie.model.mapping.ProjectMappingReqDTO;
import com.fenbeitong.openapi.plugin.yiduijie.sdk.YiDuiJieMappingApi;
import com.fenbeitong.openapi.plugin.yiduijie.service.IYiDuiJieTokenService;
import com.fenbeitong.openapi.plugin.yiduijie.service.mapping.IMappingService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <p>Title: BaseMappingServiceImpl</p>
 * <p>Description: 映射服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/13 11:15 AM
 */
@ServiceAspect
@Service
public class BaseMappingServiceImpl extends BaseYiDuiJieService implements IMappingService {

    @Autowired
    private YiDuiJieConfDao yiDuiJieConfDao;

    @Autowired
    private IYiDuiJieTokenService yiDuiJieTokenService;

    @Autowired
    private YiDuiJieMappingApi yiDuiJieMappingApi;

    @Override
    public void mappingAccount(String companyId, List<AccountMappingReqDTO> accountMappingReqList) {
        if (ObjectUtils.isEmpty(accountMappingReqList)) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.MAPPING_ACCOUNT_ERROR)));
        }
        checkReqParam(accountMappingReqList);
        YiDuiJieConf yiDuiJieConf = yiDuiJieConfDao.getByCompanyId(companyId);
        if (yiDuiJieConf == null) {
            return;
        }
        String token = yiDuiJieTokenService.getYiDuiJieToken();
        boolean success = true;
        for (AccountMappingReqDTO mappingReq : accountMappingReqList) {
            String srcName = !ObjectUtils.isEmpty(mappingReq.getSuperMappingField())
                ? (mappingReq.getFinanceDepartment() + ";" + mappingReq.getFeeType() + ";" + mappingReq.getSuperMappingField())
                : !ObjectUtils.isEmpty(mappingReq.getSuperMappingField1()) ? (mappingReq.getFinanceDepartment() + ";" + mappingReq.getFeeType() + ";;" + mappingReq.getSuperMappingField1())
                : (mappingReq.getFinanceDepartment() + ";" + mappingReq.getFeeType());
            YiDuiJieMappingReq apiReq = YiDuiJieMappingReq.builder()
                .srcName(srcName)
                .destName(mappingReq.getAccount()).build();
            YiDuiJieBaseResp mappingResp = yiDuiJieMappingApi.mappings(token, yiDuiJieConf.getAppId(), MappingType.account.getValue(), apiReq);
            if (mappingResp == null || !mappingResp.success()) {
                success = false;
            }
        }
        if (!success) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.MAPPING_ACCOUNT_ERROR)));
        }
    }

    @Override
    public List<String> addMappingAccount(String companyId, List<AccountMappingReqDTO> accountMappingReqList) {
        if (ObjectUtils.isEmpty(accountMappingReqList)) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.MAPPING_ACCOUNT_ERROR)));
        }
        checkReqParam(accountMappingReqList);
        YiDuiJieConf yiDuiJieConf = yiDuiJieConfDao.getByCompanyId(companyId);
        if (yiDuiJieConf == null) {
            return IntStream.range(0, accountMappingReqList.size()).mapToObj(i -> RandomUtils.bsonId()).collect(Collectors.toList());
        }
        String token = yiDuiJieTokenService.getYiDuiJieToken();
        boolean success = true;
        List<String> thirdMappingIdList = Lists.newArrayList();
        for (AccountMappingReqDTO mappingReq : accountMappingReqList) {
            String srcName = !ObjectUtils.isEmpty(mappingReq.getSuperMappingField())
                ? (mappingReq.getFinanceDepartment() + ";" + mappingReq.getFeeType() + ";" + mappingReq.getSuperMappingField())
                : !ObjectUtils.isEmpty(mappingReq.getSuperMappingField1()) ? (mappingReq.getFinanceDepartment() + ";" + mappingReq.getFeeType() + ";;" + mappingReq.getSuperMappingField1())
                : (mappingReq.getFinanceDepartment() + ";" + mappingReq.getFeeType());
            YiDuiJieMappingReq apiReq = YiDuiJieMappingReq.builder()
                .srcName(srcName)
                .destName(mappingReq.getAccount()).build();
            YiDuiJieUpsertMappingResp mappingResp = yiDuiJieMappingApi.upsertMappings(token, yiDuiJieConf.getAppId(), MappingType.account.getValue(), apiReq);
            if (mappingResp == null || !mappingResp.success()) {
                success = false;
            } else {
                String id = mappingResp.getId();
                if (!ObjectUtils.isEmpty(id)) {
                    thirdMappingIdList.add(id);
                }
            }
        }
        if (!success) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.MAPPING_ACCOUNT_ERROR)));
        }
        return thirdMappingIdList;
    }

    @Override
    public void updateMappingAccount(String companyId, List<AccountMappingReqDTO> accountMappingReqList) {
        if (ObjectUtils.isEmpty(accountMappingReqList)) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.MAPPING_ACCOUNT_UPDATE_ERROR)));
        }
        checkReqParam(accountMappingReqList, AccountMappingReqDTO.UpdateAccountGroup.class);
        YiDuiJieConf yiDuiJieConf = yiDuiJieConfDao.getByCompanyId(companyId);
        if (yiDuiJieConf == null) {
            return;
        }
        String token = yiDuiJieTokenService.getYiDuiJieToken();
        boolean success = true;
        for (AccountMappingReqDTO mappingReq : accountMappingReqList) {
            String srcName = !ObjectUtils.isEmpty(mappingReq.getSuperMappingField())
                ? (mappingReq.getFinanceDepartment() + ";" + mappingReq.getFeeType() + ";" + mappingReq.getSuperMappingField())
                : !ObjectUtils.isEmpty(mappingReq.getSuperMappingField1()) ? (mappingReq.getFinanceDepartment() + ";" + mappingReq.getFeeType() + ";;" + mappingReq.getSuperMappingField1())
                : (mappingReq.getFinanceDepartment() + ";" + mappingReq.getFeeType());
            YiDuiJieMappingReq apiReq = YiDuiJieMappingReq.builder()
                .id(mappingReq.getId())
                .srcName(srcName)
                .destName(mappingReq.getAccount()).build();
            YiDuiJieUpsertMappingResp mappingResp = yiDuiJieMappingApi.upsertMappings(token, yiDuiJieConf.getAppId(), MappingType.account.getValue(), apiReq);
            if (mappingResp == null || !mappingResp.success()) {
                success = false;
            }
        }
        if (!success) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.MAPPING_ACCOUNT_UPDATE_ERROR)));
        }
    }

    @Override
    public void deleteMappingAccount(String companyId, List<String> thirdMappingIdList) {
        if (ObjectUtils.isEmpty(thirdMappingIdList)) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.MAPPING_ACCOUNT_DELETE_ERROR)));
        }
        YiDuiJieConf yiDuiJieConf = yiDuiJieConfDao.getByCompanyId(companyId);
        if (yiDuiJieConf == null) {
            return;
        }
        String token = yiDuiJieTokenService.getYiDuiJieToken();
        YiDuiJieBaseResp yiDuiJieBaseResp = yiDuiJieMappingApi.deleteMappings(token, yiDuiJieConf.getAppId(), MappingType.account.getValue(), thirdMappingIdList);
        if (yiDuiJieBaseResp == null || !yiDuiJieBaseResp.success()) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.MAPPING_ACCOUNT_DELETE_ERROR)));
        }
    }

    @Override
    public void mappingDepartment(String companyId, List<DepartmentMappingReqDTO> departmentMappingReqList) {
        if (ObjectUtils.isEmpty(departmentMappingReqList)) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.MAPPING_DEPT_ERROR)));
        }
        checkReqParam(departmentMappingReqList);
        YiDuiJieConf yiDuiJieConf = yiDuiJieConfDao.getByCompanyId(companyId);
        if (yiDuiJieConf == null) {
            return;
        }
        String token = yiDuiJieTokenService.getYiDuiJieToken();
        boolean success = true;
        for (DepartmentMappingReqDTO mappingReq : departmentMappingReqList) {
            YiDuiJieMappingReq apiReq = YiDuiJieMappingReq.builder()
                .srcName(mappingReq.getBusinessDepartment())
                .destName(mappingReq.getFinanceDepartment()).build();
            YiDuiJieBaseResp mappingResp = yiDuiJieMappingApi.mappings(token, yiDuiJieConf.getAppId(), MappingType.department.getValue(), apiReq);
            if (mappingResp == null || !mappingResp.success()) {
                success = false;
            }
        }
        if (!success) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.MAPPING_DEPT_ERROR)));
        }
    }

    @Override
    public List<String> addMappingDepartment(String companyId, List<DepartmentMappingReqDTO> departmentMappingReqList) {
        if (ObjectUtils.isEmpty(departmentMappingReqList)) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.MAPPING_DEPT_ERROR)));
        }
        checkReqParam(departmentMappingReqList);
        YiDuiJieConf yiDuiJieConf = yiDuiJieConfDao.getByCompanyId(companyId);
        if (yiDuiJieConf == null) {
            return IntStream.range(0, departmentMappingReqList.size()).mapToObj(i -> RandomUtils.bsonId()).collect(Collectors.toList());
        }
        String token = yiDuiJieTokenService.getYiDuiJieToken();
        boolean success = true;
        List<String> thirdMappingIdList = Lists.newArrayList();
        for (DepartmentMappingReqDTO mappingReq : departmentMappingReqList) {
            YiDuiJieMappingReq apiReq = YiDuiJieMappingReq.builder()
                .srcName(mappingReq.getBusinessDepartment())
                .destName(mappingReq.getFinanceDepartment()).build();
            YiDuiJieUpsertMappingResp mappingResp = yiDuiJieMappingApi.upsertMappings(token, yiDuiJieConf.getAppId(), MappingType.department.getValue(), apiReq);
            if (mappingResp == null || !mappingResp.success()) {
                success = false;
            } else {
                if (!ObjectUtils.isEmpty(mappingResp.getId())) {
                    thirdMappingIdList.add(mappingResp.getId());
                }
            }
        }
        if (!success) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.MAPPING_DEPT_ERROR)));
        }
        return thirdMappingIdList;
    }

    @Override
    public void updateMappingDepartment(String companyId, List<DepartmentMappingReqDTO> departmentMappingReqList) {
        if (ObjectUtils.isEmpty(departmentMappingReqList)) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.MAPPING_DEPT_UPDATE_ERROR)));
        }
        checkReqParam(departmentMappingReqList, DepartmentMappingReqDTO.UpdateDepartmentGroup.class);
        YiDuiJieConf yiDuiJieConf = yiDuiJieConfDao.getByCompanyId(companyId);
        if (yiDuiJieConf == null) {
            return;
        }
        String token = yiDuiJieTokenService.getYiDuiJieToken();
        boolean success = true;
        for (DepartmentMappingReqDTO mappingReq : departmentMappingReqList) {
            YiDuiJieMappingReq apiReq = YiDuiJieMappingReq.builder()
                .id(mappingReq.getId())
                .srcName(mappingReq.getBusinessDepartment())
                .destName(mappingReq.getFinanceDepartment()).build();
            YiDuiJieUpsertMappingResp mappingResp = yiDuiJieMappingApi.upsertMappings(token, yiDuiJieConf.getAppId(), MappingType.department.getValue(), apiReq);
            if (mappingResp == null || !mappingResp.success()) {
                success = false;
            }
        }
        if (!success) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.MAPPING_DEPT_UPDATE_ERROR)));
        }
    }

    @Override
    public void deleteMappingDepartment(String companyId, List<String> thirdMappingIdList) {
        if (ObjectUtils.isEmpty(thirdMappingIdList)) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.MAPPING_DEPT_DELETE_ERROR)));
        }
        YiDuiJieConf yiDuiJieConf = yiDuiJieConfDao.getByCompanyId(companyId);
        if (yiDuiJieConf == null) {
            return;
        }
        String token = yiDuiJieTokenService.getYiDuiJieToken();
        YiDuiJieBaseResp yiDuiJieBaseResp = yiDuiJieMappingApi.deleteMappings(token, yiDuiJieConf.getAppId(), MappingType.department.getValue(), thirdMappingIdList);
        if (yiDuiJieBaseResp == null || !yiDuiJieBaseResp.success()) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.MAPPING_DEPT_DELETE_ERROR)));
        }
    }

    @Override
    public List<String> addMappingProject(String companyId, List<ProjectMappingReqDTO> projectMappingReqList) {
        if (ObjectUtils.isEmpty(projectMappingReqList)) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.MAPPING_PROJECT_ADD_ERROR)));
        }
        checkReqParam(projectMappingReqList);
        YiDuiJieConf yiDuiJieConf = yiDuiJieConfDao.getByCompanyId(companyId);
        if (yiDuiJieConf == null) {
            return IntStream.range(0, projectMappingReqList.size()).mapToObj(i -> RandomUtils.bsonId()).collect(Collectors.toList());
        }
        String token = yiDuiJieTokenService.getYiDuiJieToken();
        boolean success = true;
        List<String> thirdMappingIdList = Lists.newArrayList();
        for (ProjectMappingReqDTO mappingReq : projectMappingReqList) {
            YiDuiJieMappingReq apiReq = YiDuiJieMappingReq.builder()
                .srcName(mappingReq.getBusinessProject())
                .destName(mappingReq.getFinanceProject()).build();
            YiDuiJieUpsertMappingResp mappingResp = yiDuiJieMappingApi.upsertMappings(token, yiDuiJieConf.getAppId(), MappingType.project.getValue(), apiReq);
            if (mappingResp == null || !mappingResp.success()) {
                success = false;
            } else {
                if (!ObjectUtils.isEmpty(mappingResp.getId())) {
                    thirdMappingIdList.add(mappingResp.getId());
                }
            }
        }
        if (!success) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.MAPPING_PROJECT_ADD_ERROR)));
        }
        return thirdMappingIdList;
    }

    @Override
    public void updateMappingProject(String companyId, List<ProjectMappingReqDTO> projectMappingReqList) {
        if (ObjectUtils.isEmpty(projectMappingReqList)) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.MAPPING_PROJECT_UPDATE_ERROR)));
        }
        checkReqParam(projectMappingReqList, ProjectMappingReqDTO.UpdateProjectGroup.class);
        YiDuiJieConf yiDuiJieConf = yiDuiJieConfDao.getByCompanyId(companyId);
        if (yiDuiJieConf == null) {
            return;
        }
        String token = yiDuiJieTokenService.getYiDuiJieToken();
        boolean success = true;
        for (ProjectMappingReqDTO mappingReq : projectMappingReqList) {
            YiDuiJieMappingReq apiReq = YiDuiJieMappingReq.builder()
                .id(mappingReq.getId())
                .srcName(mappingReq.getBusinessProject())
                .destName(mappingReq.getFinanceProject()).build();
            YiDuiJieUpsertMappingResp mappingResp = yiDuiJieMappingApi.upsertMappings(token, yiDuiJieConf.getAppId(), MappingType.project.getValue(), apiReq);
            if (mappingResp == null || !mappingResp.success()) {
                success = false;
            }
        }
        if (!success) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.MAPPING_PROJECT_UPDATE_ERROR)));
        }
    }

    @Override
    public void deleteMappingProject(String companyId, List<String> thirdMappingIdList) {
        if (ObjectUtils.isEmpty(thirdMappingIdList)) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.MAPPING_PROJECT_DELETE_ERROR)));
        }
        YiDuiJieConf yiDuiJieConf = yiDuiJieConfDao.getByCompanyId(companyId);
        if (yiDuiJieConf == null) {
            return;
        }
        String token = yiDuiJieTokenService.getYiDuiJieToken();
        YiDuiJieBaseResp yiDuiJieBaseResp = yiDuiJieMappingApi.deleteMappings(token, yiDuiJieConf.getAppId(), MappingType.project.getValue(), thirdMappingIdList);
        if (yiDuiJieBaseResp == null || !yiDuiJieBaseResp.success()) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.MAPPING_PROJECT_DELETE_ERROR)));
        }
    }

    @Override
    public List<MappingDTO> listMapping(String companyId, String mappingType) {
        YiDuiJieConf yiDuiJieConf = yiDuiJieConfDao.getByCompanyId(companyId);
        if (yiDuiJieConf == null) {
            return Lists.newArrayList();
        }
        String token = yiDuiJieTokenService.getYiDuiJieToken();
        String appInstanceId = yiDuiJieConf.getAppId();
        YiDuiJieListMappingResp listMappingResp = yiDuiJieMappingApi.listMappings(token, appInstanceId, mappingType, 1, 1);
        if (listMappingResp == null || !listMappingResp.success()) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.LIST_MAPPING_ERROR)));
        }
        //先查出来总条数 再穷尽所有条数
        int total = listMappingResp.getTotal();
        List<MappingDTO> mappingList = Lists.newArrayList();
        int pageIndex = 1;
        while (total > 0 && total != mappingList.size()) {
            YiDuiJieListMappingResp batchListMappingResp = yiDuiJieMappingApi.listMappings(token, appInstanceId, mappingType, pageIndex, 100);
            if (batchListMappingResp != null && batchListMappingResp.success()) {
                List<MappingDTO> batchMappingList = batchListMappingResp.getMappingList();
                if (!ObjectUtils.isEmpty(batchMappingList)) {
                    mappingList.addAll(batchMappingList);
                }
            }
            pageIndex++;
        }
        return mappingList;
    }
}
