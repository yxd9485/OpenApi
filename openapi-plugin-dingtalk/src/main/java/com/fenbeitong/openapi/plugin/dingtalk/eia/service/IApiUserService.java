package com.fenbeitong.openapi.plugin.dingtalk.eia.service;

import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkFreeLoginDto;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkUser;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;

import java.util.List;

/**
 * <p>Title: IApiUserService</p>
 * <p>Description: 钉钉用户服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/16 12:26 PM
 */
public interface IApiUserService {

    /**
     * 根据钉钉部门ID，获取其下的所有用户
     *
     * @param departmentId 钉钉部门ID
     * @param corpId       corpId
     * @return 钉钉用户列表
     */
    List<DingtalkUser> getAllUserByDepartment(long departmentId, String corpId);

    /**
     * 根据corpId和授权码获取钉钉登录用户ID
     *
     * @param corpId   corpId
     * @param authCode 登录授权码
     * @return 钉钉登录用户ID
     */
    String getAuthUserId(String corpId, String authCode);

    /**
     * 根据corpId和授权码获取钉钉免登所使用信息
     *
     * @param corpId   corpId
     * @param authCode 登录授权码
     * @param companyId 公司id
     * @param freeLabel 免登自定义字段
     * @param freeAccountConfig 免登脚本信息
     * @return 钉钉登录自定义字段的值
     */
    String getAuthFreeLoginLabel(String corpId, String authCode, String companyId, String freeLabel, OpenThirdScriptConfig freeAccountConfig);

    /**
     * 获取登录信息
     * @param freeLoginDto 用户信息
     * @return 登录信息
     */
    LoginResVO getLoginInfo(DingtalkFreeLoginDto freeLoginDto);

    /**
     * 根据corpId和手机号获取钉钉 userID
     *
     * @param corpId   corpId
     * @param phoneNum 用户手机号
     * @return 钉钉 userID
     */
    String getDingtalkUserIdByPhoneNum(String corpId, String phoneNum);
}
