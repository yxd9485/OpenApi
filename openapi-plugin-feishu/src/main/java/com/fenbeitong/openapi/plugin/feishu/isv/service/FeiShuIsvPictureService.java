package com.fenbeitong.openapi.plugin.feishu.isv.service;

import com.fenbeitong.finhub.common.utils.OssHandler;
import com.fenbeitong.openapi.plugin.feishu.eia.service.FeiShuEiaCompanyAuthService;
import com.fenbeitong.openapi.plugin.feishu.isv.entity.FeishuIsvCompany;
import com.fenbeitong.openapi.plugin.feishu.isv.util.FeiShuIsvHttpUtils;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.io.File;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@ServiceAspect
@Service
@Slf4j
public class FeiShuIsvPictureService {

    @Value("${feishu.api-host}")
    private String feishuHost;

    @Value("${feishu.upload-pic}")
    private String uploadPic;

    @Autowired
    private RestHttpUtils httpUtil;

    @Autowired
    private FeiShuIsvHttpUtils feiShuIsvHttpUtils;

    @Autowired
    protected OssHandler ossHandler;

    @Autowired
    private FeiShuIsvCompanyDefinitionService feiShuIsvCompanyDefinitionService;

    @Autowired
    private FeiShuIsvCompanyAuthService feiShuIsvCompanyAuthService;


    /**
     * 飞书上传图片
     * @return
     */
    public String uploadPic(String object){
//        Map map = JsonUtils.toObj(object, Map.class);
//        String uploadUrl=map.get("uploadUrl")!=null?map.get("uploadUrl").toString():null;
//        String companyId=map.get("companyId")!=null?map.get("companyId").toString():null;
//        File file = ossHandler.getObjectToTemp(uploadUrl);
//        FileBody bin = new FileBody(file);
//        Map<String, Object> param = new HashMap<>();
//        param.put("image", bin);
//        param.put("image_type", "message");
//        FeishuIsvCompany feishuIsvCompany=feiShuIsvCompanyDefinitionService.getFeiShuIsvCompanyByCompanyId(companyId);
//        if(feishuIsvCompany==null){
//            return "公司还没有关联到飞书";
//        }
//        String res = feiShuIsvHttpUtils.postJsonWithTenantAccessToken(feishuHost.concat(uploadPic),JsonUtils.toJson(param),feishuIsvCompany.getCorpId());
//
//        JSONObject jsonObject = JSONObject.parseObject(res);
//        Integer errcode = jsonObject.getInteger("code");
//        if (errcode != null && (errcode == 0 )) {
//            log.info("上传图片成功");
//            JSONObject data = JSONObject.parseObject(jsonObject.get("data").toString());
//            return data.getString("image_key");
//        }else {
//            log.info("上传图片失败,图片路径为{}",uploadUrl);
//        }
//        return "上传图片失败";

        Map map = JsonUtils.toObj(object, Map.class);
        String uploadUrl=map.get("uploadUrl")!=null?map.get("uploadUrl").toString():null;
        String companyId=map.get("companyId")!=null?map.get("companyId").toString():null;
        FeishuIsvCompany feishuIsvCompany=feiShuIsvCompanyDefinitionService.getFeiShuIsvCompanyByCompanyId(companyId);
        if(feishuIsvCompany==null){
            return "公司还没有关联到飞书";
        }
        String tenantAccessToken = feiShuIsvCompanyAuthService.getTenantAccessTokenByCorpId(feishuIsvCompany.getCorpId());
        uploadUrl=uploadUrl.split("com/")[1];
        File file = ossHandler.getObjectToTemp(uploadUrl);
        CloseableHttpClient client = getHttpClient();
        HttpPost post = new HttpPost(feishuHost.concat(uploadPic));
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        FileBody bin = new FileBody(file);
        builder.addPart("image", bin);
        builder.addTextBody("image_type", "message");
        HttpEntity multiPartEntity = builder.build();
        post.setEntity(multiPartEntity);
        post.setHeader("Authorization", "Bearer " + tenantAccessToken);
        try {
            CloseableHttpResponse response = client.execute(post);
            System.out.println("http response code:" + response.getStatusLine().getStatusCode());
            for (Header header: response.getAllHeaders()) {
                System.out.println(header.toString());
            }
            HttpEntity resEntity = response.getEntity();

            if (resEntity == null) {
                System.out.println("never here?");
                return "";
            }
            System.out.println("Response content length: " + resEntity.getContentLength());
            return EntityUtils.toString(resEntity);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public static CloseableHttpClient getHttpClient() {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build(),
                    NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new PlainConnectionSocketFactory())
                    .register("https", sslConnectionSocketFactory)
                    .build();
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(100);
            CloseableHttpClient httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslConnectionSocketFactory)
                    .setDefaultCookieStore(new BasicCookieStore())
                    .setConnectionManager(cm).build();
            return httpclient;
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }
}
