CREATE TABLE `open_company_voucher_config` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `COMPANY_ID` varchar(50) DEFAULT NULL COMMENT '公司ID',
  `COMPANY_NAME` varchar(200) DEFAULT NULL COMMENT '公司名称',
  `VOUCHER_TYPE` int(1) DEFAULT NULL COMMENT '凭证类型 1:报销单;2:账单;3:对公付款',
  `STRATEGY_ID` bigint(20) DEFAULT NULL COMMENT '策略ID',
  `DESCRIPTION` varchar(50) DEFAULT NULL COMMENT '描述',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='公司凭证配置表';

CREATE TABLE `open_voucher_draft` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `STATUS` int(1) DEFAULT NULL COMMENT '状态 -1:初始化;1:已生效',
  `ACCOUNT_CODE` varchar(64) DEFAULT NULL COMMENT '科目编码',
  `ACCOUNT_NAME` varchar(100) DEFAULT NULL COMMENT '科目名称',
  `VOUCHER_TYPE` int(1) DEFAULT NULL COMMENT '凭证分录类型 1:业务线借方;2:业务线进项税;3:服务费借方;4:服务费进项税;5:贷方科目',
  `VOUCHER_TYPE_NAME` varchar(64) DEFAULT NULL COMMENT '凭证分录类型名称',
  `SUMMARY` varchar(100) DEFAULT NULL COMMENT '摘要信息',
  `DEBIT` decimal(20,2) DEFAULT NULL COMMENT '借方金额',
  `CREDIT` decimal(20,2) DEFAULT NULL COMMENT '贷方金额',
  `EMPLOYEE_CODE` varchar(50) DEFAULT NULL COMMENT '人员编码',
  `EMPLOYEE_NAME` varchar(100) DEFAULT NULL COMMENT '人员名称',
  `DEPT_CODE` varchar(50) DEFAULT NULL COMMENT '部门编码',
  `DEPT_NAME` varchar(100) DEFAULT NULL COMMENT '部门名称',
  `PROJECT_CODE` varchar(50) DEFAULT NULL COMMENT '项目编码',
  `PROJECT_NAME` varchar(100) DEFAULT NULL COMMENT '项目名称',
  `SUPPLIER_CODE` varchar(50) DEFAULT NULL COMMENT '供应商编码',
  `OPERATOR_ID` varchar(64) DEFAULT NULL COMMENT '制单人id',
  `OPERATOR_NAME` varchar(100) DEFAULT NULL COMMENT '制单人名称',
  `VOUCHER_DATE` varchar(20) DEFAULT NULL COMMENT '制单日期',
  `COST_CENTER_CODE` varchar(50) DEFAULT NULL COMMENT '分贝通项目编码',
  `COST_CENTER_NAME` varchar(100) DEFAULT NULL COMMENT '分贝通项目名称',
  `ORG_UNIT_FULL_NAME` varchar(128) DEFAULT NULL COMMENT '分贝通部门路径',
  `PROJECT_ACCOUNTING` int(1) DEFAULT NULL COMMENT '项目核算',
  `DEPARTMENT_ACCOUNTING` int(1) DEFAULT NULL COMMENT '部门核算',
  `EMPLOYEE_ACCOUNTING` int(1) DEFAULT NULL COMMENT '人员核算',
  `SUPPLIER_ACCOUNTING` int(1) DEFAULT NULL COMMENT '供应商核算',
  `BATCH_ID` varchar(64) DEFAULT NULL COMMENT '批次id',
  `BATCH_LINE_ID` varchar(64) DEFAULT NULL COMMENT '批次行id',
  `ATTR1` varchar(64) DEFAULT NULL COMMENT '备用字段1',
  `ATTR2` varchar(64) DEFAULT NULL COMMENT '备用字段2',
  `ATTR3` varchar(64) DEFAULT NULL COMMENT '备用字段3',
  `ATTR4` varchar(64) DEFAULT NULL COMMENT '备用字段4',
  `ATTR5` varchar(64) DEFAULT NULL COMMENT '备用字段5',
  `ATTR6` varchar(64) DEFAULT NULL COMMENT '备用字段6',
  `ATTR7` varchar(64) DEFAULT NULL COMMENT '备用字段7',
  `ATTR8` varchar(64) DEFAULT NULL COMMENT '备用字段8',
  `ATTR9` varchar(64) DEFAULT NULL COMMENT '备用字段9',
  `ATTR10` varchar(64) DEFAULT NULL COMMENT '备用字段10',
  `ATTR11` varchar(64) DEFAULT NULL COMMENT '备用字段11',
  `ATTR12` varchar(64) DEFAULT NULL COMMENT '备用字段12',
  `ATTR13` varchar(64) DEFAULT NULL COMMENT '备用字段13',
  `ATTR14` varchar(64) DEFAULT NULL COMMENT '备用字段14',
  `ATTR15` varchar(64) DEFAULT NULL COMMENT '备用字段15',
  `ATTR16` varchar(64) DEFAULT NULL COMMENT '备用字段16',
  `ATTR17` varchar(64) DEFAULT NULL COMMENT '备用字段17',
  `ATTR18` varchar(64) DEFAULT NULL COMMENT '备用字段18',
  `ATTR19` varchar(64) DEFAULT NULL COMMENT '备用字段19',
  `ATTR20` varchar(64) DEFAULT NULL COMMENT '备用字段20',
  `ATTR21` text DEFAULT NULL COMMENT '备用字段21(text)',
  `ATTR22` text DEFAULT NULL COMMENT '备用字段22(text)',
  `ATTR23` text DEFAULT NULL COMMENT '备用字段23(text)',
  `ATTR24` text DEFAULT NULL COMMENT '备用字段24(text)',
  `ATTR25` text DEFAULT NULL COMMENT '备用字段25(text)',
  `ATTR26` text DEFAULT NULL COMMENT '备用字段26(text)',
  `ATTR27` text DEFAULT NULL COMMENT '备用字段27(text)',
  `ATTR28` text DEFAULT NULL COMMENT '备用字段28(text)',
  `ATTR29` text DEFAULT NULL COMMENT '备用字段29(text)',
  `ATTR30` text DEFAULT NULL COMMENT '备用字段30(text)',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='凭证草稿表';

