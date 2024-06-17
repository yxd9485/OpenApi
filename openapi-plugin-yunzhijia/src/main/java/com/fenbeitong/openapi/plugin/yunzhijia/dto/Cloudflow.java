package com.fenbeitong.openapi.plugin.yunzhijia.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.yunzhijia.utils.AESEncryptor;
import okhttp3.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class Cloudflow {
    private OkHttpClient client = new OkHttpClient();

    private CloudflowConfiguration configuration;
    private String accessToken;
    private String refreshToken;
    private long expiredUntil;

    private String fileAccessToken;

    public Cloudflow(CloudflowConfiguration configuration) {
        this.configuration = configuration;
    }

    public static void main(String[] args) {
        CloudflowConfiguration configuration = new CloudflowConfiguration();
        // 开发者设置页面可查询【请改为自己的appId】
        configuration.appId = "SP9977917";
        // 开发者设置页面可查询【请改为自己的开发者secret】
        configuration.secret = "OtTnxerSNoD5mQGhKVf4TxAjqhxLGw";
        // 开发者设置页面可查询【请改为自己的开发者key】
        configuration.key = "T22cdkEko3flglPe";
        // 在云之家首页右上角点击我的团队可查询【请改为自己的eid】
        configuration.eid = "9977917";
        // 文件集成secret(管理中心->系统设置->系统集成->文件服务上传下载)
        configuration.fileSecret = "oCHgV1ECUdTZlRaRUWwCSS65XxCPOYdD";

        Cloudflow cloudflow = new Cloudflow(configuration);
        cloudflow.getAccessToken();
        System.out.println(cloudflow.accessToken);
        cloudflow.refreshAccessToken();
        System.out.println(cloudflow.refreshToken);
    }


    private String post(String url, String param) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, param);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cache-Control", "no-cache")
                .build();

        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // 获取accessToken
    // https://yunzhijia.com/cloudflow-openplatform/before/1004
    private void getAccessToken() {
        JSONObject param = new JSONObject();
        param.put("appId", configuration.appId);
        param.put("eid",configuration.eid);
        param.put("secret",configuration.secret);
        param.put("timestamp", System.currentTimeMillis());
        param.put("scope","team");

        String url = "https://yunzhijia.com/gateway/oauth2/token/getAccessToken";
        String retString = post(url, param.toJSONString());

        JSONObject jsonObject = JSON.parseObject(retString);
        System.out.println("获取到accessToken:\n" + jsonObject);
        if (jsonObject.getBooleanValue("success")) {
            JSONObject data = jsonObject.getJSONObject("data");
            accessToken = data.getString("accessToken");
            refreshToken = data.getString("refreshToken");
            expiredUntil = System.currentTimeMillis() + data.getIntValue("expireIn") * 1000;
        }
    }

    // 刷新accessToken
    // https://open.yunzhijia.com/openplatform/resourceCenter/doc#/gitbook-wiki/server-api/accessToken.html
    private void refreshAccessToken() {
        JSONObject param = new JSONObject();
        param.put("appId", configuration.appId);
        param.put("eid", configuration.eid);
        param.put("refreshToken", refreshToken);
        param.put("timestamp", System.currentTimeMillis());
        param.put("scope", "team");

        String url = "https://www.yunzhijia.com/gateway/oauth2/token/refreshToken";
        String retString = post(url, param.toJSONString());

        JSONObject jsonObject = JSON.parseObject(retString);
        System.out.println("刷新accessToken:\n" + jsonObject);
        if (jsonObject.getBooleanValue("success")) {
            JSONObject data = jsonObject.getJSONObject("data");
            refreshToken = data.getString("refreshToken");
            accessToken = data.getString("accessToken");
            expiredUntil = System.currentTimeMillis() + data.getIntValue("expireIn") * 1000;
        }
    }

    /**
     * 如果当前时间已经大于accessToken过期时间则刷新accessToken
     */
    private void checkAndRefreshAccessToken() {
        if (expiredUntil == 0L) {
            getAccessToken();
        } else if (System.currentTimeMillis() >= expiredUntil) {
            refreshAccessToken();
        }
    }

    /**
     * 获取单据实例
     * <p>
     * 参考:https://yunzhijia.com/cloudflow-openplatform/other/3003
     *
     * @param param
     * @return
     */
    public JSONObject getFormInstance(Map<String, String> param) {
        checkAndRefreshAccessToken();
        String url = "https://yunzhijia.com/gateway/workflow/form/thirdpart/viewFormInst?accessToken=";
        url += accessToken;
        String retString = post(url, JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(retString);
        System.out.println("获取单据实例:\n" + jsonObject);
        return jsonObject;
    }

    /**
     * 获取审批痕迹
     * <p>
     * 参考:https://yunzhijia.com/cloudflow-openplatform/other/3004
     *
     * @param param
     * @return
     */
    public JSONObject getFlowRecord(Map<String, String> param) {
        checkAndRefreshAccessToken();
        String url = "https://yunzhijia.com/gateway/workflow/form/thirdpart/getFlowRecord?accessToken=";
        url += accessToken;
        String retString = post(url, JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(retString);
        System.out.println("获取审批痕迹:\n" + jsonObject);
        return jsonObject;
    }

    /**
     * 发起审批
     * <p>
     * 参考:https://yunzhijia.com/cloudflow-openplatform/other/3005
     *
     * @param param
     * @return
     */
    public JSONObject createInst(Map<String, Object> param) {
        checkAndRefreshAccessToken();
        String url = "https://yunzhijia.com/gateway/workflow/form/thirdpart/createInst?accessToken=";
        url += accessToken;
        String retString = post(url, JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(retString);
        System.out.println("发起审批:\n" + jsonObject);
        return jsonObject;
    }

    /**
     * 修改表单
     * <p>
     * 注意修改表单不要违反操作人所对应的节点字段权限，
     * 如不能编辑只读字段、必填字段必须要传等等
     * <p>
     * 参考:https://yunzhijia.com/cloudflow-openplatform/other/3006'
     *
     * @param param
     * @return
     */
    public JSONObject modifyInst(Map<String, Object> param) {
        checkAndRefreshAccessToken();
        String url = "https://yunzhijia.com/gateway/workflow/form/thirdpart/modifyInst?accessToken=";
        url += accessToken;
        String retString = post(url, JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(retString);
        System.out.println("修改表单:\n" + jsonObject);
        return jsonObject;
    }

    /**
     * 获取使用了互联控件的模版列表
     * <p>
     * 参考:https://yunzhijia.com/cloudflow-openplatform/other/3001
     *
     * @param param
     * @return
     */
    public JSONObject getTemplateListByGroupId(Map<String, Object> param) {
        checkAndRefreshAccessToken();
        String url = "https://yunzhijia.com/gateway/workflow/form/thirdpart/getByGroupId?accessToken=";
        url += accessToken;
        String retString = post(url, JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(retString);
        System.out.println("获取使用了互联控件的模版列表:\n" + jsonObject);
        return jsonObject;
    }

    /**
     * 获取模版
     * <p>
     * 参考:https://yunzhijia.com/cloudflow-openplatform/other/3002
     *
     * @param param
     * @return
     */
    public JSONObject getTemplateByCodeId(Map<String, String> param) {
        checkAndRefreshAccessToken();
        String url = "https://yunzhijia.com/gateway/workflow/form/thirdpart/viewFormDef?accessToken=";
        url += accessToken;
        String retString = post(url, JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(retString);
        System.out.println("获取模版:\n" + jsonObject);
        return jsonObject;
    }

    /**
     * 解密推送数据()
     * <p>
     * 参考:https://yunzhijia.com/cloudflow-openplatform/external/2003
     *
     * @param cipher
     * @return
     */
    public JSONObject decryptNotification(String cipher) {
        if (cipher == null || cipher.isEmpty()) {
            return null;
        }

        AESEncryptor encryptor = new AESEncryptor(configuration.key);
        String plainText = encryptor.decrypt(cipher);
        return JSON.parseObject(plainText);
    }

    /**
     * 获取审批状态
     * <p>
     * 参考:https://yunzhijia.com/cloudflow-openplatform/other/3010
     *
     * @param param
     * @return
     */
    public JSONObject getFlowStatus(Map<String, String> param) {
        checkAndRefreshAccessToken();
        String url = "https://www.yunzhijia.com/gateway/workflow/form/thirdpart/getFlowStatus?accessToken=";
        url += accessToken;
        String retString = post(url, JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(retString);
        System.out.println("获取审批状态:\n" + jsonObject);
        return jsonObject;
    }
    
    /**
     * 获取流程监控可查询模板
     * <p>
     * 参考:https://yunzhijia.com/cloudflow-openplatform/other/3012
     * 
     * @param param
     * @return
     */
    public JSONObject getTemplates(Map<String, Object> param) {
    	checkAndRefreshAccessToken();
        String url = "https://www.yunzhijia.com/gateway/workflow/form/thirdpart/getTemplates?accessToken=";
        url += accessToken;
        String retString = post(url, JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(retString);
        System.out.println("获取模板:\n" + jsonObject);
        return jsonObject;
    }
    
    /**
     * 获取流程数据
     * <p>
     * 参考:https://yunzhijia.com/cloudflow-openplatform/other/3013
     * 
     * @param param
     * @return
     */
    public JSONObject findFlows(Map<String, Object> param) {
    	checkAndRefreshAccessToken();
        String url = "https://www.yunzhijia.com/gateway/workflow/form/thirdpart/findFlows?accessToken=";
        url += accessToken;
        String retString = post(url, JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(retString);
        System.out.println("获取流程列表:\n" + jsonObject);
        return jsonObject;
    }

    //////////////////////// 文件上传下载

    /**
     * 获取文件上传下载accessToken
     * <p>
     * https://yunzhijia.com/cloudflow-openplatform/fileUploadAndDownload/4001
     */
    private void getFileAccessToken() {
        JSONObject param = new JSONObject();
        param.put("eid", configuration.eid);
        param.put("secret", configuration.fileSecret);
        param.put("scope", "resGroupSecret");
        param.put("timestamp", System.currentTimeMillis());

        String url = "https://www.yunzhijia.com/gateway/oauth2/token/getAccessToken";
        String retString = post(url, param.toJSONString());

        JSONObject jsonObject = JSON.parseObject(retString);
        System.out.println("获取file accessToken:\n" + jsonObject);
        if (jsonObject.getBooleanValue("success")) {
            JSONObject data = jsonObject.getJSONObject("data");
            fileAccessToken = data.getString("accessToken");
        }
    }

    /**
     * 文件上传
     * <p>
     * https://yunzhijia.com/cloudflow-openplatform/fileUploadAndDownload/4002
     *
     * @return
     */
    public JSONObject uploadFile() {
        getFileAccessToken();

        String url = "https://www.yunzhijia.com/docrest/doc/file/uploadfile";
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(Paths.get("testupload.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        MediaType fileType = MediaType.parse("application/octet-stream");
        RequestBody fileBody = RequestBody.create(fileType, bytes);
        RequestBody body = new MultipartBody.Builder()
                .addFormDataPart("file", "testupload.txt", fileBody)
                .addFormDataPart("bizkey", "cloudflow")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("content-type", "multipart/form-data")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("x-accessToken", fileAccessToken)
                .build();

        try {
            Response response = client.newCall(request).execute();
            JSONObject jsonObject = JSON.parseObject(response.body().string());
            System.out.println("文件上传\n" + jsonObject);
            return jsonObject;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 文件下载
     * <p>
     * https://yunzhijia.com/cloudflow-openplatform/fileUploadAndDownload/4003
     *
     * @param fileId
     */
    public void downloadFile(String fileId) {
        getFileAccessToken();

        String url = "https://www.yunzhijia.com/docrest/doc/user/downloadfile?bizkey=cloudflow&fileId=";
        url += fileId;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("x-accessToken", fileAccessToken)
                .addHeader("Cache-Control", "no-cache")
                .build();

        try {
            Response response = client.newCall(request).execute();
            try (InputStream is = response.body().byteStream();
                 FileOutputStream fos = new FileOutputStream("download.txt")) {
                int length;
                byte[] buf = new byte[2048];
                while ((length = is.read(buf)) != -1) {
                    fos.write(buf, 0, length);
                }
                fos.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
