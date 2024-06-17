package com.fenbeitong.openapi.plugin.wechat.isv.service;

import com.fenbeitong.finhub.auth.entity.base.UserComInfoVO;
import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.finhub.common.utils.OssHandler;
import com.fenbeitong.openapi.plugin.support.common.dto.UserCenterResponse;
import com.fenbeitong.openapi.plugin.support.employee.dto.UcEmployeeSelfInfoResponse;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdEmployeeDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdEmployee;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnit;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.isv.dao.WechatIsvContactTranslateDao;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.*;
import com.fenbeitong.openapi.plugin.wechat.isv.entity.WeChatIsvCompany;
import com.fenbeitong.openapi.plugin.wechat.isv.entity.WechatIsvContactTranslate;
import com.fenbeitong.openapi.plugin.wechat.isv.enums.WeChatIsvContactTranslateState;
import com.fenbeitong.openapi.plugin.wechat.isv.util.WeChatIsvHttpUtils;
import com.fenbeitong.usercenter.api.model.dto.common.CommonIdDTO;
import com.fenbeitong.usercenter.api.service.common.ICommonService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode.WECHAT_ISV_COMPANY_UNDEFINED;

/**
 * @author lizhen
 * @date 2020/9/9
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatIsvTransferService {


    @Value("${wechat.api-host}")
    private String wechatHost;

    @Value("${host.harmony}")
    private String harmonyHost;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private OpenThirdEmployeeDao openThirdEmployeeDao;

    @Autowired
    private OpenThirdOrgUnitDao openThirdOrgUnitDao;

    @Autowired
    private WeChatIsvCompanyDefinitionService weChatIsvCompanyDefinitionService;

    @Autowired
    private WeChatIsvHttpUtils weChatIsvHttpUtils;

    @DubboReference(check = false)
    private ICommonService iCommonService;

    @Autowired
    private OssHandler ossHandler;

    @Autowired
    private WechatIsvContactTranslateDao wechatIsvContactTranslateDao;

    @Autowired
    private RestHttpUtils httpUtil;

    public void genUploadFile(String companyId) {
        //原有人员
        List<OpenThirdEmployee> srcEmployeeList = openThirdEmployeeDao.listEmployeeByCompanyIdAndOpenType(OpenType.WECHAT_ISV.getType(), companyId);
        //原有人员三方id
        List<String> srcThirdEmployeeIdList = srcEmployeeList.stream().map(OpenThirdEmployee::getThirdEmployeeId).collect(Collectors.toList());
        //原有部门
        List<OpenThirdOrgUnit> srcOrgUnitList = openThirdOrgUnitDao.listOrgUnitByCompanyIdAndOpenType(OpenType.WECHAT_ISV.getType(), companyId);
        //原有部门id
        List<String> srcOrgUnitIdList = srcOrgUnitList.stream().map(OpenThirdOrgUnit::getThirdOrgUnitId).collect(Collectors.toList());
        List<Map<String, String>> employeeIdList = new ArrayList<>();
        for (OpenThirdEmployee openThirdEmployee : srcEmployeeList) {
            Map<String, String> map = new HashMap<>();
            map.put("third_employee_id", openThirdEmployee.getThirdEmployeeId());
            map.put("name", "$userName=" + openThirdEmployee.getThirdEmployeeId() + "$");
            employeeIdList.add(map);
        }
    }


    /**
     * @param user
     * @param word
     * @param offset 查询的偏移量，每次调用的offset在上一次offset基础上加上limit
     * @param limit  查询返回的最大数量，默认为50，最多为200，查询返回的数量可能小于limit指定的值
     * @return
     */
    public EmployeeAndOrgUnitThirdIdsResponse searchContact(UserComInfoVO user, String word, Integer offset, Integer limit) {
        if (user == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.TOKEN_INFO_IS_ERROR));
        }

        String companyId = user.getCompany_id();
        WeChatIsvCompany weChatIsvCompany = weChatIsvCompanyDefinitionService.getByCompanyId(companyId);
        if (weChatIsvCompany == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WECHAT_ISV_COMPANY_UNDEFINED));
        }
        String url = "/cgi-bin/service/contact/search?provider_access_token=";
        WeChatIsvSearchContactRequest weChatIsvSearchContactRequest = new WeChatIsvSearchContactRequest();
        weChatIsvSearchContactRequest.setAgentid(weChatIsvCompany.getAgentid());
        weChatIsvSearchContactRequest.setAuthCorpid(weChatIsvCompany.getCorpId());
        weChatIsvSearchContactRequest.setQueryWord(word);
        weChatIsvSearchContactRequest.setOffset(offset);
        weChatIsvSearchContactRequest.setLimit(limit);
        // weChatIsvSearchContactRequest.setQueryType(1);
        String res = weChatIsvHttpUtils.postJsonWithProviderAccessToken(wechatHost + url, JsonUtils.toJson(weChatIsvSearchContactRequest));
        WeChatIsvSearchContactResponse weChatIsvSearchContactResponse = JsonUtils.toObj(res, WeChatIsvSearchContactResponse.class);
        if (weChatIsvSearchContactResponse == null || weChatIsvSearchContactResponse.getErrcode() != 0) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_ERROR));
        }
        EmployeeAndOrgUnitThirdIdsResponse employeeAndOrgUnitThirdIdsResponse = new EmployeeAndOrgUnitThirdIdsResponse();
        WeChatIsvSearchContactResponse.QueryResult queryResult = weChatIsvSearchContactResponse.getQueryResult();
        if (!ObjectUtils.isEmpty(queryResult) && !ObjectUtils.isEmpty(queryResult.getUser()) && !ObjectUtils.isEmpty(queryResult.getUser().getUserid())) {
            List<String> employeeList = queryResult.getUser().getUserid();
            //type 1：分贝id 2：第三方id, businessType 业务类型：1：部门 2：项目 3：员工
            List<CommonIdDTO> commonIdDTOS = iCommonService.queryIdDTO(companyId, employeeList, 2, 3);
            employeeAndOrgUnitThirdIdsResponse.setEmployeeList(commonIdDTOS);
        }
        if (!ObjectUtils.isEmpty(queryResult) && !ObjectUtils.isEmpty(queryResult.getParty()) && !ObjectUtils.isEmpty(queryResult.getParty().getDepartmentId())) {
            List<String> departmentId = queryResult.getParty().getDepartmentId();
            //type 1：分贝id 2：第三方id, businessType 业务类型：1：部门 2：项目 3：员工
            List<CommonIdDTO> commonIdDTOS = iCommonService.queryIdDTO(companyId, departmentId, 2, 1);
            employeeAndOrgUnitThirdIdsResponse.setOrgUnitList(commonIdDTOS);
        }
        return employeeAndOrgUnitThirdIdsResponse;
    }

    /**
     * 通讯录转译
     *
     * @param key
     */
    public String translateContactFromOss(String taskId, String key, String companyId) {
        //1.校验企业
        WeChatIsvCompany weChatIsvCompany = weChatIsvCompanyDefinitionService.getByCompanyId(companyId);
        if (weChatIsvCompany == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WECHAT_ISV_COMPANY_UNDEFINED));
        }
        String corpId = weChatIsvCompany.getCorpId();
        WechatIsvContactTranslate wechatIsvContactTranslate = wechatIsvContactTranslateDao.getByTaskId(taskId);
        if (wechatIsvContactTranslate != null) {
            return wechatIsvContactTranslate.getJobId();
        }
        //2.获取数据
        File file = ossHandler.getObjectToTemp(key);
        //3.上传微信
        file = fixFileSuffix(file, key);
        String mediaId = uploadFileToWechat(file);
        //4.转译
        String fileName = file.getName();
        String jobId = startTranslate(fileName, mediaId, corpId);
        //5.保存
        wechatIsvContactTranslate = new WechatIsvContactTranslate();
        wechatIsvContactTranslate.setTaskId(taskId);
        wechatIsvContactTranslate.setCorpId(corpId);
        wechatIsvContactTranslate.setOssKey(key);
        wechatIsvContactTranslate.setMediaId(mediaId);
        wechatIsvContactTranslate.setJobId(jobId);
        wechatIsvContactTranslate.setStatus(WeChatIsvContactTranslateState.WECHAT_PROCESS.getCode());
        wechatIsvContactTranslateDao.save(wechatIsvContactTranslate);
        return jobId;
    }

    private static File fixFileSuffix(File file, String ossKey) {
        String fileName = file.getAbsolutePath();
        //没有后缀的,从osskey取，如果osskey也没有，使用xlsx
        if (fileName.indexOf(".") <= 0) {
            String suffix = ".xlsx";
            if (ossKey.indexOf(".") > 0) {
                suffix = ossKey.substring(ossKey.lastIndexOf("."));
            }
            fileName = fileName + suffix;
            File newFile = new File(fileName);
            //更名时如果已存在先删除
            if (newFile.exists()) {
                newFile.delete();
            }
            boolean b = file.renameTo(newFile);
            if (!b) {
                log.error("文件更名失败！");
                throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_RANSLATE_FILE_RENAME_FAILED));
            }
            file = newFile;
        }
        return file;
    }

    /**
     * 上传需要转译的文件
     *
     * @param file
     */
    public String uploadFileToWechat(File file) {
        String url = wechatHost + "/cgi-bin/service/media/upload?&type=file&provider_access_token=";
        String res = weChatIsvHttpUtils.postFileWithProviderAccessToken(url, file);
        WeChatIsvUploadFileResponse weChatIsvUploadFileResponse = JsonUtils.toObj(res, WeChatIsvUploadFileResponse.class);
        if (weChatIsvUploadFileResponse == null || weChatIsvUploadFileResponse.getErrcode() != 0) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_UPDATE_FILE_FAILED));
        }
        file.delete();
        String mediaId = weChatIsvUploadFileResponse.getMediaId();
        return mediaId;
    }

    /**
     * 异步通讯录id转译
     *
     * @param fileName
     * @param mediaId
     * @param corpId
     */
    public String startTranslate(String fileName, String mediaId, String corpId) {
        String url = wechatHost + "/cgi-bin/service/contact/id_translate?provider_access_token=";
        WeChatIsvContactTranslateRequest weChatIsvContactTranslateRequest = new WeChatIsvContactTranslateRequest();
        weChatIsvContactTranslateRequest.setAuthCorpid(corpId);
        weChatIsvContactTranslateRequest.setOutputFileName(fileName);
        weChatIsvContactTranslateRequest.setMediaIdList(Lists.newArrayList(mediaId));
        String res = weChatIsvHttpUtils.postJsonWithProviderAccessToken(url, JsonUtils.toJson(weChatIsvContactTranslateRequest));
        WeChatIsvContactTranslateResponse weChatIsvContactTranslateResponse = JsonUtils.toObj(res, WeChatIsvContactTranslateResponse.class);
        if (weChatIsvContactTranslateResponse == null || weChatIsvContactTranslateResponse.getErrcode() != 0) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_CONTACT_TRANSLATE_FAILED));
        }
        return weChatIsvContactTranslateResponse.getJobid();
    }


    /**
     * 获取异步任务结果
     *
     * @param jobId
     * @return
     */
    public WeChatIsvGetTranslateResultResponse getTranslateResult(String jobId) {
        String url = wechatHost + "/cgi-bin/service/batch/getresult";
        Map<String, Object> params = new HashMap<>();
        params.put("jobid", jobId);
        String res = weChatIsvHttpUtils.getWithProviderAccessToken(url, params);
        WeChatIsvGetTranslateResultResponse weChatIsvGetTranslateResultResponse = JsonUtils.toObj(res, WeChatIsvGetTranslateResultResponse.class);
        return weChatIsvGetTranslateResultResponse;
    }


    /**
     * @param wechatIsvContactTranslate
     */
    public void callbackTranslateResult(WechatIsvContactTranslate wechatIsvContactTranslate) {
        String jobId = wechatIsvContactTranslate.getJobId();
        String taskId = wechatIsvContactTranslate.getTaskId();
        WeChatIsvGetTranslateResultResponse weChatIsvGetTranslateResultResponse = getTranslateResult(jobId);
        int status = WeChatIsvContactTranslateState.WECHAT_PROCESS.getCode();
        String downloadUrl = "";
        if (weChatIsvGetTranslateResultResponse == null || weChatIsvGetTranslateResultResponse.getErrcode() != 0) {
            log.info("微信转译失败" + weChatIsvGetTranslateResultResponse == null ? "" : weChatIsvGetTranslateResultResponse.getErrmsg());
            status = WeChatIsvContactTranslateState.FAILED.getCode();
        } else {
            status = WeChatIsvContactTranslateState.SUCCESS.getCode();
            downloadUrl = weChatIsvGetTranslateResultResponse.getResult().getContactIdTranslate().getUrl();
        }
        String harmonyUrl = harmonyHost + "/internal/export/outer/" + taskId + "/progress";
        Map<String, Object> param = new HashMap<>();
        param.put("status", status);
        param.put("downloadUrl", downloadUrl);
        String res = httpUtil.postJson(harmonyUrl, JsonUtils.toJson(param));
    }
}
