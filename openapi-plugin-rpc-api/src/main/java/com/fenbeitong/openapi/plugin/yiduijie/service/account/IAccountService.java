package com.fenbeitong.openapi.plugin.yiduijie.service.account;

import com.fenbeitong.openapi.plugin.yiduijie.model.account.Account;

import java.util.List;

/**
 * <p>Title: IAccountService</p>
 * <p>Description: 科目服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 3:09 PM
 */
public interface IAccountService {

    /**
     * 同步科目
     *
     * @param companyId   公司id
     * @param accountList 科目列表
     */
    void upsertAccount(String companyId, List<Account> accountList);

    /**
     * 公司科目列表
     *
     * @param companyId 公司id
     * @return 科目列表
     */
    List<Account> listAccount(String companyId);
}
