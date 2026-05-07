-- =============================================
-- 星瀚WMS - 初始化数据库脚本
-- =============================================

-- 创建数据库（如果 docker-compose 已自动创建则跳过）
-- CREATE DATABASE IF NOT EXISTS starlink_wms DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- =============================================
-- 1. 系统用户表
-- =============================================
CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username    VARCHAR(64)  NOT NULL COMMENT '用户名',
    password    VARCHAR(256) NOT NULL COMMENT '密码(BCrypt加密)',
    real_name   VARCHAR(64)  DEFAULT NULL COMMENT '真实姓名',
    email       VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    phone       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    avatar      VARCHAR(512) DEFAULT NULL COMMENT '头像URL',
    status      TINYINT      DEFAULT 1 COMMENT '状态: 0=禁用, 1=启用',
    remark      VARCHAR(256) DEFAULT NULL COMMENT '备注',
    deleted     TINYINT      DEFAULT 0 COMMENT '逻辑删除: 0=未删, 1=已删',
    create_by   VARCHAR(64)  DEFAULT NULL COMMENT '创建人',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(64)  DEFAULT NULL COMMENT '更新人',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- =============================================
-- 2. 公司/租户表
-- =============================================
CREATE TABLE IF NOT EXISTS sys_company (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '公司ID',
    cpy_name    VARCHAR(128) NOT NULL COMMENT '公司名称',
    cpy_code    VARCHAR(64)  DEFAULT NULL COMMENT '公司编码',
    domain      VARCHAR(256) DEFAULT NULL COMMENT '域名',
    logo        VARCHAR(512) DEFAULT NULL COMMENT 'Logo URL',
    contact     VARCHAR(64)  DEFAULT NULL COMMENT '联系人',
    phone       VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
    address     VARCHAR(256) DEFAULT NULL COMMENT '地址',
    status      TINYINT      DEFAULT 1 COMMENT '状态: 0=禁用, 1=启用',
    deleted     TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_cpy_code (cpy_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公司/租户表';

-- =============================================
-- 3. 角色表
-- =============================================
CREATE TABLE IF NOT EXISTS sys_role (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    role_name   VARCHAR(64)  NOT NULL COMMENT '角色名称',
    role_key    VARCHAR(64)  NOT NULL COMMENT '角色标识',
    sort_order  INT          DEFAULT 0 COMMENT '排序',
    status      TINYINT      DEFAULT 1 COMMENT '状态',
    remark      VARCHAR(256) DEFAULT NULL COMMENT '备注',
    deleted     TINYINT      DEFAULT 0,
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_role_key (role_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- =============================================
-- 4. 用户-角色关联表
-- =============================================
CREATE TABLE IF NOT EXISTS sys_user_role (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL COMMENT '用户ID',
    role_id     BIGINT NOT NULL COMMENT '角色ID',
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- =============================================
-- 5. 菜单/权限表
-- =============================================
CREATE TABLE IF NOT EXISTS sys_menu (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '菜单ID',
    parent_id   BIGINT       DEFAULT 0 COMMENT '父菜单ID',
    menu_name   VARCHAR(64)  NOT NULL COMMENT '菜单名称',
    menu_type   VARCHAR(16)  DEFAULT 'MENU' COMMENT '类型: MENU=目录, BUTTON=按钮',
    path        VARCHAR(256) DEFAULT NULL COMMENT '路由地址',
    component   VARCHAR(256) DEFAULT NULL COMMENT '组件路径',
    permission  VARCHAR(128) DEFAULT NULL COMMENT '权限标识',
    icon        VARCHAR(64)  DEFAULT NULL COMMENT '图标',
    sort_order  INT          DEFAULT 0 COMMENT '排序',
    visible     TINYINT      DEFAULT 1 COMMENT '是否可见',
    status      TINYINT      DEFAULT 1 COMMENT '状态',
    deleted     TINYINT      DEFAULT 0,
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- =============================================
-- 6. 角色-菜单关联表
-- =============================================
CREATE TABLE IF NOT EXISTS sys_role_menu (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    UNIQUE KEY uk_role_menu (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- =============================================
-- 7. 登录日志表
-- =============================================
CREATE TABLE IF NOT EXISTS sys_login_log (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       DEFAULT NULL COMMENT '用户ID',
    username    VARCHAR(64)  DEFAULT NULL COMMENT '用户名',
    ip_address  VARCHAR(64)  DEFAULT NULL COMMENT 'IP地址',
    browser     VARCHAR(128) DEFAULT NULL COMMENT '浏览器',
    os          VARCHAR(64)  DEFAULT NULL COMMENT '操作系统',
    status      VARCHAR(16)  NOT NULL COMMENT '状态: SUCCESS/FAIL',
    message     VARCHAR(256) DEFAULT NULL COMMENT '消息',
    login_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- =============================================
-- 初始化数据
-- =============================================

-- 默认管理员 (密码: admin123)
INSERT INTO sys_user (username, password, real_name, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 1);

-- 默认角色
INSERT INTO sys_role (role_name, role_key, remark) VALUES
('超级管理员', 'super_admin', '系统超级管理员'),
('系统管理员', 'admin', '系统管理员');

-- 赋予 admin 超级管理员角色
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1);

-- 基础菜单（示例：系统管理）
INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, icon, sort_order, permission) VALUES
(0, '系统管理', 'MENU', '/system', 'Layout', 'Setting', 1, NULL),
(1, '用户管理', 'MENU', '/system/user', 'system/user/index', 'User', 1, 'system:user:list'),
(1, '角色管理', 'MENU', '/system/role', 'system/role/index', 'Avatar', 2, 'system:role:list'),
(1, '菜单管理', 'MENU', '/system/menu', 'system/menu/index', 'Menu', 3, 'system:menu:list');

-- 给超级管理员赋予所有菜单权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4);
