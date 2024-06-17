package com.fenbeitong.openapi.plugin.yiduijie.controller;

import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieResponseUtils;
import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieResultEntity;
import com.fenbeitong.openapi.plugin.yiduijie.constant.MappingType;
import com.fenbeitong.openapi.plugin.yiduijie.model.account.Account;
import com.fenbeitong.openapi.plugin.yiduijie.model.mapping.AccountMappingReqDTO;
import com.fenbeitong.openapi.plugin.yiduijie.model.mapping.MappingDTO;
import com.fenbeitong.openapi.plugin.yiduijie.service.account.IAccountService;
import com.fenbeitong.openapi.plugin.yiduijie.service.mapping.IMappingService;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Title: YiDuiJieAccountController</p>
 * <p>Description: 易对接科目</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/13 11:48 AM
 */
@RestController
@RequestMapping("/yiduijie/account")
@Api(tags = "易对接科目", description = "易对接科目")
public class YiDuiJieAccountController {

    @Autowired
    @Qualifier("springAccountService")
    private IAccountService accountService;

    @Autowired
    @Qualifier("springMappingService")
    private IMappingService mappingService;

    /**
     * 同步科目
     *
     * @param companyId   公司id
     * @param accountList 科目列表
     * @return 操作结果
     */
    @RequestMapping("/upsertAccount/{company_id}")
    @ApiOperation(value = "1、同步科目", notes = "同步科目", httpMethod = "POST", response = YiDuiJieResultEntity.class, position = 1)
    public Object upsertAccount(@ApiParam(name = "company_id", value = "公司ID", required = true) @PathVariable("company_id") String companyId, @RequestBody List<Account> accountList) {
        accountService.upsertAccount(companyId, accountList);
        return YiDuiJieResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 科目列表
     *
     * @param companyId 公司id
     * @return 科目列表
     */
    @RequestMapping("/listAccount/{company_id}")
    @ApiOperation(value = "2、科目列表", notes = "2、科目列表", httpMethod = "GET", response = YiDuiJieResultEntity.class, position = 2)
    public Object listAccount(@ApiParam(name = "company_id", value = "公司ID", required = true) @PathVariable("company_id") String companyId) {
        List<Account> accountList = accountService.listAccount(companyId);
        return YiDuiJieResponseUtils.success(accountList);
    }

    /**
     * 增加科目映射
     *
     * @param companyId             公司id
     * @param accountMappingReqList 科目映射信息
     * @return 操作结果
     */
    @RequestMapping("/addMappingAccount/{company_id}")
    @ApiOperation(value = "3、增加科目映射", notes = "增加科目映射", httpMethod = "POST", response = YiDuiJieResultEntity.class, position = 3)
    public Object addMappingAccount(@ApiParam(name = "company_id", value = "公司ID", required = true) @PathVariable("company_id") String companyId, @RequestBody List<AccountMappingReqDTO> accountMappingReqList) {
        mappingService.addMappingAccount(companyId, accountMappingReqList);
        return YiDuiJieResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 更新科目映射
     *
     * @param companyId             公司id
     * @param accountMappingReqList 科目映射信息
     * @return 操作结果
     */
    @RequestMapping("/updateMappingAccount/{company_id}")
    @ApiOperation(value = "4、更新科目映射", notes = "更新科目映射", httpMethod = "POST", response = YiDuiJieResultEntity.class, position = 4)
    public Object updateMappingAccount(@ApiParam(name = "company_id", value = "公司ID", required = true) @PathVariable("company_id") String companyId, @RequestBody List<AccountMappingReqDTO> accountMappingReqList) {
        mappingService.updateMappingAccount(companyId, accountMappingReqList);
        return YiDuiJieResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 删除科目映射
     *
     * @param companyId          公司id
     * @param thirdMappingIdList 科目映射id信息
     * @return 操作结果
     */
    @RequestMapping("/deleteMappingAccount/{company_id}")
    @ApiOperation(value = "5、删除科目映射", notes = "删除科目映射", httpMethod = "POST", response = YiDuiJieResultEntity.class, position = 5)
    public Object deleteMappingAccount(@ApiParam(name = "company_id", value = "公司ID", required = true) @PathVariable("company_id") String companyId, @RequestBody List<String> thirdMappingIdList) {
        mappingService.deleteMappingAccount(companyId, thirdMappingIdList);
        return YiDuiJieResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 科目映射列表
     *
     * @param companyId 公司id
     * @return 科目映射列表
     */
    @RequestMapping("/listAccountMapping/{company_id}")
    @ApiOperation(value = "6、科目映射列表", notes = "科目映射列表", httpMethod = "GET", response = YiDuiJieResultEntity.class, position = 6)
    public Object listAccountMapping(@ApiParam(name = "company_id", value = "公司ID", required = true) @PathVariable("company_id") String companyId) {
        List<MappingDTO> mappingList = mappingService.listMapping(companyId, MappingType.account.getValue());
        return YiDuiJieResponseUtils.success(mappingList);
    }

    /**
     * 清空科目映射列表
     *
     * @param companyId 公司id
     * @return 操作结果
     */
    @RequestMapping("/clearAccountMapping/{company_id}")
    @ApiOperation(value = "7、清空科目映射列表", notes = "清空科目映射列表", httpMethod = "GET", response = YiDuiJieResultEntity.class, position = 7)
    public Object clearAccountMapping(@ApiParam(name = "company_id", value = "公司ID", required = true) @PathVariable("company_id") String companyId) {
        List<MappingDTO> mappingList = mappingService.listMapping(companyId, MappingType.account.getValue());
        mappingService.deleteMappingAccount(companyId, mappingList.stream().map(MappingDTO::getId).collect(Collectors.toList()));
        return YiDuiJieResponseUtils.success(mappingList);
    }

}
