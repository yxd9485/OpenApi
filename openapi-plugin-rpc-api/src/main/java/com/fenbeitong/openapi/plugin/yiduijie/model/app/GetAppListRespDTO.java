package com.fenbeitong.openapi.plugin.yiduijie.model.app;

import lombok.*;

import java.io.Serializable;

/**
 * <p>Title: GetAppListRespDTO</p>
 * <p>Description: 获取应用列表</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 2:12 PM
 */
@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetAppListRespDTO implements Serializable {

    /**
     * 应用id  app42
     */
    private String appId;

    /**
     * 应用标题 用友NC对接分贝通
     */
    private String title;

}
