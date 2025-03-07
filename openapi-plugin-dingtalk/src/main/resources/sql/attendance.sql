CREATE TABLE `tb_attendance_dingtalk` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `MAIN_ID` bigint(20) NOT NULL COMMENT '考勤表id',
  `DINGTALK_CHECK_ID` bigint(20) DEFAULT NULL COMMENT '钉钉打卡唯一ID',
  `RECORD_ID` bigint(20) DEFAULT NULL COMMENT '打卡记录ID',
  `GROUP_ID` bigint(20) DEFAULT NULL COMMENT '考勤组ID',
  `PLAN_ID` bigint(20) DEFAULT NULL COMMENT '排班ID',
  `CHECK_TYPE` varchar(50) DEFAULT NULL COMMENT '考勤类型 OnDuty:上班;OffDuty:下班',
  `CORP_ID` varchar(50) DEFAULT NULL COMMENT '钉钉公司ID',
  `USER_ID` varchar(50) DEFAULT NULL COMMENT '钉钉用户ID',
  `WORK_DATE` datetime NOT NULL COMMENT '工作日',
  `BASE_CHECK_TIME` datetime NOT NULL COMMENT '标准(签到/签退)时间',
  `USER_CHECK_TIME` datetime NOT NULL COMMENT '用户(签到/签退)时间',
  `TIME_RESULT` smallint NOT NULL COMMENT '时间结果',
  `TIME_RESULT_DESC` varchar(128) NOT NULL COMMENT '时间结果描述',
  `LOCATION_RESULT` smallint NOT NULL COMMENT '位置结果',
  `LOCATION_RESULT_DESC` varchar(128) NOT NULL COMMENT '位置结果描述',
  `SOURCE_TYPE`  varchar(128) DEFAULT NULL COMMENT '数据来源',
  `APPROVE_ID`  varchar(128) DEFAULT NULL COMMENT '关联的审批id',
  `PROC_INST_ID`  varchar(128) DEFAULT NULL COMMENT '关联的审批实例id',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`ID`),
  KEY `idx_kaoqin_dingtalk_main_id` (`MAIN_ID`) USING HASH,
  KEY `idx_kaoqin_dingtalk_check_id` (`DINGTALK_CHECK_ID`) USING HASH,
  KEY `idx_kaoqin_dingtalk_record_id` (`RECORD_ID`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='钉钉考勤记录';
