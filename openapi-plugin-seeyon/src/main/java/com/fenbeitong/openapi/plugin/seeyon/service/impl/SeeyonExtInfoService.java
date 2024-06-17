package com.fenbeitong.openapi.plugin.seeyon.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.seeyon.dao.SeeyonExtInfoDao;
import com.fenbeitong.openapi.plugin.seeyon.dao.SeeyonOpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountEmpResp;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonExtInfo;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOpenMsgSetup;
import com.fenbeitong.openapi.plugin.seeyon.helper.JacksonHelper;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ServiceAspect
@Service
@Slf4j
public class SeeyonExtInfoService {
    @Autowired
    SeeyonExtInfoDao seeyonExtInfoDao;
    @Autowired
    SeeyonOpenMsgSetupDao seeyonOpenMsgSetupDao;

    public SeeyonExtInfo getSeeyonExtInfo(String companyId, Integer type, Integer state) {
        long start = System.currentTimeMillis();
        SeeyonExtInfo seeyonExtInfo = null;
        if (StringUtils.isNotBlank(companyId) && type != null && state != null) {
            Example example = new Example(SeeyonExtInfo.class);
            //一个类型只能使用一个
            example.createCriteria()
                    .andEqualTo("companyId", companyId)
                    .andEqualTo("state", state)
                    .andEqualTo("type", type);
            List<SeeyonExtInfo> seeyonExtInfos = seeyonExtInfoDao.listByExample(example);

            if (!ObjectUtils.isEmpty(seeyonExtInfos)) {
                seeyonExtInfo = seeyonExtInfos.get(0);
            }
        }
        return seeyonExtInfo;
    }

    /**
     * 获取公司同步频率
     *
     * @param companyId
     * @return
     */
    public SeeyonOpenMsgSetup getSyncFrequency(String companyId) {//配置单位为分钟
        Map itemCodeMap = Maps.newHashMap();
        itemCodeMap.put("companyId", companyId);
        itemCodeMap.put("itemCode", "company_sync_frequency");
        List<SeeyonOpenMsgSetup> list = seeyonOpenMsgSetupDao.seeyonOpenMsgSetupList(itemCodeMap);
        return list.size() > 0 ? list.get(0) : null;
    }


