package com.fenbeitong.openapi.plugin.func.reimburse.service;

import com.fenbeitong.openapi.plugin.func.reimburse.dto.RemiDetailResDTO;
import com.fenbeitong.openapi.plugin.func.reimburse.dto.RemiUpdStatusDTO;
import com.fenbeitong.openapi.plugin.func.reimburse.dto.RemiDetailReqDTO;
import org.springframework.validation.BindException;

import java.util.List;

/**
 * @ClassName FuncRemiService
 * @Description 报销详情查询
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/9/16 下午9:37
 **/
public interface FuncRemiService {
    Object getRemiDetailInfo(RemiDetailReqDTO req, String companyId) throws BindException;

    Object updateRemiStatus(RemiUpdStatusDTO reimbIds) throws BindException;

    /**
     * 使用报销单id获取报销单详情
     *
     * @param idList
     * @param companyId
     * @return
     */
    List<RemiDetailResDTO> queryReimburseBillListByIdList(List<String> idList, String companyId);
}
