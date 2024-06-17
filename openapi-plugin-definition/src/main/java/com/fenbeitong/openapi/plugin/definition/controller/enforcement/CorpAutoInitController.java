package com.fenbeitong.openapi.plugin.definition.controller.enforcement;

import com.dingtalk.api.response.OapiDepartmentListResponse;
import com.fenbeitong.openapi.plugin.definition.dto.DefinitionResultDTO;
import com.fenbeitong.openapi.plugin.definition.dto.auto.CorpAutoOrgEmpDTO;
import com.fenbeitong.openapi.plugin.definition.service.PluginCorpDefinitionService;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkCallbackTagConstant;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkUser;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingTalkSyncThirdEmployeeService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingTalkSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkCallBackService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/auto/corp/init")
@Slf4j
public class CorpAutoInitController {
    @Autowired
    private IDingTalkSyncThirdOrgService dingtalkSyncThirdOrgService;
    @Autowired
    private IDingTalkSyncThirdEmployeeService dingTalkSyncThirdEmployeeService;
    @Autowired
    IDingtalkCallBackService iDingtalkCallBackService;
    @Value("${dingtalk.callback.host}")
    private String dingtalkCallbackHost;
    @Autowired
    PluginCorpDefinitionService pluginCorpDefinitionService;

    @GetMapping("")
    public ModelAndView corpCheck(Model model) {
        return new ModelAndView("init/corp_init_lay", "userModel", model);
    }

    @PostMapping("/org_emp")
    public ModelAndView corpAutoInit(String companyId, String initType) {

        List<CorpAutoOrgEmpDTO> list = Lists.newArrayList();
        if (initType.equals("0")) {
            //同步部门
            //dingtalkSyncThirdOrgService.syncThirdOrg(companyId);
            //同步人员
            //dingTalkSyncThirdEmployeeService.syncThirdEmployee(companyId);
            //同步部门人员
            dingTalkSyncThirdEmployeeService.syncThirdOrgEmployee(companyId);
        } else if (initType.equals("1")) {
            List<OapiDepartmentListResponse.Department> unSynDeptList = dingtalkSyncThirdOrgService.checkDingtalkDepartment(companyId);
            if (!ObjectUtils.isEmpty(unSynDeptList)) {//返回有部门数据
                unSynDeptList.stream().forEach(e -> list.add(CorpAutoOrgEmpDTO.builder()
                        .thirdOrgName(e.getName())
                        .thirdOrgId(String.valueOf(e.getId()))
                        .build()));
            }
        } else if (initType.equals("2")) {
            List<DingtalkUser> unSynDingtalkUserList = dingTalkSyncThirdEmployeeService.checkDingtalkEmployee(companyId);
            unSynDingtalkUserList.stream().forEach(user -> list.add(CorpAutoOrgEmpDTO.builder()
                    .thirdEmpId(user.getUserid())
                    .thirdEmpName(user.getName())
                    .build()));
        }

        ModelAndView mav = new ModelAndView();
        //设置试图名称
        mav.setViewName("check_error_list");
        //添加页面获取对象数据
        mav.addObject("check_error_list", list);
        return mav;
    }


    @GetMapping("/callback/regist")
    public ModelAndView corpRegistCallback(Model model) {
        return new ModelAndView("init/corp_callback_lay", "userModel", model);
    }

    @PostMapping("/callback/regist/config")
    public ModelAndView registCallback(String thirdCompanyId ,String type) {
        List<CorpAutoOrgEmpDTO> list = Lists.newArrayList();
        if("0".equals(type)){//注册
            String[] callbackTags = {DingtalkCallbackTagConstant.USER_ADD_ORG, DingtalkCallbackTagConstant.USER_MODIFY_ORG,
                    DingtalkCallbackTagConstant.USER_LEAVE_ORG, DingtalkCallbackTagConstant.ORG_DEPT_CREATE, DingtalkCallbackTagConstant.ORG_DEPT_MODIFY,
                    DingtalkCallbackTagConstant.ORG_DEPT_REMOVE, DingtalkCallbackTagConstant.BPMS_INSTANCE_CHANGE};

            List<String> callbackList = iDingtalkCallBackService.list(thirdCompanyId);
            // 检查是否已注册回调事件，如果有则更新
            if (CollectionUtils.isEmpty(callbackList)) {
                iDingtalkCallBackService.register(thirdCompanyId, callbackTags, dingtalkCallbackHost);
            } else {
                iDingtalkCallBackService.update(thirdCompanyId, callbackTags, dingtalkCallbackHost);
            }
        }else{//清空
            String[] strings = {"bpms_instance_change"};
            iDingtalkCallBackService.update(thirdCompanyId, strings, dingtalkCallbackHost);
        }
        ModelAndView mav = new ModelAndView();
        //设置试图名称
        mav.setViewName("succeed");
        //添加页面获取对象数据
        mav.addObject("succeed", list);
        return mav;
    }

    /**
     * 初始化机构和人员信息
     * @param companyId ：公司id
     * @return
     */
    @GetMapping("/org_emp_init")
    @ResponseBody
    public DefinitionResultDTO corpAutoInitOrgAndEmp(String companyId) {
        DefinitionResultDTO resultDTO = null;
        try {
            //同步部门人员
            dingTalkSyncThirdEmployeeService.syncThirdOrgEmployee(companyId);
            resultDTO = DefinitionResultDTO.success("数据同步成功！");
        } catch (Exception e) {
//            e.printStackTrace();
            resultDTO = DefinitionResultDTO.error(e.getMessage());
        }
        return resultDTO;
    }

    @GetMapping("/callback/regist/configinfo")
    @ResponseBody
    public DefinitionResultDTO registCallbackConf(String thirdCompanyId) {
        DefinitionResultDTO resultDTO = null;
        try {
            String[] callbackTags = {DingtalkCallbackTagConstant.USER_ADD_ORG, DingtalkCallbackTagConstant.USER_MODIFY_ORG,
                    DingtalkCallbackTagConstant.USER_LEAVE_ORG, DingtalkCallbackTagConstant.ORG_DEPT_CREATE, DingtalkCallbackTagConstant.ORG_DEPT_MODIFY,
                    DingtalkCallbackTagConstant.ORG_DEPT_REMOVE, DingtalkCallbackTagConstant.BPMS_INSTANCE_CHANGE};

            List<String> callbackList = iDingtalkCallBackService.list(thirdCompanyId);
            // 检查是否已注册回调事件，如果有则更新
            if (CollectionUtils.isEmpty(callbackList)) {
                iDingtalkCallBackService.register(thirdCompanyId, callbackTags, dingtalkCallbackHost);
            } else {
                iDingtalkCallBackService.update(thirdCompanyId, callbackTags, dingtalkCallbackHost);
            }
            resultDTO = DefinitionResultDTO.success("注册成功！");
        } catch (Exception e) {
//            e.printStackTrace();
            resultDTO = DefinitionResultDTO.error(e.getMessage());
        }
        return resultDTO;
    }



}
