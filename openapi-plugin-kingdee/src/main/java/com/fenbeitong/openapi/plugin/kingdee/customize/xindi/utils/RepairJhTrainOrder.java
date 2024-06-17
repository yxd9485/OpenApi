package com.fenbeitong.openapi.plugin.kingdee.customize.xindi.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.func.order.service.FuncAirOrderServiceImpl;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.luastar.swift.base.json.JsonUtils;
import com.luastar.swift.base.net.HttpClientUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * <p>Title: RepairJhTrainOrder</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/6/30 12:04 PM
 */
public class RepairJhTrainOrder {

    public static void main(String[] args) {
        testPrice();
    }

    public static void main1(String[] args) {
        String str = "订单id;" +
                "60d94d936013459578d854ac;" +
                "60d94d80445de4dd1e3a7170;" +
                "60d82f1a445de4dd1e3a33f6;" +
                "60d82e2c6013459578d81705;" +
                "60d81857b5aaffa6923095ff;" +
                "60d81835b5aaffa6923095e6;" +
                "60d6ccb8445de4dd1e39f53d;" +
                "60d6c90b445de4dd1e39f3cd;" +
                "60d6a938b5aaffa6923051c5;" +
                "60d6a67eb5aaffa69230509e;" +
                "60d5ea2b6013459578d7b82a;" +
                "60d56733445de4dd1e39a01d;" +
                "60d5654d445de4dd1e399f3d;" +
                "60d565f26013459578d783ee;" +
                "60d49fd16013459578d75c42;" +
                "60d46c07b5aaff8b3e4260aa;" +
                "60d326251aead23416a138cc;" +
                "60d32334445de462f557e3d9;" +
                "60d32286b5aaff21c76be44e;" +
                "60d2fec41aead23416a11ea1;" +
                "60d2b256b5aaff21c76b9555;" +
                "60d2b24b601345fdc0e69dd6;" +
                "60d292f7601345fdc0e68a23;" +
                "60d1f1c4445de462f5576cb7;" +
                "60d1d8d3601345fdc0e66b33;" +
                "60d1c9bf1aead23416a0ad63;" +
                "60d18e4a601345fdc0e63692;" +
                "60d17d61445de462f55720d8;" +
                "60d17d33b5aaff21c76b23c8;" +
                "60d15d25b5aaff21c76b0ed5;" +
                "60d15c701aead23416a05e6a;" +
                "60d13e88445de462f556f955;" +
                "60d136e8601345fdc0e5fd96;" +
                "60d11c2a601345fdc0e5f50f;" +
                "60d094c11aead23416a0356e;" +
                "60d09471445de462f556e191;" +
                "60d01f40601345fdc0e5a97b;" +
                "60cf22a3601345fdc0e57382;" +
                "60cf220fb5aaff21c76a6c28;" +
                "60cf13c2b5aaff21c76a6822;" +
                "60cf0bd01aead234169fb8df;" +
                "60cefb52601345fdc0e56705;" +
                "60cef1df445de462f5565dc2;" +
                "60cedf60445de462f5565870;" +
                "60cedf1f445de462f5565860;" +
                "60cecdeeb5aaff21c76a52fa;" +
                "60cecc9d445de462f5565248;" +
                "60cec0471aead234169fa1b4;" +
                "60cebcfe445de462f5564c96;" +
                "60cea4781aead234169f98c8;" +
                "60cea26d445de462f55643f5;" +
                "60ce9ee2601345fdc0e54b03;" +
                "60ce9ebf1aead234169f96ed;" +
                "60ce9ebf1aead234169f96eb;" +
                "60ce6eec601345fdc0e54472;" +
                "60cdaf1c445de462f5562604;" +
                "60cdaea81aead234169f79d4;" +
                "60cdad6c445de462f556258a;" +
                "60cd595d1aead234169f5d8e;" +
                "60cd40a91aead234169f56c5;" +
                "60cd40af445de462f556045c;" +
                "60cd278cb5aaff21c76a018c;" +
                "60cc89b4601345fdc0e4f8ea;" +
                "60cc2f8a1aead234169f195b;" +
                "60cc2bb2b5aaff21c769c3c9;" +
                "60cc117d1aead234169f08ea;" +
                "60cbf20fb5aaff21c769a6d3;" +
                "60cbcc20b5aaff21c7699fc8;" +
                "60cb5a26b5aaff21c7699a33;" +
                "60cb5a25b5aaff21c7699a2c;" +
                "60ca8c011aead20be238f0d0;" +
                "60c9f1191aead20be238dd88;" +
                "60c9f07d445de432b1e0a5d8;" +
                "60c99e7e601345cb06bd815a;" +
                "60c9711a445de432b1e0549b;" +
                "60c96b39b5aafff8171f4bcd;" +
                "60c947e5445de432b1e040f2;" +
                "60c85e25445de40b83fab598;" +
                "60c85908445de40b83fab359;" +
                "60c7116b445de40b83fa6982;" +
                "60c710ddb5aaffa091446be6;" +
                "60c6d2191aead2b414e2de6b;" +
                "60c611d4b5aaffa09144546b;" +
                "60c60ed71aead2b414e2d395;" +
                "60c5b195445de40b83fa4902;" +
                "60c5b167b5aaffa091444a26;" +
                "60c5b0fe445de40b83fa48d7;" +
                "60c5ad946013457e61acb0f3;" +
                "60c54c886013457e61aca4bb;" +
                "60c4aa876013457e61ac9e78;" +
                "60c4aa0bb5aaffa0914437e9;" +
                "60c455331aead2b414e2aba3;" +
                "60c4550d6013457e61ac9338;" +
                "60c450536013457e61ac9290;" +
                "60c44e7b6013457e61ac923f;" +
                "60c42df31aead2b414e2a58e;" +
                "60c30b466013457e61ac629d;" +
                "60c2f3761aead2b414e2732b;" +
                "60c2ebd51aead2b414e2702b;" +
                "60c2b41e1aead2b8a379848f;" +
                "60c214a61aead2b8a3797629;" +
                "60c214161aead2b8a379761a;" +
                "60c21355b5aaffaf3ce371b1;" +
                "60c212a7445de410ed67d7d7;" +
                "60c189896013458291901c2c;" +
                "60c1885bb5aaffaf3ce327ad;" +
                "60c1880ab5aaffaf3ce3276c;" +
                "60c187aa1aead2b8a37929ef;" +
                "60c167a9445de410ed677845;" +
                "60c15e53b5aaffaf3ce3136e;" +
                "60c15229445de410ed67710a;" +
                "60c151eb445de410ed6770fb;" +
                "60c0c68c601345f5848b7705;" +
                "60c0a2c41aead221c92dbefb;" +
                "60c0a223445de4836a968bf5;" +
                "60c09feb601345f5848b646f;" +
                "60c09dba601345f5848b62ea;" +
                "60c09a6d1aead221c92dba2a;" +
                "60c098ba445de4836a968638;" +
                "60c097e7445de4836a968599;" +
                "60c0917c601345f5848b5a5d;" +
                "60c09104601345f5848b59f9;" +
                "60c08b0eb5aaff19510ad1ec;" +
                "60c08af5b5aaff19510ad1d5;" +
                "60c08acf601345f5848b55e4;" +
                "60c080f5b5aaff19510acace;" +
                "60c0805d445de4836a96757e;" +
                "60c07fa8601345f5848b4de5;" +
                "60c053ae601345f5848b328c;" +
                "60c04c21b5aaff19510aa8e1;" +
                "60c04be4b5aaff19510aa8b6;" +
                "60c04a2c445de4836a96528d;" +
                "60c04a11601345f5848b2c86;" +
                "60c036aa601345f5848b1e09;" +
                "60c03579b5aaff19510a98e6;" +
                "60c034fe601345f5848b1cde;" +
                "60c012ae601345f5848b0c85;" +
                "60bf509b60134588fd67457b;" +
                "60bf297f445de40f5f0ff880;" +
                "60bf133a60134588fd671d4e;" +
                "60be2ff3445de40f5f0fac60;" +
                "60bdb12e60134588fd6699dc;" +
                "60bd638a1aead2ba532a9ada;" +
                "60bc57c7b5aaffa4ae5c5ad9;" +
                "60bc44d4b5aaffa4ae5c54aa;" +
                "60bc44ac60134588fd66432e;" +
                "60bc31c1b5aaffa4ae5c4ea5;" +
                "60bc316c60134588fd663d73;" +
                "60bc254a1aead2ba532a6102;" +
                "60bc22941aead2ba532a604d;" +
                "60bb3af460134588fd662129;" +
                "60bb354760134588fd661fdb;" +
                "60bb28d660134588fd661caa;" +
                "60b9ce1fb5aaffa4ae5be57b;" +
                "60b9a232445de40f5f0e8a4f;" +
                "60b99fb71aead2ba5329e2e4;" +
                "60b99eab60134588fd65bcfe;" +
                "60b99ba3b5aaffa4ae5bcd47;" +
                "60b99589b5aaffa4ae5bca47;" +
                "60b995101aead2ba5329ddb6;" +
                "60b991d6445de40f5f0e823b;" +
                "60b8d50360134588fd659b0c;" +
                "60b832ab1aead2740c93e605;" +
                "60b6ea2e445de4c03b613044;" +
                "60b6e8b26013453b1a41a464;" +
                "60b6eb531aead2740c937a11;" +
                "60b631a4b5aaff54fd3ff05b;" +
                "60b63126b5aaff54fd3ff037;" +
                "60b6311d445de4c03b6115b3;" +
                "60b5f193b5aaffd5afee90e1;" +
                "60b5e6071aead2f2a54d6eba;" +
                "60b5e4e4601345e3ef0d5bec;" +
                "60b5e4401aead2f2a54d6da0;" +
                "60b5e306b5aaffd5afee8876;" +
                "60b5e19d445de46ceafbbaa2;" +
                "60b5e0aa1aead2f2a54d6b8f;" +
                "60b5d19bb5aaffd5afee7f2e;" +
                "60b5d173601345e3ef0d51e3;" +
                "60b5d059b5aaffd5afee7e88;" +
                "60b4641f445de46ceafb3d73;" +
                "60b450361aead2f2a54ce586;" +
                "60b35c41445de46ceafb103b;" +
                "60b326d61aead2f2a54cb4c4;" +
                "60a3269db5aaff370b75627c;" +
                "60a322ee6013452df56af4b2;" +
                "609e36321aead20910012968;" +
                "609d074d1aead2952e83aa17;" +
                "609d0657601345c23baa3354;" +
                "609cd514b5aaffb8c6003b43;" +
                "609b313db5aaffb8c6ff7fd5;" +
                "6099275c60134510367830d9;" +
                "6074206d1aead2adab01319b;" +
                "605062b0b5aaff30d0ff1c17;" +
                "60504b5d445de4ba4ab5c741;" +
                "604f2f67445de4ba4ab5873e;" +
                "604dc8691aead2b70e0d26fd;" +
                "604c4d2bb5aaffbc870e6233;" +
                "60481f681aead20bddb33a74;" +
                "60438052b5aaff05f6a0a91a;" +
                "604029adb5aaff05f6a00130;" +
                "603dfa48601345588b0dd29e;" +
                "603da32fb5aaffbad5f7d763;" +
                "6000554cb5aaff8e3ed7fdd5;" +
                "5ffee50db5aaff8e3ed7c7a0";
        List<String> orderIds = Lists.newArrayList(str.split(";"));
        Scanner scanner = new Scanner(System.in);
        String orderJson = scanner.nextLine();
        List<Map> orderInfos = JsonUtils.toObj(orderJson, new TypeReference<List<Map>>() {
        });
        List<Map> orders = Lists.newArrayList();
        for (Map order : orderInfos) {
            String orderId = (String) order.get("order_id");
            String contactName = (String) order.get("contact_name");
            String userName = (String) order.get("user_name");
            if (orderIds.contains(orderId) && contactName.equals(userName)) {
                orders.add(order);
            }
            String callBackData = (String) order.get("callback_data");
            Map<String, Object> data = JsonUtils.toObj(callBackData, Map.class);
            Map<String, Object> thirdInfo = (Map<String, Object>) data.get("third_info");
            if (thirdInfo == null) {
                continue;
            }
            thirdInfo.put("passenger_unit_id", thirdInfo.get("unit_id"));
            thirdInfo.put("passenger_user_id", thirdInfo.get("user_id"));
            System.out.println(JsonUtils.toJson(data));
        }
        System.out.println(orders.size());
    }

