package com.fenbeitong.openapi.plugin.yiduijie.model.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>Title: BillConfigProjectDeptDTO</p>
 * <p>Description: 部门对应项目</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/10 4:17 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BillConfigProjectDeptDTO implements Serializable {

    /**
     * 分贝通项目名称
     */
    @NotBlank(message = "分贝通项目名称[project]不可为空")
    private String project;

    /**
     * 项目归属部门
     */
    @NotBlank(message = "项目归属部门[department]不可为空")
    private String department;
}
