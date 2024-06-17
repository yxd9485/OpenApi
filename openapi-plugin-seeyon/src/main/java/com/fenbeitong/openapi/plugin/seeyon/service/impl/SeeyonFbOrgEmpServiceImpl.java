package com.fenbeitong.openapi.plugin.seeyon.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.seeyon.constant.FbOrgEmpConstants;
import com.fenbeitong.openapi.plugin.seeyon.dao.SeeyonExtInfoDao;
import com.fenbeitong.openapi.plugin.seeyon.dao.SeeyonFbOrgEmpDao;
import com.fenbeitong.openapi.plugin.seeyon.dao.SeeyonOpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.seeyon.dao.SeeyonOrgEmpDao;
import com.fenbeitong.openapi.plugin.seeyon.dto.*;
import com.fenbeitong.openapi.plugin.seeyon.entity.*;
import com.fenbeitong.openapi.plugin.seeyon.helper.JacksonHelper;
import com.fenbeitong.openapi.plugin.seeyon.helper.Jsr310DateHelper;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonFbOrgEmpService;
import com.fenbeitong.openapi.plugin.seeyon.transformer.SeeyonEmpApiWrapper;
import com.fenbeitong.openapi.plugin.seeyon.transformer.SeeyonOrgApiWrapper;
import com.fenbeitong.openapi.plugin.seeyon.utils.IdGenerator;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportCreateEmployeeReqDTO;
import com.fenbeitong.openapi.plugin.support.employee.service.AbstractEmployeeService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeBean;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;
import com.fenbeitong.usercenter.api.service.employee.IThirdEmployeeService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ServiceAspect
@Service
@Slf4j
public class SeeyonFbOrgEmpServiceImpl extends AbstractEmployeeService implements SeeyonFbOrgEmpService {
    public static Map<String, List<SeeyonOpenMsgSetup>> itemCacheMap = Maps.newHashMap();
    @Autowired
    SeeyonFbOrgEmpDao seeyonFbOrgEmpDao;
    @Autowired
    SeeyonOrgEmpDao seeyonOrgEmpDao;
    @Autowired
    SeeyonExtInfoDao seeyonExtInfoDao;
    @Autowired
    SeeyonOpenMsgSetupDao seeyonOpenMsgSetupDao;
    @Autowired
    SeeyonEmpServiceImpl seeyonEmpService;
    @Autowired
    SeeyonEmailService seeyonEmailService;
    @DubboReference(check = false)
    private IThirdEmployeeService iThirdEmployeeService;

    @Override
    public boolean createOrg(SeeyonClient seeyonClient, SeeyonAccountOrgResp accountOrgResponse) {
        SeeyonApiOrgRequest apiOrgRequest = SeeyonOrgApiWrapper.createOrg(seeyonClient, accountOrgResponse);
        SeeyonFbOrgEmp createOrg =
                SeeyonFbOrgEmp.builder()
                        .id(IdGenerator.getId32bit())
                        .companyId(seeyonClient.getOpenapiAppId())
                        .jsonData(JsonUtils.toJsonSnake(apiOrgRequest))
                        .dataType(FbOrgEmpConstants.CALL_TYPE_ORG)
                        .dataExecuteManner(FbOrgEmpConstants.CALL_METHOD_CREATE)
                        .sort(FbOrgEmpConstants.CALL_ORDER_THREE)
                        .executeMark(FbOrgEmpConstants.CALL_EXECUTE_WAIT)
                        .orgPath(accountOrgResponse.getPath())
                        .createTime(LocalDateTime.now())
                        .build();
        int save = seeyonFbOrgEmpDao.save(createOrg);
        if (save != 1) {
            return false;
        }
        return true;
    }

    @Override
    public boolean updateOrg(SeeyonClient seeyonClient, SeeyonAccountOrgResp accountOrgResponse) {
        SeeyonApiOrgRequest apiOrgRequest = SeeyonOrgApiWrapper.updateOrg(seeyonClient, accountOrgResponse);
        SeeyonFbOrgEmp updateOrg =
                SeeyonFbOrgEmp.builder()
                        .id(IdGenerator.getId32bit())
                        .companyId(seeyonClient.getOpenapiAppId())
                        .jsonData(JsonUtils.toJsonSnake(apiOrgRequest))
                        .dataType(FbOrgEmpConstants.CALL_TYPE_ORG)
                        .dataExecuteManner(FbOrgEmpConstants.CALL_METHOD_UPDATE)
                        .sort(FbOrgEmpConstants.CALL_ORDER_SIX)
                        .executeMark(FbOrgEmpConstants.CALL_EXECUTE_WAIT)
                        .orgPath(accountOrgResponse.getPath())
                        .createTime(LocalDateTime.now())
                        .build();
        int save = seeyonFbOrgEmpDao.save(updateOrg);
        if (save != 1) {
            return false;
        }
        return true;
    }

