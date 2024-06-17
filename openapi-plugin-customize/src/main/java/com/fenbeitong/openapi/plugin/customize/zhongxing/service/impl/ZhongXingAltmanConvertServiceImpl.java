package com.fenbeitong.openapi.plugin.customize.zhongxing.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.customize.zhongxing.service.IZhongXingAltmanConvertService;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackConfDao;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackConf;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.callback.service.IBusinessDataPushService;
import com.fenbeitong.openapi.plugin.support.company.service.impl.UcCompanyServiceImpl;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * @ClassName ZhongXingAltmanConvertServiceImpl
 * @Description 众行传媒万能订单转火车票正向单数据转换
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/3/31 下午2:52
 **/
@ServiceAspect
@Service
@Slf4j
public class ZhongXingAltmanConvertServiceImpl implements IZhongXingAltmanConvertService {

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;
    @Autowired
    private IEtlService etlService;
    @Autowired
    private ThirdCallbackRecordDao callbackRecordDao;

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    @Autowired
    private ThirdCallbackRecordDao thirdCallbackRecordDao;

    @Autowired
    private ThirdCallbackConfDao callbackConfDao;

    @Autowired
    private UcCompanyServiceImpl companyService;

    @Override
    public void zxCallbackDataConvert(String data) {
        if (StringUtils.isBlank(data)) {
            log.info("众行回传万能订单数据为空");
            throw new OpenApiArgumentException("众行回传万能订单数据为空");
        }
        Map<String, Object> dataMap = JsonUtils.toObj(data, Map.class);
        if (ObjectUtils.isEmpty(dataMap)) {
            log.info("众行回传万能订单数据转换为空");
            throw new OpenApiArgumentException("众行回传万能订单数据转换为空");
        }
        String orderId = StringUtils.obj2str(MapUtils.getValueByExpress(dataMap, "order_info:order_id"));
        List<OpenMsgSetup> etlConfigList = openMsgSetupDao.selectByItemCode("zx_altman_relief_convert");

        if (ObjectUtils.isEmpty(etlConfigList)) {
            log.info("万能转火车结构找不到转换配置，转换失败");
            throw new OpenApiArgumentException("万能转火车结构找不到转换配置，转换失败,订单号："+orderId);
        }
        String companyId = etlConfigList.get(0).getCompanyId();
        ThirdCallbackConf thirdCallbackConf = companyId == null ? null : callbackConfDao.queryByCompanyIdAndCallBackType(companyId, CallbackType.ZHONGXIN_ORDER.getType());

        if (ObjectUtils.isEmpty(thirdCallbackConf)) {
            log.info("未找到数据推送配置为空，companyId:{},callbackType:{}", companyId, CallbackType.ZHONGXIN_ORDER.getType());
            throw new OpenApiArgumentException("众行数据转换未找到数据推送配置");
        }
        //etl配置,火车票代打印转换
        long trainEtlConfigId = NumericUtils.obj2long(etlConfigList.get(0).getIntVal1());
        long carEtlConfigId = NumericUtils.obj2long(etlConfigList.get(0).getIntVal2());
        Map<String, Object> dataParam = JsonUtils.toObj(data, new TypeReference<Map<String, Object>>() {
        });
        //原订单类型
        int orderCategoryType = NumericUtils.obj2int(MapUtils.getValueByExpress(dataParam, "order_info:order_category_type"));
        //分类明细（筛选火车票代打印服务费）
        int orderTypeClassify = NumericUtils.obj2int(MapUtils.getValueByExpress(dataParam, "classify_info:order_type_classify"));
        int orderType = NumericUtils.obj2int(MapUtils.getValueByExpress(dataParam, "classify_info:order_type"));
        String companyName = companyService.getCompanyName(companyId);
        //根据原订单类型和分类明细进行过滤并转换，其他正常订单则直接记表并推送
        if (orderCategoryType == 911 && orderTypeClassify == 100250) {
            //转火车票正向单
            Map<String, Object> transferMap = etlService.transform(trainEtlConfigId, dataParam);
            if(ObjectUtils.isEmpty(transferMap)){
                log.info("众行传媒火车票代打服务费订单转换失败，订单号:{}",orderId);
                throw new FinhubException(-9999, "众行传媒火车票代打服务费订单转换失败，订单号:{}",orderId);
            }
            JsonUtils.toJson(dataParam);
            Map<String,Object> trainInfo = JsonUtils.toObj(JsonUtils.toJson(MapUtils.getValueByExpress(transferMap, "train_info")), new TypeReference<Map<String,Object>>() {
            });
            trainInfo.put("start_time",MapUtils.getValueByExpress(transferMap,"order_info:create_time"));
            trainInfo.put("end_time",MapUtils.getValueByExpress(transferMap,"order_info:create_time"));
            ThirdCallbackRecord record = new ThirdCallbackRecord();
            record.setType(15);
            record.setTypeName("火车票");
            record.setOrderId(StringUtils.obj2str(MapUtils.getValueByExpress(transferMap, "order_info:order_id")));
            record.setTicketId(StringUtils.obj2str(MapUtils.getValueByExpress(transferMap, "train_info:ticket_id")));
            record.setOrderStatus(NumberUtils.toInt(StringUtils.obj2str(MapUtils.getValueByExpress(transferMap, "order_info:status"))));
            record.setCompanyId(companyId);
            record.setCompanyName(companyName);
            record.setContactName(StringUtils.obj2str(MapUtils.getValueByExpress(transferMap, "passenger_info:name")));
            record.setUserName(StringUtils.obj2str(MapUtils.getValueByExpress(transferMap, "user_info:name")));
            record.setCallbackType(CallbackType.ZHONGXIN_ORDER.getType());
            record.setCallbackData(JsonUtils.toJson(transferMap));
            callbackRecordDao.saveSelective(record);
            businessDataPushService.pushData(companyId, record, 0, 4);
        } else if (orderCategoryType == 912 && orderType == 3) {
            //用车的减免订单，转用车逆向单
            Map<String, Object> transferMap = etlService.transform(carEtlConfigId, dataParam);
            if(ObjectUtils.isEmpty(transferMap)){
                log.info("众行传媒用车减免订单转换失败，订单号:{}",orderId);
                throw new FinhubException(-9999, "众行传媒用车减免订单转换失败，订单号:{}",orderId);
            }
            ThirdCallbackRecord record = new ThirdCallbackRecord();
            record.setType(3);
            record.setTypeName("用车");
            record.setOrderId(StringUtils.obj2str(MapUtils.getValueByExpress(transferMap, "order_info:order_id")));
            record.setOrderStatus(NumberUtils.toInt(StringUtils.obj2str(MapUtils.getValueByExpress(transferMap, "order_info:status"))));
            record.setCompanyId(companyId);
            record.setCompanyName(companyName);
            record.setContactName(StringUtils.obj2str(MapUtils.getValueByExpress(transferMap, "passenger_info:name")));
            record.setUserName(StringUtils.obj2str(MapUtils.getValueByExpress(transferMap, "user_info:name")));
            record.setCallbackType(CallbackType.ZHONGXIN_ORDER.getType());
            record.setCallbackData(JsonUtils.toJson(transferMap));
            callbackRecordDao.saveSelective(record);
            businessDataPushService.pushData(companyId, record, 0, 4);
        } else {
            String typeName = StringUtils.obj2str(MapUtils.getValueByExpress(dataParam, "order_info:order_category_name"));
            ThirdCallbackRecord zxCallbackData = new ThirdCallbackRecord();
            zxCallbackData.setType(orderCategoryType);
            zxCallbackData.setTypeName(typeName);
            zxCallbackData.setOrderId(orderId);
            zxCallbackData.setOrderStatus(NumberUtils.toInt(StringUtils.obj2str(MapUtils.getValueByExpress(dataParam, "order_info:status"))));
            zxCallbackData.setCompanyId(companyId);
            zxCallbackData.setCompanyName(companyName);
            zxCallbackData.setContactName(StringUtils.obj2str(MapUtils.getValueByExpress(dataParam, "passenger_info:name")));
            zxCallbackData.setUserName(StringUtils.obj2str(MapUtils.getValueByExpress(dataParam, "user_info:name")));
            zxCallbackData.setCallbackType(CallbackType.ZHONGXIN_ORDER.getType());
            zxCallbackData.setCallbackData(data);
            callbackRecordDao.saveSelective(zxCallbackData);
            businessDataPushService.pushData(companyId, zxCallbackData, 0, 4);
        }
    }
}
