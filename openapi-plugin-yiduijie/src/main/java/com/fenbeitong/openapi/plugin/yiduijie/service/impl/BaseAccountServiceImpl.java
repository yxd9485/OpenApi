package com.fenbeitong.openapi.plugin.yiduijie.service.impl;

import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieApiResponseCode;
import com.fenbeitong.openapi.plugin.yiduijie.constant.MappingType;
import com.fenbeitong.openapi.plugin.yiduijie.dao.YiDuiJieConfDao;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieBaseResp;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieListAccountResp;
import com.fenbeitong.openapi.plugin.yiduijie.entity.YiDuiJieConf;
import com.fenbeitong.openapi.plugin.yiduijie.exception.OpenApiYiDuiJieException;
import com.fenbeitong.openapi.plugin.yiduijie.model.account.Account;
import com.fenbeitong.openapi.plugin.yiduijie.sdk.YiDuiJieDatasetApi;
import com.fenbeitong.openapi.plugin.yiduijie.service.IYiDuiJieTokenService;
import com.fenbeitong.openapi.plugin.yiduijie.service.account.IAccountService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * <p>Title: BaseAccountServiceImpl</p>
 * <p>Description: 科目服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/14 4:19 PM
 */
@ServiceAspect
@Service
public class BaseAccountServiceImpl extends BaseYiDuiJieService implements IAccountService {

    @Autowired
    private YiDuiJieConfDao yiDuiJieConfDao;

    @Autowired
    private IYiDuiJieTokenService yiDuiJieTokenService;

    @Autowired
    private YiDuiJieDatasetApi yiDuiJieDatasetApi;

    @Override
    public void upsertAccount(String companyId, List<Account> accountList) {
        if (ObjectUtils.isEmpty(accountList)) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.UPSERT_ACCOUNT_ERROR)));
        }
        checkReqParam(accountList);
        YiDuiJieConf yiDuiJieConf = yiDuiJieConfDao.getByCompanyId(companyId);
        if (yiDuiJieConf == null) {
            return;
        }
        String token = yiDuiJieTokenService.getYiDuiJieToken();
        YiDuiJieBaseResp upsertDatasetResp = yiDuiJieDatasetApi.upsertDataset(token, yiDuiJieConf.getAppId(), MappingType.account.getValue(), JsonUtils.toJson(accountList));
        if (upsertDatasetResp == null || !upsertDatasetResp.success()) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.UPSERT_ACCOUNT_ERROR)));
        }
    }

    @Override
    public List<Account> listAccount(String companyId) {
        YiDuiJieConf yiDuiJieConf = yiDuiJieConfDao.getByCompanyId(companyId);
        if (yiDuiJieConf == null) {
            return Lists.newArrayList();
        }
        String token = yiDuiJieTokenService.getYiDuiJieToken();
        YiDuiJieListAccountResp listAccountResp = yiDuiJieDatasetApi.listAccountDataset(token, yiDuiJieConf.getAppId());
        if (listAccountResp == null || !listAccountResp.success()) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.LIST_ACCOUNT_ERROR)));
        }
        return ObjectUtils.isEmpty(listAccountResp.getAccountList()) ? Lists.newArrayList() : listAccountResp.getAccountList();
    }
}
