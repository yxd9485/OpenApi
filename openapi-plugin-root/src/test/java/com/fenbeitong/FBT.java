package com.fenbeitong;

import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.support.util.SignTool;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.Operator;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: Teest</p>
 * <p>Description: 撤销</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/6/30 7:47 下午
 */
public class FBT {

    public static void main(String[] args) {
        cancel();
    }

    /**
     * 订单批量撤销
     */
    public static void cancel() {
        String str = "60d33320f2f61b4291dddc04, 60d1e038718cd50a36691326, 60d08d51f6b3ba16fe14d448, 60cf3a68f6b3ba16fe12f432, 60cde781f6b3ba16fe11f2c5, 60cddf106976b93431a94e87, 60cdd6a03e9753397b30d6f3, 60cdce306976b93431a93f64, 60cdc5c07167083a9002b0db, 60cdbd503e9753397b30bcc9, 60cdb4e0cbcf4b4617fc681a, 60cdac707167083a90029272, 60cda40053da9507ada88b6e, 60cd9b90f6b3ba16fe11a19a, 60cd93203e9753397b308dd8, 60cd8ab053da9507ada86ed1, 60cd824053da9507ada8638d, 60cd79d06976b93431a8df53, 60cd7160f6b3ba16fe116af2, 60cd68f053da9507ada83fc6, 60cd60806976b93431a8bd27, 60cd5811f6b3ba16fe1148ca, 60cd4fa053da9507ada81e64, 60cd47307167083a9002172a, 60cd3ec03e9753397b302425, 60cd36503e9753397b301b7b, 60cd2de0cbcf4b4617fbcb0e, 60cd2570f6b3ba16fe1116a8, 60cd1d00f6b3ba16fe111476, 60cd1490f6b3ba16fe1112b1, 60cd0c20f6b3ba16fe111192, 60cd03b07167083a9001f44d, 60ccfb413e9753397b300ab3, 60ccf2d153da9507ada7ef1c, 60ccea60f6b3ba16fe110dfb, 60cce1f0cbcf4b4617fbbd68, 60ccd980f6b3ba16fe110a9c, 60ccd11053da9507ada7e6e0, 60ccc8a06976b93431a86fa7, 60ccc030cbcf4b4617fbad0c, 60ccb7c07167083a9001d862, 60ccaf5053da9507ada7c898, 60cca6e053da9507ada7b923, 60cc9e706976b93431a837bf, 60cc96006976b93431a82811, 60cc8d90f6b3ba16fe10ac6b, 60cc852053da9507ada77f1e, 60cc7cb0cbcf4b4617fb4297, 60cc74403e9753397b2f794c, 60cc6bd07167083a90014e94, 60cc63607167083a90013916, 60cc5af06976b93431a7ac4a, 60cc5281cbcf4b4617fad9eb, 60cc4a10f6b3ba16fe100f5f, 60cc41a0cbcf4b4617fab1cd, 60cc39303e9753397b2ee68d, 60cc30c053da9507ada6af3b, 60cc28516976b93431a73445, 60cc1fe0f6b3ba16fe0fab44, 60cc17703e9753397b2e99b3, 60cc0f0053da9507ada65fc1, 60cc0690f6b3ba16fe0f6dfc, 60cbfe206976b93431a6d537, 60cbf5b053da9507ada62653, 60cbed4053da9507ada61447, 60cbe4d06976b93431a6a254, 60cbdc606976b93431a6987d, 60cbd3f07167083a900008d5, 60cbcb80cbcf4b4617f9d2d8, 60cbc310f6b3ba16fe0f0fdb, 60cbbaa0f6b3ba16fe0f0ea6, 60cbb2303e9753397b2e170f, 60cba9c03e9753397b2e165a, 60cba1513e9753397b2e1578, 60cb98e053da9507ada5ebae, 60cb9070cbcf4b4617f9cb30, 60cb8800cbcf4b4617f9c975, 60cb7f90f6b3ba16fe0f046a, 60cb772053da9507ada5e117, 60cb6eb03e9753397b2e0484, 60cb66407167083a90ffe65b, 60cb5dd03e9753397b2dee53, 60cb5560f6b3ba16fe0ecfe6, 60cb4cf153da9507ada59fd9, 60cb4480dc645d6932014193, 60cb3c1033d2a72ee3722bed, 60cb33a09ed29359ca3f6087, 60cb2b30eb71697d09645286, 60cb22c09ed29359ca3f3a09, 60cb1a50eb71697d096425da, 60cb11e033d2a72ee371c6ea, 60cb0970dc645d693200b8e3, 60cb0100dc645d693200a32f, 60caf890d392fb5972ad598c, 60caf0209ed29359ca3eb3f8, 60cae7b0d392fb5972ad2fcf, 60cadf40dc645d6932004f18, 60cad6d133d2a72ee37132b2, 60cace6033d2a72ee3711f5b, 60cac5f0eb71697d09634c4f, 60cabd809ed29359ca3e3325, 60cab5109ed29359ca3e202f, 60caaca0eb71697d09630f40, 60caa430dc645d6932ffc435, 60ca9bc0eb71697d0962e652, 60ca93509ed29359ca3dd1ce, 60ca8ae0ce933351dbde3d0d, 60ca8270dc645d6932ff8ed4, 60ca7a00ce933351dbde3340, 60ca719033d2a72ee3707d30, 60ca6920ce933351dbde2f3e, 60ca60b0eb71697d0962ba5e, 60ca58409ed29359ca3db5b3, 60ca4fd0dc645d6932ff8468, 60ca4760dc645d6932ff8392, 60ca3ef0eb71697d0962b6fe, 60ca3680eb71697d0962b569, 60ca2e10eb71697d0962b22f, 60ca25a033d2a72ee3706ea6, 60ca1d3033d2a72ee3706954, 60ca14c0d392fb5972ac370d, 60ca0c50dc645d6932ff5f05, 60ca03e1eb71697d09627e72, 60c9fb709bdf54077fc05a0c, 60c9f3009bdf54077fc043f4, 60c9f19806adc313313ffc1a, 60c9f030eb206325cc6fd0f7";
        String[] ss = str.split(",");
        Arrays.asList(ss).forEach(t ->
        {
            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
            Map<String, Object> dataMap = new HashMap<>(2);
            dataMap.put("apply_id", t.trim());
            dataMap.put("third_type", 1);
            String data = gson.toJson(dataMap);
            MultiValueMap params = genApiAuthParamsWithEmployee(data, "60c8eaaea614100525a50882", true);
            params.add("data", data);
            String jsonText = RestHttpUtils.postFormUrlEncode("http://open.fenbeitong.com/open/api/approve/cancel", null, params);
        });

    }

    public static MultiValueMap genApiAuthParamsWithEmployee(String data, String employeeId, boolean fbtEmployee) {
        MultiValueMap params = new LinkedMultiValueMap();
        long timestamp = System.currentTimeMillis();
        String sign = SignTool.genSign(timestamp, data, "fc8aaf23b44757fc19dea612f82fbb45");
        String accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0b2tlbiIsImFwcElkIjoiNWYxMmFhMjE5M2M2ZDYyNzg1OGMxMGI0IiwiaXNzIjoiYXBpIiwiZXhwIjoxNjI1OTIzOTY0LCJqdGkiOiI2MGRjNzI3YzY5ZmI3NTFlYzA5N2ZjZjcifQ.HhB5UWbE39PEf-Ulgqb4IRa-lzDkvu5i69DGu5fc0eg";
        String employeeType = fbtEmployee ? "0" : "1";
        params.add("timestamp", String.valueOf(timestamp));
        params.add("access_token", accessToken);
        params.add("sign", sign);
        params.add("employee_id", employeeId);
        params.add("employee_type", employeeType);
        return params;
    }




}
