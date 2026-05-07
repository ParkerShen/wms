-- =============================================
-- 修复超级管理员权限（删除操作导致数据丢失）
-- =============================================

-- 1. 确保超级管理员角色拥有所有菜单权限
INSERT IGNORE INTO sys_role_menu (role_id, menu_id) VALUES
(1, 1),  -- 系统管理
(1, 2),  -- 用户管理
(1, 3),  -- 角色管理
(1, 4);  -- 菜单管理

-- 2. 确保 admin 用户拥有超级管理员角色
INSERT IGNORE INTO sys_user_role (user_id, role_id) VALUES
(1, 1);

-- 3. 检查修复结果
SELECT '角色菜单权限' as check_item, COUNT(*) as count FROM sys_role_menu WHERE role_id = 1;
SELECT '用户角色关联' as check_item, COUNT(*) as count FROM sys_user_role WHERE user_id = 1 AND role_id = 1;
