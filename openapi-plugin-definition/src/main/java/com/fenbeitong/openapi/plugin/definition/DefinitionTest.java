package com.fenbeitong.openapi.plugin.definition;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.support.city.constants.CityRelationType;
import com.fenbeitong.openapi.plugin.support.city.entity.CityRelation;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.Data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by log.chang on 2021/1/6.
 */
public class DefinitionTest {

    public static void main(String[] args) {
        // 659004549
        //dealKailinCity();
        initKailinCity();
    }

    private static void initKailinCity() {
        List<CityRelation> cityRelationList = new ArrayList<>();
        try {
            FileReader fr = new FileReader("/Users/log.chang/Documents/分贝通/系统对接/客户档案/开林/城市对照.txt");
            BufferedReader bf = new BufferedReader(fr);
            String str;
            // 按行读取字符串
            while ((str = bf.readLine()) != null) {
                String arr[] = str.split("\t");
                CityRelation cityRelation = CityRelation.builder().outterCode(arr[0]).fbtCode(arr[2]).relationType(CityRelationType.Ecology.getCode()).build();
                cityRelationList.add(cityRelation);
            }
            bf.close();
            fr.close();
        } catch (Exception ex) {
        }

        StringBuilder sb = new StringBuilder("insert into open_city_relation (outter_code,fbt_code,relation_type) \nvalues");
        for (int i = 0; i < cityRelationList.size(); i++) {
            CityRelation cr = cityRelationList.get(i);
            if (i > 0)
                sb.append(",");
            sb.append("\n(" + "'").append(cr.getOutterCode()).append("',").append("'").append(cr.getFbtCode()).append("',").append("1").append(")");
        }
        sb.append(";");
        System.err.println(sb.toString());
    }

    /**
     * 初始化开林泛微城市映射
     */
    private static void dealKailinCity() {
        List<ECity> eCityList = geteECityList("/Users/log.chang/Documents/分贝通/系统对接/客户档案/开林/srcCity.json");
        List<FbtCity> fbtCityList = geteFbtCityList("/Users/log.chang/Documents/分贝通/系统对接/客户档案/开林/tgtCity");
        List<CityRelation> cityRelationList = new ArrayList<>();
        dealCharteredCities(eCityList, cityRelationList);
        dealCity(eCityList, fbtCityList, cityRelationList);
        for (CityRelation cr : cityRelationList) {
            String outterCode = cr.getOutterCode();
            String fbtCode = cr.getFbtCode();
            ECity eCity = eCityList.stream().filter(ec -> StringUtils.obj2str(ec.getId()).equals(outterCode)).findFirst().get();
            FbtCity fbtCity = fbtCityList.stream().filter(fbtC -> fbtC.getId().equals(fbtCode)).findFirst().get();
            System.err.println(eCity.getId() + "\t" + eCity.getCityName() + "\t" + fbtCity.getId() + "\t" + fbtCity.getCityName());
        }
        dealOthers(eCityList, cityRelationList);
    }

    /**
     * 其他城市
     */
    private static void dealOthers(List<ECity> eCityList, List<CityRelation> cityRelationList) {
        outter:
        for (ECity eCity : eCityList) {
            for (CityRelation cr : cityRelationList) {
                if (StringUtils.obj2str(eCity.getId()).equals(cr.getOutterCode()))
                    continue outter;
            }
            System.err.println(eCity.getId() + "\t" + eCity.getCityName());
        }
    }

    /**
     * 直辖市
     */
    private static void dealCharteredCities(List<ECity> eCityList, List<CityRelation> cityRelationList) {
        for (ECity eCity : eCityList) {
            if (cityRelationList.stream().anyMatch(cr -> cr.getOutterCode().equals(StringUtils.obj2str(eCity.getId()))))
                continue;
            String parentCityId = eCity.getProvinceId();
            CityRelation cityRelation;
            if ("110000".equals(parentCityId)) {
                cityRelation = CityRelation.builder().outterCode(StringUtils.obj2str(eCity.getId())).fbtCode("1000001").relationType(CityRelationType.Ecology.getCode()).build();
            } else if ("310000".equals(parentCityId)) {
                cityRelation = CityRelation.builder().outterCode(StringUtils.obj2str(eCity.getId())).fbtCode("2000002").relationType(CityRelationType.Ecology.getCode()).build();
            } else if ("120000".equals(parentCityId)) {
                cityRelation = CityRelation.builder().outterCode(StringUtils.obj2str(eCity.getId())).fbtCode("3000003").relationType(CityRelationType.Ecology.getCode()).build();
            } else if ("500000".equals(parentCityId)) {
                cityRelation = CityRelation.builder().outterCode(StringUtils.obj2str(eCity.getId())).fbtCode("4000004").relationType(CityRelationType.Ecology.getCode()).build();
            } else {
                continue;
            }
            cityRelationList.add(cityRelation);
        }
    }

