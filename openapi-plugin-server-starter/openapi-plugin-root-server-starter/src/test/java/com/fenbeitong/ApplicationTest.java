package com.fenbeitong;

import com.alibaba.fastjson.JSON;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiServiceGetCorpTokenRequest;
import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.dingtalk.api.response.OapiServiceGetCorpTokenResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.fenbeimeta.sdk.enums.common.MetaCategoryTypeEnum;
import com.fenbeitong.fenbeimeta.sdk.enums.common.SystemEnum;
import com.fenbeitong.fenbeimeta.sdk.model.dto.BaseOperationDTO;
import com.fenbeitong.fenbeimeta.sdk.model.vo.data.DataListSimpleVO;
import com.fenbeitong.fenbeimeta.sdk.service.companyconfig.ICompanyObjectsConfigService;
import com.fenbeitong.fenbeimeta.sdk.service.order.IMetaOrderService;
import com.fenbeitong.finhub.common.constant.FinhubMessageCode;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.finhub.kafka.consumer.KafkaConsumerUtils;
import com.fenbeitong.noc.api.service.altman.model.vo.AltmanStereoInfoVO;
import com.fenbeitong.noc.api.service.altman.service.IAltmanOrderSearchService;
import com.fenbeitong.openapi.plugin.event.saas.dto.WebHookOrderEvent;
import com.fenbeitong.openapi.plugin.support.util.SuperAdminUtils;
import com.fenbeitong.openapi.plugin.util.*;
import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingTalkConstant;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkCarApplyProcessInfo;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkTripApplyProcessInfo;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.event.core.EventBusCenter;
import com.fenbeitong.openapi.plugin.event.saas.dto.ApplyPushEvents;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkSyncService;
import com.fenbeitong.openapi.plugin.moka.util.SignHelper;
import com.fenbeitong.openapi.plugin.support.common.service.OpenIdTranService;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigCode;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportBindEmployeeReqDTO;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportEmployeeBindInfo;
import com.fenbeitong.openapi.plugin.support.employee.service.AbstractEmployeeService;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.service.DepartmentUtilService;
import com.fenbeitong.openapi.plugin.support.project.service.OpenProjectService;
import com.fenbeitong.openapi.plugin.support.util.ApiJWTToken;
import com.fenbeitong.openapi.plugin.support.util.ApiJwtTokenTool;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.dto.UserInfoResponse;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.CompanyAuthResponse;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvEmployeeService;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvPullThirdOrgService;
import com.fenbeitong.openapi.sdk.dto.common.OpenApiRespDTO;
import com.fenbeitong.openapi.sdk.dto.organization.DeleteOrgUnitReqDTO;
import com.fenbeitong.openapi.sdk.dto.organization.OrgUnitDTO;
import com.fenbeitong.openapi.sdk.dto.project.ListThirdProjectRespDTO;
import com.fenbeitong.openapi.sdk.webservice.organization.FbtOrganizationService;
import com.fenbeitong.openapi.sdk.webservice.project.FbtProjectService;
import com.fenbeitong.usercenter.api.model.dto.contracts.ContractInfoDTO;
import com.fenbeitong.usercenter.api.model.enums.common.IdBusinessTypeEnums;
import com.fenbeitong.usercenter.api.service.company.ICompanyNewInfoService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.luastar.swift.base.net.HttpClientUtils;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ObjectUtils;
import retrofit2.Call;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Slf4j
public class ApplicationTest extends AbstractEmployeeService {

    @Autowired
    private ApiJwtTokenTool jwtTokenTool;

    @Autowired
    WeChatIsvEmployeeService weChatIsvEmployeeService;

    @Autowired
    WeChatIsvPullThirdOrgService weChatIsvPullThirdOrgService;

    @Autowired
    WeChatIsvCompanyAuthService weChatIsvCompanyAuthService;

    @Autowired
    private FbtOrganizationService fbtOrganizationService;

    @Autowired
    IFxkSyncService IFxkSyncService;

    @Autowired
    DepartmentUtilService departmentUtilService;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    OpenSysConfigDao openSysConfigDao;

    @Autowired
    private RestHttpUtils httpUtils;

    @Autowired
    private EventBusCenter eventBusCenter;

    @DubboReference(check = false)
    private IAltmanOrderSearchService altmanOrderSearchService;

    @Autowired
    private ICompanyObjectsConfigService iCompanyObjectsConfigService;

    @Autowired
    private IMetaOrderService iMetaOrderService;

    @Autowired
    private SuperAdminUtils superAdminUtils;

    @Autowired
    private OpenIdTranService openIdTranService;

    @Test
    public void tranId() {
        String companyId = "5ebbe50b23445f707937a0b2";
        String thirdId = "157099533157";
        String fbId = "5ebbe51223445f707937a0bb";
        Integer bizType = IdBusinessTypeEnums.ORG.getKey();
        // 三方转分贝通 取的thirdId
        String fbIdRes = openIdTranService.thirdIdToFbId(companyId, thirdId, bizType);
        // 分贝通转三方 取的是thirdId
        String thirdIdRes = openIdTranService.fbIdToThirdId(companyId, fbId, bizType);
        System.out.println("分贝id" + fbIdRes);
        System.out.println("三方id" + thirdIdRes);
        System.out.println("------------");
        Map<String, String> map1 = openIdTranService.thirdIdToFbIdBatch(companyId, Lists.newArrayList("157099533157", "125021620581", "188462081255"), bizType);
        Map<String, String> map2 = openIdTranService.fbIdToThirdIdBatch(companyId, Lists.newArrayList("5ebbe51223445f707937a0bb", "5ebbe51223445f707937a0bd", "5ebbe51223445f707937a0be"), bizType);
        System.out.println(JsonUtils.toJson(map1));
        System.out.println(JsonUtils.toJson(map2));
    }

