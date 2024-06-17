package com.fenbeitong.openapi.plugin.zhongxin.isv.service.citic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.fenbeitong.openapi.plugin.zhongxin.common.constant.ZhongxinConstant;
import com.fenbeitong.openapi.plugin.zhongxin.isv.dto.citic.ZhongxinGetMessageReqDTO;
import com.fenbeitong.openapi.plugin.zhongxin.isv.dto.citic.ZhongxinGetMessageRespDTO;

/**
 * <p>Title:  ZhongxinGetMessageService</p>
 * <p>Description: 中信获取短信验证码</p>
 * <p>Company:  中信银行</p>
 *
 * @author: haoqiang.wang
 * @Date: 2021/4/17 下午6:04
 **/
@ServiceAspect
@Service
@Slf4j
public class ZhongxinGetMessageService extends AbstractZhongxinService {

    /**
     * 获取中信银行验证码处理类
     * @param corpId
     * @param userId
     * @return
     */
    public ZhongxinGetMessageRespDTO getMessage(String corpId, String userId) {
        //1、进行请求信息组装
        ZhongxinGetMessageReqDTO getMessageReqDTO = new ZhongxinGetMessageReqDTO();
        getMessageReqDTO.setCORP_ID(corpId);
        getMessageReqDTO.setUSER_ID(userId);
        initRequestBody(getMessageReqDTO);
        String businessJsonStr = JSON.toJSONString(getMessageReqDTO);

        //2.请求中信
        String decryptBusiness = commonHandler(businessJsonStr);
        ZhongxinGetMessageRespDTO getMessageRespDTO = JSONObject.parseObject(decryptBusiness, ZhongxinGetMessageRespDTO.class);
        return getMessageRespDTO;
    }

    /**
     * 中信银行具体接口码
     * @return
     */
    @Override
    public String getTransCode(){
        return ZhongxinConstant.CITI_GET_MESSAGE_TRANS_CODE;
    }

}
