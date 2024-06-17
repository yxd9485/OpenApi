package com.fenbeitong.openapi.plugin.definition.controller.enforcement;

import com.fenbeitong.openapi.plugin.definition.dto.DefinitionResultDTO;
import com.fenbeitong.openapi.plugin.definition.dto.auto.CorpAutoAccountConfigReqDTO;
import com.fenbeitong.openapi.plugin.definition.dto.auto.CorpAutoCheckDTO;
import com.fenbeitong.openapi.plugin.definition.dto.auto.CorpAutoOrgEmpDTO;
import com.fenbeitong.openapi.plugin.definition.dto.auto.CorpAutoScenePrivDTO;
import com.fenbeitong.openapi.plugin.definition.dto.company.auth.AuthRegisterReqDTO;
import com.fenbeitong.openapi.plugin.definition.dto.plugin.apply.ThirdApplyDefinitionInfoDTO;
import com.fenbeitong.openapi.plugin.definition.dto.plugin.corp.CreatePluginCorpDefinitionReqDTO;
import com.fenbeitong.openapi.plugin.definition.dto.plugin.corp.PluginCorpDefinitionInfoDTO;
import com.fenbeitong.openapi.plugin.definition.dto.plugin.message.MsgRecipientInfoDTO;
import com.fenbeitong.openapi.plugin.definition.process.ICheckProcess;
import com.fenbeitong.openapi.plugin.definition.process.ProcessCheckFactory;
import com.fenbeitong.openapi.plugin.definition.service.*;
import com.fenbeitong.openapi.plugin.definition.service.auto.AutoOriganizationService;
import com.fenbeitong.openapi.plugin.support.permission.entity.PermissionDefinition;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/auto/corp")
@Slf4j
public class CorpAutoConfigController {

    @Autowired
    private AuthDefinitionService authDefinitionService;
    @Autowired
    private PluginCorpDefinitionService pluginCorpDefinitionService;
    @Autowired
    ThirdApplyDefinitionService thirdApplyDefinitionService;
    @Autowired
    private MsgRecipientDefinitionService msgRecipientDefinitionService;
    @Autowired
    private PermissionDefinitionService permissionDefinitionService;
    @Autowired
    private AutoOriganizationService autoOriganizationService;
    @Autowired
    ProcessCheckFactory processCheckFactory;


    @GetMapping("/account")
    public ModelAndView corpAccount(Model model) {
        return new ModelAndView("auto/corp_account_lay", "userModel", model);
    }

    /**
     * 企业账户配置s
     *
     * @param corpAutoAccountConfigReqDTO
     * @return
     */
    @PostMapping("/account/config")
    public String corpAccountConfigTest(@ModelAttribute(value = "corpAutoAccountConfig") CorpAutoAccountConfigReqDTO corpAutoAccountConfigReqDTO) {
        //非空判断
        AuthRegisterReqDTO registerReqDTO = AuthRegisterReqDTO.builder()
                .appId(corpAutoAccountConfigReqDTO.getFbtCompId())
                .appName(corpAutoAccountConfigReqDTO.getFbtCompName())
                .appRemark(corpAutoAccountConfigReqDTO.getRemark())
                .virtualNumber(Integer.valueOf(corpAutoAccountConfigReqDTO.getVirtualPhone()))
                .build();
        ModelMap model = new ModelMap();
        try {
            DefinitionResultDTO success = DefinitionResultDTO.success(authDefinitionService.register(registerReqDTO));
            //根据返回结果判断返回相应的页面
            if (success.getCode() != 0) {
                model.put("error", "失败了");
                return "failed";
            }
        } catch (Exception e) {
            model.put("error", "为什么获取不到");
            return "failed";
        }
        return "succeed";
    }

    @GetMapping("/plugin")
    public ModelAndView corpPlugin(Model model) {
        return new ModelAndView("auto/corp_plugin_lay", "userModel", model);
    }

    /**
     * 企业插件配置
     *
     * @param fbt_comp_id
     * @param admin_fbt_id
     * @param admin_third_id
     * @param third_comp_id
     * @param third_app_key
     * @param third_app_secret
     * @param fbt_micro_app
     * @param third_agent_id
     * @param proxy_url
     * @return
     */
    @PostMapping("/plugin/config")
    public String corpPluginConfig(String fbt_comp_id, String admin_fbt_id, String admin_third_id, String third_comp_id, String third_app_key, String third_app_secret, String fbt_micro_app, String third_agent_id, String proxy_url, Integer open_type) {
        //非空判断
        CreatePluginCorpDefinitionReqDTO createPluginCorpDefinitionReqDTO = CreatePluginCorpDefinitionReqDTO.builder()
                .appId(fbt_comp_id)
                .adminId(admin_fbt_id)
                .thirdAdminId(admin_third_id)
                .thirdCorpId(third_comp_id)
                .thirdAppKey(third_app_key)
                .thirdAppSecret(third_app_secret)
                .thirdAppName(fbt_micro_app)
                .thirdAgentId(Long.valueOf(third_agent_id))
                .proxyUrl(proxy_url)
                .openType(open_type)
                .build();

        ModelMap model = new ModelMap();
        try {
            PluginCorpDefinitionInfoDTO pluginCorp = pluginCorpDefinitionService.createPluginCorp(createPluginCorpDefinitionReqDTO);
            log.info("添加插件返回数据", JsonUtils.toJson(pluginCorp));
            //根据返回结果判断返回相应的页面
            if (!ObjectUtils.anyNotNull(pluginCorp)) {
                model.put("error", "失败了");
                return "failed";
            }
        } catch (Exception e) {
            model.put("error", "为什么获取不到");
            return "failed";
        }
        return "succeed";
    }