    @Test
    public void metaData() {
        String companyId = "5747fbc10f0e60e0709d8d7d";
        String adminId = superAdminUtils.superAdmin(companyId);
        BaseOperationDTO baseOperationDTO = new BaseOperationDTO().setCompanyId(companyId).setCurrentOperatorId(
            adminId).setCurrentOperatorName(adminId);
        Optional<String> optionalApiName = iCompanyObjectsConfigService.queryObjectApiName(baseOperationDTO, SystemEnum.FENBEI_NOC, MetaCategoryTypeEnum.ULTRA_MAN);
        String apiName = optionalApiName.orElseThrow(() -> new FinhubException(FinhubMessageCode.ILLEGAL_ARGUMENT, "请初始化对象"));
        String jsonData = "[{\n" +
            "    \"bizOrderId\":\"bizOrderId11\",\n" +
            "    \"bizTicketNo\":\"bizTicketNo11\",\n" +
            "    \"bizOrderStatus\":\"bizOrderStatus11\",\n" +
            "    \"bizConsumerName\":\"bizConsumerName11\",\n" +
            "    \"bizTravel\":\"bizTravel11\",\n" +
            "    \"bizNumber\":\"bizNumber11\",\n" +
            "    \"bizTravelTime\":\"bizTravelTime11\",\n" +
            "    \"bizFee\":\"1\",\n" +
            "    \"bizServiceFee\":\"1\",\n" +
            "    \"bizExt1\":\"主订单号11111\",\n" +
            "    \"bizExt2\":\"617f8e59bfc33d7dad1e4b90\",\n" +
            "    \"bizExt3\":\"ce单号11111\"\n" +
            "}]";
        DataListSimpleVO dataListSimpleVO = iMetaOrderService.queryDataList(baseOperationDTO, apiName, jsonData);
        System.out.println(JsonUtils.toJson(dataListSimpleVO));
    }

    @Test
    public void testOrderInfo() {
//        String orderId = "OAM211118170125227031137";
        String orderId = "OAM211119103606406198799";
        AltmanStereoInfoVO fenbeinoc = altmanOrderSearchService.stereoDetail(orderId, null, "stereo", "openapi", "openapi");
        System.out.println(JsonUtils.toJson(fenbeinoc));
    }

    /**
     * 获取moka 的部门信息
     */
    @Test
    public void getMokaDepartment() {
        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("entCode", "031de2919f2d40d8950dc659d78a52a5");
        paraMap.put("apiCode", "2e4613e658d7262cf106dc4e4120eeca");
        paraMap.put("userName", "zhougx1@fadada.com");
        paraMap.put("timestamp", System.currentTimeMillis());
        paraMap.put("nonce", 472909);
        paraMap.put("sign", SignHelper.getSignStr(paraMap));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic ZjllODgwYTVkYTkwNDczY2ExZTQ1MDRmZmJiYWY3NjE6");
        String result = httpUtils.get("https://api.mokahr.com/api-platform/hcm/oapi/v1/data", headers, paraMap);

        log.info(result);
    }

    /**
     * 获取moka 的人员信息
     */

    @Test
    public void getMokaUser() {
        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("entCode", "031de2919f2d40d8950dc659d78a52a5");
        paraMap.put("apiCode", "a3b388d567698c9e4cf31bb346b0085a");
        paraMap.put("userName", "zhougx1@fadada.com");
        paraMap.put("timestamp", System.currentTimeMillis());
        paraMap.put("nonce", 472909);
        paraMap.put("sign", SignHelper.getSignStr(paraMap));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic ZjllODgwYTVkYTkwNDczY2ExZTQ1MDRmZmJiYWY3NjE6");
        String result = httpUtils.get("https://api.mokahr.com/api-platform/hcm/oapi/v1/data", headers, paraMap);

        log.info(result);
    }


    @DubboReference(check = false)
    ICompanyNewInfoService iCompanyNewInfoService;

    @Test
    public void test() {
        Map<String, String> map = new HashMap<>();
        map.put("appId", "5d232e1b23445f75406ed26a");
        try {
            ApiJWTToken apiJWTToken = jwtTokenTool.genJWTToken(map);
            String token = apiJWTToken.getToken();
            System.out.println(token);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 企业微信
     */

    @Test
    public void test1() {
        HashMap<String, Object> configMap = Maps.newHashMap();
        configMap.put("code", "ww8c3bdf11c0c1742d");
        configMap.put("type", OpenSysConfigCode.WX_AUTH_CONFIG.getCode());
        OpenSysConfig openSysConfig = openSysConfigDao.getOpenSysConfig(configMap);

        if (!ObjectUtils.isEmpty(openSysConfig)) {
            // 企业wqxc token
            String userInfoUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=" + "sLzncn9jFCOgkj9mBtXFHDf9Xe2PBpzY5V4NfhEwqsqnCf57Az3_1NWpLmFMb1NvR0hkVZN9v8Y_t3bJ9GjrpJDJhcW4EDXJdpA-VGlVs8dvKHWskp-1EqRaiJL5Ul_4I3WTxVATcnwoAwtGhhOc38BcGfge8gvsrBgfWKjDncnGZk-4Rp_GeLeod0lFOAsH6l9e0beJXrW8PHPf-qZ3jA" +
                "&userid=" + "linzhi.shi";
            // 3.返回token
            String userInfo = HttpClientUtils.get(userInfoUrl, 3000);
            // 员工号
            String jobNumber = "";
            log.info("wechat auth userInfo = {}", userInfo);
            UserInfoResponse userInfoResponse = JsonUtils.toObj(userInfo, UserInfoResponse.class);
            if (userInfoResponse == null || (Optional.ofNullable(userInfoResponse.getErrcode()).orElse(-1) != 0)) {
                throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_CORP_EMPLOYEE_NOT_EXISTS));
            }
            UserInfoResponse.ExtattrBean extattrBean = userInfoResponse.getExtAttr();
            if (!ObjectUtils.isEmpty(extattrBean) && extattrBean.getAttrs().size() > 0) {
                jobNumber = userInfoResponse.getAttrValueByAttrName("工号", "0");

                log.info("工号：{}", jobNumber);
            }
        }
    }

