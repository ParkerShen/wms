-- =============================================
-- 操作日志表（记录增删改操作）
-- =============================================
CREATE TABLE IF NOT EXISTS sys_oper_log (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    module      VARCHAR(64)  DEFAULT NULL COMMENT '操作模块',
    oper_type   VARCHAR(32)  DEFAULT NULL COMMENT '操作类型: CREATE/UPDATE/DELETE',
    oper_desc   VARCHAR(512) DEFAULT NULL COMMENT '操作描述',
    method      VARCHAR(256) DEFAULT NULL COMMENT '请求方法',
    uri         VARCHAR(256) DEFAULT NULL COMMENT '请求URI',
    params      TEXT         DEFAULT NULL COMMENT '请求参数',
    result      VARCHAR(16)  DEFAULT NULL COMMENT '结果: SUCCESS/FAIL',
    error_msg   VARCHAR(1024) DEFAULT NULL COMMENT '错误信息',
    oper_by     VARCHAR(64)  DEFAULT NULL COMMENT '操作人',
    oper_time   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    ip_address  VARCHAR(64)  DEFAULT NULL COMMENT 'IP地址',
    duration    INT          DEFAULT NULL COMMENT '耗时(ms)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 给表加注释（如果表已存在则跳过）
-- ALTER TABLE sys_oper_log COMMENT='操作日志表';
