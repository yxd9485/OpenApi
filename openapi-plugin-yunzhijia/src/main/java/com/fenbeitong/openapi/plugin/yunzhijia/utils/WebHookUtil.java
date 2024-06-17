package com.fenbeitong.openapi.plugin.yunzhijia.utils;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;


public class WebHookUtil { 

	public static String getAuthorization(String appid,String appkey,String contentMd5,String contentType,String date){
		String auth = appid +":"+sha(appkey,contentMd5,contentType,date);
		return auth;
	}
	
	public static String getAuthorization(String token,String contentMd5,String contentType,String date){
		String auth = sha(token,contentMd5,contentType,date);
		return auth;
	}
	
	public static String sha(String... data){
        String str = "";
        int n = data.length;
        for (int i=0;i<n;i++){
            str =str+data[i];
        }
        return DigestUtils.shaHex(str);
    }	
	
	public static String getContentMd5(String content){
		return Base64.encodeBase64String(DigestUtils.md5(content));
	}
	
	
	public static String getGMTTime(){
		Date d=new Date();
		DateFormat format=new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",Locale.US);		
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		return format.format(d);
	}	
	
	public static Map<Object,Object> getHeaders(String appid,String appkey,String content,String contentType){
		Map<Object,Object> headers = new HashMap<Object,Object>();
		String contentMd5 = WebHookUtil.getContentMd5(content);
		String date = WebHookUtil.getGMTTime();
		
		headers.put("Content-Md5",contentMd5 );
		headers.put("Content-Type",contentType );
		headers.put("Date", date);
		headers.put("Authorization", WebHookUtil.getAuthorization(appid,appkey,contentMd5, contentType, date));
		headers.put("User-Agent", "kingdee yunzhijia webhook client-1.0");
		
		return headers;		
		
	}
	
	public static Map<String,Object> getCommonParams(String appid, String access_token){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("appid", appid);
		params.put("access_token", access_token);
		return params;
	}


	// 按key字段顺序排序，组装k1=v1&k2=v2形式
	public static String mapToString(Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		Set<String> keys = map.keySet();
		for (String key : keys) {
			sb.append(key).append("=").append(map.get(key)).append("&");
		}
		if (sb.length() > 0) {
			return sb.substring(0, sb.length() - 1);
		} else {
			return sb.toString();
		}
	}

}