    public SeeyonExtInfo parseSeeyonExtInfo(SeeyonExtInfo seeyonExtInfo, SeeyonAccountEmpResp seeyonAccountEmpResp) {
//        log.info("开始解析seeyon_ext_info companyId: {}",seeyonExtInfo.getCompanyId());
        long start = System.currentTimeMillis();
        //根据公司ID查询需要获取的字段值
        SeeyonExtInfo byExample = null;
        if (!ObjectUtils.isEmpty(seeyonExtInfo)) {
            //查看公司是否设置了提前获取的字段属性配置
            Map specialItemCodeMap = Maps.newHashMap();
            specialItemCodeMap.put("companyId", seeyonExtInfo.getCompanyId());
            specialItemCodeMap.put("itemCode", "company_seeyon_special_ext_info");
            List<SeeyonOpenMsgSetup> list = seeyonOpenMsgSetupDao.seeyonOpenMsgSetupList(specialItemCodeMap);
            if (!ObjectUtils.isEmpty(list)) {//非空
                //TODO 取第一个，如果一条记录无法满足要求，需要根据多条数据进行拼接成一个，然后进行数据标记，
                List<String> str2List = Lists.newArrayList();
                List<String> str3List = Lists.newArrayList();
                //所有包含的属性值，如果在属性值范围内，则进行特定值的获取，并进行seeyon_ext_info权限的配置，配置相对应的分贝权限ID
                for (SeeyonOpenMsgSetup n : list) {
                    List<String> strVal2s = Arrays.asList(n.getStrVal2().split(","));
                    List<String> strVal3s = Arrays.asList(n.getStrVal3().split(","));
                    str2List.addAll(strVal2s);
                    str3List.addAll(strVal3s);
                }
                SeeyonOpenMsgSetup seeyonOpenMsgSetup = list.get(0);
                //设置包含的数值
                seeyonOpenMsgSetup.setStrVal2(String.join(",",str2List));
                seeyonOpenMsgSetup.setStrVal3(String.join(",",str3List));
                Integer intVal1 = seeyonOpenMsgSetup.getIntVal1();
                if (1 == intVal1) {//配置了公司提前设置属性
                    //获取的字段属性key,例：officeNum
                    String strVal1 = seeyonOpenMsgSetup.getStrVal1();
                    Map map = JsonUtils.toObj(JsonUtils.toJson(seeyonAccountEmpResp), Map.class);
                    //获取指定key的value值，根据strval2配置的值进行过滤
//                    String targetValue = (String) map.get(strVal1);
                    //可以根人员职务级别Id orgLevelId，人员岗位Id orgPostId，人员所属部门Id orgDepartmentId
                    String targetValue =  com.fenbeitong.openapi.plugin.util.StringUtils.obj2str( map.get(strVal1));
                    //具体包含的值，例：99，100，特殊权限类型
                    String strVal2 = seeyonOpenMsgSetup.getStrVal2();
                    //如果指定字段值包含在设置值内,或者手机号
                    if (StringUtils.isNotBlank(targetValue)) {
                        if (strVal2.contains(targetValue.trim())) {
                            HashMap<String, Object> descriptionMap = Maps.newHashMap();
                            //设置自定义属性给description
                            descriptionMap.put(seeyonExtInfo.getMapKey(), targetValue);
                            seeyonAccountEmpResp.setDescription(JacksonHelper.toJson(descriptionMap));
                        }
                        //特殊人员数据,如果单独根据人员过滤，可以配置人员的第三方ID,或者手机号.后期扩展使用
                        String strVal3 = seeyonOpenMsgSetup.getStrVal3();
                        if(StringUtils.isNotBlank(strVal3)){//不为空，则说明有特殊人员数据需要处理，可以指定某些人的权限,根据人员ID进行过滤，数据初始化时可以不进行配置，
                            //数据初始化完成后，可配置该数据，后期该人员权限则不进行更新，或者指定更新
                            if(strVal3.contains(com.fenbeitong.openapi.plugin.util.StringUtils.obj2str(seeyonAccountEmpResp.getId()))){
                                HashMap<String, Object> descriptionMap = Maps.newHashMap();
                                descriptionMap.put(seeyonExtInfo.getMapKey(), null);
                                seeyonAccountEmpResp.setDescription(JacksonHelper.toJson(descriptionMap));
                            }

                        }
                    }
                }
            }
            //取出目标字段，description
            String targetColum = seeyonExtInfo.getTargetColum();
            String accountEmpStr = JacksonHelper.toJson(seeyonAccountEmpResp);
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
                                String mapValueTrim = mapValue.trim();
                                if (mapValueTrim.equals("10000")) {
                                    byExample = SeeyonExtInfo.builder()
                                            .roleType(10000)
                                            .companyId(seeyonExtInfo.getCompanyId())
                                            .build();
                                    return byExample;
                                }
                                //根据取出的值获取对应的分贝权限类型
                                Example example = new Example(SeeyonExtInfo.class);
                                //一个类型只能使用一个
                                example.createCriteria()
                                        .andEqualTo("companyId", seeyonExtInfo.getCompanyId())
                                        .andEqualTo("state", 0)
                                        .andEqualTo("mapValue", mapValueTrim)
                                        .andEqualTo("type", 1);
                                byExample = seeyonExtInfoDao.getByExample(example);
                                return byExample;
                            }
                        }
                    } catch (Exception e) {
                        log.info("用户配置description字段格式错误 {}", targetColumStr);
//                        e.printStackTrace();
                    }
                }
            }
        }
//        log.info("解析seeyon_ext_info结束 companyId: {},耗时：{} ms",seeyonExtInfo.getCompanyId(),System.currentTimeMillis() - start);
        return byExample;
    }


    /**
     * 解析自定义字段description
     * @param seeyonAccountEmpResp
     * @return
     */
    public Map parseExtInfo(SeeyonAccountEmpResp seeyonAccountEmpResp) {
        String description = seeyonAccountEmpResp.getDescription();
        if (!ObjectUtils.isEmpty(description)) {
           return JsonUtils.toObj(description,Map.class);
        }
        return null;
    }



}
