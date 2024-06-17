package com.fenbeitong.openapi.plugin.customize.wawj.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.customize.wawj.entity.OpenWawjFuliApply;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * <p>Title: OpenWawjFuliApplyDao</p>
 * <p>Description: 我爱我家福利申请</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/10/27 12:06 PM
 */
@Component
public class OpenWawjFuliApplyDao extends OpenApiBaseDao<OpenWawjFuliApply> {

    public OpenWawjFuliApply getOldApply(String companyId, String thirdUserId, String applyId, Date workDate) {
        Example example = new Example(OpenWawjFuliApply.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("companyId", companyId);
        criteria.andEqualTo("workDate", workDate);
        criteria.andEqualTo("thirdUserId", thirdUserId);
        criteria.andEqualTo("applyId", applyId);
        return getByExample(example);
    }

    public List<OpenWawjFuliApply> listApply(String companyId, Integer status) {
        Example example = new Example(OpenWawjFuliApply.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("companyId", companyId);
        criteria.andEqualTo("status", status);
        return listByExample(example);
    }

    public List<OpenWawjFuliApply> listCloseableApply(String companyId, Integer status, Date workDate) {
        Example example = new Example(OpenWawjFuliApply.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("companyId", companyId);
        criteria.andLessThan("workDate", workDate);
        criteria.andEqualTo("status", status);
        return listByExample(example);
    }
}
