package com.starlink.wms.modules.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.starlink.wms.common.exception.BusinessException;
import com.starlink.wms.modules.auth.dto.*;
import com.starlink.wms.modules.auth.entity.SysMenu;
import com.starlink.wms.modules.auth.entity.SysRoleMenu;
import com.starlink.wms.modules.auth.entity.SysUserRole;
import com.starlink.wms.modules.auth.mapper.SysMenuMapper;
import com.starlink.wms.modules.auth.mapper.SysRoleMenuMapper;
import com.starlink.wms.modules.auth.mapper.SysUserRoleMapper;
import com.starlink.wms.modules.auth.service.MenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements MenuService {

    private static final Logger log = LoggerFactory.getLogger(MenuServiceImpl.class);

    private final SysMenuMapper menuMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysUserRoleMapper userRoleMapper;

    public MenuServiceImpl(SysMenuMapper menuMapper, SysRoleMenuMapper roleMenuMapper, SysUserRoleMapper userRoleMapper) {
        this.menuMapper = menuMapper;
        this.roleMenuMapper = roleMenuMapper;
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    public List<MenuResp> getTree() {
        List<SysMenu> all = menuMapper.selectList(
                new LambdaQueryWrapper<SysMenu>().orderByAsc(SysMenu::getSortOrder));
        return buildTree(all, 0L);
    }

    /** 递归构建树 */
    private List<MenuResp> buildTree(List<SysMenu> all, Long parentId) {
        return all.stream()
                .filter(m -> Objects.equals(m.getParentId(), parentId))
                .sorted(Comparator.comparingInt(m -> m.getSortOrder() != null ? m.getSortOrder() : 0))
                .map(m -> {
                    MenuResp resp = toResp(m);
                    resp.setChildren(buildTree(all, m.getId()));
                    return resp;
                }).collect(Collectors.toList());
    }

    @Override
    public MenuResp getById(Long id) {
        SysMenu menu = menuMapper.selectById(id);
        if (menu == null) throw new BusinessException("菜单不存在");
        return toResp(menu);
    }

    @Override
    @Transactional
    public void create(MenuCreateReq req) {
        SysMenu menu = new SysMenu();
        merge(menu, req);
        menuMapper.insert(menu);

        // 自动给超级管理员角色(roleId=1)分配此菜单权限
        SysRoleMenu rm = new SysRoleMenu();
        rm.setRoleId(1L);
        rm.setMenuId(menu.getId());
        roleMenuMapper.insert(rm);

        log.info("新增菜单(已自动分配给超级管理员): {}", req.getMenuName());
    }

    @Override
    public void update(MenuCreateReq req) {
        SysMenu menu = menuMapper.selectById(req.getId());
        if (menu == null) throw new BusinessException("菜单不存在");
        merge(menu, req);
        menuMapper.updateById(menu);
        log.info("更新菜单: id={}", req.getId());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // 如果有子菜单，不能删除
        Long childCount = menuMapper.selectCount(
                new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, id));
        if (childCount > 0) throw new BusinessException("请先删除子菜单");

        menuMapper.deleteById(id);
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getMenuId, id));
        log.info("删除菜单: id={}", id);
    }

    @Override
    public List<Long> getAllMenuIds() {
        return menuMapper.selectList(null).stream()
                .map(SysMenu::getId).collect(Collectors.toList());
    }

    @Override
    public List<MenuResp> getUserMenuTree(Long userId) {
        // 1. 查用户角色
        List<Long> roleIds = userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId))
                .stream().map(SysUserRole::getRoleId).collect(Collectors.toList());

        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 查角色拥有的菜单ID（去重）
        List<Long> menuIds = roleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getRoleId, roleIds))
                .stream().map(SysRoleMenu::getMenuId).distinct().collect(Collectors.toList());

        if (menuIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 查出所有菜单，确保父菜单也被包含（角色可能只分配了子菜单）
        List<SysMenu> allMenus = menuMapper.selectList(null);
        Map<Long, SysMenu> menuMap = allMenus.stream()
                .collect(Collectors.toMap(SysMenu::getId, m -> m));

        Set<Long> fullMenuIds = new HashSet<>(menuIds);
        for (Long menuId : menuIds) {
            Long parentId = menuMap.get(menuId).getParentId();
            while (parentId != null && parentId != 0) {
                fullMenuIds.add(parentId);
                SysMenu parent = menuMap.get(parentId);
                parentId = parent != null ? parent.getParentId() : null;
            }
        }

        // 4. 按完整菜单列表构建树
        List<SysMenu> userMenus = allMenus.stream()
                .filter(m -> fullMenuIds.contains(m.getId()))
                .collect(Collectors.toList());
        return buildTree(userMenus, 0L);
    }

    private void merge(SysMenu menu, MenuCreateReq req) {
        menu.setParentId(req.getParentId() != null ? req.getParentId() : 0);
        menu.setMenuName(req.getMenuName());
        menu.setMenuType(StringUtils.hasText(req.getMenuType()) ? req.getMenuType() : "MENU");
        menu.setPath(req.getPath());
        menu.setComponent(req.getComponent());
        menu.setPermission(req.getPermission());
        menu.setIcon(req.getIcon());
        menu.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0);
        menu.setVisible(req.getVisible() != null ? req.getVisible() : 1);
        menu.setStatus(req.getStatus() != null ? req.getStatus() : 1);
    }

    private MenuResp toResp(SysMenu menu) {
        MenuResp resp = new MenuResp();
        resp.setId(menu.getId());
        resp.setParentId(menu.getParentId());
        resp.setMenuName(menu.getMenuName());
        resp.setMenuType(menu.getMenuType());
        resp.setPath(menu.getPath());
        resp.setComponent(menu.getComponent());
        resp.setPermission(menu.getPermission());
        resp.setIcon(menu.getIcon());
        resp.setSortOrder(menu.getSortOrder());
        resp.setVisible(menu.getVisible());
        resp.setStatus(menu.getStatus());
        resp.setCreateTime(menu.getCreateTime());
        return resp;
    }
}
