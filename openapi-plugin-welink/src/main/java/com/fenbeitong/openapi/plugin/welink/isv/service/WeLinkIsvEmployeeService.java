package com.fenbeitong.openapi.plugin.welink.isv.service;

import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.openapi.plugin.support.employee.service.AbstractEmployeeService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.welink.common.WeLinkResponseCode;
import com.fenbeitong.openapi.plugin.welink.common.exception.OpenApiWeLinkException;
import com.fenbeitong.openapi.plugin.welink.isv.constant.WeLinkIsvConstant;
import com.fenbeitong.openapi.plugin.welink.isv.dto.WeLinkIsvUserSimpleRespDTO;
import com.fenbeitong.openapi.plugin.welink.isv.dto.WeLinkIsvUsersEmailRespDTO;
import com.fenbeitong.openapi.plugin.welink.isv.dto.WeLinkIsvUsersListRespDTO;
import com.fenbeitong.openapi.plugin.welink.isv.util.WeLinkIsvHttpUtils;
import com.fenbeitong.usercenter.api.service.employee.IThirdEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lizhen on 2020/4/16.
 */
@ServiceAspect
@Service
@Slf4j
public class WeLinkIsvEmployeeService extends AbstractEmployeeService {

    @Value("${welink.api-host}")
    private String welinkHost;

    @Autowired
    private WeLinkIsvHttpUtils weLinkIsvHttpUtils;

    @DubboReference(check = false)
    private IThirdEmployeeService iThirdEmployeeService;
    /**
     * 查询部门人员信息列表
     * @param corpId
     * @param deptCode
     * @param pageNo
     * @return
     */
    public List<WeLinkIsvUsersListRespDTO.WeLinkIsvUserInfo> weLinkUsersList(String corpId, String deptCode, Integer pageNo) {
        String url = welinkHost + WeLinkIsvConstant.USERS_LIST_URL;
        Map<String, Object> param = new HashMap<>();
        param.put("deptCode", deptCode);
        param.put("pageNo", pageNo);//
        param.put("pageSize", "50");
        String res = weLinkIsvHttpUtils.getJsonWithAccessToken(url, param, corpId);
        WeLinkIsvUsersListRespDTO weLinkIsvUsersListRespDTO = JsonUtils.toObj(res, WeLinkIsvUsersListRespDTO.class);
        if (weLinkIsvUsersListRespDTO == null || (!"0".equals(weLinkIsvUsersListRespDTO.getCode())) && ! "47012".equals(weLinkIsvUsersListRespDTO.getCode())) {
            throw new OpenApiWeLinkException(WeLinkResponseCode.WELINK_ISV_USERS_LIST_FAILED);
        }
        Integer totalPage = weLinkIsvUsersListRespDTO.getPages(); //总页数
        totalPage = totalPage == null ? 0 : totalPage;
        //递归获取剩余分页
        if (pageNo < totalPage) {
            List<WeLinkIsvUsersListRespDTO.WeLinkIsvUserInfo> weLinkIsvUserInfos = weLinkUsersList(corpId, deptCode, pageNo + 1);
            weLinkIsvUsersListRespDTO.getData().addAll(weLinkIsvUserInfos);
        }
        return weLinkIsvUsersListRespDTO.getData();
    }

    /**
     * 获取用户邮箱信息
     * @param userId
     * @param corpId
     * @return
     */
    public WeLinkIsvUsersEmailRespDTO welinkUsersEmail(String userId, String corpId) {
        String url = welinkHost + WeLinkIsvConstant.URERS_EMAIL_URL;
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        String res = weLinkIsvHttpUtils.getJsonWithAccessToken(url, param, corpId);
        WeLinkIsvUsersEmailRespDTO weLinkIsvUsersEmailRespDTO = JsonUtils.toObj(res, WeLinkIsvUsersEmailRespDTO.class);
        if (weLinkIsvUsersEmailRespDTO == null || !"0".equals(weLinkIsvUsersEmailRespDTO.getCode())) {
            log.info("welink isv usersEmail:{}", res);
            throw new OpenApiWeLinkException(WeLinkResponseCode.WELINK_ISV_USERS_EMAIL_FAILED);
        }
        return weLinkIsvUsersEmailRespDTO;
    }

    /**
     * 查询用户基本信息
     *
     * @param userId
     * @param corpId
     * @return
     */
    public WeLinkIsvUserSimpleRespDTO userSimple(String userId, String corpId) {
        String url = welinkHost + WeLinkIsvConstant.USER_SIMPLE_URL;
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        String res = weLinkIsvHttpUtils.getJsonWithAccessToken(url, param, corpId);
        log.info("welink isv userSimple res is {}", res);
        WeLinkIsvUserSimpleRespDTO weLinkIsvUserSimpleRespDTO = JsonUtils.toObj(res, WeLinkIsvUserSimpleRespDTO.class);
        if (weLinkIsvUserSimpleRespDTO == null || !"0".equals(weLinkIsvUserSimpleRespDTO.getCode())) {
            log.info("welink isv userSimple:{}", res);
            throw new OpenApiWeLinkException(WeLinkResponseCode.WELINK_ISV_USER_SIMPLE_FAILED);
        }
        return weLinkIsvUserSimpleRespDTO;
    }

    @Override
    protected IThirdEmployeeService getThirdEmployeeService() {
        return iThirdEmployeeService;
    }
}