    /**
     * 纷享销客删除企业 5efae27c23445f5ca563162d
     * moka 5d232e1b23445f75406ed26a
     * 法大大 Dev 5fbcf10427f65fe543fd8617   41357
     * 海普诺凯2 Dev 5fcd9d0527f65f391e9e83bf   5
     * 我爱我家 test 5f927eb6aa4227afdc398981 5f927eb6aa4227afdc398981
     * 木偶公司 Dev 5d1b1d2f23445f4dca76304b ww8c3bdf11c0c1742d
     * 宁波伟立 5fec39b627f65f619a022265 623054110703876
     */

    final String companyId = "5f2a498223445f6c85fac354";
    final String topId = "1617660";


    @Test
    public void deleteCompany() {
        clearAllEmployee(companyId);
        Call<OpenApiRespDTO<List<OrgUnitDTO>>> call = fbtOrganizationService.queryAll(companyId);
        DeleteOrgUnitReqDTO deleteOrgUnitReqDTO = new DeleteOrgUnitReqDTO();
        final List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<OpenThirdOrgUnitDTO>();
        String operatorId = superAdmin(companyId);
        deleteOrgUnitReqDTO.setCompanyId(companyId);
        deleteOrgUnitReqDTO.setOperatorId(operatorId);
        try {
            List<OrgUnitDTO> orgUnitDTOS = call.execute().body().getData();
            // 转换部门
            orgUnitDTOS.forEach(t -> {
                OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
                openThirdOrgUnitDTO.setCompanyId(companyId);
                openThirdOrgUnitDTO.setThirdOrgUnitParentId(t.getOrgThirdUnitParentId());
                openThirdOrgUnitDTO.setThirdOrgUnitId(t.getOrgThirdUnitId());
                departmentList.add(openThirdOrgUnitDTO);
            });

            // 部门排序
            final List<OpenThirdOrgUnitDTO> departmentList1 = departmentUtilService.deparmentSort(departmentList, topId);
            Collections.reverse(departmentList1);
            departmentList1.forEach(t -> {
                deleteOrgUnitReqDTO.setThirdOrgId(t.getThirdOrgUnitId());
                Call<OpenApiRespDTO> delete = fbtOrganizationService.delete(deleteOrgUnitReqDTO);
                OpenApiRespDTO body = null;
                try {
                    body = delete.execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String msg = body.getMsg();
                log.info("删除信息:{}", msg);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 纷享销客删除企业人员 5efae27c23445f5ca563162d
     * moka 5d232e1b23445f75406ed26a
     */
    @Test
    public void deletePerson() {
        clearAllEmployee(companyId);
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }


    @Test
    public void syncAll() {
        weChatIsvPullThirdOrgService.pullThirdOrg("ww557cec61d4919573");
    }

    @Test
    public void clearAllEmployee() {
        weChatIsvEmployeeService.clearAllEmployee("5e7c43ff23445f3a9aaff8ad", "5e7c43ff23445f3a9aaff8ae");
    }

    @Test
    public void bindUser() {
        SupportBindEmployeeReqDTO bindEmployeeReq = new SupportBindEmployeeReqDTO();
        bindEmployeeReq.setCompanyId("5e7c43ff23445f3a9aaff8ad");
        bindEmployeeReq.setBindList(Lists.newArrayList(SupportEmployeeBindInfo.builder().phone("13046909949").thirdEmployeeId("15311410000").build()));
        bindEmployeeReq.setOperatorId("5e7c43ff23445f3a9aaff8ae");
        weChatIsvEmployeeService.bindUser(bindEmployeeReq);
    }

    @Test
    public void testInit() {
        String res = "{\"access_token\":\"gjmHXrECYLxLAcDaQxuIuqWAWAefb8KwX90GbLpEyr2EKeDofQFCrzzF2jW-5E_a8YFVHeknwYTIdMt7KEC9tP1aWFNrARSMGwDNEFVDqSsXKXSoXsrsnMkcKp-EygDE03BCWXaU09640pTtXbtE5PdnyQAnj3yfDJgNq30_D1TLoDNWVMPTEVP_x6KmDw62DmeLDJ5A7Y26tHcZSOOW_w\",\"expires_in\":7200,\"permanent_code\":\"PhyD4YgSiujjH-IIIeRe3QpEnWDsKxgkk9xBJVraPag\",\"auth_corp_info\":{\"corpid\":\"lz1557cea61d4919013\",\"corp_name\":\"lz测试企业202009211130\",\"corp_type\":\"verified\",\"corp_round_logo_url\":\"\",\"corp_square_logo_url\":\"https://p.qlogo.cn/bizmail/FMicukDErQT0WUFlQLlzNb5lgBpuu5Vx7JiclGzzTWLNLJFl2CVDUD9w/0\",\"corp_user_max\":300,\"corp_agent_max\":300,\"corp_wxqrcode\":\"http://p.qpic.cn/pic_wework/4146136874/f4f277fc71dc0579631faeb83a8ad9f08ab05a2c45f6aac1/0\",\"corp_full_name\":\"lz测试企业202009211130\",\"subject_type\":1,\"verified_end_time\":1614237303,\"corp_scale\":\"101-200人\",\"corp_industry\":\"IT服务\",\"corp_sub_industry\":\"计算机软件/硬件/信息服务\",\"location\":\"北京市\"},\"auth_info\":{\"agent\":[{\"agentid\":1000035,\"name\":\"lz测试企业202009211130\",\"square_logo_url\":\"https://wework.qpic.cn/bizmail/UbOQIo9psHiaPkHqoDyMTKbZ1aH5flJwx9heHWVttq4XpZYYoXVCQRw/0\",\"privilege\":{\"level\":1,\"allow_party\":[],\"allow_user\":[\"qy0138c78ca829b99bb3e2ec3981\",\"TengShengLong\",\"15311410634\"],\"allow_tag\":[],\"extra_party\":[],\"extra_user\":[],\"extra_tag\":[]}}]},\"auth_user_info\":{\"userid\":\"15311410001\",\"name\":\"张三\",\"avatar\":\"https://wework.qpic.cn/bizmail/0ETxTLCkjRUFCc6lKicyqUicx7AK2AxaE9fgM3yAQFOb2X7dUCRlKLag/0\"}}";
        CompanyAuthResponse companyAuthResponse = JsonUtils.toObj(res, CompanyAuthResponse.class);
        weChatIsvCompanyAuthService.initCompany(companyAuthResponse);
    }

    @Test
    public void testContact() {
        ContractInfoDTO contractInfoDTO = new ContractInfoDTO();
        contractInfoDTO.setCompanyId("5e7c92ee23445f45dca30b42");
        contractInfoDTO.setEndDate(DateUtils.toDate("2021-03-18"));
        iCompanyNewInfoService.updateCompanyContractInfo(contractInfoDTO);
    }


    @Autowired
    OpenProjectService openProjectService;
    @Autowired
    private FbtProjectService fbtProjectService;

    String projectToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxLUprV2hRRnRmNWlidzRFY3AxYmYwMS9rN1E1aG9rcWVNNjVnVHdsaTNwSjljbDQra3JpZ2c3Y0dRYVNrZjNIaFpZNGpWUUxoMVZRY3N2Qy9KUHh0TnBadktqNmtUZ1BtQyIsImlzcyI6ImZlbmJlaXRvbmciLCJleHAiOjE2MTg0Njg5MzAsImlhdCI6MTYxNTg3NjkzMCwianRpIjoiZURrUGQxNk5VUmdTdENyYjg5ak1LVm4waHRNV2lUY1pDMEtoTWhSQ1M0ZEF3eHhPekFLZTkyNEdPa3VqQmRIeSJ9.77ls42X1AGxXtbgOfqgFnN1QC5kcA8berA5WUnBEj4A";
    String projectCompanyId = "5efae27c23445f5ca563162d";

    @Test
    public void deleteProject1() {
        ListThirdProjectRespDTO listThirdProjectRespDTO = openProjectService.getProjectByCompanyId(projectCompanyId);
        List<String> list = listThirdProjectRespDTO.getData().stream().map(t -> t.getId()).collect(Collectors.toList());
        Map<String, String> headMap = Maps.newHashMap();
        headMap.put("Content-Type", "application/json");
        headMap.put("X-Auth-Token", projectToken);
        String url = "http://usercenter-dev.fenbeijinfu.com/uc/project/center/batch_delete";
        List<List<String>> gropList = groupListByQuantity(list, 1000);
        gropList.forEach(t -> {
            Map<String, List> reqJson = Maps.newHashMap();
            reqJson.put("costcenterIds", t);
            HttpHeaders httpHeaders = new HttpHeaders();
            if (!ObjectUtils.isEmpty(headMap)) {
                for (String key : headMap.keySet()) {
                    httpHeaders.add(key, headMap.get(key));
                }
            }
            RestHttpUtils.postJson(url, httpHeaders, JsonUtils.toJson(reqJson));
        });


    }

    /**
     * 项目删除
     */

    public void deleteProject(ListThirdProjectRespDTO listThirdProjectRespDTO) {
        List<String> list = listThirdProjectRespDTO.getData().stream().map(t -> t.getId()).collect(Collectors.toList());

        Map<String, String> headMap = Maps.newHashMap();
        headMap.put("Content-Type", "application/json");
        headMap.put("X-Auth-Token", projectToken);
        String url = "http://usercenter-dev.fenbeijinfu.com/uc/project/center/batch_delete";
        List<List<String>> gropList = groupListByQuantity(list, 1000);
        gropList.forEach(t -> {
            Map<String, List> reqJson = Maps.newHashMap();
            reqJson.put("costcenterIds", t);
            HttpHeaders httpHeaders = new HttpHeaders();
            if (!ObjectUtils.isEmpty(headMap)) {
                for (String key : headMap.keySet()) {
                    httpHeaders.add(key, headMap.get(key));
                }
            }
            RestHttpUtils.postJson(url, httpHeaders, JsonUtils.toJson(reqJson));
        });


    }


    /**
     * 项目停用并删除项目
     */
    @Test
    public void stopProject() {
        ListThirdProjectRespDTO listThirdProjectRespDTO = openProjectService.getProjectByCompanyId(projectCompanyId);
        List<String> list = listThirdProjectRespDTO.getData().stream().map(t -> t.getId()).collect(Collectors.toList());

        Map<String, String> headMap = Maps.newHashMap();
        headMap.put("Content-Type", "application/json");
        headMap.put("X-Auth-Token", projectToken);
        String url = "http://usercenter-dev.fenbeijinfu.com/uc/project/center/updateStateByBatch";

        List<List<String>> gropList = groupListByQuantity(list, 100);

        gropList.forEach(t -> {

            Map<String, Object> reqJson = Maps.newHashMap();
            reqJson.put("idList", t);
            reqJson.put("state", 0);
            HttpHeaders httpHeaders = new HttpHeaders();
            if (!ObjectUtils.isEmpty(headMap)) {
                for (String key : headMap.keySet()) {
                    httpHeaders.add(key, headMap.get(key));
                }
            }
            RestHttpUtils.postJson(url, httpHeaders, JsonUtils.toJson(reqJson));

        });

        deleteProject(listThirdProjectRespDTO);


    }

    private List groupListByQuantity(List list, int quantity) {
        if (list == null || list.size() == 0) {
            return list;
        }
        if (quantity <= 0) {
            new IllegalArgumentException("Wrong quantity.");
        }
        List wrapList = new ArrayList();
        int count = 0;
        while (count < list.size()) {
            wrapList.add(list.subList(count, (count + quantity) > list.size() ? list.size() : count + quantity));
            count += quantity;
        }
        return wrapList;
    }

    @Autowired
    private IDingtalkIsvCompanyAuthService dingtalkIsvCompanyAuthService;
    /**
     * 获取市场应用token
     */
    String corpId = "ding6f2254dff8ac0ee924f2f5cc6abecb85"; //dev2

    @Test
    public void gitIsvToken() {
        String accessToken = dingtalkIsvCompanyAuthService.getCorpAccessTokenByCorpId(corpId);
        log.info("accessToken:  {}", accessToken);
    }


    @Test
    public void ttt() {
        DefaultDingTalkClient client = new DefaultDingTalkClient("http://open-dingtalk-proxy.fenbeijinfu.com/service/get_corp_token");
        OapiServiceGetCorpTokenRequest req = new OapiServiceGetCorpTokenRequest();
        req.setAuthCorpid("ding811986329e737983");
        try {
            OapiServiceGetCorpTokenResponse response = client.execute(req, "suiteuo7mfutvq3xc56r0", "IxTEN7jNnxAswKNNiB6WVQIksM0avWhrB7tixs92XeUEJlyqR8vYhFVSwTZ6qJUV", "suiteTicket");
            log.info("调用钉钉accessToken接口完成，返回结果：{}", response.getBody());
        } catch (ApiException e) {
            log.warn("调用钉钉accessToken接口异常", e);
        }
    }

    @Test
    public void testCar() {
        List<DingtalkCarApplyProcessInfo.UseCarApplyRule> UseCarApplyRules = Lists.newArrayList(
            DingtalkCarApplyProcessInfo.UseCarApplyRule.builder().type("taxi_scheduling_fee").value(-1).build(),
            DingtalkCarApplyProcessInfo.UseCarApplyRule.builder().type("allow_same_city").value(false).build(),
            DingtalkCarApplyProcessInfo.UseCarApplyRule.builder().type("allow_called_for_other").value(true).build(),
            DingtalkCarApplyProcessInfo.UseCarApplyRule.builder().type("price_limit").value(-1).build(),
            DingtalkCarApplyProcessInfo.UseCarApplyRule.builder().type("day_price_limit").value(-1).build(),
            DingtalkCarApplyProcessInfo.UseCarApplyRule.builder().type("times_limit_flag").value(0).build()
        );

//        List<DingtalkCarApplyProcessInfo.UseCarApplyRule> useCarApplyRules = new ArrayList<>();
        DingtalkTripApplyProcessInfo.TripListBean tripListBean = new DingtalkTripApplyProcessInfo.TripListBean();
        String formStr = "[{\"componentType\":\"TextareaField\",\"id\":\"TextareaField_IOUGFF5VEY80\",\"name\":\"申请事由\",\"value\":\"受试者随访\"},{\"componentType\":\"TableField\",\"extValue\":\"{\\\"statValue\\\":[],\\\"componentName\\\":\\\"TableField\\\"}\",\"id\":\"TableField_MKFGUYL33R40\",\"name\":\"用车城市\",\"value\":\"[{\\\"rowValue\\\":[{\\\"componentType\\\":\\\"TextNote\\\",\\\"label\\\":\\\"说明\\\",\\\"value\\\":\\\"用车城市个数不可超过10个\\\",\\\"key\\\":\\\"TextNote_NL74BG13DUO0\\\"},{\\\"componentType\\\":\\\"AddressField\\\",\\\"label\\\":\\\"城市\\\",\\\"extendValue\\\":{\\\"province\\\":{\\\"name\\\":\\\"海南省\\\",\\\"id\\\":\\\"460000\\\"},\\\"city\\\":{\\\"name\\\":\\\"海口市\\\",\\\"id\\\":\\\"460100\\\"},\\\"district\\\":{\\\"name\\\":\\\"龙华区\\\",\\\"id\\\":\\\"460106\\\"}},\\\"value\\\":\\\"海南省,海口市,龙华区\\\",\\\"key\\\":\\\"AddressField_L8ZBBYSO0TC0\\\"}],\\\"rowNumber\\\":\\\"TableField_MKFGUYL33R40_1OFFQ3PEWLPC0\\\"},{\\\"rowValue\\\":[{\\\"componentType\\\":\\\"TextNote\\\",\\\"label\\\":\\\"说明\\\",\\\"value\\\":\\\"用车城市个数不可超过10个\\\",\\\"key\\\":\\\"TextNote_NL74BG13DUO0\\\"},{\\\"componentType\\\":\\\"AddressField\\\",\\\"label\\\":\\\"城市\\\",\\\"extendValue\\\":{\\\"province\\\":{\\\"name\\\":\\\"海南省\\\",\\\"id\\\":\\\"460000\\\"},\\\"city\\\":{\\\"name\\\":\\\"三亚市\\\",\\\"id\\\":\\\"460200\\\"},\\\"district\\\":{\\\"name\\\":\\\"天涯区\\\",\\\"id\\\":\\\"460204\\\"}},\\\"value\\\":\\\"海南省,三亚市,天涯区\\\",\\\"key\\\":\\\"AddressField_L8ZBBYSO0TC0\\\"}],\\\"rowNumber\\\":\\\"TableField_MKFGUYL33R40_514EFQBA9FG0\\\"}]\"},{\"componentType\":\"DDDateRangeField\",\"id\":\"DDDateRangeField_1K8C58TRD7Y8\",\"name\":\"[\\\"开始时间\\\",\\\"结束时间\\\"]\",\"value\":\"[\\\"2022-02-25\\\",\\\"2022-02-26\\\",null]\"},{\"componentType\":\"NumberField\",\"id\":\"NumberField_1Z4WNFE0689S0\",\"name\":\"用车次数\",\"value\":\"4\"},{\"componentType\":\"MoneyField\",\"id\":\"MoneyField_1HWHV3FP13C00\",\"name\":\"用车费用\",\"value\":\"150\"},{\"componentType\":\"MoneyField\",\"extValue\":\"{\\\"upper\\\":\\\"壹佰伍拾元整\\\",\\\"componentName\\\":\\\"MoneyField\\\"}\",\"id\":\"MoneyField_NOS3VOY7HKW0\",\"name\":\"金额（元）\",\"value\":\"150\"},{\"componentType\":\"DDSelectField\",\"extValue\":\"{\\\"label\\\":\\\"项目\\\",\\\"key\\\":\\\"option_0\\\"}\",\"id\":\"DDSelectField_EMZYMKI17MDC\",\"name\":\"是否是项目用车申请\",\"value\":\"项目\"}]";
        List<OapiProcessinstanceGetResponse.FormComponentValueVo> formComponentList = JsonUtils.toObj(formStr, new TypeReference<List<OapiProcessinstanceGetResponse.FormComponentValueVo>>() {
        });
        for (OapiProcessinstanceGetResponse.FormComponentValueVo formComponent : formComponentList) {
            switch (formComponent.getName()) {
                case DingTalkConstant.car.KEY_TRIP_COUNT:
                    if (!ObjectUtils.isEmpty(UseCarApplyRules)) {
                        UseCarApplyRules.stream().forEach(r -> {
                            if ("times_limit_flag".equals(r.getType()) && Integer.valueOf(0).equals(r.getValue())) {
                                r.setValue(2);
                            }
                        });
                        UseCarApplyRules.add(DingtalkCarApplyProcessInfo.UseCarApplyRule.builder().type("times_limit").value(formComponent.getValue()).build());
                    }
                    break;
                case DingTalkConstant.car.KEY_TRIP_FEE:
                    tripListBean.setEstimatedAmount(com.fenbeitong.finhub.common.utils.NumericUtils.obj2int(formComponent.getValue()));
                    break;
                default:
                    break;
            }
        }

        int estimatedAmount = tripListBean.getEstimatedAmount();
        boolean limitPrice = estimatedAmount != 0;
        UseCarApplyRules.add(DingtalkCarApplyProcessInfo.UseCarApplyRule.builder().type("price_limit_flag").value(limitPrice ? 2 : 0).build());
        if (limitPrice) {
            UseCarApplyRules.add(DingtalkCarApplyProcessInfo.UseCarApplyRule.builder().type("total_price").value(estimatedAmount).build());
        }
        System.out.println(UseCarApplyRules);
    }

    public static void main(String[] args) {

        List<DingtalkCarApplyProcessInfo.UseCarApplyRule> UseCarApplyRules = Lists.newArrayList(
            DingtalkCarApplyProcessInfo.UseCarApplyRule.builder().type("taxi_scheduling_fee").value(-1).build(),
            DingtalkCarApplyProcessInfo.UseCarApplyRule.builder().type("allow_same_city").value(false).build(),
            DingtalkCarApplyProcessInfo.UseCarApplyRule.builder().type("allow_called_for_other").value(true).build(),
            DingtalkCarApplyProcessInfo.UseCarApplyRule.builder().type("price_limit").value(-1).build(),
            DingtalkCarApplyProcessInfo.UseCarApplyRule.builder().type("day_price_limit").value(-1).build(),
            DingtalkCarApplyProcessInfo.UseCarApplyRule.builder().type("times_limit_flag").value(0).build()
        );


//        List<DingtalkCarApplyProcessInfo.UseCarApplyRule> useCarApplyRules = new ArrayList<>();
        DingtalkTripApplyProcessInfo.TripListBean tripListBean = new DingtalkTripApplyProcessInfo.TripListBean();
        String formStr = "[{\"componentType\":\"TextareaField\",\"id\":\"TextareaField_IOUGFF5VEY80\",\"name\":\"申请事由\",\"value\":\"受试者随访\"},{\"componentType\":\"TableField\",\"extValue\":\"{\\\"statValue\\\":[],\\\"componentName\\\":\\\"TableField\\\"}\",\"id\":\"TableField_MKFGUYL33R40\",\"name\":\"用车城市\",\"value\":\"[{\\\"rowValue\\\":[{\\\"componentType\\\":\\\"TextNote\\\",\\\"label\\\":\\\"说明\\\",\\\"value\\\":\\\"用车城市个数不可超过10个\\\",\\\"key\\\":\\\"TextNote_NL74BG13DUO0\\\"},{\\\"componentType\\\":\\\"AddressField\\\",\\\"label\\\":\\\"城市\\\",\\\"extendValue\\\":{\\\"province\\\":{\\\"name\\\":\\\"海南省\\\",\\\"id\\\":\\\"460000\\\"},\\\"city\\\":{\\\"name\\\":\\\"海口市\\\",\\\"id\\\":\\\"460100\\\"},\\\"district\\\":{\\\"name\\\":\\\"龙华区\\\",\\\"id\\\":\\\"460106\\\"}},\\\"value\\\":\\\"海南省,海口市,龙华区\\\",\\\"key\\\":\\\"AddressField_L8ZBBYSO0TC0\\\"}],\\\"rowNumber\\\":\\\"TableField_MKFGUYL33R40_1OFFQ3PEWLPC0\\\"},{\\\"rowValue\\\":[{\\\"componentType\\\":\\\"TextNote\\\",\\\"label\\\":\\\"说明\\\",\\\"value\\\":\\\"用车城市个数不可超过10个\\\",\\\"key\\\":\\\"TextNote_NL74BG13DUO0\\\"},{\\\"componentType\\\":\\\"AddressField\\\",\\\"label\\\":\\\"城市\\\",\\\"extendValue\\\":{\\\"province\\\":{\\\"name\\\":\\\"海南省\\\",\\\"id\\\":\\\"460000\\\"},\\\"city\\\":{\\\"name\\\":\\\"三亚市\\\",\\\"id\\\":\\\"460200\\\"},\\\"district\\\":{\\\"name\\\":\\\"天涯区\\\",\\\"id\\\":\\\"460204\\\"}},\\\"value\\\":\\\"海南省,三亚市,天涯区\\\",\\\"key\\\":\\\"AddressField_L8ZBBYSO0TC0\\\"}],\\\"rowNumber\\\":\\\"TableField_MKFGUYL33R40_514EFQBA9FG0\\\"}]\"},{\"componentType\":\"DDDateRangeField\",\"id\":\"DDDateRangeField_1K8C58TRD7Y8\",\"name\":\"[\\\"开始时间\\\",\\\"结束时间\\\"]\",\"value\":\"[\\\"2022-02-25\\\",\\\"2022-02-26\\\",null]\"},{\"componentType\":\"DDSelectField\",\"extValue\":\"{\\\"label\\\":\\\"项目\\\",\\\"key\\\":\\\"option_0\\\"}\",\"id\":\"DDSelectField_EMZYMKI17MDC\",\"name\":\"是否是项目用车申请\",\"value\":\"项目\"}]";
//        String formStr="[{\"componentType\":\"TextareaField\",\"id\":\"TextareaField_IOUGFF5VEY80\",\"name\":\"申请事由\",\"value\":\"受试者随访\"},{\"componentType\":\"TableField\",\"extValue\":\"{\\\"statValue\\\":[],\\\"componentName\\\":\\\"TableField\\\"}\",\"id\":\"TableField_MKFGUYL33R40\",\"name\":\"用车城市\",\"value\":\"[{\\\"rowValue\\\":[{\\\"componentType\\\":\\\"TextNote\\\",\\\"label\\\":\\\"说明\\\",\\\"value\\\":\\\"用车城市个数不可超过10个\\\",\\\"key\\\":\\\"TextNote_NL74BG13DUO0\\\"},{\\\"componentType\\\":\\\"AddressField\\\",\\\"label\\\":\\\"城市\\\",\\\"extendValue\\\":{\\\"province\\\":{\\\"name\\\":\\\"海南省\\\",\\\"id\\\":\\\"460000\\\"},\\\"city\\\":{\\\"name\\\":\\\"海口市\\\",\\\"id\\\":\\\"460100\\\"},\\\"district\\\":{\\\"name\\\":\\\"龙华区\\\",\\\"id\\\":\\\"460106\\\"}},\\\"value\\\":\\\"海南省,海口市,龙华区\\\",\\\"key\\\":\\\"AddressField_L8ZBBYSO0TC0\\\"}],\\\"rowNumber\\\":\\\"TableField_MKFGUYL33R40_1OFFQ3PEWLPC0\\\"},{\\\"rowValue\\\":[{\\\"componentType\\\":\\\"TextNote\\\",\\\"label\\\":\\\"说明\\\",\\\"value\\\":\\\"用车城市个数不可超过10个\\\",\\\"key\\\":\\\"TextNote_NL74BG13DUO0\\\"},{\\\"componentType\\\":\\\"AddressField\\\",\\\"label\\\":\\\"城市\\\",\\\"extendValue\\\":{\\\"province\\\":{\\\"name\\\":\\\"海南省\\\",\\\"id\\\":\\\"460000\\\"},\\\"city\\\":{\\\"name\\\":\\\"三亚市\\\",\\\"id\\\":\\\"460200\\\"},\\\"district\\\":{\\\"name\\\":\\\"天涯区\\\",\\\"id\\\":\\\"460204\\\"}},\\\"value\\\":\\\"海南省,三亚市,天涯区\\\",\\\"key\\\":\\\"AddressField_L8ZBBYSO0TC0\\\"}],\\\"rowNumber\\\":\\\"TableField_MKFGUYL33R40_514EFQBA9FG0\\\"}]\"},{\"componentType\":\"DDDateRangeField\",\"id\":\"DDDateRangeField_1K8C58TRD7Y8\",\"name\":\"[\\\"开始时间\\\",\\\"结束时间\\\"]\",\"value\":\"[\\\"2022-02-25\\\",\\\"2022-02-26\\\",null]\"},{\"componentType\":\"NumberField\",\"id\":\"NumberField_1Z4WNFE0689S0\",\"name\":\"用车次数\",\"value\":\"4\"},{\"componentType\":\"MoneyField\",\"id\":\"MoneyField_1HWHV3FP13C00\",\"name\":\"用车费用\",\"value\":\"150\"},{\"componentType\":\"MoneyField\",\"extValue\":\"{\\\"upper\\\":\\\"壹佰伍拾元整\\\",\\\"componentName\\\":\\\"MoneyField\\\"}\",\"id\":\"MoneyField_NOS3VOY7HKW0\",\"name\":\"金额（元）\",\"value\":\"150\"},{\"componentType\":\"DDSelectField\",\"extValue\":\"{\\\"label\\\":\\\"项目\\\",\\\"key\\\":\\\"option_0\\\"}\",\"id\":\"DDSelectField_EMZYMKI17MDC\",\"name\":\"是否是项目用车申请\",\"value\":\"项目\"}]";
        List<OapiProcessinstanceGetResponse.FormComponentValueVo> formComponentList = JsonUtils.toObj(formStr, new TypeReference<List<OapiProcessinstanceGetResponse.FormComponentValueVo>>() {
        });
        for (OapiProcessinstanceGetResponse.FormComponentValueVo formComponent : formComponentList) {
            switch (formComponent.getName()) {
                case DingTalkConstant.car.KEY_TRIP_COUNT:
                    if (!ObjectUtils.isEmpty(UseCarApplyRules)) {
                        UseCarApplyRules.stream().forEach(r -> {
                            if ("times_limit_flag".equals(r.getType()) && Integer.valueOf(0).equals(r.getValue())) {
                                r.setValue(2);
                            }
                        });
                        UseCarApplyRules.add(DingtalkCarApplyProcessInfo.UseCarApplyRule.builder().type("times_limit").value(formComponent.getValue()).build());
                    }
                    break;
                case DingTalkConstant.car.KEY_TRIP_FEE:
                    tripListBean.setEstimatedAmount(com.fenbeitong.finhub.common.utils.NumericUtils.obj2int(formComponent.getValue()));
                    break;
                default:
                    break;
            }
        }

        int estimatedAmount = tripListBean.getEstimatedAmount();
        boolean limitPrice = estimatedAmount != 0;
        UseCarApplyRules.add(DingtalkCarApplyProcessInfo.UseCarApplyRule.builder().type("price_limit_flag").value(limitPrice ? 2 : 0).build());
        if (limitPrice) {
            UseCarApplyRules.add(DingtalkCarApplyProcessInfo.UseCarApplyRule.builder().type("total_price").value(estimatedAmount).build());
        }
        System.out.println(UseCarApplyRules);
    }

    /**
     * 科脉自定义申请单 审批结束后回调流程
     */
    @Test
    public void keMaiDefApply() {
        String record = "{\"type\":\"approval_on_process_completed\",\"msg\":{\"applyOrderId\":\"6200e80741d40d59a368c7df\",\"companyId\":\"5d1b1d2f23445f4dca76304b\",\"category\":\"beforehand_apply_form\",\"applyOrderType\":16,\"applyType\":24,\"processDefId\":\"62009492e3d4ac3f707ab156\",\"processInstanceId\":\"6200e80841d40d59a368c7e0\",\"processInstanceStatus\":\"4\",\"starterId\":\"5fb4de7327f65f4e0265c22e\",\"starterName\":\"阮景卿\",\"processStartTime\":1644226568205,\"processUpdateTime\":1644226619566,\"processEndTime\":1644226619566}}";
        String type = StringUtils.obj2str(MapUtils.getValueByExpress(JsonUtils.toObj(StringUtils.obj2str(record), Map.class), "type"));
        String msg = StringUtils.obj2str(JsonUtils.toJson(MapUtils.getValueByExpress(JsonUtils.toObj(StringUtils.obj2str(record), Map.class), "msg")));
        WebHookOrderEvent event = JSON.parseObject(msg, WebHookOrderEvent.class);
        if (type.equals("approval_on_task_completed")) {
            event.setViewType(1);
        }
        event.setNodeStatus(event.getTaskStatus());
        event.setType(type);
        eventBusCenter.postSync(event);
    }

    @Test
    public void sassPushRevokeEventTest() {
        String value = "{\n" +
            "\t\"title\": \"用餐\",\n" +
            "\t\"content\": \"用餐通知\",\n" +
            "\t\"desc\": \"用餐通知\",\n" +
            "\t\"alert\": true,\n" +
            "\t\"msgType\": \"create\",\n" +
            "\t\"msg\": \"{\\\"myself\\\":true,\\\"setting_type\\\":\\\"5\\\",\\\"view_type\\\":\\\"1\\\",\\\"id\\\":\\\"623979126315463eb24d5c9d\\\",\\\"apply_type\\\":5}\",\n" +
            "\t\"userId\": \"5ba1f0c423445f7a42a2d11f\",\n" +
            "\t\"companyId\": \"5d1b1d2f23445f4dca76304b\",\n" +
            "\t\"applyId\": \"623979126315463eb24d5c9d\",\n" +
            "\t\"settingType\": 5\n" +
            "}";

        ApplyPushEvents applyPushEvents = KafkaConsumerUtils.invokeIMessage(value, ApplyPushEvents.class);
        applyPushEvents.setEventMsg(JsonUtils.toObj(applyPushEvents.getMsg(), ApplyPushEvents.Msg.class));
        eventBusCenter.postSync(applyPushEvents);

    }

}
