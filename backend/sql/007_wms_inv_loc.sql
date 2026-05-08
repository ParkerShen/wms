-- ===================== 库存管理 =====================
DROP TABLE IF EXISTS wms_inv_trans;
DROP TABLE IF EXISTS wms_inv_loc;

CREATE TABLE wms_inv_loc (
  id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
  wh_id           BIGINT       DEFAULT NULL COMMENT '仓库ID',
  wh_code         VARCHAR(64)  DEFAULT NULL COMMENT '仓库代码',
  loc_code        VARCHAR(64)  DEFAULT NULL COMMENT '库位代码',
  zone_code       VARCHAR(64)  DEFAULT NULL COMMENT '库区代码',
  sku_id          BIGINT       DEFAULT NULL COMMENT 'SKU ID',
  sku_code        VARCHAR(128) DEFAULT NULL COMMENT 'SKU编码',
  cust_id         BIGINT       DEFAULT NULL COMMENT '客户ID',
  cust_code       VARCHAR(64)  DEFAULT NULL COMMENT '客户代码',
  lot_no          VARCHAR(64)  DEFAULT NULL COMMENT '批次号',
  qty             INT          DEFAULT 0 COMMENT '可用库存',
  total_qty       INT          DEFAULT 0 COMMENT '总库存',
  bad_qty         INT          DEFAULT 0 COMMENT '坏品数量',
  hold_qty        INT          DEFAULT 0 COMMENT '冻结数量',
  alloc_qty       INT          DEFAULT 0 COMMENT '分配数量',
  picking_qty     INT          DEFAULT 0 COMMENT '拣货数量',
  last_update_time DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_wh_id (wh_id), KEY idx_sku_code (sku_code), KEY idx_cust_id (cust_id), KEY idx_loc_code (loc_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库位库存';

-- 库存流水表
CREATE TABLE wms_inv_trans (
  id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
  bill_type       VARCHAR(32)  NOT NULL COMMENT '单据类型:ASN/SO/ADJUST/MOVE',
  bill_no         VARCHAR(64)  NOT NULL COMMENT '单据号',
  operation_type  VARCHAR(32)  NOT NULL COMMENT '操作类型:IN_RECEIVE/IN_PUTAWAY/OUT_PICKING/OUT_SHIP/ADJUST/MOVE',
  wh_id           BIGINT       DEFAULT NULL COMMENT '仓库ID',
  wh_code         VARCHAR(64)  DEFAULT NULL COMMENT '仓库代码',
  sku_code        VARCHAR(128) DEFAULT NULL COMMENT 'SKU编码',
  cust_code       VARCHAR(64)  DEFAULT NULL COMMENT '客户代码',
  loc_code        VARCHAR(64)  DEFAULT NULL COMMENT '库位代码',
  lot_no          VARCHAR(64)  DEFAULT NULL COMMENT '批次号',
  trans_qty       INT          DEFAULT 0 COMMENT '变动数量(正入负出)',
  before_qty      INT          DEFAULT 0 COMMENT '变动前数量',
  after_qty       INT          DEFAULT 0 COMMENT '变动后数量',
  remark          VARCHAR(500) DEFAULT NULL COMMENT '备注',
  creator         BIGINT       DEFAULT NULL COMMENT '操作人',
  create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  KEY idx_bill_no (bill_no), KEY idx_sku_code (sku_code), KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存流水';

-- 菜单权限
SET @wms_dir_id = (SELECT id FROM sys_menu WHERE permission = 'wms:warehouse:list' LIMIT 1);
SET @parent_id = (SELECT parent_id FROM sys_menu WHERE id = @wms_dir_id);
INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, permission, icon, sort_order, visible, status)
VALUES (@parent_id, '库存管理', 'MENU', '/wms/inventory', 'wms/inventory/index', 'wms:inventory:list', 'Coin', 7, 1, 1);
SET @menu_id = LAST_INSERT_ID();
INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, permission, icon, sort_order, visible, status)
VALUES
  (@menu_id, '库存调整', 'BUTTON', NULL, NULL, 'wms:inventory:adjust', NULL, 1, 0, 1);

-- 测试库存数据
INSERT INTO wms_inv_loc (wh_id, wh_code, loc_code, zone_code, sku_code, cust_id, cust_code, lot_no, qty, total_qty, alloc_qty, picking_qty)
VALUES
  (1, 'WH-001', 'A-01-01', 'A', 'SKU-001', 1, 'CUST-001', 'LOT-202605', 200, 300, 30, 0),
  (1, 'WH-001', 'A-01-02', 'A', 'SKU-002', 1, 'CUST-001', 'LOT-202605', 180, 230, 20, 0),
  (1, 'WH-001', 'B-01-01', 'B', 'SKU-003', 2, 'CUST-002', 'LOT-202606', 150, 200, 30, 10),
  (2, 'WH-002', 'C-01-01', 'C', 'SKU-001', 1, 'CUST-001', 'LOT-202605', 500, 500, 0, 0),
  (2, 'WH-002', 'C-01-02', 'C', 'SKU-002', 1, 'CUST-001', 'LOT-202605', 500, 500, 0, 0);

INSERT INTO wms_inv_trans (bill_type, bill_no, operation_type, wh_id, wh_code, sku_code, cust_code, loc_code, lot_no, trans_qty, before_qty, after_qty, remark)
VALUES
  ('ASN', 'ASN-20260503', 'IN_RECEIVE', 2, 'WH-002', 'SKU-001', 'CUST-001', 'C-01-01', 'LOT-202605', 500, 0, 500, '洛杉矶入库'),
  ('ASN', 'ASN-20260503', 'IN_RECEIVE', 2, 'WH-002', 'SKU-002', 'CUST-001', 'C-01-02', 'LOT-202605', 500, 0, 500, '洛杉矶入库'),
  ('SO', 'SO-20260503', 'OUT_PICKING', 1, 'WH-001', 'SKU-001', 'CUST-001', 'A-01-01', 'LOT-202605', -60, 260, 200, '出库拣货'),
  ('SO', 'SO-20260503', 'OUT_PICKING', 1, 'WH-001', 'SKU-002', 'CUST-001', 'A-01-02', 'LOT-202605', -40, 220, 180, '出库拣货');

-- 更新菜单排序（把入库/出库/库存放到客户管理后面）
UPDATE sys_menu SET sort_order = sort_order + 10 WHERE parent_id = @parent_id;