    /**
     * 普通市、县
     */
    private static void dealCity(List<ECity> eCityList, List<FbtCity> fbtCityList, List<CityRelation> cityRelationList) {
        for (ECity eCity : eCityList) {
            if (cityRelationList.stream().anyMatch(cr -> cr.getOutterCode().equals(StringUtils.obj2str(eCity.getId()))))
                continue;
            final String eCityName = eCity.getCityName();
            if (eCityName.contains("地区")) {
                String tempCityName = eCityName.substring(0, eCityName.indexOf("地区"));
                CityRelation cityRelation = matchFbtCity(eCity.getId(), tempCityName, fbtCityList);
                if (cityRelation == null)
                    continue;
                cityRelationList.add(cityRelation);
            } else if (eCityName.contains("市")) {
                String tempCityName = eCityName.substring(0, eCityName.indexOf("市"));
                CityRelation cityRelation = matchFbtCity(eCity.getId(), tempCityName, fbtCityList);
                if (cityRelation == null)
                    continue;
                cityRelationList.add(cityRelation);
            } else if (eCityName.contains("县")) {
                String tempCityName = eCityName.substring(0, eCityName.indexOf("县"));
                CityRelation cityRelation = matchFbtCity(eCity.getId(), tempCityName, fbtCityList);
                if (cityRelation == null)
                    continue;
                cityRelationList.add(cityRelation);
            } else if (fbtCityList.stream().anyMatch(fbtC -> eCityName.equals(fbtC.getCityName()))) {
                FbtCity fbtCity = fbtCityList.stream().filter(fbtC -> eCityName.equals(fbtC.getCityName())).findFirst().get();
                CityRelation cityRelation = CityRelation.builder().relationType(CityRelationType.Ecology.getCode()).fbtCode(fbtCity.getId()).outterCode(StringUtils.obj2str(eCity.getId())).build();
                cityRelationList.add(cityRelation);
            }
        }
    }

    private static CityRelation matchFbtCity(int cityId, String cityName, List<FbtCity> fbtCityList) {
        FbtCity fbtCity = fbtCityList.stream().filter(c -> c.getCityName().contains(cityName)).findFirst().orElse(null);
        if (fbtCity == null)
            return null;
        CityRelation cityRelation = new CityRelation();
        cityRelation.setRelationType(CityRelationType.Ecology.getCode());
        cityRelation.setOutterCode(StringUtils.obj2str(cityId));
        cityRelation.setFbtCode(fbtCity.getId());
        return cityRelation;
    }

    private static List<ECity> geteECityList(String fileName) {
        List<ECity> eCityList = new ArrayList<>();
        try {
            FileReader fr = new FileReader(fileName);
            BufferedReader bf = new BufferedReader(fr);
            String str;
            // 按行读取字符串
            while ((str = bf.readLine()) != null) {
                ECity eCity = JsonUtils.toObj(str, ECity.class);
                // 港澳台、废弃的天津市、省不处理
                if (eCity != null && !"820001".equals(eCity.getProvinceId()) && !"810000".equals(eCity.getProvinceId()) && !"820000".equals(eCity.getProvinceId()) && !"710000".equals(eCity.getProvinceId()) && !eCity.getCityName().contains("全部"))
                    eCityList.add(eCity);
            }
            bf.close();
            fr.close();
        } catch (Exception ex) {

        }
        return eCityList;
    }

    private static List<FbtCity> geteFbtCityList(String fileName) {

        List<FbtCity> fbtCityList = new ArrayList<>();
        try {
            FileReader fr = new FileReader(fileName);
            BufferedReader bf = new BufferedReader(fr);
            String str;
            // 按行读取字符串
            while ((str = bf.readLine()) != null) {
                String arr[] = str.split("\t");
                FbtCity fbtCity = new FbtCity();
                fbtCity.setId(arr[0]);
                fbtCity.setCityName(arr[1]);
                fbtCity.setParentName(arr[2]);
                fbtCityList.add(fbtCity);
            }
            bf.close();
            fr.close();
        } catch (Exception ex) {

        }
        return fbtCityList;
    }

    @Data
    public static class ECity {
        @JsonProperty("ID")
        private Integer id;
        @JsonProperty("CITYNAME")
        private String cityName;
        @JsonProperty("PROVINCEID")
        private String provinceId;
    }

    @Data
    public static class FbtCity {
        private String id;
        private String cityName;
        private String parentName;
    }

}
