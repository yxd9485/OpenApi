CREATE TABLE `open_bill_detail_record` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `BILL_ID` varchar(32) NOT NULL COMMENT '账单Id',
  `BILL_NAME` varchar(32) DEFAULT NULL COMMENT '账单名称',
  `BILL_NO` varchar(64) NOT NULL COMMENT '账单编号',
  `COMPANY_ID` varchar(64) NOT NULL COMMENT '公司Id',
  `ORDER_CATEGORY` int(8) NOT NULL COMMENT '订单场景类型',
  `ORDER_ID` varchar(32) NOT NULL COMMENT '订单号',
  `BILL_BEGIN_DATE` timestamp  COMMENT '账单的开始日期',
  `BILL_END_DATE` timestamp  COMMENT '账单的结束日期',
  `ORDER_CREATE_TIME` timestamp COMMENT '订单的创建时间 预订/退票/下单日期时间',
  `TICKET_NO` varchar(64) DEFAULT NULL COMMENT '票号',
  `TICKET_STSTUS` varchar(50) DEFAULT NULL COMMENT '机票/订单/支付状态',
  `EMPLOYEE_ID` varchar(200) DEFAULT NULL COMMENT '预订人/下单人/用餐人id',
  `EMPLOYEE_NAME` varchar(50) DEFAULT NULL COMMENT '预订/下单/用餐人名称',
  `EMPLOYEE_PHONE` varchar(50) DEFAULT NULL COMMENT '预订/下单/用餐人手机号',
  `DEPARTMENT_NAME` varchar(50) DEFAULT NULL COMMENT '预定/下单/用餐人直属部门',
  `CUSTOMER_NAME` varchar(200) DEFAULT NULL COMMENT '旅客/乘车人/使用人/发件人/交易人姓名',
  `CUSTOMER_PHONE` varchar(50) DEFAULT NULL COMMENT '旅客/乘车人/发件人手机号/使用人/交易人手机号',
  `CUSTOMER_DEP` varchar(50) DEFAULT NULL COMMENT '旅客直属部门/乘车人部门/使用人直属部门',
  `FLIGHT_NO` varchar(50) DEFAULT NULL COMMENT '航班号/车次/服务类型/门店地址/收件人手机号',
  `DEPARTURE_NAME` varchar(200) DEFAULT NULL COMMENT '出发城市/下单城市',
  `FROM_STATION_NAME` varchar(50) DEFAULT NULL COMMENT '出发车站/出发地/发件地址',
  `ARRIVAL_NAME` varchar(50) DEFAULT NULL COMMENT '到达城市/入住城市/目的城市/用餐城市',
  `TO_STATION_NAME` varchar(50) DEFAULT NULL COMMENT '到达车站/目的地/门店地址/收件地址',
  `DEPARTURE_TIME` datetime DEFAULT NULL COMMENT '起飞/入住/出发/行程开始时间',
  `BACK_DEPARTURE_TIME` datetime DEFAULT NULL COMMENT '到达/离店/支付/完成时间',
  `SALE_PRICE` decimal(10,2) DEFAULT NULL COMMENT '票销售价/平均客房单价/车票单价/商品单价',
  `AIRPORT_FEE` decimal(10,2) DEFAULT NULL COMMENT '机场建设费',
  `FUEL_FEE` decimal(10,2) DEFAULT NULL COMMENT '燃油费',
  `TAXES` decimal(10,2) DEFAULT NULL COMMENT '税费',
  `TAXI_PRICE` decimal(10,2) DEFAULT NULL COMMENT '用车金额/采购商品总价',
  `SERVICE_FEE` decimal(10,2) DEFAULT NULL COMMENT '服务费',
  `COMPANY_PAY_PRICE` decimal(10,2) DEFAULT NULL COMMENT '企业支付',
  `PERSONA_PAY_PRICE` decimal(10,2) DEFAULT NULL COMMENT '员工支付/个人支付(含分贝币支付)',
  `ENDORSE_DIFF_FEE` decimal(10,2) DEFAULT NULL COMMENT '改签差价',
  `CHANGE_FEE` decimal(10,2) DEFAULT NULL COMMENT '改签费/改签手续费',
  `UPGRATE_FEE` decimal(10,2) DEFAULT NULL COMMENT '升舱费/火车抢票费/用车调度费',
  `REFUND_EXT_FEE` decimal(10,2) DEFAULT NULL COMMENT '退票费',
  `COUPON_AMOUNT` decimal(10,2) DEFAULT NULL COMMENT '优惠券/折扣',
  `INSURANCE_PRICE` decimal(10,2) DEFAULT NULL COMMENT '保险费/运费',
  `TOTAL_AMOUNT` decimal(10,2) DEFAULT NULL COMMENT '应收总额',
  `SERVICE_FEE_REDUCTION` decimal(10,2) DEFAULT NULL COMMENT '减免金额',
  `RED_COUPON` decimal(10,2) DEFAULT NULL COMMENT '红包券支付',
  `DEPT_COST_ATTRIBUTION_ID` varchar(50) DEFAULT NULL COMMENT '部门费用归属id',
  `DEPT_COST_ATTRIBUTION_NAME` varchar(50) DEFAULT NULL COMMENT '部门费用归属名称',
  `ITEM_COST_ATTRIBUTION_ID` varchar(50) DEFAULT NULL COMMENT '项目费用归属id',
  `ITEM_COST_ATTRIBUTION_NAME` varchar(50) DEFAULT NULL COMMENT '项目费用归属名称',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`ID`),
  KEY `idx_company_id` (`COMPANY_ID`) USING BTREE,
  KEY `idx_bill_no` (`BILL_NO`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单明细数据';

ALTER TABLE `open_bill_detail_record`
MODIFY COLUMN `CHANGE_FEE` decimal(10, 2) NULL DEFAULT NULL COMMENT '改签费' AFTER `ENDORSE_DIFF_FEE`,
ADD COLUMN `CHANGE_EXT_FEE` decimal(10, 2) NULL COMMENT '改签服务费' AFTER `CHANGE_FEE`;