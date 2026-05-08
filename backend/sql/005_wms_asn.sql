-- ===================== ASN 入库管理 =====================
DROP TABLE IF EXISTS wms_asn_sku;
DROP TABLE IF EXISTS wms_asn;

CREATE TABLE wms_asn (
  id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
  asn_no          VARCHAR(64)  NOT NULL COMMENT '入库单号',
  cust_id         BIGINT       DEFAULT NULL COMMENT '客户ID',
  cust_code       VARCHAR(64)  DEFAULT NULL COMMENT '客户代码',
  wh_id           BIGINT       DEFAULT NULL COMMENT '仓库ID',
  wh_code         VARCHAR(64)  DEFAULT NULL COMMENT '仓库代码',
  asn_type        VARCHAR(32)  DEFAULT 'NORMAL' COMMENT '类型:NORMAL正常/TRANSFER调拨/RETURN退货',
  status          VARCHAR(32)  DEFAULT 'DRAFT' COMMENT '状态:DRAFT草稿/SUBMITTED已提交/RECEIVING收货中/FINISHED已完成/CANCEL已取消',
  cust_reference_no VARCHAR(128) DEFAULT NULL COMMENT '客户参考号',
  total_sku_qty   INT          DEFAULT NULL COMMENT 'SKU总数量',
  total_pkg_qty   INT          DEFAULT NULL COMMENT '总箱数',
  receipt_mode    VARCHAR(32)  DEFAULT NULL COMMENT '收货方式',
  remark          VARCHAR(500) DEFAULT NULL COMMENT '备注',
  creator         BIGINT       DEFAULT NULL COMMENT '创建人',
  create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  modifier        BIGINT       DEFAULT NULL COMMENT '修改人',
  modify_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  deleted         TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  UNIQUE KEY uk_asn_no (asn_no),
  KEY idx_cust_id (cust_id), KEY idx_wh_id (wh_id), KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库通知单';

CREATE TABLE wms_asn_sku (
  id              BIGINT   AUTO_INCREMENT PRIMARY KEY,
  asn_id          BIGINT   NOT NULL COMMENT '入库单ID',
  asn_no          VARCHAR(64) NOT NULL COMMENT '入库单号',
  sku_id          BIGINT   DEFAULT NULL COMMENT 'SKU ID',
  sku_code        VARCHAR(128) DEFAULT NULL COMMENT 'SKU编码',
  cust_sku_code   VARCHAR(128) DEFAULT NULL COMMENT '客户SKU编码',
  sku_name        VARCHAR(200) DEFAULT NULL COMMENT 'SKU名称',
  expected_qty    INT      DEFAULT 0 COMMENT '预计数量',
  actual_qty      INT      DEFAULT 0 COMMENT '实际收货数量',
  bad_qty         INT      DEFAULT 0 COMMENT '损坏数量',
  remark          VARCHAR(500) DEFAULT NULL COMMENT '备注',
  KEY idx_asn_id (asn_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库商品明细';

-- 菜单权限
SET @wms_dir_id = (SELECT id FROM sys_menu WHERE permission = 'wms:warehouse:list' LIMIT 1);
SET @parent_id = (SELECT parent_id FROM sys_menu WHERE id = @wms_dir_id);
INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, permission, icon, sort_order, visible, status)
VALUES (@parent_id, '入库管理', 'MENU', '/wms/asn', 'wms/asn/index', 'wms:asn:list', 'Download', 5, 1, 1);
SET @menu_id = LAST_INSERT_ID();
INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, permission, icon, sort_order, visible, status)
VALUES
  (@menu_id, '新增入库', 'BUTTON', NULL, NULL, 'wms:asn:add', NULL, 1, 0, 1),
  (@menu_id, '编辑入库', 'BUTTON', NULL, NULL, 'wms:asn:edit', NULL, 2, 0, 1),
  (@menu_id, '删除入库', 'BUTTON', NULL, NULL, 'wms:asn:delete', NULL, 3, 0, 1),
  (@menu_id, '提交入库', 'BUTTON', NULL, NULL, 'wms:asn:submit', NULL, 4, 0, 1);

-- 测试数据
INSERT INTO wms_asn (asn_no, cust_id, cust_code, wh_id, wh_code, asn_type, status, cust_reference_no, total_sku_qty, total_pkg_qty, receipt_mode, remark)
VALUES
  ('ASN-20260501', 1, 'CUST-001', 1, 'WH-001', 'NORMAL', 'DRAFT', 'PO-20260501', 500, 20, 'TRUCK', '深圳前海仓入库'),
  ('ASN-20260502', 2, 'CUST-002', 1, 'WH-001', 'NORMAL', 'SUBMITTED', 'PO-20260502', 200, 8, 'TRUCK', '越秀供应链入库'),
  ('ASN-20260503', 3, 'CUST-003', 2, 'WH-002', 'NORMAL', 'FINISHED', 'PO-20260503', 1000, 40, 'CONTAINER', '洛杉矶海外仓入库');

INSERT INTO wms_asn_sku (asn_id, asn_no, sku_code, cust_sku_code, sku_name, expected_qty, actual_qty)
VALUES
  (1, 'ASN-20260501', 'SKU-001', 'HUAWEI-P40', '华为P40手机', 300, 0),
  (1, 'ASN-20260501', 'SKU-002', 'IPHONE-15', 'iPhone 15手机壳', 200, 0),
  (2, 'ASN-20260502', 'SKU-003', 'TOY-CAR-RC', '遥控赛车', 200, 0),
  (3, 'ASN-20260503', 'SKU-001', 'HUAWEI-P40', '华为P40手机', 500, 500),
  (3, 'ASN-20260503', 'SKU-002', 'IPHONE-15', 'iPhone 15手机壳', 500, 500);
