CREATE TABLE `open_company_bill_set` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `company_id` varchar(64) NOT NULL COMMENT '公司ID',
  `scene_type` varchar(16) COMMENT '场景类型',
  `express_str` varchar(255) NOT NULL COMMENT '计算表达式',
  `express_type` varchar(64) NOT NULL COMMENT '计算类型  1、税的计算公式',
  `state` varchar(1) NOT NULL DEFAULT '0' COMMENT '状态属性：0:可用，1:不可用',
  `ext_info` text COMMENT '扩展字段',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `index_companyId` (`company_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4