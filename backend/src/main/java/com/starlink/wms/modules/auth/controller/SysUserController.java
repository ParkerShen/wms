package com.starlink.wms.modules.auth.controller;

import com.starlink.wms.common.Result;
import com.starlink.wms.modules.auth.dto.UserCreateReq;
import com.starlink.wms.modules.auth.dto.UserPageReq;
import com.starlink.wms.modules.auth.dto.UserResp;
import com.starlink.wms.modules.auth.service.SysUserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统用户管理 Controller
 */
@RestController
@RequestMapping("/api/system/user")
public class SysUserController {

    private final SysUserService sysUserService;

    public SysUserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    /** GET /api/system/user/page - 分页查询 */
    @GetMapping("/page")
    public Result<com.baomidou.mybatisplus.core.metadata.IPage<UserResp>> page(UserPageReq req) {
        return Result.success(sysUserService.listPage(req));
    }

    /** GET /api/system/user/{id} - 查询单个 */
    @GetMapping("/{id}")
    public Result<UserResp> get(@PathVariable Long id) {
        return Result.success(sysUserService.getById(id));
    }

    /** POST /api/system/user - 新增 */
    @PostMapping
    public Result<Void> create(@Valid @RequestBody UserCreateReq req) {
        sysUserService.create(req);
        return Result.success("新增成功");
    }

    /** PUT /api/system/user - 修改 */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody UserCreateReq req) {
        sysUserService.update(req);
        return Result.success("修改成功");
    }

    /** DELETE /api/system/user/{id} - 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.deleteById(id);
        return Result.success("删除成功");
    }

    /** PUT /api/system/user/{id}/role - 分配角色 */
    @PutMapping("/{id}/role")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        sysUserService.assignRoles(id, roleIds);
        return Result.success("分配成功");
    }
}
