package com.fenbeitong.openapi.plugin.wechat.isv.controller;

import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lizhen on 2020/12/21.
 */
@RestController
@RequestMapping("/wechat/isv/tencent/cloud")
public class WeChatIsvTencentCloudCallbackController {

    @RequestMapping("/callback")
    private Object commandCallback(HttpServletRequest request, @RequestBody String data, String signature,
                                   String timestamp, String eventId) throws Exception {
        Map map = JsonUtils.toObj(data, Map.class);
        Map<String, Object> result = new HashMap<>();
        String action = StringUtils.obj2str(map.get("action"));
        if ("verifyInterface".equals(action)) {
            result.put("echoback", StringUtils.obj2str(map.get("echoback")));
        } else if("createInstance".equals(action)) {
            result.put("signId", "36441d902ba");
        } else {
            result.put("success", "ture");
        }
        return result;
    }


}
