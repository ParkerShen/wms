-- ===================== 1. 建表 =====================
DROP TABLE IF EXISTS wms_warehouse;
CREATE TABLE wms_warehouse (
  id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
  wh_code         VARCHAR(64)  NOT NULL COMMENT '仓库代码',
  wh_name         VARCHAR(200) NOT NULL COMMENT '仓库名称',
  wh_type         VARCHAR(32)  DEFAULT 'WH' COMMENT '仓库类型:PC/WH/VL/WH_TRANSIT/LGS',
  country_code    VARCHAR(8)   DEFAULT NULL COMMENT '国家',
  currency_code   VARCHAR(8)   DEFAULT NULL COMMENT '币种',
  time_zone       VARCHAR(32)  DEFAULT NULL COMMENT '时区',
  weight_unit     VARCHAR(16)  DEFAULT NULL COMMENT '重量单位',
  length_unit     VARCHAR(16)  DEFAULT NULL COMMENT '长度单位',
  status          VARCHAR(16)  DEFAULT 'VALID' COMMENT '状态:VALID/INVALID',
  remark          VARCHAR(500) DEFAULT NULL COMMENT '备注',

  shipping_name   VARCHAR(100) DEFAULT NULL COMMENT '发货人',
  shipping_tel    VARCHAR(50)  DEFAULT NULL COMMENT '发货电话',
  shipping_email  VARCHAR(100) DEFAULT NULL COMMENT '发货邮箱',
  consignee_name  VARCHAR(100) DEFAULT NULL COMMENT '收货人',
  consignee_tel   VARCHAR(50)  DEFAULT NULL COMMENT '收货电话',
  consignee_email VARCHAR(100) DEFAULT NULL COMMENT '收货邮箱',
  postal_code     VARCHAR(32)  DEFAULT NULL COMMENT '邮编',
  province        VARCHAR(64)  DEFAULT NULL COMMENT '省/州',
  city            VARCHAR(64)  DEFAULT NULL COMMENT '城市',
  address1        VARCHAR(255) DEFAULT NULL COMMENT '地址1',
  contact         VARCHAR(100) DEFAULT NULL COMMENT '联系人',
  email           VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  tel_no          VARCHAR(50)  DEFAULT NULL COMMENT '电话',

  biz_type        VARCHAR(64)  DEFAULT NULL COMMENT '经营方式',
  service_function VARCHAR(255) DEFAULT NULL COMMENT '服务功能',
  storage_area    DECIMAL(12,2) DEFAULT NULL COMMENT '仓储面积(m²)',
  storage_volume  DECIMAL(12,2) DEFAULT NULL COMMENT '仓储库容(m³)',
  throughput      INT          DEFAULT NULL COMMENT '订单处理量',
  handle_capacity INT          DEFAULT NULL COMMENT '处理容量(SKU数)',
  valid_day       INT          DEFAULT NULL COMMENT '有效天数',
  storage_type    VARCHAR(32)  DEFAULT NULL COMMENT '仓租模式',
  coop_partner    VARCHAR(200) DEFAULT NULL COMMENT '合作方',
  wms_system      VARCHAR(100) DEFAULT NULL COMMENT 'WMS系统',

  creator         BIGINT       DEFAULT NULL COMMENT '创建人',
  create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  modifier        BIGINT       DEFAULT NULL COMMENT '修改人',
  modify_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  deleted         TINYINT      DEFAULT 0 COMMENT '逻辑删除:0未删,1已删',

  UNIQUE KEY uk_wh_code (wh_code),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库主表';

-- ===================== 2. 菜单权限数据 =====================
-- 目录：仓库管理
INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, permission, icon, sort_order, visible, status)
VALUES (0, '仓库管理', 'DIR', '/wms', NULL, NULL, 'Box', 20, 1, 1);

-- 菜单：仓库设置
INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, permission, icon, sort_order, visible, status)
VALUES (LAST_INSERT_ID(), '仓库设置', 'MENU', '/wms/warehouse', 'wms/warehouse/index', 'wms:warehouse:list', 'OfficeBuilding', 1, 1, 1);

-- 按钮权限
SET @menu_id = LAST_INSERT_ID();
INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, permission, icon, sort_order, visible, status)
VALUES
  (@menu_id, '新增仓库', 'BUTTON', NULL, NULL, 'wms:warehouse:add', NULL, 1, 0, 1),
  (@menu_id, '编辑仓库', 'BUTTON', NULL, NULL, 'wms:warehouse:edit', NULL, 2, 0, 1),
  (@menu_id, '删除仓库', 'BUTTON', NULL, NULL, 'wms:warehouse:delete', NULL, 3, 0, 1);

-- ===================== 3. 测试数据 =====================
INSERT INTO wms_warehouse (wh_code, wh_name, wh_type, country_code, currency_code, time_zone, weight_unit, length_unit, status, province, city, address1, contact, tel_no)
VALUES
  ('WH-001', '深圳前海仓', 'WH', 'CN', 'CNY', 'Asia/Shanghai', 'KG', 'CM', 'VALID', '广东', '深圳', '南山区前海湾保税港区', '张三', '0755-88888888'),
  ('WH-002', '洛杉矶海外仓', 'WH', 'US', 'USD', 'America/Los_Angeles', 'LB', 'INCH', 'VALID', 'California', 'Los Angeles', '1234 E Imperial Hwy', 'John', '+1-310-555-1234'),
  ('WH-003', '汉堡中转仓', 'WH_TRANSIT', 'DE', 'EUR', 'Europe/Berlin', 'KG', 'CM', 'VALID', 'Hamburg', 'Hamburg', 'Hafenstrasse 1', 'Hans', '+49-40-555-5678');
