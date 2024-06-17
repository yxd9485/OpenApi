package com.fenbeitong.openapi.plugin.yunzhijia.service.impl.apply;

import com.fenbeitong.openapi.plugin.support.apply.service.AbstractApplyService;
import com.fenbeitong.openapi.plugin.yunzhijia.dao.YunzhijiaApplyDao;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaAccessTokenReqDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaApplyEventDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.entity.YunzhijiaAddressList;
import com.fenbeitong.openapi.plugin.yunzhijia.entity.YunzhijiaApply;
import com.fenbeitong.openapi.plugin.yunzhijia.entity.YunzhijiaOrgUnit;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import tk.mybatis.mapper.entity.Example;

@ServiceAspect
@Service
public class YunzhijiaApplyServiceImpl extends AbstractApplyService implements IYunzhijiaApplyService {

    @Autowired
    YunzhijiaApplyDao yunzhijiaApplyDao;
    @Autowired
    YunzhijiaRemoteApplyService yunzhijiaRemoteApplyService;


    public YunzhijiaApply getYunzhijiaApplyByCorpId(String corpId) {
        Example example = new Example(YunzhijiaApply.class);
        example.createCriteria().andEqualTo("corpId", corpId);
        YunzhijiaApply byExample = yunzhijiaApplyDao.getByExample(example);
        return byExample;
    }

    @Override
    public YunzhijiaApplyEventDTO getYunzhijiaApplyDetail(YunzhijiaAccessTokenReqDTO yunzhijiaAccessTokenReqDTO, String formCodeId, String formInstId) {
        return yunzhijiaRemoteApplyService.getYunzhijiaRemoteApplyDetail(yunzhijiaAccessTokenReqDTO,formCodeId,formInstId);
    }

}