    @GetMapping("/approve")
    public ModelAndView corpApprove(Model model) {
        return new ModelAndView("auto/corp_approve_lay", "userModel", model);
    }

    /**
     * 企业审批配置
     *
     * @param fbt_comp_id
     * @param third_approve_code
     * @param third_approve_name
     * @param third_approve_type
     * @return
     */
    @PostMapping("/approve/config")
    public String corpApproveConfig(String fbt_comp_id, String third_approve_code, String third_approve_name, String third_approve_type) {
        //非空判断
        ModelMap model = new ModelMap();
        try {
            ThirdApplyDefinitionInfoDTO thirdApplyDefinition = thirdApplyDefinitionService.createThirdApplyDefinition(third_approve_code,
                    third_approve_name, Integer.valueOf(third_approve_type), fbt_comp_id);
            if (!ObjectUtils.anyNotNull(thirdApplyDefinition)) {
                model.put("error", "失败了");
                return "failed";
            }
        } catch (Exception e) {
            model.put("error", "为什么获取不到");
            return "failed";
        }
        return "succeed";
    }


    @GetMapping("/msg")
    public ModelAndView corpMsg(Model model) {
        return new ModelAndView("auto/corp_msg_lay", "userModel", model);
    }

    /**
     * 企业消息配置
     *
     * @param third_comp_id
     * @param third_agent_id
     * @param third_msg_receive_id
     * @param third_msg_receive_name
     * @return
     */
    @PostMapping("/msg/config")
    public String corpMsgConfig(String third_comp_id, String third_agent_id, String third_msg_receive_id, String third_msg_receive_name) {
        //非空判断
        ModelMap model = new ModelMap();
        try {
            MsgRecipientInfoDTO msgRecipientDefinition = msgRecipientDefinitionService.createMsgRecipientDefinition(third_comp_id,
                    third_agent_id, third_msg_receive_id, third_msg_receive_name);
            if (!ObjectUtils.anyNotNull(msgRecipientDefinition)) {
                model.put("error", "失败了");
                return "failed";
            }
        } catch (Exception e) {
            model.put("error", "为什么获取不到");
            return "failed";
        }
        return "succeed";
    }


    @GetMapping("/priv/add")
    public ModelAndView corpPrivAdd(Model model) {
        return new ModelAndView("auto/corp_priv_add_lay", "userModel", model);
    }

    /**
     * 企业权限配置
     *
     * @param corpAutoScenePrivDTO
     * @return
     */
    @PostMapping("/priv/config")
    public ModelAndView corpPrivConfig(@ModelAttribute(value = "autoCompPrivConfig") CorpAutoScenePrivDTO corpAutoScenePrivDTO) {
        List<PermissionDefinition> permissionDefinitions = permissionDefinitionService.batchCreatePermission(corpAutoScenePrivDTO);
        ModelAndView mav = new ModelAndView();
        //设置试图名称
        mav.setViewName("list");
        //添加页面获取对象数据
        mav.addObject("list", permissionDefinitions);
        return mav;
    }



    @GetMapping("/org_emp/bind")
    public ModelAndView corpOrgEmp(Model model) {
        return new ModelAndView("auto/corp_org_emp_lay", "userModel", model);
    }

    /**
     * 部门人员绑定
     * @param corpAutoOrgEmpDTO
     * @return
     */
    @PostMapping("/org_emp/bind/config")
    public ModelAndView corpOrgEmpConfig(CorpAutoOrgEmpDTO corpAutoOrgEmpDTO) {

        autoOriganizationService.autoBindDepartmentAndEmployee(corpAutoOrgEmpDTO);
        ModelAndView mav = new ModelAndView();
        //设置试图名称
        mav.setViewName("succeed");
        //添加页面获取对象数据
        mav.addObject("succeed", null);
        return mav;
    }

    /**
     * 企业检查
     * @param model
     * @return
     */
    @GetMapping("/check/org_emp")
    public ModelAndView corpCheck(Model model) {
        return new ModelAndView("check/corp_check_lay", "userModel", model);
    }

    /**
     * 企业检查集合
     *
     * @param
     * @return
     */
    @PostMapping("/check/org/config")
    public ModelAndView corpCheckConfig(@ModelAttribute(value = "corpCheck") CorpAutoCheckDTO corpAutoCheckDTO) {
        ICheckProcess processApply = processCheckFactory.getProcessApply(corpAutoCheckDTO.getCheckType());
        List<CorpAutoOrgEmpDTO> check = processApply.check(corpAutoCheckDTO);
        ModelAndView mav = new ModelAndView();
        //设置试图名称
        mav.setViewName("check_error_list");
        //添加页面获取对象数据
        mav.addObject("check_error_list", check);
        return mav;
    }



    @GetMapping("/task/gen")
    public ModelAndView corpGenTaskLay(Model model) {
        return new ModelAndView("task/corp_gen_task_lay", "userModel", model);

//        //非空判断
//        ModelMap model = new ModelMap();
//        try {
//            pluginCorpDefinitionService.genTask(corpId,pluginType,taskType,dataId);
//        } catch (Exception e) {
//            model.put("error", "failed");
//            return "failed";
//        }
//        return "succeed";
    }



    @PostMapping("/task/genTask")
    public String corpGenTask(String corpId,String pluginType,String taskType,String dataId) {
        //非空判断
        ModelMap model = new ModelMap();
        try {
            pluginCorpDefinitionService.genTask(corpId,pluginType,taskType,dataId);
        } catch (Exception e) {
            model.put("error", "failed");
            return "failed";
        }
        return "succeed";
    }
}
