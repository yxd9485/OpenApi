package com.fenbeitong.openapi.plugin.dingtalk.isv.controller;

import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkApproveKitResponseUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.util.DingtalkComponentSignUtil;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dto.IFormFieldDTO;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvApproveKitService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvComponentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lizhen on 2021/3/16.
 */
@RestController
@Slf4j
@RequestMapping("/dingtalk/isv/component")
public class DingtalkIsvComponentController {

    @Autowired
    private DingtalkComponentSignUtil dingtalkComponentSignUtil;

    @Autowired
    private IDingtalkIsvComponentService dingtalkIsvComponentService;

    @Autowired
    private IDingtalkIsvApproveKitService dingtalkIsvApproveKitService;

    @RequestMapping("/getUserToken")
    public Object getUserToken(HttpServletRequest request, HttpServletResponse response, String userid, String corpId) {
        dingtalkComponentSignUtil.checkSign(request);
        String userToken = dingtalkIsvComponentService.getUserToken(corpId, userid);
        return DingtalkResponseUtils.success(userToken);
    }

    @RequestMapping("/getCorpId")
    public Object getCorpId(HttpServletRequest request, HttpServletResponse response, String userid, String corpId) {
        Map<String, Object> result = new HashMap<>();
        result.put("corpId", corpId);
        return DingtalkResponseUtils.success(result);
    }

    @RequestMapping("/approve/getUserApprovalList")
    public Object getUserApprovalList(HttpServletRequest request, HttpServletResponse response, String userid, String corpId) {
        dingtalkComponentSignUtil.checkSign(request);
        Object approvalList = dingtalkIsvComponentService.getApprovalList(request);
        return DingtalkResponseUtils.success(approvalList);
    }

    @RequestMapping("/approve/getUserApprovalCCList")
    public Object getUserApprovalCCList(HttpServletRequest request, HttpServletResponse response, String userid, String corpId) {
        dingtalkComponentSignUtil.checkSign(request);
        Object approvalList = dingtalkIsvComponentService.getApprovalCCList(request);
        return DingtalkResponseUtils.success(approvalList);
    }

    @RequestMapping("/uc/getScheduleList")
    public Object getScheduleList(HttpServletRequest request, HttpServletResponse response, String userid, String corpId) {
        dingtalkComponentSignUtil.checkSign(request);
        Object approvalList = dingtalkIsvComponentService.getScheduleList(request);
        return DingtalkResponseUtils.success(approvalList);
    }


    @RequestMapping("/uc/getMyConsume")
    public Object getMyConsume(HttpServletRequest request, HttpServletResponse response, String userid, String corpId) {
        dingtalkComponentSignUtil.checkSign(request);
        Object approvalList = dingtalkIsvComponentService.getMyConsume(request);
        return DingtalkResponseUtils.success(approvalList);
    }

    /**
     * 查询企业是否开通授权
     * @param request
     * @param response
     * @param userid
     * @param corpId
     * @return
     */
    @RequestMapping("/uc/checkMaincorpId")
    public Object checkMaincorpIdIsExsit(HttpServletRequest request, HttpServletResponse response, String userid, String corpId) {
        dingtalkComponentSignUtil.checkSign(request);
        Object exist = dingtalkIsvComponentService.checkMaincorpIdIsExsit(request ,corpId);
        return DingtalkResponseUtils.success(exist);
    }

    /**
     * 查询企业看板数据
     * @param request
     * @param response
     * @param userid
     * @param corpId
     * @return
     */
    @RequestMapping("/uc/dashboard")
    public Object dashboard(HttpServletRequest request, HttpServletResponse response, String userid, String corpId) {
          dingtalkComponentSignUtil.checkSign(request);
          return dingtalkIsvComponentService.dashboardData(request ,corpId);
    }

