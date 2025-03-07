CREATE TABLE `open_ebs_comp_relations` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `RELATION_ID` varchar(64) DEFAULT NULL COMMENT '关联ID',
  `PS_COMP_CODE` varchar(64) DEFAULT NULL COMMENT 'PS公司编码',
  `PS_LOCATION_CODE` varchar(64) DEFAULT NULL COMMENT 'PS地点编码',
  `EBS_COMP_CODE` varchar(64) DEFAULT NULL COMMENT 'EBS公司公司编码',
  `ENABLED_FLAG` varchar(64) DEFAULT NULL COMMENT '有效标识',
  `ENABLE_START_DATE` varchar(64) DEFAULT NULL COMMENT 'EBS生效日期',
  `ENABLE_END_DATE` varchar(64) DEFAULT NULL COMMENT 'EBS失效日期',
  `CREATION_DATE` varchar(64) DEFAULT NULL COMMENT '创建日期',
  `CREATED_BY` varchar(64) DEFAULT NULL COMMENT '创建人',
  `LAST_UPDATE_DATE` varchar(64) DEFAULT NULL COMMENT '最后更新日期',
  `LAST_UPDATED_BY` varchar(64) DEFAULT NULL COMMENT '最后更新人',
  `ATTRIBUTE1` varchar(64) DEFAULT NULL COMMENT '属性段1',
  `ATTRIBUTE2` varchar(64) DEFAULT NULL COMMENT '属性段2',
  `ATTRIBUTE3` varchar(64) DEFAULT NULL COMMENT '属性段3',
  `ATTRIBUTE4` varchar(64) DEFAULT NULL COMMENT '属性段4',
  `ATTRIBUTE5` varchar(64) DEFAULT NULL COMMENT '属性段5',
  `ATTRIBUTE6` varchar(64) DEFAULT NULL COMMENT '属性段6',
  `ATTRIBUTE7` varchar(64) DEFAULT NULL COMMENT '属性段7',
  `ATTRIBUTE8` varchar(64) DEFAULT NULL COMMENT '属性段8',
  `ATTRIBUTE9` varchar(64) DEFAULT NULL COMMENT '属性段9',
  `ATTRIBUTE10` varchar(64) DEFAULT NULL COMMENT '属性段10',
  `INTERFACE_DATE` varchar(64) DEFAULT NULL COMMENT '接口传入日期',
  `CURR_FLAG` varchar(64) DEFAULT NULL COMMENT '最新版本标识',
  PRIMARY KEY (`ID`),
  KEY `idx_open_ebs_comp_relations_relation_id` (`RELATION_ID`) USING HASH
) ENGINE = InnoDB AUTO_INCREMENT = 1000 DEFAULT CHARSET = utf8mb4 COMMENT = '公司与法人映射关系';

