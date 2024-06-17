package com.fenbeitong.openapi.plugin.yunzhijia.service;

/**
 * 公共号相关接口
 * @Auther zhang.peng
 * @Date 2021/7/30
 */
public interface IYunzhijiaPublicNoticeService {

    boolean isSubscribe(String pubid, String eid);

    boolean companySubscribe(String pubid,String eid,String ssb, String pubtoken,long time);
}
