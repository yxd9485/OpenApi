package com.fenbeitong.openapi.plugin.voucher.service;

import com.fenbeitong.openapi.plugin.voucher.dto.*;

import java.util.List;
import java.util.Map;

/**
 * <p>Title: IFinanceProjectService</p>
 * <p>Description: 财务配置</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/28 10:15 AM
 */
public interface IFinanceConfigService {

    /**
     * /saas_plus/finance/field/config/list
     * {"request_id":"0VZli5Wt4NKoBRg0","code":0,"type":0,"msg":"success","data":{"dept_first":1,"personnel":1,"service_fee":1,"outsider_deduction":1,"advanced_mapping_config":1}}
     */
    /**
     * 财务配置项
     *
     * @param companyId 公司id
     * @param token     用户token
     * @return 0:不单独核算;1:服务费单独核算
     */
    FinanceConfigDto financeConfig(String companyId, String token);


    /**
     * 查询高级映射是否开启
     * /saas_plus/finance/biz_debtor_course/query_super_mapping_config
     * {"request_id":"9YaxIrWIvn4sIGhD","code":0,"type":0,"msg":"success","data":{"advanced_mapping_config":1,"priority_match":2}}
     * @param companyId
     * @param token
     * @return
     */
    FinanceConfigDto querySuperMappingConfig(String companyId,String token);

    /**
     * 加载项目映射信息
     * <p>
     * /saas_plus/finance/costcenter/relation/list?pageSize=20&pageIndex=1&costCenterName=&costCenterCode=&financeCostCenterCode=&status=1
     *
     * @param companyId 公司id
     * @param token     操作人token
     * @return 项目映射列表
     */
    List<FinanceProjectMappingDto> listProjectMapping(String companyId, String token);

    /**
     * /saas_plus/finance/org_unit/list?page_index=1&page_size=20&finance_dept=&fbt_dept=&dept_state=1&finance_dept_code=
     * /**
     * 加载部门映射信息
     *
     * @param companyId 公司id
     * @param token     操作人token
     * @return 项目映射列表
     */
    List<FinanceDeptMappingDto> listDeptMapping(String companyId, String token);

