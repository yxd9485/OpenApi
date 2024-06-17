package com.fenbeitong.openapi.plugin.wechat.eia.service.apply;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.support.apply.dao.ThirdApplyDefinitionDao;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.eia.dao.WeChatApplyDao;
import com.fenbeitong.openapi.plugin.wechat.eia.entity.WeChatApply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

/**
 * 企业微信审批相关service
 * Created by dave.hansins on 19/12/16.
 */
@ServiceAspect
@Service
public class WeChatEiaApprovolService {

    @Autowired
    WeChatApplyDao weChatApplyDao;
    @Autowired
    ThirdApplyDefinitionDao thirdApplyDefinitionDao;
    @Autowired
    PluginCorpDefinitionDao pluginCorpDefinitionDao;

    /**
     * 根据企业ID查询secret相关信息
     * @param corpId
     * @return
     */
    public WeChatApply getWeChatApplyInfoByCorpId(String corpId){
        if(ObjectUtils.isEmpty(corpId)){
            throw new OpenApiPluginException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_CORP_ID_NOT_NULL));
        }
        Example example = new Example(WeChatApply.class);
        example.createCriteria().andEqualTo("corpId",corpId);
        WeChatApply weChatApplyInfoByCorpId = weChatApplyDao.getByExample(example);
        if(ObjectUtils.isEmpty(weChatApplyInfoByCorpId)){//
            throw new OpenApiPluginException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_CORP_ID_NOT_EXIST));
        }
        return weChatApplyInfoByCorpId;
    }


    /**
     * 根据审批模板获取审批类型
     * @param processCode
     * @return
     */
    public ThirdApplyDefinition  getThirdApplyConfigByProcessCode(String processCode){
        ThirdApplyDefinition thirdApplyConfigByProcessCode = thirdApplyDefinitionDao.getThirdApplyConfigByProcessCode(processCode);
        return thirdApplyConfigByProcessCode;
    }


    /**
     * 根据企业ID获取企业企业插件信息
     * @param thirdCorpId
     * @return
     */
    public PluginCorpDefinition getPluginCorpDefinitionByThirdCorpId(String thirdCorpId){
    PluginCorpDefinition corpByCorpId = pluginCorpDefinitionDao.getCorpByThirdCorpId(thirdCorpId);
    return corpByCorpId;
}




}
