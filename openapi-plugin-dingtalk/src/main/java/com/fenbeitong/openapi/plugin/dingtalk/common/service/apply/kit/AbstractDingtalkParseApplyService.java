package com.fenbeitong.openapi.plugin.dingtalk.common.service.apply.kit;


import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CostAttributionDTO;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description 钉钉表单解析
 * @Author xiaohai
 * @Date 2021-09-16
 **/
public abstract class AbstractDingtalkParseApplyService  {


    /**
     * 解析城市数据
     * @param cityExtendValue
     * @return
     */
    public String getCityList(String cityExtendValue){
        List<String> cityIdList = CollectionUtils.newArrayList();
        String cityValue = StringEscapeUtils.unescapeJava(cityExtendValue);
        List<Map> list = JsonUtils.toObj(cityValue, List.class , Map.class);
        list.forEach( city -> {
            String area = StringUtils.obj2str( city.get("area") );
            cityIdList.add(area);
        });
        if (!ObjectUtils.isEmpty(cityIdList)) {
            return String.join(",", cityIdList);
        }
        return "";
    }

    /**
     * 解析费用归属部门数据
     * @param costProjectExtendValue
     * @param costAttributionList
     */
    public void getCostDeaprtmentListNew(String costProjectExtendValue ,List<CostAttributionDTO> costAttributionList){
        if(StringUtils.isBlank(costProjectExtendValue)){
            return;
        }
        Map<String,Object> map = JsonUtils.toObj(costProjectExtendValue, Map.class);
        if(map == null){
            return;
        }
        Object directlyDepartmentInfo = map.get("directlyDepartmentInfo");
        Map<String,Object> deptMap = JsonUtils.toObj(JsonUtils.toJson(directlyDepartmentInfo), Map.class);
        String id = StringUtils.obj2str(deptMap.get("id"));
        String name = StringUtils.obj2str(deptMap.get("name"));
        CostAttributionDTO costAttributionDTO = new CostAttributionDTO();
        costAttributionDTO.setCostAttributionId( id );
        costAttributionDTO.setCostAttributionName( name );
        costAttributionDTO.setCostAttributionCategory(1);
        costAttributionList.add(costAttributionDTO);
    }

    /**
     * 解析费用归属部门数据
     * @param costProjectExtendValue
     * @param costAttributionList
     */
    public void getCostDeaprtmentList(String costProjectExtendValue ,List<CostAttributionDTO> costAttributionList){
        String costDepartmentValue = StringEscapeUtils.unescapeJava(costProjectExtendValue);
        List<Map> list = JsonUtils.toObj(costDepartmentValue, List.class , Map.class);
        if(!CollectionUtils.isBlank(list)){
            list.forEach( city -> {
                CostAttributionDTO costAttributionDTO = new CostAttributionDTO();
                costAttributionDTO.setCostAttributionId(StringUtils.obj2str( city.get("id") ));
                costAttributionDTO.setCostAttributionName(StringUtils.obj2str( city.get("name") ));
                costAttributionDTO.setCostAttributionCategory(1);
                costAttributionList.add(costAttributionDTO);
            });
        }

    }



    /**
     * 解析费用归属项目数据
     * @param costProjectExtendValue
     * @param costAttributionList
     */
    public void getCostProjectList(String costProjectExtendValue ,List<CostAttributionDTO> costAttributionList){
        String costProjectValue = StringEscapeUtils.unescapeJava(costProjectExtendValue);
        Map<String,Object> map = com.fenbeitong.openapi.plugin.util.JsonUtils.toObj(costProjectValue,  Map.class);
        if(map == null ) {
            return ;
        }
        CostAttributionDTO costAttributionDTO = new CostAttributionDTO();
        costAttributionDTO.setCostAttributionId(StringUtils.obj2str( map.get("id") ));
        costAttributionDTO.setCostAttributionName(StringUtils.obj2str( map.get("name") ));
        costAttributionDTO.setCostAttributionCategory(2);
        costAttributionList.add(costAttributionDTO);
    }


}