    @Override
    public boolean delOrg(SeeyonClient seeyonClient, SeeyonAccountOrgResp accountOrgResponse) {
        SeeyonApiOrgRequest apiOrgRequest = SeeyonOrgApiWrapper.delOrg(seeyonClient, accountOrgResponse);
        SeeyonFbOrgEmp delOrg =
                SeeyonFbOrgEmp.builder()
                        .id(IdGenerator.getId32bit())
                        .companyId(seeyonClient.getOpenapiAppId())
                        .jsonData(JsonUtils.toJsonSnake(apiOrgRequest))
                        .dataType(FbOrgEmpConstants.CALL_TYPE_ORG)
                        .dataExecuteManner(FbOrgEmpConstants.CALL_METHOD_DELETE)
                        .sort(FbOrgEmpConstants.CALL_ORDER_TWO)
                        .executeMark(FbOrgEmpConstants.CALL_EXECUTE_WAIT)
                        .orgPath(accountOrgResponse.getPath())
                        .createTime(LocalDateTime.now())
                        .build();
        int save = seeyonFbOrgEmpDao.save(delOrg);
        if (save != 1) {
            return false;
        }
        return true;
    }

    @Override
    public boolean delOrg(SeeyonClient seeyonClient, SeeyonOrgDepartment seeyonOrgDepartment) {
        SeeyonApiOrgRequest apiOrgRequest = SeeyonOrgApiWrapper.delOrg(seeyonClient, seeyonOrgDepartment);
        String delOrgJson = JsonUtils.toJsonSnake(apiOrgRequest);
        SeeyonFbOrgEmp delOrg =
                SeeyonFbOrgEmp.builder()
                        .id(IdGenerator.getId32bit())
                        .companyId(seeyonClient.getOpenapiAppId())
                        .jsonData(delOrgJson)
                        .dataType(FbOrgEmpConstants.CALL_TYPE_ORG)
                        .dataExecuteManner(FbOrgEmpConstants.CALL_METHOD_DELETE)
                        .sort(FbOrgEmpConstants.CALL_ORDER_TWO)
                        .executeMark(FbOrgEmpConstants.CALL_EXECUTE_WAIT)
                        .orgPath(seeyonOrgDepartment.getPath())
                        .createTime(LocalDateTime.now())
                        .build();
        int save = seeyonFbOrgEmpDao.save(delOrg);
        if (save != 1) {
            return false;
        }
        return true;
    }

