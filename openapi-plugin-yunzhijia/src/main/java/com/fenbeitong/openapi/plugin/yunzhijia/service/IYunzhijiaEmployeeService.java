package com.fenbeitong.openapi.plugin.yunzhijia.service;

import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaEmployeeDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaEmployeeReqDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaResponse;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.ticket.YunzhijiaUserReqDto;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;

import java.util.List;

public interface IYunzhijiaEmployeeService {

    YunzhijiaResponse<List<YunzhijiaEmployeeDTO>> getYunzhijiaEmployeeDetail(YunzhijiaEmployeeReqDTO yunzhijiaEmployeeReqDTO);

    List<YunzhijiaEmployeeDTO> getYunzhijiaEmployeeList(String accessToken,YunzhijiaEmployeeReqDTO yunzhijiaEmployeeReqDTO);

    EmployeeContract getEmployeeByEmployeeId(String companyId, String employeeId);
}
