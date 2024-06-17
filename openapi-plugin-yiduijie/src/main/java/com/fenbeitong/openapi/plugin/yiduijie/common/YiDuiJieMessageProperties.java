package com.fenbeitong.openapi.plugin.yiduijie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * <p>Title: YiDuiJieMessageProperties</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/03/10 12:08 PM
 */
@Slf4j
public class YiDuiJieMessageProperties {

    private final static Properties pro = new Properties();

    static {
        InputStream in = null;
        BufferedReader bf = null;
        try {
            ClassPathResource resource = new ClassPathResource("prop/yiduijie.message.properties");
            in = resource.getInputStream();
            //解决中文乱码
            bf = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            pro.load(bf);
        } catch (IOException e) {
            log.warn("流读取异常", e);
        } finally {
            try{
                if (in != null) {
                    in.close();
                }
                if (bf != null) {
                    bf.close();
                }
            } catch (IOException e) {
                log.warn("流关闭异常", e);
            }
        }
    }

    public static String getProperty(String key) {
        return pro.getProperty("yiduijie.msg." + key);
    }
}
