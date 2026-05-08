-- ===================== SO 出库管理 =====================
DROP TABLE IF EXISTS wms_so_sku;
DROP TABLE IF EXISTS wms_so;

CREATE TABLE wms_so (
  id               BIGINT       AUTO_INCREMENT PRIMARY KEY,
  so_no            VARCHAR(64)  NOT NULL COMMENT '出库单号',
  cust_id          BIGINT       DEFAULT NULL COMMENT '客户ID',
  cust_code        VARCHAR(64)  DEFAULT NULL COMMENT '客户代码',
  wh_id            BIGINT       DEFAULT NULL COMMENT '仓库ID',
  wh_code          VARCHAR(64)  DEFAULT NULL COMMENT '仓库代码',
  status           VARCHAR(32)  DEFAULT 'DRAFT' COMMENT '状态:DRAFT草稿/SUBMITTED已提交/PICKING拣货中/PACKING打包中/SHIPPED已出库/CANCEL已取消',
  cust_reference_no VARCHAR(128) DEFAULT NULL COMMENT '客户订单号',
  product_id       BIGINT       DEFAULT NULL COMMENT '产品ID',
  product_code     VARCHAR(64)  DEFAULT NULL COMMENT '产品代码',
  shipping_type    VARCHAR(32)  DEFAULT 'CHANNEL' COMMENT '发货类型:CHANNEL渠道/SELF_DELIVERY自提/CUSTOMER_PROVIDE客供',
  consignee_name   VARCHAR(100) DEFAULT NULL COMMENT '收件人',
  consignee_phone  VARCHAR(50)  DEFAULT NULL COMMENT '收件人电话',
  consignee_address VARCHAR(500) DEFAULT NULL COMMENT '收件人地址',
  consignee_city   VARCHAR(64)  DEFAULT NULL COMMENT '收件城市',
  consignee_state  VARCHAR(64)  DEFAULT NULL COMMENT '收件州/省',
  consignee_zip    VARCHAR(32)  DEFAULT NULL COMMENT '邮编',
  consignee_country VARCHAR(8) DEFAULT NULL COMMENT '收件国家',
  total_sku_qty    INT          DEFAULT NULL COMMENT 'SKU总数量',
  total_pkg_qty    INT          DEFAULT NULL COMMENT '总包裹数',
  remark           VARCHAR(500) DEFAULT NULL COMMENT '备注',
  creator          BIGINT       DEFAULT NULL COMMENT '创建人',
  create_time      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  modifier         BIGINT       DEFAULT NULL COMMENT '修改人',
  modify_time      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  deleted          TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  UNIQUE KEY uk_so_no (so_no),
  KEY idx_cust_id (cust_id), KEY idx_wh_id (wh_id), KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库通知单';

CREATE TABLE wms_so_sku (
  id              BIGINT   AUTO_INCREMENT PRIMARY KEY,
  so_id           BIGINT   NOT NULL COMMENT '出库单ID',
  so_no           VARCHAR(64) NOT NULL COMMENT '出库单号',
  sku_id          BIGINT   DEFAULT NULL COMMENT 'SKU ID',
  sku_code        VARCHAR(128) DEFAULT NULL COMMENT 'SKU编码',
  cust_sku_code   VARCHAR(128) DEFAULT NULL COMMENT '客户SKU编码',
  sku_name        VARCHAR(200) DEFAULT NULL COMMENT 'SKU名称',
  qty             INT      DEFAULT 0 COMMENT '数量',
  picked_qty      INT      DEFAULT 0 COMMENT '已拣货数量',
  remark          VARCHAR(500) DEFAULT NULL COMMENT '备注',
  KEY idx_so_id (so_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库商品明细';

-- 菜单权限
SET @wms_dir_id = (SELECT id FROM sys_menu WHERE permission = 'wms:warehouse:list' LIMIT 1);
SET @parent_id = (SELECT parent_id FROM sys_menu WHERE id = @wms_dir_id);
INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, permission, icon, sort_order, visible, status)
VALUES (@parent_id, '出库管理', 'MENU', '/wms/so', 'wms/so/index', 'wms:so:list', 'Upload', 6, 1, 1);
SET @menu_id = LAST_INSERT_ID();
INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, permission, icon, sort_order, visible, status)
VALUES
  (@menu_id, '新增出库', 'BUTTON', NULL, NULL, 'wms:so:add', NULL, 1, 0, 1),
  (@menu_id, '编辑出库', 'BUTTON', NULL, NULL, 'wms:so:edit', NULL, 2, 0, 1),
  (@menu_id, '删除出库', 'BUTTON', NULL, NULL, 'wms:so:delete', NULL, 3, 0, 1),
  (@menu_id, '提交出库', 'BUTTON', NULL, NULL, 'wms:so:submit', NULL, 4, 0, 1);

-- 测试数据
INSERT INTO wms_so (so_no, cust_id, cust_code, wh_id, wh_code, status, cust_reference_no, product_code, shipping_type, consignee_name, consignee_phone, consignee_address, consignee_city, consignee_state, consignee_zip, consignee_country, total_sku_qty, remark)
VALUES
  ('SO-20260501', 1, 'CUST-001', 1, 'WH-001', 'DRAFT', 'ORD-20260501', 'EXP-US', 'CHANNEL', 'John Smith', '+1-310-555-1111', '1234 Main St', 'Los Angeles', 'CA', '90001', 'US', 50, '美国发货'),
  ('SO-20260502', 1, 'CUST-001', 1, 'WH-001', 'SUBMITTED', 'ORD-20260502', 'EXP-US', 'CHANNEL', 'Alice Wang', '+1-626-555-2222', '5678 Oak Ave', 'Los Angeles', 'CA', '90002', 'US', 30, ''),
  ('SO-20260503', 2, 'CUST-002', 1, 'WH-001', 'SHIPPED', 'ORD-20260503', 'EXP-US', 'CHANNEL', 'Bob Lee', '+1-415-555-3333', '910 Pine St', 'San Francisco', 'CA', '94101', 'US', 100, '已完成');

INSERT INTO wms_so_sku (so_id, so_no, sku_code, cust_sku_code, sku_name, qty, picked_qty)
VALUES
  (1, 'SO-20260501', 'SKU-001', 'HUAWEI-P40', '华为P40手机', 30, 0),
  (1, 'SO-20260501', 'SKU-002', 'IPHONE-15', 'iPhone 15手机壳', 20, 0),
  (2, 'SO-20260502', 'SKU-003', 'TOY-CAR-RC', '遥控赛车', 30, 10),
  (3, 'SO-20260503', 'SKU-001', 'HUAWEI-P40', '华为P40手机', 60, 60),
  (3, 'SO-20260503', 'SKU-002', 'IPHONE-15', 'iPhone 15手机壳', 40, 40);