    /**
     * /saas_plus/invoice/finance_course/list?courseName=&courseCode=&page=1&size=20
     * {"request_id":"sVi7IT1F3hw5S9sc","code":0,"type":0,"msg":"success","data":{"pageIndex":1,"pageSize":20,"total":81,"data":[{"id":"5faa535991b491d8ec1e672d","companyId":"5ef800bd23445f02927e3826","courseName":"4","courseCode":"43","employeeAccounting":true,"departmentAccounting":true,"projectAccounting":false,"supplierAccounting":false,"courseType":1,"state":1,"createTime":"2020-11-10 16:46:18","updateTime":"2020-11-10 16:46:18","used":1},{"id":"5fa1143e91b491a625489095","companyId":"5ef800bd23445f02927e3826","courseName":"2131","courseCode":"121211","employeeAccounting":true,"departmentAccounting":false,"projectAccounting":false,"supplierAccounting":false,"courseType":1,"state":1,"createTime":"2020-11-03 16:26:39","used":0},{"id":"5fa113cd91b491a625489071","companyId":"5ef800bd23445f02927e3826","courseName":"213","courseCode":"1212","employeeAccounting":true,"departmentAccounting":false,"projectAccounting":false,"supplierAccounting":false,"courseType":1,"state":1,"createTime":"2020-11-03 16:24:45","used":1},{"id":"5fa1130d91b491a62548900a","companyId":"5ef800bd23445f02927e3826","courseName":"455","courseCode":"5554","employeeAccounting":true,"departmentAccounting":true,"projectAccounting":true,"supplierAccounting":true,"courseType":1,"state":1,"createTime":"2020-11-03 16:21:34","updateTime":"2020-11-03 16:21:38","used":0},{"id":"5f432d470eaae134fceec53f","companyId":"5ef800bd23445f02927e3826","courseName":"研发支出/交通费/事由","courseCode":"6301/11/01","employeeAccounting":true,"departmentAccounting":true,"projectAccounting":true,"supplierAccounting":true,"courseType":1,"state":1,"createTime":"2020-08-24 11:00:23","updateTime":"2020-08-24 11:00:39","used":0},{"id":"5f432d470eaae134fceec540","companyId":"5ef800bd23445f02927e3826","courseName":"研发支出/招待费/事由","courseCode":"6301/11/02","employeeAccounting":true,"departmentAccounting":true,"projectAccounting":true,"supplierAccounting":true,"courseType":1,"state":1,"createTime":"2020-08-24 11:00:23","used":0},{"id":"5f432d470eaae134fceec541","companyId":"5ef800bd23445f02927e3826","courseName":"销售费用/交通费/事由","courseCode":"6301/11/03","employeeAccounting":true,"departmentAccounting":true,"projectAccounting":true,"supplierAccounting":true,"courseType":1,"state":1,"createTime":"2020-08-24 11:00:23","used":1},{"id":"5f432d470eaae134fceec542","companyId":"5ef800bd23445f02927e3826","courseName":"销售费用/招待费/事由","courseCode":"6301/11/04","employeeAccounting":true,"departmentAccounting":true,"projectAccounting":true,"supplierAccounting":true,"courseType":1,"state":1,"createTime":"2020-08-24 11:00:23","used":1},{"id":"5f432d470eaae134fceec543","companyId":"5ef800bd23445f02927e3826","courseName":"管理费用/交通费/事由","courseCode":"6301/11/05","employeeAccounting":true,"departmentAccounting":true,"projectAccounting":true,"supplierAccounting":true,"courseType":1,"state":1,"createTime":"2020-08-24 11:00:23","used":0},{"id":"5f432d470eaae134fceec544","companyId":"5ef800bd23445f02927e3826","courseName":"管理费用/招待费/事由","courseCode":"6301/11/06","employeeAccounting":true,"departmentAccounting":true,"projectAccounting":true,"supplierAccounting":true,"courseType":1,"state":1,"createTime":"2020-08-24 11:00:23","used":0},{"id":"5f432d470eaae134fceec545","companyId":"5ef800bd23445f02927e3826","courseName":"研发支出/差旅费/住宿/事由","courseCode":"6301/11/07","employeeAccounting":true,"departmentAccounting":true,"projectAccounting":true,"supplierAccounting":true,"courseType":1,"state":1,"createTime":"2020-08-24 11:00:23","used":0},{"id":"5f432d470eaae134fceec546","companyId":"5ef800bd23445f02927e3826","courseName":"研发支出/差旅费/交通/事由","courseCode":"6301/11/08","employeeAccounting":true,"departmentAccounting":true,"projectAccounting":true,"supplierAccounting":true,"courseType":1,"state":1,"createTime":"2020-08-24 11:00:23","used":0},{"id":"5f432d470eaae134fceec547","companyId":"5ef800bd23445f02927e3826","courseName":"销售费用/差旅费/交通/事由","courseCode":"6301/11/09","employeeAccounting":true,"departmentAccounting":true,"projectAccounting":true,"supplierAccounting":true,"courseType":1,"state":1,"createTime":"2020-08-24 11:00:23","used":0},{"id":"5f432d470eaae134fceec548","companyId":"5ef800bd23445f02927e3826","courseName":"销售费用/差旅费/住宿/事由","courseCode":"6301/11/10","employeeAccounting":true,"departmentAccounting":true,"projectAccounting":true,"supplierAccounting":true,"courseType":1,"state":1,"createTime":"2020-08-24 11:00:23","used":0},{"id":"5f432d470eaae134fceec549","companyId":"5ef800bd23445f02927e3826","courseName":"管理费用/差旅费/交通/事由","courseCode":"6301/11/11","employeeAccounting":true,"departmentAccounting":true,"projectAccounting":true,"supplierAccounting":true,"courseType":1,"state":1,"createTime":"2020-08-24 11:00:23","used":0},{"id":"5f432d470eaae134fceec54a","companyId":"5ef800bd23445f02927e3826","courseName":"管理费用/差旅费/住宿/事由","courseCode":"6301/11/12","employeeAccounting":true,"departmentAccounting":true,"projectAccounting":true,"supplierAccounting":true,"courseType":1,"state":1,"createTime":"2020-08-24 11:00:23","used":0},{"id":"5f432d470eaae134fceec54b","companyId":"5ef800bd23445f02927e3826","courseName":"研发支出/快递费/事由","courseCode":"6301/11/13","employeeAccounting":true,"departmentAccounting":true,"projectAccounting":true,"supplierAccounting":true,"courseType":1,"state":1,"createTime":"2020-08-24 11:00:23","used":0},{"id":"5f432d470eaae134fceec54c","companyId":"5ef800bd23445f02927e3826","courseName":"销售费用/快递费/事由","courseCode":"6301/11/14","employeeAccounting":true,"departmentAccounting":true,"projectAccounting":true,"supplierAccounting":true,"courseType":1,"state":1,"createTime":"2020-08-24 11:00:23","used":0},{"id":"5f432d470eaae134fceec54d","companyId":"5ef800bd23445f02927e3826","courseName":"管理费用/快递费/事由","courseCode":"6301/11/15","employeeAccounting":true,"departmentAccounting":true,"projectAccounting":true,"supplierAccounting":true,"courseType":1,"state":1,"createTime":"2020-08-24 11:00:23","used":0},{"id":"5f432d470eaae134fceec54e","companyId":"5ef800bd23445f02927e3826","courseName":"管理费用/福利费/事由","courseCode":"6301/11/16","employeeAccounting":true,"departmentAccounting":true,"projectAccounting":true,"supplierAccounting":true,"courseType":1,"state":1,"createTime":"2020-08-24 11:00:23","used":0}]}}
     */
    /**
     * 科目清单
     *
     * @param companyId 公司id
     * @param token     用户token
     * @return
     */
    List<FinanceCourseDto> listCourse(String companyId, String token);

