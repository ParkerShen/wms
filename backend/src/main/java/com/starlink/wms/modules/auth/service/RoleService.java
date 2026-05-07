package com.starlink.wms.modules.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starlink.wms.modules.auth.dto.*;

import java.util.List;

public interface RoleService {
    IPage<RoleResp> listPage(RolePageReq req);
    RoleResp getById(Long id);
    void create(RoleCreateReq req);
    void update(RoleCreateReq req);
    void deleteById(Long id);
    /** 获取角色的菜单ID列表 */
    List<Long> getRoleMenuIds(Long roleId);
    /** 分配角色菜单 */
    void assignMenu(Long roleId, List<Long> menuIds);
}
