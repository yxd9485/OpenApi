package com.fenbeitong.openapi.plugin.wechat.eia.service.organization;

import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatLinkedCorpDTO;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatLinkedCorpDepListDTO;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WechatDepartmentListRespDTO;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.luastar.swift.base.net.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取企业微信的组织架构
 * Created by Z.H.W on 20/02/18.
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatEiaOrgUnitService {

    @Value("${wechat.api-host}")
    private String wechatHost;


    /**
     * 根据token获取企业微信的全量部门
     *
     * @param qywxAccessToken 企业微信token
     * @param deptId          部门id
     * @return
     */
    public List<Map<String, Object>> getQywxAllDepByDepId(String qywxAccessToken, String deptId) {
        //1.调用企业微信API获取token，
        String qywxAllDepUrl = wechatHost + "/cgi-bin/department/list?access_token=" + qywxAccessToken + "&id=" + deptId;
        //3.返回token
        String depListInfo = RestHttpUtils.postJson(qywxAllDepUrl, null);
        log.info("根据获取企业微信部门集合返回数据 {}", depListInfo);
        if (StringUtils.isBlank(depListInfo)) {
            //throw new FinhubException(QywxMessageCode.DATA_DEP_LIST_IS_NULL,"部门集合数据为空");
            log.info("部门集合数据为空");
        }
        Map map = JsonUtils.toObj(depListInfo, Map.class);
        Integer errcode = (Integer) map.get("errcode");
        List depList = null;
        //返回是否成功标识
        if (errcode == 0) {
            depList = (List) map.get("department");
        }
        return depList;
    }

    /**
     * 根据token获取企业微信的全量部门
     *
     * @param qywxAccessToken 企业微信token
     * @param deptId          部门id
     * @return
     */
    public WechatDepartmentListRespDTO getAllDepByDepId(String qywxAccessToken, String deptId) {
        //1.调用企业微信API获取token，
        String qywxAllDepUrl = wechatHost + "/cgi-bin/department/list?access_token=" + qywxAccessToken + "&id=" + deptId;
        //3.返回token
        String depListInfo = RestHttpUtils.postJson(qywxAllDepUrl, null);
        log.info("根据获取企业微信部门集合返回数据 {}", depListInfo);
        if (StringUtils.isBlank(depListInfo)) {
            log.info("部门集合数据为空");
        }
        return JsonUtils.toObj(depListInfo, WechatDepartmentListRespDTO.class);
    }

    /**
     * @Description 查询互联公司
     * @Author duhui
     * @Date 2022/1/14
     **/
    public WeChatLinkedCorpDTO getLinkedCorp(String AccessToken) {
        String url = wechatHost + "/cgi-bin/linkedcorp/agent/get_perm_list?access_token=" + AccessToken;
        //3.返回token
        String data = HttpClientUtils.get(url, 3000);
        log.info("获取互联企业范围 {}", data);
        if (StringUtils.isBlank(data)) {
            throw new OpenApiWechatException(120127, "获取互联企业范围异常");
        }
        WeChatLinkedCorpDTO weChatLinkedCorpDTO = JsonUtils.toObj(data, WeChatLinkedCorpDTO.class);
        if (ObjectUtils.isEmpty(weChatLinkedCorpDTO.getDepartmentIds()) || weChatLinkedCorpDTO.getDepartmentIds().size() <= 0) {
            throw new OpenApiWechatException(120128, "获取互联企业范围数据为空");
        }
        return weChatLinkedCorpDTO;
    }

    /**
     * @Description 获取互联企业部门列表
     * @Author duhui
     * @Date 2022/1/14
     **/
    public WeChatLinkedCorpDepListDTO getLinkedCorpDepartmentList(String AccessToken, String departmentId) {
        String url = wechatHost + "/cgi-bin/linkedcorp/department/list?access_token=" + AccessToken;
        //3.返回token
        Map<String, String> map = new HashMap<String, String>(4) {{
            put("department_id", departmentId);
        }};
        String data = HttpClientUtils.postBody(url, JsonUtils.toJson(map));
        log.info("获取互联企业部门列表数据 {}", data);
        if (StringUtils.isBlank(data)) {
            throw new OpenApiWechatException(120129, "互联企业部门列表数据异常");
        }
        WeChatLinkedCorpDepListDTO weChatLinkedCorpDepListDTO = JsonUtils.toObj(data, WeChatLinkedCorpDepListDTO.class);
        if (ObjectUtils.isEmpty(weChatLinkedCorpDepListDTO.getDepartmentList()) || weChatLinkedCorpDepListDTO.getDepartmentList().size() <= 0) {
            throw new OpenApiWechatException(120130, "获取互联企业部门数据为空");
        }
        return weChatLinkedCorpDepListDTO;
    }

}
