package com.fenbeitong.openapi.plugin.yunzhijia.controller;

import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseCode;
import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaOrgDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaOrgInChargeDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaOrgReqDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaResponse;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaOrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/yunzhijia/org")
public class YunzhijiaOrgController {
    @Autowired
    IYunzhijiaOrgService iYunzhijiaOrgService;

    /**
     * 查询云之家部门详情
     *
     * @param yunzhijiaOrgReqDTO
     * @return
     */
    @RequestMapping("/detail")
    @ResponseBody
    public Object getYunzhijiaOrgDetail(@RequestBody YunzhijiaOrgReqDTO yunzhijiaOrgReqDTO) {
        YunzhijiaResponse<List<YunzhijiaOrgDTO>> yunzhijiaOrgDetail = iYunzhijiaOrgService.getYunzhijiaOrgDetail(yunzhijiaOrgReqDTO);
        return YunzhijiaResponseUtils.success(yunzhijiaOrgDetail.getData());
    }

    /**
     * 获取部门基础信息或负责人信息
     *
     * @param yunzhijiaOrgReqDTO
     * @return
     */
    @RequestMapping("/leader")
    @ResponseBody
    public Object getYunzhijiaOrgLeader(@RequestBody YunzhijiaOrgReqDTO yunzhijiaOrgReqDTO) {
        YunzhijiaResponse<YunzhijiaOrgInChargeDTO>  yunzhijiaOrgInChargeRespDTO = iYunzhijiaOrgService.getYunzhijiaRemoteOrgBaseOrLeaderDetail(yunzhijiaOrgReqDTO);
        if (yunzhijiaOrgInChargeRespDTO.getErrorCode() == Integer.valueOf(YunzhijiaResponseCode.YUNZHIJIA_SUCCESS)) {
            return YunzhijiaResponseUtils.success(yunzhijiaOrgInChargeRespDTO.getData());
        }
        return YunzhijiaResponseUtils.error(yunzhijiaOrgInChargeRespDTO.getErrorCode(),yunzhijiaOrgInChargeRespDTO.getError());
    }
}
