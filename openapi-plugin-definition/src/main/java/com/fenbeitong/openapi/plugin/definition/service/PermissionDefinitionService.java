package com.fenbeitong.openapi.plugin.definition.service;

import com.fenbeitong.openapi.plugin.definition.dto.auto.CorpAutoScenePrivDTO;
import com.fenbeitong.openapi.plugin.definition.dto.plugin.permission.CreatePermissionDefinitionReqDTO;
import com.fenbeitong.openapi.plugin.definition.dto.plugin.permission.PermissionDefinitionInfoDTO;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.permission.dao.PermissionDefinitionDao;
import com.fenbeitong.openapi.plugin.support.permission.entity.PermissionDefinition;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.fenbeitong.openapi.plugin.definition.constant.DefinitionRespCode.PERMISSION_ALREADY_EXIST;

/**
 * 三方用户对接权限配置服务
 * Created by log.chang on 2019/12/14.
 */
@ServiceAspect
@Service
@Slf4j
public class PermissionDefinitionService {

    @Autowired
    private PermissionDefinitionDao permissionDefinitionDao;

    /**
     * 添加权限配置
     */
    public synchronized List<PermissionDefinitionInfoDTO> createPermission(CreatePermissionDefinitionReqDTO req) {
        preCreatePermission(req);
        Date now = DateUtils.now();
        PermissionDefinition permissionDefinition = PermissionDefinition.builder()
                .appId(req.getAppId())
                .scene(req.getScene())
                .permissionJson(req.getPermissionJson())
                .roleType(req.getRoleType())
                .createTime(now)
                .updateTime(now).build();
        permissionDefinitionDao.saveSelective(permissionDefinition);
        List<PermissionDefinition> permissionDefinitionList = permissionDefinitionDao.listByAppId(req.getAppId());
        return permissionDefinitionList.stream().map(pd -> PermissionDefinitionInfoDTO.builder()
                .id(pd.getId())
                .appId(pd.getAppId())
                .scene(pd.getScene())
                .permissionJson(pd.getPermissionJson())
                .roleType(pd.getRoleType()).build()).collect(Collectors.toList());
    }

