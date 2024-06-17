package com.fenbeitong.openapi.plugin.customize.chenguangrongxin.service;

/**
 * @ClassName IBillService
 * @Description 推送辰光融信账单数据
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/9/19 下午9:10
 **/
public interface IBillService {

     void pushBillData(String companyId, String billNo);
}
