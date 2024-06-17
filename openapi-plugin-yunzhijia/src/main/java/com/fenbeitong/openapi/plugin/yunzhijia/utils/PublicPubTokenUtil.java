package com.fenbeitong.openapi.plugin.yunzhijia.utils;

import com.fenbeitong.openapi.plugin.util.RandomUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaPubTokenDTO;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * 公共号工具
 * @Auther zhang.peng
 * @Date 2021/7/30
 */
public class PublicPubTokenUtil {

    /**
     * 获取公共号 token
     * @param corpId 团队 eid
     * @param pubId 公共号id
     * @param pubSecret 公共号秘钥
     * @return token
     */
    public static String getPubToken(String corpId , String pubId, String pubSecret,boolean hasRandom,long timestamp){
        String random = RandomUtils.bsonId();
        YunzhijiaPubTokenDTO pubTokenBuild = YunzhijiaPubTokenDTO.builder()
                .no(corpId)
                .pubId(pubId)
                .pubSecret(pubSecret)
                .time(String.valueOf(timestamp))
                .build();
        if (hasRandom){
            pubTokenBuild.setNonce(random);
            return sha(pubTokenBuild.getNo(),pubTokenBuild.getPubId(), pubTokenBuild.getPubSecret(), pubTokenBuild.getNonce(), pubTokenBuild.getTime());
        } else {
            return sha(pubTokenBuild.getNo(),pubTokenBuild.getPubId(), pubTokenBuild.getPubSecret(), pubTokenBuild.getTime());
        }
    }

    public static String sha(String... data) {
        Arrays.sort(data);
        String join = StringUtils.join(data);
        String pubtoken = DigestUtils.sha1Hex(join);
        return pubtoken;
    }
}