    @Override
    public boolean createEmp(SeeyonClient seeyonClient, SeeyonAccountEmpResp accountEmpResponse, SeeyonExtInfo seeyonExtInfo) {
        //根据公司ID查询需要获取的字段值
        SeeyonExtInfo byExample = null;
        if (!ObjectUtils.isEmpty(seeyonExtInfo)) {
            //取出目标字段，description
            String targetColum = seeyonExtInfo.getTargetColum();
            String accountEmpStr = JacksonHelper.toJson(accountEmpResponse);
            Map map = JsonUtils.toObj(accountEmpStr, Map.class);
            if (!ObjectUtils.isEmpty(map)) {
                //根据description获取具体值
                Object targetColumStr = map.get(targetColum);
                if (!ObjectUtils.isEmpty(targetColumStr)) {
                    try {
                        //解析description具体值
                        String result = JacksonHelper.toJson(targetColumStr);
                        Map<String, String> map1 = JSONObject.parseObject(result, Map.class);
                        String mapKey = seeyonExtInfo.getMapKey();
                        if (StringUtils.isNotBlank(mapKey)) {
                            String mapValue = map1.get(mapKey);
                            if (StringUtils.isNotBlank(mapValue)) {
                                //根据取出的值获取对应的分贝权限类型
                                Example example = new Example(SeeyonExtInfo.class);
                                //一个类型只能使用一个
                                example.createCriteria()
                                        .andEqualTo("companyId", seeyonExtInfo.getCompanyId())
                                        .andEqualTo("state", 0)
                                        .andEqualTo("mapValue", mapValue)
                                        .andEqualTo("type", 1);
                                byExample = seeyonExtInfoDao.getByExample(example);
                            }
                        }
                    } catch (Exception e) {
                        log.info("用户配置description字段格式错误 : {},人员ID: {}", targetColumStr,accountEmpResponse.getId());
//                        e.printStackTrace();
                    }
                }
            }
        }
        SeeyonApiEmpRequest apiEmpRequest = SeeyonEmpApiWrapper.createEmp(seeyonClient, accountEmpResponse, byExample);
        SeeyonFbOrgEmp createEmp =
                SeeyonFbOrgEmp.builder()
                        .id(IdGenerator.getId32bit())
                        .companyId(seeyonClient.getOpenapiAppId())
                        .jsonData(JsonUtils.toJsonSnake(getEmpListRequest(apiEmpRequest)))
                        .dataType(FbOrgEmpConstants.CALL_TYPE_EMP)
                        .dataExecuteManner(FbOrgEmpConstants.CALL_METHOD_CREATE)
                        .sort(FbOrgEmpConstants.CALL_ORDER_FOUR)
                        .executeMark(FbOrgEmpConstants.CALL_EXECUTE_WAIT)
                        .createTime(LocalDateTime.now())
                        .orgPath(String.valueOf(accountEmpResponse.getId()))
                        .build();
        int save = seeyonFbOrgEmpDao.save(createEmp);
        if (save != 1) {
            return false;
        }
        return true;
    }

    @Override
    public boolean updateEmp(SeeyonClient seeyonClient, SeeyonAccountEmpResp accountEmpResponse, SeeyonExtInfo seeyonExtInfo) {
        //根据公司ID查询需要获取的字段值
        SeeyonExtInfo byExample = null;
        if (!ObjectUtils.isEmpty(seeyonExtInfo)) {
            //取出目标字段，description
            String targetColum = seeyonExtInfo.getTargetColum();
            String s = JacksonHelper.toJson(accountEmpResponse);
            Map map = JsonUtils.toObj(s, Map.class);
            //根据description获取具体值
            Object targetColumStr = map.get(targetColum);
            if (!ObjectUtils.isEmpty(targetColumStr)) {
                try {
                    String s1 = JsonUtils.toJson(targetColumStr);
                    String substring = s1.substring(1, s1.length() - 1);
                    String replace = substring.replace("\\", "");
                    //解析description具体值
                    Map<String, String> map1 = JsonUtils.toObj(replace, Map.class);
                    String mapKey = seeyonExtInfo.getMapKey();
                    if (StringUtils.isNotBlank(mapKey) && !ObjectUtils.isEmpty(map1)) {
                        String mapValue = map1.get(mapKey);
                        if (StringUtils.isNotBlank(mapValue)) {
                            //根据取出的值获取对应的分贝权限类型
                            Example example = new Example(SeeyonExtInfo.class);
                            //一个类型只能使用一个
                            example.createCriteria()
                                    .andEqualTo("companyId", seeyonExtInfo.getCompanyId())
                                    .andEqualTo("state", 0)
                                    .andEqualTo("mapValue", mapValue)
                                    .andEqualTo("type", 1);
                            byExample = seeyonExtInfoDao.getByExample(example);
                        }
                    }
                } catch (Exception e) {
                    log.info("description字段数据格式错误 {}", targetColumStr);
                }
            }
        }

        //根据公司配置获取手机号使用方，默认使用接口获取的手机号，如果公司配置，则可以以分贝通手机号为准
        Map itemCodeMap = Maps.newHashMap();
        itemCodeMap.put("companyId", seeyonClient.getOpenapiAppId());
        itemCodeMap.put("itemCode", "company_use_fb_phone");
        List<SeeyonOpenMsgSetup> itemList = itemCacheMap.get(seeyonClient.getOpenapiAppId());
        List<SeeyonOpenMsgSetup> list = Lists.newArrayList();
        if (!ObjectUtils.isEmpty(itemList)) {
            list = itemList;
        } else {
            list = seeyonOpenMsgSetupDao.seeyonOpenMsgSetupList(itemCodeMap);
        }
        if (!ObjectUtils.isEmpty(list)) {
            SeeyonOpenMsgSetup seeyonOpenMsgSetup = list.get(0);
            Integer intVal1 = seeyonOpenMsgSetup.getIntVal1();
            if (ObjectUtils.isEmpty(itemCacheMap.get(seeyonClient.getOpenapiAppId()))) {
                itemCacheMap.put(seeyonClient.getOpenapiAppId(), list);
            }
            if (intVal1 == 1) {//配置了特殊公司的值，才进行相应的属性设置，否则默认走正常逻辑
                //查询分贝通手机号
                Long id = accountEmpResponse.getId();
                ThirdEmployeeRes employeeByThirdId = seeyonEmpService.getEmployeeByThirdId(seeyonClient.getOpenapiAppId(), String.valueOf(id));
                log.info(" 人员Id: {}, 根据人员ID查询分贝通人员信息结果: {}", id, JsonUtils.toJson(employeeByThirdId));
                if (!ObjectUtils.isEmpty(employeeByThirdId)) {//不为空则进行手机号的更新操作
                    ThirdEmployeeBean employee = employeeByThirdId.getEmployee();
                    String phoneNum = employee.getPhone_num();
                    //手机号覆盖操作
                    accountEmpResponse.setTelNumber(phoneNum);
                }
            }
        }
        SeeyonApiEmpRequest apiEmpRequest = SeeyonEmpApiWrapper.updateEmp(seeyonClient, accountEmpResponse, byExample);
        SeeyonFbOrgEmp updateEmp =
                SeeyonFbOrgEmp.builder()
                        .id(IdGenerator.getId32bit())
                        .companyId(seeyonClient.getOpenapiAppId())
                        .jsonData(JsonUtils.toJsonSnake(getEmpListRequest(apiEmpRequest)))
                        .dataType(FbOrgEmpConstants.CALL_TYPE_EMP)
                        .dataExecuteManner(FbOrgEmpConstants.CALL_METHOD_UPDATE)
                        .sort(FbOrgEmpConstants.CALL_ORDER_FIVE)
                        .executeMark(FbOrgEmpConstants.CALL_EXECUTE_WAIT)
                        .createTime(LocalDateTime.now())
                        .orgPath(String.valueOf(accountEmpResponse.getId()))
                        .build();
        int save = seeyonFbOrgEmpDao.save(updateEmp);
        if (save != 1) {
            return false;
        }
        return true;
    }