    /**
     * 批量添加权限数据
     *
     * @param corpAutoScenePrivDTO
     * @return
     */
    public synchronized List<PermissionDefinition> batchCreatePermission(CorpAutoScenePrivDTO corpAutoScenePrivDTO) {
        //判断对象属性是否为空，如果为空说明为添加权限为0的数据
        ArrayList<CreatePermissionDefinitionReqDTO> list = Lists.newArrayList();
        if (StringUtils.isBlank(corpAutoScenePrivDTO.getRoleType()) || "0".equals(corpAutoScenePrivDTO.getRoleType())) {
            CreatePermissionDefinitionReqDTO createPermissionDefinitionReqDTO = CreatePermissionDefinitionReqDTO.builder()
                    .appId(corpAutoScenePrivDTO.getCompanyId())
                    .scene("air")
                    .roleType(0)
                    .permissionJson("{\"switch_flag\":false,\"air_priv_type\":1,\"oneself_limit\":1,\"air_verify_flag\":true,\"air_rule_limit_flag\":false,\"exceed_buy_type\":3,\"refund_ticket_type\":0,\"changes_ticket_type\":0}")
                    .build();
            CreatePermissionDefinitionReqDTO createPermissionDefinitionReqDTO1 = CreatePermissionDefinitionReqDTO.builder()
                    .appId(corpAutoScenePrivDTO.getCompanyId())
                    .scene("hotel")
                    .roleType(0)
                    .permissionJson("{\"switch_flag\":false,\"hotel_priv_type\":1,\"oneself_limit\":0,\"hotel_verify_flag\":true,\"hotel_rule_limit_flag\":false,\"exceed_buy_type\":3,\"refund_ticket_type\":0,\"personal_pay\":false}")
                    .build();
            CreatePermissionDefinitionReqDTO createPermissionDefinitionReqDTO2 = CreatePermissionDefinitionReqDTO.builder()
                    .appId(corpAutoScenePrivDTO.getCompanyId())
                    .scene("train")
                    .roleType(0)
                    .permissionJson("{\"switch_flag\":false,\"train_priv_type\":1,\"oneself_limit\":0,\"train_verify_flag\":true,\"train_rule_limit_flag\":false,\"exceed_buy_type\":1,\"refund_ticket_type\":0,\"changes_ticket_type\":0}")
                    .build();
            CreatePermissionDefinitionReqDTO createPermissionDefinitionReqDTO3 = CreatePermissionDefinitionReqDTO.builder()
                    .appId(corpAutoScenePrivDTO.getCompanyId())
                    .scene("car")
                    .roleType(0)
                    .permissionJson("{\"exceed_buy_type\":1,\"personal_pay\":false,\"car_priv_flag\":true,\"rule_limit_flag\":false,\"allowShuttle\":false}")
                    .build();
            CreatePermissionDefinitionReqDTO createPermissionDefinitionReqDTO4 = CreatePermissionDefinitionReqDTO.builder()
                    .appId(corpAutoScenePrivDTO.getCompanyId())
                    .scene("intl_air")
                    .roleType(0)
                    .permissionJson("{\"switch_flag\":false,\"air_priv_type\":1,\"oneself_limit\":1,\"air_verify_flag\":true,\"air_rule_limit_flag\":false,\"exceed_buy_type\":3,\"refund_ticket_type\":0,\"changes_ticket_type\":0}")
                    .build();
            CreatePermissionDefinitionReqDTO createPermissionDefinitionReqDTO5 = CreatePermissionDefinitionReqDTO.builder()
                    .appId(corpAutoScenePrivDTO.getCompanyId())
                    .scene("mall")
                    .roleType(0)
                    .permissionJson("{\"mall_priv_flag\":false,\"rule_limit_flag\":false,\"exceed_buy_flag\":false,\"mall_verify_flag\":true}")
                    .build();
            CreatePermissionDefinitionReqDTO createPermissionDefinitionReqDTO6 = CreatePermissionDefinitionReqDTO.builder()
                    .appId(corpAutoScenePrivDTO.getCompanyId())
                    .scene("takeaway")
                    .roleType(0)
                    .permissionJson("{\"takeaway_priv_flag\":false,\"takeaway_rule_limit_flag\":false,\"exceed_buy_type\":1,\"personal_pay\":false}")
                    .build();

            CreatePermissionDefinitionReqDTO createPermissionDefinitionReqDTO8 = CreatePermissionDefinitionReqDTO.builder()
                    .appId(corpAutoScenePrivDTO.getCompanyId())
                    .scene("dinners")
                    .roleType(0)
                    .permissionJson("{\"dinners_priv_flag\":false,\"rule_limit_flag\":false,\"dinner_policy\":{\"exceed_buy_flag\":1},\"meishi_policy\":{\"exceed_buy_type\":1,\"personal_pay\":false}}")
                    .build();
            CreatePermissionDefinitionReqDTO createPermissionDefinitionReqDTO9 = CreatePermissionDefinitionReqDTO.builder()
                    .appId(corpAutoScenePrivDTO.getCompanyId())
                    .scene("shansong")
                    .roleType(0)
                    .permissionJson("{\"shansong_priv_flag\":false}")
                    .build();
            CreatePermissionDefinitionReqDTO createPermissionDefinitionReqDTO10 = CreatePermissionDefinitionReqDTO.builder()
                    .appId(corpAutoScenePrivDTO.getCompanyId())
                    .scene("shunfeng")
                    .roleType(0)
                    .permissionJson("{\"shunfeng_priv_flag\":false}")
                    .build();
            list.add(createPermissionDefinitionReqDTO);
            list.add(createPermissionDefinitionReqDTO1);
            list.add(createPermissionDefinitionReqDTO2);
            list.add(createPermissionDefinitionReqDTO3);
            list.add(createPermissionDefinitionReqDTO4);
            list.add(createPermissionDefinitionReqDTO5);
            list.add(createPermissionDefinitionReqDTO6);
            list.add(createPermissionDefinitionReqDTO8);
            list.add(createPermissionDefinitionReqDTO9);
            list.add(createPermissionDefinitionReqDTO10);
        } else {

            // 拼装具体的权限json数据,
            // 最终形式 {"switch_flag":true,"oneself_limit":0,"air_priv_type":3,"air_verify_flag":false,"air_rule_limit_flag":false,"refund_ticket_type":0,"changes_ticket_type":0}
            //{"takeaway_priv_flag":true,"takeaway_rule_limit_flag":true,"takeaway_rule_id":111,"exceed_buy_type":1,"personal_pay":true}
            String s = JsonUtils.toJsonSnake(corpAutoScenePrivDTO);
            Map<String, Object> map = JsonUtils.toObj(s, Map.class);
            //总权限开关
            if (((String) map.get("scene")).equals("air") || ((String) map.get("scene")).equals("train") || ((String) map.get("scene")).equals("hotel") || ((String) map.get("scene")).equals("intl_air")) {
                map.put("switch_flag", true);
            } else {
                map.put(((String) map.get("scene")) + "_priv_flag", true);
            }

            //新建差旅信息,机票(国内，国际)，酒店，火车
            if (!(((String) map.get("scene")).equals("air") || ((String) map.get("scene")).equals("train") || ((String) map.get("scene")).equals("hotel") || ((String) map.get("scene")).equals("intl_air"))) {
                map.remove("oneself_limit");
            }
            //预定权限,机票，酒店，火车为priv_type类型，其他业务线为priv_flag
            if (ObjectUtils.isEmpty(map.get("priv_type"))) {//如果没有填写订单审批，则场景不需要事前订单信息
                map.remove("priv_type");
            } else {//机票，酒店，火车，采购需要事前审批
                if (((String) map.get("scene")).equals("air") || ((String) map.get("scene")).equals("train") || ((String) map.get("scene")).equals("hotel")) {
                    map.put(map.get("scene") + "_priv_type", map.get("priv_type"));
                } else if (((String) map.get("scene")).equals("intl_air")) {//国际机票
                    map.put("air_priv_type", map.get("priv_type"));
                } else {
                    map.put(map.get("scene") + "_priv_flag", true);
                    map.remove("priv_type");
                }
            }
            //是否限制规则，主要用于没有限制的场景
            if (ObjectUtils.isEmpty(map.get("rule_limit_flag"))) {
                map.remove("rule_limit_flag");
            } else {//机票，酒店，火车，外卖
                if (((String) map.get("scene")).equals("air") || ((String) map.get("scene")).equals("train") || ((String) map.get("scene")).equals("hotel") || ((String) map.get("scene")).equals("takeaway")) {
                    map.put(map.get("scene") + "_rule_limit_flag", map.get("rule_limit_flag"));
                    map.remove("rule_limit_flag");
                } else if (((String) map.get("scene")).equals("intl_air")) {//国际机票规则设置
                    map.put("air_rule_limit_flag", map.get("rule_limit_flag"));
                    map.remove("rule_limit_flag");
                }

            }

            //规则ID，普通规则ID，用车会包含多个规则ID,用车还需要设置审批用车规则ID
            //[{"rule_id":[111,2222],"type":1},{"type":2,"rule_id":[5555,6666]}]
            List<Map<String, Object>> ruleIdList = Lists.newArrayList();
            if (((String) map.get("scene")).equals("car")) {//用车
                Map<String, Object> ruleIdMap = Maps.newHashMap();
                if (ObjectUtils.isEmpty(map.get("rule_id"))) {
                    map.remove("rule_id");
                } else {//规则ID不为空
                    List<String> strings = Lists.newArrayList();
                    if (((String) (map.get("rule_id"))).contains(",")) {//判断是否传递多个普通规则ID
                        String[] rule_ids = ((String) (map.get("rule_id"))).split(",");
                        strings = Arrays.asList(rule_ids);
                    } else {
                        strings.add(((String) map.get("rule_id")));
                    }
                    ruleIdMap.put("rule_id", strings);
                    ruleIdMap.put("type", 1);
                    map.remove("rule_id");
                    ruleIdList.add(ruleIdMap);
                }

                //设置申请用车规则ID
//                if(((String) map.get("scene")).equals("car")){//用车
                Map<String, Object> applyRuleIdMap = Maps.newHashMap();
                if (ObjectUtils.isEmpty(((String) (map.get("apply_rule_id"))))) {
                    map.remove("apply_rule_id");
                } else {//规则ID不为空
                    List<String> strings = Lists.newArrayList();
                    if (((String) (map.get("apply_rule_id"))).contains(",")) {//判断是否传递多个普通规则ID
                        String[] rule_ids = ((String) (map.get("apply_rule_id"))).split(",");
                        strings = Arrays.asList(rule_ids);
                    } else {
                        strings.add(((String) map.get("apply_rule_id")));
                    }
                    applyRuleIdMap.put("rule_id", strings);
                    applyRuleIdMap.put("type", 2);
                    map.remove("apply_rule_id");
                    ruleIdList.add(applyRuleIdMap);
                }

                map.put("rule_ids", ruleIdList);
            } else {//其他规则ID，
                if (((String) map.get("scene")).equals("air") || ((String) map.get("scene")).equals("train") || ((String) map.get("scene")).equals("hotel") || ((String) map.get("scene")).equals("takeaway") ) {
                    map.put(map.get("scene") + "_rule_id", map.get("rule_id"));
                    map.remove("rule_id");
                } else if (((String) map.get("scene")).equals("intl_air")) {//国际机票
                    map.put("air_id", map.get("rule_id"));
                    map.remove("rule_id");
                }
                map.remove("apply_rule_id");
            }

            //事前审批
            if (((String) map.get("scene")).equals("car") || ((String) map.get("scene")).equals("takeaway") || ((String) map.get("scene")).equals("dinners") || ((String) map.get("scene")).equals("shansong") || ((String) map.get("scene")).equals("shunfeng")) {//该场景没有事前审批
                map.remove("verify_flag");
            } else {//机票，酒店，火车，采购需要事前审批
                if (((String) map.get("scene")).equals("intl_air")) {
                    map.put("air_verify_flag", map.get("verify_flag"));
                } else {
                    map.put(map.get("scene") + "_verify_flag", map.get("verify_flag"));
                }
                map.remove("verify_flag");
            }
            //订单审批
            if (((String) map.get("scene")).equals("car") || ((String) map.get("scene")).equals("takeaway") || ((String) map.get("scene")).equals("dinners") || ((String) map.get("scene")).equals("shansong") || ((String) map.get("scene")).equals("shunfeng") || ((String) map.get("scene")).equals("mall")) {//如果没有填写订单审批，则场景不需要事前订单信息
                map.remove("order_verify_flag");
            } else {//机票，酒店，火车，采购需要事前审批
                map.put(map.get("scene") + "_order_verify_flag", map.get("order_verify_flag"));
                map.remove("order_verify_flag");
            }

            Map<String, Object> dinnerMap = Maps.newHashMap();
            Map<String, Object> meishiMap = Maps.newHashMap();
            if (ObjectUtils.isEmpty(map.get("exceed_buy_type"))) {//超规
                map.remove("exceed_buy_type");
            } else {//不为空的情况下，需要单独设置用餐的超规，{"rule_priv_flag":true,"rule_limit_flag":true,"rule_id":"ofaijwf","dinner_policy":{"exceed_buy_flag":1},"meishi_policy":{"exceed_buy_type":1,"personal_pay":true}}
                if (((String) map.get("scene")).equals("dinners")) {//用餐场景
                    dinnerMap.put("exceed_buy_flag", map.get("exceed_buy_type"));
                    map.put("dinner_policy", dinnerMap);
                    meishiMap.put("exceed_buy_type", map.get("exceed_buy_type"));
                    map.remove("exceed_buy_type");
                }
            }

            if(((String) map.get("scene")).equals("mall")){//采购获取是否可以超标字段
                map.remove("exceed_buy_type");
            }else{
                map.remove("exceed_buy_flag");
            }

            if (!(((String) map.get("scene")).equals("air") || ((String) map.get("scene")).equals("train") || ((String) map.get("scene")).equals("hotel") ||((String) map.get("scene")).equals("intl_air"))) {//退票
                map.remove("refund_ticket_type");
            }

            if (!(((String) map.get("scene")).equals("air") || ((String) map.get("scene")).equals("train")  || ((String) map.get("scene")).equals("intl_air") ) ) {//改签
                map.remove("changes_ticket_type");
            }

            if (!((String) map.get("scene")).equals("car")) {//接送机
                map.remove("allow_shuttle");
            }
            if (!(boolean) map.get("personal_pay")) {//个人支付
                map.remove("personal_pay");
            } else {
                if (((String) map.get("scene")).equals("dinners")) {//用餐场景
                    meishiMap.put("personal_pay", map.get("personal_pay"));
                    map.put("meishi_policy", meishiMap);
                    map.remove("personal_pay");
                }
            }
            if(StringUtils.isBlank((String)(map.get("rule_id")))){
                map.remove("rule_id");
            }
            if(((String) map.get("scene")).equals("shansong") || ((String) map.get("scene")).equals("shunfeng")){
                map.remove("rule_limit_flag");
                map.remove("exceed_buy_type");
                map.remove("personal_pay");
            }
            map.remove("scene");
            map.remove("priv_type");
            map.remove("company_id");
            map.remove("role_type");

            log.info("jsonmap : {}", JsonUtils.toJson(map));

            CreatePermissionDefinitionReqDTO createPermissionDefinitionReqDTO7 = CreatePermissionDefinitionReqDTO.builder()
                    .appId(corpAutoScenePrivDTO.getCompanyId())
                    .scene(corpAutoScenePrivDTO.getScene())
                    .roleType(Integer.valueOf(corpAutoScenePrivDTO.getRoleType()))
                    .permissionJson(JsonUtils.toJson(map))
                    .build();
            list.add(createPermissionDefinitionReqDTO7);
        }

        Date now = DateUtils.now();
        List<PermissionDefinition> permissionDefinitions = Lists.newArrayList();

        list.stream().forEach(permission -> permissionDefinitions.add(PermissionDefinition.builder()
                .appId(permission.getAppId())
                .scene(permission.getScene())
                .permissionJson(permission.getPermissionJson())
                .roleType(permission.getRoleType())
                .createTime(now)
                .updateTime(now).build()));

        permissionDefinitionDao.saveList(permissionDefinitions);
        //根据公司ID查询所有权限数据
        List<PermissionDefinition> permissionDefinitionList = permissionDefinitionDao.listByAppId(corpAutoScenePrivDTO.getCompanyId());
        return permissionDefinitionList;
    }

    private void preCreatePermission(CreatePermissionDefinitionReqDTO req) {
        if (permissionDefinitionDao.getByUnion(req.getAppId(), req.getRoleType(), req.getScene()) != null)
            throw new OpenApiPluginSupportException(PERMISSION_ALREADY_EXIST, req.getAppId(), req.getScene(), req.getRoleType());
    }

    /**
     * 查询企业权限配置
     */
    public List<PermissionDefinitionInfoDTO> listPermission(String appId) {
        List<PermissionDefinition> permissionDefinitionList = permissionDefinitionDao.listByAppId(appId);
        return permissionDefinitionList.stream().map(pd -> PermissionDefinitionInfoDTO.builder()
                .id(pd.getId())
                .appId(pd.getAppId())
                .scene(pd.getScene())
                .permissionJson(pd.getPermissionJson())
                .roleType(pd.getRoleType()).build()).collect(Collectors.toList());
    }

    /**
     * 删除权限配置
     */
    public Map deletePermission(int permissionId) {
        //PermissionDefinition permissionDefinition = PermissionDefinition.builder().id(permissionId).build();
        permissionDefinitionDao.deleteById(permissionId);
        return new HashMap<>();
    }
}
