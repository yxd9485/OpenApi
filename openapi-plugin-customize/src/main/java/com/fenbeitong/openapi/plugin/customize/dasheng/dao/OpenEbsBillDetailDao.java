package com.fenbeitong.openapi.plugin.customize.dasheng.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.customize.dasheng.entity.OpenEbsBillDetail;
import com.fenbeitong.openapi.plugin.customize.dasheng.mapper.OpenEbsBillDetailMapper;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * <p>Title: OpenEbsBillDetailDao</p>
 * <p>Description: 51talk账单明细dao</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/10/19 5:17 PM
 */
@Component
public class OpenEbsBillDetailDao extends OpenApiBaseDao<OpenEbsBillDetail> {

    public List<OpenEbsBillDetail> list(String billNo) {
        Example example = new Example(OpenEbsBillDetail.class);
        example.selectProperties("id");
        example.createCriteria().andEqualTo("billNo",billNo);
        return listByExample(example);
    }

    public List<OpenEbsBillDetail> listSumData(String billNo){
        return ((OpenEbsBillDetailMapper)mapper).listSumData(billNo);
    }

}
