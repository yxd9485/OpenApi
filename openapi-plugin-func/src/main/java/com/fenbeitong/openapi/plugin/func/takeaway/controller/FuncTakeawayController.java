package com.fenbeitong.openapi.plugin.func.takeaway.controller;

import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.takeaway.service.FuncTakeawayService;
import com.fenbeitong.openapi.plugin.support.apply.dto.TakeawayAddressDTO;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/func/takeaway")
public class FuncTakeawayController {
    @Autowired
    FuncTakeawayService funcTakeawayService;
    @Autowired
    CommonAuthService commonAuthService;

    @RequestMapping("/address/list")
    @ResponseBody
    public Object getTakeawayAddressList(@Valid ApiRequest apiRequest) {
        Map<String, String> stringStringMap = commonAuthService.signCheck(apiRequest);
        @NotBlank(message = "数据[data]不可为空") String data = apiRequest.getData();
        Map<String, String> dataMap = JsonUtils.toObj(data, Map.class);
        String orderType = StringUtils.obj2str(dataMap.get("order_type"));
        List<TakeawayAddressDTO> fbTakeawayAddressList = funcTakeawayService.getFbTakeawayAddressList(stringStringMap.get("company_id"), stringStringMap.get("employee_id"),stringStringMap.get("employee_type"), orderType);
        return FuncResponseUtils.success(fbTakeawayAddressList);
    }
}
