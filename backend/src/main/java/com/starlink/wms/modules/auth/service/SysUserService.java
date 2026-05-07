package com.starlink.wms.modules.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starlink.wms.modules.auth.dto.*;

/**
 * 系统用户管理服务
 */
public interface SysUserService {

    /** 分页查询 */
    IPage<UserResp> listPage(UserPageReq req);

    /** 查询单个 */
    UserResp getById(Long id);

    /** 新增 */
    void create(UserCreateReq req);

    /** 修改 */
    void update(UserCreateReq req);

    /** 删除 */
    void deleteById(Long id);

    /** 获取用户角色ID列表 */
    java.util.List<Long> getUserRoleIds(Long userId);

    /** 分配角色 */
    void assignRoles(Long userId, java.util.List<Long> roleIds);
}