    /**
     * /saas_plus/finance/biz_debtor_course/query
     *
     * {"request_id":"Ct9xVSOcu156NP1p","code":0,"type":0,"msg":"success","data":{"total":1,"dataList":[{"groupId":"5fb3925691b491f2329dda34","orgUnitName":"总经办","bizName":"国内机票","courseName":"4","fieldInfoName":"CWXM-002","fieldInfoType":2}]}}
     */
    /**
     * 账单业务线借方科目映射
     *
     * @param companyId 公司id
     * @param token     用户token
     * @return
     */
    List<FinanceBillBizDebtorCourseMappingDto> listBillBizDebtorCourseMapping(String companyId, String token);

    /**
     * /saas_plus/finance/debtor_course/query
     * {"request_id":"vIdNMwc7t7gXhkxO","code":0,"type":0,"msg":"success","data":{"total":3,"dataList":[{"groupId":"5fad00cf91b491871d1b7aba","orgUnitName":"总经办","costCategory":"通讯费","courseName":"销售费用/招待费/事由"},{"groupId":"5fa3b92691b49100b4442b9a","orgUnitName":"技术服务与运维部,营销中心","costCategory":"快递费21,会议费","courseName":"销售费用/交通费/事由"},{"groupId":"5fa3b91f91b49100b4442b97","orgUnitName":"总经办,研发中心,整机产品部","costCategory":"电话给他很一般用户不能开机能看见你GV就会比较会比较好变化报价会比较好看见你看","courseName":"213"}]}}
     */
    /**
     * 虚拟卡核销单借方科目映射
     *
     * @param companyId
     * @param token
     * @return
     */
    List<VirtualCardDebtorCourseMappingDto> listVirtualCardDebtorCourseMapping(String companyId, String token);

