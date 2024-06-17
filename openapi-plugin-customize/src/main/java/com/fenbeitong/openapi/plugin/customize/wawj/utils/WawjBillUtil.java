package com.fenbeitong.openapi.plugin.customize.wawj.utils;

import com.fenbeitong.openapi.plugin.customize.wawj.entity.OpenWawjBillDetail;
import com.fenbeitong.openapi.plugin.support.util.SignTool;
import com.google.common.collect.Lists;

/**
 * <p>Title: WawjBillUtil</p>
 * <p>Description: 我爱我家账单工具类</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/4 12:01 PM
 */
public class WawjBillUtil {

    public static void setMd5Value(OpenWawjBillDetail billDetail) {
        String companyCode = billDetail.getCompanyCode();
        String accountCompanyCode = billDetail.getAccountCompanyCode();
        String incorporatedCompany = billDetail.getIncorporatedCompany();
        String expenseItemCode = billDetail.getExpenseItemCode();
        String expenseTypeCode = billDetail.getExpenseTypeCode();
        String dimension2Code = billDetail.getDimension2Code();
        String dimension3Code = billDetail.getDimension3Code();
        String dimension4Code = billDetail.getDimension4Code();
        String lineDescription = billDetail.getLineDescription();
        String content = String.join("-", Lists.newArrayList(companyCode, accountCompanyCode, incorporatedCompany, expenseItemCode, expenseTypeCode, dimension2Code, dimension3Code, dimension4Code, lineDescription));
        String md5Value = SignTool.md5(content);
        billDetail.setMd5Value(md5Value);
    }
}
