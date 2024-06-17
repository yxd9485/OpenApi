package com.fenbeitong.openapi.plugin.zhongxin.isv.service.citic;

import com.alibaba.fastjson.JSON;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import com.fenbeitong.openapi.plugin.zhongxin.common.config.CiticBankConfig;
import com.fenbeitong.openapi.plugin.zhongxin.common.exception.OpenApiZhongxinException;
import com.fenbeitong.openapi.plugin.zhongxin.isv.dto.citic.BaseZhongxinIsvReqDTO;
import com.fenbeitong.openapi.plugin.zhongxin.isv.util.ZhongxinBankUtil;
import com.fenbeitong.openapi.plugin.zhongxin.isv.util.ZhongxinResponseCode;

import java.util.Date;

/**
 * <p>Title:  AbstractZhongxinService</p>
 * <p>Description: 请求中信银行接口基础类</p>
 * <p>Company:  中信银行</p>
 *
 * @author: haoqiang.wang
 * @Date: 2021/4/17 下午5:59
 **/
@Slf4j
public abstract class AbstractZhongxinService {

    @Autowired
    protected CiticBankConfig citicBankConfig;

    @Value("${citic.api-host}")
    private String requestUrl;

    /**
     * 公共信息组装
     *
     * @param baseZhongxinIsvReqDTO
     */
    protected void initRequestBody(BaseZhongxinIsvReqDTO baseZhongxinIsvReqDTO) {
        Date nowDate = new Date();
        baseZhongxinIsvReqDTO.setOPENVER("1.0.0");
        baseZhongxinIsvReqDTO.setOPENMERCODE(citicBankConfig.getOpenMerCode());
        baseZhongxinIsvReqDTO.setOPENMERNAME(citicBankConfig.getOpenMerName());
        baseZhongxinIsvReqDTO.setOPENBUSITYPE(citicBankConfig.getOpenBusType());
        baseZhongxinIsvReqDTO.setOPENTRANSCODE(getTransCode());
        baseZhongxinIsvReqDTO.setOPENLAUNCHDATE(DateUtils.toStr(nowDate, "yyyyMMdd"));
        baseZhongxinIsvReqDTO.setOPENLAUNCHTIME(DateUtils.toStr(nowDate, "HHmmss"));
        baseZhongxinIsvReqDTO.setOPENMERFLOWID(ZhongxinBankUtil.getFlowId(nowDate));
    }

    /**
     * 和中信银行通用交互逻辑
     *
     * @param businessJsonStr
     * @return
     */
    protected String commonHandler(String businessJsonStr) {
        log.info("请求中信银行明文数据为：{}", businessJsonStr);
        //1.进行加密
        String encryptBody;
        try {
            encryptBody = ZhongxinBankUtil.getEncodeStr(businessJsonStr, citicBankConfig.getEncryptKeyStr(), citicBankConfig.getSignKeyStr());
        } catch (Exception e) {
            log.error("数据加密失败！", e);
            throw new OpenApiZhongxinException(ZhongxinResponseCode.ENCODE_ERROR, "数据加密失败");
        }

        //2、进行中信银行通讯请求
        String respEncryptBody;
        try {
            respEncryptBody = requestCitic(encryptBody);
            log.info("中信银行返回密文数据为：{}", respEncryptBody);
        }catch (Exception e){
            log.error("请求中信银行异常，", e);
            throw new OpenApiZhongxinException(ZhongxinResponseCode.ZHONG_XIN_REQUEST_FAILED, "请求三方通讯异常");
        }

        //3、获取返回数据明文
        String decryptBusiness;
        String encryptBodyRes = JSON.parseObject(respEncryptBody).getString("encryptBody");
        try {
            decryptBusiness = ZhongxinBankUtil.getDecodeStr(encryptBodyRes, citicBankConfig.getDecryptKeyStr());
            log.info("中信银行交易返回明文信息为:{}", decryptBusiness);
        } catch (Exception e) {
            log.error("数据解密失败！", e);
            throw new OpenApiZhongxinException(ZhongxinResponseCode.DECODE_ERROR, "数据解密失败");
        }

        //4、进行返回数据验签
        String signData = JSON.parseObject(respEncryptBody).getString("sign");
        if (ZhongxinBankUtil.verify(decryptBusiness, citicBankConfig.getVerifyKeyStr(), signData)) {
            return decryptBusiness;
        } else {
            log.info("验签失败！");
            throw new OpenApiZhongxinException(ZhongxinResponseCode.VERIFY_ERROR, "数据验签失败");
        }
    }

    /**
     * http请求通讯
     *
     * @param requestData
     * @return
     */
    protected String requestCitic(String requestData) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("connection", "keep-alive");
        String responseStr = RestHttpUtils.postJson(requestUrl, httpHeaders, requestData);
        return responseStr;
    }

    /**
     * 中信银行具体接口码
     *
     * @return
     */
    abstract String getTransCode();
}
