package com.fenbeitong.openapi.plugin.yunzhijia.service.impl.publicnotice;

import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.constant.YunzhijiaUrlConstant;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.publicnotice.YunzhijiaSetSubscribeDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.publicnotice.YunzhijiaWhetherSubscribeDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.enums.YunzhijiaSubscribeType;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaPublicNoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * 公共号服务实现
 * @Auther zhang.peng
 * @Date 2021/7/30
 */
@Slf4j
@ServiceAspect
@Service
public class YunzhijiaPublicNoticeServiceImpl implements IYunzhijiaPublicNoticeService {

    //是否订阅
    public boolean isSubscribe(String pubid, String eid){
        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("pubid", pubid);
        requestMap.add("mid", eid);
        String result = RestHttpUtils.postForm(YunzhijiaUrlConstant.IS_COMPANY_SUBSCRIBE_OR_NOT,requestMap);
        if (StringUtils.isBlank(result)){
            return false;
        }
        YunzhijiaWhetherSubscribeDTO whetherSubscribeDTO = JsonUtils.toObj(result, YunzhijiaWhetherSubscribeDTO.class);
        if ( null == whetherSubscribeDTO ){
            return false;
        }
        String whetherSub = whetherSubscribeDTO.getSsb();
        return YunzhijiaSubscribeType.YES.getCode().equals(whetherSub);
    }

    // 企业订阅
    public boolean companySubscribe(String pubid,String eid,String ssb, String pubtoken,long time){
        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("pubid", pubid);
        requestMap.add("mid", eid);
        requestMap.add("ssb", ssb);
        requestMap.add("time", time);
        requestMap.add("pubtoken", pubtoken);
        String result = RestHttpUtils.postForm(YunzhijiaUrlConstant.COMPANY_SUBSCRIBE_PUBLIC_NOTICE,requestMap);
        if (StringUtils.isBlank(result)){
            return false;
        }
        YunzhijiaSetSubscribeDTO setSubscribeDTO = JsonUtils.toObj(result, YunzhijiaSetSubscribeDTO.class);
        if ( null == setSubscribeDTO ){
            return false;
        }
        return setSubscribeDTO.isSuccess();
    }
}
