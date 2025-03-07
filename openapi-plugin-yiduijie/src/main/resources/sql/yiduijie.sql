CREATE TABLE `yiduijie_conf` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `COMPANY_ID` varchar(50) DEFAULT NULL COMMENT '公司ID',
  `COMPANY_NAME` varchar(200) DEFAULT NULL COMMENT '公司名称',
  `USER_ID` varchar(50) DEFAULT NULL COMMENT '子账号编号',
  `CLIENT_ID` varchar(128) DEFAULT NULL COMMENT '部署的客户端编号',
  `APP_ID` varchar(128) DEFAULT NULL COMMENT '应用实例编号',
  `REMARK` varchar(500) DEFAULT NULL COMMENT '备注',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`ID`),
  KEY `idx_yiduijie_conf_company` (`COMPANY_ID`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='易对接配置表';

CREATE TABLE `yiduijie_create_voucher_record` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `OPERATOR` varchar(20) DEFAULT NULL COMMENT '操作人',
  `BUSINESS_TYPE` varchar(20) DEFAULT NULL COMMENT '业务类型',
  `COMPANY_ID` varchar(50) DEFAULT NULL COMMENT '公司ID',
  `BATCH_ID` varchar(50) DEFAULT NULL COMMENT '批次ID',
  `CALLBACK_URL` varchar(512) DEFAULT NULL COMMENT '回调地址',
  `STATUS` int(5) DEFAULT NULL COMMENT '状态编码',
  `TYPE` varchar(50) DEFAULT NULL COMMENT '单据类型',
  `LOCAL_ID` varchar(128) DEFAULT NULL COMMENT '外部系统单据号',
  `MESSAGE` varchar(256) DEFAULT NULL COMMENT '错误原因',
  `EXCEL_URL` varchar(512) DEFAULT NULL COMMENT '易对接excel链接地址',
  `FBT_EXCEL_URL` varchar(512) DEFAULT NULL COMMENT '分贝通excel链接地址',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`ID`),
  KEY `idx_yiduijie_create_voucher_record_batch` (`BATCH_ID`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='易对接生成凭证表';
