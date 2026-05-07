package com.starlink.wms.modules.auth.service;

import com.starlink.wms.modules.auth.dto.*;

import java.util.List;

public interface MenuService {
    /** 获取菜单树 */
    List<MenuResp> getTree();
    /** 查询单个 */
    MenuResp getById(Long id);
    /** 新增 */
    void create(MenuCreateReq req);
    /** 修改 */
    void update(MenuCreateReq req);
    /** 删除 */
    void deleteById(Long id);
    /** 获取所有菜单ID（用于角色分配时全选） */
    List<Long> getAllMenuIds();

    /** 根据用户角色获取有权限的菜单树 */
    List<MenuResp> getUserMenuTree(Long userId);
}
