package com.fenbeitong.openapi.plugin.ecology.v8.util;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.http.HttpRequest;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.ecology.v8.common.EcologyConstant;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 泛微 REST 接口工具
 * @Auther zhang.peng
 * @Date 2021/12/31
 */
@Service
@ServiceAspect
@Slf4j
public class EcologyRestApiUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 第一步：
     *
     * 调用ecology注册接口,根据appid进行注册,将返回服务端公钥和Secret信息
     */
    public Map<String,String> doRegister(String address , String appId ){

        //获取当前系统RSA加密的公钥
        String publicKey = "";
        String privateKey = "";

        String localPrivateKey = MessageFormat.format(RedisKeyConstant.ECOLOGY_REST_INTERFACE_INFO, EcologyConstant.LOCAL_PRIVATE_KEY);
        String localPublicKey = MessageFormat.format(RedisKeyConstant.ECOLOGY_REST_INTERFACE_INFO, EcologyConstant.LOCAL_PUBLIC_KEY);
        // 缓存逻辑
        if ( null != redisTemplate.opsForValue().get(localPrivateKey) && null != redisTemplate.opsForValue().get(localPublicKey) ){
            publicKey = (String) redisTemplate.opsForValue().get(localPublicKey);
        } else {
            RSA rsa = new RSA();

            privateKey = rsa.getPrivateKeyBase64();
            redisTemplate.opsForValue().set(localPrivateKey, privateKey, 1, TimeUnit.DAYS);

            publicKey = rsa.getPublicKeyBase64();
            redisTemplate.opsForValue().set(localPublicKey, publicKey, 1, TimeUnit.DAYS);
        }
        log.info(" appId : {}, publicKey : {}",appId,publicKey);

        //调用ECOLOGY系统接口进行注册
        String data = HttpRequest.post(address + "/api/ec/dev/auth/regist")
            .header("appid",appId)
            .header("cpk",publicKey)
            .timeout(2000)
            .execute().body();

        // 打印ECOLOGY响应信息
        log.info("注册接口响应信息 : {}",data);
        Map<String,String> datas = JsonUtils.toObj(data,Map.class);

        if ( null != datas ){
            log.info("SERVER_PUBLIC_KEY : {}",datas.get("spk"));
            log.info("SERVER_SECRET : {}",datas.get("secrit"));
            // 加入缓存
            String secretPublicKey = MessageFormat.format(RedisKeyConstant.ECOLOGY_REST_INTERFACE_INFO, EcologyConstant.SECRET_PUBLIC_KEY);
            String secretKey = MessageFormat.format(RedisKeyConstant.ECOLOGY_REST_INTERFACE_INFO, EcologyConstant.SECRET);
            redisTemplate.opsForValue().set(secretKey, datas.get("secrit"), 1, TimeUnit.DAYS);
            redisTemplate.opsForValue().set(secretPublicKey, datas.get("spk"), 1, TimeUnit.DAYS);
        }
        return datas;
    }

    /**
     * 第二步：
     *
     * 通过第一步中注册系统返回信息进行获取token信息
     */
    public Map<String,String> getToken(String address , String appId){
        // 从系统缓存或者数据库中获取ECOLOGY系统公钥和Secret信息
        String secret = "";
        String spk = "";
        String secretKey = MessageFormat.format(RedisKeyConstant.ECOLOGY_REST_INTERFACE_INFO, EcologyConstant.SECRET);
        String secretPublicKey = MessageFormat.format(RedisKeyConstant.ECOLOGY_REST_INTERFACE_INFO, EcologyConstant.SECRET_PUBLIC_KEY);
        String secretValue = (String) redisTemplate.opsForValue().get(secretKey);
        String secretPublicValue = (String) redisTemplate.opsForValue().get(secretPublicKey);
        secret = secretValue;
        spk = secretPublicValue;

        // 如果为空,说明还未进行注册,调用注册接口进行注册认证与数据更新
        if ( StringUtils.isBlank(secret) || StringUtils.isBlank(spk) ){
            doRegister(address,appId);
            // 重新获取最新ECOLOGY系统公钥和Secret信息
            secret = secretValue;
            spk = secretPublicValue;
        }

        // 公钥加密,所以RSA对象私钥为null
        RSA rsa = new RSA(null,spk);
        //对秘钥进行加密传输，防止篡改数据
        String encryptSecret = rsa.encryptBase64(secret, CharsetUtil.CHARSET_UTF_8, KeyType.PublicKey);
        log.info(" appId :{} , secret : {}",appId,encryptSecret);
        cookieStore.clear();
        //调用ECOLOGY系统接口进行注册
        String data = HttpRequest.post(address+ "/api/ec/dev/auth/applytoken")
            .header("appid",appId)
            .header("secret",encryptSecret)
            .header("time","3600")
            .execute().body();

        log.info("getToken : {}",data);
        Map<String,String> datas = JsonUtils.toObj(data,Map.class);

        //ECOLOGY返回的token
        if ( null != datas.get("token") ){
            String tokenKey = MessageFormat.format(RedisKeyConstant.ECOLOGY_REST_INTERFACE_INFO, EcologyConstant.SERVER_TOKEN);
            // token 的有效期是 30 分钟，这里设置 29 分钟
            redisTemplate.opsForValue().set(tokenKey, datas.get("token"), 29, TimeUnit.MINUTES);
        }

        return datas;
    }

    public static CookieStore cookieStore = new BasicCookieStore();

    private CloseableHttpClient getHttpClientSSL() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext sslcontext = null;
        try {
            sslcontext = createIgnoreVerifySSL();
        } catch (Exception ex) {
            //logger.error(ex.getMessage());
        }
        Registry<ConnectionSocketFactory> socketFactoryRegistry;
        if (sslcontext != null){
            socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslcontext))
                .build();
        } else {
            socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .build();
        }

        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        HttpClients.custom().setConnectionManager(connManager);

        RequestConfig defaultRequestConfig = RequestConfig.custom()
            .setSocketTimeout(30000)
            .setConnectTimeout(30000)
            .setConnectionRequestTimeout(30000)
            .setStaleConnectionCheckEnabled(true)
            .build();

        CloseableHttpClient client = HttpClients.custom()
            .setConnectionManager(connManager)
            .setDefaultCookieStore(cookieStore)
            .setDefaultRequestConfig(defaultRequestConfig)
            .build();
        return client;
    }

    private CloseableHttpClient getHttpClient() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext sslcontext = createIgnoreVerifySSL();

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.INSTANCE)
            .build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        HttpClients.custom().setConnectionManager(connManager);

        RequestConfig defaultRequestConfig = RequestConfig.custom()
            .setSocketTimeout(5000)
            .setConnectTimeout(5000)
            .setConnectionRequestTimeout(5000)
            .setStaleConnectionCheckEnabled(true)
            .build();

        CloseableHttpClient client = HttpClients.custom()
            .setConnectionManager(connManager)
            .setDefaultCookieStore(cookieStore)
            .setDefaultRequestConfig(defaultRequestConfig)
            .build();
        return client;
    }

    /**
        * 绕过验证
     *
         * @return
         * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }

    public String getDataSSL(String url, Map<String, String> param, Map<String, String> headers) throws KeyManagementException, NoSuchAlgorithmException {
        String result = null;
        CloseableHttpClient httpClient = getHttpClientSSL();

        CloseableHttpResponse response = null;
        try {
            URIBuilder builder = new URIBuilder(url);

            for (Map.Entry<String, String> entry : param.entrySet()) {
                builder.addParameter(entry.getKey(), entry.getValue());
            }


            HttpGet get = new HttpGet(builder.build());

            if (headers != null)
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    get.setHeader(entry.getKey(), entry.getValue());
                }

            response = httpClient.execute(get);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            } else {
                log.info("接口失败 : {}",response);
            }
            return result;
        } catch (URISyntaxException | ClientProtocolException | HttpHostConnectException e) {
            log.info("请求地址:" + url + ",请求参数：" + param + ",响应数据：" + result);
            e.printStackTrace();
            throw new KeyManagementException(e);
        } catch (IOException e) {
            log.info("请求地址:" + url + ",请求参数：" + param + ",响应数据：" + result);
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * SSL协议发送post请求，参数用map接收
     *
     * @param url   地址
     * @param param 参数
     * @return 返回值
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public String postDataSSL(String url, Map<String, String> param, Map<String, String> headers) throws KeyManagementException, NoSuchAlgorithmException {
        String result = null;
        CloseableHttpClient httpClient = getHttpClientSSL();
        HttpPost post = new HttpPost(url);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        CloseableHttpResponse response = null;
        try {
            post.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));

            if (headers != null)
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    post.setHeader(entry.getKey(), entry.getValue());
                }

            response = httpClient.execute(post);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    private static String entityToString(HttpEntity entity) throws IOException {
        String result = null;
        if (entity != null) {
            long lenth = entity.getContentLength();
            if (lenth != -1 && lenth < 2048) {
                result = EntityUtils.toString(entity, "UTF-8");
            } else {
                InputStreamReader reader1 = new InputStreamReader(entity.getContent(), "UTF-8");
                CharArrayBuffer buffer = new CharArrayBuffer(2048);
                char[] tmp = new char[1024];
                int l;
                while ((l = reader1.read(tmp)) != -1) {
                    buffer.append(tmp, 0, l);
                }
                result = buffer.toString();
            }
        }
        return result;
    }

}