ALTER TABLE `open_company_voucher_config` ADD COLUMN  `EXCEL_CONFIG_ID` bigint(20) NULL DEFAULT NULL COMMENT '导出excel配置ID' AFTER `STRATEGY_ID`;

CREATE TABLE `open_express_config` (
`id` varchar(64) NOT NULL COMMENT '主键',
`company_id` varchar(64) NULL COMMENT '公司id',
`type` int(3) NULL COMMENT '1:商务账单-生成凭证;2:虚拟卡核销单-生成凭证;3:对公付款单-生成凭证;4:报销单-生成凭证',
`desc` varchar(255) NULL COMMENT '条件描述',
`before_script` text NULL COMMENT '处理前脚本',
`before_row_script` text NULL COMMENT '行处理前脚本',
`after_row_script` text NULL COMMENT '行处理后脚本',
`listener` VARCHAR(256) NULL COMMENT '处理类',
`create_at` bigint(20)  NULL DEFAULT 0 COMMENT '创建时间',
`create_by` varchar(64) NULL DEFAULT '' COMMENT '创建人ID',
`create_name` varchar(32) NULL DEFAULT '' COMMENT '创建人名称',
`update_at` bigint(20)  NULL DEFAULT 0 COMMENT '更新时间',
`update_by` varchar(64) NULL DEFAULT '' COMMENT '更新人ID',
`update_name` varchar(32) NULL DEFAULT '' COMMENT '更新人名称',
`is_del` tinyint(3)  NULL DEFAULT 0 COMMENT '是否删除',
`is_test` tinyint(3)  NULL DEFAULT 0 COMMENT '是否测试',
 PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'ql表达式配置' ROW_FORMAT = Dynamic;

CREATE TABLE `open_express_config_detail` (
`id` varchar(64) NOT NULL COMMENT '主键',
`main_id` varchar(64) NULL COMMENT '主键id',
`condition_desc` varchar(255) NULL COMMENT '条件描述',
`condition_express` text NULL COMMENT '条件表达式',
`match_value` text NULL COMMENT '符合条件的json',
`create_at` bigint(20)  NULL DEFAULT 0 COMMENT '创建时间',
`create_by` varchar(64) NULL DEFAULT '' COMMENT '创建人ID',
`create_name` varchar(32) NULL DEFAULT '' COMMENT '创建人名称',
`update_at` bigint(20)  NULL DEFAULT 0 COMMENT '更新时间',
`update_by` varchar(64) NULL DEFAULT '' COMMENT '更新人ID',
`update_name` varchar(32) NULL DEFAULT '' COMMENT '更新人名称',
`is_del` tinyint(3)  NULL DEFAULT 0 COMMENT '是否删除',
`is_test` tinyint(3)  NULL DEFAULT 0 COMMENT '是否测试',
 PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '条件表达式' ROW_FORMAT = Dynamic;


CREATE TABLE `customize_voucher` (
`id` varchar(64) NOT NULL COMMENT '主键',
`company_id` varchar(64) NULL COMMENT '公司id',
`bill_no` varchar(64) NULL COMMENT '账单编号',
`status` tinyint(3) NULL COMMENT '状态: 1:生成中;2:生成失败;3:生成成功',
`url` text NULL COMMENT 'excel url地址',
`create_at` bigint(20)  NULL DEFAULT 0 COMMENT '创建时间',
`create_by` varchar(64) NULL DEFAULT '' COMMENT '创建人ID',
`create_name` varchar(32) NULL DEFAULT '' COMMENT '创建人名称',
`update_at` bigint(20)  NULL DEFAULT 0 COMMENT '更新时间',
`update_by` varchar(64) NULL DEFAULT '' COMMENT '更新人ID',
`update_name` varchar(32) NULL DEFAULT '' COMMENT '更新人名称',
`is_del` tinyint(3)  NULL DEFAULT 0 COMMENT '是否删除',
`is_test` tinyint(3)  NULL DEFAULT 0 COMMENT '是否测试',
 PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '条件表达式' ROW_FORMAT = Dynamic;


CREATE TABLE `customize_voucher_mapping` (
`id` varchar(64) NOT NULL COMMENT '主键',
`company_id` varchar(64) NULL COMMENT '账单编号',
`type` tinyint(3) NULL COMMENT '类型 1:人员;2:部门;3:项目',
`src_code` text NULL COMMENT '源编码',
`src_name` text NULL COMMENT '源名称',
`tgt_code` text  NULL COMMENT '目标编码',
`tgt_name` text  NULL COMMENT '目标名称',
`create_by` varchar(64) NULL DEFAULT '' COMMENT '创建人ID',
`create_name` varchar(32) NULL DEFAULT '' COMMENT '创建人名称',
`update_at` bigint(20)  NULL DEFAULT 0 COMMENT '更新时间',
`update_by` varchar(64) NULL DEFAULT '' COMMENT '更新人ID',
`update_name` varchar(32) NULL DEFAULT '' COMMENT '更新人名称',
`is_del` tinyint(3)  NULL DEFAULT 0 COMMENT '是否删除',
`is_test` tinyint(3)  NULL DEFAULT 0 COMMENT '是否测试',
 PRIMARY KEY (`id`),
 INDEX `customize_voucher_mapping_company_type` (`company_id`, `type`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '自定义凭证映射' ROW_FORMAT = Dynamic;