package com.fenbeitong.openapi.plugin.yunzhijia.handler;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseCode;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaOrgDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaOrgReqDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaResponse;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaCorpService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaOrgService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class YunzhijiaOrgHandler {
    @Autowired
    IYunzhijiaOrgService yunzhijiaOrgService;
    @Autowired
    IYunzhijiaCorpService yunzhijiaCorpService;

    /**
     * 根据企业ID查询插件注册信息
     *
     * @param corpId
     * @return
     */
    public PluginCorpDefinition getPluginCorpDefinitionByCorpId(String corpId) {
        //2.检查企业是否注册
        PluginCorpDefinition byCorpId = yunzhijiaCorpService.getByCorpId(corpId);
        if (ObjectUtils.isEmpty(byCorpId)) {
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_CORP_UN_REGIST)));
        }
        return byCorpId;
    }

    /**
     * 根据企业ID查询云之家部门详情
     *
     * @param corpId
     * @param dataId
     * @return
     */
    protected YunzhijiaResponse<List<YunzhijiaOrgDTO>> getYunzhijiaOrg(String corpId, String dataId) {
        //3.请求参数组装
        ArrayList<String> orgIdList = Lists.newArrayList();
        orgIdList.add(dataId);
        YunzhijiaOrgReqDTO build = YunzhijiaOrgReqDTO.builder()
                .eid(corpId)
                .type(0)
                .array(orgIdList)
                .build();
        //4.查询云之家部门详情
        YunzhijiaResponse<List<YunzhijiaOrgDTO>> yunzhijiaOrgDetail = yunzhijiaOrgService.getYunzhijiaOrgDetail(build);
        if (ObjectUtils.isEmpty(yunzhijiaOrgDetail) || yunzhijiaOrgDetail.getData().size() < 0) {
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_ORG_NULL)));
        }
        return yunzhijiaOrgDetail;
    }
}
