package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResultEntity;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.isv.constant.CompanyAuthState;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvComponentService;
import com.fenbeitong.openapi.plugin.support.apply.dto.BaseDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.UserApproveCCListReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.UserApproveListReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.IOpenApplyService;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 小组件服务
 *
 * @author lizhen
 * @date 2021/3/16
 */
@ServiceAspect
@Service
@Slf4j
public class DingtalkIsvComponentServiceImpl implements IDingtalkIsvComponentService {

    @Autowired
    private IDingtalkIsvCompanyDefinitionService dingtalkIsvCompanyDefinitionService;

    @Autowired
    private OpenEmployeeServiceImpl openEmployeeService;

    @Autowired
    private IOpenApplyService openApplyService;

    @Value("${host.usercenter}")
    private String ucHost;

    @Value("${host.appgate}")
    private String appgateHost;

    @Override
    public String getUserToken(String corpId, String userId) {
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCorpId(corpId);
        if (dingtalkIsvCompany == null) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_COMPANY_UNDEFINED);
        }
        String companyId = dingtalkIsvCompany.getCompanyId();
        String token = openEmployeeService.getEmployeeFbToken(companyId, userId, "1");
        if (StringUtils.isBlank(token)) {
            log.info("钉钉小组件获取token失败");
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_USER_NOT_EXISTS);
        }
        return token;
    }

    @Override
    public Object getApprovalList(HttpServletRequest request) {
        String token = getToken(request);
        UserApproveListReqDTO req = new UserApproveListReqDTO();
        req.setType(NumericUtils.obj2int(request.getParameter("type")));
        req.setPage(NumericUtils.obj2int(request.getParameter("page")));
        req.setPageSize(NumericUtils.obj2int(request.getParameter("page_size")));
        req.setApplyType(NumericUtils.obj2int(request.getParameter("apply_type")));
        req.setClientVersion(request.getParameter("client_version"));
        req.setName(request.getParameter("name"));
        Object userApproveList = openApplyService.getUserApproveList(token, req);
        return userApproveList;
    }

    @Override
    public Object getApprovalCCList(HttpServletRequest request) {
        String token = getToken(request);
        UserApproveCCListReqDTO req = new UserApproveCCListReqDTO();
        req.setPage(NumericUtils.obj2int(request.getParameter("page")));
        req.setPageSize(NumericUtils.obj2int(request.getParameter("page_size")));
        req.setApplyType(NumericUtils.obj2int(request.getParameter("apply_type")));
        req.setClientVersion(request.getParameter("client_version"));
        req.setName(request.getParameter("name"));
        Object userApproveList = openApplyService.getUserApproveCCList(token, req);
        return userApproveList;
    }

    /**
     * 获取日程
     * @param request
     * @return
     */
    @Override
    public Object getScheduleList(HttpServletRequest request) {
        String token = getToken(request);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", token);
        String result = RestHttpUtils.get(ucHost + "/uc/attention/trip/schedule_list_v2", httpHeaders, null);
        BaseDTO baseResult = JsonUtils.toObj(result, BaseDTO.class);
        if (baseResult == null || !baseResult.success()) {
            String msg = baseResult == null ? "" : Optional.ofNullable(baseResult.getMsg()).orElse("");
            throw new OpenApiPluginSupportException((DingtalkResponseCode.GET_SCHEDULE_LIST_FAILED), ":" + msg);
        }
        return baseResult.getData();
    }

    /**
     * 获取日程
     * @param request
     * @return
     */
    @Override
    public Object getMyConsume(HttpServletRequest request) {
        String token = getToken(request);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", token);
        String result = RestHttpUtils.get(appgateHost + "/api/appgate/my/consume", httpHeaders, null);
        BaseDTO baseResult = JsonUtils.toObj(result, BaseDTO.class);
        if (baseResult == null || !baseResult.success()) {
            String msg = baseResult == null ? "" : Optional.ofNullable(baseResult.getMsg()).orElse("");
            throw new OpenApiPluginSupportException((DingtalkResponseCode.GET_CONSUME_FAILED), ":" + msg);
        }
        return baseResult.getData();
    }

    @Override
    public Object checkMaincorpIdIsExsit(HttpServletRequest request, String corpId) {
        log.info("查询企业是否授权 checkMaincorpIdIsExsit corpId={}" , corpId);
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCorpId(corpId);
        Map<String,Object> map = new HashMap<String,Object>();
        if (dingtalkIsvCompany == null) {
            //企业未授权
            map.put("exsit" , false);
            return map;
        }
        Integer state = dingtalkIsvCompany.getState();
        if(CompanyAuthState.AUTH_SUCCESS.getCode() == state){
            map.put("exsit" , true);
            return map;
        }
        map.put("exsit" , false);
        return map;
    }

    @Override
    public Object dashboardData(HttpServletRequest request, String corpId) {
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCorpId(corpId);
        if (dingtalkIsvCompany == null) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_COMPANY_UNDEFINED);
        }
        Map<String ,Object> loginAuthMap = getLoginAuth(request);
        Integer companyDashboardView =  NumericUtils.obj2int(loginAuthMap.get("companyDashboardView"));
        Integer companySavingMoneyAnalysis = NumericUtils.obj2int(loginAuthMap.get("companySavingMoneyAnalysis"));
        String token = StringUtils.obj2str(loginAuthMap.get("token"));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", token);
        String companyId = dingtalkIsvCompany.getCompanyId();
        Map<String ,Object> retMap = new HashMap<>();
        if( companyDashboardView == 1 ){
            DingtalkResultEntity dingtalkResultEntity = companyMonthlyConsumeSum(httpHeaders, companyId);
            //企业报表权限
            retMap.put("totalConsume" , dingtalkResultEntity.getData());
            //当月票数
            retMap.put("orderCount" , ordermonthly(httpHeaders, companyId));
            //企业消费数据
            retMap.put("orgConsumeList" , companyConsume(httpHeaders, companyId));
        }
        if(companySavingMoneyAnalysis == 1){
            //本月商务节省数据
            retMap.put("totalSaving" , besmartsavatotal(httpHeaders, companyId));
        }
        if(loginAuthMap.containsKey("token")) loginAuthMap.remove("token");
        retMap.put("authInfo" , loginAuthMap);
        return DingtalkResponseUtils.success(retMap);
    }

    //本月消费总合
    private DingtalkResultEntity companyMonthlyConsumeSum(HttpHeaders httpHeaders , String companyId){
        String interfaceUrl = "/besmart/v1/dashboard/company/%s/monthly/%s/%s/consume_stats/sum/";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startMonth = getMonthFirstDay();// 本月第一天(yyyy-MM-dd)
        String endMonth = sdf.format( new Date() ); //当前日期(yyyy-MM-dd)
        String url = String.format(interfaceUrl, companyId, startMonth, endMonth);
        String result = RestHttpUtils.get(appgateHost + url, httpHeaders, null);

        BaseDTO<Map> baseResult = JsonUtils.toObj(result, BaseDTO.class);
        if(baseResult == null || baseResult.getCode() == 403){
            //权限不足
            String msg = baseResult == null ? "" : Optional.ofNullable(baseResult.getMsg()).orElse("");
            return DingtalkResponseUtils.error(baseResult.getCode() , msg);
        }

        if (baseResult == null || !baseResult.success()) {
            String msg = baseResult == null ? "" : Optional.ofNullable(baseResult.getMsg()).orElse("");
            throw new OpenApiPluginSupportException((DingtalkResponseCode.GET_CONSUME_FAILED), ":" + msg);
        }
        Map map = baseResult.getData();
        return DingtalkResponseUtils.success( map.get("total_consume") );
    }

    //本月商务节省数据
    private Object besmartsavatotal(HttpHeaders httpHeaders , String companyId){
        Map<String,Object> param = new HashMap<>();
        param.put("companyId" , companyId );
        param.put("type" ,1);
        param.put("id" , companyId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        param.put("dateTime" , sdf.format( new Date() ));//本月日期 日期格式年月（yyyyMM）
        String result = RestHttpUtils.get(appgateHost + "/besmart/v1/dashboard/statement/save/total", httpHeaders, param);
        Map map = handleResult(result);
        return map.get("totalSaving");
    }

    //当月票数
    private Object ordermonthly(HttpHeaders httpHeaders,String companyId){
        Map<String,Object> param = new HashMap<>();
        param.put("companyId" , companyId );
        String result = RestHttpUtils.get(appgateHost + "/besmart/v1/dashboard/company_data/order/monthly", httpHeaders, param);
        Map map = handleResult(result);
        return map.get("orderCount");
    }

    //企业消费数据
    private Object companyConsume(HttpHeaders httpHeaders,String companyId){
        Map<String,Object> param = new HashMap<>();
        param.put("companyId" , companyId );
        param.put("pageSize" , 5 );
        param.put("pageIndex" , 1 );
        String result = RestHttpUtils.get(appgateHost + "/besmart/v1/dashboard/company_data/org/consume", httpHeaders, param);
        Map map = handleResult(result);
        return map.get("orgConsumeList");
    }

    private Map handleResult(String result){
        BaseDTO<Map> baseResult = JsonUtils.toObj(result, BaseDTO.class);
        if (baseResult == null || !baseResult.success()) {
            String msg = baseResult == null ? "" : Optional.ofNullable(baseResult.getMsg()).orElse("");
            throw new OpenApiPluginSupportException((DingtalkResponseCode.GET_CONSUME_FAILED), ":" + msg);
        }
        return baseResult.getData();
    }


    public String getMonthFirstDay(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.add(Calendar.MONTH,0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(calendar.getTime());
    }

    //获取登陆信息
    private Map<String ,Object> getLoginAuth(HttpServletRequest request) {
        String userId = request.getParameter("userid");
        String corpId = request.getParameter("corpId");
        log.info("获取token  userId={},corpId={}", userId , corpId);
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCorpId(corpId);
        if (dingtalkIsvCompany == null) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_COMPANY_UNDEFINED);
        }
        String companyId = dingtalkIsvCompany.getCompanyId();
        LoginResVO loginResVO = openEmployeeService.loginAuthInit(companyId, userId, "1");
        if(loginResVO == null) throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_USER_NOT_EXISTS);
        Integer companyDashboardView =  (loginResVO.getAuth_info()!=null && loginResVO.getAuth_info().getCompany_dashboard_view()!=null) ?
                                         loginResVO.getAuth_info().getCompany_dashboard_view() : 0 ;
        Integer companySavingMoneyAnalysis = (loginResVO.getAuth_info() != null && loginResVO.getAuth_info().getCompany_saving_money_analysis()!=null ) ?
                                              loginResVO.getAuth_info().getCompany_saving_money_analysis() : 0 ;
        Map<String ,Object> authMap = new HashMap<>();
        authMap.put("companyDashboardView" , companyDashboardView);
        authMap.put("companySavingMoneyAnalysis" , companySavingMoneyAnalysis);
        String token = loginResVO.getLogin_info() != null ? loginResVO.getLogin_info().getToken() : "";
        if (StringUtils.isBlank(token)) {
            log.info("钉钉小组件获取token失败");
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_USER_NOT_EXISTS);
        }
        authMap.put("token" , token);
        return authMap;
    }

    private String getToken(HttpServletRequest request) {
        String userId = request.getParameter("userid");
        String corpId = request.getParameter("corpId");
        log.info("获取token  userId={},corpId={}", userId , corpId);
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCorpId(corpId);
        if (dingtalkIsvCompany == null) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_COMPANY_UNDEFINED);
        }
        String companyId = dingtalkIsvCompany.getCompanyId();
        String token = openEmployeeService.getEmployeeFbToken(companyId, userId, "1");
        if (StringUtils.isBlank(token)) {
            log.info("钉钉小组件获取token失败");
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_USER_NOT_EXISTS);
        }
        return token;
    }


}