CREATE TABLE `open_ebs_org_cost_relations` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `RELATION_ID` varchar(64) DEFAULT NULL COMMENT '关联ID',
  `PS_SET_ID` varchar(64) DEFAULT NULL COMMENT 'PS部门类型',
  `PS_DEPT_ID` varchar(64) DEFAULT NULL COMMENT 'PS部门编码',
  `PS_DEPT_DESC` varchar(64) DEFAULT NULL COMMENT 'PS部门描述',
  `PS_EFFECTIVE_DATE` varchar(64) DEFAULT NULL COMMENT 'PS生效日期',
  `EBS_CC_CODE` varchar(64) DEFAULT NULL COMMENT 'EBS成本中心编码',
  `EBS_CC_DESC` varchar(64) DEFAULT NULL COMMENT 'EBS成本中心描述',
  `PARENT_DEPT_CODE` varchar(64) DEFAULT NULL COMMENT '父部门编码',
  `ENABLED_FLAG` varchar(64) DEFAULT NULL COMMENT 'PS有效标识',
  `START_DATE_ACTIVE` varchar(64) DEFAULT NULL COMMENT 'EBS生效日期',
  `END_DATE_ACTIVE` varchar(64) DEFAULT NULL COMMENT 'EBS失效日期',
  `CC_ENABLED_FLAG` varchar(64) DEFAULT NULL COMMENT 'EBS有效标识',
  `ENABLE_START_DATE` varchar(64) DEFAULT NULL COMMENT '有效日期',
  `ENABLE_END_DATE` varchar(64) DEFAULT NULL COMMENT '失效日期',
  `CREATION_DATE` varchar(64) DEFAULT NULL COMMENT '创建日期',
  `CREATED_BY` varchar(64) DEFAULT NULL COMMENT '创建人',
  `LAST_UPDATE_DATE` varchar(64) DEFAULT NULL COMMENT '最后更新日期',
  `LAST_UPDATED_BY` varchar(64) DEFAULT NULL COMMENT '最后更新人',
  `ATTRIBUTE1` varchar(64) DEFAULT NULL COMMENT '属性段1',
  `ATTRIBUTE2` varchar(64) DEFAULT NULL COMMENT '属性段2',
  `ATTRIBUTE3` varchar(64) DEFAULT NULL COMMENT '属性段3',
  `ATTRIBUTE4` varchar(64) DEFAULT NULL COMMENT '属性段4',
  `ATTRIBUTE5` varchar(64) DEFAULT NULL COMMENT '属性段5',
  `ATTRIBUTE6` varchar(64) DEFAULT NULL COMMENT '属性段6',
  `ATTRIBUTE7` varchar(64) DEFAULT NULL COMMENT '属性段7',
  `ATTRIBUTE8` varchar(64) DEFAULT NULL COMMENT '属性段8',
  `ATTRIBUTE9` varchar(64) DEFAULT NULL COMMENT '属性段9',
  `ATTRIBUTE10` varchar(64) DEFAULT NULL COMMENT '属性段10',
  `INTERFACE_DATE` varchar(64) DEFAULT NULL COMMENT '接口传入日期',
  `CURR_FLAG` varchar(64) DEFAULT NULL COMMENT '最新版本标识',
  `COST_ATTR_CODE` varchar(64) DEFAULT NULL COMMENT '费用属性编码',
  PRIMARY KEY (`ID`),
  KEY `idx_open_ebs_org_cost_relation_id` (`RELATION_ID`) USING HASH
) ENGINE = InnoDB AUTO_INCREMENT = 1000 DEFAULT CHARSET = utf8mb4 COMMENT = '部门与成本中心映射关系';

CREATE TABLE `open_ebs_coe_cost_items` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `RELATION_ID` varchar(64) DEFAULT NULL COMMENT '关联ID',
  `BUDGET_TYPE_ID` varchar(64) DEFAULT NULL COMMENT '预算类型ID',
  `TARGET_TYPE_CODE` varchar(64) DEFAULT NULL COMMENT '类型',
  `COST_ITEM_ID` varchar(64) DEFAULT NULL COMMENT '费用用途ID',
  `COST_ITEM_CODE` varchar(64) DEFAULT NULL COMMENT '费用用途编码',
  `COST_TYPE_CODE` varchar(64) DEFAULT NULL COMMENT '费用类型',
  `COST_ATTR_CODE` varchar(64) DEFAULT NULL COMMENT '费用属性编码',
  `GL_ACCOUNT_CODE` varchar(64) DEFAULT NULL COMMENT '成本中心段',
  `REFERENCE_CODE` varchar(64) DEFAULT NULL COMMENT '会计科目段',
  `DESCRIPTION` varchar(64) DEFAULT NULL COMMENT '说明',
  `IS_MARKETING` varchar(64) DEFAULT NULL COMMENT '是否推广类',
  `ENABLED_FLAG` varchar(64) DEFAULT NULL COMMENT '有效标识',
  `ENABLE_START_DATE` varchar(64) DEFAULT NULL COMMENT '生效日期',
  `ENABLE_END_DATE` varchar(64) DEFAULT NULL COMMENT '失效日期',
  `CREATION_DATE` varchar(64) DEFAULT NULL COMMENT '最后更新日期',
  `CREATED_BY` varchar(64) DEFAULT NULL COMMENT '最后更新人',
  `LAST_UPDATE_DATE` varchar(64) DEFAULT NULL COMMENT '最后更新日期',
  `LAST_UPDATED_BY` varchar(64) DEFAULT NULL COMMENT '最后更新人',
  `ATTRIBUTE1` varchar(64) DEFAULT NULL COMMENT '属性段1',
  `ATTRIBUTE2` varchar(64) DEFAULT NULL COMMENT '属性段2',
  `ATTRIBUTE3` varchar(64) DEFAULT NULL COMMENT '属性段3',
  `ATTRIBUTE4` varchar(64) DEFAULT NULL COMMENT '属性段4',
  `ATTRIBUTE5` varchar(64) DEFAULT NULL COMMENT '属性段5',
  `ATTRIBUTE6` varchar(64) DEFAULT NULL COMMENT '属性段6',
  `ATTRIBUTE7` varchar(64) DEFAULT NULL COMMENT '属性段7',
  `ATTRIBUTE8` varchar(64) DEFAULT NULL COMMENT '属性段8',
  `ATTRIBUTE9` varchar(64) DEFAULT NULL COMMENT '属性段9',
  `ATTRIBUTE10` varchar(64) DEFAULT NULL COMMENT '属性段10',
  `INTERFACE_DATE` varchar(64) DEFAULT NULL COMMENT '接口传入日期',
  `CURR_FLAG` varchar(64) DEFAULT NULL COMMENT '是否最新版标识',
  PRIMARY KEY (`ID`),
  KEY `idx_open_ebs_coe_cost_items_relation_id` (`RELATION_ID`) USING HASH
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '费用用途与会计科目';

