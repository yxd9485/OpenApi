package com.fenbeitong.openapi.plugin.zhongxin.isv.util;

import com.fenbeitong.openapi.plugin.zhongxin.common.exception.OpenApiZhongxinException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import com.fenbeitong.openapi.plugin.zhongxin.isv.entity.ZhongxinResultEntity;

/**
 * <p>Title: WechatResponseUtils</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/10 4:54 PM
 */
@Slf4j
public class ZhongxinResponseUtils {

    public static ZhongxinResultEntity success(Object data) {
        ZhongxinResultEntity result = new ZhongxinResultEntity<>();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(0);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    public static ZhongxinResultEntity error(int code, Object msg) {
        ZhongxinResultEntity result = new ZhongxinResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(code);
        if (msg instanceof String){
            result.setMsg((String) msg);
        } else if(msg instanceof OpenApiZhongxinException && null != ((OpenApiZhongxinException) msg).getArgs()){
            result.setMsg(((OpenApiZhongxinException) msg).getArgs()[0].toString());
        } else if(msg instanceof Exception){
            result.setMsg(((Exception) msg).getMessage());
        } else{
            result.setMsg("");
        }
        return result;
    }

}
