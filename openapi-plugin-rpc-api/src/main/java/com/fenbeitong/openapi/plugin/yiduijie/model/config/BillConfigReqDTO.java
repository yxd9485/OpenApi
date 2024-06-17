package com.fenbeitong.openapi.plugin.yiduijie.model.config;

import com.fenbeitong.openapi.plugin.yiduijie.constant.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * <p>Title: BillConfigReqDTO</p>
 * <p>Description: 账单配置请求参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/10 3:56 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BillConfigReqDTO implements Serializable {

    /**
     * 公司id
     */
    @NotBlank(message = "公司id[companyId]不可为空")
    private String companyId;

    /**
     * 人员选择
     * 1:预订人 2:实际使用人
     * @see EmployeeSource
     */
    private Integer employeeSource;

    /**
     * 部门选择 1:费用归属部门
     * @see DepartmentSource
     */
    private Integer departmentSource;

    /**
     * 项目费用固定核算部门列表
     */
    @Valid
    private List<BillConfigProjectDeptDTO> projectDeptList;

    /**
     * 服务单独核算
     * 0:服务不单独核算 1:服务单独核算
     * @see TreatmentOfFee
     */
    private Integer treatmentOfFee;

    /**
     * 服务费借方科目名称
     */
    private String feeCredit;

    /**
     * 服务费进项税科目映射
     */
    private String treatmentOfTaxFee;

    /**
     * 业务线进行税科目映射
     */
    private String treatmentOfTaxDefault;

    /**
     * 贷方科目映射
     */
    private String defaultCredit;

    /**
     * 凭证制单人
     */
    private String defaultmaker;

    /**
     * 企业账单进项税规则配置列表
     */
    @Valid
    private List<BillConfigTaxCalcDTO> taxCalcDtoList;

    /**
     * 分贝通在客户系统的供应商代码，当应付科目启用供应商辅助核算时会自动填上
     */
    private String supplierCode;

    /**
     * 1：外部人员不抵扣
     * 2：参与抵扣
     * @see ExternalPersonTaxReduce
     */
    private Integer externalPersonTaxReduce;

    /**
     * 不参与费用抵扣的部门，可以多个，用英文逗号分隔
     */
    private String departmentTaxReduce;

    /**
     * 不参与费用抵扣的项目，可以多个，用英文逗号分隔
     */
    private String projectTaxReduce;

    /**
     * 1:根据辅助核算自动合并 2:生成明细
     * @see MergingOfExpense
     */
    private Integer mergingOfExpense;

    /**
     * 1:按照业务线计算进项税 2:所有进项税合并到一行
     * @see MergingOfTax
     */
    private Integer mergingOfTax;
}