    private static void push() {
        Scanner scanner = new Scanner(System.in);
        String orderJson = scanner.nextLine();
        List<Map> orderInfos = JsonUtils.toObj(orderJson, new TypeReference<List<Map>>() {
        });
        Date day0601 = DateUtils.toDate("2021-06-01 00:00:00");
        List<Map> orders = Lists.newArrayList();
        for (Map order : orderInfos) {
            String orderId = (String) order.get("order_id");
            String callBackData = (String) order.get("callback_data");
            Map<String, Object> data = JsonUtils.toObj(callBackData, Map.class);
            Map<String, Object> ordeInfo = (Map<String, Object>) data.get("order_info");
            String createTime = (String) ordeInfo.get("create_time");
            if (DateUtils.toDate(createTime).compareTo(day0601) >= 0) {
                orders.add(order);
                String result = HttpClientUtils.postBody("http://open-plus.fenbeitong.com/openapi/customize/reimburse/push/5efc1d7e06a5c30942c5efb3", callBackData, null);
                System.out.println("推送订单 " + orderId + " " + result);
            }
        }
        System.out.println(orders.size());
    }

    public static void  testPrice(){
        Map transformMap = Maps.newHashMap();
        transformMap.put("id","111");
        FuncAirOrderServiceImpl.AirOrderPirceInfo pirceInfo = new FuncAirOrderServiceImpl.AirOrderPirceInfo();
        pirceInfo.setTotalPrice(new BigDecimal("0"));
        pirceInfo.setOrderPrice(new BigDecimal("0"));
        pirceInfo.setCompanyTotalPay(new BigDecimal("0"));
        pirceInfo.setPersonalTotalPay(new BigDecimal("0"));
        pirceInfo.setFee(new BigDecimal("0"));
        pirceInfo.setTaxes(new BigDecimal("0"));
        pirceInfo.setTicketPrice(new BigDecimal("0"));
        pirceInfo.setRedEnvelope(new BigDecimal("0"));
        pirceInfo.setAirPortFuelTax(new BigDecimal("0"));
        pirceInfo.setCouponAmount(new BigDecimal("0"));
        pirceInfo.setAirPortFee(new BigDecimal("0"));
        pirceInfo.setFuelFee(new BigDecimal("0"));
        pirceInfo.setInsurancePrice(new BigDecimal("0"));
        pirceInfo.setChangeFee(new BigDecimal("0"));
        pirceInfo.setChangeServiceFee(new BigDecimal("0"));
        pirceInfo.setChangeUpgradePrice(new BigDecimal("0"));
        pirceInfo.setRefundFee(new BigDecimal("0"));
        pirceInfo.setRefundServiceFee(new BigDecimal("0"));
        pirceInfo.setDiscount(new BigDecimal("0"));
        transformMap.put("price_info", MapUtils.request2map2(pirceInfo));
        System.out.println(JsonUtils.toJson(transformMap));
    }
}