    /**
     * /saas_plus/finance/debtor_course/info
     * {"request_id":"5zPtBJzKufNj0TDO","code":0,"type":0,"msg":"success","data":{"courseType7":{"courseName":"服务费科目","courseId":"5f432a970eaae134fcee9ffb","courseCode":"7201.03"}}}
     */
    /**
     * 2:虚拟卡进项税映射
     * 3:虚拟卡贷方科目映射
     * 4:业务线进税项映射
     * 5:服务费进税项映射
     * 6:业务线贷方科目映射
     * 7:服务费借方科目映射
     *
     * @param companyId
     * @param token
     * @return
     */
    Map<String, FinanceCourseDto> getSingleCourseMapping(String companyId, String token);

    /**
     * /saas_plus/invoice/biz_taxes/list
     * {"request_id":"YcVZhn79UWPmK0va","code":0,"type":0,"msg":"success","data":[{"id":"5ef888120eaae1367a7c657f","companyId":"5ef800bd23445f02927e3826","invoiceType":"增值税专用发票","code":100,"isDeduction":true,"taxesComputeMode":1,"isUpdate":false,"state":1,"createTime":"2020-06-28 20:07:46"},{"id":"5ef888120eaae1367a7c6580","companyId":"5ef800bd23445f02927e3826","invoiceType":"增值税普通发票","code":101,"isDeduction":true,"taxesComputeMode":1,"isUpdate":false,"state":1,"createTime":"2020-06-28 20:07:46"},{"id":"5ef888120eaae1367a7c6581","companyId":"5ef800bd23445f02927e3826","invoiceType":"增值税电子普通发票","code":102,"isDeduction":true,"taxesComputeMode":1,"isUpdate":false,"state":1,"createTime":"2020-06-28 20:07:46"},{"id":"5ef888120eaae1367a7c6582","companyId":"5ef800bd23445f02927e3826","invoiceType":"增值税普通发票（卷式）","code":103,"isDeduction":true,"taxesComputeMode":1,"isUpdate":false,"state":1,"createTime":"2020-06-28 20:07:46"},{"id":"5ef888120eaae1367a7c6583","companyId":"5ef800bd23445f02927e3826","invoiceType":"机动车销售统一发票","code":104,"isDeduction":true,"taxesComputeMode":1,"isUpdate":false,"state":1,"createTime":"2020-06-28 20:07:46"},{"id":"5ef888120eaae1367a7c6584","companyId":"5ef800bd23445f02927e3826","invoiceType":"二手车销售统一发票","code":105,"isDeduction":true,"taxesComputeMode":1,"isUpdate":false,"state":1,"createTime":"2020-06-28 20:07:46"},{"id":"5ef888120eaae1367a7c6585","companyId":"5ef800bd23445f02927e3826","invoiceType":"定额发票","code":200,"isDeduction":false,"isUpdate":false,"state":1,"createTime":"2020-06-28 20:07:46"},{"id":"5ef888120eaae1367a7c6586","companyId":"5ef800bd23445f02927e3826","invoiceType":"机打发票","code":400,"isDeduction":false,"isUpdate":false,"state":1,"createTime":"2020-06-28 20:07:46"},{"id":"5ef888120eaae1367a7c6587","companyId":"5ef800bd23445f02927e3826","invoiceType":"出租车发票","code":500,"isDeduction":false,"isUpdate":false,"state":1,"createTime":"2020-06-28 20:07:46"},{"id":"5ef888120eaae1367a7c6588","companyId":"5ef800bd23445f02927e3826","invoiceType":"火车票","code":503,"isDeduction":true,"taxesComputeMode":2,"taxes":0.09,"isUpdate":true,"state":1,"createTime":"2020-06-28 20:07:46","updateTime":"2020-10-30 19:55:31"},{"id":"5ef888120eaae1367a7c6589","companyId":"5ef800bd23445f02927e3826","invoiceType":"客运汽车票","code":505,"isDeduction":true,"taxesComputeMode":2,"taxes":0.05,"isUpdate":true,"state":1,"createTime":"2020-06-28 20:07:46","updateTime":"2020-08-24 14:13:05"},{"id":"5ef888120eaae1367a7c658a","companyId":"5ef800bd23445f02927e3826","invoiceType":"航空运输电子客票行程单","code":506,"isDeduction":true,"taxesComputeMode":2,"taxes":0.09,"isUpdate":true,"state":1,"createTime":"2020-06-28 20:07:46","updateTime":"2020-08-24 14:16:47"},{"id":"5ef888120eaae1367a7c658b","companyId":"5ef800bd23445f02927e3826","invoiceType":"过路费发票","code":507,"isDeduction":true,"taxesComputeMode":2,"taxes":0.03,"isUpdate":true,"state":1,"createTime":"2020-06-28 20:07:46","updateTime":"2020-08-26 13:53:51"},{"id":"5ef888120eaae1367a7c658c","companyId":"5ef800bd23445f02927e3826","invoiceType":"船票","code":508,"isDeduction":true,"taxesComputeMode":2,"taxes":0.05,"isUpdate":true,"state":1,"createTime":"2020-06-28 20:07:46","updateTime":"2020-08-26 13:54:12"}]}
     */
    /**
     * 加载账单进项税规则配置
     *
     * @param companyId
     * @param token
     * @return
     */
    List<FinanceBillTaxRuleDto> listBillTaxRule(String companyId, String token);

