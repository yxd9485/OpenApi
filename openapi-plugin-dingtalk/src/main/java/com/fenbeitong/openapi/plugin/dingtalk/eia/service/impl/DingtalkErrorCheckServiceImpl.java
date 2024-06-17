package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.dingtalk.api.response.OapiUserGetResponse;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkUser;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingTalkNoticeService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkCorpService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkErrorCheckService;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.dingtalk.common.message.csm.entity.DingtalkCsmMsgRecipient;
import com.fenbeitong.openapi.plugin.dingtalk.common.message.csm.service.IDingtalkCsmMsgRecipientService;
import com.fenbeitong.openapi.plugin.support.task.dao.TaskDao;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.sdk.dto.common.OpenApiRespDTO;
import com.fenbeitong.openapi.sdk.dto.employee.GetUserByPhoneNumReqDTO;
import com.fenbeitong.openapi.sdk.dto.employee.GetUserByPhoneNumRespDTO;
import com.fenbeitong.openapi.sdk.webservice.employee.FbtEmployeeService;
import com.google.common.collect.Lists;
import com.luastar.swift.base.utils.ObjUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;
import retrofit2.Call;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lizhen
 * @date 2020/11/4
 */
@ServiceAspect
@Service
@Slf4j
public class DingtalkErrorCheckServiceImpl implements IDingtalkErrorCheckService {

    private final static String ORG_ERR_MSG = "父级部门第三方部门不存在,当前第三方部门已经存在,同一级别部门名称重复,第三方和父级部门不能重复,服务器异常请稍后重试,系统繁忙";
    private final static String EMP_ERR_MSG = "部门信息参数不正确,第三方不存在,当前部门不存在,部门名称不存在,第三方已经被其它用户绑定,该用户尚未加入该企业组织架构中,服务器异常请稍后重试,手机号已经存在请使用其他手机号,系统繁忙";
    private final static String EMP_ERR_PHONE_MSG = "手机号已经存在，请使用其他手机号";
    private final static String DEP_ERR_MSG = "部门名称不可以含";


    @Autowired
    private TaskDao taskDao;

    @Autowired
    private ExceptionRemind exceptionRemind;

    @Autowired
    private IDingtalkCorpService dingtalkCorpService;

    @Autowired
    private ApiUserServiceImpl apiUserService;

    @Autowired
    private FbtEmployeeService fbtUserCenterService;

    @Autowired
    private IDingtalkCsmMsgRecipientService dingtalkCsmMsgRecipientService;

    @Autowired
    private IDingTalkNoticeService dingTalkNoticeService;

    /**
     * 检测任务执行失败信息，发送消息
     */
    @Override
    public void checkFailedTask() {
        log.info("检查是否有执行失败的任务");
        int failedTaskNum = taskDao.countFailedTask();
        if (failedTaskNum == 0) {
            log.info("没有执行失败的任务信息");
            return;
        } else {
            List<Task> failedTasks = taskDao.listRecentFailedTask(3);
            String msg = StringUtils.formatString("{0}条任务执行失败，请及时处理:\n {1}", failedTaskNum, failedTasks);
            log.info(msg);
            exceptionRemind.taskRemindDingTalk(msg);
        }
    }


