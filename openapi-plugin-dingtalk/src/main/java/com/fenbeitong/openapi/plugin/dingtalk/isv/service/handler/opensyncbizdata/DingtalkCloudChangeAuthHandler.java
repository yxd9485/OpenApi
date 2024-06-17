package com.fenbeitong.openapi.plugin.dingtalk.isv.service.handler.opensyncbizdata;

import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkCallbackTagConstant;
import com.fenbeitong.openapi.plugin.dingtalk.isv.constant.OpenSyncBizDataType;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizData;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitManagersDao;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnitManagers;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Map;

/**
 * @author lizhen
 * @date 2020/7/15
 */
@Component
@Slf4j
public class DingtalkCloudChangeAuthHandler implements IOpenSyncBizDataTaskHandler {
    @Autowired
    private IDingtalkIsvCompanyDefinitionService dingtalkIsvCompanyDefinitionService;
    @Autowired
    private OpenThirdOrgUnitManagersDao openThirdOrgUnitManagersDao;


    @Autowired
    IDingtalkIsvCompanyAuthService dingtalkIsvCompanyAuthService;

    @Override
    public OpenSyncBizDataType getTaskType() {
        return OpenSyncBizDataType.DINGTALK_ISV_CHANGE_AUTH;
    }

    @Override
    public TaskResult execute(OpenSyncBizData task) {
        String corpId = task.getCorpId();
        String bizData = task.getBizData();
        Map<String, Object> dataMap = JsonUtils.toObj(bizData, Map.class);
        String syncAction = StringUtils.obj2str(dataMap.get("syncAction"));
        // 删除部门主管中间表
        deleteDepManagers(corpId);
        // 企业授权套件
        if (DingtalkCallbackTagConstant.ORG_SUITE_AUTH.equals(syncAction)) {
            //不处理，在biz_type=17中新增授权信息
//            dingtalkIsvCompanyAuthService.companyAuth(corpId);
        } else if (DingtalkCallbackTagConstant.ORG_SUITE_CHANGE.equals(syncAction)) {
            //企业变更授权范围
//            dingtalkIsvCompanyAuthService.companyAuth(corpId);
            dingtalkIsvCompanyAuthService.updateCompanyAuth(corpId);
        } else if (DingtalkCallbackTagConstant.ORG_SUITE_RELIEVE.equals(syncAction)) {
            //表示企业解除授权
            dingtalkIsvCompanyAuthService.companyCancelAuth(corpId);
        }
        return TaskResult.SUCCESS;
    }

    private void deleteDepManagers(String corpId) {
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCorpId(corpId);
        if (!ObjectUtils.isEmpty(dingtalkIsvCompany) && !StringUtils.isBlank(dingtalkIsvCompany.getCompanyId())) {
            Example example = new Example(OpenThirdOrgUnitManagers.class);
            example.createCriteria().andEqualTo("companyId", dingtalkIsvCompany.getCompanyId());
            openThirdOrgUnitManagersDao.deleteByExample(example);
        }
    }

}
