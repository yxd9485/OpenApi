package com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.constant;

public interface FxkConstant {

    interface organization {
        // 组织架构顶级id
        String ORGANIZATION_TOP_ID = "0";
    }
    interface webapp {
        // webapp首页
        String EIA_WEB_APP_HOME = "/fxiaokeLogin?appId=%s&";
        // 纷享销客重定向时的地址
        String EIA_WEB_APP_REDIRECT_URL="/fxiaokeRedirectLogin?code=%s&state=%s";
    }

}