    @Override
    public boolean delEmp(SeeyonClient seeyonClient, SeeyonAccountEmpResp accountEmpResponse) {
        SeeyonApiEmpRequest apiEmpRequest = SeeyonEmpApiWrapper.delEmp(seeyonClient, accountEmpResponse);
        SeeyonFbOrgEmp delEmp =
                SeeyonFbOrgEmp.builder()
                        .id(IdGenerator.getId32bit())
                        .companyId(seeyonClient.getOpenapiAppId())
                        .jsonData(JsonUtils.toJsonSnake(getEmpDelListRequest(apiEmpRequest)))
                        .dataType(FbOrgEmpConstants.CALL_TYPE_EMP)
                        .dataExecuteManner(FbOrgEmpConstants.CALL_METHOD_DELETE)
                        .sort(FbOrgEmpConstants.CALL_ORDER_ONE)
                        .executeMark(FbOrgEmpConstants.CALL_EXECUTE_WAIT)
                        .createTime(LocalDateTime.now())
                        .orgPath(String.valueOf(accountEmpResponse.getId()))
                        .build();
        int save = seeyonFbOrgEmpDao.save(delEmp);
        if (save != 1) {
            return false;
        }
        return true;
    }

    @Override
    public boolean delEmp(SeeyonClient seeyonClient, SeeyonOrgEmployee seeyonOrgEmployee) {
        SeeyonApiEmpRequest apiEmpRequest = SeeyonEmpApiWrapper.delEmp(seeyonClient, seeyonOrgEmployee);
        String delEmpJson = JsonUtils.toJson(getEmpDelListRequest(apiEmpRequest));
        SeeyonFbOrgEmp delEmp =
                SeeyonFbOrgEmp.builder()
                        .id(IdGenerator.getId32bit())
                        .companyId(seeyonClient.getOpenapiAppId())
                        .jsonData(delEmpJson)
                        .dataType(FbOrgEmpConstants.CALL_TYPE_EMP)
                        .dataExecuteManner(FbOrgEmpConstants.CALL_METHOD_DELETE)
                        .sort(FbOrgEmpConstants.CALL_ORDER_ONE)
                        .executeMark(FbOrgEmpConstants.CALL_EXECUTE_WAIT)
                        .createTime(LocalDateTime.now())
                        .orgPath(String.valueOf(seeyonOrgEmployee.getId()))
                        .build();
        int save = seeyonFbOrgEmpDao.save(delEmp);
        if (save != 1) {
            return false;
        }
        return true;
    }

