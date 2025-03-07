CREATE TABLE `open_yufu_sso_config` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `COMPANY_ID` varchar(50) DEFAULT NULL COMMENT '公司ID',
  `COMPANY_NAME` varchar(200) DEFAULT NULL COMMENT '公司名称',
  `PLATFORM_TYPE` int(1) DEFAULT NULL COMMENT '平台类型 1:企业web;2:webapp',
  `TENANT` varchar(64) DEFAULT NULL COMMENT '租户',
  `ISSUER` varchar(64) DEFAULT NULL COMMENT 'issuer',
  `AUDIENCE` varchar(64) DEFAULT NULL COMMENT 'audience',
  `USER_NAME_TYPE` int(1) DEFAULT NULL COMMENT '用户名类型 1:手机号;2:邮箱',
  `PUBLIC_KEY` varchar(3000) DEFAULT NULL COMMENT '公钥key',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`ID`),
  KEY `idx_open_yufu_sso_config_company` (`COMPANY_ID`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='玉符单点登录配置表';