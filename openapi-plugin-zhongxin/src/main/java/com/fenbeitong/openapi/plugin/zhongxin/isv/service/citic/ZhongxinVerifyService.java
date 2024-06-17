package com.fenbeitong.openapi.plugin.zhongxin.isv.service.citic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.fenbeitong.openapi.plugin.zhongxin.common.constant.ZhongxinConstant;
import com.fenbeitong.openapi.plugin.zhongxin.isv.dto.citic.ZhongxinCodeCheckReqDTO;
import com.fenbeitong.openapi.plugin.zhongxin.isv.dto.citic.ZhongxinCodeCheckRespDTO;

/**
 * <p>Title:  ZhongxinVerifyService</p>
 * <p>Description: 中银授权码验证</p>
 * <p>Company:  中信银行</p>
 *
 * @author: haoqiang.wang
 * @Date: 2021/4/19 上午11:23
 **/
@ServiceAspect
@Service
@Slf4j
public class ZhongxinVerifyService extends AbstractZhongxinService {

    /**
     * 授权码授权
     * @param corpId
     * @param userId
     * @param verifyCode
     * @return
     */
    public ZhongxinCodeCheckRespDTO verify(String corpId, String userId, String verifyCode){
        //1、进行请求信息组装
        ZhongxinCodeCheckReqDTO codeCheckReqDTO = new ZhongxinCodeCheckReqDTO();
        codeCheckReqDTO.setCORP_ID(corpId);
        codeCheckReqDTO.setUSER_ID(userId);
        codeCheckReqDTO.setCHECKCODE(verifyCode);
        initRequestBody(codeCheckReqDTO);
        String businessJsonStr = JSON.toJSONString(codeCheckReqDTO);

        //2.请求中信
        String decryptBusiness = commonHandler(businessJsonStr);
        ZhongxinCodeCheckRespDTO codeCheckRespDTO = JSONObject.parseObject(decryptBusiness, ZhongxinCodeCheckRespDTO.class);
        return codeCheckRespDTO;
    }

    /**
     * 中信银行具体接口码
     * @return
     */
    public String getTransCode(){
        return ZhongxinConstant.CITI_VERIFY_TRANS_CODE;
    }
}
