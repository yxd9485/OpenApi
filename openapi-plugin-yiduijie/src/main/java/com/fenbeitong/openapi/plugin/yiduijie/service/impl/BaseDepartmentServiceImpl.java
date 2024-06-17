package com.fenbeitong.openapi.plugin.yiduijie.service.impl;

import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieApiResponseCode;
import com.fenbeitong.openapi.plugin.yiduijie.constant.MappingType;
import com.fenbeitong.openapi.plugin.yiduijie.dao.YiDuiJieConfDao;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieBaseResp;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieListDepartmentResp;
import com.fenbeitong.openapi.plugin.yiduijie.entity.YiDuiJieConf;
import com.fenbeitong.openapi.plugin.yiduijie.exception.OpenApiYiDuiJieException;
import com.fenbeitong.openapi.plugin.yiduijie.model.department.Department;
import com.fenbeitong.openapi.plugin.yiduijie.sdk.YiDuiJieDatasetApi;
import com.fenbeitong.openapi.plugin.yiduijie.service.IYiDuiJieTokenService;
import com.fenbeitong.openapi.plugin.yiduijie.service.department.IDepartmentService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;


/**
 * <p>Title: BaseDepartmentServiceImpl</p>
 * <p>Description: 同步记账部门服务实现</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/14 6:13 PM
 */
@ServiceAspect
@Service
public class BaseDepartmentServiceImpl extends BaseYiDuiJieService implements IDepartmentService {

    @Autowired
    private YiDuiJieConfDao yiDuiJieConfDao;

    @Autowired
    private IYiDuiJieTokenService yiDuiJieTokenService;

    @Autowired
    private YiDuiJieDatasetApi yiDuiJieDatasetApi;

    @Override
    public void upsertDepartment(String companyId, List<Department> departmentList) {
        if (ObjectUtils.isEmpty(departmentList)) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.UPSERT_DEPT_ERROR)));
        }
        checkReqParam(departmentList);
        YiDuiJieConf yiDuiJieConf = yiDuiJieConfDao.getByCompanyId(companyId);
        if (yiDuiJieConf == null) {
            return;
        }
        String token = yiDuiJieTokenService.getYiDuiJieToken();
        YiDuiJieBaseResp upsertDatasetResp = yiDuiJieDatasetApi.upsertDataset(token, yiDuiJieConf.getAppId(), MappingType.department.getValue(), JsonUtils.toJson(departmentList));
        if (upsertDatasetResp == null || !upsertDatasetResp.success()) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.UPSERT_DEPT_ERROR)));
        }
    }

    @Override
    public List<Department> listDepartment(String companyId) {
        YiDuiJieConf yiDuiJieConf = yiDuiJieConfDao.getByCompanyId(companyId);
        if (yiDuiJieConf == null) {
            return Lists.newArrayList();
        }
        String token = yiDuiJieTokenService.getYiDuiJieToken();
        YiDuiJieListDepartmentResp listDepartmentResp = yiDuiJieDatasetApi.listDeptDataset(token, yiDuiJieConf.getAppId());
        if (listDepartmentResp == null || !listDepartmentResp.success()) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.LIST_DEPT_ERROR)));
        }
        return ObjectUtils.isEmpty(listDepartmentResp.getDepartmentList()) ? Lists.newArrayList() : listDepartmentResp.getDepartmentList();
    }
}
