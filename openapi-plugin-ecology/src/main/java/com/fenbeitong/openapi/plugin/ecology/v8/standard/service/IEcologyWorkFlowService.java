package com.fenbeitong.openapi.plugin.ecology.v8.standard.service;

import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflow;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: IEcologyWorkFlowService</p>
 * <p>Description: 泛微工作流服务接口</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/1 11:47 AM
 */
public interface IEcologyWorkFlowService {

    /**
     * 同步指定创建日期的审批流程
     *
     * @param configId   数据转换配置id
     * @param companyId  公司id
     * @param createDate 创建日期
     */
    void syncApply(Long configId, String companyId, Date createDate, Map<String, Integer> applyNameMapping);

    /**
     * 更新审批流程信息
     *
     * @param configId  数据转换配置id
     * @param companyId 公司id
     */
    void updateApply(Long configId, String companyId);

    /**
     * 关闭审批工作流
     *
     * @param companyId 公司id
     */
    void closeApply(String companyId);

    /**
     * 获取差旅审批
     *
     * @param companyId
     * @return
     */
    List<OpenEcologyWorkflow> getTripWorkflow(String companyId);

    /**
     * 创建审批
     *
     * @param companyId
     */
    void createApply(String companyId);

}
