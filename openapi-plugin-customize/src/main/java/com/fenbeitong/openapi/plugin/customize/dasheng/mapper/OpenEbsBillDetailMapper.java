package com.fenbeitong.openapi.plugin.customize.dasheng.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.customize.dasheng.entity.OpenEbsBillDetail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>Title: OpenEbsBillDetailMapper</p>
 * <p>Description: 51talk账单明细mapper</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/10/19 5:18 PM
 */
@Component
public interface OpenEbsBillDetailMapper extends OpenApiBaseMapper<OpenEbsBillDetail> {

    @Select("select `COA_COM` `coaCom`, `COA_CC` `coaCc`, `COA_ACC` `coaAcc`, SUM(`DEBIT`) `debit`,`DESP` desp from open_ebs_bill_detail where BILL_NO=#{billNo} group by `COA_COM`, `COA_CC`, `COA_ACC`,`DESP` ")
    List<OpenEbsBillDetail> listSumData(@Param("billNo") String billNo);
}
