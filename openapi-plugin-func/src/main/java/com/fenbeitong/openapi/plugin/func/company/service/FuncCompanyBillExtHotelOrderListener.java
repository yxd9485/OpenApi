package com.fenbeitong.openapi.plugin.func.company.service;

import com.fenbeitong.openapi.plugin.etl.service.impl.DefaultEtlListener;
import com.fenbeitong.openapi.plugin.support.employee.service.BaseEmployeeRefServiceImpl;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>Title: FuncCompanyBillExtCarOrderListener</p>
 * <p>Description: 用车订单扩展字段监听</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/8 5:00 PM
 */
@ServiceAspect
@Service
public class FuncCompanyBillExtHotelOrderListener extends DefaultEtlListener {

    @Autowired
    private BaseEmployeeRefServiceImpl employeeRefService;

    @SuppressWarnings("unchecked")
    @Override
    public void afterTransform(Map<String, Object> srcMap, Map transformMap) {
        if (ObjectUtils.isEmpty(transformMap)) {
            return;
        }
        Map hotelMap = (Map) transformMap.get("hotel");
        List<Map> guestList = (List<Map>) hotelMap.get("guestInfo");
        List<Map> liveWith = (List<Map>) hotelMap.get("liveWith");
        if (!ObjectUtils.isEmpty(guestList)) {
            List<String> userPhones = guestList.stream().map(guest -> (String) guest.get("userPhone")).filter(phone -> !ObjectUtils.isEmpty(phone)).collect(Collectors.toList());
            if (!ObjectUtils.isEmpty(userPhones)) {
                hotelMap.put("userPhone", String.join(",", userPhones));
            }
        }
        if (!ObjectUtils.isEmpty(liveWith)) {
            List<String> liveWithUserPhones = liveWith.stream().map(guest -> (String) guest.get("liveWithUserPhone")).filter(phone -> !ObjectUtils.isEmpty(phone)).collect(Collectors.toList());
            if (!ObjectUtils.isEmpty(liveWithUserPhones)) {
                hotelMap.put("liveWithUserPhone", String.join(",", liveWithUserPhones));
            }
        }
//        List<String> userIdList = Lists.newArrayList();
//        List<String> guestIdList = null;
//        if (!ObjectUtils.isEmpty(guestList)) {
//            guestIdList = guestList.stream().filter(g -> (boolean) g.get("employee")).map(g -> (String) g.get("userId")).collect(Collectors.toList());
//            userIdList.addAll(guestIdList);
//        }
//        List<String> liveWithIdList = null;
//        if (!ObjectUtils.isEmpty(liveWith)) {
//            liveWithIdList = liveWith.stream().filter(g -> (boolean) g.get("employee")).map(g -> (String) g.get("userId")).collect(Collectors.toList());
//            userIdList.addAll(liveWithIdList);
//        }
//        if (!ObjectUtils.isEmpty(userIdList)) {
//            String companyId = (String) hotelMap.get("companyId");
//            List<EmployeeContract> employeeList = employeeRefService.getEmployeeExtService().queryEmployeeListInfo(userIdList, companyId);
//            if (!ObjectUtils.isEmpty(employeeList)) {
//                Map<String, EmployeeContract> employeeMap = employeeList.stream().collect(Collectors.toMap(EmployeeContract::getEmployee_id, Function.identity()));
//                List<EmployeeContract> guestEmployeeList = guestIdList == null ? null : guestIdList.stream().map(employeeMap::get).filter(Objects::nonNull).collect(Collectors.toList());
//                List<EmployeeContract> liveWithEmployeeList = liveWithIdList == null ? null : liveWithIdList.stream().map(employeeMap::get).filter(Objects::nonNull).collect(Collectors.toList());
//                String userId = ObjectUtils.isEmpty(guestEmployeeList) ? null : guestEmployeeList.stream().map(EmployeeContract::getEmployee_id).collect(Collectors.joining(","));
//                String userDeptId = ObjectUtils.isEmpty(guestEmployeeList) ? null : guestEmployeeList.stream().map(EmployeeContract::getOrg_id).collect(Collectors.joining(","));
//                String liveWithUserId = ObjectUtils.isEmpty(liveWithEmployeeList) ? null : liveWithEmployeeList.stream().map(EmployeeContract::getEmployee_id).collect(Collectors.joining(","));
//                String liveWithDeptId = ObjectUtils.isEmpty(liveWithEmployeeList) ? null : liveWithEmployeeList.stream().map(EmployeeContract::getOrg_id).collect(Collectors.joining(","));
//                hotelMap.put("userId", userId);
//                hotelMap.put("userDeptId", userDeptId);
//                hotelMap.put("liveWithUserId", liveWithUserId);
//                hotelMap.put("liveWithDeptId", liveWithDeptId);
//            }
//        }
        List<Map> applyInfo = (List<Map>) hotelMap.get("applyInfo");
        if (!ObjectUtils.isEmpty(applyInfo)) {
            hotelMap.put("applyId", applyInfo.get(0).get("apply_id"));
        }
        List<Map> orderApplyInfo = (List<Map>) hotelMap.get("orderApplyInfo");
        if (!ObjectUtils.isEmpty(orderApplyInfo)) {
            hotelMap.put("orderApplyId", orderApplyInfo.get(0).get("apply_id"));
        }
    }
}
