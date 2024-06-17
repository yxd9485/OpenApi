package com.fenbeitong.openapi.plugin.definition.service.excel;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.definition.dao.AttrSpecDao;
import com.fenbeitong.openapi.plugin.definition.dao.OpenEmployeePrivDao;
import com.fenbeitong.openapi.plugin.definition.entity.AttrSpec;
import com.fenbeitong.openapi.plugin.definition.entity.OpenEmployeePriv;
import com.fenbeitong.openapi.plugin.support.company.service.ICompanyService;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

@ServiceAspect
@Service
@Slf4j
public class ExcelUploadService {

    @Autowired
    private AttrSpecDao attrSpecDao;
    @Autowired
    private OpenEmployeePrivDao openEmployeePrivDao;
    @Autowired
    ICompanyService companyService;

    private final static String EXCEL2003 = "xls";
    private final static String EXCEL2007 = "xlsx";
    private final static Long INIT_RULE_TYPE = 0l;
    private final static Integer CAR_ROLE_TYPE = 1;
    private final static Integer APPLY_CAR_ROLE_TYLE = 2;
    private final static String AIR_INIT_DATA = "{\"unemployee_air\":false,\"air_other_flag\":false,\"air_priv_flag\":false,\"air_verify_flag\":false,\"air_rule_limit_flag\":false}";
    private final static String INTL_AIR_INIT_DATA = "{\"exceed_buy_type\":1,\"unemployee_air\":false,\"air_priv_flag\":false,\"air_other_flag\":false,\"air_verify_flag\":false,\"air_rule_limit_flag\":false,\"air_order_verify_flag\":false}";
    private final static String HOTEL_INIT_DATA = "{\"unemployee_hotel\":false,\"hotel_other_flag\":false,\"hotel_priv_flag\":false,\"hotel_verify_flag\":false,\"hotel_rule_limit_flag\":false}";
    private final static String TRAIN_INIT_DATA = "{\"unemployee_train\":false,\"train_priv_flag\":false,\"train_other_flag\":false,\"train_verify_flag\":false,\"train_rule_limit_flag\":false}";
    private final static String CAR_INIT_DATA = "{\"car_priv_flag\":false,\"rule_limit_flag\":false,\"exceed_buy_type\":1,\"allow_shuttle\":false,\"personal_pay\":true}";
    private final static String DINNERS_INIT_DATA = "{\"rule_limit_flag\":false,\"rule_priv_flag\":false,\"rule_id\":\"ofaijwf\",\"dinner_policy\":{\"exceed_buy_flag\":3},\"meishi_policy\":{\"exceed_buy_type\":1,\"personal_pay\":true}}";
    private final static String MALL_INIT_DATA = "{\"rule_limit_flag\":false,\"mall_priv_flag\":false,\"exceed_buy_flag\":false}";
    private final static String TAKEAWAY_INIT_DATA = "{\"takeaway_priv_flag\":false,\"takeaway_rule_limit_flag\":false,\"exceed_buy_type\":1,\"personal_pay\":false}";
    private final static String SHANSONG_INIT_DATA = "{\"shansong_priv_flag\":false}";
    private final static String SHUNFENG_INIT_DATA = "{\"shunfeng_priv_flag\":false}";
    private final static String PAYMENT_APPLY_INIT_DATA = "{\"payment_apply_priv_flag\":false}";
    private final static String VIRTUAL_CARD_INIT_DATA = "{\"virtual_card_priv_flag\":false}";
    private final static String MILEAGE_INIT_DATA = "{\"rule_priv_flag\":false}";
    private final static String AUTH_SWITCH_NAME = "关闭";
    private final static String AUTH_RULE_ID = "规则id";
    private final static String AIR_TYPE = "air";
    private final static String AIR_SWITCH = "switch_flag";
    private final static String AIR_NAME = "国内机票预订";
    private final static int AIR_NUM = 3;
    private final static String INTL_AIR_NAME = "国际机票预定";
    private final static String INTL_AIR_TYPE = "intl_air";
    private final static String INTL_AIR_SWITCH = "switch_flag";
    private final static int INTL_AIR_NUM = 13;
    private final static String HOTEL_NAME = "酒店预定";
    private final static String HOTEL_TYPE = "hotel";
    private final static String HOTEL_SWITCH = "switch_flag";
    private final static int HOTEL_NUM = 21;
    private final static String TRAIN_NAME = "火车票预定";
    private final static String TRAIN_TYPE = "train";
    private final static String TRAIN_SWITCH = "switch_flag";
    private final static int TRAIN_NUM = 31;
    private final static String CAR_NAME = "用车";
    private final static String CAR_TYPE = "car";
    private final static String CAR_SWITCH = "car_priv_flag";
    private final static String CAR_RULE_ID = "用车规则id";
    private final static String CAR_APPLY_RULE_ID = "申请用车规则id";
    private final static int CAR_NUM = 41;
    private final static String DINNERS_NAME = "用餐";
    private final static String DINNERS_TYPE = "dinners";
    private final static String DINNERS_SWITCH = "rule_priv_flag";
    private final static String DINNERS_EXCEED_BUY_TYPE = "exceed_buy_type";
    private final static String DINNERS_PERSONAL_PAY_REPLACE = "meishi_policy";
    private final static String DINNERS_PERSONAL_PAY = "personal_pay";
    private final static int DINNERS_NUM = 48;
    private final static int DINNERS_EXCEED_NUM = 51;
    private final static int DINNERS_PERSON_PAY_NUM = 52;
    private final static String MALL_NAME = "采购";
    private final static String MALL_TYPE = "mall";
    private final static String MALL_SWITCH = "mall_priv_flag";
    private final static int MALL_NUM = 53;
    private final static String TAKEAWAY_NAME = "外卖";
    private final static String TAKEAWAY_TYPE = "takeaway";
    private final static String TAKEAWAY_SWITCH = "takeaway_priv_flag";
    private final static String TAKEAWAY_RULE_ID = "takeaway_rule_id";
    private final static int TAKEAWAY_NUM = 58;
    private final static String SHANSONG_NAME = "闪送";
    private final static String SHANSONG_TYPE = "shansong";
    private final static String SHANSONG_SWITCH = "shansong_priv_flag";
    private final static int SHANSONG_NUM = 63;
    private final static String SHUNFENG_NAME = "快递";
    private final static String SHUNFENG_TYPE = "shunfeng";
    private final static String SHUNFENG_SWITCH = "shunfeng_priv_flag";
    private final static int SHUNFENG_NUM = 64;
    private final static int PAYMENT_APPLY_NUM = 65;
    private final static String PAYMENT_APPLY_NAME = "付款";
    private final static String PAYMENT_APPLY_TYPE = "payment_apply";
    private final static String PAYMENT_APPLY_SWITCH = "payment_apply_priv_flag";
    private final static int VIRTUAL_CARD_NUM = 66;
    private final static String VIRTUAL_CARD_NAME = "虚拟卡";
    private final static String VIRTUAL_CARD_TYPE = "virtual_card";
    private final static String VIRTUAL_CARD_SWITCH = "virtual_card_priv_flag";
    private final static int MILEAGE_NUM = 67;
    private final static String MILEAGE_NAME = "里程";
    private final static String MILEAGE_TYPE = "mileage";
    private final static String MILEAGE_SWITCH = "rule_priv_flag";


