package com.fenbeitong.openapi.plugin.yunzhijia.service.impl;

import com.fenbeitong.openapi.plugin.support.common.service.AbstractOpenapiService;
import com.fenbeitong.openapi.plugin.yunzhijia.utils.WebHookUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.Map;
@Slf4j
@ServiceAspect
@Service
public class YunzhijiaCallbackService extends AbstractOpenapiService {
    @Override
    public String getProcessorKey() {
        return this.getClass().getName();
    }



    public static boolean checkAuth(String appid,String appkey,String content,Map<String,String> headers){
        boolean ret = false;
        if(content!=null &&headers!=null){
            String contentType = headers.get("Content-Type");
            if(contentType==null){
                contentType = headers.get("content-type");
            }
            String contentMd5 = headers.get("Content-MD5");
            if(contentMd5==null){
                contentMd5 = headers.get("content-md5");
            }
            String date = headers.get("Date");
            if(date==null){
                date = headers.get("date");
            }
            String auth = headers.get("Authorization");
            if(auth==null){
                auth = headers.get("authorization");
            }
            log.info("auth header:content-type={},md5={},date={},authorization={}",contentType,contentMd5,date,auth);
            String md5 = WebHookUtil.getContentMd5(content);
            log.info("auth header:local product MD5={},check={}",md5,(md5!=null&&md5.equals(contentMd5)));

            if(md5!=null&&md5.equals(contentMd5)){
                String authorization = WebHookUtil.getAuthorization( appid, appkey,contentMd5, contentType, date);
                log.info("auth header:local authorization={},check={}",authorization,(auth!=null&&auth.equals(authorization)));
                if(auth!=null&&auth.equals(authorization)){
                    ret = true;
                }
            }
        }

        return ret;
    }

    public static boolean checkAuth(String token, String content, Map<String,String> headers){
        boolean ret = false;
        if(content!=null &&headers!=null){
            String contentType = headers.get("Content-Type");
            if(contentType==null){
                contentType = headers.get("content-type");
            }
            String contentMd5 = headers.get("Content-MD5");
            if(contentMd5==null){
                contentMd5 = headers.get("content-md5");
            }
            String date = headers.get("Date");
            if(date==null){
                date = headers.get("date");
            }
            String auth = headers.get("Authorization");
            if(auth==null){
                auth = headers.get("authorization");
            }
            log.info("auth header:content-type={},md5={},date={},authorization={}",contentType,contentMd5,date,auth);
            String md5 = WebHookUtil.getContentMd5(content);
            log.info("auth header:local product MD5={},check={}",md5,(md5!=null&&md5.equals(contentMd5)));

            if(md5!=null&&md5.equals(contentMd5)){
                String authorization = WebHookUtil.getAuthorization( token,contentMd5, contentType, date);
                log.info("auth header:local authorization={},check={}",authorization,(auth!=null&&auth.equals(authorization)));
                if(auth!=null&&auth.equals(authorization)){
                    ret = true;
                }
            }
        }

        return ret;
    }



}
