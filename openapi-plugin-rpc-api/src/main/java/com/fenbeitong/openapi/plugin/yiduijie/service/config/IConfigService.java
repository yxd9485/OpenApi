package com.fenbeitong.openapi.plugin.yiduijie.service.config;

import com.fenbeitong.openapi.plugin.yiduijie.constant.VoucherCreateType;
import com.fenbeitong.openapi.plugin.yiduijie.constant.VoucherTaxType;
import com.fenbeitong.openapi.plugin.yiduijie.model.config.BillConfigReqDTO;
import com.fenbeitong.openapi.plugin.yiduijie.model.config.ConfigDTO;

import java.util.List;
import java.util.Map;

/**
 * <p>Title: IConfigService</p>
 * <p>Description: 配置服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/13 7:16 PM
 */
public interface IConfigService {


    /**
     * 配置
     *
     * @param companyId 公司id
     * @param configMap 配置参数
     */
    void setConfig(String companyId, Map<String, Object> configMap);

    /**
     * 凭证通用配置
     *
     * @param companyId  公司id
     * @param createType 生成方式 详见枚举 VoucherCreateType
     * @param taxType    税金合并方式 详见枚举 VoucherTaxType
     * @see VoucherCreateType
     * @see VoucherTaxType
     */
    void setCreateVoucherConfig(String companyId, int createType, int taxType);

    /**
     * 公司配置进项税或者贷方科目
     *
     * @param companyId   公司id
     * @param accountType 科目类型 2:进项税;3:贷方科目
     * @param accountName 科目名称
     */
    void setAccountConfig(String companyId, int accountType, String accountName);

    /**
     * 查询配置信息
     *
     * @param companyId 公司id
     * @return 配置信息列表
     */
    List<ConfigDTO> listConfig(String companyId);

    /**
     * 设置其他配置
     *
     * @param companyId 公司id
     * @param config    配置信息
     */
    void setExtConfig(String companyId, String config);

    /**
     * 查询其他配置
     *
     * @param companyId 公司id
     * @return 其他配置信息
     */
    String listExtConfig(String companyId);

    /**
     * 配置账单高级映射优先级
     *
     * @param companyId    公司id
     * @param mappingOrder 事由或者项目
     */
    void setMappingOrder(String companyId, String mappingOrder);

    /**
     * 设置账单参数
     */
    void setBillConfig(BillConfigReqDTO req);
}
