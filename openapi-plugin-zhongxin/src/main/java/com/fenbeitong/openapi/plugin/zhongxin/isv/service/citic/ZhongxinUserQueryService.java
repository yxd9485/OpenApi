package com.fenbeitong.openapi.plugin.zhongxin.isv.service.citic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.zhongxin.common.constant.ZhongxinConstant;
import com.fenbeitong.openapi.plugin.zhongxin.common.exception.OpenApiZhongxinException;
import com.fenbeitong.openapi.plugin.zhongxin.isv.dto.citic.ZhongxinUserQueryReqDTO;
import com.fenbeitong.openapi.plugin.zhongxin.isv.dto.citic.ZhongxinUserQueryRespDTO;
import com.fenbeitong.openapi.plugin.zhongxin.isv.util.ZhongxinResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * <p>Title:  ZhongxinUserQueryService</p>
 * <p>Description: 根据手机号查询用户三方id</p>
 * <p>Company: 中信银行 </p>
 *
 * @author: haoqiang.wang
 * @Date: 2021/4/22 下午3:42
 **/
@ServiceAspect
@Service
@Slf4j
public class ZhongxinUserQueryService extends AbstractZhongxinService{

    /**
     * 查询中信三方id
     * @param corpId
     * @param userId
     * @return
     */
    public String queryEmployeeHash(String corpId, String userId) {
        //1、进行请求信息组装
        ZhongxinUserQueryReqDTO userQueryReqDTO = new ZhongxinUserQueryReqDTO();
        userQueryReqDTO.setINNPRTCNO(corpId);
        userQueryReqDTO.setQRYTYP("0");
        userQueryReqDTO.setMOBNO(userId);
        initRequestBody(userQueryReqDTO);
        String businessJsonStr = JSON.toJSONString(userQueryReqDTO);

        //2.请求中信
        String decryptBusiness = commonHandler(businessJsonStr);
        ZhongxinUserQueryRespDTO userQueryRespDTO = JSONObject.parseObject(decryptBusiness, ZhongxinUserQueryRespDTO.class);
        if(ZhongxinConstant.SUCCESS.equals(userQueryRespDTO.getRETCODE())){
            return userQueryRespDTO.getHASH();
        }
        throw new OpenApiZhongxinException(ZhongxinResponseCode.ZHONG_XIN_THIRD_USER_QUERY_FAILED, "查询三方id失败");
    }

    /**
     * 中信银行具体接口码
     * @return
     */
    @Override
    public String getTransCode(){
        return ZhongxinConstant.CITI_USER_QUERY_TRANS_CODE;
    }
}
