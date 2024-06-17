package com.fenbeitong.openapi.plugin.customize.wawj.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.customize.wawj.entity.OpenWawjBillDetail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>Title: OpenWawjBillDetailMapper</p>
 * <p>Description: 我爱我家账单明细mapper</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/11/11 3:32 PM
 */
@Component
public interface OpenWawjBillDetailMapper extends OpenApiBaseMapper<OpenWawjBillDetail> {

    @Select(" select " +
            " `MD5VALUE` `md5Value`,`COMPANY_CODE` `companyCode`,`EMPLOYEE_CODE` `employeeCode`, " +
            " `INCORPORATED_COMPANY` `incorporatedCompany`, `ACCOUNT_COMPANY_CODE` `accountCompanyCode`," +
            " `LINE_DESCRIPTION` `lineDescription`,`EXPENSE_TYPE_CODE` `expenseTypeCode`," +
            " `EXPENSE_ITEM_CODE` `expenseItemCode`,sum(`REPORT_AMOUNT`) `reportAmount`," +
            " `SUMMARY_DEPT_CODE` `summaryDeptCode`, `DIMENSION2_CODE` `dimension2Code`," +
            " `DIMENSION3_CODE` `dimension3Code`, `DIMENSION4_CODE` `dimension4Code`" +
            " from open_wawj_bill_detail " +
            " where BILL_NO=#{billNo}" +
            " group by `MD5VALUE`,`COMPANY_CODE`,`EMPLOYEE_CODE`,`INCORPORATED_COMPANY`," +
            " `ACCOUNT_COMPANY_CODE`,`LINE_DESCRIPTION`, `EXPENSE_TYPE_CODE`, `EXPENSE_ITEM_CODE`, " +
            "`SUMMARY_DEPT_CODE`, `DIMENSION2_CODE`, `DIMENSION3_CODE`, `DIMENSION4_CODE` " +
            " order by `COMPANY_CODE`,`INCORPORATED_COMPANY`")
    List<OpenWawjBillDetail> listSumData(@Param("billNo") String billNo);
}