    /**
     * /saas_plus/finance/org_cost/get
     * {"request_id":"SoL1zpXjWQMmNuJO","code":0,"type":0,"msg":"success","data":{"dept_list":[{"id":"5fb2351591b4918453b0659d","orgCostId":"5ef8132523445f02927e41f3","orgCostName":"售前解决&行业大客户部","orgCostInfo":"售前解决&行业大客户部"},{"id":"5fad00e691b491871d1b7ac1","orgCostId":"5ef8132723445f02927e4250","orgCostName":"销售运营部","orgCostInfo":"营销中心/销售运营部"},{"id":"5f435a550eaae134fcef5732","orgCostId":"5ef8132523445f02927e41ef","orgCostName":"营销中心/区域销售部/市场部","orgCostInfo":"营销中心/区域销售部/市场部"}],"cost_center_list":[{"id":"5fa3b98d91b49100b4442bc9","orgCostId":"5f71be3927f65f66d685edc1","orgCostName":"2020092871147","orgCostInfo":"2020092871147"},{"id":"5fa3b98d91b49100b4442bca","orgCostId":"5f71be3927f65f66d685edc9","orgCostName":"2020092801952","orgCostInfo":"2020092801952"},{"id":"5fad00e691b491871d1b7ac0","orgCostId":"5f71bf1d27f65f66d685eec8","orgCostName":"2020092847045","orgCostInfo":"2020092847045"},{"id":"5f435a550eaae134fcef5731","orgCostId":"5f3f6e3f23445f590d7cccdf","orgCostName":"景龙项目001","orgCostInfo":"jl002"},{"id":"5fa3b98d91b49100b4442bcb","orgCostId":"5f71bf1d27f65f66d685eec6","orgCostName":"2020092889951","orgCostInfo":"2020092889951"}]}}
     */
    /**
     * 获取不计税部门和项目配置详情
     *
     * @param companyId
     * @param token
     * @return
     */
    FinanceBillExcludeTaxDto getFinanceBillExcludeTaxDto(String companyId, String token);

