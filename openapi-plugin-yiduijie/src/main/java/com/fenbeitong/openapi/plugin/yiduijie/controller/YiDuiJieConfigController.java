package com.fenbeitong.openapi.plugin.yiduijie.controller;

import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieResponseUtils;
import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieResultEntity;
import com.fenbeitong.openapi.plugin.yiduijie.model.config.ConfigDTO;
import com.fenbeitong.openapi.plugin.yiduijie.service.config.IConfigService;
import com.google.common.collect.Maps;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>Title: YiDuiJieConfigController</p>
 * <p>Description: 易对接配置</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/13 11:48 AM
 */
@RestController
@RequestMapping("/yiduijie/config")
@Api(tags = "易对接配置", description = "易对接配置")
public class YiDuiJieConfigController {

    @Autowired
    @Qualifier("springConfigService")
    private IConfigService configService;

    /**
     * 设置配置
     *
     * @param companyId 公司id
     * @param config 配置信息
     * @return 操作结果
     */
    @RequestMapping("/setConfig/{company_id}")
    @ApiOperation(value = "1、配置参数", notes = "配置参数", httpMethod = "POST", response = YiDuiJieResultEntity.class, position = 1)
    public Object setConfig(@ApiParam(name = "company_id", value = "公司ID", required = true) @PathVariable("company_id") String companyId, @ApiParam(name = "config", value = "其他配置信息", required = true) @RequestBody Map<String, Object> config) {
        configService.setConfig(companyId, config);
        return YiDuiJieResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 设置凭证通知配置
     *
     * @param companyId  公司id
     * @param createType
     * @param taxType
     * @return 操作结果
     */
    @RequestMapping("/setVoucherCreateConfig/{company_id}")
    @ApiOperation(value = "2、配置凭证生成方式", notes = "配置凭证生成方式", httpMethod = "POST", response = YiDuiJieResultEntity.class, position = 2)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "company_id", value = "公司ID", required = true),
            @ApiImplicitParam(name = "create_type", value = "凭证生成方式:(1:直接对接系统;2:excel导出)", required = true),
            @ApiImplicitParam(name = "tax_type", value = "税金处理方式:(1:税金合并;2:税金拆分)", required = true)
    })
    public Object setVoucherCreateConfig(@PathVariable("company_id") String companyId, @RequestParam("create_type") int createType, @RequestParam("tax_type") int taxType) {
        configService.setCreateVoucherConfig(companyId, createType, taxType);
        return YiDuiJieResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 查询配置信息
     *
     * @param companyId 公司id
     * @return 配置信息列表
     */
    @RequestMapping("/listConfig/{company_id}")
    @ApiOperation(value = "3、查询配置信息", notes = "查询配置信息", httpMethod = "GET", response = YiDuiJieResultEntity.class, position = 3)
    public Object listConfig(@ApiParam(name = "company_id", value = "公司ID", required = true) @PathVariable("company_id") String companyId) {
        List<ConfigDTO> configList = configService.listConfig(companyId);
        return YiDuiJieResponseUtils.success(configList);
    }

    /**
     * 设置其他配置
     *
     * @param companyId 公司id
     * @param config
     * @return 操作结果
     */
    @RequestMapping("/setExtConfig/{company_id}")
    @ApiOperation(value = "4、配置其他参数", notes = "配置其他参数", httpMethod = "POST", response = YiDuiJieResultEntity.class, position = 4)
    public Object setVoucherCreateConfig(@ApiParam(name = "company_id", value = "公司ID", required = true) @PathVariable("company_id") String companyId, @ApiParam(name = "config", value = "其他配置信息", required = true) @RequestBody String config) {
        configService.setExtConfig(companyId, config);
        return YiDuiJieResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 查询其他配置信息
     *
     * @param companyId 公司id
     * @return 其他配置信息
     */
    @RequestMapping("/listExtConfig/{company_id}")
    @ApiOperation(value = "5、查询其他配置信息", notes = "查询其他配置信息", httpMethod = "GET", response = YiDuiJieResultEntity.class, position = 5)
    public Object listExtConfig(@ApiParam(name = "company_id", value = "公司ID", required = true) @PathVariable("company_id") String companyId) {
        String config = configService.listExtConfig(companyId);
        return YiDuiJieResponseUtils.success(config);
    }

    /**
     * 设置进项税(贷方)科目映射
     *
     * @param companyId   公司id
     * @param accountType 科目类型 2:进项税;3:贷方科目
     * @param accountName 科目名称
     * @return 操作结果
     */
    @RequestMapping("/setAccountConfig/{company_id}")
    @ApiOperation(value = "6、设置进项税(贷方)科目映射", notes = "设置进项税(贷方)科目映射", httpMethod = "POST", response = YiDuiJieResultEntity.class, position = 6)
    public Object setAccountConfig(@ApiParam(name = "company_id", value = "公司ID", required = true) @PathVariable("company_id") String companyId, @ApiParam(name = "account_type", value = "科目类型 2:进项税;3:贷方科目", required = true) @RequestParam("account_type") int accountType, @ApiParam(name = "account_name", value = "科目名称", required = true) @RequestParam("account_name") String accountName) {
        configService.setAccountConfig(companyId, accountType, accountName);
        return YiDuiJieResponseUtils.success(Maps.newHashMap());
    }


}
