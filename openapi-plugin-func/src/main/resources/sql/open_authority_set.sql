CREATE TABLE `open_authority_set` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `company_id` varchar(64) NOT NULL COMMENT '公司ID',
  `default_auth` varchar(16) DEFAULT NULL COMMENT '公司人员的默认权限',
  `update_auth_flag` int(2) NOT NULL COMMENT '0表示不更新1表示更新',
  `invert_update_auth_data` varchar(5000) NOT NULL COMMENT '要更新或者不更新人的手机号，用逗号隔开',
  `update_data_flag` int(2) DEFAULT '1' COMMENT '0表示不更新人员信息，1表示更新人员信息',
  `update_auth_data` varchar(5000) NOT NULL COMMENT '要更新或者不更新人的手机号，用逗号隔开',
  `state` varchar(1) NOT NULL DEFAULT '0' COMMENT '状态属性：0:可用，1:不可用',
  `ext_info` text COMMENT '扩展字段',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `index_companyId` (`company_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4;