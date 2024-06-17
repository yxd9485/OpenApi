package com.fenbeitong.openapi.plugin.ecology.v8.service.impl;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.ecology.v8.common.EcologyConstant;
import com.fenbeitong.openapi.plugin.ecology.v8.dto.EcologyApproveUserInfoDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.dto.EcologyRestCommonResultDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.service.IDealApproveService;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dao.OpenEcologyResturlConfigDao;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyResturlConfig;
import com.fenbeitong.openapi.plugin.ecology.v8.util.EcologyRestApiUtils;
import com.fenbeitong.openapi.plugin.support.apply.dao.OpenOrderApplyDao;
import com.fenbeitong.openapi.plugin.support.apply.entity.OpenOrderApply;
import com.fenbeitong.openapi.plugin.support.employee.service.IGetEmployeeInfoFromUcService;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 处理消息
 * @Auther zhang.peng
 * @Date 2021/11/15
 */
@Service
@Slf4j
@ServiceAspect
public class DealApproveServiceImpl implements IDealApproveService {

    private static final String SUPER_USER_NAME = "直接上级";

    private static final String SUCCESS_CODE = "SUCCESS";

    private static final String TOKEN_MESSAGE = "token不存在或者超时";

    private static final int FAIL_RESULT = -1;

    @Autowired
    private OpenOrderApplyDao openOrderApplyDao;

    @Autowired
    private OpenEcologyResturlConfigDao openEcologyResturlConfigDao;

    @Autowired
    private EcologyRestApiUtils ecologyRestApiUtils;

    @Autowired
    private IGetEmployeeInfoFromUcService getEmployeeInfoFromUcService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean revokeAndDeleteEcologyApprove(String companyId , String oldApproveId , String userId ){
        // 获取泛微 restful 接口配置信息
        OpenEcologyResturlConfig ecologyRestUrlConfigList = openEcologyResturlConfigDao.getRestConfigCompanyId(companyId);
        Map<String, Object> condition = new HashMap<>();
        condition.put("companyId", companyId);
        condition.put("fbtApplyId", oldApproveId);
        OpenOrderApply openOrderApply = openOrderApplyDao.getOpenOrderApply(condition);
        if ( null == openOrderApply ){
            log.info("该公司没有当前审批单 , companyId : {}, fbtApplyId : {}",companyId,oldApproveId);
            return false;
        }
        oldApproveId = openOrderApply.getThirdApplyId();
        if ( null == ecologyRestUrlConfigList ){
            log.info(" 未配置泛微 rest 接口地址 , 不处理 , 泛微表单 requestId {} ",oldApproveId);
            return false;
        }
        String domain = ecologyRestUrlConfigList.getDomainName();
        String appId = ecologyRestUrlConfigList.getEcologyAppId();
        String spk = "";
        String token = "";
        // 获取 token 和 泛微秘钥 , 优先从缓存获取
        String tokenKey = MessageFormat.format(RedisKeyConstant.ECOLOGY_REST_INTERFACE_INFO, EcologyConstant.SERVER_TOKEN);
        String secretKey = MessageFormat.format(RedisKeyConstant.ECOLOGY_REST_INTERFACE_INFO, EcologyConstant.SECRET);
        String secretPublicKey = MessageFormat.format(RedisKeyConstant.ECOLOGY_REST_INTERFACE_INFO, EcologyConstant.SECRET_PUBLIC_KEY);
        // value 值
        String tokenValue = (String) redisTemplate.opsForValue().get(tokenKey);
        String serverSecret = (String) redisTemplate.opsForValue().get(secretKey);
        String secretPublicValue = (String) redisTemplate.opsForValue().get(secretPublicKey);
        // 注册信息,缓存里没有查接口
        if (StringUtils.isBlank(serverSecret) && StringUtils.isBlank(secretPublicValue)){
            ecologyRestApiUtils.doRegister(domain,appId);
            secretPublicValue = (String) redisTemplate.opsForValue().get(secretPublicKey);
        }
        // 获取 token,缓存里没有查接口
        if (StringUtils.isBlank(tokenValue)){
            ecologyRestApiUtils.getToken(domain,appId);
            tokenValue = (String) redisTemplate.opsForValue().get(tokenKey);
        }
        spk = secretPublicValue;
        token = tokenValue;
        RSA rsa = new RSA(null,spk);
        // 对用户信息进行加密传输,暂仅支持传输OA用户ID 发起人userid
        String userThirdEmployeeId = getUserThirdEmployeeId(companyId,userId);
        if (StringUtils.isBlank(userThirdEmployeeId)){
            log.info("用户三方 id 为空 , 不处理 , ftbEmployeeId {} , companyId {}",userId,companyId);
            return false;
        }
        String encryptUserId = rsa.encryptBase64(userThirdEmployeeId, CharsetUtil.CHARSET_UTF_8, KeyType.PublicKey);
        Map<String,String> heads = new HashMap<>();
        heads.put("appid",appId);
        heads.put("token",token);
        heads.put("userid",encryptUserId);
        // 请求Id
        Map<String,String> jsonObject = new HashMap<>();
        jsonObject.put("requestid",oldApproveId);
        // 获取当前审批人
        String superiorUserId = getSuperUserId(domain,jsonObject,heads,appId);
        if (StringUtils.isBlank(superiorUserId)){
            return false;
        }
        String superiorEncryptUserId = rsa.encryptBase64(superiorUserId,CharsetUtil.CHARSET_UTF_8,KeyType.PublicKey);
        heads.put("userid",superiorEncryptUserId);
        // 退回
        boolean result = doReject(domain,jsonObject,heads);
        if (!result){
            return false;
        }
        heads.put("userid",encryptUserId);
        // 删除
        boolean deleteResult = doDelete(domain,jsonObject,heads);
        return deleteResult;
    }

