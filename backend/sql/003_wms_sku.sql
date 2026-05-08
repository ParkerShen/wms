-- ===================== 1. 建表 =====================
DROP TABLE IF EXISTS wms_sku;
CREATE TABLE wms_sku (
  id                 BIGINT       AUTO_INCREMENT PRIMARY KEY,
  cust_id            BIGINT       DEFAULT NULL COMMENT '客户ID',
  cust_code          VARCHAR(64)  DEFAULT NULL COMMENT '客户代码',
  sku_code           VARCHAR(128) NOT NULL COMMENT 'SKU编码',
  customer_sku_code  VARCHAR(128) DEFAULT NULL COMMENT '客户SKU编码',
  bar_code           VARCHAR(128) DEFAULT NULL COMMENT '条码',
  hs_code            VARCHAR(32)  DEFAULT NULL COMMENT '海关编码',
  sku_type           VARCHAR(16)  DEFAULT 'SKU' COMMENT 'SKU类型:TEMP_SKU/SKU/PACKAGE',
  sku_name_zh        VARCHAR(200) DEFAULT NULL COMMENT '中文名称',
  sku_name_en        VARCHAR(200) DEFAULT NULL COMMENT '英文名称',
  sku_name_fr        VARCHAR(200) DEFAULT NULL COMMENT '法文名称',

  declared_amount    DECIMAL(12,2) DEFAULT NULL COMMENT '申报价值',
  declared_weight    DECIMAL(10,4) DEFAULT NULL COMMENT '申报重量(kg)',
  declared_length    DECIMAL(10,2) DEFAULT NULL COMMENT '申报长(cm)',
  declared_width     DECIMAL(10,2) DEFAULT NULL COMMENT '申报宽(cm)',
  declared_height    DECIMAL(10,2) DEFAULT NULL COMMENT '申报高(cm)',
  declared_volume    DECIMAL(10,4) DEFAULT NULL COMMENT '申报体积(m³)',

  classify_id        BIGINT       DEFAULT NULL COMMENT '商品分类ID',
  brand              VARCHAR(100) DEFAULT NULL COMMENT '品牌',
  origin_country     VARCHAR(8)   DEFAULT NULL COMMENT '原产地',
  sn_type            VARCHAR(16)  DEFAULT 'NONE' COMMENT 'SN类型:NONE/LOT_NUM/ASSIGN',
  lot_type           VARCHAR(16)  DEFAULT 'NONE' COMMENT '批次类型',

  magnetic_flag      TINYINT DEFAULT 0 COMMENT '磁性:1是,0否',
  danger_flag        TINYINT DEFAULT 0 COMMENT '危险品:1是,0否',
  charged_flag       TINYINT DEFAULT 0 COMMENT '带电:1是,0否',
  liquid_flag        TINYINT DEFAULT 0 COMMENT '液体:1是,0否',
  status             VARCHAR(16)  DEFAULT 'VALID' COMMENT '状态',
  remark             VARCHAR(500) DEFAULT NULL COMMENT '备注',

  creator      BIGINT       DEFAULT NULL COMMENT '创建人',
  create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  modifier     BIGINT       DEFAULT NULL COMMENT '修改人',
  modify_time  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  deleted      TINYINT      DEFAULT 0 COMMENT '逻辑删除:0未删,1已删',

  UNIQUE KEY uk_sku_code (sku_code),
  KEY idx_cust_id (cust_id),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SKU/商品表';

-- ===================== 2. 菜单权限数据 =====================
SET @wms_dir_id = (SELECT id FROM sys_menu WHERE permission = 'wms:warehouse:list' LIMIT 1);
SET @parent_id = (SELECT parent_id FROM sys_menu WHERE id = @wms_dir_id);

INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, permission, icon, sort_order, visible, status)
VALUES (@parent_id, 'SKU管理', 'MENU', '/wms/sku', 'wms/sku/index', 'wms:sku:list', 'Goods', 3, 1, 1);

SET @menu_id = LAST_INSERT_ID();
INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, permission, icon, sort_order, visible, status)
VALUES
  (@menu_id, '新增SKU', 'BUTTON', NULL, NULL, 'wms:sku:add', NULL, 1, 0, 1),
  (@menu_id, '编辑SKU', 'BUTTON', NULL, NULL, 'wms:sku:edit', NULL, 2, 0, 1),
  (@menu_id, '删除SKU', 'BUTTON', NULL, NULL, 'wms:sku:delete', NULL, 3, 0, 1);

-- ===================== 3. 测试数据 =====================
INSERT INTO wms_sku (cust_id, cust_code, sku_code, customer_sku_code, bar_code, hs_code, sku_name_zh, sku_name_en, declared_weight, declared_length, declared_width, declared_height, declared_amount, origin_country, status)
VALUES
  (1, 'CUST-001', 'SKU-001', 'HUAWEI-P40', '6901443378901', '851712', '华为P40手机', 'Huawei P40 Phone', 0.196, 14.9, 7.2, 0.8, 4999.00, 'CN', 'VALID'),
  (1, 'CUST-001', 'SKU-002', 'IPHONE-15', '1942533878902', '851713', 'iPhone 15手机壳', 'iPhone 15 Case', 0.050, 16.0, 8.0, 1.2, 29.99, 'CN', 'VALID'),
  (2, 'CUST-002', 'SKU-003', 'TOY-CAR-RC', '6921443378903', '950300', '遥控赛车', 'RC Racing Car', 0.350, 25.0, 15.0, 10.0, 89.00, 'CN', 'VALID');
