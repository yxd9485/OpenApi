package com.fenbeitong.openapi.plugin.feishu.isv.service;

import com.fenbeitong.finhub.auth.entity.base.UserComInfoVO;
import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseCode;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeishuJsapiSignRespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuException;
import com.fenbeitong.openapi.plugin.feishu.common.service.AbstractFeiShuJsapiService;
import com.fenbeitong.openapi.plugin.feishu.common.util.AbstractFeiShuHttpUtils;
import com.fenbeitong.openapi.plugin.feishu.common.util.DdConfigSign;
import com.fenbeitong.openapi.plugin.feishu.isv.entity.FeishuIsvCompany;
import com.fenbeitong.openapi.plugin.feishu.isv.util.FeiShuIsvHttpUtils;
import com.fenbeitong.openapi.plugin.support.common.dto.UserCenterResponse;
import com.fenbeitong.openapi.plugin.support.employee.dto.UcEmployeeSelfInfoResponse;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by xiaohai on 2021/11/17.
 */
@Slf4j
@ServiceAspect
@Service
public class FeiShuIsvjsapiService extends AbstractFeiShuJsapiService{

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private FeiShuIsvCompanyDefinitionService feiShuIsvCompanyService;

    @Autowired
    private FeiShuIsvHttpUtils feiShuIsvHttpUtils;

    @Value("${feishu.isv.appId}")
    private String appId;

    @Override
    protected AbstractFeiShuHttpUtils getFeiShuHttpUtils() {
        return feiShuIsvHttpUtils;
    }

    public FeishuJsapiSignRespDTO getJsapiSign(UserComInfoVO user, String data) {
        Map<String, Object> map = JsonUtils.toObj(data, Map.class);
        String url = StringUtils.obj2str(map.get("url"));
        if (user == null) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.WEB_LOGIN_VALIDATE_FAILED);
        }
        String companyId = user.getCompany_id();
        //查询企业授权信息
        FeishuIsvCompany feiShuIsvCompany = feiShuIsvCompanyService.getFeiShuIsvCompanyByCompanyId(companyId);
        if (feiShuIsvCompany == null) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_COMPANY_UNDEFINED);
        }
        String corpId = feiShuIsvCompany.getCorpId();
        String jsapiTicket = getJsapiTicket(corpId);
        String noncestr = DdConfigSign.getRandomStr(32);
        Long timeStamp = System.currentTimeMillis();
        String sign = null;
        try {
            sign = DdConfigSign.sign(jsapiTicket, noncestr, timeStamp, url);
        } catch (Exception e) {
            log.error("签名失败", e);
            throw new OpenApiFeiShuException(NumericUtils.obj2int(FeiShuResponseCode.AES_ERROR));
        }
        return FeishuJsapiSignRespDTO.builder().appId(appId).nonceStr(noncestr).timeStamp(timeStamp).signature(sign).build();
    }

}
