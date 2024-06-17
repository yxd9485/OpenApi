package com.fenbeitong.openapi.plugin.kingdee.support.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.ResultVo;
import com.fenbeitong.openapi.plugin.kingdee.support.service.KingdeeService;
import com.fenbeitong.openapi.plugin.kingdee.support.util.ResultUtil;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description: 金蝶报销单接口实现类
 * @date 2020/09/17 14:24
 */
@ServiceAspect
@Service
@Slf4j
public class KingdeeServiceImpl implements KingdeeService {

    @Override
    public ResultVo login(String url, MultiValueMap postData) {
        HttpHeaders httpHeaders = new HttpHeaders();
        ResponseEntity<String> responseEntity = RestHttpUtils.postFormToEntity(url, httpHeaders, postData);
        Map resultMap = JsonUtils.toObj(responseEntity.getBody(), Map.class);
        //获取登录cookie
        if (responseEntity.getStatusCode() == HttpStatus.OK && resultMap != null && 1 == (Integer) resultMap.get("LoginResultType")) {
            String login_cookie = "";
            Set<String> keys = responseEntity.getHeaders().keySet();
            for (String key : keys) {
                if (key.equalsIgnoreCase("Set-Cookie")) {
                    List<String> cookies = responseEntity.getHeaders().get(key);
                    for (String cookie : cookies) {
                        if (cookie.startsWith("kdservice-sessionid")) {
                            login_cookie = cookie;
                            break;
                        }
                    }
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("cookie", login_cookie);
            return ResultUtil.success(map);
        }
        return ResultUtil.error(resultMap.get("Message").toString());
    }

    @Override
    public String view(String url, String cookie, String content) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Cookie", cookie);
        return RestHttpUtils.postJson(url, httpHeaders, content);
    }

    @Override
    public ResultVo save(String url, String cookie, String content) {
        //保存
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Cookie", cookie);
        String result = RestHttpUtils.postJson(url, httpHeaders, content);
        JSONObject jsonObject = JSON.parseObject(result);
        Map<String, Object> map = (Map<String, Object>) jsonObject.get("Result");
        Map<String, Object> responseStatus = (Map<String, Object>) map.get("ResponseStatus");
        Boolean isSuccess = (Boolean) responseStatus.get("IsSuccess");
        if (isSuccess) {
            return ResultUtil.success(responseStatus);
        } else {
            return ResultUtil.error(responseStatus);
        }
    }

    @Override
    public ResultVo submit(String url, String cookie, String content) {
        //保存
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Cookie", cookie);
        String result = RestHttpUtils.postJson(url, httpHeaders, content);
        JSONObject jsonObject = JSON.parseObject(result);
        Map<String, Object> map = (Map<String, Object>) jsonObject.get("Result");
        Map<String, Object> responseStatus = (Map<String, Object>) map.get("ResponseStatus");
        Boolean isSuccess = (Boolean) responseStatus.get("IsSuccess");
        if (isSuccess) {
            return ResultUtil.success(responseStatus);
        } else {
            return ResultUtil.error(responseStatus);
        }
    }


    @Override
    public String getNumberBySave(String url, String cookie, String content) {
        //保存
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Cookie", cookie);
        String result = RestHttpUtils.postJson(url, httpHeaders, content);
        log.info("金蝶保存返结果:{}", result);
        JSONObject jsonObject = JSON.parseObject(result);
        Map<String, Object> map = (Map<String, Object>) jsonObject.get("Result");
        Map<String, Object> responseStatus = (Map<String, Object>) map.get("ResponseStatus");
        Boolean isSuccess = (Boolean) responseStatus.get("IsSuccess");
        if (isSuccess) {
            return map.get("Number").toString();
        } else {
            log.warn("金蝶保存失败,req:{}", content);
            return null;
        }
    }

    @Override
    public boolean submitAndAudit(String url, String cookie, String content) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Cookie", cookie);
        String result = RestHttpUtils.postJson(url, httpHeaders, content);
        log.info("提交和审核返回结果：{}", result);
        JSONObject jsonObject = JSON.parseObject(result);
        Map<String, Object> map = (Map<String, Object>) jsonObject.get("Result");
        Map<String, Object> responseStatus = (Map<String, Object>) map.get("ResponseStatus");
        Boolean isSuccess = (Boolean) responseStatus.get("IsSuccess");
        if (isSuccess) {
            return true;
        } else {
            log.info("金蝶提交或审核失败!");
            return false;
        }
    }

}