CREATE TABLE `open_ebs_bill_detail` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `COA_COM` varchar(64) DEFAULT NULL COMMENT 'EBS法人',
  `COA_BU` varchar(64) DEFAULT NULL COMMENT 'COA_BU',
  `COA_CC` varchar(64) DEFAULT NULL COMMENT '成本中心',
  `COA_ACC` varchar(64) DEFAULT NULL COMMENT '会计科目',
  `COA_IC` varchar(64) DEFAULT NULL COMMENT 'COA_IC',
  `COA_EC` varchar(64) DEFAULT NULL COMMENT 'COA_EC',
  `COA_RE` varchar(64) DEFAULT NULL COMMENT 'COA_RE',
  `COA_RESERVE1` varchar(64) DEFAULT NULL COMMENT 'COA_RESERVE1',
  `COA_RESERVE2` varchar(64) DEFAULT NULL COMMENT 'COA_RESERVE2',
  `COA_RESERVE3` varchar(64) DEFAULT NULL COMMENT 'COA_RESERVE3',
  `DEBIT` decimal(20,2) DEFAULT NULL COMMENT '借项',
  `CREDIT` decimal(20,2) DEFAULT NULL COMMENT '贷项',
  `DESP` varchar(128) DEFAULT NULL COMMENT '行说明',
  `BILL_NO` varchar(64) DEFAULT NULL COMMENT '账单编号',
  `BILL_ID` varchar(64) NULL COMMENT '账单id' AFTER `BILL_NO`,
  `BILL_DETAIL_ID` varchar(64) NULL COMMENT '账单详情id'`,
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' `,
  `UPDATE_TIME` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';
  PRIMARY KEY (`ID`),
  KEY `idx_open_ebs_bill_detail_billno` (`BILL_NO`) USING HASH
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '51talk账单明细';

CREATE TABLE `open_ebs_bill_scene_costitem_config` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `ORDER_CATEGORY` varchar(64) DEFAULT NULL COMMENT '场景编号',
  `ORDER_CATEGORY_NAME` varchar(64) DEFAULT NULL COMMENT '场景名称',
  `COST_ITEM_CODE` varchar(64) DEFAULT NULL COMMENT '费用用途编码',
  `COST_ITEM_DESC` varchar(64) DEFAULT NULL COMMENT '费用用途名称',
  PRIMARY KEY (`ID`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '51talk场景对应费用配置';