    @Override
    public boolean saveEmp(SeeyonOrgEmployee seeyonOrgEmployee) {
        int save = seeyonOrgEmpDao.save(seeyonOrgEmployee);
        if (save != 1) {
            return false;
        }
        return true;
    }

    @Override
    public List<SeeyonFbOrgEmp> getSeeyonFbOrgEmps(SeeyonClient seeyonClient) {
        Example example = new Example(SeeyonFbOrgEmp.class);
        example.createCriteria()
                .andEqualTo("companyId", seeyonClient.getOpenapiAppId())
                .andEqualTo("executeMark", FbOrgEmpConstants.CALL_EXECUTE_WAIT)
                .andBetween("createTime",
                        Jsr310DateHelper.getStartTime(),
                        Jsr310DateHelper.getEndTime());
        example.orderBy("sort").orderBy("orgPath").asc();
        //结果已经是排序后的数据，根据排序进行数据的调用操作
        List<SeeyonFbOrgEmp> seeyonFbOrgEmps = seeyonFbOrgEmpDao.listByExample(example);
        List<SeeyonFbOrgEmp> seeyonFbOrgEmps1 = new ArrayList<SeeyonFbOrgEmp>();
        seeyonFbOrgEmps.stream().forEach(seeyonFbOrgEmp -> {
            String jsonData = seeyonFbOrgEmp.getJsonData();
            SupportCreateEmployeeReqDTO employeeInsertDTO = JsonUtils.toObj(jsonData, SupportCreateEmployeeReqDTO.class);
            if(!ObjectUtils.isEmpty(employeeInsertDTO.getEmployeeList())){//可以构造出人员数据，说明属于新增或者更新的人员数据,还需要加入部门数据
                if (StringUtils.isNotBlank(employeeInsertDTO.getEmployeeList().get(0).getPhone())) {
                    seeyonFbOrgEmps1.add(seeyonFbOrgEmp);
                }
            }else{//部门数据
                seeyonFbOrgEmps1.add(seeyonFbOrgEmp);
            }
        });
        return seeyonFbOrgEmps1;
    }

    @Override
    public List<SeeyonFbOrgEmp> getSeeyonFbOrgEmpsDesc(SeeyonClient seeyonClient) {
        Example example = new Example(SeeyonFbOrgEmp.class);
        example.createCriteria()
                .andEqualTo("companyId", seeyonClient.getOpenapiAppId())
                .andBetween("createTime",
                        Jsr310DateHelper.getStartTime(),
                        Jsr310DateHelper.getEndTime());
        example.orderBy("sort").asc().orderBy("orgPath").asc();
        //结果已经是排序后的数据，根据排序进行数据的调用操作
        List<SeeyonFbOrgEmp> seeyonFbOrgEmps = seeyonFbOrgEmpDao.listByExample(example);
        return seeyonFbOrgEmps;
    }

    @Override
    public List<SeeyonFbOrgEmp> getSeeyonFbOrgEmpsByfbOrgEmp(SeeyonFbOrgEmp seeyonFbOrgEmp) {
        String companyId = seeyonFbOrgEmp.getCompanyId();
        Example example = new Example(SeeyonFbOrgEmp.class);
        Example.Criteria criteria = example.createCriteria()
                .andEqualTo("companyId", companyId)
                .andEqualTo("orgPath", seeyonFbOrgEmp.getOrgPath())
                .andEqualTo("dataType", seeyonFbOrgEmp.getDataType())
                .andEqualTo("sort", seeyonFbOrgEmp.getSort());
        if (StringUtils.isNotBlank(seeyonFbOrgEmp.getExecuteMark())) {
            criteria.andEqualTo("executeMark", seeyonFbOrgEmp.getExecuteMark());
        }
        example.orderBy("createTime").desc();
        //结果已经是排序后的数据，根据排序进行数据的调用操作
        List<SeeyonFbOrgEmp> seeyonFbOrgEmps = seeyonFbOrgEmpDao.listByExample(example);
        return seeyonFbOrgEmps;
    }

