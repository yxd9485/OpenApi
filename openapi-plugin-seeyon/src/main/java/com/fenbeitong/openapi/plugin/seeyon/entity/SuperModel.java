package com.fenbeitong.openapi.plugin.seeyon.entity;


import com.fenbeitong.openapi.plugin.seeyon.helper.BeanHelper;
import com.fenbeitong.openapi.plugin.util.JsonUtils;

/**
 * BaseModel
 *
 * <p>Base Model
 *
 * <p>Each Model should extends this
 *
 * @author ivan
 * @version 1.0 Created by ivan on 12/13/18 - 7:26 PM.
 */
public class SuperModel implements java.io.Serializable {

  private static final long serialVersionUID = 4473095782318771317L;

  public <T> T beanToBean(Class<T> targetClass) {
    return BeanHelper.beanToBean(this, targetClass);
  }

  public String presentJsonData() {
    return JsonUtils.toJson(this);
  }
}
