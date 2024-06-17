package com.fenbeitong.openapi.plugin.feishu.common.service.form;

import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalSimpleFormDTO;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

/**
 * @Description:
 * @Author: xiaohai
 * @Date: 2022/6/29 下午12:02
 */
public interface ParseApplyFormService<T> extends InitializingBean {

    /**
     * 解析表单数据
     * @param approvalDefines
     * @param applyDetail
     */
    void parseFormInfo(List<FeiShuApprovalSimpleFormDTO> approvalDefines, T applyDetail);

}
