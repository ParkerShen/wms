-- ===================== 1. 建表 =====================
DROP TABLE IF EXISTS bms_product;
CREATE TABLE bms_product (
  id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
  product_code VARCHAR(64)  NOT NULL COMMENT '产品代码',
  product_name VARCHAR(200) NOT NULL COMMENT '产品名称',
  product_version INT      DEFAULT 1 COMMENT '产品版本',
  biz_type     VARCHAR(64)  DEFAULT NULL COMMENT '业务类型:EXPRESS_SHARING/WAREHOUSE_DELIVERY',
  product_type VARCHAR(32)  DEFAULT 'SINGLE_PIECE' COMMENT '产品类型:SINGLE_PIECE一票一件/MULTI_PIECE一票多件',
  country_code VARCHAR(8)   DEFAULT NULL COMMENT '国家代码',
  currency_code VARCHAR(8)  DEFAULT NULL COMMENT '币种',
  channel_rules VARCHAR(32) DEFAULT 'COST' COMMENT '渠道选择:COST成本/FAST时效/PROFIT利润',
  billing_rules VARCHAR(32) DEFAULT 'ORDER' COMMENT '计费规则:ORDER票/PIECE件/BOX箱/CBM立方米',
  billable_weight_type VARCHAR(8) DEFAULT 'MAX' COMMENT '计费重:GW实重/VW泡重/MAX取大',
  dim_weight_coefficient INT DEFAULT 5000 COMMENT '体积重系数',
  freight_coefficient DECIMAL(10,4) DEFAULT 1.0000 COMMENT '运费系数',
  weight_unit  VARCHAR(16)  DEFAULT 'KG' COMMENT '重量单位',
  length_unit  VARCHAR(16)  DEFAULT 'CM' COMMENT '长度单位',
  online_flag  VARCHAR(16)  DEFAULT 'ONLINE' COMMENT '线上:ONLINE/OFFLINE',
  status       VARCHAR(16)  DEFAULT 'VALID' COMMENT '状态:VALID/INVALID',
  remark       VARCHAR(500) DEFAULT NULL COMMENT '备注',

  oda_flag              TINYINT DEFAULT 0 COMMENT '偏远:1是,0否',
  pod_flag              TINYINT DEFAULT 0 COMMENT '签收证明:1是,0否',
  insurance_flag        TINYINT DEFAULT 0 COMMENT '保险:1是,0否',
  dangerous_flag        TINYINT DEFAULT 0 COMMENT '危险品:1是,0否',
  return_label_flag     TINYINT DEFAULT 0 COMMENT '退件标签:1是,0否',
  end_provider_code     VARCHAR(64) DEFAULT NULL COMMENT '末端服务商',
  zone_template_id      BIGINT DEFAULT NULL COMMENT '分区模板ID',
  weight_template_id    BIGINT DEFAULT NULL COMMENT '重量模板ID',

  creator      BIGINT       DEFAULT NULL COMMENT '创建人',
  create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  modifier     BIGINT       DEFAULT NULL COMMENT '修改人',
  modify_time  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  deleted      TINYINT      DEFAULT 0 COMMENT '逻辑删除:0未删,1已删',

  UNIQUE KEY uk_product_code (product_code),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品/渠道表';

-- ===================== 2. 菜单权限数据 =====================
SET @wms_dir_id = (SELECT id FROM sys_menu WHERE permission = 'wms:warehouse:list' LIMIT 1);
SET @parent_id = (SELECT parent_id FROM sys_menu WHERE id = @wms_dir_id);

INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, permission, icon, sort_order, visible, status)
VALUES (@parent_id, '产品管理', 'MENU', '/wms/product', 'wms/product/index', 'wms:product:list', 'List', 4, 1, 1);

SET @menu_id = LAST_INSERT_ID();
INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, permission, icon, sort_order, visible, status)
VALUES
  (@menu_id, '新增产品', 'BUTTON', NULL, NULL, 'wms:product:add', NULL, 1, 0, 1),
  (@menu_id, '编辑产品', 'BUTTON', NULL, NULL, 'wms:product:edit', NULL, 2, 0, 1),
  (@menu_id, '删除产品', 'BUTTON', NULL, NULL, 'wms:product:delete', NULL, 3, 0, 1);

-- ===================== 3. 测试数据 =====================
INSERT INTO bms_product (product_code, product_name, biz_type, product_type, country_code, currency_code, channel_rules, billing_rules, billable_weight_type, dim_weight_coefficient, freight_coefficient, weight_unit, length_unit, online_flag)
VALUES
  ('EXP-US', '美国快递标准', 'EXPRESS_SHARING', 'SINGLE_PIECE', 'US', 'USD', 'COST', 'PIECE', 'MAX', 5000, 1.0000, 'KG', 'CM', 'ONLINE'),
  ('EXP-US-ECO', '美国快递经济', 'EXPRESS_SHARING', 'MULTI_PIECE', 'US', 'USD', 'COST', 'ORDER', 'MAX', 5000, 0.8500, 'KG', 'CM', 'ONLINE'),
  ('WH-CN-US', '中美仓储派送', 'WAREHOUSE_DELIVERY', 'SINGLE_PIECE', 'CN', 'CNY', 'PROFIT', 'BOX', 'MAX', 6000, 1.2000, 'KG', 'CM', 'ONLINE');
