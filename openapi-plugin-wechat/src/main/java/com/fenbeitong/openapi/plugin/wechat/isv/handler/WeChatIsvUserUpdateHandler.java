package com.fenbeitong.openapi.plugin.wechat.isv.handler;

import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdEmployeeDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdEmployee;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.fenbeitong.openapi.plugin.support.util.VirtualPhoneUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatGetUserResponse;
import com.fenbeitong.openapi.plugin.wechat.isv.entity.WeChatIsvCompany;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvEmployeeService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode.WECHAT_ISV_COMPANY_UNDEFINED;

/**
 * Created by lizhen on 2020/3/27.
 */
@Component
@Slf4j
public class WeChatIsvUserUpdateHandler implements ITaskHandler {

    @Autowired
    private WeChatIsvCompanyDefinitionService weChatIsvCompanyDefinitionService;

    @Autowired
    private WeChatIsvEmployeeService weChatIsvEmployeeService;

    @Autowired
    private VirtualPhoneUtils virtualPhoneUtils;

    @Autowired
    private WeChatIsvUserAddHandler weChatIsvUserAddHandler;

    @Autowired
    private OpenThirdEmployeeDao openThirdEmployeeDao;

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Override
    public TaskType getTaskType() {
        return TaskType.WECHAT_ISV_UPDATE_USER;
    }

    @Override
    public TaskResult execute(Task task) {
        //1.解析task
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        //2.检查企业是否注册
        WeChatIsvCompany weChatIsvCompany = weChatIsvCompanyDefinitionService.getByCorpId(corpId);
        if (weChatIsvCompany == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WECHAT_ISV_COMPANY_UNDEFINED));
        }
        String companyId = weChatIsvCompany.getCompanyId();
        //3.调用企业微信获取员工详情
        WeChatGetUserResponse wechatUser = null;
        try {
            wechatUser = weChatIsvEmployeeService.getWechatUser(dataId, corpId);
        } catch (OpenApiWechatException e) {
            if (e.getCode() == NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_CORP_EMPLOYEE_IS_NULL)) {
                return TaskResult.EXPIRED;
            }
            throw e;
        }
        //5.查询分贝通员工信息
        List<OpenThirdEmployee> srcEmployeeList = openThirdEmployeeDao.listEmployeeByThirdEmployeeId(OpenType.WECHAT_ISV.getType(), companyId, Lists.newArrayList(dataId));
        //如果在分贝通不存在，则新增
        if (srcEmployeeList == null || srcEmployeeList.size() == 0) {
            return weChatIsvUserAddHandler.execute(task);
        }
        //6.拼装分贝通请求参数
        //转换人员
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
        openThirdEmployeeDTO.setCompanyId(companyId);
        openThirdEmployeeDTO.setThirdDepartmentId(wechatUser.getDepartmentStr());
        openThirdEmployeeDTO.setThirdEmployeeId(wechatUser.getUserId());
        openThirdEmployeeDTO.setThirdEmployeeEmail(wechatUser.getEmail());
        if (!StringUtils.isBlank(wechatUser.getGender())) {
            openThirdEmployeeDTO.setThirdEmployeeGender(Integer.valueOf(wechatUser.getGender()));
        }
        // 1=已激活，2=已禁用，4=未激活，5=退出企业。
        if (1 == wechatUser.getStatus() || 2 == wechatUser.getStatus()) {
            openThirdEmployeeDTO.setStatus(wechatUser.getStatus());
        }
        // 未激活算正常状态
        if (4 == wechatUser.getStatus()) {
            openThirdEmployeeDTO.setStatus(1);
        }
        // 退出企业丢弃删除
        if (5 == wechatUser.getStatus()) {
            return TaskResult.EXPIRED;
        }
        if ("1".equals(openThirdEmployeeDTO.getThirdDepartmentId()) || "0".equals(openThirdEmployeeDTO.getThirdDepartmentId())) {
            openThirdEmployeeDTO.setThirdDepartmentId(corpId);
        }
        employeeList.add(openThirdEmployeeDTO);
        openSyncThirdOrgService.updateEmployee(OpenType.WECHAT_ISV.getType(), companyId, employeeList);
        return TaskResult.SUCCESS;
    }

}
