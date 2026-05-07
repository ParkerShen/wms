package com.starlink.wms.modules.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starlink.wms.common.exception.BusinessException;
import com.starlink.wms.modules.auth.dto.*;
import com.starlink.wms.modules.auth.entity.SysUser;
import com.starlink.wms.modules.auth.entity.SysUserRole;
import com.starlink.wms.modules.auth.mapper.SysUserMapper;
import com.starlink.wms.modules.auth.mapper.SysUserRoleMapper;
import com.starlink.wms.modules.auth.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysUserServiceImpl implements SysUserService {

    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    public SysUserServiceImpl(SysUserMapper userMapper, SysUserRoleMapper userRoleMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public IPage<UserResp> listPage(UserPageReq req) {
        // 构建查询条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .like(StringUtils.hasText(req.getUsername()), SysUser::getUsername, req.getUsername())
                .like(StringUtils.hasText(req.getRealName()), SysUser::getRealName, req.getRealName())
                .eq(req.getStatus() != null, SysUser::getStatus, req.getStatus())
                .orderByDesc(SysUser::getId);

        // 分页查询
        Page<SysUser> page = new Page<>(req.getPage(), req.getPageSize());
        IPage<SysUser> result = userMapper.selectPage(page, wrapper);

        // 转换为响应 DTO（脱敏）
        return result.convert(this::toResp);
    }

    @Override
    public UserResp getById(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return toResp(user);
    }

    @Override
    @Transactional
    public void create(UserCreateReq req) {
        // 角色校验
        validateRoles(null, req.getRoleIds());

        // 检查用户名是否重复
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, req.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 默认密码
        String password = StringUtils.hasText(req.getPassword())
                ? req.getPassword() : "123456";

        SysUser user = new SysUser();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(password));
        user.setRealName(req.getRealName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setStatus(req.getStatus() != null ? req.getStatus() : 1);

        userMapper.insert(user);

        // 分配角色
        Long userId = user.getId();
        List<SysUserRole> list = req.getRoleIds().stream().map(roleId -> {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            return ur;
        }).collect(Collectors.toList());
        list.forEach(userRoleMapper::insert);

        log.info("新增用户: {}", req.getUsername());
    }

    @Override
    @Transactional
    public void update(UserCreateReq req) {
        // 角色校验
        validateRoles(req.getId(), req.getRoleIds());

        SysUser user = userMapper.selectById(req.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 修改用户名时检查是否重复
        if (StringUtils.hasText(req.getUsername())
                && !req.getUsername().equals(user.getUsername())) {
            Long count = userMapper.selectCount(
                    new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getUsername, req.getUsername())
                            .ne(SysUser::getId, req.getId()));
            if (count > 0) {
                throw new BusinessException("用户名已存在");
            }
            user.setUsername(req.getUsername());
        }

        if (StringUtils.hasText(req.getPassword())) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        if (StringUtils.hasText(req.getRealName())) user.setRealName(req.getRealName());
        if (StringUtils.hasText(req.getEmail())) user.setEmail(req.getEmail());
        if (StringUtils.hasText(req.getPhone())) user.setPhone(req.getPhone());
        if (req.getStatus() != null) user.setStatus(req.getStatus());

        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 重新分配角色
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, req.getId()));
        List<SysUserRole> list = req.getRoleIds().stream().map(roleId -> {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(req.getId());
            ur.setRoleId(roleId);
            return ur;
        }).collect(Collectors.toList());
        list.forEach(userRoleMapper::insert);

        log.info("更新用户: id={}", req.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id == 1L) {
            throw new BusinessException("不能删除超级管理员");
        }
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        userMapper.deleteById(id);
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
        log.info("删除用户: id={}", id);
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId))
                .stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        validateRoles(userId, roleIds);
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        List<SysUserRole> list = roleIds.stream().map(roleId -> {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            return ur;
        }).collect(Collectors.toList());
        list.forEach(userRoleMapper::insert);
        log.info("分配角色: userId={}, roleIds={}", userId, roleIds);
    }

    /**
     * 校验角色：
     * 1. 每个用户至少一个角色
     * 2. 超级管理员角色(roleId=1)只能分配给用户ID=1的admin
     */
    private void validateRoles(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            throw new BusinessException("每个用户至少需要一个角色");
        }
        if (roleIds.contains(1L) && (userId == null || userId != 1L)) {
            throw new BusinessException("超级管理员角色只能分配给admin用户");
        }
    }

    /** 实体 → 响应DTO */
    private UserResp toResp(SysUser user) {
        UserResp resp = new UserResp();
        resp.setId(user.getId());
        resp.setUsername(user.getUsername());
        resp.setRealName(user.getRealName());
        resp.setEmail(user.getEmail());
        resp.setPhone(user.getPhone());
        resp.setAvatar(user.getAvatar());
        resp.setStatus(user.getStatus());
        resp.setRemark(user.getRemark());
        resp.setCreateTime(user.getCreateTime());
        resp.setRoleIds(getUserRoleIds(user.getId()));
        return resp;
    }
}
