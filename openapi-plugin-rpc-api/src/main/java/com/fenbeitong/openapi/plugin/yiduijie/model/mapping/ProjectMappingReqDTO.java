package com.fenbeitong.openapi.plugin.yiduijie.model.mapping;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>Title: ProjectMappingReqDTO</p>
 * <p>Description: 项目映射请求</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/10 12:02 PM
 */
@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMappingReqDTO implements Serializable {

    @NotBlank(message = "三方映射ID(id)不可为空", groups = UpdateProjectGroup.class)
    private String id;

    /**
     * 分贝通项目编号
     */
    @NotBlank(message = "分贝通项目编号(business_project)不可为空")
    private String businessProject;

    /**
     * 财务项目编号
     */
    @NotBlank(message = "财务项目编号(finance_project)不可为空")
    private String financeProject;

    public interface UpdateProjectGroup {

    }

}
