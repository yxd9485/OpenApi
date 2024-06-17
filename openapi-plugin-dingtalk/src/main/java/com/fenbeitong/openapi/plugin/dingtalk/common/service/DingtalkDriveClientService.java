package com.fenbeitong.openapi.plugin.dingtalk.common.service;

import com.aliyun.dingtalkdrive_1_0.models.AddFileHeaders;
import com.aliyun.dingtalkdrive_1_0.models.AddFileRequest;
import com.aliyun.dingtalkdrive_1_0.models.AddFileResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiFileUploadSingleRequest;
import com.dingtalk.api.request.OapiProcessinstanceCspaceInfoRequest;
import com.dingtalk.api.request.OapiV2UserGetRequest;
import com.dingtalk.api.response.OapiFileUploadSingleResponse;
import com.dingtalk.api.response.OapiProcessinstanceCspaceInfoResponse;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.util.FileUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.taobao.api.ApiException;
import com.taobao.api.FileItem;
import com.taobao.api.internal.util.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * @author xiaohai
 * @date 2022/03/21
 */
@ServiceAspect
@Service
@Slf4j
public class DingtalkDriveClientService {

    @Autowired
    private IDingtalkIsvCompanyAuthService dingtalkIsvCompanyAuthService;

    /**
     * 使用 Token 初始化账号Client
     * @return Client
     * @throws Exception
     */
    public static com.aliyun.dingtalkdrive_1_0.Client createClient() throws Exception {
        Config config = new Config();
        config.protocol = "https";
        config.regionId = "central";
        return new com.aliyun.dingtalkdrive_1_0.Client(config);
    }

    /**
     * 添加文件
     */
    public AddFileResponse addFiles( AddFileRequest addFileRequest  , String accessToken , String spaceId) throws Exception {
        java.util.List<String> args = java.util.Arrays.asList();
        com.aliyun.dingtalkdrive_1_0.Client client = createClient();
        AddFileHeaders addFileHeaders = new AddFileHeaders();
        addFileHeaders.xAcsDingtalkAccessToken = accessToken;
        try {
            AddFileResponse addFileResponse = client.addFileWithOptions(spaceId, addFileRequest, addFileHeaders, new RuntimeOptions());
            return addFileResponse;
        } catch (TeaException err) {
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
                log.warn(err.message);
                throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_DINGTALK_ERROR);
            }
        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
                log.warn(err.message);
                throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_DINGTALK_ERROR);
            }
        }
        return null;
    }

    public OapiFileUploadSingleResponse uploadFile(OapiFileUploadSingleRequest request, String fileName, InputStream inputStream , String accessToken) {
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/file/upload/single?"+ WebUtils.buildQuery(request.getTextParams(),"utf-8"));
           //必须重新new一个请求
            request = new OapiFileUploadSingleRequest();
            request.setFile(new FileItem(fileName , inputStream));
            OapiFileUploadSingleResponse response = client.execute(request, accessToken);
            log.info("调用钉钉上传文件接口完成，参数: accessToken: {}, req: {}，result: {}", accessToken, JsonUtils.toJson(request), response.getBody());
            return response;
        } catch (Exception e) {
            log.warn("调用钉钉上传文件接口异常：", e);
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_DINGTALK_ERROR);
        }
    }

    /**
     * 查询用户信息
     * @param req
     * @param accessToken
     * @return
     */
    public OapiV2UserGetResponse userGetInfo(OapiV2UserGetRequest req, String accessToken) {
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/get");
            OapiV2UserGetResponse resp = client.execute(req, accessToken);
            return resp;
        } catch (ApiException e) {
            log.warn("调用钉钉获取用户信息接口异常：", e);
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_DINGTALK_ERROR);
        }
    }

    public List<AddFileResponse>  uploadFileToDingtalk(String corpId , Long agentid , String userId , List<String> applyAttachmentUrls){
        OapiV2UserGetRequest req = new OapiV2UserGetRequest();
        String accessToken = dingtalkIsvCompanyAuthService.getCorpAccessTokenByCorpId( corpId );
        req.setUserid(userId);
        OapiV2UserGetResponse oapiV2UserGetResponse = userGetInfo(req, accessToken);
        String unionid = oapiV2UserGetResponse.getResult().getUnionid();
        List<AddFileResponse> listFiles = new ArrayList<>();
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/processinstance/cspace/info");
            OapiProcessinstanceCspaceInfoRequest spaceInfoReq = new OapiProcessinstanceCspaceInfoRequest();
            spaceInfoReq.setUserId( userId );
            spaceInfoReq.setAgentId( agentid );
            OapiProcessinstanceCspaceInfoResponse response = client.execute(spaceInfoReq, accessToken);
            String spaceId = StringUtils.obj2str( response.getResult().getSpaceId() );
            applyAttachmentUrls.forEach(url -> {
                if(StringUtils.isNotBlank(url)){
                    String[] split = url.split("/");
                    String fileName = split[split.length-1];
                    BufferedInputStream bufferedInputStream = null;
                    try{
                        bufferedInputStream = FileUtils.httpFile( url );
                        OapiFileUploadSingleRequest request = new OapiFileUploadSingleRequest();
                        request.setFileSize(45L);
                        request.setAgentId(StringUtils.obj2str( agentid ));
                        OapiFileUploadSingleResponse oapiFileUploadSingleResponse = uploadFile(request, fileName, bufferedInputStream, accessToken);
                        String mediaId = oapiFileUploadSingleResponse.getMediaId();
                        AddFileRequest addFileRequest = new AddFileRequest()
                            .setParentId("0")
                            .setFileType("file")
                            .setFileName(fileName)
                            .setMediaId(mediaId)
                            .setAddConflictPolicy("autoRename")
                            .setUnionId(unionid);
                        AddFileResponse addFileResponse = addFiles(addFileRequest, accessToken, spaceId);
                        listFiles.add( addFileResponse );
                    }catch (Exception e){
                        log.warn("调用钉钉上传文件接口异常：", e);
                        throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_DINGTALK_ERROR);
                    }finally {
                        if(bufferedInputStream!=null){
                            try {
                                bufferedInputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_DINGTALK_ERROR);
        }
       return listFiles;
    }

}
