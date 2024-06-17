package com.fenbeitong.openapi.plugin.dingtalk.yida.util;

import com.alibaba.xxpt.gateway.shared.client.http.ExecutableClient;
import com.fenbeitong.openapi.plugin.dingtalk.yida.dao.DingtalkYidaCorpDao;
import com.fenbeitong.openapi.plugin.dingtalk.yida.entity.DingtalkYidaCorp;
import com.fenbeitong.openapi.plugin.support.common.dao.OpenAuthoritySetDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: YiDaExecutableClientUtil</p>
 * <p>Description: ExecutableClient创建工具。按企业创建1个</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author lizhen
 * @date 2021/8/12 4:38 下午
 */

@Component
public class YiDaExecutableClientUtil {

    @Autowired
    private OpenAuthoritySetDao openAuthoritySetDao;

    @Autowired
    private DingtalkYidaCorpDao dingtalkYidaCorpDao;

    private static Map<String, ExecutableClient> map = new HashMap<String, ExecutableClient>();

    public ExecutableClient getInstance(String corpId) {
        ExecutableClient executableClient = map.get(corpId);
        if (executableClient == null) {
            synchronized (YiDaExecutableClientUtil.class) {
                executableClient = map.get(corpId);
                if (executableClient == null) {
                    DingtalkYidaCorp dingtalkYidaCorp = dingtalkYidaCorpDao.getDingtalkYidaCorpByCorpId(corpId);
                    executableClient = new ExecutableClient();
                    executableClient.setAccessKey(dingtalkYidaCorp.getAccessKey());
                    executableClient.setDomainName("s-api.alibaba-inc.com");
                    executableClient.setSecretKey(dingtalkYidaCorp.getSecretKey());
                    executableClient.setProtocal("https");
                    executableClient.init();
                    map.put(corpId, executableClient);
                }
            }
        }
        return executableClient;
    }


}
