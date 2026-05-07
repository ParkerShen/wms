package com.starlink.wms.modules.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starlink.wms.common.exception.BusinessException;
import com.starlink.wms.modules.auth.dto.*;
import com.starlink.wms.modules.auth.entity.SysRole;
import com.starlink.wms.modules.auth.entity.SysRoleMenu;
import com.starlink.wms.modules.auth.entity.SysUserRole;
import com.starlink.wms.modules.auth.mapper.SysRoleMapper;
import com.starlink.wms.modules.auth.mapper.SysRoleMenuMapper;
import com.starlink.wms.modules.auth.mapper.SysUserRoleMapper;
import com.starlink.wms.modules.auth.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMenuMapper roleMenuMapper;

    public RoleServiceImpl(SysRoleMapper roleMapper, SysUserRoleMapper userRoleMapper, SysRoleMenuMapper roleMenuMapper) {
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMenuMapper = roleMenuMapper;
    }

    @Override
    public IPage<RoleResp> listPage(RolePageReq req) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .like(StringUtils.hasText(req.getRoleName()), SysRole::getRoleName, req.getRoleName())
                .eq(req.getStatus() != null, SysRole::getStatus, req.getStatus())
                .orderByAsc(SysRole::getSortOrder);

        Page<SysRole> page = new Page<>(req.getPage(), req.getPageSize());
        return roleMapper.selectPage(page, wrapper).convert(this::toResp);
    }

    @Override
    public RoleResp getById(Long id) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) throw new BusinessException("角色不存在");
        RoleResp resp = toResp(role);
        resp.setMenuIds(getRoleMenuIds(id));
        return resp;
    }

    @Override
    @Transactional
    public void create(RoleCreateReq req) {
        Long count = roleMapper.selectCount(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleKey, req.getRoleKey()));
        if (count > 0) throw new BusinessException("角色标识已存在");

        SysRole role = new SysRole();
        role.setRoleName(req.getRoleName());
        role.setRoleKey(req.getRoleKey());
        role.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0);
        role.setStatus(req.getStatus() != null ? req.getStatus() : 1);
        role.setRemark(req.getRemark());
        roleMapper.insert(role);

        if (req.getMenuIds() != null && !req.getMenuIds().isEmpty()) {
            saveRoleMenu(role.getId(), req.getMenuIds());
        }
        log.info("新增角色: {}", req.getRoleName());
    }

    @Override
    @Transactional
    public void update(RoleCreateReq req) {
        SysRole role = roleMapper.selectById(req.getId());
        if (role == null) throw new BusinessException("角色不存在");

        if (StringUtils.hasText(req.getRoleName())) role.setRoleName(req.getRoleName());
        if (StringUtils.hasText(req.getRoleKey())) role.setRoleKey(req.getRoleKey());
        if (req.getSortOrder() != null) role.setSortOrder(req.getSortOrder());
        if (req.getStatus() != null) role.setStatus(req.getStatus());
        role.setRemark(req.getRemark());
        roleMapper.updateById(role);

        // 更新菜单权限
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, req.getId()));
        if (req.getMenuIds() != null && !req.getMenuIds().isEmpty()) {
            saveRoleMenu(req.getId(), req.getMenuIds());
        }
        log.info("更新角色: id={}", req.getId());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (id == 1) throw new BusinessException("不能删除超级管理员角色");
        roleMapper.deleteById(id);
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, id));
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, id));
        log.info("删除角色: id={}", id);
    }

    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        return roleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId))
                .stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignMenu(Long roleId, List<Long> menuIds) {
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        if (menuIds != null && !menuIds.isEmpty()) {
            saveRoleMenu(roleId, menuIds);
        }
    }

    private void saveRoleMenu(Long roleId, List<Long> menuIds) {
        List<SysRoleMenu> list = menuIds.stream().map(menuId -> {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(roleId);
            rm.setMenuId(menuId);
            return rm;
        }).collect(Collectors.toList());
        list.forEach(roleMenuMapper::insert);
    }

    private RoleResp toResp(SysRole role) {
        RoleResp resp = new RoleResp();
        resp.setId(role.getId());
        resp.setRoleName(role.getRoleName());
        resp.setRoleKey(role.getRoleKey());
        resp.setSortOrder(role.getSortOrder());
        resp.setStatus(role.getStatus());
        resp.setRemark(role.getRemark());
        resp.setCreateTime(role.getCreateTime());
        return resp;
    }
}
