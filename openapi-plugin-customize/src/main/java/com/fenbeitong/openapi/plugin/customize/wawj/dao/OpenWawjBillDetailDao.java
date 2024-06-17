package com.fenbeitong.openapi.plugin.customize.wawj.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.customize.wawj.entity.OpenWawjBillDetail;
import com.fenbeitong.openapi.plugin.customize.wawj.mapper.OpenWawjBillDetailMapper;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * <p>Title: OpenWawjBillDetailDao</p>
 * <p>Description: 我爱我家账单明细dao</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/11/11 3:29 PM
 */
@Component
public class OpenWawjBillDetailDao extends OpenApiBaseDao<OpenWawjBillDetail> {


    public List<OpenWawjBillDetail> listSummary(String billNo) {
        return ((OpenWawjBillDetailMapper) mapper).listSumData(billNo);
    }

    public List<OpenWawjBillDetail> listByMd5Value(String md5Value) {
        Example example = new Example(OpenWawjBillDetail.class);
        example.selectProperties("id");
        example.createCriteria().andEqualTo("md5Value", md5Value);
        return listByExample(example);
    }

    public List<OpenWawjBillDetail> listByBatchId(List<String> batchIdList) {
        Example example = new Example(OpenWawjBillDetail.class);
        example.createCriteria().andIn("batchId", batchIdList);
        return listByExample(example);
    }

    public List<OpenWawjBillDetail> listByBillNo(String billNo) {
        Example example = new Example(OpenWawjBillDetail.class);
        example.selectProperties("id,companyCode,accountCompanyCode,incorporatedCompany,expenseItemCode,expenseTypeCode,dimension2Code,dimension3Code,dimension4Code,lineDescription".split(","));
        example.createCriteria().andEqualTo("billNo", billNo);
        return listByExample(example);
    }
}