    private static final ThreadLocal<Map<Integer, Object>> threadLocal = new ThreadLocal<>();


    public String readExcel(MultipartFile file, String appId, int authNum, Boolean initDataFlag) {
        String fileName = file.getOriginalFilename();
        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
            return "上传文件格式不正确";
        }
        //加载数据
        Map<String, AttrSpec> attrSpecMap = new HashMap<>();
        attrSpecDao.listAttrSpec(new HashMap<String, Object>()).stream().forEach(attrSpec ->
                attrSpecMap.put(attrSpec.getSpecType() + "-" + attrSpec.getAttrValueName() + "-" + attrSpec.getValueName(), attrSpec));

        //创建工作簿
        Workbook workbook = null;
        Cell cell = null;
        try {
            //生成字节流
            InputStream is = file.getInputStream();
            //判断版本
            if (fileName.endsWith(EXCEL2007)) {
                workbook = new XSSFWorkbook(is);
            }
            if (fileName.endsWith(EXCEL2003)) {
                workbook = new HSSFWorkbook(is);
            }
            //读取
            if (workbook != null) {
                Sheet sheet = workbook.getSheetAt(0);
                //getFirstRowNum()获取第一行
                //getLastRowNum()获取最后一行
                Integer[] authTypeArr = new Integer[100];
                List<OpenEmployeePriv> resultList = new ArrayList<>();
                HashMap<String, JSONObject> airJsonMap = new HashMap<>();
                HashMap<String, JSONObject> intlAirJsonMap = new HashMap<>();
                HashMap<String, JSONObject> hotelJsonMap = new HashMap<>();
                HashMap<String, JSONObject> trainJsonMap = new HashMap<>();
                HashMap<String, JSONObject> carJsonMap = new HashMap<>();
                HashMap<String, JSONObject> dinnersJsonMap = new HashMap<>();
                HashMap<String, JSONObject> mallJsonMap = new HashMap<>();
                HashMap<String, JSONObject> takeawayJsonMap = new HashMap<>();
                HashMap<String, JSONObject> shansongJsonMap = new HashMap<>();
                HashMap<String, JSONObject> shunfengJsonMap = new HashMap<>();
                HashMap<String, JSONObject> paymentApplyJsonMap = new HashMap<>();
                HashMap<String, JSONObject> virtualCardJsonMap = new HashMap<>();
                HashMap<String, JSONObject> mileageJsonMap = new HashMap<>();
                //根据公司ID获取公司规则集合信息
                Map companyRules = companyService.getCompanyRules(appId);
                for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    int firstCellNum = row.getFirstCellNum();// 获取所在行的第一个行号
                    //int lastCellNum = row.getLastCellNum();// 获取所在行的最后一个行号
                    if (row.getPhysicalNumberOfCells() != 0) {
                        for (int j = firstCellNum; j <= firstCellNum + 3 + authNum; j++) {
                            if (i == 0 && j >= 4) {
                                cell = row.getCell(j);
                                String cellValue = getCellValue(cell);
                                if (StringUtils.isNotEmpty(cellValue)) {
                                    authTypeArr[j - 4] = Integer.valueOf(cellValue);
                                }
                            }
                            if (j >= 4) {
                                if (i >= AIR_NUM - 1 && i < INTL_AIR_NUM - 1) {//#组装air数据
                                    String result = assemblyData(row, i, j, airJsonMap, attrSpecMap, AIR_TYPE, AIR_NAME, authTypeArr, companyRules);
                                    if (result != null) {
                                        return result;
                                    }
                                } else if (i >= INTL_AIR_NUM - 1 && i < HOTEL_NUM - 1) {//#组装intl_ail数据
                                    String result = assemblyData(row, i, j, intlAirJsonMap, attrSpecMap, INTL_AIR_TYPE, INTL_AIR_NAME, authTypeArr, companyRules);
                                    if (result != null) {
                                        return result;
                                    }
                                } else if (i >= HOTEL_NUM - 1 && i < TRAIN_NUM - 1) {//#组装hotel数据
                                    String result = assemblyData(row, i, j, hotelJsonMap, attrSpecMap, HOTEL_TYPE, HOTEL_NAME, authTypeArr, companyRules);
                                    if (result != null) {
                                        return result;
                                    }
                                } else if (i >= TRAIN_NUM - 1 && i < CAR_NUM - 1) {//#组装train数据
                                    String result = assemblyData(row, i, j, trainJsonMap, attrSpecMap, TRAIN_TYPE, TRAIN_NAME, authTypeArr, companyRules);
                                    if (result != null) {
                                        return result;
                                    }
                                } else if (i >= CAR_NUM - 1 && i < DINNERS_NUM - 1) {//#组装car数据
                                    String result = assemblyData(row, i, j, carJsonMap, attrSpecMap, CAR_TYPE, CAR_NAME, authTypeArr, companyRules);
                                    if (result != null) {
                                        return result;
                                    }
                                } else if (i >= DINNERS_NUM - 1 && i < MALL_NUM - 1) {//#组装dinners数据
                                    String result = assemblyData(row, i, j, dinnersJsonMap, attrSpecMap, DINNERS_TYPE, DINNERS_NAME, authTypeArr, companyRules);
                                    if (result != null) {
                                        return result;
                                    }
                                } else if (i >= MALL_NUM - 1 && i < TAKEAWAY_NUM - 1) {//#组装mall数据
                                    String result = assemblyData(row, i, j, mallJsonMap, attrSpecMap, MALL_TYPE, MALL_NAME, authTypeArr, companyRules);
                                    if (result != null) {
                                        return result;
                                    }
                                } else if (i >= TAKEAWAY_NUM - 1 && i < SHANSONG_NUM - 1) {//#组装takeaway数据
                                    String result = assemblyData(row, i, j, takeawayJsonMap, attrSpecMap, TAKEAWAY_TYPE, TAKEAWAY_NAME, authTypeArr, companyRules);
                                    if (result != null) {
                                        return result;
                                    }
                                } else if (i >= SHANSONG_NUM - 1 && i < SHUNFENG_NUM - 1) {//#组装shansong数据
                                    String result = assemblyData(row, i, j, shansongJsonMap, attrSpecMap, SHANSONG_TYPE, SHANSONG_NAME, authTypeArr, companyRules);
                                    if (result != null) {
                                        return result;
                                    }
                                } else if (i >= SHUNFENG_NUM - 1 && i < PAYMENT_APPLY_NUM - 1) {//#组装shunfeng数据
                                    String result = assemblyData(row, i, j, shunfengJsonMap, attrSpecMap, SHUNFENG_TYPE, SHUNFENG_NAME, authTypeArr, companyRules);
                                    if (result != null) {
                                        return result;
                                    }
                                } else if (i >= PAYMENT_APPLY_NUM - 1 && i < VIRTUAL_CARD_NUM - 1) {//#组装payment_apply数据
                                    String result = assemblyData(row, i, j, paymentApplyJsonMap, attrSpecMap, PAYMENT_APPLY_TYPE, PAYMENT_APPLY_NAME, authTypeArr, companyRules);
                                    if (result != null) {
                                        return result;
                                    }
                                } else if (i >= VIRTUAL_CARD_NUM - 1 && i < MILEAGE_NUM - 1) {//#组装virtual_card数据
                                    String result = assemblyData(row, i, j, virtualCardJsonMap, attrSpecMap, VIRTUAL_CARD_TYPE, VIRTUAL_CARD_NAME, authTypeArr, companyRules);
                                    if (result != null) {
                                        return result;
                                    }
                                } else if (i >= MILEAGE_NUM - 1) {//#组装mileage数据
                                    String result = assemblyData(row, i, j, mileageJsonMap, attrSpecMap, MILEAGE_TYPE, MILEAGE_NAME, authTypeArr, companyRules);
                                    if (result != null) {
                                        return result;
                                    }
                                }
                            }
                        }
                    }
                }
                if (initDataFlag != null && initDataFlag) {
                    initDatForFirst(appId, INIT_RULE_TYPE, AIR_TYPE, resultList, AIR_INIT_DATA);
                    initDatForFirst(appId, INIT_RULE_TYPE, INTL_AIR_TYPE, resultList, INTL_AIR_INIT_DATA);
                    initDatForFirst(appId, INIT_RULE_TYPE, HOTEL_TYPE, resultList, HOTEL_INIT_DATA);
                    initDatForFirst(appId, INIT_RULE_TYPE, TRAIN_TYPE, resultList, TRAIN_INIT_DATA);
                    initDatForFirst(appId, INIT_RULE_TYPE, CAR_TYPE, resultList, CAR_INIT_DATA);
                    initDatForFirst(appId, INIT_RULE_TYPE, DINNERS_TYPE, resultList, DINNERS_INIT_DATA);
                    initDatForFirst(appId, INIT_RULE_TYPE, MALL_TYPE, resultList, MALL_INIT_DATA);
                    initDatForFirst(appId, INIT_RULE_TYPE, TAKEAWAY_TYPE, resultList, TAKEAWAY_INIT_DATA);
                    initDatForFirst(appId, INIT_RULE_TYPE, SHANSONG_TYPE, resultList, SHANSONG_INIT_DATA);
                    initDatForFirst(appId, INIT_RULE_TYPE, SHUNFENG_TYPE, resultList, SHUNFENG_INIT_DATA);
                    initDatForFirst(appId, INIT_RULE_TYPE, PAYMENT_APPLY_TYPE, resultList, PAYMENT_APPLY_INIT_DATA);
                    initDatForFirst(appId, INIT_RULE_TYPE, VIRTUAL_CARD_TYPE, resultList, VIRTUAL_CARD_INIT_DATA);
                    initDatForFirst(appId, INIT_RULE_TYPE, MILEAGE_TYPE, resultList, MILEAGE_INIT_DATA);
                }
                convertData(airJsonMap, appId, AIR_SWITCH, AIR_TYPE, resultList);
                convertData(intlAirJsonMap, appId, INTL_AIR_SWITCH, INTL_AIR_TYPE, resultList);
                convertData(hotelJsonMap, appId, HOTEL_SWITCH, HOTEL_TYPE, resultList);
                convertData(trainJsonMap, appId, TRAIN_SWITCH, TRAIN_TYPE, resultList);
                convertData(carJsonMap, appId, CAR_SWITCH, CAR_TYPE, resultList);
                convertData(dinnersJsonMap, appId, DINNERS_SWITCH, DINNERS_TYPE, resultList);
                convertData(mallJsonMap, appId, MALL_SWITCH, MALL_TYPE, resultList);
                convertData(takeawayJsonMap, appId, TAKEAWAY_SWITCH, TAKEAWAY_TYPE, resultList);
                convertData(shansongJsonMap, appId, SHANSONG_SWITCH, SHANSONG_TYPE, resultList);
                convertData(shunfengJsonMap, appId, SHUNFENG_SWITCH, SHUNFENG_TYPE, resultList);
                convertData(paymentApplyJsonMap, appId, PAYMENT_APPLY_SWITCH, PAYMENT_APPLY_TYPE, resultList);
                convertData(virtualCardJsonMap, appId, VIRTUAL_CARD_SWITCH, VIRTUAL_CARD_TYPE, resultList);
                convertData(mileageJsonMap, appId, MILEAGE_SWITCH, MILEAGE_TYPE, resultList);
                openEmployeePrivDao.saveList(resultList);
            }
        } catch (Exception e) {
            log.info(String.format("parse excel exception!"), e);
            return String.format("parse excel exception!");
        } finally {
            threadLocal.remove();
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    log.info(String.format("parse excel exception!"), e);
                    return String.format("parse excel exception!");
                }
            }
        }
        return "upload success";

    }


    public int deleteAuthByCondition(String appId, String scenes, String roleTypes) {
        Map<String, Object> conditon = new HashMap<>();
        if (StringUtils.isNotEmpty(appId)) {
            conditon.put("companyId", appId);
        } else {
            return 0;
        }
        if (StringUtils.isNotEmpty(scenes) && !Arrays.asList(scenes.split(",")).isEmpty()) {
            conditon.put("scenes", Arrays.asList(scenes.split(",")));
        }
        if (StringUtils.isNotEmpty(roleTypes) && !Arrays.asList(roleTypes.split(",")).isEmpty()) {
            conditon.put("roleTypes", Arrays.asList(roleTypes.split(",")));
        }
        return openEmployeePrivDao.deleteOpenEmployeePriv(conditon);
    }


    private OpenEmployeePriv getOpenEmployeePriv(String companyId, String sceneType, Long roleType, String jsonData) {
        return new OpenEmployeePriv().builder()
                .companyId(companyId)
                .createTime(new Date())
                .roleType(roleType)
                .privJsonData(jsonData)
                .scene(sceneType)
                .updateTime(new Date())
                .build();
    }

    private String assemblyData(Row row, int i, int j, Map<String, JSONObject> map, Map<String, AttrSpec> attrSpecMap, String scenceType, String scenceName, Integer[] authTypeArr, Map ruleMap) throws Exception {
        int firstCellNum = row.getFirstCellNum();
        Cell cell = row.getCell(j);
        String cellValue = getCellValue(cell);
        Cell cellFirst = row.getCell(firstCellNum);
        String firstCellValue = getCellValue(cellFirst);
        String jsonKey = null;
        Object jsonValue = null;
        if (firstCellValue.contains(AUTH_RULE_ID)) {
            Cell cellSecond = row.getCell(firstCellNum + 1);
            String secondCellValue = getCellValue(cellSecond);
            jsonKey = attrSpecMap.get(scenceType + "-" + firstCellValue + "-" + secondCellValue) != null ? attrSpecMap.get(scenceType + "-" + firstCellValue + "-" + secondCellValue).getAttrCd() : null;
            if (jsonKey.contains("ids") && firstCellValue.equals(CAR_RULE_ID) && StringUtils.isNotEmpty(cellValue)) {
                jsonKey = "rule_ids";
                boolean carRuleBcool = checkRuleId(ruleMap, cellValue, "taxiRuleList");
                if (!carRuleBcool) {
                    return "用车规则ID错误";
                }
                JSONArray jsonArray = new JSONArray();
                RuleIdObject ruleIdObject = new RuleIdObject();
                ruleIdObject.setRule_id(new int[]{Integer.valueOf(cellValue)});
                ruleIdObject.setType(CAR_ROLE_TYPE);
                jsonArray.add(ruleIdObject);
                jsonValue = jsonArray;
            } else if (jsonKey.contains("ids") && firstCellValue.equals(CAR_APPLY_RULE_ID) && StringUtils.isNotEmpty(cellValue)) {
                JSONArray jsonArray = new JSONArray();
                JSONArray jsonArray1 = map.get(String.valueOf(authTypeArr[j - 4])).getJSONArray("rule_ids");
                if (jsonArray1 != null && jsonArray1.size() > 0) {
                    jsonArray = jsonArray1;
                }
                boolean applyCarRuleBool = checkRuleId(ruleMap, cellValue, "taxiApproveRuleList");
                if (!applyCarRuleBool) {
                    return "申请用车规则ID错误";
                }
                if (jsonArray.size() <= 1) {
                    RuleIdObject ruleIdObject = new RuleIdObject();
                    ruleIdObject.setRule_id(new int[]{Integer.valueOf(cellValue)});
                    ruleIdObject.setType(APPLY_CAR_ROLE_TYLE);
                    jsonArray.add(ruleIdObject);
                }
                jsonValue = jsonArray;
            } else {
                if (StringUtils.isNotEmpty(jsonKey) && jsonKey.equals(TAKEAWAY_RULE_ID) && StringUtils.isNotEmpty(cellValue)) {
                    //判断外卖规则ID
                    boolean takeawayRuleBool = checkRuleId(ruleMap, cellValue, "takeawayRuleList");
                    if (!takeawayRuleBool) {
                        return "外卖规则ID错误";
                    }
                    jsonValue = Integer.valueOf(cellValue);


                } else {
                    //其他场景的规则ID
                    if (AIR_NAME.equals(scenceName)) {//国内机票
                        boolean airRuleBool = checkRuleId(ruleMap, cellValue, "airRuleList");
                        if (!airRuleBool) {
                            return "国内机票规则ID错误";
                        }

                    } else if (INTL_AIR_NAME.equals(scenceName)) {
                        boolean intlAirRuleBool = checkRuleId(ruleMap, cellValue, "intlAirRuleList");
                        if (!intlAirRuleBool) {
                            return "国际机票规则ID错误";
                        }
                    } else if (TRAIN_NAME.equals(scenceName)) {
                        boolean trainRuleBool = checkRuleId(ruleMap, cellValue, "trainRuleList");
                        if (!trainRuleBool) {
                            return "火车规则ID错误";
                        }
                    } else if (HOTEL_NAME.equals(scenceName)) {
                        boolean hotelRuleBool = checkRuleId(ruleMap, cellValue, "hotelRuleList");
                        if (!hotelRuleBool) {
                            return "酒店规则ID错误";
                        }
                    } else if (DINNERS_NAME.equals(scenceName)) {
                        boolean dinnerRuleBool = checkRuleId(ruleMap, cellValue, "dinnerRuleList");
                        if (!dinnerRuleBool) {
                            return "用餐规则ID错误";
                        }
                    } else if (MALL_NAME.equals(scenceName)) {
                        boolean mallRuleBool = checkRuleId(ruleMap, cellValue, "mallRuleList");
                        if (!mallRuleBool) {
                            return "采购规则ID错误";
                        }
                    }
                    jsonValue = cellValue;
                }
            }
        } else {
            jsonKey = attrSpecMap.get(scenceType + "-" + firstCellValue + "-" + cellValue) != null ? attrSpecMap.get(scenceType + "-" + firstCellValue + "-" + cellValue).getAttrCd() : null;
            if (StringUtils.isNotEmpty(jsonKey) && (i + 1) == DINNERS_EXCEED_NUM) {
                //String jsonKeyNew = DINNERS_EXCEED_BUY_FLAG_REPLACE;
                //JSONObject jsonObject = new JSONObject(true);
                jsonValue = attrSpecMap.get(scenceType + "-" + firstCellValue + "-" + cellValue) != null ? convertString(attrSpecMap.get(scenceType + "-" + firstCellValue + "-" + cellValue).getValue()) : null;
                if (threadLocal.get() != null) {
                    Map m = (Map) threadLocal.get();
                    m.put(authTypeArr[j - 4], jsonValue);
                } else {
                    Map<Integer, Object> result = new HashMap<>();
                    result.put(authTypeArr[j - 4], jsonValue);
                    threadLocal.set(result);
                }
                //jsonObject.put(jsonKey, jsonValue);
                jsonKey = null; //jsonKeyNew;
                jsonValue = null; //jsonObject;
                cellValue = "";
            } else if (StringUtils.isNotEmpty(jsonKey) && (i + 1) == DINNERS_PERSON_PAY_NUM && jsonKey.equals(DINNERS_PERSONAL_PAY)) {
                String jsonKeyNew = DINNERS_PERSONAL_PAY_REPLACE;
                JSONObject jsonObject = new JSONObject(true);
                jsonValue = attrSpecMap.get(scenceType + "-" + firstCellValue + "-" + cellValue) != null ? convertString(attrSpecMap.get(scenceType + "-" + firstCellValue + "-" + cellValue).getValue()) : null;

                Map<Integer, Object> s = (Map) threadLocal.get();
                if (!ObjectUtils.isEmpty(s)) {
                    jsonObject.put(DINNERS_EXCEED_BUY_TYPE, convertString(String.valueOf(s.get(authTypeArr[j - 4]))));
                } else {
                    jsonObject.put(DINNERS_EXCEED_BUY_TYPE, 1);
                }
                jsonObject.put(jsonKey, jsonValue);
                jsonKey = jsonKeyNew;
                jsonValue = jsonObject;
            } else {
                jsonValue = attrSpecMap.get(scenceType + "-" + firstCellValue + "-" + cellValue) != null ? convertString(attrSpecMap.get(scenceType + "-" + firstCellValue + "-" + cellValue).getValue()) : null;
            }
        }
        if (firstCellValue.equals(scenceName) && cellValue.equals(AUTH_SWITCH_NAME)) {
        } else {
            if (map.containsKey(String.valueOf(authTypeArr[j - 4]))) {
                if (jsonKey != null && jsonValue != null && !jsonValue.equals("")) {
                    map.get(String.valueOf(authTypeArr[j - 4])).put(jsonKey, jsonValue);
                } else if (StringUtils.isNotEmpty(cellValue)) {
                    return ("第" + (i + 1) + "行 ,第" + (j + 1) + "列, jsonkey: " + jsonKey + " jsonValue: " + jsonValue + " 填写有误, 请选择对应选项");
                }
            } else {
                if (jsonKey != null && jsonValue != null && !jsonValue.equals("")) {
                    JSONObject jsonObject = new JSONObject(true);
                    jsonObject.put(jsonKey, jsonValue);
                    map.put(String.valueOf(authTypeArr[j - 4]), jsonObject);
                } else if (StringUtils.isNotEmpty(cellValue)) {
                    return ("第" + (i + 1) + "行 , 第" + (j + 1) + "列, jsonkey: " + jsonKey + " jsonValue: " + jsonValue + " 填写有误, 请选择对应选项");
                }
            }
        }
        return null;
    }

    private void convertData(Map<String, JSONObject> map, String companyId, String scenceSwitchKey, String scenceType, List<OpenEmployeePriv> openEmployeePrivs) {
        for (String key : map.keySet()) {
            if (map.get(key).containsKey(scenceSwitchKey)) {
                openEmployeePrivs.add(getOpenEmployeePriv(companyId, scenceType, Long.valueOf(key), map.get(key).toJSONString()));
            }
        }

    }

    private void initDatForFirst(String companyId, Long roleType, String scenceType, List<OpenEmployeePriv> openEmployeePrivs, String jsonData) {
        openEmployeePrivs.add(new OpenEmployeePriv().builder()
                .companyId(companyId)
                .createTime(new Date())
                .roleType(roleType)
                .privJsonData(jsonData)
                .scene(scenceType)
                .updateTime(new Date())
                .build());
    }


    private String getCellValue(Cell cell) {

        if (cell == null) {
            return "";
        }
        if ("NUMERIC".equals(cell.getCellType().name())) {
            return new BigDecimal(cell.getNumericCellValue()).toString();
        } else if ("STRING".equals(cell.getCellType().name()))
            return StringUtils.trimToEmpty(cell.getStringCellValue());
        else if ("FORMULA".equals(cell.getCellType().name())) {
            return StringUtils.trimToEmpty(cell.getCellFormula());
        } else if ("BLANK".equals(cell.getCellType().name())) {
            return "";
        } else if ("BOOLEAN".equals(cell.getCellType().name())) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if ("ERROR".equals(cell.getCellType().name())) {
            return "ERROR";
        } else {
            return cell.toString().trim();
        }
    }

    private Object convertString(String value) {
        if (value.contains("true") || value.contains("false")) {
            return Boolean.valueOf(value);
        } else if (value.contains("0") || value.contains("1") || value.contains("2") || value.contains("3") || value.contains("4") || value.contains("5"))
            return Integer.valueOf(value);
        else {
            return value;
        }
    }

    @Data
    private class RuleIdObject {
        int[] rule_id;
        int type;
    }

    public boolean checkRuleId(Map<String, Object> ruleMap, String cellValue, String mapKey) {
        if (ObjectUtils.isEmpty(ruleMap)) {
            return false;
        }
        List<Map> ruleList = (List) ruleMap.get(mapKey);
        if (!ObjectUtils.isEmpty(ruleList)) {
            List<String> ruleIds = Lists.newArrayList();
            ruleList.stream().forEach(e -> ruleIds.add((String) e.get("ruleId")));
            if (StringUtils.isNotBlank(cellValue)) {
                if (!ruleIds.contains(cellValue)) {//不包含规则ID，需要错误提示
                    return false;
                }
            }
        }
        return true;
    }


}


