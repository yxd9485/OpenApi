package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.fenbeitong.finhub.common.utils.CheckUtils;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dao.DingtalkApplyDao;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingtalkApply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * <p>Title: DingtalkApplyService</p>
 * <p>Description: 钉钉配置服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/20 7:27 PM
 */
@ServiceAspect
@Service
public class DingtalkApplyServiceImpl {

    @Autowired
    private DingtalkApplyDao dingtalkApplyDao;

    /**
     * 根据流程编码获取审批单
     *
     * @param processCode 钉钉平台流程编码
     * @return DingtalkApply
     */
    public DingtalkApply getAppyByProcessCode(String processCode) {
        Example example = new Example(DingtalkApply.class);
        example.createCriteria().andEqualTo("processCode", processCode);
        return dingtalkApplyDao.getByExample(example);
    }

    /**
     * 根据流程编码和公司id，获取申请单注册信息
     *
     * @param processCode 钉钉平台流程编码
     * @param companyId   公司id
     * @return DingtalkApply
     */
    public DingtalkApply getApplyByCodeAndCompanyId(String processCode, String companyId) {
        Example example = new Example(DingtalkApply.class);
        example.createCriteria().andEqualTo("processCode", processCode)
            .andEqualTo("companyId", companyId);
        return dingtalkApplyDao.getByExample(example);
    }

    /**
     * 查询指定企业下的所有审批单
     *
     * @param companyId 分贝通平台企业ID
     * @return List<DingtalkApply>
     */
    public List<DingtalkApply> getApplyByCompanyId(String companyId) {
        CheckUtils.checkEmpty(companyId, "企业ID不能为空");
        Example example = new Example(DingtalkApply.class);
        example.createCriteria().andEqualTo("companyId", companyId);
        return dingtalkApplyDao.listByExample(example);
    }
}
