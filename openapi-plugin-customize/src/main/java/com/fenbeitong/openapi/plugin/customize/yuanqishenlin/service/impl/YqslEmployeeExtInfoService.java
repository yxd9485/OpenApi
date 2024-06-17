package com.fenbeitong.openapi.plugin.customize.yuanqishenlin.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.common.utils.basis.StringUtil;
import com.fenbeitong.openapi.plugin.customize.yuanqishenlin.dto.YuanqiEmployeeDetailDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.BaseDTO;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName YqslEmployeeExtInfoService
 * @Description 元气森林人员扩展信息获取
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/8/30 下午4:51
 **/
@Service
@Slf4j
public class YqslEmployeeExtInfoService {
    @Autowired
    private OpenSysConfigService openSysConfigService;

    //元气森林人员详情查询，扩展字段信息替换
    public YuanqiEmployeeDetailDTO getYqEmployeeDetail(OpenThirdEmployeeDTO openThirdEmployeeDTO) {
        BasicTextEncryptor basicTextEncryptor = new BasicTextEncryptor();
        //查询加解密密码和域名
        String yqslSysConfig = openSysConfigService.getOpenSysConfigByTypeCode(OpenSysConfigType.YQSL_SYS_CONFIG.getType(), openThirdEmployeeDTO.getCompanyId());
        String host = StringUtil.obj2str(MapUtils.getValueByExpress(JsonUtils.toObj(yqslSysConfig, Map.class), "host"));
        String encryPassword = StringUtil.obj2str(MapUtils.getValueByExpress(JsonUtils.toObj(yqslSysConfig, Map.class), "encry_password"));
        //加密所需的salt(盐)
        basicTextEncryptor.setPassword(encryPassword);
        //调用接口查询人员信息
        Map<String, Object> param = new HashMap();
        param.put("feishu_user_id", openThirdEmployeeDTO.getThirdEmployeeId());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        String result = RestHttpUtils.get(host + "/api/fbt/get_user_detail", headers, param);
        log.info("查询元气人员信息返回：" + JsonUtils.toJson(result));
        Integer code = NumericUtils.obj2int(MapUtils.getValueByExpress(JsonUtils.toObj(result, Map.class), "code"));
        if (!Integer.valueOf(0).equals(code)) {
            //未查询到人员信息则返回
            log.info("查询元气人员信息不存在,员工三方ID：" + param);
            return null;
        }
        BaseDTO<YuanqiEmployeeDetailDTO> yqEmployeeDetailRes = JsonUtils.toObj(result, new TypeReference<BaseDTO<YuanqiEmployeeDetailDTO>>() {
        });
        YuanqiEmployeeDetailDTO yqEmployeeDetailDTO = yqEmployeeDetailRes.getData();
        String identityNum = StringUtils.isEmpty(yqEmployeeDetailDTO.getIdentityNumber()) ? null : basicTextEncryptor.decrypt(yqEmployeeDetailDTO.getIdentityNumber());
        String bankCardNo = StringUtils.isEmpty(yqEmployeeDetailDTO.getBankCardNo()) ? null : basicTextEncryptor.decrypt(yqEmployeeDetailDTO.getBankCardNo());
        openThirdEmployeeDTO.setThirdEmployeeIdCard(identityNum);
        yqEmployeeDetailDTO.setBankCardNo(bankCardNo);
        return yqEmployeeDetailDTO;
    }

}