    /**
     * 根据错误手机号进行推送给相应客户成功，每3小时同步一次
     * 让客户进行相应的修改操作，三个小时推送一次,直到用户修改完成后就不需要推送该消息
     */
    @Override
    public void checkFailedTaskAndPhone() throws IOException {
        log.info("手机号重复错误检查是否有执行失败的任务请求参数");
        int failedTaskNum = taskDao.countFailedTask();
        if (failedTaskNum == 0) {
            log.info("手机号重复错误没有执行失败的任务信息");
            return;
        } else {
            List<Task> failedTasks = taskDao.listRecentFailedTask(500);
            for (Task dingtalkTask : failedTasks) {//根据公司和客户成功对应关系，发送需要修改的手机号，让客户去修改
                String corpId = dingtalkTask.getCorpId();
                String executeResult = dingtalkTask.getExecuteResult();
                String dataId = dingtalkTask.getDataId();
                //任务类型
                String taskType = dingtalkTask.getTaskType();
                DingtalkCsmMsgRecipient csmMsgRecipient = dingtalkCsmMsgRecipientService.getCsmMsgRecipient(dingtalkTask.getCorpId());
                if (!ObjectUtils.isEmpty(csmMsgRecipient)) {
                    //发送相应的消息
                    String fbtCorpId = csmMsgRecipient.getFbtCorpId();
                    String csmDingtalkId = csmMsgRecipient.getCsmDingtalkId();
                    //根据返回的错误信息进行更新操作,重新执行任务
                    if ((TaskType.DINGTALK_EIA_CREATE_OR_UPDATE_USER.getKey().equals(taskType))) {//新增人员
                        if (executeResult.contains(EMP_ERR_PHONE_MSG)) {
                            OapiUserGetResponse userGetResponse = apiUserService.getUserWithOriginal(corpId, dataId);
                            DingtalkUser user = com.fenbeitong.openapi.plugin.util.JsonUtils.toObj(userGetResponse.getBody(), DingtalkUser.class);
                            //钉钉分贝手机号
                            String dingtalkFbtMobile = user.getFbtMobile();
                            //钉钉ID
                            String dingtalkUserId = user.getUserid();
                            //钉钉用户名
                            String dingtalkUserName = user.getName();
                            log.info("新增人员事件公司名称： {},员工ID：{},员工姓名： {}", corpId, dataId, dingtalkUserName);
                            //根据钉钉企业ID查询分贝公司ID
                            PluginCorpDefinition dingtalkCorp = dingtalkCorpService.getByCorpId(dingtalkTask.getCorpId());

                            ArrayList<String> phoneList = Lists.newArrayList(dingtalkFbtMobile);
                            PluginCorpDefinition corpDefinition = dingtalkCorpService.getByCorpId(corpId);
                            Call<OpenApiRespDTO<List<GetUserByPhoneNumRespDTO>>> userInfoByPhoneNum = fbtUserCenterService.getUserInfoByPhoneNum(GetUserByPhoneNumReqDTO.builder()
                                    .phoneNums(phoneList)
                                    .companyId(corpDefinition.getAppId())
                                    .build());
                            OpenApiRespDTO<List<GetUserByPhoneNumRespDTO>> respDTO = userInfoByPhoneNum.execute().body();
                            List<GetUserByPhoneNumRespDTO> fbUserList = respDTO == null ? Lists.newArrayList() : respDTO.getData();
                            log.info("新增人员事件根据手机号和公司ID查询员工信息返回结果： {}", JsonUtils.toJson(fbUserList));
                            if (!ObjectUtils.isEmpty(fbUserList)) {//有返回结果，说明是同一公司不同人使用了相同的手机号,也有可能是自己使用了这个手机号,比对第三方用户ID
                                GetUserByPhoneNumRespDTO fbUser = fbUserList.get(0);
                                String thirdEmployeeId = fbUser.getThirdEmployeeId();
                                String userName = fbUser.getUserName();
                                log.info("新增人员事件根据手机号查询钉钉ID： {},分贝ID： {}", dingtalkUserId, thirdEmployeeId);
                                if (!dingtalkUserId.equals(thirdEmployeeId)) {
                                    //被公司其他人占用了，需要把占用的手机号确认
                                    String msg = dingtalkCorp.getAppName() + ":" + dingtalkUserName + "手机号:" + dingtalkFbtMobile + "，与该公司" + userName + "手机号相同，无法同步员工数据，请核实并修改手机号后再次同步";
                                    //发送相应的消息
                                    dingTalkNoticeService.sendMsg(fbtCorpId, csmDingtalkId, msg);
                                } else {//个人使用，再次添加，则需要把任务进行删除处理
                                    Integer integer = taskDao.delete(dingtalkTask);
                                    log.info("新增人员事件删除任务返回数据 {}", integer);
                                }
                            } else {//其他公司
                                userInfoByPhoneNum = fbtUserCenterService.getUserInfoOnlyByPhoneNum(GetUserByPhoneNumReqDTO.builder()
                                        .phoneNums(phoneList)
                                        .companyId(corpDefinition.getAppId())
                                        .build());
                                respDTO = userInfoByPhoneNum.execute().body();
                                fbUserList = respDTO == null ? Lists.newArrayList() : respDTO.getData();
                                log.info("新增人员事件根据手机号查询员工信息返回结果： {}", JsonUtils.toJson(fbUserList));
                                if (!ObjectUtils.isEmpty(fbUserList)) {//返回成功信息
                                    GetUserByPhoneNumRespDTO fbUser = fbUserList.get(0);
                                    Object companyName = fbUser.getCompanyName();
                                    String fbUserName = fbUser.getUserName();
                                    String msg = dingtalkUserName + " 手机号:" + dingtalkFbtMobile + ",员工数据无法同步至" + dingtalkCorp.getAppName() + "公司,请尽快处理,处理步骤如下:\n" +
                                            "1:确认手机号是否为该员工手机号,若手机号错误请客户修改为正确手机号后再次同步;\n" +
                                            "2:该员工手机号无误,则联系员工自行操作退出上家企业或联系上家企业管理员进行离职操作,上家公司为:" + companyName;
                                    dingTalkNoticeService.sendMsg(fbtCorpId, csmDingtalkId, msg);
                                } else {//返回信息异常
                                    log.info("根据手机号查询人员信息返回结果异常01： {}", JsonUtils.toJson(respDTO));
                                    throw new FinhubException(100894, "异常结果");
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * 定时同步不符合分贝规则的部门名称信息，通知相应的csm
     */
    @Override
    public void failOrgList2CsmOrClient() {
        log.info("更新任务时检查是否有执行失败的任务请求参数");
        int failedTaskNum = taskDao.countFailedTask();
        if (failedTaskNum == 0) {
            log.info("更新任务时没有执行失败的任务信息");
            return;
        } else {
            List<Task> failedTasks = taskDao.listRecentFailedTask(500);
            for (Task dingtalkTask : failedTasks) {
                String taskType = dingtalkTask.getTaskType();
                //如果是新增部门或者是更新部门时
                if (TaskType.DINGTALK_EIA_CREATE_OR_UPDATE_DEPT.getKey().equals(taskType)) {
                    String executeResult = dingtalkTask.getExecuteResult();
                    //部门信息错误，无法进行新增或者更新
                    if (executeResult.contains(DEP_ERR_MSG)) {
                        DingtalkCsmMsgRecipient csmMsgRecipient = dingtalkCsmMsgRecipientService.getCsmMsgRecipient(dingtalkTask.getCorpId());
                        if (ObjUtils.isNotEmpty(csmMsgRecipient)) {
                            log.info("钉钉错误部门 企业ID: {},部门ID: {}", dingtalkTask.getCorpId(), dingtalkTask.getDataId());
                            String fbtCorpId = csmMsgRecipient.getFbtCorpId();
                            String csmDingtalkId = csmMsgRecipient.getCsmDingtalkId();
                            //备注信息填写公司名称
                            String description = csmMsgRecipient.getDescription();
                            String substring = executeResult.substring(executeResult.indexOf("部门") + 2, executeResult.indexOf("at") - 1);
                            String sendMsg = "公司名称：" + description + "\n部门名称：" + substring + " 请及时修改成符合分贝规则的部门名称，以免影响部门的人员同步以及相应的审批配置";
                            dingTalkNoticeService.sendMsg(fbtCorpId, csmDingtalkId, sendMsg);
                        }
                    }
                }
            }
        }
    }


}
