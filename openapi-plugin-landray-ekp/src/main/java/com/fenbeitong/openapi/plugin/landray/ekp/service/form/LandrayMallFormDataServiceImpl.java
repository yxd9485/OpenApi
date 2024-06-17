package com.fenbeitong.openapi.plugin.landray.ekp.service.form;

import cn.hutool.json.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.etl.entity.OpenEtlMappingConfig;
import com.fenbeitong.openapi.plugin.landray.ekp.service.LandrayFormDataBuildService;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.support.service.mall.MallInfoUtil;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 采购信息表单组装
 * @Auther zhang.peng
 * @Date 2021/8/5
 */
@ServiceAspect
@Service
public class LandrayMallFormDataServiceImpl extends LandrayFormDataBuildService {

    @Override
    public MultiValueMap<String,Object> buildFormDataInfo( FenbeitongApproveDto fenbeitongApproveDto , List<OpenEtlMappingConfig> openEtlMappingConfigList , String fbtApplyId) {
        String formValuesJson = "";
        MultiValueMap<String,Object> wholeForm = new LinkedMultiValueMap<>();
        if (CollectionUtils.isEmpty(openEtlMappingConfigList)){
            return wholeForm;
        }
        //采购
        Map<String,Object> formParams = new HashMap<>();
        Map<String,String> src2TgtMap = new HashMap<>();
        openEtlMappingConfigList.stream().forEach(etlMappingConfig -> {
            String srcData = etlMappingConfig.getSrcCol();
            String tgtData = etlMappingConfig.getTgtCol();
            src2TgtMap.put(srcData,tgtData);
            if ("third_dept_id".equals(srcData)){
                formParams.put(tgtData,fenbeitongApproveDto.getThirdDepartmentId());
            }
            if ("employee_id".equals(srcData)){
                formParams.put(tgtData,fenbeitongApproveDto.getThirdEmployeeId());
            }
            if ("apply_total_price".equals(srcData)){
                formParams.put(tgtData,MallInfoUtil.getMallAmount(fenbeitongApproveDto.getApplyTotalPrice()));
            }
            if ("apply_desc".equals(srcData)){
                formParams.put(tgtData, MallInfoUtil.getMallReason(fenbeitongApproveDto));
            }
            if ("apply_id".equals(srcData)){
                formParams.put(tgtData,fbtApplyId);
            }
            if ("phone".equals(srcData)){
                formParams.put(tgtData,fenbeitongApproveDto.getPhone());
            }
            if ("apply_id".equals(srcData)){
                formParams.put(tgtData,fbtApplyId);
            }
            if ("employee_id".equals(srcData)){
                formParams.put(tgtData,fenbeitongApproveDto.getEmployeeId());
            }
        });
        buildMallInfo(fenbeitongApproveDto,formParams,src2TgtMap);
        formValuesJson = JSONObject.toJSONString(formParams);
        String title = fenbeitongApproveDto.getApplyName() + "的分贝通采购审批单";
        wholeForm.add("docSubject", title );
        wholeForm.add("formValues", formValuesJson );
        wholeForm.add("docContent", fenbeitongApproveDto.getApplyDesc() );
        return wholeForm;
    }

    public static void buildMallInfo( FenbeitongApproveDto fenbeitongApproveDto , Map<String,Object> formParams , Map<String,String> src2TgtMap){
        List<FenbeitongApproveDto.Trip> tripList = fenbeitongApproveDto.getTripMallList();
        if ( CollectionUtils.isEmpty(tripList) ){
            return;
        }
        JSONArray jsonArray = new JSONArray();//定义一个json集合
        FenbeitongApproveDto.Trip trip = tripList.get(0);
        List<FenbeitongApproveDto.Mall> mallList = trip.getMallList();
        if ( CollectionUtils.isEmpty(mallList) ){
            return;
        }
        String goodsNameTgtName = src2TgtMap.get(GOODS_NAME);
        String goodsAmountTgtName = src2TgtMap.get(GOODS_COUNT);
        String goodsSalePriceTgtName = src2TgtMap.get(GOODS_SALE_PRICE);
        mallList.stream().forEach(mallInfo->{
            JSONObject jo = new JSONObject();//定义一个json的对象
            jo.put(goodsNameTgtName,mallInfo.getName());
            jo.put(goodsAmountTgtName, null == mallInfo.getAmount() ? 0 : Integer.parseInt(mallInfo.getAmount()));
            if (StringUtils.isBlank(mallInfo.getSalePrice())){
                String price = mallInfo.getPrice();
                price = StringUtils.isBlank(price) ? "0" : price;
                DecimalFormat df = new DecimalFormat("0.00");
                df.setRoundingMode(RoundingMode.HALF_UP);
                String convertPrice = df.format(Double.parseDouble(price));
                jo.put(goodsSalePriceTgtName,convertPrice);
            } else {
                jo.put(goodsSalePriceTgtName,null == mallInfo.getSalePrice() ? 0.0 : Double.parseDouble(mallInfo.getSalePrice()));
            }
            jsonArray.add(jo);
        });
        formParams.put("fd_mall_list", jsonArray);
        return;
    }

    private static final String GOODS_NAME = "goods_name";
    private static final String GOODS_COUNT = "goods_count";
    private static final String GOODS_SALE_PRICE = "goods_sale_price";
}
