package com.fenbeitong.openapi.plugin.zhongxin.isv.service.citic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.zhongxin.isv.dto.citic.ZhongxinUserAddInfoReqDTO;
import com.fenbeitong.openapi.plugin.zhongxin.isv.dto.citic.ZhongxinUserAddReqDTO;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.fenbeitong.openapi.plugin.zhongxin.common.constant.ZhongxinConstant;
import com.fenbeitong.openapi.plugin.zhongxin.common.exception.OpenApiZhongxinException;
import com.fenbeitong.openapi.plugin.zhongxin.isv.dto.citic.ZhongxinUserAddRespDTO;
import com.fenbeitong.openapi.plugin.zhongxin.isv.dto.citic.ZhongxinUserAddInfoRespDTO;
import com.fenbeitong.openapi.plugin.zhongxin.isv.util.ZhongxinResponseCode;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:  ZhongxinUserAddService</p>
 * <p>Description: 中信用户新增</p>
 * <p>Company: 中信银行 </p>
 *
 * @author: haoqiang.wang
 * @Date: 2021/4/19 下午7:01
 **/
@ServiceAspect
@Service
@Slf4j
public class ZhongxinUserAddService extends AbstractZhongxinService {


    /**
     * 中信银行新增员工
     * @param employee
     * @return
     */
    public ZhongxinUserAddRespDTO userAdd(EmployeeContract employee, String corpId){
        log.info("web后台新增的员工证件类型为：{}", employee.getId_type());
        //1.请求参数组装
        ZhongxinUserAddReqDTO userAddReqDTO = new ZhongxinUserAddReqDTO();
        List<ZhongxinUserAddInfoReqDTO> userAddInfoReqDTOList = new ArrayList<>();
        ZhongxinUserAddInfoReqDTO userAddInfoReqDTO = new ZhongxinUserAddInfoReqDTO();
        userAddInfoReqDTO.setEMPCODE(employee.getEmployee_id());
        userAddInfoReqDTO.setMOBNO(employee.getPhone_num());
        userAddInfoReqDTO.setNAME(employee.getName());
        userAddInfoReqDTO.setIDTYPE("101");
        userAddInfoReqDTO.setIDNO(employee.getId_number());
        userAddInfoReqDTOList.add(userAddInfoReqDTO);

        userAddReqDTO.setINNPRTCNO(corpId);
        userAddReqDTO.setEMPLIST(userAddInfoReqDTOList);
        initRequestBody(userAddReqDTO);
        String businessJsonStr = JSON.toJSONString(userAddReqDTO);

        //2.请求中信
        String decryptBusiness = commonHandler(businessJsonStr);
        ZhongxinUserAddRespDTO userAddRespDTO = JSONObject.parseObject(decryptBusiness, ZhongxinUserAddRespDTO.class);
        return userAddRespDTO;
    }

    /**
     * 获取中信银行员工唯一标识
     * @param userAddRespDTO
     * @return
     */
    public String getEmployeeId(ZhongxinUserAddRespDTO userAddRespDTO){
        //通讯成功
        if(ZhongxinConstant.SUCCESS.equals(userAddRespDTO.getRETCODE())){
            List<ZhongxinUserAddInfoRespDTO> listDTOS = userAddRespDTO.getRESULTLIST();
            //目前只支持单笔
            ZhongxinUserAddInfoRespDTO userRespDTO = listDTOS.get(0);
            if(ZhongxinConstant.SUCCESS.equals(userRespDTO.getRETCODE())){
                //表示员工新增成功
                return userRespDTO.getHASH();
            }else if("KXY2006".equals(userRespDTO.getRETCODE())){
                //员工已存在特殊处理
                return userRespDTO.getHASH();
            }
        }
        throw new OpenApiZhongxinException(ZhongxinResponseCode.EMPLOYEE_ADD_FAILED, "新增员工失败");
    }


    /**
     * 中信银行具体接口码
     * @return
     */
    public String getTransCode(){
        return ZhongxinConstant.CITI_USER_ADD_TRANS_CODE;
    }
}