    /**
     * 用车初始化接口
     * @param request
     * @param response
     * @param userid
     * @param corpId
     * @return
     */
    @RequestMapping("/car/initForm")
    public Object carInitForm(HttpServletRequest request, HttpServletResponse response , String userid, String corpId) {
        try {
            dingtalkComponentSignUtil.checkSign(request);
            List<IFormFieldDTO> carInitData = dingtalkIsvApproveKitService.getCarInitData(request);
            Map<String ,Object> map = new HashMap<>();
            map.put("dataList" , carInitData);
            return DingtalkApproveKitResponseUtils.success(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DingtalkApproveKitResponseUtils.error( "内部错误");
    }

    /**
     * 用车刷新接口
     * @param request
     * @param response
     * @param userid
     * @param corpId
     * @return
     */
    @RequestMapping("/car/refreshData")
    public Object carRefreshData(HttpServletRequest request, HttpServletResponse response , String userid, String corpId) {
        try {
            dingtalkComponentSignUtil.checkSign(request);
            List<IFormFieldDTO> refreshData = dingtalkIsvApproveKitService.getCarRefreshData(request);
            Map<String ,Object> map = new HashMap<>();
            map.put("dataList" , refreshData);
            return DingtalkApproveKitResponseUtils.success(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DingtalkApproveKitResponseUtils.error( "内部错误");
    }

    /**
     * 用车提交接口
     * @param request
     * @param response
     * @param userid
     * @param corpId
     * @return
     */
    @RequestMapping("/car/submitCheck")
    public Object carSubmit(HttpServletRequest request, HttpServletResponse response , String userid, String corpId) {
        try {
            dingtalkComponentSignUtil.checkSign(request);
            return dingtalkIsvApproveKitService.checkSubmitData(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DingtalkApproveKitResponseUtils.error( "内部错误");
    }

    /**
     * 差旅初始化接口
     * @param request
     * @param response
     * @param userid
     * @param corpId
     * @return
     */
    @RequestMapping("/trip/initForm")
    public Object tripInitForm(HttpServletRequest request, HttpServletResponse response , String userid, String corpId) {
        try {
            dingtalkComponentSignUtil.checkSign(request);
            List<IFormFieldDTO> carInitData = dingtalkIsvApproveKitService.getTripInitData(request);
            Map<String ,Object> map = new HashMap<>();
            map.put("dataList" , carInitData);
            return DingtalkApproveKitResponseUtils.success(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DingtalkApproveKitResponseUtils.error( "内部错误");
    }

    /**
     * 差旅初始化接口
     * @param request
     * @param response
     * @param userid
     * @param corpId
     * @return
     */
    @RequestMapping("/trip/refreshForm")
    public Object tripRefreshForm(HttpServletRequest request, HttpServletResponse response , String userid, String corpId) {
        try {
            dingtalkComponentSignUtil.checkSign(request);
            List<IFormFieldDTO> carInitData = dingtalkIsvApproveKitService.getTripRefreshData(request);
            Map<String ,Object> map = new HashMap<>();
            map.put("dataList" , carInitData);
            return DingtalkApproveKitResponseUtils.success(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DingtalkApproveKitResponseUtils.error( "内部错误");
    }

    /**
     * 差旅提交校验接口
     * @param request
     * @param response
     * @param userid
     * @param corpId
     * @return
     */
    @RequestMapping("/trip/submitCheck")
    public Object tripSubmit(HttpServletRequest request, HttpServletResponse response , String userid, String corpId) {
        try {
            dingtalkComponentSignUtil.checkSign(request);
            return dingtalkIsvApproveKitService.checkSubmitTripData(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DingtalkApproveKitResponseUtils.error( "内部错误");
    }


    /**
     * 用餐初始化接口
     * @param request
     * @param response
     * @param userid
     * @param corpId
     * @return
     */
    @RequestMapping("/dinnner/initForm")
    public Object dinnerInitForm(HttpServletRequest request, HttpServletResponse response , String userid, String corpId) {
        dingtalkComponentSignUtil.checkSign(request);
        List<IFormFieldDTO> dinnerInitData = dingtalkIsvApproveKitService.getDinnerInitData(request);
        Map<String ,Object> map = new HashMap<>();
        map.put("dataList" , dinnerInitData);
        return DingtalkApproveKitResponseUtils.success( map );
    }

    /**
     * 用餐初始化接口
     * @param request
     * @param response
     * @param userid
     * @param corpId
     * @return
     */
    @RequestMapping("/dinner/refreshForm")
    public Object dinnerRefreshForm(HttpServletRequest request, HttpServletResponse response , String userid, String corpId) {
        dingtalkComponentSignUtil.checkSign(request);
        List<IFormFieldDTO> dinnerRefreshData = dingtalkIsvApproveKitService.getDinnerRefreshData(request);
        Map<String ,Object> map = new HashMap<>();
        map.put("dataList" , dinnerRefreshData);
        return DingtalkApproveKitResponseUtils.success(map);
    }

    /**
     * 用餐提交校验接口
     * @param request
     * @param response
     * @param userid
     * @param corpId
     * @return
     */
    @RequestMapping("/dinner/submitCheck")
    public Object dinnerSubmit(HttpServletRequest request, HttpServletResponse response , String userid, String corpId) {
        try {
            dingtalkComponentSignUtil.checkSign(request);
            return dingtalkIsvApproveKitService.getDinnerSubmitData(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DingtalkApproveKitResponseUtils.error( "内部错误");
    }


    /**
     * 外卖初始化接口
     * @param request
     * @param response
     * @param userid
     * @param corpId
     * @return
     */
    @RequestMapping("/takeaway/initForm")
    public Object takeawayInitForm(HttpServletRequest request, HttpServletResponse response , String userid, String corpId) {
        dingtalkComponentSignUtil.checkSign(request);
        List<IFormFieldDTO> dinnerInitData = dingtalkIsvApproveKitService.getTakeawayInitData(request);
        Map<String ,Object> map = new HashMap<>();
        map.put("dataList" , dinnerInitData);
        return DingtalkApproveKitResponseUtils.success( map );
    }

    /**
     * 外卖初始化接口
     * @param request
     * @param response
     * @param userid
     * @param corpId
     * @return
     */
    @RequestMapping("/takeaway/refreshForm")
    public Object takeawayRefreshForm(HttpServletRequest request, HttpServletResponse response , String userid, String corpId) {
        dingtalkComponentSignUtil.checkSign(request);
        List<IFormFieldDTO> dinnerRefreshData = dingtalkIsvApproveKitService.getTakeawayRefreshData(request);
        Map<String ,Object> map = new HashMap<>();
        map.put("dataList" , dinnerRefreshData);
        return DingtalkApproveKitResponseUtils.success(map);
    }

    /**
     * 外卖提交校验接口
     * @param request
     * @param response
     * @param userid
     * @param corpId
     * @return
     */
    @RequestMapping("/takeaway/submitCheck")
    public Object takeawaySubmit(HttpServletRequest request, HttpServletResponse response , String userid, String corpId) {
        dingtalkComponentSignUtil.checkSign(request);
        return dingtalkIsvApproveKitService.getTakeawaySubmitData(request);
    }

}
