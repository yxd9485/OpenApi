CREATE TABLE `open_authority_deploy` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `company_id` varchar(64) NOT NULL COMMENT '公司ID',
  `target_column` varchar(16) COMMENT '目标字段名称',
  `src_column` varchar(32) NOT NULL COMMENT '源字段的key',
  `src_value` varchar(64) NOT NULL COMMENT '源字段的值',
  `role_type` int(2) NOT NULL COMMENT '分贝人员权限类型',
  `state` varchar(1) NOT NULL DEFAULT '0' COMMENT '状态属性：0:可用，1:不可用',
  `type` int(2) NOT NULL DEFAULT '1' COMMENT '类型，部门：0，人员：1，其他：2',
  `scrip` varchar(255) DEFAULT NULL COMMENT '脚本属性',
  `ext_info` text COMMENT '扩展字段',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `ind_cId_scr_co_v` (`company_id`,`src_column`,`src_value`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4