    /**
     * /saas_plus/invoice/taxes/list
     * {"request_id":"skWHR5goH46AefMo","code":0,"type":0,"msg":"success","data":[{"id":"5ef888120eaae1367a7c657f","companyId":"5ef800bd23445f02927e3826","invoiceType":"增值税专用发票","code":100,"isDeduction":true,"taxesComputeMode":1,"isUpdate":false,"state":1,"createTime":"2020-06-28 20:07:46"},{"id":"5ef888120eaae1367a7c6580","companyId":"5ef800bd23445f02927e3826","invoiceType":"增值税普通发票","code":101,"isDeduction":true,"taxesComputeMode":1,"isUpdate":false,"state":1,"createTime":"2020-06-28 20:07:46"},{"id":"5ef888120eaae1367a7c6581","companyId":"5ef800bd23445f02927e3826","invoiceType":"增值税电子普通发票","code":102,"isDeduction":true,"taxesComputeMode":1,"isUpdate":false,"state":1,"createTime":"2020-06-28 20:07:46"},{"id":"5ef888120eaae1367a7c6582","companyId":"5ef800bd23445f02927e3826","invoiceType":"增值税普通发票（卷式）","code":103,"isDeduction":true,"taxesComputeMode":1,"isUpdate":false,"state":1,"createTime":"2020-06-28 20:07:46"},{"id":"5ef888120eaae1367a7c6583","companyId":"5ef800bd23445f02927e3826","invoiceType":"机动车销售统一发票","code":104,"isDeduction":true,"taxesComputeMode":1,"isUpdate":false,"state":1,"createTime":"2020-06-28 20:07:46"},{"id":"5ef888120eaae1367a7c6584","companyId":"5ef800bd23445f02927e3826","invoiceType":"二手车销售统一发票","code":105,"isDeduction":true,"taxesComputeMode":1,"isUpdate":false,"state":1,"createTime":"2020-06-28 20:07:46"},{"id":"5ef888120eaae1367a7c6585","companyId":"5ef800bd23445f02927e3826","invoiceType":"定额发票","code":200,"isDeduction":false,"isUpdate":false,"state":1,"createTime":"2020-06-28 20:07:46"},{"id":"5ef888120eaae1367a7c6586","companyId":"5ef800bd23445f02927e3826","invoiceType":"机打发票","code":400,"isDeduction":false,"isUpdate":false,"state":1,"createTime":"2020-06-28 20:07:46"},{"id":"5ef888120eaae1367a7c6587","companyId":"5ef800bd23445f02927e3826","invoiceType":"出租车发票","code":500,"isDeduction":false,"isUpdate":false,"state":1,"createTime":"2020-06-28 20:07:46"},{"id":"5ef888120eaae1367a7c6588","companyId":"5ef800bd23445f02927e3826","invoiceType":"火车票","code":503,"isDeduction":true,"taxesComputeMode":2,"taxes":0.09,"isUpdate":true,"state":1,"createTime":"2020-06-28 20:07:46","updateTime":"2020-10-30 19:55:31"},{"id":"5ef888120eaae1367a7c6589","companyId":"5ef800bd23445f02927e3826","invoiceType":"客运汽车票","code":505,"isDeduction":true,"taxesComputeMode":2,"taxes":0.05,"isUpdate":true,"state":1,"createTime":"2020-06-28 20:07:46","updateTime":"2020-08-24 14:13:05"},{"id":"5ef888120eaae1367a7c658a","companyId":"5ef800bd23445f02927e3826","invoiceType":"航空运输电子客票行程单","code":506,"isDeduction":true,"taxesComputeMode":2,"taxes":0.09,"isUpdate":true,"state":1,"createTime":"2020-06-28 20:07:46","updateTime":"2020-08-24 14:16:47"},{"id":"5ef888120eaae1367a7c658b","companyId":"5ef800bd23445f02927e3826","invoiceType":"过路费发票","code":507,"isDeduction":true,"taxesComputeMode":2,"taxes":0.03,"isUpdate":true,"state":1,"createTime":"2020-06-28 20:07:46","updateTime":"2020-08-26 13:53:51"},{"id":"5ef888120eaae1367a7c658c","companyId":"5ef800bd23445f02927e3826","invoiceType":"船票","code":508,"isDeduction":true,"taxesComputeMode":2,"taxes":0.05,"isUpdate":true,"state":1,"createTime":"2020-06-28 20:07:46","updateTime":"2020-08-26 13:54:12"}]}
     */
    /**
     * 虚拟卡发票税率配置
     *
     * @param companyId
     * @param token
     * @return
     */
    List<VirtualCardTaxRateDto> listVirtualCardTaxRate(String companyId, String token);

    /**
     * 虚拟卡抵扣类型配置
     *
     * @param companyId
     * @param token
     * @return
     */
    List<VirtualCardDeductionTypeDto> listVirtualCardDeductionType(String companyId, String token);

    /**
     * 凭证管理通用配置
     *
     * @param companyId
     * @param token
     * @return
     */
    FinanceVoucherManageDto financeVoucherManage(String companyId, String token);
}
