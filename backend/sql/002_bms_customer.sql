-- ===================== 1. 建表 =====================
DROP TABLE IF EXISTS bms_customer;
CREATE TABLE bms_customer (
  id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
  cust_code    VARCHAR(64)  NOT NULL COMMENT '客户代码',
  cust_name    VARCHAR(200) NOT NULL COMMENT '客户名称',
  contact      VARCHAR(100) DEFAULT NULL COMMENT '联系人',
  email        VARCHAR(200) DEFAULT NULL COMMENT '邮箱',
  tel_no       VARCHAR(50)  DEFAULT NULL COMMENT '电话',
  address      VARCHAR(500) DEFAULT NULL COMMENT '地址',
  api_account  VARCHAR(100) DEFAULT NULL COMMENT 'API账号',
  api_password VARCHAR(100) DEFAULT NULL COMMENT 'API密码',
  status       VARCHAR(16)  DEFAULT 'VALID' COMMENT '状态:VALID/INVALID',
  remark       VARCHAR(500) DEFAULT NULL COMMENT '备注',

  auto_audit_sku         TINYINT DEFAULT 1 COMMENT 'SKU自动审核:1是,0否',
  auto_sku_code          TINYINT DEFAULT 0 COMMENT '自动生成SKU编码:1是,0否',
  sku_classify_required  TINYINT DEFAULT 0 COMMENT '商品分类必填:1是,0否',
  service_platform       VARCHAR(32) DEFAULT NULL COMMENT '平台:NODE/TUME',
  sku_wh_sync_type       VARCHAR(32) DEFAULT NULL COMMENT 'SKU复核数据类型',
  express_advance_flag   TINYINT DEFAULT 0 COMMENT '运单预扣款:1是,0否',
  support_cust_provider  TINYINT DEFAULT 0 COMMENT '客供面单:1是,0否',
  match_label_flag       TINYINT DEFAULT 0 COMMENT '匹配标签:1是,0否',
  new_fee_structure      TINYINT DEFAULT 0 COMMENT '新模板计费:1是,0否',
  order_source           TINYINT DEFAULT 0 COMMENT '订单来源:0自主,1平台',
  shipping_code          VARCHAR(64) DEFAULT NULL COMMENT '发货代码',
  shipping_address       VARCHAR(500) DEFAULT NULL COMMENT '发货地址',
  show_flag              TINYINT DEFAULT 1 COMMENT '是否展示:1是,0否',

  creator      BIGINT       DEFAULT NULL COMMENT '创建人',
  create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  modifier     BIGINT       DEFAULT NULL COMMENT '修改人',
  modify_time  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  deleted      TINYINT      DEFAULT 0 COMMENT '逻辑删除:0未删,1已删',

  UNIQUE KEY uk_cust_code (cust_code),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户主表';

-- ===================== 2. 菜单权限数据 =====================
SET @wms_dir_id = (SELECT id FROM sys_menu WHERE permission = 'wms:warehouse:list' LIMIT 1);
SET @parent_id = (SELECT parent_id FROM sys_menu WHERE id = @wms_dir_id);

INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, permission, icon, sort_order, visible, status)
VALUES (@parent_id, '客户管理', 'MENU', '/wms/customer', 'wms/customer/index', 'wms:customer:list', 'UserFilled', 2, 1, 1);

SET @menu_id = LAST_INSERT_ID();
INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, permission, icon, sort_order, visible, status)
VALUES
  (@menu_id, '新增客户', 'BUTTON', NULL, NULL, 'wms:customer:add', NULL, 1, 0, 1),
  (@menu_id, '编辑客户', 'BUTTON', NULL, NULL, 'wms:customer:edit', NULL, 2, 0, 1),
  (@menu_id, '删除客户', 'BUTTON', NULL, NULL, 'wms:customer:delete', NULL, 3, 0, 1);

-- ===================== 3. 测试数据 =====================
INSERT INTO bms_customer (cust_code, cust_name, contact, email, tel_no, address, status, service_platform, auto_audit_sku)
VALUES
  ('CUST-001', '深圳华强贸易有限公司', '李经理', 'li@hqtrade.com', '0755-11111111', '深圳市福田区华强北路1号', 'VALID', 'NODE', 1),
  ('CUST-002', '广州越秀供应链', '王总', 'wang@gzyuexiu.com', '020-22222222', '广州市越秀区解放北路100号', 'VALID', 'NODE', 1),
  ('CUST-003', '洛杉矶GlobalBuy Inc', 'Mike', 'mike@globalbuy.com', '+1-310-555-6789', '4567 W Century Blvd, Los Angeles', 'VALID', 'TUME', 0);