    public String getSuperUserId( String domain , Map<String,String> jsonObject , Map<String,String> heads , String appId ){
        try {
            String url = EcologyConstant.GET_SUPER_APPROVE_USER_ID_URL;
            log.info("获取上级审批人参数信息 : url : {}, jsonParam : {}, head :{}",domain + url,JsonUtils.toJson(jsonObject),JsonUtils.toJson(heads));
            ecologyRestApiUtils.cookieStore.clear();
            String result = ecologyRestApiUtils.getDataSSL(domain + url,jsonObject,heads);
            // {"msg":"token不存在或者超时：e81532a3-4a3f-4427-95da-ff18d6d50ff0","code":-1,"msgShowType":"none","status":false}
            log.info("获取当前审批人返回结果 : {} ",result);
            Map<String,Object> resultMap = JsonUtils.toObj(result,Map.class);
            Object code =  resultMap.get("code");
            if ( code instanceof Integer ){
                // token 过期重试
                if ( FAIL_RESULT == (int)code && ((String)resultMap.get("msg")).contains(TOKEN_MESSAGE) ){
                    ecologyRestApiUtils.getToken(domain,appId);
                    String tokenKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, EcologyConstant.SERVER_TOKEN);
                    String tokenValue = (String) redisTemplate.opsForValue().get(tokenKey);
                    heads.put("token",tokenValue);
                    result = ecologyRestApiUtils.getDataSSL(domain + url,jsonObject,heads);
                }
            }
            EcologyApproveUserInfoDTO approveUserInfoDTO = JsonUtils.toObj(result,EcologyApproveUserInfoDTO.class);
            if ( null == approveUserInfoDTO ){
                log.info(" 转换泛微上级审批人失败 ");
                return null;
            }
            if (CollectionUtils.isBlank(approveUserInfoDTO.getData()) ){
                log.info(" 返回数据为空 ");
                return null;
            }
            List<EcologyApproveUserInfoDTO.UserInfo> userList = approveUserInfoDTO.getData().stream().filter(user-> SUPER_USER_NAME.equals(user.getNodeName())).collect(Collectors.toList());
            if (CollectionUtils.isBlank(userList)){
                log.info(" 上级员工信息为空 ");
                return null;
            }
            EcologyApproveUserInfoDTO.UserInfo userInfo = userList.get(0);
            return userInfo.getUserid();
        } catch (Exception e) {
            log.info("获取当前审批人失败 :{}",e.getMessage());
            return null;
        }
    }

    public boolean doReject( String domain , Map<String,String> jsonObject , Map<String,String> heads ){
        try {
            String url = EcologyConstant.REJECT_APPROVE_URL;
            log.info("退回参数信息 : url : {}, jsonParam : {}, head :{}",domain + url,JsonUtils.toJson(jsonObject),JsonUtils.toJson(heads));
            ecologyRestApiUtils.cookieStore.clear();
            String rejectMsg = ecologyRestApiUtils.postDataSSL(domain + url, jsonObject, heads);
            log.info("退回审批信息 : {} ",rejectMsg);
            // 退回 {"code":"SUCCESS","errMsg":{}}
            EcologyRestCommonResultDTO rejectInfoDTO = JsonUtils.toObj(rejectMsg,EcologyRestCommonResultDTO.class);
            if ( null == rejectInfoDTO ){
                log.info("转换退回信息为空");
                return false;
            }
            if (!SUCCESS_CODE.equals(rejectInfoDTO.getCode())){
                log.info("退回审批失败");
                return false;
            }
            return true;
        } catch (Exception e){
            log.info("退回审批异常 :{}",e.getMessage());
            return false;
        }
    }

    public boolean doDelete( String domain , Map<String,String> jsonObject , Map<String,String> heads ){
        try {
            String url = EcologyConstant.DELETE_APPROVE_URL;
            ecologyRestApiUtils.cookieStore.clear();
            log.info("删除审批参数信息 : url : {}, jsonParam : {}, head :{}",domain + url,JsonUtils.toJson(jsonObject),JsonUtils.toJson(heads));
            String deleteMsg = ecologyRestApiUtils.postDataSSL(domain + url, jsonObject, heads);
            log.info("删除审批返回结果 : {}",deleteMsg);
            // {"code":"SUCCESS","errMsg":{}}
            EcologyRestCommonResultDTO deleteDTO = JsonUtils.toObj(deleteMsg,EcologyRestCommonResultDTO.class);
            if ( null == deleteDTO ){
                log.info("转换退回信息为空");
                return false;
            }
            if (!SUCCESS_CODE.equals(deleteDTO.getCode())){
                log.info("退回审批失败");
                return false;
            }
            return true;
        } catch (Exception e) {
            log.info("删除审批异常 :{}",e.getMessage());
            return false;
        }
    }

    public String getUserThirdEmployeeId( String companyId , String employeeId ){
        ThirdEmployeeRes thirdEmployeeRes = getEmployeeInfoFromUcService.getEmployInfoByEmployeeId(companyId,employeeId,"0");
        if ( null == thirdEmployeeRes || null == thirdEmployeeRes.getEmployee()){
            return null;
        } else {
            // uc
            return thirdEmployeeRes.getEmployee().getThirdEmployeeId();
        }
    }

}