    @Override
    public List<SeeyonFbOrgEmp> filterList(SeeyonClient seeyonClient, List<SeeyonFbOrgEmp> sourceList) {
        Map itemCodeMap = Maps.newHashMap();
        itemCodeMap.put("companyId", seeyonClient.getOpenapiAppId());
        itemCodeMap.put("itemCode", "company_early_warning");
        SeeyonOpenMsgSetup openMsgSetup = seeyonOpenMsgSetupDao.getOpenMsgSetup(itemCodeMap);
        if (!ObjectUtils.isEmpty(openMsgSetup)) {//不为空，说明存在配置值
            Integer intVal1 = openMsgSetup.getIntVal1();
            if (1 == intVal1) {//说明公司配置了预警的信息，需要根据预警信息进行过滤
                //获取需要删除的人员和部门数据，如果人员和部门数据大于客户之前约定的部门数据，则不进行数据的更新，把需要删除的部门和人员数据剔除掉
                long deleteOrgCount = sourceList
                        .stream().filter(fbOrgEmp ->
                                FbOrgEmpConstants.CALL_ORDER_TWO.equals(fbOrgEmp.getSort()))
                        .count();
                if (deleteOrgCount > openMsgSetup.getIntVal2()) {//部门数量预警,并进行相应邮件的发送消息通知
                    sourceList = sourceList.stream().filter(source ->
                            !FbOrgEmpConstants.CALL_ORDER_TWO.equals(source.getSort())
                    ).collect(Collectors.toList());
                    //邮件预警通知
                    senEmail(seeyonClient, "部门");
                }
                long deleteEmployeeCount = sourceList
                        .stream().filter(fbOrgEmp ->
                                FbOrgEmpConstants.CALL_ORDER_ONE.equals(fbOrgEmp.getSort()))
                        .count();
                if (deleteEmployeeCount > openMsgSetup.getIntVal3()) {//人员数量预警,并进行相应邮件的发送消息通知
                    sourceList = sourceList.stream().filter(source ->
                            !FbOrgEmpConstants.CALL_ORDER_ONE.equals(source.getSort())
                    ).collect(Collectors.toList());
                    //邮件预警通知
                    senEmail(seeyonClient, "人员");
                }
            }
        }
        return sourceList;
    }

    private void senEmail(SeeyonClient seeyonClient, String msgFalg) {
        //邮件预警通知
        Map sendEmailNoticeMap = Maps.newHashMap();
        sendEmailNoticeMap.put("companyId", seeyonClient.getOpenapiAppId());
        sendEmailNoticeMap.put("itemCode", "company_send_self_email_notice");
        String contents = "同步" + msgFalg + "数据超过与客户约定的删除数量，请检查确认后再次同步";
        StringBuilder stringBuilder = new StringBuilder(contents);
        //即使邮件没有发送通知成功，部门已经不同步，下次同步删除部门时，会再次发送邮件信息
        seeyonEmailService.sendEmail(seeyonClient.getSeeyonOrgName(), sendEmailNoticeMap, stringBuilder,"");
    }

    /**
     * 新增更新人员
     *
     * @param apiEmpRequest
     * @return
     */
    private SeeyonApiEmpListRequest getEmpListRequest(SeeyonApiEmpRequest apiEmpRequest) {
        List<SeeyonApiEmpRequest> apiEmpRequestList = new ArrayList<>();
        apiEmpRequestList.add(apiEmpRequest);
        SeeyonApiEmpListRequest apiEmpListRequest = new SeeyonApiEmpListRequest();
        apiEmpListRequest.setEmployeeList(apiEmpRequestList);
        return apiEmpListRequest;
    }

    /**
     * 删除人员
     *
     * @param apiEmpRequest
     * @return
     */
    private SeeyonApiEmpDelListRequest getEmpDelListRequest(SeeyonApiEmpRequest apiEmpRequest) {
        List<String> apiEmpDelRequestList = new ArrayList<>();
        apiEmpDelRequestList.add(apiEmpRequest.getThirdEmployeeId());
        SeeyonApiEmpDelListRequest apiEmpDelListRequest = new SeeyonApiEmpDelListRequest();
        apiEmpDelListRequest.setThirdEmployeeIds(apiEmpDelRequestList);
        return apiEmpDelListRequest;
    }


    @Override
    protected IThirdEmployeeService getThirdEmployeeService() {
        return iThirdEmployeeService;
    }
}
