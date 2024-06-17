package com.fenbeitong.openapi.plugin.customize.common.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.customize.common.dto.CustomFieldTranDTO;
import com.fenbeitong.openapi.plugin.customize.common.service.CustomFieldTranService;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.company.CompanyNewDto;
import com.fenbeitong.usercenter.api.service.company.ICompanyService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

/**
 * 自定义字段清洗
 * 按公司 将老的expandJson洗到web后台可展示的自定义字段中
 *
 * @author ctl
 * @date 2021/10/29
 */
@RestController
@RequestMapping("/customize")
@Slf4j
public class CustomFieldTranController {

    @Autowired
    private CustomFieldTranService customFieldTranService;

    @DubboReference(check = false)
    private ICompanyService companyService;

    /**
     * excel url的方式读取数据
     *
     * @param dto
     * @return
     */
    @PostMapping("/tranCustomField")
    public Object tranCustomField(@RequestBody CustomFieldTranDTO dto) {
        String companyId = dto.getCompanyId();
        String url = dto.getUrl();
        if (StringUtils.isBlank(companyId)) {
            throw new OpenApiArgumentException("[companyId]不能为空");
        }
        if (StringUtils.isBlank(url)) {
            throw new OpenApiArgumentException("[url]不能为空");
        }
        CompanyNewDto companyNewDto = companyService.queryCompanyNewByCompanyId(dto.getCompanyId());
        if (companyNewDto == null) {
            throw new OpenApiArgumentException("公司不存在");
        }
        customFieldTranService.tranAndNotify(dto, companyNewDto.getCompanyName());
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

}
