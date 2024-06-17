CREATE TABLE `open_altman_order_config` (
  `ID` int(25) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `company_id` varchar(128) NOT NULL COMMENT '公司ID',
  `scence_type` varchar(100) NOT NULL COMMENT '场景类型air/car/hotel/train/mall/takeaway',
  `order_name` varchar(50) NOT NULL COMMENT '订单名称  万能订单—德邦用车订单',
  `order_snapshot` varchar(50) NOT NULL COMMENT '订单简写  万能订单—德邦用车订单',
  `supplier_id` int(10) NOT NULL COMMENT '供应商ID 3',
  `supplier_name` varchar(20) NOT NULL COMMENT '供应商名称 曹操',
  `order_type_classify` int(20) NOT NULL COMMENT '业务类别ID 100011',
  `order_type_classify_name` varchar(20) NOT NULL COMMENT '业务类别名称 曹操',
  `order_type_name` varchar(20) NOT NULL COMMENT '业务名称 用车',
  `order_type_desc` varchar(20) NOT NULL COMMENT '业务描述 德邦-曹操用车',
  `business_type` int(5) NOT NULL COMMENT '分贝业务类型1 pop 2 托管 3 采销',
  `invoic_provide_status` int(2) DEFAULT NULL COMMENT '发票提供的状态1、提供 0、不提供',
  `scene_invoice_type` int(10) DEFAULT NULL COMMENT '开票的类型 1、专票 2、普票/电子票 27、企业配置',
  `invoice_provide_type` int(10) DEFAULT NULL COMMENT '开票方 1、遵循开票规则 2、回填所选供应商名称',
  `invoice_provide_name` varchar(100) DEFAULT NULL COMMENT '开票方名称 / 遵循开票规则 / 回填所选供应商名称',
  `invoice_provider` varchar(100) DEFAULT NULL COMMENT '发票提供者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`ID`),
  KEY `idx_company_id` (`company_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='万能订单配置表'

INSERT INTO `openapi-dev`.`open_altman_order_config`(`ID`, `company_id`, `scence_type`, `order_name`, `order_snapshot`, `supplier_id`, `supplier_name`, `order_type_classify`, `order_type_classify_name`, `order_type_name`, `order_type_desc`, `business_type`, `invoic_provide_status`, `scene_invoice_type`, `invoice_provide_type`, `invoice_provide_name`, `invoice_provider`, `create_time`, `update_time`) VALUES (1, '5ece078223445f477cc3b9cc', 'car', '万能订单—德邦用车订单', '万能订单—德邦用车订单', 3, '曹操', 100011, '曹操', '用车', '德邦-曹操用车', 1, 0, 27, 2, '曹操', NULL, now(), now());