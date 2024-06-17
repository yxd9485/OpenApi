package com.fenbeitong.openapi.plugin.customize.sync.service;

/**
 * @Description
 * @Author duhui
 * @Date 2021/8/5
 **/
public interface OrgAndProjectService {
   /**
    * @Description  组织架构全量更新
    * @param companyId 企业ID
    * @param topId 根部门ID
    * @Author duhui
    * @Date  2021/8/27
   **/
    String OrgSync(String companyId, String topId);


  /**
   * @Description  项目全量同步
   * @param companyId 企业ID
   * @param type open_customize_config表单TYPE
   * @param constraintUpdate 是否强制更新 true 强制更新 false不强制更新
   * @Author duhui
   * @Date  2021/8/27
  **/
    String ProjectSync(String companyId, Integer type, boolean constraintUpdate);
}
